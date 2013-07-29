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

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
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
				
				if ( strSQL.indexOf( ConstantsAbstractDBEngine._CALL ) == 0 || strSQL.indexOf( ConstantsAbstractDBEngine._CALL1 ) == 0 ) { 

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
    
    public CResultSetResult ExecutePlainCallableStatement( Connection DBConnection, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	CResultSetResult Result = new CResultSetResult( -1, -1, "" ); 
    	
    	try {
    	
    		CallableStatement CallStatement = DBConnection.prepareCall( strSQL );

			/*CallStatement.registerOutParameter( 1, Types.INTEGER );
			CallStatement.registerOutParameter( 2, Types.INTEGER );
			CallStatement.registerOutParameter( 3, Types.INTEGER );
			CallStatement.registerOutParameter( 4, Types.DATE );
			CallStatement.registerOutParameter( 5, Types.TIME );*/

    		CallStatement.execute();
    		
    		Result.intCode = 1; //CallStatement.getInt( "IdValid" );
    		Result.Result = CallStatement.getResultSet();
    		if ( this.IsValidResult( Result.Result, Logger, Lang ) == false ) 
    			Result.Result = null;
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

			for ( CMemoryFieldData Field: TmpMemoryRowSet.getFieldsData() ) {

				String strFieldName =  Field.strName;
				
				if ( strFieldName == null || strFieldName.isEmpty() )
					strFieldName =  Field.strLabel;
				
    			switch ( this.getJavaSQLColumnType( Field.intSQLType, Logger, Lang ) ) {
    			
					case Types.INTEGER: { Field.addData( NamedCallableStatement.getInt( strFieldName ) ); break; }
					case Types.BIGINT: { Field.addData( NamedCallableStatement.getLong( strFieldName ) ); break; }
					case Types.SMALLINT: { Field.addData( NamedCallableStatement.getShort( strFieldName ) ); break; }
					case Types.VARCHAR: 
					case Types.CHAR: { Field.addData( NamedCallableStatement.getString( strFieldName ) ); break; }
					case Types.BOOLEAN: { Field.addData( NamedCallableStatement.getBoolean( strFieldName ) ); break; }
					case Types.BLOB: { 
						
						                /*if ( intBlobType == 1 ) {  //Reimplement in PostgreSQL
						                
											java.sql.Blob BlobData = new SerialBlob( NamedCallableStatement.getBytes( strFieldName ) );
						                	
						                    Field.addData( BlobData );
						                	
						                }
						                else*/ 
						                Field.addData( NamedCallableStatement.getBlob( strFieldName ) );
						                	
						                break; 
						             
					                  }
					case Types.DATE: { Field.addData( NamedCallableStatement.getDate( strFieldName ) ); break; }
					case Types.TIME: { Field.addData( NamedCallableStatement.getTime( strFieldName ) ); break; }
					case Types.TIMESTAMP: { Field.addData( NamedCallableStatement.getTimestamp( strFieldName ) ); break; }
					case Types.FLOAT: 
					case Types.DECIMAL: { Field.addData( NamedCallableStatement.getFloat( strFieldName ) ); break; }
					case Types.DOUBLE: { Field.addData( NamedCallableStatement.getDouble( strFieldName ) ); break; }

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

						this.setFieldDataToPreparedStatement( MemoryRowSet, NamedPreparedStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );

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

							this.setFieldDataToPreparedStatement( MemoryRowSet, NamedPreparedStatement, NamedParam.getKey(), NamedParam.getKey(), intIndexCall, true, Logger, Lang );

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
					               
					                SimpleDateFormat DFormatter = new SimpleDateFormat("yyyyMMdd");
					                strResult = DFormatter.format( Resultset.getDate( intColumnIndex ) );
									break; 
					             
				                 }
				case Types.TIME: {  
					
		                            SimpleDateFormat TFormatter = new SimpleDateFormat("HHmmss");
					                strResult = TFormatter.format( Resultset.getTime( intColumnIndex ) );
									break; 
					               
				                 }
				case Types.TIMESTAMP: {  
					
		                                 SimpleDateFormat DTFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");
					                     strResult = DTFormatter.format( Resultset.getTimestamp( intColumnIndex ) );
					                     break; 
					                    
				                      }
				case Types.FLOAT: 
				case Types.DECIMAL: {  strResult = Float.toString( Resultset.getFloat( intColumnIndex ) ); break; }
				case Types.DOUBLE: {  break; }
	
		    }
	   
	    }
	    catch ( Exception Ex ) {
		   
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	    }
	   
	   return strResult;
    	
    }
    
    public String getFieldValueAsString( int intFieldType, String strFieldName, ResultSet Resultset, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
	    String strResult = "";

	    try {
	   
		    switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
			
				case Types.INTEGER: { strResult = Integer.toString( Resultset.getInt( strFieldName ) ); break; }
				case Types.BIGINT: { strResult = Long.toString( Resultset.getLong( strFieldName ) ); break; }
				case Types.SMALLINT: { strResult = Short.toString( Resultset.getShort( strFieldName ) ); break; }
				case Types.VARCHAR: 
				case Types.CHAR: {  
					
					                strResult = Resultset.getString( strFieldName );
									break; 
					             
				                 }
				case Types.BOOLEAN: {  break; }
				//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				case Types.BLOB: { 	
					
					                Blob BinaryBLOBData = Resultset.getBlob( strFieldName );
		
					                String strBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ) );
		
					                strResult = strBase64Coded;//Formated in base64
					                
								    break; 
		
								 }
				case Types.DATE: {
					               
					                SimpleDateFormat DFormatter = new SimpleDateFormat("yyyyMMdd");
					                strResult = DFormatter.format( Resultset.getDate( strFieldName ) );
									break; 
					             
				                 }
				case Types.TIME: {  
					
		                            SimpleDateFormat TFormatter = new SimpleDateFormat("HHmmss");
					                strResult = TFormatter.format( Resultset.getTime( strFieldName ) );
									break; 
					               
				                 }
				case Types.TIMESTAMP: {  
					
		                                 SimpleDateFormat DTFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");
					                     strResult = DTFormatter.format( Resultset.getTimestamp( strFieldName ) );
					                     break; 
					                    
				                      }
				case Types.FLOAT: 
				case Types.DECIMAL: {  strResult = Float.toString( Resultset.getFloat( strFieldName ) ); break; }
				case Types.DOUBLE: {  break; }
	
		    }
	   
	    }
	    catch ( Exception Ex ) {
		   
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	    }
	   
	   return strResult;
    	
    }

    public Object getFieldValueAsObject( int intFieldType, String strFieldName, ResultSet Resultset, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	Object Result = null;
    	
    	try {
    	
			switch ( this.getJavaSQLColumnType( intFieldType, Logger, Lang ) ) {
			
				case Types.INTEGER: { Result = Resultset.getInt( strFieldName ); break; }
				case Types.BIGINT: { Result = Resultset.getLong( strFieldName ); break; }
				case Types.SMALLINT: { Result = Resultset.getShort( strFieldName ); break; }
				case Types.VARCHAR: 
				case Types.CHAR: { Result = Resultset.getString( strFieldName ); break; }
				case Types.BOOLEAN: { Result = Resultset.getBoolean( strFieldName ); break; }
				//case -2: //PostgreSQL ByteA  now managed for CPGSQLDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				//case -4: //Firebird Blob now managed for CFirebirdDBEngine Class method getJavaSQLColumnType to convert to Types.BLOB constant value
				case Types.BLOB: { 
					
					                Result = Resultset.getBlob( strFieldName );
	                                 
					                break; 
					                
					              }
				case Types.DATE: { Result = Resultset.getDate( strFieldName ); break; }
				case Types.TIME: { Result = Resultset.getTime( strFieldName ); break; }
				case Types.TIMESTAMP: { Result = Resultset.getTimestamp( strFieldName ); break; }
				case Types.FLOAT: 
				case Types.DECIMAL: { Result = Resultset.getFloat( strFieldName ); break; }
				case Types.DOUBLE: { Result = Resultset.getDouble( strFieldName ); break; }
	
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
				case Types.FLOAT: 
				case Types.DECIMAL: { Result = Resultset.getFloat( intColumnIndex ); break; }
				case Types.DOUBLE: { Result = Resultset.getDouble( intColumnIndex ); break; }
	
			}
    	
	    }
	    catch ( Exception Ex ) {
		   
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	    }
    	
    	return Result;
    	
    }
    
}
