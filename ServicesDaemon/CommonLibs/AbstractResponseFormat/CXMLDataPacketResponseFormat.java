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
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import net.maindataservices.Base64;

import net.maindataservices.Base64;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import sun.misc.BASE64Encoder;
//import org.apache.commons.codec.binary.Base64;

import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import AbstractService.ConstantsServicesTags;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultSetResult;
import ExtendedLogger.CExtendedLogger;


public class CXMLDataPacketResponseFormat extends CAbstractResponseFormat {

	public CXMLDataPacketResponseFormat() {

		this.strName = XMLDataPacketTags._ResponseFormat_XML_DATAPACKET;
		strMinVersion = "1.0";
		strMaxVersion = "2.0";
		
	}

    public String ConvertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, String strEnconding, boolean bIndent, CExtendedLogger Logger, CLanguage Lang ) {
        
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
            
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );
        
        }
        
        return strResult;
    
    }

    public String ConvertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, String strEnconding, CExtendedLogger Logger, CLanguage Lang ) {

       return ConvertXMLDocumentToString( XMLDocument, bOmitDeclaration, strEnconding, true, Logger, Lang );

    }

    public String ConvertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, boolean bIndent, CExtendedLogger Logger, CLanguage Lang ) {

       return ConvertXMLDocumentToString( XMLDocument, bOmitDeclaration, OwnerConfig.getConfigValue( ConstantsResponseFormat._XML_DataPacket_CharSet ), bIndent, Logger, Lang );

    }

    public String ConvertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, CExtendedLogger Logger, CLanguage Lang ) {

       return ConvertXMLDocumentToString( XMLDocument, bOmitDeclaration, OwnerConfig.getConfigValue( ConstantsResponseFormat._XML_DataPacket_CharSet ), true, Logger, Lang );

    }

    public String ConvertXMLDocumentToString( Document XMLDocument, String strEnconding, CExtendedLogger Logger, CLanguage Lang ) {

       return ConvertXMLDocumentToString( XMLDocument, false, strEnconding, true, Logger, Lang );

    }

    public String ConvertXMLDocumentToString( Document XMLDocument, CExtendedLogger Logger, CLanguage Lang ) {

       return ConvertXMLDocumentToString( XMLDocument, false, OwnerConfig.getConfigValue( ConstantsResponseFormat._XML_DataPacket_CharSet ), true, Logger, Lang );

    }

    public Document BuildBasicResponseXMLStruct( String strVersion, boolean  bAttachErrorNode, CExtendedLogger Logger, CLanguage Lang ) {

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

               if ( bAttachErrorNode == true ) {

            	   Element Error = XMLDocument.createElement( XMLDataPacketTags._Error );
            	   Error.setAttribute( XMLDataPacketTags._XML_StructCode , "0" );
            	   Error.setAttribute( XMLDataPacketTags._XML_StructDescription , "" );
                   ErrorsSection.appendChild( Error );
               
               }
               
               DataPacketSection.appendChild( ErrorsSection );

            }
            else if ( bAttachErrorNode == true ) {

               Element ErrorSection = XMLDocument.createElement( XMLDataPacketTags._Error );
               ErrorSection.setAttribute( XMLDataPacketTags._XML_StructCode , "0" );
               ErrorSection.setAttribute( XMLDataPacketTags._XML_StructDescription , "" );
               DataPacketSection.appendChild( ErrorSection );

            }
            

        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }

        return XMLDocument;
    	
    }
    
    public Document BuildBasicResponseXMLStruct( String strVersion, CExtendedLogger Logger, CLanguage Lang ) {

    	return BuildBasicResponseXMLStruct( strVersion, true, Logger, Lang );

    }

    public Document BuildXMLSimpleMessageStruct( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, CExtendedLogger Logger, CLanguage Lang ) {

        Document XMLDocument = BuildBasicResponseXMLStruct( strVersion, Logger, Lang );

        try {

            NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

            if ( XML_FieldsSection.getLength() > 0 ) {

               if ( strSecurityToken != null && strSecurityToken.trim().isEmpty() == false ) {

                  Element XML_FieldSecurityToken = XMLDocument.createElement( XMLDataPacketTags._Field );
                  XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructSecurityToken );
                  XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBigInt );

                  XML_FieldsSection.item( 0 ).appendChild( XML_FieldSecurityToken );

               }

               if ( strTransactionID != null && strTransactionID.trim().isEmpty() == false ) {

                  Element XML_FieldSecurityToken = XMLDocument.createElement( XMLDataPacketTags._Field );
                  XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructTransactionID );
                  XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBigInt );

                  XML_FieldsSection.item( 0 ).appendChild( XML_FieldSecurityToken );

               }

               Element XML_FieldCode = XMLDocument.createElement( XMLDataPacketTags._Field );
               XML_FieldCode.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructCode );
               XML_FieldCode.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeInteger );

               XML_FieldsSection.item( 0 ).appendChild( XML_FieldCode );

               Element XML_FieldDescription = XMLDocument.createElement( XMLDataPacketTags._Field );
               XML_FieldDescription.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructDescription );
               XML_FieldDescription.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
               XML_FieldDescription.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructDescriptionLength );

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

                  XML_ROW.setAttribute( XMLDataPacketTags._XML_StructTransactionID, strTransactionID );

               }

               if ( strSecurityToken != null && strSecurityToken.trim().equals("") == false ) {

                  XML_ROW.setAttribute( XMLDataPacketTags._XML_StructSecurityToken, strSecurityToken );
                 
               }

               XML_ROW.setAttribute( XMLDataPacketTags._XML_StructCode, Integer.toString( intCode ) );
               XML_ROW.setAttribute( XMLDataPacketTags._XML_StructDescription, strDescription );

               XML_RowDataSection.item( 0 ).appendChild( XML_ROW );

            }

            if ( bAttachToError == true ) {

               NodeList XML_ErrorsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Errors );

               if ( XML_ErrorsSection != null )
                  ( (Element) XML_ErrorsSection.item( 0 ) ).setAttribute( XMLDataPacketTags._ErrorCount, "1" );

               NodeList XML_ErrorSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Error );

               if ( XML_ErrorSection.getLength() > 0 ) {

                  ((Element) XML_ErrorSection.item( 0 )).setAttribute( XMLDataPacketTags._XML_StructCode , Integer.toString( intCode ) );
                  ((Element) XML_ErrorSection.item( 0 )).setAttribute( XMLDataPacketTags._XML_StructDescription , strDescription );

                  if ( strTransactionID != null && strTransactionID.trim().equals( "" ) == false ) {

                     ((Element) XML_ErrorSection.item( 0 )).setAttribute( XMLDataPacketTags._XML_StructTransactionID, strTransactionID );

                  }

                  if ( strSecurityToken != null && strSecurityToken.trim().equals( "" ) == false ) {

                     ((Element) XML_ErrorSection.item( 0 )).setAttribute( XMLDataPacketTags._XML_StructSecurityToken, strSecurityToken );

                  }

               }

            }

        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }

        return XMLDocument;

    }
    
    class CSimpleXMLFieldDefinition {

    	public String strFieldName = "";
    	public String strFieldType = "";
    	public String strFieldWidth = "";
    	public String strFieldSubType = "";
    	
    	public CSimpleXMLFieldDefinition( String strFieldName, String strFieldType, String strFieldSubType, String strFieldWidth ) {
    		
    		this.strFieldName = strFieldName;
    		this.strFieldType = strFieldType;
    		this.strFieldSubType = strFieldSubType;
    		this.strFieldWidth = strFieldWidth;
    		
    	}
    	
    }
    
    public Document BuildXMLFieldDefinedStruct( ArrayList<CSimpleXMLFieldDefinition> FieldDefinitions, String strVersion, CExtendedLogger Logger, CLanguage Lang ) {

        Document XMLDocument = BuildBasicResponseXMLStruct( strVersion, false, Logger, Lang );

        try {

            NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

            if ( XML_FieldsSection.getLength() > 0 ) {

    			//Iterator<Entry<String, String>> i = FieldDefinitions.entrySet().iterator();
            	
                for ( int intIndexFieldDefinition = 0; intIndexFieldDefinition < FieldDefinitions.size(); intIndexFieldDefinition++ ) {
                	
                	//Entry<String,String> FieldDefinition = i.next();
                	CSimpleXMLFieldDefinition FieldDefinition = FieldDefinitions.get( intIndexFieldDefinition );
                	
                    Element XML_FieldSecurityToken = XMLDocument.createElement( XMLDataPacketTags._Field );
                    XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._AttrName, FieldDefinition.strFieldName );
                    XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._FieldType, FieldDefinition.strFieldType );

                    if ( FieldDefinition.strFieldSubType != null && FieldDefinition.strFieldSubType.isEmpty() == false ) {
                    	
                        XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._FieldSubType, FieldDefinition.strFieldSubType );
                    	
                    }
                    
                    if ( FieldDefinition.strFieldWidth != null && FieldDefinition.strFieldWidth.isEmpty() == false ) {
                    	
                        XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, FieldDefinition.strFieldWidth );
                    	
                    }
                    
                } 
            	
            }

            NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

            if ( XML_ParamsSection.getLength() > 0 ) {

               ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , "0" );

            }

        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }

        return XMLDocument;

    }
    
    public Document AddXMLToErrorSection( Document XMLDocument, LinkedHashMap<String,String> FieldValues, String strVersion, boolean bIncrementErrorCount ) {

        NodeList XML_ErrorsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Errors );

        if ( XML_ErrorsSection != null && XML_ErrorsSection.getLength() > 0 ) {
           
            //XML-DataPacket 1.1 Errors Section found
        	
        	if ( bIncrementErrorCount == true ) {

        		String strCountError = ( (Element) XML_ErrorsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._ErrorCount );

        		int intCountError = 0;

        		if ( strCountError != null )
        			intCountError = net.maindataservices.Utilities.StrToInteger( strCountError ) + 1;

        		( (Element) XML_ErrorsSection.item( 0 ) ).setAttribute( XMLDataPacketTags._ErrorCount, Integer.toString( intCountError ) );

        	}
        	
            Element Error = XMLDocument.createElement( XMLDataPacketTags._Error );
            
            Iterator<Entry<String, String>> i = FieldValues.entrySet().iterator();

            while ( i.hasNext() ) {

            	Entry<String,String> FieldValue = i.next();

            	Error.setAttribute( FieldValue.getKey(), FieldValue.getValue() );

            }
            
            ( (Element) XML_ErrorsSection.item( 0 ) ).appendChild( Error );
        	
        }
        else {
        	
            //XML-DataPacket 1.0 NO errors section found

        	NodeList XML_DataPacketSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._DataPacket );

            if ( XML_DataPacketSection != null && XML_DataPacketSection.getLength() > 0 ) {
            	
                Element Error = XMLDocument.createElement( XMLDataPacketTags._Error );

                Iterator<Entry<String, String>> i = FieldValues.entrySet().iterator();

                while ( i.hasNext() ) {

                	Entry<String,String> FieldValue = i.next();

                	Error.setAttribute( FieldValue.getKey(), FieldValue.getValue() );

                }
                
                ( (Element) XML_DataPacketSection.item( 0 ) ).appendChild( Error );
            	
            }            
            
        }
    	
    	return XMLDocument;
    	
    }
    
    public Document AddXMLToErrorSection( Document XMLDocument, int intCode, String strDescription, String strVersion, boolean bIncrementErrorCount ) {
    	
        NodeList XML_ErrorsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Errors );

        if ( XML_ErrorsSection != null && XML_ErrorsSection.getLength() > 0 ) {
           
            //XML-DataPacket 1.1 Errors Section found

        	if ( bIncrementErrorCount == true ) {
        	
        		String strCountError = ( (Element) XML_ErrorsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._ErrorCount );

        		int intCountError = 0;

        		if ( strCountError != null )
        			intCountError = net.maindataservices.Utilities.StrToInteger( strCountError ) + 1;

        		( (Element) XML_ErrorsSection.item( 0 ) ).setAttribute( XMLDataPacketTags._ErrorCount, Integer.toString( intCountError ) );

        	};
        	
            Element Error = XMLDocument.createElement( XMLDataPacketTags._Error );
            Error.setAttribute( XMLDataPacketTags._XML_StructCode , Integer.toString( intCode ) );
            Error.setAttribute( XMLDataPacketTags._XML_StructDescription , strDescription );
            
            ( (Element) XML_ErrorsSection.item( 0 ) ).appendChild( Error );
        	
        }
        else {
        	
            //XML-DataPacket 1.0 NO errors section found

        	NodeList XML_DataPacketSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._DataPacket );

            if ( XML_DataPacketSection != null && XML_DataPacketSection.getLength() > 0 ) {
            	
                Element Error = XMLDocument.createElement( XMLDataPacketTags._Error );
                Error.setAttribute( XMLDataPacketTags._XML_StructCode , Integer.toString( intCode ) );
                Error.setAttribute( XMLDataPacketTags._XML_StructDescription , strDescription );
                
                ( (Element) XML_DataPacketSection.item( 0 ) ).appendChild( Error );
            	
            }            
            
        }
    	
    	return XMLDocument;
    	
    }
    
    public Document AddXMLSimpleMessage( Document XMLDocument, int intCode, String strDescription, String strVersion, boolean bAttachToError, CExtendedLogger Logger, CLanguage Lang ) {
    	
        NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

        if ( XML_RowDataSection != null && XML_RowDataSection.getLength() > 0 ) {

            NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

            if ( XML_ParamsSection.getLength() > 0 ) {

         	   String strCurrentRowCount = ( (Element) XML_ParamsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._RowCount );
            	
         	   int intCurrentRowCount = 0;
         	   
         	   if ( strCurrentRowCount != null )
         	       intCurrentRowCount = net.maindataservices.Utilities.StrToInteger( strCurrentRowCount );
            		
               ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , Integer.toString( intCurrentRowCount + 1 ) );

            }

        	Element XML_ROW = XMLDocument.createElement( XMLDataPacketTags._Row );

        	XML_ROW.setAttribute( XMLDataPacketTags._XML_StructCode, Integer.toString( intCode ) );
        	XML_ROW.setAttribute( XMLDataPacketTags._XML_StructDescription, strDescription );

        	XML_RowDataSection.item( 0 ).appendChild( XML_ROW );
           
        }
    	
        if ( bAttachToError == true ) {
        	
        	XMLDocument = AddXMLToErrorSection( XMLDocument, intCode, strDescription, strVersion, true );
        	
        }
        
    	return XMLDocument;
    	
    }

    public Document AddXMLSimpleMessage( Document XMLDocument, LinkedHashMap<String,String> FieldValues, String strVersion, boolean bAttachToError, CExtendedLogger Logger, CLanguage Lang ) {
    	
        NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

        if ( XML_RowDataSection != null && XML_RowDataSection.getLength() > 0 ) {

            NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

            if ( XML_ParamsSection.getLength() > 0 ) {

         	   String strCurrentRowCount = ( (Element) XML_ParamsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._RowCount );
            	
         	   int intCurrentRowCount = 0;
         	   
         	   if ( strCurrentRowCount != null )
         	       intCurrentRowCount = net.maindataservices.Utilities.StrToInteger( strCurrentRowCount );
            		
               ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , Integer.toString( intCurrentRowCount + 1 ) );

            }
        	
           Element XML_ROW = XMLDocument.createElement( XMLDataPacketTags._Row );

		   Iterator<Entry<String, String>> i = FieldValues.entrySet().iterator();

           while ( i.hasNext() ) {
        	   
        	   Entry<String,String> FieldValue = i.next();
        	   
               XML_ROW.setAttribute( FieldValue.getKey(), FieldValue.getValue() );
        	   
           }

           XML_RowDataSection.item( 0 ).appendChild( XML_ROW );
           
        }
    	
        if ( bAttachToError == true ) {
        	
        	XMLDocument = AddXMLToErrorSection( XMLDocument, FieldValues, strVersion, true );
        	
        }
        
    	return XMLDocument;
    	
    }
    
    public String BuildXMLSimpleMessage( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, CExtendedLogger Logger, CLanguage Lang ) {

        Document XMLDocument = BuildXMLSimpleMessageStruct( strSecurityToken, strTransactionID, intCode, strDescription, bAttachToError, strVersion, Logger, Lang );

        String strXMLBuffer = ConvertXMLDocumentToString( XMLDocument, false, OwnerConfig.getConfigValue( ConstantsResponseFormat._XML_DataPacket_CharSet ), true, Logger, Lang );

        return strXMLBuffer;

    }
    
    public static String EncodeToValidXMLData( String strData ) {

        String strValidXMLData = "";
        
        try {

           strValidXMLData = new String( strData.getBytes( "ISO-8859-1" ), "UTF-8" );

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

    public Document BuildXMLMetaData( Document XMLDocument, ResultSetMetaData DataSetMetaData, ArrayList<String> arrIncludedFields, ArrayList<String> arrExcludedFields, CExtendedLogger Logger, CLanguage Lang ) {

        try {

           NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

           if ( XML_FieldsSection.getLength() > 0 ) {

              for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

                 String strFieldName   = DataSetMetaData.getColumnName( i );
                 
                 if ( strFieldName == null || strFieldName.isEmpty() == true )
                	 strFieldName = DataSetMetaData.getColumnLabel( i );
                 
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
					                        XML_Field.setAttribute( XMLDataPacketTags._FieldSubType, XMLDataPacketTags._FieldTypeBlobSubTypeBinary );
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
	    			
                    XML_FieldsSection.item( 0 ).appendChild( XML_Field );

                 }

              }

           }
           
        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
        
        return XMLDocument;

    }

    public Document AddXMLToRowDataSection( Document XMLDocument, ResultSet SQLDataSet, ArrayList<String> arrIncludedFields, ArrayList<String> arrExcludedFields, CExtendedLogger Logger, CLanguage Lang ) {

        try {

    	   int intRowCount = 0; 

           NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

           if ( XML_RowDataSection.getLength() > 0 ) {

              SimpleDateFormat DFormatter = new SimpleDateFormat("yyyyMMdd");
              SimpleDateFormat TFormatter = new SimpleDateFormat("HHmmss");
              SimpleDateFormat DTFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");

              java.sql.ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();

              while ( SQLDataSet.next() == true ) {

            	  intRowCount += 1;
            	  
            	  Element XML_ROW = XMLDocument.createElement( XMLDataPacketTags._Row );

            	  for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

            		  String strFieldName = DataSetMetaData.getColumnName( i );

            		  if ( strFieldName == null || strFieldName.isEmpty() == true )
            			  strFieldName = DataSetMetaData.getColumnLabel( i );

            		  int intFieldType = DataSetMetaData.getColumnType( i );

            		  if ( SQLDataSet.getObject( i ) != null ) {

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
							case -4:  //What the hell firebird
							case Types.BLOB: { 	
								
						                        Blob BinaryBLOBData = SQLDataSet.getBlob( strFieldName );
						                       
						                        String strBase64Coded = new String( Base64.encode( BinaryBLOBData.getBytes( 1, (int) BinaryBLOBData.length() ) ) );
				
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

            		  }
            		  else {

            			  XML_ROW.setAttribute( strFieldName, "" );

            		  }

            	  }

            	  XML_RowDataSection.item( 0 ).appendChild( XML_ROW );

              }

           }

           NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

           if ( XML_ParamsSection.getLength() > 0 ) {

        	   String strCurrentRowCount = ( (Element) XML_ParamsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._RowCount );
           	
        	   int intCurrentRowCount = 0;
        	   
        	   if ( strCurrentRowCount != null )
        	       intCurrentRowCount = net.maindataservices.Utilities.StrToInteger( strCurrentRowCount );
           		
              ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , Integer.toString( intCurrentRowCount + intRowCount ) );

           }
           
        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
        
        return XMLDocument;

    }
    
    public Document BuildXMLMetaData( Document XMLDocument, CMemoryRowSet MemoryRowSet, CExtendedLogger Logger, CLanguage Lang ) {
    	
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
					                        XML_Field.setAttribute( XMLDataPacketTags._FieldSubType, XMLDataPacketTags._FieldTypeBlobSubTypeBinary );
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

            	   XML_FieldsSection.item( 0 ).appendChild( XML_Field );

               }

            }
    	
    	}
    	catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

    	}

    	return XMLDocument;
    
    }
    
    public Document AddXMLToRowDataSection( Document XMLDocument, CMemoryRowSet MemoryRowSet, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	try {

    		int intRowCount = 0;
    		
    		NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

    		if ( XML_RowDataSection.getLength() > 0 ) {

    			SimpleDateFormat DFormatter = new SimpleDateFormat("yyyyMMdd");
    			SimpleDateFormat TFormatter = new SimpleDateFormat("HHmmss");
    			SimpleDateFormat DTFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");

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
    	    			
    				}

    				XML_RowDataSection.item( 0 ).appendChild( XML_ROW );

    			}

    		}

            NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

            if ( XML_ParamsSection.getLength() > 0 ) {

         	   String strCurrentRowCount = ( (Element) XML_ParamsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._RowCount );
            	
         	   int intCurrentRowCount = 0;
         	   
         	   if ( strCurrentRowCount != null )
         	       intCurrentRowCount = net.maindataservices.Utilities.StrToInteger( strCurrentRowCount );
            		
               ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , Integer.toString( intCurrentRowCount + intRowCount ) );

            }
            
    	}
    	catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
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
    	
    	if ( OwnerConfig != null )
    	   return OwnerConfig.getConfigValue( ConstantsResponseFormat._XML_DataPacket_ContentType );
    	else
    	   return ""; 
    	
    }
    
    @Override
    public String getCharacterEncoding() {
    	
    	if ( OwnerConfig != null )
    	   return OwnerConfig.getConfigValue( ConstantsResponseFormat._XML_DataPacket_CharSet );
    	else
    	   return ""; 
    	
    }

    public int DescribeService( CAbstractService Service, Element XMLNode_RowDataSection, CExtendedLogger Logger, CLanguage Lang ) {
	   
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
	        
            	intResult += 1;
            	
            	Element XMLNode_Row = XMLDocument.createElement( XMLDataPacketTags._Row );

            	if ( Service.getAuthRequired() == true )  
            		XMLNode_Row.setAttribute( XMLDataPacketTags._XML_StructAuthRequired , "Yes" );
            	else
            		XMLNode_Row.setAttribute( XMLDataPacketTags._XML_StructAuthRequired , "No" );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XML_StructServiceName , Service.getServiceName() );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XML_StructAccessType , strServiceType );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XML_StructDescription , Service.getServiceDescription() );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XML_StructAuthor , Service.getServiceAuthor() );

            	XMLNode_Row.setAttribute( XMLDataPacketTags._XML_StructAuthorContact , Service.getServiceAuthorContact() );

            	XMLNode_RowDataSection.appendChild( XMLNode_Row );

            	Element XMLNode_InputParameters = XMLDocument.createElement( XMLDataPacketTags._InputParameters );

            	Entry< String,ArrayList<CInputServiceParameter> > GroupIPSEntry = It.next();
            	
    			String strKey = GroupIPSEntry.getKey();

            	if ( strKey.toLowerCase().equals( ConstantsServicesTags._Default ) == false ) {
            		
            		XMLNode_InputParameters.setAttribute(  XMLDataPacketTags._XML_StructParamSetName, strKey ); 
            	
            	}
            	
            	for ( CInputServiceParameter InputServiceParameter : GroupIPSEntry.getValue() ) {

            		Element XMLNode_Row_InputParameter = XMLDocument.createElement( XMLDataPacketTags._Row );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XML_StructParamName , InputServiceParameter.getParameterName() );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XML_StructRequired , InputServiceParameter.getParameterRequired()?"Yes":"No" );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XML_StructType , InputServiceParameter.getParameterDataType() );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XML_StructTypeWidth , InputServiceParameter.getParameterDataTypeWidth() );

            		//XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XMLStructSubType, InputServiceParameter.getParameterScope() );

            		XMLNode_Row_InputParameter.setAttribute( XMLDataPacketTags._XML_StructDescription , InputServiceParameter.getParameterDescription() );

            		XMLNode_InputParameters.appendChild( XMLNode_Row_InputParameter );

            	}

            	XMLNode_RowDataSection.appendChild( XMLNode_InputParameters );
	   
            }
	        
    	}   
    	
    	return intResult;

     }

    @Override 
    public String EnumerateServices( HashMap<String,CAbstractService> RegisteredServices, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );

        NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

        if ( XML_FieldsSection.getLength() > 0 ) {

           Element XML_FieldAuthRequired = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldAuthRequired.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructAuthRequired );
           XML_FieldAuthRequired.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldAuthRequired.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructAuthRequiredLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldAuthRequired );
           
           Element XML_FieldName = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldName.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructServiceName );
           XML_FieldName.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldName.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructServiceNameLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldName );

           Element XML_FieldType = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldType.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructAccessType );
           XML_FieldType.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldType.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructAccessTypeLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldType );

           Element XML_FieldDescription = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldDescription.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructDescription );
           XML_FieldDescription.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldDescription.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructDescriptionLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldDescription );

           Element XML_FieldAutor = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldAutor.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructAuthor );
           XML_FieldAutor.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldAutor.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructAuthorLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldAutor );

           Element XML_FieldAutorContact = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldAutorContact.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructAuthorContact );
           XML_FieldAutorContact.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
           XML_FieldAutorContact.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructAuthorContactLength );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldAutorContact );

           Element XML_FieldInputParameters = XMLDocument.createElement( XMLDataPacketTags._Field );
           XML_FieldInputParameters.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._InputParameters );
           XML_FieldInputParameters.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeNested );

           XML_FieldsSection.item( 0 ).appendChild( XML_FieldInputParameters );

              Element XML_FieldParameterName = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterName.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructParamName );
              XML_FieldParameterName.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterName.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructParamNameLength );

              XML_FieldInputParameters.appendChild( XML_FieldParameterName );

              Element XML_FieldParameterRequired = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterRequired.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructRequired );
              XML_FieldParameterRequired.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterRequired.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructRequiredLength );

              XML_FieldInputParameters.appendChild( XML_FieldParameterRequired );

              Element XML_FieldParameterType = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterType.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructType );
              XML_FieldParameterType.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterType.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructTypeLength );

              XML_FieldInputParameters.appendChild( XML_FieldParameterType );

              Element XML_FieldParameterTypeWidth = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterTypeWidth.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructTypeWidth );
              XML_FieldParameterTypeWidth.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeShortInteger );

              XML_FieldInputParameters.appendChild( XML_FieldParameterTypeWidth );

              Element XML_FieldParameterSubType = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterSubType.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructSubType );
              XML_FieldParameterSubType.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterType.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructSubTypeLength );

              XML_FieldInputParameters.appendChild( XML_FieldParameterSubType );

              Element XML_FieldParameterDescription = XMLDocument.createElement( XMLDataPacketTags._Field );
              XML_FieldParameterDescription.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructParamDescription );
              XML_FieldParameterDescription.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeString );
              XML_FieldParameterDescription.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, XMLDataPacketTags._XML_StructParamDescriptionLength );

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
                 intRowCount += this.DescribeService( Service, (Element) XML_RowDataSection.item( 0 ), Logger, Lang );
        
           }

        }

        NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

        if ( XML_ParamsSection.getLength() > 0 ) {

           ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount, Integer.toString( intRowCount ) );

        }

    	return ConvertXMLDocumentToString( XMLDocument, true, this.getCharacterEncoding(), Logger, Lang );
    	
    }
    
    @Override
	public String FormatResultSet( ResultSet ResultSet, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

    	String strResult = "";
    	
        try {

	        ResultSetMetaData DataSetMetaData = ResultSet.getMetaData();
	
	        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );
	
	        ArrayList<String> arrIncludedFields = new ArrayList<String>();
	        ArrayList<String> arrExcludedFields = new ArrayList<String>();
	
	        XMLDocument = BuildXMLMetaData( XMLDocument, DataSetMetaData, arrIncludedFields, arrExcludedFields, Logger, Lang );
	
	        XMLDocument = AddXMLToRowDataSection( XMLDocument, ResultSet, arrIncludedFields, arrExcludedFields, Logger, Lang );

            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );

        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }

    	return strResult;
		
	}
    
    public String FormatResultsSets( ArrayList<ResultSet> ResultsSetsList, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	String strResult = "";
    	
        try {

        	if ( ResultsSetsList.size() > 0 ) {

        		ResultSet ResultSetMetaData = ResultsSetsList.get( 0 );
	        	
		        ResultSetMetaData DataSetMetaData = ResultSetMetaData.getMetaData();
		
		        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );
		
		        ArrayList<String> arrIncludedFields = new ArrayList<String>();
		        ArrayList<String> arrExcludedFields = new ArrayList<String>();
		
		        XMLDocument = BuildXMLMetaData( XMLDocument, DataSetMetaData, arrIncludedFields, arrExcludedFields, Logger, Lang );
		
		        for ( ResultSet ResultSetToAdd: ResultsSetsList ) {    
		        
		           XMLDocument = AddXMLToRowDataSection( XMLDocument, ResultSetToAdd, arrIncludedFields, arrExcludedFields, Logger, Lang );
		        
		        }
		        
	            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );

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
    public String FormatResultSet( CResultSetResult ResultSetResult, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	String strResult = "";
    	
        try {

        	Document XMLDocument = null;
        	
        	if ( ResultSetResult.Result != null ) {

            	XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );

        		ArrayList<String> arrIncludedFields = new ArrayList<String>();
        		ArrayList<String> arrExcludedFields = new ArrayList<String>();

        		XMLDocument = BuildXMLMetaData( XMLDocument, ResultSetResult.Result.getMetaData(), arrIncludedFields, arrExcludedFields, Logger, Lang );

        		XMLDocument = AddXMLToRowDataSection( XMLDocument, ResultSetResult.Result, arrIncludedFields, arrExcludedFields, Logger, Lang );

        	}
        	else {

        		ArrayList<CSimpleXMLFieldDefinition> FieldDefinitons = new ArrayList<CSimpleXMLFieldDefinition>();
        		
        		FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructAffectedRows, XMLDataPacketTags._FieldTypeBigInt, "", "" ) );
        		FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructCode, XMLDataPacketTags._FieldTypeInteger, "", "" ) );
        		FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructDescription, XMLDataPacketTags._FieldTypeString, "", XMLDataPacketTags._XML_StructDescriptionLength ) );
        		
        		XMLDocument = this.BuildXMLFieldDefinedStruct( FieldDefinitons, strVersion, Logger, Lang );
        		
        		LinkedHashMap<String,String> FieldValues = new LinkedHashMap<String,String>();
        		
        		FieldValues.put( XMLDataPacketTags._XML_StructAffectedRows, Long.toString( ResultSetResult.lngAffectedRows ) );
        		FieldValues.put( XMLDataPacketTags._XML_StructCode, Integer.toString( ResultSetResult.intCode ) );
        		FieldValues.put( XMLDataPacketTags._XML_StructDescription, ResultSetResult.strDescription );
        		
        		if ( ResultSetResult.intCode >= 0 )
        			XMLDocument = AddXMLSimpleMessage( XMLDocument, FieldValues, strVersion, false, Logger, Lang );
        		else
        			XMLDocument = AddXMLSimpleMessage( XMLDocument, FieldValues, strVersion, true, Logger, Lang );

        	}

        	strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );
	            
        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }
    	
    	return strResult;
    	
    }

    public String FormatResultsSets( ArrayList<CResultSetResult> ResultsSetsList, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang, int intDummyParam ) {
    	
    	String strResult = "";
    	
        try {

        	if ( ResultsSetsList.size() > 0 ) {

        		ResultSet ResultSetMetaData = CResultSetResult.getFirstResultSetNotNull( ResultsSetsList );
        		
    			Document XMLDocument = null; 

    			boolean bNodeErrorAdded = false;
    			
        		if ( ResultSetMetaData != null ) {
		        
        			XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, false, Logger, Lang );
        			
        			ResultSetMetaData DataSetMetaData = ResultSetMetaData.getMetaData();

        			ArrayList<String> arrIncludedFields = new ArrayList<String>();
        			ArrayList<String> arrExcludedFields = new ArrayList<String>();

        			XMLDocument = BuildXMLMetaData( XMLDocument, DataSetMetaData, arrIncludedFields, arrExcludedFields, Logger, Lang );

            		LinkedHashMap<String,String> FieldValues = new LinkedHashMap<String,String>();
        			
        			for ( CResultSetResult ResultSetResultToAdd: ResultsSetsList ) { 
        				
        				//String strTmp = "";

        				if ( ResultSetResultToAdd.Result != null ) {   

        					//strTmp = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding() );
        				
        					XMLDocument = AddXMLToRowDataSection( XMLDocument, ResultSetResultToAdd.Result, arrIncludedFields, arrExcludedFields, Logger, Lang );

        					//strTmp = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding() );
        					
        				}
        				else {
        					              
                    		FieldValues.put( XMLDataPacketTags._XML_StructAffectedRows, Long.toString( ResultSetResultToAdd.lngAffectedRows ) );
                    		FieldValues.put( XMLDataPacketTags._XML_StructCode, Integer.toString( ResultSetResultToAdd.intCode ) );
                    		FieldValues.put( XMLDataPacketTags._XML_StructDescription, ResultSetResultToAdd.strDescription );

        					XMLDocument = AddXMLToErrorSection( XMLDocument, FieldValues, strVersion, true );
        					
        					bNodeErrorAdded = true;
        					
        				}

        			}
		        
        		}
        		else {
        			
            		ArrayList<CSimpleXMLFieldDefinition> FieldDefinitons = new ArrayList<CSimpleXMLFieldDefinition>();
            		
            		FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructAffectedRows, XMLDataPacketTags._FieldTypeBigInt, "", "" ) );
            		FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructCode, XMLDataPacketTags._FieldTypeInteger, "", "" ) );
            		FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructDescription, XMLDataPacketTags._FieldTypeString, "", XMLDataPacketTags._XML_StructDescriptionLength ) );
            		
            		XMLDocument = this.BuildXMLFieldDefinedStruct( FieldDefinitons, strVersion, Logger, Lang );
            		
            		LinkedHashMap<String,String> FieldValues = new LinkedHashMap<String,String>();
        			
        			for ( CResultSetResult ResultSetResultToAdd: ResultsSetsList ) {    

                		FieldValues.put( XMLDataPacketTags._XML_StructAffectedRows, Long.toString( ResultSetResultToAdd.lngAffectedRows ) );
                		FieldValues.put( XMLDataPacketTags._XML_StructCode, Integer.toString( ResultSetResultToAdd.intCode ) );
                		FieldValues.put( XMLDataPacketTags._XML_StructDescription, ResultSetResultToAdd.strDescription );
        				
        				if ( ResultSetResultToAdd.intCode >= 0 ) {
        				
        					XMLDocument = AddXMLSimpleMessage( XMLDocument, FieldValues, strVersion, false, Logger, Lang );
        					
        				}	
        				else {
        				
        					XMLDocument = AddXMLSimpleMessage( XMLDocument, FieldValues, strVersion, true, Logger, Lang );
        				 
        					bNodeErrorAdded = true;
        				
        				}	
        			
        			}
        			
        		}
		        
        		if ( bNodeErrorAdded == false ) { //Add the default node error
        			
        		    XMLDocument = this.AddXMLToErrorSection( XMLDocument, 0, "", strVersion, false );
        			
        		}
        		
	            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );

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

	        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );
	
	        XMLDocument = BuildXMLMetaData( XMLDocument, MemoryRowSet, Logger, Lang );
	
	        XMLDocument = AddXMLToRowDataSection( XMLDocument, MemoryRowSet, Logger, Lang );

            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );

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
    public String FormatMemoryRowSets( ArrayList<CMemoryRowSet> MemoryRowSets, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	String strResult = "";

        try {

        	if ( MemoryRowSets.size() > 0 ) {
        	
		        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );
		
		        CMemoryRowSet FirstMemoryRowSet = MemoryRowSets.get( 0 );

        		XMLDocument = BuildXMLMetaData( XMLDocument, FirstMemoryRowSet, Logger, Lang );
		
		        for ( CMemoryRowSet MemoryRowSetToAdd: MemoryRowSets ) {   
        		
		        	XMLDocument = AddXMLToRowDataSection( XMLDocument, MemoryRowSetToAdd, Logger, Lang );
		        
		        }
	
	            strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );

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
    public String FormatSimpleMessage( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	return BuildXMLSimpleMessage( strSecurityToken, strTransactionID, intCode, strDescription, bAttachToError, strVersion, Logger, Lang );
    	
    }

}
