package CommonClasses;

import java.io.Serializable;


public class CNativeSessionInfo implements Serializable {

	private static final long serialVersionUID = -2482993508939917256L;

	public String strName;
	public String strSecurityTokenID;
	public String strSessionID;
	public CConfigNativeDBConnection ConfigNativeDBConnection;
	
	public CNativeSessionInfo( ) {
		
		 strName = "";
		 strSecurityTokenID = "";
		 strSessionID = "";
		 
		 ConfigNativeDBConnection = null;
		 
	}

	public CNativeSessionInfo( CNativeSessionInfo NativeSessionInfoToClone ) {
		
		 this.strName = NativeSessionInfoToClone.strName;
		 this.strSecurityTokenID = NativeSessionInfoToClone.strSecurityTokenID;
		 this.strSessionID = NativeSessionInfoToClone.strSessionID;
		 
		 this.ConfigNativeDBConnection = new CConfigNativeDBConnection( NativeSessionInfoToClone.ConfigNativeDBConnection );
		 
	}
	
}
