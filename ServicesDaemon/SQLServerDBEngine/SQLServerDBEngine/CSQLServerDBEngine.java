package SQLServerDBEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.CachedRowSet;

import net.maindataservices.Utilities;

import AbstractDBEngine.CAbstractDBConnection;
import AbstractDBEngine.CAbstractDBEngine;
import AbstractDBEngine.CDBEngineConfigNativeDBConnection;
import AbstractDBEngine.CJDBConnection;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CNamedPreparedStatement;
import CommonClasses.CResultSetResult;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import ExtendedLogger.CExtendedLogger;

public class CSQLServerDBEngine extends CAbstractDBEngine {

	public CSQLServerDBEngine() {
		
		strName = "sqlserver";
		strVersion = "2012";
		
	}

	@Override
	public CAbstractDBConnection getDBConnection( CDBEngineConfigNativeDBConnection ConfigDBConnection, CExtendedLogger Logger, CLanguage Lang) {
		
		CAbstractDBConnection DBConnection = null;
		
		try {
			
            String strDatabaseURL = "jdbc:sqlserver://" + ConfigDBConnection.strIP + ":" + ConfigDBConnection.intPort + ";databaseName=" + ConfigDBConnection.strDatabase ;

            if ( Logger != null ) {
            	
        		Logger.logMessage( "1", Lang.translate( "Trying to connect with the next URL: [%s] and user: [%s]", strDatabaseURL, ConfigDBConnection.strUser ) );        
            	
            }

            Class.forName( ConfigDBConnection.strDriver );

            Connection JDBConnection = DriverManager.getConnection( strDatabaseURL, ConfigDBConnection.strUser, ConfigDBConnection.strPassword ); 
			
            DBConnection = new CJDBConnection();
            DBConnection.setDBConnection( JDBConnection );
            
            if ( Logger != null ) {
            	
        		Logger.logMessage( "1", Lang.translate( "Database connection established to next URL: [%s] and user: [%s]", strDatabaseURL, ConfigDBConnection.strUser ) );        
            	
            }
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1015", Ex.getMessage(), Ex ); 
			
		}
		
		return DBConnection;
		
	}

	@Override
	public ResultSet executeDummyCommand( CAbstractDBConnection DBConnection, String strOptionalDummySQL, CExtendedLogger Logger, CLanguage Lang) {

		ResultSet Result = null;
		
		try {
		
			if ( strOptionalDummySQL != null && strOptionalDummySQL.isEmpty() == true ) {
				
				strOptionalDummySQL = "SELECT TOP 1 * FROM SYS.VIEWS";
				
			}
			
			Statement SQLStatement = ((Connection) DBConnection.getDBConnection()).createStatement();			
			
			Result = SQLStatement.executeQuery( strOptionalDummySQL );
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1015", Ex.getMessage(), Ex ); 

		}
	
		return Result;
		
	}

	//This method is for bug in SQL server driver, always get Resultset != null in method getGeneratedKey() must be a null for tables has not identity column type
	public boolean IsDummyGeneratedKeyResult( ResultSet Resultset, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;

		try {

			ResultSetMetaData rsmd = Resultset.getMetaData();
			int intColumnCount = rsmd.getColumnCount();

			if ( Resultset.next() ) {

				do {

					for ( int intColumnIndex = 1; intColumnIndex <= intColumnCount; intColumnIndex++ ) {

						String strKey = Resultset.getString( intColumnIndex );
						
						if ( strKey == null || strKey.isEmpty() || Float.parseFloat( strKey ) == 0 ) {
							
							bResult = true;
							break;
							
						}
					
					}
				
				} while ( bResult == false && Resultset.next() );
			
			}
			else {

				bResult = true;

			}

			Resultset.beforeFirst();
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1015", Ex.getMessage(), Ex );

		}
		
		return bResult;
		
	} 
	
	@Override
    public CResultSetResult executePlainInsertCommand( CAbstractDBConnection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {

		//Re implement by issue of getGeneratedKey for identity column
    	
    	CResultSetResult Result = new CResultSetResult( -1, -1, "" ); 
    	
    	try {
    	
    		Statement SQLStatement = ((Connection) DBConnection.getDBConnection()).createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );

            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query
            	
        		if ( Lang != null )   
				   Logger.logInfo( "0x2003", Lang.translate( "Init plain SQL statement" ) );
        		else
 				   Logger.logInfo( "0x2003", "Init plain SQL statement" );
        			
            }

            Result.lngAffectedRows = SQLStatement.executeUpdate( strSQL , Statement.RETURN_GENERATED_KEYS );
    		
            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query
            	
        		if ( Lang != null )   
				   Logger.logInfo( "0x2004", Lang.translate( "End plain SQL statement" ) );
        		else
 				   Logger.logInfo( "0x2004", "End plain SQL statement" );
        			
            }
            
            Result.intCode = 1;

    		if ( Result.lngAffectedRows > 0 ) {

    			Result.Result = SQLStatement.getGeneratedKeys(); //Save the generated keys
    			
				if ( Result.Result != null && ( this.isValidResult( Result.Result, Logger, Lang ) == false || this.IsDummyGeneratedKeyResult( Result.Result, Logger, Lang ) ) ) 
					Result.Result = null;

				if ( Result.Result != null )
    				Result.SQLStatement = SQLStatement;
    			
    		}
    		
    		if ( Lang != null )   
    			Result.strDescription = Lang.translate( "Sucess to execute the plain SQL statement [%s]", strSQL );
    		else
    			Result.strDescription = String.format( "Sucess to execute the plain SQL statement [%s]", strSQL );
    	
    	}
    	catch ( Exception Ex ) {
    		
    		if ( Lang != null )   
    		    Result.strDescription = Lang.translate( "Error to execute the plain SQL statement [%s]", strSQL );
    		else
    		    Result.strDescription = String.format( "Error to execute the plain SQL statement [%s]", strSQL ) ;

    		if ( Logger != null )
				Logger.logException( "-1015", Ex.getMessage(), Ex );
    		
    	}
    	
    	return Result;
    	
    }
	
	@Override
    public ArrayList<CResultSetResult> executeComplexInsertCommand( CAbstractDBConnection DBConnection, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
		//Re implement by issue of getGeneratedKey for identity column
		
    	ArrayList<CResultSetResult> Result = new ArrayList<CResultSetResult>();

    	try {
    	
    		HashMap<String,String> Delimiters = new HashMap<String,String>();

    		Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
    		Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );

    		CNamedPreparedStatement MainNamedPreparedStatement = new CNamedPreparedStatement( (Connection) DBConnection.getDBConnection(), strSQL, Delimiters );		
    	
			LinkedHashMap<String,Integer> NamedParams = MainNamedPreparedStatement.getNamedParams();
			
			Iterator<Entry<String, Integer>> i = NamedParams.entrySet().iterator();
			
			CMemoryRowSet MemoryRowSet = new CMemoryRowSet( false );
			
			int intMaxCalls = 0;
			
			while ( i.hasNext() ) {
			       
				Entry<String,Integer> NamedParam = i.next();
				
				String strInputServiceParameterValue = Request.getParameter( NamedParam.getKey() );

				int intMacroIndex = Utilities.getIndexByValue( strMacrosNames, ConfigXMLTagsServicesDaemon._StartMacroTag + NamedParam.getKey() + ConfigXMLTagsServicesDaemon._EndMacroTag );

				if ( intMacroIndex >= 0 ) {
					
					if ( intMacroIndex < intMacrosTypes.length && intMacroIndex < strMacrosValues.length ) {
						
						this.setMacroValueToNamedPreparedStatement( MainNamedPreparedStatement, NamedParam.getKey(), intMacroIndex, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );

					}
					
				}
				else if ( strInputServiceParameterValue.isEmpty() == false ) {
					
					CMemoryFieldData MemoryField = new CMemoryFieldData( strInputServiceParameterValue, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
					
					if ( MemoryField.strName.isEmpty() == false && MemoryField.Data.size() > 0 && MemoryField.intSQLType >= 0 ) {

						if ( MemoryField.Data.size() > intMaxCalls )
							intMaxCalls = MemoryField.Data.size();
						
						MemoryField.strName = NamedParam.getKey();
						MemoryField.strLabel = NamedParam.getKey();
						
						MemoryRowSet.addLinkedField( MemoryField );
						
					}
					
				}
				
			}
			
			boolean bIsValidResultSet = true;
			
			for ( int intIndexCall = 0; intIndexCall < intMaxCalls; intIndexCall++ ) {
				
				i = NamedParams.entrySet().iterator();

				String strParsedStatement = new String( MainNamedPreparedStatement.getParsedStatement() );
				
				int intCount = Utilities.countSubString( strParsedStatement, "?" );
				
				strParsedStatement = strParsedStatement.replace( "?", "%s" );
				
				try {

					ArrayList<String> strRow = MemoryRowSet.RowToString( intIndexCall, true, strDateFormat, strTimeFormat, strDateTimeFormat, true, Logger, Lang );				

					if ( intCount > strRow.size() ) {
						
						for ( int I = 0; I < intCount - strRow.size(); I++ ) {
							
							strRow.add( "unkown" );
							
						}
						
					}

					strParsedStatement = String.format( strParsedStatement, strRow.toArray() );

				}
				catch ( Exception Ex ) {
					
					if ( Logger != null ) {
						
						Logger.logException( "-1017", Ex.getMessage(), Ex );
					
					}	
					
				}

				CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( (Connection) DBConnection.getDBConnection(), MainNamedPreparedStatement.getNamedParams(), MainNamedPreparedStatement.getParsedStatement() ); //, Statement.RETURN_GENERATED_KEYS ); //, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
				
				while ( i.hasNext() ) {
				       
					Entry<String,Integer> NamedParam = i.next();

					this.setFieldDataToPreparedStatement( MemoryRowSet, NamedPreparedStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );
				
				}

				try {
				
					if ( bLogParsedSQL == true ) {
						
						Logger.logInfo( "2", Lang.translate( "Executing the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
					}
					
		            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query
		            	
		        		if ( Lang != null )   
						   Logger.logInfo( "0x2005", Lang.translate( "Init complex SQL statement" ) );
		        		else
		 				   Logger.logInfo( "0x2005", "Init complex SQL statement" );
		        			
		            }
					
					int intAffectedRows = NamedPreparedStatement.executeUpdate();
				
		            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query
		            	
		        		if ( Lang != null )   
						   Logger.logInfo( "0x2006", Lang.translate( "End complex SQL statement" ) );
		        		else
		 				   Logger.logInfo( "0x2006", "End complex SQL statement" );
		        			
		            }
					
					ResultSet GeneratedKeys = null;
					
					if ( intAffectedRows > 0 ) {
						
						GeneratedKeys = NamedPreparedStatement.getGeneratedKeys(); //Save the generated keys
					
						if ( bIsValidResultSet == false ) {
							
							GeneratedKeys = null;
							
						}
						else if ( GeneratedKeys != null ) {
						
							CMemoryRowSet TmpMemoryRowSet = new CMemoryRowSet( false, GeneratedKeys, this, Logger, Lang );

							CachedRowSet TmpGeneratedKeys = TmpMemoryRowSet.createCachedRowSet();

							if ( this.IsDummyGeneratedKeyResult( TmpGeneratedKeys, Logger, Lang ) ) {

								bIsValidResultSet = false; 
								
								GeneratedKeys = null;

							}
							else {
								
								GeneratedKeys = TmpGeneratedKeys;
								
							}
						
						}
						
					}  	
						
					if ( GeneratedKeys != null && ( bIsValidResultSet == false || this.isValidResult( GeneratedKeys, Logger, Lang) == false ) ) {
						
						bIsValidResultSet = false;
						
						GeneratedKeys = null;
						
					}

					if ( GeneratedKeys != null ) {

						Result.add( new CResultSetResult( intAffectedRows, 1, Lang.translate( "Sucess to execute the SQL statement" ), NamedPreparedStatement, GeneratedKeys ) ); //Return back the generated keys

					}    
					else { 

						Result.add( new CResultSetResult( intAffectedRows, 1, Lang.translate( "Sucess to execute the SQL statement" ) ) ); //No key generated

						NamedPreparedStatement.close(); //Close immediately to prevent resource leak in database driver

					}	
					
				}
				catch ( Exception Ex ) {
					
					if ( Logger != null ) {
					
						Result.add( new CResultSetResult( -1, -1, Lang.translate( "Error to execute the SQL statement, see the log file for more details" ) ) );

						Logger.logError( "-1001", Lang.translate( "Error to execute the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
						Logger.logException( "-1016", Ex.getMessage(), Ex );
					
					}	
				
				}
				
			}

			MainNamedPreparedStatement.close();

    	}
    	catch ( Exception Ex ) {
    		
			if ( Logger != null )
				Logger.logException( "-1015", Ex.getMessage(), Ex ); 
    		
    	}
		
    	return Result;
    	
    } 
	
}
