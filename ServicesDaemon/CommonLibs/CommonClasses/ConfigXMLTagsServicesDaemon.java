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

public interface ConfigXMLTagsServicesDaemon {

	public static final String _Config = "Config";
	public static final String _System = "System";
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
}
