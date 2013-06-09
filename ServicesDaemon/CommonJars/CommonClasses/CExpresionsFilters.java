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

						bResult = strExpression.toLowerCase().toLowerCase().equals( ExpFilter.strExpression );

					}
					else if ( ExpFilter.strType.equals( CExpresionFilter._Partial ) ) {

						bResult = strExpression.toLowerCase().contains( ExpFilter.strExpression );

					}
					else if ( ExpFilter.strType.equals( CExpresionFilter._RExp ) ) {

						bResult = strExpression.matches( ExpFilter.strExpression );

					}
				
				}

				if ( bResult ) break;

			}
		
		}
		catch ( Exception Ex ) {
			
			if ( Logger != null )
				Logger.LogException( "-1020", Ex.getMessage(), Ex ); 
			
		}
		
		return bResult;
		
	} 
	
}
