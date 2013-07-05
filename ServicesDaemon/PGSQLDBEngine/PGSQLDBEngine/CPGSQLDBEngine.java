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
package PGSQLDBEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractDBEngine.CDBEngineConfigConnection;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CPGSQLDBEngine extends CAbstractDBEngine {

	public CPGSQLDBEngine() {
		
		strName = "pgsql";
		strVersion = "9.1";
		
	}
	
	@Override
	public Connection getDBConnection( CDBEngineConfigConnection ConfigDBConnection, CExtendedLogger Logger, CLanguage Lang ) {

		Connection DBConnection = null;
		
		try {
			
            String strDatabaseURL = "jdbc:postgresql://" + ConfigDBConnection.strIP + ":" + ConfigDBConnection.intPort + "/" + ConfigDBConnection.strDatabase;

            if ( Logger != null ) {
            	
        		Logger.LogMessage( "1", Lang.Translate( "Trying to connect with the next URL: [%s] and user: [%s]", strDatabaseURL, ConfigDBConnection.strUser ) );        
            	
            }

            Class.forName( ConfigDBConnection.strDriver );

            DBConnection = DriverManager.getConnection( strDatabaseURL, ConfigDBConnection.strUser, ConfigDBConnection.strPassword ); 
			
            if ( Logger != null ) {
            	
        		Logger.LogMessage( "1", Lang.Translate( "Database connection established to next URL: [%s] and user: [%s]", strDatabaseURL, ConfigDBConnection.strUser ) );        
            	
            }
			
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 
			
		}
		
		return DBConnection;
	
	}
	
	@Override
	public ResultSet ExecuteDummySQL( Connection DBConnection, String strOptionalDummyQuery, CExtendedLogger Logger, CLanguage Lang ) {
		
		ResultSet Result = null;
		
		try {
		
			if ( strOptionalDummyQuery.isEmpty() == true ) {
				
				strOptionalDummyQuery = "SHOW TABLES";
				
			}
			
			Statement SQLStatement = DBConnection.createStatement();			
			
			Result = SQLStatement.executeQuery( strOptionalDummyQuery );
			
		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.LogException( "-1015", Ex.getMessage(), Ex ); 

		}
	
		return Result;
		
	}

}
