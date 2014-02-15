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
package SystemExecuteDBCommand;

import java.sql.Types;

public interface ConstantsService {

	public static final String _Request_Commit            = "Commit";
	public static final String _Request_Commit_Type       = "Integer";
	public static final String _Request_Commit_Length     = "0";

	public static final String _Request_InternalFetchSize        = "InternalFetchSize";
	public static final String _Request_InternalFetchSize_Type   = "Integer";
	public static final String _Request_InternalFetchSize_Length = "0";

	public static final String _Request_SQL            = "SQL";
	public static final String _Request_SQL_Type       = "VarChar";
	public static final String _Request_SQL_Length     = "10240";

	public static final String _Response_AffectedRows     = "AffectedRows";
	public static final int _Response_AffectedRows_TypeID  = Types.BIGINT;
	public static final String _Response_AffectedRows_Type = "BigInt";
	
	public static final String _Main_File = "SystemExecuteDBCommand";
	public static final String _Conf_File = _Main_File + ".conf";
	public static final String _Main_File_Log = _Main_File + ".log";
	
	public static final boolean _Log_SQL_Statement = false;
	
	//public static final String strDefaultPreExecuteDir = "PreExecute/";
	//public static final String strDefaultPostExecuteDir = "PostExecute/";
	
}
