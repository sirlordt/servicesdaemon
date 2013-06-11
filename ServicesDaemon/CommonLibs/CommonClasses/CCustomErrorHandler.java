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
