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

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.serial.SerialBlob;

import net.maindataservices.Base64;
import net.maindataservices.Utilities;


import AbstractService.CInputServiceParameter;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryFieldData.TFieldScope;
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
				
				if ( strSQL.indexOf( ConstantsAbstractDBEngine._CALL ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._CALL1 ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._CALL_FUNCTION ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._CALL_FUNCTION1 ) == 0 ) { 

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
				else if ( strSQL.indexOf( ConstantsAbstractDBEngine._CREATE ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._ALTER ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._MODIFY ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._DROP ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._SHOW ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._DESCRIBE ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._SET ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._GO ) == 0) {
					
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
    
    public String RemoveCallFunction( String strSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
		try {
			
			if ( strSQL.indexOf( ConstantsAbstractDBEngine._CALL_FUNCTION ) == 0  ) {

				String strTmp = strSQL.substring( ConstantsAbstractDBEngine._CALL_FUNCTION.length() ).trim(); //strSQL.replace( ConstantsAbstractDBEngine._CALL_FUNCTION, ConstantsAbstractDBEngine._CALL); //( ConstantsAbstractDBEngine._CALL_FUNCTION.length() );

				return strTmp;

			}
			else if ( strSQL.indexOf( ConstantsAbstractDBEngine._CALL_FUNCTION1 ) == 0 ) {

				String strTmp = "{" + strSQL.substring( ConstantsAbstractDBEngine._CALL_FUNCTION1.length() ).trim(); //strSQL.replace( ConstantsAbstractDBEngine._CALL_FUNCTION1, ConstantsAbstractDBEngine._CALL1 );

				return strTmp;

			}
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
		}
    	
    	return strSQL;
    	
    }
	
	public boolean isModifySQLStatement( SQLStatementType SQLType ) {
		
		return SQLType == SQLStatementType.Insert || SQLType == SQLStatementType.Update || SQLType == SQLStatementType.Delete;
		
	} 
	
	public boolean setMacroValueToNamedPreparedStatement( CNamedPreparedStatement NamedPreparedStatement, String strFieldName, int intMacroIndex, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult =  false;
		
		try {

			if ( strMacrosValues[ intMacroIndex ] != null && strMacrosValues[ intMacroIndex ].isEmpty() == false && strMacrosValues[ intMacroIndex ].equals( NamesSQLTypes._NULL ) == false ) {

				switch ( this.getJavaSQLColumnType( intMacrosTypes[ intMacroIndex ], Logger, Lang ) ) {
				
					case Types.INTEGER: { NamedPreparedStatement.setInt( strFieldName, Integer.parseInt( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.BIGINT: { NamedPreparedStatement.setLong( strFieldName, Long.parseLong( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.SMALLINT: { NamedPreparedStatement.setShort( strFieldName, Short.parseShort( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.VARCHAR: 
					case Types.CHAR: { NamedPreparedStatement.setString( strFieldName, strMacrosValues[ intMacroIndex ] ); bResult = true; break; }
					case Types.BOOLEAN: { NamedPreparedStatement.setBoolean( strFieldName, Boolean.parseBoolean( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
					//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
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
			
				switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
				
					case Types.INTEGER: { NamedPreparedStatement.setInt( strFieldName, Integer.parseInt( strFieldValue ) ); break; }
					case Types.BIGINT: { NamedPreparedStatement.setLong( strFieldName, Long.parseLong( strFieldValue ) ); break; }
					case Types.SMALLINT: { NamedPreparedStatement.setShort( strFieldName, Short.parseShort( strFieldValue ) ); break; }
					case Types.VARCHAR: 
					case Types.CHAR: { NamedPreparedStatement.setString( strFieldName, strFieldValue ); break; }
					case Types.BOOLEAN: { NamedPreparedStatement.setBoolean( strFieldName, Boolean.parseBoolean( strFieldValue ) ); break; }
					//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
					//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
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

				switch ( this.getJavaSQLColumnType( intMacrosTypes[ intMacroIndex ], Logger, Lang ) ) {
				
					case Types.INTEGER: { NamedCallableStatement.setInt( strFieldName, Integer.parseInt( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.BIGINT: { NamedCallableStatement.setLong( strFieldName, Long.parseLong( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.SMALLINT: { NamedCallableStatement.setShort( strFieldName, Short.parseShort( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					case Types.VARCHAR: 
					case Types.CHAR: { NamedCallableStatement.setString( strFieldName, strMacrosValues[ intMacroIndex ] ); bResult = true; break; }
					case Types.BOOLEAN: { NamedCallableStatement.setBoolean( strFieldName, Boolean.parseBoolean( strMacrosValues[ intMacroIndex ] ) ); bResult = true; break; }
					//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
					//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
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
	
	public boolean setFieldValueToNamedCallableStatement( CNamedCallableStatement NamedCallableStatement, TParameterScope FieldScope, int intFieldType, String strFieldName, String strFieldValue, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult =  false;
		
		try {

			if ( FieldScope.equals( TParameterScope.IN ) || FieldScope.equals( TParameterScope.INOUT ) ) {

				if ( strFieldValue != null && strFieldValue.isEmpty() == false && strFieldValue.equals( NamesSQLTypes._NULL ) == false ) {
				
					switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
					
						case Types.INTEGER: { NamedCallableStatement.setInt( strFieldName, Integer.parseInt( strFieldValue ) ); break; }
						case Types.BIGINT: { NamedCallableStatement.setLong( strFieldName, Long.parseLong( strFieldValue ) ); break; }
						case Types.SMALLINT: { NamedCallableStatement.setShort( strFieldName, Short.parseShort( strFieldValue ) ); break; }
						case Types.VARCHAR: 
						case Types.CHAR: { NamedCallableStatement.setString( strFieldName, strFieldValue ); break; }
						case Types.BOOLEAN: { NamedCallableStatement.setBoolean( strFieldName, Boolean.parseBoolean( strFieldValue ) ); break; }
						//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
						//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
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
			
			if ( FieldScope.equals( TParameterScope.OUT ) ) {
				
				try {
					
					NamedCallableStatement.registerOutParameter( strFieldName, intFieldType );
				
				}
				catch ( Exception Ex ) {
					
					if ( Logger != null ) {
						
						Logger.LogException( "-1015", Ex.getMessage(), Ex );
					
					}
					
				}
				
			}
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			
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
				
				Result = new CMemoryRowSet( false, NamedPreparedStatement.executeQuery(), this, Logger, Lang );
				
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
			
			boolean bContainsOutParams = false; 
			
			boolean bIsOutParam =  false;
			
			CMemoryRowSet TmpMemoryRowSet = new CMemoryRowSet( false );
			
			while ( i.hasNext() ) {
			       
				Entry<String,Integer> NamedParam = i.next();

				CInputServiceParameter InputServiceParameterDef = this.getInputServiceParameterByName( InputServiceParameters, NamedParam.getKey() );

				String strInputServiceParameterValue = Request.getParameter( NamedParam.getKey() );

				int intMacroIndex = Utilities.getIndexByValue( strMacrosNames, ConfigXMLTagsServicesDaemon._StartMacroTag + NamedParam.getKey() + ConfigXMLTagsServicesDaemon._EndMacroTag );
				
				bIsOutParam = InputServiceParameterDef != null && ( InputServiceParameterDef.getParameterScope().equals( TParameterScope.OUT ) || InputServiceParameterDef.getParameterScope().equals( TParameterScope.INOUT ) );
				
				if ( InputServiceParameterDef != null && ( strInputServiceParameterValue != null || bIsOutParam ) ) {
				
					if ( bIsOutParam ) {

                        int intFieldLength = 0;						
						
						if ( InputServiceParameterDef.getParameterDataTypeWidth() != null || InputServiceParameterDef.getParameterDataTypeWidth().isEmpty() == false ) {
							
							intFieldLength = Integer.parseInt( InputServiceParameterDef.getParameterDataTypeWidth() );
							
						}
						
						TmpMemoryRowSet.addField( InputServiceParameterDef.getParameterName(), InputServiceParameterDef.getParameterDataTypeID(), InputServiceParameterDef.getParameterDataType(), intFieldLength, InputServiceParameterDef.getParameterName(), TFieldScope.OUT );
						
						bContainsOutParams = true;
						
					}
					
					this.setFieldValueToNamedCallableStatement( NamedCallableStatement, InputServiceParameterDef.getParameterScope(), InputServiceParameterDef.getParameterDataTypeID(), NamedParam.getKey(), strInputServiceParameterValue, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
	    			
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
                
				if ( ( strInputServiceParameterValue == null && bIsOutParam == false ) && intMacroIndex == -1 ) {
                	
                    if ( Logger != null ) {
                    	
                		Logger.LogWarning( "-1", Lang.Translate( "Input parameter [%s] value not found on request", NamedParam.getKey() ) );        
                    	
                    }

                    bSQLParsed = false;
                	
                }
				
			}
			
			if ( bSQLParsed == true ) {
				
				NamedCallableStatement.execute();
				
				ResultSet CallableStatementResultSet = NamedCallableStatement.getResultSet();

				if ( CallableStatementResultSet == null && bContainsOutParams ) {
					
					CallableStatementResultSet = this.BuildCachedRowSetFromFieldsScopeOut( TmpMemoryRowSet, NamedCallableStatement, Logger, Lang );
					
				}

				Result = new CMemoryRowSet( false, CallableStatementResultSet, this, Logger, Lang );
				
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
    
    public CResultSetResult ExecutePlainQuerySQL( Connection DBConnection, String strSQL, int intInternaFetchSize, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	CResultSetResult Result = new CResultSetResult( -1, -1, "" ); 
    	
    	try {
    	
    		Statement SQLStatement = DBConnection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
    		
            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query 
            	
        		if ( Lang != null )   
				   Logger.LogInfo( "0x2001", Lang.Translate( "Init plain SQL statement" ) );
        		else
 				   Logger.LogInfo( "0x2001", "Init plain SQL statement" );
        			
            }	
    		
    		ResultSet QueryResult = SQLStatement.executeQuery( strSQL );
    		
    		QueryResult.setFetchSize( intInternaFetchSize );
    		
    		if ( QueryResult.getFetchSize() != intInternaFetchSize ) {
    			
				Logger.LogWarning( "-1", Lang.Translate(  "The JDBC Driver [%s] ignoring fetch size value [%s], using JDBC driver default value [%s]", DBConnection.getMetaData().getDriverName(), Integer.toString( intInternaFetchSize ), Integer.toString( QueryResult.getFetchSize() ) ) );
    			
    		}
    		
            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query
            	
        		if ( Lang != null )   
				   Logger.LogInfo( "0x2002", Lang.Translate( "End plain SQL statement" ) );
        		else
 				   Logger.LogInfo( "0x2002", "End plain SQL statement" );
        			
            }	

    		Result.intCode = 1;
    		Result.Result = QueryResult;

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
    	
    		Statement SQLStatement = DBConnection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );

            if ( Logger != null ) {
            	
        		if ( Lang != null )   
				   Logger.LogInfo( "0x2003", Lang.Translate( "Init plain SQL statement" ) );
        		else
 				   Logger.LogInfo( "0x2003", "Init plain SQL statement" );
        			
            }
            
    		Result.lngAffectedRows = SQLStatement.executeUpdate( strSQL );

            if ( Logger != null ) {
            	
        		if ( Lang != null )   
				   Logger.LogInfo( "0x2004", Lang.Translate( "End plain SQL statement" ) );
        		else
 				   Logger.LogInfo( "0x2004", "End plain SQL statement" );
        			
            }
            
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
    
    public boolean IsValidResult( ResultSet Resultset, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	try {
    	
    		if ( Resultset != null ) {
    		
    			ResultSetMetaData DataSetMetaData = Resultset.getMetaData();

    			for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

    				String strFieldName   = DataSetMetaData.getColumnName( i );

    				if ( strFieldName == null || strFieldName.isEmpty() == true )
    					strFieldName = DataSetMetaData.getColumnLabel( i );

    				int  intFieldType = this.getJavaSQLColumnType( DataSetMetaData.getColumnType( i ), Logger, Lang );

    				if ( intFieldType == 1111 || ( strFieldName == null || strFieldName.isEmpty() ) ) { //JDK other field type

    					return false;

    				}

    			}
            
    		}
    		else {
    			
    			return false;
    			
    		}
    		
    	}
    	catch ( Exception Ex ) {
    		
    		if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
    		
    		return false;
    		
    	}
    	
    	return true;
    	
    }
    
    public CResultSetResult ExecutePlainCallableStatement( Connection DBConnection, String strSQL, int intInternaFetchSize, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	CResultSetResult Result = new CResultSetResult( -1, -1, "" ); 
    	
    	try {
    	
    		strSQL = this.RemoveCallFunction(strSQL, Logger, Lang );
    		
    		CallableStatement CallStatement = DBConnection.prepareCall( strSQL );

			/*CallStatement.registerOutParameter( 1, Types.INTEGER );
			CallStatement.registerOutParameter( 2, Types.INTEGER );
			CallStatement.registerOutParameter( 3, Types.INTEGER );
			CallStatement.registerOutParameter( 4, Types.DATE );
			CallStatement.registerOutParameter( 5, Types.TIME );*/

    		CallStatement.execute();
    		
    		Result.intCode = 1; //CallStatement.getInt( "IdValid" );
    		Result.Result = CallStatement.getResultSet();
    		
    		if ( this.IsValidResult( Result.Result, Logger, Lang ) == false ) {
    		
    			Result.Result = null;
    			
    		}	
    		else { 
			
    			Result.Result.setFetchSize( intInternaFetchSize );

    			if ( Result.Result.getFetchSize() != intInternaFetchSize ) {

    				Logger.LogWarning( "-1", Lang.Translate(  "The JDBC Driver [%s] ignoring fetch size value [%s], using JDBC driver default value [%s]", DBConnection.getMetaData().getDriverName(), Integer.toString( intInternaFetchSize ), Integer.toString( Result.Result.getFetchSize() ) ) );

    			}

    		}

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

	public boolean setFieldDataToPreparedStatement( CMemoryRowSet MemoryRowSet, CNamedPreparedStatement NamedPreparedStatement, String strPreparedStatementFieldName, String strFieldName, int intIndexRow, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		CMemoryFieldData MemoryField = MemoryRowSet.getFieldByName( strFieldName );
		
		if ( MemoryField != null ) {
			
			if ( intIndexRow >= MemoryField.Data.size() && bUseLastRowIfRowNotExits ) {
				
				intIndexRow = MemoryField.Data.size() - 1;
				
			}
			
			if ( intIndexRow >= 0 && intIndexRow < MemoryField.Data.size() ) {
				
				Object FieldData = MemoryField.Data.get( intIndexRow );
				
				if ( FieldData != null ) { 
					
					try {

						switch ( this.getJavaSQLColumnType( MemoryField.intSQLType, Logger, Lang ) ) {

							case Types.INTEGER: { NamedPreparedStatement.setInt( strPreparedStatementFieldName, (Integer) FieldData ); bResult = true; break; }
							case Types.BIGINT: { NamedPreparedStatement.setLong( strPreparedStatementFieldName, (Long) FieldData ); bResult = true; break; }
							case Types.SMALLINT: { NamedPreparedStatement.setShort( strPreparedStatementFieldName, (Short) FieldData ); bResult = true; break; }
							case Types.VARCHAR: 
							case Types.CHAR: { NamedPreparedStatement.setString( strPreparedStatementFieldName, (String) FieldData ); bResult = true; break; }
							case Types.BOOLEAN: { NamedPreparedStatement.setBoolean( strPreparedStatementFieldName, ( Boolean ) FieldData ); bResult = true; break; }
							//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
							//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
							case Types.BLOB: { 	
								
								                NamedPreparedStatement.setBlob( strPreparedStatementFieldName, (Blob) FieldData );
					                            
											    bResult = true;
											    
											    break; 
					
											 }
							case Types.DATE: {
								               
												NamedPreparedStatement.setDate( strPreparedStatementFieldName, (java.sql.Date) FieldData );
								               
												bResult = true;
												
												break; 
								             
							                 }
							case Types.TIME: {  
												
												NamedPreparedStatement.setTime( strPreparedStatementFieldName, (java.sql.Time) FieldData );
								               
												bResult = true;
												
												break; 
								               
							                 }
							case Types.TIMESTAMP: {  
								
													 NamedPreparedStatement.setTimestamp( strPreparedStatementFieldName, (java.sql.Timestamp) FieldData );
		
													 bResult = true;
													 
								                     break; 
								                    
							                      }
							case Types.FLOAT: 
							case Types.DECIMAL: { NamedPreparedStatement.setFloat( strPreparedStatementFieldName, (Float) FieldData ); bResult = true; break; }
							case Types.DOUBLE: { NamedPreparedStatement.setDouble( strPreparedStatementFieldName, (Double) FieldData ); bResult = true; break; }

							default:{
								
								if ( Logger != null ) {
									
									if ( Lang != null )
									    Logger.LogWarning( "-1", Lang.Translate( "Unknown SQL data type [%s]", Integer.toString( MemoryField.intSQLType ) ) );
									else
									    Logger.LogWarning( "-1", String.format( "Unknown SQL data type [%s]", Integer.toString( MemoryField.intSQLType ) ) );
									
									
								}
								
								break;
								
							}
							
						}

					}
					catch ( Exception Ex ) {

						if ( Logger != null ) {
							
							Logger.LogException( "-1015", Ex.getMessage(), Ex );
						
						}	

					}
				
				}
				else {

					try {

						NamedPreparedStatement.setNull( strPreparedStatementFieldName, MemoryField.intSQLType );
					
						bResult = true;
						
					}
					catch ( Exception Ex ) {
						
						if ( Logger != null ) {
							
							Logger.LogException( "-1015", Ex.getMessage(), Ex );
						
						}
						
					}
					
				}

			}
			
		}
		
		return bResult;
		
	}
    
	public boolean setFieldDataToCallableStatement( CMemoryRowSet MemoryRowSet, CNamedCallableStatement NamedCallableStatement, String strCallableStatementFieldName, String strFieldName, int intIndexRow, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		CMemoryFieldData MemoryField = MemoryRowSet.getFieldByName( strFieldName );

		if ( MemoryField != null ) {

			if ( MemoryField.Scope.equals( TFieldScope.IN ) || MemoryField.Scope.equals( TFieldScope.INOUT ) ) {
				
				if ( intIndexRow >= MemoryField.Data.size() && bUseLastRowIfRowNotExits ) {
					
					intIndexRow = MemoryField.Data.size() - 1;
					
				}
				
				if ( intIndexRow >= 0 && intIndexRow < MemoryField.Data.size() ) {
					
					Object FieldData = MemoryField.Data.get( intIndexRow );
					
					if ( FieldData != null ) { 
						
						try {
		
							switch ( this.getJavaSQLColumnType( MemoryField.intSQLType, Logger, Lang ) ) {
		
								case Types.INTEGER: { NamedCallableStatement.setInt( strCallableStatementFieldName, (Integer) FieldData ); bResult = true; break; }
								case Types.BIGINT: { NamedCallableStatement.setLong( strCallableStatementFieldName, (Long) FieldData ); bResult = true; break; }
								case Types.SMALLINT: { NamedCallableStatement.setShort( strCallableStatementFieldName, (Short) FieldData ); bResult = true; break; }
								case Types.VARCHAR: 
								case Types.CHAR: { NamedCallableStatement.setString( strCallableStatementFieldName, (String) FieldData ); bResult = true; break; }
								case Types.BOOLEAN: { NamedCallableStatement.setBoolean( strCallableStatementFieldName, ( Boolean ) FieldData ); bResult = true; break; }
								//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
								//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
								case Types.BLOB: { 	
									
					                                NamedCallableStatement.setBlob( strCallableStatementFieldName, (Blob) FieldData );
						                            
												    bResult = true;
												    
												    break; 
						
												 }
								case Types.DATE: {
									               
													NamedCallableStatement.setDate( strCallableStatementFieldName, (java.sql.Date) FieldData );
									               
													bResult = true;
													
													break; 
									             
								                 }
								case Types.TIME: {  
													
													NamedCallableStatement.setTime( strCallableStatementFieldName, (java.sql.Time) FieldData );
									               
													bResult = true;
													
													break; 
									               
								                 }
								case Types.TIMESTAMP: {  
									
														 NamedCallableStatement.setTimestamp( strCallableStatementFieldName, (java.sql.Timestamp) FieldData );
			
														 bResult = true;
														 
									                     break; 
									                    
								                      }
								case Types.FLOAT: 
								case Types.DECIMAL: { NamedCallableStatement.setFloat( strCallableStatementFieldName, (Float) FieldData ); bResult = true; break; }
								case Types.DOUBLE: { NamedCallableStatement.setDouble( strCallableStatementFieldName, (Double) FieldData ); bResult = true; break; }
		
								default:{
									
									if ( Logger != null ) {
										
										if ( Lang != null )
										    Logger.LogWarning( "-1", Lang.Translate( "Unknown SQL data type [%s]", Integer.toString( MemoryField.intSQLType ) ) );
										else
										    Logger.LogWarning( "-1", String.format( "Unknown SQL data type [%s]", Integer.toString( MemoryField.intSQLType ) ) );
										
										
									}
									
									break;
									
								}
								
							}
		
						}
						catch ( Exception Ex ) {
		
							if ( Logger != null ) {
								
								Logger.LogException( "-1015", Ex.getMessage(), Ex );
							
							}	
		
						}
					
					}
					else {
		
						try {
		
							NamedCallableStatement.setNull( strCallableStatementFieldName, MemoryField.intSQLType );
						
							bResult = true;
							
						}
						catch ( Exception Ex ) {
							
							if ( Logger != null ) {
								
								Logger.LogException( "-1016", Ex.getMessage(), Ex );
							
							}
							
						}
						
					}
		
				}
			
			}
			
			if ( MemoryField.Scope.equals( TFieldScope.OUT ) ) {
				
				try {
				
					NamedCallableStatement.registerOutParameter( strCallableStatementFieldName, MemoryField.intSQLType );
				
				}
				catch ( Exception Ex ) {
					
					if ( Logger != null ) {
						
						Logger.LogException( "-1017", Ex.getMessage(), Ex );
					
					}
					
				}
				
			}

		}

		return bResult;

	}
	
	public CachedRowSet BuildCachedRowSetFromFieldsScopeOut( CMemoryRowSet MemoryRowSet, CNamedCallableStatement NamedCallableStatement, CExtendedLogger Logger, CLanguage Lang ) {
		
		CachedRowSet Result = null;
		
		try {
			
			CMemoryRowSet TmpMemoryRowSet = new CMemoryRowSet( false );
			
			for ( CMemoryFieldData Field: MemoryRowSet.getFieldsData() ) {

				if ( Field.Scope.equals( TFieldScope.OUT ) || Field.Scope.equals( TFieldScope.INOUT ) ) {

					TmpMemoryRowSet.addField( Field, false );
					
				}
				
			}

			LinkedHashMap<String,Integer> NamedParams = NamedCallableStatement.getNamedParams();
			
			for ( CMemoryFieldData Field: TmpMemoryRowSet.getFieldsData() ) {

				String strFieldName =  Field.strName;
				
				if ( strFieldName == null || strFieldName.isEmpty() )
					strFieldName =  Field.strLabel;
				
				int intFieldIndex = NamedParams.get( strFieldName );
				
    			switch ( this.getJavaSQLColumnType( Field.intSQLType, Logger, Lang ) ) {
    			
					case Types.INTEGER: { Field.addData( NamedCallableStatement.getInt( intFieldIndex ) ); break; }
					case Types.BIGINT: { Field.addData( NamedCallableStatement.getLong( intFieldIndex ) ); break; }
					case Types.SMALLINT: { Field.addData( NamedCallableStatement.getShort( intFieldIndex ) ); break; }
					case Types.VARCHAR: 
					case Types.CHAR: { Field.addData( NamedCallableStatement.getString( intFieldIndex ) ); break; }
					case Types.BOOLEAN: { Field.addData( NamedCallableStatement.getBoolean( intFieldIndex ) ); break; }
					case Types.BLOB: { 
						
						                Field.addData( NamedCallableStatement.getBlob( intFieldIndex ) );
						                	
						                break; 
						             
					                  }
					case Types.DATE: { Field.addData( NamedCallableStatement.getDate( intFieldIndex ) ); break; }
					case Types.TIME: { Field.addData( NamedCallableStatement.getTime( intFieldIndex ) ); break; }
					case Types.TIMESTAMP: { Field.addData( NamedCallableStatement.getTimestamp( intFieldIndex ) ); break; }
					case Types.FLOAT: 
					case Types.DECIMAL: { Field.addData( NamedCallableStatement.getFloat( intFieldIndex ) ); break; }
					case Types.DOUBLE: { Field.addData( NamedCallableStatement.getDouble( intFieldIndex ) ); break; }

				}
				
			}
			
			Result = TmpMemoryRowSet.createCachedRowSet();
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null ) {
				
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			
			}	
			
		}
		
		return Result;
		
	}
	
    public ArrayList<CResultSetResult> ExecuteComplexQueySQL( Connection DBConnection, int intInternaFetchSize, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
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

						this.setFieldDataToPreparedStatement( MemoryRowSet, NamedPreparedStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );

					}

					if ( bLogParsedSQL == true ) {
						
						Logger.LogInfo( "2", Lang.Translate( "Executing the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
					}
					
		            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query
		            	
		        		if ( Lang != null )   
						   Logger.LogInfo( "0x2005", Lang.Translate( "Init complex SQL statement" ) );
		        		else
		 				   Logger.LogInfo( "0x2005", "Init complex SQL statement" );
		        			
		            }
					
					ResultSet QueryResult = NamedPreparedStatement.executeQuery();
				
					QueryResult.setFetchSize( intInternaFetchSize );
		    		
		    		if ( QueryResult.getFetchSize() != intInternaFetchSize ) {
		    			
						Logger.LogWarning( "-1", Lang.Translate(  "The JDBC Driver [%s] ignoring fetch size value [%s], using JDBC driver default value [%s]", DBConnection.getMetaData().getDriverName(), Integer.toString( intInternaFetchSize ), Integer.toString( QueryResult.getFetchSize() ) ) );
		    			
		    		}
					
		            if ( Logger != null ) { //Trace how much time in execute sql, useful for trace expensive query
		            	
		        		if ( Lang != null )   
						   Logger.LogInfo( "0x2006", Lang.Translate( "End complex SQL statement" ) );
		        		else
		 				   Logger.LogInfo( "0x2006", "End complex SQL statement" );
		        			
		            }
		            
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
    		
			Result.add( new CResultSetResult( -1, -1, Lang.Translate( "Error to execute the SQL statement, see the log file for more details" ) ) );

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

							this.setFieldDataToPreparedStatement( MemoryRowSet, NamedPreparedStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );

						}

						if ( bLogParsedSQL == true ) {

							Logger.LogInfo( "2", Lang.Translate( "Executing the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );

						}

			            if ( Logger != null ) {
			            	
			        		if ( Lang != null )   
							   Logger.LogInfo( "0x2007", Lang.Translate( "Init complex SQL statement" ) );
			        		else
			 				   Logger.LogInfo( "0x2007", "Init complex SQL statement" );
			        			
			            }
						
						int intAffectedRows = NamedPreparedStatement.executeUpdate();

			            if ( Logger != null ) {
			            	
			        		if ( Lang != null )   
							   Logger.LogInfo( "0x2008", Lang.Translate( "End complex SQL statement" ) );
			        		else
			 				   Logger.LogInfo( "0x2008", "End complex SQL statement" );
			        			
			            }
			            
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
   
    public ArrayList<CResultSetResult> ExecuteComplexCallableStatement( Connection DBConnection, int intInternaFetchSize, HttpServletRequest Request, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, boolean bLogParsedSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	ArrayList<CResultSetResult> Result = new ArrayList<CResultSetResult>();

    	try {
    	
    		strSQL = this.RemoveCallFunction( strSQL, Logger, Lang );
    		
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
					
					if ( MemoryField.strName.isEmpty() == false && ( MemoryField.Data.size() > 0 || MemoryField.Scope.equals( TFieldScope.OUT ) ) && MemoryField.intSQLType >= 0 ) {

						if ( MemoryField.Data.size() > intMaxCalls )
							intMaxCalls = MemoryField.Data.size();
						
						MemoryField.strName = NamedParam.getKey();
						MemoryField.strLabel = NamedParam.getKey();
						
						MemoryRowSet.addLinkedField( MemoryField );
						
					}
					
				}
				
			}
			
			boolean bContainsOutParams = MemoryRowSet.ContainsFieldsScopeOut();
			boolean bIsValidResultSet = true;
			
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

					this.setFieldDataToCallableStatement( MemoryRowSet, NamedCallableStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );
				
				}

				try {
				
					if ( bLogParsedSQL == true ) {
						
						Logger.LogInfo( "2", Lang.Translate( "Executing the next SQL statement [%s] index call [%s]", strParsedStatement, Integer.toString( intIndexCall ) )  );
						
					}
					
					NamedCallableStatement.execute();
					
					ResultSet CallableStatementResultSet = NamedCallableStatement.getResultSet();
					
					if ( bIsValidResultSet == false || this.IsValidResult( CallableStatementResultSet, Logger, Lang) == false ) {
						
						bIsValidResultSet = false;
						
						CallableStatementResultSet = null;
						
					}
					else {
						
						CallableStatementResultSet.setFetchSize( intInternaFetchSize );
			    		
			    		if ( CallableStatementResultSet.getFetchSize() != intInternaFetchSize ) {
			    			
							Logger.LogWarning( "-1", Lang.Translate(  "The JDBC Driver [%s] ignoring fetch size value [%s], using JDBC driver default value [%s]", DBConnection.getMetaData().getDriverName(), Integer.toString( intInternaFetchSize ), Integer.toString( CallableStatementResultSet.getFetchSize() ) ) );
			    			
			    		}
						
					}
					
					if ( CallableStatementResultSet == null && bContainsOutParams ) {
						
						CallableStatementResultSet = this.BuildCachedRowSetFromFieldsScopeOut( MemoryRowSet, NamedCallableStatement, Logger, Lang );
						
					}
					
					Result.add( new CResultSetResult( 0, 1, Lang.Translate( "Sucess to execute the SQL statement" ), NamedCallableStatement, CallableStatementResultSet ) );
					
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
    
    public int getJavaSQLColumnType( int intSQLType, CExtendedLogger Logger, CLanguage Lang ) {
    	
	    switch ( intSQLType ) {

			case -4: //Firebird Blob, ver 2.5.2, Jaybird Driver 2.2, MySQL Blob, ver 5.5, driver from oracle
			case Types.BLOB: { 	
				
				return Types.BLOB;
				
			}   
	    
			default: {
				
				return intSQLType;
				
			}
			
	    }
	    
    }
    
    public String getJavaSQLColumnTypeName( int intSQLType, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	return NamesSQLTypes.getJavaSQLTypeName( intSQLType ); //Do nothing
    	
    }
    
    public String getFieldValueAsString( int intFieldType, int intColumnIndex, ResultSet Resultset, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
	    String strResult = "";

	    try {
	   
		    switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
			
				case Types.INTEGER: { strResult = Integer.toString( Resultset.getInt( intColumnIndex ) ); break; }
				case Types.BIGINT: { strResult = Long.toString( Resultset.getLong( intColumnIndex ) ); break; }
				case Types.SMALLINT: { strResult = Short.toString( Resultset.getShort( intColumnIndex ) ); break; }
				case Types.VARCHAR: 
				case Types.CHAR: {  
					
					                strResult = Resultset.getString( intColumnIndex );
									break; 
					             
				                 }
				case Types.BOOLEAN: { strResult = Resultset.getBoolean( intColumnIndex )?"true":"false"; break; }
				//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				case Types.BLOB: { 	
					
					                Blob BinaryBLOBData = Resultset.getBlob( intColumnIndex );
		
					                String strBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ) );
		
					                strResult = strBase64Coded;//Formated in base64
					                
								    break; 
		
								 }
				case Types.DATE: {
					               
					                SimpleDateFormat DFormatter = new SimpleDateFormat( strDateFormat );
					                strResult = DFormatter.format( Resultset.getDate( intColumnIndex ) );
									break; 
					             
				                 }
				case Types.TIME: {  
					
		                            SimpleDateFormat TFormatter = new SimpleDateFormat( strTimeFormat );
					                strResult = TFormatter.format( Resultset.getTime( intColumnIndex ) );
									break; 
					               
				                 }
				case Types.TIMESTAMP: {  
					
		                                 SimpleDateFormat DTFormatter = new SimpleDateFormat( strDateTimeFormat );
					                     strResult = DTFormatter.format( Resultset.getTimestamp( intColumnIndex ) );
					                     break; 
					                    
				                      }
				case Types.FLOAT: 
				case Types.DECIMAL: {  strResult = Float.toString( Resultset.getFloat( intColumnIndex ) ); break; }
				case Types.DOUBLE: {  break; }
	
		    }
	   
		    if ( Resultset.wasNull() ) {
		    	
		    	strResult = "null";
		    	
		    }
		    
	    }
	    catch ( Exception Ex ) {
		   
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	    }
	   
	   return strResult;
    	
    }
    
    public String getFieldValueAsString( int intFieldType, String strColumnName, ResultSet Resultset, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
	    String strResult = "";

	    try {
	   
		    switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
			
				case Types.INTEGER: { strResult = Integer.toString( Resultset.getInt( strColumnName ) ); break; }
				case Types.BIGINT: { strResult = Long.toString( Resultset.getLong( strColumnName ) ); break; }
				case Types.SMALLINT: { strResult = Short.toString( Resultset.getShort( strColumnName ) ); break; }
				case Types.VARCHAR: 
				case Types.CHAR: {  
					
					                strResult = Resultset.getString( strColumnName );
									break; 
					             
				                 }
				case Types.BOOLEAN: { strResult = Boolean.toString( Resultset.getBoolean( strColumnName ) ); break; }
				//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				case Types.BLOB: { 	
					
					                Blob BinaryBLOBData = Resultset.getBlob( strColumnName );
		
					                String strBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ) );
		
					                strResult = strBase64Coded;//Formated in base64
					                
								    break; 
		
								 }
				case Types.DATE: {
					               
					                SimpleDateFormat DFormatter = new SimpleDateFormat( strDateFormat );
					                strResult = DFormatter.format( Resultset.getDate( strColumnName ) );
									break; 
					             
				                 }
				case Types.TIME: {  
					
		                            SimpleDateFormat TFormatter = new SimpleDateFormat( strTimeFormat );
					                strResult = TFormatter.format( Resultset.getTime( strColumnName ) );
									break; 
					               
				                 }
				case Types.TIMESTAMP: {  
					
		                                 SimpleDateFormat DTFormatter = new SimpleDateFormat( strDateTimeFormat );
					                     strResult = DTFormatter.format( Resultset.getTimestamp( strColumnName ) );
					                     break; 
					                    
				                      }
				case Types.FLOAT:
				case Types.NUMERIC:
				case Types.DECIMAL: {  strResult = Float.toString( Resultset.getFloat( strColumnName ) ); break; }
				case Types.DOUBLE: {  break; }
	
		    }
		    
		    if ( Resultset.wasNull() ) {
		    	
		    	strResult = "null";
		    	
		    }
	   
	    }
	    catch ( Exception Ex ) {
		   
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	    }
	   
	   return strResult;
    	
    }

    public Object getFieldValueAsObject( int intFieldType, String strColumnName, ResultSet Resultset, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	Object Result = null;
    	
    	try {
    	
			switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
			
				case Types.INTEGER: { Result = Resultset.getInt( strColumnName ); break; }
				case Types.BIGINT: { Result = Resultset.getLong( strColumnName ); break; }
				case Types.SMALLINT: { Result = Resultset.getShort( strColumnName ); break; }
				case Types.VARCHAR: 
				case Types.CHAR: { Result = Resultset.getString( strColumnName ); break; }
				case Types.BOOLEAN: { Result = Resultset.getBoolean( strColumnName ); break; }
				//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				case Types.BLOB: { 
					
					                Result = Resultset.getBlob( strColumnName );
	                                 
					                break; 
					                
					              }
				case Types.DATE: { Result = Resultset.getDate( strColumnName ); break; }
				case Types.TIME: { Result = Resultset.getTime( strColumnName ); break; }
				case Types.TIMESTAMP: { Result = Resultset.getTimestamp( strColumnName ); break; }
				case Types.NUMERIC:
				case Types.FLOAT: 
				case Types.DECIMAL: { Result = Resultset.getFloat( strColumnName ); break; }
				case Types.DOUBLE: { Result = Resultset.getDouble( strColumnName ); break; }
	
			}
    	
    		if ( Resultset.wasNull() ) {
    			
    			Result = null;
    			
    		}
			
	    }
	    catch ( Exception Ex ) {
		   
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	    }
    	
    	return Result;
    	
    }

    public Object getFieldValueAsObject( int intFieldType, int intColumnIndex, ResultSet Resultset, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	Object Result = null;
    	
    	try {
    	
			switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
			
				case Types.INTEGER: { Result = Resultset.getInt( intColumnIndex ); break; }
				case Types.BIGINT: { Result = Resultset.getLong( intColumnIndex ); break; }
				case Types.SMALLINT: { Result = Resultset.getShort( intColumnIndex ); break; }
				case Types.VARCHAR: 
				case Types.CHAR: { Result = Resultset.getString( intColumnIndex ); break; }
				case Types.BOOLEAN: { Result = Resultset.getBoolean( intColumnIndex ); break; }
				//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				case Types.BLOB: { 
					
					                 Result = Resultset.getBlob( intColumnIndex );
					                
					                 break; 
					                
					              }
				case Types.DATE: { Result = Resultset.getDate( intColumnIndex ); break; }
				case Types.TIME: { Result = Resultset.getTime( intColumnIndex ); break; }
				case Types.TIMESTAMP: { Result = Resultset.getTimestamp( intColumnIndex ); break; }
				case Types.NUMERIC:
				case Types.FLOAT: 
				case Types.DECIMAL: { Result = Resultset.getFloat( intColumnIndex ); break; }
				case Types.DOUBLE: { Result = Resultset.getDouble( intColumnIndex ); break; }
	
			}
    	
    		if ( Resultset.wasNull() ) {
    			
    			Result = null;
    			
    		}
			
	    }
	    catch ( Exception Ex ) {
		   
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	    }
    	
    	return Result;
    	
    }
    
    public boolean writeBlobValueAsStringToFile( int intFieldType, int intColumnIndex, String strTempDir, String strTempFile, OutputStream BlobFileOutStream, ResultSet Resultset, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;
    	
	    try {
	 	   
		    switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
			
				//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				case Types.BLOB: { 	
					
				                    InputStream BlobInStream = Resultset.getBinaryStream( intColumnIndex );
					                
					                Base64.encode( BlobInStream, BlobFileOutStream );
				                    
				                    bResult = true;
					                
								    break; 
		
								 }
	
		    }
	   
	    }
	    catch ( Exception Ex ) {
		   
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	    }
    	
    	return bResult;
    	
    }
    
    public boolean writeBlobValueAsStringToFile( int intFieldType, String strColumnName, String strTempDir, String strTempFile, OutputStream BlobFileOutStream, ResultSet Resultset, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;
    	
	    try {
	 	   
		    switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
			
				//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				case Types.BLOB: { 	
					
				                    InputStream BlobInStream = Resultset.getBinaryStream( strColumnName );
					                
					                Base64.encode( BlobInStream, BlobFileOutStream );
				                    
				                    bResult = true;
					                
								    break; 
		
								 }
	
		    }
	   
	    }
	    catch ( Exception Ex ) {
		   
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	    }
    	
    	return bResult;
    	
    }
    
    public boolean databaseObjectExists( Connection DBConnection, int intObjectType, String strObjectName, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;
    	
    	try {
		
    		DatabaseMetaData DBMetadata = DBConnection.getMetaData();
    		
    		if ( intObjectType == 1 ) { //Table

    			try {

    				String[] ObjectType = { "TABLE", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM" };

    				ResultSet RS = DBMetadata.getTables(  null, null, strObjectName, ObjectType );

    				bResult = RS != null && RS.next();

    			} 
    			catch ( Exception Ex ) {

    				if ( Logger != null )
    					Logger.LogException( "-1010", Ex.getMessage(), Ex );

    			}
        		
    		}
    		else if ( intObjectType == 2 ) { //View
    			
    			try {
    			
    				String[] ObjectType = { "VIEW" };

    				ResultSet RS = DBMetadata.getTables(  null, null, strObjectName, ObjectType );

    				bResult = RS != null && RS.next();

    			} 
    			catch ( Exception Ex ) {

    				if ( Logger != null )
    					Logger.LogException( "-1011", Ex.getMessage(), Ex );

    			}
    			
    		}
    		else if ( intObjectType == 3 ) { //Function

    			try {

    				ResultSet RS = DBMetadata.getFunctions( null, null, strObjectName ); 

    				bResult = RS != null && RS.next();

    			} 
    			catch ( Exception Ex ) {

    				if ( Logger != null )
    					Logger.LogException( "-1010", Ex.getMessage(), Ex );

    			}
    			
    		}
    		else if ( intObjectType == 4 ) { //Procedure
    			
    			try {

    				ResultSet RS = DBMetadata.getProcedures( null, null, strObjectName ); 

    				bResult = RS != null && RS.next();
    		
    			} 
    			catch ( Exception Ex ) {

    				if ( Logger != null )
    					Logger.LogException( "-1010", Ex.getMessage(), Ex );

    			}
        		
    		}

    	} 
    	catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
    	
    	}
    	
    	return bResult;
    	
    }
    
    public CResultSetResult describeDatabaseObject( Connection DBConnection, int intObjectType, String strObjectName, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	CResultSetResult Result =  new CResultSetResult();
    	
    	Result.intCode = -1;
    	Result.strDescription = "";
    	
    	try {
		
    		DatabaseMetaData DBMetadata = DBConnection.getMetaData();
    		
    		if ( intObjectType == 1 || intObjectType == 2 ) { //Table Or View
    		
    			try {
    			
    				Result.Result = DBMetadata.getColumns( null, null, strObjectName, "%" ); 

    			} 
    			catch ( Exception Ex ) {

    				if ( Logger != null )
    					Logger.LogException( "-1010", Ex.getMessage(), Ex );

    			}
    		
    		}
    		else if ( intObjectType == 3 ) { //Function

    			try {
    			
    				Result.Result = DBMetadata.getFunctionColumns( null, null, strObjectName, "%" ); 
    			
    			} 
    			catch ( Exception Ex ) {

    				if ( Logger != null )
    					Logger.LogException( "-1010", Ex.getMessage(), Ex );

    			}
    			
    		}
    		else if ( intObjectType == 4 ) { //Procedure
    			
    			try {
    				
    				Result.Result = DBMetadata.getProcedureColumns( null, null, strObjectName, "%" ); 
    			
    			} 
    			catch ( Exception Ex ) {

    				if ( Logger != null )
    					Logger.LogException( "-1010", Ex.getMessage(), Ex );

    			}
    			
    		}

    		if ( Result.Result != null )
    		    Result.intCode = 1;
    		
    	} 
    	catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
    	
    	}
    	
    	return Result;
    	
    }
    
    public CResultSetResult getDatabaseInfo( Connection DBConnection, String strConfiguredDriverNameClass, CExtendedLogger Logger, CLanguage Lang ) {

    	CResultSetResult Result =  new CResultSetResult();
    	
    	Result.intCode = -1;
    	Result.strDescription = "";
    	
    	try {
    		
    		DatabaseMetaData DBMetadata = DBConnection.getMetaData();
    		
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
    		MemoryRowSet.addRow( "generatedKeyAlwaysReturned", Boolean.toString( DBMetadata.generatedKeyAlwaysReturned() ) );
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
    		MemoryRowSet.addRow( "CatalogSeparator", DBMetadata.getCatalogSeparator() );
    		MemoryRowSet.addRow( "CatalogTerm", DBMetadata.getCatalogTerm() );
    		MemoryRowSet.addRow( "databaseMajorVersion", Integer.toString( DBMetadata.getDatabaseMajorVersion() ) );
    		MemoryRowSet.addRow( "databaseMinorVersion", Integer.toString( DBMetadata.getDatabaseMinorVersion() ) );
    		MemoryRowSet.addRow( "databaseProductName", DBMetadata.getDatabaseProductName() );
    		MemoryRowSet.addRow( "databaseProductVersion", DBMetadata.getDatabaseProductVersion() );
    		MemoryRowSet.addRow( "defaultTransactionIsolation", Integer.toString( DBMetadata.getDefaultTransactionIsolation() ) );
    		MemoryRowSet.addRow( "driverMajorVersion", Integer.toString( DBMetadata.getDriverMajorVersion() ) );
    		MemoryRowSet.addRow( "driverMinorVersion", Integer.toString( DBMetadata.getDriverMinorVersion() ) );
    		MemoryRowSet.addRow( "driverName", DBMetadata.getDriverName() );
    		MemoryRowSet.addRow( "driverVersion", DBMetadata.getDriverVersion() );
    		MemoryRowSet.addRow( "configuredDriverNameClass", strConfiguredDriverNameClass );
    		MemoryRowSet.addRow( "extraNameCharacters", DBMetadata.getExtraNameCharacters() );
    		MemoryRowSet.addRow( "identifierQuoteString", net.maindataservices.Utilities.ReplaceToHTMLEntity( DBMetadata.getIdentifierQuoteString() ) );
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
    				Logger.LogException( "-1010", Ex.getMessage(), Ex );

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
    				Logger.LogException( "-1010", Ex.getMessage(), Ex );

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
    				Logger.LogException( "-1010", Ex.getMessage(), Ex );

    		}
    		
        	MemoryRowSet.addRow( "countProcedures", Integer.toString( intCountProcedures ) );

        	Result.Result = MemoryRowSet.createCachedRowSet();

    		Result.intCode = 1;
    		
    	} 
    	catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
    	
    	}
    	
    	return Result;
    	
    }
    
    public CResultSetResult listDatabaseObjects( Connection DBConnection, int intObjectType, CExtendedLogger Logger, CLanguage Lang ) {

    	CResultSetResult Result =  new CResultSetResult();
    	
    	Result.intCode = -1;
    	Result.strDescription = "";
    	
    	try {
    		
    		DatabaseMetaData DBMetadata = DBConnection.getMetaData();
    		
        	CMemoryRowSet MemoryRowSet =  new CMemoryRowSet( false );
        	
        	MemoryRowSet.addField( "Name", Types.VARCHAR, NamesSQLTypes._VARCHAR, 256, "Name" );
        	MemoryRowSet.addField( "Type", Types.VARCHAR, NamesSQLTypes._VARCHAR, 128, "Type" );
        	MemoryRowSet.addField( "Remarks", Types.VARCHAR, NamesSQLTypes._VARCHAR, 2048, "Remarks" );


        	if ( intObjectType == 0 || intObjectType == 1 ) { //All or Tables
        		
        		try {
        		
        			String[] ObjectType = { "TABLE", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM" };

        			ResultSet RS = DBMetadata.getTables(  null, null, "%", ObjectType );

        			while ( RS != null && RS.next() ) {

        				MemoryRowSet.addRow( RS.getString(3), RS.getString(4), RS.getString(5) );

        			}
        		
        		}
        		catch ( Exception Ex ) {
        			
        			if ( Logger != null )
        				Logger.LogException( "-1010", Ex.getMessage(), Ex );
        			
        		}
        		
        	}
        	
        	if ( intObjectType == 0 || intObjectType == 2 ) { //All or Views
        		
        		try {
            		
        			String[] ObjectType = { "VIEW" };

        			ResultSet RS = DBMetadata.getTables(  null, null, "%", ObjectType );

        			while ( RS != null && RS.next() ) {

        				MemoryRowSet.addRow( RS.getString(3), RS.getString(4), RS.getString(5) );

        			}

        		}
        		catch ( Exception Ex ) {
        			
        			if ( Logger != null )
        				Logger.LogException( "-1011", Ex.getMessage(), Ex );
        			
        		}
        		
        	}
        	
        	if ( intObjectType == 0 || intObjectType == 3 ) { //All or Functions
        		
        		try {
        		
        			ResultSet RS = DBMetadata.getFunctions( null, null, "%" );

        			while ( RS.next() ) {

        				MemoryRowSet.addRow( RS.getString(3), "Function", RS.getString(4) );

        			}
        		
        		}
        		catch ( Exception Ex ) {
        			
        			if ( Logger != null )
        				Logger.LogException( "-1012", Ex.getMessage(), Ex );
        			
        		}
        		
        	}

        	if ( intObjectType == 0 || intObjectType == 4 ) { //All or Stored Procedure
        		
        		try {

        			ResultSet RS = DBMetadata.getProcedures( null, null, "%" );

        			while ( RS != null && RS.next() ) {

        				MemoryRowSet.addRow( RS.getString(3), "Stored Procedure", RS.getString(4) );

        			}

        		}
        		catch ( Exception Ex ) {
        			
        			if ( Logger != null )
        				Logger.LogException( "-1013", Ex.getMessage(), Ex );
        			
        		}
        		
        	}

        	Result.Result = MemoryRowSet.createCachedRowSet();
    		Result.intCode = 1;
    		
    	} 
    	catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1014", Ex.getMessage(), Ex );
    	
    	}
    	
    	return Result;
        	
    }
    
}
