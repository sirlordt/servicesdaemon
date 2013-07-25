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

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.serial.SerialBlob;

import net.maindataservices.Base64;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractDBEngine.CDBEngineConfigConnection;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CNamedCallableStatement;
import CommonClasses.CNamedPreparedStatement;
import CommonClasses.NamesSQLTypes;
import CommonClasses.CMemoryFieldData.TFieldScope;
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
					case Types.BLOB: { 	
						
										java.sql.Blob BlobData = new SerialBlob( Base64.decode( strMacrosValues[ intMacroIndex ].getBytes() ) );
									
										//Reimplement for PostgreSQL
		                                //PostgreSQL Case ByteA type
		                                NamedPreparedStatement.setBinaryStream( strFieldName, BlobData.getBinaryStream(), BlobData.getBinaryStream().available() );  
			                            
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
					case Types.BLOB: { 	
						
										java.sql.Blob BlobData = new SerialBlob( Base64.decode( strFieldValue.getBytes() ) );
									
										//Reimplement for PostgreSQL
		                                //PostgreSQL Case ByteA type
		                                NamedPreparedStatement.setBinaryStream( strFieldName, BlobData.getBinaryStream(), BlobData.getBinaryStream().available() );  
			                            
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
					case Types.BLOB: { 	
						
										java.sql.Blob BlobData = new SerialBlob( Base64.decode( strMacrosValues[ intMacroIndex ].getBytes() ) );
									
										//Reimplement for PostgreSQL
		                                //PostgreSQL Case ByteA type
		                                NamedCallableStatement.setBinaryStream( strFieldName, BlobData.getBinaryStream(), BlobData.getBinaryStream().available() );  
			                            
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
						case Types.BLOB: { 	
							
											java.sql.Blob BlobData = new SerialBlob( Base64.decode( strFieldValue.getBytes() ) );
										
											//Reimplement for PostgreSQL
			                                //PostgreSQL Case ByteA type
			                                NamedCallableStatement.setBinaryStream( strFieldName, BlobData.getBinaryStream(), BlobData.getBinaryStream().available() );  
				                            
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
	
	@Override
	public ResultSet ExecuteDummySQL( Connection DBConnection, String strOptionalDummyQuery, CExtendedLogger Logger, CLanguage Lang ) {
		
		ResultSet Result = null;
		
		try {
		
			if ( strOptionalDummyQuery.isEmpty() == true ) {
				
				strOptionalDummyQuery = "SELECT * FROM information_schema.tables limit 1";
				
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
							case Types.BLOB: { 	
								
								                //Reimplement for PostgreSQL
                                                //PostgreSQL Case ByteA type
								                NamedPreparedStatement.setBinaryStream( strPreparedStatementFieldName, ((Blob) FieldData).getBinaryStream(), ((Blob) FieldData).getBinaryStream().available() );  
					                            
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
    
	@Override
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
								case Types.BLOB: { 	
									
					                                //Reimplement for PostgreSQL
                                                    //PostgreSQL Case ByteA type
					                	            NamedCallableStatement.setBinaryStream( strCallableStatementFieldName, ((Blob) FieldData).getBinaryStream(), ((Blob) FieldData).getBinaryStream().available() );  
						                            
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

	@Override
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
						
                                        //Reimplement for PostgreSQL
                                        //PostgreSQL Case ByteA type
										java.sql.Blob BlobData = new SerialBlob( NamedCallableStatement.getBytes( strFieldName ) );
						                	
						                Field.addData( BlobData );
						                	
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
	
	@Override
    public int getJavaSQLColumnType( int intSQLType, CExtendedLogger Logger, CLanguage Lang ) {
    	
	    switch ( intSQLType ) {

		    case -2: //PostgreSQL ByteA, ver 9.1 
			case Types.BLOB: { 	
				
				return Types.BLOB;
				
			}   
	    
			default: {
				
				return super.getJavaSQLColumnType( intSQLType, Logger, Lang );
				
			}
			
	    }
	    
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
				case Types.BLOB: { 	
					
					                //Reimplement for PostgreSQL
					                //PostgreSQL Case ByteA type
				                    strResult = new String( Base64.encode( Resultset.getBytes( intColumnIndex ) ) );
				        				
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
    
    @Override
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
				case Types.BOOLEAN: { strResult = Resultset.getBoolean( strFieldName )?"true":"false"; break; }
				case Types.BLOB: { 	
					
					                strResult = new String( Base64.encode( Resultset.getBytes( strFieldName ) ) );
					                
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
				case Types.BLOB: { 
					
						             Result = new SerialBlob( Resultset.getBytes( strFieldName ) );
	                                 
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
				case Types.BLOB: { 
					
					                 Result = new SerialBlob( Resultset.getBytes( intColumnIndex ) );
	                	
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
