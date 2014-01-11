package CommonClasses;

import java.io.Serializable;

public class CRegisteredManagerInfo implements Serializable {

	private static final long serialVersionUID = 8771675150886514479L;

	public String strContext;
	public String strManagerURL;
	public int intWeight;
	public int intStandardizedWeight;  //From 1 to 100
	public int intLoad;
	public Long lngLastUpdate; //On milliseconds
	
	public CRegisteredManagerInfo() {
		
		this.strContext = "";
		this.strManagerURL = "";
		this.intWeight = -1;
		this.intStandardizedWeight = -1;
		this.intLoad = -1;
		this.lngLastUpdate = 0L;
		
	}

	public CRegisteredManagerInfo( CRegisteredManagerInfo RegisteredManagerInfo ) {
		
		this.strContext = RegisteredManagerInfo.strContext;
		this.strManagerURL = RegisteredManagerInfo.strManagerURL;
		this.intWeight = RegisteredManagerInfo.intLoad;
		this.intStandardizedWeight = RegisteredManagerInfo.intStandardizedWeight;
		this.intLoad = RegisteredManagerInfo.intLoad;
		this.lngLastUpdate = RegisteredManagerInfo.lngLastUpdate;
		
	}
	
}
