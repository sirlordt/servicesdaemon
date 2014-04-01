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

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
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
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import net.maindataservices.Base64;





import net.maindataservices.Base64;
import net.maindataservices.Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import sun.misc.BASE64Encoder;
//import org.apache.commons.codec.binary.Base64;





import AbstractDBEngine.CAbstractDBEngine;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryFieldData;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultDataSet;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsMessagesCodes;
import ExtendedLogger.CExtendedLogger;

public class CXMLDataPacketResponseFormat extends CAbstractResponseFormat {

	public CXMLDataPacketResponseFormat() {

		this.strName = XMLDataPacketTags._ResponseFormat_XML_DATAPACKET;
		strMinVersion = "1.0";
		strMaxVersion = "2.0";
		
	}

    public String convertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, String strEnconding, boolean bIndent, CExtendedLogger Logger, CLanguage Lang ) {
        
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
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );
        
        }
        
        return strResult;
    
    }

    public String convertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, String strEnconding, CExtendedLogger Logger, CLanguage Lang ) {

       return convertXMLDocumentToString( XMLDocument, bOmitDeclaration, strEnconding, true, Logger, Lang );

    }

    public String convertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, boolean bIndent, CExtendedLogger Logger, CLanguage Lang ) {

       return convertXMLDocumentToString( XMLDocument, bOmitDeclaration, (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._XML_DataPacket_CharSet, null ), bIndent, Logger, Lang );

    }

    public String convertXMLDocumentToString( Document XMLDocument, boolean bOmitDeclaration, CExtendedLogger Logger, CLanguage Lang ) {

       return convertXMLDocumentToString( XMLDocument, bOmitDeclaration, (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._XML_DataPacket_CharSet, null ), true, Logger, Lang );

    }

    public String convertXMLDocumentToString( Document XMLDocument, String strEnconding, CExtendedLogger Logger, CLanguage Lang ) {

       return convertXMLDocumentToString( XMLDocument, false, strEnconding, true, Logger, Lang );

    }

    public String convertXMLDocumentToString( Document XMLDocument, CExtendedLogger Logger, CLanguage Lang ) {

       return convertXMLDocumentToString( XMLDocument, false, (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._XML_DataPacket_CharSet, null ), true, Logger, Lang );

    }

    public Document buildBasicResponseXMLStruct( String strVersion, boolean  bAttachErrorNode, CExtendedLogger Logger, CLanguage Lang ) {

        Document XMLDocument = null;

        try {

            DocumentBuilderFactory DocBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder DocBuilder = DocBuilderFactory.newDocumentBuilder();
            XMLDocument = DocBuilder.newDocument();
            //XMLDocument.

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
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

        }

        return XMLDocument;
    	
    }
    
    public Document buildBasicResponseXMLStruct( String strVersion, CExtendedLogger Logger, CLanguage Lang ) {

    	return buildBasicResponseXMLStruct( strVersion, true, Logger, Lang );

    }

    public Document buildXMLSimpleMessageStruct( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, CExtendedLogger Logger, CLanguage Lang ) {

        Document XMLDocument = buildBasicResponseXMLStruct( strVersion, Logger, Lang );

        try {

            NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

            if ( XML_FieldsSection.getLength() > 0 ) {

               if ( strSecurityToken != null && strSecurityToken.trim().isEmpty() == false ) {

                  Element XML_FieldSecurityToken = XMLDocument.createElement( XMLDataPacketTags._Field );
                  XML_FieldSecurityToken.setAttribute( XMLDataPacketTags._AttrName, XMLDataPacketTags._XML_StructSecurityTokenID );
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

                  XML_ROW.setAttribute( XMLDataPacketTags._XML_StructSecurityTokenID, strSecurityToken );
                 
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

                     ((Element) XML_ErrorSection.item( 0 )).setAttribute( XMLDataPacketTags._XML_StructSecurityTokenID, strSecurityToken );

                  }

               }

            }

        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

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
    
    public Document buildXMLFieldDefinedStruct( ArrayList<CSimpleXMLFieldDefinition> FieldDefinitions, String strVersion, CExtendedLogger Logger, CLanguage Lang ) {

        Document XMLDocument = buildBasicResponseXMLStruct( strVersion, false, Logger, Lang );

        try {

            NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

            if ( XML_FieldsSection.getLength() > 0 ) {

    			//Iterator<Entry<String, String>> i = FieldDefinitions.entrySet().iterator();
            	
                for ( int intIndexFieldDefinition = 0; intIndexFieldDefinition < FieldDefinitions.size(); intIndexFieldDefinition++ ) {
                	
                	//Entry<String,String> FieldDefinition = i.next();
                	CSimpleXMLFieldDefinition FieldDefinition = FieldDefinitions.get( intIndexFieldDefinition );
                	
                    Element XML_Field = XMLDocument.createElement( XMLDataPacketTags._Field );
                    XML_Field.setAttribute( XMLDataPacketTags._AttrName, FieldDefinition.strFieldName );
                    XML_Field.setAttribute( XMLDataPacketTags._FieldType, FieldDefinition.strFieldType );

                    if ( FieldDefinition.strFieldSubType != null && FieldDefinition.strFieldSubType.isEmpty() == false ) {
                    	
                        XML_Field.setAttribute( XMLDataPacketTags._FieldSubType, FieldDefinition.strFieldSubType );
                    	
                    }
                    
                    if ( FieldDefinition.strFieldWidth != null && FieldDefinition.strFieldWidth.isEmpty() == false ) {
                    	
                        XML_Field.setAttribute( XMLDataPacketTags._FieldTypeStringWidth, FieldDefinition.strFieldWidth );
                    	
                    }
                    
                    XML_FieldsSection.item(0).appendChild( XML_Field );
                    
                } 
            	
            }

            NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

            if ( XML_ParamsSection.getLength() > 0 ) {

               ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , "0" );

            }

        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

        }

        return XMLDocument;

    }
    
    public Document addXMLToErrorSection( Document XMLDocument, LinkedHashMap<String,String> FieldValues, String strVersion, boolean bIncrementErrorCount ) {

        NodeList XML_ErrorsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Errors );

        if ( XML_ErrorsSection != null && XML_ErrorsSection.getLength() > 0 ) {
           
            //XML-DataPacket 1.1 Errors Section found
        	
        	if ( bIncrementErrorCount == true ) {

        		String strCountError = ( (Element) XML_ErrorsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._ErrorCount );

        		int intCountError = 0;

        		if ( strCountError != null )
        			intCountError = net.maindataservices.Utilities.strToInteger( strCountError ) + 1;

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
    
    public Document addXMLToErrorSection( Document XMLDocument, int intCode, String strDescription, String strVersion, boolean bIncrementErrorCount ) {
    	
        NodeList XML_ErrorsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Errors );

        if ( XML_ErrorsSection != null && XML_ErrorsSection.getLength() > 0 ) {
           
            //XML-DataPacket 1.1 Errors Section found

        	if ( bIncrementErrorCount == true ) {
        	
        		String strCountError = ( (Element) XML_ErrorsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._ErrorCount );

        		int intCountError = 0;

        		if ( strCountError != null )
        			intCountError = net.maindataservices.Utilities.strToInteger( strCountError ) + 1;

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
    
    public Document addXMLSimpleMessage( Document XMLDocument, int intCode, String strDescription, String strVersion, boolean bAttachToError, CExtendedLogger Logger, CLanguage Lang ) {
    	
        NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

        if ( XML_RowDataSection != null && XML_RowDataSection.getLength() > 0 ) {

            NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

            if ( XML_ParamsSection.getLength() > 0 ) {

         	   String strCurrentRowCount = ( (Element) XML_ParamsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._RowCount );
            	
         	   int intCurrentRowCount = 0;
         	   
         	   if ( strCurrentRowCount != null )
         	       intCurrentRowCount = net.maindataservices.Utilities.strToInteger( strCurrentRowCount );
            		
               ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , Integer.toString( intCurrentRowCount + 1 ) );

            }

        	Element XML_ROW = XMLDocument.createElement( XMLDataPacketTags._Row );

        	XML_ROW.setAttribute( XMLDataPacketTags._XML_StructCode, Integer.toString( intCode ) );
        	XML_ROW.setAttribute( XMLDataPacketTags._XML_StructDescription, strDescription );

        	XML_RowDataSection.item( 0 ).appendChild( XML_ROW );
           
        }
    	
        if ( bAttachToError == true ) {
        	
        	XMLDocument = addXMLToErrorSection( XMLDocument, intCode, strDescription, strVersion, true );
        	
        }
        
    	return XMLDocument;
    	
    }

    public Document addXMLSimpleMessage( Document XMLDocument, LinkedHashMap<String,String> FieldValues, String strVersion, boolean bAttachToError, CExtendedLogger Logger, CLanguage Lang ) {
    	
        NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

        if ( XML_RowDataSection != null && XML_RowDataSection.getLength() > 0 ) {

            NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

            if ( XML_ParamsSection.getLength() > 0 ) {

         	   String strCurrentRowCount = ( (Element) XML_ParamsSection.item( 0 ) ).getAttribute( XMLDataPacketTags._RowCount );
            	
         	   int intCurrentRowCount = 0;
         	   
         	   if ( strCurrentRowCount != null )
         	       intCurrentRowCount = net.maindataservices.Utilities.strToInteger( strCurrentRowCount );
            		
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
        	
        	XMLDocument = addXMLToErrorSection( XMLDocument, FieldValues, strVersion, true );
        	
        }
        
    	return XMLDocument;
    	
    }
    
    public String buildXMLSimpleMessage( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, CExtendedLogger Logger, CLanguage Lang ) {

        Document XMLDocument = buildXMLSimpleMessageStruct( strSecurityToken, strTransactionID, intCode, strDescription, bAttachToError, strVersion, Logger, Lang );

        String strXMLBuffer = convertXMLDocumentToString( XMLDocument, false, (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._XML_DataPacket_CharSet, null ), true, Logger, Lang );

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

    /*public Document BuildXMLMetaData( Document XMLDocument, ResultSetMetaData DataSetMetaData, CAbstractDBEngine DBEngine, ArrayList<String> arrIncludedFields, ArrayList<String> arrExcludedFields, CExtendedLogger Logger, CLanguage Lang ) {

        try {

           NodeList XML_FieldsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Fields );

           if ( XML_FieldsSection.getLength() > 0 ) {

              for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

                 String strFieldName   = DataSetMetaData.getColumnName( i );
                 
                 if ( strFieldName == null || strFieldName.isEmpty() == true )
                	 strFieldName = DataSetMetaData.getColumnLabel( i );
                 
                 int    intFieldType   = DBEngine.getJavaSQLColumnType( DataSetMetaData.getColumnType( i ), Logger, Lang );
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
						case Types.BOOLEAN: { 
							
							                   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBoolean );
						                       break; 
						                       
					                        }
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
						case Types.NUMERIC:	
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

    }*/
    
    public void printXMLHeader( PrintWriter TempResponseFormatedFileWriter, String strCharacterEncoding ) {
    
		TempResponseFormatedFileWriter.println( "<?xml version=\"1.0\" encoding=\"" + strCharacterEncoding + "\"?>" );
    
    }
    
    public void printXMLDataPacketSection( PrintWriter TempResponseFormatedFileWriter, String strVersion, boolean bOpen ) {

		if ( bOpen )  
			TempResponseFormatedFileWriter.println( "<" + XMLDataPacketTags._DataPacket + " " + XMLDataPacketTags._DPVersion + "=\"" + strVersion + "\">" );
		else
			TempResponseFormatedFileWriter.println( "</" + XMLDataPacketTags._DataPacket + ">" );
    	
    }
    
    public void printXMLRowDataSection( PrintWriter TempResponseFormatedFile, boolean bOpen ) {
    	
		if ( bOpen )  
			TempResponseFormatedFile.println( "  <" + XMLDataPacketTags._RowData + ">" );
		else
			TempResponseFormatedFile.println( "  </" + XMLDataPacketTags._RowData + ">" );
    	
    }

    public boolean printXMLMetaDataSection( PrintWriter TempResponseFormatedFile, ResultSetMetaData DataSetMetaData, CAbstractDBEngine DBEngine, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;
    	
    	try {

    		TempResponseFormatedFile.println( "  <" + XMLDataPacketTags._MetaData + ">" );

    		TempResponseFormatedFile.println( "    <" + XMLDataPacketTags._Fields + ">" );

    		for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

    			String strFieldName   = DataSetMetaData.getColumnName( i );

    			if ( strFieldName == null || strFieldName.isEmpty() == true )
    				strFieldName = DataSetMetaData.getColumnLabel( i );

    			int intFieldType   = DBEngine.getJavaSQLColumnType( DataSetMetaData.getColumnType( i ), Logger, Lang );
    			int intFieldLength = DataSetMetaData.getColumnDisplaySize( i );

    			String strFieldDef = "      <" + XMLDataPacketTags._Field + " " + XMLDataPacketTags._AttrName + "=\"" + strFieldName + "\" ";

    			switch ( intFieldType ) {

	    			case Types.INTEGER: { strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" + XMLDataPacketTags._FieldTypeInteger + "\"/>"; break; }
	    			case Types.BIGINT: { strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" + XMLDataPacketTags._FieldTypeBigInt + "\"/>"; break; }
	    			case Types.SMALLINT: { strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" + XMLDataPacketTags._FieldTypeShortInteger + "\"/>"; break; }
	    			case Types.VARCHAR: 
	    			case Types.CHAR: {  
	
	    				strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" + XMLDataPacketTags._FieldTypeString + "\" " + XMLDataPacketTags._FieldTypeStringWidth + "=\"" + Integer.toString( intFieldLength ) + "\"/>";
	    				break; 
	
	    			}
	    			case Types.BOOLEAN: { 
	
	    				strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" + XMLDataPacketTags._FieldTypeBoolean + "\"/>";
	    				break; 
	
	    			}
	    			case Types.BLOB: { 	
	
	    				strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" + XMLDataPacketTags._FieldTypeBlob + "\" " + XMLDataPacketTags._FieldSubType + "=\"" + XMLDataPacketTags._FieldTypeBlobSubTypeBinary + "\"/>";
	    				break; 
	
	    			}
	    			case Types.DATE: {
	
	    				strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" + XMLDataPacketTags._FieldTypeDate + "\"/>";
	    				break; 
	
	    			}
	    			case Types.TIME: {  
	
	    				strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" + XMLDataPacketTags._FieldTypeTime + "\"/>";
	    				break; 
	
	    			}
	    			case Types.TIMESTAMP: {  
	
	    				strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" +  XMLDataPacketTags._FieldTypeDateTime + "\"/>";
	    				break; 
	
	    			}
	    			case Types.FLOAT:
	    			case Types.NUMERIC:	
	    			case Types.DECIMAL: {  strFieldDef = strFieldDef + XMLDataPacketTags._FieldType + "=\"" + XMLDataPacketTags._FieldTypeFloat + "\"/>"; break; }
	    			case Types.DOUBLE: {  break; }

    			}
    			
        		TempResponseFormatedFile.println( strFieldDef );

    		}

    		TempResponseFormatedFile.println( "    </" + XMLDataPacketTags._Fields + ">" );
    		
    		TempResponseFormatedFile.println( "    <" + XMLDataPacketTags._Params + "/>                            " ); //" " + XMLDataPacketTags._RowCount + "=\"0\" />" );
    		
    		TempResponseFormatedFile.println( "  </" + XMLDataPacketTags._MetaData + ">" );
    		
    		//TempResponseFormatedFile.println( "  <" + XMLDataPacketTags._RowData + ">" );
    		
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
    
    public long printAddXMLToRowDataSection( String strTempDir, String strTempFile, PrintWriter TempResponseFormatedFileWriter, OutputStream TempStreamResponseFormatedFile, ResultSet SQLDataSet, CAbstractDBEngine DBEngine, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	long longRowCount = 0; 

    	try {

            //SimpleDateFormat DFormatter = new SimpleDateFormat("yyyyMMdd");
            //SimpleDateFormat TFormatter = new SimpleDateFormat("HHmmss");
            //SimpleDateFormat DTFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");

            java.sql.ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();

            while ( SQLDataSet.next() == true ) {

            	longRowCount += 1;

            	TempResponseFormatedFileWriter.print( "    <" + XMLDataPacketTags._Row + " " );  

            	for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

            		String strFieldName = DataSetMetaData.getColumnName( i );

            		if ( strFieldName == null || strFieldName.isEmpty() == true )
            			strFieldName = DataSetMetaData.getColumnLabel( i );

            		int intFieldType = DataSetMetaData.getColumnType( i );

            		intFieldType = DBEngine.getJavaSQLColumnType( intFieldType, Logger, Lang );

            		if ( SQLDataSet.getObject( i ) != null ) {

            			if ( intFieldType != Types.BLOB ) {

            				String strFieldValue = DBEngine.getFieldValueAsString( intFieldType, strFieldName, SQLDataSet, "yyyyMMdd", "HHmmss", "yyyyMMdd HHmmss", Logger, Lang );

            				TempResponseFormatedFileWriter.print( strFieldName + "=\"" + strFieldValue + "\" " );

            			}
            			else {

            				TempResponseFormatedFileWriter.print( strFieldName + "=\"" );
            				TempResponseFormatedFileWriter.flush();
            				DBEngine.writeBlobValueAsStringToFile( intFieldType, strFieldName, strTempDir, strTempFile, TempStreamResponseFormatedFile, SQLDataSet, "yyyyMMdd", "HHmmss", "yyyyMMdd HHmmss", Logger, Lang );
            				TempResponseFormatedFileWriter.print( "\" " );

            			}

            		}
            		else {

            			TempResponseFormatedFileWriter.print( strFieldName + "=\"\" " );  

            		}

            	}

            	TempResponseFormatedFileWriter.println( "/>" );  

            }
    		
    		
    	}
    	catch ( Exception Ex ) {

    		if ( Logger != null )
    			Logger.logException( "-1010", Ex.getMessage(), Ex );
    		else if ( OwnerConfig != null && OwnerConfig.Logger != null )
    			OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

    	}

    	return longRowCount;
    	   
    }

    public boolean printXMLErrorsSection( PrintWriter TempResponseFormatedFile, ArrayList<String> strErrorCodeDescription, String strVersion ) {
    	
    	boolean bResult = false;
    	
    	if ( strVersion.equals( "1.0" ) ) {
    		
        	if ( strErrorCodeDescription.size() == 0 )
        		TempResponseFormatedFile.println( "  <" + XMLDataPacketTags._Error + " " + XMLDataPacketTags._XML_StructCode + "=\"0\" " + XMLDataPacketTags._XML_StructDescription + "=\"\" />" );
        	else
        		TempResponseFormatedFile.println( "  <" + XMLDataPacketTags._Error + " "+ strErrorCodeDescription.get( 0 ) + " />" );
    		
    	}
    	else {
    		
        	if ( strErrorCodeDescription.size() == 0 ) {

        		TempResponseFormatedFile.println( "  <" + XMLDataPacketTags._Errors + " " + XMLDataPacketTags._ErrorCount + "=\"0\" >" );
        		TempResponseFormatedFile.println( "    <" + XMLDataPacketTags._Error + " " + XMLDataPacketTags._XML_StructCode + "=\"0\" " + XMLDataPacketTags._XML_StructDescription + "=\"\" />" );
        		TempResponseFormatedFile.println( "  </" + XMLDataPacketTags._Errors + ">" );
        		
        	}	
        	else {
        	
        		TempResponseFormatedFile.println( "  <" + XMLDataPacketTags._Errors + " " + XMLDataPacketTags._ErrorCount + "=\"" + Integer.toString( strErrorCodeDescription.size() ) + "\" >" );
        		
        		for ( int intIndex = 0; intIndex < strErrorCodeDescription.size(); intIndex++ ) {
        		
        			TempResponseFormatedFile.println( "    <" + XMLDataPacketTags._Error + " " + strErrorCodeDescription.get( intIndex ) + " />" );
        			
        		}
        		
        		TempResponseFormatedFile.println( "  </" + XMLDataPacketTags._Errors + ">" );
        		
        	}
    		
    	}
    	
    	return bResult;
    	
    }    
    
    public boolean printParamsSectionRowCount( String strTempFile, long lngRowCount, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;

		try {

			RandomAccessFile TempFormatedResponseFile = new RandomAccessFile( strTempFile, "rw" );
			
	    	String strLine;
	    	
	    	long lngCursorPosition = TempFormatedResponseFile.getChannel().position();
	    	
	    	while ( ( strLine = TempFormatedResponseFile.readLine() ) != null ) {
	    		
	    		strLine = strLine.trim();
	    		
	    		if ( strLine.indexOf( "<" + XMLDataPacketTags._Params ) == 0 ) {
	    			
	    			TempFormatedResponseFile.getChannel().position( lngCursorPosition );
	    			
	    			if ( strLine.substring( strLine.length() - 2, strLine.length() ).equals( "/>" ) ) {
	    				
	    				strLine = strLine.substring( 0, strLine.length() - 2 );
	    				
	    			}
	    			
	    			strLine = "    " +  strLine + " " + XMLDataPacketTags._RowCount + "=\"" + Long.toString( lngRowCount ) + "\" />";
	    			
	    			TempFormatedResponseFile.writeBytes( strLine );
	    			
	    			break;
	    			
	    		}

	    		lngCursorPosition = TempFormatedResponseFile.getChannel().position();
	    		
	    	}
	    	
	    	TempFormatedResponseFile.close();
	    	
		} 
		catch ( Exception Ex ) {

    		if ( Logger != null )
    			Logger.logException( "-1010", Ex.getMessage(), Ex );
    		else if ( OwnerConfig != null && OwnerConfig.Logger != null )
    			OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
    	
    	return bResult;
    	
    }
    
    /*public Document AddXMLToRowDataSection( Document XMLDocument, ResultSet SQLDataSet, CAbstractDBEngine DBEngine, ArrayList<String> arrIncludedFields, ArrayList<String> arrExcludedFields, CExtendedLogger Logger, CLanguage Lang ) {

        try {

    	   int intRowCount = 0; 

           NodeList XML_RowDataSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._RowData );

           if ( XML_RowDataSection.getLength() > 0 ) {

              //SimpleDateFormat DFormatter = new SimpleDateFormat("yyyyMMdd");
              //SimpleDateFormat TFormatter = new SimpleDateFormat("HHmmss");
              //SimpleDateFormat DTFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");

              java.sql.ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();

              boolean bOnFirstRow = SQLDataSet.getRow() == 1;
              
              while ( bOnFirstRow == true || SQLDataSet.next() == true ) {

            	  bOnFirstRow = false;
            	  
            	  intRowCount += 1;
            	  
            	  Element XML_ROW = XMLDocument.createElement( XMLDataPacketTags._Row );

            	  for ( int i = 1; i <= DataSetMetaData.getColumnCount(); i++ ) {

            		  String strFieldName = DataSetMetaData.getColumnName( i );

            		  if ( strFieldName == null || strFieldName.isEmpty() == true )
            			  strFieldName = DataSetMetaData.getColumnLabel( i );
            		  
            		  int intFieldType = DataSetMetaData.getColumnType( i );

            		  intFieldType = DBEngine.getJavaSQLColumnType( intFieldType, Logger, Lang );

            		  if ( SQLDataSet.getObject( i ) != null ) {

            			  String strFieldValue = DBEngine.getFieldValueAsString(intFieldType, strFieldName, SQLDataSet, "yyyyMMdd", "HHmmss", "yyyyMMdd HHmmss", Logger, Lang );

            			  XML_ROW.setAttribute( strFieldName, strFieldValue );
            			  
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

    }*/
    
    public Document buildXMLMetaData( Document XMLDocument, CMemoryRowSet MemoryRowSet, CExtendedLogger Logger, CLanguage Lang ) {
    	
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
						case Types.BOOLEAN: { 
							
			                                   XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeBoolean );
		                                       break; 
		                       
	                                        }
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
						case Types.NUMERIC:	
						case Types.DECIMAL: {  XML_Field.setAttribute( XMLDataPacketTags._FieldType, XMLDataPacketTags._FieldTypeFloat ); break; }
						case Types.DOUBLE: {  break; }

				   }

            	   XML_FieldsSection.item( 0 ).appendChild( XML_Field );

               }

            }
    	
    	}
    	catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

    	}

    	return XMLDocument;
    
    }
    
    public Document addXMLToRowDataSection( Document XMLDocument, CMemoryRowSet MemoryRowSet, CExtendedLogger Logger, CLanguage Lang ) {
    	
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
			    		String strFieldName = null;
    					int intFieldType = 0;
			    				
			    		if ( Field != null ) {
			    			
				    		strFieldName = Field.strName;
	    					intFieldType = Field.intSQLType;
			    			FieldData = Field.getData( intRowIndex );
			    		
			    		}

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
	    						case Types.BOOLEAN: { 
	    							
	    							                   XML_ROW.setAttribute( strFieldName, Boolean.toString( (Boolean) FieldData ) );
				                                       break; 
				                       
			                                        }
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
	    						case Types.NUMERIC:
	    						case Types.DECIMAL: {  XML_ROW.setAttribute( strFieldName, Float.toString( (Float) FieldData ) ); break; }
	    						case Types.DOUBLE: {  break; }

    						}
    	    			
    					}
    					else if ( strFieldName != null ) {

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
         	       intCurrentRowCount = net.maindataservices.Utilities.strToInteger( strCurrentRowCount );
            		
               ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount , Integer.toString( intCurrentRowCount + intRowCount ) );

            }
            
    	}
    	catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

    	}

    	return XMLDocument;
    	
    }

    /*
    @Override
	public CAbstractResponseFormat getNewInstance() {
    	
    	CXMLDataPacketResponseFormat NewInstance = new CXMLDataPacketResponseFormat();
    	
    	NewInstance.initResponseFormat( this.ServicesDaemonConfig, this.OwnerConfig );
    	
    	return NewInstance;
    	
    }
    */
    
    @Override
    public String getContentType() {
    	
    	if ( OwnerConfig != null )
    	   return (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._XML_DataPacket_ContentType, null );
    	else
    	   return ""; 
    	
    }
    
    @Override
    public String getCharacterEncoding() {
    	
    	if ( OwnerConfig != null )
    	   return (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._XML_DataPacket_CharSet, null );
    	else
    	   return ""; 
    	
    }

    public int describeService( CAbstractService Service, Element XMLNode_RowDataSection, CExtendedLogger Logger, CLanguage Lang ) {
	   
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

            	if ( strKey.toLowerCase().equals( ConstantsCommonClasses._Default ) == false ) {
            		
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
    public String enumerateServices( HashMap<String,CAbstractService> RegisteredServices, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

        Document XMLDocument = this.buildBasicResponseXMLStruct( strVersion, Logger, Lang );

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

              String strServName = Pairs.getKey();

              CAbstractService Service = RegisteredServices.get( strServName );
              
              if ( Service != null )
                 intRowCount += this.describeService( Service, (Element) XML_RowDataSection.item( 0 ), Logger, Lang );
        
           }

        }

        NodeList XML_ParamsSection = XMLDocument.getElementsByTagName( XMLDataPacketTags._Params );

        if ( XML_ParamsSection.getLength() > 0 ) {

           ((Element) XML_ParamsSection.item( 0 )).setAttribute( XMLDataPacketTags._RowCount, Integer.toString( intRowCount ) );

        }

    	return convertXMLDocumentToString( XMLDocument, true, this.getCharacterEncoding(), Logger, Lang );
    	
    }
    
    /*Override
	public boolean FormatResultSet( HttpServletResponse Response, ResultSet SQLDataSet, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

    	boolean bResult = false;
    	
        /*try {

	        ResultSetMetaData DataSetMetaData = SQLDataSet.getMetaData();
	
	        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );
	
	        ArrayList<String> arrIncludedFields = new ArrayList<String>();
	        ArrayList<String> arrExcludedFields = new ArrayList<String>();
	
	        String strTempDir = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Temp_Dir );
	        
	        String strTempResponseFormatedFilePath = strTempDir + UUID.randomUUID();
	        
	        FileOutputStream TempResponseFormatedFileStream = new FileOutputStream( strTempResponseFormatedFilePath );
	        DataOutputStream TempResponseFormatedFileWriter = new DataOutputStream( TempResponseFormatedFileStream );
	        
	        XMLDocument = BuildXMLMetaData( XMLDocument, DataSetMetaData, DBEngine, arrIncludedFields, arrExcludedFields, Logger, Lang );
	
	        XMLDocument = AddXMLToRowDataSection( XMLDocument, SQLDataSet, DBEngine, arrIncludedFields, arrExcludedFields, Logger, Lang );

            //strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );

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
    	
        try {

        	if ( SQLDataSetList.size() > 0 ) {

        		ResultSet ResultSetMetaData = SQLDataSetList.get( 0 );
	        	
		        ResultSetMetaData DataSetMetaData = ResultSetMetaData.getMetaData();
		
		        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );
		
		        ArrayList<String> arrIncludedFields = new ArrayList<String>();
		        ArrayList<String> arrExcludedFields = new ArrayList<String>();
		
		        XMLDocument = BuildXMLMetaData( XMLDocument, DataSetMetaData, DBEngine, arrIncludedFields, arrExcludedFields, Logger, Lang );
		
		        for ( ResultSet SQLDataSetToAdd: SQLDataSetList ) {    
		        
		           XMLDocument = AddXMLToRowDataSection( XMLDocument, SQLDataSetToAdd, DBEngine, arrIncludedFields, arrExcludedFields, Logger, Lang );
		        
		        }
		        
	            //strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );

        	}
	            
        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

        }//
    	
    	return bResult;
    	
    }*/

    @Override
    public boolean formatResultSet( HttpServletResponse Response, CResultDataSet ResultDataSet, CAbstractDBEngine DBEngine, int intInternalFetchSize, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, boolean bDeleteTempReponseFile, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;
    	
        try {

        	if ( Utilities.versionGreaterEquals( strVersion, this.strMinVersion ) && Utilities.versionLessEquals( strVersion, this.strMaxVersion ) ) {

        		String strTempDir = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Temp_Dir, null );

        		String strTempResponseFormatedFilePath = strTempDir + UUID.randomUUID() + ".formated_response";

        		ServletOutputStream OutStream = Response.getOutputStream(); //new FileOutputStream( strTempResponseFormatedFilePath ); 

        		PrintWriter TempResponseFormatedFileWriter = new PrintWriter( OutStream ); // strTempResponseFormatedFilePath, this.getCharacterEncoding() );

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

        			this.printXMLHeader( TempResponseFormatedFileWriter, this.getCharacterEncoding() );

        			this.printXMLDataPacketSection( TempResponseFormatedFileWriter, strVersion, true );

        			//long lngRowCount = -1;

        			if ( this.printXMLMetaDataSection( TempResponseFormatedFileWriter, ((ResultSet) ResultDataSet.Result).getMetaData(), DBEngine, Logger, Lang ) ) {

        				this.printXMLRowDataSection( TempResponseFormatedFileWriter, true );
        				//lngRowCount = 
        				this.printAddXMLToRowDataSection( strTempDir, strTempResponseFormatedFilePath, TempResponseFormatedFileWriter, OutStream, ((ResultSet) ResultDataSet.Result), DBEngine, Logger, Lang );    	        	
        				this.printXMLRowDataSection( TempResponseFormatedFileWriter, false );

        				ArrayList<String> strErrorCodeDescription = new ArrayList<String>();

        				this.printXMLErrorsSection( TempResponseFormatedFileWriter, strErrorCodeDescription, strVersion );

        			}

        			this.printXMLDataPacketSection( TempResponseFormatedFileWriter, strVersion, false );

        			TempResponseFormatedFileWriter.close();

        			TempResponseFormatedFileWriter = null;

        			/*if ( lngRowCount >= 0 ) {

        				this.PrintParamsSectionRowCount( strTempResponseFormatedFilePath, lngRowCount, Logger, Lang );

        			}*/

        			/*XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );

        		    ArrayList<String> arrIncludedFields = new ArrayList<String>();
        		    ArrayList<String> arrExcludedFields = new ArrayList<String>();

        		    XMLDocument = BuildXMLMetaData( XMLDocument, SQLDataSetResult.Result.getMetaData(), DBEngine, arrIncludedFields, arrExcludedFields, Logger, Lang );

        		    XMLDocument = AddXMLToRowDataSection( XMLDocument, SQLDataSetResult.Result, DBEngine, arrIncludedFields, arrExcludedFields, Logger, Lang );*/

        		}
        		else {

        			Document XMLDocument = null;

        			ArrayList<CSimpleXMLFieldDefinition> FieldDefinitons = new ArrayList<CSimpleXMLFieldDefinition>();

        			FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructAffectedRows, XMLDataPacketTags._FieldTypeBigInt, "", "" ) );
        			FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructCode, XMLDataPacketTags._FieldTypeInteger, "", "" ) );
        			FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructDescription, XMLDataPacketTags._FieldTypeString, "", XMLDataPacketTags._XML_StructDescriptionLength ) );

        			XMLDocument = this.buildXMLFieldDefinedStruct( FieldDefinitons, strVersion, Logger, Lang );

        			LinkedHashMap<String,String> FieldValues = new LinkedHashMap<String,String>();

        			FieldValues.put( XMLDataPacketTags._XML_StructAffectedRows, Long.toString( ResultDataSet.lngAffectedRows ) );
        			FieldValues.put( XMLDataPacketTags._XML_StructCode, Integer.toString( ResultDataSet.intCode ) );
        			FieldValues.put( XMLDataPacketTags._XML_StructDescription, ResultDataSet.strDescription );

        			if ( ResultDataSet.intCode >= 0 )
        				XMLDocument = addXMLSimpleMessage( XMLDocument, FieldValues, strVersion, false, Logger, Lang );
        			else
        				XMLDocument = addXMLSimpleMessage( XMLDocument, FieldValues, strVersion, true, Logger, Lang );

        			TempResponseFormatedFileWriter.print( this.convertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang ) );

        			TempResponseFormatedFileWriter.close();

        			TempResponseFormatedFileWriter = null;

        		}

        		/****
    			File TempResponseFormatedFile = new File( strTempResponseFormatedFilePath ); 

    			this.CopyToResponseStream( Response, TempResponseFormatedFile, 10240, Logger, Lang );

    			if ( bDeleteTempReponseFile )
    				TempResponseFormatedFile.delete();
        		****/
        		
        		//strResult = this.ConvertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );
        		
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

					ServletOutputStream OutStream = Response.getOutputStream(); //new FileOutputStream( strTempResponseFormatedFilePath ); 

					PrintWriter TempResponseFormatedFileWriter = new PrintWriter( OutStream ); // strTempResponseFormatedFilePath, this.getCharacterEncoding() );

					Document XMLDocument = null; 

					boolean bNodeErrorAdded = false;

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

						ResultSet DataSet = (ResultSet) ResultObject;
						
						this.printXMLHeader( TempResponseFormatedFileWriter, this.getCharacterEncoding() );

						this.printXMLDataPacketSection( TempResponseFormatedFileWriter, strVersion, true );

						long lngRowCount = -1;

						if ( this.printXMLMetaDataSection( TempResponseFormatedFileWriter, DataSet.getMetaData(), DBEngine, Logger, Lang ) ) {

							ArrayList<String> strErrorCodeDescription = new ArrayList<String>();

							this.printXMLRowDataSection( TempResponseFormatedFileWriter, true );

							for ( CResultDataSet ResultDataSetToAdd: ResultDataSetList ) { 

								if ( ResultDataSetToAdd.Result != null ) {   

									if ( lngRowCount < 0 )
										lngRowCount = 0;

									lngRowCount += this.printAddXMLToRowDataSection( strTempDir, strTempResponseFormatedFilePath, TempResponseFormatedFileWriter, OutStream, (ResultSet) ResultDataSetToAdd.Result, DBEngine, Logger, Lang );    	        	

								}
								else {

									strErrorCodeDescription.add( XMLDataPacketTags._XML_StructAffectedRows + "=\"" + Long.toString( ResultDataSetToAdd.lngAffectedRows ) + "\" " + XMLDataPacketTags._XML_StructCode + "=\"" + Integer.toString( ResultDataSetToAdd.intCode ) + "\" " + XMLDataPacketTags._XML_StructDescription + "=\"" + ResultDataSetToAdd.strDescription + "\"" );

								}

							};

							this.printXMLRowDataSection( TempResponseFormatedFileWriter, false );

							this.printXMLErrorsSection( TempResponseFormatedFileWriter, strErrorCodeDescription, strVersion );

						}

						this.printXMLDataPacketSection( TempResponseFormatedFileWriter, strVersion, false );

						TempResponseFormatedFileWriter.close();

						TempResponseFormatedFileWriter = null;

						//if ( lngRowCount >= 0 ) {

							//this.PrintParamsSectionRowCount( strTempResponseFormatedFilePath, lngRowCount, Logger, Lang );

						//}

					}
					else {

						ArrayList<CSimpleXMLFieldDefinition> FieldDefinitons = new ArrayList<CSimpleXMLFieldDefinition>();

						FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructAffectedRows, XMLDataPacketTags._FieldTypeBigInt, "", "" ) );
						FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructCode, XMLDataPacketTags._FieldTypeInteger, "", "" ) );
						FieldDefinitons.add( new CSimpleXMLFieldDefinition( XMLDataPacketTags._XML_StructDescription, XMLDataPacketTags._FieldTypeString, "", XMLDataPacketTags._XML_StructDescriptionLength ) );

						XMLDocument = this.buildXMLFieldDefinedStruct( FieldDefinitons, strVersion, Logger, Lang );

						LinkedHashMap<String,String> FieldValues = new LinkedHashMap<String,String>();

						for ( CResultDataSet ResultSetResultToAdd: ResultDataSetList ) {    

							FieldValues.put( XMLDataPacketTags._XML_StructAffectedRows, Long.toString( ResultSetResultToAdd.lngAffectedRows ) );
							FieldValues.put( XMLDataPacketTags._XML_StructCode, Integer.toString( ResultSetResultToAdd.intCode ) );
							FieldValues.put( XMLDataPacketTags._XML_StructDescription, ResultSetResultToAdd.strDescription );

							if ( ResultSetResultToAdd.intCode >= 0 ) {

								XMLDocument = addXMLSimpleMessage( XMLDocument, FieldValues, strVersion, false, Logger, Lang );

							}	
							else {

								XMLDocument = addXMLSimpleMessage( XMLDocument, FieldValues, strVersion, true, Logger, Lang );

								bNodeErrorAdded = true;

							}	

						}

						if ( bNodeErrorAdded == false ) { //Add the default node error

							XMLDocument = this.addXMLToErrorSection( XMLDocument, 0, "", strVersion, false );

						}

						TempResponseFormatedFileWriter.print( this.convertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang ) );

						TempResponseFormatedFileWriter.close();

						TempResponseFormatedFileWriter = null;

					}

					File TempResponseFormatedFile = new File( strTempResponseFormatedFilePath ); 

					this.copyToResponseStream( Response, TempResponseFormatedFile, 10240, Logger, Lang );

					if ( bDeleteTempReponseFile )
						TempResponseFormatedFile.delete();

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

	        Document XMLDocument = this.buildBasicResponseXMLStruct( strVersion, Logger, Lang );
	
	        XMLDocument = buildXMLMetaData( XMLDocument, MemoryRowSet, Logger, Lang );
	
	        XMLDocument = addXMLToRowDataSection( XMLDocument, MemoryRowSet, Logger, Lang );

            strResult = this.convertXMLDocumentToString( XMLDocument, this.getCharacterEncoding(), Logger, Lang );

        }
        catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );

        }

    	return strResult;
    	
    }
    
    /*Override
    public String FormatMemoryRowSets( ArrayList<CMemoryRowSet> MemoryRowSetList, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	String strResult = "";

        try {

        	if ( MemoryRowSetList.size() > 0 ) {
        	
		        Document XMLDocument = this.BuildBasicResponseXMLStruct( strVersion, Logger, Lang );
		
		        CMemoryRowSet FirstMemoryRowSet = MemoryRowSetList.get( 0 );

        		XMLDocument = BuildXMLMetaData( XMLDocument, FirstMemoryRowSet, Logger, Lang );
		
		        for ( CMemoryRowSet MemoryRowSetToAdd: MemoryRowSetList ) {   
        		
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
    	
    }*/
    
    @Override
    public String formatSimpleMessage( String strSecurityTokenID, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	return buildXMLSimpleMessage( strSecurityTokenID, strTransactionID, intCode, strDescription, bAttachToError, strVersion, Logger, Lang );
    	
    }

}
