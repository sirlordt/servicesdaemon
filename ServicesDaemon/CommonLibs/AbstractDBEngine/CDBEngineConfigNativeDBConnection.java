package AbstractDBEngine;

import java.io.Serializable;

public class CDBEngineConfigNativeDBConnection implements Serializable {

	private static final long serialVersionUID = -3032416583619389235L;
	
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
	public String strUser;
	public String strPassword;
	public String strDateFormat;
	public String strTimeFormat;
	public String strDateTimeFormat;
	public String strSessionKey;
	
	public CDBEngineConfigNativeDBConnection() {
		
		strName = "";
		strDriver = "";
		strEngine = "";
		strEngineVersion = "";
		strIP = "";
		strAddressType = "";
		intPort = -1;
		strDatabase = "";
		bAutoCommit = false;
		strDummySQL = "";	
		strAuthType = "";
		strUser = "";
		strPassword = "";
		strDateFormat = "";
		strTimeFormat = "";
		strDateTimeFormat = "";
		strSessionKey = "";
		
	}
	
	public CDBEngineConfigNativeDBConnection( CDBEngineConfigNativeDBConnection DBEngineConfigNativeDBConnectionToClone ) {
		
		this.strName = DBEngineConfigNativeDBConnectionToClone.strName;
		this.strDriver = DBEngineConfigNativeDBConnectionToClone.strDriver;
		this.strEngine = DBEngineConfigNativeDBConnectionToClone.strEngine;
		this.strEngineVersion = DBEngineConfigNativeDBConnectionToClone.strEngineVersion;
		this.strIP = DBEngineConfigNativeDBConnectionToClone.strIP;
		this.strAddressType = DBEngineConfigNativeDBConnectionToClone.strAddressType;
		this.intPort = DBEngineConfigNativeDBConnectionToClone.intPort;
		this.strDatabase = DBEngineConfigNativeDBConnectionToClone.strDatabase;
		this.bAutoCommit = DBEngineConfigNativeDBConnectionToClone.bAutoCommit;
		this.strDummySQL = DBEngineConfigNativeDBConnectionToClone.strDummySQL;	
		this.strAuthType = DBEngineConfigNativeDBConnectionToClone.strAuthType;
		this.strUser = DBEngineConfigNativeDBConnectionToClone.strUser;
		this.strPassword = DBEngineConfigNativeDBConnectionToClone.strPassword;
		this.strDateFormat = DBEngineConfigNativeDBConnectionToClone.strDateFormat;
		this.strTimeFormat = DBEngineConfigNativeDBConnectionToClone.strTimeFormat;
		this.strDateTimeFormat = DBEngineConfigNativeDBConnectionToClone.strDateTimeFormat;
		this.strSessionKey = DBEngineConfigNativeDBConnectionToClone.strSessionKey;
		
	}
	
}
