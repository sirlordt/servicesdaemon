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

public interface ConstantsCommonConfigXMLTags {

	public static final String _Global_Date_Time_Format = "Global_Date_Time_Format";
	public static final String _Global_Date_Format = "Global_Date_Format";
	public static final String _Global_Time_Format = "Global_Time_Format";
	
	public static final String _Temp_Dir = "Temp_Dir";
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

	public static final String _Date_Format = "Date_Format";
	public static final String _Time_Format = "Time_Format";
	public static final String _Date_Time_Format = "Date_Time_Format";

	public static final String _DBConnections = "DBConnections";
	public static final String _DBConnection = "DBConnection";
	public static final String _Database = "Database";
	
	public static final String _Config = "Config";
	public static final String _System = "System";
	public static final String _Main = "Main";
	public static final String _Backup = "Backup";
	public static final String _Managers_Dir = "Managers_Dir";
	public static final String _Key_Store_File = "Key_Store_File";
	public static final String _Key_Store_Password = "Key_Store_Password";
	public static final String _Key_Manager_Password = "Key_Manager_Password";
	public static final String _Max_Idle_Time = "Max_Idle_Time";
	public static final String _Max_Request_Header_Size = "Max_Request_Header_Size";
	public static final String _Response_Request_Method = "Response_Request_Method";
	public static final String _Request_Method_OnlyGET = "OnlyGET";
	public static final String _Request_Method_OnlyPOST = "OnlyPOST";
	public static final String _Request_Method_ANY = "Any";
	
	public static final String _Logger = "Logger";
	public static final String _ClassName_MethodName = "ClassName_MethodName";
	public static final String _Exact_Match = "Exact_Match";
	public static final String _Log_Missing_Translations = "Log_Missing_Translations";
	public static final String _Level = "Level";

	public static final String _Instance_ID = "Log_Instance_ID";
	public static final String _Log_IP = "Log_IP";
	public static final String _Log_Port = "Log_Port";
	
	public static final String _Request_Timeout = "Connect_Timeout";
	public static final String _Socket_Timeout = "Socket_Timeout";
	public static final String _Name = "Name";
	public static final String _URL = "URL";
	public static final String _User = "User";
	public static final String _Password = "Password";
	public static final String _HTTP_Log_URL = "HTTP_Log_URL";
	public static final String _HTTP_Log_User = "HTTP_Log_User";
	public static final String _HTTP_Log_Password = "HTTP_Log_Password";
	public static final String _Proxy_IP = "Proxy_IP";
	public static final String _Proxy_Port = "Proxy_Port";
	public static final String _Proxy_User = "Proxy_User";
	public static final String _Proxy_Password = "Proxy_Password";
	
	public static final String _AccessControl = "AccessControl";
	public static final String _From = "From";
	public static final String _Context_Path = "Context_Path";
	public static final String _Context_Path_Default = "/*";
	public static final String _Action = "Action";
	public static final String _Action_Deny = "deny";
	public static final String _Action_Allow = "allow";
	
	public static final String _NetworkInterfaces = "NetworkInterfaces";
	public static final String _NetworkInterface = "NetworkInterface";
	public static final String _IP = "IP";
	public static final String _Port = "Port";
	public static final String _Use_SSL = "Use_SSL";
	
	public static final String _StartMacroTag = "[macro]";
	public static final String _EndMacroTag = "[/macro]";

	public static final String _StartParamValue = "[paramvalue]";
	public static final String _EndParamValue = "[/paramvalue]";
	
	public static final String _Password_Crypted = "crypted";
	public static final String _Password_Crypted_Sep = ":";
	
	public static final String _Class_Name = "Class_Name";
	public static final String _Config_File = "Config_File";
	
	public static final String _Filters = "Filters";
	public static final String _Type = "Type";
	public static final String _Type_Allow = "allow";
	public static final String _Type_Block = "block";
	public static final String _Type_Exact = "exact";
	public static final String _Type_Partial = "partial";
	public static final String _Type_RExp = "rexp";
	public static final String _Filter = "Filter";
	
}
