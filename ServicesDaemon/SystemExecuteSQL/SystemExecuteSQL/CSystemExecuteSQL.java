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
package SystemExecuteSQL;

import java.io.File;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import CommonClasses.CDBConnectionsManager;
import CommonClasses.CExpresionsFilters;
import CommonClasses.CResultSetResult;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.CSessionInfoManager;
import CommonClasses.DefaultConstantsServicesDaemon;
import DBServicesManager.CConfigDBConnection;
import DBServicesManager.CDBServicesManagerConfig;

public class CSystemExecuteSQL extends CAbstractService {

	protected CSystemExecuteSQLConfig SystemExecuteSQLConfig = null;
	
    public final static String getJarFolder() {

        String name =  CSystemExecuteSQL.class.getCanonicalName().replace( '.', '/' );

        String s = CSystemExecuteSQL.class.getClass().getResource( "/" + name + ".class" ).toString();

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
	
	public CSystemExecuteSQL() {
	}

	@Override
	public boolean InitializeService( CServicesDaemonConfig ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = false; 
		
		super.InitializeService( ServicesDaemonConfig, OwnerConfig );

		try {
		
			this.bCheckParametersLeftovers = false;
			this.bAuthRequired = true;
			this.strJarRunningPath = getJarFolder();
			DefaultConstantsSystemExecuteSQL.strDefaultRunningPath = this.strJarRunningPath;
			this.strServiceName = "System.Execute.SQL";
			this.strServiceVersion = "0.0.0.1";

			this.SetupService( DefaultConstantsSystemExecuteSQL.strDefaultMainFileLog, DefaultConstantsSystemExecuteSQL.strDefaultRunningPath + DefaultConstantsServices.strDefaultLangsDir + DefaultConstantsSystemExecuteSQL.strDefaultMainFile + "." + ServicesDaemonConfig.strDefaultLang ); //Init the Logger and Lang

			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Running dir: [%s]", this.strJarRunningPath ) );        
			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Version: [%s]", this.strServiceVersion ) );        

			CClassPathLoader ClassPathLoader = new CClassPathLoader( ServiceLogger, ServiceLang );

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPreExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPostExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePostExecute();

			SystemExecuteSQLConfig = CSystemExecuteSQLConfig.getSystemExecuteSQLConfig( ServicesDaemonConfig, ( CDBServicesManagerConfig ) OwnerConfig );

			if ( SystemExecuteSQLConfig.LoadConfig( DefaultConstantsSystemExecuteSQL.strDefaultRunningPath + DefaultConstantsSystemExecuteSQL.strDefaultConfFile, ServiceLang, ServiceLogger ) == true ) {

				bResult = true;

				this.strServiceDescription = ServiceLang.Translate( "Allow execute SQL statement using a transaction id" );

				ArrayList< CInputServiceParameter > ServiceInputParameters = new ArrayList< CInputServiceParameter >();

				CInputServiceParameter InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormat, false, ConstantsServicesTags._RequestResponseFormatType, ConstantsServicesTags._RequestResponseFormatLength, TParameterScope.IN, ServiceLang.Translate( "Response format name, example: XML-DATAPACKET, CSV, JSON" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormatVersion, false, ConstantsServicesTags._RequestResponseFormatVersionType, ConstantsServicesTags._RequestResponseFormatVersionLength, TParameterScope.IN, ServiceLang.Translate( "Response format version, example: 1.1" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsSystemExecuteSQL._Request_Commit, false, ConstantsSystemExecuteSQL._Request_Commit_Type, ConstantsSystemExecuteSQL._Request_Commit_Length, TParameterScope.IN, ServiceLang.Translate( "Commit all pending operations in context of current transaction, example: 1" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsSystemExecuteSQL._Request_InternalFetchSize, false, ConstantsSystemExecuteSQL._Request_InternalFetchSize_Type, ConstantsSystemExecuteSQL._Request_InternalFetchSize_Length, TParameterScope.IN, ServiceLang.Translate( "Adjust the internal result set fetch size (Rows) for better performance for specific SQL consult, example: 25000" ) );

				ServiceInputParameters.add( InputParameter ); 	
				
				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestServiceName, true, ConstantsServicesTags._RequestServiceNameType, ConstantsServicesTags._RequestServiceNameLength, TParameterScope.IN, ServiceLang.Translate( "Service Name" ) );

				ServiceInputParameters.add( InputParameter );

				InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestTransactionID, true, ConstantsServicesTags._RequestTransactionIDType, "0", TParameterScope.IN, ServiceLang.Translate( "Transaction id obtained with a start transaction service call" ) );

				ServiceInputParameters.add( InputParameter );

				InputParameter = new CInputServiceParameter( ConstantsSystemExecuteSQL._Request_SQL, true, ConstantsSystemExecuteSQL._Request_SQL_Type, ConstantsSystemExecuteSQL._Request_SQL_Length, TParameterScope.IN, ServiceLang.Translate( "SQL statement" ) );

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
	
	public boolean CheckExpressionsFilters( String strSessionKey, String strSQL ) {

		boolean bResult = false;
		
		CExpresionsFilters ExpresionFilters = CExpresionsFilters.getExpresionsFiltersByName( SystemExecuteSQLConfig.ExpressionsFilters, strSessionKey );

		if ( ExpresionFilters == null ) {
			
			bResult = true;
			
		}
		else {
			
			if ( ExpresionFilters.checkExpressionInFilters( strSQL, ServiceLogger ) ) {
		    
				if ( ExpresionFilters.strType == ConfigXMLTagsSystemExecuteSQL._Type_Allow )
					bResult = true;
				
			}
			else {
				
				if ( ExpresionFilters.strType == ConfigXMLTagsSystemExecuteSQL._Type_Block )
					bResult = true;
				
			}
			
		}
		
		return bResult;
		
	}
	
	public boolean ExecutePlainSQL( CConfigDBConnection ConfigDBConnection, Connection DBConnection, CAbstractDBEngine DBEngine, String strSQL, int intInternalFetchSize, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion, String strTransactionID ) {
		
		boolean bResult = false;
		
		try {
		
			SQLStatementType SQLType = DBEngine.getSQLStatementType( strSQL, ServiceLogger, ServiceLang );

			if ( SQLType == SQLStatementType.Select ) { //Select

				//int intInternalFetchSize = Integer.parseInt( OwnerConfig.getConfigValue( "Internal_Fetch_Size" ) );
				
				CResultSetResult ResultSetResult = DBEngine.ExecutePlainQuerySQL( DBConnection, strSQL, intInternalFetchSize, ServiceLogger, ServiceLang ); //SQLStatement.executeQuery( strSQL );

				if ( ResultSetResult != null ) {

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.LogInfo( "0x2501", ServiceLang.Translate( "Init response format data set" ) );
		        		else
		        			ServiceLogger.LogInfo( "0x2501", "Init response format data set" );
		        			
		            }	
					
					ResponseFormat.FormatResultSet( Response, ResultSetResult, DBEngine, intInternalFetchSize, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.LogInfo( "0x2502", ServiceLang.Translate( "End response format data set" ) );
		        		else
		        			ServiceLogger.LogInfo( "0x2502", "End response format data set" );
		        			
		            }
		            
					//Response.getWriter().print( "Hello" );

					bResult = true;
					
				}
				else {

					if ( ServiceLogger != null ) {

						ServiceLogger.LogError( "-1008", ServiceLang.Translate( "The SQL statement [%s] is invalid for transaction id: [%s]", strSQL, strTransactionID ) );        

					}

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

					String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1008, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
					Response.getWriter().print( strResponseBuffer );

				}

			}
			else { 

				CResultSetResult ResultSetResult = null;
				
				if ( SQLType == SQLStatementType.Insert )
					ResultSetResult = DBEngine.ExecutePlainInsertSQL( DBConnection, strSQL, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Update )
					ResultSetResult = DBEngine.ExecutePlainUpdateSQL( DBConnection, strSQL, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Delete )
					ResultSetResult = DBEngine.ExecutePlainDeleteSQL( DBConnection, strSQL, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Call ) {
				
					//int intInternalFetchSize = Integer.parseInt( OwnerConfig.getConfigValue( "Internal_Fetch_Size" ) );

					ResultSetResult = DBEngine.ExecutePlainCallableStatement( DBConnection, strSQL, intInternalFetchSize, ServiceLogger, ServiceLang );
				
				}	
				else if ( SQLType == SQLStatementType.DDL )
					ResultSetResult = DBEngine.ExecutePlainDDLSQL( DBConnection, strSQL, ServiceLogger, ServiceLang );

				if ( ResultSetResult != null ) {

					/*CMemoryRowSet RowSet = new CMemoryRowSet( false );

					RowSet.addField( ConstantsServicesTags._Code, ConstantsServicesTags._CodeTypeID, ConstantsServicesTags._CodeType, 0, "" );
					RowSet.addField( ConstantsServicesTags._Description, ConstantsServicesTags._DescriptionTypeID, ConstantsServicesTags._DescriptionType, ConstantsServicesTags._DescriptionLength, ConstantsServicesTags._Description );
					RowSet.addField( ConstantsSystemExecuteSQL._ResponseAffectedRows, ConstantsSystemExecuteSQL._ResponseAffectedRowsTypeID, ConstantsSystemExecuteSQL._ResponseAffectedRowsType, 0, "" );

					RowSet.setAllData( ConstantsServicesTags._Code, "1" );
					RowSet.setAllData( ConstantsServicesTags._Description, ServiceLang.Translate( "Success execute the SQL statement" ) );
					RowSet.setAllData( ConstantsSystemExecuteSQL._ResponseAffectedRows, Integer.toString( intAffectedRows ) );*/

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );
					
		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.LogInfo( "0x2501", ServiceLang.Translate( "Init response format data set" ) );
		        		else
		        			ServiceLogger.LogInfo( "0x2501", "Init response format data set" );
		        			
		            }	

		            ResponseFormat.FormatResultSet( Response, ResultSetResult, DBEngine, intInternalFetchSize, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.LogInfo( "0x2502", ServiceLang.Translate( "End response format data set" ) );
		        		else
		        			ServiceLogger.LogInfo( "0x2502", "End response format data set" );
		        			
		            }

		            //Response.getWriter().print( strResponseBuffer );

					DBEngine.CloseResultSetResultStatement( ResultSetResult, ServiceLogger, ServiceLang );
					
					bResult = true;
					
				}
				else {

					if ( ServiceLogger != null ) {

						ServiceLogger.LogError( "-1007", ServiceLang.Translate( "The SQL statement [%s] is invalid for transaction id: [%s]", strSQL, strTransactionID ) );        

					}

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

					String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1007, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
					Response.getWriter().print( strResponseBuffer );

				}

			}
		
		}
		catch ( Exception Ex ) {
			
			if ( ServiceLogger != null )
				ServiceLogger.LogException( "-1020", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.LogException( "-1020", Ex.getMessage(), Ex );
			
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
	
	public boolean ExecuteComplexSQL( CConfigDBConnection ConfigDBConnection, Connection DBConnection, CAbstractDBEngine DBEngine, String strSQL, int intInternalFetchSize, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion, String strTransactionID ) {
		
		boolean bResult = false;
		
		try {
			
			SQLStatementType SQLType = DBEngine.getSQLStatementType( strSQL, ServiceLogger, ServiceLang );

			if ( SQLType == SQLStatementType.Select ) { //Select

				//int intInternalFetchSize = Integer.parseInt( OwnerConfig.getConfigValue( "Internal_Fetch_Size" ) );
				
				ArrayList<CResultSetResult> ResultsSets = DBEngine.ExecuteComplexQueySQL( DBConnection, intInternalFetchSize, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );
				
				if ( ResultsSets != null && ResultsSets.size() > 0 ) {
					
					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.LogInfo( "0x2501", ServiceLang.Translate( "Init response format data set" ) );
		        		else
		        			ServiceLogger.LogInfo( "0x2501", "Init response format data set" );
		        			
		            }	
					
					ResponseFormat.FormatResultsSets( Response, ResultsSets, DBEngine, intInternalFetchSize, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang, 1 );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.LogInfo( "0x2502", ServiceLang.Translate( "End response format data set" ) );
		        		else
		        			ServiceLogger.LogInfo( "0x2502", "End response format data set" );
		        			
		            }

		            //Response.getWriter().print( strResponseBuffer );

					bResult = true;
					
				}
				else {
					
					if ( ServiceLogger != null ) {

						ServiceLogger.LogError( "-1010", ServiceLang.Translate( "The SQL statement [%s] not has results for transaction id: [%s]", strSQL, strTransactionID ) );        

					}

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

					String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1010, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
					Response.getWriter().print( strResponseBuffer );
					
				}
				
				DBEngine.CloseResultSetResultStatement( ResultsSets, ServiceLogger, ServiceLang );
				
			}
			else if ( SQLType != SQLStatementType.Unknown ) {

				ArrayList<CResultSetResult> ResultSetsResults = null;
				
				if ( SQLType == SQLStatementType.Insert )
					ResultSetsResults = DBEngine.ExecuteComplexInsertSQL( DBConnection, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Update )
					ResultSetsResults = DBEngine.ExecuteComplexUpdateSQL( DBConnection, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Delete )
					ResultSetsResults = DBEngine.ExecuteComplexDeleteSQL( DBConnection, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Call ) {
				
					//int intInternalFetchSize = Integer.parseInt( OwnerConfig.getConfigValue( "Internal_Fetch_Size" ) );

					ResultSetsResults = DBEngine.ExecuteComplexCallableStatement( DBConnection, intInternalFetchSize, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );

				}	
				else if ( SQLType == SQLStatementType.DDL )
					ResultSetsResults = DBEngine.ExecuteComplexDDL( DBConnection, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );

				if ( ResultSetsResults != null ) {
					
					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.LogInfo( "0x2501", ServiceLang.Translate( "Init response format data set" ) );
		        		else
		        			ServiceLogger.LogInfo( "0x2501", "Init response format data set" );
		        			
		            }	
					
					ResponseFormat.FormatResultsSets( Response, ResultSetsResults, DBEngine, intInternalFetchSize, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang, 0 );
		    		
		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.LogInfo( "0x2502", ServiceLang.Translate( "End response format data set" ) );
		        		else
		        			ServiceLogger.LogInfo( "0x2502", "End response format data set" );
		        			
		            }

		            //Response.getWriter().print( strResponseBuffer );

					DBEngine.CloseResultSetResultStatement( ResultSetsResults, ServiceLogger, ServiceLang );
					
					bResult = true;
					
				}
				else {
					
					if ( ServiceLogger != null ) {

						ServiceLogger.LogError( "-1011", ServiceLang.Translate( "The SQL statement [%s] not has results for transaction id: [%s]", strSQL, strTransactionID ) );        

					}

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

					String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1011, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
					Response.getWriter().print( strResponseBuffer );
					
				}
				
			}
			else {
				
				if ( ServiceLogger != null ) {

					ServiceLogger.LogError( "-1009", ServiceLang.Translate( "The SQL statement [%s] type is unkown for transaction id: [%s]", strSQL, strTransactionID ) );        

				}
				
			}
			
		}
		catch ( Exception Ex ) {

			if ( ServiceLogger != null )
				ServiceLogger.LogException( "-1020", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.LogException( "-1020", Ex.getMessage(), Ex );

		}
		
		return bResult;
		
	}
	
	@Override
	public int ExecuteService( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) { 

		int intResultCode = -1000;
		
		CSessionInfoManager SessionInfoManager = CSessionInfoManager.getSessionInfoManager();
		
		CConfigDBConnection LocalConfigDBConnection = null;
		
		if ( SessionInfoManager != null )
			LocalConfigDBConnection = SessionInfoManager.getConfigDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );
		
		if ( this.CheckServiceInputParameters( GroupsInputParametersService.get( ConstantsServicesTags._Default ), Request, Response, ResponseFormat, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang ) == true ) {
			
			CServicePreExecuteResult ServicePreExecuteResult = this.RunServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

				try {

					String strTransactionID = ( String ) Request.getParameter( ConstantsServicesTags._RequestTransactionID );

					CDBConnectionsManager DBConnectionsManager = CDBConnectionsManager.getDBConnectionManager();

					Connection DBConnection = DBConnectionsManager.getDBConnection( strTransactionID, ServiceLogger, ServiceLang );

					if ( DBConnection != null ) {

						//CSessionInfoManager SessionInfoManager = CSessionInfoManager.getSessionInfoManager();

						//CConfigDBConnection LocalConfigDBConnection = SessionInfoManager.getConfigDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang ); 

						if ( LocalConfigDBConnection != null ) {

							CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ); 

							if ( DBEngine != null ) {

								Semaphore DBConnectionSemaphore = DBConnectionsManager.getDBConnectionSemaphore( strTransactionID, ServiceLogger, ServiceLang );

								if ( DBConnectionSemaphore != null ) {

									String strSQL = ( String ) Request.getParameter( ConstantsSystemExecuteSQL._Request_SQL );

									if ( strSQL != null ) {

										if ( SystemExecuteSQLConfig.bLogSQLStatement )
											ServiceLogger.LogInfo( "2", strSQL );        

										if ( this.CheckExpressionsFilters( LocalConfigDBConnection.strSessionKey, strSQL ) == true ) {

											boolean bPlainSQLStatment = DBEngine.CheckPlainSQLStatement( strSQL, ServiceLogger, ServiceLang );

											DBConnectionSemaphore.acquire(); //Blocks another threads to use this connection

											if ( bPlainSQLStatment == true ) {
												
												int intInternalFetchSize = Integer.parseInt( OwnerConfig.getConfigValue( "Internal_Fetch_Size" ) );
												
												String strInternalFetchSize = Request.getParameter( ConstantsSystemExecuteSQL._Request_InternalFetchSize );
												
												if ( strInternalFetchSize != null && net.maindataservices.Utilities.CheckStringIsInteger( strInternalFetchSize, ServiceLogger ) ) {
													
													if ( Integer.parseInt( strInternalFetchSize ) > 0 )
													   intInternalFetchSize = Integer.parseInt( strInternalFetchSize );
													
												}

												if ( this.ExecutePlainSQL( LocalConfigDBConnection, DBConnection, DBEngine, strSQL, intInternalFetchSize, Response, ResponseFormat, strResponseFormatVersion, strTransactionID ) == true ) {

													intResultCode = 1;

													String strCommit = ( String ) Request.getParameter( ConstantsSystemExecuteSQL._Request_Commit );
													
													if ( LocalConfigDBConnection.bAutoCommit == false && strCommit != null && strCommit.equals( "1" ) ) {
														
														DBEngine.commit( DBConnection, ServiceLogger, ServiceLang );

														ServiceLogger.LogInfo( "0x1502", ServiceLang.Translate( "Success commit transaction with SessionKey: [%s], SecurityTokenID: [%s], TransactionID: [%s], Database: [%s]", LocalConfigDBConnection.strSessionKey, strSecurityTokenID, strTransactionID, LocalConfigDBConnection.strName ) );        
														
													}
													else if ( LocalConfigDBConnection.bAutoCommit == true ) {
														
														ServiceLogger.LogInfo( "0x1502", ServiceLang.Translate( "Success commit transaction with SessionKey: [%s], SecurityTokenID: [%s], TransactionID: [%s], Database: [%s]", LocalConfigDBConnection.strSessionKey, strSecurityTokenID, strTransactionID, LocalConfigDBConnection.strName ) );        
														
													}
													
												}	

											}
											else {

												int intInternalFetchSize = Integer.parseInt( OwnerConfig.getConfigValue( "Internal_Fetch_Size" ) );
												
												String strInternalFetchSize = Request.getParameter( ConstantsSystemExecuteSQL._Request_InternalFetchSize );
												
												if ( strInternalFetchSize != null && net.maindataservices.Utilities.CheckStringIsInteger( strInternalFetchSize, ServiceLogger ) ) {
													
													if ( Integer.parseInt( strInternalFetchSize ) > 0 )
													   intInternalFetchSize = Integer.parseInt( strInternalFetchSize );
													
												}
												
												if ( this.ExecuteComplexSQL( LocalConfigDBConnection, DBConnection, DBEngine, strSQL, intInternalFetchSize, Request, Response, ResponseFormat, strResponseFormatVersion, strTransactionID ) == true ) {

													intResultCode = 1;

													String strCommit = ( String ) Request.getParameter( ConstantsSystemExecuteSQL._Request_Commit );
													
													if ( LocalConfigDBConnection.bAutoCommit == false && strCommit != null && strCommit.equals( "1" ) ) {
														
														DBEngine.commit( DBConnection, ServiceLogger, ServiceLang );
														
													}
													
												}
												/*else {
													
													Response.setContentType( ResponseFormat.getContentType() );
													Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

													String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1006, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
													Response.getWriter().print( strResponseBuffer );
													
												}*/

											}

											DBConnectionSemaphore.release(); //Release another threads to use this connection

										}
										else {

											try {

												if ( ServiceLogger != null ) {

													ServiceLogger.LogError( "-1006", ServiceLang.Translate( "The SQL statement [%s] is blocked or not allowed by filter for transaction id: [%s]", strSQL, strTransactionID ) );        

												}

												Response.setContentType( ResponseFormat.getContentType() );
												Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

												String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1006, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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
									else {

										try {

											if ( ServiceLogger != null ) {

												ServiceLogger.LogError( "-1005", ServiceLang.Translate( "The SQL statement is null for transaction id: [%s]", strTransactionID ) );        

											}

											Response.setContentType( ResponseFormat.getContentType() );
											Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

											String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1005, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
											Response.getWriter().print( strResponseBuffer );

										}
										catch ( Exception Ex ) {

											if ( ServiceLogger != null )
												ServiceLogger.LogException( "-1024", Ex.getMessage(), Ex ); 
											else if ( OwnerLogger != null )
												OwnerLogger.LogException( "-1024", Ex.getMessage(), Ex );

										}

									}


								}                        
								else {

									try {

										if ( ServiceLogger != null ) {

											ServiceLogger.LogError( "-1004", ServiceLang.Translate( "The database connection semaphore not found for transaction id: [%s]", strTransactionID ) );        

										}

										Response.setContentType( ResponseFormat.getContentType() );
										Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

										String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1004, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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

									String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1003, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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

							String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1002, ServiceLang.Translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
							Response.getWriter().print( strResponseBuffer );

						}

					}
					else {

						if ( ServiceLogger != null ) {

							ServiceLogger.LogError( "-1001", ServiceLang.Translate( "No found transaction for id: [%s]", strTransactionID ) );        

						}

						Response.setContentType( ResponseFormat.getContentType() );
						Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

						String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1001, ServiceLang.Translate( "No found transaction for id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_DateTime_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Date_Format ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:OwnerConfig.getConfigValue( ConstantsSystemExecuteSQL._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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
