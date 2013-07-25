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
package CommonClasses;

import java.sql.Blob;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.sql.rowset.serial.SerialBlob;

//import sun.misc.BASE64Decoder;

import net.maindataservices.Base64;

import ExtendedLogger.CExtendedLogger;

public class CMemoryFieldData {

	public enum TFieldScope { IN, OUT, INOUT };
	
	public String strName = "";
	public int intSQLType = -1;
	public String strSQLTypeName = "";
	public 	int intLength = 0;
	public String strLabel = "";
	public ArrayList<Object> Data = null;
	public TFieldScope Scope = TFieldScope.IN;
	
	public CMemoryFieldData() {
		
		Data = new ArrayList<Object>();
		
	}
	
	public CMemoryFieldData( String strName, int intSQLType, String strSQLName, int intLength, String strLabel ) {

		this.strName = strName;
		this.intSQLType = intSQLType;
		this.strSQLTypeName = strSQLName;
		this.intLength = intLength;
	    this.strLabel = strLabel;
	    
		Data = new ArrayList<Object>();
	
	}

	public CMemoryFieldData( CMemoryFieldData MemoryField ) {

		this.strName = MemoryField.strName;
		this.intSQLType = MemoryField.intSQLType;
		this.strSQLTypeName = MemoryField.strSQLTypeName;
		this.intLength = MemoryField.intLength;
	    this.strLabel = MemoryField.strLabel;
	    
		Data = new ArrayList<Object>();
		Data.addAll( MemoryField.Data );
	
	}

	public CMemoryFieldData( String strSMDToParse, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		CSMDNode DataNode = new CSMDNode( strSMDToParse );
		
		this.strName = DataNode.strName;
		this.intSQLType = NamesSQLTypes.ConvertToJavaSQLType( DataNode.getAttributeByName( "datatype" ) );
		this.strSQLTypeName = DataNode.getAttributeByName( "datatype" );
		
		String strScope = DataNode.getAttributeByName( "scope" );
		
		if ( strScope != null && strScope.isEmpty() == false ) {
			
			try {
			
				Scope = TFieldScope.valueOf( strScope.toUpperCase() );
			
			}
			catch ( Exception Ex ) {
				
				if ( Logger != null ) {
					
					Logger.LogException( "-1015", Ex.getMessage(), Ex );
				
				}	
				
			}
			
		}
		
		this.intLength = 0;
	    this.strLabel = DataNode.strName;
	    
		Data = new ArrayList<Object>();
	
		if ( this.strName.isEmpty() == false && this.intSQLType >= 0 ) {
			
			for ( CSMDNode ChildNode : DataNode.ChildNodes ) {
				
				if ( ChildNode.strValue.isEmpty() == false ) {
					
					if ( ChildNode.strValue.trim().toLowerCase().equals( NamesSQLTypes._NULL ) ) {
						
						this.addNull();
						
					}
					else {
						
						this.addData( ChildNode.strValue, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
						
					}
					
				}
				else {
					
					this.addNull();
					
				}
				
			}
			
		}
		
	}

	public Object getData( int intRow ) {
		
	   	if ( intRow < this.Data.size() )   
		    return Data.get( intRow );
	   	else
	   		return null;  
		
	}

	public boolean setData( int intRow, Object Data ) {
		
		boolean bResult = false;
		
		if ( intRow < this.Data.size() ) {
			
			this.Data.set( intRow, Data );

			bResult = true;
			
		}
		
		return bResult;
	}
	
	public void setData( int intRow, String strData, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {

			switch ( intSQLType ) {

				case Types.INTEGER: { this.setData( intRow, Integer.parseInt( strData ) ); break; }
				case Types.BIGINT: { this.setData( intRow, Long.parseLong( strData ) ); break; }
				case Types.SMALLINT: { this.setData( intRow, Short.parseShort( strData ) ); break; }
				case Types.VARCHAR: 
				case Types.CHAR: { this.setData( intRow, strData ); break; }
				case Types.BOOLEAN: { this.setData( intRow, Boolean.parseBoolean( strData ) ); break; }
				case Types.BLOB: {

					                java.sql.Blob BlobData = new SerialBlob( Base64.decode( strData.getBytes() ) );
			
					                this.setData( intRow, BlobData );
					                
									break;
				
								 }
				case Types.DATE: { 
					
								    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
									
									java.util.Date D = DateFormat.parse( strData );
									
									this.setData( intRow, D ); 
		
									break; 
					              
				                 }
				case Types.TIME: { 
					
									SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
									
									java.util.Date T = TimeFormat.parse( strData );
		
									this.setData( intRow, T ); 
									
									break; 
									
								}
				case Types.TIMESTAMP: { 
					
										   SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
											
										   java.util.Date DateTime = DateTimeFormat.parse( strData );
			
										   this.setData( intRow, DateTime ); 
									
										   break; 
										
									  }
				case Types.FLOAT: 
				case Types.DECIMAL: { this.setData( intRow, Float.parseFloat( strData ) ); break; }
				case Types.DOUBLE: { this.setData( intRow, Double.parseDouble( strData ) ); break; }

				default:{
					
					if ( Logger != null ) {
						
						if ( Lang != null )
						    Logger.LogWarning( "-1", Lang.Translate( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
						else
						    Logger.LogWarning( "-1", String.format( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
						
						
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

	public void addData( Object Data ) {
		
		this.Data.add( Data );
		
	}
	
	public void addData( Object Data, int intCountRows ) {
		
		for ( int intRows = 0; intRows < intCountRows; intRows++ ) {  

			this.Data.add( Data );
			
		}
		
	}
	
	public void addData( String strData, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
			
			if ( strData != null && strData.isEmpty() == false && strData.toLowerCase().equals( "null" ) == false ) {

				switch ( intSQLType ) {
	
					case Types.INTEGER: { this.addData( Integer.parseInt( strData ) ); break; }
					case Types.BIGINT: { this.addData( Long.parseLong( strData ) ); break; }
					case Types.SMALLINT: { this.addData( Short.parseShort( strData ) ); break; }
					case Types.VARCHAR: 
					case Types.CHAR: { this.addData( (String) strData ); break; }
					case Types.BOOLEAN: { this.addData( Boolean.parseBoolean( strData ) ); break; }
					case Types.BLOB: {
	
										//BASE64Decoder decoder = new BASE64Decoder();
										//byte[] decodedBytes = decoder.decodeBuffer( strData );
					
										//java.sql.Blob BlobData = new SerialBlob( decodedBytes ); 
										java.sql.Blob BlobData = new SerialBlob( Base64.decode( strData.getBytes() ) );
					
										this.addData( BlobData );
						                
										break;
					
									 }
					case Types.DATE: { 
						
									    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
										
										java.util.Date D = DateFormat.parse( strData );
										
										this.addData( D ); 
			
										break; 
						              
					                 }
					case Types.TIME: { 
						
										SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
										
										java.util.Date T = TimeFormat.parse( strData );
			
										this.addData( T ); 
										
										break; 
										
									}
					case Types.TIMESTAMP: { 
						
											   SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
												
											   java.util.Date DateTime = DateTimeFormat.parse( strData );
				
											   this.addData( DateTime ); 
										
											   break; 
											
										  }
					case Types.FLOAT: 
					case Types.DECIMAL: { this.addData( Float.parseFloat( strData ) ); break; }
					case Types.DOUBLE: { this.addData( Double.parseDouble( strData ) ); break; }
	
					default:{
					
						if ( Logger != null ) {
							
							if ( Lang != null )
							    Logger.LogWarning( "-1", Lang.Translate( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
							else
							    Logger.LogWarning( "-1", String.format( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
							
							
						}
						
						break;
						
					}
					
				}

			}
			else {
				
				this.addNull();
				
			}
				
		}
		catch ( Exception Ex ) {

			if ( Logger != null ) {
				
				Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
			}	

		}
		
	}

	public ArrayList<Object> getAllData() {
		
		return Data;
		
	}
	
	public void setAllData( ArrayList<Object> Data ) {
		
		this.Data.clear();
		this.Data.addAll( Data );
		
	}

	public void setAllData( Object Data ) {

		for ( int intIndex = 0; intIndex < this.Data.size(); intIndex++ ) {   

			this.Data.set( intIndex, Data );

		}

	}
	
	public void addAllData( ArrayList<Object> Data ) {
		
		this.Data.addAll( Data );
		
	}

	public boolean DataIsNull( int intRow ) {
		
		if ( intRow < this.Data.size() )   
			return this.Data.get( intRow ) == null;
		else
			return true;

	}

	public void clearData() {
		
		Data.clear();
		
	}
	
	public void addNull() {
		
	    Data.add( null );
		
	}
	
	public void addNulls( int intCountOfRowToAdd ) {
		
		for ( int intIndex = 0; intIndex < intCountOfRowToAdd; intIndex++ ) {
			
		    Data.add( null );
			
		}
		
	}
	
	public boolean checkFieldValue( int intIndexRow, Object Value, int intSQLTypeValue, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;

		if ( intIndexRow >= 0 && intIndexRow < Data.size() &&  NamesSQLTypes.IsCompatiblesSQLTypes( intSQLType, intSQLTypeValue ) ) {
		
			Object FieldData = Data.get( intIndexRow );
			
			if ( FieldData != null ) {

				try {

					switch ( intSQLType ) {

						case Types.INTEGER: { bResult = (Integer) Value == (Integer) FieldData; break; }
						case Types.BIGINT: { bResult = (Long) Value == (Long) FieldData; break; }
						case Types.SMALLINT: { bResult = (Short) Value == (Short) FieldData; break; }
						case Types.VARCHAR: 
						case Types.CHAR: { bResult = ((String) Value).equals( (String) FieldData ); break; }
						case Types.BOOLEAN: { bResult = (Boolean) Value  == (Boolean) FieldData; break; }
						case Types.BLOB: {
	
										    Blob BinaryBLOBData = (Blob) Value;
					
											String strFieldValueBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ), "UTF-8" );
					
											BinaryBLOBData = (Blob) FieldData;
					
											String strFieldDataBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ), "UTF-8" );
					
											bResult = strFieldValueBase64Coded.equals( strFieldDataBase64Coded );
					
											break;
						
										 }
						case Types.DATE: { bResult = ((Date) Value ).compareTo( (Date) FieldData ) == 0; break; }
						case Types.TIME: { bResult = ((Time) Value ).compareTo( (Time) FieldData ) == 0; break; }
						case Types.TIMESTAMP: { bResult = ( (Timestamp) Value ).compareTo( (Timestamp) FieldData ) == 0; break; }
						case Types.FLOAT: 
						case Types.DECIMAL: { bResult = (Float) Value == (Float) FieldData; break; }
						case Types.DOUBLE: { bResult = (Double) Value == (Double) FieldData; break; }

						default:{
							
							if ( Logger != null ) {
								
								if ( Lang != null )
								    Logger.LogWarning( "-1", Lang.Translate( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
								else
								    Logger.LogWarning( "-1", String.format( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
								
								
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
			else if ( Value == null ) {
				
				bResult = true;
				
			}

		}
		
		return bResult;
		
	}
	
	public boolean checkFieldValue( int intIndexRow, String strValue, int intSQLTypeValue, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;

		if ( intIndexRow >= 0 && intIndexRow < Data.size() &&  NamesSQLTypes.IsCompatiblesSQLTypes( intSQLType, intSQLTypeValue ) ) {
		
			Object FieldData = Data.get( intIndexRow );
			
			if ( FieldData != null ) {

				try {

					switch ( intSQLType ) {

						case Types.INTEGER: { bResult = Integer.parseInt( strValue ) == (Integer) FieldData; break; }
						case Types.BIGINT: { bResult = Long.parseLong( strValue ) == (Long) FieldData; break; }
						case Types.SMALLINT: { bResult = Short.parseShort( strValue ) == (Short) FieldData; break; }
						case Types.VARCHAR: 
						case Types.CHAR: { bResult = strValue.equals( (String) FieldData ); break; }
						case Types.BOOLEAN: { bResult = Boolean.parseBoolean( strValue ) == (Boolean) FieldData; break; }
						case Types.BLOB: {
	
											Blob BinaryBLOBData = (Blob) FieldData;
					
											String strFieldDataBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ), "UTF-8" );
					
											bResult = strValue.equals( strFieldDataBase64Coded );
					
											break;
						
										 }
						case Types.DATE: { 
							
										    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
											
											java.util.Date D = DateFormat.parse( strValue );
											
											bResult = D.compareTo( (Date) FieldData ) == 0; 
				
											break; 
							              
						                 }
						case Types.TIME: { 
							
											SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
											
											java.util.Date T = TimeFormat.parse( strValue );
				
											bResult = T.compareTo( (Time) FieldData ) == 0; 
											
											break; 
											
										}
						case Types.TIMESTAMP: { 
							
												   SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
													
												   java.util.Date DateTime = DateTimeFormat.parse( strValue );
					
												   bResult = DateTime.compareTo( (Timestamp) FieldData ) == 0; 
											
												   break; 
												
											  }
						case Types.FLOAT: 
						case Types.DECIMAL: { bResult = Float.parseFloat( strValue ) == (Float) FieldData; break; }
						case Types.DOUBLE: { bResult = Double.parseDouble( strValue ) == (Double) FieldData; break; }

						default:{
							
							if ( Logger != null ) {
								
								if ( Lang != null )
								    Logger.LogWarning( "-1", Lang.Translate( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
								else
								    Logger.LogWarning( "-1", String.format( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
								
								
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
			else if ( strValue == null || strValue.equals( NamesSQLTypes._NULL ) ) {
				
				bResult = true;
				
			}

		}
		
		return bResult;
		
	}
	
	public String FieldValueToString( int intIndexRow, boolean bResumeBlob, String strDateFormat, String strTimeFormat, String strDateTimeFormat, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {
		
		String strResult = "";
		
		if ( intIndexRow >= Data.size() &&  bUseLastRowIfRowNotExits == true )
			intIndexRow = Data.size() - 1;
			
		
		if ( intIndexRow >= 0 && intIndexRow < Data.size() ) {
		
			Object FieldData = Data.get( intIndexRow );
			
			if ( FieldData != null ) {

				try {

					switch ( intSQLType ) {

						case Types.INTEGER: { strResult = Integer.toString( (Integer) FieldData ); break; }
						case Types.BIGINT: { strResult = Long.toString( (Long) FieldData ); break; }
						case Types.SMALLINT: { strResult = Short.toString( (Short) FieldData ); break; }
						case Types.VARCHAR: 
						case Types.CHAR: { strResult = (String) FieldData; break; }
						case Types.BOOLEAN: { strResult = Boolean.toString( (Boolean) FieldData ); break; }
						case Types.BLOB: {
	
											Blob BinaryBLOBData = (Blob) FieldData;
					
											String strFieldDataBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ), "UTF-8" );
					
											int intBlobLength = strFieldDataBase64Coded.length();
											
											if ( bResumeBlob == false || intBlobLength <= 200 ) {
											 
												strResult = strFieldDataBase64Coded;
												
											}    
											else {
											    
												strResult = strFieldDataBase64Coded.substring( 0, 100 ) + "..." + strFieldDataBase64Coded.substring( intBlobLength - 100, intBlobLength );
												
											}	
					
											break;
						
										 }
						case Types.DATE: { 
							
										    SimpleDateFormat DateFormat = new SimpleDateFormat( strDateFormat );
											
											strResult = DateFormat.format( (Date) FieldData ); 
				
											break; 
							              
						                 }
						case Types.TIME: { 
							
											SimpleDateFormat TimeFormat = new SimpleDateFormat( strTimeFormat );
											
											strResult = TimeFormat.format( (Time) FieldData ); 
											
											break; 
											
										}
						case Types.TIMESTAMP: { 
							
												   SimpleDateFormat DateTimeFormat = new SimpleDateFormat( strDateTimeFormat );
					
												   strResult = DateTimeFormat.format( (Timestamp) FieldData ); 
											
												   break; 
												
											  }
						case Types.FLOAT: 
						case Types.DECIMAL: { strResult = Float.toString( (Float) FieldData ); break; }
						case Types.DOUBLE: { strResult = Double.toString( (Double) FieldData ); break; }

						default:{
							
							if ( Logger != null ) {
								
								if ( Lang != null )
								    Logger.LogWarning( "-1", Lang.Translate( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
								else
								    Logger.LogWarning( "-1", String.format( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
								
								
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
				
				strResult = NamesSQLTypes._NULL;
				
			}

		}
		
		return strResult;
		
	}
	
	/*
	public boolean setFieldDataToPreparedStatement( CNamedPreparedStatement NamedPreparedStatement, String strPreparedStatementFieldName, int intIndexRow, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		if ( intIndexRow >= Data.size() && bUseLastRowIfRowNotExits ) {
			
			intIndexRow = Data.size() - 1;
			
		}
		
		if ( intIndexRow >= 0 && intIndexRow < Data.size() ) {
			
			Object FieldData = Data.get( intIndexRow );
			
			if ( FieldData != null ) { 
				
				try {

					switch ( intSQLType ) {

						case Types.INTEGER: { NamedPreparedStatement.setInt( strPreparedStatementFieldName, (Integer) FieldData ); bResult = true; break; }
						case Types.BIGINT: { NamedPreparedStatement.setLong( strPreparedStatementFieldName, (Long) FieldData ); bResult = true; break; }
						case Types.SMALLINT: { NamedPreparedStatement.setShort( strPreparedStatementFieldName, (Short) FieldData ); bResult = true; break; }
						case Types.VARCHAR: 
						case Types.CHAR: { NamedPreparedStatement.setString( strPreparedStatementFieldName, (String) FieldData ); bResult = true; break; }
						case Types.BOOLEAN: { NamedPreparedStatement.setBoolean( strPreparedStatementFieldName, ( Boolean ) FieldData ); bResult = true; break; }
						case Types.BLOB: { 	
							
							                if ( intBlobType == 1 ) //PostgreSQL Cases 
							                	NamedPreparedStatement.setBinaryStream( strPreparedStatementFieldName, ((Blob) FieldData).getBinaryStream(), ((Blob) FieldData).getBinaryStream().available() );  
							                else
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
											
											NamedPreparedStatement.setTime( strPreparedStatementFieldName, (Time) FieldData );
							               
											bResult = true;
											
											break; 
							               
						                 }
						case Types.TIMESTAMP: {  
							
												 NamedPreparedStatement.setTimestamp( strPreparedStatementFieldName, (Timestamp) FieldData );
	
												 bResult = true;
												 
							                     break; 
							                    
						                      }
						case Types.FLOAT: 
						case Types.DECIMAL: { NamedPreparedStatement.setFloat( strPreparedStatementFieldName, (Float) FieldData ); bResult = true; break; }
						case Types.DOUBLE: { NamedPreparedStatement.setDouble( strPreparedStatementFieldName, (Double) FieldData ); bResult = true; break; }

						default:{
							
							if ( Logger != null ) {
								
								if ( Lang != null )
								    Logger.LogWarning( "-1", Lang.Translate( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
								else
								    Logger.LogWarning( "-1", String.format( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
								
								
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

					NamedPreparedStatement.setNull( strPreparedStatementFieldName, intSQLType );
				
					bResult = true;
					
				}
				catch ( Exception Ex ) {
					
					if ( Logger != null ) {
						
						Logger.LogException( "-1015", Ex.getMessage(), Ex );
					
					}
					
				}
				
			}

		}
		
		return bResult;
		
	}

	public boolean setFieldDataToCallableStatement( CNamedCallableStatement NamedCallableStatement, String strCallableStatementFieldName, int intIndexRow, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		if ( Scope.equals( TFieldScope.IN ) || Scope.equals( TFieldScope.INOUT ) ) {
		
			if ( intIndexRow >= Data.size() && bUseLastRowIfRowNotExits ) {
				
				intIndexRow = Data.size() - 1;
				
			}
			
			if ( intIndexRow >= 0 && intIndexRow < Data.size() ) {
				
				Object FieldData = Data.get( intIndexRow );
				
				if ( FieldData != null ) { 
					
					try {
	
						switch ( this.intSQLType ) {
	
							case Types.INTEGER: { NamedCallableStatement.setInt( strCallableStatementFieldName, (Integer) FieldData ); bResult = true; break; }
							case Types.BIGINT: { NamedCallableStatement.setLong( strCallableStatementFieldName, (Long) FieldData ); bResult = true; break; }
							case Types.SMALLINT: { NamedCallableStatement.setShort( strCallableStatementFieldName, (Short) FieldData ); bResult = true; break; }
							case Types.VARCHAR: 
							case Types.CHAR: { NamedCallableStatement.setString( strCallableStatementFieldName, (String) FieldData ); bResult = true; break; }
							case Types.BOOLEAN: { NamedCallableStatement.setBoolean( strCallableStatementFieldName, ( Boolean ) FieldData ); bResult = true; break; }
							case Types.BLOB: { 	
								
				                                if ( intBlobType == 1 ) //PostgreSQL Cases 
				                	                NamedCallableStatement.setBinaryStream( strCallableStatementFieldName, ((Blob) FieldData).getBinaryStream(), ((Blob) FieldData).getBinaryStream().available() );  
				                                else
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
												
												NamedCallableStatement.setTime( strCallableStatementFieldName, (Time) FieldData );
								               
												bResult = true;
												
												break; 
								               
							                 }
							case Types.TIMESTAMP: {  
								
													 NamedCallableStatement.setTimestamp( strCallableStatementFieldName, (Timestamp) FieldData );
		
													 bResult = true;
													 
								                     break; 
								                    
							                      }
							case Types.FLOAT: 
							case Types.DECIMAL: { NamedCallableStatement.setFloat( strCallableStatementFieldName, (Float) FieldData ); bResult = true; break; }
							case Types.DOUBLE: { NamedCallableStatement.setDouble( strCallableStatementFieldName, (Double) FieldData ); bResult = true; break; }
	
							default:{
								
								if ( Logger != null ) {
									
									if ( Lang != null )
									    Logger.LogWarning( "-1", Lang.Translate( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
									else
									    Logger.LogWarning( "-1", String.format( "Unknown SQL data type [%s]", Integer.toString( intSQLType ) ) );
									
									
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
	
						NamedCallableStatement.setNull( strCallableStatementFieldName, this.intSQLType );
					
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
		
		if ( Scope.equals( TFieldScope.OUT ) ) {
			
			try {
			
				NamedCallableStatement.registerOutParameter( strCallableStatementFieldName, this.intSQLType );
			
			}
			catch ( Exception Ex ) {
				
				if ( Logger != null ) {
					
					Logger.LogException( "-1017", Ex.getMessage(), Ex );
				
				}
				
			}
			
		}
		
		return bResult;
		
	}*/
	
}
