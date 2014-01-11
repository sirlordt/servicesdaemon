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

import ExtendedLogger.CExtendedLogger;

public class CSecurityTokensManager implements Serializable {

	private static final long serialVersionUID = -8619961063144856850L;
	
	protected static LinkedHashMap<String,CSecurityTokensManager> SecurityTokensManagerList = null;
	
	static {
		
		SecurityTokensManagerList = new LinkedHashMap<String,CSecurityTokensManager>();
		
	}
	
	public static CSecurityTokensManager getSecurityTokensManager( String strManagerName ) {
		
		CSecurityTokensManager SecurityTokensManager = SecurityTokensManagerList.get( strManagerName );
		
		if ( SecurityTokensManager == null ) {
			
			SecurityTokensManager = new CSecurityTokensManager( strManagerName );
			
			SecurityTokensManagerList.put( strManagerName, SecurityTokensManager );
			
		}
		/*else {
			
			SecurityTokensManager = SecurityTokensManagerList.get( intManagerLevel );
			
			SecurityTokensManager.intManagerLevel = SecurityTokensManagerList.size() - 1;
			
		}*/
		
		return SecurityTokensManager;
		
	}
	
	protected String strManagerName;
	protected ArrayList<String> ListOfSecurityTokensID = null;
	
	public CSecurityTokensManager( String strManagerName ) {
		
		this.strManagerName = strManagerName;
		ListOfSecurityTokensID = new ArrayList<String>();
		
	}

	public CSecurityTokensManager( String strManagerName, ArrayList<String> ListOfSecurityTokensID ) {
		
		this.strManagerName = strManagerName;
		this.ListOfSecurityTokensID = new ArrayList<String>( ListOfSecurityTokensID ); //Clone list
		
	}

	public String getManagerName() {
		
		return strManagerName;
		
	}
	
	public synchronized ArrayList<String> getListOfSecurityTokensID() {
		
		return ListOfSecurityTokensID;
		
	}
	
	public synchronized void setListOfSecurityTokensID( ArrayList<String> ListOfSecurityTokensID ) {
		
		this.ListOfSecurityTokensID = ListOfSecurityTokensID; //Overwrite the list of known SecurityTokensID
		
	}

	public synchronized boolean addSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		if ( ListOfSecurityTokensID.contains( strSecurityTokenID ) == false ) {
			
			ListOfSecurityTokensID.add( strSecurityTokenID );

			if ( Logger != null && Lang != null ) {
				
				Logger.logMessage( "1" , Lang.translate( "Added [%s] = [%s]", "SecurityTokenID", strSecurityTokenID ) );
				
			}

			bResult = true;
			
		}
		
		return bResult;
		
	}

	public synchronized boolean removeSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		if ( ListOfSecurityTokensID.contains( strSecurityTokenID ) ) {
			
			ListOfSecurityTokensID.remove( strSecurityTokenID );

			if ( Logger != null && Lang != null ) {
				
				Logger.logWarning( "-1" , Lang.translate( "[%s] = [%s] removed", "SecurityTokenID", strSecurityTokenID ) );
				
			}

			bResult = true;
			
		}
		
		return bResult;
		
	}
	
	public synchronized boolean checkSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = ListOfSecurityTokensID.contains( strSecurityTokenID );
		
		if ( bResult == false && Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] = [%s] not found", "SecurityTokenID", strSecurityTokenID ) );
			
		}

		return bResult;
		
	}
	
}
