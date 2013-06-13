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
package PGSQLDBEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import Utilities.Utilities;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractDBEngine.CDBEngineConfigConnection;
import AbstractService.CInputServiceParameter;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CNamedCallableStatement;
import CommonClasses.CNamedPreparedStatement;
import CommonClasses.CResultSetResult;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import ExtendedLogger.CExtendedLogger;

public class CPGSQLDBEngine extends CAbstractDBEngine {

	public CPGSQLDBEngine() {
		
		strName = "pgsql";
		strVersion = "9.1";
		
	}
	
	
	@Override
	public Connection getDBConnection( CDBEngineConfigConnection ConfigDBConnection, CExtendedLogger Logger, CLanguage Lang ) {

		Connection DBConnection = null;
		
		try {
			
            String strDatabaseURL = "jdbc:postgresql://" + ConfigDBConnection.strIP + ":" + ConfigDBConnection.intPort + "/" + ConfigDBConnection.strDatabase;

            if ( Logger != null ) {
            	
        		Logger.LogMessage( "1", Lang.Translate( "Trying to connect with the next URL: [%s] and user: [%s]", strDatabaseURL, ConfigDBConnection.strUser ) );        
            	
            }

            Class.forName( ConfigDBConnection.strDriver );

            DBConnection = DriverManager.getConnection( strDatabaseURL, ConfigDBConnection.strUser, ConfigDBConnection.strPassword ); 
			
            if ( Logger != null ) {
            	
        		Logger.LogMessage( "1", Lang.Translate( "Database connection established to next URL: [%s] and user: [%s]", strDatabaseURL, ConfigDBConnection.strUser ) );        
            	
            }
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 
			
		}
		
		return DBConnection;
	
	}

	@Override
	public boolean isValid(Connection DBConnection, CExtendedLogger Logger, CLanguage Lang) {

		boolean bValid = false;
		
		try {   
		    
			bValid = DBConnection.isValid( 5 );
			
			if ( bValid == false && Logger != null && Lang != null ) 
				Logger.LogMessage( "1", Lang.Translate( "Database connection is invalid" ) );        
			
		}
		catch ( Exception Ex ) {
		
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 

		}
		
		return bValid;
	
	}

	@Override
	public boolean setAutoCommit(Connection DBConnection, boolean bAutoCommit, CExtendedLogger Logger, CLanguage Lang) {

		try {   
			
			if ( this.isValid( DBConnection, Logger, Lang ) ) {

				DBConnection.setAutoCommit( bAutoCommit );

				if ( Logger != null && Lang != null ) {

					Logger.LogMessage( "1", Lang.Translate( "Database connection auto commit set to: [%s]", Boolean.toString( bAutoCommit ) ) );        

				}

	            return true;
				
			}
			else {
				
	            return false;
				
			}
            
		}	
		catch ( Exception Ex ) {

			if ( Logger != null  && Lang != null ) {
        		
				Logger.LogWarning( "-1", Lang.Translate( "Cannot change the database connection auto commit to: [%s]", Boolean.toString( bAutoCommit ) ) );        
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
				
			}	

			return false;
			
		}
	
	}

	@Override
	public boolean commit(Connection DBConnection, CExtendedLogger Logger, CLanguage Lang) {

		try {

			if ( this.isValid( DBConnection, Logger, Lang ) ) {

				DBConnection.commit();

				if ( Logger != null && Lang != null ) {

					Logger.LogMessage( "1", Lang.Translate( "Database transaction commited"  ) );        

				}

				return true;

			}
			else {
				
				return false;
				
			}

		}	
		catch ( Exception Ex ) {

			if ( Logger != null && Lang != null ) {
        		
				Logger.LogWarning( "-1", Lang.Translate( "Cannot commit database transaction" ) );        
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
				
			}	

			return false;

		}
	
	}

	@Override
	public boolean rollback(Connection DBConnection, CExtendedLogger Logger, CLanguage Lang) {

		try {

			if ( this.isValid( DBConnection, Logger, Lang ) ) {

				DBConnection.rollback();

				if ( Logger != null && Lang != null ) {

					Logger.LogMessage( "1", Lang.Translate( "Database transaction rollback"  ) );        

				}

				return true;
				
			}
			else {
				
				return false;
				
			}

		}	
		catch ( Exception Ex ) {

			if ( Logger != null && Lang != null ) {
        		
				Logger.LogWarning( "-1", Lang.Translate( "Cannot rollback database transaction" ) );        
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
				
			}	

			return false;

		}
	
	}

	@Override
	public boolean close(Connection DBConnection, CExtendedLogger Logger, CLanguage Lang) {

		
		try {

			if ( this.isValid( DBConnection, Logger, Lang ) ) {

				DBConnection.close();

				if ( Logger != null && Lang != null ) {

					Logger.LogMessage( "1", Lang.Translate( "Database connection closed"  ) );        

				}

				return true;

			}
			else {

				return false;

			}

		}	
		catch ( Exception Ex ) {

			if ( Logger != null && Lang != null ) {
        		
				Logger.LogWarning( "-1", Lang.Translate( "Cannot close database connection" ) );        
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
				
			}	

			return false;

		}
		
	
	}

	@Override
    public CMemoryRowSet InputServiceParameterQuerySQL( Connection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {

		CMemoryRowSet Result = null;
		
		try {	
			
			HashMap<String,String> Delimiters = new HashMap<String,String>();
			
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );
			
			CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( DBConnection, strSQL, Delimiters );		

			/*if ( Logger != null ) {
				
				String strTmpSQL = SQLStatement.getParsedStatement();

				Logger.LogWarning( "-1", strTmpSQL );
				
			}*/
			
			HashMap<String,Integer> NamedParams = NamedPreparedStatement.getNamedParams();
			
			Iterator<Entry<String, Integer>> i = NamedParams.entrySet().iterator();
			
			boolean bSQLParsed = true;
			
			while ( i.hasNext() ) {
			       
				Entry<String,Integer> NamedParam = i.next();

				CInputServiceParameter InputServiceParameterDef = this.getInputServiceParameterByName(InputServiceParameters, NamedParam.getKey() );

				String strInputServiceParameterValue = Request.getParameter( NamedParam.getKey() );

				int intMacroIndex = Utilities.getIndexByValue( strMacrosNames, ConfigXMLTagsServicesDaemon._StartMacroTag + NamedParam.getKey() + ConfigXMLTagsServicesDaemon._EndMacroTag );
				
				if ( InputServiceParameterDef != null && strInputServiceParameterValue != null ) {
				
					this.setFieldValueToNamedPreparedStatement( NamedPreparedStatement, InputServiceParameterDef.getParameterDataTypeID(), NamedParam.getKey(), strInputServiceParameterValue, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
				
                }
				else if ( intMacroIndex >= 0 ) {
					
					if ( intMacroIndex < intMacrosTypes.length && intMacroIndex < strMacrosValues.length ) {
				
						this.setMacroValueToNamedPreparedStatement( NamedPreparedStatement, NamedParam.getKey(), intMacroIndex, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );

					}
					else {
						
                		Logger.LogWarning( "-1", Lang.Translate( "The macro index [%s] is greater than macro values length [%s] and/or macro types length [%s]", Integer.toString( intMacroIndex ), Integer.toString( strMacrosValues.length ), Integer.toString( intMacrosTypes.length ) ) );        
						
					}
					
				}
                
				if ( InputServiceParameterDef == null && intMacroIndex == -1 ) {
                	
                    if ( Logger != null ) {
                    	
                		Logger.LogWarning( "-1", Lang.Translate( "Input parameter [%s] definitions not found", NamedParam.getKey() ) );        
                		Logger.LogWarning( "-1", Lang.Translate( "Macro value [%s] not found", NamedParam.getKey() ) );        
                    	
                    }

                    bSQLParsed = false;
                	
                }
                
				if ( strInputServiceParameterValue == null && intMacroIndex == -1 ) {
                	
                    if ( Logger != null ) {
                    	
                		Logger.LogWarning( "-1", Lang.Translate( "Input parameter [%s] value not found on request", NamedParam.getKey() ) );        
                    	
                    }

                    bSQLParsed = false;
                	
                }
				
			}
			
			if ( bSQLParsed == true ) {
				
				Result = new CMemoryRowSet( false, NamedPreparedStatement.executeQuery() );
				
			}
			else {
				
                if ( Logger != null ) {
                	
            		Logger.LogError( "-1001", Lang.Translate( "Cannot parse the next SQL statement [%s]", strSQL ) );        
                	
                }
				
			}
			
			NamedPreparedStatement.close();
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 

		}

		return Result;
		
	}

	@Override
    public boolean InputServiceParameterModifySQL( Connection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		try {	
			
			HashMap<String,String> Delimiters = new HashMap<String,String>();
			
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );
			
			CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( DBConnection, strSQL, Delimiters );		

			/*if ( Logger != null ) {
				
				String strTmpSQL = SQLStatement.getParsedStatement();

				Logger.LogWarning( "-1", strTmpSQL );
				
			}*/
			
			HashMap<String,Integer> NamedParams = NamedPreparedStatement.getNamedParams();
			
			Iterator<Entry<String, Integer>> i = NamedParams.entrySet().iterator();
			
			boolean bSQLParsed = true;
			
			while ( i.hasNext() ) {
			       
				Entry<String,Integer> NamedParam = i.next();

				CInputServiceParameter InputServiceParameterDef = this.getInputServiceParameterByName(InputServiceParameters, NamedParam.getKey() );

				String strInputServiceParameterValue = Request.getParameter( NamedParam.getKey() );

				int intMacroIndex = Utilities.getIndexByValue( strMacrosNames, ConfigXMLTagsServicesDaemon._StartMacroTag + NamedParam.getKey() + ConfigXMLTagsServicesDaemon._EndMacroTag );
				
				if ( InputServiceParameterDef != null && strInputServiceParameterValue != null ) {
				
					this.setFieldValueToNamedPreparedStatement( NamedPreparedStatement, InputServiceParameterDef.getParameterDataTypeID(), NamedParam.getKey(), strInputServiceParameterValue, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
				
                }
				else if ( intMacroIndex >= 0 ) {
					
					if ( intMacroIndex < intMacrosTypes.length && intMacroIndex < strMacrosValues.length ) {
				
						this.setMacroValueToNamedPreparedStatement( NamedPreparedStatement, NamedParam.getKey(), intMacroIndex, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );

					}
					else {
						
                		Logger.LogWarning( "-1", Lang.Translate( "The macro index [%s] is greater than macro values length [%s] and/or macro types length [%s]", Integer.toString( intMacroIndex ), Integer.toString( strMacrosValues.length ), Integer.toString( intMacrosTypes.length ) ) );        
						
					}
					
				}
                
				if ( InputServiceParameterDef == null && intMacroIndex == -1 ) {
                	
                    if ( Logger != null ) {
                    	
                		Logger.LogWarning( "-1", Lang.Translate( "Input parameter [%s] definitions not found", NamedParam.getKey() ) );        
                		Logger.LogWarning( "-1", Lang.Translate( "Macro value [%s] not found", NamedParam.getKey() ) );        
                    	
                    }

                    bSQLParsed = false;
                	
                }
                
				if ( strInputServiceParameterValue == null && intMacroIndex == -1 ) {
                	
                    if ( Logger != null ) {
                    	
                		Logger.LogWarning( "-1", Lang.Translate( "Input parameter [%s] value not found on request", NamedParam.getKey() ) );        
                    	
                    }

                    bSQLParsed = false;
                	
                }
				
			}
			
			if ( bSQLParsed == true ) {
				
				NamedPreparedStatement.executeUpdate();
				bResult = true;
				
			}
			else {
				
                if ( Logger != null ) {
                	
            		Logger.LogError( "-1001", Lang.Translate( "Cannot parse the next SQL statement [%s]", strSQL ) );        
                	
                }
				
			}
			
			NamedPreparedStatement.close();
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 

		}

		return bResult;
		
	}
	
	@Override
	public CMemoryRowSet InputServiceParameterStoredProcedure( Connection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, CExtendedLogger Logger, CLanguage Lang  ) {

		CMemoryRowSet Result = null;
		
		try {	
			
			HashMap<String,String> Delimiters = new HashMap<String,String>();
			
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );
			
			CNamedCallableStatement NamedCallableStatement = new CNamedCallableStatement( DBConnection, strSQL, Delimiters );		

			HashMap<String,Integer> NamedParams = NamedCallableStatement.getNamedParams();
			
			Iterator<Entry<String, Integer>> i = NamedParams.entrySet().iterator();
			
			boolean bSQLParsed = true;
			
			while ( i.hasNext() ) {
			       
				Entry<String,Integer> NamedParam = i.next();

				CInputServiceParameter InputServiceParameterDef = this.getInputServiceParameterByName(InputServiceParameters, NamedParam.getKey() );

				String strInputServiceParameterValue = Request.getParameter( NamedParam.getKey() );

				int intMacroIndex = Utilities.getIndexByValue( strMacrosNames, ConfigXMLTagsServicesDaemon._StartMacroTag + NamedParam.getKey() + ConfigXMLTagsServicesDaemon._EndMacroTag );
				
				if ( InputServiceParameterDef != null && strInputServiceParameterValue != null ) {
				
					this.setFieldValueToNamedCallableStatement( NamedCallableStatement, InputServiceParameterDef.getParameterDataTypeID(), NamedParam.getKey(), strInputServiceParameterValue, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
	    			
                }
				else if ( intMacroIndex >= 0 ) {
					
					if ( intMacroIndex < intMacrosTypes.length && intMacroIndex < strMacrosValues.length ) {
				
						this.setMacroValueToNamedCallableStatement( NamedCallableStatement, NamedParam.getKey(), intMacroIndex, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );

					}
					else {
						
                		Logger.LogWarning( "-1", Lang.Translate( "The macro index [%s] is greater than macro values length [%s] and/or macro types length [%s]", Integer.toString( intMacroIndex ), Integer.toString( strMacrosValues.length ), Integer.toString( intMacrosTypes.length ) ) );        
						
					}
					
				}
                
				if ( InputServiceParameterDef == null && intMacroIndex == -1 ) {
                	
                    if ( Logger != null ) {
                    	
                		Logger.LogWarning( "-1", Lang.Translate( "Input parameter [%s] definitions not found", NamedParam.getKey() ) );        
                		Logger.LogWarning( "-1", Lang.Translate( "Macro value [%s] not found", NamedParam.getKey() ) );        
                    	
                    }

                    bSQLParsed = false;
                	
                }
                
				if ( strInputServiceParameterValue == null && intMacroIndex == -1 ) {
                	
                    if ( Logger != null ) {
                    	
                		Logger.LogWarning( "-1", Lang.Translate( "Input parameter [%s] value not found on request", NamedParam.getKey() ) );        
                    	
                    }

                    bSQLParsed = false;
                	
                }
				
			}
			
			if ( bSQLParsed == true ) {
				
				Result = new CMemoryRowSet( false, NamedCallableStatement.executeQuery() );
				
			}
			else {
				
                if ( Logger != null ) {
                	
            		Logger.LogError( "-1001", Lang.Translate( "Cannot parse the next SQL statement [%s]", strSQL ) );        
                	
                }
				
			}
			
			NamedCallableStatement.close();
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 

		}

		return Result;
		
	}
	
	public ResultSet ExecuteDummyQuery( Connection DBConnection, String strOptionalDummyQuery, CExtendedLogger Logger, CLanguage Lang ) {
		
		ResultSet Result = null;
		
		try {
		
			if ( strOptionalDummyQuery.isEmpty() == true ) {
				
				strOptionalDummyQuery = "SHOW TABLES";
				
			}
			
			Statement SQLStatement = DBConnection.createStatement();			
			
			Result = SQLStatement.executeQuery( strOptionalDummyQuery );
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 

		}
	
		return Result;
		
	}
	
	@Override
	public boolean CheckPlainSQLStatement( String strSQL, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
		
			if ( strSQL.indexOf( ConfigXMLTagsServicesDaemon._StartParamValue ) == -1 )
				return true;
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
		}

		return false;
		
	}

	@Override
    public Statement CreatePlainSQLStatment( Connection DBConnection, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
		
			return DBConnection.createStatement();

		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
		}
		
		return null;
		
	}
	
    public ArrayList<CResultSetResult> ExecuteComplexQueySQL( Connection DBConnection, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	ArrayList<CResultSetResult> Result = new ArrayList<CResultSetResult>();

    	try {
    	
    		HashMap<String,String> Delimiters = new HashMap<String,String>();

    		Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
    		Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );

    		CNamedPreparedStatement MainNamedPreparedStatement = new CNamedPreparedStatement( DBConnection, strSQL, Delimiters );		
    	
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
						
						Logger.LogException( "-1017", Ex.getMessage(), Ex );
					
					}	
					
				}

				CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( DBConnection, MainNamedPreparedStatement.getNamedParams(), MainNamedPreparedStatement.getParsedStatement() );
				
				while ( i.hasNext() ) {
				       
					Entry<String,Integer> NamedParam = i.next();

					MemoryRowSet.setFieldDataToPreparedStatement( NamedPreparedStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );
				
				}

				try {
				
					if ( bLogParsedSQL == true ) {
						
						Logger.LogInfo( "2", Lang.Translate( "Executing the next SQL statment [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
					}
					
					ResultSet QueryResult = NamedPreparedStatement.executeQuery();
				
					if ( QueryResult != null ) {
						
						Result.add( new CResultSetResult( 1, Lang.Translate( "Sucess to execute the SQL statement" ), NamedPreparedStatement, QueryResult ) );
						
					}
					
				}
				catch ( Exception Ex ) {
					
					if ( Logger != null ) {
					
						Result.add( new CResultSetResult( -1, Lang.Translate( "Error to execute the SQL statement, see the log file for more details" ), null, null ) );

						Logger.LogError( "-1001", Lang.Translate( "Error to execute the next SQL statment [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
						Logger.LogException( "-1016", Ex.getMessage(), Ex );
					
					}	
				
				}
				
			}

			MainNamedPreparedStatement.close();
			
    	}
    	catch ( Exception Ex ) {
    		
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 
    		
    	}
		
    	return Result;
    	
    }

    public void CloseQueyStatement( ArrayList<CResultSetResult> QueryResults, CExtendedLogger Logger, CLanguage Lang ) {

    	if ( QueryResults != null ) {

    		for ( CResultSetResult QueryResult: QueryResults ) {

    			try {

    				if ( QueryResult.NamedPreparedStatement != null ) {   

    					QueryResult.NamedPreparedStatement.close();

    					QueryResult.NamedPreparedStatement = null;

    				}

    			}
    			catch ( Exception Ex ) {

    				if ( Logger != null )
    					Logger.LogException( "-1015", Ex.getMessage(), Ex ); 

    			}

    		}

    	}
    	
    }
    
    public ArrayList<Long> ExecuteComplexModifySQL( Connection DBConnection, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	ArrayList<Long> Result = new ArrayList<Long>();

    	try {
    		
    		HashMap<String,String> Delimiters = new HashMap<String,String>();

    		Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
    		Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );

    		CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( DBConnection, strSQL, Delimiters );		
    	
			LinkedHashMap<String,Integer> NamedParams = NamedPreparedStatement.getNamedParams();
			
			Iterator<Entry<String, Integer>> i = NamedParams.entrySet().iterator();
			
			CMemoryRowSet MemoryRowSet = new CMemoryRowSet( false );
			
			int intMaxCalls = 0;
			
			while ( i.hasNext() ) {
			       
				Entry<String,Integer> NamedParam = i.next();
				
				String strInputServiceParameterValue = Request.getParameter( NamedParam.getKey() );

				int intMacroIndex = Utilities.getIndexByValue( strMacrosNames, ConfigXMLTagsServicesDaemon._StartMacroTag + NamedParam.getKey() + ConfigXMLTagsServicesDaemon._EndMacroTag );

				if ( intMacroIndex >= 0 ) {
					
					if ( intMacroIndex < intMacrosTypes.length && intMacroIndex < strMacrosValues.length ) {
						
						this.setMacroValueToNamedPreparedStatement( NamedPreparedStatement, NamedParam.getKey(), intMacroIndex, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );

					}
					
				}
				else if ( strInputServiceParameterValue != null && strInputServiceParameterValue.isEmpty() == false ) {
					
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
			
			for ( int intIndexCall = 0; intIndexCall < intMaxCalls; intIndexCall++ ) {
				
				i = NamedParams.entrySet().iterator();

				String strParsedStatement = new String( NamedPreparedStatement.getParsedStatement() );
				
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
						
						Logger.LogException( "-1017", Ex.getMessage(), Ex );
					
					}	
					
				}
				
				while ( i.hasNext() ) {
				       
					Entry<String,Integer> NamedParam = i.next();

					MemoryRowSet.setFieldDataToPreparedStatement( NamedPreparedStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );
				
				}

				try {
				
					if ( bLogParsedSQL == true ) {
						
						Logger.LogInfo( "2", Lang.Translate( "Executing the next SQL statment [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
					}
					
					int intAffectedRows = NamedPreparedStatement.executeUpdate();
				
					if ( intAffectedRows >= 0 ) {
						
						Result.add( Long.valueOf( intAffectedRows ) );
						
					}
					
				}
				catch ( Exception Ex ) {
					
					Result.add( -1L ); //Fail report back to caller
					
					if ( Logger != null ) {
					
						Logger.LogError( "-1001", Lang.Translate( "Error to execute the next SQL statment [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
						Logger.LogException( "-1016", Ex.getMessage(), Ex );
					
					}	
				
				}
				
			}
    		
			NamedPreparedStatement.close();
    		
    	}
    	catch ( Exception Ex ) {
    		
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 
    		
    	}
		
    	return Result;
    	
    }

}
