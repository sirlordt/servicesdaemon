package ServicesDaemon;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

//import org.eclipse.jetty.server.Server;

import AbstractServicesManager.CAbstractServicesManager;
import CommonClasses.CLanguage;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.ConstantsMessagesCodes;
import ExtendedLogger.CExtendedLogger;

public class CAtferRunServerTask extends TimerTask {
	
	//public Server MainServer = null;
	public IMessageObject MessageToProgramRunner = null;
	public ArrayList<CAbstractServicesManager> RegisteredServicesManagers = null;
	public CConfigServicesDaemon ServicesDaemonConfig = null;
	public CExtendedLogger ServicesDaemonLogger = null;
	public CLanguage ServicesDaemonLang = null;
	
	public Timer AtferRunServerTimer = null;
	
	boolean bTaskRunningLock = false;
	
	@Override
	public void run() {
		
        if ( (Boolean) MessageToProgramRunner.sendMessage( ConstantsMessagesCodes._Server_Running, null ) == true && bTaskRunningLock == false ) {

        	try {
        	
        		this.cancel(); //Cancel the task

        		AtferRunServerTimer.cancel(); //Cancel the timer

        		bTaskRunningLock = true; //Don't run again the task

        		LinkedHashMap<String,Object> DataInfo = new LinkedHashMap<String,Object>();

        		DataInfo.put( "RegisteredServicesManagers", RegisteredServicesManagers );

        		for ( CAbstractServicesManager AbstractServicesManager: RegisteredServicesManagers ) {

        			ServicesDaemonLogger.logInfo( "1", ServicesDaemonLang.translate( "Post initialize of services manager: [%s]", AbstractServicesManager.getContextPath() ) );
        			AbstractServicesManager.postInitManager( ServicesDaemonConfig, DataInfo );

        		}

        		bTaskRunningLock = false;

        	}
        	catch ( Error Err ) {

        		if ( ServicesDaemonLogger != null )
        			ServicesDaemonLogger.logError( "-1020", Err.getMessage(), Err );

        	}
        	catch ( Exception Ex ) {

        		if ( ServicesDaemonLogger != null )
        			ServicesDaemonLogger.logException( "-1021", Ex.getMessage(), Ex );

        	}
          	
        }
		
	}

}
