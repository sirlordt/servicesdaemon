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
package FirebirdDBEngine;

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

//import com.sun.org.apache.xml.internal.security.utils.Base64;

import Utilities.Utilities;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractService.CInputServiceParameter;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CNamedCallableStatement;
import CommonClasses.CResultSetResult;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import CommonClasses.CNamedPreparedStatement;
import DBServicesManager.CConfigDBConnection;
import ExtendedLogger.CExtendedLogger;

public class CFirebirdDBEngine extends CAbstractDBEngine {

	public CFirebirdDBEngine() {
		
		strName = "firebird";
		strVersion = "2.5";
		
	}
	
	@Override
	public synchronized Connection getDBConnection( CConfigDBConnection ConfigDBConnection, CExtendedLogger Logger, CLanguage Lang ) {

		Connection DBConnection = null;
		
		try {
			
            String strDatabaseURL = "jdbc:firebirdsql:" + ConfigDBConnection.strIP + "/" + ConfigDBConnection.intPort + ":" + ConfigDBConnection.strDatabase;

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
	
	/*public String ReplaceSQLMacrosNamesToValues( int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL ) {

		String strResult = strSQL;
		
		if ( intMacrosTypes.length == strMacrosNames.length && strMacrosNames.length == strMacrosValues.length ) {
			
			for ( int intMacroNameIndex = 0; intMacroNameIndex < strMacrosNames.length; intMacroNameIndex++ ) {

				if ( intMacrosTypes[ intMacroNameIndex ] == Types.VARCHAR ||  intMacrosTypes[ intMacroNameIndex ] == Types.CHAR ) {
					
					strResult = strResult.replace( strMacrosNames[ intMacroNameIndex ], "'" + strMacrosValues[ intMacroNameIndex ] + "'" );
					
				}
				else {
					
					strResult = strResult.replace( strMacrosNames[ intMacroNameIndex ], strMacrosValues[ intMacroNameIndex ] );
					
				}
				
			}
			
		}
		
		return strResult;
		
	}*/
	
	@Override
    public CMemoryRowSet ExecuteCheckMethodSQL( Connection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, CExtendedLogger Logger, CLanguage Lang ) {

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

					/*if ( strInputServiceParameterValue.toLowerCase().equals( NamesSQLTypes._NULL ) == false ) {
						
		    			switch ( InputServiceParameterDef.getParameterDataTypeID() ) {
		    			
							case Types.INTEGER: { NamedPreparedStatement.setInt( NamedParam.getKey(), Integer.parseInt( strInputServiceParameterValue ) ); break; }
							case Types.BIGINT: { NamedPreparedStatement.setLong( NamedParam.getKey(), Long.parseLong( strInputServiceParameterValue ) ); break; }
							case Types.SMALLINT: { NamedPreparedStatement.setShort( NamedParam.getKey(), Short.parseShort( strInputServiceParameterValue ) ); break; }
							case Types.VARCHAR: 
							case Types.CHAR: { NamedPreparedStatement.setString( NamedParam.getKey(), strInputServiceParameterValue ); break; }
							case Types.BOOLEAN: { NamedPreparedStatement.setBoolean( NamedParam.getKey(), Boolean.parseBoolean( strInputServiceParameterValue ) ); break; }
							case Types.BLOB: { 	
								
												java.sql.Blob BlobData = new SerialBlob( Base64.decode( strInputServiceParameterValue.getBytes() ) );
											
											    NamedPreparedStatement.setBlob( NamedParam.getKey(), BlobData );
					                            
											    break; 
					
											 }
							case Types.DATE: {
								               
											    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
												
												java.util.Date Date = DateFormat.parse( strInputServiceParameterValue );
												
												java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
												
												NamedPreparedStatement.setDate( NamedParam.getKey(), SQLDate );
								               
												break; 
								             
							                 }
							case Types.TIME: {  
								
												SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
												
												java.util.Date Date = TimeFormat.parse( strInputServiceParameterValue );
												
												java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
												
												NamedPreparedStatement.setTime( NamedParam.getKey(), SQLTime );
								               
												break; 
								               
							                 }
							case Types.TIMESTAMP: {  
								
													 SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
													
													 java.util.Date Date = DateTimeFormat.parse( strInputServiceParameterValue );
													
													 java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
													
													 NamedPreparedStatement.setTimestamp( NamedParam.getKey(), SQLTimeStamp );
	
								                     break; 
								                    
							                      }
							case Types.FLOAT: 
							case Types.DECIMAL: { NamedPreparedStatement.setFloat( NamedParam.getKey(), Float.parseFloat( strInputServiceParameterValue ) ); break; }
							case Types.DOUBLE: { NamedPreparedStatement.setDouble( NamedParam.getKey(), Double.parseDouble( strInputServiceParameterValue ) ); break; }
	
						}
					
					}
					else {
						
						NamedPreparedStatement.setNull( NamedParam.getKey(), InputServiceParameterDef.getParameterDataTypeID() );
						
					}*/
					
					/*if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_VARCHAR ) || InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_CHAR ) ) {
						
						NamedPreparedStatement.setString( NamedParam.getKey(), strInputServiceParameterValue );
						
					}
					else if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_INTEGER ) ) {
						
						NamedPreparedStatement.setInt( NamedParam.getKey(), Integer.parseInt( strInputServiceParameterValue ) );

					}
					else if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_SMALLINT ) ) {
						
						NamedPreparedStatement.setShort( NamedParam.getKey(), Short.parseShort( strInputServiceParameterValue ) );

					}
					else if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_BIGINT ) ) {
						
						NamedPreparedStatement.setLong( NamedParam.getKey(), Long.parseLong( strInputServiceParameterValue ) );

					}
					else if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_DATE ) ) {
						
						SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
						
						java.util.Date Date = DateFormat.parse( strInputServiceParameterValue );
						
						java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
						
						NamedPreparedStatement.setDate( NamedParam.getKey(), SQLDate );

					}
					else if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_TIME ) ) {
						
						SimpleDateFormat DateFormat = new SimpleDateFormat( strTimeFormat );
						
						java.util.Date Date = DateFormat.parse( strInputServiceParameterValue );
						
						java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
						
						NamedPreparedStatement.setTime( NamedParam.getKey(), SQLTime );

					}
					else if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_TIMESTAMP ) ) {
						
						SimpleDateFormat DateFormat = new SimpleDateFormat( strDateTimeFormat );
						
						java.util.Date Date = DateFormat.parse( strInputServiceParameterValue );
						
						java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
						
						NamedPreparedStatement.setTimestamp( NamedParam.getKey(), SQLTimeStamp );

					}
					else if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_BLOB ) ) {
						
						java.sql.Blob BlobData = new SerialBlob( Base64.decode( strInputServiceParameterValue ) );
						
						NamedPreparedStatement.setBlob( NamedParam.getKey(), BlobData );

					}
					else if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_FLOAT ) || InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_NUMERIC ) || InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_DECIMAL ) || InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_CURRENCY ) || InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_MONEY ) ) {
						
						NamedPreparedStatement.setFloat( NamedParam.getKey(), Float.parseFloat( strInputServiceParameterValue ) );

					}
					else if ( InputServiceParameterDef.getParameterType().equals( NamesSQLTypes.strSQL_BOOLEAN ) ) {
						
						NamedPreparedStatement.setBoolean( NamedParam.getKey(), Boolean.parseBoolean( strInputServiceParameterValue ) );

					}*/
				
                }
				else if ( intMacroIndex >= 0 ) {
					
					if ( intMacroIndex < intMacrosTypes.length && intMacroIndex < strMacrosValues.length ) {
				
						this.setMacroValueToNamedPreparedStatement( NamedPreparedStatement, NamedParam.getKey(), intMacroIndex, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
						
		    			/*switch ( intMacrosTypes[ intMacroIndex ] ) {
		    			
							case Types.INTEGER: { NamedPreparedStatement.setInt( NamedParam.getKey(), Integer.parseInt( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.BIGINT: { NamedPreparedStatement.setLong( NamedParam.getKey(), Long.parseLong( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.SMALLINT: { NamedPreparedStatement.setShort( NamedParam.getKey(), Short.parseShort( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.VARCHAR: 
							case Types.CHAR: { NamedPreparedStatement.setString( NamedParam.getKey(), strMacrosValues[ intMacroIndex ] ); break; }
							case Types.BOOLEAN: { NamedPreparedStatement.setBoolean( NamedParam.getKey(), Boolean.parseBoolean( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.BLOB: { 	
								
												java.sql.Blob BlobData = new SerialBlob( Base64.decode( strMacrosValues[ intMacroIndex ].getBytes() ) );
											
											    NamedPreparedStatement.setBlob( NamedParam.getKey(), BlobData );
					                            
											    break; 
					
											 }
							case Types.DATE: {
								               
											    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
												
												java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
												
												java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
												
												NamedPreparedStatement.setDate( NamedParam.getKey(), SQLDate );
								               
												break; 
								             
							                 }
							case Types.TIME: {  
								
												SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
												
												java.util.Date Date = TimeFormat.parse( strMacrosValues[ intMacroIndex ] );
												
												java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
												
												NamedPreparedStatement.setTime( NamedParam.getKey(), SQLTime );
								               
												break; 
								               
							                 }
							case Types.TIMESTAMP: {  
								
													 SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
													
													 java.util.Date Date = DateTimeFormat.parse( strMacrosValues[ intMacroIndex ] );
													
													 java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
													
													 NamedPreparedStatement.setTimestamp( NamedParam.getKey(), SQLTimeStamp );
	
								                     break; 
								                    
							                      }
							case Types.FLOAT: 
							case Types.DECIMAL: { NamedPreparedStatement.setFloat( NamedParam.getKey(), Float.parseFloat( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.DOUBLE: { NamedPreparedStatement.setDouble( NamedParam.getKey(), Double.parseDouble( strMacrosValues[ intMacroIndex ] ) ); break; }
	
						}*/

		    			/*if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_VARCHAR ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_CHAR ) ) {
							
							NamedPreparedStatement.setString( NamedParam.getKey(), strMacrosValues[ intMacroIndex ] );
							
						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_INTEGER ) ) {
							
							NamedPreparedStatement.setInt( NamedParam.getKey(), Integer.parseInt( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_SMALLINT ) ) {
							
							NamedPreparedStatement.setShort( NamedParam.getKey(), Short.parseShort( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_BIGINT ) ) {
							
							NamedPreparedStatement.setLong( NamedParam.getKey(), Long.parseLong( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_DATE ) ) {
							
							SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
							
							java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
							
							java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
							
							NamedPreparedStatement.setDate( NamedParam.getKey(), SQLDate );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_TIME ) ) {
							
							SimpleDateFormat DateFormat = new SimpleDateFormat( strTimeFormat );
							
							java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
							
							java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
							
							NamedPreparedStatement.setTime( NamedParam.getKey(), SQLTime );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_TIMESTAMP ) ) {
							
							SimpleDateFormat DateFormat = new SimpleDateFormat( strDateTimeFormat );
							
							java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
							
							java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
							
							NamedPreparedStatement.setTimestamp( NamedParam.getKey(), SQLTimeStamp );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_BLOB ) ) {
							
							java.sql.Blob BlobData = new SerialBlob( Base64.decode( strMacrosValues[ intMacroIndex ] ) );
							
							NamedPreparedStatement.setBlob( NamedParam.getKey(), BlobData );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_FLOAT ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_NUMERIC ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_DECIMAL ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_CURRENCY ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_MONEY ) ) {
							
							NamedPreparedStatement.setFloat( NamedParam.getKey(), Float.parseFloat( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_DOUBLE ) ) {
							
							NamedPreparedStatement.setDouble( NamedParam.getKey(), Double.parseDouble( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_BOOLEAN ) ) {
							
							NamedPreparedStatement.setBoolean( NamedParam.getKey(), Boolean.parseBoolean( strMacrosValues[ intMacroIndex ] ) );

						}*/

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
	public CMemoryRowSet ExecuteCheckMethodStoredProcedure( Connection DBConnection, HttpServletRequest Request, ArrayList<CInputServiceParameter> InputServiceParameters, int[] intMacrosTypes, String[] strMacrosNames, String[] strMacrosValues, String strDateFormat, String strTimeFormat, String strDateTimeFormat, String strSQL, CExtendedLogger Logger, CLanguage Lang  ) {

		CMemoryRowSet Result = null;
		
		try {	
			
			HashMap<String,String> Delimiters = new HashMap<String,String>();
			
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartMacroTag, ConfigXMLTagsServicesDaemon._EndMacroTag );
			Delimiters.put( ConfigXMLTagsServicesDaemon._StartParamValue, ConfigXMLTagsServicesDaemon._EndParamValue );
			
			CNamedCallableStatement NamedCallableStatement = new CNamedCallableStatement( DBConnection, strSQL, Delimiters );		

			/*if ( Logger != null ) {
				
				String strTmpSQL = SQLStatement.getParsedSetaement();

				Logger.LogWarning( "-1", strTmpSQL );
				
			}*/

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

					/*if ( strInputServiceParameterValue.toLowerCase().equals( NamesSQLTypes._NULL ) == false ) {
		    			
						switch ( InputServiceParameterDef.getParameterDataTypeID() ) {
		    			
							case Types.INTEGER: { NamedCallableStatement.setInt( NamedParam.getKey(), Integer.parseInt( strInputServiceParameterValue ) ); break; }
							case Types.BIGINT: { NamedCallableStatement.setLong( NamedParam.getKey(), Long.parseLong( strInputServiceParameterValue ) ); break; }
							case Types.SMALLINT: { NamedCallableStatement.setShort( NamedParam.getKey(), Short.parseShort( strInputServiceParameterValue ) ); break; }
							case Types.VARCHAR: 
							case Types.CHAR: { NamedCallableStatement.setString( NamedParam.getKey(), strInputServiceParameterValue ); break; }
							case Types.BOOLEAN: { NamedCallableStatement.setBoolean( NamedParam.getKey(), Boolean.parseBoolean( strInputServiceParameterValue ) ); break; }
							case Types.BLOB: { 	
								
												java.sql.Blob BlobData = new SerialBlob( Base64.decode( strInputServiceParameterValue.getBytes() ) );
											
											    NamedCallableStatement.setBlob( NamedParam.getKey(), BlobData );
					                            
											    break; 
					
											 }
							case Types.DATE: {
								               
											    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
												
												java.util.Date Date = DateFormat.parse( strInputServiceParameterValue );
												
												java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
												
												NamedCallableStatement.setDate( NamedParam.getKey(), SQLDate );
								               
												break; 
								             
							                 }
							case Types.TIME: {  
								
												SimpleDateFormat DateFormat = new SimpleDateFormat( strTimeFormat );
												
												java.util.Date Date = DateFormat.parse( strInputServiceParameterValue );
												
												java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
												
												NamedCallableStatement.setTime( NamedParam.getKey(), SQLTime );
								               
												break; 
								               
							                 }
							case Types.TIMESTAMP: {  
								
													 SimpleDateFormat DateFormat = new SimpleDateFormat( strDateTimeFormat );
													
													 java.util.Date Date = DateFormat.parse( strInputServiceParameterValue );
													
													 java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
													
													 NamedCallableStatement.setTimestamp( NamedParam.getKey(), SQLTimeStamp );
	
								                     break; 
								                    
							                      }
							case Types.FLOAT: 
							case Types.DECIMAL: { NamedCallableStatement.setFloat( NamedParam.getKey(), Float.parseFloat( strInputServiceParameterValue ) ); break; }
							case Types.DOUBLE: { NamedCallableStatement.setDouble( NamedParam.getKey(), Double.parseDouble( strInputServiceParameterValue ) ); break; }
	
						}
				
					}
					else {
						
						NamedCallableStatement.setNull( NamedParam.getKey(), InputServiceParameterDef.getParameterDataTypeID() );
						
					}*/
	    			
                }
				else if ( intMacroIndex >= 0 ) {
					
					if ( intMacroIndex < intMacrosTypes.length && intMacroIndex < strMacrosValues.length ) {
				
						this.setMacroValueToNamedCallableStatement( NamedCallableStatement, NamedParam.getKey(), intMacroIndex, intMacrosTypes, strMacrosNames, strMacrosValues, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );

						/*switch ( intMacrosTypes[ intMacroIndex ] ) {
		    			
							case Types.INTEGER: { NamedCallableStatement.setInt( NamedParam.getKey(), Integer.parseInt( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.BIGINT: { NamedCallableStatement.setLong( NamedParam.getKey(), Long.parseLong( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.SMALLINT: { NamedCallableStatement.setShort( NamedParam.getKey(), Short.parseShort( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.VARCHAR: 
							case Types.CHAR: { NamedCallableStatement.setString( NamedParam.getKey(), strMacrosValues[ intMacroIndex ] ); break; }
							case Types.BOOLEAN: { NamedCallableStatement.setBoolean( NamedParam.getKey(), Boolean.parseBoolean( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.BLOB: { 	
								
												java.sql.Blob BlobData = new SerialBlob( Base64.decode( strMacrosValues[ intMacroIndex ].getBytes() ) );
											
											    NamedCallableStatement.setBlob( NamedParam.getKey(), BlobData );
					                            
											    break; 
					
											 }
							case Types.DATE: {
								               
											    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
												
												java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
												
												java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
												
												NamedCallableStatement.setDate( NamedParam.getKey(), SQLDate );
								               
												break; 
								             
							                 }
							case Types.TIME: {  
								
												SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
												
												java.util.Date Date = TimeFormat.parse( strMacrosValues[ intMacroIndex ] );
												
												java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
												
												NamedCallableStatement.setTime( NamedParam.getKey(), SQLTime );
								               
												break; 
								               
							                 }
							case Types.TIMESTAMP: {  
								
													 SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
													
													 java.util.Date Date = DateTimeFormat.parse( strMacrosValues[ intMacroIndex ] );
													
													 java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
													
													 NamedCallableStatement.setTimestamp( NamedParam.getKey(), SQLTimeStamp );
	
								                     break; 
								                    
							                      }
							case Types.FLOAT: 
							case Types.DECIMAL: { NamedCallableStatement.setFloat( NamedParam.getKey(), Float.parseFloat( strMacrosValues[ intMacroIndex ] ) ); break; }
							case Types.DOUBLE: { NamedCallableStatement.setDouble( NamedParam.getKey(), Double.parseDouble( strMacrosValues[ intMacroIndex ] ) ); break; }
	
						}*/

		    			/*if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_VARCHAR ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_CHAR ) ) {
							
							NamedCallableStatement.setString( NamedParam.getKey(), strMacrosValues[ intMacroIndex ] );
							
						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_INTEGER ) ) {
							
							NamedCallableStatement.setInt( NamedParam.getKey(), Integer.parseInt( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_SMALLINT ) ) {
							
							NamedCallableStatement.setShort( NamedParam.getKey(), Short.parseShort( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_BIGINT ) ) {
							
							NamedCallableStatement.setLong( NamedParam.getKey(), Long.parseLong( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_DATE ) ) {
							
							SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
							
							java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
							
							java.sql.Date SQLDate = new java.sql.Date( Date.getTime() ); 
							
							NamedCallableStatement.setDate( NamedParam.getKey(), SQLDate );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_TIME ) ) {
							
							SimpleDateFormat DateFormat = new SimpleDateFormat( strTimeFormat );
							
							java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
							
							java.sql.Time SQLTime = new java.sql.Time( Date.getTime() ); 
							
							NamedCallableStatement.setTime( NamedParam.getKey(), SQLTime );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_TIMESTAMP ) ) {
							
							SimpleDateFormat DateFormat = new SimpleDateFormat( strDateTimeFormat );
							
							java.util.Date Date = DateFormat.parse( strMacrosValues[ intMacroIndex ] );
							
							java.sql.Timestamp SQLTimeStamp = new java.sql.Timestamp( Date.getTime() ); 
							
							NamedCallableStatement.setTimestamp( NamedParam.getKey(), SQLTimeStamp );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_BLOB ) ) {
							
							java.sql.Blob BlobData = new SerialBlob( Base64.decode( strMacrosValues[ intMacroIndex ] ) );
							
							NamedCallableStatement.setBlob( NamedParam.getKey(), BlobData );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_FLOAT ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_NUMERIC ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_DECIMAL ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_CURRENCY ) || strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_MONEY ) ) {
							
							NamedCallableStatement.setFloat( NamedParam.getKey(), Float.parseFloat( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_DOUBLE ) ) {
							
							NamedCallableStatement.setDouble( NamedParam.getKey(), Double.parseDouble( strMacrosValues[ intMacroIndex ] ) );

						}
						else if ( strMacrosTypes[ intMacroIndex ].equals( NamesSQLTypes.strSQL_BOOLEAN ) ) {
							
							NamedCallableStatement.setBoolean( NamedParam.getKey(), Boolean.parseBoolean( strMacrosValues[ intMacroIndex ] ) );

						}*/

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
				
				strOptionalDummyQuery = "SELECT DISTINCT RDB$RELATION_NAME FROM RDB$RELATION_FIELDS";
				
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
			
			//ResultSet TmpQueryResult = null;
			
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
						
						//TmpQueryResult = QueryResult;
	
						/*if ( Logger != null ) {
						
							int intFetchSize = QueryResult.getFetchSize();
							
							Logger.LogInfo( "2", Lang.Translate( "Fetch size [%s]", Integer.toString( intFetchSize ) )  );
						
						}*/
						
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

			//int intFetchSize = TmpQueryResult.getFetchSize();
			
			//Logger.LogInfo( "2", Lang.Translate( "Fetch size [%s]", Integer.toString( intFetchSize ) )  );
			
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
