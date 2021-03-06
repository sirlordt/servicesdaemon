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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ExtendedLogger.CExtendedLogger;

public class CExpresionsFilters {

	public String strName = "";
	public String strType = "";  //block allow

	public ArrayList<CExpresionFilter> Filters = null;
	
	public CExpresionsFilters() {

		Filters = new ArrayList<CExpresionFilter>();
	
	}
	
	public static CExpresionsFilters getExpresionsFiltersByName( ArrayList<CExpresionsFilters> ExpresionsFiltersList, String strName ) {
		
		CExpresionsFilters ExpressionsFilters = null;
	
		if ( ExpresionsFiltersList != null ) {

			for ( CExpresionsFilters ExpFilters: ExpresionsFiltersList ) {

				if ( ExpFilters.strName.equals( "*" ) ) {

					ExpressionsFilters = ExpFilters;
					break;
					
				}
				else {

					Pattern pattern = Pattern.compile( ExpFilters.strName );
					Matcher matcher = pattern.matcher( strName );

					if ( matcher.find() ) {

						ExpressionsFilters = ExpFilters;
						break;

					}
				
				}
				
			}
			
		}
		
		return ExpressionsFilters;
		
	}

	public boolean checkExpressionInFilters( String strExpression, CExtendedLogger Logger ) {
		
		boolean bResult = false;
		
		try {
		
			for ( CExpresionFilter ExpFilter: Filters ) {

				if ( ExpFilter.strExpression.equals( "*" ) ) {
					
					bResult = true;
					
				}
				else {
				
					if ( ExpFilter.strType.equals( CExpresionFilter._Exact ) ) {

						bResult = strExpression.toLowerCase().toLowerCase().equals( ExpFilter.strExpression.toLowerCase() );

					}
					else if ( ExpFilter.strType.equals( CExpresionFilter._Partial ) ) {

						bResult = strExpression.toLowerCase().contains( ExpFilter.strExpression.toLowerCase() );

					}
					else if ( ExpFilter.strType.equals( CExpresionFilter._RExp ) ) {

						bResult = strExpression.matches( ExpFilter.strExpression.toLowerCase() );

					}
				
				}

				if ( bResult ) break;

			}
		
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.logException( "-1020", Ex.getMessage(), Ex ); 
			
		}
		
		return bResult;
		
	} 
	
}
