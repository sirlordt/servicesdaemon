package MemcachedCEngine;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.ConstantsCommonConfigXMLTags;
import ExtendedLogger.CExtendedLogger;

public class CConfigMemcachedCEngine extends CAbstractConfigLoader {

	String strProtocol;
	String strServersList;
	String strUser;
	String strPassword;
	
	public CConfigMemcachedCEngine( String strRunningPath ) {
		
		super( strRunningPath );

		this.strProtocol = ConstantsConfigXMLTags._Binary;
		
		this.strUser = "";
		this.strPassword = "";
		
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._System );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		
	}

    public boolean loadConfigSectionSystem( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
		
			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConstantsConfigXMLTags._Protocol, ConstantsConfigXMLTags._Servers_List, ConstantsConfigXMLTags._User, ConstantsConfigXMLTags._Password };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Protocol ) ) {
			            	
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) { 
								
								if ( NodeAttribute.getNodeValue().trim().equalsIgnoreCase( ConstantsConfigXMLTags._Binary ) == false ) {

									this.strProtocol = ConstantsConfigXMLTags._Other;
								
								}
								
						        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strProtocol", this.strProtocol ) );
								
							}
							else {
								
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsConfigXMLTags._Protocol, ConstantsConfigXMLTags._Binary ) );
								
							}
							
			            }
						else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Servers_List ) ) {
			            	
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) { 
								
								strServersList = NodeAttribute.getNodeValue().trim();
								
						        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strServersList", this.strServersList ) );
								
							}
							else {
								
						        Logger.logError( "-1002", Lang.translate( "The [%s] attribute is empty for the node [%s]", ConstantsConfigXMLTags._Servers_List, ConfigSectionNode.getNodeName() ) );

						        bResult = false;
						        
						        break;
						        
							}
							
							
			            }
						else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._User ) ) {
			            	
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) { 

								this.strUser = NodeAttribute.getNodeValue().trim();
								
						        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strUser", this.strUser ) );
								
							}
							else {
								
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty for the node [%s]", ConstantsConfigXMLTags._User, ConfigSectionNode.getNodeName() ) );
								
							}
							
			            }
						else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Password ) ) {

							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) { 

								this.strPassword = NodeAttribute.getNodeValue().trim();
								
						        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strPassword", this.strPassword ) );
								
							}
							else {
								
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty for the node [%s]", ConstantsConfigXMLTags._Password, ConfigSectionNode.getNodeName() ) );
								
							}
							
			            }
			            
			        }
	            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsConfigXMLTags._Protocol ) ) {
            		
				        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsConfigXMLTags._Protocol, ConstantsConfigXMLTags._Binary ) );
	            		
	            	}
	            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsConfigXMLTags._Servers_List ) ) {
	            		
				        Logger.logError( "-1001", Lang.translate( "The [%s] attribute not found, for the node [%s]", ConstantsConfigXMLTags._Servers_List, ConfigSectionNode.getNodeName() ) );
	            		
				        bResult = false;
				        
				        break;
				        
	            	}
	            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsConfigXMLTags._User ) ) {
	            		
				        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsConfigXMLTags._User, "" ) );
	            		
	            	}
	            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsConfigXMLTags._Password ) ) {
	            		
				        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsConfigXMLTags._Password, "" ) );
	            		
	            	}
				
				}
			
			}   
        
        }
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.logException( "-1020", Ex.getMessage(), Ex );
			
		}
        
        return bResult;

	}
	
	@Override
	public boolean loadConfigSection( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = true;

		Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._System ) == true ) {
           
			if ( this.loadConfigSectionSystem( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1001", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		
		return bResult;
		
	}
	
	@Override
	public Object sendMessage(String strMessageName, Object MessageData) {

		return null;
		
	}

}
