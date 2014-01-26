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
package SystemExecuteSQL;

import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CExpresionFilter;
import CommonClasses.CExpresionsFilters;
import CommonClasses.CLanguage;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.ConstantsCommonConfigXMLTags;
import ExtendedLogger.CExtendedLogger;

public class CConfigSystemExecuteSQL extends CAbstractConfigLoader {

	protected static CConfigSystemExecuteSQL SystemExecuteSQLConfig = null;

	public static CConfigSystemExecuteSQL getSystemExecuteSQLConfig( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig, String strRunningPath ) {

		if ( SystemExecuteSQLConfig == null ) {
		
			SystemExecuteSQLConfig = new CConfigSystemExecuteSQL( strRunningPath );
		
		}
		
		SystemExecuteSQLConfig.OwnerConfig = OwnerConfig;

		SystemExecuteSQLConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemExecuteSQLConfig;
		
	}
	
	public CAbstractConfigLoader OwnerConfig = null;
	public CConfigServicesDaemon ServicesDaemonConfig = null;
	
	public CExtendedLogger ServiceLogger =  null;
	public CLanguage ServiceLang = null;
	
    public boolean bLogSQLStatement = false;
	
	//public String strPreExecuteDir = "";
	//public String strPostExecuteDir = "";
	
	public ArrayList<CExpresionsFilters> ExpressionsFilters = null;
	
	public CConfigSystemExecuteSQL( String strRunningPath ) {
		
		super( strRunningPath );
		
		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._Logger );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		//strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsServicesDaemon._System );  //
		//bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsConfigXMLTags._Filters );  //2
		bFirstLevelConfigSectionsMustExists.add( true );
		
		this.bLogSQLStatement = ConstantsService._Log_SQL_Statement;
		
		//this.strPreExecuteDir = DefaultConstantsSystemExecuteSQL.strDefaultPreExecuteDir;
		//this.strPostExecuteDir = DefaultConstantsSystemExecuteSQL.strDefaultPostExecuteDir;
		
		this.ExpressionsFilters = new ArrayList<CExpresionsFilters>();
		
	}
	
	public boolean loadConfigSectionLogger( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
	
			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConstantsConfigXMLTags._LogSQL_Statement };
				
				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._LogSQL_Statement ) ) {

							this.bLogSQLStatement = NodeAttribute.getNodeValue().toLowerCase().equals( "true" )?true:false;
	
				            Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "bLogSQLStatement", NodeAttribute.getNodeValue() ) );

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

	public boolean loadConfigSectionSystem( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
	
			/*if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConfigXMLTagsSystemExecuteSQL._PreExecute_Dir, ConfigXMLTagsSystemExecuteSQL._PostExecute_Dir };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
		            	if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemExecuteSQL._PreExecute_Dir ) ) {

							this.strPreExecuteDir = NodeAttribute.getNodeValue();
		
					        if ( this.strPreExecuteDir.isEmpty() == false && new File( this.strPreExecuteDir ).isAbsolute() == false ) {
					        
                               this.strPreExecuteDir = DefaultConstantsServicesDaemon.strDefaultRunningPath + this.strPreExecuteDir;
					        	
					        }
							
							Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strPreExecuteDir", this.strPreExecuteDir ) );
				        
					        if ( Utilities.CheckDir( this.strPreExecuteDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
		            	else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemExecuteSQL._PostExecute_Dir ) ) {

							this.strPostExecuteDir = NodeAttribute.getNodeValue();
		
					        if ( this.strPostExecuteDir.isEmpty() == false && new File( this.strPostExecuteDir ).isAbsolute() == false ) {
					        
                               this.strPostExecuteDir = DefaultConstantsServicesDaemon.strDefaultRunningPath + this.strPostExecuteDir;
					        	
					        }
							
							Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "strPostExecuteDir", this.strPostExecuteDir ) );
				        
					        if ( Utilities.CheckDir( this.strPostExecuteDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
		            
		            }
		            
		        }
			
			}*/
        
        }
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;
	
	}    
	
	public CExpresionsFilters loadConfigFilter( Node ConfigSectionNode, String strFiltersName, String strFiltersType, CLanguage Lang, CExtendedLogger Logger ) {
		
		CExpresionsFilters ExpFilters = new CExpresionsFilters();
		
        try {

    		ExpFilters.strName = strFiltersName;
    		ExpFilters.strType = strFiltersType;		        		
		
    		NodeList ConfigFiltersList = ConfigSectionNode.getChildNodes();

    		if ( ConfigFiltersList.getLength() > 0 ) {

    			for ( int intConfigFilterIndex = 0; intConfigFilterIndex < ConfigFiltersList.getLength(); intConfigFilterIndex++ ) {

    				Node ConfigFilterNode = ConfigFiltersList.item( intConfigFilterIndex );

    				Logger.logMessage( "1", Lang.translate( "Reading XML node filter: [%s]", ConfigFilterNode.getNodeName() ) );        

    				if ( ConfigFilterNode.getNodeName().equals( ConstantsConfigXMLTags._Filter ) == true ) {

    					if ( ConfigFilterNode.hasAttributes() == true ) {

    						String strAttributesOrder[] = { ConstantsConfigXMLTags._Type };

    						String strType = "";
    						String strExpression = "";

    						if ( ConfigFilterNode.getNodeValue() != null && ConfigFilterNode.getNodeValue().isEmpty() == false ) {

    							NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

    							strExpression = ConfigFilterNode.getNodeValue();

    							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

    								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

    								if ( NodeAttribute != null ) {

    									Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
    									Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

    									if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Type ) ) {

    										if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

    											if ( NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsConfigXMLTags._Type_Allow ) || NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsConfigXMLTags._Type_Block ) ) {

    												strType = NodeAttribute.getNodeValue().toLowerCase();

    												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", strAttributesOrder[ intAttributesIndex ], strType ) );

    											}
    											else {

    												Logger.logError( "1004", Lang.translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s,%s]", strAttributesOrder[ intAttributesIndex ], NodeAttribute.getNodeValue(), ConstantsConfigXMLTags._Type_Exact, ConstantsConfigXMLTags._Type_Partial, ConstantsConfigXMLTags._Type_RExp ) );

    											}

    										}
    										else {

    											Logger.logError( "1003", Lang.translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConfigFilterNode.getNodeName(), Integer.toString( intConfigFilterIndex ) ) );

    											break;

    										}

    									}

    								}
    								else {

    									Logger.logError( "1002", Lang.translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConfigFilterNode.getNodeName(), Integer.toString( intConfigFilterIndex ) ) );

    									break;

    								}

    							}

    							if ( strType.isEmpty() == false && strExpression.isEmpty() == false ) {

    								CExpresionFilter ExpFilter = new CExpresionFilter();
    								ExpFilter.strType = strType;
    								ExpFilter.strExpression = strExpression;

    								ExpFilters.Filters.add( ExpFilter );
    								
    							}

    						}
    						else {

    							Logger.logError( "1001", Lang.translate( "The [%s] node value cannot empty string, at relative index [%s], ignoring the node", ConfigFilterNode.getNodeName(), Integer.toString( intConfigFilterIndex ) ) );

    						}
    					
    					}

    				}

    			}

    		}
        	
        
        }
		catch ( Exception Ex ) {
			
			Logger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return ExpFilters;
		
	}
	
	public boolean loadConfigSectionFilters( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
	
			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConstantsConfigXMLTags._Name, ConstantsConfigXMLTags._Type };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

				String strName = "";
				String strType = "";
				
				for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
		            	if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Name ) ) {

							strName = NodeAttribute.getNodeValue();
		
					        if ( strName.isEmpty() == false ) {
					        
								Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], strName ) );
					        	
					        }
					        else {
					        	
								Logger.logError( "1002", Lang.translate( "The [%s] attribute cannot empty string, for the section [%s], ignoring the section", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );
					        	
								break;
								
					        }
							
						}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Type ) ) {

					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {
					        
								if ( NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsConfigXMLTags._Type_Allow ) || NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsConfigXMLTags._Type_Block ) ) {
					        	
									strType = NodeAttribute.getNodeValue().toLowerCase();

									Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], strType ) );
								
								}
								else {
									
									Logger.logError( "1004", Lang.translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s]", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], NodeAttribute.getNodeValue(), ConstantsConfigXMLTags._Type_Allow, ConstantsConfigXMLTags._Type_Block ) );
									
								}
								
								
					        }
					        else {
					        	
								Logger.logError( "1003", Lang.translate( "The [%s] attribute cannot empty string, for the section [%s], ignoring the section", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );

								break;
					        	
					        }
						
						}
		            
		            }
		            else {

		            	Logger.logError( "1001", Lang.translate( "The [%s] attribute not found, for the section [%s], ignoring the section", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );
		            	
						break;
						
		            }
		            
		        }
				
				if ( strName.isEmpty() == false && strType.isEmpty() == false ) {
					
		        	CExpresionsFilters ExpFilters = CExpresionsFilters.getExpresionsFiltersByName( this.ExpressionsFilters, strName );
		        	
		        	if ( ExpFilters == null ) { //dont exits another filter name
		        		
		        		ExpFilters = this.loadConfigFilter(ConfigSectionNode, strName, strType, Lang, Logger );
		        		
		        		if ( ExpFilters.Filters.size() > 0 ) {
		        			
							Logger.logMessage( "1", Lang.translate( "Count of registered filters [%s], for filters name [%s] section [%s]", Integer.toString( ExpFilters.Filters.size() ), strName, ConfigSectionNode.getNodeName() ) );

		        			this.ExpressionsFilters.add( ExpFilters );
		        			
		        		}
		        		else {
		        			
							Logger.logError( "1006", Lang.translate( "Cannot add filters name [%s], the filters list is empty, for the section [%s]", strName, ConfigSectionNode.getNodeName() ) );
		        			
		        		}
		        	
		        	}
		        	else {
		        	
						Logger.logError( "1005", Lang.translate( "Duplicate filters name [%s], for the section [%s]", strName, ConfigSectionNode.getNodeName() ) );
		        		
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
	public boolean loadConfigSection( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;

		Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals(  ConstantsCommonConfigXMLTags._Logger ) == true ) {
	           
			if ( this.loadConfigSectionLogger( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1001", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		else if ( ConfigSectionNode.getNodeName().equals(  CommonClasses.ConstantsCommonConfigXMLTags._System ) == true ) {
           
			if ( this.loadConfigSectionSystem( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1002", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		else if ( ConfigSectionNode.getNodeName().equals(  ConstantsConfigXMLTags._Filters ) == true ) {
	           
			if ( this.loadConfigSectionFilters( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1003", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		
		return bResult;
		
	}

	@Override
	public boolean loadConfig( String strConfigFilePath, CLanguage Lang, CExtendedLogger Logger ) {
		
        if ( ServiceLogger == null )
        	ServiceLogger = Logger;

        if ( ServiceLang == null )
        	ServiceLang = Lang;
        
	    return super.loadConfig( strConfigFilePath, Lang, Logger );
	
	}
	
	@Override
	public Object sendMessage(String strMessageName, Object MessageData) {

		return "";
		
	}

}
