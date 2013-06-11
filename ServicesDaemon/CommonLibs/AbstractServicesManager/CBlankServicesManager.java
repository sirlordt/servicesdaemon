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
package AbstractServicesManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CBlankServicesManager extends CAbstractServicesManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8018726094628460202L;

	public CBlankServicesManager() {

		super();
		
	}
	
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) {
    	
	   try {
    	
		   response.setContentType("text/html");
		   response.setStatus(HttpServletResponse.SC_OK);
	   
	   }
	   catch ( Exception Ex ) {
  
		   ServicesDaemonConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	   }
        
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response ) {
	
    	doGet( request, response );
    	
	}

}
