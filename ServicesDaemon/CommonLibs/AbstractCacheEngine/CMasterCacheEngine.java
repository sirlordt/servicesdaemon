package AbstractCacheEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import CommonClasses.CClassPathLoader;
import CommonClasses.CLanguage;
import CommonClasses.ConstantsCommonClasses;
import ExtendedLogger.CExtendedLogger;

public class CMasterCacheEngine implements IAbstractCacheEngine {

	public final String _CacheName = "MasterCacheEngine";
	public final String _CacheVersion = "0.0.0.1";
	public final String _CacheDriver = "all";
	
	protected static CMasterCacheEngine MasterCacheEngine = null;
	
	public static CMasterCacheEngine getMasterCacheEngine() {
		
		if ( MasterCacheEngine == null ) {
			
			MasterCacheEngine = new CMasterCacheEngine();
			
		}
		
		return MasterCacheEngine;
		
	}
	
	protected ArrayList<IAbstractCacheEngine> RegisteredCacheEngines = null;
	protected int intCountRegisteredCacheEngines = 0;
	
	CMasterCacheEngine() {
	
		RegisteredCacheEngines = new ArrayList<IAbstractCacheEngine>();
		
	}
	
	@Override
	public String getName() {

		return _CacheName;
		
	}


	@Override
	public String getVersion() {

		return _CacheVersion;
		
	}

	@Override
	public String getDriverName() {

		return _CacheDriver;
		
	}

	@Override
	public Object getNativeCacheInstance() {

		return null;
		
	}

	@Override
	public boolean initializeCacheEngine( CExtendedLogger Logger, CLanguage Lang ) {

		return false;
		
	}
	
	public boolean loadAndRegisterCacheEnginesFromDir( String strPathToDir, CClassPathLoader ClassPathLoader, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {
		
			if ( ClassPathLoader == null ) {

				ClassPathLoader = new CClassPathLoader();

    			ClassPathLoader.LoadClassFiles( strPathToDir, ConstantsCommonClasses._Lib_Ext, 2, Logger, Lang );
				
			}
			
			ServiceLoader<IAbstractCacheEngine> sl = ServiceLoader.load( IAbstractCacheEngine.class );
			sl.reload();
			
			RegisteredCacheEngines.clear();
		
			Iterator<IAbstractCacheEngine> it = sl.iterator();

			while ( it.hasNext() ) {

				try {
					
					IAbstractCacheEngine CacheEngineInstance = it.next();

					if ( CacheEngineInstance.initializeCacheEngine( Logger, Lang ) == true ) {
					   
						RegisteredCacheEngines.add( CacheEngineInstance );

						Logger.logMessage( "1", Lang.translate( "Registered cache engine: [%s] version: [%s]", CacheEngineInstance.getName().toLowerCase(), CacheEngineInstance.getVersion() ) );        
					
					}
					
				} 
				catch ( Error Err ) {

					if ( Logger != null )
						Logger.logError( "-1020", Err.getMessage(), Err );
					
				}
				catch ( Exception Ex ) {

					if ( Logger != null )
						Logger.logException( "-1021", Ex.getMessage(), Ex );

				}

			}
    		
			int intCountRegisteredCacheEngines = RegisteredCacheEngines.size();
			
			Logger.logMessage( "1", Lang.translate( "Count of cache engines registered: [%s]", Integer.toString( intCountRegisteredCacheEngines ) ) );        

			bResult = intCountRegisteredCacheEngines > 0;
			
		} 
		catch ( Exception Ex ) {

			if ( Logger != null ) {
        		
				Logger.logException( "-1015", Ex.getMessage(), Ex );
				
			}	

		}
		
		return bResult;
		
	}
	
	public boolean registerCacheEngine( IAbstractCacheEngine CacheEngineInstance, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		if ( CacheEngineInstance != null ) {
			
			RegisteredCacheEngines.add( CacheEngineInstance );

			Logger.logMessage( "1", Lang.translate( "Registered cache engine: [%s] version: [%s]", CacheEngineInstance.getName().toLowerCase(), CacheEngineInstance.getVersion() ) );        
			
			bResult = true;
			
		}

		intCountRegisteredCacheEngines = RegisteredCacheEngines.size();
		
		Logger.logMessage( "1", Lang.translate( "Count of cache engines registered: [%s]", Integer.toString( intCountRegisteredCacheEngines ) ) );        
		
		return bResult;
		
	} 
	
	public boolean unregisterCacheEngine( IAbstractCacheEngine CacheEngineInstance, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		if ( CacheEngineInstance != null ) {
			
			RegisteredCacheEngines.remove( CacheEngineInstance );

			Logger.logMessage( "1", Lang.translate( "Unregistered cache engine: [%s] version: [%s]", CacheEngineInstance.getName().toLowerCase(), CacheEngineInstance.getVersion() ) );        
			
			bResult = true;
			
		}

		intCountRegisteredCacheEngines = RegisteredCacheEngines.size();
		
		Logger.logMessage( "1", Lang.translate( "Count of cache engines registered: [%s]", Integer.toString( intCountRegisteredCacheEngines ) ) );        
		
		return bResult;
		
	} 
	
	public ArrayList<IAbstractCacheEngine> getCacheEngineByName( String strName ) {
		
		ArrayList<IAbstractCacheEngine> Result = new ArrayList<IAbstractCacheEngine>();
		
		for ( int I = 0; I < RegisteredCacheEngines.size(); I++ ) {
			
			IAbstractCacheEngine RegisteredCacheEngine = RegisteredCacheEngines.get( I );
			
			if ( RegisteredCacheEngine.getName().equalsIgnoreCase( strName ) ) {
				
				Result.add( RegisteredCacheEngine );
				
			}
			
		}
		
		return Result;
		
	}

	public ArrayList<IAbstractCacheEngine> getCacheEngineByDriver( String strDriverName ) {
		
		ArrayList<IAbstractCacheEngine> Result = new ArrayList<IAbstractCacheEngine>();
		
		for ( int I = 0; I < RegisteredCacheEngines.size(); I++ ) {
			
			IAbstractCacheEngine RegisteredCacheEngine = RegisteredCacheEngines.get( I );
			
			if ( RegisteredCacheEngine.getDriverName().equalsIgnoreCase( strDriverName ) ) {
				
				Result.add( RegisteredCacheEngine );
				
			}
			
		}
		
		return Result;
		
	}
	
	@Override
	public boolean addToCache( String strMaster, String strKey, int intLiveSeconds, Object ObjectToStore, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;

		try {
		
			if ( intCountRegisteredCacheEngines > 0 ) {

				for ( int I=0; I < RegisteredCacheEngines.size(); I++ ) {

					IAbstractCacheEngine CacheEngineInstance = RegisteredCacheEngines.get( I );
					
					bResult = CacheEngineInstance.addToCache( strMaster, strKey, intLiveSeconds, ObjectToStore, Logger, Lang );

				}

			}
			else {
				
				Logger.logWarning( "-1", Lang.translate( "Not registered cache engines found" ) );        
				
			}
		
		} 
		catch ( Exception Ex ) {

			if ( Logger != null ) {
        		
				Logger.logException( "-1015", Ex.getMessage(), Ex );
				
			}	

		}
		
		return bResult;
		
	}

	@Override
	public Object getFromCache( String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang ) {
		
		Object Result = null;

		try {
		
			if ( intCountRegisteredCacheEngines > 0 ) {

				for ( int I=0; I < RegisteredCacheEngines.size(); I++ ) {

					IAbstractCacheEngine CacheEngineInstance = RegisteredCacheEngines.get( I );
					
					Result = CacheEngineInstance.getFromCache( strMaster, strKey, Logger, Lang );

					if ( Result != null ) {
						
						break;
						
					}
					
				}

			}
			else {
				
				Logger.logWarning( "-1", Lang.translate( "Not registered cache engines found" ) );        
				
			}
		
		} 
		catch ( Exception Ex ) {

			if ( Logger != null ) {
        		
				Logger.logException( "-1015", Ex.getMessage(), Ex );
				
			}	

		}
		
		return Result;

	}

	@Override
	public boolean removeFromCache( String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;

		try {
		
			if ( intCountRegisteredCacheEngines > 0 ) {

				for ( int I=0; I < RegisteredCacheEngines.size(); I++ ) {

					IAbstractCacheEngine CacheEngineInstance = RegisteredCacheEngines.get( I );
					
					CacheEngineInstance.removeFromCache( strMaster, strKey, Logger, Lang );

				}

			}
			else {
				
				Logger.logWarning( "-1", Lang.translate( "Not registered cache engines found" ) );        
				
			}
		
		} 
		catch ( Exception Ex ) {

			if ( Logger != null ) {
        		
				Logger.logException( "-1015", Ex.getMessage(), Ex );
				
			}	

		}
		
		return bResult;
		
	}

	@Override
	public Future<Object> asyncGetFromCache( final String strMaster, final String strKey, final CExtendedLogger Logger, final CLanguage Lang ) throws Exception {

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		
		final CMasterCacheEngine _this = this;
		
		return executorService.submit( new Callable<Object>() {
	    
			@Override
	        public Object call() throws Exception {;
		
	            return _this.getFromCache( strMaster, strKey, Logger, Lang );
	        
	        }
		
		});
		
		
	}
	
}
