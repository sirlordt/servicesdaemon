package CommonClasses;

public class CConfigAccessControl {

	public String strFromIP;
	public String strContextPath;
	public boolean bAlwaysDeny;
	public boolean bAlwaysAllow;
	
	public CConfigAccessControl() {
		
		strFromIP = "";
		strContextPath = ConfigXMLTagsServicesDaemon._Context_Path_Default;
		bAlwaysDeny = false;
		bAlwaysAllow = false;
		
	}

}
