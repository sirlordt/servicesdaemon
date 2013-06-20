package AbstractResponseFormat;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.WebRowSet;

import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.WebRowSetImpl;

import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultSetResult;
import CommonClasses.NamesSQLTypes;

public class CJavaXMLWebRowSetResponseFormat extends CAbstractResponseFormat {

	public CJavaXMLWebRowSetResponseFormat() {

		this.strName = JavaXMLWebRowSetTags._ResponseFormat_JAVA_XML_WEBROWSET;
		strMinVersion = "1.0";
		strMaxVersion = "1.0";
		
	}
	
	@Override
	public CAbstractResponseFormat getNewInstance() {

		CJavaXMLWebRowSetResponseFormat NewInstance = new CJavaXMLWebRowSetResponseFormat();
    	
    	NewInstance.InitResponseFormat( this.ServicesDaemonConfig, this.OwnerConfig );
    	
    	return NewInstance;
	
	}

	@Override
	public String getContentType() {

    	return OwnerConfig.getConfigValue( "strJavaXML_WebRowSet_ContentType" );
	
	}

	@Override
	public String getCharacterEncoding() {

    	return OwnerConfig.getConfigValue( "strJavaXML_WebRowSet_CharSet" );
	
	}

    public void DescribeService( CAbstractService Service, CachedRowSet CachedRowset ) {
    	
    	if ( Service.getHiddenService() == false ) {
    		
    		String strServiceType = "ReadWrite";
    		
	        if ( Service.getServiceType() == 1 )
	           strServiceType = "Read";
	        else if ( Service.getServiceType() == 2 )
	           strServiceType = "Write";
    		
        	HashMap< String, ArrayList< CInputServiceParameter > > GroupsInputParametersService = Service.getGroupsInputParametersService();   

	        Iterator< Entry< String, ArrayList< CInputServiceParameter > > > It = GroupsInputParametersService.entrySet().iterator();

	        OwnerConfig.Logger.LogMessage( "1", OwnerConfig.Lang.Translate( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
	        
            while ( It.hasNext() ) {
	        
            	try {

            		Entry< String,ArrayList<CInputServiceParameter> > GroupIPSEntry = It.next();

        			String strKey = GroupIPSEntry.getKey();
            		
            		for ( CInputServiceParameter InputServiceParameter : GroupIPSEntry.getValue() ) {

                		CachedRowset.moveToInsertRow();

                		if ( Service.getAuthRequired() == true )
                			CachedRowset.updateString( 1, "yes" );
                		else
                			CachedRowset.updateString( 1, "no" );

            			CachedRowset.updateString( 2, Service.getServiceName() );
                		
            			CachedRowset.updateString( 3, strServiceType );
                		
            			CachedRowset.updateString( 4, Service.getServiceDescription() );

            			CachedRowset.updateString( 5, Service.getServiceAuthor() );

            			CachedRowset.updateString( 6, Service.getServiceAuthorContact() );
            			
            			CachedRowset.updateString( 7, strKey );

            			CachedRowset.updateString( 8, InputServiceParameter.getParameterName() );

            			CachedRowset.updateString( 9, InputServiceParameter.getParameterRequired()?"Yes":"No" );

            			CachedRowset.updateString( 10, InputServiceParameter.getParameterDataType() );

            			CachedRowset.updateInt( 11, Integer.parseInt( InputServiceParameter.getParameterDataTypeWidth() ) );
            			
            			CachedRowset.updateString( 12, InputServiceParameter.getParameterScope().toString() );

            			CachedRowset.updateString( 13, InputServiceParameter.getParameterDescription() );
            			
            			CachedRowset.insertRow();
            			
            		}
            	
            	}
            	catch ( Exception Ex ) {
            		
                	OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );
            		
            	}
            	
            } 	
    		
    	}
    	
    }
	
	@Override
	public String EnumerateServices( HashMap<String, CAbstractService> RegisteredServices, String strVersion ) {

		String strResult = "";
		
		try {

			if ( strVersion.equals( "1.0" ) ) {
			
				RowSetMetaData RowsetMetaData = new RowSetMetaDataImpl();
				RowsetMetaData.setColumnCount( 13 );

				RowsetMetaData.setColumnName( 1, JavaXMLWebRowSetTags._XML_StructAuthRequired );
				RowsetMetaData.setColumnLabel( 1, JavaXMLWebRowSetTags._XML_StructAuthRequired );
				RowsetMetaData.setColumnType( 1, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 1, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 1, JavaXMLWebRowSetTags._XML_StructAuthorLength );

				RowsetMetaData.setColumnName( 2, JavaXMLWebRowSetTags._XML_StructServiceName );
				RowsetMetaData.setColumnLabel( 2, JavaXMLWebRowSetTags._XML_StructServiceName );
				RowsetMetaData.setColumnType( 2, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 2, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 2, JavaXMLWebRowSetTags._XML_StructServiceNameLength );

				RowsetMetaData.setColumnName( 3, JavaXMLWebRowSetTags._XML_StructAccessType );
				RowsetMetaData.setColumnLabel( 3, JavaXMLWebRowSetTags._XML_StructAccessType );
				RowsetMetaData.setColumnType( 3, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 3, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 3, JavaXMLWebRowSetTags._XML_StructAccessTypeLength );

				RowsetMetaData.setColumnName( 4, JavaXMLWebRowSetTags._XML_StructDescription );
				RowsetMetaData.setColumnLabel( 4, JavaXMLWebRowSetTags._XML_StructDescription );
				RowsetMetaData.setColumnType( 4, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 4, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 4, JavaXMLWebRowSetTags._XML_StructDescriptionLength );

				RowsetMetaData.setColumnName( 5, JavaXMLWebRowSetTags._XML_StructAuthor );
				RowsetMetaData.setColumnLabel( 5, JavaXMLWebRowSetTags._XML_StructAuthor );
				RowsetMetaData.setColumnType( 5, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 5, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 5, JavaXMLWebRowSetTags._XML_StructAuthorLength );

				RowsetMetaData.setColumnName( 6, JavaXMLWebRowSetTags._XML_StructAuthorContact );
				RowsetMetaData.setColumnLabel( 6, JavaXMLWebRowSetTags._XML_StructAuthorContact );
				RowsetMetaData.setColumnType( 6, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 6, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 6, JavaXMLWebRowSetTags._XML_StructAuthorContactLength );

				RowsetMetaData.setColumnName( 7, JavaXMLWebRowSetTags._XML_StructParamSetName );
				RowsetMetaData.setColumnLabel( 7, JavaXMLWebRowSetTags._XML_StructParamSetName );
				RowsetMetaData.setColumnType( 7, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 7, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 7, JavaXMLWebRowSetTags._XML_StructParamSetNameLength );

				RowsetMetaData.setColumnName( 8, JavaXMLWebRowSetTags._XML_StructParamName );
				RowsetMetaData.setColumnLabel( 8, JavaXMLWebRowSetTags._XML_StructParamName );
				RowsetMetaData.setColumnType( 8, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 8, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 7, JavaXMLWebRowSetTags._XML_StructParamNameLength );

				RowsetMetaData.setColumnName( 9, JavaXMLWebRowSetTags._XML_StructRequired );
				RowsetMetaData.setColumnLabel( 9, JavaXMLWebRowSetTags._XML_StructRequired );
				RowsetMetaData.setColumnType( 9, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 9, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 9, JavaXMLWebRowSetTags._XML_StructRequiredLength );

				RowsetMetaData.setColumnName( 10, JavaXMLWebRowSetTags._XML_StructType );
				RowsetMetaData.setColumnLabel( 10, JavaXMLWebRowSetTags._XML_StructType );
				RowsetMetaData.setColumnType( 10, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 10, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 10, JavaXMLWebRowSetTags._XML_StructTypeLength );

				RowsetMetaData.setColumnName( 11, JavaXMLWebRowSetTags._XML_StructTypeWidth );
				RowsetMetaData.setColumnLabel( 11, JavaXMLWebRowSetTags._XML_StructTypeWidth );
				RowsetMetaData.setColumnType( 11, Types.SMALLINT );
				RowsetMetaData.setColumnTypeName( 11, NamesSQLTypes._SMALLINT );

				RowsetMetaData.setColumnName( 12, JavaXMLWebRowSetTags._XML_StructSubType );
				RowsetMetaData.setColumnLabel( 12, JavaXMLWebRowSetTags._XML_StructSubType );
				RowsetMetaData.setColumnType( 12, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 12, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 12, JavaXMLWebRowSetTags._XML_StructSubTypeLength );

				RowsetMetaData.setColumnName( 13, JavaXMLWebRowSetTags._XML_StructParamDescription );
				RowsetMetaData.setColumnLabel( 13, JavaXMLWebRowSetTags._XML_StructParamDescription );
				RowsetMetaData.setColumnType( 13, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( 13, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( 13, JavaXMLWebRowSetTags._XML_StructParamDescriptionLength );

				CachedRowSet CachedRowset = new CachedRowSetImpl();
				CachedRowset.setMetaData( RowsetMetaData );				

				Iterator<Entry<String, CAbstractService>> it = RegisteredServices.entrySet().iterator();

				while ( it.hasNext() ) {

					Entry<String, CAbstractService> Pairs = it.next();

					String strServName = (String) Pairs.getKey();

					CAbstractService Service = RegisteredServices.get( strServName );

					if ( Service != null )
						this.DescribeService( Service, CachedRowset );

				}

    			CachedRowset.moveToCurrentRow();
				
				CachedRowset.beforeFirst();
				
				WebRowSet wrs = new WebRowSetImpl();

				StringWriter sw = new StringWriter();

				wrs.writeXml( CachedRowset, sw );

				strResult = sw.toString();

				wrs.close();
			
			}
			else {
				
	        	OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
				
			}
			
		}
		catch ( Exception Ex ) {
			
        	OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );
        	
		}
		
		return strResult;
	
	}

	@Override
	public String FormatResultSet( ResultSet ResultSet, String strVersion ) {

		String strResult = "";

		try {

			if ( strVersion.equals( "1.0" ) ) {

				WebRowSet wrs = new WebRowSetImpl();

				StringWriter sw = new StringWriter();

				wrs.writeXml( ResultSet, sw );

				strResult = sw.toString();

				wrs.close();

			}
			else {
				
	        	OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
				
			}
		
		}
		catch ( Exception Ex ) {

			OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;
		
	}

	@Override
	public String FormatResultsSets( ArrayList<ResultSet> ResultsSets, String strVersion ) {

		String strResult = "";

		try {

			if ( strVersion.equals( "1.0" ) ) {

				WebRowSet wrs = new WebRowSetImpl();

				StringWriter sw = new StringWriter();

				wrs.writeXml( ResultsSets.get( 0 ), sw );

				String strFirstResultSetXML = sw.toString();
				
				if ( ResultsSets.size() > 1 ) {
				
					strFirstResultSetXML = strFirstResultSetXML.substring( 0, strFirstResultSetXML.length() - 24 ); //</data>\n</webRowSet>\n 
					
					sw.flush();
					
					StringBuilder strBuffer = new StringBuilder();
					
					for ( int intIndexResultSet = 1; intIndexResultSet < ResultsSets.size(); intIndexResultSet++ ) {

						wrs.writeXml( ResultsSets.get( intIndexResultSet ), sw );

						String strTmpResultSetXML = sw.toString();

						strTmpResultSetXML = strTmpResultSetXML.substring( strTmpResultSetXML.indexOf( "<currentRow>" ),  strTmpResultSetXML.length() );

						strTmpResultSetXML = strTmpResultSetXML.substring( 0, strTmpResultSetXML.length() - 24 );
						
						strBuffer.append( strTmpResultSetXML );
						
						sw.flush();

					}
					
					strResult = strFirstResultSetXML + strBuffer.toString() + "</data>\n</webRowSet>\n";

				}
				else {
					
					strResult = strFirstResultSetXML;
					
				}
				
				wrs.close();

			}
			else {
				
	        	OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
				
			}
		
		}
		catch ( Exception Ex ) {

			OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;
		
	}

	@Override
	public String FormatResultsSets( ArrayList<CResultSetResult> ResultsSetsList, String strVersion, int intDummyParam ) {
    	
    	String strResult = "";
    	
        try {

        	if ( ResultsSetsList.size() > 0 ) {

        		ResultSet ResultSet = CResultSetResult.getFirstResultSetNotNull( ResultsSetsList );

        		CMemoryRowSet MemoryRowSet = new CMemoryRowSet( false );
        		
        		if ( ResultSet != null ) {
        			
        			MemoryRowSet.cloneOnlyMetaData( ResultSet, null );
        			MemoryRowSet.addField( JavaXMLWebRowSetTags._XML_StructSQLOperationCode, Types.INTEGER, NamesSQLTypes._INTEGER, 0, JavaXMLWebRowSetTags._XML_StructSQLOperationCode );
        			MemoryRowSet.addField( JavaXMLWebRowSetTags._XML_StructSQLOperationDescription, Types.VARCHAR, NamesSQLTypes._VARCHAR, JavaXMLWebRowSetTags._XML_StructSQLOperationDescriptionLength, JavaXMLWebRowSetTags._XML_StructSQLOperationDescription );
        			
        			LinkedHashMap<String,Object> DefaultFieldValues = new LinkedHashMap<String,Object>();
        			
        			for ( CResultSetResult ResultSetResultToAdd: ResultsSetsList ) {    

        				if ( ResultSetResultToAdd.Result != null && ResultSetResultToAdd.intCode >= 0 ) {   
        				
        					DefaultFieldValues.clear();
        					
                			DefaultFieldValues.put( JavaXMLWebRowSetTags._XML_StructSQLOperationCode, ResultSetResultToAdd.intCode );
                			DefaultFieldValues.put( JavaXMLWebRowSetTags._XML_StructSQLOperationDescription, ResultSetResultToAdd.strDescription );

                			MemoryRowSet.addRowData( ResultSet );
        					MemoryRowSet.NormalizeRowCount( DefaultFieldValues ); //add null to code and description field values

        				}
        				else {
        					
            				MemoryRowSet.addData( JavaXMLWebRowSetTags._XML_StructSQLOperationCode, ResultSetResultToAdd.intCode );
            				MemoryRowSet.addData( JavaXMLWebRowSetTags._XML_StructSQLOperationDescription, ResultSetResultToAdd.strDescription );
        					MemoryRowSet.NormalizeRowCount(); //add null to another fields values
        					
        				}

        			}
		        
        		}
        		else {
        			
        			MemoryRowSet.addField( JavaXMLWebRowSetTags._XML_StructSQLOperationCode, Types.INTEGER, NamesSQLTypes._INTEGER, 0, JavaXMLWebRowSetTags._XML_StructSQLOperationCode );
        			MemoryRowSet.addField( JavaXMLWebRowSetTags._XML_StructSQLOperationDescription, Types.VARCHAR, NamesSQLTypes._VARCHAR, JavaXMLWebRowSetTags._XML_StructSQLOperationDescriptionLength, JavaXMLWebRowSetTags._XML_StructSQLOperationDescription );
        			
        			for ( CResultSetResult ResultSetResultToAdd: ResultsSetsList ) {    

        				MemoryRowSet.addData( JavaXMLWebRowSetTags._XML_StructSQLOperationCode, ResultSetResultToAdd.intCode );
        				MemoryRowSet.addData( JavaXMLWebRowSetTags._XML_StructSQLOperationDescription, ResultSetResultToAdd.strDescription );
        			
        			}
        			
        		}
		        
        		CachedRowSet CachedRowset = MemoryRowSet.createCachedRowSet();
        		
				WebRowSet wrs = new WebRowSetImpl();

				StringWriter sw = new StringWriter();

				wrs.writeXml( CachedRowset, sw );

				strResult = sw.toString();

				wrs.close();

        	}
	            
        }
        catch ( Exception Ex ) {

        	OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
    	
    	return strResult;

	}

	@Override
	public String FormatMemoryRowSet( CMemoryRowSet MemoryRowSet, String strVersion ) {
		
		String strResult = "";

		try {

			if ( strVersion.equals( "1.0" ) ) {

				WebRowSet wrs = new WebRowSetImpl();

				StringWriter sw = new StringWriter();
				
				wrs.writeXml( MemoryRowSet.createCachedRowSet(), sw );

				strResult = sw.toString();

				wrs.close();

			}
			else {
				
	        	OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
				
			}
		
		}
		catch ( Exception Ex ) {

			OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;

	}

	@Override
	public String FormatMemoryRowSets( ArrayList<CMemoryRowSet> MemoryRowSets, String strVersion ) {

		String strResult = "";

		try {

			if ( strVersion.equals( "1.0" ) ) {

				WebRowSet wrs = new WebRowSetImpl();

				StringWriter sw = new StringWriter();

				wrs.writeXml( MemoryRowSets.get( 0 ).createCachedRowSet(), sw );

				String strFirstResultSetXML = sw.toString();
				
				if ( MemoryRowSets.size() > 1 ) {
				
					strFirstResultSetXML = strFirstResultSetXML.substring( 0, strFirstResultSetXML.length() - 24 ); //</data>\n</webRowSet>\n 
					
					sw.flush();
					
					StringBuilder strBuffer = new StringBuilder();
					
					for ( int intIndexResultSet = 1; intIndexResultSet < MemoryRowSets.size(); intIndexResultSet++ ) {

						wrs.writeXml( MemoryRowSets.get( intIndexResultSet ).createCachedRowSet(), sw );

						String strTmpResultSetXML = sw.toString();

						strTmpResultSetXML = strTmpResultSetXML.substring( strTmpResultSetXML.indexOf( "<currentRow>" ),  strTmpResultSetXML.length() );

						strTmpResultSetXML = strTmpResultSetXML.substring( 0, strTmpResultSetXML.length() - 24 );
						
						strBuffer.append( strTmpResultSetXML );
						
						sw.flush();

					}
					
					strResult = strFirstResultSetXML + strBuffer.toString() + "</data>\n</webRowSet>\n";

				}
				else {
					
					strResult = strFirstResultSetXML;
					
				}
				
				wrs.close();

			}
			else {
				
	        	OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
				
			}
		
		}
		catch ( Exception Ex ) {

			OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;
	
	}

	@Override
	public String FormatSimpleMessage( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion ) {

		String strResult = "";
		
		try {

			if ( strVersion.equals( "1.0" ) ) {
			
				int intCountFileds = 2;
				
				if ( strSecurityToken != null && strSecurityToken.trim().isEmpty() == false ) {
					
					intCountFileds += 1;
					
				}
				
				if ( strTransactionID != null && strTransactionID.trim().isEmpty() == false ) {
					
					intCountFileds += 1;
					
				}
				
				RowSetMetaData RowsetMetaData = new RowSetMetaDataImpl();
				RowsetMetaData.setColumnCount( intCountFileds );

				int intFieldIndex = 1;
				
				if ( strSecurityToken != null && strSecurityToken.trim().isEmpty() == false ) {
					
					RowsetMetaData.setColumnName( intFieldIndex, JavaXMLWebRowSetTags._XML_StructSecurityToken );
					RowsetMetaData.setColumnLabel( intFieldIndex, JavaXMLWebRowSetTags._XML_StructSecurityToken );
					RowsetMetaData.setColumnType( intFieldIndex, Types.BIGINT );
					RowsetMetaData.setColumnTypeName( intFieldIndex, NamesSQLTypes._BIGINT );
					intFieldIndex += 1;
					
				}
				
				if ( strTransactionID != null && strTransactionID.trim().isEmpty() == false ) {
					
					RowsetMetaData.setColumnName( intFieldIndex, JavaXMLWebRowSetTags._XML_StructTransactionID );
					RowsetMetaData.setColumnLabel( intFieldIndex, JavaXMLWebRowSetTags._XML_StructTransactionID );
					RowsetMetaData.setColumnType( intFieldIndex, Types.BIGINT );
					RowsetMetaData.setColumnTypeName( intFieldIndex, NamesSQLTypes._BIGINT );
					intFieldIndex += 1;
					
				}
				
				RowsetMetaData.setColumnName( intFieldIndex, JavaXMLWebRowSetTags._XML_StructCode );
				RowsetMetaData.setColumnLabel( intFieldIndex, JavaXMLWebRowSetTags._XML_StructCode );
				RowsetMetaData.setColumnType( intFieldIndex, Types.INTEGER );
				RowsetMetaData.setColumnTypeName( intFieldIndex, NamesSQLTypes._INTEGER );
				intFieldIndex += 1;

				RowsetMetaData.setColumnName( intFieldIndex, JavaXMLWebRowSetTags._XML_StructDescription );
				RowsetMetaData.setColumnLabel( intFieldIndex, JavaXMLWebRowSetTags._XML_StructDescription );
				RowsetMetaData.setColumnType( intFieldIndex, Types.VARCHAR );
				RowsetMetaData.setColumnTypeName( intFieldIndex, NamesSQLTypes._VARCHAR );
				RowsetMetaData.setColumnDisplaySize( intFieldIndex, JavaXMLWebRowSetTags._XML_StructDescriptionLength );
				//intFieldIndex += 1;
	
				CachedRowSet CachedRowset = new CachedRowSetImpl();

				CachedRowset.setMetaData( RowsetMetaData );				

        		CachedRowset.moveToInsertRow();

				intFieldIndex = 1;

				if ( strSecurityToken != null && strSecurityToken.trim().isEmpty() == false ) {
				    
					CachedRowset.updateLong( intFieldIndex, Long.parseLong( strSecurityToken ) );
					intFieldIndex += 1;
				  
				}
        		
				if ( strTransactionID != null && strTransactionID.trim().isEmpty() == false ) {
					
					CachedRowset.updateLong( intFieldIndex, Long.parseLong( strTransactionID ) );
					intFieldIndex += 1;
					
				}
				
				CachedRowset.updateInt( intFieldIndex, intCode );
				intFieldIndex += 1;
				
				CachedRowset.updateString( intFieldIndex, strDescription );
				//intFieldIndex += 1;
				
    			CachedRowset.insertRow();
    			
				CachedRowset.moveToCurrentRow();
				
				CachedRowset.beforeFirst();
				
				WebRowSet wrs = new WebRowSetImpl();

				StringWriter sw = new StringWriter();

				wrs.writeXml( CachedRowset, sw );

				strResult = sw.toString();

				wrs.close();
			
			}
			else {
				
	        	OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
				
			}
			
		}
		catch ( Exception Ex ) {
			
        	OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );
        	
		}
		
		return strResult;
		
	}

}
