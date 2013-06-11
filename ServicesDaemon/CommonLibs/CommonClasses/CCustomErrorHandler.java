package CommonClasses;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.handler.ErrorHandler;

public class CCustomErrorHandler extends ErrorHandler {

	public CCustomErrorHandler() {

		super();
	
	}

	@Override
	protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
	
		String uri= request.getRequestURI();
	    
		writeErrorPageMessage(request,writer,code,message,uri);
	    
		if (showStacks)
			writeErrorPageStacks(request,writer);
	    
	}
	
	@Override
	protected void  writeErrorPageMessage(HttpServletRequest request, Writer writer, int code, String message,String uri) throws IOException {
	        
		writer.write( "ERROR " + Integer.toString(code) );
	        
		writer.write("<br/>Path " );
	       
		write(writer,uri);
	        
		writer.write("<br/>" );
	        
		write(writer,message);
	    
	}	
	
}
