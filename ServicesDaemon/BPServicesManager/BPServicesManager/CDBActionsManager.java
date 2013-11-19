package BPServicesManager;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import CommonClasses.CLanguage;
import CommonClasses.NamesSQLTypes;
import ExtendedLogger.CExtendedLogger;

public class CDBActionsManager {
	
	public ArrayList<CDBAction> DBActions; 
	
	public String strDBActionsFilePath = "";
	
	CDBActionsManager() {
		
		//Don't Load in memory wait for manual call to method LoadDBActionsFromFile
		
		DBActions = new ArrayList<CDBAction>(); 
		
		//this.strDBActionsFilePath = strDBActionsFilePath;
		
	}
	
	CDBActionsManager( String strDBActionsFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
		DBActions = new ArrayList<CDBAction>(); 
		
		LoadDBActionsFromFile( strDBActionsFilePath, Logger, Lang );
		
	}
	
	public boolean LoadDBActionsFromFile( String strDBActionsFilePath, final CExtendedLogger Logger, final CLanguage Lang ) {
		
		boolean bResult = false;
		
		this.strDBActionsFilePath = strDBActionsFilePath;

		DBActions.clear();
		
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler XMLParserHandler = new DefaultHandler() {

				boolean bActionDBBlock = false;
				boolean bCommandTag = false;
				boolean bMapTag = false;
				boolean bInputParamTag = false;
				String strDataType = "";
				CDBAction DBAction = null;
				
				public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException {

					if ( qName.equals( ConfigXMLTagsDBActions._ActionDB ) ) {
						
						bActionDBBlock = true;
						
						if ( attributes.getLength() == 2 ) {
							
							DBAction = new CDBAction(); 
							
							for ( int intIndexAttrib = 0; intIndexAttrib < attributes.getLength(); intIndexAttrib++ ) {
								
								String strAttribName = attributes.getQName( intIndexAttrib );
								
								if ( strAttribName.equals( ConfigXMLTagsDBActions._ID ) ) {
									
									DBAction.strID = attributes.getValue( intIndexAttrib );
									
								}
								else if ( strAttribName.equals( ConfigXMLTagsDBActions._Description ) ) {
									
									DBAction.strDescription = attributes.getValue( intIndexAttrib );
									
								}
								else {
									
									break;
									
								}
								
							}
							
						}
						
					}
					else if ( bActionDBBlock ) {
						
						if ( qName.equals( ConfigXMLTagsDBActions._Command ) ) {

							bCommandTag = true;
							
						}
						else if ( qName.equals( ConfigXMLTagsDBActions._Map ) ) {
							
							bMapTag = true;
							
						}
						else if ( qName.equals( ConfigXMLTagsDBActions._InputParam ) ) {
							
							if ( attributes.getLength() == 1 ) {
							
								String strAttribName = attributes.getQName( 0 );
								
								if ( strAttribName.equals( ConfigXMLTagsDBActions._DataType ) ) {
									
									strDataType = attributes.getValue( 0 );
									
									if ( NamesSQLTypes.CheckJavaSQLType( strDataType ) ) {
									    
										bInputParamTag = true;
										
									}
								
								}
								
							}

						}
						
					}
					
				}

				
				public void endElement( String uri, String localName, String qName ) throws SAXException {

					if ( qName.equals( ConfigXMLTagsDBActions._ActionDB ) ) {
						
						if ( DBAction.strID.isEmpty() == false && DBAction.strCommand.isEmpty() == false ) {
							
							if ( checkDBActionForId( DBAction.strID, Logger, Lang ) == false ) {
							
							    DBActions.add( DBAction );
							
								Logger.LogMessage( "1", Lang.Translate( "Added the DBAction to list ID = [%s]", DBAction.strID ) );
							
							}
							else {
								
								Logger.LogError( "-1001", Lang.Translate( "The DBAction ID [%s] already exists in the list from DB actions. This ID must be unique for config file!", DBAction.strID ) );
								
							}
							
						}
						else {
							
							Logger.LogWarning( "-1", Lang.Translate( "The DBAction in URI [%s] contains invalid or empty values, ID = [%s] and Command = [%s]", uri, DBAction.strID, DBAction.strCommand ) );
							
						}
						
						bActionDBBlock = false;
						bCommandTag = false;
						bMapTag = false;
						bInputParamTag = false;
						strDataType = "";
						DBAction = null;
						
					}

				}

				public void characters( char ch[], int start, int length ) throws SAXException {

					if ( bActionDBBlock ) {
					
						String strValue = new String( ch, start, length );
						
						if ( bCommandTag ) {

							DBAction.strCommand = strValue;

							bCommandTag = false;
							
						}
						else if ( bInputParamTag || bMapTag ) {
							
							if ( DBAction.KeywordsMarkedForMap.contains( strValue ) == false ) {

								if ( DBAction.InputParams.get( strValue ) == null ) {
								
									if ( bInputParamTag )
										DBAction.InputParams.put( strValue, strDataType );
									else
										DBAction.KeywordsMarkedForMap.add( strValue );
									
								}
								else {

									Logger.LogWarning( "-1", Lang.Translate( "The value [%s] is already in the list for input parameters", strValue ) );
									
								}
								
							}
							else {
								
								Logger.LogWarning( "-1", Lang.Translate( "The value [%s] is already in the list keywords marked for maps", strValue ) );
								
							}
							
							bInputParamTag = false;
							bMapTag = false;
							
						}
					
					}

				}
				
				
			};

			saxParser.parse( strDBActionsFilePath, XMLParserHandler );

			Logger.LogMessage( "1", Lang.Translate( "Count of DBActions registered: [%s]", Integer.toString( DBActions.size() ) ) );
			
			bResult = true;
			
		} 
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}		
		
		return bResult;
		
	}

	boolean checkDBActionForId( String strID, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		try {
		
			Logger.LogMessage( "1", Lang.Translate( "Check for exists for DBAction ID = [%s]", strID ) );

			if ( DBActions != null ) {

				Logger.LogMessage( "1", Lang.Translate( "Count of DBActions registered: [%s]", Integer.toString( DBActions.size() ) ) );

				for ( int I =0; I < DBActions.size(); I++ ) {

					if ( DBActions.get( I ).strID.equals( strID ) ) {

						bResult = true;

						Logger.LogMessage( "1", Lang.Translate( "DBAction ID = [%s] exists", strID ) );

						break;

					}

				}

				if ( bResult == false )
					Logger.LogWarning( "-1", Lang.Translate( "DBAction ID = [%s] not exists", strID ) );

			}
			else {

				Logger.LogWarning( "-1", Lang.Translate( "DBActions list is null" ) );

			}
		
		} 
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}		

		return bResult;
		
	}
	
	CDBAction getDBActionForId( String strID, CExtendedLogger Logger, CLanguage Lang ) {
		
		CDBAction DBAction = null;
		
		try {
		
			Logger.LogMessage( "1", Lang.Translate( "Get DBAction ID = [%s]", strID ) );

			if ( DBActions != null ) {

				Logger.LogMessage( "1", Lang.Translate( "Count of DBActions registered: [%s]", Integer.toString( DBActions.size() ) ) );

				for ( int I =0; I < DBActions.size(); I++ ) {

					if ( DBActions.get( I ).strID.equals( strID ) ) {

						DBAction = DBActions.get( I );

						Logger.LogMessage( "1", Lang.Translate( "DBAction ID = [%s] found and returned", strID ) );

						break;

					}

				}

				if ( DBAction == null )
					Logger.LogWarning( "-1", Lang.Translate( "DBAction ID = [%s] not found", strID ) );

			}
			else {

				Logger.LogWarning( "-1", Lang.Translate( "DBActions list is null" ) );

			}
		
		} 
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}		

		return DBAction;
		
	}

	CDBAction getDBActionCopyForId( String strID, CExtendedLogger Logger, CLanguage Lang ) {
		
		CDBAction DBAction = null;

		try {
		
			Logger.LogMessage( "1", Lang.Translate( "Get DBAction copy ID = [%s]", strID ) );

			if ( DBActions != null ) {

				Logger.LogMessage( "1", Lang.Translate( "Count of DBActions registered: [%s]", Integer.toString( DBActions.size() ) ) );

				for ( int I =0; I < DBActions.size(); I++ ) {

					if ( DBActions.get( I ).strID.equals( strID ) ) {

						DBAction = new CDBAction( DBActions.get( I ) ); //Create clone copy

						Logger.LogMessage( "1", Lang.Translate( "DBAction ID = [%s] found and copied", strID ) );

						break;

					}

				}

				if ( DBAction == null )
					Logger.LogWarning( "-1", Lang.Translate( "DBAction ID = [%s] not found", strID ) );

			}
			else {

				Logger.LogWarning( "-1", Lang.Translate( "DBActions list is null" ) );

			}
		
		} 
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}		
		
		return DBAction;
		
	}

}
