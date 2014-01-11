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

public interface ConstantsConfigXMLTags {

	public static final String _Global_Date_Time_Format = "Global_Date_Time_Format";
	public static final String _Global_Date_Format = "Global_Date_Format";
	public static final String _Global_Time_Format = "Global_Time_Format";
	
	public static final String _Temp_Dir = "Temp_Dir";
	public static final String _DBServices_Dir = "DBServices_Dir";
	public static final String _DBDrivers_Dir = "DBDrivers_Dir";
	public static final String _DBEngines_Dir = "DBEngines_Dir";
	public static final String _Responses_Formats_Dir = "Responses_Formats_Dir";

	public static final String _Default_Response_Format = "Default_Response_Format";
	public static final String _Default_Response_Format_Version = "Default_Response_Format_Version";

	public static final String _Internal_Fetch_Size = "Internal_Fetch_Size";
	
	public static final String _ResponseFormat_XML_DATAPACKET = "XML-DATAPACKET";
	public static final String _ResponseFormat_JSON = "JSON";
	public static final String _ResponseFormat_JAVA_XML_WEBROWSET = "JAVA-XML-WEBROWSET";
	public static final String _ResponseFormat_CSV = "CSV";

	public static final String _RegisterServices = "RegisterServices";
	public static final String _Register = "Register";
	public static final String _Password = "Password";
	public static final String _URL = "URL";
	public static final String _Proxy_IP = "Proxy_IP";
	public static final String _Proxy_Port = "Proxy_Port";
	public static final String _Proxy_User = "Proxy_User";
	public static final String _Proxy_Password = "Proxy_Password";
	public static final String _Interval = "Interval";
	public static final String _Weight = "Weight";
	public static final String _ReportLoad = "ReportLoad";
	public static final String _ReportIPType = "ReportIPType";
	public static final String _IPV4 = "ipv4";
	public static final String _IPV6 = "ipv6";
	
	public static final String _BuiltinResponsesFormats = "BuiltinResponsesFormats";
	public static final String _BuiltinResponseFormat = "BuiltinResponseFormat";
	public static final String _Content_Type = "Content_Type";
	public static final String _Char_Set = "Char_Set";
	public static final String _Fields_Quote = "Fields_Quote";
	public static final String _Separator_Symbol = "Separator_Symbol";
	public static final String _Show_Headers = "Show_Headers";
	
	public static final String _DBConnections = "DBConnections";
	public static final String _DBConnection = "DBConnection";
	public static final String _Name = "Name";
	public static final String _Driver = "Driver";
	public static final String _Engine = "Engine";
	public static final String _Engine_Version = "Engine_Version";
	public static final String _Database = "Database";
	public static final String _Auto_Commit = "Auto_Commit";
	public static final String _Dummy_SQL = "Dummy_SQL";
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

	public static final String _Self_Client_Request_Timeout = "Self_Client_Request_Timeout";
	public static final String _Self_Client_Socket_Timeout = "Self_Client_Socket_Timeout";
	
}
