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

public abstract class DefaultConstantsDBServicesManager {

	public static String strDefaultRunningPath = "";

	public static final String strDefaultGlobalDateTimeFormat = "dd/mm/yyyy HH:mm:ss";
	public static final String strDefaultGlobalDateFormat = "dd/mm/yyyy";
	public static final String strDefaultGlobalTimeFormat = "HH:mm:ss";

	public static final String strDefaultMainFile = "DBServicesManager";
	public static final String strDefaultConfFile = strDefaultMainFile + ".conf";
	public static final String strDefaultMainFileLog = strDefaultMainFile + ".log";
	public static final String strDefaultLogsSystemDir = "Logs/";
	
	public static final String strDefaultDBServicesDir = "DBServices/"; 
	public static final String strDefaultDBDriversDir = "DBDrivers/";
	public static final String strDefaultDBEnginesDir = "DBEngines/";
	public static final String strDefaultResponsesFormatsDir = "ResponsesFormats/";

	public static final String strDefaultLoggerName = "DBServicesManageLogger"; 

	public static final String strDefaultResponseFormat = ConfigXMLTagsDBServicesManager._ResponseFormat_XML_DATAPACKET;
	public static final String strDefaultResponseFormatVersion = "1.1";
	
	public static final String strDefaultChasetXML = "UTF-8";
	public static final String strDefaultContentTypeXML = "text/xml";

	public static final String strDefaultChasetJSON = "UTF-8";
	public static final String strDefaultContentTypeJSON = "text/json";
	
	public static final String strDefaultChasetCSV = "UTF-8";
	public static final String strDefaultContentTypeCSV = "text/plain";
	public static final boolean bDefaultFieldsQuoteCSV = true;
	public static final String strDefaultSeparatorSymbolCSV = ";";
	public static final boolean bDefaultShowHeadersCSV = true;
	
	public static final String strDefaultVersionAny = "any";
	
}
