package BPServicesManager;

import java.util.Properties;

public class CConfigDBConnection {

	public String strName;
    public String strURL;
    public String strDatabase;
    public String strUser;
    public String strPassword;
    public String strMapsFilePath;
    public String strDBActionsFilePath;

    public Properties KeywordsMaps;
    
    public CDBActionsManager DBActionsManager;
    
	public CConfigDBConnection() {

		strName = "";
		strURL = "";
		strDatabase = "";
		strUser = "";
		strPassword = "";
		strMapsFilePath = "";
		strDBActionsFilePath = "";
		
		KeywordsMaps = null;
		
		DBActionsManager = null;
		
	}
	
	public CConfigDBConnection( CConfigDBConnection ConfigDBConnection ) {
	
        strName = ConfigDBConnection.strName;
        strURL = ConfigDBConnection.strURL;
        strDatabase = ConfigDBConnection.strDatabase;
        strUser = ConfigDBConnection.strUser;
        strPassword = ConfigDBConnection.strPassword;
        strMapsFilePath = ConfigDBConnection.strMapsFilePath;
        strDBActionsFilePath = ConfigDBConnection.strDBActionsFilePath;
	
	}	

	public CConfigDBConnection getCloneConfigDBConnection() {
	
		CConfigDBConnection ConfigDBConnection = new CConfigDBConnection( this );
		
		return ConfigDBConnection;
		
	}
	
}
