/*******************************************************************************
 * Copyright (c) 2013 SirLordT <sirlordt@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     SirLordT <sirlordt@gmail.com> - initial API and implementation
 ******************************************************************************/
package CommonClasses;

//import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.sql.*;

import DBServicesManager.CConfigDBConnection;
import ExtendedLogger.CExtendedLogger;

public class CDBConnectionsManager {
	
	protected static CDBConnectionsManager DBConnectionManager = null;
 	
	static {
		
		DBConnectionManager = new CDBConnectionsManager(); //( null, null );
		
	}
	
	/*public static CDBConnectionsManager getDBConnectionManager( CExtendedLogger Logger, CLanguage Lang ) {
		
		DBConnectionManager.Logger = Logger;
		DBConnectionManager.Lang = Lang;
		
		return DBConnectionManager;
		
	}*/

	public static CDBConnectionsManager getDBConnectionManager() {
		
		return DBConnectionManager;
		
	}

    //protected CExtendedLogger Logger; 
    //protected CLanguage Lang;

	protected HashMap<String,Connection> DBConnectionFromTransactionID;  //TransactionID => DBConnection
	protected HashMap<String,ArrayList<String>> TransactionsIDFromSecurityTokenID;  //SecurityToken => array( TransactionID, TransactionID )
	protected HashMap<String,Semaphore> DBSConnectionSemaphoreFromTransactionID; //TransactionID => DBSemaphore
	protected HashMap<String,CConfigDBConnection> ConfigDBConnectionFromSecurityTokenID; // SecurityTokenID => ConfigDBConnection 
	
	protected HashMap<String,String> SecurityTokenIDFromTransactionID; //TransactionID => SecurityTokenID
	
	public CDBConnectionsManager( /*CExtendedLogger Logger, CLanguage Lang*/ ) {

		/*this.Logger = Logger;
		this.Lang = Lang;*/
		
		DBConnectionFromTransactionID = new HashMap<String,Connection>();
		DBSConnectionSemaphoreFromTransactionID = new HashMap<String,Semaphore>();
		TransactionsIDFromSecurityTokenID = new HashMap<String,ArrayList<String>>();
		SecurityTokenIDFromTransactionID = new HashMap<String,String>();
		ConfigDBConnectionFromSecurityTokenID = new HashMap<String,CConfigDBConnection>();
		
	}
	
	/*public CExtendedLogger getLogger() {
		
		return Logger;
		
	}

	public void setLogger( CExtendedLogger Logger ) {
		
		this.Logger = Logger;
		
	}

	public CLanguage getLang() {
		
		return Lang;
		
	}

	public void setLang( CLanguage Lang ) {
		
		this.Lang = Lang;
		
	}

	public void Initialize( CExtendedLogger Logger, CLanguage Lang ) {
		
		this.Logger = Logger;
		this.Lang = Lang;
		
	}*/
	
	public synchronized Connection getDBConnection( String strTransactionID, CExtendedLogger Logger, CLanguage Lang ) {

		Connection DBConnection = DBConnectionFromTransactionID.get( strTransactionID );

		if ( DBConnection == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "DBConnection", "TransactionID", strTransactionID ) );
			
		}

		return DBConnection;

	}

	public synchronized Semaphore getDBConnectionSemaphore( String strTransactionID, CExtendedLogger Logger, CLanguage Lang ) {

		Semaphore DBConnectionSemaphore = DBSConnectionSemaphoreFromTransactionID.get( strTransactionID );

		if ( DBConnectionSemaphore == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "DBConnectionSemaphore", "TransactionID", strTransactionID ) );
			
		}
		
		return DBConnectionSemaphore;

	}
	
	public synchronized CConfigDBConnection getConfigDBConnection( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {
	
		CConfigDBConnection ConfigDBConnection = ConfigDBConnectionFromSecurityTokenID.get( strSecurityTokenID );  
				
		if ( ConfigDBConnection == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "ConfigDBConnection", "strSecurityTokenID", strSecurityTokenID ) );
			
		}

		return ConfigDBConnection;
		
	} 

	public synchronized String getSecurityTokenID( String strTransactionID, CExtendedLogger Logger, CLanguage Lang ) {

		String strSecurityTokenID = SecurityTokenIDFromTransactionID.get( strTransactionID );

		if ( strSecurityTokenID == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "TransactionID", strTransactionID ) );
			
		}

		return strSecurityTokenID;

	}

	public synchronized ArrayList<String> getTransactionsIDFromSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {

		ArrayList<String> TransactionsID = TransactionsIDFromSecurityTokenID.get( strSecurityTokenID );
		
		if ( TransactionsID == null && Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "[%s] from [%s] = [%s] not found", "TransactionsID", "SecurityTokenID", strSecurityTokenID ) );
			
		}
		
		return TransactionsID;

	}

	public synchronized boolean addDBConnection( String strSecurityTokenID, String strTransactionID, Connection DBConnectionToAdd, CConfigDBConnection ConfigDBConnectionToAdd, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		if ( DBConnectionFromTransactionID.get( strTransactionID ) == null ) {

			DBConnectionFromTransactionID.put( strTransactionID, DBConnectionToAdd );

			Semaphore DBConnectionSemaphore = new Semaphore( 1 );

			DBSConnectionSemaphoreFromTransactionID.put( strTransactionID, DBConnectionSemaphore );

			SecurityTokenIDFromTransactionID.put( strTransactionID, strSecurityTokenID );
			
			ConfigDBConnectionFromSecurityTokenID.put( strSecurityTokenID, ConfigDBConnectionToAdd );

			bResult = true;

		}

		ArrayList<String> strlstTransactionsId = TransactionsIDFromSecurityTokenID.get( strSecurityTokenID );

		if ( strlstTransactionsId == null ) {

			strlstTransactionsId = new ArrayList<String>();

		}

		strlstTransactionsId.add( strTransactionID );

		TransactionsIDFromSecurityTokenID.put( strSecurityTokenID, strlstTransactionsId );
		
		if ( Logger != null && Lang != null ) {
			
			Logger.LogMessage( "1" , Lang.Translate( "Added [%s] [%s] = [%s] and [%s] = [%s]", "DBConnection", "TransactionID", strTransactionID, "SecurityToken", strSecurityTokenID ) );
			
		}

		return bResult;

	}

	public synchronized boolean removeDBConnectionByTransactionId( /*String strSecurityTokenID,*/ String strTransactionID, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		String strSecurityTokenID = SecurityTokenIDFromTransactionID.get( strTransactionID );
		
		if ( strSecurityTokenID != null && strSecurityTokenID.equals( "" ) == false ) {

			ArrayList<String> strlstTrasactionsId = TransactionsIDFromSecurityTokenID.get( strSecurityTokenID );

			if ( strlstTrasactionsId != null ) {

				int intTransactionIndex = strlstTrasactionsId.indexOf( strTransactionID ); 

				if ( intTransactionIndex >= 0 ) {

					strlstTrasactionsId.remove( strTransactionID );

				}

				TransactionsIDFromSecurityTokenID.put( strSecurityTokenID, strlstTrasactionsId );

			}

		}

		SecurityTokenIDFromTransactionID.remove( strTransactionID );

		if ( DBConnectionFromTransactionID.get( strTransactionID ) != null ) {

			Semaphore DBConnectionSemaphore = (Semaphore) DBSConnectionSemaphoreFromTransactionID.get( strTransactionID );

			if ( DBConnectionSemaphore != null ) {

				boolean bUnlockSemaphore = false;

				try {

					if ( DBConnectionSemaphore.availablePermits() >= 1 ) {

						bUnlockSemaphore = true;

						DBConnectionSemaphore.acquire();

					}

				}
				catch ( Exception Ex ) {

					if ( Logger != null ) {
						
						Logger.LogException( "-1010" , Ex.getMessage(), Ex );
						
					}
					else {
						
						System.out.println( Ex );
						Ex.printStackTrace( System.out );
						
					}

				}


				DBConnectionFromTransactionID.remove( strTransactionID );
				DBSConnectionSemaphoreFromTransactionID.remove( strTransactionID );

				if ( bUnlockSemaphore == true ) {

					DBConnectionSemaphore.release();

				}

			}

			bResult = true;

			if ( Logger != null && Lang != null ) {
				
				Logger.LogWarning( "-1" , Lang.Translate( "Removed [%s] by [%s] = [%s]", "DBConnection", "TransactionID", strTransactionID ) );
				
			}
			
		}

		return bResult;

	}

	public synchronized boolean removeDBConnectionsBySecurityToken( String strSecurityToken, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		ArrayList<String> strlstTrasactionsId = (ArrayList<String>) TransactionsIDFromSecurityTokenID.get( strSecurityToken );

		if ( strlstTrasactionsId != null ) {

			for ( int I=0; I < strlstTrasactionsId.size(); I++ ) {

				bResult = removeDBConnectionByTransactionId( strlstTrasactionsId.get( I ), Logger, Lang );

			}

		}
		else {

			bResult = true;

		}

		TransactionsIDFromSecurityTokenID.remove( strSecurityToken );

		if ( Logger != null && Lang != null ) {
			
			Logger.LogWarning( "-1" , Lang.Translate( "Removed [%s] by [%s] = [%s]", "DBConnection", "SecurityToken", strSecurityToken ) );
			
		}

		return bResult;

	}

}
