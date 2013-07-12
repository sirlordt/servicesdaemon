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
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import DBServicesManager.CDBServicesManagerConfig;
import ExtendedLogger.CExtendedLogger;

public class CSystemExecuteSQLConfig extends CAbstractConfigLoader {

	protected static CSystemExecuteSQLConfig SystemExecuteSQLConfig = null;

	static {
		
		SystemExecuteSQLConfig = new CSystemExecuteSQLConfig();
		
	} 

	public static CSystemExecuteSQLConfig getSystemExecuteSQLConfig( CServicesDaemonConfig ServicesDaemonConfig, CDBServicesManagerConfig DBServicesManagerConfig ) {
		
		SystemExecuteSQLConfig.DBServicesManagerConfig = DBServicesManagerConfig;

		SystemExecuteSQLConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemExecuteSQLConfig;
		
	}
	
	public CDBServicesManagerConfig DBServicesManagerConfig = null;
	public CServicesDaemonConfig ServicesDaemonConfig = null;
	
	public CExtendedLogger ServiceLogger =  null;
	public CLanguage ServiceLang = null;
	
    public boolean bLogSQLStatement = false;
	
	//public String strPreExecuteDir = "";
	//public String strPostExecuteDir = "";
	
	public ArrayList<CExpresionsFilters> ExpressionsFilters = null;
	
	public CSystemExecuteSQLConfig() {
		
		super();
		
		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsServicesDaemon._Logger );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		//strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsServicesDaemon._System );  //
		//bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsSystemExecuteSQL._Filters );  //2
		bFirstLevelConfigSectionsMustExists.add( true );
		
		this.bLogSQLStatement = DefaultConstantsSystemExecuteSQL.bDefaultLogSQLStatement;
		
		//this.strPreExecuteDir = DefaultConstantsSystemExecuteSQL.strDefaultPreExecuteDir;
		//this.strPostExecuteDir = DefaultConstantsSystemExecuteSQL.strDefaultPostExecuteDir;
		
		this.ExpressionsFilters = new ArrayList<CExpresionsFilters>();
		
	}
	
	public boolean LoadConfigSectionLogger( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
	
			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConfigXMLTagsSystemExecuteSQL._LogSQL_Statement };
				
				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemExecuteSQL._LogSQL_Statement ) ) {

							this.bLogSQLStatement = NodeAttribute.getNodeValue().toLowerCase().equals( "true" )?true:false;
	
				            Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "bLogSQLStatement", NodeAttribute.getNodeValue() ) );

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

	public boolean LoadConfigSectionSystem( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

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
        	
			Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;
	
	}    
	
	public CExpresionsFilters LoadConfigFilter( Node ConfigSectionNode, String strFiltersName, String strFiltersType, CLanguage Lang, CExtendedLogger Logger ) {
		
		CExpresionsFilters ExpFilters = new CExpresionsFilters();
		
        try {

    		ExpFilters.strName = strFiltersName;
    		ExpFilters.strType = strFiltersType;		        		
		
    		NodeList ConfigFiltersList = ConfigSectionNode.getChildNodes();

    		if ( ConfigFiltersList.getLength() > 0 ) {

    			for ( int intConfigFilterIndex = 0; intConfigFilterIndex < ConfigFiltersList.getLength(); intConfigFilterIndex++ ) {

    				Node ConfigFilterNode = ConfigFiltersList.item( intConfigFilterIndex );

    				Logger.LogMessage( "1", Lang.Translate( "Reading XML node filter: [%s]", ConfigFilterNode.getNodeName() ) );        

    				if ( ConfigFilterNode.getNodeName().equals( ConfigXMLTagsSystemExecuteSQL._Filter ) == true ) {

    					if ( ConfigFilterNode.hasAttributes() == true ) {

    						String strAttributesOrder[] = { ConfigXMLTagsSystemExecuteSQL._Type };

    						String strType = "";
    						String strExpression = "";

    						if ( ConfigFilterNode.getNodeValue() != null && ConfigFilterNode.getNodeValue().isEmpty() == false ) {

    							NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

    							strExpression = ConfigFilterNode.getNodeValue();

    							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

    								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

    								if ( NodeAttribute != null ) {

    									Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
    									Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

    									if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemExecuteSQL._Type ) ) {

    										if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

    											if ( NodeAttribute.getNodeValue().toLowerCase().equals( ConfigXMLTagsSystemExecuteSQL._Type_Allow ) || NodeAttribute.getNodeValue().toLowerCase().equals( ConfigXMLTagsSystemExecuteSQL._Type_Block ) ) {

    												strType = NodeAttribute.getNodeValue().toLowerCase();

    												Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", strAttributesOrder[ intAttributesIndex ], strType ) );

    											}
    											else {

    												Logger.LogError( "1004", Lang.Translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s,%s]", strAttributesOrder[ intAttributesIndex ], NodeAttribute.getNodeValue(), ConfigXMLTagsSystemExecuteSQL._Type_Exact, ConfigXMLTagsSystemExecuteSQL._Type_Partial, ConfigXMLTagsSystemExecuteSQL._Type_RExp ) );

    											}

    										}
    										else {

    											Logger.LogError( "1003", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConfigFilterNode.getNodeName(), Integer.toString( intConfigFilterIndex ) ) );

    											break;

    										}

    									}

    								}
    								else {

    									Logger.LogError( "1002", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConfigFilterNode.getNodeName(), Integer.toString( intConfigFilterIndex ) ) );

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

    							Logger.LogError( "1001", Lang.Translate( "The [%s] node value cannot empty string, at relative index [%s], ignoring the node", ConfigFilterNode.getNodeName(), Integer.toString( intConfigFilterIndex ) ) );

    						}
    					
    					}

    				}

    			}

    		}
        	
        
        }
		catch ( Exception Ex ) {
			
			Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return ExpFilters;
		
	}
	
	public boolean LoadConfigSectionFilters( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
	
			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConfigXMLTagsSystemExecuteSQL._Name, ConfigXMLTagsSystemExecuteSQL._Type };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

				String strName = "";
				String strType = "";
				
				for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
		            	if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemExecuteSQL._Name ) ) {

							strName = NodeAttribute.getNodeValue();
		
					        if ( strName.isEmpty() == false ) {
					        
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], strName ) );
					        	
					        }
					        else {
					        	
								Logger.LogError( "1002", Lang.Translate( "The [%s] attribute cannot empty string, for the section [%s], ignoring the section", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );
					        	
								break;
								
					        }
							
						}
		            	else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemExecuteSQL._Type ) ) {

					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {
					        
								if ( NodeAttribute.getNodeValue().toLowerCase().equals( ConfigXMLTagsSystemExecuteSQL._Type_Allow ) || NodeAttribute.getNodeValue().toLowerCase().equals( ConfigXMLTagsSystemExecuteSQL._Type_Block ) ) {
					        	
									strType = NodeAttribute.getNodeValue().toLowerCase();

									Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], strType ) );
								
								}
								else {
									
									Logger.LogError( "1004", Lang.Translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s]", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], NodeAttribute.getNodeValue(), ConfigXMLTagsSystemExecuteSQL._Type_Allow, ConfigXMLTagsSystemExecuteSQL._Type_Block ) );
									
								}
								
								
					        }
					        else {
					        	
								Logger.LogError( "1003", Lang.Translate( "The [%s] attribute cannot empty string, for the section [%s], ignoring the section", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );

								break;
					        	
					        }
						
						}
		            
		            }
		            else {

		            	Logger.LogError( "1001", Lang.Translate( "The [%s] attribute not found, for the section [%s], ignoring the section", "CExpressionsFilters." + strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );
		            	
						break;
						
		            }
		            
		        }
				
				if ( strName.isEmpty() == false && strType.isEmpty() == false ) {
					
		        	CExpresionsFilters ExpFilters = CExpresionsFilters.getExpresionsFiltersByName( this.ExpressionsFilters, strName );
		        	
		        	if ( ExpFilters == null ) { //dont exits another filter name
		        		
		        		ExpFilters = this.LoadConfigFilter(ConfigSectionNode, strName, strType, Lang, Logger );
		        		
		        		if ( ExpFilters.Filters.size() > 0 ) {
		        			
							Logger.LogMessage( "1", Lang.Translate( "Count of registered filters [%s], for filters name [%s] section [%s]", Integer.toString( ExpFilters.Filters.size() ), strName, ConfigSectionNode.getNodeName() ) );

		        			this.ExpressionsFilters.add( ExpFilters );
		        			
		        		}
		        		else {
		        			
							Logger.LogError( "1006", Lang.Translate( "Cannot add filters name [%s], the filters list is empty, for the section [%s]", strName, ConfigSectionNode.getNodeName() ) );
		        			
		        		}
		        	
		        	}
		        	else {
		        	
						Logger.LogError( "1005", Lang.Translate( "Duplicate filters name [%s], for the section [%s]", strName, ConfigSectionNode.getNodeName() ) );
		        		
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

	public boolean LoadConfigSection( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;

		Logger.LogMessage( "1", Lang.Translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals(  ConfigXMLTagsServicesDaemon._Logger ) == true ) {
	           
			if ( this.LoadConfigSectionLogger( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.LogError( "-1001", Lang.Translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		else if ( ConfigSectionNode.getNodeName().equals(  CommonClasses.ConfigXMLTagsServicesDaemon._System ) == true ) {
           
			if ( this.LoadConfigSectionSystem( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.LogError( "-1002", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		else if ( ConfigSectionNode.getNodeName().equals(  ConfigXMLTagsSystemExecuteSQL._Filters ) == true ) {
	           
			if ( this.LoadConfigSectionFilters( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.LogError( "-1003", Lang.Translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		
		return bResult;
		
	}

	public boolean LoadConfig( String strConfigFilePath, CLanguage Lang, CExtendedLogger Logger ) {
		
        if ( ServiceLogger == null )
        	ServiceLogger = Logger;

        if ( ServiceLang == null )
        	ServiceLang = Lang;
        
	    return super.LoadConfig( strConfigFilePath, Lang, Logger );
	
	}

}
