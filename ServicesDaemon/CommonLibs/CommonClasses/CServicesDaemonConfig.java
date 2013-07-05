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
package CommonClasses;

//import java.io.File;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.maindataservices.Utilities;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import ExtendedLogger.CExtendedLogFilter;
import ExtendedLogger.CExtendedLogger;

public class CServicesDaemonConfig extends CAbstractConfigLoader {
	
	protected static CServicesDaemonConfig ServicesDaemonConfig = null;

    public ArrayList<String> InitArgs = null;
	public String strDefaultLang;
	public String strManagersDir;
    public String strKeyStoreFile;
    public String strKeyStorePassword;
    public String strKeyManagerPassword;
	public int intMaxIdleTime;
	public int intMaxRequestHeaderSize;
	public String strResponseRequestMethod; //OnlyGET, OnlyPOST, Any
    
    public String strClassNameMethodName; 
    public boolean bExactMatch;
    public boolean bLogMissingTranslations;
    public Level LoggingLevel;
    
    public List<CConfigNetworkInterface> ConfiguredNetworkInterfaces;
	public List<CConfigAccessControl> ConfiguredAccessControl;
	
	static {
		
		ServicesDaemonConfig = new CServicesDaemonConfig();
		
	} 
	
	public static CServicesDaemonConfig getServicesDaemonConfig() {
		
		return ServicesDaemonConfig;
		
	} 
	
	public CServicesDaemonConfig() {
		
		super();
		
		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsServicesDaemon._Logger );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsServicesDaemon._System );  //2
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsServicesDaemon._NetworkInterfaces ); //3
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsServicesDaemon._AccessControl ); //4
		bFirstLevelConfigSectionsMustExists.add( true );
		
		strDefaultLang = DefaultConstantsServicesDaemon.strDefaultLang; //"init.lang";
		strManagersDir = DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultManagersDir; //"Managers/"; 
	    strKeyStoreFile = DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultKeyStoreFile; //"ServicesDaemon.keystore";
	    strKeyStorePassword = DefaultConstantsServicesDaemon.strDefaultDefaultPassword; //"12345678";
	    strKeyManagerPassword = DefaultConstantsServicesDaemon.strDefaultDefaultPassword; //"12345678";
	    intMaxIdleTime = DefaultConstantsServicesDaemon.intDefaultMaxIdleTime; //30000
	    intMaxRequestHeaderSize = DefaultConstantsServicesDaemon.intDefaultMaxRequestHeaderSize; //8192	
	    
		strResponseRequestMethod = ConfigXMLTagsServicesDaemon._Request_Method_ANY;

	    strClassNameMethodName = DefaultConstantsServicesDaemon.strDefaultLogClassMethod; //*.*
	    bExactMatch = DefaultConstantsServicesDaemon.bDefaultLogExactMatch; //false
	    bLogMissingTranslations = DefaultConstantsServicesDaemon.bDefaultLogMissingTranslations; //false
	    LoggingLevel = Level.parse( DefaultConstantsServicesDaemon.strDefaultLogLevel ); //ALL
     
	    
	    ConfiguredNetworkInterfaces = new ArrayList<CConfigNetworkInterface>(); 
		ConfiguredAccessControl = new ArrayList<CConfigAccessControl>();
	
	}
	
	public boolean LoadConfigSectionLogger( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {
		
        boolean bResult = true;
        
        try {
		   
			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConfigXMLTagsServicesDaemon._ClassName_MethodName, ConfigXMLTagsServicesDaemon._Exact_Match, ConfigXMLTagsServicesDaemon._Level, ConfigXMLTagsServicesDaemon._Log_Missing_Translations };
				
				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._ClassName_MethodName ) ) {

							this.strClassNameMethodName = NodeAttribute.getNodeValue();
						 	   
							CExtendedLogFilter ExFilter = (CExtendedLogFilter) Logger.getFilter();

							ExFilter.setLogFilters( NodeAttribute.getNodeValue() );
	
				            Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strClassNameMethodName", NodeAttribute.getNodeValue() ) );

						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Exact_Match ) ) {

							this.bExactMatch = NodeAttribute.getNodeValue().toLowerCase().equals( "true" );
		            		  
							if ( this.bExactMatch == false ) {
								 
								CExtendedLogFilter ExFilter = (CExtendedLogFilter) Logger.getFilter();

								ExFilter.setExactMatch( bExactMatch );
		
					            Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "bExactMatch", NodeAttribute.getNodeValue() ) );
						
							}
							
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Log_Missing_Translations ) ) {

							this.bLogMissingTranslations = NodeAttribute.getNodeValue().toLowerCase().equals( "true" );
		            		  
							if ( this.bLogMissingTranslations == true ) {
								 
					            Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "bLogMissingTranslations", NodeAttribute.getNodeValue() ) );
						
							}
							
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Level ) ) {

							this.LoggingLevel = Level.parse( NodeAttribute.getNodeValue() );
							
							if ( this.LoggingLevel.equals( Logger.getLevel() ) == false ) {
								
								Logger.setLevel( this.LoggingLevel );
								
							}
		            		  
				            Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "LoggingLevel", NodeAttribute.getNodeValue() ) );
						
						}
		            
		            }
		            
		        }
			
			}
		
		
		}
		catch ( Exception Ex ) {
			
        	Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
        	bResult = false;
        	
		}
        
        return bResult;
		
	}
    
    public boolean LoadConfigSectionSystem( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
		   
			if ( ConfigSectionNode.hasAttributes() == true ) {
		
				String strAttributesOrder[] = { ConfigXMLTagsServicesDaemon._Managers_Dir, ConfigXMLTagsServicesDaemon._Key_Store_File, ConfigXMLTagsServicesDaemon._Key_Store_Password, ConfigXMLTagsServicesDaemon._Key_Manager_Password, ConfigXMLTagsServicesDaemon._Max_Idle_Time, ConfigXMLTagsServicesDaemon._Max_Request_Header_Size, ConfigXMLTagsServicesDaemon._Response_Request_Method };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
		            	if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Managers_Dir ) ) {

							this.strManagersDir = NodeAttribute.getNodeValue();
		
					        if ( this.strManagersDir.isEmpty() == false && new File( this.strManagersDir ).isAbsolute() == false ) {
					        
                               this.strManagersDir = DefaultConstantsServicesDaemon.strDefaultRunningPath + this.strManagersDir;
					        	
					        }
							
							Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strManagersDir", this.strManagersDir ) );
				        
					        if ( Utilities.CheckDir( this.strManagersDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Key_Store_File ) ) {

							this.strKeyStoreFile = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strKeyStoreFile.isEmpty() == false && new File( this.strKeyStoreFile ).isAbsolute() == false ) {
						        
					        	this.strKeyStoreFile = DefaultConstantsServicesDaemon.strDefaultRunningPath + this.strKeyStoreFile;
						        	
						    }

					        Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strKeyStoreFile", this.strKeyStoreFile) );
						
					        if ( this.strKeyStoreFile.isEmpty() == false && Utilities.CheckFile( this.strKeyStoreFile, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Key_Store_Password ) ) {

							this.strKeyStorePassword = NodeAttribute.getNodeValue();
		            		  
				            Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strKeyStorePassword", NodeAttribute.getNodeValue() ) );
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Key_Manager_Password ) ) {

							this.strKeyManagerPassword = NodeAttribute.getNodeValue();
		            		  
				            Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strKeyManagerPassword", NodeAttribute.getNodeValue() ) );
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Max_Idle_Time ) ) {

							this.intMaxIdleTime = Utilities.StrToInteger( NodeAttribute.getNodeValue(), Logger );
		            		  
				            if ( this.intMaxIdleTime < DefaultConstantsServicesDaemon.intDefaultMinIdleTime ) {   
							
				            	this.intMaxIdleTime = DefaultConstantsServicesDaemon.intDefaultMinIdleTime;
				            	
				            	Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute value [%s] is invalid, using the default max idle time of [%s] seconds", ConfigXMLTagsServicesDaemon._Max_Idle_Time, NodeAttribute.getNodeValue(), Integer.toString( DefaultConstantsServicesDaemon.intDefaultMinIdleTime ) ) );
				            	
				            }

				            Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "intMaxIdleTime", Integer.toString( this.intMaxIdleTime ) ) );
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Max_Request_Header_Size ) ) {

							this.intMaxRequestHeaderSize = Utilities.StrToInteger( NodeAttribute.getNodeValue(), Logger );
		            		  
				            if ( this.intMaxRequestHeaderSize < DefaultConstantsServicesDaemon.intDefaultMinRequestHeaderSize ) {   
							
				            	this.intMaxRequestHeaderSize = DefaultConstantsServicesDaemon.intDefaultMinRequestHeaderSize;
				            	
				            	Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute value [%s] is invalid, using the default max request header size of [%s] bytes", ConfigXMLTagsServicesDaemon._Max_Request_Header_Size, NodeAttribute.getNodeValue(), Integer.toString( DefaultConstantsServicesDaemon.intDefaultMinRequestHeaderSize ) ) );
				            	
				            }

				            Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "intMaxRequestHeaderSize", Integer.toString( this.intMaxRequestHeaderSize ) ) );
						
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
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsServicesDaemon._Managers_Dir ) ) {
		            
		            	if ( new File( this.strManagersDir ).isAbsolute() == false ) {

		            		this.strManagersDir = DefaultConstantsServicesDaemon.strDefaultRunningPath + this.strManagersDir;

		            	}

		            	Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strManagersDir", this.strManagersDir ) );

		            	if ( Utilities.CheckDir( this.strManagersDir, Logger, Lang ) == false ) {

		            		bResult = false;

		            		break;

		            	}
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsServicesDaemon._Key_Store_File ) ) {
			            
		            	if ( new File( this.strKeyStoreFile ).isAbsolute() == false ) {

		            		this.strKeyStoreFile = DefaultConstantsServicesDaemon.strDefaultRunningPath + this.strKeyStoreFile;

		            	}

		            	Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strKeyStoreFile", this.strKeyStoreFile ) );

		            	if ( Utilities.CheckDir( this.strKeyStoreFile, Logger, Lang ) == false ) {

		            		bResult = false;

		            		break;

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
	
	public boolean LoadConfigSectionNetworkInterfaces( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {
        
		boolean bResult = false;
         
        try {

			String strAttributesOrder[] = { ConfigXMLTagsServicesDaemon._IP, ConfigXMLTagsServicesDaemon._Port, ConfigXMLTagsServicesDaemon._Use_SSL };

        	NodeList ConfigNetworkInterfacesList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigNetworkInterfacesList.getLength() > 0 ) {
			
	            for ( int intConfigNetworkInterfaceIndex = 0; intConfigNetworkInterfaceIndex < ConfigNetworkInterfacesList.getLength(); intConfigNetworkInterfaceIndex++ ) {
	                
					Node ConfigNetworkInterfaceNode = ConfigNetworkInterfacesList.item( intConfigNetworkInterfaceIndex );
	                 
	    			Logger.LogMessage( "1", Lang.Translate( "Reading XML network interface: [%s]", ConfigNetworkInterfaceNode.getNodeName() ) );        
	                 
					if ( ConfigNetworkInterfaceNode.getNodeName().equals( ConfigXMLTagsServicesDaemon._NetworkInterface ) == true ) {

						String strIP = "";
						String strAddressType = "";
						int intPort = -1;
						boolean bUseSSL = false;
						
						if ( ConfigNetworkInterfaceNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigNetworkInterfaceNode.getAttributes();

					        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
					        	
					            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
					        	
					            if ( NodeAttribute != null ) {
					            	
					            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
					            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
									
									if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._IP ) ) {

										if ( Utilities.isValidIPV4( NodeAttribute.getNodeValue() ) == true ) {
											
											strIP = NodeAttribute.getNodeValue();
											strAddressType = "ipv4";
										
										}
										else if ( Utilities.isValidIPV6( NodeAttribute.getNodeValue() ) == true ) {
										
											strIP = NodeAttribute.getNodeValue();
											strAddressType = "ipv6";

										}
							            else {
							            	
											Logger.LogError( "-1000", Lang.Translate( "The [%s] attribute value [%s] is not valid ip address", ConfigXMLTagsServicesDaemon._IP, NodeAttribute.getNodeValue() ) );
											break; //Stop parse more attributes
											
							            }
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Port ) ) {

										int intTmpPort = Utilities.StrToInteger( NodeAttribute.getNodeValue().trim(), Logger );
											
										if ( intTmpPort >= DefaultConstantsServicesDaemon.intDefaultMinPortNumber && intTmpPort <= DefaultConstantsServicesDaemon.intDefaultMaxPortNumber ) {
												
											intPort = intTmpPort;
												
										}
										else {
												
											Logger.LogError( "-1001", Lang.Translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConfigXMLTagsServicesDaemon._Port, NodeAttribute.getNodeValue(), Integer.toString( DefaultConstantsServicesDaemon.intDefaultMinPortNumber ), Integer.toString( DefaultConstantsServicesDaemon.intDefaultMaxPortNumber ) ) );
											break; //Stop parse more attributes
												
										}
									    
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Use_SSL ) ) {

										if ( NodeAttribute.getNodeValue().equals( "true" ) ) {
											
											if ( this.strKeyStoreFile.isEmpty() == false ) {   
											
												if ( this.strKeyStorePassword.isEmpty() == false ) {
												
													if ( this.strKeyManagerPassword.isEmpty() == false ) {

														bUseSSL = true;

													}
													else {
														
											            Logger.LogError( "-1002", Lang.Translate( "Cannot use SSL beacuse no [%s] defined in config file section [%s]", ConfigXMLTagsServicesDaemon._Key_Manager_Password, ConfigXMLTagsServicesDaemon._System ) );
											            break; //Stop parse more attributes
														
													}
												
												}
												else {
													
										            Logger.LogError( "-1003", Lang.Translate( "Cannot use SSL beacuse no [%s] defined in config file section [%s]", ConfigXMLTagsServicesDaemon._Key_Store_Password, ConfigXMLTagsServicesDaemon._System ) );
										            break; //Stop parse more attributes
													
												}
											
											}
											else {
												
									            Logger.LogError( "-1004", Lang.Translate( "Cannot use SSL beacuse no [%s] defined in config file section [%s]", ConfigXMLTagsServicesDaemon._Key_Store_File, ConfigXMLTagsServicesDaemon._System ) );
									            break; //Stop parse more attributes
												
											}
											
										}
								        
									}
					            
					            }
					            
					        }
					        
					        if ( strIP.isEmpty() == false && intPort != -1 ) { // && intPort >= GlobalConstants.intMinPortNumber && intPort <= GlobalConstants.intMaxPortNumber ) {
					        
					        	boolean bPortUsed = false;
					        	String strPreviusIP = "";
					        	
					            //Check for duplicate port number and ip
					        	for ( int intIndex = 0; intIndex <  this.ConfiguredNetworkInterfaces.size(); intIndex++ ) {
					        		
					        		CConfigNetworkInterface NetInterface = this.ConfiguredNetworkInterfaces.get( intIndex );
					        		
					        		if ( NetInterface.intPort == intPort && ( NetInterface.strIP.equals( strIP ) || NetInterface.strIP.equals( DefaultConstantsServicesDaemon.strDefaultIPV4All ) || NetInterface.strIP.equals( DefaultConstantsServicesDaemon.strDefaultIPV6All ) || strIP.equals( DefaultConstantsServicesDaemon.strDefaultIPV4All ) || strIP.equals( DefaultConstantsServicesDaemon.strDefaultIPV6All ) ) ) {

					        			strPreviusIP = NetInterface.strIP;
					        			bPortUsed = true;
					        			break;
					        			
					        		}
					        		
					        	}
					        	
                                if ( bPortUsed == false ) {
					        	
						        	CConfigNetworkInterface NetInterface = new CConfigNetworkInterface();
						        	NetInterface.strIP = strIP;
						        	NetInterface.intPort = intPort;
						        	NetInterface.strAddressType = strAddressType;
						        	NetInterface.bUseSSL = bUseSSL;
						        	
						        	this.ConfiguredNetworkInterfaces.add( NetInterface );
						        	
					            	Logger.LogMessage( "1", Lang.Translate( "Network interface defined and added. IP: [%s], Address_Type: [%s], Port: [%s], Use_SSL: [%s]", strIP, strAddressType, Integer.toString( intPort ), bUseSSL == true?"true":"false" ) );

                                }
                                else {
                                	
					            	Logger.LogError( "-1005", Lang.Translate( "The port number: [%s] for the IP address: [%s] is used in previus network interface config with IP address: [%s], cannot use the port number again", Integer.toString( intPort ), strIP, strPreviusIP ) );
                                	
                                }
				            	
					        }
					        else {
					        	
					            Logger.LogError( "-1006", Lang.Translate( "Network interface config attributes is not valid" ) );
					        	
					        }
						
						}
					
					}
	            
	            }
	            
	            if ( this.ConfiguredNetworkInterfaces.isEmpty() == true ) {
	            	
	    			Logger.LogWarning( "-1", Lang.Translate( "No valid network interface defined. Adding default" ) );        
	            	
		        	//Add default network interface config
	    			CConfigNetworkInterface NetInterface = new CConfigNetworkInterface();
		        	
		        	this.ConfiguredNetworkInterfaces.add( NetInterface );

	            }
	            
	            bResult = true;
	        
	        }
	        else {
	        	
    			Logger.LogWarning( "-1", Lang.Translate( "No network interface defined. Adding default" ) );        
	        	
	        	//Add default network interface config
    			CConfigNetworkInterface NetInterface = new CConfigNetworkInterface();
	        	
	        	this.ConfiguredNetworkInterfaces.add( NetInterface );
	        	
	        	bResult = true;
	        	
	        }
	        
        }
		catch ( Exception Ex ) {
			
			Logger.LogException( "-1011", Ex.getMessage(), Ex );
			
		}
        
		Logger.LogMessage( "1", Lang.Translate( "Count of network interfaces defined: [%s]", Integer.toString( this.ConfiguredNetworkInterfaces.size() ) ) );        
        
        return bResult;

	}

	public boolean LoadConfigSectionAccessControl( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = false;
		
        try {

			String strAttributesOrder[] = { ConfigXMLTagsServicesDaemon._IP, ConfigXMLTagsServicesDaemon._Context_Path, ConfigXMLTagsServicesDaemon._Action };

			NodeList ConfigAccessControlList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigAccessControlList.getLength() > 0 ) {
			
	            for ( int intConfigAccessControlIndex = 0; intConfigAccessControlIndex < ConfigAccessControlList.getLength(); intConfigAccessControlIndex++ ) {
	                
	            	Node ConfigConnectionNode = ConfigAccessControlList.item( intConfigAccessControlIndex );
	                 
	    			Logger.LogMessage( "1", Lang.Translate( "Reading XML access control rule: [%s]", ConfigConnectionNode.getNodeName() ) );        
	                 
					if ( ConfigConnectionNode.getNodeName().equals( ConfigXMLTagsServicesDaemon._From ) == true ) {
		            
						String strFromIP = "";
						String strContextPath = ConfigXMLTagsServicesDaemon._Context_Path_Default;
						String strAction = "";
						boolean bAlwaysDeny = false;
						boolean bAlwaysAllow = false;
						
						if ( ConfigConnectionNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigConnectionNode.getAttributes();

					        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
					            
					        	Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
					        	
					            if ( NodeAttribute != null ) {
					            	
					            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
					            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
									
									if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._IP ) ) {

										if ( NodeAttribute.getNodeValue().isEmpty() == false ) {
											
											strFromIP = NodeAttribute.getNodeValue();
											
										}
							            else {
							            	
											Logger.LogError( "-1000", Lang.Translate( "The [%s] attribute cannot empty string", ConfigXMLTagsServicesDaemon._IP ) );
											break; //Stop parse more attributes
											
							            }
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Context_Path ) ) {

										if ( NodeAttribute.getNodeValue().isEmpty() == false ) {
											
											strContextPath = NodeAttribute.getNodeValue();
										
										}
							            else {
							            	
											Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConfigXMLTagsServicesDaemon._Context_Path, ConfigXMLTagsServicesDaemon._Context_Path_Default ) );
											
							            }
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsServicesDaemon._Action ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConfigXMLTagsServicesDaemon._Action_Deny ) || NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConfigXMLTagsServicesDaemon._Action_Allow ) ) {    
										 
										    strAction = NodeAttribute.getNodeValue().toLowerCase();
											bAlwaysAllow = NodeAttribute.getNodeValue().toLowerCase().equals( ConfigXMLTagsServicesDaemon._Action_Allow );
										    bAlwaysDeny = NodeAttribute.getNodeValue().toLowerCase().equals( ConfigXMLTagsServicesDaemon._Action_Deny );
										
										}
										else {
											
											Logger.LogError( "-1001", Lang.Translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s]", ConfigXMLTagsServicesDaemon._Action, ConfigXMLTagsServicesDaemon._Action_Allow, ConfigXMLTagsServicesDaemon._Action_Deny ) );
											break; //Stop parse more attributes
											
										}
										
									}
									
					            }
					         
					        }
					        
					        if ( strFromIP.isEmpty() == false &&  strAction.isEmpty() == false ) { 
						        
					        	boolean bFromIPUsed = false;
					        	int intAccessControlRuleIndex = -1;
					        	
					            //Check for duplicate IP address
					        	for ( int intIndex = 0; intIndex <  this.ConfiguredAccessControl.size(); intIndex++ ) {
					        		
					        		CConfigAccessControl AccessControl = this.ConfiguredAccessControl.get( intIndex );
					        		
					        		if ( AccessControl.strFromIP.equals( strFromIP ) ) {

					        			intAccessControlRuleIndex = intIndex;
					        			bFromIPUsed = true;
					        			break;
					        			
					        		}
					        		
					        	}
					        	
                                if ( bFromIPUsed == false ) {
					        	
						        	CConfigAccessControl AccessControl = new CConfigAccessControl();
						        	AccessControl.strFromIP = strFromIP;
						        	AccessControl.strContextPath = strContextPath;
						        	AccessControl.bAlwaysAllow = bAlwaysAllow;
						        	AccessControl.bAlwaysDeny = bAlwaysDeny;
						        	
						        	this.ConfiguredAccessControl.add( AccessControl );
						        	
					            	Logger.LogMessage( "1", Lang.Translate( "Access control rule defined and added. IP: [%s], ContextPath: [%s], Action: [%s]", strFromIP, strContextPath, strAction ) );

                                }
                                else {
                                	
					            	Logger.LogError( "-1002", Lang.Translate( "The IP address [%s] is used in previus access control rule number: [%s], cannot use the IP address again", strFromIP, Integer.toString( intAccessControlRuleIndex ) ) );
                                	
                                }
				            	
					        }
					        else {
					        	
					            Logger.LogError( "-1003", Lang.Translate( "Access control rule config attributes is not valid" ) );
					        	
					        }
						
						}    
					
					}
		            
		        }
		        
	            
	            if ( this.ConfiguredAccessControl.isEmpty() == true ) {
	            	
	    			Logger.LogWarning( "-1", Lang.Translate( "No valid access control rules defined" ) );        
	            	
	            }
	            
	            bResult = true;
	            
	        }
	        
        }
        catch ( Exception Ex ) {
				
			Logger.LogException( "-1010", Ex.getMessage(), Ex );
				
		}

		Logger.LogMessage( "1", Lang.Translate( "Count of access controls rules defined: [%s]", Integer.toString( this.ConfiguredAccessControl.size() ) ) );        
						
		return bResult;
		
	}
	
	public boolean LoadConfigSection( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;
		
		Logger.LogMessage( "1", Lang.Translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals(  ConfigXMLTagsServicesDaemon._Logger ) == true ) {
           
			if ( this.LoadConfigSectionLogger( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.LogError( "-1001", Lang.Translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConfigXMLTagsServicesDaemon._System ) == true ) {
         
			if ( this.LoadConfigSectionSystem( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.LogError( "-1002", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
    			
			}

        }
        else if ( ConfigSectionNode.getNodeName().equals( ConfigXMLTagsServicesDaemon._NetworkInterfaces ) == true ) {
             
			if ( this.LoadConfigSectionNetworkInterfaces( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.LogError( "-1003", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			}
			
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConfigXMLTagsServicesDaemon._AccessControl ) == true ) {

        	if ( this.LoadConfigSectionAccessControl( ConfigSectionNode, Lang, Logger ) == false ) {
        		
    			Logger.LogError( "-1004", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
        		
        	}
             
        }

		return bResult;
		
	}

	/*public boolean LoadConfig( String strConfigFilePath, CLanguage Lang, CExtendedLogger Logger ) {
		
        if ( this.Logger == null )
        	this.Logger = Logger;

        if ( this.Lang == null )
        	this.Lang = Lang;

	    return super.LoadConfig( strConfigFilePath, Lang, Logger );
	
	}*/
         
}
