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

import java.io.Serializable;

import AbstractDBEngine.CDBEngineConfigNativeDBConnection;

public class CConfigNativeDBConnection implements Serializable {

	private static final long serialVersionUID = -7714648765221540677L;
	
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
	
	public CConfigNativeDBConnection() {
		
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

	public CConfigNativeDBConnection( CConfigNativeDBConnection ConfigDBConnection ) {
		
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

	public CDBEngineConfigNativeDBConnection getDBEngineConfigConnection( boolean bUseSessionUser ){
		
		CDBEngineConfigNativeDBConnection DBEngineConfigConnection = new CDBEngineConfigNativeDBConnection();
		
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
