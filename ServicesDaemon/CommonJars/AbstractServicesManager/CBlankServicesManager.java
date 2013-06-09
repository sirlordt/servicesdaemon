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
