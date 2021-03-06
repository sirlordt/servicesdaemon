package SystemDatabaseInfo;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AbstractDBEngine.IAbstractDBConnection;
import AbstractDBEngine.CAbstractDBEngine;
import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import AbstractService.CServicePreExecuteResult;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CClassPathLoader;
import CommonClasses.CConfigNativeDBConnection;
import CommonClasses.CResultDataSet;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.CNativeSessionInfoManager;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsMessagesCodes;
import DBCommonClasses.CDBAbstractService;

public class CSystemDatabaseInfo extends CDBAbstractService {
	
    public CSystemDatabaseInfo() {
    	
    	super();
    	
    }
    
	@Override
	public boolean initializeService( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = super.initializeService( ServicesDaemonConfig, OwnerConfig );
		
		try {
		
			this.bAuthRequired = true;
			this.strRunningPath = net.maindataservices.Utilities.getJarFolder( this.getClass() );
			this.strServiceName = "System.Database.Info";
			this.strServiceVersion = "0.0.0.1";

			this.setupService( ConstantsService._Main_File_Log, this.strRunningPath + ConstantsCommonClasses._Langs_Dir + ConstantsService._Main_File + "." + ConstantsCommonClasses._Lang_Ext ); //Init the Logger and Lang

			ServiceLogger.logMessage( "1", ServiceLang.translate( "Running dir: [%s]", this.strRunningPath ) );        
			ServiceLogger.logMessage( "1", ServiceLang.translate( "Version: [%s]", this.strServiceVersion ) );        

			CClassPathLoader ClassPathLoader = new CClassPathLoader();

			ClassPathLoader.loadClassFiles( this.strRunningPath + ConstantsCommonClasses._Pre_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang  );

			this.loadAndRegisterServicePreExecute();

			ClassPathLoader.loadClassFiles( this.strRunningPath + ConstantsCommonClasses._Post_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang  );

			this.loadAndRegisterServicePostExecute();

			this.strServiceDescription = ServiceLang.translate( "Provides information on the database and the DBMS features" );

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

		}
		catch ( Exception Ex ) {

			bResult = false;
			
			if ( OwnerLogger != null )
        		OwnerLogger.logException( "-1010", Ex.getMessage(), Ex );
			
		}

		return bResult;
		
	}
    
	@Override
	public int executeService(int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

		int intResultCode = -1000;

		CNativeSessionInfoManager SessionInfoManager = CNativeSessionInfoManager.getSessionInfoManager();

		CConfigNativeDBConnection LocalConfigDBConnection = null;

		if ( SessionInfoManager != null )
			LocalConfigDBConnection = SessionInfoManager.getConfigNativeDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang ); 

		if ( this.checkServiceInputParameters( GroupsInputParametersService.get( ConstantsCommonClasses._Default ), Request, Response, ResponseFormat, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang ) == true ) {

			CServicePreExecuteResult ServicePreExecuteResult = this.runServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

				if ( strSecurityTokenID != null && strSecurityTokenID.isEmpty() == false ) {

					try { 
						
						if ( LocalConfigDBConnection != null ) {

							CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ); 

							if ( DBEngine != null ) {

								IAbstractDBConnection DBConnection = DBEngine.getDBConnection( LocalConfigDBConnection.getDBEngineConfigConnection( false ), ServiceLogger, ServiceLang );

								if ( DBConnection != null ) {
									
									HashMap<String,String> ConfiguredValues = new HashMap<String,String>();
									ConfiguredValues.put( "configuredDriverNameClass", LocalConfigDBConnection.strDriver );
									ConfiguredValues.put( "configuredEngineName", LocalConfigDBConnection.strEngine );
									ConfiguredValues.put( "configuredEngineVersion", LocalConfigDBConnection.strEngineVersion );
									ConfiguredValues.put( "configuredIP", LocalConfigDBConnection.strIP );
									ConfiguredValues.put( "configuredPort", Integer.toString( LocalConfigDBConnection.intPort ) );
									ConfiguredValues.put( "configuredDatabaseName", LocalConfigDBConnection.strDatabase );
									ConfiguredValues.put( "configuredDummySQL", LocalConfigDBConnection.strDummySQL );
									ConfiguredValues.put( "configuredSessionUser", LocalConfigDBConnection.strSessionUser );
									ConfiguredValues.put( "configuredTransacionUser", LocalConfigDBConnection.strTransactionUser );
									ConfiguredValues.put( "configuredDateFormat", LocalConfigDBConnection.strDateFormat );
									ConfiguredValues.put( "configuredTimeFormat", LocalConfigDBConnection.strTimeFormat );
									ConfiguredValues.put( "configuredDateTimeFormat", LocalConfigDBConnection.strDateTimeFormat );
									
									CResultDataSet Result = DBEngine.getDatabaseInfo( DBConnection, ConfiguredValues, ServiceLogger, ServiceLang );

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									if ( Result.intCode < 0 && ( Result.strDescription == null || Result.strDescription.isEmpty() ) )
										Result.strDescription = ServiceLang.translate( "No information available for the database name: [%s]", LocalConfigDBConnection.strName ); 
									
									ResponseFormat.formatResultSet( Response, Result, DBEngine, 1000, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
									
								}
								else {

									if ( ServiceLogger != null ) {

										ServiceLogger.logError( "-1003", ServiceLang.translate( "Failed to connect to database name: [%s]", LocalConfigDBConnection.strName ) );        

									}

									try {

										Response.setContentType( ResponseFormat.getContentType() );
										Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

										String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1003, ServiceLang.translate( "Failed to connect to database name: [%s], see the log file for more details", LocalConfigDBConnection.strName ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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

									if ( ServiceLogger != null ) {

										ServiceLogger.logError( "-1002", ServiceLang.translate( "The database engine name [%s] version [%s] not found", LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion, LocalConfigDBConnection.strName ) );        

									}

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1002, ServiceLang.translate( "Failed to start transaction for security token: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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

								ServiceLogger.logError( "-1001", ServiceLang.translate( "Cannot locate in session the database connection config for the security token: [%s]", strSecurityTokenID ) );        

							}

							Response.setContentType( ResponseFormat.getContentType() );
							Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

							String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1001, ServiceLang.translate( "Failed to start transaction for security token: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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
