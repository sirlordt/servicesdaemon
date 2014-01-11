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
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
//import java.sql.*;

import AbstractDBEngine.CAbstractDBConnection;
import ExtendedLogger.CExtendedLogger;

public class CNativeDBConnectionsManager {
	
	protected static CNativeDBConnectionsManager NativeDBConnectionManager = null;
 	
	static {
		
		NativeDBConnectionManager = new CNativeDBConnectionsManager(); //( null, null );
		
	}
	
	public static CNativeDBConnectionsManager getNativeDBConnectionManager() {
		
		return NativeDBConnectionManager;
		
	}

    protected HashMap<String,CAbstractDBConnection> NativeDBConnectionFromTransactionID;  //TransactionID => CAbstractDBConnection
    
	/*protected HashMap<String,Connection> NativeDBConnectionFromTransactionID;  //TransactionID => DBConnection
	protected HashMap<String,ArrayList<String>> TransactionsIDFromSecurityTokenID;  //SecurityToken => array( TransactionID, TransactionID, ... )
	protected HashMap<String,Semaphore> NativeDBConnectionSemaphoreFromTransactionID; //TransactionID => DBSemaphore
	protected HashMap<String,CConfigNativeDBConnection> ConfigNativeDBConnectionFromSecurityTokenID; // SecurityTokenID => ConfigDBConnection
		
	protected HashMap<String,String> SecurityTokenIDFromTransactionID; //TransactionID => SecurityTokenID*/
	
	public CNativeDBConnectionsManager() {

		NativeDBConnectionFromTransactionID = new HashMap<String,CAbstractDBConnection>();
		/*NativeDBConnectionSemaphoreFromTransactionID = new HashMap<String,Semaphore>();
		TransactionsIDFromSecurityTokenID = new HashMap<String,ArrayList<String>>();
		SecurityTokenIDFromTransactionID = new HashMap<String,String>();
		ConfigNativeDBConnectionFromSecurityTokenID = new HashMap<String,CConfigNativeDBConnection>();*/
		
	}
	
	public synchronized CAbstractDBConnection getDBConnection( String strTransactionID, CExtendedLogger Logger, CLanguage Lang ) {

		//Connection DBConnection = NativeDBConnectionFromTransactionID.get( strTransactionID );

		CAbstractDBConnection DBConnection = null;
		
		for ( Entry<String,CAbstractDBConnection> ConnectionItem: NativeDBConnectionFromTransactionID.entrySet() ) {
			
			if ( ConnectionItem.getValue().getTransactionID().equals( strTransactionID ) ) {
			
				DBConnection = ConnectionItem.getValue();
				break;
			
			}	
			
		}
		
		if ( DBConnection == null && Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "DBConnection", "TransactionID", strTransactionID ) );
			
		}

		return DBConnection;

	}

	public synchronized Semaphore getNativeDBConnectionSemaphore( String strTransactionID, CExtendedLogger Logger, CLanguage Lang ) {

		//Semaphore DBConnectionSemaphore = NativeDBConnectionSemaphoreFromTransactionID.get( strTransactionID );

		Semaphore DBConnectionSemaphore = null;
		
		for ( Entry<String,CAbstractDBConnection> ConnectionItem: NativeDBConnectionFromTransactionID.entrySet() ) {
			
			if ( ConnectionItem.getValue().getTransactionID().equals( strTransactionID ) ) {

				DBConnectionSemaphore = (Semaphore) ConnectionItem.getValue().getDBConnectionSemaphore();
				break;

			}	
			
		}
		
		if ( DBConnectionSemaphore == null && Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "DBConnectionSemaphore", "TransactionID", strTransactionID ) );
			
		}
		
		return DBConnectionSemaphore;

	}
	
	public synchronized CConfigNativeDBConnection getConfigNativeDBConnection( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {
	
		//CConfigNativeDBConnection ConfigDBConnection = ConfigNativeDBConnectionFromSecurityTokenID.get( strSecurityTokenID );  
				
		CConfigNativeDBConnection ConfigDBConnection = null;
		
		for ( Entry<String,CAbstractDBConnection> ConnectionItem: NativeDBConnectionFromTransactionID.entrySet() ) {
			
			if ( ConnectionItem.getValue().getSecurityTokenID().equals( strSecurityTokenID ) ) {
			
				ConfigDBConnection =  (CConfigNativeDBConnection) ConnectionItem.getValue().getExtraObjectInfo( "ConfigDBConnection" );
			    break;
				
			}	
			
		}
		
		if ( ConfigDBConnection == null && Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "ConfigDBConnection", "strSecurityTokenID", strSecurityTokenID ) );
			
		}

		return ConfigDBConnection;
		
	} 

	public synchronized String getSecurityTokenID( String strTransactionID, CExtendedLogger Logger, CLanguage Lang ) {

		//String strSecurityTokenID = SecurityTokenIDFromTransactionID.get( strTransactionID );

		String strSecurityTokenID = null;
		
		for ( Entry<String,CAbstractDBConnection> ConnectionItem: NativeDBConnectionFromTransactionID.entrySet() ) {
			
			if ( ConnectionItem.getValue().getTransactionID().equals( strTransactionID ) ) {
			
				strSecurityTokenID =  ConnectionItem.getValue().getSecurityTokenID();
				break;
				
			}	
			
		}

		if ( strSecurityTokenID == null && Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "SecurityTokenID", "TransactionID", strTransactionID ) );
			
		}

		return strSecurityTokenID;

	}

	public synchronized ArrayList<String> getTransactionsIDFromSecurityTokenID( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {

		//ArrayList<String> TransactionsID = TransactionsIDFromSecurityTokenID.get( strSecurityTokenID );
		ArrayList<String> TransactionsID = null; //TransactionsIDFromSecurityTokenID.get( strSecurityTokenID );
		
		for ( Entry<String,CAbstractDBConnection> ConnectionItem: NativeDBConnectionFromTransactionID.entrySet() ) {
			
			if ( ConnectionItem.getValue().getSecurityTokenID().equals( strSecurityTokenID ) ) {
			
				if ( TransactionsID == null ) {
					
					TransactionsID = new ArrayList<String>();
					
				}
 				
				TransactionsID.add( ConnectionItem.getValue().getTransactionID() );
				
			}	
			
		}
		
		
		if ( TransactionsID == null && Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "[%s] from [%s] = [%s] not found", "TransactionsID", "SecurityTokenID", strSecurityTokenID ) );
			
		}
		
		return TransactionsID;

	}

	public synchronized boolean addNativeDBConnection( String strSecurityTokenID, String strTransactionID, CConfigNativeDBConnection ConfigDBConnectionToAdd, CAbstractDBConnection DBConnectionToAdd, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		if ( NativeDBConnectionFromTransactionID.get( strTransactionID ) == null ) {

			NativeDBConnectionFromTransactionID.put( strTransactionID, DBConnectionToAdd );

			Semaphore DBConnectionSemaphore = new Semaphore( 1 );

			/*NativeDBConnectionSemaphoreFromTransactionID.put( strTransactionID, DBConnectionSemaphore );

			SecurityTokenIDFromTransactionID.put( strTransactionID, strSecurityTokenID );
			
			ConfigNativeDBConnectionFromSecurityTokenID.put( strSecurityTokenID, ConfigDBConnectionToAdd );*/
			
			DBConnectionToAdd.setSecurityTokenID( strSecurityTokenID );
			DBConnectionToAdd.setTransactionID( strTransactionID );
			DBConnectionToAdd.setDBSemaphore( DBConnectionSemaphore );
			DBConnectionToAdd.setExtraObjectInfo( "ConfigDBConnection", ConfigDBConnectionToAdd );

			bResult = true;

		}

		/*ArrayList<String> strlstTransactionsId = TransactionsIDFromSecurityTokenID.get( strSecurityTokenID );

		if ( strlstTransactionsId == null ) {

			strlstTransactionsId = new ArrayList<String>();

		}

		strlstTransactionsId.add( strTransactionID );

		TransactionsIDFromSecurityTokenID.put( strSecurityTokenID, strlstTransactionsId );*/
		
		if ( Logger != null && Lang != null ) {
			
			Logger.logMessage( "1" , Lang.translate( "Added [%s] [%s] = [%s] and [%s] = [%s]", "DBConnection", "TransactionID", strTransactionID, "SecurityToken", strSecurityTokenID ) );
			
		}

		return bResult;

	}

	public synchronized boolean removeNativeDBConnectionByTransactionId( String strTransactionID, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		CAbstractDBConnection DBConnection = null;
		
		for ( Entry<String,CAbstractDBConnection> ConnectionItem: NativeDBConnectionFromTransactionID.entrySet() ) {
			
			if ( ConnectionItem.getValue().getTransactionID().equals( strTransactionID ) ) {

				DBConnection = ConnectionItem.getValue();
				break;
				
			}	
			
		}
		
		if ( DBConnection != null ) {
			
			boolean bUnlockSemaphore = DBConnection.lockConnection( true, Logger, Lang );
			
			try {
			
				NativeDBConnectionFromTransactionID.remove( DBConnection.getTransactionID() );
			
			}
			catch ( Exception Ex ) {
				
				if ( Logger != null ) {
					
					Logger.logException( "-1020" , Ex.getMessage(), Ex );
					
				}
				
			}
			
			if ( bUnlockSemaphore )
				DBConnection.unlockConnection( Logger, Lang );
			
			bResult = true;

			if ( Logger != null && Lang != null ) {
				
				Logger.logWarning( "-1" , Lang.translate( "Removed [%s] by [%s] = [%s]", "DBConnection", "TransactionID", strTransactionID ) );
				
			}
			
		}
		
		/*String strSecurityTokenID = SecurityTokenIDFromTransactionID.get( strTransactionID );
		
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

		if ( NativeDBConnectionFromTransactionID.get( strTransactionID ) != null ) {

			Semaphore DBConnectionSemaphore = NativeDBConnectionSemaphoreFromTransactionID.get( strTransactionID );

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


				NativeDBConnectionFromTransactionID.remove( strTransactionID );
				NativeDBConnectionSemaphoreFromTransactionID.remove( strTransactionID );

				if ( bUnlockSemaphore == true ) {

					DBConnectionSemaphore.release();

				}

			}

			bResult = true;

			if ( Logger != null && Lang != null ) {
				
				Logger.LogWarning( "-1" , Lang.Translate( "Removed [%s] by [%s] = [%s]", "DBConnection", "TransactionID", strTransactionID ) );
				
			}
			
		}*/

		return bResult;

	}

	public synchronized boolean removeNativeDBConnectionsBySecurityToken( String strSecurityTokenID, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;

		CAbstractDBConnection DBConnection = null;
		
		for ( Entry<String,CAbstractDBConnection> ConnectionItem: NativeDBConnectionFromTransactionID.entrySet() ) {
			
			DBConnection = ConnectionItem.getValue();
			
			if ( DBConnection.getSecurityTokenID().equals( strSecurityTokenID ) ) {

				if ( DBConnection != null ) {
					
					boolean bUnlockSemaphore = DBConnection.lockConnection( true, Logger, Lang );
					
					try {
					
						NativeDBConnectionFromTransactionID.remove( DBConnection.getTransactionID() );
					
					}
					catch ( Exception Ex ) {
						
						if ( Logger != null ) {
							
							Logger.logException( "-1020" , Ex.getMessage(), Ex );
							
						}
						
					}
					
					if ( bUnlockSemaphore )
						DBConnection.unlockConnection( Logger, Lang );
					
					bResult = true;

					if ( Logger != null && Lang != null ) {
						
						Logger.logWarning( "-1" , Lang.translate( "Removed [%s] by [%s] = [%s]", "DBConnection", "TransactionID", DBConnection.getTransactionID() ) );
						
					}
					
				}
				
				break;
				
			}	
			
		}
		
		/*ArrayList<String> strlstTrasactionsId = TransactionsIDFromSecurityTokenID.get( strSecurityToken );

		if ( strlstTrasactionsId != null ) {

			for ( int I=0; I < strlstTrasactionsId.size(); I++ ) {

				bResult = removeNativeDBConnectionByTransactionId( strlstTrasactionsId.get( I ), Logger, Lang );

			}

		}
		else {

			bResult = true;

		}

		TransactionsIDFromSecurityTokenID.remove( strSecurityToken );*/

		if ( Logger != null && Lang != null ) {
			
			Logger.logWarning( "-1" , Lang.translate( "Removed [%s] by [%s] = [%s]", "DBConnection", "SecurityTokenID", strSecurityTokenID ) );
			
		}

		return bResult;

	}

}
