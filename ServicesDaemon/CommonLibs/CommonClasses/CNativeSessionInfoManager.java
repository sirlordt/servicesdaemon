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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import AbstractDBEngine.IAbstractDBConnection;
import AbstractDBEngine.CAbstractDBEngine;
import ExtendedLogger.CExtendedLogger;

public class CNativeSessionInfoManager implements Serializable {

	private static final long serialVersionUID = -7029733166283200287L;

	protected static CNativeSessionInfoManager SessionInfoManager = null;
	
	/*static {
		
		SessionInfoManager = new CSessionInfoManager();
		
	}*/
	
	public static CNativeSessionInfoManager getSessionInfoManager() {
		
		if ( SessionInfoManager == null ) {
			
			SessionInfoManager = new CNativeSessionInfoManager();
			
		}
		
		return SessionInfoManager;
		
	}

	public static void setSessionInfoManager( CNativeSessionInfoManager newSessionInfoManager ) {
		
		if ( newSessionInfoManager != null )
			SessionInfoManager = newSessionInfoManager;
		
	}

	public static synchronized boolean forceEndSession( String strSecurityManagerName, String strSecurityTokenID, boolean bReplicateToCluster, CNativeSessionInfoManager SessionInfoManager, CConfigNativeDBConnection LocalConfigDBConnection, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		if ( LocalConfigDBConnection != null ) {

			CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ); 

			if ( DBEngine != null ) {

				CNativeDBConnectionsManager DBConnectionsManager = CNativeDBConnectionsManager.getNativeDBConnectionManager();

				ArrayList<String> TransactionsID = new ArrayList<String>(); 
				ArrayList<String> TransactionsIDFromManager = DBConnectionsManager.getTransactionsIDFromSecurityTokenID( strSecurityTokenID, Logger, Lang );

				if ( TransactionsIDFromManager != null )
					TransactionsID.addAll( TransactionsIDFromManager );

				SessionInfoManager.removeSecurityTokenID( strSecurityTokenID, bReplicateToCluster, Logger, Lang );

				CSecurityTokensManager SecurityTokensManager = CSecurityTokensManager.getSecurityTokensManager( strSecurityManagerName );

				SecurityTokensManager.removeSecurityTokenID( strSecurityTokenID, Logger, Lang );

				bResult = true;

				for ( String strCurrentTransactionID : TransactionsID ) {

					//Semaphore DBConnectionSemaphore = DBConnectionsManager.getNativeDBConnectionSemaphore( strCurrentTransactionID, Logger, Lang );
					IAbstractDBConnection DBConnection = DBConnectionsManager.getDBConnection( strCurrentTransactionID, Logger, Lang );

					//if ( DBConnectionSemaphore != null ) {

						try {

							DBConnectionsManager.removeNativeDBConnectionByTransactionId( strCurrentTransactionID, Logger, Lang );

							DBConnection.lockConnection( false, Logger, Lang ); //Blocks another threads to use this connection
							//DBConnectionSemaphore.acquire(); //Blocks another threads to use this connection

							try {  

								DBEngine.rollback( DBConnection, Logger, Lang );

								DBEngine.close( DBConnection, Logger, Lang );
								
								Logger.logInfo( "0x1504", Lang.translate( "Success rollback and end transaction with SessionKey: [%s], SecurityTokenID: [%s], TransactionID: [%s], Database: [%s]", LocalConfigDBConnection.strSessionKey, strSecurityTokenID, strCurrentTransactionID, LocalConfigDBConnection.strName ) );        

							}
							catch ( Exception Ex ) {

								bResult = false;

								if ( Logger != null )
									Logger.logException( "-1024", Ex.getMessage(), Ex ); 

							}

							DBConnection.unlockConnection( Logger, Lang ); //Release another threads to use this connection
							//DBConnectionSemaphore.release(); //Release another threads to use this connection

						}
						catch ( Exception Ex ) {

							bResult = false;

							if ( Logger != null )
								Logger.logException( "-1023", Ex.getMessage(), Ex ); 

						}

					/*}                        
					else {

						if ( Logger != null ) {

							Logger.LogError( "-1004", Lang.Translate( "The database connection semaphore not found for transaction id: [%s]", strCurrentTransactionID ) );        

						}

						bResult = false;

					}*/

				}  //end for

			}
			else {

				if ( Logger != null ) {

					Logger.logError( "-1003", Lang.translate( "The database engine name [%s] version [%s] not found", LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ) );        

				}

			}

		}
		else {

			if ( Logger != null ) {

				Logger.logError( "-1002", Lang.translate( "Cannot locate in session the database connection config for the security token id: [%s]", strSecurityTokenID ) );        

			}

		}
		
		return bResult;
		
	}
	
	/*protected HashMap<String,String> SecurityTokenIDFromName = null; //Name => SecurityTokenID  
	
	protected HashMap<String,String> NameFromSecurityTokenID = null; //SecurityTokenID => Name 

	protected HashMap<String,String> SecurityTokenIDFromSessionID = null; //SessionID => SecurityTokenID 
	 
	protected HashMap<String,String> SessionIDFromSecurityTokenID = null; //SecurityTokenID => SessionID  

	protected HashMap<String,CConfigDBConnection> ConfigDBConnectionFromSecurityTokenID = null; //SecurityTokenID => CConfigDBConnection*/ 
	
	protected LinkedHashMap<String,CNativeSessionInfo> SessionsInfoList = null; //SecurityTokenID => CSessionInfo
	
	public CNativeSessionInfoManager() {

		/*SecurityTokenIDFromName = new HashMap<String,String>();
	
		NameFromSecurityTokenID = new HashMap<String,String>();
		
		SecurityTokenIDFromSessionID = new HashMap<String,String>();

		SessionIDFromSecurityTokenID = new HashMap<String,String>();

		ConfigDBConnectionFromSecurityTokenID = new HashMap<String,CConfigDBConnection>();*/
		
		SessionsInfoList = new LinkedHashMap<String,CNativeSessionInfo>();
		
	}

	public CNativeSessionInfoManager( LinkedHashMap<String,CNativeSessionInfo> SessionsInfoList ) {
		
		SessionsInfoList = new LinkedHashMap<String,CNativeSessionInfo>( SessionsInfoList );
		
	}
	
	public synchronized LinkedHashMap<String,CNativeSessionInfo> getSessionsInfoList() {
		
		return SessionsInfoList;
		
	}
	
	public synchronized void setSessionsInfoList( LinkedHashMap<String,CNativeSessionInfo> SessionsInfoList ) {
		
		if ( SessionsInfoList != null )
			this.SessionsInfoList = SessionsInfoList; //Overwrite the session info list
		
	}
	
	public synchronized void addSessionInfo( CNativeSessionInfo NativeSessionInfoToAdd, boolean bReplicateToCluster, CExtendedLogger Logger, CLanguage Lang ) {
		
		SessionsInfoList.put( NativeSessionInfoToAdd.strSecurityTokenID, NativeSessionInfoToAdd );
			
		if ( Logger != null && Lang != null ) {
			
			Logger.logMessage( "1" , Lang.translate( "Added [%s] = [%s] and [%s] = [%s]", "Name", NativeSessionInfoToAdd.strName, "SecurityTokenID", NativeSessionInfoToAdd.strSecurityTokenID ) );
			
		}
		
		if ( bReplicateToCluster ) {
			
			//Replicate to Cluster
			
		}
		
	}
	
	/*public synchronized boolean addSecurityTokenIDToName( String strName, String strSecurityTokenID, String strSessionID, CExtendedLogger Logger, CLanguage Lang ) {

		for ( Entry<String,CNativeSessionInfo> ItemInList : SessionsInfoList.entrySet() ) {
			
			CNativeSessionInfo SessionInfo = ItemInList.getValue();
			
			if ( SessionInfo.strName.equals( strName ) ) {
				
				return false; //break the method call
				
			}
			
		}
		
		CNativeSessionInfo SessionInfo = new CNativeSessionInfo();
		SessionInfo.strName = strName;
		SessionInfo.strSecurityTokenID = strSecurityTokenID;
		SessionInfo.strSessionID = strSessionID;
		SessionInfo.ConfigNativeDBConnection = null;
		
		if ( Logger != null && Lang != null ) {
			
			Logger.LogMessage( "1" , Lang.Translate( "Added [%s] = [%s] and [%s] = [%s]", "Name", strName, "SecurityTokenID", strSecurityTokenID ) );
			
		}

		return true;
		
		\*
		
		boolean bResult = false;

		if ( SecurityTokenIDFromName.containsKey( strName ) == false ) {

			SecurityTokenIDFromName.put( strName, strSecurityTokenID ); 

			NameFromSecurityTokenID.put( strSecurityTokenID, strName );
			
			SecurityTokenIDFromName.put( strSessionID, strSecurityTokenID );

			SessionIDFromSecurityTokenID.put( strSecurityTokenID, strSessionID );
			
			if ( Logger != null && Lang != null ) {
				
				Logger.LogMessage( "1" , Lang.Translate( "Added [%s] = [%s] and [%s] = [%s]", "Name", strName, "SecurityTokenID", strSecurityTokenID ) );
				
			}

			bResult = true;
			
		}

		return bResult;
		
		*\
		
	}

	public synchronized boolean addConfigNativeDBConnectionToSecurityTokenID( String strSecurityTokenID, CConfigNativeDBConnection ConfigNativeDBConnection, CExtendedLogger Logger, CLanguage Lang ) {

		CNativeSessionInfo SessionInfo = SessionsInfoList.get( strSecurityTokenID );
		
		if ( SessionInfo != null ) {
			
			SessionInfo.ConfigNativeDBConnection = ConfigNativeDBConnection;
			
			if ( Logger != null && Lang != null ) {
				
				Logger.LogMessage( "1" , Lang.Translate( "Added [%s] = [%s] and [%s] = [%s]", "SecurityTokenID", strSecurityTokenID, "ConfigDBConnection", ConfigNativeDBConnection.strName ) );
				
			}
			
			return true;
			
		}
		
		return false;
		
		\*boolean bResult =  false;

		if ( ConfigDBConnectionFromSecurityTokenID.containsKey( strSecurityTokenID ) == false ) {

			ConfigDBConnectionFromSecurityTokenID.put( strSecurityTokenID, ConfigDBConnection );

			if ( Logger != null && Lang != null ) {
				
				Logger.LogMessage( "1" , Lang.Translate( "Added [%s] = [%s] and [%s] = [%s]", "SecurityTokenID", strSecurityTokenID, "ConfigDBConnection", ConfigDBConnection.strName ) );
				
			}

			bResult = true;
			
		}
		
		return bResult;*\
		
	}*/
	
	public synchronized boolean removeSecurityTokenID( String strSecurityTokenID, boolean bReplicateToCluster, CExtendedLogger Logger, CLanguage Lang ) {
		
		CNativeSessionInfo SessionInfo = SessionsInfoList.get( strSecurityTokenID );
		
		if ( SessionInfo != null ) {
			
			SessionInfo.ConfigNativeDBConnection = null;
			
			if ( Logger != null && Lang != null ) {
				
				Logger.logWarning( "-1" , Lang.translate( "Removed [%s] by [%s] = [%s]", "ConfigDBConnection", "SecurityTokenID", strSecurityTokenID ) );
				Logger.logWarning( "-1" , Lang.translate( "Removed [%s] by [%s] = [%s]", "Name", "SecurityTokenID", strSecurityTokenID ) );
				Logger.logWarning( "-1" , Lang.translate( "Removed [%s] by [%s] = [%s]", "SecurityTokenID", "Name", SessionInfo.strName ) );
				
			}

			SessionsInfoList.remove( strSecurityTokenID );
			
			SessionInfo = null;
			
			if ( bReplicateToCluster == true ) {
				
				
				
			}
			
			return true;
			
		}
		
		return false;
		
		/*boolean bResult = false;
		
		if ( ConfigDBConnectionFromSecurityTokenID.containsKey( strSecurityTokenID ) ) {

			ConfigDBConnectionFromSecurityTokenID.remove( strSecurityTokenID );
			
			if ( Logger != null && Lang != null ) {
				
				Logger.LogWarning( "-1" , Lang.Translate( "Removed [%s] by [%s] = [%s]", "ConfigDBConnection", "SecurityTokenID", strSecurityTokenID ) );
				
			}
		
		}
		
		if ( NameFromSecurityTokenID.containsKey( strSecurityTokenID ) ) {
		
			String strName = NameFromSecurityTokenID.get( strSecurityTokenID );
			
			NameFromSecurityTokenID.remove( strSecurityTokenID );
			
			SecurityTokenIDFromName.remove( strName );
			
			String strSessionID = SessionIDFromSecurityTokenID.get( strSecurityTokenID  );
			
			SessionIDFromSecurityTokenID.remove( strSecurityTokenID );
			
			SecurityTokenIDFromSessionID.remove( strSessionID );
			
			if ( Logger != null && Lang != null ) {
				
				Logger.LogWarning( "-1" , Lang.Translate( "Removed [%s] by [%s] = [%s]", "Name", "SecurityTokenID", strSecurityTokenID ) );
				Logger.LogWarning( "-1" , Lang.Translate( "Removed [%s] by [%s] = [%s]", "SecurityTokenID", "Name", strName ) );
				
			}
			
		}
		
		return bResult;*/
		
	}
	
	public CConfigNativeDBConnection getConfigNativeDBConnectionFromSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {
		
		CNativeSessionInfo SessionInfo = SessionsInfoList.get( strSecurityTokenID );
		
		if ( SessionInfo != null && SessionInfo.ConfigNativeDBConnection != null ) {

		    return SessionInfo.ConfigNativeDBConnection;
		
		}	
		
		if ( ( SessionInfo == null || SessionInfo.ConfigNativeDBConnection == null ) && Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "ConfigDBConnection", "SecurityTokenID", strSecurityTokenID ) );
			
		}

		return null;
		
		/*CConfigDBConnection ConfigDBConnection = ConfigDBConnectionFromSecurityTokenID.get( strSecurityTokenID );
		
		if ( ConfigDBConnection == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "ConfigDBConnection", "SecurityTokenID", strSecurityTokenID ) );
			
		}
		
		return ConfigDBConnection;*/
		
	}
	
	public CConfigNativeDBConnection getConfigNativeDBConnectionFromName( String strName, CExtendedLogger Logger, CLanguage Lang ) {
		
		for ( Entry<String,CNativeSessionInfo> ItemInList : SessionsInfoList.entrySet() ) {
			
			CNativeSessionInfo SessionInfo = ItemInList.getValue();
			
			if ( SessionInfo.strName.equals( strName ) ) {
				
				if ( SessionInfo.ConfigNativeDBConnection != null ) {

					return SessionInfo.ConfigNativeDBConnection; //break the method call
					
				}	
				else {
					
					break;
					
				}	
				
			}
			
		}
		
		if ( Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "ConfigDBConnection", "Name", strName ) );
			
		}

		return null;
		
        /*String strSecurityTokenID = SecurityTokenIDFromName.get( strName ); 
		
		CConfigDBConnection ConfigDBConnection = ConfigDBConnectionFromSecurityTokenID.get( strSecurityTokenID );

		if ( ConfigDBConnection == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "ConfigDBConnection", "SecurityTokenID", strSecurityTokenID ) );
			
		}
		
		return ConfigDBConnection;*/
		
	}
	
	public String getNameFromSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {
		
		CNativeSessionInfo SessionInfo = SessionsInfoList.get( strSecurityTokenID );
		
		if ( SessionInfo != null ) {

		    return SessionInfo.strName;
		
		}	
		
		if (Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "Name", "SecurityTokenID", strSecurityTokenID ) );
			
		}
		
		return null;
		
		/*String strName = NameFromSecurityTokenID.get( strSecurityTokenID );
		
		if ( strName == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "Name", "SecurityTokenID", strSecurityTokenID ) );
			
		}
		
		return strName;*/
		
	}
	
	public String getSecurityTokenIDFromName( String strName, CExtendedLogger Logger, CLanguage Lang ) {

		for ( Entry<String,CNativeSessionInfo> ItemInList : SessionsInfoList.entrySet() ) {
			
			CNativeSessionInfo SessionInfo = ItemInList.getValue();
			
			if ( SessionInfo.strName.equals( strName ) ) {
				
				return SessionInfo.strSecurityTokenID; //break the method call
				
			}
			
		}

		if ( Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "Name", strName ) );
			
		}
		
		return null;
		
		/*String strSecurityTokenID = SecurityTokenIDFromName.get( strName );
		
		if ( strName == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "Name", strName ) );
			
		}

		return strSecurityTokenID;*/
		
	}

	public String getSecurityTokenIDFromSessionID( String strSessionID, CExtendedLogger Logger, CLanguage Lang ) {
		  
		for ( Entry<String,CNativeSessionInfo> ItemInList : SessionsInfoList.entrySet() ) {
			
			CNativeSessionInfo SessionInfo = ItemInList.getValue();
			
			if ( SessionInfo.strSessionID.equals( strSessionID ) ) {
				
				return SessionInfo.strSecurityTokenID; //break the method call
				
			}
			
		}
		
		if ( Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "SessionID", strSessionID ) );
			
		}

		return null;
		
		/*String strSecurityTokenID = SecurityTokenIDFromSessionID.get( strSessionID );
		
		if ( strSecurityTokenID == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "SessionID", strSessionID ) );
			
		}

		return strSecurityTokenID;*/
		
	}

	public String getSecurityTokenIDFromSessionIDAndName( String strSessionID, String strName, CExtendedLogger Logger, CLanguage Lang ) {
		  
		for ( Entry<String,CNativeSessionInfo> ItemInList : SessionsInfoList.entrySet() ) {
			
			CNativeSessionInfo SessionInfo = ItemInList.getValue();
			
			if ( SessionInfo.strSessionID.equals( strSessionID ) &&  SessionInfo.strName.equals( strName ) ) {
				
				return SessionInfo.strSecurityTokenID; //break the method call
				
			}
			
		}
		
		if ( Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "SessionID/Name", strSessionID + "/" + strName ) );
			
		}
		
		return null;
		
		/*String strSecurityTokenID1 = SecurityTokenIDFromSessionID.get( strSessionID );
		
		String strSecurityTokenID2  = SecurityTokenIDFromName.get( strName );
		
		if ( ( strSessionID == null || strName == null ) && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "SessionID/Name", strSessionID ) );
			
		}

		if ( strSecurityTokenID1 != null && strSecurityTokenID2 != null && strSecurityTokenID1.equals( strSecurityTokenID2 ) )
		    return strSecurityTokenID1;
		else
			return null;*/
		
	}
	
}
