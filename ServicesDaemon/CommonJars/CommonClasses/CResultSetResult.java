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
