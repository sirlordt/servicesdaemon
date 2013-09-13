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

import java.io.File;

public class DefaultConstantsServicesDaemon {

	public static void InitDefaultConstants() {
		
		strDefaultLibsDir = "Libs" + File.separator;

		strDefaultLangsDir = "Langs" + File.separator;

		strDefaultLogsSystemDir = "Logs" + File.separator + "System" + File.separator;
		strDefaultLogsAccessDir = "Logs" + File.separator + "Access" + File.separator;
		
	    strDefaultManagersDir = "Managers" + File.separator;
		
	}
	
	public static String strDefaultRunningPath = "";
	
	public static String strDefaultLibsDir = ""; //"Libs" + File.separator;
	public static final String strDefaultLibsExt = ".jar";
	
	public static String strDefaultLangsDir = ""; //"Langs" + File.separator;
	public static final String strDefaultLang = "init.lang";
	public static final String strDefaultCommonLang = "Common.init.lang";

	public static final String strDefaultMainFile = "ServicesDaemon";
	public static final String strDefaultMainFileLog = strDefaultMainFile + ".log";
	public static final String strDefaultMainJettyFileLog = strDefaultMainFile + ".servlet.container.log";
	public static final String strDefaultMainFileAccessLog = strDefaultMainFile + ".accesss.yyyy_mm_dd.log";

	public static final String strDefaultLoggerName = "ServicesDaemonLogger"; 
	public static final String strDefaultJettyLoggerName = "JettyLogger"; 
	public static final String strDefaultJettyLoggerClassName = "org.eclipse.jetty.util.log"; 
	public static final String strDefaultConfFile = strDefaultMainFile + ".conf";
	public static String strDefaultLogsSystemDir = ""; //"Logs" + File.separator + "System" + File.separator;
	public static String strDefaultLogsAccessDir = ""; //"Logs" + File.separator + "Access" + File.separator;
	public static final String strDefaultLogClassMethod = "*.*";
	public static final boolean bDefaultLogExactMatch = false;
	public static final boolean bDefaultLogMissingTranslations = false;
	public static final String strDefaultLoggerNameMissingTranslations = "MissingTranslations";
	public static final String strDefaultMisssingTranslationsFileLog = strDefaultLoggerNameMissingTranslations + ".log";
	public static final String strDefaultLogLevel = "ALL";
	
    public static final String strDefaultServletContext = "/*";

    public static String strDefaultManagersDir = ""; //"Managers" + File.separator;
    public static final String strDefaultManagersExt = ".jar";
    public static final String strDefaultKeyStoreFile = strDefaultMainFile + ".keystore";
    public static final String strDefaultDefaultPassword = "12345678";
    
	public static final String strDefaultIPV4All = "0.0.0.0";
	public static final String strDefaultIPV6All = "0.0.0.0";

	public static final int intDefaultMinPortNumber = 1;
	public static final int intDefaultMaxPortNumber = 65535;
	
	public static final int intDefaultMinIdleTime = 100;
	public static final int intDefaultMaxIdleTime = 30000;
	public static final int intDefaultMinRequestHeaderSize = 1024;
	public static final int intDefaultMaxRequestHeaderSize = 8192;
	
}
