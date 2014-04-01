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
package FirebirdDBEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import AbstractDBEngine.IAbstractDBConnection;
import AbstractDBEngine.CAbstractDBEngine;
import AbstractDBEngine.CDBEngineConfigNativeDBConnection;
import AbstractDBEngine.CJDBConnection;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CFirebirdDBEngine extends CAbstractDBEngine {

	public CFirebirdDBEngine() {
		
		strName = "firebird";
		strVersion = "2.5";
		
	}
	
	@Override
	public synchronized IAbstractDBConnection getDBConnection( CDBEngineConfigNativeDBConnection ConfigDBConnection, CExtendedLogger Logger, CLanguage Lang ) {

		IAbstractDBConnection DBConnection = null;
		
		try {
			
            String strDatabaseURL = "jdbc:firebirdsql:" + ConfigDBConnection.strIP + "/" + ConfigDBConnection.intPort + ":" + ConfigDBConnection.strDatabase;

            if ( Logger != null ) {
            	
        		Logger.logMessage( "1", Lang.translate( "Trying to connect with the next URL: [%s] and user: [%s]", strDatabaseURL, ConfigDBConnection.strUser ) );        
            	
            }

            Class.forName( ConfigDBConnection.strDriver );

            Connection JDBConnection = DriverManager.getConnection( strDatabaseURL, ConfigDBConnection.strUser, ConfigDBConnection.strPassword ); 
			
            DBConnection = new CJDBConnection();
            DBConnection.setEngineNameAndVersion( this.strName, this.strVersion );
            DBConnection.setDBConnection( JDBConnection );
            DBConnection.setConfigDBConnection( ConfigDBConnection );
            
            if ( Logger != null ) {
            	
        		Logger.logMessage( "1", Lang.translate( "Database connection established to next URL: [%s] and user: [%s]", strDatabaseURL, ConfigDBConnection.strUser ) );        
            	
            }
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1015", Ex.getMessage(), Ex ); 
			
		}
		
		return DBConnection;
		
	}
	
	@Override
	public ResultSet executeDummyCommand( IAbstractDBConnection DBConnection, String strOptionalDummySQL, CExtendedLogger Logger, CLanguage Lang ) {
		
		ResultSet Result = null;
		
		try {
		
			if ( strOptionalDummySQL != null && strOptionalDummySQL.isEmpty() == true ) {
				
				strOptionalDummySQL = "SELECT DISTINCT RDB$RELATION_NAME FROM RDB$RELATION_FIELDS";
				
			}
			
			Statement SQLStatement = ((Connection) DBConnection.getDBConnection()).createStatement();			
			
			Result = SQLStatement.executeQuery( strOptionalDummySQL );
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1015", Ex.getMessage(), Ex ); 

		}
	
		return Result;
		
	}
    
}
