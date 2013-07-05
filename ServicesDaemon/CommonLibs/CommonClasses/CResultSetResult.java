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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CResultSetResult {

	
	public ResultSet Result = null;
	public CNamedPreparedStatement NamedPreparedStatement = null; 
	public CNamedCallableStatement NamedCallableStatement = null; 
	public Statement SQLStatement = null;
	public long lngAffectedRows = -1;
	public int intCode = -1;
	public String strDescription = "";
	
	public CResultSetResult() {
		
		
	}

	public CResultSetResult( int intAffectedRows, int intCode, String strDescription ) {
		
		this.lngAffectedRows = intAffectedRows;
		this.intCode = intCode;
		this.strDescription = strDescription;
		
	}
	
	public CResultSetResult( int intAffectedRows, int intCode, String strDescription, CNamedPreparedStatement NamedPreparedStatement, ResultSet Result ) {
		
		this.lngAffectedRows = intAffectedRows;
		this.intCode = intCode;
		this.strDescription = strDescription;
		this.NamedPreparedStatement = NamedPreparedStatement;
		this.Result = Result;
		
	}

	public CResultSetResult( int intAffectedRows, int intCode, String strDescription, CNamedCallableStatement NamedCallableStatement, ResultSet Result ) {
		
		this.lngAffectedRows = intAffectedRows;
		this.intCode = intCode;
		this.strDescription = strDescription;
		this.NamedCallableStatement = NamedCallableStatement;
		this.Result = Result;
		
	}
	
	public CResultSetResult( int intAffectedRows, int intCode, String strDescription, Statement SQLStatement, ResultSet Result ) {
		
		this.lngAffectedRows = intAffectedRows;
		this.intCode = intCode;
		this.strDescription = strDescription;
		this.SQLStatement = SQLStatement;
		this.Result = Result;
		
	}
	
	public static ResultSet getFirstResultSetNotNull( ArrayList<CResultSetResult> ResultSetStructList ){
		
		for ( CResultSetResult ResultSetStruct: ResultSetStructList ) {
		
			if ( ResultSetStruct.Result != null ) {
				
				return ResultSetStruct.Result;
				
			}
			
		}
		
		return null;
		
	}  

}
