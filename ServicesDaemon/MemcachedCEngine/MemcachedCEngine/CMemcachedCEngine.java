package MemcachedCEngine;

import java.util.concurrent.Future;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.OperationFuture;

import AbstractCacheEngine.IAbstractCacheEngine;
import CommonClasses.CLanguage;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsCommonConfigXMLTags;
import ExtendedLogger.CExtendedLogger;

public class CMemcachedCEngine implements IAbstractCacheEngine {

	public final String _CacheName = "MemCachedCEngine";
	public final String _CacheVersion = "0.0.0.1";
	public final String _CacheDriver = "sypmemcached";
	
	CConfigMemcachedCEngine ConfigMemCachedCEngine = null;
	
	protected MemcachedClient NativeCacheInstance = null;

	protected String strRunningPath = "";
	
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
	public void beginTransaction( CExtendedLogger Logger, CLanguage Lang ) {
		
		Logger.logWarning( "-1", Lang.translate( "Memcached not support transactions" ) );        
		
	}

	@Override
	public void commitTransaction( CExtendedLogger Logger, CLanguage Lang ) {
		
		Logger.logWarning( "-1", Lang.translate( "Memcached not support transactions" ) );        

	}

	@Override
	public void rollbackTransaction( CExtendedLogger Logger, CLanguage Lang ) {
		
		Logger.logWarning( "-1", Lang.translate( "Memcached not support transactions" ) );        

	}
	
	@Override
	public boolean initializeCacheEngine( CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		this.strRunningPath = net.maindataservices.Utilities.getJarFolder( this.getClass() , Logger );
		
		ConfigMemCachedCEngine = new CConfigMemcachedCEngine( this.strRunningPath );
		
		if ( ConfigMemCachedCEngine.loadConfig( this.strRunningPath + ConstansCEngine._Conf_File, Lang, Logger ) ) { 
			
			try {
			
				AuthDescriptor ad = null;

				if ( ConfigMemCachedCEngine.strUser.isEmpty() == false ) {

					ad = new AuthDescriptor( new String[]{"PLAIN"}, new PlainCallbackHandler( ConfigMemCachedCEngine.strUser, net.maindataservices.Utilities.uncryptString( ConstantsCommonConfigXMLTags._Password_Crypted, ConstantsCommonConfigXMLTags._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, ConfigMemCachedCEngine.strPassword, Logger, Lang ) ) );

				}

				if ( ConfigMemCachedCEngine.strProtocol.equalsIgnoreCase( ConstantsConfigXMLTags._Binary ) ) {

					if ( ad != null ) {

						NativeCacheInstance = new MemcachedClient( new ConnectionFactoryBuilder().setProtocol( Protocol.BINARY ).setAuthDescriptor( ad ).build(), AddrUtil.getAddresses( ConfigMemCachedCEngine.strServersList ) );


					}
					else {

						NativeCacheInstance = new MemcachedClient( new ConnectionFactoryBuilder().setProtocol( Protocol.BINARY ).build(), AddrUtil.getAddresses( ConfigMemCachedCEngine.strServersList ) );

					}

				}
				else {

					if ( ad != null ) {

						NativeCacheInstance = new MemcachedClient( new ConnectionFactoryBuilder().setAuthDescriptor( ad ).build(), AddrUtil.getAddresses( ConfigMemCachedCEngine.strServersList ) );


					}
					else {

						NativeCacheInstance = new MemcachedClient( new ConnectionFactoryBuilder().build(), AddrUtil.getAddresses( ConfigMemCachedCEngine.strServersList ) );

					}

				}
				
			}
			catch ( Exception Ex ) {
				
				if ( Logger != null )
					Logger.logException( "-1020", Ex.getMessage(), Ex );
				
			}
			catch ( Error Err ) {

				if ( Logger != null )
					Logger.logError( "-1021", Err.getMessage(), Err );
				
			}
			
			bResult = NativeCacheInstance != null;
			
		}
		
		return bResult;
		
	}

	@Override
	public boolean addToCache( String strMaster, String strKey, int intLiveSeconds, Object ObjectToStore, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {
			
			NativeCacheInstance.add( strKey, intLiveSeconds, ObjectToStore );
			
			bResult = true;
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1020", Ex.getMessage(), Ex );
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1021", Err.getMessage(), Err );
			
		}
		
		return bResult;
		
	}

	@Override
	public boolean replaceOnCache( String strMaster, String strKey, int intLiveSeconds, Object ObjectToStore, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {
			
			NativeCacheInstance.replace( strKey, intLiveSeconds, ObjectToStore );
			
			bResult = true;
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1020", Ex.getMessage(), Ex );
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1021", Err.getMessage(), Err );
			
		}
		
		return bResult;
		
	}
	
	@Override
	public Object getFromCache( String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang ) {

		Object Result = null;
		
		try {
			
			Result = NativeCacheInstance.get( strKey );
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1020", Ex.getMessage(), Ex );
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1021", Err.getMessage(), Err );
			
		}
		
		return Result;
		
	}

	@Override
	public Future<Object> asyncGetFromCache( String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang ) throws Exception {

		Future<Object> Result = null;
		
		try {
			
			Result = NativeCacheInstance.asyncGet( strKey );
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1020", Ex.getMessage(), Ex );
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1021", Err.getMessage(), Err );
			
		}
		
		return Result;
		
	}

	@Override
	public boolean removeFromCache( String strMaster, String strKey, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {
			
			OperationFuture<Boolean> OpResult = NativeCacheInstance.delete( strKey );
			
			bResult = OpResult.get();
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1020", Ex.getMessage(), Ex );
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1021", Err.getMessage(), Err );
			
		}
		
		return bResult;
		
	}

}
