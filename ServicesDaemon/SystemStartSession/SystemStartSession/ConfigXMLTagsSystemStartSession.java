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

public interface ConfigXMLTagsSystemStartSession {

	public static final String _DBConnections = "DBConnections";
	public static final String _DBConnection = "DBConnection";
	public static final String _Name = "Name";

	public static final String _InputParams = "InputParams"; 
	public static final String _Param = "Param"; 
	public static final String _Required = "Required";
	public static final String _DataType = "DataType";
	public static final String _Scope = "Scope";
	public static final String _Scope_in = "in";
	public static final String _Scope_out = "out";
	public static final String _Scope_inout = "inout";
	public static final String _Length = "Length";
	public static final String _Description = "Description";
	public static final String _CheckMethod = "CheckMethod";
	public static final String _SQLType = "SQLType";
	public static final String _SQLType_sql = "sql";
	public static final String _SQLType_stored_procedure = "stored_procedure";
	public static final String _SQL = "SQL";
	public static final String _Type = "Type";
	public static final String _Type_check_field_value = "check_field_value";
	public static final String _Type_if_exists = "if_exists";
	public static final String _SessionKey = "SessionKey";
	public static final String _Field_Name = "Field_Name";
	public static final String _Field_Type = "Field_Type";
	public static final String _Field_Value_Success = "Field_Value_Success";
	public static final String _Field_Value_Failed = "Field_Value_Failed";
	public static final String _Field_Value_Disabled = "Field_Value_Disabled";
	public static final String _Field_Value_NotFound = "Field_Value_NotFound";
	
	public static final String _AddFieldsToResponseSuccess = "AddFieldsToResponseSuccess";
	public static final String _AddFieldsToResponseFailed = "AddFieldsToResponseFailed";
	public static final String _AddFieldsToResponseDisabled = "AddFieldsToResponseDisabled";
	public static final String _AddFieldsToResponseNotFound = "AddFieldsToResponseNotFound";
	public static final String _AddFieldsToResponseAny = "AddFieldsToResponseAny";
	public static final String _AddField = "AddField";	
	
	public static final int _Section_Success = 1;
	public static final int _Section_Failed = 2;
	public static final int _Section_Disabled = 3;
	public static final int _Section_NotFound = 4;
	public static final int _Section_Any = 5;
	
	
}
