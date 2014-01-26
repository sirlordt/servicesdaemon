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
import java.sql.Types;

public interface ConstantsCommonClasses {
	
	public static final String _Libs_Dir = "Libs" + File.separator;
	public static final String _Lib_Ext = ".jar";
	
	public static final String _Langs_Dir = "Langs" + File.separator;
	public static final String _Lang_Ext = "init.lang";
	public static final String _Common_Lang_File = "Common.init.lang";

	public static final String _Logs_Dir = "Logs/";
	
	public static final String _Temp_Dir = "Temp/";

	public static final String _Responses_Formats_Dir = "ResponsesFormats/";

	public static final String _Cache_Engines_Dir = "CacheEngines/";
	
	public static final String _Main_File = "ServicesDaemon";
	public static final String _Main_File_Log = _Main_File + ".log";
	public static final String _Main_Jetty_File_Log = _Main_File + ".servlet.container.log";
	public static final String _Main_File_Access_Log = _Main_File + ".accesss.yyyy_mm_dd.log";

	public static final String _Log_Instance_ID = "ServiceDaemon:Instance0";
	public static final String _Logger_Name = "ServicesDaemonLogger"; 
	public static final String _Jetty_Logger_Name = "JettyLogger"; 
	public static final String _Jetty_Logger_Class_Name = "org.eclipse.jetty.util.log"; 
	public static final String _Conf_File = _Main_File + ".conf";
	public static final String _Logs_System_Dir = "Logs" + File.separator + "System" + File.separator;
	public static final String _Logs_Access_Dir = "Logs" + File.separator + "Access" + File.separator;
	public static final String _Log_Class_Method = "*.*";
	public static final boolean _Log_Exact_Match = false;
	public static final boolean _Log_Missing_Translations = false;
	public static final String _Logger_Name_Missing_Translations = "MissingTranslations";
	public static final String _Missing_Translations_File_Log = _Logger_Name_Missing_Translations + ".log";
	public static final String _Log_Level = "ALL";
	
    public static final String _Servlet_Context_ALL = "/*";

    public static final String _Managers_Dir = "Managers" + File.separator;
    public static final String _Managers_Ext = ".jar";
    public static final String _Key_Store_File = _Main_File + ".keystore";
    public static final String _Default_Password = "12345678";
    
	/*public static final String _IPV4_All = "0.0.0.0";
	public static final String _IPV6_All = "::";

	public static final String _IPV4_Localhost = "127.0.0.1";
	public static final String _IPV6_Localhost = "::1";*/
	
	public static final int _Min_Port_Number = 1;
	public static final int _Max_Port_Number = 65535;
	
	public static final int _Min_Idle_Time = 100;
	public static final int _Max_Idle_Time = 30000;
	public static final int _Min_Request_Header_Size = 1024;
	public static final int _Max_Request_Header_Size = 8192;

	public static final String _Log_IP = "";
	public static final int _Log_Port_Number = 30000;

	public static final String _HTTP_Log_URL = "";
	public static final String _HTTP_Log_User = "";
	public static final String _HTTP_Log_Password = "";
	public static final String _Proxy_IP = "";
	public static final int _Proxy_Port = -1;
	public static final String _Proxy_User = "";
	public static final String _Proxy_Password = "";
	
	public static final String _Crypt_Algorithm = "DES";
	public static final String _Hash_Algorithm = "SHA512";
	
	public final static String _Pre_Execute_Dir = "PreExecute" + File.separator;
	public final static String _Post_Execute_Dir = "PostExexute" + File.separator;
	
	public static final String _Version_Any = "any";
	
	public static final int _Self_Client_Request_Timeout = 3000;
	public static final int _Self_Client_Socket_Timeout = 3000;
	
	public static final int _Minimal_Request_Timeout = 1000;
	public static final int _Maximal_Request_Timeout = 100000000;
	public static final int _Minimal_Socket_Timeout = 1000;
	public static final int _Maximal_Socket_Timeout = 100000000;
	
	public static final String _Global_Date_Time_Format = "dd/mm/yyyy HH:mm:ss";
	public static final String _Global_Date_Format = "dd/mm/yyyy";
	public static final String _Global_Time_Format = "HH:mm:ss";

	public static final String _Chaset_XML = "UTF-8";
	public static final String _Content_Type_XML = "text/xml";
	
	public static final String _Chaset_JSON = "UTF-8";
	public static final String _Content_Type_JSON = "text/json";
	
	public static final String _Chaset_CSV = "UTF-8";
	public static final String _Content_Type_CSV = "text/plain";
	public static final boolean _Fields_Quote_CSV = true;
	public static final String _Separator_Symbol_CSV = ";";
	public static final boolean _Show_Headers_CSV = true;
	
	public static final int _Internal_Fetch_Size = 25000;
	
	public final static byte _ReadWriteService                       = 0;
	public final static byte _ReadService                            = 1;
	public final static byte _WriteService                           = 2;
	
	public final static String _Request_SecurityTokenID                = "SecurityTokenID";
	public final static String _Request_SecurityTokenID_Type           = "Bigint";
	public final static String _Request_SecurityTokenID_Length         = "0";
	public final static String _Request_TransactionID                  = "TransactionID";
	public final static String _Request_TransactionID_Type             = "Bigint";
	public final static String _Request_TransactionID_Length           = "0";
	public final static String _Request_ServiceName                    = "ServiceName";
	public final static String _Request_ServiceName_Type               = "Varchar";
	public final static String _Request_ServiceName_Length             = "255";
	public final static String _Request_ResponseFormat                 = "ResponseFormat";
	public final static String _Request_ResponseFormat_Type            = "Varchar";
	public final static String _Request_ResponseFormat_Length          = "75";
	public final static String _Request_ResponseFormatVersion          = "ResponseFormatVersion";
	public final static String _Request_ResponseFormatVersion_Type     = "Varchar";
	public final static String _Request_ResponseFormatVersion_Length   = "15";
	
	public final static String _SessionSecurityTokens                = "Security.Tokens";
	
	//public final static String _SessionKey                         = "Session.Key";
	
	public final static String _Default                              = "default";
	
	public final static String _SecurityTokenID                       = "SecurityTokenID";
	public final static String _SecurityTokenID_Type                  = NamesSQLTypes._BIGINT;
	public final static int _SecurityTokenID_TypeID                   = Types.BIGINT;
	public final static String _TransactionID                         = "TransactionID";
	public final static String _TransactionID_Type                    = NamesSQLTypes._BIGINT;
	public final static int _TransactionID_TypeID                     = Types.BIGINT;
	public final static String _Code                                  = "Code";
	public final static String _Code_Type                             = NamesSQLTypes._INTEGER;
	public final static int _Code_TypeID                              = Types.INTEGER;
	public final static String _Description                           = "Description";
	public final static String _Description_Type                      = NamesSQLTypes._VARCHAR;
	public final static int _Description_TypeID                       = Types.VARCHAR;
	public final static int _Description_Length                       = 255;
	
	public final static int _Register_Manager_Frecuency               = 5000;
	public final static int _Minimal_Register_Manager_Frecuency       = 5000;
	
}
