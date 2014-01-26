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

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ExtendedLogger.CExtendedLogger;

public abstract class CAbstractConfigLoader implements IMessageObject {

	protected ArrayList<String> strFirstLevelConfigSectionsOrder = null;
	protected ArrayList<Boolean> bFirstLevelConfigSectionsMustExists = null;
	protected String strConfigFilePath = "";
	
	public CExtendedLogger Logger =  null;
	public CLanguage Lang = null;

	public String strRunningPath = "";
	
	public CAbstractConfigLoader( String strRunningPath ) {

		this.strRunningPath = strRunningPath;
		
		strFirstLevelConfigSectionsOrder = new ArrayList<String>();
		bFirstLevelConfigSectionsMustExists = new ArrayList<Boolean>();
		
	}
	
	public String getRunningPath() {
		
		return strRunningPath;
		
	} 
	
	public String getConfigFilePath() {
		
		return this.strConfigFilePath;
		
	}
	
	public boolean loadConfigSection( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;

		return bResult;
		
	}
	
	public Node findConfigSectionByName( NodeList ConfigSectionList, String strConfigSectionName, CExtendedLogger Logger ) {
		
		Node ConfigSectionNode = null;
		
		try {
        
			for ( int intConfigSecitonIndex = 0; intConfigSecitonIndex < ConfigSectionList.getLength(); intConfigSecitonIndex++ ) {

				if ( ConfigSectionList.item( intConfigSecitonIndex ).getNodeName().equals( strConfigSectionName ) ) {

					ConfigSectionNode = ConfigSectionList.item( intConfigSecitonIndex );

					break;

				}

			}	

		}
		catch ( Exception Ex ) {
			
			Logger.logException( "-1001", Ex.getMessage(), Ex );        
			
		}
		
		return ConfigSectionNode;
		
	}
	
	public boolean loadConfig( String strConfigFilePath, CLanguage Lang, CExtendedLogger Logger ) {
		
		boolean bResult = true;

		if ( this.Logger == null )
			this.Logger = Logger;
		
		if ( this.Lang == null )
			this.Lang = Lang;
			
		Logger.logMessage( "1", Lang.translate( "Loading config from file: [%s]", strConfigFilePath  ) );        

		File XMLConfigFile = new File( strConfigFilePath );
		
		boolean bFileExists = XMLConfigFile.exists();
		boolean bFileCanRead = XMLConfigFile.canRead();
		boolean bIsFile = XMLConfigFile.isFile();
		
        if ( bFileExists == true && bFileCanRead == true && bIsFile == true ) { 
   
	        try {
			
				DocumentBuilderFactory XMLDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder XMLDocumentBuilder = XMLDocumentBuilderFactory.newDocumentBuilder();
				Document XMLDocument = XMLDocumentBuilder.parse( XMLConfigFile );
				XMLDocument.getDocumentElement().normalize();
	
				Logger.logMessage( "1", Lang.translate( "XML node root of config file: [%s]", XMLDocument.getDocumentElement().getNodeName() ) );        
	
		        NodeList NodeRootlst = XMLDocument.getElementsByTagName( ConstantsCommonConfigXMLTags._Config );
		          
		        if ( NodeRootlst.getLength() == 1 ) {
		           
		        	NodeList ConfigSectionList = NodeRootlst.item( 0 ).getChildNodes();
				
		            for ( int intConfigSecitonIndex = 0; intConfigSecitonIndex < strFirstLevelConfigSectionsOrder.size(); intConfigSecitonIndex++ ) {
		                
		            	Node ConfigSectionNode = findConfigSectionByName( ConfigSectionList, strFirstLevelConfigSectionsOrder.get( intConfigSecitonIndex ), Logger ); //ConfigSectionList.item( intConfigSecitonIndex );

		            	if ( ConfigSectionNode != null ) {
		            		
		            		if ( loadConfigSection( ConfigSectionNode, Lang, Logger ) == false ) {

		            			bResult = false;

		            			break;

		            		}
		            		
		            	}
		            	else if ( bFirstLevelConfigSectionsMustExists.size() >= intConfigSecitonIndex && bFirstLevelConfigSectionsMustExists.get( intConfigSecitonIndex ) == true ) {

		            		Logger.logError( "-1002", Lang.translate( "The config section [%s] not found", strFirstLevelConfigSectionsOrder.get( intConfigSecitonIndex ) ) );        

		            		bResult = false;

		            		break;

		            	}
		            	else  {

		            		Logger.logWarning( "-1", Lang.translate( "The config section [%s] not found", strFirstLevelConfigSectionsOrder.get( intConfigSecitonIndex ) ) );        

		            	}
		                 
		            } 
		            
		            if ( bResult == true ) {
		            	
		            	this.strConfigFilePath = strConfigFilePath;
		            	
		            }
				
				}
		        else {
		        	
					//Hard coded error
	    			Logger.logError( "-1001", Lang.translate( "Only one section [Config] tag allowed in config file in the path [%s]",  XMLConfigFile.getAbsolutePath() ) );        
		        
	    			bResult = false;
	    			
		        };
				
			} 
			catch ( Exception Ex ) {
			
    			Logger.logException( "-1010", Ex.getMessage(), Ex );        
				
    			bResult = false;
    			
			}
			
		
		}
		else if ( bFileExists == false ) {
			
			//Hard coded error
			Logger.logError( "-1003", Lang.translate( "The config file in the path [%s] not exists", XMLConfigFile.getAbsolutePath() ) );        
		
			bResult = false;
			
		}
		else {
			
		    if ( bFileCanRead == false ) {

				//Hard coded error
				Logger.logError( "-1004", Lang.translate( "The config file in the path [%s] cannot read, please check the owner and permissions", XMLConfigFile.getAbsolutePath() ) );        

				bResult = false;

		    }
		    else if ( bIsFile == false ) {
		    	
				//Hard coded error
				Logger.logError( "-1005", Lang.translate( "The config path [%s] not is file", XMLConfigFile.getAbsolutePath() ) );        
		    	
				bResult = false;

		    }
			
		}
		
		return bResult;
		
	}

	/*
	public String getConfigValue( String strConfigKey ) {
		
		return "";
		
	}
	
	public Object getConfigObjectValue( String strConfigObjectType, String strConfigKey ) {
		
		return null;
		
	}*/
	
}
