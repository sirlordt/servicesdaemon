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

public class CConfigDBConnection {

	public String strName;
	public String strDriver;
	public String strEngine;
	public String strEngineVersion;
	public String strIP;
	public String strAddressType;
	public int intPort;
	public String strDatabase;
	public String strAuthType;
	public String strUser;
	public String strPassword;
	public String strDateFormat;
	public String strTimeFormat;
	public String strDateTimeFormat;
	public String strSessionKey;
	
	public CConfigDBConnection() {
		
		strName = "";
		strDriver = "";
		strEngine = "";
		strEngineVersion = "";
		strIP = "127.0.0.1";
		strAddressType = "ipv4";
		intPort = 45068;
		strDatabase = "";
		strAuthType = "database";
		strUser = "";
		strPassword = "";
		strDateFormat = "";
		strTimeFormat = "";
		strDateTimeFormat = "";
		strSessionKey = "";

	}

	public CConfigDBConnection( CConfigDBConnection ConfigDBConnection ) {
		
		strName = ConfigDBConnection.strName;
		strDriver = ConfigDBConnection.strDriver;
		strEngine = ConfigDBConnection.strEngine;
		strEngineVersion = ConfigDBConnection.strEngineVersion;
		strIP = ConfigDBConnection.strIP;
		strAddressType = ConfigDBConnection.strAddressType;
		intPort = ConfigDBConnection.intPort;
		strDatabase = ConfigDBConnection.strDatabase;
		strAuthType = ConfigDBConnection.strAuthType;
		strUser = ConfigDBConnection.strUser;
		strPassword = ConfigDBConnection.strPassword;
		strDateFormat = ConfigDBConnection.strDateFormat;
		strTimeFormat = ConfigDBConnection.strTimeFormat;
		strDateTimeFormat = ConfigDBConnection.strDateTimeFormat;

	}
	
}
