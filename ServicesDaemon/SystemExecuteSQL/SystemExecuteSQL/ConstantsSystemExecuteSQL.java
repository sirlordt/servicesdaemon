package SystemExecuteSQL;

import java.sql.Types;

public interface ConstantsSystemExecuteSQL {

	public static final String _RequestSQL           = "SQL";
	public static final String _RequestSQLType       = "VarChar";
	public static final String _RequestSQLLength     = "10240";

	public static final String _ResponseAffectedRows     = "AffectedRows";
	public static final int _ResponseAffectedRowsTypeID  = Types.BIGINT;
	public static final String _ResponseAffectedRowsType = "BigInt";
	
	
}
