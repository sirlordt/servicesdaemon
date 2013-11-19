package BPServicesManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//import net.maindataservices.Utilities;

import net.maindataservices.Utilities;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import ExtendedLogger.CExtendedLogger;

public class CBPServicesManagerConfig extends CAbstractConfigLoader {

	protected static CBPServicesManagerConfig BPServicesManagerConfig = null;
	
	public String strTempDir;
	public String strBPServicesDir;
	public String strResponsesFormatsDir;
	public String strDefaultResponseFormat;
	public String strDefaultResponseFormatVersion;
	public String strResponseRequestMethod; //OnlyGET, OnlyPOST, Any
	
	public int intInternalFetchSize; //25000 default
	
	//Built in responses formats configs
	public String strXML_DataPacket_ContentType;
	public String strXML_DataPacket_CharSet;
	
	public String strJavaXML_WebRowSet_ContentType;
	public String strJavaXML_WebRowSet_CharSet;
	
	public String strJSON_ContentType;
	public String strJSON_CharSet;
	
	public String strCSV_ContentType;
	public String strCSV_CharSet;
	public boolean bCSV_FieldQuoted;
	public String strCSV_SeparatorSymbol;
	public boolean bCSV_ShowHeaders;
	
	public List<CConfigDBConnection> ConfiguredDBConnections;
	
	static {
		
		BPServicesManagerConfig = new CBPServicesManagerConfig();
		
	} 

	public static CBPServicesManagerConfig getBPServicesManagerConfig() {
		
		return BPServicesManagerConfig;
		
	}
	
	public CBPServicesManagerConfig() {
		
		super();
		
		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsServicesDaemon._System );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsBPServicesManager._BuiltinResponsesFormats ); //2
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsBPServicesManager._DBConnections ); //3
		bFirstLevelConfigSectionsMustExists.add( true );
		
		strTempDir = DefaultConstantsBPServicesManager.strDefaultRunningPath + DefaultConstantsBPServicesManager.strDefaultTempDir; //"Temp/";
		strBPServicesDir = DefaultConstantsBPServicesManager.strDefaultRunningPath + DefaultConstantsBPServicesManager.strDefaultDBServicesDir; //"DBServices/"; 
		strResponsesFormatsDir = DefaultConstantsBPServicesManager.strDefaultRunningPath + DefaultConstantsBPServicesManager.strDefaultResponsesFormatsDir; //"ResponsesFormats/";
		
		strDefaultResponseFormat = DefaultConstantsBPServicesManager.strDefaultResponseFormat;
		strDefaultResponseFormatVersion = DefaultConstantsBPServicesManager.strDefaultResponseFormatVersion;

		strResponseRequestMethod = ConfigXMLTagsServicesDaemon._Request_Method_ANY;
		
		intInternalFetchSize = DefaultConstantsBPServicesManager.intDefaultInternalFetchSize;
		
		strXML_DataPacket_CharSet = DefaultConstantsBPServicesManager.strDefaultChasetXML;
		strXML_DataPacket_ContentType = DefaultConstantsBPServicesManager.strDefaultContentTypeXML;
		
		strJavaXML_WebRowSet_CharSet = DefaultConstantsBPServicesManager.strDefaultChasetXML;
		strJavaXML_WebRowSet_ContentType = DefaultConstantsBPServicesManager.strDefaultContentTypeXML;

		strJSON_ContentType = DefaultConstantsBPServicesManager.strDefaultContentTypeJSON;
		strJSON_CharSet = DefaultConstantsBPServicesManager.strDefaultChasetJSON;
		
		strCSV_ContentType = DefaultConstantsBPServicesManager.strDefaultContentTypeCSV;
		strCSV_CharSet = DefaultConstantsBPServicesManager.strDefaultChasetCSV;
		bCSV_FieldQuoted = DefaultConstantsBPServicesManager.bDefaultFieldsQuoteCSV;
		strCSV_SeparatorSymbol = DefaultConstantsBPServicesManager.strDefaultSeparatorSymbolCSV;
		bCSV_ShowHeaders = DefaultConstantsBPServicesManager.bDefaultShowHeadersCSV;

		ConfiguredDBConnections = new ArrayList<CConfigDBConnection>();
		
	}
	
	public CConfigDBConnection getConfiguredDBConnection( String strDBConnectionName ) {
		
		CConfigDBConnection Result = null;

		for ( CConfigDBConnection DBConnection : ConfiguredDBConnections ) {
			
			if ( DBConnection.strName.equals( strDBConnectionName ) ) {
				
				Result = DBConnection;
				
				break;
				
			}
			
		}
		
		return Result;
		
	}
	
    public boolean LoadConfigSectionSystem( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
		   
			if ( ConfigSectionNode.hasAttributes() == true ) {
		
				String strAttributesOrder[] = { ConfigXMLTagsBPServicesManager._Temp_Dir, ConfigXMLTagsBPServicesManager._BPServices_Dir, ConfigXMLTagsBPServicesManager._Responses_Formats_Dir, ConfigXMLTagsBPServicesManager._Default_Response_Format, ConfigXMLTagsBPServicesManager._Default_Response_Format_Version, ConfigXMLTagsBPServicesManager._Internal_Fetch_Size, ConfigXMLTagsServicesDaemon._Response_Request_Method };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Temp_Dir ) ) {

							this.strTempDir = NodeAttribute.getNodeValue();
		
					        if ( this.strTempDir != null && this.strTempDir.isEmpty() == false && new File( this.strTempDir ).isAbsolute() == false ) {

					        	this.strTempDir = DefaultConstantsBPServicesManager.strDefaultRunningPath + this.strTempDir;
						        	
						    }

					        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strTempDir", this.strTempDir ) );
				        
					        if ( Utilities.CheckDir( this.strTempDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._BPServices_Dir ) ) {

							this.strBPServicesDir = NodeAttribute.getNodeValue();
		
					        if ( this.strBPServicesDir != null && this.strBPServicesDir.isEmpty() == false && new File( this.strBPServicesDir ).isAbsolute() == false ) {

					        	this.strBPServicesDir = DefaultConstantsBPServicesManager.strDefaultRunningPath + this.strBPServicesDir;
						        	
						    }

					        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strDBServicesDir", this.strBPServicesDir ) );
				        
					        if ( Utilities.CheckDir( this.strBPServicesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Responses_Formats_Dir ) ) {

							this.strResponsesFormatsDir = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strResponsesFormatsDir != null && this.strResponsesFormatsDir.isEmpty() == false && new File( this.strResponsesFormatsDir ).isAbsolute() == false ) {
	                        
					        	this.strResponsesFormatsDir = DefaultConstantsBPServicesManager.strDefaultRunningPath + this.strResponsesFormatsDir;
						        	
						    }

					        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strResponsesFormatsDir", this.strResponsesFormatsDir ) );
						
					        if ( Utilities.CheckDir( this.strResponsesFormatsDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Default_Response_Format ) ) {
						
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	this.strDefaultResponseFormat = NodeAttribute.getNodeValue();
						    	
						        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strDefaultResponseFormat", this.strDefaultResponseFormat ) );

						    }
					        else {
					        	
						        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Default_Response_Format, this.strDefaultResponseFormat ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Default_Response_Format_Version ) ) {
							
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	this.strDefaultResponseFormatVersion = NodeAttribute.getNodeValue();
						    	
						        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strDefaultResponseFormatVersion", this.strDefaultResponseFormatVersion ) );

						    }
					        else {
					        	
						        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", "strDefaultResponseFormatVersion", this.strDefaultResponseFormatVersion ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Response_Request_Method ) ) {
							
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConfigXMLTagsServicesDaemon._Request_Method_ANY.toLowerCase() ) ) {

					        		this.strResponseRequestMethod = ConfigXMLTagsServicesDaemon._Request_Method_ANY;
							        
					        		Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );
					        
					        
					        	}
					        	else if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConfigXMLTagsServicesDaemon._Request_Method_OnlyGET.toLowerCase() ) ) { 

					        		this.strResponseRequestMethod = ConfigXMLTagsServicesDaemon._Request_Method_OnlyGET;
							        
					        		Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );

					        	}	
					        	else if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConfigXMLTagsServicesDaemon._Request_Method_OnlyPOST.toLowerCase() ) ) {    

					        	   this.strResponseRequestMethod = ConfigXMLTagsServicesDaemon._Request_Method_OnlyPOST;
						    	
						           Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );

					        	}
					        	else {
					        		
						           Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s,%s]", "strResponseRequestMethod", ConfigXMLTagsServicesDaemon._Request_Method_ANY, ConfigXMLTagsServicesDaemon._Request_Method_OnlyGET, ConfigXMLTagsServicesDaemon._Request_Method_OnlyPOST ) );
						           Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", "strResponseRequestMethod", NodeAttribute.getNodeValue(), this.strResponseRequestMethod ) );
					        		
					        	}
						    	
						    }
					        else {
					        	
						        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Internal_Fetch_Size ) ) {
							
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	if ( net.maindataservices.Utilities.CheckStringIsInteger( NodeAttribute.getNodeValue(), this.Logger ) ) {
					        	
					        		int intTempInternalFetchSize = Integer.parseInt( NodeAttribute.getNodeValue() );
					        		
					        		if ( intTempInternalFetchSize > 0 ) {
					        		
					        			this.intInternalFetchSize = intTempInternalFetchSize;

					        			Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "intInternalFetchSize", Integer.toString( this.intInternalFetchSize ) ) );

					        		}
					        		else {
					        			
								        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", "intInternalFetchSize", NodeAttribute.getNodeValue(), Integer.toString( this.intInternalFetchSize ) ) );
					        			
					        		}
					        		
					        	}
					        	else {

							        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", "intInternalFetchSize", NodeAttribute.getNodeValue(), Integer.toString( this.intInternalFetchSize ) ) );

					        	}
						    	
						    }
					        else {
					        	
						        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", "intInternalFetchSize", Integer.toString( this.intInternalFetchSize ) ) );
					        	
					        }
					        
						}
		            
		            }
		            else {

				        if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsBPServicesManager._BPServices_Dir ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsBPServicesManager._BPServices_Dir, this.strBPServicesDir ) );

					        if ( Utilities.CheckDir( this.strBPServicesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsBPServicesManager._Temp_Dir ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsBPServicesManager._Temp_Dir, this.strTempDir ) );

					        if ( Utilities.CheckDir( this.strTempDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsBPServicesManager._Responses_Formats_Dir ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsBPServicesManager._Responses_Formats_Dir, this.strResponsesFormatsDir ) );

					        if ( Utilities.CheckDir( this.strResponsesFormatsDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsBPServicesManager._Default_Response_Format ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsBPServicesManager._Default_Response_Format, this.strDefaultResponseFormat ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsBPServicesManager._Default_Response_Format_Version ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsBPServicesManager._Default_Response_Format_Version, this.strDefaultResponseFormatVersion ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsServicesDaemon._Response_Request_Method ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsServicesDaemon._Response_Request_Method, this.strResponseRequestMethod ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsBPServicesManager._Internal_Fetch_Size ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsBPServicesManager._Internal_Fetch_Size, Integer.toString( this.intInternalFetchSize ) ) );
		            		
		            	}
		            	
		            }
		            
		        }
			
			}
		
		}
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;

	}
	
	public boolean LoadConfigSectionBuiltinResponsesFormats( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {
		
        boolean bResult = true;
        
        try {
        	
			String strAttributesOrder[] = { ConfigXMLTagsBPServicesManager._Name, ConfigXMLTagsBPServicesManager._Char_Set, ConfigXMLTagsBPServicesManager._Content_Type, ConfigXMLTagsBPServicesManager._Fields_Quote, ConfigXMLTagsBPServicesManager._Separator_Symbol, ConfigXMLTagsBPServicesManager._Show_Headers };

			NodeList ConfigBuiltinResponsesList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigBuiltinResponsesList.getLength() > 0 ) {
			
	            for ( int intConfigBuiltinResponseFormatIndex = 0; intConfigBuiltinResponseFormatIndex < ConfigBuiltinResponsesList.getLength(); intConfigBuiltinResponseFormatIndex++ ) {
	                
	            	Node ConfigBuiltinResponseFormatNode = ConfigBuiltinResponsesList.item( intConfigBuiltinResponseFormatIndex );
	                 
	    			Logger.LogMessage( "1", Lang.Translate( "Reading XML built in response format: [%s]", ConfigBuiltinResponseFormatNode.getNodeName() ) );        
	                 
				    String strBuitinResponseName = "";
	    			
	    			if ( ConfigBuiltinResponseFormatNode.getNodeName().equals( ConfigXMLTagsBPServicesManager._BuiltinResponseFormat ) == true ) {

						if ( ConfigBuiltinResponseFormatNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigBuiltinResponseFormatNode.getAttributes();

							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

								if ( NodeAttribute != null  ) {

									Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
									Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

									if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Name ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strBuitinResponseName = NodeAttribute.getNodeValue().trim().toUpperCase();
											
										}
										else {
											
									    	Logger.LogError( "-1002", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsBPServicesManager._Name, ConfigXMLTagsBPServicesManager._BuiltinResponseFormat, Integer.toString( intConfigBuiltinResponseFormatIndex ) ) );
											
										}
										
									}
									else if ( strBuitinResponseName.equals( ConfigXMLTagsBPServicesManager._ResponseFormat_XML_DATAPACKET ) ) {

										if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strXML_DataPacket_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strXML_DataPacket_CharSet", this.strXML_DataPacket_CharSet ) );

											}
											else {

												Logger.LogError( "-1003", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Char_Set, DefaultConstantsBPServicesManager.strDefaultChasetXML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strXML_DataPacket_ContentType = NodeAttribute.getNodeValue();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strXML_DataPacket_ContentType", this.strXML_DataPacket_ContentType ) );

											}
											else {

												Logger.LogError( "-1004", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Content_Type, DefaultConstantsBPServicesManager.strDefaultContentTypeXML ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}
									else if ( strBuitinResponseName.equals( ConfigXMLTagsBPServicesManager._ResponseFormat_JAVA_XML_WEBROWSET ) ) {

										if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJavaXML_WebRowSet_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strJavaXML_WebRowSet_CharSet", this.strJavaXML_WebRowSet_CharSet ) );

											}
											else {

												Logger.LogError( "-1005", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Char_Set, DefaultConstantsBPServicesManager.strDefaultChasetXML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJavaXML_WebRowSet_ContentType = NodeAttribute.getNodeValue();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strJavaXML_WebRowSet_ContentType", this.strJavaXML_WebRowSet_ContentType ) );

											}
											else {

												Logger.LogError( "-1006", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Content_Type, DefaultConstantsBPServicesManager.strDefaultContentTypeXML ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}
									else if ( strBuitinResponseName.equals( ConfigXMLTagsBPServicesManager._ResponseFormat_JSON ) ) {

										if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJSON_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strJSON_CharSet", this.strJSON_CharSet ) );

											}
											else {

												Logger.LogError( "-1007", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Char_Set, DefaultConstantsBPServicesManager.strDefaultChasetXML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJSON_ContentType = NodeAttribute.getNodeValue();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strJSON_ContentType", this.strJSON_ContentType ) );

											}
											else {

												Logger.LogError( "-1008", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Content_Type, DefaultConstantsBPServicesManager.strDefaultContentTypeXML ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}
									else if ( strBuitinResponseName.equals( ConfigXMLTagsBPServicesManager._ResponseFormat_CSV ) ) {

										if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strCSV_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strCSV_CharSet", this.strCSV_CharSet ) );

											}
											else {

												Logger.LogError( "-1009", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Char_Set, DefaultConstantsBPServicesManager.strDefaultChasetXML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strCSV_ContentType = NodeAttribute.getNodeValue();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strCSV_ContentType", this.strCSV_ContentType ) );

											}
											else {

												Logger.LogError( "-1010", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Content_Type, DefaultConstantsBPServicesManager.strDefaultContentTypeXML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Fields_Quote ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.bCSV_FieldQuoted = NodeAttribute.getNodeValue().equals( "true" );
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "bCSV_FieldQuoted", Boolean.toString( this.bCSV_FieldQuoted ) ) );

											}
											else {

												Logger.LogError( "-1010", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Fields_Quote, Boolean.toString( DefaultConstantsBPServicesManager.bDefaultFieldsQuoteCSV ) ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Separator_Symbol ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strCSV_SeparatorSymbol = NodeAttribute.getNodeValue();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strCSV_SeparatorSymbol", this.strCSV_SeparatorSymbol ) );

											}
											else {

												Logger.LogError( "-1010", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Separator_Symbol, DefaultConstantsBPServicesManager.strDefaultSeparatorSymbolCSV ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Show_Headers ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.bCSV_ShowHeaders = NodeAttribute.getNodeValue().equals( "true" );
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "bCSV_ShowHeaders", Boolean.toString( this.bCSV_ShowHeaders ) ) );

											}
											else {

												Logger.LogError( "-1010", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsBPServicesManager._Show_Headers, Boolean.toString( DefaultConstantsBPServicesManager.bDefaultShowHeadersCSV ) ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}

								}
								else if ( intAttributesIndex == 0 ) { //Only the name attribute is obligatory 
									
							    	Logger.LogError( "-1001", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsBPServicesManager._Name, ConfigXMLTagsBPServicesManager._BuiltinResponseFormat, Integer.toString( intConfigBuiltinResponseFormatIndex ) ) );
							    	break;
							    	
								}

							}

						}

					}
	            
	            }

	        } 
        	
		}
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;

	}
	
    public boolean LoadConfigSectionDBConnections( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {
		
        boolean bResult = false;
        
        try {

			String strAttributesOrder[] = { ConfigXMLTagsBPServicesManager._Name, ConfigXMLTagsBPServicesManager._URL, ConfigXMLTagsBPServicesManager._User, ConfigXMLTagsBPServicesManager._Password, ConfigXMLTagsBPServicesManager._MapsFile, ConfigXMLTagsBPServicesManager._DBActionsFile };

			NodeList ConfigConnectionsList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigConnectionsList.getLength() > 0 ) {
			
	        	int intConnectionIndex = 0;
	        	
	            for ( int intLocalConfigConnectionIndex = 0; intLocalConfigConnectionIndex < ConfigConnectionsList.getLength(); intLocalConfigConnectionIndex++ ) {
	                
	            	Node ConfigConnectionNode = ConfigConnectionsList.item( intLocalConfigConnectionIndex );
	                 
	    			Logger.LogMessage( "1", Lang.Translate( "Reading XML database connection: [%s]", ConfigConnectionNode.getNodeName() ) );        
	                 
					if ( ConfigConnectionNode.getNodeName().equals( ConfigXMLTagsBPServicesManager._DBConnection ) == true ) {

						String strName = "";
						String strDatabase = "";
						String strURL = "";
						String strUser = "";
						String strPassword = "";
						String strMapsFilePath = "";
						String strDBActionsFilePath = "";
						
						if ( ConfigConnectionNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigConnectionNode.getAttributes();

					        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
					            
					        	Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
					        	
					            if ( NodeAttribute != null ) {
					            	
					            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
					            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
									
									if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Name ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
										
											strName = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1002", Lang.Translate( "The [%s] attribute for connection number [%s] config cannot empty string", ConfigXMLTagsBPServicesManager._Name, Integer.toString( intConnectionIndex ) ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._URL ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strURL = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1003", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsBPServicesManager._URL, strURL ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Database ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDatabase = NodeAttribute.getNodeValue().toLowerCase();
										
										}
										else {
											
											Logger.LogError( "-1004", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsBPServicesManager._Database, strDatabase ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._User ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strUser = NodeAttribute.getNodeValue().toLowerCase();
										
										}
										else {
											
											Logger.LogError( "-1005", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsBPServicesManager._User, strUser ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._Password ) ) {
									
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
										
										    strPassword = NodeAttribute.getNodeValue();
									
										}
							            else {
							            	
											Logger.LogError( "-1006", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsBPServicesManager._Password, strPassword ) );
											break; //Stop parse more attributes
											
							            }

									}	
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._MapsFile ) ) {

										if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strMapsFilePath = NodeAttribute.getNodeValue().trim();

											if ( /*strMapsFile != null && strMapsFile.isEmpty() == false &&*/ new File( strMapsFilePath ).isAbsolute() == false ) {

									        	strMapsFilePath = DefaultConstantsBPServicesManager.strDefaultRunningPath + strMapsFilePath;
										        	
										    }

									        if ( Utilities.CheckFile( strMapsFilePath, Logger, Lang ) == false ) {
										    	
									        	bResult = false;
									        	
									        	break;
										    	
										    }
										
										}
										else {
											
											Logger.LogError( "-1007", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsBPServicesManager._MapsFile, strMapsFilePath ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsBPServicesManager._DBActionsFile ) ) {

										if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDBActionsFilePath = NodeAttribute.getNodeValue().trim();
										
									        if ( /*strDBActionsFile != null && strDBActionsFile.isEmpty() == false &&*/ new File( strDBActionsFilePath ).isAbsolute() == false ) {

									        	strDBActionsFilePath = DefaultConstantsBPServicesManager.strDefaultRunningPath + strDBActionsFilePath;
										        	
										    }

									        if ( Utilities.CheckFile( strDBActionsFilePath, Logger, Lang ) == false ) {
										    	
									        	bResult = false;
									        	
									        	break;
										    	
										    }
											
										}
										else {
											
											Logger.LogError( "-1008", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsBPServicesManager._DBActionsFile, strDBActionsFilePath ) );
											break; //Stop parse more attributes
											
										}
										
									}
					            
					            }
					            else {
					            	
							    	Logger.LogError( "-1001", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConfigConnectionNode.getNodeName(), Integer.toString( intLocalConfigConnectionIndex ) ) );
							    	break;
					            	
					            }
					            
					        }
					        
					        if ( strName.isEmpty() == false && strURL.isEmpty() == false && strDatabase.isEmpty() == false && strUser.isEmpty() == false && strPassword.isEmpty() == false && strMapsFilePath.isEmpty() == false && strDBActionsFilePath.isEmpty() == false ) {
					        
			                    intConnectionIndex += 1;
					        	boolean bNameUsed = false;
					        	int intPreviusDBConnectionIndex = -1;
					        	
					            //Check for duplicate names DB connections
					        	for ( int intIndex = 0; intIndex <  this.ConfiguredDBConnections.size(); intIndex++ ) {
					        		
					        		CConfigDBConnection DBConnection = this.ConfiguredDBConnections.get( intIndex );
					        		
					        		if ( DBConnection.strName.toLowerCase().equals( strName.toLowerCase() ) ) {

					        			intPreviusDBConnectionIndex = intIndex;
					        			bNameUsed = true;
					        			break;
					        			
					        		}
					        		
					        	}
					        	
                                if ( bNameUsed == false ) {

                                	CConfigDBConnection DBConnection = new CConfigDBConnection();
                                	DBConnection.strName = strName;
                                	DBConnection.strURL = strURL;
                                	DBConnection.strDatabase = strDatabase;
                                	DBConnection.strUser = strUser;
                                	DBConnection.strPassword = strPassword;
                                	DBConnection.strMapsFilePath = strMapsFilePath;
                                	DBConnection.strDBActionsFilePath = strDBActionsFilePath;

                                	DBConnection.KeywordsMaps = new Properties();
                                	
                                	File MapsFilePath = new File( strMapsFilePath );
                                	
                                	Logger.LogMessage( "1", Lang.Translate( "Loading database maps from file [%s]", strMapsFilePath ) );

                                	DBConnection.KeywordsMaps.loadFromXML( MapsFilePath.toURI().toURL().openStream() );
                                	
                                	Logger.LogMessage( "1", Lang.Translate( "Loading DBActions from file [%s]", strDBActionsFilePath ) );

                                	DBConnection.DBActionsManager = new CDBActionsManager( strDBActionsFilePath, Logger, Lang );
                                	
                                	this.ConfiguredDBConnections.add( DBConnection );

                                	Logger.LogMessage( "1", Lang.Translate( "Connection database defined and added. Name: [%s], URL: [%s], Database: [%s], User: [%s], Password: [%s], IP: [%s], Maps_File: [%s], DBActions_File: [%s]", strName, strURL, strDatabase, strUser, strPassword, strMapsFilePath, strDBActionsFilePath ) );
	                                	
                                }
                                else {
                                	
					            	Logger.LogError( "-1016", Lang.Translate( "The DB connection name: [%s] is used in previus DB connection config index: [%s], the name attribute must be unique", strName, Integer.toString( intPreviusDBConnectionIndex ) ) );
                                	
                                }
				            	
					        }
					        else {
					        	
					            Logger.LogError( "-1017", Lang.Translate( "DB connection config attributes is not valid" ) );
					        	
					        }
						
						}
					
					}
	            
	            }
	            
	            if ( this.ConfiguredDBConnections.isEmpty() == true ) {
	            	
	    			Logger.LogError( "-1018", Lang.Translate( "No valid database connections defined" ) );        
	            	
	            }
	            else {
	            
	            	bResult = true;
	            
	            }
	        
	        }
        
        }
		catch ( Exception Ex ) {
			
			Logger.LogException( "-1025", Ex.getMessage(), Ex );
			
		}

		Logger.LogMessage( "1", Lang.Translate( "Count of database connections defined: [%s]", Integer.toString( this.ConfiguredDBConnections.size() ) ) );        

		return bResult;

	}
	
	public boolean LoadConfigSection( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;

		Logger.LogMessage( "1", Lang.Translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals(  CommonClasses.ConfigXMLTagsServicesDaemon._System ) == true ) {
           
			if ( this.LoadConfigSectionSystem( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.LogError( "-1001", Lang.Translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConfigXMLTagsBPServicesManager._BuiltinResponsesFormats ) == true ) {

        	if ( this.LoadConfigSectionBuiltinResponsesFormats( ConfigSectionNode, Lang, Logger ) == false ) {
        		
    			Logger.LogError( "-1002", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
        		
        	}
             
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConfigXMLTagsBPServicesManager._DBConnections ) == true ) {

        	if ( this.LoadConfigSectionDBConnections( ConfigSectionNode, Lang, Logger ) == false ) {
        		
    			Logger.LogError( "-1003", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
        		
        	}
             
        }
		
		return bResult;
		
	}
	
    @Override
	public String getConfigValue( String strConfigKey ) {
		
		if ( strConfigKey.equals( "XML_DataPacket_CharSet" ) )
			return this.strXML_DataPacket_CharSet;
		else if ( strConfigKey.equals( "XML_DataPacket_ContentType" ) )
			return this.strXML_DataPacket_ContentType;
		else if ( strConfigKey.equals( "JavaXML_WebRowSet_CharSet" ) )
			return this.strJavaXML_WebRowSet_CharSet;  
		else if ( strConfigKey.equals( "JavaXML_WebRowSet_ContentType" ) )
			return this.strJavaXML_WebRowSet_ContentType;  
		else if ( strConfigKey.equals( "JSON_CharSet" ) )
			return this.strJSON_CharSet;  
		else if ( strConfigKey.equals( "JSON_ContentType" ) )
			return this.strJSON_ContentType;  
		else if ( strConfigKey.equals( "CSV_CharSet" ) )
			return this.strCSV_CharSet;  
		else if ( strConfigKey.equals( "CSV_ContentType" ) )
			return this.strCSV_ContentType;  
		else if ( strConfigKey.equals( "CSV_FieldsQuote" ) )
			return Boolean.toString( this.bCSV_FieldQuoted );  
		else if ( strConfigKey.equals( "CSV_SeparatorSymbol" ) )
			return this.strCSV_SeparatorSymbol;  
		else if ( strConfigKey.equals( "CSV_ShowHeaders" ) )
			return Boolean.toString( this.bCSV_ShowHeaders );  
		else if ( strConfigKey.equals( "Temp_Dir" ) )
			return this.strTempDir;  
		else if ( strConfigKey.equals( "Internal_Fetch_Size" ) )
			return Integer.toString( this.intInternalFetchSize );  
		else	
			return "";
		
	}
	
}
