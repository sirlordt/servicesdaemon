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

public class CExpresionFilter {

	public static final String _Exact = "exact";
	public static final String _Partial = "partial";
	public static final String _RExp = "rexp";
	
	public String strType = ""; //exact, partial, rexp 
	public String strExpression = "";
	
	public CExpresionFilter() {
	}

}
