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
import java.util.logging.Level;

import net.maindataservices.Utilities;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import ExtendedLogger.CExtendedLogFilter;
import ExtendedLogger.CExtendedLogger;

public class CConfigServicesDaemon extends CAbstractConfigLoader {
	
	protected static CConfigServicesDaemon ConfigServicesDaemon = null;

    public ArrayList<String> InitArgs = null;
	public String strManagersDir;
    public String strKeyStoreFile;
    public String strKeyStorePassword;
    public String strKeyManagerPassword;
	public int intMaxIdleTime;
	public int intMaxRequestHeaderSize;
	public String strResponseRequestMethod; //OnlyGET, OnlyPOST, Any
    
	public String strLogInstanceID;
    public String strClassNameMethodName; 
    public boolean bExactMatch;
    public boolean bLogMissingTranslations;
    public Level LoggingLevel;
    public String strLogIP;
    public int intLogPort;
    
	public String strHTTPLogURL;
	public String strHTTPLogUser;
	public String strHTTPLogPassword;
	public String strProxyIP;
	public int intProxyPort;
    public String strProxyUser;
	public String strProxyPassword;
    
    public ArrayList<CConfigNetworkInterface> ConfiguredNetworkInterfaces;
	public ArrayList<CConfigAccessControl> ConfiguredAccessControl;
	
	public static CConfigServicesDaemon getConfigServicesDaemon() {
		
		return ConfigServicesDaemon; //It cant return NULL
		
	}
	
	public static CConfigServicesDaemon getConfigServicesDaemon( String strRunningPath ) {
		
		if ( ConfigServicesDaemon == null ) {
			
			ConfigServicesDaemon = new CConfigServicesDaemon( strRunningPath );
			
		}
		
		return ConfigServicesDaemon;
		
	} 
	
	public CConfigServicesDaemon( String strRunningPath ) {
		
		super( strRunningPath );
		
		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._Logger );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._System );  //2
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._NetworkInterfaces ); //3
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._AccessControl ); //4
		bFirstLevelConfigSectionsMustExists.add( true );
		
		strManagersDir = this.strRunningPath + ConstantsCommonClasses._Managers_Dir; //"Managers/"; 
	    strKeyStoreFile = this.strRunningPath + ConstantsCommonClasses._Key_Store_File; //"ServicesDaemon.keystore";
	    strKeyStorePassword = ConstantsCommonClasses._Default_Password; //"12345678";
	    strKeyManagerPassword = ConstantsCommonClasses._Default_Password; //"12345678";
	    intMaxIdleTime = ConstantsCommonClasses._Max_Idle_Time; //30000
	    intMaxRequestHeaderSize = ConstantsCommonClasses._Max_Request_Header_Size; //8192	
	    
		strResponseRequestMethod = ConstantsCommonConfigXMLTags._Request_Method_ANY;

		strLogInstanceID = ConstantsCommonClasses._Log_Instance_ID;
	    strClassNameMethodName = ConstantsCommonClasses._Log_Class_Method; //*.*
	    bExactMatch = ConstantsCommonClasses._Log_Exact_Match; //false
	    bLogMissingTranslations = ConstantsCommonClasses._Log_Missing_Translations; //false
	    LoggingLevel = Level.parse( ConstantsCommonClasses._Log_Level ); //ALL
	    strLogIP = ConstantsCommonClasses._Log_IP; //""
        intLogPort = ConstantsCommonClasses._Log_Port_Number; //30000	     
	    
    	strHTTPLogURL = ConstantsCommonClasses._HTTP_Log_URL;
    	strHTTPLogUser = ConstantsCommonClasses._HTTP_Log_User;
    	strHTTPLogPassword = ConstantsCommonClasses._HTTP_Log_Password;
    	strProxyIP = ConstantsCommonClasses._Proxy_IP;
    	intProxyPort = ConstantsCommonClasses._Proxy_Port;
    	strProxyUser = ConstantsCommonClasses._Proxy_User;
    	strProxyPassword = ConstantsCommonClasses._Proxy_Password;
        
	    ConfiguredNetworkInterfaces = new ArrayList<CConfigNetworkInterface>(); 
		ConfiguredAccessControl = new ArrayList<CConfigAccessControl>();
	
	}
	
	public boolean LoadConfigSectionLogger( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {
		
        boolean bResult = true;
        
        try {
		   
			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Log_Instance_ID, ConstantsCommonConfigXMLTags._ClassName_MethodName, ConstantsCommonConfigXMLTags._Exact_Match, ConstantsCommonConfigXMLTags._Level, ConstantsCommonConfigXMLTags._Log_Missing_Translations, ConstantsCommonConfigXMLTags._Log_IP, ConstantsCommonConfigXMLTags._Log_Port, ConstantsCommonConfigXMLTags._HTTP_Log_URL, ConstantsCommonConfigXMLTags._HTTP_Log_User, ConstantsCommonConfigXMLTags._HTTP_Log_Password, ConstantsCommonConfigXMLTags._Proxy_IP, ConstantsCommonConfigXMLTags._Proxy_Port, ConstantsCommonConfigXMLTags._Proxy_User, ConstantsCommonConfigXMLTags._Proxy_Password };
				
				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Log_Instance_ID ) ) {

							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
							
								this.strLogInstanceID = NodeAttribute.getNodeValue().trim();
								
								Logger.setInstanceID( strLogInstanceID );
							
							}
							else {
								
								Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Log_Instance_ID, NodeAttribute.getNodeValue(), CommonClasses.ConstantsCommonClasses._Log_Instance_ID ) );
								
							}
						 	   
				            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strLogInstanceID", NodeAttribute.getNodeValue() ) );

						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._ClassName_MethodName ) ) {

							this.strClassNameMethodName = NodeAttribute.getNodeValue().trim();
						 	   
							CExtendedLogFilter ExFilter = (CExtendedLogFilter) Logger.getFilter();

							ExFilter.setLogFilters( NodeAttribute.getNodeValue() );
	
				            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strClassNameMethodName", NodeAttribute.getNodeValue() ) );

						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Exact_Match ) ) {

							this.bExactMatch = NodeAttribute.getNodeValue().trim().toLowerCase().equals( "true" );
		            		  
							if ( this.bExactMatch == false ) {
								 
								CExtendedLogFilter ExFilter = (CExtendedLogFilter) Logger.getFilter();

								ExFilter.setExactMatch( bExactMatch );
		
					            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "bExactMatch", NodeAttribute.getNodeValue() ) );
						
							}
							
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Log_Missing_Translations ) ) {

							this.bLogMissingTranslations = NodeAttribute.getNodeValue().trim().toLowerCase().equals( "true" );
		            		  
							if ( this.bLogMissingTranslations == true ) {
								 
					            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "bLogMissingTranslations", NodeAttribute.getNodeValue() ) );
						
							}
							
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Level ) ) {

							this.LoggingLevel = Level.parse( NodeAttribute.getNodeValue().trim() );
							
							if ( this.LoggingLevel.equals( Logger.getLevel() ) == false ) {
								
								Logger.setLevel( this.LoggingLevel );
								
							}
		            		  
				            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "LoggingLevel", NodeAttribute.getNodeValue() ) );
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Log_IP ) ) {

							String strIP = NodeAttribute.getNodeValue().trim();
							
							if ( Utilities.isValidIP( strIP ) ) {
								
								Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strLogIP", strIP ) );

								this.strLogIP = strIP;
						
								Logger.setLogIP( this.strLogIP );
								
							}
				            else {
				            	
								Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is not valid ip address", CommonClasses.ConstantsCommonConfigXMLTags._Log_IP, NodeAttribute.getNodeValue() ) );
								Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Log_IP, NodeAttribute.getNodeValue(), CommonClasses.ConstantsCommonClasses._Log_IP ) );
								
				            }
		            		  
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Log_Port ) ) {

							int intTempPort = net.maindataservices.Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );
							
							if ( intTempPort >= CommonClasses.ConstantsCommonClasses._Min_Port_Number && intTempPort <= CommonClasses.ConstantsCommonClasses._Max_Port_Number ) {
								
					            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "intLogPort", NodeAttribute.getNodeValue().trim() ) );

								this.intLogPort = intTempPort;
								
								Logger.setLogPort( intLogPort );
								
							}
							else {
								
								Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Log_Port, NodeAttribute.getNodeValue(), Integer.toString( CommonClasses.ConstantsCommonClasses._Min_Port_Number ), Integer.toString( CommonClasses.ConstantsCommonClasses._Max_Port_Number )  ) );
								//Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Log_Port, NodeAttribute.getNodeValue(), Integer.toString( CommonClasses.ConstantsCommonClasses._Log_Port_Number )  ) );

								Logger.setLogPort( CommonClasses.ConstantsCommonClasses._Log_Port_Number );
								//this.intLogPort = CommonClasses.DefaultConstantsServicesDaemon.intDefaultLogPortNumber;
								
							}
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._HTTP_Log_URL ) ) {

							String strHTTPLogURL = NodeAttribute.getNodeValue().trim();
							
							if ( Utilities.isValidHTTPURL( strHTTPLogURL ) ) {
								
								Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strHTTPLogURL", strHTTPLogURL ) );

								this.strHTTPLogURL = strHTTPLogURL;
						
								Logger.setHTTPLogURL( this.strHTTPLogURL );
								
							}
				            else {
				            	
								Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is not valid URL address", CommonClasses.ConstantsCommonConfigXMLTags._HTTP_Log_URL, NodeAttribute.getNodeValue() ) );
								Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._HTTP_Log_URL, NodeAttribute.getNodeValue(), CommonClasses.ConstantsCommonClasses._HTTP_Log_URL ) );
								
				            }
		            		  
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._HTTP_Log_User ) ) {

							this.strHTTPLogUser = NodeAttribute.getNodeValue().trim();

							Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strHTTPLogUser", strHTTPLogUser ) );

							Logger.setHTTPLogUser( this.strHTTPLogURL );
		            		  
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._HTTP_Log_Password ) ) {

							this.strHTTPLogPassword = net.maindataservices.Utilities.uncryptString( ConstantsCommonConfigXMLTags._Password_Crypted, ConstantsCommonConfigXMLTags._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, NodeAttribute.getNodeValue().trim(), null, null );

							Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strHTTPLogPassword", strHTTPLogPassword ) );

							Logger.setHTTPLogPassword( this.strHTTPLogPassword );
		            		  
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_IP ) ) {

							String strProxyIP = NodeAttribute.getNodeValue().trim();
							
							if ( Utilities.isValidIP( strProxyIP ) ) {
								
								Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strProxyIP", strProxyIP ) );

								this.strProxyIP = strProxyIP;
						
								Logger.setProxyIP( this.strProxyIP );
								
							}
				            else {
				            	
								Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is not valid ip address", CommonClasses.ConstantsCommonConfigXMLTags._Proxy_IP, NodeAttribute.getNodeValue() ) );
								Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Proxy_IP, NodeAttribute.getNodeValue(), CommonClasses.ConstantsCommonClasses._Proxy_IP ) );
								
				            }
		            		  
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_Port ) ) {

							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
							
								int intTempPort = net.maindataservices.Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );

								if ( intTempPort >= CommonClasses.ConstantsCommonClasses._Min_Port_Number && intTempPort <= CommonClasses.ConstantsCommonClasses._Max_Port_Number ) {

									Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "intProxyPort", NodeAttribute.getNodeValue().trim() ) );

									this.intProxyPort = intTempPort;

									Logger.setProxyPort( intProxyPort );

								}
								else {

									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Proxy_Port, NodeAttribute.getNodeValue(), Integer.toString( CommonClasses.ConstantsCommonClasses._Min_Port_Number ), Integer.toString( CommonClasses.ConstantsCommonClasses._Max_Port_Number )  ) );

									Logger.setProxyPort( CommonClasses.ConstantsCommonClasses._Proxy_Port );

								}
						
							}
							
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_User ) ) {

							this.strProxyUser = NodeAttribute.getNodeValue().trim();

							Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strProxyUser", strProxyUser ) );

							Logger.setProxyUser( this.strProxyUser );
		            		  
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_Password ) ) {

							this.strProxyPassword = net.maindataservices.Utilities.uncryptString( ConstantsCommonConfigXMLTags._Password_Crypted, ConstantsCommonConfigXMLTags._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, NodeAttribute.getNodeValue().trim(), null, null );

							Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strProxyPassword", strProxyPassword ) );

							Logger.setProxyPassword( this.strProxyPassword );
		            		  
						}
		            
		            }
		            
		        }
		        
		        Logger.activateSocketHandler( false );
		        Logger.activateHTTPHandler( false );
			
			}
		
		
		}
		catch ( Exception Ex ) {
			
        	Logger.logException( "-1010", Ex.getMessage(), Ex );
			
        	bResult = false;
        	
		}
        
        return bResult;
		
	}
    
    public boolean LoadConfigSectionSystem( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
		   
			if ( ConfigSectionNode.hasAttributes() == true ) {
		
				String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Managers_Dir, ConstantsCommonConfigXMLTags._Key_Store_File, ConstantsCommonConfigXMLTags._Key_Store_Password, ConstantsCommonConfigXMLTags._Key_Manager_Password, ConstantsCommonConfigXMLTags._Max_Idle_Time, ConstantsCommonConfigXMLTags._Max_Request_Header_Size, ConstantsCommonConfigXMLTags._Response_Request_Method };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
		            	if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Managers_Dir ) ) {

							this.strManagersDir = NodeAttribute.getNodeValue();
		
					        if ( this.strManagersDir.isEmpty() == false && new File( this.strManagersDir ).isAbsolute() == false ) {
					        
                               this.strManagersDir = this.strRunningPath + this.strManagersDir;
					        	
					        }
							
							Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strManagersDir", this.strManagersDir ) );
				        
					        if ( Utilities.checkDir( this.strManagersDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Key_Store_File ) ) {

							this.strKeyStoreFile = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strKeyStoreFile.isEmpty() == false && new File( this.strKeyStoreFile ).isAbsolute() == false ) {
						        
					        	this.strKeyStoreFile = this.strRunningPath + this.strKeyStoreFile;
						        	
						    }

					        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strKeyStoreFile", this.strKeyStoreFile) );
						
					        if ( this.strKeyStoreFile.isEmpty() == false && Utilities.checkFile( this.strKeyStoreFile, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Key_Store_Password ) ) {

							this.strKeyStorePassword = NodeAttribute.getNodeValue();
		            		  
				            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strKeyStorePassword", NodeAttribute.getNodeValue() ) );
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Key_Manager_Password ) ) {

							this.strKeyManagerPassword = NodeAttribute.getNodeValue();
		            		  
				            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strKeyManagerPassword", NodeAttribute.getNodeValue() ) );
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Max_Idle_Time ) ) {

							this.intMaxIdleTime = Utilities.strToInteger( NodeAttribute.getNodeValue(), Logger );
		            		  
				            if ( this.intMaxIdleTime < ConstantsCommonClasses._Min_Idle_Time ) {   
							
				            	this.intMaxIdleTime = ConstantsCommonClasses._Min_Idle_Time;
				            	
				            	Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default max idle time of [%s] seconds", ConstantsCommonConfigXMLTags._Max_Idle_Time, NodeAttribute.getNodeValue(), Integer.toString( ConstantsCommonClasses._Min_Idle_Time ) ) );
				            	
				            }

				            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "intMaxIdleTime", Integer.toString( this.intMaxIdleTime ) ) );
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Max_Request_Header_Size ) ) {

							this.intMaxRequestHeaderSize = Utilities.strToInteger( NodeAttribute.getNodeValue(), Logger );
		            		  
				            if ( this.intMaxRequestHeaderSize < ConstantsCommonClasses._Min_Request_Header_Size ) {   
							
				            	this.intMaxRequestHeaderSize = ConstantsCommonClasses._Min_Request_Header_Size;
				            	
				            	Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default max request header size of [%s] bytes", ConstantsCommonConfigXMLTags._Max_Request_Header_Size, NodeAttribute.getNodeValue(), Integer.toString( ConstantsCommonClasses._Min_Request_Header_Size ) ) );
				            	
				            }

				            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "intMaxRequestHeaderSize", Integer.toString( this.intMaxRequestHeaderSize ) ) );
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Response_Request_Method ) ) {
							
					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsCommonConfigXMLTags._Request_Method_ANY.toLowerCase() ) ) {

					        		this.strResponseRequestMethod = ConstantsCommonConfigXMLTags._Request_Method_ANY;
							        
					        		Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );
					        
					        
					        	}
					        	else if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsCommonConfigXMLTags._Request_Method_OnlyGET.toLowerCase() ) ) { 

					        		this.strResponseRequestMethod = ConstantsCommonConfigXMLTags._Request_Method_OnlyGET;
							        
					        		Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );

					        	}	
					        	else if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsCommonConfigXMLTags._Request_Method_OnlyPOST.toLowerCase() ) ) {    

					        	   this.strResponseRequestMethod = ConstantsCommonConfigXMLTags._Request_Method_OnlyPOST;
						    	
						           Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );

					        	}
					        	else {
					        		
						           Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s,%s]", "strResponseRequestMethod", ConstantsCommonConfigXMLTags._Request_Method_ANY, ConstantsCommonConfigXMLTags._Request_Method_OnlyGET, ConstantsCommonConfigXMLTags._Request_Method_OnlyPOST ) );
						           Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", "strResponseRequestMethod", NodeAttribute.getNodeValue(), this.strResponseRequestMethod ) );
					        		
					        	}
						    	
						    }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );
					        	
					        }
					        
						}
		            
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Managers_Dir ) ) {
		            
		            	if ( new File( this.strManagersDir ).isAbsolute() == false ) {

		            		this.strManagersDir = this.strRunningPath + this.strManagersDir;

		            	}

		            	Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strManagersDir", this.strManagersDir ) );

		            	if ( Utilities.checkDir( this.strManagersDir, Logger, Lang ) == false ) {

		            		bResult = false;

		            		break;

		            	}
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Key_Store_File ) ) {
			            
		            	if ( new File( this.strKeyStoreFile ).isAbsolute() == false ) {

		            		this.strKeyStoreFile = this.strRunningPath + this.strKeyStoreFile;

		            	}

		            	Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strKeyStoreFile", this.strKeyStoreFile ) );

		            	if ( Utilities.checkDir( this.strKeyStoreFile, Logger, Lang ) == false ) {

		            		bResult = false;

		            		break;

		            	}
		            	
		            }
		            
		        }
			
			}
		
		}
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;

	}
	
	public boolean LoadConfigSectionNetworkInterfaces( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {
        
		boolean bResult = false;
         
        try {

			String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._IP, ConstantsCommonConfigXMLTags._Port, ConstantsCommonConfigXMLTags._Use_SSL };

        	NodeList ConfigNetworkInterfacesList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigNetworkInterfacesList.getLength() > 0 ) {
			
	            for ( int intConfigNetworkInterfaceIndex = 0; intConfigNetworkInterfaceIndex < ConfigNetworkInterfacesList.getLength(); intConfigNetworkInterfaceIndex++ ) {
	                
					Node ConfigNetworkInterfaceNode = ConfigNetworkInterfacesList.item( intConfigNetworkInterfaceIndex );
	                 
	    			Logger.logMessage( "1", Lang.translate( "Reading XML network interface: [%s]", ConfigNetworkInterfaceNode.getNodeName() ) );        
	                 
					if ( ConfigNetworkInterfaceNode.getNodeName().equals( ConstantsCommonConfigXMLTags._NetworkInterface ) == true ) {

						String strIP = "";
						String strAddressType = "";
						int intPort = -1;
						boolean bUseSSL = false;
						
						if ( ConfigNetworkInterfaceNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigNetworkInterfaceNode.getAttributes();

					        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
					        	
					            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
					        	
					            if ( NodeAttribute != null ) {
					            	
					            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
					            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
									
									if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._IP ) ) {

										if ( Utilities.isValidIPV4( NodeAttribute.getNodeValue() ) == true ) {
											
											strIP = NodeAttribute.getNodeValue();
											strAddressType = "ipv4";
										
										}
										else if ( Utilities.isValidIPV6( NodeAttribute.getNodeValue() ) == true ) {
										
											strIP = NodeAttribute.getNodeValue();
											strAddressType = "ipv6";

										}
							            else {
							            	
											Logger.logError( "-1000", Lang.translate( "The [%s] attribute value [%s] is not valid ip address", ConstantsCommonConfigXMLTags._IP, NodeAttribute.getNodeValue() ) );
											break; //Stop parse more attributes
											
							            }
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Port ) ) {

										int intTmpPort = Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );
											
										if ( intTmpPort >= ConstantsCommonClasses._Min_Port_Number && intTmpPort <= ConstantsCommonClasses._Max_Port_Number ) {
												
											intPort = intTmpPort;
												
										}
										else {
												
											Logger.logError( "-1001", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Port, NodeAttribute.getNodeValue(), Integer.toString( ConstantsCommonClasses._Min_Port_Number ), Integer.toString( ConstantsCommonClasses._Max_Port_Number ) ) );
											break; //Stop parse more attributes
												
										}
									    
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Use_SSL ) ) {

										if ( NodeAttribute.getNodeValue().equals( "true" ) ) {
											
											if ( this.strKeyStoreFile.isEmpty() == false ) {   
											
												if ( this.strKeyStorePassword.isEmpty() == false ) {
												
													if ( this.strKeyManagerPassword.isEmpty() == false ) {

														bUseSSL = true;

													}
													else {
														
											            Logger.logError( "-1002", Lang.translate( "Cannot use SSL beacuse no [%s] defined in config file section [%s]", ConstantsCommonConfigXMLTags._Key_Manager_Password, ConstantsCommonConfigXMLTags._System ) );
											            break; //Stop parse more attributes
														
													}
												
												}
												else {
													
										            Logger.logError( "-1003", Lang.translate( "Cannot use SSL beacuse no [%s] defined in config file section [%s]", ConstantsCommonConfigXMLTags._Key_Store_Password, ConstantsCommonConfigXMLTags._System ) );
										            break; //Stop parse more attributes
													
												}
											
											}
											else {
												
									            Logger.logError( "-1004", Lang.translate( "Cannot use SSL beacuse no [%s] defined in config file section [%s]", ConstantsCommonConfigXMLTags._Key_Store_File, ConstantsCommonConfigXMLTags._System ) );
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
					        		
					        		if ( NetInterface.intPort == intPort && ( NetInterface.strIP.equals( strIP ) || NetInterface.strIP.equals( net.maindataservices.Utilities._IPV4All ) || NetInterface.strIP.equals( net.maindataservices.Utilities._IPV6All ) || strIP.equals( net.maindataservices.Utilities._IPV4All ) || strIP.equals( net.maindataservices.Utilities._IPV6All ) ) ) {

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
						        	
					            	Logger.logMessage( "1", Lang.translate( "Network interface defined and added. IP: [%s], Address_Type: [%s], Port: [%s], Use_SSL: [%s]", strIP, strAddressType, Integer.toString( intPort ), bUseSSL == true?"true":"false" ) );

                                }
                                else {
                                	
					            	Logger.logError( "-1005", Lang.translate( "The port number: [%s] for the IP address: [%s] is used in previus network interface config with IP address: [%s], cannot use the port number again", Integer.toString( intPort ), strIP, strPreviusIP ) );
                                	
                                }
				            	
					        }
					        else {
					        	
					            Logger.logError( "-1006", Lang.translate( "Network interface config attributes is not valid" ) );
					        	
					        }
						
						}
					
					}
	            
	            }
	            
	            if ( this.ConfiguredNetworkInterfaces.isEmpty() == true ) {
	            	
	    			Logger.logWarning( "-1", Lang.translate( "No valid network interface defined. Adding default" ) );        
	            	
		        	//Add default network interface config
	    			CConfigNetworkInterface NetInterface = new CConfigNetworkInterface();
		        	
		        	this.ConfiguredNetworkInterfaces.add( NetInterface );

	            }
	            
	            bResult = true;
	        
	        }
	        else {
	        	
    			Logger.logWarning( "-1", Lang.translate( "No network interface defined. Adding default" ) );        
	        	
	        	//Add default network interface config
    			CConfigNetworkInterface NetInterface = new CConfigNetworkInterface();
	        	
	        	this.ConfiguredNetworkInterfaces.add( NetInterface );
	        	
	        	bResult = true;
	        	
	        }
	        
        }
		catch ( Exception Ex ) {
			
			Logger.logException( "-1011", Ex.getMessage(), Ex );
			
		}
        
		Logger.logMessage( "1", Lang.translate( "Count of network interfaces defined: [%s]", Integer.toString( this.ConfiguredNetworkInterfaces.size() ) ) );        
        
        return bResult;

	}

	public boolean LoadConfigSectionAccessControl( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = false;
		
        try {

			String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._IP, ConstantsCommonConfigXMLTags._Context_Path, ConstantsCommonConfigXMLTags._Action };

			NodeList ConfigAccessControlList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigAccessControlList.getLength() > 0 ) {
			
	            for ( int intConfigAccessControlIndex = 0; intConfigAccessControlIndex < ConfigAccessControlList.getLength(); intConfigAccessControlIndex++ ) {
	                
	            	Node ConfigConnectionNode = ConfigAccessControlList.item( intConfigAccessControlIndex );
	                 
	    			Logger.logMessage( "1", Lang.translate( "Reading XML access control rule: [%s]", ConfigConnectionNode.getNodeName() ) );        
	                 
					if ( ConfigConnectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._From ) == true ) {
		            
						String strFromIP = "";
						String strContextPath = ConstantsCommonConfigXMLTags._Context_Path_Default;
						String strAction = "";
						boolean bAlwaysDeny = false;
						boolean bAlwaysAllow = false;
						
						if ( ConfigConnectionNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigConnectionNode.getAttributes();

					        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
					            
					        	Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
					        	
					            if ( NodeAttribute != null ) {
					            	
					            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
					            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
									
									if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._IP ) ) {

										if ( NodeAttribute.getNodeValue().isEmpty() == false ) {
											
											strFromIP = NodeAttribute.getNodeValue();
											
										}
							            else {
							            	
											Logger.logError( "-1000", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._IP ) );
											break; //Stop parse more attributes
											
							            }
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Context_Path ) ) {

										if ( NodeAttribute.getNodeValue().isEmpty() == false ) {
											
											strContextPath = NodeAttribute.getNodeValue();
										
										}
							            else {
							            	
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Context_Path, ConstantsCommonConfigXMLTags._Context_Path_Default ) );
											
							            }
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Action ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsCommonConfigXMLTags._Action_Deny ) || NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsCommonConfigXMLTags._Action_Allow ) ) {    
										 
										    strAction = NodeAttribute.getNodeValue().toLowerCase();
											bAlwaysAllow = NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsCommonConfigXMLTags._Action_Allow );
										    bAlwaysDeny = NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsCommonConfigXMLTags._Action_Deny );
										
										}
										else {
											
											Logger.logError( "-1001", Lang.translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s]", ConstantsCommonConfigXMLTags._Action, ConstantsCommonConfigXMLTags._Action_Allow, ConstantsCommonConfigXMLTags._Action_Deny ) );
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
						        	
					            	Logger.logMessage( "1", Lang.translate( "Access control rule defined and added. IP: [%s], ContextPath: [%s], Action: [%s]", strFromIP, strContextPath, strAction ) );

                                }
                                else {
                                	
					            	Logger.logError( "-1002", Lang.translate( "The IP address [%s] is used in previus access control rule number: [%s], cannot use the IP address again", strFromIP, Integer.toString( intAccessControlRuleIndex ) ) );
                                	
                                }
				            	
					        }
					        else {
					        	
					            Logger.logError( "-1003", Lang.translate( "Access control rule config attributes is not valid" ) );
					        	
					        }
						
						}    
					
					}
		            
		        }
		        
	            
	            if ( this.ConfiguredAccessControl.isEmpty() == true ) {
	            	
	    			Logger.logWarning( "-1", Lang.translate( "No valid access control rules defined" ) );        
	            	
	            }
	            
	            bResult = true;
	            
	        }
	        
        }
        catch ( Exception Ex ) {
				
			Logger.logException( "-1010", Ex.getMessage(), Ex );
				
		}

		Logger.logMessage( "1", Lang.translate( "Count of access controls rules defined: [%s]", Integer.toString( this.ConfiguredAccessControl.size() ) ) );        
						
		return bResult;
		
	}
	
	@Override
	public boolean loadConfigSection( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;
		
		Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals(  ConstantsCommonConfigXMLTags._Logger ) == true ) {
           
			if ( this.LoadConfigSectionLogger( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1001", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._System ) == true ) {
         
			if ( this.LoadConfigSectionSystem( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1002", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
    			
			}

        }
        else if ( ConfigSectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._NetworkInterfaces ) == true ) {
             
			if ( this.LoadConfigSectionNetworkInterfaces( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1003", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			}
			
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._AccessControl ) == true ) {

        	if ( this.LoadConfigSectionAccessControl( ConfigSectionNode, Lang, Logger ) == false ) {
        		
    			Logger.logError( "-1004", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
        		
        	}
             
        }

		return bResult;
		
	}
	
	@Override
	public Object sendMessage(String strMessageName, Object MessageData) {

		return "";
		
	}

}
