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


import java.util.LinkedHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import CommonClasses.CConfigServicesDaemon;
import CommonClasses.ConstantsCommonConfigXMLTags;
import CommonClasses.IMessageObject;

public class CAbstractServicesManager extends HttpServlet implements IMessageObject {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 4428843572369800671L;

	protected String strContextPath;
    protected CConfigServicesDaemon ServicesDaemonConfig;
    
    protected int intInitPriority; //Control the order for post init from managers
    
    protected String strRunningPath;
    
    public CAbstractServicesManager() { 

    	this.strContextPath = "/*";
        
    	intInitPriority = 10000;
    	
    }
    
    public String getRunningPath() {
    	
    	return strRunningPath;
    	
    } 
    
    public boolean initManager( CConfigServicesDaemon ServicesDaemonConfig ) {
    	
    	this.ServicesDaemonConfig = ServicesDaemonConfig;
    	
    	return true;
    	
    }
    
    public boolean postInitManager( CConfigServicesDaemon ServicesDaemonConfig, LinkedHashMap<String,Object> InfoData ) {
    	
    	//Do nothing
    	return true;
    	
    }
    
    public boolean endManager( CConfigServicesDaemon ServicesDaemonConfig ) {
    	
    	//Do nothing
    	return true;
    	
    }
    
    public void setContextPath( String strContextPath ) {
    	
    	this.strContextPath = strContextPath;
    	
    }
    
    public String getContextPath() {
    	
    	return this.strContextPath;
    	
    }
    
    public int getInitPriority() {
    	
    	return intInitPriority;
    	
    }
    
    protected void processRequest( HttpServletRequest request, HttpServletResponse response ) {
    	
 	   try {
 	    	
		   response.setContentType("text/html");
		   response.setStatus(HttpServletResponse.SC_OK);
		   response.getWriter().println("<h1>" + ServicesDaemonConfig.Lang.translate( "Default services manager" ) + "</h1>" );
	       response.getWriter().println("<h2>" + ServicesDaemonConfig.Lang.translate( "Session" ) + "=" + request.getSession(true).getId() + "<h2>");
	       response.getWriter().println("<h3>IP=" + request.getRemoteAddr() + "<h3>" );
	   
	   }
	   catch ( Exception Ex ) {
  
		   ServicesDaemonConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );
		   
	   }
    	
    }
    
    @Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) {
    	
    	if ( ServicesDaemonConfig.strResponseRequestMethod.equals( ConstantsCommonConfigXMLTags._Request_Method_ANY ) || ServicesDaemonConfig.strResponseRequestMethod.equals( ConstantsCommonConfigXMLTags._Request_Method_OnlyGET ) )
    		this.processRequest( request, response );
    	else
   	        response.setStatus( HttpServletResponse.SC_OK );
    		
    }

    @Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) {
	
    	if ( ServicesDaemonConfig.strResponseRequestMethod.equals( ConstantsCommonConfigXMLTags._Request_Method_ANY ) || ServicesDaemonConfig.strResponseRequestMethod.equals( ConstantsCommonConfigXMLTags._Request_Method_OnlyPOST ) )
    		this.processRequest( request, response );
    	else
   	        response.setStatus( HttpServletResponse.SC_OK );
    	
	}

	
    @Override
	public Object sendMessage(String strMessageName, Object MessageData) {

    	return null;
    	
	}
    
}
