package AbstractDBEngine;

import java.io.Serializable;

import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public abstract class CAbstractDBConnection implements Serializable {
	
	private static final long serialVersionUID = -8743052727884453690L;

	public abstract Object getDBConnection();
	public abstract void setDBConnection( Object DBConnection );
	
	public abstract Object getDBConnectionSemaphore();
	public abstract void setDBSemaphore( Object DBSemaphore );

	public abstract boolean lockConnection( boolean bCheckAvailablePermits, CExtendedLogger Logger, CLanguage Lang );
	public abstract void unlockConnection( CExtendedLogger Logger, CLanguage Lang );
	
	public abstract String getSecurityTokenID();
	public abstract void setSecurityTokenID( String strSecurityTokenID );

	public abstract String getTransactionID();
	public abstract void setTransactionID( String strTransactionID );
	
	public abstract Serializable getExtraObjectInfo( String strObjectKey );
	public abstract void setExtraObjectInfo( String strObjectKey, Serializable ExtraObjectInfo );

	public abstract boolean rebuildTransientsObjects( CExtendedLogger Logger, CLanguage Lang );
	
}
