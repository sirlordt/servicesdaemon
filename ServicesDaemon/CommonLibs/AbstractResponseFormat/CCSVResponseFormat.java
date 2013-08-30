package AbstractResponseFormat;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.maindataservices.Utilities;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultSetResult;
import CommonClasses.NamesSQLTypes;
import ExtendedLogger.CExtendedLogger;

public class CCSVResponseFormat extends CAbstractResponseFormat {

	public CCSVResponseFormat() {
		
		this.strName = CSVTags._ResponseFormat_CSV;
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
			return OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ContentType );
		else
			return "";
	
	}

	@Override
	public String getCharacterEncoding() {

		if ( OwnerConfig != null )
			return OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_CharSet );
		else
			return "";
	
	}

    public void DescribeService( CAbstractService Service, ArrayList<String> strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	if ( Service.getHiddenService() == false ) {
    		
    		String strServiceType = "ReadWrite";
    		
	        if ( Service.getServiceType() == 1 )
	           strServiceType = "Read";
	        else if ( Service.getServiceType() == 2 )
	           strServiceType = "Write";
    		
        	HashMap< String, ArrayList< CInputServiceParameter > > GroupsInputParametersService = Service.getGroupsInputParametersService();   

	        Iterator< Entry< String, ArrayList< CInputServiceParameter > > > It = GroupsInputParametersService.entrySet().iterator();

	        Logger.LogMessage( "1", Lang.Translate( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
	        
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
            			
            		}
            	
            	}
            	catch ( Exception Ex ) {
            		
                	Logger.LogException( "-1010", Ex.getMessage(), Ex );
            		
            	}
            	
            } 	
    	
    	}
    	
    }

	@Override
	public String EnumerateServices( HashMap<String, CAbstractService> RegisteredServices, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";
		
		try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {
			
				String strShowHeaders = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ShowHeaders );
				
				ArrayList<String> strResponseBuffer = new ArrayList<String>();
				
				String strSeparatorSymbol = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_SeparatorSymbol );

				String strFieldsQuote = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_FieldsQuote );
				
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
				    
				    strResponseBuffer.add( strHeaders.toString() );

				};
				
				Iterator<Entry<String, CAbstractService>> it = RegisteredServices.entrySet().iterator();

				while ( it.hasNext() ) {

					Entry<String, CAbstractService> Pairs = it.next();

					String strServName = (String) Pairs.getKey();

					CAbstractService Service = RegisteredServices.get( strServName );

					if ( Service != null )
						this.DescribeService( Service, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, Logger, Lang );

				}

				strResult = strResponseBuffer.toString();

			}
			else {
				
				if ( Logger != null )
				    Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
				else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				    OwnerConfig.Logger.LogError( "-1015", OwnerConfig.Lang.Translate( "Format version [%s] not supported", strVersion ) );
				
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

	
	public void FormatCSVHeaders( ResultSet SQLDataSet, CAbstractDBEngine DBEngine, ArrayList<String> strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol, CExtendedLogger Logger, CLanguage Lang  ) {

		try {
		
			ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();

		    StringBuffer strFieldsName = new StringBuffer();
		    StringBuffer strFieldsType = new StringBuffer();
		    
			for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

				int intFieldType = DBEngine.getJavaSQLColumnType( DataSetMetaData.getColumnType( i ), Logger, Lang );
				
				String strFieldName = DataSetMetaData.getColumnName( i );
				String strFieldType = NamesSQLTypes.getJavaSQLTypeName( intFieldType );

            	String strFieldLength = null;
            	
            	if ( DataSetMetaData.getColumnDisplaySize( i ) > 0 ) {
            		
            		strFieldLength = Integer.toString( DataSetMetaData.getColumnDisplaySize( i ) );
            		
            	}

            	if ( strFieldName == null || strFieldName.isEmpty() == true )
					strFieldName = DataSetMetaData.getColumnLabel( i );

			    if ( bFieldsQuote ) {
			    	
			    	strFieldsName.append( "\"" + strFieldName + "\"" );
            		strFieldsType.append( "\"" + strFieldType + strFieldLength != null?"(" + strFieldLength + ")":"" + "\"" );
			    	
			    }
			    else {
			    	
			    	strFieldsName.append( strFieldName );
            		strFieldsType.append( strFieldType + strFieldLength != null?"(" + strFieldLength + ")":"" );
			    	
			    }

		    	strFieldsName.append( strSeparatorSymbol );
		    	strFieldsType.append( strSeparatorSymbol );
		    	
			}
			
	    	strResponseBuffer.add( strFieldsName.toString() );
	    	strResponseBuffer.add( strFieldsType.toString() );
	    	
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );
			
		}
		
	}
	
	public void FormatCSVHeaders( CMemoryRowSet MemoryRowSet, ArrayList<String> strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol, CExtendedLogger Logger, CLanguage Lang  ) {
		
		try {
			
		    StringBuffer strFieldsName = new StringBuffer();
		    StringBuffer strFieldsType = new StringBuffer();
			
            for ( int i = 0; i < MemoryRowSet.getFieldsCount(); i++ ) {
         	   
            	CMemoryFieldData Field = MemoryRowSet.getFieldByIndex( i );

            	String strFieldName = Field.strName;
            	String strFieldType = Field.strSQLTypeName;
            	String strFieldLength = null;
            	
            	if ( Field.intLength > 0 ) {
            		
            		strFieldLength = Integer.toString( Field.intLength );
            		
            	}
            		
            	if ( bFieldsQuote ) {

            		strFieldsName.append( "\"" + strFieldName + "\"" );
            		strFieldsType.append( "\"" + strFieldType + strFieldLength != null?"(" + strFieldLength + ")":"" + "\"" );

            	}
            	else {

            		strFieldsName.append( strFieldName );
            		strFieldsType.append( strFieldType + strFieldLength != null?"(" + strFieldLength + ")":"" );

            	}

            	strFieldsName.append( strSeparatorSymbol );
            	strFieldsType.append( strSeparatorSymbol );
            
            }  
            
	    	strResponseBuffer.add( strFieldsName.toString() );
	    	strResponseBuffer.add( strFieldsType.toString() );
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );
			
		}
	
	}

	public void FormatCSVRowData( ResultSet SQLDataSet, CAbstractDBEngine DBEngine, ArrayList<String> strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang  ) {
		
		try {
			
			java.sql.ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();

		    while ( SQLDataSet.next() == true ) {

			    StringBuffer strRow = new StringBuffer();
				
		    	for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

		    		String strFieldName = DataSetMetaData.getColumnName( i );

		    		if ( strFieldName == null || strFieldName.isEmpty() == true )
		    			strFieldName = DataSetMetaData.getColumnLabel( i );

		    		int intFieldType = DataSetMetaData.getColumnType( i );

		    		intFieldType = DBEngine.getJavaSQLColumnType( intFieldType, Logger, Lang );

		    		if ( SQLDataSet.getObject( i ) != null ) {

		    			String strFieldValue = DBEngine.getFieldValueAsString( intFieldType, strFieldName, SQLDataSet, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );

		    			if ( bFieldsQuote ) {

		    				strRow.append( "\"" + strFieldValue + "\"" );

		    			}
		    			else {

		    				strRow.append( strFieldValue );

		    			}

		    		}
		    		else if ( bFieldsQuote ) {

		    			strRow.append( "\"\"" );

		    		}
		    		
		    		strRow.append( strSeparatorSymbol );

		    	}
		    	
		    	strResponseBuffer.add( strRow.toString() );

		    }  
		
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );
			
		}
        
	}

	public void FormatCSVRowData( CMemoryRowSet MemoryRowSet, ArrayList<String> strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol, String strDateFormat, String strTimeFormat, String strDateTimeFormat, CExtendedLogger Logger, CLanguage Lang  ) {
		
		try {

			int intRowCount = MemoryRowSet.getRowCount();
			
			for ( int intRowIndex = 0; intRowIndex < intRowCount; intRowIndex++ ) {

				String strRow = MemoryRowSet.RowToString( intRowIndex, bFieldsQuote, strSeparatorSymbol, strDateFormat, strTimeFormat, strDateTimeFormat, false, Logger, Lang );
				
				strResponseBuffer.add( strRow );
				
			}	
		
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1016", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1016", Ex.getMessage(), Ex );
			
		}
		
	}
	
	@Override
	public String FormatResultSet( ResultSet SQLDataSet, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";

		try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {

				ArrayList<String> strResponseBuffer = new ArrayList<String>();
				
				String strShowHeaders = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ShowHeaders );
				String strSeparatorSymbol = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_SeparatorSymbol );
				String strFieldsQuote = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_FieldsQuote );
				
				if ( strShowHeaders != null && strShowHeaders.equals( "true" ) )
				   FormatCSVHeaders( SQLDataSet, DBEngine, strResponseBuffer, strFieldsQuote != null && strFieldsQuote.equals( "true" ), strSeparatorSymbol, Logger, Lang );
				
				FormatCSVRowData( SQLDataSet, DBEngine, strResponseBuffer, strFieldsQuote != null && strFieldsQuote.equals( "true" ), (strSeparatorSymbol != null && strSeparatorSymbol.isEmpty() == false)?strSeparatorSymbol:";", strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
				
				strResult = strResponseBuffer.toString();

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
	public String FormatResultsSets( ArrayList<ResultSet> SQLDataSetList, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		String strResult = "";

		try {

			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {

				ArrayList<String> strResponseBuffer = new ArrayList<String>();
				
				String strShowHeaders = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ShowHeaders );
				String strSeparatorSymbol = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_SeparatorSymbol );
				String strFieldsQuote = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_FieldsQuote );
				
				boolean bFieldsQuote = strFieldsQuote != null && strFieldsQuote.equals( "true" );
				
				strSeparatorSymbol = (strSeparatorSymbol != null && strSeparatorSymbol.isEmpty() == false)?strSeparatorSymbol:";";

				if ( strShowHeaders != null && strShowHeaders.equals( "true" ) )
					FormatCSVHeaders( SQLDataSetList.get( 0 ), DBEngine, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, Logger, Lang );
				
				for ( int I = 0; I < SQLDataSetList.size(); I ++ ) {
				    
					FormatCSVRowData( SQLDataSetList.get( I ), DBEngine, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
				
				};
				
				strResult = strResponseBuffer.toString();

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

	public void FormatDefaultHeaders( ArrayList<String> strResponseBuffer, boolean bFieldsQuote, String strSeparatorSymbol ) {
		
		StringBuilder strFieldsName = new StringBuilder();
		StringBuilder strFieldsType = new StringBuilder();

		if ( bFieldsQuote ) {

			strFieldsName.append( "\"" + CSVTags._CSV_StructAffectedRows + "\"" );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsName.append( "\"" + CSVTags._CSV_StructCode + "\"" );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsName.append( "\"" + CSVTags._CSV_StructDescription + "\"" );
			strFieldsName.append( strSeparatorSymbol );

			strFieldsType.append( "\"" + CSVTags._FieldTypeBigInt  + "\"" );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsType.append( "\"" + CSVTags._FieldTypeInteger + "\"" );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsType.append( "\"" + CSVTags._FieldTypeString + "(" + CSVTags._CSV_StructDescriptionLength + ")\"" );
			strFieldsName.append( strSeparatorSymbol );

		}
		else {

			strFieldsName.append( CSVTags._CSV_StructAffectedRows );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsName.append( CSVTags._CSV_StructCode );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsName.append( CSVTags._CSV_StructDescription );
			strFieldsName.append( strSeparatorSymbol );

			strFieldsType.append( CSVTags._FieldTypeBigInt );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsType.append( CSVTags._FieldTypeInteger );
			strFieldsName.append( strSeparatorSymbol );
			strFieldsType.append( CSVTags._FieldTypeString + "(" + CSVTags._CSV_StructDescriptionLength + ")" );
			strFieldsName.append( strSeparatorSymbol );

		}

		strResponseBuffer.add( strFieldsName.toString() );
		strResponseBuffer.add( strFieldsType.toString() );
		
	}
	
	@Override
	public String FormatResultSet( CResultSetResult SQLDataSetResult, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) { 
		
    	String strResult = "";
    	
        try {
        	
			if ( Utilities.VersionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.VersionLessEquals( strVersion, this.strMaxVersion ) ) {

				ArrayList<String> strResponseBuffer = new ArrayList<String>();
				
				String strShowHeaders = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ShowHeaders );
				String strSeparatorSymbol = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_SeparatorSymbol );
				String strFieldsQuote = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_FieldsQuote );
				
				boolean bFieldsQuote = strFieldsQuote != null && strFieldsQuote.equals( "true" );
				
				strSeparatorSymbol = (strSeparatorSymbol != null && strSeparatorSymbol.isEmpty() == false)?strSeparatorSymbol:";";
				
				if ( SQLDataSetResult != null ) {

					if ( SQLDataSetResult.Result != null ) {   

						if ( strShowHeaders != null && strShowHeaders.equals( "true" ) )
							FormatCSVHeaders( SQLDataSetResult.Result, DBEngine, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, Logger, Lang );

						FormatCSVRowData( SQLDataSetResult.Result, DBEngine, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
						
					}
					else {

						if ( strShowHeaders != null && strShowHeaders.equals( "true" ) ) {

							FormatDefaultHeaders( strResponseBuffer, bFieldsQuote, strSeparatorSymbol );
						
						}
						
						StringBuilder strRowData = new StringBuilder();
						
						if ( bFieldsQuote ) {

							strRowData.append( "\"" + Long.toString( SQLDataSetResult.lngAffectedRows ) + "\"" );
							strRowData.append( strSeparatorSymbol );
							strRowData.append( "\"" + SQLDataSetResult.intCode + "\"" );
							strRowData.append( strSeparatorSymbol );
							strRowData.append( "\"" + SQLDataSetResult.strDescription + "\"" );
														
						}
						else {
							
							strRowData.append( Long.toString( SQLDataSetResult.lngAffectedRows ) );
							strRowData.append( strSeparatorSymbol );
							strRowData.append( SQLDataSetResult.intCode );
							strRowData.append( strSeparatorSymbol );
							strRowData.append( SQLDataSetResult.strDescription );
							
						}
						
					}

				}
				else {

					if ( strShowHeaders != null && strShowHeaders.equals( "true" ) ) {

						FormatDefaultHeaders( strResponseBuffer, bFieldsQuote, strSeparatorSymbol );
					
					}

				}

				strResult = strResponseBuffer.toString();
			
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

					ArrayList<String> strResponseBuffer = new ArrayList<String>();
					
					String strShowHeaders = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ShowHeaders );
					String strSeparatorSymbol = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_SeparatorSymbol );
					String strFieldsQuote = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_FieldsQuote );
					
					boolean bFieldsQuote = strFieldsQuote != null && strFieldsQuote.equals( "true" );
					
					strSeparatorSymbol = (strSeparatorSymbol != null && strSeparatorSymbol.isEmpty() == false)?strSeparatorSymbol:";";

					ResultSet SQLDataSet = CResultSetResult.getFirstResultSetNotNull( SQLDataSetResultList );

					if ( SQLDataSet != null ) {

						if ( strShowHeaders != null && strShowHeaders.equals( "true" ) )
							FormatCSVHeaders( SQLDataSet, DBEngine, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, Logger, Lang );

						for ( CResultSetResult SQLDataSetResultToAdd: SQLDataSetResultList ) {    

							if ( SQLDataSetResultToAdd.Result != null ) {   

								FormatCSVRowData( SQLDataSetResultToAdd.Result, DBEngine, strResponseBuffer, bFieldsQuote, strSeparatorSymbol, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
								
							}

						}

					}
					else {

						if ( strShowHeaders != null && strShowHeaders.equals( "true" ) ) {

							FormatDefaultHeaders( strResponseBuffer, bFieldsQuote, strSeparatorSymbol );
						
						}
						
						for ( CResultSetResult SQLDataSetResultToAdd: SQLDataSetResultList ) {    

							StringBuilder strRowData = new StringBuilder();
							
							if ( bFieldsQuote ) {

								strRowData.append( "\"" + Long.toString( SQLDataSetResultToAdd.lngAffectedRows ) + "\"" );
								strRowData.append( strSeparatorSymbol );
								strRowData.append( "\"" + SQLDataSetResultToAdd.intCode + "\"" );
								strRowData.append( strSeparatorSymbol );
								strRowData.append( "\"" + SQLDataSetResultToAdd.strDescription + "\"" );
															
							}
							else {
								
								strRowData.append( Long.toString( SQLDataSetResultToAdd.lngAffectedRows ) );
								strRowData.append( strSeparatorSymbol );
								strRowData.append( SQLDataSetResultToAdd.intCode );
								strRowData.append( strSeparatorSymbol );
								strRowData.append( SQLDataSetResultToAdd.strDescription );
								
							}

							strResponseBuffer.add( strRowData.toString() );
							
						}

					}

					strResult = strResponseBuffer.toString();

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

				ArrayList<String> strResponseBuffer = new ArrayList<String>();
				
				String strShowHeaders = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ShowHeaders );
				String strSeparatorSymbol = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_SeparatorSymbol );
				String strFieldsQuote = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_FieldsQuote );
				
				if ( strShowHeaders != null && strShowHeaders.equals( "true" ) )
				   FormatCSVHeaders( MemoryRowSet, strResponseBuffer, strFieldsQuote != null && strFieldsQuote.equals( "true" ), strSeparatorSymbol, Logger, Lang );
				
				FormatCSVRowData( MemoryRowSet, strResponseBuffer, strFieldsQuote != null && strFieldsQuote.equals( "true" ), (strSeparatorSymbol != null && strSeparatorSymbol.isEmpty() == false)?strSeparatorSymbol:";", strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
				
				strResult = strResponseBuffer.toString();

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

				ArrayList<String> strResponseBuffer = new ArrayList<String>();
				
				String strShowHeaders = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ShowHeaders );
				String strSeparatorSymbol = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_SeparatorSymbol );
				String strFieldsQuote = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_FieldsQuote );
				
				boolean bFieldsQuote = strFieldsQuote != null && strFieldsQuote.equals( "true" );
				
				strSeparatorSymbol = (strSeparatorSymbol != null && strSeparatorSymbol.isEmpty() == false)?strSeparatorSymbol:";";

				if ( strShowHeaders != null && strShowHeaders.equals( "true" ) )
					FormatCSVHeaders( MemoryRowSetList.get( 0 ), strResponseBuffer, bFieldsQuote, strSeparatorSymbol, Logger, Lang );
				
				for ( int I = 0; I < MemoryRowSetList.size(); I ++ ) {
				    
					FormatCSVRowData( MemoryRowSetList.get( I ), strResponseBuffer, bFieldsQuote, strSeparatorSymbol, strDateFormat, strTimeFormat, strDateTimeFormat, Logger, Lang );
				
				};
				
				strResult = strResponseBuffer.toString();

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
			
				String strShowHeaders = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ShowHeaders );
				
				ArrayList<String> strResponseBuffer = new ArrayList<String>();
				
				String strSeparatorSymbol = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_SeparatorSymbol );

				String strFieldsQuote = OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_FieldsQuote );
				
				boolean bFieldsQuote = strFieldsQuote != null && strFieldsQuote.equals( "true" );
				
				if ( strShowHeaders != null && strShowHeaders.equals( "true" ) ) {
					
				    StringBuffer strHeaders = new StringBuffer();
				    
				    if ( bFieldsQuote ) {

				    	if ( strSecurityTokenID != null && strSecurityTokenID.isEmpty() == false ) {
				    	
				    		strHeaders.append( "\"" + CSVTags._CSV_StructSecurityTokenID + "\"" );
				    	    strHeaders.append( strSeparatorSymbol );
				    	    
				    	}
				    	
				    	if ( strTransactionID != null && strTransactionID.isEmpty() == false ) {

				    		strHeaders.append( "\"" + CSVTags._CSV_StructTransactionID + "\"" );
				    		strHeaders.append( strSeparatorSymbol );
				    		
				    	}
				    	
				    	strHeaders.append( "\"" + CSVTags._CSV_StructCode + "\"" );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( "\"" + CSVTags._CSV_StructDescription + "\"" );
				    	
				    }
				    else {
				    	
				    	if ( strSecurityTokenID != null && strSecurityTokenID.isEmpty() == false ) {

				    		strHeaders.append( CSVTags._CSV_StructSecurityTokenID );
				    		strHeaders.append( strSeparatorSymbol );
				    		
				    	}
				    	
				    	if ( strTransactionID != null && strTransactionID.isEmpty() == false ) {
				    	
				    		strHeaders.append( CSVTags._CSV_StructTransactionID );
				    		strHeaders.append( strSeparatorSymbol );
				    		
				    	}	
				    	
				    	strHeaders.append( CSVTags._CSV_StructCode );
				    	strHeaders.append( strSeparatorSymbol );
				    	strHeaders.append( CSVTags._CSV_StructDescription );
				    	
				    }
				    
				    strResponseBuffer.add( strHeaders.toString() );

				};

			    StringBuffer strRowData = new StringBuffer();
			    
			    if ( bFieldsQuote ) {

			    	strRowData.append( "\"" + Integer.toString( intCode ) + "\"" );
			    	strRowData.append( "\"" + strSeparatorSymbol + "\"" );
			    	strRowData.append( "\"" + strDescription + "\"" );
			    	
			    }	
			    else {
			    
			    	strRowData.append( intCode );
			    	strRowData.append( strSeparatorSymbol );
			    	strRowData.append( strDescription );
			    	
			    }
				
			    strResponseBuffer.add( strRowData.toString() );

			    strResult = strResponseBuffer.toString();
			
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
