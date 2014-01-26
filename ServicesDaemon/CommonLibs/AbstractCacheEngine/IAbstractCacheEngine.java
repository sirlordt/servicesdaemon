package AbstractCacheEngine;

import java.util.concurrent.Future;

import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

//import java.io.Serializable;
//import java.util.ArrayList;

public interface IAbstractCacheEngine {

	public String getName();
	
	public String getVersion();

	public String getDriverName();
	
	public Object getNativeCacheInstance();
	
	public void beginTransaction( CExtendedLogger Logger, CLanguage Lang );
	
	public void commitTransaction( CExtendedLogger Logger, CLanguage Lang );
	
	public void rollbackTransaction( CExtendedLogger Logger, CLanguage Lang );
	
	public boolean initializeCacheEngine( CExtendedLogger Logger, CLanguage Lang );
	
	boolean addToCache( String strMaster, String strKey, int intLiveSeconds, Object ObjectToStore, CExtendedLogger Logger, CLanguage Lang );

	boolean replaceOnCache( String strMaster, String strKey, int intLiveSeconds, Object ObjectToStore, CExtendedLogger Logger, CLanguage Lang );

	Object getFromCache( String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang );
	
	Future<Object> asyncGetFromCache( final String strMaster, final String strKey, final CExtendedLogger Logger, final CLanguage Lang ) throws Exception;
	
	boolean removeFromCache( String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang );
	
}
