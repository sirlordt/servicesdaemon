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
package AbstractDBEngine;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialBlob;

import net.maindataservices.Base64;
import net.maindataservices.Utilities;


import AbstractService.CInputServiceParameter;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CNamedCallableStatement;
import CommonClasses.CNamedPreparedStatement;
import CommonClasses.CResultSetResult;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import CommonClasses.NamesSQLTypes;
import ExtendedLogger.CExtendedLogger;

public abstract class CAbstractDBEngine {

	protected static ArrayList<CAbstractDBEngine> RegisteredDBEngines = null;
	
	static {

		RegisteredDBEngines = new ArrayList<CAbstractDBEngine>();
		
	}
	
	public static CAbstractDBEngine getDBEngine( String strName, String strVersion ) {
		
	    CAbstractDBEngine Result = null;
	    
	    strName = strName.toLowerCase();
		 
	    for ( CAbstractDBEngine DBEngine : RegisteredDBEngines ) {
			
			if ( DBEngine.strName.toLowerCase().equals( strName ) == true ) {
			
				if ( Utilities.VersionGreaterEquals( strVersion, DBEngine.getVersion() ) ) {
				
				   Result = DBEngine;
				   break;
				   
				}
				
			}
			
		}
		
		return Result;
		
	}
	
	public static boolean ResigterDBEngine( CAbstractDBEngine DBEngine ) { 
		
		boolean bResult = false;
				
		if ( RegisteredDBEngines.contains( DBEngine ) == false ) {
			
			RegisteredDBEngines.add( DBEngine );
		
			bResult = true;
			
		}
		
		return bResult;
		
	}
    
    public static void ClearRegisteredDBEngines() {
    	
    	RegisteredDBEngines.clear();
    	
    }
    
    public static int GetCountRegisteredDBEngines() {
    	
    	return RegisteredDBEngines.size();
    	
    }

    public enum SQLStatementType { Unknown, Call, Select, Insert, Update, Delete, DDL };
    
    protected String strName;
    protected String strVersion;
    protected CServicesDaemonConfig ServicesDaemonConfig = null;
	
    public CAbstractDBEngine() {
	}

    public String getName() {
    	
    	return this.strName;
    	
    } 
    
    public String getVersion() {
    	
    	return this.strVersion;
    	
    }
    
    public boolean InitializeDBEngine( CServicesDaemonConfig ServicesDaemonConfig ) {
    	
    	this.ServicesDaemonConfig = ServicesDaemonConfig;
    	
    	return true;
    	
    }
    
    public CInputServiceParameter getInputServiceParameterByName( ArrayList<CInputServiceParameter> InputServiceParameters, String strName ) {
   	
        for ( CInputServiceParameter InputServiceParameter: InputServiceParameters ) {

        	if ( InputServiceParameter.getParameterName().equals( strName ) ) {
        		
        		return InputServiceParameter;
        		
        	}
        	
        }  	
    	
    	return null;
    	
    }
	
	public SQLStatementType getSQLStatementType( String strSQL, CExtendedLogger Logger, CLanguage Lang ) {
		
		SQLStatementType Result = SQLStatementType.Unknown;

		try {
		
			if ( strSQL != null  && strSQL.length() >= 6 ) {

				strSQL = strSQL.toLowerCase();
				
				if ( strSQL.indexOf( ConstantsAbstractDBEngine._CALL ) == 0 ) {

					Result = SQLStatementType.Call;
					
				}
				else if ( strSQL.indexOf( ConstantsAbstractDBEngine._SELECT ) == 0 ) {

					Result = SQLStatementType.Select;
					
				}
				else if ( strSQL.indexOf( ConstantsAbstractDBEngine._INSERT ) == 0 ) {
					
					Result = SQLStatementType.Insert;
					
				}
				else if ( strSQL.indexOf( ConstantsAbstractDBEngine._UPDATE ) == 0 ) {
					
					Result = SQLStatementType.Update;
					
				}
				else if ( strSQL.indexOf( ConstantsAbstractDBEngine._DELETE ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._TRUNCATE ) == 0 ) {
					
					Result = SQLStatementType.Delete;
					
				}
				else if ( strSQL.indexOf( ConstantsAbstractDBEngine._CREATE ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._ALTER ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._MODIFY ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._DROP ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._SHOW ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._DESCRIBE ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._SET ) == 0 ) {
					
					Result = SQLStatementType.DDL;
					
				}
				
			}
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
		}
			
		return Result;
		
	}
    
	public boolean isModifySQLStatement( SQLStatementType SQLType ) {
		
		return SQLType == SQLStatementType.Insert || SQLType == SQLStatementType.Update || SQLType == SQLStatementType.Delete;
		
	} 
	
	public boolean setMacroValueToNamedPreparedStatement( CNamedPreparedStatement NamedPreparedStatement, String strFieldName, int intMacroIndex, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult =  false;
		
		try {

			if ( strMacrosValues[ intMacroIndex ] != null && strMacrosValues[ intMacroIndex ].isEmpty() == false && strMacrosValues[ intMacroIndex ].equals( NamesSQLTypes._NULL ) == false ) {

				switch ( intMacrosTypes[ intMacroIndex ] ) {
				
					case Types.INTEGER: { NamedPreparedStatement.setInt( strFieldName, Integer.parseInt( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.BIGINT: { NamedPreparedStatement.setLong( strFieldName, Long.parseLong( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.SMALLINT: { NamedPreparedStatement.setShort( strFieldName, Short.parseShort( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.VARCHAR: 
					case Types.CHAR: { NamedPreparedStatement.setString( strFieldName, strMacrosValues[ intMacroIndex ] ); bResult = true; break; }
					case Types.BOOLEAN: { NamedPreparedStatement.setBoolean( strFieldName, Boolean.parseBoolean( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.BLOB: { 	
						
										java.sql.Blob BlobData = new SerialBlob( Base64.decode( strMacrosValues[ intMacroIndex ].getBytes() ) );
									
									    NamedPreparedStatement.setBlob( strFieldName, BlobData );
			                            
									    bResult = true; 
									    
									    break; 
			
									 }
					case Types.DATE: {
						               
									    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
										
										java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
										
										java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
										
										NamedPreparedStatement.setDate( strFieldName, SQLDate );
						               
										bResult = true; 
										
										break; 
						             
					                 }
					case Types.TIME: {  
						
										SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
										
										java.util.Date Date = TimeFormat.parse( strMacrosValues[ intMacroIndex ] );
										
										java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
										
										NamedPreparedStatement.setTime( strFieldName, SQLTime );
										
										bResult = true; 
										
										break; 
						               
					                 }
					case Types.TIMESTAMP: {  
						
											 SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
											
											 java.util.Date Date = DateTimeFormat.parse( strMacrosValues[ intMacroIndex ] );
											
											 java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
											
											 NamedPreparedStatement.setTimestamp( strFieldName, SQLTimeStamp );
		
											 bResult = true; 
											 
						                     break; 
						                    
					                      }
					case Types.FLOAT: 
					case Types.DECIMAL: { NamedPreparedStatement.setFloat( strFieldName, Float.parseFloat( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.DOUBLE: { NamedPreparedStatement.setDouble( strFieldName, Double.parseDouble( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					default: {
						
						        if ( Logger != null && Lang != null ) {
						    	   
						        	Logger.LogWarning( "-1", Lang.Translate( "Unkown Macro type [%s], field name [%s], field value [%s]", Integer.toString( intMacrosTypes[ intMacroIndex ] ), strFieldName, strMacrosValues[ intMacroIndex ] ) );
						    	   
						        } 
		
					         }
				
				}
			
			}
			else {
				
				NamedPreparedStatement.setNull( strFieldName, intMacrosTypes[ intMacroIndex ] );
				
			}
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
		}
		
		return bResult;
		
	}
	
	public boolean setFieldValueToNamedPreparedStatement( CNamedPreparedStatement NamedPreparedStatement, int intFieldType, String strFieldName, String strFieldValue, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult =  false;
		
		try {

			if ( strFieldValue != null && strFieldValue.isEmpty() == false && strFieldValue.equals( NamesSQLTypes._NULL ) == false ) {
			
				switch ( intFieldType ) {
				
					case Types.INTEGER: { NamedPreparedStatement.setInt( strFieldName, Integer.parseInt( strFieldValue ) ); break; }
					case Types.BIGINT: { NamedPreparedStatement.setLong( strFieldName, Long.parseLong( strFieldValue ) ); break; }
					case Types.SMALLINT: { NamedPreparedStatement.setShort( strFieldName, Short.parseShort( strFieldValue ) ); break; }
					case Types.VARCHAR: 
					case Types.CHAR: { NamedPreparedStatement.setString( strFieldName, strFieldValue ); break; }
					case Types.BOOLEAN: { NamedPreparedStatement.setBoolean( strFieldName, Boolean.parseBoolean( strFieldValue ) ); break; }
					case Types.BLOB: { 	
						
										java.sql.Blob BlobData = new SerialBlob( Base64.decode( strFieldValue.getBytes() ) );
									
									    NamedPreparedStatement.setBlob( strFieldName, BlobData );
			                            
									    break; 
			
									 }
					case Types.DATE: {
						               
									    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
										
										java.util.Date Date = DateFormat.parse( strFieldValue );
										
										java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
										
										NamedPreparedStatement.setDate( strFieldName, SQLDate );
						               
										break; 
						             
					                 }
					case Types.TIME: {  
						
										SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
										
										java.util.Date Date = TimeFormat.parse( strFieldValue );
										
										java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
										
										NamedPreparedStatement.setTime( strFieldName, SQLTime );
						               
										break; 
						               
					                 }
					case Types.TIMESTAMP: {  
						
											 SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
											
											 java.util.Date Date = DateTimeFormat.parse( strFieldValue );
											
											 java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
											
											 NamedPreparedStatement.setTimestamp( strFieldName, SQLTimeStamp );
	
						                     break; 
						                    
					                      }
					case Types.FLOAT: 
					case Types.DECIMAL: { NamedPreparedStatement.setFloat( strFieldName, Float.parseFloat( strFieldValue ) ); break; }
					case Types.DOUBLE: { NamedPreparedStatement.setDouble( strFieldName, Double.parseDouble( strFieldValue ) ); break; }
					default: {
						
						       if ( Logger != null && Lang != null ) {
						    	   
									Logger.LogWarning( "-1", Lang.Translate( "Unkown SQL field type [%s], field name [%s], field value [%s]", Integer.toString( intFieldType ), strFieldName, strFieldValue ) );
						    	   
						       } 
						
					        }
	
				}
			
			}
			else {
				
				NamedPreparedStatement.setNull( strFieldName, intFieldType );
				
			}
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
		}
		
		return bResult;
	
	}
	
	public boolean setMacroValueToNamedCallableStatement( CNamedCallableStatement NamedCallableStatement, String strFieldName, int intMacroIndex, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult =  false;
		
		try {

			if ( strMacrosValues[ intMacroIndex ] != null && strMacrosValues[ intMacroIndex ].isEmpty() == false && strMacrosValues[ intMacroIndex ].equals( NamesSQLTypes._NULL ) == false ) {

				switch ( intMacrosTypes[ intMacroIndex ] ) {
				
					case Types.INTEGER: { NamedCallableStatement.setInt( strFieldName, Integer.parseInt( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.BIGINT: { NamedCallableStatement.setLong( strFieldName, Long.parseLong( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.SMALLINT: { NamedCallableStatement.setShort( strFieldName, Short.parseShort( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.VARCHAR: 
					case Types.CHAR: { NamedCallableStatement.setString( strFieldName, strMacrosValues[ intMacroIndex ] ); bResult = true; break; }
					case Types.BOOLEAN: { NamedCallableStatement.setBoolean( strFieldName, Boolean.parseBoolean( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.BLOB: { 	
						
										java.sql.Blob BlobData = new SerialBlob( Base64.decode( strMacrosValues[ intMacroIndex ].getBytes() ) );
									
									    NamedCallableStatement.setBlob( strFieldName, BlobData );
			                            
									    bResult = true; 
									    
									    break; 
			
									 }
					case Types.DATE: {
						               
									    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
										
										java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
										
										java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
										
										NamedCallableStatement.setDate( strFieldName, SQLDate );
						               
										bResult = true; 
										
										break; 
						             
					                 }
					case Types.TIME: {  
						
										SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
										
										java.util.Date Date = TimeFormat.parse( strMacrosValues[ intMacroIndex ] );
										
										java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
										
										NamedCallableStatement.setTime( strFieldName, SQLTime );
										
										bResult = true; 
										
										break; 
						               
					                 }
					case Types.TIMESTAMP: {  
						
											 SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
											
											 java.util.Date Date = DateTimeFormat.parse( strMacrosValues[ intMacroIndex ] );
											
											 java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
											
											 NamedCallableStatement.setTimestamp( strFieldName, SQLTimeStamp );
		
											 bResult = true; 
											 
						                     break; 
						                    
					                      }
					case Types.FLOAT: 
					case Types.DECIMAL: { NamedCallableStatement.setFloat( strFieldName, Float.parseFloat( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.DOUBLE: { NamedCallableStatement.setDouble( strFieldName, Double.parseDouble( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					default: {
						
						        if ( Logger != null && Lang != null ) {
						    	   
						        	Logger.LogWarning( "-1", Lang.Translate( "Unkown Macro type [%s], field name [%s], field value [%s]", Integer.toString( intMacrosTypes[ intMacroIndex ] ), strFieldName, strMacrosValues[ intMacroIndex ] ) );
						    	   
						        } 
		
					         }
				
				}
			
			}
			else {
				
				NamedCallableStatement.setNull( strFieldName, intMacrosTypes[ intMacroIndex ] );
				
			}
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
		}
		
		return bResult;
		
	}
	
	public boolean setFieldValueToNamedCallableStatement( CNamedCallableStatement NamedCallableStatement, int intFieldType, String strFieldName, String strFieldValue, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult =  false;
		
		try {

			if ( strFieldValue != null && strFieldValue.isEmpty() == false && strFieldValue.equals( NamesSQLTypes._NULL ) == false ) {
			
				switch ( intFieldType ) {
				
					case Types.INTEGER: { NamedCallableStatement.setInt( strFieldName, Integer.parseInt( strFieldValue ) ); break; }
					case Types.BIGINT: { NamedCallableStatement.setLong( strFieldName, Long.parseLong( strFieldValue ) ); break; }
					case Types.SMALLINT: { NamedCallableStatement.setShort( strFieldName, Short.parseShort( strFieldValue ) ); break; }
					case Types.VARCHAR: 
					case Types.CHAR: { NamedCallableStatement.setString( strFieldName, strFieldValue ); break; }
					case Types.BOOLEAN: { NamedCallableStatement.setBoolean( strFieldName, Boolean.parseBoolean( strFieldValue ) ); break; }
					case Types.BLOB: { 	
						
										java.sql.Blob BlobData = new SerialBlob( Base64.decode( strFieldValue.getBytes() ) );
									
									    NamedCallableStatement.setBlob( strFieldName, BlobData );
			                            
									    break; 
			
									 }
					case Types.DATE: {
						               
									    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
										
										java.util.Date Date = DateFormat.parse( strFieldValue );
										
										java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
										
										NamedCallableStatement.setDate( strFieldName, SQLDate );
						               
										break; 
						             
					                 }
					case Types.TIME: {  
						
										SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
										
										java.util.Date Date = TimeFormat.parse( strFieldValue );
										
										java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
										
										NamedCallableStatement.setTime( strFieldName, SQLTime );
						               
										break; 
						               
					                 }
					case Types.TIMESTAMP: {  
						
											 SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
											
											 java.util.Date Date = DateTimeFormat.parse( strFieldValue );
											
											 java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
											
											 NamedCallableStatement.setTimestamp( strFieldName, SQLTimeStamp );
	
						                     break; 
						                    
					                      }
					case Types.FLOAT: 
					case Types.DECIMAL: { NamedCallableStatement.setFloat( strFieldName, Float.parseFloat( strFieldValue ) ); break; }
					case Types.DOUBLE: { NamedCallableStatement.setDouble( strFieldName, Double.parseDouble( strFieldValue ) ); break; }
					default: {
						
						       if ( Logger != null && Lang != null ) {
						    	   
									Logger.LogWarning( "-1", Lang.Translate( "Unkown SQL field type [%s], field name [%s], field value [%s]", Integer.toString( intFieldType ), strFieldName, strFieldValue ) );
						    	   
						       } 
						
					        }
	
				}
			
			}
			else {
				
				NamedCallableStatement.setNull( strFieldName, intFieldType );
				
			}
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
		}
		
		return bResult;
	
	}
	
    public abstract Connection getDBConnection( CDBEngineConfigConnection ConfigDBConnection, CExtendedLogger Logger, CLanguage Lang );
	
	public boolean isValid( Connection DBConnection, CExtendedLogger Logger, CLanguage Lang ) {
		
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
	
	public boolean setAutoCommit( Connection DBConnection, boolean bAutoCommit, CExtendedLogger Logger, CLanguage Lang ) {
		
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
	
	public boolean commit( Connection DBConnection, CExtendedLogger Logger, CLanguage Lang ) { 

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
	
	public boolean rollback( Connection DBConnection, CExtendedLogger Logger, CLanguage Lang ) {
		
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
	
	public boolean close( Connection DBConnection, CExtendedLogger Logger, CLanguage Lang ) {
		
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

    public CMemoryRowSet ExecuteQuerySQLByInputServiceParameters( Connection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {

		CMemoryRowSet Result = null;
		
		try {	
			
			HashMap<String,String> Delimiters = new HashMap<String,String>();
			
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );
			
			CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( DBConnection, strSQL, Delimiters );		

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

    public boolean ExecuteModifySQLByInputServiceParameters( Connection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		try {	
			
			HashMap<String,String> Delimiters = new HashMap<String,String>();
			
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );
			
			CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( DBConnection, strSQL, Delimiters );		

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
	
	public CMemoryRowSet ExecuteCallableStatementByInputServiceParameters( Connection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, CExtendedLogger Logger, CLanguage Lang  ) {

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
	
	public abstract ResultSet ExecuteDummySQL( Connection DBConnection, String strOptionalDummySQL, CExtendedLogger Logger, CLanguage Lang );

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
    
    public CResultSetResult ExecutePlainQuerySQL( Connection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	CResultSetResult Result = new CResultSetResult( -1, -1, "" ); 
    	
    	try {
    	
    		Statement SQLStatement = DBConnection.createStatement();
    		
    		ResultSet ResultQuery = SQLStatement.executeQuery( strSQL );
    		
    		Result.intCode = 1;
    		Result.Result = ResultQuery;

    		if ( Lang != null )   
    			Result.strDescription = Lang.Translate( "Sucess to execute the plain query SQL statement [%s]", strSQL );
    		else
    			Result.strDescription = String.format( "Sucess to execute the plain query SQL statement [%s]", strSQL );
    			  
    	
    	}
    	catch ( Exception Ex ) {
    		
    		if ( Lang != null )   
    		    Result.strDescription = Lang.Translate( "Error to execute the plain query SQL statement [%s]", strSQL );
    		else
    		    Result.strDescription = String.format( "Error to execute the plain query SQL statement [%s]", strSQL ) ;

    		if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
    		
    	}
    	
    	return Result;
    	
    }
    
    public CResultSetResult ExecutePlainCommonSQL( Connection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	CResultSetResult Result = new CResultSetResult( -1, -1, "" ); 
    	
    	try {
    	
    		Statement SQLStatement = DBConnection.createStatement();

    		Result.lngAffectedRows = SQLStatement.executeUpdate( strSQL );
    		Result.intCode = 1;

    		if ( Lang != null )   
    			Result.strDescription = Lang.Translate( "Sucess to execute the plain SQL statement [%s]", strSQL );
    		else
    			Result.strDescription = String.format( "Sucess to execute the plain SQL statement [%s]", strSQL );
    	
    		SQLStatement.close();
    		
    	}
    	catch ( Exception Ex ) {
    		
    		if ( Lang != null )   
    		    Result.strDescription = Lang.Translate( "Error to execute the plain SQL statement [%s]", strSQL );
    		else
    		    Result.strDescription = String.format( "Error to execute the plain SQL statement [%s]", strSQL ) ;

    		if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
    		
    	}
    	
    	return Result;
    	
    }
    
    public CResultSetResult ExecutePlainInsertSQL( Connection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {

    	//Warning: Reimplement for MySQL and another RDBMS with "autoincrement" column table type, see jdbc getGeneratedKeys() for more details

    	return ExecutePlainCommonSQL( DBConnection, strSQL, Logger, Lang );    	
    	
    }
    
    public CResultSetResult ExecutePlainUpdateSQL( Connection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {

    	return ExecutePlainCommonSQL( DBConnection, strSQL, Logger, Lang );    	
    	
    }
    
    public CResultSetResult ExecutePlainDeleteSQL( Connection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {

    	return ExecutePlainCommonSQL( DBConnection, strSQL, Logger, Lang );    	
    	
    }
    
    public CResultSetResult ExecutePlainDDLSQL( Connection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {

    	return ExecutePlainCommonSQL( DBConnection, strSQL, Logger, Lang );    	
    	
    }
    
    public CResultSetResult ExecutePlainCallableStatement( Connection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	CResultSetResult Result = new CResultSetResult( -1, -1, "" ); 
    	
    	try {
    	
    		CallableStatement CallStatement = DBConnection.prepareCall( strSQL );

    		CallStatement.execute();
    		
    		Result.intCode = 1;
    		Result.Result = CallStatement.getResultSet();
    		Result.lngAffectedRows = 0;

    		if ( Lang != null )   
    			Result.strDescription = Lang.Translate( "Sucess to execute the plain SQL statement [%s]", strSQL );
    		else
    			Result.strDescription = String.format( "Sucess to execute the plain SQL statement [%s]", strSQL );
    		
    	}
    	catch ( Exception Ex ) {
    		
    		if ( Lang != null )   
    		    Result.strDescription = Lang.Translate( "Error to execute the plain SQL statement [%s]", strSQL );
    		else
    		    Result.strDescription = String.format( "Error to execute the plain SQL statement [%s]", strSQL ) ;

    		if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
    		
    	}
    	
    	return Result;
    	
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

				try {
					
					CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( DBConnection, MainNamedPreparedStatement.getNamedParams(), MainNamedPreparedStatement.getParsedStatement() );

					while ( i.hasNext() ) {

						Entry<String,Integer> NamedParam = i.next();

						MemoryRowSet.setFieldDataToPreparedStatement( NamedPreparedStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );

					}

					if ( bLogParsedSQL == true ) {
						
						Logger.LogInfo( "2", Lang.Translate( "Executing the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
					}
					
					ResultSet QueryResult = NamedPreparedStatement.executeQuery();
				
					if ( QueryResult != null ) {
						
						Result.add( new CResultSetResult( -1, 1, Lang.Translate( "Sucess to execute the SQL statement" ), NamedPreparedStatement, QueryResult ) );
						
					}
					
				}
				catch ( Exception Ex ) {
					
					if ( Logger != null ) {
					
						Result.add( new CResultSetResult( -1, -1, Lang.Translate( "Error to execute the SQL statement, see the log file for more details" ) ) );

						Logger.LogError( "-1001", Lang.Translate( "Error to execute the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
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
    
    public ArrayList<CResultSetResult> ExecuteCommonComplexSQL( Connection DBConnection, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
 
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
			
			if ( intMaxCalls > 0 ) {
			
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

					try {

						CNamedPreparedStatement NamedPreparedStatement = new CNamedPreparedStatement( DBConnection, MainNamedPreparedStatement.getNamedParams(), MainNamedPreparedStatement.getParsedStatement() );

						while ( i.hasNext() ) {

							Entry<String,Integer> NamedParam = i.next();

							MemoryRowSet.setFieldDataToPreparedStatement( NamedPreparedStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );

						}

						if ( bLogParsedSQL == true ) {

							Logger.LogInfo( "2", Lang.Translate( "Executing the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );

						}

						int intAffectedRows = NamedPreparedStatement.executeUpdate();

						Result.add( new CResultSetResult( intAffectedRows, 1, Lang.Translate( "Sucess to execute the SQL statement" ) ) );

						NamedPreparedStatement.close(); //Close immediately to prevent resource leak in database driver

					}
					catch ( Exception Ex ) {

						if ( Logger != null ) {

							Result.add( new CResultSetResult( -1, -1, Lang.Translate( "Error to execute the SQL statement, see the log file for more details" ) ) );

							Logger.LogError( "-1001", Lang.Translate( "Error to execute the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );

							Logger.LogException( "-1016", Ex.getMessage(), Ex );

						}	

					}

				}

			}
			else {
				
				Result.add( new CResultSetResult( -1, -1, Lang.Translate( "Error to execute the SQL statement, see the log file for more details" ) ) );

				Logger.LogError( "-1001", Lang.Translate( "Error to execute the next SQL statement, no valid params names found for service call" )  );
				
			}
			
			MainNamedPreparedStatement.close();

    	}
    	catch ( Exception Ex ) {
    		
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 
    		
    	}
		
    	return Result;
    	
    }

    public ArrayList<CResultSetResult> ExecuteComplexInsertSQL( Connection DBConnection, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	//Warning: Reimplement for MySQL and another RDBMS with "autoincrement" column table type, see jdbc getGeneratedKeys() for more details

    	return ExecuteCommonComplexSQL( DBConnection, Request, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, strSQL, bLogParsedSQL, Logger, Lang );
    	
    } 

    public ArrayList<CResultSetResult> ExecuteComplexUpdateSQL( Connection DBConnection, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	return ExecuteCommonComplexSQL( DBConnection, Request, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, strSQL, bLogParsedSQL, Logger, Lang );
    	
    }
    
    public ArrayList<CResultSetResult> ExecuteComplexDeleteSQL( Connection DBConnection, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	return ExecuteCommonComplexSQL( DBConnection, Request, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, strSQL, bLogParsedSQL, Logger, Lang );
    	
    }
    
    public ArrayList<CResultSetResult> ExecuteComplexDDL( Connection DBConnection, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	return ExecuteCommonComplexSQL( DBConnection, Request, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, strSQL, bLogParsedSQL, Logger, Lang );
    	
    }
    
    public ArrayList<CResultSetResult> ExecuteComplexCallableStatement( Connection DBConnection, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	ArrayList<CResultSetResult> Result = new ArrayList<CResultSetResult>();

    	try {
    	
    		HashMap<String,String> Delimiters = new HashMap<String,String>();

    		Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
    		Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );

    		CNamedCallableStatement MainNamedCallableStatement = new CNamedCallableStatement( DBConnection, strSQL, Delimiters );		
    	
			LinkedHashMap<String,Integer> NamedParams = MainNamedCallableStatement.getNamedParams();
			
			Iterator<Entry<String, Integer>> i = NamedParams.entrySet().iterator();
			
			CMemoryRowSet MemoryRowSet = new CMemoryRowSet( false );
			
			int intMaxCalls = 0;
			
			while ( i.hasNext() ) {
			       
				Entry<String,Integer> NamedParam = i.next();
				
				String strInputServiceParameterValue = Request.getParameter( NamedParam.getKey() );

				int intMacroIndex = Utilities.getIndexByValue( strMacrosNames, ConfigXMLTagsServicesDaemon._StartMacroTag + NamedParam.getKey() + ConfigXMLTagsServicesDaemon._EndMacroTag );

				if ( intMacroIndex >= 0 ) {
					
					if ( intMacroIndex < intMacrosTypes.length && intMacroIndex < strMacrosValues.length ) {
						
						this.setMacroValueToNamedCallableStatement( MainNamedCallableStatement, NamedParam.getKey(), intMacroIndex, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );

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

				String strParsedStatement = new String( MainNamedCallableStatement.getParsedStatement() );
				
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

				CNamedCallableStatement NamedCallableStatement = new CNamedCallableStatement( DBConnection, MainNamedCallableStatement.getNamedParams(), MainNamedCallableStatement.getParsedStatement() );
				
				while ( i.hasNext() ) {
				       
					Entry<String,Integer> NamedParam = i.next();

					MemoryRowSet.setFieldDataToCallableStatement( NamedCallableStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );
				
				}

				try {
				
					if ( bLogParsedSQL == true ) {
						
						Logger.LogInfo( "2", Lang.Translate( "Executing the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
					}
					
					NamedCallableStatement.execute();
					
					Result.add( new CResultSetResult( 0, 1, Lang.Translate( "Sucess to execute the SQL statement" ), NamedCallableStatement, NamedCallableStatement.getResultSet() ) );
					
				}
				catch ( Exception Ex ) {
					
					if ( Logger != null ) {
					
						Result.add( new CResultSetResult( -1, -1, Lang.Translate( "Error to execute the SQL statement, see the log file for more details" ) ) );

						Logger.LogError( "-1001", Lang.Translate( "Error to execute the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
						Logger.LogException( "-1016", Ex.getMessage(), Ex );
					
					}	
				
				}
				
			}

			MainNamedCallableStatement.close();

    	}
    	catch ( Exception Ex ) {
    		
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 
    		
    	}
		
    	return Result;
    	
    }

    public void CloseResultSetResultStatement( ArrayList<CResultSetResult> ResultSetResults, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	if ( ResultSetResults != null ) {

    		for ( CResultSetResult ResultSetResult: ResultSetResults ) {

    			try {

    				if ( ResultSetResult.NamedPreparedStatement != null ) {   

    					ResultSetResult.NamedPreparedStatement.close();

    					ResultSetResult.NamedPreparedStatement = null;

    				}
    				
    				if ( ResultSetResult.NamedCallableStatement != null ) {
    					
    					ResultSetResult.NamedCallableStatement.close();

    					ResultSetResult.NamedCallableStatement = null;
    					
    				}
    				
    				if ( ResultSetResult.SQLStatement != null ) {
    					
    					ResultSetResult.SQLStatement.close();

    					ResultSetResult.SQLStatement = null;
    					
    				}
    				
    				ResultSetResult.Result = null;

    			}
    			catch ( Exception Ex ) {

    				if ( Logger != null )
    					Logger.LogException( "-1015", Ex.getMessage(), Ex ); 

    			}

    		}

    	}
    	
    }
    
    public void CloseResultSetResultStatement( CResultSetResult ResultSetResult, CExtendedLogger Logger, CLanguage Lang ) {
    	
		try {

			if ( ResultSetResult.NamedPreparedStatement != null ) {   

				ResultSetResult.NamedPreparedStatement.close();

				ResultSetResult.NamedPreparedStatement = null;

			}

			if ( ResultSetResult.NamedCallableStatement != null ) {
				
				ResultSetResult.NamedCallableStatement.close();

				ResultSetResult.NamedCallableStatement = null;
				
			}
			
			if ( ResultSetResult.SQLStatement != null ) {
				
				ResultSetResult.SQLStatement.close();

				ResultSetResult.SQLStatement = null;
				
			}
			
			ResultSetResult.Result = null;
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 

		}
    	
    }
    
}
