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

import CommonClasses.ConstantsCommonConfigXMLTags;

public interface ConstantsServicesManager {

	public static final String _Main_File = "DBServicesManager";
	public static final String _Conf_File = _Main_File + ".conf";
	public static final String _Main_File_Log = _Main_File + ".log";
	public static final String _Security_Manager_Name = _Main_File + ".SecurityManager";

	public static final String _Services_Dir = "DBServices/"; 
	public static final String _DB_Drivers_Dir = "DBDrivers/";
	public static final String _DB_Engines_Dir = "DBEngines/";
	public static final String _Logger_Name = "DBServicesManagerLogger"; 

	public static final String _Response_Format = ConstantsCommonConfigXMLTags._ResponseFormat_XML_DATAPACKET;
	public static final String _Response_Format_Version = "1.1";
	
}
