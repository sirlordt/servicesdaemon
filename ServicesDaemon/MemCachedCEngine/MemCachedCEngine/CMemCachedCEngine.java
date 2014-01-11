package MemCachedCEngine;

import java.util.concurrent.Future;

import net.spy.memcached.MemcachedClient;

import AbstractCacheEngine.IAbstractCacheEngine;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CMemCachedCEngine implements IAbstractCacheEngine {

	public final String _CacheName = "MemCachedCEngine";
	public final String _CacheVersion = "0.0.0.1";
	public final String _CacheDriver = "sypmemcached";
	
	protected MemcachedClient NativeCacheInstance = null;
	
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
		
		return NativeCacheInstance;
		
	}

	@Override
	public boolean initializeCacheEngine( CExtendedLogger Logger, CLanguage Lang ) {
		
		return false;
		
	}

	@Override
	public boolean addToCache(String strMaster, String strKey, int intLiveSeconds, Object ObjectToStore, CExtendedLogger Logger, CLanguage Lang ) {

		return false;
		
	}

	@Override
	public Object getFromCache( String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang ) {

		return null;
		
	}

	@Override
	public Future<Object> asyncGetFromCache(String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang ) throws Exception {

		return null;
		
	}

	@Override
	public boolean removeFromCache(String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang ) {

		return false;
		
	}

}
