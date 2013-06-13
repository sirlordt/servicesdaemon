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
package SystemStartSession;

import java.util.ArrayList;

import AbstractService.CInputServiceParameter;

public class CSystemStartSessionDBConnection {

	String strName;

	ArrayList<CInputServiceParameter> InputParameters;
	
	String strSQLType;
	String strSQL;
	String strSessionKey;
	String strType;
	String strFieldName;
	String strFieldType;
	String strFieldValueSuccess;
	String strFieldValueFailed;
	String strFieldValueDisabled;
	String strFieldValueNotFound;
	
	ArrayList<String> AfterCheckSQLSuccess;
	ArrayList<String> AfterCheckSQLFailed;
	ArrayList<String> AfterCheckSQLDisabled;
	ArrayList<String> AfterCheckSQLNotFound;
	ArrayList<String> AfterCheckSQLAny;
	
	ArrayList<String> AddFieldToResponseSuccess;
	ArrayList<String> AddFieldToResponseFailed;
	ArrayList<String> AddFieldToResponseDisabled;
	ArrayList<String> AddFieldToResponseNotFound;
	ArrayList<String> AddFieldToResponseAny;
	
	public CSystemStartSessionDBConnection() {

		strName = "";

		InputParameters = new ArrayList<CInputServiceParameter>();
		
		strSQL = "";
		strSessionKey = "";
		strType = "";
		strFieldName = "";
		strFieldValueSuccess = "";
		strFieldValueFailed = "";
		strFieldValueDisabled = "";
		strFieldValueNotFound = "";
		
		AfterCheckSQLSuccess = new ArrayList<String>();
		AfterCheckSQLFailed = new ArrayList<String>();
		AfterCheckSQLDisabled = new ArrayList<String>();
		AfterCheckSQLNotFound = new ArrayList<String>();;
		AfterCheckSQLAny = new ArrayList<String>();;
		
		AddFieldToResponseSuccess = new ArrayList<String>();
		AddFieldToResponseFailed = new ArrayList<String>();
		AddFieldToResponseDisabled = new ArrayList<String>();
		AddFieldToResponseNotFound = new ArrayList<String>();;
		AddFieldToResponseAny = new ArrayList<String>();;
		
	}
	
	public CInputServiceParameter FindInputParameterByName( String strName ) {
		
		CInputServiceParameter Result = null;
		
		strName = strName.toLowerCase();
		
		for ( CInputServiceParameter InputParameter: InputParameters ) {
			
			if ( InputParameter.getParameterName().toLowerCase().equals( strName ) ) {
				
                Result = InputParameter;
				
				break;
				
			}
			
		}
		
		return Result;
		
	}

	public boolean RemoveInputParameterByName( String strName ) {
		
		boolean bResult = false;
		
		CInputServiceParameter InputParameter = this.FindInputParameterByName( strName );
		
		if ( InputParameter == null ) {
			
			InputParameters.remove( InputParameter );
			
			InputParameter = null;
			
			bResult = true;
			
		}
		
		return bResult;
		
	}
	
}
