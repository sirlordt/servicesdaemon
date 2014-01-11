package AbstractResponseFormat;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
import CommonClasses.ConstantsMessagesCodes;
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
		
    	NewInstance.initResponseFormat( this.ServicesDaemonConfig, this.OwnerConfig );
    	
    	return NewInstance;
	
	}

	@Override
	public String getContentType() {

		if ( OwnerConfig != null )
			return (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._JSON_ContentType, null );
		else
			return "";
	
	}

	@Override
	public String getCharacterEncoding() {

		if ( OwnerConfig != null )
			return (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._JSON_CharSet, null );
		else
			return "";
	
	}

	public void describeService( CAbstractService Service, JSONArray JSONDocument, CExtendedLogger Logger, CLanguage Lang ) {
    	
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
					Logger.logMessage( "1", Lang.translate( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
				else
					Logger.logMessage( "1", String.format( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
				
			}    
			else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {
	            
				if ( OwnerConfig.Lang != null )
					OwnerConfig.Logger.logMessage( "1", OwnerConfig.Lang.translate( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
				else
					OwnerConfig.Logger.logMessage( "1", String.format( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
					
			}    
	        
            while ( It.hasNext() ) {
	        
            	JSONObject JSONService = new JSONObject();

            	JSONService.put( JSONTags._JSON_StructServiceName , Service.getServiceName() );

            	JSONService.put( JSONTags._JSON_StructAccessType , strServiceType );

            	JSONService.put( JSONTags._JSON_StructDescription , Service.getServiceDescription() );

            	JSONService.put( JSONTags._JSON_StructAuthorContact , Service.getServiceAuthorContact() );
        		
            	JSONService.put( JSONTags._JSON_StructAuthor , Service.getServiceAuthor() );

        		if ( Service.getAuthRequired() == true )  
            		JSONService.put( JSONTags._JSON_StructAuthRequired , "Yes" );
            	else
            		JSONService.put( JSONTags._JSON_StructAuthRequired , "No" );

            	JSONArray JSONServiceInputParameters = new JSONArray(); 
        		
            	Entry< String,ArrayList<CInputServiceParameter> > GroupIPSEntry = It.next();
            	
    			String strKey = GroupIPSEntry.getKey();
            	
            	JSONService.put( JSONTags._JSON_StructParamSetName, strKey ); 
    			
            	for ( CInputServiceParameter InputServiceParameter : GroupIPSEntry.getValue() ) {
            		
            		JSONObject JSONServiceInputParameter = new JSONObject(); 
            		
            		JSONServiceInputParameter.put( JSONTags._JSON_StructParamName, InputServiceParameter.getParameterName() );

            		JSONServiceInputParameter.put( JSONTags._JSON_StructDescription, InputServiceParameter.getParameterDescription() );
            		
            		JSONServiceInputParameter.put( JSONTags._JSON_StructTypeWidth, InputServiceParameter.getParameterDataTypeWidth() );

            		JSONServiceInputParameter.put( JSONTags._JSON_StructType, InputServiceParameter.getParameterDataType() );

            		JSONServiceInputParameter.put( JSONTags._JSON_StructRequired, InputServiceParameter.getParameterRequired()?"Yes":"No" );

            		JSONServiceInputParameters.put( JSONServiceInputParameter );
            		
            	}
            	
        		JSONService.put( XMLDataPacketTags._InputParameters, JSONServiceInputParameters );
            	JSONDocument.put( JSONService );
            	
            	
            }
    		
    	}
    	
    }

	@Override
	public String enumerateServices( HashMap<String, CAbstractService> RegisteredServices, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		Iterator<Entry<String, CAbstractService>> it = RegisteredServices.entrySet().iterator();

		JSONArray JSONDocument = new JSONArray();
		
		while ( it.hasNext() ) {

			Entry<String, CAbstractService> Pairs = it.next();

			String strServName = Pairs.getKey();

			CAbstractService Service = RegisteredServices.get( strServName );

			if ( Service != null )
				this.describeService( Service, JSONDocument, Logger, Lang );

		}

		String strTmpBuffer = JSONDocument.toString(); 
		
		return strTmpBuffer;
	
	}

	public void printJSONHeader( PrintWriter TempResponseFormatedFileWriter, boolean bOpen ) {
		
		if ( bOpen )  
			TempResponseFormatedFileWriter.println( "[" );
		else
			TempResponseFormatedFileWriter.println( "]" );
		
	}
	
	public void printJSONDataSection( PrintWriter TempResponseFormatedFileWriter, boolean bOpen, boolean bAddComma ) {
		
		if ( bOpen )  
			TempResponseFormatedFileWriter.println( "  [" );
		else if ( bAddComma == false )
			TempResponseFormatedFileWriter.println( "  ]" );
		else
			TempResponseFormatedFileWriter.println( "  ]," );
		
	}
	
	/*public void PrintJSONErrorSection( PrintWriter TempResponseFormatedFileWriter, ArrayList<String> strErrorCodeDescription ) {

		if ( strErrorCodeDescription.size() == 0 ) {
			
			TempResponseFormatedFileWriter.println( "  []" );
			
		}
		else {
			
			TempResponseFormatedFileWriter.println( "  [" );
			
			for ( int I = 0; I < strErrorCodeDescription.size(); I++ ) {
				
				TempResponseFormatedFileWriter.println( "    {" );
				
				TempResponseFormatedFileWriter.println( "      " + strErrorCodeDescription.get( I ) );
				
				TempResponseFormatedFileWriter.println( "    }" );

				if ( I < strErrorCodeDescription.size() - 1 ) {
					
					TempResponseFormatedFileWriter.println( "," );
					
				}
				
			}
			
			TempResponseFormatedFileWriter.println( "  ]" );
			
		}
		
	}*/
	
	public boolean formatResultSetToJSONArray( String strTempDir, String strTempFile, PrintWriter TempResponseFormatedFileWriter, OutputStream TempStreamResponseFormatedFile, ResultSet SQLDataSet, CAbstractDBEngine DBEngine, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;

		try {

			java.sql.ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();

			int intColumnCount = DataSetMetaData.getColumnCount();
			
			boolean bIsFirst = true;
			
			while ( SQLDataSet.next() == true ) {

				if ( bIsFirst ) {

					TempResponseFormatedFileWriter.println( "    {" );
					
					bIsFirst = false;
					
				}   
				else {
					
			       TempResponseFormatedFileWriter.println( "," );
				   TempResponseFormatedFileWriter.println( "    {" );
					
				}
				
				for ( int i = 1; i <= intColumnCount; i++ ) {

					String strFieldName = DataSetMetaData.getColumnName( i );

					if ( strFieldName == null || strFieldName.isEmpty() == true )
						strFieldName = DataSetMetaData.getColumnLabel( i );

					int intFieldType = DataSetMetaData.getColumnType( i );

					intFieldType = DBEngine.getJavaSQLColumnType( intFieldType, Logger, Lang );

					if ( SQLDataSet.getObject( i ) != null ) {

						if ( intFieldType == Types.VARCHAR || intFieldType == Types.CHAR || intFieldType == Types.NVARCHAR || intFieldType == Types.NCHAR ) {   
						
							TempResponseFormatedFileWriter.print( "      \"" + strFieldName + "\":\"" + DBEngine.getFieldValueAsString( intFieldType, strFieldName, SQLDataSet, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang ) + "\"" );
							
						}
						else if ( intFieldType == Types.BLOB ) {
							
            				TempResponseFormatedFileWriter.print( "      \"" +  strFieldName + "\":\"" );
            				TempResponseFormatedFileWriter.flush();
            				DBEngine.writeBlobValueAsStringToFile( intFieldType, strFieldName, strTempDir, strTempFile, TempStreamResponseFormatedFile, SQLDataSet, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang  );
            				TempResponseFormatedFileWriter.print( "\"" );
							
						}
						else {
							
							TempResponseFormatedFileWriter.print( "      \"" + strFieldName + "\":" + DBEngine.getFieldValueAsString( intFieldType, strFieldName, SQLDataSet, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang ) + "" );
							
						}

					}
					else {

						TempResponseFormatedFileWriter.print( "      \"" + strFieldName + "\":null" );

					}
					
					if ( i < intColumnCount ) {
						
						TempResponseFormatedFileWriter.println( "," );
						
					}
					else {
						
						TempResponseFormatedFileWriter.println( "" );
						
					}

				}

				TempResponseFormatedFileWriter.print( "    }" );
				//JSONDocument.put( JSONResultSet );

			}

			//TempResponseFormatedFileWriter.println( "" );
			
			bResult = true;
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

		}

		return bResult;
		
	}
	
	boolean formatMemoryRowSetToJSONArray( CMemoryRowSet MemoryRowSet, JSONArray JSONDocument, CExtendedLogger Logger, CLanguage Lang ) {
		
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
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

		}

		return bResult;
		
	}
	
	/*Override
	public boolean FormatResultSet( HttpServletResponse Response, ResultSet SQLDataSet, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
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
		
		return bResult;
	
	}

	Override
	public boolean FormatResultsSets( HttpServletResponse Response, ArrayList<ResultSet> SQLDataSetList, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		/*try {

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

		return bResult;
	
	}*/

	@Override
	public boolean formatResultSet( HttpServletResponse Response, CResultSetResult SQLDataSetResult, CAbstractDBEngine DBEngine, int intInternalFetchSize, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, boolean bDeleteTempReponseFile, CExtendedLogger Logger, CLanguage Lang ) {

    	boolean bResult = false;
    	
        try {

			if ( Utilities.versionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.versionLessEquals( strVersion, this.strMaxVersion ) ) {

		        String strTempDir = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Temp_Dir, null );
		        
		        String strTempResponseFormatedFilePath = strTempDir + UUID.randomUUID() + ".formated_response";
	    		
	            ServletOutputStream OutStream = Response.getOutputStream(); //new FileOutputStream( strTempResponseFormatedFilePath ); 
		        
		        PrintWriter TempResponseFormatedFileWriter = new PrintWriter( OutStream ); // strTempResponseFormatedFilePath, this.getCharacterEncoding() );
				
				if ( SQLDataSetResult.Result != null && SQLDataSetResult.intCode >= 0 ) {

        			this.printJSONHeader( TempResponseFormatedFileWriter, true );
        			
        			this.printJSONDataSection( TempResponseFormatedFileWriter, true, false );
					
					this.formatResultSetToJSONArray( strTempDir, strTempResponseFormatedFilePath, TempResponseFormatedFileWriter, OutStream, SQLDataSetResult.Result, DBEngine, strDateTimeFormat, strDateFormat,  strTimeFormat, Logger, Lang ); 
					
					TempResponseFormatedFileWriter.println( "" );
					
        			this.printJSONDataSection( TempResponseFormatedFileWriter, false, true );

					JSONArray JSONDocumentErrors = new JSONArray();
					
    				TempResponseFormatedFileWriter.print( JSONDocumentErrors.toString( 2, 2 ) );
        			
    				TempResponseFormatedFileWriter.println();
    				
        			this.printJSONHeader( TempResponseFormatedFileWriter, false );
        			
        			TempResponseFormatedFileWriter.close();

        			TempResponseFormatedFileWriter = null;
        			
				}
				else {

					JSONArray JSONDocumentRoot = new JSONArray();

					JSONObject JSONDocumentData = new JSONObject();
					
					JSONDocumentData.put( JSONTags._JSON_StructAffectedRows, SQLDataSetResult.lngAffectedRows );
					JSONDocumentData.put( JSONTags._JSON_StructCode, SQLDataSetResult.intCode );
					JSONDocumentData.put( JSONTags._JSON_StructDescription, SQLDataSetResult.strDescription );

					JSONDocumentRoot.put( JSONDocumentData );
					
					JSONArray JSONDocumentErrors = new JSONArray();
					JSONDocumentRoot.put( JSONDocumentErrors );
					
					if ( SQLDataSetResult.intCode < 0 ) {
						
						JSONDocumentErrors.put( JSONDocumentData );
						
					}
					
        			TempResponseFormatedFileWriter.print( JSONDocumentRoot.toString() );

        			TempResponseFormatedFileWriter.close();

        			TempResponseFormatedFileWriter = null;
					
				}

    			/****
    			 File TempResponseFormatedFile = new File( strTempResponseFormatedFilePath ); 

    			this.CopyToResponseStream( Response, TempResponseFormatedFile, 10240, Logger, Lang );

    			if ( bDeleteTempReponseFile )
    				TempResponseFormatedFile.delete();
    			****/
				
			}
			else {
				
				if ( Logger != null ) {
					
					if ( Lang != null )
						Logger.logError( "-1015", Lang.translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.logError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.logError( "-1015", OwnerConfig.Lang.translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.logError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
        	
        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

        }
    	
    	return bResult;
	
	}

	@Override
	public boolean formatResultsSets( HttpServletResponse Response, ArrayList<CResultSetResult> SQLDataSetResultList, CAbstractDBEngine DBEngine, int intInternalFetchSize, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, boolean bDeleteTempReponseFile, CExtendedLogger Logger, CLanguage Lang, int intDummyParam ) {

    	boolean bResult = false;
    	
        try {

			if ( Utilities.versionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.versionLessEquals( strVersion, this.strMaxVersion ) ) {
        	
				if ( SQLDataSetResultList.size() > 0 ) {

	    	        String strTempDir = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Temp_Dir, null );
	    	        
	    	        String strTempResponseFormatedFilePath = strTempDir + UUID.randomUUID() + ".formated_response";
	        		
	                ServletOutputStream OutStream = Response.getOutputStream(); //new FileOutputStream( strTempResponseFormatedFilePath ); 
	    	        
	    	        PrintWriter TempResponseFormatedFileWriter = new PrintWriter( OutStream ); // strTempResponseFormatedFilePath, this.getCharacterEncoding() );
					
					ResultSet SQLDataSet = CResultSetResult.getFirstResultSetNotNull( SQLDataSetResultList );
				
					if ( SQLDataSet != null ) {

	        			this.printJSONHeader( TempResponseFormatedFileWriter, true );
	        			
	        			this.printJSONDataSection( TempResponseFormatedFileWriter, true, false );

						JSONArray JSONDocumentErrors = new JSONArray();
						
						boolean bFirst = true;
						
						for ( CResultSetResult SQLDataSetResultToAdd: SQLDataSetResultList ) {    

							if ( bFirst == false ) {
								
								TempResponseFormatedFileWriter.println( "," );
								
							}
							
							bFirst = false;
							
							if ( SQLDataSetResultToAdd.Result != null && SQLDataSetResultToAdd.intCode >= 0 ) {   

								this.formatResultSetToJSONArray( strTempDir, strTempResponseFormatedFilePath, TempResponseFormatedFileWriter, OutStream, SQLDataSetResultToAdd.Result, DBEngine, strDateTimeFormat, strDateFormat,  strTimeFormat, Logger, Lang ); 
								
							}
							else {

								JSONObject JSONError = new JSONObject(); 
								
								JSONError.put( JSONTags._JSON_StructAffectedRows, SQLDataSetResultToAdd.lngAffectedRows );
								JSONError.put( JSONTags._JSON_StructCode, SQLDataSetResultToAdd.intCode );
								JSONError.put( JSONTags._JSON_StructDescription, SQLDataSetResultToAdd.strDescription );
								
								JSONDocumentErrors.put( JSONError );

							}

						}

						TempResponseFormatedFileWriter.println( "" );

						this.printJSONDataSection( TempResponseFormatedFileWriter, false, true );

	    				TempResponseFormatedFileWriter.print( JSONDocumentErrors.toString( 2, 2 ) );
	        			
						TempResponseFormatedFileWriter.println( "" );
	    				
	        			this.printJSONHeader( TempResponseFormatedFileWriter, false );
						
						TempResponseFormatedFileWriter.close();

						TempResponseFormatedFileWriter = null;
						
					}
					else {

						JSONArray JSONDocumentRoot = new JSONArray();

						JSONArray JSONDocumentData = new JSONArray();

						JSONArray JSONDocumentErrors = new JSONArray();

						JSONDocumentRoot.put( JSONDocumentData );
						JSONDocumentRoot.put( JSONDocumentErrors );

						for ( CResultSetResult SQLDataSetResultToAdd: SQLDataSetResultList ) {    

							JSONObject JSONData = new JSONObject(); 
							
                        	JSONData.put( JSONTags._JSON_StructAffectedRows, SQLDataSetResultToAdd.lngAffectedRows );
                        	JSONData.put( JSONTags._JSON_StructCode, SQLDataSetResultToAdd.intCode );
                        	JSONData.put( JSONTags._JSON_StructDescription, SQLDataSetResultToAdd.strDescription );

							JSONDocumentData.put( JSONData );

							if ( SQLDataSetResultToAdd.intCode < 0 ) { 
							
    							JSONDocumentErrors.put( JSONData );
							
                            }
							
						}
						
						TempResponseFormatedFileWriter.print( JSONDocumentRoot.toString() );

						TempResponseFormatedFileWriter.close();

						TempResponseFormatedFileWriter = null;
						
					}
					
					/****
		    		File TempResponseFormatedFile = new File( strTempResponseFormatedFilePath ); 
		    		
		    		this.CopyToResponseStream( Response, TempResponseFormatedFile, 10240, Logger, Lang );
		    		
		    		if ( bDeleteTempReponseFile )
		    			TempResponseFormatedFile.delete();
					******/
					
				}
	        	
			}
			else {
				
				if ( Logger != null ) {
					
					if ( Lang != null )
						Logger.logError( "-1015", Lang.translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.logError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.logError( "-1015", OwnerConfig.Lang.translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.logError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
	            
        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

        }
    	
    	return bResult;
	
	}
	
	@Override
	public String formatMemoryRowSet( CMemoryRowSet MemoryRowSet, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";

		try {

			if ( Utilities.versionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.versionLessEquals( strVersion, this.strMaxVersion ) ) {

				JSONArray JSONDocument = new JSONArray();

				if ( this.formatMemoryRowSetToJSONArray( MemoryRowSet, JSONDocument, Logger, Lang ) ) 
					strResult = JSONDocument.toString();

			}
			else {
				
				if ( Logger != null ) {
					
					if ( Lang != null )
						Logger.logError( "-1015", Lang.translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.logError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.logError( "-1015", OwnerConfig.Lang.translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.logError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
		
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;
	
	}

	/*Override
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
	
	}*/
	
	@Override
	public String formatSimpleMessage( String strSecurityTokenID, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";

		try {

			if ( Utilities.versionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.versionLessEquals( strVersion, this.strMaxVersion ) ) {

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
						Logger.logError( "-1015", Lang.translate( "Format version [%s] not supported", strVersion ) );
					else
						Logger.logError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );
				    
				}    
				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.logError( "-1015", OwnerConfig.Lang.translate( "Format version [%s] not supported", strVersion ) );
					else
						OwnerConfig.Logger.logError( "-1015", String.format( "Format version [%s] not supported", strVersion ) );

				}    
				
			}
		
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1016", Ex.getMessage(), Ex );

		}

		return strResult;
	
	}

}
