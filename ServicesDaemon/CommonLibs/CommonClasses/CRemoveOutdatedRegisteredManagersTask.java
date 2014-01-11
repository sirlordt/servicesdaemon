package CommonClasses;

import ExtendedLogger.CExtendedLogger;

public class CRemoveOutdatedRegisteredManagersTask extends Thread {

	protected boolean bStopNow;
	
	protected int intFrequencyCheckRemove;
	
	protected boolean bTaskRunningLock;

	protected CExtendedLogger Logger;
	protected CLanguage Lang;
	
	protected int intSecondTimeout;
	
	public CRemoveOutdatedRegisteredManagersTask( String strName, CExtendedLogger Logger, CLanguage Lang, int intSecondTimeout, int intFrequencyCheckRemove ) {
		
		super( strName );
		
		this.bStopNow = false;
		
		this.intFrequencyCheckRemove = intFrequencyCheckRemove;
		
		this.bTaskRunningLock = false;
		
		this.Logger = Logger;
		this.Lang = Lang;
		
		this.intSecondTimeout = intSecondTimeout;
		
	}
	
	public synchronized boolean getStopNow() {
		
		return bStopNow;
		
	}
	
	public synchronized void setStopNow() {
		
		this.bStopNow = true;
		
	}
	
	public boolean getTaskRunningLock() {
		
		return bTaskRunningLock;
		
	} 
	
	@Override
	public void run() {
		
		try {
		
			while ( bStopNow == false ) {

				Thread.sleep( intFrequencyCheckRemove );
				
				if ( bStopNow ) return;
				
				bTaskRunningLock = true;

				CRegisteredManagersControl RegisteredManagersControl = CRegisteredManagersControl.getRegisteredManagersControl();

				if ( RegisteredManagersControl != null ) {

					RegisteredManagersControl.removeOutdatedRegisteredManagers( intSecondTimeout );				

				}

				bTaskRunningLock = false;

			}
		
		}
		catch ( Error Err ) {
			
			if ( Logger != null )
                Logger.logError( "-1020" , Err.getMessage(), Err ); 
				
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
                Logger.logException( "-1021" , Ex.getMessage(), Ex ); 
				
		}
		
	}

}
