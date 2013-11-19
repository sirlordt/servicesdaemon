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


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CommonClasses.CServicesDaemonConfig;
import CommonClasses.ConfigXMLTagsServicesDaemon;

public class CAbstractServicesManager extends HttpServlet {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 4428843572369800671L;

	protected String strContextPath;
    protected CServicesDaemonConfig ServicesDaemonConfig;
    
    public CAbstractServicesManager() { 

    	this.strContextPath = "/*";
        
    }
    
    public boolean InitManager( CServicesDaemonConfig ServicesDaemonConfig ) {
    	
    	this.ServicesDaemonConfig = ServicesDaemonConfig;
    	
    	return true;
    	
    }
    
    public void setContextPath( String strContextPath ) {
    	
    	this.strContextPath = strContextPath;
    	
    }
    
    public String getContextPath() {
    	
    	return this.strContextPath;
    	
    }
    
    protected void ProcessRequest( HttpServletRequest request, HttpServletResponse response ) {
    	
 	   try {
 	    	
		   response.setContentType("text/html");
		   response.setStatus(HttpServletResponse.SC_OK);
		   response.getWriter().println("<h1>" + ServicesDaemonConfig.Lang.Translate( "Default services manager" ) + "</h1>" );
	       response.getWriter().println("<h2>" + ServicesDaemonConfig.Lang.Translate( "Session" ) + "=" + request.getSession(true).getId() + "<h2>");
	       response.getWriter().println("<h3>IP=" + request.getRemoteAddr() + "<h3>" );
	   
	   }
	   catch ( Exception Ex ) {
  
		   ServicesDaemonConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	   }
    	
    }
    
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) {
    	
    	if ( ServicesDaemonConfig.strResponseRequestMethod.equals( ConfigXMLTagsServicesDaemon._Request_Method_ANY ) || ServicesDaemonConfig.strResponseRequestMethod.equals( ConfigXMLTagsServicesDaemon._Request_Method_OnlyGET ) )
    		this.ProcessRequest( request, response );
    	else
   	        response.setStatus( HttpServletResponse.SC_OK );
    		
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response ) {
	
    	if ( ServicesDaemonConfig.strResponseRequestMethod.equals( ConfigXMLTagsServicesDaemon._Request_Method_ANY ) || ServicesDaemonConfig.strResponseRequestMethod.equals( ConfigXMLTagsServicesDaemon._Request_Method_OnlyPOST ) )
    		this.ProcessRequest( request, response );
    	else
   	        response.setStatus( HttpServletResponse.SC_OK );
    	
	}
    
}
