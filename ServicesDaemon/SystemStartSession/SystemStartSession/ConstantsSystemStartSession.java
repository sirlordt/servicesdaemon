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
package SystemStartSession;

public interface ConstantsSystemStartSession {

	public final String _Request_DBConnection_Name = "DBConnection";
	public final String _Request_DBConnection_Type = "varchar";
	public final String _Request_DBConnection_Length = "150";

	public static final String _Main_File = "SystemStartSession";
	public static final String _Conf_File = _Main_File + ".conf";
	public static final String _Main_File_Log = _Main_File + ".log";

}
