package SystemRollbackTransaction;

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
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.CSessionInfoManager;
import CommonClasses.DefaultConstantsServicesDaemon;
import DBServicesManager.CConfigDBConnection;
import DBServicesManager.CDBServicesManagerConfig;

public class CSystemRollbackTransaction extends CAbstractService {

	protected CSystemRollbackTransactionConfig SystemRollbackTransactionConfig = null;
	
    public final static String getJarFolder() {

        String name =  CSystemRollbackTransaction.class.getCanonicalName().replace( '.', '/' );

        String s = CSystemRollbackTransaction.class.getClass().getResource( "/" + name + ".class" ).toString();

        s = s.replace( '/', File.separatorChar );

        if ( s.indexOf(".jar") >= 0 )
           s = s.substring( 0, s.indexOf(".jar") + 4 );
        else
           s = s.substring( 0, s.indexOf(".class") );

        if ( s.indexOf( "jar:file:\\" )  == 0 ) { 

        	s = s.substring( 10 );

        }
        else if ( s.indexOf( "file:\\" )  == 0 ) {

        	s = s.substring( 6 );

        }

        //s = s.substring( s.indexOf(':') + 2 );

        return s.substring( 0, s.lastIndexOf( File.separatorChar ) + 1 );

    }

    public CSystemRollbackTransaction() {
	}

	@Override
	public boolean InitializeService( CServicesDaemonConfig ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = false; 
		
		super.InitializeService( ServicesDaemonConfig, OwnerConfig );

		try {
		
			this.bAuthRequired = true;
			this.strJarRunningPath = getJarFolder();
			DefaultConstantsSystemRollbackTransaction.strDefaultRunningPath = this.strJarRunningPath;
			this.strServiceName = "System.Rollback.Transaction";
			this.strServiceVersion = "0.0.0.1";

			this.SetupService( DefaultConstantsSystemRollbackTransaction.strDefaultMainFileLog, DefaultConstantsSystemRollbackTransaction.strDefaultRunningPath + DefaultConstantsServices.strDefaultLangsDir + DefaultConstantsSystemRollbackTransaction.strDefaultMainFile + "." + ServicesDaemonConfig.strDefaultLang ); //Init the Logger and Lang
			
			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Running dir: [%s]", this.strJarRunningPath ) );        
			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Version: [%s]", this.strServiceVersion ) );        

			CClassPathLoader ClassPathLoader = new CClassPathLoader( ServiceLogger, ServiceLang );

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPreExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPostExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePostExecute();

			SystemRollbackTransactionConfig = CSystemRollbackTransactionConfig.getSystemRollbackTransactionConfig( ServicesDaemonConfig, ( CDBServicesManagerConfig ) OwnerConfig );

			if ( SystemRollbackTransactionConfig.LoadConfig( DefaultConstantsSystemRollbackTransaction.strDefaultRunningPath + DefaultConstantsSystemRollbackTransaction.strDefaultConfFile, ServiceLang, ServiceLogger ) == true ) {

				bResult = true;

				this.strServiceDescription = ServiceLang.Translate( "Allow rollback transaction in database from a transaction id" );

				ArrayList< CInputServiceParameter > ServiceInputParameters = new ArrayList< CInputServiceParameter >();

				CInputServiceParameter InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormat, false, ConstantsServicesTags._RequestResponseFormatType, ConstantsServicesTags._RequestResponseFormatLength, TParameterScope.IN, ServiceLang.Translate( "Response format name, example: XML-DATAPACKET, CSV, JSON" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormatVersion, false, ConstantsServicesTags._RequestResponseFormatVersionType, ConstantsServicesTags._RequestResponseFormatVersionLength, TParameterScope.IN, ServiceLang.Translate( "Response format version, example: 1.1" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestServiceName, true, ConstantsServicesTags._RequestServiceNameType, ConstantsServicesTags._RequestServiceNameLength, TParameterScope.IN, ServiceLang.Translate( "Service Name" ) );

				ServiceInputParameters.add( InputParameter );

				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestSecurityTokenID, true, ConstantsServicesTags._RequestSecurityTokenIDType, "0", TParameterScope.IN, ServiceLang.Translate( "Security token obtained with a start session service call" ) );

				ServiceInputParameters.add( InputParameter );

				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestTransactionID, true, ConstantsServicesTags._RequestTransactionIDType, "0", TParameterScope.IN, ServiceLang.Translate( "Transaction id obtained with a start transaction service call" ) );

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
	public int ExecuteService( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) { 

		int intResultCode = -1000;
		
		if ( this.CheckServiceInputParameters( GroupsInputParametersService.get( ConstantsServicesTags._Default ), Request, Response, ResponseFormat, strResponseFormatVersion ) == true ) {
			
			CServicePreExecuteResult ServicePreExecuteResult = this.RunServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

				try {

					String strTransactionID = ( String ) Request.getParameter( ConstantsServicesTags._RequestTransactionID );

					CDBConnectionsManager DBConnectionsManager = CDBConnectionsManager.getDBConnectionManager();

					Connection DBConnection = DBConnectionsManager.getDBConnection( strTransactionID, ServiceLogger, ServiceLang );

					if ( DBConnection != null ) {

						CSessionInfoManager SessionInfoManager = CSessionInfoManager.getSessionInfoManager();

						CConfigDBConnection LocalConfigDBConnection = SessionInfoManager.getConfigDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang ); 

						if ( LocalConfigDBConnection != null ) {

							CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ); 

							if ( DBEngine != null ) {

								Semaphore DBConnectionSemaphore = DBConnectionsManager.getDBConnectionSemaphore( strTransactionID, ServiceLogger, ServiceLang );

								if ( DBConnectionSemaphore != null ) {

									try {

										DBConnectionSemaphore.acquire(); //Blocks another threads to use this connection

										try {  

											DBEngine.rollback( DBConnection, ServiceLogger, ServiceLang );

											Response.setContentType( ResponseFormat.getContentType() );
											Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

											String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", strTransactionID, 1, ServiceLang.Translate( "Success rollback transaction for id: [%s]", strTransactionID ), false, strResponseFormatVersion );
											Response.getWriter().print( strResponseBuffer );

											intResultCode = 1;

										}
										catch ( Exception Ex ) {

											if ( ServiceLogger != null )
												ServiceLogger.LogException( "-1024", Ex.getMessage(), Ex ); 
											else if ( OwnerLogger != null )
												OwnerLogger.LogException( "-1024", Ex.getMessage(), Ex );

										}

										DBConnectionSemaphore.release(); //Release another threads to use this connection

									}
									catch ( Exception Ex ) {

										if ( ServiceLogger != null )
											ServiceLogger.LogException( "-1023", Ex.getMessage(), Ex ); 
										else if ( OwnerLogger != null )
											OwnerLogger.LogException( "-1023", Ex.getMessage(), Ex );


									}


								}                        
								else {

									try {

										if ( ServiceLogger != null ) {

											ServiceLogger.LogError( "-1004", ServiceLang.Translate( "The database connection semaphore not found for transaction id: [%s]", strTransactionID ) );        

										}

										Response.setContentType( ResponseFormat.getContentType() );
										Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

										String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1004, ServiceLang.Translate( "Failed to rollback transaction for id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion );
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

									if ( ServiceLogger != null ) {

										ServiceLogger.LogError( "-1003", ServiceLang.Translate( "The database engine name [%s] version [%s] not found", LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ) );        

									}

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1003, ServiceLang.Translate( "Failed to rollback transaction for id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion );
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

								ServiceLogger.LogError( "-1002", ServiceLang.Translate( "Cannot locate in session the database connection config for the security token: [%s]", strSecurityTokenID ) );        

							}

							Response.setContentType( ResponseFormat.getContentType() );
							Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

							String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1002, ServiceLang.Translate( "Failed to rollback transaction for id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion );
							Response.getWriter().print( strResponseBuffer );

						}

					}
					else {

						if ( ServiceLogger != null ) {

							ServiceLogger.LogError( "-1001", ServiceLang.Translate( "No found database connection from transaction id: [%s]", strTransactionID ) );        

						}

						Response.setContentType( ResponseFormat.getContentType() );
						Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

						String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1001, ServiceLang.Translate( "Failed to rollback transaction for id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion );
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
