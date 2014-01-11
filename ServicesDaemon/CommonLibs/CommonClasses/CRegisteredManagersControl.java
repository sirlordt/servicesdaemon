package CommonClasses;

import java.io.Serializable;
import java.util.Vector;


public class CRegisteredManagersControl implements Serializable {

	private static final long serialVersionUID = 705415538561549398L;

	protected static CRegisteredManagersControl RegisteredManagersControl;
	
	public static CRegisteredManagersControl getRegisteredManagersControl() {
		
		if ( RegisteredManagersControl == null ) {
			
			RegisteredManagersControl = new CRegisteredManagersControl();
			
		}
		
		return RegisteredManagersControl;
		
	} 

	protected Vector<CRegisteredManagerInfo> RegisteredManagersList;
	
	public CRegisteredManagersControl() {
		
		RegisteredManagersList = new Vector<CRegisteredManagerInfo>();
		
	}
	
	public Vector<CRegisteredManagerInfo> getListRegisteredManagersOnContext( String strContext ) {
		
		Vector<CRegisteredManagerInfo> Result = null;
		
		strContext = strContext.toLowerCase();
		
		if ( strContext.equals( "*" ) == false ) {

			Result = new Vector<CRegisteredManagerInfo>();

			for ( CRegisteredManagerInfo RegisteredManagerInfo: RegisteredManagersList ) {

				if ( RegisteredManagerInfo.strContext.toLowerCase().equals( strContext ) ) {

					Result.add( RegisteredManagerInfo );

				}

			}
		
		}
		else {
			
			Result = new Vector<CRegisteredManagerInfo>( RegisteredManagersList );
			
		}
				
		return Result;
		
	}

	public CRegisteredManagerInfo getRegisteredManagerFromURL( String strManagerURL ) {
		
		strManagerURL = strManagerURL.toLowerCase();
		
		for ( CRegisteredManagerInfo RegisteredManagerInfo: RegisteredManagersList ) {

			if ( RegisteredManagerInfo.strManagerURL.toLowerCase().equals( strManagerURL ) ) {

				return RegisteredManagerInfo;

			}

		}
		
		return null;
		
	}
	
	public void registerManager( CRegisteredManagerInfo ManagerInfoToRegister ) {
		
		RegisteredManagersList.add( ManagerInfoToRegister );
		
	}
	
	public boolean removeRegisteredManagerByURL( String strManagerURL ) {
		
		for ( CRegisteredManagerInfo RegisteredManagerInfo: RegisteredManagersList ) {

			if ( RegisteredManagerInfo.strManagerURL.equals( strManagerURL ) ) {

				RegisteredManagersList.remove( RegisteredManagerInfo );
				
				return true;

			}

		}
		
		return false;
		
	}

	public boolean removeRegisteredManagerByContext( String strContext ) {
	
		boolean bResult = false;
		
		strContext = strContext.toLowerCase();
		
		for ( CRegisteredManagerInfo RegisteredManagerInfo: RegisteredManagersList ) {

			if ( RegisteredManagerInfo.strContext.toLowerCase().equals( strContext ) ) {

				RegisteredManagersList.remove( RegisteredManagerInfo );
				
				bResult = true;
				
			}

		}
		
		return bResult;
		
	}
	
	public boolean removeOutdatedRegisteredManagers( long lngMillisToOutdated  ) {
		
		boolean bResult = false;
		
		long lngSystemTimeMillis = System.currentTimeMillis(); 
		
		int intIndex = 0;
		
		while ( intIndex < RegisteredManagersList.size() ) {

			CRegisteredManagerInfo RegisteredManagerInfo = RegisteredManagersList.get( intIndex );
			
			if ( lngSystemTimeMillis - RegisteredManagerInfo.lngLastUpdate >= lngMillisToOutdated ) {

				RegisteredManagersList.remove( RegisteredManagerInfo );

				bResult = true;

			}
			else {
				
				intIndex++;
				
			}

		}
		
		return bResult;
		
	} 
	
	public void setRegisteredManagersList( Vector<CRegisteredManagerInfo> RegisteredManagersList ) {
		
		this.RegisteredManagersList = RegisteredManagersList;
		
	}
	
	public Vector<CRegisteredManagerInfo> getRegisteredManagersList() {
		
		return this.RegisteredManagersList;
		
	}
}
