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

import java.util.HashMap;

import DBServicesManager.CConfigDBConnection;
import ExtendedLogger.CExtendedLogger;

public class CSessionInfoManager {

	protected static CSessionInfoManager SessionInfoManager = null;
	
	static {
		
		SessionInfoManager = new CSessionInfoManager();
		
	}
	
	public static CSessionInfoManager getSessionInfoManager() {
		
		return SessionInfoManager;
		
	}

	protected HashMap<String,String> SecurityTokenIDFromName = null; //Name => SecurityTokenID  
	
	protected HashMap<String,String> NameFromSecurityTokenID = null; //SecurityTokenID => Name 

	protected HashMap<String,String> SecurityTokenIDFromSessionID = null; //SessionID => SecurityTokenID 
	 
	protected HashMap<String,String> SessionIDFromSecurityTokenID = null; //SecurityTokenID => SessionID  

	protected HashMap<String,CConfigDBConnection> ConfigDBConnectionFromSecurityTokenID = null; //SecurityTokenID => CConfigDBConnection 
	
	
	public CSessionInfoManager() {

		SecurityTokenIDFromName = new HashMap<String,String>();
	
		NameFromSecurityTokenID = new HashMap<String,String>();
		
		SecurityTokenIDFromSessionID = new HashMap<String,String>();

		SessionIDFromSecurityTokenID = new HashMap<String,String>();

		ConfigDBConnectionFromSecurityTokenID = new HashMap<String,CConfigDBConnection>(); 
		
	}

	public synchronized boolean addSecurityTokenIDToName( String strName, String strSecurityTokenID, String strSessionID, CExtendedLogger Logger, CLanguage Lang ) {

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
		
	}

	public synchronized boolean addConfigDBConnectionToSecurityTokenID( String strSecurityTokenID, CConfigDBConnection ConfigDBConnection, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult =  false;

		if ( ConfigDBConnectionFromSecurityTokenID.containsKey( strSecurityTokenID ) == false ) {

			ConfigDBConnectionFromSecurityTokenID.put( strSecurityTokenID, ConfigDBConnection );

			if ( Logger != null && Lang != null ) {
				
				Logger.LogMessage( "1" , Lang.Translate( "Added [%s] = [%s] and [%s] = [%s]", "SecurityTokenID", strSecurityTokenID, "ConfigDBConnection", ConfigDBConnection.strName ) );
				
			}

			bResult = true;
			
		}
		
		return bResult;
		
	}
	
	public synchronized boolean removeSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
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
		
		return bResult;
		
	} 
	
	public CConfigDBConnection getConfigDBConnectionFromSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {
		
		CConfigDBConnection ConfigDBConnection = ConfigDBConnectionFromSecurityTokenID.get( strSecurityTokenID );
		
		if ( ConfigDBConnection == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "ConfigDBConnection", "SecurityTokenID", strSecurityTokenID ) );
			
		}
		
		return ConfigDBConnection;
		
	}
	
	public CConfigDBConnection getConfigDBConnectionFromName( String strName, CExtendedLogger Logger, CLanguage Lang ) {
		
        String strSecurityTokenID = SecurityTokenIDFromName.get( strName ); 
		
		CConfigDBConnection ConfigDBConnection = ConfigDBConnectionFromSecurityTokenID.get( strSecurityTokenID );

		if ( ConfigDBConnection == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "ConfigDBConnection", "SecurityTokenID", strSecurityTokenID ) );
			
		}
		
		return ConfigDBConnection;
		
	}
	
	public String getNameFromSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {
		
		String strName = NameFromSecurityTokenID.get( strSecurityTokenID );
		
		if ( strName == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "Name", "SecurityTokenID", strSecurityTokenID ) );
			
		}
		
		return strName;
		
	}
	
	public String getSecurityTokenIDFromName( String strName, CExtendedLogger Logger, CLanguage Lang ) {
  
		String strSecurityTokenID = SecurityTokenIDFromName.get( strName );
		
		if ( strName == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "Name", strName ) );
			
		}

		return strSecurityTokenID;
		
	}

	public String getSecurityTokenIDFromSessionID( String strSessionID, CExtendedLogger Logger, CLanguage Lang ) {
		  
		String strSecurityTokenID = SecurityTokenIDFromSessionID.get( strSessionID );
		
		if ( strSessionID == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "SessionID", strSessionID ) );
			
		}

		return strSecurityTokenID;
		
	}

	public String getSecurityTokenIDFromSessionIDAndName( String strSessionID, String strName, CExtendedLogger Logger, CLanguage Lang ) {
		  
		String strSecurityTokenID1 = SecurityTokenIDFromSessionID.get( strSessionID );
		
		String strSecurityTokenID2  = SecurityTokenIDFromName.get( strName );
		
		if ( ( strSessionID == null || strName == null ) && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "SessionID/Name", strSessionID ) );
			
		}

		if ( strSecurityTokenID1 != null && strSecurityTokenID2 != null && strSecurityTokenID1.equals( strSecurityTokenID2 ) )
		    return strSecurityTokenID1;
		else
			return null;
		
	}
	
}
