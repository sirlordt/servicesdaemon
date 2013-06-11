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

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import AbstractService.CServicePreExecuteResult;
import AbstractService.ConstantsServicesTags;
import AbstractService.DefaultConstantsServices;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CClassPathLoader;
import CommonClasses.CDBConnectionsManager;
import CommonClasses.CSecurityTokensManager;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.CSessionInfoManager;
import CommonClasses.DefaultConstantsServicesDaemon;
import DBServicesManager.CConfigDBConnection;
import DBServicesManager.CDBServicesManagerConfig;

public class CSystemEndSession extends CAbstractService {

	protected CSystemEndSessionConfig SystemEndSessionConfig = null;
	
    public final static String getJarFolder() {

        String name =  CSystemEndSession.class.getCanonicalName().replace( '.', '/' );

        String s = CSystemEndSession.class.getClass().getResource( "/" + name + ".class" ).toString();

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

	public CSystemEndSession() {
	}

	@Override
	public boolean InitializeService( CServicesDaemonConfig ServicesDaemonConfig, CAbstractConfigLoader OnwerConfig ) { // Alternate manual contructor

		boolean bResult = false; 
		
		super.InitializeService( ServicesDaemonConfig, OnwerConfig );
		
		try {
			
			this.bAuthRequired = true;
			this.strJarRunningPath = getJarFolder();
			DefaultConstantsSystemEndSession.strDefaultRunningPath = this.strJarRunningPath;
			this.strServiceName = "System.End.Session";
			this.strServiceVersion = "0.0.0.1";

			this.SetupService( DefaultConstantsSystemEndSession.strDefaultMainFileLog, DefaultConstantsSystemEndSession.strDefaultRunningPath + DefaultConstantsServices.strDefaultLangsDir + DefaultConstantsSystemEndSession.strDefaultMainFile + "." + ServicesDaemonConfig.strDefaultLang ); //Init the Logger and Lang

			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Running dir: [%s]", this.strJarRunningPath ) );        
			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Version: [%s]", this.strServiceVersion ) );        

			CClassPathLoader ClassPathLoader = new CClassPathLoader( ServiceLogger, ServiceLang );

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPreExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPostExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePostExecute();

			SystemEndSessionConfig = CSystemEndSessionConfig.getSystemEndSessionConfig( ServicesDaemonConfig, ( CDBServicesManagerConfig ) OwnerConfig );

			if ( SystemEndSessionConfig.LoadConfig( DefaultConstantsSystemEndSession.strDefaultRunningPath + DefaultConstantsSystemEndSession.strDefaultConfFile, ServiceLang, ServiceLogger ) == true ) {

				bResult = true;

				this.strServiceDescription = ServiceLang.Translate( "Allow end session in database from a security token id, the security token id must be turn invalid for next call to services" );

				ArrayList< CInputServiceParameter > ServiceInputParameters = new ArrayList< CInputServiceParameter >();

				CInputServiceParameter InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormat, false, ConstantsServicesTags._RequestResponseFormatType, ConstantsServicesTags._RequestResponseFormatLength, TParameterScope.IN, ServiceLang.Translate( "Response format name, example: XML-DATAPACKET, CSV, JSON" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormatVersion, false, ConstantsServicesTags._RequestResponseFormatVersionType, ConstantsServicesTags._RequestResponseFormatVersionLength, TParameterScope.IN, ServiceLang.Translate( "Response format version, example: 1.1" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestServiceName, true, ConstantsServicesTags._RequestServiceNameType, ConstantsServicesTags._RequestServiceNameLength, TParameterScope.IN, ServiceLang.Translate( "Service Name" ) );

				ServiceInputParameters.add( InputParameter );

				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestSecurityTokenID, true, ConstantsServicesTags._RequestSecurityTokenIDType, "0", TParameterScope.IN, ServiceLang.Translate( "Security token obtained with a start session service call" ) );

				ServiceInputParameters.add( InputParameter );

				GroupsInputParametersService.put( ConstantsServicesTags._Default, ServiceInputParameters );

			};
	        
		}
		catch ( Exception Ex ) {

			bResult = false;
			
			if ( OwnerLogger != null )
        		OwnerLogger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}
		
	    return bResult;
		
	}
	
	@Override
	public int ExecuteService(int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion) { 

		int intResultCode = -1000;
		
		if ( this.CheckServiceInputParameters( GroupsInputParametersService.get( ConstantsServicesTags._Default ), Request, Response, ResponseFormat, strResponseFormatVersion ) == true ) {
			
			CServicePreExecuteResult ServicePreExecuteResult = this.RunServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

				try {

					CSessionInfoManager SessionInfoManager = CSessionInfoManager.getSessionInfoManager();

					CConfigDBConnection LocalConfigDBConnection = SessionInfoManager.getConfigDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang ); 

					if ( LocalConfigDBConnection != null ) {

						CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ); 

						if ( DBEngine != null ) {

							CDBConnectionsManager DBConnectionsManager = CDBConnectionsManager.getDBConnectionManager();

							ArrayList<String> TransactionsID = new ArrayList<String>(); 
							ArrayList<String> TransactionsIDFromManager = DBConnectionsManager.getTransactionsIDFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );

							if ( TransactionsIDFromManager != null )
								TransactionsID.addAll( TransactionsIDFromManager );

							SessionInfoManager.removeSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );

							CSecurityTokensManager SecurityTokensManager = CSecurityTokensManager.getSecurityTokensManager();

							SecurityTokensManager.removeSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );

							boolean bResult = true;

							for ( String strCurrentTransactionID : TransactionsID ) {

								Semaphore DBConnectionSemaphore = DBConnectionsManager.getDBConnectionSemaphore( strCurrentTransactionID, ServiceLogger, ServiceLang );
								Connection DBConnection = DBConnectionsManager.getDBConnection( strCurrentTransactionID, ServiceLogger, ServiceLang );

								if ( DBConnectionSemaphore != null ) {

									try {

										DBConnectionsManager.removeDBConnectionByTransactionId( strCurrentTransactionID, ServiceLogger, ServiceLang );

										DBConnectionSemaphore.acquire(); //Blocks another threads to use this connection

										try {  

											DBEngine.rollback( DBConnection, ServiceLogger, ServiceLang );

											DBEngine.close( DBConnection, ServiceLogger, ServiceLang );

										}
										catch ( Exception Ex ) {

											bResult = false;

											if ( ServiceLogger != null )
												ServiceLogger.LogException( "-1024", Ex.getMessage(), Ex ); 
											else if ( OwnerLogger != null )
												OwnerLogger.LogException( "-1024", Ex.getMessage(), Ex );

										}

										DBConnectionSemaphore.release(); //Release another threads to use this connection

									}
									catch ( Exception Ex ) {

										bResult = false;

										if ( ServiceLogger != null )
											ServiceLogger.LogException( "-1023", Ex.getMessage(), Ex ); 
										else if ( OwnerLogger != null )
											OwnerLogger.LogException( "-1023", Ex.getMessage(), Ex );

									}

								}                        
								else {

									if ( ServiceLogger != null ) {

										ServiceLogger.LogError( "-1004", ServiceLang.Translate( "The database connection semaphore not found for transaction id: [%s]", strCurrentTransactionID ) );        

									}


									bResult = false;

								}

							}  //end for

							try {

								if ( bResult == true ) {

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", 1, ServiceLang.Translate( "Success end session for security token id: [%s]", strSecurityTokenID ), false, strResponseFormatVersion );
									Response.getWriter().print( strResponseBuffer );

									intResultCode = 1;

								}
								else {

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1004, ServiceLang.Translate( "Failed to end session for security token id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion );
									Response.getWriter().print( strResponseBuffer );

								}

							}
							catch ( Exception Ex ) {

								if ( ServiceLogger != null )
									ServiceLogger.LogException( "-1022", Ex.getMessage(), Ex ); 
								else if ( OwnerLogger != null )
									OwnerLogger.LogException( "-1022", Ex.getMessage(), Ex );

							}

						}
						else {

							try {

								if ( ServiceLogger != null ) {

									ServiceLogger.LogError( "-1003", ServiceLang.Translate( "The database engine name [%s] version [%s] not found", LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ) );        

								}

								Response.setContentType( ResponseFormat.getContentType() );
								Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

								String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1003, ServiceLang.Translate( "Failed to end session for security token id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion );
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

						if ( ServiceLogger != null ) {

							ServiceLogger.LogError( "-1002", ServiceLang.Translate( "Cannot locate in session the database connection config for the security token id: [%s]", strSecurityTokenID ) );        

						}

						Response.setContentType( ResponseFormat.getContentType() );
						Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

						String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1002, ServiceLang.Translate( "Failed to end session for security token id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion );
						Response.getWriter().print( strResponseBuffer );

					}

				}
				catch ( Exception Ex ) {

					if ( ServiceLogger != null )
						ServiceLogger.LogException( "-1020", Ex.getMessage(), Ex ); 
					else if ( OwnerLogger != null )
						OwnerLogger.LogException( "-1020", Ex.getMessage(), Ex );

				}  

			}
			else {

				intResultCode = ServicePreExecuteResult.intResultCode;

			}

			CServicePostExecuteResult ServicePostExecuteResult = this.RunServicePostExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePostExecuteResult != null ) {

				intResultCode = ServicePostExecuteResult.intResultCode;

			}

		}

		return intResultCode;
	
	}

}
