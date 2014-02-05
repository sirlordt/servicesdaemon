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
package SystemEndSession;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CConfigServicesDaemon;
import ExtendedLogger.CExtendedLogger;

public class CConfigSystemEndSession extends CAbstractConfigLoader {

	protected static CConfigSystemEndSession SystemEndSessionConfig = null;

	public static CConfigSystemEndSession getSystemEndSessionConfig( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig, String strRunningPath ) {
		
		if ( SystemEndSessionConfig == null ) {
			
			SystemEndSessionConfig = new CConfigSystemEndSession( strRunningPath );
			
		}
		
		SystemEndSessionConfig.OwnerConfig = OwnerConfig;

		SystemEndSessionConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemEndSessionConfig;
		
	}
	
	public CAbstractConfigLoader OwnerConfig = null;
	public CConfigServicesDaemon ServicesDaemonConfig = null;
	
	public CExtendedLogger ServiceLogger =  null;
	public CLanguage ServiceLang = null;

	public CConfigSystemEndSession( String strRunningPath ) {
		
		super( strRunningPath );
		
	}

	@Override
	public boolean loadConfig( String strConfigFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
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
