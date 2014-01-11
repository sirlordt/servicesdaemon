package CommonClasses;

import java.util.ArrayList;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import ExtendedLogger.CExtendedLogger;

public class CRegisterManagerTask extends Thread {

	protected boolean bStopNow;
	
	protected int intThreadSleepMillis;
	
	protected boolean bTaskRunningLock;

	protected int intCountRunned; 
	
	protected CExtendedLogger Logger;
	protected CLanguage Lang;
	
	protected ArrayList<CConfigRegisterService> ConfiguredRegisterServices;
	
	protected ArrayList<CConfigNetworkInterface> ConfiguredNetworkInterfaces;
	
	protected String strRegisterContext; // BPServices
	
	protected CloseableHttpClient HTTPClient; //HttpClients.createDefault();
	
	protected CBackendServicesManager BackendServicesManager;
	
	public CRegisterManagerTask( String strName, CExtendedLogger Logger, CLanguage Lang, ArrayList<CConfigRegisterService> ConfiguredRegisterServices, ArrayList<CConfigNetworkInterface> ConfiguredNetworkInterfaces, String strRegisterContext, String strPathToTempDir, int intThreadSleepMillis, int intRequestTimetout, int intSocketTimeout ) {
		
		super( strName );
		
		this.bStopNow = false;
		
		this.intThreadSleepMillis = intThreadSleepMillis;
		
		this.bTaskRunningLock = false;

		this.intCountRunned = 0; 
		
		this.Logger = Logger;
		this.Lang = Lang;
		
		this.ConfiguredRegisterServices = ConfiguredRegisterServices;
		
		this.strRegisterContext = strRegisterContext;

		this.ConfiguredNetworkInterfaces = ConfiguredNetworkInterfaces;
		
		//this.HTTPClient = HttpClients.createDefault();
		
		RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout( intRequestTimetout ).setConnectionRequestTimeout( intRequestTimetout ).setSocketTimeout( intSocketTimeout ).build();
		
		this.HTTPClient = HttpClientBuilder.create().setDefaultRequestConfig( clientConfig ).build(); // HttpClients.createDefault();
		
		this.BackendServicesManager = new CBackendServicesManager( strPathToTempDir );
		
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
	
	public ArrayList<CConfigNetworkInterface> buildNetworkInterfacesList() {
		
		ArrayList<CConfigNetworkInterface> Result = new ArrayList<CConfigNetworkInterface>();
		
		for ( CConfigNetworkInterface ConfigNetworkInterface: ConfiguredNetworkInterfaces ) {

			if ( ConfigNetworkInterface.strIP.equals( net.maindataservices.Utilities._IPV4Localhost ) == false || ConfigNetworkInterface.strIP.equals( net.maindataservices.Utilities._IPV6Localhost ) == false ) {

				if ( ConfigNetworkInterface.strIP.equals( net.maindataservices.Utilities._IPV4All ) || ConfigNetworkInterface.strIP.equals( net.maindataservices.Utilities._IPV6All ) ) {
					
					ArrayList<String> NetAddressList = net.maindataservices.Utilities.getNetAddressList( Logger, false );
					
					for ( String strIP: NetAddressList ) {

						if ( CConfigNetworkInterface.checkNetAddressExists( Result, strIP ) == false ) {
							
							CConfigNetworkInterface NewConfigNetworkInterface = new CConfigNetworkInterface( ConfigNetworkInterface );
							
							if ( strIP.startsWith( "/" ) )
								NewConfigNetworkInterface.strIP = strIP.substring( 1 );
							else	
								NewConfigNetworkInterface.strIP = strIP;
							
							Result.add( NewConfigNetworkInterface );
							
						}
						
					}
					
				}
				else {
					
					CConfigNetworkInterface NewConfigNetworkInterface = new CConfigNetworkInterface( ConfigNetworkInterface );

					Result.add( NewConfigNetworkInterface );
					
				}
				
				
			}
			
		}
		
		return Result;
		
	}
		
	public void registerManager() {
		
		try {
		
			intCountRunned += 1;
			
			for ( CConfigRegisterService ConfigRegisterService: ConfiguredRegisterServices ) {

				if ( bStopNow )
					return;
				
				int intLoad = 0;

				Long lngTimeMillis = System.currentTimeMillis();

				Long lngCurrentRun = lngTimeMillis - ConfigRegisterService.lngLastUpdate;

				if ( lngCurrentRun > ( ConfigRegisterService.intInterval ) ) {

					if ( ConfigRegisterService.bReportLoad ) {

						//Calculate the load for this System from 0 to 100
						intLoad = net.maindataservices.Utilities.getSystemLoad( true );

					}

					ArrayList<CConfigNetworkInterface> NetworkInterfacesListToRegistered = this.buildNetworkInterfacesList(); //new ArrayList<String>();

					for ( CConfigNetworkInterface NetworkInterfaceToRegistered: NetworkInterfacesListToRegistered ) {

						String strManagerURL = NetworkInterfaceToRegistered.strIP + ":" + Integer.toString( NetworkInterfaceToRegistered.intPort );

						if ( strRegisterContext.startsWith( "/" ) )
							strManagerURL = strManagerURL + strRegisterContext;
						else
							strManagerURL = strManagerURL + "/" + strRegisterContext;

						if ( NetworkInterfaceToRegistered.bUseSSL ) {

							strManagerURL = "https://" + strManagerURL;

						}
						else {

							strManagerURL = "http://" + strManagerURL;

						}

						if ( bStopNow == false ) {
						
							if ( ConfigRegisterService.intReportIPType == 0 || ( ConfigRegisterService.intReportIPType == 1 && net.maindataservices.Utilities.isValidIPV4( NetworkInterfaceToRegistered.strIP ) ) || ( ConfigRegisterService.intReportIPType == 2 && net.maindataservices.Utilities.isValidIPV6( NetworkInterfaceToRegistered.strIP ) ) )
								BackendServicesManager.callServiceSystemRegisterManager( HTTPClient, ConfigRegisterService.strPassword, ConfigRegisterService.strURL, ConfigRegisterService.ConfigProxy, strRegisterContext, strManagerURL, ConfigRegisterService.intWeight, intLoad, Logger, Lang );
						
						}
						else
							return;
						
					}

					ConfigRegisterService.lngLastUpdate = lngTimeMillis;

				}

			}
			
			if ( intCountRunned == 30 ) {  
			
				BackendServicesManager.deleteTempResponsesFiles( Logger, Lang );
			
				intCountRunned = 0;
				
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
	
	public void unregisterManager() {
		
		for ( CConfigRegisterService ConfigRegisterService: ConfiguredRegisterServices ) {

			ArrayList<CConfigNetworkInterface> NetworkInterfacesListToUnregister = this.buildNetworkInterfacesList();
			
			for ( CConfigNetworkInterface NetworkInterfaceToUnregistered: NetworkInterfacesListToUnregister ) {
						
				String strManagerURL = NetworkInterfaceToUnregistered.strIP + ":" + Integer.toString( NetworkInterfaceToUnregistered.intPort ) + "/" + strRegisterContext;

				if ( NetworkInterfaceToUnregistered.bUseSSL ) {

					strManagerURL = "https://" + strManagerURL;

				}
				else {

					strManagerURL = "http://" + strManagerURL;

				}

				BackendServicesManager.callServiceSystemUnregisterManager( HTTPClient, ConfigRegisterService.strPassword, ConfigRegisterService.strURL, ConfigRegisterService.ConfigProxy, strRegisterContext, strManagerURL, Logger, Lang );
				
			}
			
		}
	
		BackendServicesManager.deleteTempResponsesFiles( Logger, Lang );
		
	}
	
	@Override
	public void run() {

		try { 

			while ( this.getStopNow() == false ) {

				bTaskRunningLock = true;

				registerManager();

				bTaskRunningLock = false;

				Thread.sleep( this.intThreadSleepMillis );
				
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
