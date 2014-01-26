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

	protected String strName;

	protected ArrayList<CInputServiceParameter> InputParameters;
	
	protected String strSQLType;
	protected String strSQL;
	protected String strSessionKey;
	protected String strType;
	protected String strFieldName;
	protected String strFieldType;
	protected String strFieldValueSuccess;
	protected String strFieldValueFailed;
	protected String strFieldValueDisabled;
	protected String strFieldValueNotFound;
	
	protected ArrayList<String> AfterCheckSQLSuccess;
	protected ArrayList<String> AfterCheckSQLFailed;
	protected ArrayList<String> AfterCheckSQLDisabled;
	protected ArrayList<String> AfterCheckSQLNotFound;
	protected ArrayList<String> AfterCheckSQLAny;
	
	protected ArrayList<String> AddFieldToResponseSuccess;
	protected ArrayList<String> AddFieldToResponseFailed;
	protected ArrayList<String> AddFieldToResponseDisabled;
	protected ArrayList<String> AddFieldToResponseNotFound;
	protected ArrayList<String> AddFieldToResponseAny;
	
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
		
		if ( InputParameter != null ) {
			
			InputParameters.remove( InputParameter );
			
			InputParameter = null;
			
			bResult = true;
			
		}
		
		return bResult;
		
	}
	
}
