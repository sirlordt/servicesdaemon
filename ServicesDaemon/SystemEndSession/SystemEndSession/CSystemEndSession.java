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
package SystemEndSession;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AbstractDBEngine.CAbstractDBConnection;
import AbstractDBEngine.CAbstractDBEngine;
import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import AbstractService.CServicePreExecuteResult;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CClassPathLoader;
import CommonClasses.CConfigNativeDBConnection;
import CommonClasses.CNativeDBConnectionsManager;
import CommonClasses.CSecurityTokensManager;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.CNativeSessionInfoManager;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsMessagesCodes;
import DBCommonClasses.CDBAbstractService;

public class CSystemEndSession extends CDBAbstractService {

	protected CConfigSystemEndSession SystemEndSessionConfig = null;

	public CSystemEndSession() {
		
		super();
    	
	}

	@Override
	public boolean initializeService( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = false; 
		
		super.initializeService( ServicesDaemonConfig, OwnerConfig );
		
		try {
			
			this.bAuthRequired = true;
			this.strRunningPath = net.maindataservices.Utilities.getJarFolder( this.getClass() );
			this.strServiceName = "System.End.Session";
			this.strServiceVersion = "0.0.0.1";

			this.setupService( ConstantsService._Main_File_Log, this.strRunningPath + ConstantsCommonClasses._Langs_Dir + ConstantsService._Main_File + "." + ConstantsCommonClasses._Lang_Ext ); //Init the Logger and Lang

			ServiceLogger.logMessage( "1", ServiceLang.translate( "Running dir: [%s]", this.strRunningPath ) );        
			ServiceLogger.logMessage( "1", ServiceLang.translate( "Version: [%s]", this.strServiceVersion ) );        

			CClassPathLoader ClassPathLoader = new CClassPathLoader();

			ClassPathLoader.LoadClassFiles( this.strRunningPath + ConstantsCommonClasses._Pre_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang  );

			this.loadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strRunningPath + ConstantsCommonClasses._Post_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang  );

			this.loadAndRegisterServicePostExecute();

			SystemEndSessionConfig = CConfigSystemEndSession.getSystemEndSessionConfig( ServicesDaemonConfig, OwnerConfig, this.strRunningPath );

			if ( SystemEndSessionConfig.loadConfig( this.strRunningPath + ConstantsService._Conf_File, ServiceLang, ServiceLogger ) == true ) {

				bResult = true;

				this.strServiceDescription = ServiceLang.translate( "Allow end session in database from a security token id, the security token id must be turn invalid for next call to services" );

				ArrayList< CInputServiceParameter > ServiceInputParameters = new ArrayList< CInputServiceParameter >();

				CInputServiceParameter InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ResponseFormat, false, ConstantsCommonClasses._Request_ResponseFormat_Type, ConstantsCommonClasses._Request_ResponseFormat_Length, TParameterScope.IN, ServiceLang.translate( "Response format name, example: XML-DATAPACKET, CSV, JSON" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ResponseFormatVersion, false, ConstantsCommonClasses._Request_ResponseFormatVersion_Type, ConstantsCommonClasses._Request_ResponseFormatVersion_Length, TParameterScope.IN, ServiceLang.translate( "Response format version, example: 1.1" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ServiceName, true, ConstantsCommonClasses._Request_ServiceName_Type, ConstantsCommonClasses._Request_ServiceName_Length, TParameterScope.IN, ServiceLang.translate( "Service Name" ) );

				ServiceInputParameters.add( InputParameter );

				InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_SecurityTokenID, true, ConstantsCommonClasses._Request_SecurityTokenID_Type, ConstantsCommonClasses._Request_SecurityTokenID_Length, TParameterScope.IN, ServiceLang.translate( "Security token obtained with a start session service call" ) );

				ServiceInputParameters.add( InputParameter );

				GroupsInputParametersService.put( ConstantsCommonClasses._Default, ServiceInputParameters );

			};
	        
		}
		catch ( Exception Ex ) {

			bResult = false;
			
			if ( OwnerLogger != null )
        		OwnerLogger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
		
	    return bResult;
		
	}
	
	@Override
	public int executeService(int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion) { 

		int intResultCode = -1000;
		
		CNativeSessionInfoManager SessionInfoManager = CNativeSessionInfoManager.getSessionInfoManager();
		
		CConfigNativeDBConnection LocalConfigDBConnection = null;
		
		if ( SessionInfoManager != null )
			LocalConfigDBConnection = SessionInfoManager.getConfigNativeDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );
		
		if ( this.checkServiceInputParameters( GroupsInputParametersService.get( ConstantsCommonClasses._Default ), Request, Response, ResponseFormat, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang ) == true ) {
			
			CServicePreExecuteResult ServicePreExecuteResult = this.runServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

				try {

					//CSessionInfoManager SessionInfoManager = CSessionInfoManager.getSessionInfoManager();

					//CConfigDBConnection LocalConfigDBConnection = SessionInfoManager.getConfigDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang ); 

					if ( LocalConfigDBConnection != null ) {

						CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ); 

						if ( DBEngine != null ) {

							CNativeDBConnectionsManager DBConnectionsManager = CNativeDBConnectionsManager.getNativeDBConnectionManager();

							ArrayList<String> TransactionsID = new ArrayList<String>(); 
							ArrayList<String> TransactionsIDFromManager = DBConnectionsManager.getTransactionsIDFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );

							if ( TransactionsIDFromManager != null )
								TransactionsID.addAll( TransactionsIDFromManager );

							SessionInfoManager.removeSecurityTokenID( strSecurityTokenID, true, ServiceLogger, ServiceLang );

							CSecurityTokensManager SecurityTokensManager = CSecurityTokensManager.getSecurityTokensManager( (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Security_Manager_Name, null ) );

							SecurityTokensManager.removeSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );

							boolean bResult = true;

							for ( String strCurrentTransactionID : TransactionsID ) {

								//Semaphore DBConnectionSemaphore = DBConnectionsManager.getNativeDBConnectionSemaphore( strCurrentTransactionID, ServiceLogger, ServiceLang );
								CAbstractDBConnection DBConnection = DBConnectionsManager.getDBConnection( strCurrentTransactionID, ServiceLogger, ServiceLang );

								//if ( DBConnectionSemaphore != null ) {

									try {

										DBConnectionsManager.removeNativeDBConnectionByTransactionId( strCurrentTransactionID, ServiceLogger, ServiceLang );

										DBConnection.lockConnection( false, ServiceLogger, ServiceLang ); //Blocks another threads to use this connection
										//DBConnectionSemaphore.acquire(); //Blocks another threads to use this connection

										try {  

											DBEngine.rollback( DBConnection, ServiceLogger, ServiceLang );

											DBEngine.close( DBConnection, ServiceLogger, ServiceLang );
											
											ServiceLogger.logInfo( "0x1504", ServiceLang.translate( "Success rollback and end transaction with SessionKey: [%s], SecurityTokenID: [%s], TransactionID: [%s], Database: [%s]", LocalConfigDBConnection.strSessionKey, strSecurityTokenID, strCurrentTransactionID, LocalConfigDBConnection.strName ) );        

										}
										catch ( Exception Ex ) {

											bResult = false;

											if ( ServiceLogger != null )
												ServiceLogger.logException( "-1024", Ex.getMessage(), Ex ); 
											else if ( OwnerLogger != null )
												OwnerLogger.logException( "-1024", Ex.getMessage(), Ex );

										}

										DBConnection.unlockConnection( ServiceLogger, ServiceLang ); //Release another threads to use this connection
										//DBConnectionSemaphore.release(); //Release another threads to use this connection

									}
									catch ( Exception Ex ) {

										bResult = false;

										if ( ServiceLogger != null )
											ServiceLogger.logException( "-1023", Ex.getMessage(), Ex ); 
										else if ( OwnerLogger != null )
											OwnerLogger.logException( "-1023", Ex.getMessage(), Ex );

									}

								/*}                        
								else {

									if ( ServiceLogger != null ) {

										ServiceLogger.LogError( "-1004", ServiceLang.Translate( "The database connection semaphore not found for transaction id: [%s]", strCurrentTransactionID ) );        

									}


									bResult = false;

								}*/

							}  //end for

							try {

								if ( bResult == true ) {

									ServiceLogger.logInfo( "0x1002", ServiceLang.translate( "Success end session with SessionKey: [%s], SecurityTokenID: [%s], Database: [%s]", LocalConfigDBConnection.strSessionKey, strSecurityTokenID, LocalConfigDBConnection.strName ) );        

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", 1, ServiceLang.translate( "Success end session for security token id: [%s]", strSecurityTokenID ), false, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
									Response.getWriter().print( strResponseBuffer );
									
									intResultCode = 1;

								}
								else {

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1004, ServiceLang.translate( "Failed to end session for security token id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
									Response.getWriter().print( strResponseBuffer );

								}

							}
							catch ( Exception Ex ) {

								if ( ServiceLogger != null )
									ServiceLogger.logException( "-1022", Ex.getMessage(), Ex ); 
								else if ( OwnerLogger != null )
									OwnerLogger.logException( "-1022", Ex.getMessage(), Ex );

							}

						}
						else {

							try {

								if ( ServiceLogger != null ) {

									ServiceLogger.logError( "-1003", ServiceLang.translate( "The database engine name [%s] version [%s] not found", LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ) );        

								}

								Response.setContentType( ResponseFormat.getContentType() );
								Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

								String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1003, ServiceLang.translate( "Failed to end session for security token id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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

						if ( ServiceLogger != null ) {

							ServiceLogger.logError( "-1002", ServiceLang.translate( "Cannot locate in session the database connection config for the security token id: [%s]", strSecurityTokenID ) );        

						}

						Response.setContentType( ResponseFormat.getContentType() );
						Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

						String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1002, ServiceLang.translate( "Failed to end session for security token id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
						Response.getWriter().print( strResponseBuffer );

					}

				}
				catch ( Exception Ex ) {

					if ( ServiceLogger != null )
						ServiceLogger.logException( "-1020", Ex.getMessage(), Ex ); 
					else if ( OwnerLogger != null )
						OwnerLogger.logException( "-1020", Ex.getMessage(), Ex );

				}  

			}
			else {

				intResultCode = ServicePreExecuteResult.intResultCode;

			}

			CServicePostExecuteResult ServicePostExecuteResult = this.runServicePostExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePostExecuteResult != null ) {

				intResultCode = ServicePostExecuteResult.intResultCode;

			}

		}

		return intResultCode;
	
	}

}
