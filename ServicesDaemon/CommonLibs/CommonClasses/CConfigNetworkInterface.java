package CommonClasses;

public class CConfigNetworkInterface {

	public String strIP;
	public String strAddressType;
	public int intPort;
	public boolean bUseSSL;
	
	public CConfigNetworkInterface() {

		strIP = "127.0.0.1";
		strAddressType = "ipv4";
		intPort = 8080;
		bUseSSL = false;

	}

}
