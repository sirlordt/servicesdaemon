package CommonClasses;

import java.io.Serializable;

public class CConfigProxy implements Serializable {

	private static final long serialVersionUID = 5101116084675155521L;

	public String strProxyIP;
    public int intProxyPort;
    public String strProxyUser;
    public String strProxyPassword;
    
    public CConfigProxy() {
    	
    	this.strProxyIP = "";
    	this.intProxyPort = -1;
    	this.strProxyUser = "";
    	this.strProxyPassword = "";
    	
    }
    
    public CConfigProxy( CConfigProxy CConfigProxyClone ) {
    	
    	this.strProxyIP = CConfigProxyClone.strProxyIP;
    	this.intProxyPort = CConfigProxyClone.intProxyPort;
    	this.strProxyUser = CConfigProxyClone.strProxyUser;
    	this.strProxyPassword = CConfigProxyClone.strProxyPassword;
    	
    }

    public CConfigProxy( String strProxyIP, int intProxyPort, String strProxyUser, String strProxyPassword ) {
    	
    	this.strProxyIP = strProxyIP;
    	this.intProxyPort = intProxyPort;
    	this.strProxyUser = strProxyUser;
    	this.strProxyPassword = strProxyPassword;
    	
    }
    
}
