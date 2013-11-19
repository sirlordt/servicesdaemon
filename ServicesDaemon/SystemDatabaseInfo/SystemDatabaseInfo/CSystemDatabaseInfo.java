package SystemDatabaseInfo;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

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
import CommonClasses.CResultSetResult;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.CSessionInfoManager;
import CommonClasses.DefaultConstantsServicesDaemon;
import DBServicesManager.CConfigDBConnection;

public class CSystemDatabaseInfo extends CAbstractService {

    public final static String getJarFolder() {

        String name =  CSystemDatabaseInfo.class.getCanonicalName().replace( '.', '/' );

        String s = CSystemDatabaseInfo.class.getClass().getResource( "/" + name + ".class" ).toString();

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
	
	@Override
	public boolean InitializeService( CServicesDaemonConfig ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = super.InitializeService( ServicesDaemonConfig, OwnerConfig );
		
		try {
		
			this.bAuthRequired = true;
			this.strJarRunningPath = getJarFolder();
			DefaultConstantsSystemDatabaseInfo.strDefaultRunningPath = this.strJarRunningPath;
			this.strServiceName = "System.Database.Info";
			this.strServiceVersion = "0.0.0.1";

			this.SetupService( DefaultConstantsSystemDatabaseInfo.strDefaultMainFileLog, DefaultConstantsSystemDatabaseInfo.strDefaultRunningPath + DefaultConstantsServices.strDefaultLangsDir + DefaultConstantsSystemDatabaseInfo.strDefaultMainFile + "." + ServicesDaemonConfig.strDefaultLang ); //Init the Logger and Lang

			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Running dir: [%s]", this.strJarRunningPath ) );        
			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Version: [%s]", this.strServiceVersion ) );        

			CClassPathLoader ClassPathLoader = new CClassPathLoader( ServiceLogger, ServiceLang );

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPreExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPostExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePostExecute();

			this.strServiceDescription = ServiceLang.Translate( "Provides information on the database and the DBMS features" );

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

		}
		catch ( Exception Ex ) {

			bResult = false;
			
			if ( OwnerLogger != null )
        		OwnerLogger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}

		return bResult;
		
	}
    
	@Override
	public int ExecuteService(int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

		int intResultCode = -1000;

		CSessionInfoManager SessionInfoManager = CSessionInfoManager.getSessionInfoManager();

		CConfigDBConnection LocalConfigDBConnection = null;

		if ( SessionInfoManager != null )
			LocalConfigDBConnection = SessionInfoManager.getConfigDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang ); 

		if ( this.CheckServiceInputParameters( GroupsInputParametersService.get( ConstantsServicesTags._Default ), Request, Response, ResponseFormat, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemDatabaseInfo._Global_DateTime_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemDatabaseInfo._Global_Date_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemDatabaseInfo._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang ) == true ) {

			CServicePreExecuteResult ServicePreExecuteResult = this.RunServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

				if ( strSecurityTokenID != null && strSecurityTokenID.isEmpty() == false ) {

					try { 
						
						if ( LocalConfigDBConnection != null ) {

							CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ); 

							if ( DBEngine != null ) {

								Connection DBConnection = DBEngine.getDBConnection( LocalConfigDBConnection.getDBEngineConfigConnection( false ), ServiceLogger, ServiceLang );

								if ( DBConnection != null ) {
									
									CResultSetResult Result = DBEngine.getDatabaseInfo( DBConnection, LocalConfigDBConnection.strDriver, ServiceLogger, ServiceLang );

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									if ( Result.intCode < 0 && ( Result.strDescription == null || Result.strDescription.isEmpty() ) )
										Result.strDescription = ServiceLang.Translate( "No information available for the database name: [%s]", LocalConfigDBConnection.strName ); 
									
									ResponseFormat.FormatResultSet( Response, Result, DBEngine, 1000, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
									
								}
								else {

									if ( ServiceLogger != null ) {

										ServiceLogger.LogError( "-1003", ServiceLang.Translate( "Failed to connect to database name: [%s]", LocalConfigDBConnection.strName ) );        

									}

									try {

										Response.setContentType( ResponseFormat.getContentType() );
										Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

										String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1003, ServiceLang.Translate( "Failed to connect to database name: [%s], see the log file for more details", LocalConfigDBConnection.strName ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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

										ServiceLogger.LogError( "-1002", ServiceLang.Translate( "The database engine name [%s] version [%s] not found", LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion, LocalConfigDBConnection.strName ) );        

									}

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1002, ServiceLang.Translate( "Failed to start transaction for security token: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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

								ServiceLogger.LogError( "-1001", ServiceLang.Translate( "Cannot locate in session the database connection config for the security token: [%s]", strSecurityTokenID ) );        

							}

							Response.setContentType( ResponseFormat.getContentType() );
							Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

							String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1001, ServiceLang.Translate( "Failed to start transaction for security token: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemDatabaseInfo._Global_DateTime_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemDatabaseInfo._Global_Date_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemDatabaseInfo._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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
