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

import java.sql.Date;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.maindataservices.Utilities;


import AbstractDBEngine.CAbstractDBConnection;
import AbstractDBEngine.CAbstractDBEngine;
import AbstractDBEngine.CAbstractDBEngine.SQLStatementType;
import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import AbstractService.CServicePreExecuteResult;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CClassPathLoader;
import CommonClasses.CConfigNativeDBConnection;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CNativeSessionInfo;
import CommonClasses.CSecurityTokensManager;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.CNativeSessionInfoManager;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsMessagesCodes;
import CommonClasses.NamesSQLTypes;
import DBCommonClasses.CDBAbstractService;
import ExtendedLogger.CExtendedLogger;

public class CSystemStartSession extends CDBAbstractService {

	protected CConfigSystemStartSession SystemStartSessionConfig = null;

	//protected CConfigDBServicesManager DBServicesManagerConfig = null;
	
    public CSystemStartSession() {
    	
    	super();
    	
    }

    public void AddConfiguredInputParametersToServiceInputParameters( ArrayList<CSystemStartSessionDBConnection> ConfiguredSystemStartSessionDBConnections ) {
    	
    	for ( CSystemStartSessionDBConnection SystemStartSessionDBConnection: ConfiguredSystemStartSessionDBConnections  ) {
    	   
    		SystemStartSessionDBConnection.RemoveInputParameterByName( ConstantsCommonClasses._Request_ServiceName );

    		SystemStartSessionDBConnection.RemoveInputParameterByName( ConstantsCommonClasses._Request_ResponseFormat );

    		SystemStartSessionDBConnection.RemoveInputParameterByName( ConstantsCommonClasses._Request_ResponseFormatVersion );

    		SystemStartSessionDBConnection.RemoveInputParameterByName( ConstantsSystemStartSession._Request_DBConnection_Name );

    		//Force to add standards parameters ServiceSD and DBConnection
    		CInputServiceParameter InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ResponseFormat, false, ConstantsCommonClasses._Request_ResponseFormat_Type, ConstantsCommonClasses._Request_ResponseFormat_Length, TParameterScope.IN, ServiceLang.translate( "Response format name, example: XML-DATAPACKET, CSV, JSON" ) );
    		
    		SystemStartSessionDBConnection.InputParameters.add( InputParameter ); 	
    		
    		InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ResponseFormatVersion, false, ConstantsCommonClasses._Request_ResponseFormatVersion_Type, ConstantsCommonClasses._Request_ResponseFormatVersion_Length, TParameterScope.IN, ServiceLang.translate( "Response format version, example: 1.1" ) );
    		
    		SystemStartSessionDBConnection.InputParameters.add( InputParameter ); 	
    		
    		InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ServiceName, true, ConstantsCommonClasses._Request_ServiceName_Type, ConstantsCommonClasses._Request_ServiceName_Length, TParameterScope.IN, ServiceLang.translate( "Service Name" ) );
    		
    		SystemStartSessionDBConnection.InputParameters.add( InputParameter ); 	
    		
    		InputParameter = new CInputServiceParameter( ConstantsSystemStartSession._Request_DBConnection_Name, true, ConstantsSystemStartSession._Request_DBConnection_Type, ConstantsSystemStartSession._Request_DBConnection_Length, TParameterScope.IN, ServiceLang.translate( "Database connection name" ) );
    		
    		SystemStartSessionDBConnection.InputParameters.add( InputParameter ); 	

    		GroupsInputParametersService.put( SystemStartSessionDBConnection.strName, SystemStartSessionDBConnection.InputParameters );
    		
    	}
    	
    }    
    
	@Override
	public boolean initializeService( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = false;
		
		super.initializeService( ServicesDaemonConfig, OwnerConfig );
		
        try {
		
        	//if ( CConfigDBServicesManager.class.isInstance( OwnerConfig ) )
        	//	DBServicesManagerConfig = (CConfigDBServicesManager) OwnerConfig;

        	this.bAuthRequired = false;
        	this.strRunningPath = net.maindataservices.Utilities.getJarFolder( this.getClass() );
        	this.strServiceName = "System.Start.Session";
        	this.strServiceVersion = "0.0.0.1";

        	this.setupService( ConstantsSystemStartSession._Main_File_Log, this.strRunningPath + ConstantsCommonClasses._Langs_Dir + ConstantsSystemStartSession._Main_File + "." + ConstantsCommonClasses._Lang_Ext ); //Init the Logger and Lang
        	
        	ServiceLogger.logMessage( "1", ServiceLang.translate( "Running dir: [%s]", this.strRunningPath ) );        
        	ServiceLogger.logMessage( "1", ServiceLang.translate( "Version: [%s]", this.strServiceVersion ) );        
        	
			CClassPathLoader ClassPathLoader = new CClassPathLoader();

			ClassPathLoader.LoadClassFiles( this.strRunningPath + ConstantsCommonClasses._Pre_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang  );

			this.loadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strRunningPath + ConstantsCommonClasses._Post_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang  );

			this.loadAndRegisterServicePostExecute();

        	SystemStartSessionConfig = CConfigSystemStartSession.getConfigSystemStartSession( ServicesDaemonConfig, OwnerConfig, this.strRunningPath );

        	if ( SystemStartSessionConfig.LoadConfig( this.strRunningPath + ConstantsSystemStartSession._Conf_File, ServiceLang, ServiceLogger ) == true ) {

        		bResult = true;

        		this.strServiceDescription = ServiceLang.translate( "Allow log on and get a security token" );

        		this.AddConfiguredInputParametersToServiceInputParameters( SystemStartSessionConfig.ConfiguredSystemStartSessionDBConnections );

        	};
	        
		}
		catch ( Exception Ex ) {

			bResult = false;
			
			if ( OwnerLogger != null )
        		OwnerLogger.logException( "-1010", Ex.getMessage(), Ex );
			
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
	
	public String replaceMacrosNamesForValues( String strMacroContained, String ... strMacrosValues ) {
		
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
	
	public String replaceInputParametersNamesForValues( String strParamValueContained, HttpServletRequest Request ) {
		
		String strResult = strParamValueContained;
		
		ArrayList<String> ListOfParamsNames = Utilities.parseTokensByTags( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue, strParamValueContained, true, false );
		
        for ( String strParamName : ListOfParamsNames ) {
        	
        	String strParamValue = Request.getParameter( strParamName );
        	
        	if ( strParamValue != null ) {

        		strResult = strResult.replace( ConfigXMLTagsServicesDaemon._StartParamValue + strParamName + ConfigXMLTagsServicesDaemon._EndParamValue, strParamValue );
        		
        	}
        	
        }
		
		return strResult;
		
	}
	
	public void sucessStartSession( CMemoryRowSet StartSessionRowSet, CConfigNativeDBConnection ConfigNativeDBConnection, String strCode, ArrayList<String> AddFieldToResponseSucess, ArrayList<String> AddFieldToResponseAny, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

	    try {
		
	    	Random GeneratorLevel1 = new Random( Calendar.getInstance().getTimeInMillis() );

	    	Random GeneratorLevel2 = new Random( GeneratorLevel1.nextLong() );

	    	Long lngSecutirtyToken = GeneratorLevel2.nextLong();
	    	
	    	if ( lngSecutirtyToken < 0 )
	    		lngSecutirtyToken = lngSecutirtyToken * -1;  
	    	
	    	String strSecurityTokenID = Long.toString( lngSecutirtyToken );

	    	CSecurityTokensManager SecurityTokensManager = CSecurityTokensManager.getSecurityTokensManager( (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Security_Manager_Name, null ) );
	    	
	    	SecurityTokensManager.addSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );
	    	
	    	CNativeSessionInfoManager SessionInfoManager = CNativeSessionInfoManager.getSessionInfoManager();
	    	
			HttpSession ServiceSession = Request.getSession( true );

			CNativeSessionInfo NativeSessionInfo = new CNativeSessionInfo();
			
			NativeSessionInfo.strName = ConfigNativeDBConnection.strName;
			NativeSessionInfo.strSecurityTokenID = strSecurityTokenID;
			NativeSessionInfo.strSessionID = ServiceSession.getId();
			NativeSessionInfo.ConfigNativeDBConnection = ConfigNativeDBConnection;
			
			SessionInfoManager.addSessionInfo( NativeSessionInfo, true, ServiceLogger, ServiceLang );
			
	    	//SessionInfoManager.addSecurityTokenIDToName( ConfigNativeDBConnection.strName, strSecurityTokenID, ServiceSession.getId(), ServiceLogger, ServiceLang );

	    	//SessionInfoManager.addConfigNativeDBConnectionToSecurityTokenID( strSecurityTokenID, ConfigNativeDBConnection, ServiceLogger, ServiceLang );
			
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
			ServiceLogger.logInfo( "0x1001", ServiceLang.translate( "Success start session with SessionKey: [%s], SecurityTokenID: [%s], Database: [%s]", ConfigNativeDBConnection.strSessionKey, strSecurityTokenID, ConfigNativeDBConnection.strName ) );        

	    	Response.setContentType( ResponseFormat.getContentType() );
	    	Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

	    	if ( StartSessionRowSet == null || ( AddFieldToResponseSucess.size() == 0 && AddFieldToResponseAny.size() == 0 ) ) {
	    	
	    		String strResponseBuffer = ResponseFormat.formatSimpleMessage( strSecurityTokenID, "", Integer.parseInt( strCode ), ServiceLang.translate( "Success start session" ), false, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );
	    	
	    	}
	    	else {
	    		
	    		ArrayList<String> AddFieldToResponse = new ArrayList<String>();
	    		AddFieldToResponse.addAll( AddFieldToResponseSucess );
	    		AddFieldToResponse.addAll( AddFieldToResponseAny );
	    		 
	    		StartSessionRowSet.filterFields( AddFieldToResponse );
	    		
	    		ArrayList<String> SystemFields = new ArrayList<String>();
	    		SystemFields.add( ConstantsCommonClasses._SecurityTokenID );
	    		SystemFields.add( ConstantsCommonClasses._Code );
	    		SystemFields.add( ConstantsCommonClasses._Description );
	    		
	    		StartSessionRowSet.removeFieldsByName( SystemFields );
	    		
	    		StartSessionRowSet.addField( ConstantsCommonClasses._SecurityTokenID, ConstantsCommonClasses._SecurityTokenID_TypeID, ConstantsCommonClasses._SecurityTokenID_Type, 0, ConstantsCommonClasses._SecurityTokenID );
	    		StartSessionRowSet.addField( ConstantsCommonClasses._Code, ConstantsCommonClasses._Code_TypeID, ConstantsCommonClasses._Code_Type, 0, ConstantsCommonClasses._Code );
	    		StartSessionRowSet.addField( ConstantsCommonClasses._Description, ConstantsCommonClasses._Description_TypeID, ConstantsCommonClasses._Description_Type, ConstantsCommonClasses._Description_Length, ConstantsCommonClasses._Description );
	    		
	    		StartSessionRowSet.NormalizeRowCount();
	    		
	    		if ( StartSessionRowSet.getRowCount() > 0 ) {
	    		 
	    			StartSessionRowSet.setAllData( ConstantsCommonClasses._SecurityTokenID, Long.parseLong( strSecurityTokenID ) );
	    		    StartSessionRowSet.setAllData( ConstantsCommonClasses._Code, Integer.parseInt( strCode ) );
	    		    StartSessionRowSet.setAllData( ConstantsCommonClasses._Description, ServiceLang.translate( "Success start session" ) );
	    		    
	    		}
	    		else {
	    			
	    			StartSessionRowSet.addData( ConstantsCommonClasses._SecurityTokenID, Long.parseLong( strSecurityTokenID ) );
	    		    StartSessionRowSet.addData( ConstantsCommonClasses._Code, Integer.parseInt( strCode ) );
	    		    StartSessionRowSet.addData( ConstantsCommonClasses._Description, ServiceLang.translate( "Success start session" ) );
	    			
	    		}
				
				String strResponseBuffer = ResponseFormat.formatMemoryRowSet( StartSessionRowSet, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
				
				Response.getWriter().print( strResponseBuffer );
	    						
	    	}
		
	    }
	    catch ( Exception Ex ) {
	    	
			if ( ServiceLogger != null )
				ServiceLogger.logException( "-1016", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.logException( "-1016", Ex.getMessage(), Ex );
	    	
	    }
	    
	}
	
	public void failedStartSession( CMemoryRowSet StartSessionRowSet, CConfigNativeDBConnection ConfigNativeDBConnection, String strCode, ArrayList<String> AddFieldToResponseFailed, ArrayList<String> AddFieldToResponseAny, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {
		
		try {

			ServiceLogger.logInfo( "-0x1001", ServiceLang.translate( "Failed start session with SessionKey: [%s], Database: [%s], Cause: [%s]", ConfigNativeDBConnection.strSessionKey, ConfigNativeDBConnection.strName, "failed" ) );        
	    	
			Response.setContentType( ResponseFormat.getContentType() );
	    	Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

	    	if ( StartSessionRowSet == null || ( AddFieldToResponseFailed.size() == 0 && AddFieldToResponseAny.size() == 0 ) ) {

	    		String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", Integer.parseInt( strCode ), ServiceLang.translate( "Failed start session" ), false, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );

	    	}
	    	else {
	 
	    		ArrayList<String> AddFieldToResponse = new ArrayList<String>();
	    		AddFieldToResponse.addAll( AddFieldToResponseFailed );
	    		AddFieldToResponse.addAll( AddFieldToResponseAny );

	    		StartSessionRowSet.filterFields( AddFieldToResponse );

	    		ArrayList<String> SystemFields = new ArrayList<String>();
	    		SystemFields.add( ConstantsCommonClasses._Code );
	    		SystemFields.add( ConstantsCommonClasses._Description );

	    		StartSessionRowSet.removeFieldsByName( SystemFields );

	    		StartSessionRowSet.addField( ConstantsCommonClasses._Code, ConstantsCommonClasses._Code_TypeID, ConstantsCommonClasses._Code_Type, 0, "" );
	    		StartSessionRowSet.addField( ConstantsCommonClasses._Description, ConstantsCommonClasses._Description_TypeID, ConstantsCommonClasses._Description_Type, ConstantsCommonClasses._Description_Length, ConstantsCommonClasses._Description );

	    		StartSessionRowSet.NormalizeRowCount();

	    		StartSessionRowSet.setAllData( ConstantsCommonClasses._Code, Integer.parseInt( strCode ) );
	    		StartSessionRowSet.setAllData( ConstantsCommonClasses._Description, ServiceLang.translate( "Failed start session" ) );

	    		String strResponseBuffer = ResponseFormat.formatMemoryRowSet( StartSessionRowSet, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );
	    	
	    	}
			
		}
		catch ( Exception Ex ) {
			
			if ( ServiceLogger != null )
				ServiceLogger.logException( "-1016", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.logException( "-1016", Ex.getMessage(), Ex );

		}
		
	}
	
	public void disabledStartSession( CMemoryRowSet StartSessionRowSet, CConfigNativeDBConnection ConfigNativeDBConnection, String strCode, ArrayList<String> AddFieldToResponseDisabled, ArrayList<String> AddFieldToResponseAny, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {
		
		try {

			ServiceLogger.logInfo( "-0x1002", ServiceLang.translate( "Failed start session with SessionKey: [%s], Database: [%s], Cause: [%s]", ConfigNativeDBConnection.strSessionKey, ConfigNativeDBConnection.strName, "disabled" ) );        
			
	    	Response.setContentType( ResponseFormat.getContentType() );
	    	Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

	    	if ( StartSessionRowSet == null || ( AddFieldToResponseDisabled.size() == 0 && AddFieldToResponseAny.size() == 0 ) ) {

	    		String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", Integer.parseInt( strCode ), ServiceLang.translate( "Start session failed, disabled account" ), false, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );

	    	}
	    	else {

	    		ArrayList<String> AddFieldToResponse = new ArrayList<String>();
	    		AddFieldToResponse.addAll( AddFieldToResponseDisabled );
	    		AddFieldToResponse.addAll( AddFieldToResponseAny );

	    		StartSessionRowSet.filterFields( AddFieldToResponse );

	    		ArrayList<String> SystemFields = new ArrayList<String>();
	    		SystemFields.add( ConstantsCommonClasses._Code );
	    		SystemFields.add( ConstantsCommonClasses._Description );

	    		StartSessionRowSet.removeFieldsByName( SystemFields );

	    		StartSessionRowSet.addField( ConstantsCommonClasses._Code, ConstantsCommonClasses._Code_TypeID, ConstantsCommonClasses._Code_Type, 0, "" );
	    		StartSessionRowSet.addField( ConstantsCommonClasses._Description, ConstantsCommonClasses._Description_TypeID, ConstantsCommonClasses._Description_Type, ConstantsCommonClasses._Description_Length, ConstantsCommonClasses._Description );

	    		StartSessionRowSet.NormalizeRowCount();

	    		StartSessionRowSet.setAllData( ConstantsCommonClasses._Code, Integer.parseInt( strCode ) );
	    		StartSessionRowSet.setAllData( ConstantsCommonClasses._Description, ServiceLang.translate( "Start session failed, disabled account" ) );

	    		String strResponseBuffer = ResponseFormat.formatMemoryRowSet( StartSessionRowSet, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );
    		
	    	}
			
		}
		catch ( Exception Ex ) {
			
			if ( ServiceLogger != null )
				ServiceLogger.logException( "-1016", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )  
				OwnerLogger.logException( "-1016", Ex.getMessage(), Ex );

		}

	}
	
	public void notFoundStartSession( CMemoryRowSet StartSessionRowSet, CConfigNativeDBConnection ConfigNativeDBConnection, String strCode, ArrayList<String> AddFieldToResponseNotFound, ArrayList<String> AddFieldToResponseAny, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {
		
		try {

			ServiceLogger.logInfo( "-0x1003", ServiceLang.translate( "Failed start session with SessionKey: [%s], Database: [%s], Cause: [%s]", ConfigNativeDBConnection.strSessionKey, ConfigNativeDBConnection.strName, "not found" ) );        

			Response.setContentType( ResponseFormat.getContentType() );
	    	Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

	    	if ( StartSessionRowSet == null || ( AddFieldToResponseNotFound.size() == 0 && AddFieldToResponseAny.size() == 0 ) ) {

	    		String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", Integer.parseInt( strCode ), ServiceLang.translate( "Start session failed, account not found" ), false, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );

	    	}
	    	else {

	    		ArrayList<String> AddFieldToResponse = new ArrayList<String>();
	    		AddFieldToResponse.addAll( AddFieldToResponseNotFound );
	    		AddFieldToResponse.addAll( AddFieldToResponseAny );

	    		StartSessionRowSet.filterFields( AddFieldToResponse );

	    		ArrayList<String> SystemFields = new ArrayList<String>();
	    		SystemFields.add( ConstantsCommonClasses._Code );
	    		SystemFields.add( ConstantsCommonClasses._Description );

	    		StartSessionRowSet.removeFieldsByName( SystemFields );

	    		StartSessionRowSet.addField( ConstantsCommonClasses._Code, ConstantsCommonClasses._Code_TypeID, ConstantsCommonClasses._Code_Type, 0, "" );
	    		StartSessionRowSet.addField( ConstantsCommonClasses._Description, ConstantsCommonClasses._Description_TypeID, ConstantsCommonClasses._Description_Type, ConstantsCommonClasses._Description_Length, ConstantsCommonClasses._Description );

	    		StartSessionRowSet.NormalizeRowCount();

	    		StartSessionRowSet.setAllData( ConstantsCommonClasses._Code, Integer.parseInt( strCode ) );
	    		StartSessionRowSet.setAllData( ConstantsCommonClasses._Description, ServiceLang.translate( "Start session failed, account not found" ) );

	    		String strResponseBuffer = ResponseFormat.formatMemoryRowSet( StartSessionRowSet, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

	    		Response.getWriter().print( strResponseBuffer );
	    	
	    	}
			
		}
		catch ( Exception Ex ) {
			
			if ( ServiceLogger != null )
				ServiceLogger.logException( "-1016", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.logException( "-1016", Ex.getMessage(), Ex );

		}
		
	}
	
	void afterCheckSQL( int intAfterCheckSQL, CAbstractDBEngine DBEngine, CSystemStartSessionDBConnection SystemStartSessionDBConnection, CAbstractDBConnection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
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

							DBEngine.executeCallableStatementByInputServiceParameters( DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, strSQL, ServiceLogger, ServiceLang );

						}
						else if ( DBEngine.isModifySQLStatement( SQLType ) ) {

							DBEngine.executeModifySQLByInputServiceParameters( DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, strSQL, ServiceLogger, ServiceLang );

						}
						else {

							ServiceLogger.logWarning( "-1", ServiceLang.translate( "Only modify SQL ( Call, Insert, Update, Delete ) allowed here [%s]", strSQL ) );        

						}

					}
					catch ( Exception Ex ) {

						if ( ServiceLogger != null )
							ServiceLogger.logException( "-1016", Ex.getMessage(), Ex ); 
						else if ( OwnerLogger != null )
							OwnerLogger.logException( "-1016", Ex.getMessage(), Ex );

					}

				}

			}

		}
		catch ( Exception Ex ) {

			if ( ServiceLogger != null )
				ServiceLogger.logException( "-1015", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.logException( "-1015", Ex.getMessage(), Ex );

		}
		
	}
	
	@Override
	public int executeService( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String,CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

		int intResultCode = -1000;

		CServicePreExecuteResult ServicePreExecuteResult = this.runServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

		if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

			if ( OwnerConfig != null ) {

				String strPostDBConnection = Request.getParameter( ConstantsSystemStartSession._Request_DBConnection_Name );

				if ( strPostDBConnection != null && strPostDBConnection.isEmpty() == false ) {

					HttpSession ServiceSession = Request.getSession( true );
					
					CNativeSessionInfoManager SessionInfoManager = CNativeSessionInfoManager.getSessionInfoManager();

					String strSecurityTokenIDSessionStarted = SessionInfoManager.getSecurityTokenIDFromSessionIDAndName( ServiceSession.getId(), strPostDBConnection, ServiceLogger, ServiceLang ); //.getAttribute( strPostDBConnection );

					if ( strSecurityTokenIDSessionStarted == null ) {  //No previous started session from post database name 

						ArrayList<CInputServiceParameter> InputServiceParameters = GroupsInputParametersService.get( strPostDBConnection );

						if ( InputServiceParameters == null )
						   InputServiceParameters = GroupsInputParametersService.get( "*" );
						
						CConfigNativeDBConnection ConfigNativeDBConnection = (CConfigNativeDBConnection) this.OwnerConfig.sendMessage( ConstantsMessagesCodes._getConfiguredNativeDBConnection, strPostDBConnection ); //( (CConfigDBServicesManager) this.OwnerConfig ).getConfiguredNativeDBConnection( strPostDBConnection );

						CSystemStartSessionDBConnection SystemStartSessionDBConnection = SystemStartSessionConfig.getSystemStartSessionByName( strPostDBConnection );

						if ( InputServiceParameters != null && ConfigNativeDBConnection != null && SystemStartSessionDBConnection != null ) {

							if ( this.checkServiceInputParameters( InputServiceParameters, Request, Response, ResponseFormat, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang ) == true ) {

								CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( ConfigNativeDBConnection.strEngine, ConfigNativeDBConnection.strEngineVersion ); 

								if ( DBEngine != null ) {

									try {

										CMemoryRowSet StartSessionResultSet = null;

										String strSQL = SystemStartSessionDBConnection.strSQL; 

										CConfigNativeDBConnection LocalConfigDBConnection = new CConfigNativeDBConnection( ConfigNativeDBConnection ); 

										LocalConfigDBConnection.strSessionKey = SystemStartSessionDBConnection.strSessionKey;

										Date SystemDateTime = new Date( System.currentTimeMillis() );

										SimpleDateFormat DateFormat = new SimpleDateFormat( LocalConfigDBConnection.strDateFormat );
										SimpleDateFormat TimeFormat = new SimpleDateFormat( LocalConfigDBConnection.strTimeFormat );
										SimpleDateFormat DateTimeFormat = new SimpleDateFormat( LocalConfigDBConnection.strDateTimeFormat );

										LocalConfigDBConnection.strSessionUser = this.replaceMacrosNamesForValues( LocalConfigDBConnection.strSessionUser, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strSessionPassword = this.replaceMacrosNamesForValues( LocalConfigDBConnection.strSessionPassword, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strTransactionUser = this.replaceMacrosNamesForValues( LocalConfigDBConnection.strTransactionUser, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strTransactionPassword = this.replaceMacrosNamesForValues( LocalConfigDBConnection.strTransactionPassword, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strSessionKey = this.replaceMacrosNamesForValues( LocalConfigDBConnection.strSessionKey, Request.getRemoteAddr(), Request.getHeader( "X-Forwarded-For" ), LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) );
										LocalConfigDBConnection.strSessionUser = this.replaceInputParametersNamesForValues( LocalConfigDBConnection.strSessionUser, Request );
										LocalConfigDBConnection.strSessionPassword = this.replaceInputParametersNamesForValues( LocalConfigDBConnection.strSessionPassword, Request );
										LocalConfigDBConnection.strSessionPassword = Utilities.uncryptString( ConfigXMLTagsSystemStartSession._Password_Crypted, ConfigXMLTagsSystemStartSession._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, LocalConfigDBConnection.strSessionPassword, ServiceLogger, ServiceLang );
										LocalConfigDBConnection.strTransactionUser = this.replaceInputParametersNamesForValues( LocalConfigDBConnection.strTransactionUser, Request );
										LocalConfigDBConnection.strTransactionPassword = this.replaceInputParametersNamesForValues( LocalConfigDBConnection.strTransactionPassword, Request );
										LocalConfigDBConnection.strTransactionPassword = Utilities.uncryptString( ConfigXMLTagsSystemStartSession._Password_Crypted, ConfigXMLTagsSystemStartSession._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, LocalConfigDBConnection.strTransactionPassword, ServiceLogger, ServiceLang );
										LocalConfigDBConnection.strSessionKey = this.replaceInputParametersNamesForValues( LocalConfigDBConnection.strSessionKey, Request );

										CAbstractDBConnection DBConnection = DBEngine.getDBConnection( LocalConfigDBConnection.getDBEngineConfigConnection( true ), ServiceLogger, ServiceLang );

										if ( DBConnection != null ) {

											DBEngine.setAutoCommit( DBConnection, false, ServiceLogger, ServiceLang );

											String strForwardedIP = Request.getHeader( "X-Forwarded-For" );

											if ( strForwardedIP == null )
												strForwardedIP = "";

											String strMacrosValues[] = { Request.getRemoteAddr(), strForwardedIP, LocalConfigDBConnection.strDatabase, LocalConfigDBConnection.strName, DateFormat.format( SystemDateTime ), TimeFormat.format( SystemDateTime ), DateTimeFormat.format( SystemDateTime ) }; 

											if ( strSQL.isEmpty() == false ) { //database

												if ( SystemStartSessionDBConnection.strSQLType.equals( ConfigXMLTagsSystemStartSession._SQLType_sql )  ) {

													StartSessionResultSet = DBEngine.executeQuerySQLByInputServiceParameters( DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, strSQL, ServiceLogger, ServiceLang );

												}
												else if ( SystemStartSessionDBConnection.strSQLType.equals( ConfigXMLTagsSystemStartSession._SQLType_stored_procedure )  ) {

													StartSessionResultSet = DBEngine.executeCallableStatementByInputServiceParameters( DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, strSQL, ServiceLogger, ServiceLang );

												}

											}

											//DBEngine.close( DBConnection, ServiceLogger, ServiceLang );

											if ( ConfigNativeDBConnection.strAuthType.equals( ConfigXMLTagsSystemStartSession._Auth_Type_Engine ) ) {
											
												afterCheckSQL( 1, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

												sucessStartSession( StartSessionResultSet, LocalConfigDBConnection, "1", SystemStartSessionDBConnection.AddFieldToResponseSuccess, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );
												
											}
											else if ( StartSessionResultSet != null ) {

												/*if ( ConfigDBConnection.strAuthType.equals( ConfigXMLTagsDBServicesManager._Auth_Type_Engine ) ) {

												     AfterCheckSQL( 1, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

												     SucessStartSession( StartSessionResultSet, LocalConfigDBConnection, "1", SystemStartSessionDBConnection.AddFieldToResponseSuccess, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

												}
												else {*/

												//Check field value
												if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_check_field_value ) ) {

													CMemoryFieldData Field = StartSessionResultSet.getFieldByName( SystemStartSessionDBConnection.strFieldName );

													if ( Field != null ) {

														int intSQLType = NamesSQLTypes.ConvertToJavaSQLType( SystemStartSessionDBConnection.strFieldType );

														if ( Field.checkFieldValue( 0, SystemStartSessionDBConnection.strFieldValueSuccess, intSQLType, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang ) ) {

															afterCheckSQL( 1, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

															sucessStartSession( StartSessionResultSet, LocalConfigDBConnection, SystemStartSessionDBConnection.strFieldValueSuccess, SystemStartSessionDBConnection.AddFieldToResponseSuccess, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

														}
														else if ( Field.checkFieldValue( 0, SystemStartSessionDBConnection.strFieldValueFailed, intSQLType, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang ) ) {

															afterCheckSQL( 2, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

															failedStartSession( StartSessionResultSet, LocalConfigDBConnection, SystemStartSessionDBConnection.strFieldValueFailed, SystemStartSessionDBConnection.AddFieldToResponseFailed, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

														}
														else if ( Field.checkFieldValue( 0, SystemStartSessionDBConnection.strFieldValueDisabled, intSQLType, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang ) ) {

															afterCheckSQL( 3, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

															disabledStartSession( StartSessionResultSet, LocalConfigDBConnection, SystemStartSessionDBConnection.strFieldValueDisabled, SystemStartSessionDBConnection.AddFieldToResponseDisabled, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

														}
														else if ( Field.checkFieldValue( 0, SystemStartSessionDBConnection.strFieldValueNotFound, intSQLType, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang ) ) {

															afterCheckSQL( 4, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

															notFoundStartSession( StartSessionResultSet, LocalConfigDBConnection, SystemStartSessionDBConnection.strFieldValueNotFound, SystemStartSessionDBConnection.AddFieldToResponseNotFound, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

														}
														else { //field value wrong 

															if ( ServiceLogger != null ) {

																String strFieldValue = Field.FieldValueToString( 0, true, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, false, ServiceLogger, ServiceLang );

																ServiceLogger.logError( "-1009", ServiceLang.translate( "Field name [%s] has value [%s] which is unkown, for the service config file, in result from SQL [%s]", SystemStartSessionDBConnection.strFieldName, strFieldValue, strSQL ) );        

															}

															Response.setContentType( ResponseFormat.getContentType() );
															Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

															String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1009, ServiceLang.translate( "Failed to start session in database name: [%s], see the log file for more details", ConfigNativeDBConnection.strName ), true, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
															Response.getWriter().print( strResponseBuffer );

														}

													}
													else { //Field name not found in rows result from SQL

														if ( ServiceLogger != null ) {

															ServiceLogger.logError( "-1008", ServiceLang.translate( "Field name [%s] not found in result from SQL [%s]", SystemStartSessionDBConnection.strFieldName, strSQL ) );        

														}

														Response.setContentType( ResponseFormat.getContentType() );
														Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

														String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1008, ServiceLang.translate( "Failed to start session in database name: [%s], see the log file for more details", ConfigNativeDBConnection.strName ), true, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
														Response.getWriter().print( strResponseBuffer );

													}

												}
												else if ( StartSessionResultSet.getRowCount() > 0 ) { //Only one row is necessary for valid check

													afterCheckSQL( 1, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

													//Type == if_exists
													sucessStartSession( StartSessionResultSet, LocalConfigDBConnection, "1", SystemStartSessionDBConnection.AddFieldToResponseSuccess, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

												}
												else { //No row found in result from SQL

													if ( ServiceLogger != null ) {

														ServiceLogger.logError( "-1007", ServiceLang.translate( "No rows found in result from SQL: [%s]", strSQL ) );        

													}

													Response.setContentType( ResponseFormat.getContentType() );
													Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

													String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1007, ServiceLang.translate( "Failed to start session in database name: [%s], see the log file for more details", ConfigNativeDBConnection.strName ), true, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
													Response.getWriter().print( strResponseBuffer );

												}

												//}

											}
											else {

												if ( strSQL.isEmpty() == false ) {

													if ( ServiceLogger != null ) {

														ServiceLogger.logError( "-1006", ServiceLang.translate( "Error execute the next SQL statement [%s]", strSQL ) );        

													}

												}

												if ( ConfigNativeDBConnection.strAuthType.equals( ConfigXMLTagsSystemStartSession._Auth_Type_Engine ) ) {

													afterCheckSQL( 1, DBEngine, SystemStartSessionDBConnection, DBConnection, Request, InputServiceParameters, this.getMacrosTypes(), this.getMacrosNames(), strMacrosValues, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, LocalConfigDBConnection.strDateTimeFormat, ServiceLogger, ServiceLang );

													sucessStartSession( null, LocalConfigDBConnection, "1", SystemStartSessionDBConnection.AddFieldToResponseSuccess, SystemStartSessionDBConnection.AddFieldToResponseAny, Request, Response, ResponseFormat, strResponseFormatVersion );

												}
												else {

													Response.setContentType( ResponseFormat.getContentType() );
													Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

													String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1006, ServiceLang.translate( "Failed to connect to database name: [%s], see the log file for more details", ConfigNativeDBConnection.strName ), true, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
													Response.getWriter().print( strResponseBuffer );

												}

											}

											DBEngine.commit( DBConnection, ServiceLogger, ServiceLang );
											DBEngine.close( DBConnection, ServiceLogger, ServiceLang );
											
											intResultCode = 1;

										}
										else {

											if ( ServiceLogger != null ) {

												ServiceLogger.logError( "-1005", ServiceLang.translate( "Failed to connect to database name: [%s]", LocalConfigDBConnection.strName ) );        

											}

											try {

												Response.setContentType( ResponseFormat.getContentType() );
												Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

												String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1005, ServiceLang.translate( "Failed to connect to database name: [%s], see the log file for more details", ConfigNativeDBConnection.strName ), true, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
												Response.getWriter().print( strResponseBuffer );

											}
											catch ( Exception Ex ) {

												if ( ServiceLogger != null )
													ServiceLogger.logException( "-1025", Ex.getMessage(), Ex ); 
												else if ( OwnerLogger != null )
													OwnerLogger.logException( "-1025", Ex.getMessage(), Ex );

											}

										}

									}
									catch ( Exception Ex ) {

										if ( ServiceLogger != null )
											ServiceLogger.logException( "-1024", Ex.getMessage(), Ex ); 
										else if ( OwnerLogger != null )
											OwnerLogger.logException( "-1024", Ex.getMessage(), Ex );

									}

								}
								else {

									try {

										Response.setContentType( ResponseFormat.getContentType() );
										Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

										String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1004, ServiceLang.translate( "The database engine name [%s] version [%s] not found", ConfigNativeDBConnection.strEngine, ConfigNativeDBConnection.strEngineVersion ), true, strResponseFormatVersion, ConfigNativeDBConnection.strDateTimeFormat, ConfigNativeDBConnection.strDateFormat, ConfigNativeDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
										Response.getWriter().print( strResponseBuffer );

									}
									catch ( Exception Ex ) {

										if ( ServiceLogger != null )
											ServiceLogger.logException( "-1023", Ex.getMessage(), Ex ); 
										else if ( OwnerLogger != null )
											OwnerLogger.logException( "-1023", Ex.getMessage(), Ex );

									}

								}

							}

						}
						else {

							try {

								Response.setContentType( ResponseFormat.getContentType() );
								Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

								String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1003, ServiceLang.translate( "The database connection name [%s] not found", strPostDBConnection ), true, strResponseFormatVersion, (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
								Response.getWriter().print( strResponseBuffer );

							}
							catch ( Exception Ex ) {

								if ( ServiceLogger != null )
									ServiceLogger.logException( "-1022", Ex.getMessage(), Ex ); 
								else if ( OwnerLogger != null )
									OwnerLogger.logException( "-1022", Ex.getMessage(), Ex );

							}

						}

					}
					else {

						try {

							Response.setContentType( ResponseFormat.getContentType() );
							Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

							String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1002, ServiceLang.translate( "The session already started" ), true, strResponseFormatVersion, (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
							Response.getWriter().print( strResponseBuffer );

						}
						catch ( Exception Ex ) {

							if ( ServiceLogger != null )
								ServiceLogger.logException( "-1021", Ex.getMessage(), Ex ); 
							else if ( OwnerLogger != null )
								OwnerLogger.logException( "-1021", Ex.getMessage(), Ex );

						}

					}

				}
				else {

					try {

						Response.setContentType( ResponseFormat.getContentType() );
						Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

						String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1001, ServiceLang.translate( "The [%s] parameter is required for service and was not sent or its contents is empty", ConstantsSystemStartSession._Request_DBConnection_Name ), true, strResponseFormatVersion, (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
						Response.getWriter().print( strResponseBuffer );

					}
					catch ( Exception Ex ) {

						if ( ServiceLogger != null )
							ServiceLogger.logException( "-1020", Ex.getMessage(), Ex ); 
						else if ( OwnerLogger != null )
							OwnerLogger.logException( "-1020", Ex.getMessage(), Ex );

					}

				}

			}
			else {

				String strMessage = "OwnerConfig is NULL";

				if ( ServiceLang != null )
					strMessage = ServiceLang.translate( strMessage );
				else if ( OwnerLang != null )
					strMessage = OwnerLang.translate( strMessage );

				if ( ServiceLogger != null )
					ServiceLogger.logWarning( "-1", strMessage );        
				else if ( OwnerLogger != null )
					OwnerLogger.logWarning( "-1", strMessage );        

			}

		}
		else {

			intResultCode = ServicePreExecuteResult.intResultCode;

		}

		CServicePostExecuteResult ServicePostExecuteResult = this.runServicePostExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

		if ( ServicePostExecuteResult != null ) {

			intResultCode = ServicePostExecuteResult.intResultCode;

		}
		
		return intResultCode;
	
	}

}
