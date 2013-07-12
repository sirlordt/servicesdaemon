/*******************************************************************************
 * Copyright (c) 2013 SirLordT <sirlordt@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     SirLordT <sirlordt@gmail.com> - initial API and implementation
 ******************************************************************************/
package SystemStartSession;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.maindataservices.Utilities;


import AbstractDBEngine.CAbstractDBEngine;
import AbstractDBEngine.CAbstractDBEngine.SQLStatementType;
import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import AbstractService.CServicePreExecuteResult;
import AbstractService.ConstantsServicesTags;
import AbstractService.DefaultConstantsServices;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CClassPathLoader;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CSecurityTokensManager;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.CSessionInfoManager;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import CommonClasses.DefaultConstantsServicesDaemon;
import CommonClasses.NamesSQLTypes;
import DBServicesManager.CConfigDBConnection;
import DBServicesManager.CDBServicesManagerConfig;
import DBServicesManager.ConfigXMLTagsDBServicesManager;
import ExtendedLogger.CExtendedLogger;

public class CSystemStartSession extends CAbstractService {

	protected CSystemStartSessionConfig SystemStartSessionConfig = null;

	protected CDBServicesManagerConfig DBServicesManagerConfig = null;
	
    public final static String getJarFolder() {

        String name =  CSystemStartSession.class.getCanonicalName().replace( '.', '/' );

        String s = CSystemStartSession.class.getClass().getResource( "/" + name + ".class" ).toString();

        s = s.replace( '/', File.separatorChar );

        if ( s.indexOf(".jar") >= 0 )
           s = s.substring( 0, s.indexOf(".jar") + 4 );
        else
           s = s.substring( 0, s.indexOf(".class") );

        if ( s.indexOf( "jar:file:\\" )  == 0 ) { //Windows style path SO inside jar file 

        	s = s.substring( 10 );

        }
        else if ( s.indexOf( "file:\\" )  == 0 ) { //Windows style path SO .class file

        	s = s.substring( 6 );

        }
        else { //Unix family ( Linux/BSD/Mac/Solaris ) style path SO

            s = s.substring( s.lastIndexOf(':') + 1 );

        }

        return s.substring( 0, s.lastIndexOf( File.separatorChar ) + 1 );

    }

    public void AddConfiguredInputParametersToServiceInputParameters( ArrayList<CSystemStartSessionDBConnection> ConfiguredSystemStartSessionDBConnections ) {
    	
    	for ( CSystemStartSessionDBConnection SystemStartSessionDBConnection: ConfiguredSystemStartSessionDBConnections  ) {
    	   
    		SystemStartSessionDBConnection.RemoveInputParameterByName( ConstantsServicesTags._RequestServiceName );

    		SystemStartSessionDBConnection.RemoveInputParameterByName( ConstantsServicesTags._RequestResponseFormat );

    		SystemStartSessionDBConnection.RemoveInputParameterByName( ConstantsServicesTags._RequestResponseFormatVersion );

    		SystemStartSessionDBConnection.RemoveInputParameterByName( ConstantsSystemStartSession._Request_DBConnection_Name );

    		//Force to add standards parameters ServiceSD and DBConnection
    		CInputServiceParameter InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormat, false, ConstantsServicesTags._RequestResponseFormatType, ConstantsServicesTags._RequestResponseFormatLength, TParameterScope.IN, ServiceLang.Translate( "Response format name, example: XML-DATAPACKET, CSV, JSON" ) );
    		
    		SystemStartSessionDBConnection.InputParameters.add( InputParameter ); 	
    		
    		InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormatVersion, false, ConstantsServicesTags._RequestResponseFormatVersionType, ConstantsServicesTags._RequestResponseFormatVersionLength, TParameterScope.IN, ServiceLang.Translate( "Response format version, example: 1.1" ) );
    		
    		SystemStartSessionDBConnection.InputParameters.add( InputParameter ); 	
    		
    		InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestServiceName, true, ConstantsServicesTags._RequestServiceNameType, ConstantsServicesTags._RequestServiceNameLength, TParameterScope.IN, ServiceLang.Translate( "Service name" ) );
    		
    		SystemStartSessionDBConnection.InputParameters.add( InputParameter ); 	
    		
    		InputParameter = new CInputServiceParameter( ConstantsSystemStartSession._Request_DBConnection_Name, true, ConstantsSystemStartSession._Request_DBConnection_Type, ConstantsSystemStartSession._Request_DBConnection_Length, TParameterScope.IN, ServiceLang.Translate( "Database connection name" ) );
    		
    		SystemStartSessionDBConnection.InputParameters.add( InputParameter ); 	

    		GroupsInputParametersService.put( SystemStartSessionDBConnection.strName, SystemStartSessionDBConnection.InputParameters );
    		
    	}
    	
    }    
    
	@Override
	public boolean InitializeService( CServicesDaemonConfig ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = false;
		
		super.InitializeService( ServicesDaemonConfig, OwnerConfig );
		
        try {
		
        	if ( CDBServicesManagerConfig.class.isInstance( OwnerConfig ) )
        		DBServicesManagerConfig = (CDBServicesManagerConfig) OwnerConfig;

        	this.bAuthRequired = false;
        	this.strJarRunningPath = getJarFolder();
        	DefaultConstantsSystemStartSession.strDefaultRunningPath = this.strJarRunningPath;
        	this.strServiceName = "System.Start.Session";
        	this.strServiceVersion = "0.0.0.1";

        	this.SetupService( DefaultConstantsSystemStartSession.strDefaultMainFileLog, DefaultConstantsSystemStartSession.strDefaultRunningPath + DefaultConstantsServices.strDefaultLangsDir + DefaultConstantsSystemStartSession.strDefaultMainFile + "." + ServicesDaemonConfig.strDefaultLang ); //Init the Logger and Lang
        	
        	ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Running dir: [%s]", this.strJarRunningPath ) );        
        	ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Version: [%s]", this.strServiceVersion ) );        
        	
			CClassPathLoader ClassPathLoader = new CClassPathLoader( ServiceLogger, ServiceLang );

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPreExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPostExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePostExecute();

        	SystemStartSessionConfig = CSystemStartSessionConfig.getSystemStartSessionConfig( ServicesDaemonConfig, OwnerConfig );

        	if ( SystemStartSessionConfig.LoadConfig( DefaultConstantsSystemStartSession.strDefaultRunningPath + DefaultConstantsSystemStartSession.strDefaultConfFile, ServiceLang, ServiceLogger ) == true ) {

        		bResult = true;

        		this.strServiceDescription = ServiceLang.Translate( "Allow log on and get a security token" );

        		this.AddConfiguredInputParametersToServiceInputParameters( SystemStartSessionConfig.ConfiguredSystemStartSessionDBConnections );

        	};
	        
		}
		catch ( Exception Ex ) {

			bResult = false;
			
			if ( OwnerLogger != null )
        		OwnerLogger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}
		
	    return bResult;
		
	}

	public String[] getMacrosNames() {
		
		String strResult[] = { "[macro]macro_ip[/macro]", "[macro]macro_forwarded_ip[/macro]", "[macro]macro_database[/macro]", "[macro]macro_dbconnection_name[/macro]", "[macro]macro_system_date[/macro]", "[macro]macro_system_time[/macro]", "[macro]system_datetime[/macro]" };

		return strResult;
		
	}
	
	public int[] getMacrosTypes() {
		
		int intResult[] = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.TIME, Types.TIMESTAMP };

		return intResult;
		
	}
	
	public String ReplaceMacrosNamesForValues( String strMacroContained, String ... strMacrosValues ) {
		
		String strResult = strMacroContained;
		
		String[] strMacrosTags = this.getMacrosNames(); 
		
		if ( strMacrosValues != null && strMacrosValues.length >= strMacrosTags.length ) {
			
			for ( int intIndex = 0; intIndex < strMacrosTags.length; intIndex++ ) {
			
				String strMacroValue = strMacrosValues[ intIndex ];
				
				if ( strMacroValue == null )
					strMacroValue = ""; 
				
				strResult = strResult.replace( strMacrosTags[ intIndex ], strMacroValue );
				
			}
			
		}
		
		return strResult;
		
	}
	
	public String ReplaceInputParametersNamesForValues( String strParamValueContained, HttpServletRequest Request ) {
		
		String strResult = strParamValueContained;
		
		ArrayList<String> ListOfParamsNames = Utilities.ParseTokensByTags( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue, strParamValueContained, true, false );
		
        for ( String strParamName : ListOfParamsNames ) {
        	
        	String strParamValue = Request.getParameter( strParamName );
        	
        	if ( strParamValue != null ) {

        		strResult = strResult.replace( ConfigXMLTagsServicesDaemon._StartParamValue + strParamName + ConfigXMLTagsServicesDaemon._EndParamValue, strParamValue );
        		
        	}
        	
        }
		
		return strResult;
		
	}
	
	public String UncryptString( String strCryptedPassword, CExtendedLogger Logger, CLanguage Lang ) {
		
		String strResult = strCryptedPassword;
		
		int intPassCryptedLength = ConfigXMLTagsDBServicesManager._Password_Crypted.length();
		int intPassCryptedSepLength = ConfigXMLTagsDBServicesManager._Password_Crypted_Sep.length();
		
		if ( strCryptedPassword.length() > intPassCryptedLength && strResult.substring( 0, intPassCryptedLength ).equals( ConfigXMLTagsDBServicesManager._Password_Crypted ) ) {

			strResult =  strResult.substring( intPassCryptedLength + intPassCryptedSepLength, strResult.length() );
			
			String strCryptKeys[] = { 
					                  "xfzm29dp",
					                  "6m3m7xa5",
			                          "e48c4xyi",		                  
			                          "6we7og02",		                  
			                          "4m7gypao",		                  
			                          "hy6z2m0x",		                  
			                          "2zx6kynd",		                  
			                          "1k9c0666",		                  
			                          "q3f5i11j",		                  
			                          "4y84x0j7"		                  
			                        };

			int intIndexPassSep = strResult.indexOf( ConfigXMLTagsDBServicesManager._Password_Crypted_Sep, 0 );
			
			String strCryptKeyIndex =  strResult.substring( 0, intIndexPassSep );
			
			int intCryptKeyIndex = Utilities.StrToInteger( strCryptKeyIndex );
			
			if ( intCryptKeyIndex > 0 && intCryptKeyIndex <= strCryptKeys.length ) {
				
				strResult =  strResult.substring( intIndexPassSep + intPassCryptedSepLength, strResult.length() );
				
				strResult = Utilities.UncryptString( strResult, DefaultConstantsSystemStartSession.strDefaultCryptAlgorithm, strCryptKeys[ intCryptKeyIndex ], Logger );
				
				
			}
			else {
				
		    	Logger.LogError( "-1001", Lang.Translate( "The crypt key index [%s] is not valid", strCryptKeyIndex ) );        
				
			}
			
		}
		else {
			
	    	Logger.LogWarning( "-1", Lang.Translate( "Using clear text password" ) );        
			
		}
		
		return strResult;
		
	}
	
	public void SucessStartSession( CMemoryRowSet StartSessionRowSet, CConfigDBConnection ConfigDBConnection, String strCode, ArrayList<String> AddFieldToResponseSucess, ArrayList<String> AddFieldToResponseAny, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

	    try {
		
	    	Random GeneratorLevel1 = new Random( Calendar.getInstance().getTimeInMillis() );

	    	Random GeneratorLevel2 = new Random( GeneratorLevel1.nextLong() );

	    	Long lngSecutirtyToken = GeneratorLevel2.nextLong();
	    	
	    	if ( lngSecutirtyToken < 0 )
	    		lngSecutirtyToken = lngSecutirtyToken * -1;  
	    	
	    	String strSecurityTokenID = Long.toString( lngSecutirtyToken );

	    	CSecurityTokensManager SecurityTokensManager = CSecurityTokensManager.getSecurityTokensManager();
	    	
	    	SecurityTokensManager.addSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );
	    	
	    	CSessionInfoManager SessionInfoManager = CSessionInfoManager.getSessionInfoManager();
	    	
	    	SessionInfoManager.addSecurityTokenIDToName( ConfigDBConnection.strName, strSecurityTokenID, ServiceLogger, ServiceLang );

	    	SessionInfoManager.addConfigDBConnectionToSecurityTokenID( strSecurityTokenID, ConfigDBConnection, ServiceLogger, ServiceLang );
	    	/*
	    	HttpSession ServiceSession = Request.getSession( true );
	    	
	    	@SuppressWarnings("unchecked")
			ArrayList<String> strSessionSecurityTokens = ( ArrayList<String> ) ServiceSession.getAttribute( ConstantsServicesTags._SessionSecurityTokens );
	    	
	    	if ( strSessionSecurityTokens == null )
	    		strSessionSecurityTokens = new ArrayList<String>();
	    		
	        strSessionSecurityTokens.add( strSecurityToken );
	    	
	    	ServiceSession.setAttribute( ConstantsServicesTags._SessionSecurityTokens, strSessionSecurityTokens ); 

	    	ServiceSession.setAttribute( ConfigDBConnection.strName, strSecurityToken ); 

	    	ServiceSession.setAttribute( strSecurityToken, ConfigDBConnection ); 

            */
	    	Response.setContentType( ResponseFormat.getContentType() );
	    	Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

	    	if ( StartSessionRowSet == null || ( AddFieldToResponseSucess.size() == 0 && AddFieldToResponseAny.size() == 0 ) ) {
	    	
	    		String strResponseBuffer = ResponseFormat.FormatSimpleMessage( strSecurityTokenID, "", Integer.parseInt( strCode ), ServiceLang.Translate( "Success start session" ), false, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );
	    	
	    	}
	    	else {
	    		
	    		ArrayList<String> AddFieldToResponse = new ArrayList<String>();
	    		AddFieldToResponse.addAll( AddFieldToResponseSucess );
	    		AddFieldToResponse.addAll( AddFieldToResponseAny );
	    		 
	    		StartSessionRowSet.filterFields( AddFieldToResponse );
	    		
	    		ArrayList<String> SystemFields = new ArrayList<String>();
	    		SystemFields.add( ConstantsServicesTags._SecurityTokenID );
	    		SystemFields.add( ConstantsServicesTags._Code );
	    		SystemFields.add( ConstantsServicesTags._Description );
	    		
	    		StartSessionRowSet.removeFieldsByName( SystemFields );
	    		
	    		StartSessionRowSet.addField( ConstantsServicesTags._SecurityTokenID, ConstantsServicesTags._SecurityTokenIDTypeID, ConstantsServicesTags._SecurityTokenIDType, 0, ConstantsServicesTags._SecurityTokenID );
	    		StartSessionRowSet.addField( ConstantsServicesTags._Code, ConstantsServicesTags._CodeTypeID, ConstantsServicesTags._CodeType, 0, ConstantsServicesTags._Code );
	    		StartSessionRowSet.addField( ConstantsServicesTags._Description, ConstantsServicesTags._DescriptionTypeID, ConstantsServicesTags._DescriptionType, ConstantsServicesTags._DescriptionLength, ConstantsServicesTags._Description );
	    		
	    		StartSessionRowSet.NormalizeRowCount();
	    		
	    		if ( StartSessionRowSet.getRowCount() > 0 ) {
	    		 
	    			StartSessionRowSet.setAllData( ConstantsServicesTags._SecurityTokenID, Long.parseLong( strSecurityTokenID ) );
	    		    StartSessionRowSet.setAllData( ConstantsServicesTags._Code, Integer.parseInt( strCode ) );
	    		    StartSessionRowSet.setAllData( ConstantsServicesTags._Description, ServiceLang.Translate( "Success start session" ) );
	    		    
	    		}
	    		else {
	    			
	    			StartSessionRowSet.addData( ConstantsServicesTags._SecurityTokenID, Long.parseLong( strSecurityTokenID ) );
	    		    StartSessionRowSet.addData( ConstantsServicesTags._Code, Integer.parseInt( strCode ) );
	    		    StartSessionRowSet.addData( ConstantsServicesTags._Description, ServiceLang.Translate( "Success start session" ) );
	    			
	    		}
				
				String strResponseBuffer = ResponseFormat.FormatMemoryRowSet( StartSessionRowSet, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
				
				Response.getWriter().print( strResponseBuffer );
	    						
	    	}
		
	    }
	    catch ( Exception Ex ) {
	    	
			if ( ServiceLogger != null )
				ServiceLogger.LogException( "-1016", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.LogException( "-1016", Ex.getMessage(), Ex );
	    	
	    }
	    
	}
	
	public void FailedStartSession( CMemoryRowSet StartSessionRowSet, CConfigDBConnection ConfigDBConnection, String strCode, ArrayList<String> AddFieldToResponseFailed, ArrayList<String> AddFieldToResponseAny, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {
		
		try {

	    	Response.setContentType( ResponseFormat.getContentType() );
	    	Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

	    	if ( StartSessionRowSet == null || ( AddFieldToResponseFailed.size() == 0 && AddFieldToResponseAny.size() == 0 ) ) {

	    		String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", Integer.parseInt( strCode ), ServiceLang.Translate( "Failed start session" ), false, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );

	    	}
	    	else {
	 
	    		ArrayList<String> AddFieldToResponse = new ArrayList<String>();
	    		AddFieldToResponse.addAll( AddFieldToResponseFailed );
	    		AddFieldToResponse.addAll( AddFieldToResponseAny );

	    		StartSessionRowSet.filterFields( AddFieldToResponse );

	    		ArrayList<String> SystemFields = new ArrayList<String>();
	    		SystemFields.add( ConstantsServicesTags._Code );
	    		SystemFields.add( ConstantsServicesTags._Description );

	    		StartSessionRowSet.removeFieldsByName( SystemFields );

	    		StartSessionRowSet.addField( ConstantsServicesTags._Code, ConstantsServicesTags._CodeTypeID, ConstantsServicesTags._CodeType, 0, "" );
	    		StartSessionRowSet.addField( ConstantsServicesTags._Description, ConstantsServicesTags._DescriptionTypeID, ConstantsServicesTags._DescriptionType, ConstantsServicesTags._DescriptionLength, ConstantsServicesTags._Description );

	    		StartSessionRowSet.NormalizeRowCount();

	    		StartSessionRowSet.setAllData( ConstantsServicesTags._Code, Integer.parseInt( strCode ) );
	    		StartSessionRowSet.setAllData( ConstantsServicesTags._Description, ServiceLang.Translate( "Failed start session" ) );

	    		String strResponseBuffer = ResponseFormat.FormatMemoryRowSet( StartSessionRowSet, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );
	    	
	    	}
			
		}
		catch ( Exception Ex ) {
			
			if ( ServiceLogger != null )
				ServiceLogger.LogException( "-1016", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.LogException( "-1016", Ex.getMessage(), Ex );

		}
		
	}
	
	public void DisabledStartSession( CMemoryRowSet StartSessionRowSet, CConfigDBConnection ConfigDBConnection, String strCode, ArrayList<String> AddFieldToResponseDisabled, ArrayList<String> AddFieldToResponseAny, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {
		
		try {

	    	Response.setContentType( ResponseFormat.getContentType() );
	    	Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

	    	if ( StartSessionRowSet == null || ( AddFieldToResponseDisabled.size() == 0 && AddFieldToResponseAny.size() == 0 ) ) {

	    		String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", Integer.parseInt( strCode ), ServiceLang.Translate( "Start session failed, disabled account" ), false, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );

	    	}
	    	else {

	    		ArrayList<String> AddFieldToResponse = new ArrayList<String>();
	    		AddFieldToResponse.addAll( AddFieldToResponseDisabled );
	    		AddFieldToResponse.addAll( AddFieldToResponseAny );

	    		StartSessionRowSet.filterFields( AddFieldToResponse );

	    		ArrayList<String> SystemFields = new ArrayList<String>();
	    		SystemFields.add( ConstantsServicesTags._Code );
	    		SystemFields.add( ConstantsServicesTags._Description );

	    		StartSessionRowSet.removeFieldsByName( SystemFields );

	    		StartSessionRowSet.addField( ConstantsServicesTags._Code, ConstantsServicesTags._CodeTypeID, ConstantsServicesTags._CodeType, 0, "" );
	    		StartSessionRowSet.addField( ConstantsServicesTags._Description, ConstantsServicesTags._DescriptionTypeID, ConstantsServicesTags._DescriptionType, ConstantsServicesTags._DescriptionLength, ConstantsServicesTags._Description );

	    		StartSessionRowSet.NormalizeRowCount();

	    		StartSessionRowSet.setAllData( ConstantsServicesTags._Code, Integer.parseInt( strCode ) );
	    		StartSessionRowSet.setAllData( ConstantsServicesTags._Description, ServiceLang.Translate( "Start session failed, disabled account" ) );

	    		String strResponseBuffer = ResponseFormat.FormatMemoryRowSet( StartSessionRowSet, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );
    		
	    	}
			
		}
		catch ( Exception Ex ) {
			
			if ( ServiceLogger != null )
				ServiceLogger.LogException( "-1016", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )  
				OwnerLogger.LogException( "-1016", Ex.getMessage(), Ex );

		}

	}
	
	public void NotFoundStartSession( CMemoryRowSet StartSessionRowSet, CConfigDBConnection ConfigDBConnection, String strCode, ArrayList<String> AddFieldToResponseNotFound, ArrayList<String> AddFieldToResponseAny, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {
		
		try {

	    	Response.setContentType( ResponseFormat.getContentType() );
	    	Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

	    	if ( StartSessionRowSet == null || ( AddFieldToResponseNotFound.size() == 0 && AddFieldToResponseAny.size() == 0 ) ) {

	    		String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", Integer.parseInt( strCode ), ServiceLang.Translate( "Start session failed, account not found" ), false, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );

	    	}
	    	else {

	    		ArrayList<String> AddFieldToResponse = new ArrayList<String>();
	    		AddFieldToResponse.addAll( AddFieldToResponseNotFound );
	    		AddFieldToResponse.addAll( AddFieldToResponseAny );

	    		StartSessionRowSet.filterFields( AddFieldToResponse );

	    		ArrayList<String> SystemFields = new ArrayList<String>();
	    		SystemFields.add( ConstantsServicesTags._Code );
	    		SystemFields.add( ConstantsServicesTags._Description );

	    		StartSessionRowSet.removeFieldsByName( SystemFields );

	    		StartSessionRowSet.addField( ConstantsServicesTags._Code, ConstantsServicesTags._CodeTypeID, ConstantsServicesTags._CodeType, 0, "" );
	    		StartSessionRowSet.addField( ConstantsServicesTags._Description, ConstantsServicesTags._DescriptionTypeID, ConstantsServicesTags._DescriptionType, ConstantsServicesTags._DescriptionLength, ConstantsServicesTags._Description );

	    		StartSessionRowSet.NormalizeRowCount();

	    		StartSessionRowSet.setAllData( ConstantsServicesTags._Code, Integer.parseInt( strCode ) );
	    		StartSessionRowSet.setAllData( ConstantsServicesTags._Description, ServiceLang.Translate( "Start session failed, account not found" ) );

	    		String strResponseBuffer = ResponseFormat.FormatMemoryRowSet( StartSessionRowSet, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );
	    	
	    	}
			
		}
		catch ( Exception Ex ) {
			
			if ( ServiceLogger != null )
				ServiceLogger.LogException( "-1016", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.LogException( "-1016", Ex.getMessage(), Ex );

		}
		
	}
	
	void AfterCheckSQL( int intAfterCheckSQL, CAbstractDBEngine DBEngine, CSystemStartSessionDBConnection SystemStartSessionDBConnection, Connection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {

			ArrayList<String> strAfterCheckSQL = null;

			if ( intAfterCheckSQL == 1 ) { //Success

				strAfterCheckSQL = SystemStartSessionDBConnection.AfterCheckSQLSuccess;

			}
			else if ( intAfterCheckSQL == 2 ) { //Failed

				strAfterCheckSQL = SystemStartSessionDBConnection.AfterCheckSQLFailed;

			}
			else if ( intAfterCheckSQL == 3 ) { //Disabled

				strAfterCheckSQL = SystemStartSessionDBConnection.AfterCheckSQLDisabled;

			}
			else if ( intAfterCheckSQL == 4 ) { //Not Found

				strAfterCheckSQL = SystemStartSessionDBConnection.AfterCheckSQLNotFound;

			}
			
			strAfterCheckSQL.addAll( SystemStartSessionDBConnection.AfterCheckSQLAny );

			if ( strAfterCheckSQL != null ) {

				for ( int intIndexSQL =0; intIndexSQL < strAfterCheckSQL.size(); intIndexSQL++ ) {

					String strSQL = SystemStartSessionDBConnection.AfterCheckSQLSuccess.get( intIndexSQL );

					SQLStatementType SQLType = DBEngine.getSQLStatementType( strSQL, ServiceLogger, ServiceLang );

					try {

						if ( SQLType == SQLStatementType.Call ) {

							DBEngine.ExecuteCallableStatementByInputServiceParameters( DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, strSQL, ServiceLogger, ServiceLang );

						}
						else if ( DBEngine.isModifySQLStatement( SQLType ) ) {

							DBEngine.ExecuteModifySQLByInputServiceParameters( DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, strSQL, ServiceLogger, ServiceLang );

						}
						else {

							ServiceLogger.LogWarning( "-1", ServiceLang.Translate( "Only modify SQL ( Call, Insert, Update, Delete ) allowed here [%s]", strSQL ) );        

						}

					}
					catch ( Exception Ex ) {

						if ( ServiceLogger != null )
							ServiceLogger.LogException( "-1016", Ex.getMessage(), Ex ); 
						else if ( OwnerLogger != null )
							OwnerLogger.LogException( "-1016", Ex.getMessage(), Ex );

					}

				}

			}

		}
		catch ( Exception Ex ) {

			if ( ServiceLogger != null )
				ServiceLogger.LogException( "-1015", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.LogException( "-1015", Ex.getMessage(), Ex );

		}
		
	}
	
	@Override
	public int ExecuteService( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String,CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

		int intResultCode = -1000;

		CServicePreExecuteResult ServicePreExecuteResult = this.RunServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

		if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

			if ( DBServicesManagerConfig != null ) {

				String strPostDBConnection = ( String ) Request.getParameter( ConstantsSystemStartSession._Request_DBConnection_Name );

				if ( strPostDBConnection != null && strPostDBConnection.isEmpty() == false ) {

					//HttpSession ServiceSession = Request.getSession( true );
					CSessionInfoManager SessionInfoManager = CSessionInfoManager.getSessionInfoManager();

					String strSecurityTokenIDSessionStarted = SessionInfoManager.getSecurityTokenIDFromName( strPostDBConnection, ServiceLogger, ServiceLang ); //.getAttribute( strPostDBConnection );

					if ( strSecurityTokenIDSessionStarted == null ) {  //No previous started session from post database name 

						ArrayList<CInputServiceParameter> InputServiceParameters = GroupsInputParametersService.get( strPostDBConnection );

						CConfigDBConnection ConfigDBConnection = ( (CDBServicesManagerConfig) this.OwnerConfig ).getConfiguredDBConnection( strPostDBConnection );

						CSystemStartSessionDBConnection SystemStartSessionDBConnection = SystemStartSessionConfig.getSystemStartSessionByName( strPostDBConnection );

						if ( InputServiceParameters != null && ConfigDBConnection != null && SystemStartSessionDBConnection != null ) {

							if ( this.CheckServiceInputParameters( InputServiceParameters, Request, Response, ResponseFormat, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang ) == true ) {

								CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( ConfigDBConnection.strEngine, ConfigDBConnection.strEngineVersion ); 

								if ( DBEngine != null ) {

									try {

										CMemoryRowSet StartSessionResultSet = null;

										String strSQL = SystemStartSessionDBConnection.strSQL; 

										CConfigDBConnection LocalConfigDBConnection = new CConfigDBConnection( ConfigDBConnection ); 

										LocalConfigDBConnection.strSessionKey = SystemStartSessionDBConnection.strSessionKey;

										Date SystemDateTime = new Date( System.currentTimeMillis() );

										SimpleDateFormat DateFormat = new SimpleDateFormat( LocalConfigDBConnection.strDateFormat );
										SimpleDateFormat TimeFormat = new SimpleDateFormat( LocalConfigDBConnection.strTimeFormat );
										SimpleDateFormat DateTimeFormat = new SimpleDateFormat( LocalConfigDBConnection.strDateTimeFormat );

										LocalConfigDBConnection.strSessionUser = this.ReplaceMacrosNamesForValues( LocalConfigDBConnection.strSessionUser, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strSessionPassword = this.ReplaceMacrosNamesForValues( LocalConfigDBConnection.strSessionPassword, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strTransactionUser = this.ReplaceMacrosNamesForValues( LocalConfigDBConnection.strTransactionUser, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strTransactionPassword = this.ReplaceMacrosNamesForValues( LocalConfigDBConnection.strTransactionPassword, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strSessionKey = this.ReplaceMacrosNamesForValues( LocalConfigDBConnection.strSessionKey, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strSessionUser = this.ReplaceInputParametersNamesForValues( LocalConfigDBConnection.strSessionUser, Request );
										LocalConfigDBConnection.strSessionPassword = this.ReplaceInputParametersNamesForValues( LocalConfigDBConnection.strSessionPassword, Request );
										LocalConfigDBConnection.strSessionPassword = this.UncryptString( LocalConfigDBConnection.strSessionPassword, ServiceLogger, ServiceLang );
										LocalConfigDBConnection.strTransactionUser = this.ReplaceInputParametersNamesForValues( LocalConfigDBConnection.strTransactionUser, Request );
										LocalConfigDBConnection.strTransactionPassword = this.ReplaceInputParametersNamesForValues( LocalConfigDBConnection.strTransactionPassword, Request );
										LocalConfigDBConnection.strTransactionPassword = this.UncryptString( LocalConfigDBConnection.strTransactionPassword, ServiceLogger, ServiceLang );
										LocalConfigDBConnection.strSessionKey = this.ReplaceInputParametersNamesForValues( LocalConfigDBConnection.strSessionKey, Request );

										Connection DBConnection = DBEngine.getDBConnection( LocalConfigDBConnection.getDBEngineConfigConnection( true ), ServiceLogger, ServiceLang );

										if ( DBConnection != null ) {

											DBEngine.setAutoCommit( DBConnection, false, ServiceLogger, ServiceLang );

											String strForwardedIP = Request.getHeader( "X-Forwarded-For" );

											if ( strForwardedIP == null )
												strForwardedIP = "";

											String strMacrosValues[] = { Request.getRemoteAddr(), strForwardedIP, LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) }; 

											if ( strSQL.isEmpty() == false ) { //database

												if ( SystemStartSessionDBConnection.strSQLType.equals( ConfigXMLTagsSystemStartSession._SQLType_sql )  ) {

													StartSessionResultSet = DBEngine.ExecuteQuerySQLByInputServiceParameters( DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, strSQL, ServiceLogger, ServiceLang );

												}
												else if ( SystemStartSessionDBConnection.strSQLType.equals( ConfigXMLTagsSystemStartSession._SQLType_stored_procedure )  ) {

													StartSessionResultSet = DBEngine.ExecuteCallableStatementByInputServiceParameters( DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, strSQL, ServiceLogger, ServiceLang );

												}

											}

											//DBEngine.close( DBConnection, ServiceLogger, ServiceLang );

											if ( StartSessionResultSet != null ) {

												if ( ConfigDBConnection.strAuthType.equals( ConfigXMLTagsDBServicesManager._Auth_Type_Engine ) ) {

													AfterCheckSQL( 1, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

													SucessStartSession( StartSessionResultSet, LocalConfigDBConnection, "1", SystemStartSessionDBConnection.AddFieldToResponseSuccess, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

												}
												else {

													//Check field value
													if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_check_field_value ) ) {

														CMemoryFieldData Field = StartSessionResultSet.getFieldByName( SystemStartSessionDBConnection.strFieldName );

														if ( Field != null ) {

															int intSQLType = NamesSQLTypes.ConvertToSQLType( SystemStartSessionDBConnection.strFieldType );

															if ( Field.checkFieldValue( 0, SystemStartSessionDBConnection.strFieldValueSuccess, intSQLType, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang ) ) {

																AfterCheckSQL( 1, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

																SucessStartSession( StartSessionResultSet, LocalConfigDBConnection, SystemStartSessionDBConnection.strFieldValueSuccess, SystemStartSessionDBConnection.AddFieldToResponseSuccess, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

															}
															else if ( Field.checkFieldValue( 0, SystemStartSessionDBConnection.strFieldValueFailed, intSQLType, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang ) ) {

																AfterCheckSQL( 2, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

																FailedStartSession( StartSessionResultSet, LocalConfigDBConnection, SystemStartSessionDBConnection.strFieldValueFailed, SystemStartSessionDBConnection.AddFieldToResponseFailed, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

															}
															else if ( Field.checkFieldValue( 0, SystemStartSessionDBConnection.strFieldValueDisabled, intSQLType, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang ) ) {

																AfterCheckSQL( 3, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

																DisabledStartSession( StartSessionResultSet, LocalConfigDBConnection, SystemStartSessionDBConnection.strFieldValueDisabled, SystemStartSessionDBConnection.AddFieldToResponseDisabled, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

															}
															else if ( Field.checkFieldValue( 0, SystemStartSessionDBConnection.strFieldValueNotFound, intSQLType, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang ) ) {

																AfterCheckSQL( 4, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

																NotFoundStartSession( StartSessionResultSet, LocalConfigDBConnection, SystemStartSessionDBConnection.strFieldValueNotFound, SystemStartSessionDBConnection.AddFieldToResponseNotFound, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

															}
															else { //field value wrong 

																if ( ServiceLogger != null ) {

																	String strFieldValue = Field.FieldValueToString( 0, true, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, false, ServiceLogger, ServiceLang );

																	ServiceLogger.LogError( "-1009", ServiceLang.Translate( "Field name [%s] has value [%s] which is unkown, for the service config file, in result from SQL [%s]", SystemStartSessionDBConnection.strFieldName, strFieldValue, strSQL ) );        

																}

																Response.setContentType( ResponseFormat.getContentType() );
																Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

																String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1009, ServiceLang.Translate( "Failed to start session in database name: [%s], see the log file for more details", ConfigDBConnection.strName ), true, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
																Response.getWriter().print( strResponseBuffer );

															}

														}
														else { //Field name not found in rows result from SQL

															if ( ServiceLogger != null ) {

																ServiceLogger.LogError( "-1008", ServiceLang.Translate( "Field name [%s] not found in result from SQL [%s]", SystemStartSessionDBConnection.strFieldName, strSQL ) );        

															}

															Response.setContentType( ResponseFormat.getContentType() );
															Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

															String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1008, ServiceLang.Translate( "Failed to start session in database name: [%s], see the log file for more details", ConfigDBConnection.strName ), true, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
															Response.getWriter().print( strResponseBuffer );

														}

													}
													else if ( StartSessionResultSet.getRowCount() > 0 ) { //Only one row is necessary for valid check

														AfterCheckSQL( 1, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

														//Type == if_exists
														SucessStartSession( StartSessionResultSet, LocalConfigDBConnection, "1", SystemStartSessionDBConnection.AddFieldToResponseSuccess, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

													}
													else { //No row found in result from SQL

														if ( ServiceLogger != null ) {

															ServiceLogger.LogError( "-1007", ServiceLang.Translate( "No rows found in result from SQL: [%s]", strSQL ) );        

														}

														Response.setContentType( ResponseFormat.getContentType() );
														Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

														String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1007, ServiceLang.Translate( "Failed to start session in database name: [%s], see the log file for more details", ConfigDBConnection.strName ), true, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
														Response.getWriter().print( strResponseBuffer );

													}

												}

											}
											else {

												if ( strSQL.isEmpty() == false ) {

													if ( ServiceLogger != null ) {

														ServiceLogger.LogError( "-1006", ServiceLang.Translate( "Error execute the next SQL statement [%s]", strSQL ) );        

													}

												}

												if ( ConfigDBConnection.strAuthType.equals( ConfigXMLTagsDBServicesManager._Auth_Type_Engine ) ) {

													AfterCheckSQL( 1, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

													SucessStartSession( null, LocalConfigDBConnection, "1", SystemStartSessionDBConnection.AddFieldToResponseSuccess, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

												}
												else {

													Response.setContentType( ResponseFormat.getContentType() );
													Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

													String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1006, ServiceLang.Translate( "Failed to connect to database name: [%s], see the log file for more details", ConfigDBConnection.strName ), true, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
													Response.getWriter().print( strResponseBuffer );

												}

											}

											DBEngine.commit( DBConnection, ServiceLogger, ServiceLang );
											DBEngine.close( DBConnection, ServiceLogger, ServiceLang );
											
											intResultCode = 1;

										}
										else {

											if ( ServiceLogger != null ) {

												ServiceLogger.LogError( "-1005", ServiceLang.Translate( "Failed to connect to database name: [%s]", LocalConfigDBConnection.strName ) );        

											}

											try {

												Response.setContentType( ResponseFormat.getContentType() );
												Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

												String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1005, ServiceLang.Translate( "Failed to connect to database name: [%s], see the log file for more details", ConfigDBConnection.strName ), true, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
												Response.getWriter().print( strResponseBuffer );

											}
											catch ( Exception Ex ) {

												if ( ServiceLogger != null )
													ServiceLogger.LogException( "-1025", Ex.getMessage(), Ex ); 
												else if ( OwnerLogger != null )
													OwnerLogger.LogException( "-1025", Ex.getMessage(), Ex );

											}

										}

									}
									catch ( Exception Ex ) {

										if ( ServiceLogger != null )
											ServiceLogger.LogException( "-1024", Ex.getMessage(), Ex ); 
										else if ( OwnerLogger != null )
											OwnerLogger.LogException( "-1024", Ex.getMessage(), Ex );

									}

								}
								else {

									try {

										Response.setContentType( ResponseFormat.getContentType() );
										Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

										String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1004, ServiceLang.Translate( "The database engine name [%s] version [%s] not found", ConfigDBConnection.strEngine, ConfigDBConnection.strEngineVersion ), true, strResponseFormatVersion, ConfigDBConnection.strDateTimeFormat, ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
										Response.getWriter().print( strResponseBuffer );

									}
									catch ( Exception Ex ) {

										if ( ServiceLogger != null )
											ServiceLogger.LogException( "-1023", Ex.getMessage(), Ex ); 
										else if ( OwnerLogger != null )
											OwnerLogger.LogException( "-1023", Ex.getMessage(), Ex );

									}

								}

							}

						}
						else {

							try {

								Response.setContentType( ResponseFormat.getContentType() );
								Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

								String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1003, ServiceLang.Translate( "The database connection name [%s] not found", strPostDBConnection ), true, strResponseFormatVersion, OwnerConfig.getConfigValue( ConstantsSystemStartSession._Global_DateTime_Format ), OwnerConfig.getConfigValue( ConstantsSystemStartSession._Global_Date_Format ), OwnerConfig.getConfigValue( ConstantsSystemStartSession._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
								Response.getWriter().print( strResponseBuffer );

							}
							catch ( Exception Ex ) {

								if ( ServiceLogger != null )
									ServiceLogger.LogException( "-1022", Ex.getMessage(), Ex ); 
								else if ( OwnerLogger != null )
									OwnerLogger.LogException( "-1022", Ex.getMessage(), Ex );

							}

						}

					}
					else {

						try {

							Response.setContentType( ResponseFormat.getContentType() );
							Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

							String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1002, ServiceLang.Translate( "The session already started" ), true, strResponseFormatVersion, OwnerConfig.getConfigValue( ConstantsSystemStartSession._Global_DateTime_Format ), OwnerConfig.getConfigValue( ConstantsSystemStartSession._Global_Date_Format ), OwnerConfig.getConfigValue( ConstantsSystemStartSession._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
							Response.getWriter().print( strResponseBuffer );

						}
						catch ( Exception Ex ) {

							if ( ServiceLogger != null )
								ServiceLogger.LogException( "-1021", Ex.getMessage(), Ex ); 
							else if ( OwnerLogger != null )
								OwnerLogger.LogException( "-1021", Ex.getMessage(), Ex );

						}

					}

				}
				else {

					try {

						Response.setContentType( ResponseFormat.getContentType() );
						Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

						String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1001, ServiceLang.Translate( "The [%s] parameter is required for service and was not sent or its contents is empty", ConstantsSystemStartSession._Request_DBConnection_Name ), true, strResponseFormatVersion, OwnerConfig.getConfigValue( ConstantsSystemStartSession._Global_DateTime_Format ), OwnerConfig.getConfigValue( ConstantsSystemStartSession._Global_Date_Format ), OwnerConfig.getConfigValue( ConstantsSystemStartSession._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
						Response.getWriter().print( strResponseBuffer );

					}
					catch ( Exception Ex ) {

						if ( ServiceLogger != null )
							ServiceLogger.LogException( "-1020", Ex.getMessage(), Ex ); 
						else if ( OwnerLogger != null )
							OwnerLogger.LogException( "-1020", Ex.getMessage(), Ex );

					}

				}

			}
			else {

				String strMessage = "DBServicesManagerConfig is NULL";

				if ( ServiceLang != null )
					strMessage = ServiceLang.Translate( strMessage );
				else if ( OwnerLang != null )
					strMessage = OwnerLang.Translate( strMessage );

				if ( ServiceLogger != null )
					ServiceLogger.LogWarning( "-1", strMessage );        
				else if ( OwnerLogger != null )
					OwnerLogger.LogWarning( "-1", strMessage );        

			}

		}
		else {

			intResultCode = ServicePreExecuteResult.intResultCode;

		}

		CServicePostExecuteResult ServicePostExecuteResult = this.RunServicePostExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

		if ( ServicePostExecuteResult != null ) {

			intResultCode = ServicePostExecuteResult.intResultCode;

		}
		
		return intResultCode;
	
	}

}
