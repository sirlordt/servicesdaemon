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

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import CommonClasses.CNativeDBConnectionsManager;
import CommonClasses.CExpresionsFilters;
import CommonClasses.CResultSetResult;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.CNativeSessionInfoManager;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsMessagesCodes;
import DBCommonClasses.CDBAbstractService;

public class CSystemExecuteSQL extends CDBAbstractService {

	protected CConfigSystemExecuteSQL SystemExecuteSQLConfig = null;
	
	public CSystemExecuteSQL() {
		
		super();
    	
	}

	@Override
	public boolean initializeService( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = false; 
		
		super.initializeService( ServicesDaemonConfig, OwnerConfig );

		try {
		
			this.bCheckParametersLeftovers = false;
			this.bAuthRequired = true;
			this.strRunningPath = net.maindataservices.Utilities.getJarFolder( this.getClass() );
			this.strServiceName = "System.Execute.SQL";
			this.strServiceVersion = "0.0.0.1";

			this.setupService( ConstantsSystemExecuteSQL._Main_File_Log, this.strRunningPath + ConstantsCommonClasses._Langs_Dir + ConstantsSystemExecuteSQL._Main_File + "." + ConstantsCommonClasses._Lang_Ext ); //Init the Logger and Lang

			ServiceLogger.logMessage( "1", ServiceLang.translate( "Running dir: [%s]", this.strRunningPath ) );        
			ServiceLogger.logMessage( "1", ServiceLang.translate( "Version: [%s]", this.strServiceVersion ) );        

			CClassPathLoader ClassPathLoader = new CClassPathLoader();

			ClassPathLoader.LoadClassFiles( this.strRunningPath + ConstantsCommonClasses._Pre_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang  );

			this.loadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strRunningPath + ConstantsCommonClasses._Post_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang  );

			this.loadAndRegisterServicePostExecute();

			SystemExecuteSQLConfig = CConfigSystemExecuteSQL.getSystemExecuteSQLConfig( ServicesDaemonConfig, OwnerConfig, this.strRunningPath );

			if ( SystemExecuteSQLConfig.LoadConfig( this.strRunningPath + ConstantsSystemExecuteSQL._Conf_File, ServiceLang, ServiceLogger ) == true ) {

				bResult = true;

				this.strServiceDescription = ServiceLang.translate( "Allow execute SQL statement using a transaction id" );

				ArrayList< CInputServiceParameter > ServiceInputParameters = new ArrayList< CInputServiceParameter >();

				CInputServiceParameter InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ResponseFormat, false, ConstantsCommonClasses._Request_ResponseFormat_Type, ConstantsCommonClasses._Request_ResponseFormat_Length, TParameterScope.IN, ServiceLang.translate( "Response format name, example: XML-DATAPACKET, CSV, JSON" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ResponseFormatVersion, false, ConstantsCommonClasses._Request_ResponseFormatVersion_Type, ConstantsCommonClasses._Request_ResponseFormatVersion_Length, TParameterScope.IN, ServiceLang.translate( "Response format version, example: 1.1" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsSystemExecuteSQL._Request_Commit, false, ConstantsSystemExecuteSQL._Request_Commit_Type, ConstantsSystemExecuteSQL._Request_Commit_Length, TParameterScope.IN, ServiceLang.translate( "Commit all pending operations in context of current transaction, example: 1" ) );

				ServiceInputParameters.add( InputParameter ); 	

				InputParameter = new CInputServiceParameter( ConstantsSystemExecuteSQL._Request_InternalFetchSize, false, ConstantsSystemExecuteSQL._Request_InternalFetchSize_Type, ConstantsSystemExecuteSQL._Request_InternalFetchSize_Length, TParameterScope.IN, ServiceLang.translate( "Adjust the internal result set fetch size (Rows) for better performance for specific SQL consult, example: 25000" ) );

				ServiceInputParameters.add( InputParameter ); 	
				
				InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ServiceName, true, ConstantsCommonClasses._Request_ServiceName_Type, ConstantsCommonClasses._Request_ServiceName_Length, TParameterScope.IN, ServiceLang.translate( "Service Name" ) );

				ServiceInputParameters.add( InputParameter );

				InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_TransactionID, true, ConstantsCommonClasses._Request_TransactionID_Type, "0", TParameterScope.IN, ServiceLang.translate( "Transaction id obtained with a start transaction service call" ) );

				ServiceInputParameters.add( InputParameter );

				InputParameter = new CInputServiceParameter( ConstantsSystemExecuteSQL._Request_SQL, true, ConstantsSystemExecuteSQL._Request_SQL_Type, ConstantsSystemExecuteSQL._Request_SQL_Length, TParameterScope.IN, ServiceLang.translate( "SQL statement" ) );

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
	
	public boolean ExecutePlainSQL( CConfigNativeDBConnection ConfigDBConnection, CAbstractDBConnection DBConnection, CAbstractDBEngine DBEngine, String strSQL, int intInternalFetchSize, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion, String strTransactionID ) {
		
		boolean bResult = false;
		
		try {
		
			SQLStatementType SQLType = DBEngine.getSQLStatementType( strSQL, ServiceLogger, ServiceLang );

			if ( SQLType == SQLStatementType.Select ) { //Select

				//int intInternalFetchSize = Integer.parseInt( (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Internal_Fetch_Size, null ) );
				
				CResultSetResult ResultSetResult = DBEngine.executePlainQueryCommand( DBConnection, strSQL, intInternalFetchSize, ServiceLogger, ServiceLang ); //SQLStatement.executeQuery( strSQL );

				if ( ResultSetResult != null ) {

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.logInfo( "0x2501", ServiceLang.translate( "Init response format data set" ) );
		        		else
		        			ServiceLogger.logInfo( "0x2501", "Init response format data set" );
		        			
		            }	
					
					ResponseFormat.formatResultSet( Response, ResultSetResult, DBEngine, intInternalFetchSize, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.logInfo( "0x2502", ServiceLang.translate( "End response format data set" ) );
		        		else
		        			ServiceLogger.logInfo( "0x2502", "End response format data set" );
		        			
		            }
		            
					//Response.getWriter().print( "Hello" );

					bResult = true;
					
				}
				else {

					if ( ServiceLogger != null ) {

						ServiceLogger.logError( "-1008", ServiceLang.translate( "The SQL statement [%s] is invalid for transaction id: [%s]", strSQL, strTransactionID ) );        

					}

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

					String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1008, ServiceLang.translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
					Response.getWriter().print( strResponseBuffer );

				}

			}
			else { 

				CResultSetResult ResultSetResult = null;
				
				if ( SQLType == SQLStatementType.Insert )
					ResultSetResult = DBEngine.executePlainInsertCommand( DBConnection, strSQL, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Update )
					ResultSetResult = DBEngine.executePlainUpdateCommand( DBConnection, strSQL, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Delete )
					ResultSetResult = DBEngine.executePlainDeleteCommand( DBConnection, strSQL, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Call ) {
				
					//int intInternalFetchSize = Integer.parseInt( (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Internal_Fetch_Size, null ) );

					ResultSetResult = DBEngine.executePlainCallableStatement( DBConnection, strSQL, intInternalFetchSize, ServiceLogger, ServiceLang );
				
				}	
				else if ( SQLType == SQLStatementType.DDL )
					ResultSetResult = DBEngine.executePlainDDLCommand( DBConnection, strSQL, ServiceLogger, ServiceLang );

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
		        			ServiceLogger.logInfo( "0x2501", ServiceLang.translate( "Init response format data set" ) );
		        		else
		        			ServiceLogger.logInfo( "0x2501", "Init response format data set" );
		        			
		            }	

		            ResponseFormat.formatResultSet( Response, ResultSetResult, DBEngine, intInternalFetchSize, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.logInfo( "0x2502", ServiceLang.translate( "End response format data set" ) );
		        		else
		        			ServiceLogger.logInfo( "0x2502", "End response format data set" );
		        			
		            }

		            //Response.getWriter().print( strResponseBuffer );

					DBEngine.closeResultSetResultStatement( ResultSetResult, ServiceLogger, ServiceLang );
					
					bResult = true;
					
				}
				else {

					if ( ServiceLogger != null ) {

						ServiceLogger.logError( "-1007", ServiceLang.translate( "The SQL statement [%s] is invalid for transaction id: [%s]", strSQL, strTransactionID ) );        

					}

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

					String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1007, ServiceLang.translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
					Response.getWriter().print( strResponseBuffer );

				}

			}
		
		}
		catch ( Exception Ex ) {
			
			if ( ServiceLogger != null )
				ServiceLogger.logException( "-1020", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.logException( "-1020", Ex.getMessage(), Ex );
			
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
	
	public boolean ExecuteComplexSQL( CConfigNativeDBConnection ConfigDBConnection, CAbstractDBConnection DBConnection, CAbstractDBEngine DBEngine, String strSQL, int intInternalFetchSize, HttpServletRequest Request, HttpServletResponse Response, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion, String strTransactionID ) {
		
		boolean bResult = false;
		
		try {
			
			SQLStatementType SQLType = DBEngine.getSQLStatementType( strSQL, ServiceLogger, ServiceLang );

			if ( SQLType == SQLStatementType.Select ) { //Select

				//int intInternalFetchSize = Integer.parseInt( (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Internal_Fetch_Size, null ) );
				
				ArrayList<CResultSetResult> ResultsSets = DBEngine.executeComplexQueyCommand( DBConnection, intInternalFetchSize, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );
				
				if ( ResultsSets != null && ResultsSets.size() > 0 ) {
					
					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.logInfo( "0x2501", ServiceLang.translate( "Init response format data set" ) );
		        		else
		        			ServiceLogger.logInfo( "0x2501", "Init response format data set" );
		        			
		            }	
					
					ResponseFormat.formatResultsSets( Response, ResultsSets, DBEngine, intInternalFetchSize, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang, 1 );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.logInfo( "0x2502", ServiceLang.translate( "End response format data set" ) );
		        		else
		        			ServiceLogger.logInfo( "0x2502", "End response format data set" );
		        			
		            }

		            //Response.getWriter().print( strResponseBuffer );

					bResult = true;
					
				}
				else {
					
					if ( ServiceLogger != null ) {

						ServiceLogger.logError( "-1010", ServiceLang.translate( "The SQL statement [%s] not has results for transaction id: [%s]", strSQL, strTransactionID ) );        

					}

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

					String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1010, ServiceLang.translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
					Response.getWriter().print( strResponseBuffer );
					
				}
				
				DBEngine.closeResultSetResultStatement( ResultsSets, ServiceLogger, ServiceLang );
				
			}
			else if ( SQLType != SQLStatementType.Unknown ) {

				ArrayList<CResultSetResult> ResultSetsResults = null;
				
				if ( SQLType == SQLStatementType.Insert )
					ResultSetsResults = DBEngine.executeComplexInsertCommand( DBConnection, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Update )
					ResultSetsResults = DBEngine.executeComplexUpdateCommand( DBConnection, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Delete )
					ResultSetsResults = DBEngine.executeComplexDeleteCommand( DBConnection, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );
				else if ( SQLType == SQLStatementType.Call ) {
				
					//int intInternalFetchSize = Integer.parseInt( (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Internal_Fetch_Size, null ) );

					ResultSetsResults = DBEngine.executeComplexCallableStatement( DBConnection, intInternalFetchSize, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );

				}	
				else if ( SQLType == SQLStatementType.DDL )
					ResultSetsResults = DBEngine.executeComplexDDLCommand( DBConnection, Request, getMacrosTypes(), getMacrosNames(), getMacrosNames(), ConfigDBConnection.strDateFormat, ConfigDBConnection.strTimeFormat, ConfigDBConnection.strDateTimeFormat, strSQL, SystemExecuteSQLConfig.bLogSQLStatement, ServiceLogger, ServiceLang );

				if ( ResultSetsResults != null ) {
					
					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.logInfo( "0x2501", ServiceLang.translate( "Init response format data set" ) );
		        		else
		        			ServiceLogger.logInfo( "0x2501", "Init response format data set" );
		        			
		            }	
					
					ResponseFormat.formatResultsSets( Response, ResultSetsResults, DBEngine, intInternalFetchSize, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), true, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang, 0 );
		    		
		            if ( ServiceLogger != null ) { //Trace how much time in format data
		            	
		        		if ( ServiceLang != null )   
		        			ServiceLogger.logInfo( "0x2502", ServiceLang.translate( "End response format data set" ) );
		        		else
		        			ServiceLogger.logInfo( "0x2502", "End response format data set" );
		        			
		            }

		            //Response.getWriter().print( strResponseBuffer );

					DBEngine.closeResultSetResultStatement( ResultSetsResults, ServiceLogger, ServiceLang );
					
					bResult = true;
					
				}
				else {
					
					if ( ServiceLogger != null ) {

						ServiceLogger.logError( "-1011", ServiceLang.translate( "The SQL statement [%s] not has results for transaction id: [%s]", strSQL, strTransactionID ) );        

					}

					Response.setContentType( ResponseFormat.getContentType() );
					Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

					String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1011, ServiceLang.translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, ConfigDBConnection!=null?ConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), ConfigDBConnection!=null?ConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
					Response.getWriter().print( strResponseBuffer );
					
				}
				
			}
			else {
				
				if ( ServiceLogger != null ) {

					ServiceLogger.logError( "-1009", ServiceLang.translate( "The SQL statement [%s] type is unkown for transaction id: [%s]", strSQL, strTransactionID ) );        

				}
				
			}
			
		}
		catch ( Exception Ex ) {

			if ( ServiceLogger != null )
				ServiceLogger.logException( "-1020", Ex.getMessage(), Ex ); 
			else if ( OwnerLogger != null )
				OwnerLogger.logException( "-1020", Ex.getMessage(), Ex );

		}
		
		return bResult;
		
	}
	
	@Override
	public int executeService( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) { 

		int intResultCode = -1000;
		
		CNativeSessionInfoManager SessionInfoManager = CNativeSessionInfoManager.getSessionInfoManager();
		
		CConfigNativeDBConnection LocalConfigDBConnection = null;
		
		if ( SessionInfoManager != null )
			LocalConfigDBConnection = SessionInfoManager.getConfigNativeDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang );
		
		if ( this.checkServiceInputParameters( GroupsInputParametersService.get( ConstantsCommonClasses._Default ), Request, Response, ResponseFormat, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang ) == true ) {
			
			CServicePreExecuteResult ServicePreExecuteResult = this.runServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

				try {

					String strTransactionID = Request.getParameter( ConstantsCommonClasses._Request_TransactionID );

					CNativeDBConnectionsManager DBConnectionsManager = CNativeDBConnectionsManager.getNativeDBConnectionManager();

					CAbstractDBConnection DBConnection = DBConnectionsManager.getDBConnection( strTransactionID, ServiceLogger, ServiceLang );

					if ( DBConnection != null ) {

						//CSessionInfoManager SessionInfoManager = CSessionInfoManager.getSessionInfoManager();

						//CConfigDBConnection LocalConfigDBConnection = SessionInfoManager.getConfigDBConnectionFromSecurityTokenID( strSecurityTokenID, ServiceLogger, ServiceLang ); 

						if ( LocalConfigDBConnection != null ) {

							CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ); 

							if ( DBEngine != null ) {

								//Semaphore DBConnectionSemaphore = DBConnectionsManager.getNativeDBConnectionSemaphore( strTransactionID, ServiceLogger, ServiceLang );

								//if ( DBConnectionSemaphore != null ) {

									String strSQL = Request.getParameter( ConstantsSystemExecuteSQL._Request_SQL );

									if ( strSQL != null ) {

										if ( SystemExecuteSQLConfig.bLogSQLStatement )
											ServiceLogger.logInfo( "2", strSQL );        

										if ( this.CheckExpressionsFilters( LocalConfigDBConnection.strSessionKey, strSQL ) == true ) {

											boolean bPlainSQLStatment = DBEngine.checkPlainSQLStatement( strSQL, ServiceLogger, ServiceLang );

											DBConnection.lockConnection( false, ServiceLogger, ServiceLang ); //Blocks another threads to use this connection
											//DBConnectionSemaphore.acquire(); //Blocks another threads to use this connection

											if ( bPlainSQLStatment == true ) {
												
												int intInternalFetchSize = Integer.parseInt( (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Internal_Fetch_Size, null ) );
												
												String strInternalFetchSize = Request.getParameter( ConstantsSystemExecuteSQL._Request_InternalFetchSize );
												
												if ( strInternalFetchSize != null && net.maindataservices.Utilities.checkStringIsInteger( strInternalFetchSize, ServiceLogger ) ) {
													
													if ( Integer.parseInt( strInternalFetchSize ) > 0 )
													   intInternalFetchSize = Integer.parseInt( strInternalFetchSize );
													
												}

												if ( this.ExecutePlainSQL( LocalConfigDBConnection, DBConnection, DBEngine, strSQL, intInternalFetchSize, Response, ResponseFormat, strResponseFormatVersion, strTransactionID ) == true ) {

													intResultCode = 1;

													String strCommit = Request.getParameter( ConstantsSystemExecuteSQL._Request_Commit );
													
													if ( LocalConfigDBConnection.bAutoCommit == false && strCommit != null && strCommit.equals( "1" ) ) {
														
														DBEngine.commit( DBConnection, ServiceLogger, ServiceLang );

														ServiceLogger.logInfo( "0x1502", ServiceLang.translate( "Success commit transaction with SessionKey: [%s], SecurityTokenID: [%s], TransactionID: [%s], Database: [%s]", LocalConfigDBConnection.strSessionKey, strSecurityTokenID, strTransactionID, LocalConfigDBConnection.strName ) );        
														
													}
													else if ( LocalConfigDBConnection.bAutoCommit == true ) {
														
														ServiceLogger.logInfo( "0x1502", ServiceLang.translate( "Success commit transaction with SessionKey: [%s], SecurityTokenID: [%s], TransactionID: [%s], Database: [%s]", LocalConfigDBConnection.strSessionKey, strSecurityTokenID, strTransactionID, LocalConfigDBConnection.strName ) );        
														
													}
													
												}	

											}
											else {

												int intInternalFetchSize = Integer.parseInt( (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Internal_Fetch_Size, null ) );
												
												String strInternalFetchSize = Request.getParameter( ConstantsSystemExecuteSQL._Request_InternalFetchSize );
												
												if ( strInternalFetchSize != null && net.maindataservices.Utilities.checkStringIsInteger( strInternalFetchSize, ServiceLogger ) ) {
													
													if ( Integer.parseInt( strInternalFetchSize ) > 0 )
													   intInternalFetchSize = Integer.parseInt( strInternalFetchSize );
													
												}
												
												if ( this.ExecuteComplexSQL( LocalConfigDBConnection, DBConnection, DBEngine, strSQL, intInternalFetchSize, Request, Response, ResponseFormat, strResponseFormatVersion, strTransactionID ) == true ) {

													intResultCode = 1;

													String strCommit = Request.getParameter( ConstantsSystemExecuteSQL._Request_Commit );
													
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

											DBConnection.unlockConnection( ServiceLogger, ServiceLang ); //Release another threads to use this connection
											//DBConnectionSemaphore.release(); //Release another threads to use this connection

										}
										else {

											try {

												if ( ServiceLogger != null ) {

													ServiceLogger.logError( "-1006", ServiceLang.translate( "The SQL statement [%s] is blocked or not allowed by filter for transaction id: [%s]", strSQL, strTransactionID ) );        

												}

												Response.setContentType( ResponseFormat.getContentType() );
												Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

												String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1006, ServiceLang.translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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
									else {

										try {

											if ( ServiceLogger != null ) {

												ServiceLogger.logError( "-1005", ServiceLang.translate( "The SQL statement is null for transaction id: [%s]", strTransactionID ) );        

											}

											Response.setContentType( ResponseFormat.getContentType() );
											Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

											String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1005, ServiceLang.translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
											Response.getWriter().print( strResponseBuffer );

										}
										catch ( Exception Ex ) {

											if ( ServiceLogger != null )
												ServiceLogger.logException( "-1024", Ex.getMessage(), Ex ); 
											else if ( OwnerLogger != null )
												OwnerLogger.logException( "-1024", Ex.getMessage(), Ex );

										}

									}


								/*}                        
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

								}*/

							}
							else {

								try {

									if ( ServiceLogger != null ) {

										ServiceLogger.logError( "-1003", ServiceLang.translate( "The database engine name [%s] version [%s] not found", LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ) );        

									}

									Response.setContentType( ResponseFormat.getContentType() );
									Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

									String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1003, ServiceLang.translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strSecurityTokenID ), true, strResponseFormatVersion, LocalConfigDBConnection.strDateTimeFormat, LocalConfigDBConnection.strDateFormat, LocalConfigDBConnection.strTimeFormat, this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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

								ServiceLogger.logError( "-1002", ServiceLang.translate( "Cannot locate in session the database connection config for the security token: [%s]", strSecurityTokenID ) );        

							}

							Response.setContentType( ResponseFormat.getContentType() );
							Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

							String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1002, ServiceLang.translate( "Failed to execute SQL statement for transaction id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
							Response.getWriter().print( strResponseBuffer );

						}

					}
					else {

						if ( ServiceLogger != null ) {

							ServiceLogger.logError( "-1001", ServiceLang.translate( "No found transaction for id: [%s]", strTransactionID ) );        

						}

						Response.setContentType( ResponseFormat.getContentType() );
						Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

						String strResponseBuffer = ResponseFormat.formatSimpleMessage( "", "", -1001, ServiceLang.translate( "No found transaction for id: [%s], see the log file for more details", strTransactionID ), true, strResponseFormatVersion, LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strDateFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), LocalConfigDBConnection!=null?LocalConfigDBConnection.strTimeFormat:(String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );
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
