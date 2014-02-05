package DBReplicator;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CMasterDBReplicator {

	protected static CMasterDBReplicator MasterDBReplicator = null;
	
	public static CMasterDBReplicator getMasterDBReplicator() {
		
		if ( MasterDBReplicator == null ) {
			
			MasterDBReplicator = new CMasterDBReplicator();
			
		}
		
		return MasterDBReplicator;
		
	}
	
	protected LinkedList<IDBReplicator> RegisteredDBReplicators = null; 
	
	public CMasterDBReplicator() {
		
		RegisteredDBReplicators = new LinkedList<IDBReplicator>();
		
	}
	
	public IDBReplicator getDBReplicatorByName( String strName ) {
		
		IDBReplicator Result = null;
		
		for ( IDBReplicator DBReplicator: RegisteredDBReplicators ) {
			
			if ( DBReplicator.getName().equalsIgnoreCase( strName ) ) {
				
				Result = DBReplicator;
				
				break;
				
			}
			
		}
		
		return Result;
		
	} 
	
	public void registerDBReplicator( IDBReplicator DBReplicator ) {

		RegisteredDBReplicators.add( DBReplicator );		
		
	}

	public boolean unregisterDBReplicator( IDBReplicator DBReplicator ) {

		return RegisteredDBReplicators.remove( DBReplicator );		
		
	}

	public boolean unregisterDBReplicatorByName( String strName ) {

		boolean bResult = false;
		
		IDBReplicator DBReplicator = null;
		
		do {
		
			DBReplicator = this.getDBReplicatorByName( strName );
			
			if ( DBReplicator != null ) {
			
				RegisteredDBReplicators.remove( DBReplicator );
				
				bResult = true;
				
			}	
		
		} while ( DBReplicator != null );
		
		return bResult;
		
	}
	
	public int getCountRegisteredDBReplicators() {
		
		return RegisteredDBReplicators.size();
		
	}
	
	public Future<Boolean> addComplexQueryCommandToQueue( final String strTransactionID, final String strCommand, final String strSourceDBConnectionName, final LinkedHashMap<String,String> Params, final CExtendedLogger Logger, final CLanguage Lang  ) {

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		
		return executorService.submit( new Callable<Boolean>() {
	    
			@Override
	        public Boolean call() throws Exception {;
		
	           for ( IDBReplicator DBReplicator: MasterDBReplicator.RegisteredDBReplicators ) {
	        	   
	        	   DBReplicator.addComplexQueryCommandToQueue( strTransactionID, strCommand, strSourceDBConnectionName, Params, Logger, Lang );	        	   

	           }    
	        
	           return true;
	           
	        }
		
		});
		
	}	

	public Future<Boolean> addPlainQueryCommandToQueue( final String strTransactionID, final String strCommand, final String strSourceDBConnectionName, final CExtendedLogger Logger, final CLanguage Lang  ) {

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		
		return executorService.submit( new Callable<Boolean>() {
	    
			@Override
	        public Boolean call() throws Exception {;
		
	           for ( IDBReplicator DBReplicator: MasterDBReplicator.RegisteredDBReplicators ) {
	        	   
	        	   DBReplicator.addPlainQueryCommandToQueue( strTransactionID, strCommand, strSourceDBConnectionName, Logger, Lang );	        	   

	           }    
	        
	           return true;
	           
	        }
		
		});
		
	}	
	
	public void initReplicators() {
		
		for ( IDBReplicator DBReplicator: RegisteredDBReplicators ) {
			
			DBReplicator.start();
			
		}
		
	}
	
}
