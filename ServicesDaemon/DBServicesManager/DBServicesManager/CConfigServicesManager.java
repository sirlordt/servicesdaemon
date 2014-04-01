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
package DBServicesManager;

import java.io.File;
import java.util.ArrayList;

import net.maindataservices.Utilities;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CClassPathLoader;
import CommonClasses.CConfigNativeDBConnection;
import CommonClasses.CConfigRegisterService;
import CommonClasses.CLanguage;
import CommonClasses.ConstantsCommonConfigXMLTags;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsMessagesCodes;
import ExtendedLogger.CExtendedLogger;

public class CConfigServicesManager extends CAbstractConfigLoader {

	protected static CConfigServicesManager ConfigDBServicesManager = null;

	public String strGlobalDateTimeFormat;
	public String strGlobalDateFormat;
	public String strGlobalTimeFormat;

	public int intRequestTimeout;
	public int intSocketTimeout;
	
	public String strTempDir;
	public String strServicesDir;
	public String strDBDriversDir;
	public String strDBEnginesDir;
	public String strResponsesFormatsDir;
	public String strDefaultResponseFormat;
	public String strDefaultResponseFormatVersion;
	public String strResponseRequestMethod; //OnlyGET, OnlyPOST, Any
	
	public int intInternalFetchSize; //25000 default
	
	//Built in responses formats configs
	public String strXML_DataPacket_ContentType;
	public String strXML_DataPacket_CharSet;
	
	public String strJavaXML_WebRowSet_ContentType;
	public String strJavaXML_WebRowSet_CharSet;
	
	public String strJSON_ContentType;
	public String strJSON_CharSet;
	
	public String strCSV_ContentType;
	public String strCSV_CharSet;
	public boolean bCSV_FieldQuoted;
	public String strCSV_SeparatorSymbol;
	public boolean bCSV_ShowHeaders;
	
	public ArrayList<CConfigNativeDBConnection> ConfiguredDBConnections;

	public ArrayList<CConfigRegisterService> ConfiguredRegisterServices;

	/*public static CConfigDBServicesManager getConfigDBServicesManager() {
		
		return ConfigDBServicesManager;
		
	}*/

	public static CConfigServicesManager getConfigDBServicesManager( String strRunningPath ) {
		
		if ( ConfigDBServicesManager == null ) {
			
			ConfigDBServicesManager = new CConfigServicesManager( strRunningPath );
			
		}

		return ConfigDBServicesManager;
		
	}
	
	public CConfigServicesManager( String strRunningPath ) {

		super( strRunningPath );
		
		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._System );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._RegisterServices ); //2
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._BuiltinResponsesFormats ); //3
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._DBConnections ); //4
		bFirstLevelConfigSectionsMustExists.add( true );
		
		strTempDir = strRunningPath + ConstantsCommonClasses._Temp_Dir; //"Temp/";
		strServicesDir = strRunningPath + ConstantsServicesManager._Services_Dir; //"DBServices/"; 
		strDBDriversDir = strRunningPath + ConstantsServicesManager._DB_Drivers_Dir; //"DBDrivers/";
		strDBEnginesDir = strRunningPath + ConstantsServicesManager._DB_Engines_Dir; //"DBEnginess/";
		strResponsesFormatsDir = strRunningPath + ConstantsCommonClasses._Responses_Formats_Dir; //"ResponsesFormats/";
		
		strGlobalDateTimeFormat = ConstantsCommonClasses._Global_Date_Time_Format;
		strGlobalDateFormat = ConstantsCommonClasses._Global_Date_Format;
		strGlobalTimeFormat = ConstantsCommonClasses._Global_Time_Format;
		
		strDefaultResponseFormat = ConstantsServicesManager._Response_Format;
		strDefaultResponseFormatVersion = ConstantsServicesManager._Response_Format_Version;

		strResponseRequestMethod = ConstantsCommonConfigXMLTags._Request_Method_ANY;
		
		intInternalFetchSize = ConstantsCommonClasses._Internal_Fetch_Size;
		
		strXML_DataPacket_CharSet = ConstantsCommonClasses._Chaset_XML;
		strXML_DataPacket_ContentType = ConstantsCommonClasses._Content_Type_XML;
		
		strJavaXML_WebRowSet_CharSet = ConstantsCommonClasses._Chaset_XML;
		strJavaXML_WebRowSet_ContentType = ConstantsCommonClasses._Content_Type_XML;

		strJSON_ContentType = ConstantsCommonClasses._Content_Type_JSON;
		strJSON_CharSet = ConstantsCommonClasses._Chaset_JSON;
		
		strCSV_ContentType = ConstantsCommonClasses._Content_Type_CSV;
		strCSV_CharSet = ConstantsCommonClasses._Chaset_CSV;
		bCSV_FieldQuoted = ConstantsCommonClasses._Fields_Quote_CSV;
		strCSV_SeparatorSymbol = ConstantsCommonClasses._Separator_Symbol_CSV;
		bCSV_ShowHeaders = ConstantsCommonClasses._Show_Headers_CSV;

		intRequestTimeout = ConstantsCommonClasses._Request_Timeout;
		intSocketTimeout = ConstantsCommonClasses._Socket_Timeout;

		ConfiguredDBConnections = new ArrayList<CConfigNativeDBConnection>();
		
		ConfiguredRegisterServices = new ArrayList<CConfigRegisterService>();
		
	}
	
	public CConfigNativeDBConnection getConfiguredNativeDBConnection( String strDBConnectionName ) {
		
		CConfigNativeDBConnection Result = null;

		for ( CConfigNativeDBConnection DBConnection : ConfiguredDBConnections ) {
			
			if ( DBConnection.strName.equals( strDBConnectionName ) ) {
				
				Result = DBConnection;
				
				break;
				
			}
			
		}
		
		return Result;
		
	}

    public boolean loadConfigSectionSystem( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

        boolean bResult = true;
		
        try {
		   
			if ( ConfigSectionNode.hasAttributes() == true ) {
		
				String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Global_Date_Time_Format, ConstantsCommonConfigXMLTags._Global_Date_Format, ConstantsCommonConfigXMLTags._Global_Time_Format, ConstantsCommonConfigXMLTags._Temp_Dir, ConstantsConfigXMLTags._Services_Dir, ConstantsConfigXMLTags._DBDrivers_Dir, ConstantsConfigXMLTags._DBEngines_Dir, ConstantsCommonConfigXMLTags._Responses_Formats_Dir, ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version, ConstantsCommonConfigXMLTags._Internal_Fetch_Size, ConstantsCommonConfigXMLTags._Response_Request_Method, ConstantsCommonConfigXMLTags._Request_Timeout, ConstantsCommonConfigXMLTags._Socket_Timeout };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Global_Date_Time_Format ) ) {
							
							if ( NodeAttribute.getNodeValue() != null ) {
							
								if ( NodeAttribute.getNodeValue().trim().isEmpty() == true ) {

									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Global_Date_Time_Format, this.strGlobalDateTimeFormat ) );


								}
								else if ( Utilities.isValidDateTimeFormat( this.strGlobalDateTimeFormat.trim(), Logger ) == false ) {

									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is no valid date and time format, using the default value [%s]", ConstantsCommonConfigXMLTags._Global_Date_Time_Format, NodeAttribute.getNodeValue().trim(), this.strGlobalDateTimeFormat ) );

								}
								else {
									
									this.strGlobalDateTimeFormat = NodeAttribute.getNodeValue().trim();
									
							        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strGlobalDateTimeFormat", this.strGlobalDateTimeFormat ) );
									
								}

							}
							
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Global_Date_Format ) ) {
							
							if ( NodeAttribute.getNodeValue() != null ) {
							
								if ( NodeAttribute.getNodeValue().trim().isEmpty() == true ) {

									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Global_Date_Format, this.strGlobalDateFormat ) );


								}
								else if ( Utilities.isValidDateTimeFormat( this.strGlobalDateFormat.trim(), Logger ) == false ) {

									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is no valid date format, using the default value [%s]", ConstantsCommonConfigXMLTags._Global_Date_Format, NodeAttribute.getNodeValue().trim(), this.strGlobalDateFormat ) );

								}
								else {
									
									this.strGlobalDateFormat = NodeAttribute.getNodeValue().trim();
									
							        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strGlobalDateFormat", this.strGlobalDateFormat ) );
									
								}

							}
							
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Global_Time_Format ) ) {
							
							if ( NodeAttribute.getNodeValue() != null ) {
							
								if ( NodeAttribute.getNodeValue().trim().isEmpty() == true ) {

									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Global_Time_Format, this.strGlobalTimeFormat ) );


								}
								else if ( Utilities.isValidDateTimeFormat( this.strGlobalDateFormat.trim(), Logger ) == false ) {

									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is no valid time format, using the default value [%s]", ConstantsCommonConfigXMLTags._Global_Time_Format, NodeAttribute.getNodeValue().trim(), this.strGlobalTimeFormat ) );

								}
								else {
									
									this.strGlobalTimeFormat = NodeAttribute.getNodeValue().trim();
									
							        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strGlobalTimeFormat", this.strGlobalTimeFormat ) );
									
								}

							}
							
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Temp_Dir ) ) {

							this.strTempDir = NodeAttribute.getNodeValue();
		
					        if ( this.strTempDir != null && this.strTempDir.isEmpty() == false && new File( this.strTempDir ).isAbsolute() == false ) {

					        	this.strTempDir = this.strRunningPath + this.strTempDir;
						        	
						    }

					        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strTempDir", this.strTempDir ) );
				        
					        if ( Utilities.checkDir( this.strTempDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Services_Dir ) ) {

							this.strServicesDir = NodeAttribute.getNodeValue();
		
					        if ( this.strServicesDir != null && this.strServicesDir.isEmpty() == false && new File( this.strServicesDir ).isAbsolute() == false ) {

					        	this.strServicesDir = this.strRunningPath + this.strServicesDir;
						        	
						    }

					        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strServicesDir", this.strServicesDir ) );
				        
					        if ( Utilities.checkDir( this.strServicesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._DBDrivers_Dir ) ) {

							this.strDBDriversDir = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strDBDriversDir != null && this.strDBDriversDir.isEmpty() == false && new File( this.strDBDriversDir ).isAbsolute() == false ) {

					        	this.strDBDriversDir = this.strRunningPath + this.strDBDriversDir;
						        	
						    }

					        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strDBDriversDir", this.strDBDriversDir ) );
						
					        if ( Utilities.checkDir( this.strDBDriversDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						    
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._DBEngines_Dir ) ) {

							this.strDBEnginesDir = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strDBEnginesDir != null && this.strDBEnginesDir.isEmpty() == false && new File( this.strDBEnginesDir ).isAbsolute() == false ) {

					        	this.strDBEnginesDir = this.strRunningPath + this.strDBEnginesDir;
						        	
						    }

					        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strDBEnginesDir", this.strDBEnginesDir ) );
						
					        if ( Utilities.checkDir( this.strDBEnginesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						    
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Responses_Formats_Dir ) ) {

							this.strResponsesFormatsDir = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strResponsesFormatsDir != null && this.strResponsesFormatsDir.isEmpty() == false && new File( this.strResponsesFormatsDir ).isAbsolute() == false ) {
	                        
					        	this.strResponsesFormatsDir = this.strRunningPath + this.strResponsesFormatsDir;
						        	
						    }

					        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strResponsesFormatsDir", this.strResponsesFormatsDir ) );
						
					        if ( Utilities.checkDir( this.strResponsesFormatsDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Default_Response_Format ) ) {
						
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	this.strDefaultResponseFormat = NodeAttribute.getNodeValue();
						    	
						        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strDefaultResponseFormat", this.strDefaultResponseFormat ) );

						    }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Default_Response_Format, this.strDefaultResponseFormat ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) ) {
							
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	this.strDefaultResponseFormatVersion = NodeAttribute.getNodeValue();
						    	
						        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strDefaultResponseFormatVersion", this.strDefaultResponseFormatVersion ) );

						    }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Default_Response_Format_Version, this.strDefaultResponseFormatVersion ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Response_Request_Method ) ) {
							
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

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
					        		
						           Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s,%s]", ConstantsCommonConfigXMLTags._Response_Request_Method, ConstantsCommonConfigXMLTags._Request_Method_ANY, ConstantsCommonConfigXMLTags._Request_Method_OnlyGET, ConstantsCommonConfigXMLTags._Request_Method_OnlyPOST ) );
						           Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Response_Request_Method, NodeAttribute.getNodeValue(), this.strResponseRequestMethod ) );
					        		
					        	}
						    	
						    }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Response_Request_Method, this.strResponseRequestMethod ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Internal_Fetch_Size ) ) {
							
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	if ( net.maindataservices.Utilities.checkStringIsInteger( NodeAttribute.getNodeValue(), this.Logger ) ) {
					        	
					        		int intTempInternalFetchSize = Integer.parseInt( NodeAttribute.getNodeValue() );
					        		
					        		if ( intTempInternalFetchSize > 0 ) {
					        		
					        			this.intInternalFetchSize = intTempInternalFetchSize;

					        			Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "intInternalFetchSize", Integer.toString( this.intInternalFetchSize ) ) );

					        		}
					        		else {
					        			
								        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Internal_Fetch_Size, NodeAttribute.getNodeValue(), Integer.toString( this.intInternalFetchSize ) ) );
					        			
					        		}
					        		
					        	}
					        	else {

							        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Internal_Fetch_Size, NodeAttribute.getNodeValue(), Integer.toString( this.intInternalFetchSize ) ) );

					        	}
						    	
						    }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Internal_Fetch_Size, Integer.toString( this.intInternalFetchSize ) ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Request_Timeout ) ) {

					        if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
					        
					        	int intTmpRequestTimeout = net.maindataservices.Utilities.strToInteger( NodeAttribute.getNodeValue().trim() );
					        	
					        	if ( intTmpRequestTimeout >= ConstantsCommonClasses._Minimal_Request_Timeout && intTmpRequestTimeout <= ConstantsCommonClasses._Maximal_Request_Timeout ) {
					        		
					        		intRequestTimeout = intTmpRequestTimeout;
					        		
					        	}
					        	else {
					        		
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( intTmpRequestTimeout ), Integer.toString( ConstantsCommonClasses._Minimal_Request_Timeout ), Integer.toString( ConstantsCommonClasses._Maximal_Request_Timeout ) ) );
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( intTmpRequestTimeout ), Integer.toString( ConstantsCommonClasses._Request_Timeout ) ) );
									
					        	}
					        	
					        }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( this.intRequestTimeout ) ) );
					        	
					        }
							
						}	
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Socket_Timeout ) ) {

					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	int intTmpSocketTimeout = net.maindataservices.Utilities.strToInteger( NodeAttribute.getNodeValue().trim() );
					        	
					        	if ( intTmpSocketTimeout >= ConstantsCommonClasses._Minimal_Socket_Timeout && intTmpSocketTimeout <= ConstantsCommonClasses._Maximal_Socket_Timeout ) {
					        		
					        		intSocketTimeout = intTmpSocketTimeout;
					        		
					        	}
					        	else {
					        		
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( intTmpSocketTimeout ), Integer.toString( ConstantsCommonClasses._Minimal_Socket_Timeout ), Integer.toString( ConstantsCommonClasses._Maximal_Socket_Timeout ) ) );
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( intTmpSocketTimeout ), Integer.toString( ConstantsCommonClasses._Socket_Timeout ) ) );
									
					        	}
						        
					        }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( this.intSocketTimeout ) ) );
					        	
					        }
					        
						}	
		            
		            }
		            else {

				        if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Global_Date_Time_Format ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Global_Date_Time_Format, this.strGlobalDateTimeFormat ) );
		            		
		            	}
				        else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Global_Date_Format ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Global_Date_Format, this.strGlobalDateFormat ) );
		            		
		            	}
				        else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Global_Time_Format ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Global_Time_Format, this.strGlobalTimeFormat ) );
		            		
		            	}
				        else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsConfigXMLTags._Services_Dir ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsConfigXMLTags._Services_Dir, this.strServicesDir ) );

					        if ( Utilities.checkDir( this.strServicesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Temp_Dir ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Temp_Dir, this.strTempDir ) );

					        if ( Utilities.checkDir( this.strTempDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsConfigXMLTags._DBDrivers_Dir ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsConfigXMLTags._DBDrivers_Dir, this.strDBDriversDir ) );

					        if ( Utilities.checkDir( this.strDBDriversDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsConfigXMLTags._DBEngines_Dir ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsConfigXMLTags._DBEngines_Dir, this.strDBEnginesDir ) );

					        if ( Utilities.checkDir( this.strDBEnginesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Responses_Formats_Dir ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Responses_Formats_Dir, this.strResponsesFormatsDir ) );

					        if ( Utilities.checkDir( this.strResponsesFormatsDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Default_Response_Format ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Default_Response_Format, this.strDefaultResponseFormat ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Default_Response_Format_Version, this.strDefaultResponseFormatVersion ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Response_Request_Method ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Response_Request_Method, this.strResponseRequestMethod ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Internal_Fetch_Size ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Internal_Fetch_Size, Integer.toString( this.intInternalFetchSize ) ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Request_Timeout ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( this.intRequestTimeout ) ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Socket_Timeout ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( this.intSocketTimeout ) ) );
		            		
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
	
	public boolean loadConfigSectionRegisterServices( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {
		
        boolean bResult = true;
        
        try {
        	
			String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Password, ConstantsCommonConfigXMLTags._URL, ConstantsCommonConfigXMLTags._Proxy_IP, ConstantsCommonConfigXMLTags._Proxy_Port, ConstantsCommonConfigXMLTags._Proxy_User, ConstantsCommonConfigXMLTags._Proxy_Password, ConstantsCommonConfigXMLTags._Interval, ConstantsCommonConfigXMLTags._Weight, ConstantsCommonConfigXMLTags._ReportLoad, ConstantsCommonConfigXMLTags._ReportIPType };

			NodeList ConfigRegisterServiceList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigRegisterServiceList.getLength() > 0 ) {
			
	            for ( int intConfigRegisterIndex = 0; intConfigRegisterIndex < ConfigRegisterServiceList.getLength(); intConfigRegisterIndex++ ) {
	                
	            	Node ConfigRegisterServicesNode = ConfigRegisterServiceList.item( intConfigRegisterIndex );
	                 
	    			Logger.logMessage( "1", Lang.translate( "Reading XML built in response format: [%s]", ConfigRegisterServicesNode.getNodeName() ) );        
	                 
	    			if ( ConfigRegisterServicesNode.getNodeName().equals( ConstantsCommonConfigXMLTags._Register ) == true ) {

	    				String strPassword = "";
						String strURL = "";
						String strProxyIP = "";
					    int intProxyPort = 0;
					    String strProxyUser = "";
					    String strProxyPassword = "";
						int intInterval = 0;
						int intWeight = 0;
						boolean bReportLoad = false;
						int intReportIPType = 0; //all

	    				if ( ConfigRegisterServicesNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigRegisterServicesNode.getAttributes();

							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

								if ( NodeAttribute != null  ) {

									Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
									Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

									if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Password ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strPassword = NodeAttribute.getNodeValue().trim();
											
										}
										else {
											
									    	Logger.logError( "-1002", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Password ) );
											break;
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._URL ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strURL = NodeAttribute.getNodeValue().trim();
											
										}
										else {
											
									    	Logger.logError( "-1003", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._URL ) );
											break;
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_IP ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

											if ( Utilities.isValidIP( NodeAttribute.getNodeValue() ) == true ) {

												strProxyIP = NodeAttribute.getNodeValue();

											}
											else {

												Logger.logError( "-1004", Lang.translate( "The [%s] attribute value [%s] is not valid ip address", ConstantsCommonConfigXMLTags._Proxy_IP, NodeAttribute.getNodeValue() ) );
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

												intProxyPort = intTmpPort;

											}
											else {

												Logger.logError( "-1005", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Proxy_Port, NodeAttribute.getNodeValue(), Integer.toString( ConstantsCommonClasses._Min_Port_Number ), Integer.toString( ConstantsCommonClasses._Max_Port_Number ) ) );
												break; //Stop parse more attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_Port ) );
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_User ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strProxyUser = NodeAttribute.getNodeValue().trim();
											
										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_User ) );
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_Password ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strProxyPassword = NodeAttribute.getNodeValue().trim();
											
										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_Password ) );
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Interval ) ) {

										intInterval = Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );
										
										if ( intInterval < ConstantsCommonClasses._Minimal_Register_Manager_Frecuency ) {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Interval, Integer.toString( intInterval ), Integer.toString( ConstantsCommonClasses._Register_Manager_Frecuency ) ) );
											
											intInterval = ConstantsCommonClasses._Register_Manager_Frecuency;
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Weight ) ) {

										intWeight = Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );
										
										if ( intWeight < 1 ) {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Weight, Integer.toString( intWeight ), "1" ) );

											intWeight = 1;
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._ReportLoad ) ) {

										bReportLoad = NodeAttribute.getNodeValue().trim().equals( "true" );

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._ReportIPType ) ) {

										if ( NodeAttribute.getNodeValue().trim().equals( ConstantsCommonConfigXMLTags._IPV4 ) ) {
											
											intReportIPType = 1; //IPV4
											
										}
										else if ( NodeAttribute.getNodeValue().trim().equals( ConstantsCommonConfigXMLTags._IPV6 ) ) {
											
											intReportIPType = 2; //IPV6
											
										}
										else {

											intReportIPType = 0; //IPAll
											
										}

									}

								}
								else { //The attribute is obligatory 
									
							    	Logger.logError( "-1001", Lang.translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConstantsCommonConfigXMLTags._Register, Integer.toString( intConfigRegisterIndex ) ) );
							    	break;
							    	
								}

							}
							
						}

	    				if ( strPassword.isEmpty() == false && strURL.isEmpty() == false && intInterval >= ConstantsCommonClasses._Minimal_Register_Manager_Frecuency && intWeight >= 1 ) {
							
	    					CConfigRegisterService ConfigRegisterService = new CConfigRegisterService();
	    					
	    					ConfigRegisterService.strPassword = strPassword;
	    					ConfigRegisterService.strURL = strURL;

	    					if ( strProxyIP.isEmpty() == false ) {
	    					
	    						ConfigRegisterService.ConfigProxy.strProxyIP = strProxyIP;
	    						ConfigRegisterService.ConfigProxy.intProxyPort = intProxyPort;
	    						
	    						if ( ConfigRegisterService.ConfigProxy.strProxyUser.isEmpty() == false ) {
	    							
	    							ConfigRegisterService.ConfigProxy.strProxyUser = strProxyUser;
	    							ConfigRegisterService.ConfigProxy.strProxyPassword = strProxyPassword;
	    							
	    						}
	    						
	    					}
							
	    					ConfigRegisterService.intInterval = intInterval;
	    					ConfigRegisterService.intWeight = intWeight;
	    					ConfigRegisterService.bReportLoad = bReportLoad;
	    					ConfigRegisterService.intReportIPType = intReportIPType;
	    					
	    					ConfiguredRegisterServices.add( ConfigRegisterService );
	    					
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
    
	public boolean loadConfigSectionBuiltinResponsesFormats( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {
		
        boolean bResult = true;
        
        try {
        	
			String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Name, ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonConfigXMLTags._Fields_Quote, ConstantsCommonConfigXMLTags._Separator_Symbol, ConstantsCommonConfigXMLTags._Show_Headers };

			NodeList ConfigBuiltinResponsesList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigBuiltinResponsesList.getLength() > 0 ) {
			
	            for ( int intConfigBuiltinResponseFormatIndex = 0; intConfigBuiltinResponseFormatIndex < ConfigBuiltinResponsesList.getLength(); intConfigBuiltinResponseFormatIndex++ ) {
	                
	            	Node ConfigBuiltinResponseFormatNode = ConfigBuiltinResponsesList.item( intConfigBuiltinResponseFormatIndex );
	                 
	    			Logger.logMessage( "1", Lang.translate( "Reading XML built in response format: [%s]", ConfigBuiltinResponseFormatNode.getNodeName() ) );        
	                 
				    String strBuitinResponseName = "";
	    			
	    			if ( ConfigBuiltinResponseFormatNode.getNodeName().equals( ConstantsCommonConfigXMLTags._BuiltinResponseFormat ) == true ) {

						if ( ConfigBuiltinResponseFormatNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigBuiltinResponseFormatNode.getAttributes();

							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

								if ( NodeAttribute != null  ) {

									Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
									Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

									if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Name ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strBuitinResponseName = NodeAttribute.getNodeValue().trim().toUpperCase();
											
										}
										else {
											
									    	Logger.logError( "-1002", Lang.translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConstantsCommonConfigXMLTags._Name, ConstantsCommonConfigXMLTags._BuiltinResponseFormat, Integer.toString( intConfigBuiltinResponseFormatIndex ) ) );
											
										}
										
									}
									else if ( strBuitinResponseName.equals( ConstantsCommonConfigXMLTags._ResponseFormat_XML_DATAPACKET ) ) {

										if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strXML_DataPacket_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strXML_DataPacket_CharSet", this.strXML_DataPacket_CharSet ) );

											}
											else {

												Logger.logError( "-1003", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonClasses._Chaset_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strXML_DataPacket_ContentType = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strXML_DataPacket_ContentType", this.strXML_DataPacket_ContentType ) );

											}
											else {

												Logger.logError( "-1004", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonClasses._Content_Type_XML ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}
									else if ( strBuitinResponseName.equals( ConstantsCommonConfigXMLTags._ResponseFormat_JAVA_XML_WEBROWSET ) ) {

										if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJavaXML_WebRowSet_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strJavaXML_WebRowSet_CharSet", this.strJavaXML_WebRowSet_CharSet ) );

											}
											else {

												Logger.logError( "-1005", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonClasses._Chaset_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJavaXML_WebRowSet_ContentType = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strJavaXML_WebRowSet_ContentType", this.strJavaXML_WebRowSet_ContentType ) );

											}
											else {

												Logger.logError( "-1006", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonClasses._Content_Type_XML ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}
									else if ( strBuitinResponseName.equals( ConstantsCommonConfigXMLTags._ResponseFormat_JSON ) ) {

										if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJSON_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strJSON_CharSet", this.strJSON_CharSet ) );

											}
											else {

												Logger.logError( "-1007", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonClasses._Chaset_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJSON_ContentType = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strJSON_ContentType", this.strJSON_ContentType ) );

											}
											else {

												Logger.logError( "-1008", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonClasses._Content_Type_XML ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}
									else if ( strBuitinResponseName.equals( ConstantsCommonConfigXMLTags._ResponseFormat_CSV ) ) {

										if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strCSV_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strCSV_CharSet", this.strCSV_CharSet ) );

											}
											else {

												Logger.logError( "-1009", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonClasses._Chaset_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strCSV_ContentType = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strCSV_ContentType", this.strCSV_ContentType ) );

											}
											else {

												Logger.logError( "-1010", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonClasses._Content_Type_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Fields_Quote ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.bCSV_FieldQuoted = NodeAttribute.getNodeValue().equals( "true" );
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "bCSV_FieldQuoted", Boolean.toString( this.bCSV_FieldQuoted ) ) );

											}
											else {

												Logger.logError( "-1010", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Fields_Quote, Boolean.toString( ConstantsCommonClasses._Fields_Quote_CSV ) ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Separator_Symbol ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strCSV_SeparatorSymbol = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strCSV_SeparatorSymbol", this.strCSV_SeparatorSymbol ) );

											}
											else {

												Logger.logError( "-1010", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Separator_Symbol, ConstantsCommonClasses._Separator_Symbol_CSV ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Show_Headers ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.bCSV_ShowHeaders = NodeAttribute.getNodeValue().equals( "true" );
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "bCSV_ShowHeaders", Boolean.toString( this.bCSV_ShowHeaders ) ) );

											}
											else {

												Logger.logError( "-1010", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Show_Headers, Boolean.toString( ConstantsCommonClasses._Show_Headers_CSV ) ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}

								}
								else if ( intAttributesIndex == 0 ) { //Only the name attribute is obligatory 
									
							    	Logger.logError( "-1001", Lang.translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConstantsCommonConfigXMLTags._Name, ConstantsCommonConfigXMLTags._BuiltinResponseFormat, Integer.toString( intConfigBuiltinResponseFormatIndex ) ) );
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
        	
			Logger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;

	}

    public boolean loadConfigSectionDBConnections( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {
		
        boolean bResult = false;
        
        try {

			String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Name, ConstantsConfigXMLTags._Driver, ConstantsConfigXMLTags._Engine, ConstantsConfigXMLTags._Engine_Version, ConstantsCommonConfigXMLTags._IP, ConstantsCommonConfigXMLTags._Port, ConstantsCommonConfigXMLTags._Database, ConstantsConfigXMLTags._Auto_Commit, ConstantsConfigXMLTags._Dummy_SQL, ConstantsConfigXMLTags._Auth_Type, ConstantsConfigXMLTags._SessionUser, ConstantsConfigXMLTags._SessionPassword, ConstantsConfigXMLTags._TransactionUser, ConstantsConfigXMLTags._TransactionPassword, ConstantsCommonConfigXMLTags._Date_Format, ConstantsCommonConfigXMLTags._Time_Format, ConstantsCommonConfigXMLTags._Date_Time_Format };

			NodeList ConfigConnectionsList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigConnectionsList.getLength() > 0 ) {
			
	        	int intConnectionIndex = 0;
	        	
	            for ( int intLocalConfigConnectionIndex = 0; intLocalConfigConnectionIndex < ConfigConnectionsList.getLength(); intLocalConfigConnectionIndex++ ) {
	                
	            	Node ConfigConnectionNode = ConfigConnectionsList.item( intLocalConfigConnectionIndex );
	                 
	    			Logger.logMessage( "1", Lang.translate( "Reading XML database connection: [%s]", ConfigConnectionNode.getNodeName() ) );        
	                 
					if ( ConfigConnectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._DBConnection ) == true ) {

						String strName = "";
						String strDriver = "";
						String strEngine = "";
						String strEngineVersion = "";
						String strIP = "";
						String strAddressType = "";
						int intPort = -1;
						String strDatabase = "";
						boolean bAutoCommit = false;
						String strDummySQL = "";
						String strAuthType = "";
						String strSessionUser = "";
						String strSessionPassword = "";
						String strTransactionUser = "";
						String strTransactionPassword = "";
						String strDateFormat = "";
						String strTimeFormat = "";
						String strDateTimeFormat = "";
						
						if ( ConfigConnectionNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigConnectionNode.getAttributes();

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
											
											Logger.logError( "-1002", Lang.translate( "The [%s] attribute for connection number [%s] config cannot empty string", ConstantsCommonConfigXMLTags._Name, Integer.toString( intConnectionIndex ) ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Driver ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDriver = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logError( "-1003", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsConfigXMLTags._Driver, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Engine ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strEngine = NodeAttribute.getNodeValue().toLowerCase();
										
										}
										else {
											
											Logger.logError( "-1004", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsConfigXMLTags._Engine, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Engine_Version ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strEngineVersion = NodeAttribute.getNodeValue().toLowerCase();
										
										}
										else {
											
											Logger.logError( "-1005", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsConfigXMLTags._Engine_Version, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._IP ) ) {
									
										if ( Utilities.isValidIPV4( NodeAttribute.getNodeValue() ) == true ) {
										
										    strIP = NodeAttribute.getNodeValue();
										    strAddressType = "ipv4";
									
										}
										else if ( Utilities.isValidIPV6( NodeAttribute.getNodeValue() ) == true ) {
										
											strIP = NodeAttribute.getNodeValue();
											strAddressType = "ipv6";
	
										}
							            else {
							            	
											Logger.logError( "-1006", Lang.translate( "The [%s] attribute value [%s] is not valid ip address", CommonClasses.ConstantsCommonConfigXMLTags._IP, NodeAttribute.getNodeValue() ) );
											break; //Stop parse more attributes
											
							            }

									}	
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Port ) ) {

										int intTmpPort = Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );
											
										if ( intTmpPort >= CommonClasses.ConstantsCommonClasses._Min_Port_Number && intTmpPort <= CommonClasses.ConstantsCommonClasses._Max_Port_Number ) {
												
											intPort = intTmpPort;
												
										}
										else {
												
											Logger.logError( "-1007", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", CommonClasses.ConstantsCommonConfigXMLTags._Port, NodeAttribute.getNodeValue(), Integer.toString( CommonClasses.ConstantsCommonClasses._Min_Port_Number ), Integer.toString( CommonClasses.ConstantsCommonClasses._Max_Port_Number ) ) );
											break; //Stop parse more attributes
												
										}
									    
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Database ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDatabase = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logError( "-1008", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsCommonConfigXMLTags._Database, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Auto_Commit ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											bAutoCommit = NodeAttribute.getNodeValue().equals( "1" );
										
										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsConfigXMLTags._Auto_Commit ) );
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Dummy_SQL ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDummySQL = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsConfigXMLTags._Dummy_SQL ) );
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._Auth_Type ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsConfigXMLTags._Auth_Type_Database ) || NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsConfigXMLTags._Auth_Type_Engine ) ) {    
											 
											    strAuthType = NodeAttribute.getNodeValue().toLowerCase();
											
											}
											else {
												
												Logger.logError( "-1009", Lang.translate( "The [%s] attribute value for connection [%s] must be only one of the next values: [%s,%s]", ConstantsConfigXMLTags._Auth_Type, strName, ConstantsConfigXMLTags._Auth_Type_Engine, ConstantsConfigXMLTags._Auth_Type_Database ) );
												break; //Stop parse more attributes
												
											}
										
										}
										else {
											
											Logger.logError( "-1010", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsConfigXMLTags._Auth_Type, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._SessionUser ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strSessionUser = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logError( "-1011", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsConfigXMLTags._SessionUser, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._SessionPassword ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strSessionPassword = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logError( "-1012", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsConfigXMLTags._SessionPassword, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._TransactionUser ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strTransactionUser = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsConfigXMLTags._TransactionUser ) );
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._TransactionPassword ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strTransactionPassword = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsConfigXMLTags._TransactionPassword ) );
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Date_Format ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDateFormat = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logError( "-1013", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsCommonConfigXMLTags._Date_Format, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Time_Format ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strTimeFormat = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logError( "-1014", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsCommonConfigXMLTags._Time_Format, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Date_Time_Format ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strDateTimeFormat = NodeAttribute.getNodeValue();
										
										}
										else {
											
											Logger.logError( "-1015", Lang.translate( "The [%s] attribute value for connection [%s] config cannot empty string", ConstantsCommonConfigXMLTags._Date_Time_Format, strName ) );
											break; //Stop parse more attributes
											
										}
										
									}
					            
					            }
					            else {
					            	
							    	Logger.logError( "-1001", Lang.translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConfigConnectionNode.getNodeName(), Integer.toString( intLocalConfigConnectionIndex ) ) );
							    	break;
					            	
					            }
					            
					        }
					        
					        if ( strName.isEmpty() == false && strDriver.isEmpty() == false && strEngine.isEmpty() == false && strEngineVersion.isEmpty() == false && strIP.isEmpty() == false && intPort != -1 && strDatabase.isEmpty() == false && strAuthType.isEmpty() == false && strSessionUser.isEmpty() == false && strSessionPassword.isEmpty() == false && strDateFormat.isEmpty() == false && strTimeFormat.isEmpty() == false && strDateTimeFormat.isEmpty() == false ) {
					        
			                    intConnectionIndex += 1;
					        	boolean bNameUsed = false;
					        	int intPreviusDBConnectionIndex = -1;
					        	
					            //Check for duplicate names DB connections
					        	for ( int intIndex = 0; intIndex <  this.ConfiguredDBConnections.size(); intIndex++ ) {
					        		
					        		CConfigNativeDBConnection DBConnection = this.ConfiguredDBConnections.get( intIndex );
					        		
					        		if ( DBConnection.strName.toLowerCase().equals( strName.toLowerCase() ) ) {

					        			intPreviusDBConnectionIndex = intIndex;
					        			bNameUsed = true;
					        			break;
					        			
					        		}
					        		
					        	}
					        	
                                if ( bNameUsed == false ) {

                                	boolean bDBDriverTest = false;
                                	
                        			CClassPathLoader ClassPathLoader = new CClassPathLoader();

                        			//Load the databases drivers
                        			ClassPathLoader.loadClassFiles( this.strDBDriversDir, ConstantsCommonClasses._Lib_Ext, 2, Logger, Lang  );

                        			try {

                                		Class.forName( strDriver );
                                    
                                		bDBDriverTest = true;
                                		
                                	} 
                                	catch ( Exception Ex ) {
                                    
                            			Logger.logException( "-1024", Ex.getMessage(), Ex );
                                    
                                	}

                                	if ( bDBDriverTest == true ) {
	                                	
                                	    CConfigNativeDBConnection DBConnection = new CConfigNativeDBConnection();
	                                	DBConnection.strName = strName;
	                                	DBConnection.strDriver = strDriver;
	                                	DBConnection.strEngine = strEngine;
	                                	DBConnection.strEngineVersion = strEngineVersion;
	                                	DBConnection.strIP = strIP;
	                                	DBConnection.strAddressType = strAddressType;
	                                	DBConnection.intPort = intPort;
	                                	DBConnection.strDatabase = strDatabase;
	                                	DBConnection.bAutoCommit = bAutoCommit;
	                                	DBConnection.strDummySQL = strDummySQL;
	                                	DBConnection.strAuthType = strAuthType;
	                                	DBConnection.strSessionUser = strSessionUser;
	                                	DBConnection.strSessionPassword = strSessionPassword;
	                                	DBConnection.strTransactionUser = strTransactionUser;
	                                	DBConnection.strTransactionPassword = strTransactionPassword;
	                                	DBConnection.strDateFormat = strDateFormat;
	                                	DBConnection.strTimeFormat = strTimeFormat;
	                                	DBConnection.strDateTimeFormat = strDateTimeFormat;
	
	                                	this.ConfiguredDBConnections.add( DBConnection );
	
	                                	Logger.logMessage( "1", Lang.translate( "Connection database defined and added. Name: [%s], Driver: [%s], Engine: [%s], Engine_Version: [%s], IP: [%s], Address_Type: [%s], Port: [%s], Database: [%s], AutoCommit: [%s], DummySQL: [%s], SessionUser: [%s], SessionPassword: [%s], TransactionUser: [%s], TransactionPassword: [%s], DateFormat: [%s], TimeFormat: [%s], DateTimeFormat: [%s]", strName, strDriver, strEngine, strEngineVersion, strIP, strAddressType, Integer.toString( intPort ), strDatabase, bAutoCommit==true?"true":"false", strDummySQL, strSessionUser, strSessionPassword, strTransactionUser, strTransactionPassword, strDateFormat, strTimeFormat, strDateTimeFormat ) );

                                	}
                                	else {

    					            	Logger.logError( "-1015", Lang.translate( "The database connection driver [%s] for the connection name: [%s] is invalid or not found in path [%s]", strDriver, strName, this.strDBDriversDir ) );
                                		
                                	}
	                                	
                                }
                                else {
                                	
					            	Logger.logError( "-1016", Lang.translate( "The DB connection name: [%s] is used in previus DB connection config index: [%s], the name attribute must be unique", strName, Integer.toString( intPreviusDBConnectionIndex ) ) );
                                	
                                }
				            	
					        }
					        else {
					        	
					            Logger.logError( "-1017", Lang.translate( "DB connection config attributes is not valid" ) );
					        	
					        }
						
						}
					
					}
	            
	            }
	            
	            if ( this.ConfiguredDBConnections.isEmpty() == true ) {
	            	
	    			Logger.logError( "-1018", Lang.translate( "No valid database connections defined" ) );        
	            	
	            }
	            else {
	            
	            	bResult = true;
	            
	            }
	        
	        }
        
        }
		catch ( Exception Ex ) {
			
			Logger.logException( "-1025", Ex.getMessage(), Ex );
			
		}

		Logger.logMessage( "1", Lang.translate( "Count of database connections defined: [%s]", Integer.toString( this.ConfiguredDBConnections.size() ) ) );        

		return bResult;

	}

	@Override
	public boolean loadConfigSection( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = true;

		Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals(  CommonClasses.ConstantsCommonConfigXMLTags._System ) == true ) {
           
			if ( this.loadConfigSectionSystem( ConfigSectionNode, Logger, Lang ) == false ) {
				
    			Logger.logError( "-1001", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._RegisterServices ) == true ) {

			if ( this.loadConfigSectionRegisterServices( ConfigSectionNode, Logger, Lang ) == false ) {
				
    			Logger.logError( "-1002", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	
        }	
        else if ( ConfigSectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._BuiltinResponsesFormats ) == true ) {

        	if ( this.loadConfigSectionBuiltinResponsesFormats( ConfigSectionNode, Logger, Lang ) == false ) {
        		
    			Logger.logError( "-1003", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
        		
        	}
             
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._DBConnections ) == true ) {

        	if ( this.loadConfigSectionDBConnections( ConfigSectionNode, Logger, Lang ) == false ) {
        		
    			Logger.logError( "-1004", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
        		
        	}
             
        }
		
		return bResult;
		
	}
	
    @Override
	public Object sendMessage( String strMessageName, Object MessageData ) {

    	try {
    	
    		if ( strMessageName.equals( ConstantsMessagesCodes._XML_DataPacket_CharSet ) )
    			return this.strXML_DataPacket_CharSet;
    		else if ( strMessageName.equals( ConstantsMessagesCodes._XML_DataPacket_ContentType ) )
    			return this.strXML_DataPacket_ContentType;
    		else if ( strMessageName.equals( ConstantsMessagesCodes._JavaXML_WebRowSet_CharSet ) )
    			return this.strJavaXML_WebRowSet_CharSet;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._JavaXML_WebRowSet_ContentType ) )
    			return this.strJavaXML_WebRowSet_ContentType;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._JSON_CharSet ) )
    			return this.strJSON_CharSet;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._JSON_ContentType ) )
    			return this.strJSON_ContentType;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_CharSet ) )
    			return this.strCSV_CharSet;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_ContentType ) )
    			return this.strCSV_ContentType;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_FieldsQuote ) )
    			return Boolean.toString( this.bCSV_FieldQuoted );  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_SeparatorSymbol ) )
    			return this.strCSV_SeparatorSymbol;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_ShowHeaders ) )
    			return Boolean.toString( this.bCSV_ShowHeaders );  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Global_DateTime_Format ) )
    			return this.strGlobalDateTimeFormat;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Global_Date_Format ) )
    			return this.strGlobalDateFormat;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Global_Time_Format ) )
    			return this.strGlobalTimeFormat;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Temp_Dir ) )
    			return this.strTempDir;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Internal_Fetch_Size ) )
    			return Integer.toString( this.intInternalFetchSize ); 
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Security_Manager_Name ) )
    			return ConstantsServicesManager._Security_Manager_Name;
    		else if ( strMessageName.equals( ConstantsMessagesCodes._getConfiguredNativeDBConnection ) )
    			return this.getConfiguredNativeDBConnection( (String) MessageData );
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Default_Response_Format ) )
    			return this.strDefaultResponseFormat; 
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Default_Response_Format_Version ) )
    			return this.strDefaultResponseFormatVersion;
    		else	
    			return "";
    	
    	}
    	catch ( Exception Ex ) {
    		
    		return "";
    		
    	}
		
	}
    
	/*public boolean LoadConfig( String strConfigFilePath, CLanguage Lang, CExtendedLogger Logger ) {
		
        if ( DBServicesManagerLogger == null )
        	DBServicesManagerLogger = Logger;

        if ( DBServicesManagerLang == null )
        	DBServicesManagerLang = Lang;

	    return super.LoadConfig( strConfigFilePath, Logger, Lang );
	
	}*/
	
}
