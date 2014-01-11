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
package MySQLDBEngine;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

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
import CommonClasses.NamesSQLTypes;
import ExtendedLogger.CExtendedLogger;

public class CMySQLDBEngine extends CAbstractDBEngine {

	public CMySQLDBEngine() {
		
		strName = "mysql";
		strVersion = "5.1";
		
	}
	
	@Override
	public CAbstractDBConnection getDBConnection( CDBEngineConfigNativeDBConnection ConfigDBConnection, CExtendedLogger Logger, CLanguage Lang ) {

		CAbstractDBConnection DBConnection = null;
		
		try {
			
            String strDatabaseURL = "jdbc:mysql://" + ConfigDBConnection.strIP + ":" + ConfigDBConnection.intPort + "/" + ConfigDBConnection.strDatabase;

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
	public ResultSet executeDummyCommand( CAbstractDBConnection DBConnection, String strOptionalDummySQL, CExtendedLogger Logger, CLanguage Lang ) {
		
		ResultSet Result = null;
		
		try {
		
			if ( strOptionalDummySQL != null && strOptionalDummySQL.isEmpty() == true ) {
				
				strOptionalDummySQL = "SHOW TABLES";
				
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

	@Override
    public CResultSetResult executePlainInsertCommand( CAbstractDBConnection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {

		//Reimplement by issue of getGeneratedKey for autoincrement column
    	
    	CResultSetResult Result = new CResultSetResult( -1, -1, "" ); 
    	
    	try {
    	
    		Statement SQLStatement = ((Connection) DBConnection.getDBConnection()).createStatement();

            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query
            	
        		if ( Lang != null )   
				   Logger.logInfo( "0x2003", Lang.translate( "Init plain SQL statement" ) );
        		else
 				   Logger.logInfo( "0x2003", "Init plain SQL statement" );
        			
            }
    		
    		Result.lngAffectedRows = SQLStatement.executeUpdate( strSQL, Statement.RETURN_GENERATED_KEYS );

            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query
            	
        		if ( Lang != null )   
				   Logger.logInfo( "0x2004", Lang.translate( "End plain SQL statement" ) );
        		else
 				   Logger.logInfo( "0x2004", "End plain SQL statement" );
        			
            }

            Result.intCode = 1;

    		if ( Result.lngAffectedRows > 0 ) {

    			Result.Result = SQLStatement.getGeneratedKeys(); //Save the generated keys

				if ( Result.Result != null && ( this.isValidResult( Result.Result, Logger, Lang ) == false ) ) 
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
    	
		//Reimplement by issue of getGeneratedKey for autoincrement column
		
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

				CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( (Connection) DBConnection.getDBConnection(), MainNamedPreparedStatement.getNamedParams(), MainNamedPreparedStatement.getParsedStatement() ); //, Statement.RETURN_GENERATED_KEYS );
				
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

	@Override
    public CResultSetResult getDatabaseInfo( CAbstractDBConnection DBConnection, HashMap<String,String> ConfiguredValues, CExtendedLogger Logger, CLanguage Lang ) {

    	CResultSetResult Result =  new CResultSetResult();
    	
    	Result.intCode = -1;
    	Result.strDescription = "";
    	
    	try {
    		
    		DatabaseMetaData DBMetadata = ((Connection) DBConnection.getDBConnection()).getMetaData();
    		
        	CMemoryRowSet MemoryRowSet =  new CMemoryRowSet( false );
        	
        	MemoryRowSet.addField( "Feature", Types.VARCHAR, NamesSQLTypes._VARCHAR, 256, "Feature" );
        	MemoryRowSet.addField( "Value", Types.VARCHAR, NamesSQLTypes._VARCHAR, 2048, "Value" );

        	MemoryRowSet.addRow( "allProceduresAreCallable", Boolean.toString( DBMetadata.allProceduresAreCallable() ) );
    		MemoryRowSet.addRow( "allTablesAreSelectable", Boolean.toString( DBMetadata.allTablesAreSelectable() ) );
    		MemoryRowSet.addRow( "autoCommitFailureClosesAllResultSets", Boolean.toString( DBMetadata.autoCommitFailureClosesAllResultSets() ) );
    		MemoryRowSet.addRow( "dataDefinitionCausesTransactionCommit", Boolean.toString( DBMetadata.dataDefinitionCausesTransactionCommit() ) );
    		MemoryRowSet.addRow( "dataDefinitionIgnoredInTransactions", Boolean.toString( DBMetadata.dataDefinitionIgnoredInTransactions() ) );
    		MemoryRowSet.addRow( "deletesAreDetected.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.deletesAreDetected( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "deletesAreDetected.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.deletesAreDetected( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "deletesAreDetected.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.deletesAreDetected( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "doesMaxRowSizeIncludeBlobs", Boolean.toString( DBMetadata.doesMaxRowSizeIncludeBlobs() ) );
    		//Mysql drive 5.1 generated VERY BIG exception always return true
    		MemoryRowSet.addRow( "generatedKeyAlwaysReturned", "true" );  
    		//MemoryRowSet.addRow( "generatedKeyAlwaysReturned", Boolean.toString( DBMetadata.generatedKeyAlwaysReturned() ) );
    		MemoryRowSet.addRow( "insertsAreDetected.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.insertsAreDetected( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "insertsAreDetected.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.insertsAreDetected( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "insertsAreDetected.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.insertsAreDetected( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "isCatalogAtStart", Boolean.toString( DBMetadata.isCatalogAtStart() ) );
    		MemoryRowSet.addRow( "isReadOnly", Boolean.toString( DBMetadata.isReadOnly() ) );
    		MemoryRowSet.addRow( "locatorsUpdateCopy", Boolean.toString( DBMetadata.locatorsUpdateCopy() ) );
    		MemoryRowSet.addRow( "nullPlusNonNullIsNull", Boolean.toString( DBMetadata.nullPlusNonNullIsNull() ) );
    		MemoryRowSet.addRow( "nullsAreSortedAtEnd", Boolean.toString( DBMetadata.nullsAreSortedAtEnd() ) );
    		MemoryRowSet.addRow( "nullsAreSortedAtStart", Boolean.toString( DBMetadata.nullsAreSortedAtStart() ) );
    		MemoryRowSet.addRow( "nullsAreSortedHigh", Boolean.toString( DBMetadata.nullsAreSortedHigh() ) );
    		MemoryRowSet.addRow( "nullsAreSortedLow", Boolean.toString( DBMetadata.nullsAreSortedLow() ) );
    		MemoryRowSet.addRow( "othersDeletesAreVisible.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.othersDeletesAreVisible( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "othersDeletesAreVisible.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.othersDeletesAreVisible( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "othersDeletesAreVisible.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.othersDeletesAreVisible( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "othersInsertsAreVisible.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.othersInsertsAreVisible( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "othersInsertsAreVisible.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.othersInsertsAreVisible( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "othersInsertsAreVisible.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.othersInsertsAreVisible( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "othersUpdatesAreVisible.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.othersUpdatesAreVisible( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "othersUpdatesAreVisible.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.othersUpdatesAreVisible( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "othersUpdatesAreVisible.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.othersUpdatesAreVisible( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "ownDeletesAreVisible.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.ownDeletesAreVisible( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "ownDeletesAreVisible.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.ownDeletesAreVisible( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "ownDeletesAreVisible.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.ownDeletesAreVisible( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "ownInsertsAreVisible.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.ownInsertsAreVisible( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "ownInsertsAreVisible.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.ownInsertsAreVisible( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "ownInsertsAreVisible.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.ownInsertsAreVisible( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "ownUpdatesAreVisible.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.ownUpdatesAreVisible( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "ownUpdatesAreVisible.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.ownUpdatesAreVisible( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "ownUpdatesAreVisible.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.ownUpdatesAreVisible( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "storesLowerCaseIdentifiers", Boolean.toString( DBMetadata.storesLowerCaseIdentifiers() ) );
    		MemoryRowSet.addRow( "storesLowerCaseQuotedIdentifiers", Boolean.toString( DBMetadata.storesLowerCaseQuotedIdentifiers() ) );
    		MemoryRowSet.addRow( "storesMixedCaseIdentifiers", Boolean.toString( DBMetadata.storesMixedCaseIdentifiers() ) );
    		MemoryRowSet.addRow( "storesMixedCaseQuotedIdentifiers", Boolean.toString( DBMetadata.storesMixedCaseQuotedIdentifiers() ) );
    		MemoryRowSet.addRow( "storesUpperCaseIdentifiers", Boolean.toString( DBMetadata.storesUpperCaseIdentifiers() ) );
    		MemoryRowSet.addRow( "storesUpperCaseQuotedIdentifiers", Boolean.toString( DBMetadata.storesUpperCaseQuotedIdentifiers() ) );
    		MemoryRowSet.addRow( "supportsAlterTableWithAddColumn", Boolean.toString( DBMetadata.supportsAlterTableWithAddColumn() ) );
    		MemoryRowSet.addRow( "supportsAlterTableWithDropColumn", Boolean.toString( DBMetadata.supportsAlterTableWithDropColumn() ) );
    		MemoryRowSet.addRow( "supportsANSI92EntryLevelSQL", Boolean.toString( DBMetadata.supportsANSI92EntryLevelSQL() ) );
    		MemoryRowSet.addRow( "supportsANSI92FullSQL", Boolean.toString( DBMetadata.supportsANSI92FullSQL() ) );
    		MemoryRowSet.addRow( "supportsANSI92IntermediateSQL", Boolean.toString( DBMetadata.supportsANSI92IntermediateSQL() ) );
    		MemoryRowSet.addRow( "supportsBatchUpdates", Boolean.toString( DBMetadata.supportsBatchUpdates() ) );
    		MemoryRowSet.addRow( "supportsCatalogsInDataManipulation", Boolean.toString( DBMetadata.supportsCatalogsInDataManipulation() ) );
    		MemoryRowSet.addRow( "supportsCatalogsInIndexDefinitions", Boolean.toString( DBMetadata.supportsCatalogsInIndexDefinitions() ) );
    		MemoryRowSet.addRow( "supportsCatalogsInPrivilegeDefinitions", Boolean.toString( DBMetadata.supportsCatalogsInPrivilegeDefinitions() ) );
    		MemoryRowSet.addRow( "supportsCatalogsInProcedureCalls", Boolean.toString( DBMetadata.supportsCatalogsInProcedureCalls() ) );
    		MemoryRowSet.addRow( "supportsCatalogsInTableDefinitions", Boolean.toString( DBMetadata.supportsCatalogsInTableDefinitions() ) );
    		MemoryRowSet.addRow( "supportsColumnAliasing", Boolean.toString( DBMetadata.supportsColumnAliasing() ) );
    		MemoryRowSet.addRow( "supportsConvert", Boolean.toString( DBMetadata.supportsConvert() ) );
    		MemoryRowSet.addRow( "supportsCoreSQLGrammar", Boolean.toString( DBMetadata.supportsCoreSQLGrammar() ) );
    		MemoryRowSet.addRow( "supportsCorrelatedSubqueries", Boolean.toString( DBMetadata.supportsCorrelatedSubqueries() ) );
    		MemoryRowSet.addRow( "supportsDataManipulationTransactionsOnly", Boolean.toString( DBMetadata.supportsDataManipulationTransactionsOnly() ) );
    		MemoryRowSet.addRow( "supportsDifferentTableCorrelationNames", Boolean.toString( DBMetadata.supportsDifferentTableCorrelationNames() ) );
    		MemoryRowSet.addRow( "supportsExpressionsInOrderBy", Boolean.toString( DBMetadata.supportsExpressionsInOrderBy() ) );
    		MemoryRowSet.addRow( "supportsExtendedSQLGrammar", Boolean.toString( DBMetadata.supportsExtendedSQLGrammar() ) );
    		MemoryRowSet.addRow( "supportsFullOuterJoins", Boolean.toString( DBMetadata.supportsFullOuterJoins() ) );
    		MemoryRowSet.addRow( "supportsGetGeneratedKeys", Boolean.toString( DBMetadata.supportsGetGeneratedKeys() ) );
    		MemoryRowSet.addRow( "supportsGroupBy", Boolean.toString( DBMetadata.supportsGroupBy() ) );
    		MemoryRowSet.addRow( "supportsGroupByBeyondSelect", Boolean.toString( DBMetadata.supportsGroupByBeyondSelect() ) );
    		MemoryRowSet.addRow( "supportsGroupByUnrelated", Boolean.toString( DBMetadata.supportsGroupByUnrelated() ) );
    		MemoryRowSet.addRow( "supportsIntegrityEnhancementFacility", Boolean.toString( DBMetadata.supportsIntegrityEnhancementFacility() ) );
    		MemoryRowSet.addRow( "supportsLikeEscapeClause", Boolean.toString( DBMetadata.supportsLikeEscapeClause() ) );
    		MemoryRowSet.addRow( "supportsLimitedOuterJoins", Boolean.toString( DBMetadata.supportsLimitedOuterJoins() ) );
    		MemoryRowSet.addRow( "supportsMinimumSQLGrammar", Boolean.toString( DBMetadata.supportsMinimumSQLGrammar() ) );
    		MemoryRowSet.addRow( "supportsMixedCaseIdentifiers", Boolean.toString( DBMetadata.supportsMixedCaseIdentifiers() ) );
    		MemoryRowSet.addRow( "supportsMixedCaseQuotedIdentifiers", Boolean.toString( DBMetadata.supportsMixedCaseQuotedIdentifiers() ) );
    		MemoryRowSet.addRow( "supportsMultipleOpenResults", Boolean.toString( DBMetadata.supportsMultipleOpenResults() ) );
    		MemoryRowSet.addRow( "supportsMultipleResultSets", Boolean.toString( DBMetadata.supportsMultipleResultSets() ) );
    		MemoryRowSet.addRow( "supportsMultipleTransactions", Boolean.toString( DBMetadata.supportsMultipleTransactions() ) );
    		MemoryRowSet.addRow( "supportsNamedParameters", Boolean.toString( DBMetadata.supportsNamedParameters() ) );
    		MemoryRowSet.addRow( "supportsNonNullableColumns", Boolean.toString( DBMetadata.supportsNonNullableColumns() ) );
    		MemoryRowSet.addRow( "supportsOpenCursorsAcrossCommit", Boolean.toString( DBMetadata.supportsOpenCursorsAcrossCommit() ) );
    		MemoryRowSet.addRow( "supportsOpenCursorsAcrossRollback", Boolean.toString( DBMetadata.supportsOpenCursorsAcrossRollback() ) );
    		MemoryRowSet.addRow( "supportsOpenStatementsAcrossCommit", Boolean.toString( DBMetadata.supportsOpenStatementsAcrossCommit() ) );
    		MemoryRowSet.addRow( "supportsOpenStatementsAcrossRollback", Boolean.toString( DBMetadata.supportsOpenStatementsAcrossRollback() ) );
    		MemoryRowSet.addRow( "supportsOrderByUnrelated", Boolean.toString( DBMetadata.supportsOrderByUnrelated() ) );
    		MemoryRowSet.addRow( "supportsOuterJoins", Boolean.toString( DBMetadata.supportsOuterJoins() ) );
    		MemoryRowSet.addRow( "supportsPositionedDelete", Boolean.toString( DBMetadata.supportsPositionedDelete() ) );
    		MemoryRowSet.addRow( "supportsPositionedUpdate", Boolean.toString( DBMetadata.supportsPositionedUpdate() ) );
    		MemoryRowSet.addRow( "supportsResultSetConcurrency.TYPE_FORWARD_ONLY.CONCUR_READ_ONLY", Boolean.toString( DBMetadata.supportsResultSetConcurrency( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY ) ) );
    		MemoryRowSet.addRow( "supportsResultSetConcurrency.TYPE_FORWARD_ONLY.CONCUR_UPDATABLE", Boolean.toString( DBMetadata.supportsResultSetConcurrency( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE ) ) );
    		MemoryRowSet.addRow( "supportsResultSetConcurrency.TYPE_SCROLL_INSENSITIVE.CONCUR_READ_ONLY", Boolean.toString( DBMetadata.supportsResultSetConcurrency( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY ) ) );
    		MemoryRowSet.addRow( "supportsResultSetConcurrency.TYPE_SCROLL_INSENSITIVE.CONCUR_UPDATABLE", Boolean.toString( DBMetadata.supportsResultSetConcurrency( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE ) ) );
    		MemoryRowSet.addRow( "supportsResultSetConcurrency.TYPE_SCROLL_SENSITIVE.CONCUR_READ_ONLY", Boolean.toString( DBMetadata.supportsResultSetConcurrency( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY ) ) );
    		MemoryRowSet.addRow( "supportsResultSetConcurrency.TYPE_SCROLL_SENSITIVE.CONCUR_UPDATABLE", Boolean.toString( DBMetadata.supportsResultSetConcurrency( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE ) ) );
    		MemoryRowSet.addRow( "supportsResultSetHoldability.HOLD_CURSORS_OVER_COMMIT", Boolean.toString( DBMetadata.supportsResultSetHoldability( ResultSet.HOLD_CURSORS_OVER_COMMIT ) ) );
    		MemoryRowSet.addRow( "supportsResultSetHoldability.CLOSE_CURSORS_AT_COMMIT", Boolean.toString( DBMetadata.supportsResultSetHoldability( ResultSet.CLOSE_CURSORS_AT_COMMIT ) ) );
    		MemoryRowSet.addRow( "supportsResultSetType.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.supportsResultSetType( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "supportsResultSetType.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "supportsResultSetType.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.supportsResultSetType( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "supportsSavepoints", Boolean.toString( DBMetadata.supportsSavepoints() ) );
    		MemoryRowSet.addRow( "supportsSchemasInDataManipulation", Boolean.toString( DBMetadata.supportsSchemasInDataManipulation() ) );
    		MemoryRowSet.addRow( "supportsSchemasInIndexDefinitions", Boolean.toString( DBMetadata.supportsSchemasInIndexDefinitions() ) );
    		MemoryRowSet.addRow( "supportsSchemasInPrivilegeDefinitions", Boolean.toString( DBMetadata.supportsSchemasInPrivilegeDefinitions() ) );
    		MemoryRowSet.addRow( "supportsSchemasInProcedureCalls", Boolean.toString( DBMetadata.supportsSchemasInProcedureCalls() ) );
    		MemoryRowSet.addRow( "supportsSchemasInTableDefinitions", Boolean.toString( DBMetadata.supportsSchemasInTableDefinitions() ) );
    		MemoryRowSet.addRow( "supportsSelectForUpdate", Boolean.toString( DBMetadata.supportsSelectForUpdate() ) );
    		MemoryRowSet.addRow( "supportsStatementPooling", Boolean.toString( DBMetadata.supportsStatementPooling() ) );
    		MemoryRowSet.addRow( "supportsStoredFunctionsUsingCallSyntax", Boolean.toString( DBMetadata.supportsStoredFunctionsUsingCallSyntax() ) );
    		MemoryRowSet.addRow( "supportsStoredProcedures", Boolean.toString( DBMetadata.supportsStoredProcedures() ) );
    		MemoryRowSet.addRow( "supportsSubqueriesInComparisons", Boolean.toString( DBMetadata.supportsSubqueriesInComparisons() ) );
    		MemoryRowSet.addRow( "supportsSubqueriesInExists", Boolean.toString( DBMetadata.supportsSubqueriesInExists() ) );
    		MemoryRowSet.addRow( "supportsSubqueriesInIns", Boolean.toString( DBMetadata.supportsSubqueriesInIns() ) );
    		MemoryRowSet.addRow( "supportsSubqueriesInQuantifieds", Boolean.toString( DBMetadata.supportsSubqueriesInQuantifieds() ) );
    		MemoryRowSet.addRow( "supportsTableCorrelationNames", Boolean.toString( DBMetadata.supportsTableCorrelationNames() ) );
    		MemoryRowSet.addRow( "supportsTransactionIsolationLevel.TRANSACTION_NONE", Boolean.toString( DBMetadata.supportsTransactionIsolationLevel( Connection.TRANSACTION_NONE ) ) );
    		MemoryRowSet.addRow( "supportsTransactionIsolationLevel.TRANSACTION_READ_COMMITTED", Boolean.toString( DBMetadata.supportsTransactionIsolationLevel( Connection.TRANSACTION_READ_COMMITTED ) ) );
    		MemoryRowSet.addRow( "supportsTransactionIsolationLevel.TRANSACTION_READ_UNCOMMITTED", Boolean.toString( DBMetadata.supportsTransactionIsolationLevel( Connection.TRANSACTION_READ_UNCOMMITTED ) ) );
    		MemoryRowSet.addRow( "supportsTransactionIsolationLevel.TRANSACTION_REPEATABLE_READ", Boolean.toString( DBMetadata.supportsTransactionIsolationLevel( Connection.TRANSACTION_REPEATABLE_READ ) ) );
    		MemoryRowSet.addRow( "supportsTransactionIsolationLevel.TRANSACTION_SERIALIZABLE", Boolean.toString( DBMetadata.supportsTransactionIsolationLevel( Connection.TRANSACTION_SERIALIZABLE ) ) );
    		MemoryRowSet.addRow( "supportsTransactions", Boolean.toString( DBMetadata.supportsTransactions() ) );
    		MemoryRowSet.addRow( "supportsUnion", Boolean.toString( DBMetadata.supportsUnion() ) );
    		MemoryRowSet.addRow( "supportsUnionAll", Boolean.toString( DBMetadata.supportsUnionAll() ) );
    		MemoryRowSet.addRow( "updatesAreDetected.TYPE_FORWARD_ONLY", Boolean.toString( DBMetadata.updatesAreDetected( ResultSet.TYPE_FORWARD_ONLY ) ) );
    		MemoryRowSet.addRow( "updatesAreDetected.TYPE_SCROLL_INSENSITIVE", Boolean.toString( DBMetadata.updatesAreDetected( ResultSet.TYPE_SCROLL_INSENSITIVE ) ) );
    		MemoryRowSet.addRow( "updatesAreDetected.TYPE_SCROLL_SENSITIVE", Boolean.toString( DBMetadata.updatesAreDetected( ResultSet.TYPE_SCROLL_SENSITIVE ) ) );
    		MemoryRowSet.addRow( "usesLocalFilePerTable", Boolean.toString( DBMetadata.usesLocalFilePerTable() ) );
    		MemoryRowSet.addRow( "usesLocalFiles", Boolean.toString( DBMetadata.usesLocalFiles() ) );
    		MemoryRowSet.addRow( "catalogSeparator", DBMetadata.getCatalogSeparator() );
    		MemoryRowSet.addRow( "catalogTerm", DBMetadata.getCatalogTerm() );
    		MemoryRowSet.addRow( "databaseMajorVersion", Integer.toString( DBMetadata.getDatabaseMajorVersion() ) );
    		MemoryRowSet.addRow( "databaseMinorVersion", Integer.toString( DBMetadata.getDatabaseMinorVersion() ) );
    		MemoryRowSet.addRow( "databaseProductName", DBMetadata.getDatabaseProductName() );
    		MemoryRowSet.addRow( "databaseProductVersion", DBMetadata.getDatabaseProductVersion() );
    		MemoryRowSet.addRow( "defaultTransactionIsolation", Integer.toString( DBMetadata.getDefaultTransactionIsolation() ) );
    		MemoryRowSet.addRow( "driverMajorVersion", Integer.toString( DBMetadata.getDriverMajorVersion() ) );
    		MemoryRowSet.addRow( "driverMinorVersion", Integer.toString( DBMetadata.getDriverMinorVersion() ) );
    		MemoryRowSet.addRow( "driverName", DBMetadata.getDriverName() );
    		MemoryRowSet.addRow( "driverVersion", DBMetadata.getDriverVersion() );
    		
    		for( Entry<String, String> Entry : ConfiguredValues.entrySet() ) {
    			
    			MemoryRowSet.addRow( Entry.getKey(), Entry.getValue() );
    			
    		}
    		
    		//MemoryRowSet.addRow( "configuredDriverNameClass", strConfiguredDriverNameClass );
    		MemoryRowSet.addRow( "extraNameCharacters", DBMetadata.getExtraNameCharacters() );
    		MemoryRowSet.addRow( "identifierQuoteString", net.maindataservices.Utilities.replaceToHTMLEntity( DBMetadata.getIdentifierQuoteString() ) );
    		MemoryRowSet.addRow( "JDBCMajorVersion", Integer.toString( DBMetadata.getJDBCMajorVersion() ) );
    		MemoryRowSet.addRow( "JDBCMinorVersion", Integer.toString( DBMetadata.getJDBCMinorVersion() ) );
    		MemoryRowSet.addRow( "maxBinaryLiteralLength", Integer.toString( DBMetadata.getMaxBinaryLiteralLength() ) );
    		MemoryRowSet.addRow( "maxCatalogNameLength", Integer.toString( DBMetadata.getMaxCatalogNameLength() ) );
    		MemoryRowSet.addRow( "maxCharLiteralLength", Integer.toString( DBMetadata.getMaxCharLiteralLength() ) );
    		MemoryRowSet.addRow( "maxColumnNameLength", Integer.toString( DBMetadata.getMaxColumnNameLength() ) );
    		MemoryRowSet.addRow( "maxColumnsInGroupBy", Integer.toString( DBMetadata.getMaxColumnsInGroupBy() ) );
    		MemoryRowSet.addRow( "maxColumnsInIndex", Integer.toString( DBMetadata.getMaxColumnsInIndex() ) );
    		MemoryRowSet.addRow( "maxColumnsInOrderBy", Integer.toString( DBMetadata.getMaxColumnsInOrderBy() ) );
    		MemoryRowSet.addRow( "maxColumnsInSelect", Integer.toString( DBMetadata.getMaxColumnsInSelect() ) );
    		MemoryRowSet.addRow( "maxColumnsInTable", Integer.toString( DBMetadata.getMaxColumnsInTable() ) );
    		MemoryRowSet.addRow( "maxConnections", Integer.toString( DBMetadata.getMaxConnections() ) );
    		MemoryRowSet.addRow( "maxCursorNameLength", Integer.toString( DBMetadata.getMaxCursorNameLength() ) );
    		MemoryRowSet.addRow( "maxIndexLength", Integer.toString( DBMetadata.getMaxIndexLength() ) );
    		MemoryRowSet.addRow( "maxProcedureNameLength", Integer.toString( DBMetadata.getMaxProcedureNameLength() ) );
    		MemoryRowSet.addRow( "maxRowSize", Integer.toString( DBMetadata.getMaxRowSize() ) );
    		MemoryRowSet.addRow( "maxSchemaNameLength", Integer.toString( DBMetadata.getMaxSchemaNameLength() ) );
    		MemoryRowSet.addRow( "maxStatementLength", Integer.toString( DBMetadata.getMaxStatementLength() ) );
    		MemoryRowSet.addRow( "maxStatements", Integer.toString( DBMetadata.getMaxStatements() ) );
    		MemoryRowSet.addRow( "maxTableNameLength", Integer.toString( DBMetadata.getMaxTableNameLength() ) );
    		MemoryRowSet.addRow( "maxTablesInSelect", Integer.toString( DBMetadata.getMaxTablesInSelect() ) );
    		MemoryRowSet.addRow( "maxUserNameLength", Integer.toString( DBMetadata.getMaxUserNameLength() ) );
    		MemoryRowSet.addRow( "getNumericFunctions", DBMetadata.getNumericFunctions() );
    		MemoryRowSet.addRow( "getProcedureTerm", DBMetadata.getProcedureTerm() );
    		MemoryRowSet.addRow( "resultSetHoldability", Integer.toString( DBMetadata.getResultSetHoldability() ) );
    		MemoryRowSet.addRow( "schemaTerm", DBMetadata.getSchemaTerm() );
    		MemoryRowSet.addRow( "searchStringEscape", DBMetadata.getSearchStringEscape() );
    		MemoryRowSet.addRow( "SQLKeywords", DBMetadata.getSQLKeywords() );
    		MemoryRowSet.addRow( "SQLStateType", Integer.toString( DBMetadata.getSQLStateType() ) );
    		MemoryRowSet.addRow( "systemFunctions", DBMetadata.getSystemFunctions() );
    		MemoryRowSet.addRow( "systemFunctions", DBMetadata.getSystemFunctions() );
    		MemoryRowSet.addRow( "timeDateFunctions", DBMetadata.getTimeDateFunctions() );
    		MemoryRowSet.addRow( "URL", DBMetadata.getURL() );
    		MemoryRowSet.addRow( "userName", DBMetadata.getUserName() );
    		
    		String[] ObjectType = { "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM" };

    		int intCountTables = 0;
    		int intCountViews = 0;
    		int intCountSystemTables = 0;
    		int intCountGlobalTempTables = 0;
    		int intCountLocalTempTables = 0;
    		int intCountAlias = 0;
    		int intCountSynonyms = 0;
    		
    		ResultSet RS = null;
    		
    		try {
    		
    			RS = DBMetadata.getTables(  null, null, "%", ObjectType );

    			while ( RS != null && RS.next() ) {

    				if ( RS.getString(4).toUpperCase().equals( "TABLE" ) ) {

    					intCountTables += 1;

    				}
    				else if ( RS.getString(4).toUpperCase().equals( "VIEW" ) ) {

    					intCountViews += 1;

    				}
    				else if ( RS.getString(4).toUpperCase().equals( "SYSTEM TABLE" ) ) {

    					intCountSystemTables += 1;

    				}
    				else if ( RS.getString(4).toUpperCase().equals( "GLOBAL TEMPORARY" ) ) {

    					intCountGlobalTempTables += 1;

    				}
    				else if ( RS.getString(4).toUpperCase().equals( "LOCAL TEMPORARY" ) ) {

    					intCountLocalTempTables += 1;

    				}
    				else if ( RS.getString(4).toUpperCase().equals( "ALIAS" ) ) {

    					intCountAlias += 1;

    				}
    				else if ( RS.getString(4).toUpperCase().equals( "SYNONYM" ) ) {

    					intCountSynonyms += 1;

    				}

    			}
    		
    		} 
    		catch ( Exception Ex ) {

    			if ( Logger != null )
    				Logger.logException( "-1010", Ex.getMessage(), Ex );

    		}
    		
    		MemoryRowSet.addRow( "countTables", Integer.toString( intCountTables ) );
    		MemoryRowSet.addRow( "countViews", Integer.toString( intCountViews ) );
    		MemoryRowSet.addRow( "countSystemTables", Integer.toString( intCountSystemTables ) );
    		MemoryRowSet.addRow( "countGlobalTempTables", Integer.toString( intCountGlobalTempTables ) );
    		MemoryRowSet.addRow( "countLocalTempTables", Integer.toString( intCountLocalTempTables ) );
    		MemoryRowSet.addRow( "countAlias", Integer.toString( intCountAlias ) );
    		MemoryRowSet.addRow( "countSynonyms", Integer.toString( intCountSynonyms ) );
    		MemoryRowSet.addRow( "countAllTables", Integer.toString( intCountTables + intCountSystemTables + intCountGlobalTempTables + intCountLocalTempTables ) );

    		int intCountFunctions = 0;
    		
    		try {

    			RS = DBMetadata.getFunctions( null, null, "%" );

    			while ( RS != null && RS.next() ) {

    				intCountFunctions += 1;

    			}

    		} 
    		catch ( Exception Ex ) {

    			if ( Logger != null )
    				Logger.logException( "-1010", Ex.getMessage(), Ex );

    		}
    		
        	MemoryRowSet.addRow( "countFunctions", Integer.toString( intCountFunctions ) );
			
    		int intCountProcedures = 0;

    		try {
    		
    			RS = DBMetadata.getProcedures( null, null, "%" );

    			while ( RS != null && RS.next() ) {

    				intCountProcedures += 1;

    			}
    		
    		} 
    		catch ( Exception Ex ) {

    			if ( Logger != null )
    				Logger.logException( "-1010", Ex.getMessage(), Ex );

    		}
    		
        	MemoryRowSet.addRow( "countProcedures", Integer.toString( intCountProcedures ) );

        	Result.Result = MemoryRowSet.createCachedRowSet();

    		Result.intCode = 1;
    		
    	} 
    	catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
    	
    	}
    	
    	return Result;
    	
    }
	
}
