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
