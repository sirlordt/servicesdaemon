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
package AbstractService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Utilities.Base64;

import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.InitArgsConstants;
import CommonClasses.NamesSQLTypes;
import ExtendedLogger.CExtendedLogger;

public abstract class CAbstractService {

	public enum PluginsType { PrePlugin, PostPlugin };
	
	protected boolean bAuthRequired; // true = auth requiered, false = auth NOT
	protected boolean bHiddenService; // Not show in enumerate services
	protected boolean bCheckParametersLeftovers;
	protected String strServiceName;
	protected int intServiceType;
	protected HashMap< String, ArrayList<CInputServiceParameter> > GroupsInputParametersService; // Array of InputServiceParameter class 

	protected String strServiceDescription;
	protected String strServiceAuthor;
	protected String strServiceAuthorContact;
	protected String strServiceVersion;

	protected CServicesDaemonConfig ServicesDaemonConfig;
	protected CAbstractConfigLoader OwnerConfig;
	protected CExtendedLogger OwnerLogger;
	protected CLanguage OwnerLang;

	protected String strJarRunningPath;
	protected CExtendedLogger ServiceLogger;
	protected CLanguage ServiceLang;
	
	protected ArrayList<CAbstractServicePreExecute> PreExecute = null;
	protected ArrayList<CAbstractServicePostExecute> PostExecute = null;
	
	public CAbstractService() {

	}
	
	public boolean InitializeService( CServicesDaemonConfig ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig) { // Alternate manual contructor

		boolean bResult = true;
		
		this.bCheckParametersLeftovers = true; 
		this.bAuthRequired = true; // Default auth required for services
		this.bHiddenService = false; // Default show
		this.strServiceName = "BaseClassService";
		this.intServiceType = ConstantsServicesTags._ReadService;
		this.GroupsInputParametersService = new HashMap< String, ArrayList<CInputServiceParameter> >();

		this.PreExecute = new ArrayList<CAbstractServicePreExecute>();
		this.PostExecute = new ArrayList<CAbstractServicePostExecute>();
		
		this.strServiceDescription = "BaseClassService";
		this.strServiceAuthor = "Tomás Moreno";
		this.strServiceAuthorContact = "Tomás Rafael Moreno Poggio <sirlordt@gmail.com>";
        this.strServiceVersion = "0.0.0.1";
		
		this.ServicesDaemonConfig = ServicesDaemonConfig;
		this.OwnerConfig = OwnerConfig;
		
		if ( OwnerConfig != null ) {   

			this.OwnerLogger = OwnerConfig.Logger;
			this.OwnerLang = OwnerConfig.Lang;
		
		}

		this.strJarRunningPath = "";
        this.ServiceLogger = null; 		
        this.ServiceLang = null; 		
		
		return bResult;
		
	}
	
	public void SetupService() {
		
		SetupService( this.strServiceName, strJarRunningPath + DefaultConstantsServices.strDefaultLangsDir + this.strServiceName + "." + ServicesDaemonConfig.strDefaultLang );
		
	}

	public void SetupService( String strLoggerName, String strLangFileName ) {
		
        this.ServiceLogger = CExtendedLogger.getLogger( strLoggerName );
        ServiceLogger.SetupLogger( ServicesDaemonConfig.InitArgs.contains( InitArgsConstants._LogToScreen ), strJarRunningPath + DefaultConstantsServices.strDefaultLogsSystemDir, this.strServiceName + ".log", ServicesDaemonConfig.strClassNameMethodName, ServicesDaemonConfig.bExactMatch, ServicesDaemonConfig.LoggingLevel.toString() );
		
		this.ServiceLang = CLanguage.getLanguage( ServiceLogger, strLangFileName );
		
	}
	
	public boolean LoadAndRegisterServicePreExecute() {
		
		boolean bResult = false;
		
    	try {

			PreExecute.clear();
    		
			int intCountPlugins = 0;

			ServiceLoader<CAbstractServicePreExecute> sl = ServiceLoader.load( CAbstractServicePreExecute.class );
			sl.reload();

			Iterator<CAbstractServicePreExecute> it = sl.iterator();

			while ( it.hasNext() ) {

				try {
					
					CAbstractServicePreExecute ServicePluginInstance = it.next();

					if ( ServicePluginInstance.InitializePreExecute( this.strServiceName, ServicesDaemonConfig, OwnerLogger, OwnerLang, ServiceLogger, ServiceLang ) == true ) {
					   
						PreExecute.add( ServicePluginInstance );

						intCountPlugins += 1;

						String strMessage = "Registered pre execute service name: [%s] version: [%s]";

						if ( ServiceLang != null )
							strMessage = ServiceLang.Translate( strMessage, ServicePluginInstance.getName(), ServicePluginInstance.getVersion() );
						else if ( OwnerLang != null )
							strMessage = OwnerLang.Translate( strMessage, ServicePluginInstance.getName(), ServicePluginInstance.getVersion() );
						else 
							strMessage = String.format( strMessage, ServicePluginInstance.getName(), ServicePluginInstance.getVersion() );

						if ( ServiceLogger != null )
							ServiceLogger.LogMessage( "1", strMessage );
						else if ( OwnerLogger != null )
							OwnerLogger.LogMessage( "1", strMessage );

					}
					
				} 
				catch ( Exception Ex ) {

					if ( ServiceLogger != null )
						ServiceLogger.LogException( "-1011", Ex.getMessage(), Ex );
				    else if ( OwnerLogger != null )
						OwnerLogger.LogException( "-1011", Ex.getMessage(), Ex );

				}

			}
    		
			
			String strMessage = "Count of pre execute service registered: [%s]";
			
			if ( ServiceLang != null )
				strMessage = ServiceLang.Translate( strMessage, Integer.toString( intCountPlugins ) );
			else if ( OwnerLang != null )
				strMessage = OwnerLang.Translate( strMessage, Integer.toString( intCountPlugins ) );
			else 
				strMessage = String.format( strMessage, Integer.toString( intCountPlugins ) );
			
			if ( ServiceLogger != null )
				ServiceLogger.LogMessage( "1", strMessage );
			else if ( OwnerLogger != null )
				OwnerLogger.LogMessage( "1", strMessage );

			bResult = intCountPlugins > 0;

    	} 
		catch ( Exception Ex ) {
	
			if ( ServiceLogger != null )
				ServiceLogger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerLogger != null )
				OwnerLogger.LogException( "-1010", Ex.getMessage(), Ex );
	
		}
		
		
		return bResult;
		
	}

	public boolean LoadAndRegisterServicePostExecute() {
		
		boolean bResult = false;
		
    	try {

			PostExecute.clear();
    		
			int intCountPlugins = 0;

			ServiceLoader<CAbstractServicePostExecute> sl = ServiceLoader.load( CAbstractServicePostExecute.class );
			sl.reload();

			Iterator<CAbstractServicePostExecute> it = sl.iterator();

			while ( it.hasNext() ) {

				try {
					
					CAbstractServicePostExecute ServicePluginInstance = it.next();

					if ( ServicePluginInstance.InitializePreExecute( this.strServiceName, ServicesDaemonConfig, OwnerLogger, OwnerLang, ServiceLogger, ServiceLang ) == true ) {
					   
						PostExecute.add( ServicePluginInstance );

						intCountPlugins += 1;

						String strMessage = "Registered post service plugin name: [%s] version: [%s]";

						if ( ServiceLang != null )
							strMessage = ServiceLang.Translate( strMessage, ServicePluginInstance.getName(), ServicePluginInstance.getVersion() );
						else if ( OwnerLang != null )
							strMessage = OwnerLang.Translate( strMessage, ServicePluginInstance.getName(), ServicePluginInstance.getVersion() );
						else 
							strMessage = String.format( strMessage, ServicePluginInstance.getName(), ServicePluginInstance.getVersion() );

						if ( ServiceLogger != null )
							ServiceLogger.LogMessage( "1", strMessage );
						else if ( OwnerLogger != null )
							OwnerLogger.LogMessage( "1", strMessage );
						
					}
					
				} 
				catch ( Exception Ex ) {

					if ( ServiceLogger != null )
						ServiceLogger.LogException( "-1011", Ex.getMessage(), Ex );
				    else if ( OwnerLogger != null )
						OwnerLogger.LogException( "-1011", Ex.getMessage(), Ex );

				}

			}
    		
			
			String strMessage = "Count of post execute service registered: [%s]";
			
			if ( ServiceLang != null )
				strMessage = ServiceLang.Translate( strMessage, Integer.toString( intCountPlugins ) );
			else if ( OwnerLang != null )
				strMessage = OwnerLang.Translate( strMessage, Integer.toString( intCountPlugins ) );
			else 
				strMessage = String.format( strMessage, Integer.toString( intCountPlugins ) );
			
			if ( ServiceLogger != null )
				ServiceLogger.LogMessage( "1", strMessage );
			else if ( OwnerLogger != null )
				OwnerLogger.LogMessage( "1", strMessage );

			bResult = intCountPlugins > 0;

    	} 
		catch ( Exception Ex ) {
	
			if ( ServiceLogger != null )
				ServiceLogger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerLogger != null )
				OwnerLogger.LogException( "-1010", Ex.getMessage(), Ex );
	
		}
		
		
		return bResult;
		
	}

	public CServicePreExecuteResult RunServicePreExecute( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String,CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

		CServicePreExecuteResult Result = null;
		
		if ( this.PreExecute.size() > 0 ) {
			
			for ( CAbstractServicePreExecute ServicePreExecute : this.PreExecute ) {
				
				Result = ServicePreExecute.PreExecute(intEntryCode, this.strServiceName, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );
	
				if ( Result.bStopNextPreExecute == true ) {
					
					break;
					
				}
				
			}
			
		}
		
		return Result;
		
	}
	
	public CServicePostExecuteResult RunServicePostExecute( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String,CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

		CServicePostExecuteResult Result = null;
		
		if ( this.PostExecute.size() > 0 ) {
			
			for ( CAbstractServicePostExecute ServicePostExecute : this.PostExecute ) {
				
				Result = ServicePostExecute.PostExecute( intEntryCode, this.strServiceName, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );
	
				if ( Result.bStopNextPostExecute == true ) {
					
					break;
					
				}
				
			}
			
		}
		
		return Result;
		
	}

	public boolean getCheckParametersLeftovers() {
		
		return bCheckParametersLeftovers;
		
	}

	public boolean getAuthRequired() {

		return this.bAuthRequired;

	}

	public boolean getHiddenService() {

		return this.bHiddenService;

	}

	public String getServiceName() {

		return this.strServiceName;

	}

	public int getServiceType() {

		return this.intServiceType;

	}

	public CInputServiceParameter getServiceInputParameters( String strKey, int Index ) {

		ArrayList<CInputServiceParameter> InputParameters = null; 
		
		if ( strKey != null && strKey.isEmpty() == false ) {
		
			InputParameters = GroupsInputParametersService.get( strKey );
		
		}	
		else {
			
	        Iterator< Entry< String, ArrayList< CInputServiceParameter > > > It = this.GroupsInputParametersService.entrySet().iterator();

	        if ( It.hasNext() ) {
        	  
	        	Entry< String,ArrayList<CInputServiceParameter> > GroupIPSEntry = It.next();
        	  
	        	InputParameters = GroupIPSEntry.getValue();
	        
	        }  
			
		}	
		
		if ( InputParameters != null && InputParameters.size() > Index ) {

			return InputParameters.get( Index );

		} 
		else {

			return null;

		}

	}
	
	public CInputServiceParameter getServiceInputParameters( String strKey, String strParamName ) {

		ArrayList<CInputServiceParameter> InputParameters = null; 
		
		if ( strKey != null && strKey.isEmpty() == false ) {
		
			InputParameters = GroupsInputParametersService.get( strKey );
		
		}	
		else {
			
	        Iterator< Entry< String, ArrayList< CInputServiceParameter > > > It = this.GroupsInputParametersService.entrySet().iterator();

	        if ( It.hasNext() ) {
        	  
	        	Entry< String,ArrayList<CInputServiceParameter> > GroupIPSEntry = It.next();
        	  
	        	InputParameters = GroupIPSEntry.getValue();
	        
	        }  
			
		}	
		
		if ( InputParameters != null ) {

		    for ( CInputServiceParameter InputParameter : InputParameters ) {

		    	if ( InputParameter.getParameterName().equals( strParamName ) ) {
		    		
		    		return InputParameter;
		    		
		    	}
		    	
		    } 
			
		} 

		return null;

	}

	public ArrayList< CInputServiceParameter > getGroupInputParametersService( String strKey ) {
		
		return GroupsInputParametersService.get( strKey );
		
	}

	public HashMap< String, ArrayList< CInputServiceParameter > > getGroupsInputParametersService() {
		
		return GroupsInputParametersService;
		
	}

	public String getServiceDescription() {

		return this.strServiceDescription;

	}

	public String getServiceAuthor() {

		return this.strServiceAuthor;

	}

	public String getServiceAuthorContact() {

		return this.strServiceAuthorContact;

	}

	public String getServiceVersion() {

		return this.strServiceVersion;

	}
	
	public String getJarRunningPath() {
		
		return strJarRunningPath;
		
	}

	public CServicesDaemonConfig getServicesDaemonConfig() {

		return this.ServicesDaemonConfig;

	}

	public void FillMissingWithAllParamsName( ArrayList<CInputServiceParameter> InputParametersService, ArrayList<String> arrMissingInputParameters ) {

		arrMissingInputParameters.clear();

		for ( CInputServiceParameter InputServiceParameter : InputParametersService ) {

			if ( InputServiceParameter.getParameterRequired() == true )
				arrMissingInputParameters.add( InputServiceParameter.getParameterName() );

		}

	}

	public CInputServiceParameter FindInputParamByName( ArrayList<CInputServiceParameter> InputParametersService, String strParamName ) {

		for (CInputServiceParameter InputServiceParameter : InputParametersService ) {

			if (strParamName.equals(InputServiceParameter.getParameterName()) == true) {

				return InputServiceParameter;

			}

		}

		return null;

	}

	public boolean CheckInputParamValue( String strParameterType, String strParameterTypeWidth, TParameterScope ParameterScope, String strInputParamActualValue ) {

		if ( strParameterType.toLowerCase().equals( NamesSQLTypes._VARCHAR ) == true ) {

			if ( strInputParamActualValue.length() <= Integer.parseInt( strParameterTypeWidth ) ) {

				return true;

			}

		} 
		else if ( strParameterType.toLowerCase().equals( NamesSQLTypes._SMALLINT ) == true ) {

			try {

				int intTmp = Integer.parseInt( strInputParamActualValue );

				if ( ( intTmp >= -32764 && intTmp <= 32764 ) || ( intTmp >= 0 && intTmp <= 65532 ) ) {

					return true;

				}

			} 
			catch ( Exception Ex ) {

	        	if ( ServiceLogger != null )
	        		ServiceLogger.LogException( "-1010", Ex.getMessage(), Ex ); 
	        	else if ( OwnerLogger != null )
	        		OwnerLogger.LogException( "-1010", Ex.getMessage(), Ex );

			}

		} 
		else if ( strParameterType.toLowerCase().equals( NamesSQLTypes._INTEGER ) == true ) {

			try {

				// int intTmp = Integer.parseInt( strParameterTypeWidth );
				long intTmp = Long.parseLong( strInputParamActualValue );

				if ( ( intTmp >= -2147483647 && intTmp <= 2147483647 ) || ( intTmp >= 0 && intTmp <= 4294967295l ) ) {

					return true;

				}

			} 
			catch ( Exception Ex ) {

	        	if ( ServiceLogger != null )
	        		ServiceLogger.LogException( "-1011", Ex.getMessage(), Ex ); 
	        	else if ( OwnerLogger != null )
	        		OwnerLogger.LogException( "-1011", Ex.getMessage(), Ex );

			}

		} 
		else if ( strParameterType.toLowerCase().equals( NamesSQLTypes._BOOLEAN ) == true ) {
		
			if ( strInputParamActualValue.toLowerCase().equals( "true" ) || strInputParamActualValue.toLowerCase().equals( "false" ) ) {
				
				return true;

			}			
		
		}
		else if ( strParameterType.toLowerCase().equals( NamesSQLTypes._BLOB ) == true ) {

			try {

			   	if ( strInputParamActualValue.isEmpty() == false ) {
				
			   		String strDecodedInfo = Base64.decode( strInputParamActualValue );

			   		if ( strDecodedInfo.isEmpty() == false && strDecodedInfo.equals( strInputParamActualValue )  == false ) {
			   			
			   		   return true;
			   		
			   		}
			   		
			   	}
				
			} 
			catch ( Exception Ex ) {

	        	if ( ServiceLogger != null )
	        		ServiceLogger.LogException( "-1012", Ex.getMessage(), Ex ); 
	        	else if ( OwnerLogger != null )
	        		OwnerLogger.LogException( "-1012", Ex.getMessage(), Ex );

			}

		} 
		else if (strParameterType.toLowerCase().equals( NamesSQLTypes._BIGINT ) == true ) {

			try {

				// int intTmp = Integer.parseInt( strParameterTypeWidth );
				long intTmp = Long.parseLong( strInputParamActualValue );

				if ( intTmp >= Long.MIN_VALUE && intTmp <= Long.MAX_VALUE ) {

					return true;

				}

			} 
			catch ( Exception Ex ) {

	        	if ( ServiceLogger != null )
	        		ServiceLogger.LogException( "-1013", Ex.getMessage(), Ex ); 
	        	else if ( OwnerLogger != null )
	        		OwnerLogger.LogException( "-1013", Ex.getMessage(), Ex );

			}

		} 
		else if ( strParameterType.toLowerCase().equals( NamesSQLTypes._CURRENCY ) == true || strParameterType.toLowerCase().equals( NamesSQLTypes._MONEY ) == true ) {

			if ( strInputParamActualValue.matches( "[0-9]+(.[0-9][0-9]?)?" ) == true ) {

				return true;

			}

		}	
		else if ( strParameterType.toLowerCase().equals( NamesSQLTypes._FLOAT ) == true || strParameterType.toLowerCase().equals( NamesSQLTypes._CURRENCY ) == true || strParameterType.toLowerCase().equals( NamesSQLTypes._DECIMAL ) == true ) {

			if ( strInputParamActualValue.matches( "[0-9]+(.[0-9]+)?" ) == true ) {

				return true;

			}

		} 
		else if ( strParameterType.toLowerCase().equals( NamesSQLTypes._TIMESTAMP ) == true ) {

			DateFormat dtf = new SimpleDateFormat( "dd/mm/yyyy HH:mm:ss" );

			try {

				dtf.parse( strInputParamActualValue );

			} 
			catch ( Exception Ex ) {

	        	if ( ServiceLogger != null )
	        		ServiceLogger.LogException( "-1014", Ex.getMessage(), Ex ); 
	        	else if ( OwnerLogger != null )
	        		OwnerLogger.LogException( "-1014", Ex.getMessage(), Ex );

			}

		} 
		else if ( strParameterType.toLowerCase().equals( NamesSQLTypes._DATE ) == true ) {

			DateFormat df = new SimpleDateFormat("dd/mm/yyyy");

			try {

				df.parse(strInputParamActualValue);

			} 
			catch (Exception Ex) {

	        	if ( ServiceLogger != null )
	        		ServiceLogger.LogException( "-1015", Ex.getMessage(), Ex ); 
	        	else if ( OwnerLogger != null )
	        		OwnerLogger.LogException( "-1015", Ex.getMessage(), Ex );

			}

		} 
		else if ( strParameterType.toLowerCase().equals( NamesSQLTypes._TIME ) == true ) {

			DateFormat tf = new SimpleDateFormat( "HH:mm:ss" );

			try {

				tf.parse(strInputParamActualValue);

			} 
			catch (Exception Ex) {

	        	if ( ServiceLogger != null )
	        		ServiceLogger.LogException( "-1016", Ex.getMessage(), Ex ); 
	        	else if ( OwnerLogger != null )
	        		OwnerLogger.LogException( "-1016", Ex.getMessage(), Ex );

			}

		}

		return false;

	}

	public boolean CheckServiceInputParameters( ArrayList<CInputServiceParameter> InputParametersService, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

		boolean bResult = true;

		ArrayList<String> arrMissingInputParameters = new ArrayList<String>();

		FillMissingWithAllParamsName( InputParametersService, arrMissingInputParameters );

		Map<String, String[]> ActualInputParam = Request.getParameterMap();

		//if ( ActualInputParam.size() <= arrServiceInputParameters.size() ) {

		Iterator<Entry<String, String[]>> it = ActualInputParam.entrySet().iterator();

		while ( it.hasNext() ) {

			Entry<String, String[]> Pairs = it.next();

			String strParamName = (String) Pairs.getKey();

			CInputServiceParameter InputParameter = FindInputParamByName( InputParametersService, strParamName );

			if ( InputParameter == null ) { // not found

				if ( bCheckParametersLeftovers == true ) {

					try {

						Response.setContentType( ResponseFormat.getContentType() );
						Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

						String strMessage = "The parameter name [%s] is not part of the input parameters of the service";
						
						if ( OwnerLang != null )
						    strMessage = OwnerLang.Translate( strMessage, strParamName );
						else
							strMessage = String.format( strMessage, strParamName );
							
						String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1015, strMessage, true, strResponseFormatVersion );
						Response.getWriter().print( strResponseBuffer );

					} 
					catch ( Exception Ex ) {

						if ( ServiceLogger != null )
							ServiceLogger.LogException( "-1015", Ex.getMessage(), Ex ); 
			        	else if ( OwnerLogger != null )
			        		OwnerLogger.LogException( "-1015", Ex.getMessage(), Ex );

					}

					bResult = false;
					break;

				}

			} 
			else {

				// Remove from missing because found in last find
				arrMissingInputParameters.remove( strParamName );

			}

			if ( InputParameter != null ) {

				String[] arrParamValues = Request.getParameterValues( strParamName );

				if ( arrParamValues.length == 1 ) {

					String strParamValue = arrParamValues[ 0 ];

					if ( strParamValue.charAt( 0 ) == '[' ) {

						//Remove begin '[' and end ']' from string
						strParamValue = strParamValue.substring( 1, strParamValue.length() - 1 );
						//Split the values
						String[] arrPValues = strParamValue.split(",");

						for ( int i = 0; i < arrPValues.length; i++ ) {

							if ( CheckInputParamValue( InputParameter.getParameterDataType(), InputParameter.getParameterDataTypeWidth(), InputParameter.getParameterScope(), arrPValues[ i ] ) == false ) {

								try {

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									String strMessage = "The paramete name [%s] position [%s] has the value [%s] is invalid";
									
									if ( OwnerLang != null )
									    strMessage = OwnerLang.Translate( strMessage, strParamName, Integer.toString( i ), arrPValues[ i ] );
									else
										strMessage = String.format( strMessage, strParamName, Integer.toString( i ), arrPValues[ i ] );
										
									String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1014, strMessage, true, strResponseFormatVersion );
									Response.getWriter().print( strResponseBuffer );

								}
								catch ( Exception Ex ) {

									if ( ServiceLogger != null )
										ServiceLogger.LogException( "-1014", Ex.getMessage(), Ex ); 
						        	else if ( OwnerLogger != null )
						        		OwnerLogger.LogException( "-1014", Ex.getMessage(), Ex );

								}

								bResult = false;
								break;

							}

						}


					}
					else if ( CheckInputParamValue( InputParameter.getParameterDataType(), InputParameter.getParameterDataTypeWidth(), InputParameter.getParameterScope(), strParamValue ) == false ) {

						try {

							Response.setContentType( ResponseFormat.getContentType() );
							Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

							String strMessage = "The parameter [%s] has the value [%s] is invalid";
							
							if ( OwnerLang != null )
							    strMessage = OwnerLang.Translate( strMessage, strParamName, strParamValue );
							else
								strMessage = String.format( strMessage, strParamName,strParamValue );

							String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1013, strMessage, true, strResponseFormatVersion );
							Response.getWriter().print( strResponseBuffer );

						}
						catch ( Exception Ex ) {

							if ( ServiceLogger != null )
								ServiceLogger.LogException( "-1013", Ex.getMessage(), Ex ); 
				        	else if ( OwnerLogger != null )
				        		OwnerLogger.LogException( "-1013", Ex.getMessage(), Ex );

						}

						bResult = false;

					}

				} 
				else {

					try {

						Response.setContentType( ResponseFormat.getContentType() );
						Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

						String strMessage = "The parameter [%s] is multivalued";
						
						if ( OwnerLang != null )
						    strMessage = OwnerLang.Translate( strMessage, strParamName );
						else
							strMessage = String.format( strMessage, strParamName );

						String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1012, strMessage, true, strResponseFormatVersion );
						Response.getWriter().print( strResponseBuffer );

					} 
					catch ( Exception Ex ) {

						if ( ServiceLogger != null )
							ServiceLogger.LogException( "-1012", Ex.getMessage(), Ex ); 
			        	else if ( OwnerLogger != null )
			        		OwnerLogger.LogException( "-1012", Ex.getMessage(), Ex );

					}

					bResult = false;

				}

			}

		}

		if ( bResult == true && arrMissingInputParameters.size() > 0 ) {

			try {

				Response.setContentType( ResponseFormat.getContentType() );
				Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

				String strMessage = "The following parameters %s are required for service and were not sent";
				
				if ( OwnerLang != null )
				    strMessage = OwnerLang.Translate( strMessage, arrMissingInputParameters.toString() );
				else
					strMessage = String.format( strMessage, arrMissingInputParameters.toString() );

				String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1011, strMessage, true, strResponseFormatVersion );
				Response.getWriter().print(strResponseBuffer);

			} 
			catch ( Exception Ex ) {

				if ( ServiceLogger != null )
					ServiceLogger.LogException( "-1011", Ex.getMessage(), Ex ); 
	        	else if ( OwnerLogger != null )
	        		OwnerLogger.LogException( "-1011", Ex.getMessage(), Ex );

			}

			bResult = false;

		}

		/*} 
		else {

			try {

				Response.setContentType( ResponseFormat.getContentType() );
				Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

				String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "",	-1010, DBServicesManagerConfig.DBServicesManagerLang.Translate( "Service [%s] expected [%s] parameters however were sent [%s]",	this.strServiceName, Integer.toString( this.arrServiceInputParameters.size() ), Integer.toString( ActualInputParam.size() ) ), true, strResponseFormatVersion );
				Response.getWriter().print( strResponseBuffer );

			} 
			catch ( Exception Ex ) {

	        	if ( ServiceLogger != null )
	        		ServiceLogger.LogException( "-1010", Ex.getMessage(), Ex ); 
	        	else
	        		DBServicesManagerConfig.DBServicesManagerLogger.LogException( "-1010", Ex.getMessage(), Ex );

			}

			bResult = false;

		}*/

		return bResult;

	}

	public abstract int ExecuteService( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String,CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion );

}
