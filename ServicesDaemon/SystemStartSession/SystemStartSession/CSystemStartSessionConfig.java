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
package SystemStartSession;

import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Utilities.Utilities;


import AbstractService.CInputServiceParameter;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.NamesSQLTypes;
import DBServicesManager.CConfigDBConnection;
import DBServicesManager.CDBServicesManagerConfig;
import ExtendedLogger.CExtendedLogger;

public class CSystemStartSessionConfig extends CAbstractConfigLoader {

	protected static CSystemStartSessionConfig SystemStartSessionConfig = null;

	static {
		
		SystemStartSessionConfig = new CSystemStartSessionConfig();
		
	} 

	public static CSystemStartSessionConfig getSystemStartSessionConfig( CServicesDaemonConfig ServicesDaemonConfig, CAbstractConfigLoader DBServicesManagerConfig ) {
		
		if ( CDBServicesManagerConfig.class.isInstance( DBServicesManagerConfig ) )
		    SystemStartSessionConfig.DBServicesManagerConfig = (CDBServicesManagerConfig) DBServicesManagerConfig;
		else
			SystemStartSessionConfig.DBServicesManagerConfig = null;
		
		SystemStartSessionConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemStartSessionConfig;
		
	}
	
	public ArrayList<CSystemStartSessionDBConnection> ConfiguredSystemStartSessionDBConnections;

	public CDBServicesManagerConfig DBServicesManagerConfig = null;
	public CServicesDaemonConfig ServicesDaemonConfig = null;
	
	public CSystemStartSessionConfig() {

		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConfigXMLTagsSystemStartSession._DBConnections );  //1

		ConfiguredSystemStartSessionDBConnections = new ArrayList<CSystemStartSessionDBConnection>(); 
		
	}

	public CSystemStartSessionDBConnection getSystemStartSessionByName( String strName ) {
		
		CSystemStartSessionDBConnection Result = null;
		
		for ( CSystemStartSessionDBConnection SystemStartSessionDBConnection: ConfiguredSystemStartSessionDBConnections ) {

			if ( SystemStartSessionDBConnection.strName.equals( strName ) ) {
				
				Result = SystemStartSessionDBConnection;
				
				break;
				
			}
			
		}
		
		return Result;
		
	}
	
	public boolean LoadConfigSectionParam( int intConfigSectionIndex, Node ConfigSectionNode, CSystemStartSessionDBConnection SystemStartSessionDBConnection, CLanguage Lang, CExtendedLogger Logger ) {
		
        boolean bResult = false;
		
        try {
		   
			if ( ConfigSectionNode.hasAttributes() == true ) {
		
				String strName = "";
				boolean bRequired = false;
				String strDataType = "";
				String strScope = "";
				int intLength = 0;
				String strDescription = "";
				
				String strAttributesOrder[] = { ConfigXMLTagsSystemStartSession._Name, ConfigXMLTagsSystemStartSession._Required, ConfigXMLTagsSystemStartSession._DataType, ConfigXMLTagsSystemStartSession._Scope, ConfigXMLTagsSystemStartSession._Length, ConfigXMLTagsSystemStartSession._Description };
				
				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

				for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Name ) ) {

							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
							
								strName = NodeAttribute.getNodeValue().trim();
					           
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Param + ".str" + ConfigXMLTagsSystemStartSession._Name, strName ) );

							}
							else {
								
								Logger.LogError( "-1001", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Name, NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								break;
								
							}
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Required ) ) {

							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								bRequired = NodeAttribute.getNodeValue().toLowerCase().trim().equals( "true" );
					           
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Param + ".b" + ConfigXMLTagsSystemStartSession._Required, bRequired==true?"true":"false" ) );

							}
							else {
								
								Logger.LogError( "-1002", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Required, NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								break;
								
							}
						    
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._DataType ) ) {
 
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								if ( NamesSQLTypes.CheckSQLType( NodeAttribute.getNodeValue().trim() ) == true ) {
					           
									strDataType = NodeAttribute.getNodeValue().trim();
									
									Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Param + ".str" + ConfigXMLTagsSystemStartSession._DataType, strDataType ) );
							
								}
								else {
									
									Logger.LogError( "-1004", Lang.Translate( "The [%s] attribute value [%s] must be only one of the next values: %s, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._DataType, NodeAttribute.getNodeValue(), NamesSQLTypes.SQLTypes.toString(), NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
									
									break;
									
								}

							}
							else {
								
								Logger.LogError( "-1003", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._DataType, NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								break;
								
							}

						}	
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Scope ) ) {
							 
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
							   if ( NodeAttribute.getNodeValue().trim().equals( ConfigXMLTagsSystemStartSession._Scope_in ) || NodeAttribute.getNodeValue().trim().equals( ConfigXMLTagsSystemStartSession._Scope_inout ) || NodeAttribute.getNodeValue().trim().equals( ConfigXMLTagsSystemStartSession._Scope_out ) ) {
								
								   strScope = NodeAttribute.getNodeValue().trim();

								   Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Param + ".str" + ConfigXMLTagsSystemStartSession._Scope, strScope ) );
							   
							   }
							   else {
								   
									Logger.LogError( "-1006", Lang.Translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s,%s], for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._DataType, ConfigXMLTagsSystemStartSession._Scope_in, ConfigXMLTagsSystemStartSession._Scope_inout, ConfigXMLTagsSystemStartSession._Scope_out, NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

									break;
								   
							   }

							}
							else {
								
								Logger.LogError( "-1005", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Scope, NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								break;
								
							}
							
						}	
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Length ) ) {

							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
							
								int intTmpLength = Utilities.StrToInteger( NodeAttribute.getNodeValue().trim(), Logger );
								
								if ( intTmpLength == 0 ) { 

									if ( strDataType.equals( NamesSQLTypes._VARCHAR ) == false && strDataType.equals( NamesSQLTypes._CHAR ) == false ) {
								
									    intLength = intTmpLength;
								           
									    Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Param + ".int" + ConfigXMLTagsSystemStartSession._Length, Integer.toString( intLength ) ) );

									}
									else {
										
										Logger.LogError( "-1009", Lang.Translate( "The [%s] attribute value [%s] is invalid, must be positive integer, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Length, NodeAttribute.getNodeValue().trim(), NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

										break;
										
									}
									
								} 								
								else if ( intTmpLength > 0 ) {
								
								    intLength = intTmpLength;
					           
								    Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Param + ".int" + ConfigXMLTagsSystemStartSession._Length, Integer.toString( intLength ) ) );

							    }
								else {
									
									Logger.LogError( "-1008", Lang.Translate( "The [%s] attribute value [%s] is invalid, must be positive integer, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Length, NodeAttribute.getNodeValue().trim(), NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
									
									break;
									
								}
								   
							}
							else {
								
								Logger.LogError( "-1007", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Length, NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								break;
								
							}
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Description ) ) {
						
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								strDescription = NodeAttribute.getNodeValue().trim();
					           
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Param + ".str" + ConfigXMLTagsSystemStartSession._Description, strDescription ) );

							}
							else {
								
								Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute is empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._Description, NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex) ) );

							}
					        
						}
		            
		            }
		            
		        }
				
				if ( strName.isEmpty() == false && strDataType.isEmpty() == false && strScope.isEmpty() == false  ) {
					
					CInputServiceParameter InputParameter = new CInputServiceParameter( strName, bRequired, strDataType, Integer.toString( intLength ), CInputServiceParameter.parseParameterScope( strScope ), Lang.Translate( strDescription ) );
					
					SystemStartSessionDBConnection.InputParameters.add( InputParameter );
					
					Logger.LogMessage( "1", Lang.Translate( "Added input parameter strName: [%s] bRequiered: [%s] strDataType: [%s] strScope: [%s] intLength: [%s] strDescription: [%s]", strName, bRequired==true?"true":"false", strDataType, strScope, Integer.toString( intLength ), strDescription ) );
					
					bResult = true;
					
				}
				else if ( strName.isEmpty() == true ) {
					
					Logger.LogError( "-1010", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Name, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
					
				} 
				else if ( strDataType.isEmpty() == true ) {
					
					Logger.LogError( "-1011", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._DataType, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
					
				} 
				else if ( strScope.isEmpty() == true ) {
					
					Logger.LogError( "-1012", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Scope, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
					
				} 
				else {
					
					Logger.LogError( "-1013", Lang.Translate( "Cannot add the input parameter some values ​​are missing or invalid, for the node [%s] at relative index [%s]", ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
					
				}
			
			}
		
		}
		catch ( Exception Ex ) {
			
			Logger.LogException( "-1015", Ex.getMessage(), Ex );
			
		}
        
        return bResult;
		
	}
	
	public boolean LoadConfigSectionInputParams( int intConfigSectionIndex, Node ConfigSectionNode, CSystemStartSessionDBConnection SystemStartSessionDBConnection, CLanguage Lang, CExtendedLogger Logger ) {
		
		boolean bResult = true;
		
		NodeList ConfigSectionList = ConfigSectionNode.getChildNodes();
		
        for ( int intLocalConfigSecitonIndex = 0; intLocalConfigSecitonIndex < ConfigSectionList.getLength(); intLocalConfigSecitonIndex++ ) {
            
        	Node ConfigSectionInputParams = ConfigSectionList.item( intLocalConfigSecitonIndex );

    		Logger.LogMessage( "1", Lang.Translate( "Reading XML node section: [%s]", ConfigSectionInputParams.getNodeName() ) );        

    		if ( ConfigSectionInputParams.getNodeName().equals( ConfigXMLTagsSystemStartSession._Param ) == true ) {
        	
    			LoadConfigSectionParam( intLocalConfigSecitonIndex, ConfigSectionInputParams, SystemStartSessionDBConnection, Lang, Logger );
        		
        	}
             
        }
        
        if ( SystemStartSessionDBConnection.InputParameters.size() == 0 ) {
		   
        	bResult = false;
        	
        	Logger.LogError( "-1001", Lang.Translate( "No valid input parameters defined, for the node [%s] at relative index [%s]", ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );        
        	
        }
        else {

        	boolean bInputParamRequired = false;
        	
        	for ( int intIndexParam = 0; intIndexParam < SystemStartSessionDBConnection.InputParameters.size(); intIndexParam++ ) {
        		
                 if ( SystemStartSessionDBConnection.InputParameters.get(intIndexParam).getParameterRequired() == true ) {

                	 bInputParamRequired = true;
                	 break;
                	 
                 }
        		
        	}
        	
        	if ( bInputParamRequired == false ) {
        		
        		bResult = false;
        		
            	Logger.LogError( "-1002", Lang.Translate( "At least one required parameter must be defined, for the node [%s] at relative index [%s]", ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );        
        		
        	}
        	
        }
		
		return bResult;
		
	}
	
	public boolean LoadConfigSectionCheckMethod( int intConfigSectionIndex, Node ConfigSectionNode, CSystemStartSessionDBConnection SystemStartSessionDBConnection, CLanguage Lang, CExtendedLogger Logger ) {
		
		boolean bResult =  true;

		try {	

			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConfigXMLTagsSystemStartSession._SQLType, ConfigXMLTagsSystemStartSession._SQL, ConfigXMLTagsSystemStartSession._SessionKey, ConfigXMLTagsSystemStartSession._Type, ConfigXMLTagsSystemStartSession._Field_Name, ConfigXMLTagsSystemStartSession._Field_Type, ConfigXMLTagsSystemStartSession._Field_Value_Success, ConfigXMLTagsSystemStartSession._Field_Value_Failed, ConfigXMLTagsSystemStartSession._Field_Value_Disabled, ConfigXMLTagsSystemStartSession._Field_Value_NotFound };
				
				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

				for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._SQLType ) ) {
							 
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								if ( NodeAttribute.getNodeValue().equals( ConfigXMLTagsSystemStartSession._SQLType_sql ) || NodeAttribute.getNodeValue().equals( ConfigXMLTagsSystemStartSession._SQLType_stored_procedure ) ) {
					           
									SystemStartSessionDBConnection.strSQLType = NodeAttribute.getNodeValue().trim().toLowerCase();
									
									Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._SQLType, SystemStartSessionDBConnection.strSQLType ) );
							
								}
								else {
									
									Logger.LogError( "-1001", Lang.Translate( "The [%s] attribute value [%s] must be only one of the next values: [%s.%s], for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._SQLType, NodeAttribute.getNodeValue(), ConfigXMLTagsSystemStartSession._SQLType_sql, ConfigXMLTagsSystemStartSession._SQLType_stored_procedure, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
									
									bResult = false;

									break;
									
								}

							}
							else {
								
								Logger.LogError( "-1002", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._DataType, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								break;
								
							}

						}	
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._SQL ) ) {

							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
							
								SystemStartSessionDBConnection.strSQL = NodeAttribute.getNodeValue().trim();
					           
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._SQL, SystemStartSessionDBConnection.strSQL ) );

							}
							else {
								
								Logger.LogError( "-1003", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._SQL, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								bResult = false;
								
								break;
								
							}
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._SessionKey ) ) {

							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								SystemStartSessionDBConnection.strSessionKey = NodeAttribute.getNodeValue().trim();
					           
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._SessionKey, SystemStartSessionDBConnection.strSessionKey ) );

							}
							else {
								
								Logger.LogError( "-1004", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._SessionKey, NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								bResult = false;

								break;
								
							}
						    
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Type ) ) {
 
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								if ( NodeAttribute.getNodeValue().equals( ConfigXMLTagsSystemStartSession._Type_if_exists ) || NodeAttribute.getNodeValue().equals( ConfigXMLTagsSystemStartSession._Type_check_field_value ) ) {
					           
									SystemStartSessionDBConnection.strType = NodeAttribute.getNodeValue().trim().toLowerCase();
									
									Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Type, SystemStartSessionDBConnection.strType ) );
							
								}
								else {
									
									Logger.LogError( "-1005", Lang.Translate( "The [%s] attribute value [%s] must be only one of the next values: [%s.%s], for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._DataType, ConfigXMLTagsSystemStartSession._Type_check_field_value, ConfigXMLTagsSystemStartSession._Type_if_exists, NodeAttribute.getNodeValue(), NamesSQLTypes.SQLTypes.toString(), ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
									
									bResult = false;

									break;
									
								}

							}
							else {
								
								Logger.LogError( "-1006", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._DataType, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								break;
								
							}

						}	
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Field_Name ) ) {
							 
							if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_if_exists ) || NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								SystemStartSessionDBConnection.strFieldName = NodeAttribute.getNodeValue().trim();

								 Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Field_Name, SystemStartSessionDBConnection.strFieldName ) );

							}
							else {
								
								Logger.LogError( "-1007", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._Field_Name, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								bResult = false;

								break;
								
							}
							
						}	
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Field_Type ) ) {
							 
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								if ( NamesSQLTypes.CheckSQLType( NodeAttribute.getNodeValue().trim() ) == true ) {
							           
									SystemStartSessionDBConnection.strFieldType = NodeAttribute.getNodeValue().trim();
									
									Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Field_Type, SystemStartSessionDBConnection.strFieldType ) );
							
								}
								else {
									
									Logger.LogError( "-1008", Lang.Translate( "The [%s] attribute value [%s] must be only one of the next values: %s, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._Field_Type, ConfigSectionNode.getNodeValue(), NamesSQLTypes.SQLTypes.toString(), NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
									
									break;
									
								}

							}
							else {
								
								Logger.LogError( "-1009", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._Field_Type, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								break;
								
							}

						}	
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Field_Value_Success ) ) {

							if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_if_exists ) || NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
							
								SystemStartSessionDBConnection.strFieldValueSuccess = NodeAttribute.getNodeValue().trim();
								           
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Field_Value_Success, SystemStartSessionDBConnection.strFieldValueSuccess ) );
								   
							}
							else {
								
								Logger.LogError( "-1010", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._Field_Value_Success, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

								bResult = false;

								break;
								
							}
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Field_Value_Failed ) ) {
						
							if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_if_exists ) || NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								SystemStartSessionDBConnection.strFieldValueFailed = NodeAttribute.getNodeValue().trim();
					           
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Field_Value_Failed, SystemStartSessionDBConnection.strFieldValueFailed ) );

							}
							else {
								
								Logger.LogError( "-1011", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._Field_Value_Failed, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
								
								bResult = false;

								break;
								
							}
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Field_Value_Disabled ) ) {
							
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								SystemStartSessionDBConnection.strFieldValueDisabled = NodeAttribute.getNodeValue().trim();
					           
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Field_Value_Disabled, SystemStartSessionDBConnection.strFieldValueDisabled ) );

							}
							else {
								
								Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute is empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._Field_Value_Disabled, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
								
							}
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Field_Value_NotFound ) ) {
							
							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								SystemStartSessionDBConnection.strFieldValueNotFound = NodeAttribute.getNodeValue().trim();
					           
								Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Field_Value_NotFound, SystemStartSessionDBConnection.strFieldValueNotFound ) );

							}
							else {
								
								Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute is empty string, for the node [%s] at relative index [%s]", ConfigXMLTagsSystemStartSession._Field_Value_NotFound, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
								
							}
					        
						}
		            
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._SQLType ) ) {
		            	
				        Logger.LogError( "-1012", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._SQLType, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
				        
				        bResult = false;
				        
				        break;
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._SQL ) ) {
		            	
				        Logger.LogError( "-1013", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._SQL, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
				        
				        bResult = false;
				        
				        break;
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._SessionKey ) ) {
		            	
				        Logger.LogError( "-1014", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._SessionKey, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
				        
				        bResult = false;
				        
				        break;
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._Type ) ) {
		            	
				        Logger.LogError( "-1015", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Type, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );
				        
				        bResult = false;
				        
				        break;
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._Field_Name ) ) {

		            	if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_check_field_value ) ) {

		            		Logger.LogError( "-1016", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Field_Name, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

		            		bResult = false;

		            		break;
		            	
		            	}
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._Field_Type ) ) {
		            	
		            	if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_check_field_value ) ) {

		            		Logger.LogError( "-1017", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Field_Type, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

		            		bResult = false;

		            		break;
		            	
		            	}
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._Field_Value_Success ) ) {
		            	
		            	if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_check_field_value ) ) {

		            		Logger.LogError( "-1018", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Field_Value_Success, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

		            		bResult = false;

		            		break;
		            	
		            	}
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._Field_Value_Failed ) ) {
		            	
		            	if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_check_field_value ) ) {

		            		Logger.LogError( "-1019", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Field_Value_Failed, ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

		            		bResult = false;

		            		break;
		            	
		            	}
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._Field_Value_Disabled ) ) {
		            	
		            	if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_check_field_value ) ) {

		            		Logger.LogError( "-1020", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Field_Value_Disabled ) );

		            		bResult = false;

		            		break;
		            	
		            	}
		            	
		            }
		            else if ( strAttributesOrder[ intAttributesIndex ].equals( ConfigXMLTagsSystemStartSession._Field_Value_NotFound ) ) {
		            	
		            	if ( SystemStartSessionDBConnection.strType.equals( ConfigXMLTagsSystemStartSession._Type_check_field_value ) ) {

		            		Logger.LogError( "-1021", Lang.Translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Field_Value_NotFound ) );

		            		bResult = false;

		            		break;
		            	
		            	}
		            	
		            }
		            
		        }
				
			}
			
		}
		catch ( Exception Ex ) {

			bResult = false;

			Logger.LogException( "-1025", Ex.getMessage(), Ex );

		}
		
		return bResult;
		
	}
	
	public boolean LoadConfigSectionAddField( int intTypeSection, int intConfigSectionIndex, Node ConfigSectionNode, CSystemStartSessionDBConnection SystemStartSessionDBConnection, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;
		
		try {

			if ( ConfigSectionNode.hasAttributes() == true ) {
				
				String strAttributesOrder[] = { ConfigXMLTagsSystemStartSession._Name };
				
				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

				for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.LogMessage( "1", Lang.Translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConfigXMLTagsSystemStartSession._Name ) ) {

							if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
								
								//Logger.LogMessage( "1", Lang.Translate( "Runtime config value [%s] changed to: [%s]",  ConfigXMLTagsSystemStartSession._Name, NodeAttribute.getNodeValue().trim() ) );

								if ( intTypeSection == ConfigXMLTagsSystemStartSession._Section_Success ) { //Sucess

									SystemStartSessionDBConnection.AddFieldToResponseSuccess.add( NodeAttribute.getNodeValue().trim() );

									Logger.LogMessage( "1", Lang.Translate( "Added field [%s] for response success", NodeAttribute.getNodeValue().trim() ) );

								}
								else if ( intTypeSection == ConfigXMLTagsSystemStartSession._Section_Failed ) { //Failed
									
									SystemStartSessionDBConnection.AddFieldToResponseFailed.add( NodeAttribute.getNodeValue().trim() );
								
									Logger.LogMessage( "1", Lang.Translate( "Added field [%s] for response failed", NodeAttribute.getNodeValue().trim() ) );
									
								}
								else if ( intTypeSection == ConfigXMLTagsSystemStartSession._Section_Disabled ) { //Disabled
									
									SystemStartSessionDBConnection.AddFieldToResponseDisabled.add( NodeAttribute.getNodeValue().trim() );
								
									Logger.LogMessage( "1", Lang.Translate( "Added field [%s] for response disabled", NodeAttribute.getNodeValue().trim() ) );
									
								}
								else if ( intTypeSection == ConfigXMLTagsSystemStartSession._Section_NotFound ) { //Not Found
									
									SystemStartSessionDBConnection.AddFieldToResponseNotFound.add( NodeAttribute.getNodeValue().trim() );
								
									Logger.LogMessage( "1", Lang.Translate( "Added field [%s] for response not found", NodeAttribute.getNodeValue().trim() ) );

								}
								else if ( intTypeSection == ConfigXMLTagsSystemStartSession._Section_Any ) { //Any
									
									SystemStartSessionDBConnection.AddFieldToResponseAny.add( NodeAttribute.getNodeValue().trim() );
								
									Logger.LogMessage( "1", Lang.Translate( "Added field [%s] for any response", NodeAttribute.getNodeValue().trim() ) );
									
								}
					           

							}
							else {
								
								Logger.LogWarning( "-1", Lang.Translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s], ignoring the node", ConfigXMLTagsSystemStartSession._Name, NodeAttribute.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );

							}
						
						}
		            
		            }
		            
		        }
				
			}
			
		
		}
		catch ( Exception Ex ) {

			bResult = false;

			Logger.LogException( "-1015", Ex.getMessage(), Ex );

		}
		
		return bResult;
		
	}
	
	public boolean LoadConfigSectionAddFieldsToResponse( int intTypeSecction, int intConfigSectionIndex, Node ConfigSectionNode, CSystemStartSessionDBConnection SystemStartSessionDBConnection, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;
		
		NodeList ConfigSectionList = ConfigSectionNode.getChildNodes();
		
        for ( int intLocalConfigSecitonIndex = 0; intLocalConfigSecitonIndex < ConfigSectionList.getLength(); intLocalConfigSecitonIndex++ ) {
            
        	Node ConfigSectionAddFieldsToResponse = ConfigSectionList.item( intLocalConfigSecitonIndex );

    		Logger.LogMessage( "1", Lang.Translate( "Reading XML node section: [%s]", ConfigSectionAddFieldsToResponse.getNodeName() ) );        

    		if ( ConfigSectionAddFieldsToResponse.getNodeName().equals( ConfigXMLTagsSystemStartSession._AddField ) == true ) {
        	
    			LoadConfigSectionAddField( intTypeSecction, intLocalConfigSecitonIndex, ConfigSectionAddFieldsToResponse, SystemStartSessionDBConnection, Lang, Logger );
        		
        	}
             
        }
		
		return bResult;
		
	}
	
	public boolean LoadConfigSectionDBConnection( int intConfigSectionIndex, Node ConfigSectionNode, String strDBConnectioName, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;
		
		CSystemStartSessionDBConnection SystemStartSessionDBConnection = new CSystemStartSessionDBConnection(); 
		
		SystemStartSessionDBConnection.strName = strDBConnectioName;
		
		NodeList ConfigSectionList = ConfigSectionNode.getChildNodes();
		
        for ( int intLocalConfigSectionIndex = 0; intLocalConfigSectionIndex < ConfigSectionList.getLength(); intLocalConfigSectionIndex++ ) {
            
        	Node ConfigSectionDBConnection = ConfigSectionList.item( intLocalConfigSectionIndex );

    		Logger.LogMessage( "1", Lang.Translate( "Reading XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

    		if ( ConfigSectionDBConnection.getNodeName().equals( ConfigXMLTagsSystemStartSession._InputParams ) == true ) {
        	
    			if ( LoadConfigSectionInputParams( intLocalConfigSectionIndex, ConfigSectionDBConnection, SystemStartSessionDBConnection, Lang, Logger ) == false ) {
	        		
    			    Logger.LogError( "-1001", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

    			    bResult = false;
    			    
    			    break;
    			    
        		}
    			
        		
        	}
    		else if ( ConfigSectionDBConnection.getNodeName().equals( ConfigXMLTagsSystemStartSession._CheckMethod ) == true ) {
        		
    			if ( LoadConfigSectionCheckMethod( intLocalConfigSectionIndex, ConfigSectionDBConnection, SystemStartSessionDBConnection, Lang, Logger ) == false ) {
	        		
    			    Logger.LogError( "-1002", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

    			    bResult = false;
    			    
    			    break;
    			    
        		}
        		
        	}
    		else if ( ConfigSectionDBConnection.getNodeName().equals( ConfigXMLTagsSystemStartSession._AddFieldsToResponseSuccess ) == true ) {
        		
    			if ( LoadConfigSectionAddFieldsToResponse( ConfigXMLTagsSystemStartSession._Section_Success, intLocalConfigSectionIndex, ConfigSectionDBConnection, SystemStartSessionDBConnection, Lang, Logger ) == false ) {
    				
    			    Logger.LogError( "-1003", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

    			    bResult = false;
    			    
    			    break;
    				
    			}
        		
        	}
    		else if ( ConfigSectionDBConnection.getNodeName().equals( ConfigXMLTagsSystemStartSession._AddFieldsToResponseFailed ) == true ) {
        		
    			if ( LoadConfigSectionAddFieldsToResponse( ConfigXMLTagsSystemStartSession._Section_Failed, intLocalConfigSectionIndex, ConfigSectionDBConnection, SystemStartSessionDBConnection, Lang, Logger ) == false ) {
    				
    			    Logger.LogError( "-1004", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

    			    bResult = false;
    			    
    			    break;
    				
    			}
        		
        	}
    		else if ( ConfigSectionDBConnection.getNodeName().equals( ConfigXMLTagsSystemStartSession._AddFieldsToResponseDisabled ) == true ) {
        		
    			if ( LoadConfigSectionAddFieldsToResponse( ConfigXMLTagsSystemStartSession._Section_Disabled, intLocalConfigSectionIndex, ConfigSectionDBConnection, SystemStartSessionDBConnection, Lang, Logger ) == false ) {
    				
    			    Logger.LogError( "-1005", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

    			    bResult = false;
    			    
    			    break;
    				
    			}
        		
        	}
    		else if ( ConfigSectionDBConnection.getNodeName().equals( ConfigXMLTagsSystemStartSession._AddFieldsToResponseNotFound ) == true ) {
        		
    			if ( LoadConfigSectionAddFieldsToResponse( ConfigXMLTagsSystemStartSession._Section_NotFound, intLocalConfigSectionIndex, ConfigSectionDBConnection, SystemStartSessionDBConnection, Lang, Logger ) == false ) {
    				
    			    Logger.LogError( "-1006", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

    			    bResult = false;
    			    
    			    break;
    				
    			}
        		
        	}
    		else if ( ConfigSectionDBConnection.getNodeName().equals( ConfigXMLTagsSystemStartSession._AddFieldsToResponseAny ) == true ) {
        		
    			if ( LoadConfigSectionAddFieldsToResponse( ConfigXMLTagsSystemStartSession._Section_Any, intLocalConfigSectionIndex, ConfigSectionDBConnection, SystemStartSessionDBConnection, Lang, Logger ) == false ) {
    				
    			    Logger.LogError( "-1007", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

    			    bResult = false;
    			    
    			    break;
    				
    			}
        		
        	}
             
        }    
		
        if ( bResult == true ) {
        	
		    this.ConfiguredSystemStartSessionDBConnections.add( SystemStartSessionDBConnection );

		    Logger.LogMessage( "1", Lang.Translate( "Added DBConnection name [%s] to list, configured input params count: [%s]", SystemStartSessionDBConnection.strName, Integer.toString( SystemStartSessionDBConnection.InputParameters.size() ) ) );        
        	
        }
        else {
        	
		    Logger.LogWarning( "-1008", Lang.Translate( "Failed to add the DBConnection, for the section [%s] at relative index [%s], ignoring the section", ConfigSectionNode.getNodeName(), Integer.toString( intConfigSectionIndex ) ) );        
        	
        }
        
		return bResult;
		
	}
	
	public boolean LoadConfigSectionDBConnections( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {
		
		boolean bResult = true;
		
		if ( DBServicesManagerConfig != null ) {
		
			NodeList ConfigSectionList = ConfigSectionNode.getChildNodes();

			for ( int intConfigSecitonIndex = 0; intConfigSecitonIndex < ConfigSectionList.getLength(); intConfigSecitonIndex++ ) {

				Node ConfigSectionDBConnection = ConfigSectionList.item( intConfigSecitonIndex );

				Logger.LogMessage( "1", Lang.Translate( "Reading XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

				if ( ConfigSectionDBConnection.getNodeName().equals( ConfigXMLTagsSystemStartSession._DBConnection ) == true ) {

					NamedNodeMap NodeAttributes = ConfigSectionDBConnection.getAttributes();

					Node NameAttribute = NodeAttributes.getNamedItem( ConfigXMLTagsSystemStartSession._Name );

					if ( NameAttribute != null ) { 

						if ( NameAttribute.getNodeValue().isEmpty() == false ) {

							String strDBConnectionName = NameAttribute.getNodeValue();

							CConfigDBConnection ConfigDBConnection = DBServicesManagerConfig.getConfiguredDBConnection( strDBConnectionName );

							if ( ConfigDBConnection != null ) {

								if ( this.getSystemStartSessionByName( strDBConnectionName ) == null ) { 

									if ( LoadConfigSectionDBConnection( intConfigSecitonIndex, ConfigSectionDBConnection, strDBConnectionName, Lang, Logger ) == false ) {

										Logger.LogWarning( "-1", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionDBConnection.getNodeName() ) );        

									}

								}
								else {

									Logger.LogWarning( "-3", Lang.Translate( "The [%s] attribute value [%s] is invalid, name duplicated config file, for the section [%s] at relative index [%s], ignoring the section", ConfigXMLTagsSystemStartSession._Name, strDBConnectionName, ConfigSectionDBConnection.getNodeName(), Integer.toString( intConfigSecitonIndex ) ) );        

								}

							}
							else {

								Logger.LogWarning( "-2", Lang.Translate( "The [%s] attribute value [%s] is invalid, name not found in databases conections from manager config file, for the section [%s] at relative index [%s], ignoring the section", ConfigXMLTagsSystemStartSession._Name, strDBConnectionName, ConfigSectionDBConnection.getNodeName(), Integer.toString( intConfigSecitonIndex ) ) );        

							}

						}
						else {

							Logger.LogWarning( "-3", Lang.Translate( "The [%s] attribute cannot empty string, for the section [%s] at relative index [%s], ignoring the section", ConfigXMLTagsSystemStartSession._Name, ConfigSectionDBConnection.getNodeName(), Integer.toString( intConfigSecitonIndex ) ) );        

						}

					}
					else {

						Logger.LogWarning( "-4", Lang.Translate( "The [%s] attribute not found, for the section [%s] at relative index [%s], ignoring the section", ConfigXMLTagsSystemStartSession._Name, ConfigSectionDBConnection.getNodeName(), Integer.toString( intConfigSecitonIndex ) ) );        

					}

				}

			} 
        
		}
		else {
			
        	String strMessage = "DBServicesManagerConfig is NULL";
        	
        	if ( Lang != null )
        		strMessage = Lang.Translate( strMessage );
			
			Logger.LogWarning( "-1", strMessage );        
			
		}
		
        if ( this.ConfiguredSystemStartSessionDBConnections.size() == 0 ) {

        	bResult = false;
        	
        	Logger.LogError( "-1001", Lang.Translate( "The list of DBConections is empty" ) );        
        	
        }
        else {
        	
        	Logger.LogMessage( "1", Lang.Translate( "DBConections count registered: [%s]", Integer.toString( this.ConfiguredSystemStartSessionDBConnections.size() ) ) );        
        	
        }

        
        
		return bResult;
		
	}
	
	public boolean LoadConfigSection( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

		boolean bResult = true;

		Logger.LogMessage( "1", Lang.Translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals( ConfigXMLTagsSystemStartSession._DBConnections ) == true ) {
           
			if ( this.LoadConfigSectionDBConnections( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.LogError( "-1001", Lang.Translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
		
		return bResult;
		
	}
	
	/*public boolean LoadConfig( String strConfigFilePath, CLanguage Lang, CExtendedLogger Logger ) {
		
        if ( ServiceLogger == null )
        	ServiceLogger = Logger;

        if ( ServiceLang == null )
        	ServiceLang = Lang;

	    return super.LoadConfig( strConfigFilePath, Lang, Logger );
	
	}*/
	

}
