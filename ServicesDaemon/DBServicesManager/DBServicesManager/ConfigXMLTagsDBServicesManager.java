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

public interface ConfigXMLTagsDBServicesManager {

	public static final String _DBServices_Dir = "DBServices_Dir";
	public static final String _DBDrivers_Dir = "DBDrivers_Dir";
	public static final String _DBEngines_Dir = "DBEngines_Dir";
	public static final String _Responses_Formats_Dir = "Responses_Formats_Dir";

	public static final String _Default_Response_Format = "Default_Response_Format";
	public static final String _Default_Response_Format_Version = "Default_Response_Format_Version";

	public static final String _ResponseFormat_XML_DATAPACKET = "XML-DATAPACKET";
	public static final String _ResponseFormat_JSON = "JSON";
	public static final String _ResponseFormat_JAVA_XML_WEBROWSET = "JAVA-XML-WEBROWSET";
	public static final String _ResponseFormat_CSV = "CSV";

	public static final String _BuiltinResponsesFormats = "BuiltinResponsesFormats";
	public static final String _BuiltinResponseFormat = "BuiltinResponseFormat";
	public static final String _Content_Type = "Content_Type";
	public static final String _Char_Set = "Char_Set";
	
	public static final String _DBConnections = "DBConnections";
	public static final String _DBConnection = "DBConnection";
	public static final String _Name = "Name";
	public static final String _Driver = "Driver";
	public static final String _Engine = "Engine";
	public static final String _Engine_Version = "Engine_Version";
	public static final String _Database = "Database";
	public static final String _Auth_Type = "Auth_Type";
	public static final String _Auth_Type_Engine = "engine";
	public static final String _Auth_Type_Database = "database";
	public static final String _SessionUser = "Session_User";
	public static final String _SessionPassword = "Session_Password";
	public static final String _TransactionUser = "Transaction_User";
	public static final String _TransactionPassword = "Transaction_Password";
	public static final String _Password_Crypted = "crypted";
	public static final String _Password_Crypted_Sep = ":";
	
	public static final String _Date_Format = "Date_Format";
	public static final String _Time_Format = "Time_Format";
	public static final String _Date_Time_Format = "Date_Time_Format";

}
