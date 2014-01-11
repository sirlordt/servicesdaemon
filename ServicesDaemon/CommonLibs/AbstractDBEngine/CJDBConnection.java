package AbstractDBEngine;

import java.io.Serializable;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.concurrent.Semaphore;

import CommonClasses.CConfigNativeDBConnection;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CJDBConnection extends CAbstractDBConnection {

	private static final long serialVersionUID = 8690527474914592870L;

	protected transient Connection DBConnection;
	protected transient Semaphore DBSemaphore;
	protected String strSecurityTokenID;
	protected String strTransactionID;
	
	protected LinkedHashMap<String,Serializable> ExtraObjectsInfoList;
	
	public CJDBConnection() {
		
		DBConnection = null;
		DBSemaphore = null;
		strSecurityTokenID = "";
		strTransactionID = "";
		ExtraObjectsInfoList = new LinkedHashMap<String,Serializable>();
		
	}

	public CJDBConnection( Connection DBConnection, Semaphore DBSemaphore, String strSecurityTokenID, String strTransactionID ) {
		
		this.DBConnection = DBConnection;
		this.DBSemaphore = DBSemaphore;
		this.strSecurityTokenID = strSecurityTokenID;
		this.strTransactionID = strTransactionID;
		ExtraObjectsInfoList = new LinkedHashMap<String,Serializable>();
		
	}
	
	@Override
	public Object getDBConnection() {

		return DBConnection;
		
	}

	@Override
	public void setDBConnection( Object DBConnection ) {
		
		this.DBConnection = (Connection) DBConnection;
		
	}

	@Override
	public Object getDBConnectionSemaphore() {

		return DBSemaphore;
		
	}

	@Override
	public void setDBSemaphore( Object DBSemaphore ) {

		this.DBSemaphore = (Semaphore) DBSemaphore;
		
	}
	
	@Override
	public boolean lockConnection( boolean bCheckAvailablePermits, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = false;
		
		if ( DBSemaphore != null ) {
		
			try {
				
				if ( bCheckAvailablePermits == false || DBSemaphore.availablePermits() >= 1 ) {

					DBSemaphore.acquire();

					bResult = true;
					
				}
			
			} 
			catch ( Exception Ex ) {

				if ( Logger != null )
					Logger.logException( "-1015", Ex.getMessage(), Ex );
				
			}
			
		}
		
		return bResult;
		
	}

	@Override
	public void unlockConnection( CExtendedLogger Logger, CLanguage Lang ) {

		if ( DBSemaphore != null ) {
			
			try {
				
				DBSemaphore.release();
			
			} 
			catch ( Exception Ex ) {

				if ( Logger != null )
					Logger.logException( "-1015", Ex.getMessage(), Ex );
				
			}
			
		}
		
	}

	@Override
	public String getSecurityTokenID() {

		return strSecurityTokenID;
		
	}

	@Override
	public void setSecurityTokenID( String strSecurityTokenID ) {

        this.strSecurityTokenID = strSecurityTokenID;
		
	}

	@Override
	public String getTransactionID() {

		return strTransactionID;
		
	}

	@Override
	public void setTransactionID( String strTransactionID ) {

		this.strTransactionID = strTransactionID;
		
	}

	@Override
	public synchronized Serializable getExtraObjectInfo( String strObjectKey ) {

		return ExtraObjectsInfoList.get( strObjectKey );
		
	}

	@Override
	public synchronized void setExtraObjectInfo(String strObjectKey, Serializable ExtraObjectInfo) {

		ExtraObjectsInfoList.put( strObjectKey, ExtraObjectInfo );
		
	}

	@Override
	public synchronized boolean rebuildTransientsObjects( CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false; 
		
		if ( DBSemaphore == null ) {
			
			DBSemaphore = new Semaphore( 1 ); //Recreate semaphore
			
		}
		
		try {

			if ( DBConnection == null ) {
			
				CConfigNativeDBConnection LocalConfigDBConnection = (CConfigNativeDBConnection) ExtraObjectsInfoList.get( "ConfigDBConnection" );

				if ( LocalConfigDBConnection != null ) {

					//Try to reconnect

					CAbstractDBEngine DBEngine = CAbstractDBEngine.getDBEngine( LocalConfigDBConnection.strEngine, LocalConfigDBConnection.strEngineVersion ); 

					if ( DBEngine != null ) {

						CAbstractDBConnection InternalDBConnection = DBEngine.getDBConnection( LocalConfigDBConnection.getDBEngineConfigConnection( false ), Logger, Lang );

						this.DBConnection = (Connection) InternalDBConnection.getDBConnection();
						
						InternalDBConnection = null;

						bResult = true;

					}

				}
			
			}

		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1015", Ex.getMessage(), Ex );

		}

		return bResult;
		
	}
	
}
