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
