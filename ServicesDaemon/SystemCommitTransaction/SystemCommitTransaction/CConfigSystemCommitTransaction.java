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
package SystemCommitTransaction;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CConfigServicesDaemon;
import ExtendedLogger.CExtendedLogger;

public class CConfigSystemCommitTransaction extends CAbstractConfigLoader {

	protected static CConfigSystemCommitTransaction SystemCommitTransactionConfig = null;

	public static CConfigSystemCommitTransaction getSystemCommitTransactionConfig( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig, String strRunningPath ) {
		
		if ( SystemCommitTransactionConfig == null ) {
			
			SystemCommitTransactionConfig = new CConfigSystemCommitTransaction( strRunningPath );
			
		}
		
		SystemCommitTransactionConfig.OwnerConfig = OwnerConfig;

		SystemCommitTransactionConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemCommitTransactionConfig;
		
	}
	
	public CAbstractConfigLoader OwnerConfig = null;
	public CConfigServicesDaemon ServicesDaemonConfig = null;
	
	public CExtendedLogger ServiceLogger =  null;
	public CLanguage ServiceLang = null;

	public CConfigSystemCommitTransaction( String strRunningPath ) {
		
		super( strRunningPath );
		
	}

	@Override
	public boolean loadConfig( String strConfigFilePath, CLanguage Lang, CExtendedLogger Logger ) {
		
        if ( ServiceLogger == null )
        	ServiceLogger = Logger;

        if ( ServiceLang == null )
        	ServiceLang = Lang;

        this.strConfigFilePath = strConfigFilePath;
        
	    return true; //do nothing always true
	
	}
	
	@Override
	public Object sendMessage(String strMessageName, Object MessageData) {

		return "";
		
	}
	
}
