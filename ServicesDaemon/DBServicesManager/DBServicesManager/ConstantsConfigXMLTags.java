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

	public static final String _DBServices_Dir = "DBServices_Dir";
	public static final String _DBDrivers_Dir = "DBDrivers_Dir";
	public static final String _DBEngines_Dir = "DBEngines_Dir";

	public static final String _Driver = "Driver";
	public static final String _Engine = "Engine";
	public static final String _Engine_Version = "Engine_Version";
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
	
}
