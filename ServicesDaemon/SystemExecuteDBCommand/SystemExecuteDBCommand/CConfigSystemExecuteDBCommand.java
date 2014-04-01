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
package SystemExecuteDBCommand;

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
import DBReplicator.CMasterDBReplicator;
import DBReplicator.IDBChannelReplicator;
import DBReplicator.IDBReplicator;
import ExtendedLogger.CExtendedLogger;

public class CConfigSystemExecuteDBCommand extends CAbstractConfigLoader {

	protected static CConfigSystemExecuteDBCommand SystemExecuteSQLConfig = null;

	public static CConfigSystemExecuteDBCommand getConfigSystemExecuteSQL( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig, String strRunningPath ) {

		if ( SystemExecuteSQLConfig == null ) {
		
			SystemExecuteSQLConfig = new CConfigSystemExecuteDBCommand( strRunningPath );
		
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
	
	public CConfigSystemExecuteDBCommand( String strRunningPath ) {
		
		super( strRunningPath );
		
		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._Logger );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._Filters );  //2
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsConfigXMLTags._Replicators );  //3
		bFirstLevelConfigSectionsMustExists.add( true );
		
		this.bLogSQLStatement = ConstantsService._Log_SQL_Statement;
		
		//this.strPreExecuteDir = DefaultConstantsSystemExecuteSQL.strDefaultPreExecuteDir;
		//this.strPostExecuteDir = DefaultConstantsSystemExecuteSQL.strDefaultPostExecuteDir;
		
		this.ExpressionsFilters = new ArrayList<CExpresionsFilters>();
		
	}
	
	public boolean loadConfigSectionLogger( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

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

	/*
	public boolean loadConfigSectionSystem( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

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
			
			}* /
        
        }
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;
	
	}*/    
	
	public CExpresionsFilters loadConfigFilter( Node ConfigSectionNode, String strFiltersName, String strFiltersType, CExtendedLogger Logger, CLanguage Lang ) {
		
		CExpresionsFilters ExpFilters = new CExpresionsFilters();
		
        try {

    		ExpFilters.strName = strFiltersName;
    		ExpFilters.strType = strFiltersType;		        		
		
    		NodeList ConfigFiltersList = ConfigSectionNode.getChildNodes();

    		if ( ConfigFiltersList.getLength() > 0 ) {

    			for ( int intConfigFilterIndex = 0; intConfigFilterIndex < ConfigFiltersList.getLength(); intConfigFilterIndex++ ) {

    				Node ConfigFilterNode = ConfigFiltersList.item( intConfigFilterIndex );

    				Logger.logMessage( "1", Lang.translate( "Reading XML node filter: [%s]", ConfigFilterNode.getNodeName() ) );        

    				if ( ConfigFilterNode.getNodeName().equals( ConstantsCommonConfigXMLTags._Filter ) == true ) {

    					if ( ConfigFilterNode.hasAttributes() == true ) {

    						String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Type };

    						String strType = "";
    						String strExpression = "";

    						if ( ConfigFilterNode.getTextContent() != null && ConfigFilterNode.getTextContent().isEmpty() == false ) {

    							NamedNodeMap NodeAttributes = ConfigFilterNode.getAttributes();

    							strExpression = ConfigFilterNode.getTextContent();

    							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

    								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

    								if ( NodeAttribute != null ) {

    									Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
    									Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

    									if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Type ) ) {

    										if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

    											if ( NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsCommonConfigXMLTags._Type_Exact ) || NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsCommonConfigXMLTags._Type_Partial ) || NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsCommonConfigXMLTags._Type_RExp ) ) {

    												strType = NodeAttribute.getNodeValue().toLowerCase();

    												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", strAttributesOrder[ intAttributesIndex ], strType ) );

    											}
    											else {

    												Logger.logError( "1004", Lang.translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s,%s]", strAttributesOrder[ intAttributesIndex ], NodeAttribute.getNodeValue(), ConstantsCommonConfigXMLTags._Type_Exact, ConstantsCommonConfigXMLTags._Type_Partial, ConstantsCommonConfigXMLTags._Type_RExp ) );

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
	
	public boolean loadConfigSectionExecutionFilters( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

        boolean bResult = true;
		
        try {
	
			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Name, ConstantsCommonConfigXMLTags._Type };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

				String strName = "";
				String strType = "";
				
				for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
		            	if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Name ) ) {

							strName = NodeAttribute.getNodeValue();
		
					        if ( strName.isEmpty() == false ) {
					        
								Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "Filters." + strAttributesOrder[ intAttributesIndex ], strName ) );
					        	
					        }
					        else {
					        	
								Logger.logError( "1001", Lang.translate( "The [%s] attribute cannot empty string, for the section [%s], ignoring the section", "Filters." + strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );
					        	
								break;
								
					        }
							
						}
		            	else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Type ) ) {

					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {
					        
								if ( NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsCommonConfigXMLTags._Type_Allow ) || NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsCommonConfigXMLTags._Type_Block ) ) {
					        	
									strType = NodeAttribute.getNodeValue().toLowerCase();

									Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "Filters." + strAttributesOrder[ intAttributesIndex ], strType ) );
								
								}
								else {
									
									Logger.logError( "1002", Lang.translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s]", "Filters." + strAttributesOrder[ intAttributesIndex ], NodeAttribute.getNodeValue(), ConstantsCommonConfigXMLTags._Type_Allow, ConstantsCommonConfigXMLTags._Type_Block ) );
									
								}
								
								
					        }
					        else {
					        	
								Logger.logError( "1003", Lang.translate( "The [%s] attribute cannot empty string, for the section [%s], ignoring the section", "Filters." + strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );

								break;
					        	
					        }
						
						}
		            
		            }
		            else {

		            	Logger.logError( "1004", Lang.translate( "The [%s] attribute not found, for the section [%s], ignoring the section", "Filters." + strAttributesOrder[ intAttributesIndex ], ConfigSectionNode.getNodeName() ) );
		            	
						break;
						
		            }
		            
		        }
				
				if ( strName.isEmpty() == false && strType.isEmpty() == false ) {
					
		        	CExpresionsFilters ExpFilters = CExpresionsFilters.getExpresionsFiltersByName( this.ExpressionsFilters, strName );
		        	
		        	if ( ExpFilters == null ) { //dont exits another filter name
		        		
		        		ExpFilters = this.loadConfigFilter( ConfigSectionNode, strName, strType, Logger, Lang );
		        		
		        		if ( ExpFilters.Filters.size() > 0 ) {
		        			
							Logger.logMessage( "1", Lang.translate( "Count of registered filters [%s], for filters name [%s] section [%s]", Integer.toString( ExpFilters.Filters.size() ), strName, ConfigSectionNode.getNodeName() ) );

		        			this.ExpressionsFilters.add( ExpFilters );
		        			
		        		}
		        		else {
		        			
							Logger.logError( "1005", Lang.translate( "Cannot add filters name [%s], the filters list is empty, for the section [%s]", strName, ConfigSectionNode.getNodeName() ) );
		        			
		        		}
		        	
		        	}
		        	else {
		        	
						Logger.logError( "1006", Lang.translate( "Duplicate filters name [%s], for the section [%s]", strName, ConfigSectionNode.getNodeName() ) );
		        		
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

	public boolean loadConfigSectionChannels( int intConfigSectionIndex, Node ConfigSectionNode, IDBReplicator DBReplicator, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {

			String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Class_Name, ConstantsCommonConfigXMLTags._Config_File };
			
			NodeList ConfigSectionList = ConfigSectionNode.getChildNodes();
		
			if ( ConfigSectionList.getLength() > 0 ) {

				for ( int intConfigChannelIndex = 0; intConfigChannelIndex < ConfigSectionList.getLength(); intConfigChannelIndex++ ) {

					Node ConfigChannelNode = ConfigSectionList.item( intConfigChannelIndex );

					Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigChannelNode.getNodeName() ) );        

					if ( ConfigChannelNode.getNodeName().equals( ConstantsConfigXMLTags._Channel ) == true ) {
			
						String strClassName = "";
						String strConfigFile = "";
						
						if ( ConfigChannelNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigChannelNode.getAttributes();

							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

								if ( NodeAttribute != null ) {

									Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
									Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

									if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Class_Name ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

											strClassName = NodeAttribute.getNodeValue();

										}
										else {

											Logger.logError( "-1002", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Class_Name ) );
											break; //Stop parse more attributes

										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Config_File ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

											strConfigFile = NodeAttribute.getNodeValue();

										}
										else {

											Logger.logError( "-1003", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Config_File ) );
											break; //Stop parse more attributes

										}
										
									}
								
								}	
								else {

									Logger.logError( "-1001", Lang.translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConfigChannelNode.getNodeName(), Integer.toString( intConfigChannelIndex ) ) );
									break;

								}
						
							}
						
							if ( strClassName.trim().isEmpty() == false && strConfigFile.trim().isEmpty() == false ) {
								
								try {
								
									Object DBChannelReplicatorObject = Class.forName( strClassName ).newInstance();
									
									if ( DBChannelReplicatorObject instanceof IDBChannelReplicator ) {
										
										IDBChannelReplicator DBChannelReplicator = (IDBChannelReplicator) DBChannelReplicatorObject;
										
										if ( DBChannelReplicator.loadConfig( ServicesDaemonConfig.strInstanceID, DBReplicator.getReplicationStorePath(), this.strRunningPath, strConfigFile, Logger, Lang) ) {
											
											DBReplicator.addChannelReplicator( DBChannelReplicator );
											
											Logger.logMessage( "1", Lang.translate( "Added channel: [%s], version: [%s] to replicator: [%s]", DBReplicator.getName(), DBChannelReplicator.getName(), DBChannelReplicator.getVersion() ) );        
											
										}
										else {
											
											Logger.logError( "-1006", Lang.translate( "Failed to load config from file: [%s]", strConfigFile ) );
											
										}
										
									}
									else {
										
										Logger.logError( "-1005", Lang.translate( "The class [%s] not implements the [%s] interface", DBChannelReplicatorObject.getClass().getCanonicalName(), IDBChannelReplicator.class.getCanonicalName() ) );
										
									}
								
								}
								catch ( Exception Ex ) {

									Logger.logException( "-1025", Ex.getMessage(), Ex );

								}
								catch ( Error Err ) {
									
									Logger.logError( "-1026", Err.getMessage(), Err );
									
								}
								
							}
							else {

								Logger.logError( "-1004", Lang.translate( "Replicator channel config attributes is not valid" ) );
								
							}
							
						}
						
					}
				
				}
			
				Logger.logMessage( "1", Lang.translate( "Count of channels: [%s] for replicator: [%s]", Integer.toString( DBReplicator.getChannelsReplicatorCount() ), DBReplicator.getName() ) );        
				bResult = true;
				
			}	
						
		}
		catch ( Exception Ex ) {

			Logger.logException( "-1030", Ex.getMessage(), Ex );

		}
        
        return bResult;
        
	}
	
	public boolean loadConfigSectionReplicatorFilters( int intConfigSectionIndex, Node ConfigSectionNode, IDBReplicator DBReplicator, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = true;

		try {

			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Type };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

				String strType = "";
				
				for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

					Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

					if ( NodeAttribute != null ) {

						Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
						Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

						if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Type ) ) {

					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {
						        
								if ( NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsCommonConfigXMLTags._Type_Allow ) || NodeAttribute.getNodeValue().toLowerCase().equals( ConstantsCommonConfigXMLTags._Type_Block ) ) {
					        	
									strType = NodeAttribute.getNodeValue().toLowerCase();

									Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strType", strType ) );
								
								}
								else {
									
									Logger.logError( "1001", Lang.translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s]", ConstantsCommonConfigXMLTags._Type, NodeAttribute.getNodeValue(), ConstantsCommonConfigXMLTags._Type_Allow, ConstantsCommonConfigXMLTags._Type_Block ) );
									
								}
								
								
					        }
					        else {
					        	
								Logger.logError( "1002", Lang.translate( "The [%s] attribute cannot empty string, for the section [%s], ignoring the section", ConstantsCommonConfigXMLTags._Type, ConfigSectionNode.getNodeName() ) );

								break;
					        	
					        }
							
						}

					}
		            else {

		            	Logger.logError( "1003", Lang.translate( "The [%s] attribute not found, for the section [%s], ignoring the section", ConstantsCommonConfigXMLTags._Type, ConfigSectionNode.getNodeName() ) );
		            	
						break;
						
		            }

				}
				
				if ( strType.isEmpty() == false ) {
					
					CExpresionsFilters ExpFilters = this.loadConfigFilter( ConfigSectionNode, "filters", strType, Logger, Lang );

					if ( ExpFilters.Filters.size() > 0 ) {

						DBReplicator.setFilters( ExpFilters );

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
	
	public boolean loadConfigSectionReplicator( int intConfigSectionIndex, Node ConfigSectionNode, String strClassName, String strName, String strSourceDBConnectionName, long lngMaxStoreFileSize, long lngOnFailGoSleepFor, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {
		
			NodeList ConfigSectionList = ConfigSectionNode.getChildNodes();

			Object DBReplicatorObject = Class.forName( strClassName ).newInstance();
			
			if ( DBReplicatorObject instanceof IDBReplicator ) {

				IDBReplicator DBReplicator = (IDBReplicator) DBReplicatorObject;

				if ( DBReplicator.initialize( this.strRunningPath, strName, strSourceDBConnectionName, lngMaxStoreFileSize, lngOnFailGoSleepFor, ServiceLogger, ServiceLang ) ) {

					for ( int intLocalConfigSectionIndex = 0; intLocalConfigSectionIndex < ConfigSectionList.getLength(); intLocalConfigSectionIndex++ ) {

						Node ConfigSection = ConfigSectionList.item( intLocalConfigSectionIndex );

						Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigSection.getNodeName() ) );        

						if ( ConfigSection.getNodeName().equals( ConstantsConfigXMLTags._Channels ) == true ) {

							if ( loadConfigSectionChannels( intLocalConfigSectionIndex, ConfigSection, DBReplicator, Logger, Lang ) == false ) {

								Logger.logWarning( "-1", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSection.getNodeName() ) );        
								break;

							}

						}
						else if ( ConfigSection.getNodeName().equals( ConstantsCommonConfigXMLTags._Filters ) == true ) {

							if ( loadConfigSectionReplicatorFilters( intLocalConfigSectionIndex, ConfigSection, DBReplicator, Logger, Lang ) == false ) {

								Logger.logWarning( "-1", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSection.getNodeName() ) );        
								break;

							}

						}

					}

					//if ( DBReplicator.getChannelsReplicatorCount() > 0 ) {

					CMasterDBReplicator MasterDBReplicator = CMasterDBReplicator.getMasterDBReplicator();

					MasterDBReplicator.registerDBReplicator( DBReplicator );

					bResult = true;

					/*}
					else {

						Logger.logWarning( "-1", Lang.translate( "No valid channels found for replicator: [%s]. Ignoring the replicator config entry", DBReplicator.getName() ) );        

					}*/

				}
				else {
					
					Logger.logError( "-1002", Lang.translate( "Failed to initialize the DB replicator" ) );        
					
				}

			}	
			else {
				
				Logger.logError( "-1001", Lang.translate( "The class [%s] not implements the [%s] interface", DBReplicatorObject.getClass().getCanonicalName(), IDBReplicator.class.getCanonicalName() ) );
				
			}
        
		}
		catch ( Error Err ) {

			Logger.logError( "-1025", Err.getMessage(), Err );

		}
		catch ( Exception Ex ) {

			Logger.logException( "-1026", Ex.getMessage(), Ex );

		}
        
        return bResult;
		
	}
	
	public boolean loadConfigSectionReplicators( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		try {

			String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Name, ConstantsCommonConfigXMLTags._Class_Name, ConstantsConfigXMLTags._Source_DBConnection_Name, ConstantsConfigXMLTags._Max_Store_File_Size, ConstantsConfigXMLTags._On_Fail_Go_Sleep_For };

			NodeList ConfigReplicatorsList = ConfigSectionNode.getChildNodes();

			if ( ConfigReplicatorsList.getLength() > 0 ) {

				for ( int intConfigReplicatorIndex = 0; intConfigReplicatorIndex < ConfigReplicatorsList.getLength(); intConfigReplicatorIndex++ ) {

					Node ConfigReplicatorNode = ConfigReplicatorsList.item( intConfigReplicatorIndex );

					Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigReplicatorNode.getNodeName() ) );        

					if ( ConfigReplicatorNode.getNodeName().equals( ConstantsConfigXMLTags._Replicator ) == true ) {

						String strClassName = "";
						String strName = "";
						String strSourceDBConnectionName = "";
						long lngMaxStoreFileSize = 0;
						long lngOnFailGoSleepFor = 0;

						if ( ConfigReplicatorNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigReplicatorNode.getAttributes();

							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

								if ( NodeAttribute != null ) {

									Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
									Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

									if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Name ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

											strName = NodeAttribute.getNodeValue();

										}
										else {

											Logger.logError( "-1002", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Name ) );
											break; //Stop parse more attributes

										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Class_Name ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

											strClassName = NodeAttribute.getNodeValue();

										}
										else {

											Logger.logError( "-1003", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Class_Name ) );
											break; //Stop parse more attributes

										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Source_DBConnection_Name ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strSourceDBConnectionName = NodeAttribute.getNodeValue().trim();
											
										}
										else {
											
									    	Logger.logError( "-1004", Lang.translate( "The [%s] attribute cannot empty string", ConstantsConfigXMLTags._Source_DBConnection_Name ) );
											break;
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Max_Store_File_Size ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											long lngTmp = net.maindataservices.Utilities.strToLong( NodeAttribute.getNodeValue().trim(), Logger );

											if ( lngTmp >= ConstantsConfigXMLTags._Min_Store_File_Size ) {
											
												lngMaxStoreFileSize = lngTmp;
											
											}
											else {
												
												lngMaxStoreFileSize = ConstantsConfigXMLTags._Min_Store_File_Size;
												
												Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsConfigXMLTags._Max_Store_File_Size, NodeAttribute.getNodeValue(), Long.toString( ConstantsConfigXMLTags._Min_Store_File_Size ), Long.toString( Long.MAX_VALUE ) ) );
										    	Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsConfigXMLTags._Max_Store_File_Size, NodeAttribute.getNodeValue(), Long.toString( ConstantsConfigXMLTags._Min_Store_File_Size ) ) );
												
											}
											
										}
										else {
											
									    	Logger.logError( "-1005", Lang.translate( "The [%s] attribute cannot empty string", ConstantsConfigXMLTags._Max_Store_File_Size ) );
											break;
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._On_Fail_Go_Sleep_For ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											long lngTmp = net.maindataservices.Utilities.strToLong( NodeAttribute.getNodeValue().trim(), Logger );

											if ( lngTmp >= ConstantsConfigXMLTags._Min_Fail_Sleep_Millis ) {
											
												lngOnFailGoSleepFor = lngTmp;
											
											}
											else {
												
												lngOnFailGoSleepFor = ConstantsConfigXMLTags._Fail_Sleep_Millis;
												
												Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsConfigXMLTags._On_Fail_Go_Sleep_For, NodeAttribute.getNodeValue(), Long.toString( ConstantsConfigXMLTags._Min_Fail_Sleep_Millis ), Long.toString( Long.MAX_VALUE ) ) );
										    	Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsConfigXMLTags._On_Fail_Go_Sleep_For, NodeAttribute.getNodeValue(), Long.toString( ConstantsConfigXMLTags._Fail_Sleep_Millis ) ) );
												
											}
											
										}
										else {
											
									    	Logger.logError( "-1006", Lang.translate( "The [%s] attribute cannot empty string", ConstantsConfigXMLTags._On_Fail_Go_Sleep_For ) );
											break;
											
										}
										
									}

								}
								else {

									Logger.logError( "-1001", Lang.translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConfigReplicatorNode.getNodeName(), Integer.toString( intConfigReplicatorIndex ) ) );
									break;

								}

							}

							if ( strName.isEmpty() == false && strSourceDBConnectionName.isEmpty() == false /*&& strURL.isEmpty() == false && strDatabase.isEmpty() == false && strUser.isEmpty() == false && strPassword.isEmpty() == false*/ ) {

								CMasterDBReplicator MasterDBReplicator = CMasterDBReplicator.getMasterDBReplicator();
								
								if ( MasterDBReplicator.getDBReplicatorByName( strName ) == null ) {
								
									if ( loadConfigSectionReplicator( intConfigReplicatorIndex, ConfigReplicatorNode, strClassName, strName, strSourceDBConnectionName, lngMaxStoreFileSize, lngOnFailGoSleepFor, Logger, Lang ) == false ) {

										Logger.logWarning( "-1", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigReplicatorNode.getNodeName() ) );        

									}
								
								}
								else {
									
									Logger.logError( "-1005", Lang.translate( "Replicator name [%s] already registered", strName ) );
									
								}

							}
							else {

								Logger.logError( "-1004", Lang.translate( "Replicator config attributes is not valid" ) );

							}

						}

					}

				}

				bResult = true;

			}

		}
		catch ( Exception Ex ) {

			Logger.logException( "-1025", Ex.getMessage(), Ex );

		}

		Logger.logMessage( "1", Lang.translate( "Count of replicators defined: [%s]", Integer.toString( CMasterDBReplicator.getMasterDBReplicator().getCountRegisteredDBReplicators() ) ) );        

		return bResult;

	}
	
	@Override
	public boolean loadConfigSection( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = true;

		Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals(  ConstantsCommonConfigXMLTags._Logger ) == true ) {
	           
			if ( this.loadConfigSectionLogger( ConfigSectionNode, Logger, Lang ) == false ) {
				
    			Logger.logError( "-1001", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		/*else if ( ConfigSectionNode.getNodeName().equals(  CommonClasses.ConstantsCommonConfigXMLTags._System ) == true ) {
           
			if ( this.loadConfigSectionSystem( ConfigSectionNode, Logger, Lang ) == false ) {
				
    			Logger.logError( "-1002", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }*/
		else if ( ConfigSectionNode.getNodeName().equals(  ConstantsCommonConfigXMLTags._Filters ) == true ) {
	           
			if ( this.loadConfigSectionExecutionFilters( ConfigSectionNode, Logger, Lang ) == false ) {
				
    			Logger.logError( "-1003", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		else if ( ConfigSectionNode.getNodeName().equals(  ConstantsConfigXMLTags._Replicators ) == true ) {
	           
			if ( this.loadConfigSectionReplicators( ConfigSectionNode, Logger, Lang ) == false ) {
				
    			Logger.logError( "-1004", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		
		return bResult;
		
	}

	@Override
	public boolean loadConfig( String strConfigFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
        if ( ServiceLogger == null )
        	ServiceLogger = Logger;

        if ( ServiceLang == null )
        	ServiceLang = Lang;
        
	    return super.loadConfig( strConfigFilePath, Logger, Lang );
	
	}
	
	@Override
	public Object sendMessage(String strMessageName, Object MessageData) {

		return "";
		
	}

}
