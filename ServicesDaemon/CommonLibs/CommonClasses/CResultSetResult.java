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
import java.util.ArrayList;

public class CResultSetResult {

	public int intCode = -1;
	public ResultSet Result = null;
	public CNamedPreparedStatement NamedPreparedStatement = null; 
	public String strDescription;
	
	public CResultSetResult() {
		
		
	}

	public CResultSetResult( int intCode, String strDescription, CNamedPreparedStatement NamedPreparedStatement, ResultSet Result ) {
		
		this.intCode = intCode;
		this.strDescription = strDescription;
		this.NamedPreparedStatement = NamedPreparedStatement;
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
