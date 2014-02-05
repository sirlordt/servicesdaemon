package DBReplicator;

import java.io.File;

import net.maindataservices.Utilities;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CConfigProxy;
import CommonClasses.CLanguage;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsCommonConfigXMLTags;
import ExtendedLogger.CExtendedLogger;

public class CConfigHTTPDBChannelReplicator extends CAbstractConfigLoader {

	protected static CConfigHTTPDBChannelReplicator ConfigHTTPDBChannelReplicator = null;

	public static CConfigHTTPDBChannelReplicator getConfigHTTPDBChannelReplicator( String strRunningPath ) {

		if ( ConfigHTTPDBChannelReplicator == null ) {
		
			ConfigHTTPDBChannelReplicator = new CConfigHTTPDBChannelReplicator( strRunningPath );
		
		}
		
		return ConfigHTTPDBChannelReplicator;
		
	}
	
	public int intRequestTimeout;
	public int intSocketTimeout;
	public String strURL;
	public CConfigProxy ConfigProxy;
	/*public String strProxyIP;
	public int intProxyPort;
	public String strProxyUser;
	public String strProxyPassword;*/
	public String strDatabase;
	public String strUser;
	public String strPassword;
	
	public CConfigHTTPDBChannelReplicator( String strRunningPath ) {

		super( strRunningPath );

		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._System );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		
		intRequestTimeout = ConstantsCommonClasses._Request_Timeout;
		intSocketTimeout = ConstantsCommonClasses._Socket_Timeout;
		strURL = "";
		ConfigProxy = new CConfigProxy();
		strDatabase = "";
		strUser = "";
		strPassword = "";
		
	}
	
	public boolean loadConfigSectionSystem( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

        boolean bResult = true;
		
        try {
	
			if ( ConfigSectionNode.hasAttributes() == true ) {

				String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._URL, ConstantsCommonConfigXMLTags._Proxy_IP, ConstantsCommonConfigXMLTags._Proxy_Port, ConstantsCommonConfigXMLTags._Proxy_User, ConstantsCommonConfigXMLTags._Proxy_Password, ConstantsCommonConfigXMLTags._Database, ConstantsCommonConfigXMLTags._User, ConstantsCommonConfigXMLTags._Password };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
		            	if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Request_Timeout ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			int intTmpRequestTimeout = net.maindataservices.Utilities.strToInteger( NodeAttribute.getNodeValue().trim() );

		            			if ( intTmpRequestTimeout >= ConstantsCommonClasses._Minimal_Request_Timeout && intTmpRequestTimeout <= ConstantsCommonClasses._Maximal_Request_Timeout ) {

			            			intRequestTimeout = intTmpRequestTimeout;

		            			}
		            			else {

									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( intTmpRequestTimeout ), Integer.toString( ConstantsCommonClasses._Minimal_Request_Timeout ), Integer.toString( ConstantsCommonClasses._Maximal_Request_Timeout ) ) );
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( intTmpRequestTimeout ), Integer.toString( ConstantsCommonClasses._Request_Timeout ) ) );
		            				bResult = false;
		            				break;

		            			}
		            			
		            		}
		            		else {

		            			Logger.logError( "-1002", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Request_Timeout ) );
		            			bResult = false;
		            			break;

		            		}

		            	}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Socket_Timeout ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			int intTmpSocketTimeout = net.maindataservices.Utilities.strToInteger( NodeAttribute.getNodeValue().trim() );

		            			if ( intTmpSocketTimeout >= ConstantsCommonClasses._Minimal_Socket_Timeout && intTmpSocketTimeout <= ConstantsCommonClasses._Maximal_Socket_Timeout ) {

		            				intSocketTimeout = intTmpSocketTimeout;

		            			}
		            			else {

									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( intTmpSocketTimeout ), Integer.toString( ConstantsCommonClasses._Minimal_Socket_Timeout ), Integer.toString( ConstantsCommonClasses._Maximal_Socket_Timeout ) ) );
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( intTmpSocketTimeout ), Integer.toString( ConstantsCommonClasses._Socket_Timeout ) ) );
		            				bResult = false;
		            				break;

		            			}

		            		}
		            		else {

		            			Logger.logError( "-1003", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Socket_Timeout ) );
	            				bResult = false;
	            				break;

		            		}

		            	}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._URL ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			if ( net.maindataservices.Utilities.isValidHTTPURL( NodeAttribute.getNodeValue().trim() ) ) {
		            			
		            				strURL = NodeAttribute.getNodeValue().trim();
		            			
		            			}
		            			else {
		            				
			            			Logger.logError( "-1005", Lang.translate( "The [%s] attribute value [%s] is not valid URL address", ConstantsCommonConfigXMLTags._URL, NodeAttribute.getNodeValue().trim() ) );
			            			bResult = false;
			            			break;
		            				
		            			}

		            		}
		            		else {

		            			Logger.logError( "-1004", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._URL ) );
		            			bResult = false;
		            			break;

		            		}

		            	}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_IP ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			if ( Utilities.isValidIP( NodeAttribute.getNodeValue() ) == true ) {

		            				ConfigProxy.strProxyIP = NodeAttribute.getNodeValue();

		            			}
		            			else {

		            				Logger.logError( "-1006", Lang.translate( "The [%s] attribute value [%s] is not valid ip address", CommonClasses.ConstantsCommonConfigXMLTags._IP, NodeAttribute.getNodeValue() ) );
		            				bResult = false;
		            				break; //Stop parse more attributes

		            			}

		            		}
		            		else {

		            			Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_IP ) );

		            		}

		            	}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_Port ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			int intTmpPort = Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );

		            			if ( intTmpPort >= ConstantsCommonClasses._Min_Port_Number && intTmpPort <= ConstantsCommonClasses._Max_Port_Number ) {

		            				ConfigProxy.intProxyPort = intTmpPort;

		            			}
		            			else {

		            				Logger.logError( "-1007", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", CommonClasses.ConstantsCommonConfigXMLTags._Proxy_Port, NodeAttribute.getNodeValue(), Integer.toString( ConstantsCommonClasses._Min_Port_Number ), Integer.toString( ConstantsCommonClasses._Max_Port_Number ) ) );
		            				bResult = false;
		            				break; //Stop parse more attributes

		            			}

		            		}
		            		else {

		            			Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_Port ) );

		            		}

		            	}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_User ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			ConfigProxy.strProxyUser = NodeAttribute.getNodeValue().trim();

		            		}
		            		else {

		            			Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_User ) );

		            		}

		            	}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_Password ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			ConfigProxy.strProxyPassword = NodeAttribute.getNodeValue().trim();

		            		}
		            		else {

		            			Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_Password ) );

		            		}

		            	}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Database ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			strDatabase = NodeAttribute.getNodeValue().trim();

		            		}
		            		else {

		            			Logger.logError( "-1008", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Database ) );
	            				bResult = false;
		            			break; //Stop parse more attributes

		            		}

		            	}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._User ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			strUser = NodeAttribute.getNodeValue().trim();

		            		}
		            		else {

		            			Logger.logError( "-1009", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._User ) );
	            				bResult = false;
		            			break; //Stop parse more attributes

		            		}

		            	}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Password ) ) {

		            		if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

		            			strPassword = net.maindataservices.Utilities.uncryptString( ConstantsCommonConfigXMLTags._Password_Crypted, ConstantsCommonConfigXMLTags._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, NodeAttribute.getNodeValue(), Logger, Lang );

		            		}
		            		else {

		            			Logger.logError( "-1010", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Password ) );
	            				bResult = false;
		            			break; //Stop parse more attributes

		            		}

		            	}

		            }
					else {

						Logger.logError( "-1001", Lang.translate( "The [%s] attribute not found, for the node [%s]", strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );
        				bResult = false;
						break;

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
	
	@Override
	public boolean loadConfigSection( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = true;

		Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals(  ConstantsCommonConfigXMLTags._System ) == true ) {
	           
			if ( this.loadConfigSectionSystem( ConfigSectionNode, Logger, Lang ) == false ) {
				
    			Logger.logError( "-1001", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }

		return bResult;
		
	}
	
	@Override
	public boolean loadConfig( String strConfigFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
        this.Logger = Logger;

        this.Lang = Lang;
        
        if ( new File( strConfigFilePath ).isAbsolute() == false )
        	return super.loadConfig( strRunningPath + strConfigFilePath, Logger, Lang );
        else
        	return super.loadConfig( strConfigFilePath, Logger, Lang );
	
	}

	@Override
	public Object sendMessage(String strMessageName, Object MessageData) {

		return "";
		
	}

}
