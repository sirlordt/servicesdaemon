package CommonClasses;

import java.io.Serializable;

public class CConfigRegisterService implements Serializable  {

	private static final long serialVersionUID = -1701435156073955045L;

	public String strPassword;
	public String strURL;
	public CConfigProxy ConfigProxy;
	public int intInterval;
	public int intWeight;
	public boolean bReportLoad;
	public int intReportIPType; //0 = all, 1 = ipv4, 2 = ipv6 
	
	public long lngLastUpdate;
	
	public CConfigRegisterService() {
		
		strPassword = "";
		strURL = "";
		ConfigProxy = new CConfigProxy();
		intInterval = 0;
		intWeight = 0;
		bReportLoad = false;
		intReportIPType = 0;
		
		lngLastUpdate = 0;
		
	}

	public CConfigRegisterService( CConfigRegisterService ConfigRegisterServices ) {
		
		strPassword = ConfigRegisterServices.strPassword;
		strURL = ConfigRegisterServices.strURL;
		ConfigProxy = new CConfigProxy( ConfigRegisterServices.ConfigProxy );
		intInterval = ConfigRegisterServices.intInterval;
		intWeight = ConfigRegisterServices.intWeight;
		bReportLoad = ConfigRegisterServices.bReportLoad;
		intReportIPType = ConfigRegisterServices.intReportIPType;
		lngLastUpdate = ConfigRegisterServices.lngLastUpdate;
		
	}
	
}
