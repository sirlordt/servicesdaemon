package DBServicesManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Utilities.Utilities;


import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CClassPathLoader;
import CommonClasses.CLanguage;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import CommonClasses.DefaultConstantsServicesDaemon;
import ExtendedLogger.CExtendedLogger;

public class CDBServicesManagerConfig extends CAbstractConfigLoader {

	protected static CDBServicesManagerConfig DBServicesManagerConfig = null;

	public String strDBServicesDir;
	public String strDBDriversDir;
	public String strDBEnginesDir;
	public String strResponsesFormatsDir;
	public String strDefaultResponseFormat;
	public String strDefaultResponseFormatVersion;
	public String strResponseRequestMethod; //OnlyGET, OnlyPOST, Any
	
	//Built in responses formats configs
	public String strXML_DataPacket_ContentType;
	public String strXML_DataPacket_CharSet;
	
	public List<CConfigDBConnection> ConfiguredDBConnections;

	static {
		
		DBServicesManagerConfig = new CDBServicesManagerConfig();
		
	} 

	public static CDBServicesManagerConfig getDBServicesManagerConfig() {
		
		return DBServicesManagerConfig;
		
	}
	
	public CDBServicesManagerConfig() {

		super();
		
		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsServicesDaemon._System );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsDBServicesManager._BuiltinResponsesFormats ); //2
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsDBServicesManager._DBConnections ); //3
		bFirstLevelConfigSectionsMustExists.add( true );
		
		strDBServicesDir = DefaultConstantsDBServicesManager.strDefaultRunningPath + DefaultConstantsDBServicesManager.strDefaultDBServicesDir; //"DBServices/"; 
		strDBDriversDir = DefaultConstantsDBServicesManager.strDefaultRunningPath + DefaultConstantsDBServicesManager.strDefaultDBDriversDir; //"DBDrivers/";
		strDBEnginesDir = DefaultConstantsDBServicesManager.strDefaultRunningPath + DefaultConstantsDBServicesManager.strDefaultDBEnginesDir; //"DBEnginess/";
		strResponsesFormatsDir = DefaultConstantsDBServicesManager.strDefaultRunningPath + DefaultConstantsDBServicesManager.strDefaultResponsesFormatsDir; //"ResponsesFormats/";
		
		strDefaultResponseFormat = DefaultConstantsDBServicesManager.strDefaultResponseFormat;
		strDefaultResponseFormatVersion = DefaultConstantsDBServicesManager.strDefaultResponseFormatVersion;

		strResponseRequestMethod = ConfigXMLTagsServicesDaemon._Request_Method_ANY;
		
		strXML_DataPacket_CharSet = DefaultConstantsDBServicesManager.strDefaultChaset;
		strXML_DataPacket_ContentType = DefaultConstantsDBServicesManager.strDefaultContentType;
		
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
		
				String strAttributesOrder[] = { ConfigXMLTagsDBServicesManager._DBServices_Dir, ConfigXMLTagsDBServicesManager._DBDrivers_Dir, ConfigXMLTagsDBServicesManager._DBEngines_Dir, ConfigXMLTagsDBServicesManager._Responses_Formats_Dir, ConfigXMLTagsDBServicesManager._Default_Response_Format, ConfigXMLTagsDBServicesManager._Default_Response_Format_Version, ConfigXMLTagsServicesDaemon._Response_Request_Method };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._DBServices_Dir ) ) {

							this.strDBServicesDir = NodeAttribute.getNodeValue();
		
					        if ( this.strDBServicesDir.isEmpty() == false && new File( this.strDBServicesDir ).isAbsolute() == false ) {

					        	this.strDBServicesDir = DefaultConstantsDBServicesManager.strDefaultRunningPath + this.strDBServicesDir;
						        	
						    }

					        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strDBServicesDir", this.strDBServicesDir ) );
				        
					        if ( Utilities.CheckDir( this.strDBServicesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._DBDrivers_Dir ) ) {

							this.strDBDriversDir = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strDBDriversDir.isEmpty() == false && new File( this.strDBDriversDir ).isAbsolute() == false ) {

					        	this.strDBDriversDir = DefaultConstantsDBServicesManager.strDefaultRunningPath + this.strDBDriversDir;
						        	
						    }

					        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strDBDriversDir", this.strDBDriversDir ) );
						
					        if ( Utilities.CheckDir( this.strDBDriversDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
					        else {
					        	
					        	//Load class 
					        	
					        	
					        }
						    
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._DBEngines_Dir ) ) {

							this.strDBEnginesDir = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strDBEnginesDir.isEmpty() == false && new File( this.strDBEnginesDir ).isAbsolute() == false ) {

					        	this.strDBEnginesDir = DefaultConstantsDBServicesManager.strDefaultRunningPath + this.strDBEnginesDir;
						        	
						    }

					        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strDBEnginesDir", this.strDBEnginesDir ) );
						
					        if ( Utilities.CheckDir( this.strDBEnginesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						    
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Responses_Formats_Dir ) ) {

							this.strResponsesFormatsDir = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strResponsesFormatsDir.isEmpty() == false && new File( this.strResponsesFormatsDir ).isAbsolute() == false ) {
	                        
					        	this.strResponsesFormatsDir = DefaultConstantsDBServicesManager.strDefaultRunningPath + this.strResponsesFormatsDir;
						        	
						    }

					        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strResponsesFormatsDir", this.strResponsesFormatsDir ) );
						
					        if ( Utilities.CheckDir( this.strResponsesFormatsDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Default_Response_Format ) ) {
						
					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	this.strDefaultResponseFormat = NodeAttribute.getNodeValue();
						    	
						        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strDefaultResponseFormat", this.strDefaultResponseFormat ) );

						        break;
						    	
						    }
					        else {
					        	
						        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", "strDefaultResponseFormat", this.strDefaultResponseFormat ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Default_Response_Format_Version ) ) {
							
					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	this.strDefaultResponseFormatVersion = NodeAttribute.getNodeValue();
						    	
						        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strDefaultResponseFormatVersion", this.strDefaultResponseFormatVersion ) );

						        break;
						    	
						    }
					        else {
					        	
						        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", "strDefaultResponseFormatVersion", this.strDefaultResponseFormatVersion ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Response_Request_Method ) ) {
							
					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

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
		            
		            }
		            else {

				        if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsDBServicesManager._DBServices_Dir ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsDBServicesManager._DBServices_Dir, this.strDBServicesDir ) );

					        if ( Utilities.CheckDir( this.strDBServicesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsDBServicesManager._DBDrivers_Dir) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsDBServicesManager._DBDrivers_Dir, this.strDBDriversDir ) );

					        if ( Utilities.CheckDir( this.strDBDriversDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsDBServicesManager._DBEngines_Dir) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsDBServicesManager._DBEngines_Dir, this.strDBEnginesDir ) );

					        if ( Utilities.CheckDir( this.strDBEnginesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsDBServicesManager._Responses_Formats_Dir ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsDBServicesManager._Responses_Formats_Dir, this.strResponsesFormatsDir ) );

					        if ( Utilities.CheckDir( this.strResponsesFormatsDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsDBServicesManager._Default_Response_Format ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsDBServicesManager._Default_Response_Format, this.strDefaultResponseFormat ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsDBServicesManager._Default_Response_Format_Version ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsDBServicesManager._Default_Response_Format_Version, this.strDefaultResponseFormatVersion ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsServicesDaemon._Response_Request_Method ) ) {
		            		
					        Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute not found, using the default value [%s]", ConfigXMLTagsServicesDaemon._Response_Request_Method, this.strResponseRequestMethod ) );
		            		
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
        	
			String strAttributesOrder[] = { ConfigXMLTagsDBServicesManager._Name, ConfigXMLTagsDBServicesManager._Char_Set, ConfigXMLTagsDBServicesManager._Content_Type };

			NodeList ConfigBuiltinResponsesList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigBuiltinResponsesList.getLength() > 0 ) {
			
	            for ( int intConfigBuiltinResponseFormatIndex = 0; intConfigBuiltinResponseFormatIndex < ConfigBuiltinResponsesList.getLength(); intConfigBuiltinResponseFormatIndex++ ) {
	                
	            	Node ConfigBuiltinResponseFormatNode = ConfigBuiltinResponsesList.item( intConfigBuiltinResponseFormatIndex );
	                 
	    			Logger.LogMessage( "1", Lang.Translate( "Reading XML built in response format: [%s]", ConfigBuiltinResponseFormatNode.getNodeName() ) );        
	                 
				    String strBuitinResponseName = "";
	    			
	    			if ( ConfigBuiltinResponseFormatNode.getNodeName().equals( ConfigXMLTagsDBServicesManager._BuiltinResponseFormat ) == true ) {

						if ( ConfigBuiltinResponseFormatNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigBuiltinResponseFormatNode.getAttributes();

							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

								if ( NodeAttribute != null  ) {

									Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
									Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

									if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Name ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strBuitinResponseName = NodeAttribute.getNodeValue().trim().toUpperCase();
											
										}
										else {
											
									    	Logger.LogError( "-1002", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsDBServicesManager._Name, ConfigXMLTagsDBServicesManager._BuiltinResponseFormat, Integer.toString( intConfigBuiltinResponseFormatIndex ) ) );
											
										}
										
									}
									else if ( strBuitinResponseName.equals( ConfigXMLTagsDBServicesManager._ResponseFormat_XML_DATAPACKET ) ) {

										if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strXML_DataPacket_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strXML_DataPacket_CharSet", this.strXML_DataPacket_CharSet ) );

											}
											else {

												Logger.LogError( "-1003", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsDBServicesManager._Char_Set, DefaultConstantsDBServicesManager.strDefaultChaset ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strXML_DataPacket_ContentType = NodeAttribute.getNodeValue();
												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strXML_DataPacket_ContentType", this.strXML_DataPacket_ContentType ) );

											}
											else {

												Logger.LogError( "-1004", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsDBServicesManager._Content_Type, DefaultConstantsDBServicesManager.strDefaultContentType ) );
												break; //stop the parse another attributes

											}

										}

									}
									else if ( strBuitinResponseName.equals( ConfigXMLTagsDBServicesManager._ResponseFormat_JAVA_XML_WEBROWSET ) ) {


									}
									else if ( strBuitinResponseName.equals( ConfigXMLTagsDBServicesManager._ResponseFormat_JSON ) ) {


									}
									else if ( strBuitinResponseName.equals( ConfigXMLTagsDBServicesManager._ResponseFormat_CSV ) ) {


									}

								}
								else if ( intAttributesIndex == 0 ) {
									
							    	Logger.LogError( "-1001", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsDBServicesManager._Name, ConfigXMLTagsDBServicesManager._BuiltinResponseFormat, Integer.toString( intConfigBuiltinResponseFormatIndex ) ) );
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

			String strAttributesOrder[] = { ConfigXMLTagsDBServicesManager._Name, ConfigXMLTagsDBServicesManager._Driver, ConfigXMLTagsDBServicesManager._Engine, ConfigXMLTagsDBServicesManager._Engine_Version, ConfigXMLTagsServicesDaemon._IP, ConfigXMLTagsServicesDaemon._Port, ConfigXMLTagsDBServicesManager._Database, ConfigXMLTagsDBServicesManager._Auth_Type, ConfigXMLTagsDBServicesManager._User, ConfigXMLTagsDBServicesManager._Password, ConfigXMLTagsDBServicesManager._Date_Format, ConfigXMLTagsDBServicesManager._Time_Format, ConfigXMLTagsDBServicesManager._Date_Time_Format };

			NodeList ConfigConnectionsList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigConnectionsList.getLength() > 0 ) {
			
	        	int intConnectionIndex = 0;
	        	
	            for ( int intLocalConfigConnectionIndex = 0; intLocalConfigConnectionIndex < ConfigConnectionsList.getLength(); intLocalConfigConnectionIndex++ ) {
	                
	            	Node ConfigConnectionNode = ConfigConnectionsList.item( intLocalConfigConnectionIndex );
	                 
	    			Logger.LogMessage( "1", Lang.Translate( "Reading XML database connection: [%s]", ConfigConnectionNode.getNodeName() ) );        
	                 
					if ( ConfigConnectionNode.getNodeName().equals( ConfigXMLTagsDBServicesManager._DBConnection ) == true ) {

						String strName = "";
						String strDriver = "";
						String strEngine = "";
						String strEngineVersion = "";
						String strIP = "";
						String strAddressType = "";
						int intPort = -1;
						String strDatabase = "";
						String strAuthType = "";
						String strUser = "";
						String strPassword = "";
						String strDateFormat = "";
						String strTimeFormat = "";
						String strDateTimeFormat = "";
						
						if ( ConfigConnectionNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigConnectionNode.getAttributes();

					        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
					            
					        	Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
					        	
					            if ( NodeAttribute != null ) {
					            	
					            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
					            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
									
									if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Name ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
										
											strName = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1002", Lang.Translate( "The [%s] attribute for connection number [%s] config cannot empty string", DBServicesManager.ConfigXMLTagsDBServicesManager._Name, Integer.toString( intConnectionIndex ) ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Driver ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDriver = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1003", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._Driver, strDriver ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Engine ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strEngine = NodeAttribute.getNodeValue().toLowerCase();
										
										}
										else {
											
											Logger.LogError( "-1004", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._Engine, strEngine ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Engine_Version ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strEngineVersion = NodeAttribute.getNodeValue().toLowerCase();
										
										}
										else {
											
											Logger.LogError( "-1005", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._Engine_Version, strEngineVersion ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._IP ) ) {
									
										if ( Utilities.isValidIPV4( NodeAttribute.getNodeValue() ) == true ) {
										
										    strIP = NodeAttribute.getNodeValue();
										    strAddressType = "ipv4";
									
										}
										else if ( Utilities.isValidIPV6( NodeAttribute.getNodeValue() ) == true ) {
										
											strIP = NodeAttribute.getNodeValue();
											strAddressType = "ipv6";
	
										}
							            else {
							            	
											Logger.LogError( "-1006", Lang.Translate( "The [%s] attribute value [%s] is not valid ip address", CommonClasses.ConfigXMLTagsServicesDaemon._IP, NodeAttribute.getNodeValue() ) );
											break; //Stop parse more attributes
											
							            }

									}	
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Port ) ) {

										int intTmpPort = Utilities.StrToInteger( NodeAttribute.getNodeValue().trim(), Logger );
											
										if ( intTmpPort >= CommonClasses.DefaultConstantsServicesDaemon.intDefaultMinPortNumber && intTmpPort <= CommonClasses.DefaultConstantsServicesDaemon.intDefaultMaxPortNumber ) {
												
											intPort = intTmpPort;
												
										}
										else {
												
											Logger.LogError( "-1007", Lang.Translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", CommonClasses.ConfigXMLTagsServicesDaemon._Port, NodeAttribute.getNodeValue(), Integer.toString( CommonClasses.DefaultConstantsServicesDaemon.intDefaultMinPortNumber ), Integer.toString( CommonClasses.DefaultConstantsServicesDaemon.intDefaultMaxPortNumber ) ) );
											break; //Stop parse more attributes
												
										}
									    
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Database ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDatabase = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1008", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._Database, strDatabase ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Auth_Type ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConfigXMLTagsDBServicesManager._Auth_Type_Database ) || NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConfigXMLTagsDBServicesManager._Auth_Type_Engine ) ) {    
											 
											    strAuthType = NodeAttribute.getNodeValue().toLowerCase();
											
											}
											else {
												
												Logger.LogError( "-1009", Lang.Translate( "The [%s] attribute value for connection [%s] must be only one of the next values: [%s,%s]", ConfigXMLTagsDBServicesManager._Auth_Type, strName, ConfigXMLTagsDBServicesManager._Auth_Type_Engine, ConfigXMLTagsDBServicesManager._Auth_Type_Database ) );
												break; //Stop parse more attributes
												
											}
										
										}
										else {
											
											Logger.LogError( "-1010", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._Auth_Type, strAuthType ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._User ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strUser = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1011", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._User, strUser ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Password ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strPassword = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1012", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._Password, strPassword ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Date_Format ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDateFormat = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1013", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._Date_Format, strDateFormat ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Time_Format ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strTimeFormat = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1014", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._Time_Format, strTimeFormat ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsDBServicesManager._Date_Time_Format ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDateTimeFormat = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.LogError( "-1015", Lang.Translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConfigXMLTagsDBServicesManager._Date_Time_Format, strDateTimeFormat ) );
											break; //Stop parse more attributes
											
										}
										
									}
					            
					            }
					            else {
					            	
							    	Logger.LogError( "-1001", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConfigConnectionNode.getNodeName(), Integer.toString( intLocalConfigConnectionIndex ) ) );
							    	break;
					            	
					            }
					            
					        }
					        
					        if ( strName.isEmpty() == false && strDriver.isEmpty() == false && strEngine.isEmpty() == false && strEngineVersion.isEmpty() == false && strIP.isEmpty() == false && intPort != -1 && strDatabase.isEmpty() == false && strAuthType.isEmpty() == false && strUser.isEmpty() == false && strPassword.isEmpty() == false && strDateFormat.isEmpty() == false && strTimeFormat.isEmpty() == false && strDateTimeFormat.isEmpty() == false ) {
					        
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

                                	boolean bDBDriverTest = false;
                                	
                        			CClassPathLoader ClassPathLoader = new CClassPathLoader( Logger, Lang );

                        			//Load the databases drivers
                        			ClassPathLoader.LoadClassFiles( this.strDBDriversDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

                        			try {

                                		Class.forName( strDriver );
                                    
                                		bDBDriverTest = true;
                                		
                                	} 
                                	catch ( Exception Ex ) {
                                    
                            			Logger.LogException( "-1024", Ex.getMessage(), Ex );
                                    
                                	}

                                	if ( bDBDriverTest == true ) {
	                                	
                                	    CConfigDBConnection DBConnection = new CConfigDBConnection();
	                                	DBConnection.strName = strName;
	                                	DBConnection.strDriver = strDriver;
	                                	DBConnection.strEngine = strEngine;
	                                	DBConnection.strEngineVersion = strEngineVersion;
	                                	DBConnection.strIP = strIP;
	                                	DBConnection.strAddressType = strAddressType;
	                                	DBConnection.intPort = intPort;
	                                	DBConnection.strDatabase = strDatabase;
	                                	DBConnection.strAuthType = strAuthType;
	                                	DBConnection.strUser = strUser;
	                                	DBConnection.strPassword = strPassword;
	                                	DBConnection.strDateFormat = strDateFormat;
	                                	DBConnection.strTimeFormat = strTimeFormat;
	                                	DBConnection.strDateTimeFormat = strDateTimeFormat;
	
	                                	this.ConfiguredDBConnections.add( DBConnection );
	
	                                	Logger.LogMessage( "1", Lang.Translate( "Connection database defined and added. Name: [%s], Driver: [%s], Engine: [%s], Engine_Version: [%s], IP: [%s], Address_Type: [%s], Port: [%s], Database: [%s], User: [%s], Password: [%s], DateFormat: [%s], TimeFormat: [%s], DateTimeFormat: [%s]", strName, strDriver, strEngine, strEngineVersion, strIP, strAddressType, Integer.toString( intPort ), strDatabase, strUser, strPassword, strDateFormat, strTimeFormat, strDateTimeFormat ) );

                                	}
                                	else {

    					            	Logger.LogError( "-1015", Lang.Translate( "The database connection driver [%s] for the connection name: [%s] is invalid or not found in path [%s]", strDriver, strName, this.strDBDriversDir ) );
                                		
                                	}
	                                	
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
        else if ( ConfigSectionNode.getNodeName().equals( ConfigXMLTagsDBServicesManager._BuiltinResponsesFormats ) == true ) {

        	if ( this.LoadConfigSectionBuiltinResponsesFormats( ConfigSectionNode, Lang, Logger ) == false ) {
        		
    			Logger.LogError( "-1002", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
        		
        	}
             
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConfigXMLTagsDBServicesManager._DBConnections ) == true ) {

        	if ( this.LoadConfigSectionDBConnections( ConfigSectionNode, Lang, Logger ) == false ) {
        		
    			Logger.LogError( "-1003", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
        		
        	}
             
        }
		
		return bResult;
		
	}
	
    @Override
	public String getConfigValue( String strConfigKey ) {
		
		if ( strConfigKey.equals( "strXML_DataPacket_CharSet" ) )
		   return this.strXML_DataPacket_CharSet;
		else if ( strConfigKey.equals( "strXML_DataPacket_ContentType" ) )
			   return this.strXML_DataPacket_ContentType;
		else	
    	   return "";
		
	}
	
	/*public boolean LoadConfig( String strConfigFilePath, CLanguage Lang, CExtendedLogger Logger ) {
		
        if ( DBServicesManagerLogger == null )
        	DBServicesManagerLogger = Logger;

        if ( DBServicesManagerLang == null )
        	DBServicesManagerLang = Lang;

	    return super.LoadConfig( strConfigFilePath, Lang, Logger );
	
	}*/
	
}