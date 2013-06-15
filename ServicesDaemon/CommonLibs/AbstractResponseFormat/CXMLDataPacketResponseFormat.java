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
package AbstractResponseFormat;

import java.io.StringWriter;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import AbstractService.ConstantsServicesTags;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultSetResult;
import Utilities.Base64;


public class CXMLDataPacketResponseFormat extends CAbstractResponseFormat {

	public CXMLDataPacketResponseFormat() {

		this.strName = XMLDataPacketTags._ResponseFormat_XML_DATAPACKET;
		strMinVersion = "1.0";
		strMaxVersion = "2.0";
		
	}

    public String ConvertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, String strEnconding, boolean bIndent ) {
        
        String strResult = "";
        
        try {

            TransformerFactory TransFactory = TransformerFactory.newInstance();
            Transformer Trans = TransFactory.newTransformer();

            if (  bOmitDeclaration == true )
               Trans.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );

           
            //if ( strEnconding.trim() == null ? "" != null : !strEnconding.trim().equals("") )
            if ( strEnconding.trim().equals("") == false )
               Trans.setOutputProperty( OutputKeys.ENCODING, strEnconding );

            if ( bIndent == true )
               Trans.setOutputProperty( OutputKeys.INDENT, "yes" );

            XMLDocument.setXmlStandalone( true );
            //Trans.setOutputProperty( OutputKeys.STANDALONE, "yes" );

            //create string from xml tree
            StringWriter StrWriter = new StringWriter();

            StreamResult StreamRslt = new StreamResult( StrWriter );

            DOMSource DSource = new DOMSource( XMLDocument );

            Trans.transform( DSource, StreamRslt );

            strResult = StrWriter.toString();
            
        }
        catch ( Exception Ex ) {
            
            OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );
        
        }
        
        return strResult;
    
    }

    public String ConvertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, String strEnconding ) {

       return ConvertXMLDocumentToString( XMLDocument, bOmitDeclaration, strEnconding, true );

    }

    public String ConvertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, boolean bIndent ) {

       return ConvertXMLDocumentToString( XMLDocument, bOmitDeclaration, OwnerConfig.getConfigValue( "strXML_DataPacket_CharSet" ), bIndent );

    }

    public String ConvertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration ) {

       return ConvertXMLDocumentToString( XMLDocument, bOmitDeclaration, OwnerConfig.getConfigValue( "strXML_DataPacket_CharSet" ), true );

    }

    public String ConvertXMLDocumentToString( Document XMLDocument, String strEnconding ) {

       return ConvertXMLDocumentToString( XMLDocument, false, strEnconding, true );

    }

    public String ConvertXMLDocumentToString( Document XMLDocument ) {

       return ConvertXMLDocumentToString( XMLDocument, false, OwnerConfig.getConfigValue( "strXML_DataPacket_CharSet" ), true );

    }

    public Document BuildBasicResponseXMLStruct( String strVersion ) {

        Document XMLDocument = null;

        try {

            DocumentBuilderFactory DocBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder DocBuilder = DocBuilderFactory.newDocumentBuilder();
            XMLDocument = DocBuilder.newDocument();

            Element DataPacketSection = XMLDocument.createElement( XMLDataPacketTags._DataPacket );

            DataPacketSection.setAttribute( XMLDataPacketTags._DPVersion, strVersion );
            XMLDocument.appendChild( DataPacketSection );

            Element MetaDataSection = XMLDocument.createElement( XMLDataPacketTags._MetaData );
            DataPacketSection.appendChild( MetaDataSection );

            Element FieldsSection = XMLDocument.createElement( XMLDataPacketTags._Fields );
            MetaDataSection.appendChild( FieldsSection );

            Element ParamsSection = XMLDocument.createElement( XMLDataPacketTags._Params );
            ParamsSection.setAttribute( XMLDataPacketTags._RowCount , "0" );
            MetaDataSection.appendChild( ParamsSection );

            Element RowDataSection = XMLDocument.createElement( XMLDataPacketTags._RowData );
            DataPacketSection.appendChild( RowDataSection );

            if (  strVersion.equals( "1.1" ) ) {

               Element ErrorsSection = XMLDocument.createElement( XMLDataPacketTags._Errors );
               ErrorsSection.setAttribute( XMLDataPacketTags._ErrorCount, "0" );

               Element Error = XMLDocument.createElement( XMLDataPacketTags._Error );
               Error.setAttribute( XMLDataPacketTags._XMLStructCode , "0" );
               Error.setAttribute( XMLDataPacketTags._XMLStructDescription , "" );
               
               ErrorsSection.appendChild( Error );
               
               DataPacketSection.appendChild( ErrorsSection );

            }
            else {

               Element ErrorSection = XMLDocument.createElement( XMLDataPacketTags._Error );
               ErrorSection.setAttribute( XMLDataPacketTags._XMLStructCode , "0" );
               ErrorSection.setAttribute( XMLDataPacketTags._XMLStructDescription , "" );
               DataPacketSection.appendChild( ErrorSection );

            }
            

        }
        catch ( Exception Ex ) {

            OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }


        return XMLDocument;

    }

    public Document BuildXMLSimpleMessageStruct( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion ) {

        Document XMLDocument = BuildBasicResponseXMLStruct( strVersion );

        try {

            NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

            if ( XML_FieldsSection.getLength() > 0 ) {

               if ( strSecurityToken != null && strSecurityToken.trim().equals("") == false ) {

                  Element XML_FieldSecurityToken = XMLDocument.createElement( XMLDataPacketTags._Field );
                  XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructSecurityToken );
                  XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBigInt );

                  XML_FieldsSection.item( 0 ).appendChild( XML_FieldSecurityToken );

               }

               if ( strTransactionID != null && strTransactionID.trim().equals("") == false ) {

                  Element XML_FieldSecurityToken = XMLDocument.createElement( XMLDataPacketTags._Field );
                  XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructTransactionID );
                  XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBigInt );

                  XML_FieldsSection.item( 0 ).appendChild( XML_FieldSecurityToken );

               }

               Element XML_FieldCode = XMLDocument.createElement( XMLDataPacketTags._Field );
               XML_FieldCode.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructCode );
               XML_FieldCode.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeInteger );

               XML_FieldsSection.item( 0 ).appendChild( XML_FieldCode );

               Element XML_FieldDescription = XMLDocument.createElement( XMLDataPacketTags._Field );
               XML_FieldDescription.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructDescription );
               XML_FieldDescription.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
               XML_FieldDescription.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructDescriptionLength );

               XML_FieldsSection.item( 0 ).appendChild( XML_FieldDescription );

            }


            NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

            if ( XML_ParamsSection.getLength() > 0 ) {

               ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , "1" );

            }

            NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

            if ( XML_RowDataSection.getLength() > 0 ) {

               Element XML_ROW = XMLDocument.createElement( XMLDataPacketTags._Row );

               if ( strTransactionID != null && strTransactionID.trim().equals("") == false ) {

                  XML_ROW.setAttribute( XMLDataPacketTags._XMLStructTransactionID, strTransactionID );

               }

               if ( strSecurityToken != null && strSecurityToken.trim().equals("") == false ) {

                  XML_ROW.setAttribute( XMLDataPacketTags._XMLStructSecurityToken, strSecurityToken );
                 
               }

               XML_ROW.setAttribute( XMLDataPacketTags._XMLStructCode, Integer.toString( intCode ) );
               XML_ROW.setAttribute( XMLDataPacketTags._XMLStructDescription, strDescription );

               XML_RowDataSection.item( 0 ).appendChild( XML_ROW );

            }

            if ( bAttachToError == true ) {

               NodeList XML_ErrorsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Errors );

               if ( XML_ErrorsSection != null )
                  ( (Element) XML_ErrorsSection.item( 0 ) ).setAttribute( XMLDataPacketTags._ErrorCount, "1" );

               NodeList XML_ErrorSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Error );

               if ( XML_ErrorSection.getLength() > 0 ) {

                  ((Element) XML_ErrorSection.item( 0 )).setAttribute( XMLDataPacketTags._XMLStructCode , Integer.toString( intCode ) );
                  ((Element) XML_ErrorSection.item( 0 )).setAttribute( XMLDataPacketTags._XMLStructDescription , strDescription );

                  if ( strTransactionID != null && strTransactionID.trim().equals( "" ) == false ) {

                     ((Element) XML_ErrorSection.item( 0 )).setAttribute( XMLDataPacketTags._XMLStructTransactionID, strTransactionID );

                  }

                  if ( strSecurityToken != null && strSecurityToken.trim().equals( "" ) == false ) {

                     ((Element) XML_ErrorSection.item( 0 )).setAttribute( XMLDataPacketTags._XMLStructSecurityToken, strSecurityToken );

                  }

               }

            }

        }
        catch ( Exception Ex ) {

        	OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }

        return XMLDocument;

    }

    public Document AddXMLToErrorSection( Document XMLDocument, int intCode, String strDescription, String strVersion ) {

        NodeList XML_ErrorsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Errors );

        if ( XML_ErrorsSection != null && XML_ErrorsSection.getLength() > 0 ) {
           
            //XML-DataPacket 1.1 Errors Section found

        	String strCountError = ( (Element) XML_ErrorsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._ErrorCount );
        	
        	int intCountError = 0;
        	
        	if ( strCountError != null )
        	   intCountError = Utilities.Utilities.StrToInteger( strCountError ) + 1;
        		
        	( (Element) XML_ErrorsSection.item( 0 ) ).setAttribute( XMLDataPacketTags._ErrorCount, Integer.toString( intCountError ) );

            Element Error = XMLDocument.createElement( XMLDataPacketTags._Error );
            Error.setAttribute( XMLDataPacketTags._XMLStructCode , Integer.toString( intCode ) );
            Error.setAttribute( XMLDataPacketTags._XMLStructDescription , strDescription );
            
            ( (Element) XML_ErrorsSection.item( 0 ) ).appendChild( Error );
        	
        }
        else {
        	
            //XML-DataPacket 1.0 NO errors section found

        	NodeList XML_DataPacketSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._DataPacket );

            if ( XML_DataPacketSection != null && XML_DataPacketSection.getLength() > 0 ) {
            	
                Element Error = XMLDocument.createElement( XMLDataPacketTags._Error );
                Error.setAttribute( XMLDataPacketTags._XMLStructCode , Integer.toString( intCode ) );
                Error.setAttribute( XMLDataPacketTags._XMLStructDescription , strDescription );
                
                ( (Element) XML_DataPacketSection.item( 0 ) ).appendChild( Error );
            	
            }            
            
        }
    	
    	
    	return XMLDocument;
    	
    }
    
    public Document AddXMLSimpleMessage( Document XMLDocument, int intCode, String strDescription, String strVersion, boolean bAttachToError ) {
    	
        NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

        if ( XML_RowDataSection != null && XML_RowDataSection.getLength() > 0 ) {

           Element XML_ROW = XMLDocument.createElement( XMLDataPacketTags._Row );

           XML_ROW.setAttribute( XMLDataPacketTags._XMLStructCode, Integer.toString( intCode ) );
           XML_ROW.setAttribute( XMLDataPacketTags._XMLStructDescription, strDescription );

           XML_RowDataSection.item( 0 ).appendChild( XML_ROW );
           
        }
    	
        if ( bAttachToError == true ) {
        	
        	XMLDocument = AddXMLToErrorSection( XMLDocument, intCode, strDescription, strVersion );
        	
        }
        
    	return XMLDocument;
    	
    }

    public String BuildXMLSimpleMessage( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion ) {

        Document XMLDocument = BuildXMLSimpleMessageStruct( strSecurityToken, strTransactionID, intCode, strDescription, bAttachToError, strVersion );

        String strXMLBuffer = ConvertXMLDocumentToString( XMLDocument, false, OwnerConfig.getConfigValue( "strXML_DataPacket_CharSet" ), true );

        return strXMLBuffer;

    }
    
    public static String EncodeTsValidXMLData( String strData ) {

        String strValidXMLData = "";
        
        try {

           strValidXMLData = new String( strData.getBytes("ISO-8859-1"),"UTF-8" );

        }
        catch ( Exception Error ) {

           System.out.println( Error );
           Error.printStackTrace( System.out );

        }

        strValidXMLData = strValidXMLData.replaceAll( "&", "&amp;" );
        strValidXMLData = strValidXMLData.replaceAll( "<", "&lt;" );
        strValidXMLData = strValidXMLData.replaceAll( ">", "&gt;" );
        strValidXMLData = strValidXMLData.replaceAll( "\"", "&quot;" );
        strValidXMLData = strValidXMLData.replaceAll( "\'", "&apos;" );

        return strValidXMLData;

    }

    public Document BuildXMLMetaData( Document XMLDocument, ResultSetMetaData DataSetMetaData, ArrayList<String> arrIncludedFields, ArrayList<String> arrExcludedFields ) {

        try {

           NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

           if ( XML_FieldsSection.getLength() > 0 ) {

              for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

                 String strFieldName   = DataSetMetaData.getColumnName( i );
                 int    intFieldType   = DataSetMetaData.getColumnType( i );
                 int    intFieldLength = DataSetMetaData.getColumnDisplaySize( i );

                 if ( ( arrIncludedFields.isEmpty() == true || arrIncludedFields.contains( strFieldName ) == true ) && ( arrExcludedFields.isEmpty() == true || arrExcludedFields.contains( strFieldName )  == false ) ) {

                    Element XML_Field = XMLDocument.createElement( XMLDataPacketTags._Field );

                    XML_Field.setAttribute( XMLDataPacketTags._AttrName, strFieldName );

	    			switch ( intFieldType ) {
	    			
						case Types.INTEGER: { XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeInteger ); break; }
						case Types.BIGINT: { XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBigInt ); break; }
						case Types.SMALLINT: { XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeShortInteger ); break; }
						case Types.VARCHAR: 
						case Types.CHAR: {  
							
					                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
					                       XML_Field.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, Integer.toString( intFieldLength ) );
							               break; 
							             
						                 }
						case Types.BOOLEAN: {  break; }
						case Types.BLOB: { 	
							
					                        XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBlob );
					                        XML_Field.setAttribute( XMLDataPacketTags._FieldTypeSubType, XMLDataPacketTags._FieldTypeBlobSubTypeBinary );
										    break; 
				
										 }
						case Types.DATE: {
							               
		                                    XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeDate );
											break; 
							             
						                 }
						case Types.TIME: {  
							
		                                    XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeTime );
											break; 
							               
						                 }
						case Types.TIMESTAMP: {  
							
		                                         XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeDateTime );
							                     break; 
							                    
						                      }
						case Types.FLOAT: 
						case Types.DECIMAL: {  XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeFloat ); break; }
						case Types.DOUBLE: {  break; }

					}
	    			
                    /*if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_VARCHAR ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_CHAR ) == true ) {

                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
                       XML_Field.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, Integer.toString( intFieldLength ) );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_INTEGER ) == true) {

                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeInteger );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_SMALLINT ) == true) {

                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeShortInteger );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_BIGINT ) == true) {

                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBigInt );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_DATE ) == true) {

                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeDate );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_TIME ) == true) {
                       
                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeTime );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_TIMESTAMP ) == true) {

                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeDateTime );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_BLOB ) == true) {

                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBlob );
                       XML_Field.setAttribute( XMLDataPacketTags._FieldTypeSubType, XMLDataPacketTags._FieldTypeBlobSubTypeBinary );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_FLOAT ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_DECIMAL ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_NUMERIC ) == true ) {

                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeFloat );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_CURRENCY ) == true ) {

                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeCurrency );
                       XML_Field.setAttribute( XMLDataPacketTags._FieldTypeSubType, XMLDataPacketTags._FieldTypeCurrencySubTypeMoney );

                    }*/

                    XML_FieldsSection.item( 0 ).appendChild( XML_Field );

                 }

              }

           }
           
        }
        catch ( Exception Ex ) {

        	OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
        
        return XMLDocument;

    }

    public Document AddXMLToRowDataSection( Document XMLDocument, ResultSet SQLDataSet, ArrayList<String> arrIncludedFields, ArrayList<String> arrExcludedFields ) {

        try {

    	   int intRowCount = 0; 

           NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

           if ( XML_RowDataSection.getLength() > 0 ) {

              SimpleDateFormat DFormatter = new SimpleDateFormat("yyyymmdd");
              SimpleDateFormat TFormatter = new SimpleDateFormat("HHmmss");
              SimpleDateFormat DTFormatter = new SimpleDateFormat("yyyymmdd HHmmss");

              java.sql.ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();

              while ( SQLDataSet.next() == true ) {

            	  intRowCount += 1;
            	  
            	  Element XML_ROW = XMLDocument.createElement( XMLDataPacketTags._Row );

                 for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

                    String strFieldName = DataSetMetaData.getColumnName( i );
                    int intFieldType = DataSetMetaData.getColumnType( i );

	    			switch ( intFieldType ) {
	    			
						case Types.INTEGER: { XML_ROW.setAttribute( strFieldName, Integer.toString( SQLDataSet.getInt( strFieldName ) ) ); break; }
						case Types.BIGINT: { XML_ROW.setAttribute( strFieldName, Long.toString( SQLDataSet.getLong( strFieldName ) ) ); break; }
						case Types.SMALLINT: { XML_ROW.setAttribute( strFieldName, Short.toString( SQLDataSet.getShort( strFieldName ) ) ); break; }
						case Types.VARCHAR: 
						case Types.CHAR: {  
							
											XML_ROW.setAttribute( strFieldName, SQLDataSet.getString( strFieldName ) );
											break; 
							             
						                 }
						case Types.BOOLEAN: {  break; }
						case Types.BLOB: { 	
							
					                        Blob BinaryBLOBData = SQLDataSet.getBlob( strFieldName );
					                       
					                        String strBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ), "UTF-8" );
			
					                        XML_ROW.setAttribute( strFieldName, strBase64Coded );//Formated in base64
										    break; 
				
										 }
						case Types.DATE: {
							               
				                            XML_ROW.setAttribute( strFieldName, DFormatter.format( SQLDataSet.getDate( strFieldName ) ) );
											break; 
							             
						                 }
						case Types.TIME: {  
							
		                                    XML_ROW.setAttribute( strFieldName, TFormatter.format( SQLDataSet.getTime( strFieldName ) ) );
											break; 
							               
						                 }
						case Types.TIMESTAMP: {  
							
		                                         XML_ROW.setAttribute( strFieldName, DTFormatter.format( SQLDataSet.getTimestamp( strFieldName ) ) );
							                     break; 
							                    
						                      }
						case Types.FLOAT: 
						case Types.DECIMAL: {  XML_ROW.setAttribute( strFieldName, Float.toString( SQLDataSet.getFloat( strFieldName ) ) ); break; }
						case Types.DOUBLE: {  break; }

					}
	    			
                    /*if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_VARCHAR ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_CHAR ) == true ) {

                       XML_ROW.setAttribute( strFieldName, SQLDataSet.getString( strFieldName ) );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_INTEGER ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_SMALLINT ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_BIGINT ) == true ) {

                       XML_ROW.setAttribute( strFieldName, Long.toString( SQLDataSet.getLong( strFieldName ) ) );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_DATE ) == true) {

                       XML_ROW.setAttribute( strFieldName, DFormatter.format( SQLDataSet.getDate( strFieldName ) ) );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_TIME ) == true) {

                       XML_ROW.setAttribute( strFieldName, TFormatter.format( SQLDataSet.getTime( strFieldName ) ) );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_TIMESTAMP ) == true) {

                       XML_ROW.setAttribute( strFieldName, DTFormatter.format( SQLDataSet.getTimestamp( strFieldName ) ) );

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_BLOB ) == true) {

                       Blob BinaryBLOBData = SQLDataSet.getBlob( strFieldName );
                       
                       String strBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ), "UTF-8" );

                       XML_ROW.setAttribute( strFieldName, strBase64Coded );//Formated in base64

                    }
                    else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_FLOAT ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_DECIMAL ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_NUMERIC ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_CURRENCY ) == true ) {

                       XML_ROW.setAttribute( strFieldName, Float.toString( SQLDataSet.getFloat( strFieldName ) ) );

                    }*/

                 }

                 XML_RowDataSection.item( 0 ).appendChild( XML_ROW );

              }

           }

           NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

           if ( XML_ParamsSection.getLength() > 0 ) {

        	   String strCurrentRowCount = ( (Element) XML_ParamsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._RowCount );
           	
        	   int intCurrentRowCount = 0;
        	   
        	   if ( strCurrentRowCount != null )
        	       intCurrentRowCount = Utilities.Utilities.StrToInteger( strCurrentRowCount );
           		
              ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , Integer.toString( intCurrentRowCount + intRowCount ) );

           }
           
        }
        catch ( Exception Ex ) {

        	OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
        
        return XMLDocument;

    }
    
    public Document BuildXMLMetaData( Document XMLDocument, CMemoryRowSet MemoryRowSet ) {
    	
    	try {
    
            NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

            if ( XML_FieldsSection.getLength() > 0 ) {

               for ( int i = 0; i < MemoryRowSet.getFieldsCount(); i++ ) {
            	   
            	   CMemoryFieldData Field = MemoryRowSet.getFieldByIndex( i );
            	   
            	   String strFieldName   = Field.strName;
            	   int intFieldType      = Field.intSQLType;
            	   int intFieldLength    = Field.intLength;

            	   Element XML_Field = XMLDocument.createElement( XMLDataPacketTags._Field );

            	   XML_Field.setAttribute( XMLDataPacketTags._AttrName, strFieldName );

	    			switch ( intFieldType ) {
	    			
						case Types.INTEGER: { XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeInteger ); break; }
						case Types.BIGINT: { XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBigInt ); break; }
						case Types.SMALLINT: { XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeShortInteger ); break; }
						case Types.VARCHAR: 
						case Types.CHAR: {  
							
					                       XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
					                       XML_Field.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, Integer.toString( intFieldLength ) );
							               break; 
							             
						                 }
						case Types.BOOLEAN: {  break; }
						case Types.BLOB: { 	
							
					                        XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBlob );
					                        XML_Field.setAttribute( XMLDataPacketTags._FieldTypeSubType, XMLDataPacketTags._FieldTypeBlobSubTypeBinary );
										    break; 
				
										 }
						case Types.DATE: {
							               
		                                    XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeDate );
											break; 
							             
						                 }
						case Types.TIME: {  
							
		                                    XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeTime );
											break; 
							               
						                 }
						case Types.TIMESTAMP: {  
							
		                                         XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeDateTime );
							                     break; 
							                    
						                      }
						case Types.FLOAT: 
						case Types.DECIMAL: {  XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeFloat ); break; }
						case Types.DOUBLE: {  break; }

					}
	    			
            	   /*if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_VARCHAR ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_CHAR ) == true ) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
            		   XML_Field.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, Integer.toString( intFieldLength ) );

            	   }
            	   else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_INTEGER ) == true) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeInteger );

            	   }
            	   else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_SMALLINT ) == true) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeShortInteger );

            	   }
            	   else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_BIGINT ) == true) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBigInt );

            	   }
            	   else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_DATE ) == true) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeDate );

            	   }
            	   else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_TIME ) == true) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeTime );

            	   }
            	   else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_TIMESTAMP ) == true) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeDateTime );

            	   }
            	   else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_BLOB ) == true) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBlob );
            		   XML_Field.setAttribute( XMLDataPacketTags._FieldTypeSubType, XMLDataPacketTags._FieldTypeBlobSubTypeBinary );

            	   }
            	   else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_FLOAT ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_DECIMAL ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_NUMERIC ) == true ) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeFloat );

            	   }
            	   else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_CURRENCY ) == true ) {

            		   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeCurrency );
            		   XML_Field.setAttribute( XMLDataPacketTags._FieldTypeSubType, XMLDataPacketTags._FieldTypeCurrencySubTypeMoney );

            	   }*/

            	   XML_FieldsSection.item( 0 ).appendChild( XML_Field );

               }

            }
    	
    	}
    	catch ( Exception Ex ) {

    		OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

    	}

    	return XMLDocument;
    
    }
    public Document AddXMLToRowDataSection( Document XMLDocument, CMemoryRowSet MemoryRowSet ) {
    	
    	try {

    		int intRowCount = 0;
    		
    		NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

    		if ( XML_RowDataSection.getLength() > 0 ) {

    			SimpleDateFormat DFormatter = new SimpleDateFormat("yyyymmdd");
    			SimpleDateFormat TFormatter = new SimpleDateFormat("HHmmss");
    			SimpleDateFormat DTFormatter = new SimpleDateFormat("yyyymmdd HHmmss");

				intRowCount = MemoryRowSet.getRowCount();
				int intColCount = MemoryRowSet.getFieldsCount();
				
				for ( int intRowIndex = 0; intRowIndex < intRowCount; intRowIndex++ ) {

    				Element XML_ROW = XMLDocument.createElement( XMLDataPacketTags._Row );

    				for ( int intIndexColumn = 1; intIndexColumn <= intColCount; intIndexColumn++ ) {

			    		CMemoryFieldData Field = MemoryRowSet.getFieldByIndex( intIndexColumn - 1 );
						
			    		Object FieldData = null; 
			    				
			    		if ( Field != null ) {
			    			
			    			FieldData = Field.getData( intRowIndex );
			    		
			    		}

			    		String strFieldName = Field.strName;//DataSetMetaData.getColumnName( i );
    					int intFieldType = Field.intSQLType; //DataSetMetaData.getColumnTypeName( i );

    					if  ( FieldData != null ) {
    					
    						switch ( intFieldType ) {

	    						case Types.INTEGER: { XML_ROW.setAttribute( strFieldName, Integer.toString( (Integer) FieldData ) ); break; }
	    						case Types.BIGINT: { XML_ROW.setAttribute( strFieldName, Long.toString( (Long) FieldData ) ); break; }
	    						case Types.SMALLINT: { XML_ROW.setAttribute( strFieldName, Short.toString( (Short) FieldData ) ); break; }
	    						case Types.VARCHAR: 
	    						case Types.CHAR: {  
	
					    							XML_ROW.setAttribute( strFieldName, (String) FieldData );
					    							break; 
					
					    						 }
	    						case Types.BOOLEAN: {  break; }
	    						case Types.BLOB: { 	
	
					    							Blob BinaryBLOBData = (Blob) FieldData;
					
					    							String strBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ), "UTF-8" );
					
					    							XML_ROW.setAttribute( strFieldName, strBase64Coded );//Formated in base64
					    							break; 
					
					    						 }
	    						case Types.DATE: {
	
					    							XML_ROW.setAttribute( strFieldName, DFormatter.format( (Date) FieldData ) );
					    							break; 
					
					    						 }
	    						case Types.TIME: {  
	
	    							                XML_ROW.setAttribute( strFieldName, TFormatter.format( (Time) FieldData ) );
	    							                break; 
	
	    						                 }
	    						case Types.TIMESTAMP: {  
	
	    							                      XML_ROW.setAttribute( strFieldName, DTFormatter.format( (Timestamp) FieldData ) );
	    							                      break; 
	
	    						                      }
	    						case Types.FLOAT: 
	    						case Types.DECIMAL: {  XML_ROW.setAttribute( strFieldName, Float.toString( (Float) FieldData ) ); break; }
	    						case Types.DOUBLE: {  break; }

    						}
    	    			
    					}
    					else {

    						XML_ROW.setAttribute( strFieldName, "" );
    						
    					}
    	    			
    					/*if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_VARCHAR ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_CHAR ) == true ) {

    						XML_ROW.setAttribute( strFieldName, (String) FieldData );

    					}
    					else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_INTEGER ) == true ) {

    						XML_ROW.setAttribute( strFieldName, Long.toString( (Integer) FieldData ) );

    					}
    					else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_BIGINT ) == true ) {

    						XML_ROW.setAttribute( strFieldName, Long.toString( (Long) FieldData ) );
    						
    					}
    					else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_SMALLINT ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_BIGINT ) == true ) {

    						XML_ROW.setAttribute( strFieldName, Long.toString( (Short) FieldData ) );
    						
    					}
    					else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_DATE ) == true) {

    						XML_ROW.setAttribute( strFieldName, DFormatter.format( (Date) FieldData ) );

    					}
    					else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_TIME ) == true) {

    						XML_ROW.setAttribute( strFieldName, TFormatter.format( (Time) FieldData ) );

    					}
    					else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_TIMESTAMP ) == true) {

    						XML_ROW.setAttribute( strFieldName, DTFormatter.format( (Timestamp) FieldData ) );

    					}
    					else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_BLOB ) == true) {

    						Blob BinaryBLOBData = (Blob) FieldData;

    						String strBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ), "UTF-8" );

    						XML_ROW.setAttribute( strFieldName, strBase64Coded );//Formated in base64

    					}
    					else if ( strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_FLOAT ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_DECIMAL ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_NUMERIC ) == true || strFieldType.toLowerCase().equals( NamesSQLTypes.strSQL_CURRENCY ) == true ) {

    						XML_ROW.setAttribute( strFieldName, Float.toString( (Float) FieldData ) );

    					}*/

    				}

    				XML_RowDataSection.item( 0 ).appendChild( XML_ROW );

    			}

    		}

            NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

            if ( XML_ParamsSection.getLength() > 0 ) {

         	   String strCurrentRowCount = ( (Element) XML_ParamsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._RowCount );
            	
         	   int intCurrentRowCount = 0;
         	   
         	   if ( strCurrentRowCount != null )
         	       intCurrentRowCount = Utilities.Utilities.StrToInteger( strCurrentRowCount );
            		
               ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , Integer.toString( intCurrentRowCount + intRowCount ) );

            }
            
    	}
    	catch ( Exception Ex ) {

    		OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

    	}

    	return XMLDocument;
    	
    }
    

    public CAbstractResponseFormat getNewInstance() {
    	
    	CXMLDataPacketResponseFormat NewInstance = new CXMLDataPacketResponseFormat();
    	
    	NewInstance.InitResponseFormat( this.ServicesDaemonConfig, this.OwnerConfig );
    	
    	return NewInstance;
    	
    }
    
    
    @Override
    public String getContentType() {
    	
    	return OwnerConfig.getConfigValue( "strXML_DataPacket_ContentType" );
    	
    }
    
    @Override
    public String getCharacterEncoding() {
    	
    	return OwnerConfig.getConfigValue( "strXML_DataPacket_CharSet" );
    	
    }

    public int DescribeService( CAbstractService Service, Element XMLNode_RowDataSection ) {
	   
    	int intResult = 0;
    	
    	if ( Service.getHiddenService() == false ) {
    	    
    		String strServiceType = "ReadWrite";
	
	        if ( Service.getServiceType() == 1 )
	           strServiceType = "Read";
	        else if ( Service.getServiceType() == 2 )
	           strServiceType = "Write";
	
	        Document XMLDocument = XMLNode_RowDataSection.getOwnerDocument();
	
        	HashMap< String, ArrayList< CInputServiceParameter > > GroupsInputParametersService = Service.getGroupsInputParametersService();   

	        Iterator< Entry< String, ArrayList< CInputServiceParameter > > > It = GroupsInputParametersService.entrySet().iterator();

	        OwnerConfig.Logger.LogMessage( "1", OwnerConfig.Lang.Translate( "Service [%s] input params count: [%s]", Service.getServiceName(), Integer.toString( GroupsInputParametersService.size() ) ) );
	        
            while ( It.hasNext() ) {
	        
            	intResult += 1;
            	
            	Element XMLNode_Row = XMLDocument.createElement( XMLDataPacketTags._Row );

            	if ( Service.getAuthRequired() == true )  
            		XMLNode_Row.setAttribute( XMLDataPacketTags._XMLStructAuthRequired , "Yes" );
            	else
            		XMLNode_Row.setAttribute( XMLDataPacketTags._XMLStructAuthRequired , "No" );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XMLStructServiceName , Service.getServiceName() );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XMLStructAccessType , strServiceType );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XMLStructDescription , Service.getServiceDescription() );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XMLStructAuthor , Service.getServiceAuthor() );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XMLStructAuthorContact , Service.getServiceAuthorContact() );

            	XMLNode_RowDataSection.appendChild( XMLNode_Row );

            	Element XMLNode_InputParameters = XMLDocument.createElement( XMLDataPacketTags._InputParameters );

            	Entry< String,ArrayList<CInputServiceParameter> > GroupIPSEntry = It.next();
            	
    			String strKey = GroupIPSEntry.getKey();

            	if ( strKey.toLowerCase().equals( ConstantsServicesTags._Default ) == false ) {
            		
            		XMLNode_InputParameters.setAttribute(  XMLDataPacketTags._XMLStructParamSetName, strKey ); 
            	
            	}
            	
            	for ( CInputServiceParameter InputServiceParameter : GroupIPSEntry.getValue() ) {

            		Element XMLNode_Row_InputParameter = XMLDocument.createElement( XMLDataPacketTags._Row );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XMLStructParamName , InputServiceParameter.getParameterName() );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XMLStructRequired , InputServiceParameter.getParameterRequired()?"Yes":"No" );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XMLStructType , InputServiceParameter.getParameterDataType() );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XMLStructTypeWidth , InputServiceParameter.getParameterDataTypeWidth() );

            		//XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XMLStructSubType, InputServiceParameter.getParameterScope() );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XMLStructDescription , InputServiceParameter.getParameterDescription() );

            		XMLNode_InputParameters.appendChild( XMLNode_Row_InputParameter );

            	}

            	XMLNode_RowDataSection.appendChild( XMLNode_InputParameters );
	   
            }
	        
    	}   
    	
    	return intResult;

     }

    @Override 
    public String EnumerateServices( HashMap<String,CAbstractService> RegisteredServices, String strVersion ) {

        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion );

        NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

        if ( XML_FieldsSection.getLength() > 0 ) {

           Element XML_FieldAuthRequired = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldAuthRequired.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructAuthRequired );
           XML_FieldAuthRequired.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldAuthRequired.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructAuthRequiredLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldAuthRequired );
           
           Element XML_FieldName = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldName.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructServiceName );
           XML_FieldName.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldName.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructServiceNameLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldName );

           Element XML_FieldType = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldType.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructAccessType );
           XML_FieldType.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldType.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructAccessTypeLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldType );

           Element XML_FieldDescription = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldDescription.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructDescription );
           XML_FieldDescription.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldDescription.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructDescriptionLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldDescription );

           Element XML_FieldAutor = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldAutor.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructAuthor );
           XML_FieldAutor.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldAutor.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructAuthorLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldAutor );

           Element XML_FieldAutorContact = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldAutorContact.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructAuthorContact );
           XML_FieldAutorContact.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldAutorContact.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructAuthorContactLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldAutorContact );

           Element XML_FieldInputParameters = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldInputParameters.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._InputParameters );
           XML_FieldInputParameters.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeNested );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldInputParameters );

              Element XML_FieldParameterName = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterName.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructParamName );
              XML_FieldParameterName.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterName.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructParamNameLength );

              XML_FieldInputParameters.appendChild( XML_FieldParameterName );

              Element XML_FieldParameterRequired = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterRequired.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructRequired );
              XML_FieldParameterRequired.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterRequired.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructRequiredLength );

              XML_FieldInputParameters.appendChild( XML_FieldParameterRequired );

              Element XML_FieldParameterType = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterType.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructType );
              XML_FieldParameterType.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterType.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructTypeLength );

              XML_FieldInputParameters.appendChild( XML_FieldParameterType );

              Element XML_FieldParameterTypeWidth = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterTypeWidth.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructTypeWidth );
              XML_FieldParameterTypeWidth.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeShortInteger );

              XML_FieldInputParameters.appendChild( XML_FieldParameterTypeWidth );

              Element XML_FieldParameterSubType = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterSubType.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructSubType );
              XML_FieldParameterSubType.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterType.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructSubTypeLength );

              XML_FieldInputParameters.appendChild( XML_FieldParameterSubType );

              Element XML_FieldParameterDescription = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterDescription.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XMLStructDescription );
              XML_FieldParameterDescription.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterDescription.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XMLStructDescriptionLength );

              XML_FieldInputParameters.appendChild( XML_FieldParameterDescription );

        }

        NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

		int intRowCount = 0;
        
        if ( XML_RowDataSection.getLength() > 0 ) {

           Iterator<Entry<String, CAbstractService>> it = RegisteredServices.entrySet().iterator();

           while ( it.hasNext() ) {

              Entry<String, CAbstractService> Pairs = it.next();

              String strServName = (String) Pairs.getKey();

              CAbstractService Service = RegisteredServices.get( strServName );
              
              if ( Service != null )
                 intRowCount += this.DescribeService( Service, (Element) XML_RowDataSection.item( 0 ) );
        
           }

        }

        NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

        if ( XML_ParamsSection.getLength() > 0 ) {

           ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount, Integer.toString( intRowCount ) );

        }

    	return ConvertXMLDocumentToString( XMLDocument, true, this.getCharacterEncoding() );
    	
    }
    
    @Override
	public String FormatResultSet( ResultSet ResultSet, String strVersion ) {

    	String strResult = "";
    	
        try {

	        ResultSetMetaData DataSetMetaData = ResultSet.getMetaData();
	
	        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion );
	
	        ArrayList<String> arrIncludedFields = new ArrayList<String>();
	        ArrayList<String> arrExcludedFields = new ArrayList<String>();
	
	        XMLDocument = BuildXMLMetaData( XMLDocument, DataSetMetaData, arrIncludedFields, arrExcludedFields );
	
	        XMLDocument = AddXMLToRowDataSection( XMLDocument, ResultSet, arrIncludedFields, arrExcludedFields );

            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding() );

        }
        catch ( Exception Ex ) {

        	OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }

    	return strResult;
		
	}
    
    public String FormatResultsSets( ArrayList<ResultSet> ResultsSetsList, String strVersion ) {
    	
    	String strResult = "";
    	
        try {

        	if ( ResultsSetsList.size() > 0 ) {

        		ResultSet ResultSetMetaData = ResultsSetsList.get( 0 );
	        	
		        ResultSetMetaData DataSetMetaData = ResultSetMetaData.getMetaData();
		
		        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion );
		
		        ArrayList<String> arrIncludedFields = new ArrayList<String>();
		        ArrayList<String> arrExcludedFields = new ArrayList<String>();
		
		        XMLDocument = BuildXMLMetaData( XMLDocument, DataSetMetaData, arrIncludedFields, arrExcludedFields );
		
		        for ( ResultSet ResultSetToAdd: ResultsSetsList ) {    
		        
		           XMLDocument = AddXMLToRowDataSection( XMLDocument, ResultSetToAdd, arrIncludedFields, arrExcludedFields );
		        
		        }
		        
	            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding() );

        	}
	            
        }
        catch ( Exception Ex ) {

        	OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
    	
    	return strResult;
    	
    }

    public String FormatResultsSets( ArrayList<CResultSetResult> ResultsSetsList, String strVersion, int intDummyParam ) {
    	
    	String strResult = "";
    	
        try {

        	if ( ResultsSetsList.size() > 0 ) {

        		ResultSet ResultSetMetaData = CResultSetResult.getFirstResultSetNotNull( ResultsSetsList );
        		
    			Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion );

        		if ( ResultSetMetaData != null ) {
		        
        			ResultSetMetaData DataSetMetaData = ResultSetMetaData.getMetaData();

        			ArrayList<String> arrIncludedFields = new ArrayList<String>();
        			ArrayList<String> arrExcludedFields = new ArrayList<String>();

        			XMLDocument = BuildXMLMetaData( XMLDocument, DataSetMetaData, arrIncludedFields, arrExcludedFields );

        			for ( CResultSetResult ResultSetResultToAdd: ResultsSetsList ) {    

        				if ( ResultSetResultToAdd.Result != null && ResultSetResultToAdd.intCode >= 0 ) {   
        				
        					XMLDocument = AddXMLToRowDataSection( XMLDocument, ResultSetResultToAdd.Result, arrIncludedFields, arrExcludedFields );

        				}
        				else {
        					              
        					XMLDocument = AddXMLToErrorSection( XMLDocument, ResultSetResultToAdd.intCode, ResultSetResultToAdd.strDescription, strVersion );
        					
        				}

        			}
		        
        		}
        		else {
        			
        			for ( CResultSetResult ResultSetResultToAdd: ResultsSetsList ) {    

        				XMLDocument = AddXMLSimpleMessage( XMLDocument, ResultSetResultToAdd.intCode, ResultSetResultToAdd.strDescription, strVersion, true );
        			
        			}
        		}
		        
	            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding() );

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

	        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion );
	
	        XMLDocument = BuildXMLMetaData( XMLDocument, MemoryRowSet );
	
	        XMLDocument = AddXMLToRowDataSection( XMLDocument, MemoryRowSet );

            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding() );

        }
        catch ( Exception Ex ) {

        	OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }

    	return strResult;
    	
    }
    
    @Override
    public String FormatMemoryRowSets( ArrayList<CMemoryRowSet> MemoryRowtSets, String strVersion ) {
    	
    	String strResult = "";

        try {

        	if ( MemoryRowtSets.size() > 0 ) {
        	
		        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion );
		
		        CMemoryRowSet FirstMemoryRowSet = MemoryRowtSets.get( 0 );

        		XMLDocument = BuildXMLMetaData( XMLDocument, FirstMemoryRowSet );
		
		        for ( CMemoryRowSet MemoryRowSetToAdd: MemoryRowtSets ) {   
        		
		        	XMLDocument = AddXMLToRowDataSection( XMLDocument, MemoryRowSetToAdd );
		        
		        }
	
	            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding() );

        	} 
	            
        }
        catch ( Exception Ex ) {

        	OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
        
    	return strResult;
    	
    }
    
    @Override
    public String FormatSimpleMessage( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion ) {
    	
    	return BuildXMLSimpleMessage( strSecurityToken, strTransactionID, intCode, strDescription, bAttachToError, strVersion );
    	
    }

}
