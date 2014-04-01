package AbstractResponseFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.maindataservices.Utilities;
import AbstractDBEngine.CAbstractDBEngine;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultDataSet;
import CommonClasses.ConstantsMessagesCodes;
import CommonClasses.NamesSQLTypes;
import ExtendedLogger.CExtendedLogger;

public class CCSVResponseFormat extends CAbstractResponseFormat {

	public CCSVResponseFormat() {
		
		this.strName = CSVTags._ResponseFormat_CSV;
		strMinVersion = "1.0";
		strMaxVersion = "1.0";
		
	}
	
	/*
	@Override
	public CAbstractResponseFormat getNewInstance() {

		CJSONResponseFormat NewInstance = new CJSONResponseFormat(); 
		
    	NewInstance.initResponseFormat( this.ServicesDaemonConfig, this.OwnerConfig );
    	
    	return NewInstance;
	
	}
    */

	@Override
	public String getContentType() {

		if ( OwnerConfig != null )
			return (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_ContentType, null );
		else
			return "";
	
	}

	@Override
	public String getCharacterEncoding() {

		if ( OwnerConfig != null )
			return (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_CharSet, null );
		else
			return "";
	
	}

    public void describeService( CAbstractService Service, StringBuilder strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	if ( Service.getHiddenService() == false ) {
    		
    		String strServiceType = "ReadWrite";
    		
	        if ( Service.getServiceType() == 1 )
	           strServiceType = "Read";
	        else if ( Service.getServiceType() == 2 )
	           strServiceType = "Write";
    		
        	HashMap< String, ArrayList< CInputServiceParameter > > GroupsInputParametersService = Service.getGroupsInputParametersService();   

	        Iterator< Entry< String, ArrayList< CInputServiceParameter > > > It = GroupsInputParametersService.entrySet().iterator();

	        Logger.logMessage( "1", Lang.translate( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
	        
            while ( It.hasNext() ) {
	        
            	try {

            		Entry< String,ArrayList<CInputServiceParameter> > GroupIPSEntry = It.next();

        			String strKey = GroupIPSEntry.getKey();
            		
            		for ( CInputServiceParameter InputServiceParameter : GroupIPSEntry.getValue() ) {

    				    StringBuffer strRowData = new StringBuffer();
                		
    				    if ( bFieldsQuote ) {

                    		if ( Service.getAuthRequired() == true )
                    			strRowData.append( "\"yes\"" );
                    		else
                    			strRowData.append( "\"no\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );
    				    	
                    		strRowData.append( "\"" + Service.getServiceName() + "\"" );

                    		strRowData.append( strSeparatorSymbol );
                    		
                    		strRowData.append( "\"" + strServiceType + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + Service.getServiceDescription() + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + Service.getServiceAuthor() + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + Service.getServiceAuthorContact() + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + strKey + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + InputServiceParameter.getParameterName() + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + new String( InputServiceParameter.getParameterRequired()?"\"Yes\"":"\"No\"" ) + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + InputServiceParameter.getParameterDataType() + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + InputServiceParameter.getParameterDataTypeWidth() + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + InputServiceParameter.getParameterScope().toString() + "\"" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( "\"" + InputServiceParameter.getParameterDescription() + "\"" );
                    		
    				    }
    				    else {
    				    	
                    		if ( Service.getAuthRequired() == true )
                    			strRowData.append( "yes" );
                    		else
                    			strRowData.append( "no" );
    				    	
                    		strRowData.append( strSeparatorSymbol );
    				    	
                    		strRowData.append( Service.getServiceName() );

                    		strRowData.append( strSeparatorSymbol );
                    		
                    		strRowData.append( strServiceType );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( Service.getServiceDescription() );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( Service.getServiceAuthor() );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( Service.getServiceAuthorContact() );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( strKey );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( InputServiceParameter.getParameterName() );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( InputServiceParameter.getParameterRequired()?"Yes":"No" );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( InputServiceParameter.getParameterDataType() );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( InputServiceParameter.getParameterDataTypeWidth() );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( InputServiceParameter.getParameterScope().toString() );
                    		
                    		strRowData.append( strSeparatorSymbol );

                    		strRowData.append( InputServiceParameter.getParameterDescription() );
                    		
    				    }
            			
    				    strResponseBuffer.append( strRowData.toString() );
    				    strResponseBuffer.append( "\r\n" );
    				    
            		}
            	
            	}
            	catch ( Exception Ex ) {
            		
                	Logger.logException( "-1010", Ex.getMessage(), Ex );
            		
            	}
            	
            } 	
    	
    	}
    	
    }

	@Override
	public String enumerateServices( HashMap<String, CAbstractService> RegisteredServices, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";
		
		try {

			if ( Utilities.versionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.versionLessEquals( strVersion, this.strMaxVersion ) ) {
			
				String strShowHeaders = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_ShowHeaders, null );
				
				StringBuilder strResponseBuffer = new StringBuilder();
				
				String strSeparatorSymbol = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_SeparatorSymbol, null );

				String strFieldsQuote = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_FieldsQuote, null );
				
				boolean bFieldsQuote = strFieldsQuote != null && strFieldsQuote.equals( "true" );
				
				if ( strShowHeaders != null && strShowHeaders.equals( "true" ) ) {
					
				    StringBuffer strHeaders = new StringBuffer();
				    
				    if ( bFieldsQuote ) {

				    	strHeaders.append( "\"" + CSVTags._CSV_StructAuthRequired + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructServiceName + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructAccessType + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructDescription + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructAuthor + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructAuthorContact + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructParamSetName + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructParamName + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructRequired + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructType + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructTypeWidth + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructSubType + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructParamDescription + "\"" );
				    	
				    }
				    else {
				    	
				    	strHeaders.append( CSVTags._CSV_StructAuthRequired );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructServiceName );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructAccessType );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructDescription );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructAuthor );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructAuthorContact );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructParamSetName );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructParamName );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructRequired );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructType );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructTypeWidth );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructSubType );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructParamDescription );
				    	
				    }
				    
				    strResponseBuffer.append( strHeaders.toString() );
				    strResponseBuffer.append( "\r\n" );

				};
				
				Iterator<Entry<String, CAbstractService>> it = RegisteredServices.entrySet().iterator();

				while ( it.hasNext() ) {

					Entry<String, CAbstractService> Pairs = it.next();

					String strServName = Pairs.getKey();

					CAbstractService Service = RegisteredServices.get( strServName );

					if ( Service != null )
						this.describeService( Service, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, Logger, Lang );

				}

				strResult = strResponseBuffer.toString();

			}
			else {
				
				if ( Logger != null )
				    Logger.logError( "-1015", OwnerConfig.Lang.translate( "Format version [%s] not supported", strVersion ) );
				else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				    OwnerConfig.Logger.logError( "-1015", OwnerConfig.Lang.translate( "Format version [%s] not supported", strVersion ) );
				
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
	
	public void formatCSVHeaders( ResultSet DataSet, CAbstractDBEngine DBEngine, StringBuilder strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol, CExtendedLogger Logger, CLanguage Lang  ) {

		try {
		
			ResultSetMetaData DataSetMetaData = DataSet.getMetaData();

		    StringBuffer strFieldsName = new StringBuffer();
		    StringBuffer strFieldsType = new StringBuffer();
		    
		    int intColumnCount = DataSetMetaData.getColumnCount();
		    
			for ( int i = 1; i <= intColumnCount; i++ ) {

				int intFieldType = DBEngine.getJavaSQLColumnType( DataSetMetaData.getColumnType( i ), Logger, Lang );
				
				String strFieldName = DataSetMetaData.getColumnName( i );
				String strFieldType = NamesSQLTypes.getJavaSQLTypeName( intFieldType );

            	String strFieldLength = null;
            	
            	if ( NamesSQLTypes.IsString( intFieldType ) ) { // DataSetMetaData.getColumnDisplaySize( i ) > 0 ) {
            		
            		strFieldLength = Integer.toString( DataSetMetaData.getColumnDisplaySize( i ) );
            		
            	}

            	if ( strFieldName == null || strFieldName.isEmpty() == true )
					strFieldName = DataSetMetaData.getColumnLabel( i );

			    if ( bFieldsQuote ) {
			    	
			    	strFieldsName.append( "\"" + strFieldName + "\"" );
            		strFieldsType.append( "\"" + new String( strFieldType + strFieldLength != null?"(" + strFieldLength + ")":"" + "\"" ) );
			    	
			    }
			    else {
			    	
			    	strFieldsName.append( strFieldName );
            		strFieldsType.append( strFieldType + new String( strFieldLength != null?"(" + strFieldLength + ")":"" ) );
			    	
			    }

			    if ( i < intColumnCount ) {
		    	 
			    	strFieldsName.append( strSeparatorSymbol );
		    	    strFieldsType.append( strSeparatorSymbol );
		    	    
			    }   
		    	
			}
			
	    	strResponseBuffer.append( strFieldsName.toString() );
	    	strResponseBuffer.append( "\r\n" );
	    	strResponseBuffer.append( strFieldsType.toString() );
	    	strResponseBuffer.append( "\r\n" );
	    	
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1016", Ex.getMessage(), Ex );
			
		}
		
	}
	
	public void formatCSVHeaders( CMemoryRowSet MemoryRowSet, StringBuilder strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol, CExtendedLogger Logger, CLanguage Lang  ) {
		
		try {
			
		    StringBuffer strFieldsName = new StringBuffer();
		    StringBuffer strFieldsType = new StringBuffer();
			
		    int intColumnCount = MemoryRowSet.getFieldsCount();
		    
            for ( int i = 0; i < intColumnCount; i++ ) {
         	   
            	CMemoryFieldData Field = MemoryRowSet.getFieldByIndex( i );

            	String strFieldName = Field.strName;
            	String strFieldType = Field.strSQLTypeName;
            	String strFieldLength = null;
            	
            	if ( NamesSQLTypes.IsString( Field.intSQLType ) ) {
            		
            		strFieldLength = Integer.toString( Field.intLength );
            		
            	}
            		
            	if ( bFieldsQuote ) {

            		strFieldsName.append( "\"" + strFieldName + "\"" );
            		strFieldsType.append( "\"" + strFieldType + new String( strFieldLength != null?"(" + strFieldLength + ")":"" + "\"" ) );

            	}
            	else {

            		strFieldsName.append( strFieldName );
            		strFieldsType.append( strFieldType + new String( strFieldLength != null?"(" + strFieldLength + ")":"" ) );

            	}

			    if ( i + 1 < intColumnCount ) {
			    	 
			    	strFieldsName.append( strSeparatorSymbol );
		    	    strFieldsType.append( strSeparatorSymbol );
		    	    
			    }   
            
            }  
            
	    	strResponseBuffer.append( strFieldsName.toString() );
	    	strResponseBuffer.append( "\r\n" );
	    	strResponseBuffer.append( strFieldsType.toString() );
	    	strResponseBuffer.append( "\r\n" );
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1016", Ex.getMessage(), Ex );
			
		}
	
	}

	public void formatCSVRowData( String strTempDir, String strTempFile, PrintWriter TempResponseFormatedFileWriter, OutputStream TempStreamResponseFormatedFile, ResultSet SQLDataSet, CAbstractDBEngine DBEngine, boolean bFieldsQuote, String strSeparatorSymbol, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang  ) {
		
		try {
			
			java.sql.ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();

		    int intColumnCount = DataSetMetaData.getColumnCount();
		    
		    while ( SQLDataSet.next() == true ) {

			    for ( int i = 1; i <= intColumnCount; i++ ) {

		    		String strFieldName = DataSetMetaData.getColumnName( i );

		    		if ( strFieldName == null || strFieldName.isEmpty() == true )
		    			strFieldName = DataSetMetaData.getColumnLabel( i );

		    		int intFieldType = DataSetMetaData.getColumnType( i );

		    		intFieldType = DBEngine.getJavaSQLColumnType( intFieldType, Logger, Lang );

		    		if ( SQLDataSet.getObject( i ) != null ) {

						if ( intFieldType == Types.BLOB ) {
						
			    			if ( bFieldsQuote ) {

	            				TempResponseFormatedFileWriter.print( "\"" );
	            				TempResponseFormatedFileWriter.flush();
			    				DBEngine.writeBlobValueAsStringToFile( intFieldType, strFieldName, strTempDir, strTempFile, TempStreamResponseFormatedFile, SQLDataSet, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang  );
	            				TempResponseFormatedFileWriter.print( "\"" );
			    			
			    			}
			    			else {
			    				
			    				DBEngine.writeBlobValueAsStringToFile( intFieldType, strFieldName, strTempDir, strTempFile, TempStreamResponseFormatedFile, SQLDataSet, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang  );
			    				
			    			}
            				
						}
						else {

			    			if ( bFieldsQuote ) {

			    				TempResponseFormatedFileWriter.print( "\"" + DBEngine.getFieldValueAsString( intFieldType, strFieldName, SQLDataSet, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang ) + "\"" );
			    			
			    			}
			    			else {
			    				
			    				TempResponseFormatedFileWriter.print( DBEngine.getFieldValueAsString( intFieldType, strFieldName, SQLDataSet, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang ) );

			    			}
							
						}

		    		}
		    		else {
		    			
		    			if ( bFieldsQuote ) {
			    		
		    				TempResponseFormatedFileWriter.print( "\"null\"" );

		    			}
		    			else {
		    				
		    				TempResponseFormatedFileWriter.print( "null" );
		    				
		    			}

		    		}
		    		
		    		if ( i < intColumnCount )
		    			TempResponseFormatedFileWriter.print( strSeparatorSymbol );

		    	}
		    	
			    TempResponseFormatedFileWriter.println( "" );

		    }  
		
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1016", Ex.getMessage(), Ex );
			
		}
        
	}

	public void formatCSVRowData( CMemoryRowSet MemoryRowSet, StringBuilder strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang  ) {
		
		try {

			int intRowCount = MemoryRowSet.getRowCount();
			
			for ( int intRowIndex = 0; intRowIndex < intRowCount; intRowIndex++ ) {

				String strRow = MemoryRowSet.RowToString( intRowIndex, bFieldsQuote, strSeparatorSymbol, strDateFormat, strTimeFormat, strDateTimeFormat, false, Logger, Lang );
				
				strResponseBuffer.append( strRow );
		    	strResponseBuffer.append( "\r\n" );
				
			}	
		
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1016", Ex.getMessage(), Ex );
			
		}
		
	}

	public void formatDefaultHeaders( StringBuilder strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol ) {
		
		StringBuilder strFieldsName = new StringBuilder();
		StringBuilder strFieldsType = new StringBuilder();

		if ( bFieldsQuote ) {

			strFieldsName.append( "\"" + CSVTags._CSV_StructAffectedRows + "\"" );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsName.append( "\"" + CSVTags._CSV_StructCode + "\"" );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsName.append( "\"" + CSVTags._CSV_StructDescription + "\"" );

			strFieldsType.append( "\"" + CSVTags._FieldTypeBigInt  + "\"" );
			strFieldsType.append( strSeparatorSymbol );
			strFieldsType.append( "\"" + CSVTags._FieldTypeInteger + "\"" );
			strFieldsType.append( strSeparatorSymbol );
			strFieldsType.append( "\"" + CSVTags._FieldTypeString + "(" + CSVTags._CSV_StructDescriptionLength + ")\"" );

		}
		else {

			strFieldsName.append( CSVTags._CSV_StructAffectedRows );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsName.append( CSVTags._CSV_StructCode );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsName.append( CSVTags._CSV_StructDescription );

			strFieldsType.append( CSVTags._FieldTypeBigInt );
			strFieldsType.append( strSeparatorSymbol );
			strFieldsType.append( CSVTags._FieldTypeInteger );
			strFieldsType.append( strSeparatorSymbol );
			strFieldsType.append( CSVTags._FieldTypeString + "(" + CSVTags._CSV_StructDescriptionLength + ")" );

		}

		strResponseBuffer.append( strFieldsName.toString() );
    	strResponseBuffer.append( "\r\n" );
		strResponseBuffer.append( strFieldsType.toString() );
    	strResponseBuffer.append( "\r\n" );
		
	}
	
	@Override
	public boolean formatResultSet( HttpServletResponse Response, CResultDataSet ResultDataSet, CAbstractDBEngine DBEngine, int intInternalFetchSize, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, boolean bDeleteTempReponseFile, CExtendedLogger Logger, CLanguage Lang ) { 
		
    	boolean bResult = false;
    	
        try {
        	
			if ( Utilities.versionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.versionLessEquals( strVersion, this.strMaxVersion ) ) {

		        String strTempDir = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Temp_Dir, null );
		        
		        String strTempResponseFormatedFilePath = strTempDir + UUID.randomUUID() + ".formated_response";
	    		
	            ServletOutputStream OutStream = Response.getOutputStream(); //new FileOutputStream( strTempResponseFormatedFilePath ); 
		        
		        PrintWriter TempResponseFormatedFileWriter = new PrintWriter( OutStream ); // strTempResponseFormatedFilePath, this.getCharacterEncoding() );
				
				String strShowHeaders = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_ShowHeaders, null );
				String strSeparatorSymbol = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_SeparatorSymbol, null );
				String strFieldsQuote = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_FieldsQuote, null );
				
				boolean bFieldsQuote = strFieldsQuote != null && strFieldsQuote.equals( "true" );
				
				strSeparatorSymbol = (strSeparatorSymbol != null && strSeparatorSymbol.isEmpty() == false)?strSeparatorSymbol:";";
				
    	        if ( ResultDataSet.Result != null && ResultDataSet.Result instanceof ResultSet == false ) {
    	        	
    				if ( Logger != null ) {
    					
    					if ( Lang != null )
    						Logger.logWarning( "-1", Lang.translate( "Result object type [%s] not supported", ResultDataSet.getClass().getName() ) );
    					else
    						Logger.logWarning( "-1", String.format( "Result object type [%s] not supported", ResultDataSet.getClass().getName() ) );
    				    
    				}    
    				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

    					if ( OwnerConfig.Lang != null )
    						OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Result object type [%s] not supported", ResultDataSet.getClass().getName() ) );
    					else
    						OwnerConfig.Logger.logWarning( "-1", String.format( "Result object type [%s] not supported", ResultDataSet.getClass().getName() ) );

    				}    
    	        	
    	        }
		        
				if ( ResultDataSet.Result != null && ResultDataSet.Result instanceof ResultSet && ResultDataSet.intCode >= 0 ) {

					if ( strShowHeaders != null && strShowHeaders.equals( "true" ) ) {
					
						StringBuilder strResponseBuffer = new StringBuilder();

						formatCSVHeaders( (ResultSet) ResultDataSet.Result, DBEngine, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, Logger, Lang );

	        			TempResponseFormatedFileWriter.print( strResponseBuffer.toString() );
	        			
	        			strResponseBuffer = null;
	        			
					}
					
					formatCSVRowData( strTempDir, strTempResponseFormatedFilePath, TempResponseFormatedFileWriter, OutStream, (ResultSet) ResultDataSet.Result, DBEngine, bFieldsQuote, strSeparatorSymbol, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );

        			TempResponseFormatedFileWriter.close();

        			TempResponseFormatedFileWriter = null;
					
				}
				else {

					StringBuilder strResponseBuffer = new StringBuilder();
					
					if ( strShowHeaders != null && strShowHeaders.equals( "true" ) ) {

						formatDefaultHeaders( strResponseBuffer, bFieldsQuote, strSeparatorSymbol );

					}

					if ( bFieldsQuote ) {

						strResponseBuffer.append( "\"" + Long.toString( ResultDataSet.lngAffectedRows ) + "\"" );
						strResponseBuffer.append( strSeparatorSymbol );
						strResponseBuffer.append( "\"" + ResultDataSet.intCode + "\"" );
						strResponseBuffer.append( strSeparatorSymbol );
						strResponseBuffer.append( "\"" + ResultDataSet.strDescription + "\"" );

					}
					else {

						strResponseBuffer.append( Long.toString( ResultDataSet.lngAffectedRows ) );
						strResponseBuffer.append( strSeparatorSymbol );
						strResponseBuffer.append( ResultDataSet.intCode );
						strResponseBuffer.append( strSeparatorSymbol );
						strResponseBuffer.append( ResultDataSet.strDescription );

					}

        			TempResponseFormatedFileWriter.print( strResponseBuffer.toString() );

        			strResponseBuffer = null;
        			
        			TempResponseFormatedFileWriter.close();

        			TempResponseFormatedFileWriter = null;
					
				}

				/****
    			File TempResponseFormatedFile = new File( strTempResponseFormatedFilePath ); 

    			this.CopyToResponseStream( Response, TempResponseFormatedFile, 10240, Logger, Lang );

    			if ( bDeleteTempReponseFile )
    				TempResponseFormatedFile.delete();
				****/
				
    			bResult = true;
    			
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
	public boolean formatResultsSets( HttpServletResponse Response, ArrayList<CResultDataSet> ResultDataSetList, CAbstractDBEngine DBEngine, int intInternalFetchSize, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, boolean bDeleteTempReponseFile, CExtendedLogger Logger, CLanguage Lang, int intDummyParam ) {
		
    	boolean bResult = false;
    	
        try {

			if ( Utilities.versionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.versionLessEquals( strVersion, this.strMaxVersion ) ) {
        	
				if ( ResultDataSetList.size() > 0 ) {

			        String strTempDir = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Temp_Dir, null );
			        
			        String strTempResponseFormatedFilePath = strTempDir + UUID.randomUUID() + ".formated_response";
		    		
		            FileOutputStream TempFileOutStream = new FileOutputStream( strTempResponseFormatedFilePath ); 
			        
			        PrintWriter TempResponseFormatedFileWriter = new PrintWriter( TempFileOutStream ); // strTempResponseFormatedFilePath, this.getCharacterEncoding() );
					
					String strShowHeaders = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_ShowHeaders, null );
					String strSeparatorSymbol = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_SeparatorSymbol, null );
					String strFieldsQuote = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_FieldsQuote, null );
					
					boolean bFieldsQuote = strFieldsQuote != null && strFieldsQuote.equals( "true" );
					
					strSeparatorSymbol = (strSeparatorSymbol != null && strSeparatorSymbol.isEmpty() == false)?strSeparatorSymbol:";";

					Object ResultObject = (ResultSet) CResultDataSet.getFirstResultSetNotNull( ResultDataSetList );

	    	        if ( ResultObject != null && ResultObject instanceof ResultSet == false ) {
	    	        	
	    				if ( Logger != null ) {
	    					
	    					if ( Lang != null )
	    						Logger.logWarning( "-1", Lang.translate( "Result object type [%s] not supported", ResultObject.getClass().getName() ) );
	    					else
	    						Logger.logWarning( "-1", String.format( "Result object type [%s] not supported", ResultObject.getClass().getName() ) );
	    				    
	    				}    
	    				else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

	    					if ( OwnerConfig.Lang != null )
	    						OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Result object type [%s] not supported", ResultObject.getClass().getName() ) );
	    					else
	    						OwnerConfig.Logger.logWarning( "-1", String.format( "Result object type [%s] not supported", ResultObject.getClass().getName() ) );

	    				}    
	    	        	
	    	        }
	    	        
					if ( ResultObject != null && ResultObject instanceof ResultSet ) {

						StringBuilder strResponseBuffer = new StringBuilder();

						ResultSet ResultDataSet = ( ResultSet ) ResultObject;
						
						if ( strShowHeaders != null && strShowHeaders.equalsIgnoreCase( "true" ) )
							formatCSVHeaders( ResultDataSet, DBEngine, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, Logger, Lang );

	        			TempResponseFormatedFileWriter.print( strResponseBuffer.toString() );
	        			
	        			strResponseBuffer = null;
						
						for ( CResultDataSet ResultDataSetToAdd: ResultDataSetList ) {    

							if ( ResultDataSetToAdd.Result != null && ResultDataSetToAdd.Result instanceof ResultSet ) {   

								formatCSVRowData( strTempDir, strTempResponseFormatedFilePath, TempResponseFormatedFileWriter, TempFileOutStream, ((ResultSet) ResultDataSetToAdd.Result), DBEngine, bFieldsQuote, strSeparatorSymbol, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
								
							}

						}

	        			TempResponseFormatedFileWriter.close();

	        			TempResponseFormatedFileWriter = null;
						
					}
					else {

						StringBuilder strResponseBuffer = new StringBuilder();
						
						if ( strShowHeaders != null && strShowHeaders.equals( "true" ) ) {

							formatDefaultHeaders( strResponseBuffer, bFieldsQuote, strSeparatorSymbol );
						
						}
						
						for ( CResultDataSet ResultDataSetToAdd: ResultDataSetList ) {    

							StringBuilder strRowData = new StringBuilder();
							
							if ( bFieldsQuote ) {

								strRowData.append( "\"" + Long.toString( ResultDataSetToAdd.lngAffectedRows ) + "\"" );
								strRowData.append( strSeparatorSymbol );
								strRowData.append( "\"" + ResultDataSetToAdd.intCode + "\"" );
								strRowData.append( strSeparatorSymbol );
								strRowData.append( "\"" + ResultDataSetToAdd.strDescription + "\"" );
															
							}
							else {
								
								strRowData.append( Long.toString( ResultDataSetToAdd.lngAffectedRows ) );
								strRowData.append( strSeparatorSymbol );
								strRowData.append( ResultDataSetToAdd.intCode );
								strRowData.append( strSeparatorSymbol );
								strRowData.append( ResultDataSetToAdd.strDescription );
								
							}

							strResponseBuffer.append( strRowData.toString() );
							
						}
						
	        			TempResponseFormatedFileWriter.print( strResponseBuffer.toString() );

						strResponseBuffer = null;
						
	        			TempResponseFormatedFileWriter.close();

	        			TempResponseFormatedFileWriter = null;
						
					}

	    			File TempResponseFormatedFile = new File( strTempResponseFormatedFilePath ); 

	    			this.copyToResponseStream( Response, TempResponseFormatedFile, 10240, Logger, Lang );

	    			if ( bDeleteTempReponseFile )
	    				TempResponseFormatedFile.delete();

	    			bResult = true;
	    			
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

				StringBuilder strResponseBuffer = new StringBuilder();
				
				String strShowHeaders = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_ShowHeaders, null );
				String strSeparatorSymbol = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_SeparatorSymbol, null );
				String strFieldsQuote = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_FieldsQuote, null );
				
				if ( strShowHeaders != null && strShowHeaders.equals( "true" ) )
				   formatCSVHeaders( MemoryRowSet, strResponseBuffer, strFieldsQuote != null && strFieldsQuote.equals( "true" ), strSeparatorSymbol, Logger, Lang );
				
				formatCSVRowData( MemoryRowSet, strResponseBuffer, strFieldsQuote != null && strFieldsQuote.equals( "true" ), (strSeparatorSymbol != null && strSeparatorSymbol.isEmpty() == false)?strSeparatorSymbol:";", strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
				
				strResult = strResponseBuffer.toString();

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
	
	@Override
	public String formatSimpleMessage( String strSecurityTokenID, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";
		
		try {

			if ( Utilities.versionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.versionLessEquals( strVersion, this.strMaxVersion ) ) {
			
				String strShowHeaders = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_ShowHeaders, null );
				
				StringBuilder strResponseBuffer = new StringBuilder();
				
				String strSeparatorSymbol = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_SeparatorSymbol, null );

				String strFieldsQuote = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._CSV_FieldsQuote, null );
				
				boolean bFieldsQuote = strFieldsQuote != null && strFieldsQuote.equals( "true" );
				
				if ( strShowHeaders != null && strShowHeaders.equals( "true" ) ) {
					
				    StringBuffer strFieldsName = new StringBuffer();
				    StringBuffer strFieldsType = new StringBuffer();
				    
				    if ( bFieldsQuote ) {

				    	if ( strSecurityTokenID != null && strSecurityTokenID.isEmpty() == false ) {
				    	
				    		strFieldsName.append( "\"" + CSVTags._CSV_StructSecurityTokenID + "\"" );
				    	    strFieldsName.append( strSeparatorSymbol );
				    	    strFieldsType.append( "\"" + CSVTags._FieldTypeBigInt + "\"" );
				    	    strFieldsType.append( strSeparatorSymbol );
				    	    
				    	}
				    	
				    	if ( strTransactionID != null && strTransactionID.isEmpty() == false ) {

				    		strFieldsName.append( "\"" + CSVTags._CSV_StructTransactionID + "\"" );
				    		strFieldsName.append( strSeparatorSymbol );
				    	    strFieldsType.append( "\"" + CSVTags._FieldTypeBigInt + "\"" );
				    	    strFieldsType.append( strSeparatorSymbol );
				    		
				    	}
				    	
				    	strFieldsName.append( "\"" + CSVTags._CSV_StructCode + "\"" );
				    	strFieldsName.append( strSeparatorSymbol );
				    	strFieldsName.append( "\"" + CSVTags._CSV_StructDescription + "\"" );
				    	
			    	    strFieldsType.append( "\"" + CSVTags._FieldTypeInteger + "\"" );
			    	    strFieldsType.append( strSeparatorSymbol );
			    	    strFieldsType.append( "\"" + CSVTags._FieldTypeString + "(" + CSVTags._CSV_StructDescriptionLength + ")\"" );
			    	    
				    }
				    else {
				    	
				    	if ( strSecurityTokenID != null && strSecurityTokenID.isEmpty() == false ) {

				    		strFieldsName.append( CSVTags._CSV_StructSecurityTokenID );
				    		strFieldsName.append( strSeparatorSymbol );
				    	    strFieldsType.append( CSVTags._FieldTypeBigInt );
				    	    strFieldsType.append( strSeparatorSymbol );
				    		
				    	}
				    	
				    	if ( strTransactionID != null && strTransactionID.isEmpty() == false ) {
				    	
				    		strFieldsName.append( CSVTags._CSV_StructTransactionID );
				    		strFieldsName.append( strSeparatorSymbol );
				    	    strFieldsType.append( CSVTags._FieldTypeBigInt );
				    	    strFieldsType.append( strSeparatorSymbol );
				    		
				    	}	
				    	
				    	strFieldsName.append( CSVTags._CSV_StructCode );
				    	strFieldsName.append( strSeparatorSymbol );
				    	strFieldsName.append( CSVTags._CSV_StructDescription );
				    	
			    	    strFieldsType.append( CSVTags._FieldTypeInteger );
			    	    strFieldsType.append( strSeparatorSymbol );
			    	    strFieldsType.append( CSVTags._FieldTypeString + "(" + CSVTags._CSV_StructDescriptionLength + ")" );
				    	
				    }
				    
				    strResponseBuffer.append( strFieldsName.toString() );
				    strResponseBuffer.append( "\r\n" );
				    strResponseBuffer.append( strFieldsType.toString() );
				    strResponseBuffer.append( "\r\n" );

				};

			    StringBuffer strRowData = new StringBuffer();
			    
			    if ( bFieldsQuote ) {

			    	if ( strSecurityTokenID != null && strSecurityTokenID.isEmpty() == false ) {

			    		strRowData.append( "\"" + strSecurityTokenID + "\"" );
				    	strRowData.append( "\"" + strSeparatorSymbol + "\"" );
			    		
			    	}
			    	
			    	if ( strTransactionID != null && strTransactionID.isEmpty() == false ) {

			    		strRowData.append( "\"" + strTransactionID + "\"" );
				    	strRowData.append( "\"" + strSeparatorSymbol + "\"" );
			    	
			    	}
			    	
			    	strRowData.append( "\"" + Integer.toString( intCode ) + "\"" );
			    	strRowData.append( "\"" + strSeparatorSymbol + "\"" );
			    	strRowData.append( "\"" + strDescription + "\"" );
			    	
			    }	
			    else {
			    
			    	if ( strSecurityTokenID != null && strSecurityTokenID.isEmpty() == false ) {

			    		strRowData.append( strSecurityTokenID );
				    	strRowData.append( strSeparatorSymbol );
			    	}
			    	
			    	if ( strTransactionID != null && strTransactionID.isEmpty() == false ) {

			    		strRowData.append( strTransactionID );
				    	strRowData.append( strSeparatorSymbol );
			    	
			    	}
			    	
			    	strRowData.append( intCode );
			    	strRowData.append( strSeparatorSymbol );
			    	strRowData.append( strDescription );
			    	
			    }
				
			    strResponseBuffer.append( strRowData.toString() );
			    strResponseBuffer.append( "\r\n" );

			    strResult = strResponseBuffer.toString();
			
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
