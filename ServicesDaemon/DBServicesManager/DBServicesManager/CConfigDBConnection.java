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

import AbstractDBEngine.CDBEngineConfigConnection;

public class CConfigDBConnection {

	public String strName;
	public String strDriver;
	public String strEngine;
	public String strEngineVersion;
	public String strIP;
	public String strAddressType;
	public int intPort;
	public String strDatabase;
	public boolean bAutoCommit;
	public String strDummySQL;	
	public String strAuthType;
	public String strSessionUser;
	public String strSessionPassword;
	public String strTransactionUser;
	public String strTransactionPassword;
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
		strDummySQL = "";
		strAuthType = "database";
		strSessionUser = "";
		strSessionPassword = "";
		strTransactionUser = "";
		strTransactionPassword = "";
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
		bAutoCommit = ConfigDBConnection.bAutoCommit;
		strDummySQL = ConfigDBConnection.strDummySQL;
		strAuthType = ConfigDBConnection.strAuthType;
		strSessionUser = ConfigDBConnection.strSessionUser;
		strSessionPassword = ConfigDBConnection.strSessionPassword;
		strTransactionUser = ConfigDBConnection.strTransactionUser;
		strTransactionPassword = ConfigDBConnection.strTransactionPassword;
		strDateFormat = ConfigDBConnection.strDateFormat;
		strTimeFormat = ConfigDBConnection.strTimeFormat;
		strDateTimeFormat = ConfigDBConnection.strDateTimeFormat;

	}

	public CDBEngineConfigConnection getDBEngineConfigConnection( boolean bUseSessionUser ){
		
		CDBEngineConfigConnection DBEngineConfigConnection = new CDBEngineConfigConnection();
		
		DBEngineConfigConnection.strName = this.strName;
		DBEngineConfigConnection.strDriver = this.strDriver;
		DBEngineConfigConnection.strEngine = this.strEngine;
		DBEngineConfigConnection.strEngineVersion = this.strEngineVersion;
		DBEngineConfigConnection.strIP = this.strIP;
		DBEngineConfigConnection.strAddressType = this.strAddressType;
		DBEngineConfigConnection.intPort = this.intPort;
		DBEngineConfigConnection.strDatabase = this.strDatabase;
		DBEngineConfigConnection.bAutoCommit = this.bAutoCommit;
		DBEngineConfigConnection.strDummySQL = this.strDummySQL;
		DBEngineConfigConnection.strAuthType = this.strAuthType;
		
		if ( bUseSessionUser == true || this.strTransactionUser.isEmpty() == true ) {

			DBEngineConfigConnection.strUser = this.strSessionUser;
			DBEngineConfigConnection.strPassword = this.strSessionPassword;

		}
		else {

			DBEngineConfigConnection.strUser = this.strTransactionUser;
			DBEngineConfigConnection.strPassword = this.strTransactionPassword;

		}
		
		DBEngineConfigConnection.strDateFormat = this.strDateFormat;
		DBEngineConfigConnection.strTimeFormat = this.strTimeFormat;
		DBEngineConfigConnection.strDateTimeFormat = this.strDateTimeFormat;
		
		
		return DBEngineConfigConnection;
		
	} 
	
}
