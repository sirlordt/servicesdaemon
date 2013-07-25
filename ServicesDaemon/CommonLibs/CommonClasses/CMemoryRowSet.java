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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
//import javax.sql.rowset.serial.SerialBlob;

import AbstractDBEngine.CAbstractDBEngine;
//import AbstractResponseFormat.CAbstractResponseFormat;
import CommonClasses.CMemoryFieldData.TFieldScope;
import ExtendedLogger.CExtendedLogger;

import com.sun.rowset.CachedRowSetImpl;

public class CMemoryRowSet {

	protected boolean bAllowDuplicateNames;
	protected ArrayList<CMemoryFieldData> FieldsData;

	public CMemoryRowSet( boolean bAllowDuplicateNames ) {
		
		this.bAllowDuplicateNames = bAllowDuplicateNames;
		
		FieldsData = new ArrayList<CMemoryFieldData>();

	}

	public CMemoryRowSet( CMemoryRowSet MemoryResultSet ) {
		
		this( MemoryResultSet.bAllowDuplicateNames );
		
		FieldsData.addAll( MemoryResultSet.FieldsData );
		
	}
	
	public CMemoryRowSet( boolean bAllowDuplicateNames, ResultSet ResultSetToClone, CAbstractDBEngine DBEngine, CExtendedLogger Logger, CLanguage Lang ) {
		
		this( bAllowDuplicateNames );

		this.cloneResultSet( ResultSetToClone, null, DBEngine, Logger, Lang );
		
	}

	public CMemoryRowSet( boolean bAllowDuplicateNames, ResultSet ResultSetToClone, CAbstractDBEngine DBEngine, ArrayList<String> strFieldsNames, CExtendedLogger Logger, CLanguage Lang ) {
		
		this( bAllowDuplicateNames );

		this.cloneResultSet( ResultSetToClone, strFieldsNames, DBEngine, Logger, Lang );
		
	}

	public void clearData() {
		
		for ( CMemoryFieldData Field: FieldsData ) {
			
			Field.clearData();
			
		}
		
	}

	public void clearAll() {
		
		this.clearData();
		
		FieldsData.clear();
		
	}
	
	public boolean cloneResultSet( ResultSet ResultSetToClone, CAbstractDBEngine DBEngine, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		this.clearAll();		
		bResult = addMetaData( ResultSetToClone, DBEngine, null, Logger, Lang );
		bResult = addRowData( ResultSetToClone, DBEngine, Logger, Lang );
		
		return bResult;
		
	}

	public boolean cloneResultSet( ResultSet ResultSetToClone, ArrayList<String> strFieldsNames, CAbstractDBEngine DBEngine, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		this.clearAll();		
		bResult = addMetaData( ResultSetToClone, DBEngine, strFieldsNames, Logger, Lang );
		bResult = addRowData( ResultSetToClone, DBEngine, Logger, Lang );
		
		return bResult;
		
	}

	public boolean cloneOnlyMetaData( ResultSet ResultSetToClone, CAbstractDBEngine DBEngine, ArrayList<String> strFieldsNames, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		this.clearAll();		
		bResult = addMetaData( ResultSetToClone, DBEngine, strFieldsNames, Logger, Lang );
		
		return bResult;
		
	}

	public boolean cloneRowData( ResultSet ResultSetToClone, CAbstractDBEngine DBEngine, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		this.clearData();
		bResult = addRowData( ResultSetToClone, DBEngine, Logger, Lang );
		
		return bResult;
		
	}
	
	public RowSetMetaData createCachedRowSetMetaData() {
		
		RowSetMetaData RowsetMetaData = null;
		
		try {
		
		    RowsetMetaData = new RowSetMetaDataImpl();
		    RowsetMetaData.setColumnCount( FieldsData.size() );
			
		    int intIndexColumn = 1;
		    
		    for ( CMemoryFieldData Field: FieldsData ) {
		    	
		    	RowsetMetaData.setColumnName( intIndexColumn, Field.strName );
		    	RowsetMetaData.setColumnType( intIndexColumn, Field.intSQLType );
		    	RowsetMetaData.setColumnTypeName( intIndexColumn, Field.strSQLTypeName );
		    	if ( Field.intLength > 0 )
		    	   RowsetMetaData.setColumnDisplaySize( intIndexColumn, Field.intLength );
		    	if ( Field.strLabel != null && Field.strLabel.isEmpty() == false )
		    	   RowsetMetaData.setColumnLabel( intIndexColumn, Field.strLabel );
		    	
		    	intIndexColumn += 1;
		    	
		    }
			
		
		}
		catch ( Exception Ex )  {
			
		}
		
		return RowsetMetaData;
		
	}
	
	public CachedRowSet createCachedRowSet() {
		
		return this.createCachedRowSet( null );
		
	}
	
	public CachedRowSet createCachedRowSet( RowSetMetaData RowsetMetaData ) {
		
		CachedRowSetImpl ResultRowset = null;
		
		try {
			
			ResultRowset = new CachedRowSetImpl();

			if ( RowsetMetaData == null ) {
			
				RowsetMetaData = this.createCachedRowSetMetaData(); 
			
			}
			
			ResultRowset.setMetaData( RowsetMetaData );
			
			try {

				int intRowCount = this.getRowCount();
				int intColCount = this.getFieldsCount();
				
				for ( int intRowIndex = 0; intRowIndex < intRowCount; intRowIndex++ ) {

			    	ResultRowset.moveToInsertRow();

			    	for ( int intIndexColumn = 1; intIndexColumn <= intColCount; intIndexColumn++ ) {
					
			    		CMemoryFieldData Field = this.FieldsData.get( intIndexColumn - 1 );
					
			    		Object FieldData = null; 
			    				
			    		if ( Field != null ) {
			    			
			    			FieldData = Field.getData( intRowIndex );
			    		
			    		}

			    		if ( FieldData != null ) {

			    			switch ( Field.intSQLType ) {
	
								case Types.INTEGER: { ResultRowset.updateInt( intIndexColumn, (Integer) FieldData ); break; }
								case Types.BIGINT: { ResultRowset.updateLong( intIndexColumn, (Long) FieldData ); break; }
								case Types.SMALLINT: { ResultRowset.updateShort( intIndexColumn, (Short) FieldData ); break; }
								case Types.VARCHAR: 
								case Types.CHAR: { ResultRowset.updateString( intIndexColumn, (String) FieldData ); break; }
								case Types.BOOLEAN: { ResultRowset.updateBoolean( intIndexColumn, (Boolean) FieldData ); break; }
								case Types.BLOB: { ResultRowset.updateBlob( intIndexColumn, (Blob) FieldData ); break; }
								case Types.DATE: { ResultRowset.updateDate( intIndexColumn, (java.sql.Date) FieldData  ); break; }
								case Types.TIME: { ResultRowset.updateTime( intIndexColumn, (Time) FieldData ); break; }
								case Types.TIMESTAMP: { ResultRowset.updateTimestamp( intIndexColumn, (Timestamp)FieldData ); break; }
								case Types.FLOAT: 
								case Types.DECIMAL: { ResultRowset.updateFloat( intIndexColumn, (Float) FieldData ); break; }
								case Types.DOUBLE: { ResultRowset.updateDouble( intIndexColumn, (Double) FieldData ); break; }
	
							}
			    		
			    		}
			    		else {
			    			
							ResultRowset.updateNull( intIndexColumn );
			    			
			    		}
			    		
					}
					
					ResultRowset.insertRow();
					
				}	
	    		
			    ResultRowset.moveToCurrentRow();
				
			}
			catch ( Exception Ex ) {
				
				//System.out.println( Ex );
				
			}
			
		
		}
		catch ( Exception Ex )  {
			
		}
		
		return ResultRowset;
		
	}

	public boolean addMetaData( ResultSet ResultSetToClone, CAbstractDBEngine DBEngine, ArrayList<String> strFieldsNames, CExtendedLogger Logger, CLanguage Lang ) {
	
		boolean bResult = false;
		
		try {
		
			if ( ResultSetToClone != null && ResultSetToClone.getMetaData() != null ) {

				ResultSetMetaData MetaData = ResultSetToClone.getMetaData(); 

				for ( int intIndexMetaData = 1; intIndexMetaData <= MetaData.getColumnCount(); intIndexMetaData++ ) {

					String strFieldName = MetaData.getColumnName( intIndexMetaData );

					if ( strFieldName == null || strFieldName.isEmpty() == true )
						strFieldName = MetaData.getColumnLabel( intIndexMetaData );
					
					if ( strFieldsNames == null || strFieldsNames.contains( strFieldName ) ) {
					
						CMemoryFieldData Field = this.getFieldByName( strFieldName );

						boolean bAddField = false;

						if ( Field != null ) { 

							if ( bAllowDuplicateNames == true ) {

								if ( DBEngine.getJavaSQLColumnType( MetaData.getColumnType( intIndexMetaData ), Logger, Lang ) != Field.intSQLType ) {

									bAddField = true;

								}

							}

						}
						else {

							bAddField = true;

						}

						if ( bAddField ) {

							int intSQLType = DBEngine.getJavaSQLColumnType( MetaData.getColumnType( intIndexMetaData ), Logger, Lang );
							String strSQLTypeName = DBEngine.getJavaSQLColumnTypeName( intSQLType, Logger, Lang );
							
							bResult = this.addField( strFieldName, intSQLType, strSQLTypeName, MetaData.getColumnDisplaySize( intIndexMetaData ), MetaData.getColumnLabel( intIndexMetaData ) );

						}

					}
					
				}
               
			}

		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}
		
		return bResult;
		
	}

	public boolean addMetaData( CMemoryRowSet MemoryResultSetToAdd, ArrayList<String> strFieldsNames, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		try {
			
			if ( MemoryResultSetToAdd != null ) {

				ArrayList<CMemoryFieldData> FieldsFrom = MemoryResultSetToAdd.getFieldsData();
				
				int intCountColumns = FieldsFrom.size(); 

				for ( int intIndexColumn = 1; intIndexColumn <= intCountColumns; intIndexColumn++ ) {

					CMemoryFieldData FieldFrom = FieldsFrom.get( intIndexColumn );
 
					if ( strFieldsNames == null || strFieldsNames.contains( FieldFrom.strName ) ) {
					
						CMemoryFieldData FieldTo = this.getFieldByNameAndType( FieldFrom.strName, FieldFrom.intSQLType );

						if ( FieldTo == null ) {

							this.addField( FieldFrom.strName, FieldFrom.intSQLType, FieldFrom.strSQLTypeName, FieldFrom.intLength, FieldFrom.strLabel );

							bResult = true;

						}
					
					}
					
				}
               
			}

		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}
		
		return bResult;
		
	}

	public boolean addRowData( ResultSet ResultSetToClone, CAbstractDBEngine DBEngine, CExtendedLogger Logger, CLanguage Lang ) {
	
		boolean bResult = true;
		
		try {
			
			if ( ResultSetToClone != null && ResultSetToClone.getMetaData() != null ) {

				ResultSetMetaData MetaData = ResultSetToClone.getMetaData(); 

				HashMap<String,CMemoryFieldData> CachedColumnsName = new HashMap<String,CMemoryFieldData>(); 
				
				for ( int intIndexColumn = 1; intIndexColumn <= MetaData.getColumnCount(); intIndexColumn++ ) {

					String strColumnName = MetaData.getColumnName( intIndexColumn );
					
					if ( strColumnName == null || strColumnName.isEmpty() == true )
						strColumnName = MetaData.getColumnLabel( intIndexColumn );

					CMemoryFieldData Field = this.getFieldByNameAndType( strColumnName, MetaData.getColumnType( intIndexColumn ) );

			        CachedColumnsName.put( strColumnName, Field );
					
				}

				while ( ResultSetToClone.next() ) {

					for ( int intIndexColumn = 1; intIndexColumn <= MetaData.getColumnCount(); intIndexColumn++ ) {

						String strColumnName = MetaData.getColumnName( intIndexColumn );
								
						if ( strColumnName == null || strColumnName.isEmpty() == true )
							strColumnName = MetaData.getColumnLabel( intIndexColumn );
						
						CMemoryFieldData Field = CachedColumnsName.get( strColumnName ); //this.getFieldByNameAndType( MetaData.getColumnName( intIndexColumn ), MetaData.getColumnType( intIndexColumn ) );

						if ( Field != null ) {

							Object FieldData = DBEngine.getFieldValueAsObject( Field.intSQLType, strColumnName, ResultSetToClone, Logger, Lang);
							
                       	    Field.addData( FieldData );
							
							/*
			    			switch ( Field.intSQLType ) {
			    			
								case Types.INTEGER: { Field.addData( ResultSetToClone.getInt( intIndexColumn ) ); break; }
								case Types.BIGINT: { Field.addData( ResultSetToClone.getLong( intIndexColumn ) ); break; }
								case Types.SMALLINT: { Field.addData( ResultSetToClone.getShort( intIndexColumn ) ); break; }
								case Types.VARCHAR: 
								case Types.CHAR: { Field.addData( ResultSetToClone.getString( intIndexColumn ) ); break; }
								case Types.BOOLEAN: { Field.addData( ResultSetToClone.getBoolean( intIndexColumn ) ); break; }
								case Types.BLOB: { 
									
					                                 /*if ( strEngineName.equals( "pgsql" ) ) {
						                
										                  java.sql.Blob BlobData = new SerialBlob( ResultSetToClone.getBytes( intIndexColumn ) );
					                	
					                                      Field.addData( BlobData );
					                	
					                                 }
					                                 else {*
									                 
					                                	 Field.addData( ResultSetToClone.getBlob( intIndexColumn ) );
					                                 
					                                 //};     
									                
									                 break; 
									                
									              }
								case Types.DATE: { Field.addData( ResultSetToClone.getDate( intIndexColumn ) ); break; }
								case Types.TIME: { Field.addData( ResultSetToClone.getTime( intIndexColumn ) ); break; }
								case Types.TIMESTAMP: { Field.addData( ResultSetToClone.getTimestamp( intIndexColumn ) ); break; }
								case Types.FLOAT: 
								case Types.DECIMAL: { Field.addData( ResultSetToClone.getFloat( intIndexColumn ) ); break; }
								case Types.DOUBLE: { Field.addData( ResultSetToClone.getDouble( intIndexColumn ) ); break; }
	
							}
 
					*/
					
						}

					}

				}

			}

		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );

			bResult = false;
			
		}

		return bResult;
		
	}
	
	public boolean addRowData( CMemoryRowSet MemoryResultSetToAdd, CExtendedLogger Logger, CLanguage Lang  ) {
		
		boolean bResult = false;
		
		try {
			
			if ( MemoryResultSetToAdd != null ) {

				ArrayList<CMemoryFieldData> FieldsFrom = MemoryResultSetToAdd.getFieldsData();
				
				int intCountColumns = FieldsFrom.size(); 

				for ( int intIndexColumn = 0; intIndexColumn < intCountColumns; intIndexColumn++ ) {

					CMemoryFieldData FieldFrom = FieldsFrom.get( intIndexColumn );
					
					CMemoryFieldData FieldTo = this.getFieldByNameAndType( FieldFrom.strName, FieldFrom.intSQLType );

					if ( FieldTo != null ) {

						ArrayList<Object> DataFrom = FieldFrom.getAllData(); 
						
						FieldTo.addAllData( DataFrom );
						
					}
					
				}
               
			}

		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}
		
		return bResult;
		
	}

	public boolean addField( String strName, int intSQLType, String strSQLName, int intLength, String strLabel ) {
		
		return this.addField( strName, intSQLType, strSQLName, intLength, strLabel, TFieldScope.IN );
		
	}

	public boolean addField( String strName, int intSQLType, String strSQLName, int intLength, String strLabel, TFieldScope Scope ) {
		
		if ( bAllowDuplicateNames == true || getFieldByName( strName ) == null ) {

			if ( getFieldByNameAndType( strName, intSQLType ) == null ) {
			
				CMemoryFieldData FieldData = new CMemoryFieldData( strName, intSQLType, strSQLName, intLength, strLabel );

				FieldData.Scope = Scope;
				
				FieldsData.add( FieldData );

				return true;
				
			}
			else {
				
				return false;
				
			}
			
		}
		else {
			
			return false;

		}
		
	}

	public boolean addLinkedField( CMemoryFieldData FieldData ) {
		
		if ( bAllowDuplicateNames == true || getFieldByName( FieldData.strName ) == null ) {

			if ( getFieldByNameAndType( FieldData.strName, FieldData.intSQLType ) == null ) {
			
				FieldsData.add( FieldData );

				return true;
				
			}
			else {
				
				return false;
				
			}
			
		}
		else {
			
			return false;

		}
		
	}
	
	public boolean addField( CMemoryFieldData FieldData, boolean bCopyRows ) {
		
		if ( bAllowDuplicateNames == true || getFieldByName( FieldData.strName ) == null ) {

			if ( getFieldByNameAndType( FieldData.strName, FieldData.intSQLType ) == null ) {
			
				CMemoryFieldData NewFieldData = new CMemoryFieldData( FieldData.strName, FieldData.intSQLType, FieldData.strSQLTypeName, FieldData.intLength, FieldData.strLabel );

				FieldsData.add( NewFieldData );

				if ( bCopyRows == true ) {
					
					NewFieldData.Data = new ArrayList<Object>( FieldData.Data );
					
				}
				
				return true;
				
			}
			else {
				
				return false;
				
			}
			
		}
		else {
			
			return false;

		}
		
	}
	
	public boolean addData( String strName, Object Data ) {
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null )  
		   Field.addData(Data);
		
		return Field != null;
		
	}
	
	public boolean addData( String strName, Object Data[] ) {
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null ) { 
		      
			for ( int intIndex = 0; intIndex < Data.length; intIndex++ ) {
			
				Field.addData( Data[ intIndex ] );
			
			}
			
		}   
		
		return Field != null;
		
	}
	
	public boolean addData( String strName, ArrayList<Object> Data ) {
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null ) { 
		      
			for ( int intIndex = 0; intIndex < Data.size(); intIndex++ ) {
			
				Field.addData( Data.get( intIndex ) );
			
			}
			
		}   
		
		return Field != null;
		
	}

	public void addRow( Object ... Data ) {
		
		for ( int intIndex = 0; intIndex < Data.length; intIndex++ ) {
		
			CMemoryFieldData Field = this.getFieldByIndex( intIndex );

			if ( Field != null ) { 

				Field.addData( Data[ intIndex ] );

			}   
		
		}
		
	}

	public boolean removeFieldsByName( String strName ) {
		
		ArrayList<CMemoryFieldData> FieldsToDelete = getFieldsByName( strName );
		
		for ( CMemoryFieldData Field: FieldsToDelete ) {
			
			FieldsData.remove( Field );
			
		}
		
		return FieldsToDelete.size() > 0;
		
	}

	public boolean removeFieldsByName( String strNames[] ) {
		
		boolean bResult = false;
		
		for ( int intIndex = 0; intIndex < strNames.length; intIndex++ ) {
		
			ArrayList<CMemoryFieldData> FieldsToDelete = getFieldsByName( strNames[ intIndex ] );

			for ( CMemoryFieldData Field: FieldsToDelete ) {

				FieldsData.remove( Field );

				bResult = true;
				
			}

		}
		
		return bResult;
		
	}

	public boolean removeFieldsByName( ArrayList<String> strNames ) {
		
		boolean bResult = false;
		
		for ( int intIndex = 0; intIndex < strNames.size(); intIndex++ ) {
		
			ArrayList<CMemoryFieldData> FieldsToDelete = getFieldsByName( strNames.get( intIndex ) );

			for ( CMemoryFieldData Field: FieldsToDelete ) {

				FieldsData.remove( Field );

				bResult = true;
				
			}

		}
		
		return bResult;
		
	}
	
	public boolean removeFieldsByNameAndType( String strName, int intSQLType ) {
		
		CMemoryFieldData FieldToDelete = getFieldByNameAndType( strName, intSQLType );
		
		if ( FieldToDelete != null )
		   FieldsData.remove( FieldToDelete );
		
		return FieldToDelete != null;
		
	}

	public boolean removeFieldsByNameAndType( String strNames[], int intSQLTypes[] ) {
		
		boolean bResult = false;
		
        if ( strNames.length == intSQLTypes.length ) {
		
        	for ( int intIndex = 0; intIndex < strNames.length; intIndex++ ) {

        		CMemoryFieldData FieldToDelete = getFieldByNameAndType( strNames[ intIndex ], intSQLTypes[ intIndex ] );

        		if ( FieldToDelete != null )
        			FieldsData.remove( FieldToDelete );

        	}
        
        }
        
		return bResult;
		
	}

	public boolean removeFieldsByName( ArrayList<String> strNames, ArrayList<Integer> intSQLTypes ) {
		
		boolean bResult = false;
		
        if ( strNames.size() == intSQLTypes.size() ) {
    		
        	for ( int intIndex = 0; intIndex < strNames.size(); intIndex++ ) {

        		CMemoryFieldData FieldToDelete = getFieldByNameAndType( strNames.get( intIndex ), intSQLTypes.get( intIndex ) );

        		if ( FieldToDelete != null )
        			FieldsData.remove( FieldToDelete );

        	}
        
        }
		
		return bResult;
		
	}
	
	public void filterFields( ArrayList<String> strNames ) {
		
		if ( strNames.size() > 0 ) {  

			ArrayList<CMemoryFieldData> FilteredFields = new ArrayList<CMemoryFieldData>(); 

			for ( String strFieldName : strNames ) {

				CMemoryFieldData Field = this.getFieldByName( strFieldName );

				if ( Field != null ) {

					FilteredFields.add( Field );

				}

			}

			if ( FieldsData.size() != FilteredFields.size() )
			    FieldsData = FilteredFields;

		}   
		
	}

	public boolean setData( String strName, int intRow, Object Data ) {
		
		boolean bResult = false;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null )  
		   bResult = Field.setData( intRow, Data );
		
		return bResult;
		
	}
	
	public void setAllData( String strName, ArrayList<Object> Data ) {

		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null )  
		   Field.setAllData( Data );
		
	}

	public void setAllData( String strName, Object Data ) {

		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null )  
		   Field.setAllData( Data );
		
	}
	
	public void addAllData( String strName, ArrayList<Object> Data ) {

		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null )  
		   Field.addAllData( Data );
		
	}

	public Object getData( String strName, int intRow ) {
		
		Object Result = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null )  
		   Result = Field.getData( intRow );
		
		return Result;
		
	}

	public Integer getDataAsInteger( String strName, int intRow ) {
		
		Integer intResult = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   intResult = (Integer) Field.getData( intRow );
		
		return intResult;
		
	}
	
	public Long getDataAsLong( String strName, int intRow ) {
		
		Long intResult = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   intResult = (Long) Field.getData( intRow );
		
		return intResult;
		
	}
	
	public Date getDataAsDate( String strName, int intRow ) {
		
		Date Result = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Date) Field.getData( intRow );
		
		return Result;
		
	}

	public Time getDataAsTime( String strName, int intRow ) {
		
		Time Result = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Time) Field.getData( intRow );
		
		return Result;
		
	}

	public String getDataAsString( String strName, int intRow ) {
		
		String strResult = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   strResult = (String) Field.getData( intRow );
		
		return strResult;
		
	}

	public Blob getDataAsBlob( String strName, int intRow ) {
		
		Blob Result = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Blob) Field.getData( intRow );
		
		return Result;
		
	}

	public Float getDataAsFloat( String strName, int intRow ) {
		
		Float Result = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Float) Field.getData( intRow );
		
		return Result;
		
	}

	public Boolean getDataAsBoolean( String strName, int intRow ) {
		
		Boolean Result = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Boolean) Field.getData( intRow );
		
		return Result;
		
	}

	public Double getDataAsDouble( String strName, int intRow ) {
		
		Double Result = null;
		
		CMemoryFieldData Field = getFieldByName( strName );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Double) Field.getData( intRow );
		
		return Result;
		
	}

	public Object getData( int intFieldIndex, int intRow ) {
		
		Object Result = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null )  
		   Result = Field.getData( intRow );
		
		return Result;
		
	}

	public Integer getDataAsInteger( int intFieldIndex, int intRow ) {
		
		Integer intResult = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   intResult = (Integer) Field.getData( intRow );
		
		return intResult;
		
	}
	
	public Long getDataAsLong( int intFieldIndex, int intRow ) {
		
		Long intResult = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   intResult = (Long) Field.getData( intRow );
		
		return intResult;
		
	}
	
	public Date getDataAsDate( int intFieldIndex, int intRow ) {
		
		Date Result = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Date) Field.getData( intRow );
		
		return Result;
		
	}

	public Time getDataAsTime( int intFieldIndex, int intRow ) {
		
		Time Result = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Time) Field.getData( intRow );
		
		return Result;
		
	}

	public String getDataAsString( int intFieldIndex, int intRow ) {
		
		String strResult = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   strResult = (String) Field.getData( intRow );
		
		return strResult;
		
	}

	public Blob getDataAsBlob( int intFieldIndex, int intRow ) {
		
		Blob Result = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Blob) Field.getData( intRow );
		
		return Result;
		
	}

	public Float getDataAsFloat( int intFieldIndex, int intRow ) {
		
		Float Result = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Float) Field.getData( intRow );
		
		return Result;
		
	}

	public Boolean getDataAsBoolean( int intFieldIndex, int intRow ) {
		
		Boolean Result = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Boolean) Field.getData( intRow );
		
		return Result;
		
	}

	public Double getDataAsDouble( int intFieldIndex, int intRow ) {
		
		Double Result = null;
		
		CMemoryFieldData Field = getFieldByIndex( intFieldIndex );
		
		if ( Field != null && Field.getData( intRow ) != null )  
		   Result = (Double) Field.getData( intRow );
		
		return Result;
		
	}
	
	public ArrayList<Object> getRowDataAsArrayList( int intRow ) {
		
		ArrayList<Object> Result = new ArrayList<Object>();
		
		for ( CMemoryFieldData Field: FieldsData ) {

			Result.add( Field.Data );
			
		}
		
		return Result;
		
	}

	public Object[] getRowDataAsArray( int intRow ) {
		
		ArrayList<Object> Result = new ArrayList<Object>();
		
		for ( CMemoryFieldData Field: FieldsData ) {

			Result.add( Field.Data );
			
		}
		
		return Result.toArray();
		
	}

	public ArrayList<Integer> getRowFieldTypeAsArrayList( int intRow ) {
		
		ArrayList<Integer> Result = new ArrayList<Integer>();
		
		for ( CMemoryFieldData Field: FieldsData ) {

			Result.add( Field.intSQLType );
			
		}
		
		return Result;
		
	}

	public int[] getRowFieldTypeAsArray( int intRow ) {
		
		int[] Result = new int[ FieldsData.size() ];
		
		int intIndex = 0;
		
		for ( CMemoryFieldData Field: FieldsData ) {

			Result[ intIndex ] = Field.intSQLType ;
			
			intIndex += 1;
			
		}
		
		return Result;
		
	}

	public ArrayList<String> getRowFieldNameAsArrayList( int intRow ) {
		
		ArrayList<String> Result = new ArrayList<String>();
		
		for ( CMemoryFieldData Field: FieldsData ) {

			Result.add( Field.strName );
			
		}
		
		return Result;
		
	}

	public String[] getRowFieldNameAsArray( int intRow ) {
		
		String[] Result = new String[ FieldsData.size() ];
		
		int intIndex = 0;
		
		for ( CMemoryFieldData Field: FieldsData ) {

			Result[ intIndex ] = Field.strName;
			
			intIndex += 1;
			
		}
		
		return Result;
		
	}

	public ArrayList<CMemoryFieldData> getFieldsByName( String strName ) {
		
		ArrayList<CMemoryFieldData> Result = new ArrayList<CMemoryFieldData>();
		
		strName = strName.toLowerCase();
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( Field.strName.toLowerCase() == strName ) {

				Result.add( Field );
				
			}
			
		}
		
		return Result;
		
	}

	public ArrayList<Integer> getFieldsIndexByName( String strName ) {
		
		ArrayList<Integer> Result = new ArrayList<Integer>();
		
		strName = strName.toLowerCase();
		
		int intFieldIndex = 1;
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( Field.strName.toLowerCase() == strName ) {

				Result.add( intFieldIndex );
				
			}
			
			intFieldIndex += 1;
		}
		
		return Result;
		
	}

	public CMemoryFieldData getFieldByName( String strName ) {
		
		CMemoryFieldData Result = null;
		
		strName = strName.toLowerCase();
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( Field.strName.toLowerCase().equals( strName ) ) {

				Result = Field;
				break;
				
			}
			
		}
		
		return Result;
		
	}

	public CMemoryFieldData getFieldByNameAndType( String strName, int intSQLType ) {
		
		CMemoryFieldData Result = null;
		
		strName = strName.toLowerCase();
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( Field.strName.toLowerCase().equals( strName ) && Field.intSQLType == intSQLType ) {

				Result = Field;
				break;
				
			}
			
		}
		
		return Result;
		
	}
	
	public CMemoryFieldData getFieldByIndex( int intIndex ) {
		
		CMemoryFieldData Result = null;
		
		int intIndexField = 0;
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( intIndexField == intIndex ) {

				Result = Field;
				break;
				
			}
			
			intIndexField += 1;
						
		}
		
		return Result;
		
	}
	
	public int getFieldIndexByName( String strName ) {
		
		int intResult = 0;
		
		strName = strName.toLowerCase();
		
		int intFieldIndex = 1;
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( Field.strName.toLowerCase() == strName ) {

				intResult = intFieldIndex;
				break;
				
			}
			
		}
		
		return intResult;
		
	}

	public int getFieldsCount() {
		
		return FieldsData.size();

	}

	public ArrayList<CMemoryFieldData> getFieldsData() {
		
		return FieldsData;
		
	}
	
	public int getRowCount() {
		
		int intResult = 0;
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( intResult < Field.Data.size() ) {
				
				intResult = Field.Data.size();
				
			}
			
		}
		
		return intResult;
		
	}

	public boolean isFieldsRowCountDistinct() {
	
		if ( FieldsData.size() > 1 ) {

			int intResult = FieldsData.get( 0 ).Data.size();

			for ( CMemoryFieldData Field: FieldsData ) {

				if ( intResult != Field.Data.size() ) {

					intResult = Field.Data.size();
					return true;

				}

			}


		}

		return false;
		
	}
	
	public void NormalizeRowCount() {
		
		int intRowCount = 0;
		
		intRowCount = this.getRowCount();
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( Field.Data.size() < intRowCount ) {
				
				Field.addNulls( intRowCount - Field.Data.size() );
				
			}
			
		}
		
	}
	
	public void NormalizeRowCount( LinkedHashMap<String,Object> DefaultFieldValues ) {
		
		int intRowCount = 0;
		
		intRowCount = this.getRowCount();
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( Field.Data.size() < intRowCount ) {

				Object DefaultFieldValue = DefaultFieldValues.get( Field.strName );

				if ( DefaultFieldValue == null ) {

					Field.addNulls( intRowCount - Field.Data.size() );

				}
				else {

					Field.addData( DefaultFieldValue, intRowCount - Field.Data.size() );

				}

			}
			
		}
		
	}
	
	/*public boolean setFieldDataToPreparedStatement( CNamedPreparedStatement NamedPreparedStatement, String strPreparedStatementFieldName, String strFieldName, int intIndexRow, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		CMemoryFieldData MemoryField = this.getFieldByName( strFieldName );
		
		if ( MemoryField != null ) {
			
			bResult = MemoryField.setFieldDataToPreparedStatement( NamedPreparedStatement, strPreparedStatementFieldName, intIndexRow, bUseLastRowIfRowNotExits, Logger, Lang );
			
		}
		
		return bResult;
		
	}
	
	/*public boolean setFieldDataToCallableStatement( CNamedCallableStatement NamedCallableStatement, String strCallableStatementFieldName, String strFieldName, int intIndexRow, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		CMemoryFieldData MemoryField = this.getFieldByName( strFieldName );
		
		if ( MemoryField != null ) {
			
			bResult = MemoryField.setFieldDataToCallableStatement( NamedCallableStatement, strCallableStatementFieldName, intIndexRow, bUseLastRowIfRowNotExits, Logger, Lang );
			
		}
		
		return bResult;
		
	}*/
	
	public ArrayList<String> RowToString( int intIndexRow, boolean bVarCharQuoted, String strDateFormat, String strTimeFormat, String strDateTimeFormat, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {
		
		ArrayList<String> Result = new ArrayList<String>();
		
		for ( CMemoryFieldData MemoryField: FieldsData ) {
			
			if ( MemoryField.Scope.equals( TFieldScope.IN ) || MemoryField.Scope.equals( TFieldScope.INOUT ) ) {
			
				if ( bVarCharQuoted == true && ( MemoryField.intSQLType == Types.VARCHAR || MemoryField.intSQLType == Types.CHAR ) ) {

					if ( MemoryField.DataIsNull( intIndexRow ) == false )
						Result.add( "\'" + MemoryField.FieldValueToString( intIndexRow, true, strDateFormat, strTimeFormat, strDateTimeFormat, bUseLastRowIfRowNotExits, Logger, Lang ) + "\'" );
					else
						Result.add( MemoryField.FieldValueToString( intIndexRow, true, strDateFormat, strTimeFormat, strDateTimeFormat, bUseLastRowIfRowNotExits, Logger, Lang ) );

				}	
				else	
					Result.add( MemoryField.FieldValueToString( intIndexRow, true, strDateFormat, strTimeFormat, strDateTimeFormat, bUseLastRowIfRowNotExits, Logger, Lang ) );
			
			}
			
		}
		
		return Result;
		
	}

	public String RowToString( int intIndexRow, boolean bVarCharQuoted, String strFieldSeparator, String strDateFormat, String strTimeFormat, String strDateTimeFormat, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {
		
		String strResult = "";
		
		for ( CMemoryFieldData MemoryField: FieldsData ) {
			
			if ( bVarCharQuoted == true && ( MemoryField.intSQLType == Types.VARCHAR || MemoryField.intSQLType == Types.CHAR ) ) {
			
				if ( strResult.isEmpty() == false )
					strResult = strResult + strFieldSeparator + "\'" + MemoryField.FieldValueToString( intIndexRow, true, strDateFormat, strTimeFormat, strDateTimeFormat, bUseLastRowIfRowNotExits, Logger, Lang ) + "\'";
				else
					strResult =  strFieldSeparator + "\'" + MemoryField.FieldValueToString( intIndexRow, true, strDateFormat, strTimeFormat, strDateTimeFormat, bUseLastRowIfRowNotExits, Logger, Lang ) + "\'";

			}
			else {
				
				if ( strResult.isEmpty() == false )
					strResult = strResult + strFieldSeparator + MemoryField.FieldValueToString( intIndexRow, true, strDateFormat, strTimeFormat, strDateTimeFormat, bUseLastRowIfRowNotExits, Logger, Lang );
				else
					strResult =  strFieldSeparator + MemoryField.FieldValueToString( intIndexRow, true, strDateFormat, strTimeFormat, strDateTimeFormat, bUseLastRowIfRowNotExits, Logger, Lang );
				
			}
			
		}
		
		return strResult;
		
	}
	
	public String FieldValueToString( String strName, int intIndexRow, boolean bResumeBlob, String strDateFormat, String strTimeFormat, String strDateTimeFormat, boolean bUseLastRowIfRowNotExits, CExtendedLogger Logger, CLanguage Lang ) {

		CMemoryFieldData MemoryFieldData = this.getFieldByName( strName );
		
		if ( MemoryFieldData != null ) {
			
			return MemoryFieldData.FieldValueToString(intIndexRow, bResumeBlob, strDateFormat, strTimeFormat, strDateTimeFormat, bUseLastRowIfRowNotExits, Logger, Lang );
			
		}
			
		return null;
		
	}
	
	public boolean ContainsFieldsScopeOut() {
		
		for ( CMemoryFieldData Field: FieldsData ) {

			if ( Field.Scope.equals( TFieldScope.OUT ) || Field.Scope.equals( TFieldScope.INOUT ) ) {

				return true;
				
			}
			
		}
		
		return false;
		
	}
	
	/*public CachedRowSet BuildCachedRowSetFromFieldsScopeOut( CNamedCallableStatement NamedCallableStatement, CExtendedLogger Logger, CLanguage Lang ) {
		
		CachedRowSet Result = null;
		
		try {
			
			CMemoryRowSet TmpMemoryRowSet = new CMemoryRowSet( false );
			
			for ( CMemoryFieldData Field: FieldsData ) {

				if ( Field.Scope.equals( TFieldScope.OUT ) || Field.Scope.equals( TFieldScope.INOUT ) ) {

					TmpMemoryRowSet.addField( Field, false );
					
				}
				
			}

			for ( CMemoryFieldData Field: TmpMemoryRowSet.FieldsData ) {

				String strFieldName =  Field.strName;
				
				if ( strFieldName == null || strFieldName.isEmpty() )
					strFieldName =  Field.strLabel;
				
    			switch ( Field.intSQLType ) {
    			
					case Types.INTEGER: { Field.addData( NamedCallableStatement.getInt( strFieldName ) ); break; }
					case Types.BIGINT: { Field.addData( NamedCallableStatement.getLong( strFieldName ) ); break; }
					case Types.SMALLINT: { Field.addData( NamedCallableStatement.getShort( strFieldName ) ); break; }
					case Types.VARCHAR: 
					case Types.CHAR: { Field.addData( NamedCallableStatement.getString( strFieldName ) ); break; }
					case Types.BOOLEAN: { Field.addData( NamedCallableStatement.getBoolean( strFieldName ) ); break; }
					case Types.BLOB: { 
						
						                /*if ( intBlobType == 1 ) {
						                
											java.sql.Blob BlobData = new SerialBlob( NamedCallableStatement.getBytes( strFieldName ) );
						                	
						                    Field.addData( BlobData );
						                	
						                }
						                else* 
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
		
	}*/
	
}
