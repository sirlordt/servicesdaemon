package AbstractResponseFormat;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.maindataservices.Base64;
import net.maindataservices.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultSetResult;
import CommonClasses.NamesSQLTypes;
import ExtendedLogger.CExtendedLogger;

public class CJSONResponseFormat extends CAbstractResponseFormat {

	public CJSONResponseFormat() {
		
		this.strName = JSONTags._ResponseFormat_JSON;
		strMinVersion = "1.0";
		strMaxVersion = "1.0";
		
	}
	
	@Override
	public CAbstractResponseFormat getNewInstance() {

		CJSONResponseFormat NewInstance = new CJSONResponseFormat(); 
		
    	NewInstance.InitResponseFormat( this.ServicesDaemonConfig, this.OwnerConfig );
    	
    	return NewInstance;
	
	}

	@Override
	public String getContentType() {

		if ( OwnerConfig != null )
			return OwnerConfig.getConfigValue( ConstantsResponseFormat._JSON_ContentType );
		else
			return "";
	
	}

	@Override
	public String getCharacterEncoding() {

		if ( OwnerConfig != null )
			return OwnerConfig.getConfigValue( ConstantsResponseFormat._JSON_CharSet );
		else
			return "";
	
	}

	public void DescribeService( CAbstractService Service, JSONArray JSONDocument, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	if ( Service.getHiddenService() == false ) {
    		
    		String strServiceType = "ReadWrite";
    		
	        if ( Service.getServiceType() == 1 )
	           strServiceType = "Read";
	        else if ( Service.getServiceType() == 2 )
	           strServiceType = "Write";
    		
        	HashMap< String, ArrayList< CInputServiceParameter > > GroupsInputParametersService = Service.getGroupsInputParametersService();   

	        Iterator< Entry< String, ArrayList< CInputServiceParameter > > > It = GroupsInputParametersService.entrySet().iterator();

			if ( Logger != null ) {

				if ( Lang != null )
					Logger.LogMessage( "1", Lang.Translate( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
				else
					Logger.LogMessage( "1", String.format( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
				
			}    
			else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {
	            
				if ( OwnerConfig.Lang != null )
					OwnerConfig.Logger.LogMessage( "1", OwnerConfig.Lang.Translate( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
				else
					OwnerConfig.Logger.LogMessage( "1", String.format( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
					
			}    
	        
            while ( It.hasNext() ) {
	        
            	JSONObject JSONService = new JSONObject();

            	JSONService.put( XMLDataPacketTags._XML_StructServiceName , Service.getServiceName() );

            	JSONService.put( XMLDataPacketTags._XML_StructAccessType , strServiceType );

            	JSONService.put( XMLDataPacketTags._XML_StructDescription , Service.getServiceDescription() );

            	JSONService.put( XMLDataPacketTags._XML_StructAuthorContact , Service.getServiceAuthorContact() );
        		
            	JSONService.put( XMLDataPacketTags._XML_StructAuthor , Service.getServiceAuthor() );

        		if ( Service.getAuthRequired() == true )  
            		JSONService.put( XMLDataPacketTags._XML_StructAuthRequired , "Yes" );
            	else
            		JSONService.put( XMLDataPacketTags._XML_StructAuthRequired , "No" );

            	JSONArray JSONServiceInputParameters = new JSONArray(); 
        		
            	Entry< String,ArrayList<CInputServiceParameter> > GroupIPSEntry = It.next();
            	
    			String strKey = GroupIPSEntry.getKey();
            	
            	JSONService.put( XMLDataPacketTags._XML_StructParamSetName, strKey ); 
    			
            	for ( CInputServiceParameter InputServiceParameter : GroupIPSEntry.getValue() ) {
            		
            		JSONObject JSONServiceInputParameter = new JSONObject(); 
            		
            		JSONServiceInputParameter.put( XMLDataPacketTags._XML_StructParamName, InputServiceParameter.getParameterName() );

            		JSONServiceInputParameter.put( XMLDataPacketTags._XML_StructDescription, InputServiceParameter.getParameterDescription() );
            		
            		JSONServiceInputParameter.put( XMLDataPacketTags._XML_StructTypeWidth, InputServiceParameter.getParameterDataTypeWidth() );

            		JSONServiceInputParameter.put( XMLDataPacketTags._XML_StructType, InputServiceParameter.getParameterDataType() );

            		JSONServiceInputParameter.put( XMLDataPacketTags._XML_StructRequired, InputServiceParameter.getParameterRequired()?"Yes":"No" );

            		JSONServiceInputParameters.put( JSONServiceInputParameter );
            		
            	}
            	
        		JSONService.put( XMLDataPacketTags._InputParameters, JSONServiceInputParameters );
            	JSONDocument.put( JSONService );
            	
            	
            }
    		
    	}
    	
    }

	@Override
	public String EnumerateServices( HashMap<String, CAbstractService> RegisteredServices, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		Iterator<Entry<String, CAbstractService>> it = RegisteredServices.entrySet().iterator();

		JSONArray JSONDocument = new JSONArray();
		
		while ( it.hasNext() ) {

			Entry<String, CAbstractService> Pairs = it.next();

			String strServName = (String) Pairs.getKey();

			CAbstractService Service = RegisteredServices.get( strServName );

			if ( Service != null )
				this.DescribeService( Service, JSONDocument, Logger, Lang );

		}

		String strTmpBuffer = JSONDocument.toString(); 
		
		return strTmpBuffer;
	
	}

	boolean FormatResultSetToJSONArray( ResultSet SQLDataSet, CAbstractDBEngine DBEngine, JSONArray JSONDocument, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;

		try {

			java.sql.ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();

			boolean bOnFirstRow = SQLDataSet.getRow() == 1;

			while ( bOnFirstRow == true || SQLDataSet.next() == true ) {

				bOnFirstRow = false;

				bOnFirstRow = false;

				JSONObject JSONResultSet = new JSONObject();

				for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

					String strFieldName = DataSetMetaData.getColumnName( i );

					if ( strFieldName == null || strFieldName.isEmpty() == true )
						strFieldName = DataSetMetaData.getColumnLabel( i );

					int intFieldType = DataSetMetaData.getColumnType( i );

					intFieldType = DBEngine.getJavaSQLColumnType( intFieldType, Logger, Lang );

					if ( SQLDataSet.getObject( i ) != null ) {

						if ( intFieldType != Types.BLOB ) {   
						
							Object FieldValue = DBEngine.getFieldValueAsObject(intFieldType, strFieldName, SQLDataSet, Logger, Lang );
						
							JSONResultSet.put( strFieldName, FieldValue );
							
						}
						else {
							
							String strFieldValue = DBEngine.getFieldValueAsString( intFieldType, strFieldName, SQLDataSet, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
							
							JSONResultSet.put( strFieldName, strFieldValue );
							
						}

					}
					else {

						JSONResultSet.putOpt( strFieldName, JSONObject.NULL );

					}

				}

				JSONDocument.put( JSONResultSet );

			}

			bResult = true;
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

		}

		return bResult;
		
	}
	
	boolean FormatMemoryRowSetToJSONArray( CMemoryRowSet MemoryRowSet, JSONArray JSONDocument, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;

		try {

			int intRowCount = MemoryRowSet.getRowCount();
			int intColCount = MemoryRowSet.getFieldsCount();

			for ( int intRowIndex = 0; intRowIndex < intRowCount; intRowIndex++ ) {

				JSONObject JSONResultSet = new JSONObject();

				for ( int intIndexColumn = 1; intIndexColumn <= intColCount; intIndexColumn++ ) {

					CMemoryFieldData Field = MemoryRowSet.getFieldByIndex( intIndexColumn - 1 );

					Object FieldData = null; 
					String strFieldName = null;
					int intFieldType = -1;
					
					if ( Field != null ) {

						strFieldName = Field.strName;
						intFieldType = Field.intSQLType;
						FieldData = Field.getData( intRowIndex );

						if  ( FieldData != null ) {
							
							if ( intFieldType != Types.BLOB ) {   
								
								JSONResultSet.put( strFieldName, FieldData );
								
							}
							else {
								
    							Blob BinaryBLOBData = (Blob) FieldData;
    							
    							String strBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ), "UTF-8" );
							
								JSONResultSet.put( strFieldName, strBase64Coded );
								
							}

						}
						else {

							JSONResultSet.putOpt( strFieldName, JSONObject.NULL );

						}
						
					}

				}

	            JSONDocument.put( JSONResultSet );
	            
			}

			bResult = true;
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

		}

		return bResult;
		
	}
	
	@Override
	public String FormatResultSet( ResultSet SQLDataSet, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";
		
        try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {

				JSONArray JSONDocument = new JSONArray();
            
				if ( this.FormatResultSetToJSONArray( SQLDataSet, DBEngine, JSONDocument, strDateTimeFormat, strDateFormat,  strTimeFormat, Logger, Lang ) ) 
					strResult = JSONDocument.toString();
				
			}
        	
        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
		
		return strResult;
	
	}

	@Override
	public String FormatResultsSets( ArrayList<ResultSet> SQLDataSetList, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";

		try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {
			
				if ( SQLDataSetList.size() > 1 ) {

					JSONArray JSONDocument = new JSONArray();
		        	
					for ( ResultSet SQLDataSetToAdd: SQLDataSetList ) {    

						this.FormatResultSetToJSONArray( SQLDataSetToAdd, DBEngine, JSONDocument, strDateTimeFormat, strDateFormat,  strTimeFormat, Logger, Lang );

					}

					strResult = JSONDocument.toString();
					
				}	
			
			}
			else {
				
				if ( Logger != null ) {
					
					if ( Lang != null )
						Logger.LogError( "-1015", Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
		
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;
	
	}

	@Override
	public String FormatResultSet( CResultSetResult SQLDataSetResult, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

    	String strResult = "";
    	
        try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {

				if ( SQLDataSetResult.Result != null && SQLDataSetResult.intCode >= 0 ) {

					JSONArray JSONDocumentRoot = new JSONArray();

					JSONArray JSONDocumentData = new JSONArray();
					
					JSONArray JSONDocumentErrors = new JSONArray();

					JSONDocumentRoot.put( JSONDocumentData );
					JSONDocumentRoot.put( JSONDocumentErrors );
					
					if ( this.FormatResultSetToJSONArray( SQLDataSetResult.Result, DBEngine, JSONDocumentData, strDateTimeFormat, strDateFormat,  strTimeFormat, Logger, Lang ) ) 
						strResult = JSONDocumentRoot.toString();

				}
				else {

					JSONArray JSONDocumentRoot = new JSONArray();

					JSONObject JSONDocumentData = new JSONObject();
					
					JSONArray JSONDocumentErrors = new JSONArray();

					JSONDocumentRoot.put( JSONDocumentData );
					JSONDocumentRoot.put( JSONDocumentErrors );

					JSONDocumentData.put( JavaXMLWebRowSetTags._XML_StructSQLOperationAffectedRows, SQLDataSetResult.lngAffectedRows );
					JSONDocumentData.put( JavaXMLWebRowSetTags._XML_StructSQLOperationCode, SQLDataSetResult.intCode );
					JSONDocumentData.put( JavaXMLWebRowSetTags._XML_StructSQLOperationDescription, SQLDataSetResult.strDescription );

					strResult = JSONDocumentRoot.toString();
					
				}
				

			}
			else {
				
				if ( Logger != null ) {
					
					if ( Lang != null )
						Logger.LogError( "-1015", Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
        	
        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
    	
    	return strResult;
	
	}

	@Override
	public String FormatResultsSets( ArrayList<CResultSetResult> SQLDataSetResultList, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang, int intDummyParam ) {

    	String strResult = "";
    	
        try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {
        	
				if ( SQLDataSetResultList.size() > 0 ) {

					ResultSet SQLDataSet = CResultSetResult.getFirstResultSetNotNull( SQLDataSetResultList );
				
					if ( SQLDataSet != null ) {

						JSONArray JSONDocumentRoot = new JSONArray();
						
						JSONArray JSONDocumentData = new JSONArray();
						
						JSONArray JSONDocumentErrors = new JSONArray();
						
						JSONDocumentRoot.put( JSONDocumentData );
						JSONDocumentRoot.put( JSONDocumentErrors );
						
						for ( CResultSetResult SQLDataSetResultToAdd: SQLDataSetResultList ) {    

							if ( SQLDataSetResultToAdd.Result != null && SQLDataSetResultToAdd.intCode >= 0 ) {   

								this.FormatResultSetToJSONArray( SQLDataSetResultToAdd.Result, DBEngine, JSONDocumentData, strDateTimeFormat, strDateFormat,  strTimeFormat, Logger, Lang );
								
							}
							else {

								JSONObject JSONError = new JSONObject(); 
								
								JSONError.put( JavaXMLWebRowSetTags._XML_StructSQLOperationAffectedRows, SQLDataSetResultToAdd.lngAffectedRows );
								JSONError.put( JavaXMLWebRowSetTags._XML_StructSQLOperationCode, SQLDataSetResultToAdd.intCode );
								JSONError.put( JavaXMLWebRowSetTags._XML_StructSQLOperationDescription, SQLDataSetResultToAdd.strDescription );
								
								JSONDocumentErrors.put( JSONError );

							}

						}

						strResult = JSONDocumentRoot.toString();
						
					}
					else {

						CMemoryRowSet MemoryRowSet = new CMemoryRowSet( false );

						MemoryRowSet.addField( JavaXMLWebRowSetTags._XML_StructSQLOperationAffectedRows, Types.BIGINT, NamesSQLTypes._BIGINT, 0, JavaXMLWebRowSetTags._XML_StructSQLOperationAffectedRows );
						MemoryRowSet.addField( JavaXMLWebRowSetTags._XML_StructSQLOperationCode, Types.INTEGER, NamesSQLTypes._INTEGER, 0, JavaXMLWebRowSetTags._XML_StructSQLOperationCode );
						MemoryRowSet.addField( JavaXMLWebRowSetTags._XML_StructSQLOperationDescription, Types.VARCHAR, NamesSQLTypes._VARCHAR, JavaXMLWebRowSetTags._XML_StructSQLOperationDescriptionLength, JavaXMLWebRowSetTags._XML_StructSQLOperationDescription );

						for ( CResultSetResult SQLDataSetResultToAdd: SQLDataSetResultList ) {    

							MemoryRowSet.addData( JavaXMLWebRowSetTags._XML_StructSQLOperationAffectedRows,- SQLDataSetResultToAdd.lngAffectedRows );
							MemoryRowSet.addData( JavaXMLWebRowSetTags._XML_StructSQLOperationCode, SQLDataSetResultToAdd.intCode );
							MemoryRowSet.addData( JavaXMLWebRowSetTags._XML_StructSQLOperationDescription, SQLDataSetResultToAdd.strDescription );

						}

						JSONArray JSONDocumentRoot = new JSONArray();

						JSONArray JSONDocumentData = new JSONArray();

						JSONArray JSONDocumentErrors = new JSONArray();

						JSONDocumentRoot.put( JSONDocumentData );
						JSONDocumentRoot.put( JSONDocumentErrors );
						
						if ( this.FormatMemoryRowSetToJSONArray( MemoryRowSet, JSONDocumentData, Logger, Lang ) ) 
							strResult = JSONDocumentRoot.toString();
						
					}
					
				
				}
	        	
			}
			else {
				
				if ( Logger != null ) {
					
					if ( Lang != null )
						Logger.LogError( "-1015", Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
	            
        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
    	
    	return strResult;
	
	}
	
	@Override
	public String FormatMemoryRowSet( CMemoryRowSet MemoryRowSet, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";

		try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {

				JSONArray JSONDocument = new JSONArray();

				if ( this.FormatMemoryRowSetToJSONArray( MemoryRowSet, JSONDocument, Logger, Lang ) ) 
					strResult = JSONDocument.toString();

			}
			else {
				
				if ( Logger != null ) {
					
					if ( Lang != null )
						Logger.LogError( "-1015", Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
		
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;
	
	}

	@Override
	public String FormatMemoryRowSets( ArrayList<CMemoryRowSet> MemoryRowSetList, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";

		try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {

				JSONArray JSONDocument = new JSONArray();

				for ( int intIndexResultSet = 1; intIndexResultSet < MemoryRowSetList.size(); intIndexResultSet++ ) {

					this.FormatMemoryRowSetToJSONArray( MemoryRowSetList.get( intIndexResultSet ), JSONDocument, Logger, Lang ); 

				}

				strResult = JSONDocument.toString();

			}
			else {
				
				if ( Logger != null ) {
					
					if ( Lang != null )
						Logger.LogError( "-1015", Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
		
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;
	
	}
	
	@Override
	public String FormatSimpleMessage( String strSecurityTokenID, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";

		try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {

				JSONArray JSONDocumentRoot = new JSONArray();

				JSONObject JSONMessage = new JSONObject();
				
				if ( strSecurityTokenID != null && strSecurityTokenID.trim().isEmpty() == false ) {

					JSONMessage.put( JavaXMLWebRowSetTags._XML_StructSecurityTokenID, Long.parseLong( strSecurityTokenID ) );
					
				}
				
				if ( strTransactionID != null && strTransactionID.trim().isEmpty() == false ) {
				
					JSONMessage.put( JavaXMLWebRowSetTags._XML_StructTransactionID, Long.parseLong( strTransactionID ) );
				
				}
				
				JSONMessage.put( JavaXMLWebRowSetTags._XML_StructCode, intCode );
				JSONMessage.put( JavaXMLWebRowSetTags._XML_StructDescription, strDescription );
					
				JSONDocumentRoot.put( JSONMessage );

				strResult = JSONDocumentRoot.toString();

			}
			else {
				
				if ( Logger != null ) {
					
					if ( Lang != null )
						Logger.LogError( "-1015", Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.LogError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
		
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;
	
	}

}
