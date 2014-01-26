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
package SystemPing;

public interface ConstantsService {

	public static final String _Request_Ping          = "Ping";
	public final static String _Request_Ping_Type     = "Bigint";

	public static final String _Response_Pong         = "Pong";
	public static final String _Response_Date_Request = "DateRequest";
	public static final String _Response_Time_Request = "TimeRequest";

	public static final String _Main_File = "SystemPing";
	public static final String _Conf_File = _Main_File + ".conf";
	public static final String _Main_File_Log = _Main_File + ".log";
	   
}
