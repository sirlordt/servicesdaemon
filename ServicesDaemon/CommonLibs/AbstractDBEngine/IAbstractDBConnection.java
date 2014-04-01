package AbstractDBEngine;

import java.io.Serializable;

import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public interface IAbstractDBConnection extends Serializable {
	
	public void setEngineNameAndVersion( String strEngineName, String strEngineVersion );
	public String getEngineName();
	public String getEngineVersion();
	
	public Object getDBConnection();
	public void setDBConnection( Object DBConnection );
	
	public Object getConfigDBConnection();
	public void setConfigDBConnection( Object ConfigDBConnection );
	
	public Object getDBConnectionSemaphore();
	public void setDBSemaphore( Object DBSemaphore );

	public boolean lockConnection( boolean bCheckAvailablePermits, CExtendedLogger Logger, CLanguage Lang );
	public void unlockConnection( CExtendedLogger Logger, CLanguage Lang );
	
	public String getSecurityTokenID();
	public void setSecurityTokenID( String strSecurityTokenID );

	public String getTransactionID();
	public void setTransactionID( String strTransactionID );
	
	public Serializable getExtraObjectInfo( String strObjectKey );
	public void setExtraObjectInfo( String strObjectKey, Serializable ExtraObjectInfo );

	public boolean rebuildTransientsObjects( CExtendedLogger Logger, CLanguage Lang );
	
}
