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

import java.util.ArrayList;

import ExtendedLogger.CExtendedLogger;

public class CSecurityTokensManager {

	protected static CSecurityTokensManager SecurityTokensManager = null;
	
	static {
		
		SecurityTokensManager = new CSecurityTokensManager();
		
	}
	
	public static CSecurityTokensManager getSecurityTokensManager() {
		
		return SecurityTokensManager;
		
	}
	
	protected ArrayList<String> SecurityTokensListID = null;
	
	public CSecurityTokensManager() {
		
		SecurityTokensListID = new ArrayList<String>();
		
	}

	public synchronized boolean addSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		if ( SecurityTokensListID.contains( strSecurityTokenID ) == false ) {
			
			SecurityTokensListID.add( strSecurityTokenID );

			if ( Logger != null && Lang != null ) {
				
				Logger.LogMessage( "1" , Lang.Translate( "Added [%s] = [%s]", "SecurityTokenID", strSecurityTokenID ) );
				
			}

			bResult = true;
			
		}
		
		return bResult;
		
	}

	public synchronized boolean removeSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		if ( SecurityTokensListID.contains( strSecurityTokenID ) ) {
			
			SecurityTokensListID.remove( strSecurityTokenID );

			if ( Logger != null && Lang != null ) {
				
				Logger.LogWarning( "-1" , Lang.Translate( "[%s] = [%s] removed", "SecurityTokenID", strSecurityTokenID ) );
				
			}

			bResult = true;
			
		}
		
		return bResult;
		
	}
	
	public synchronized boolean checkSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = SecurityTokensListID.contains( strSecurityTokenID );
		
		if ( bResult == false && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] = [%s] not found", "SecurityTokenID", strSecurityTokenID ) );
			
		}

		return bResult;
		
	}
	
}
