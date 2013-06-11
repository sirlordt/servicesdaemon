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

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import ExtendedLogger.CExtendedLogger;

public class CClassPathLoader {

    protected static final String METHOD_ADD_URL = "addURL";
    @SuppressWarnings("rawtypes")
    protected static final Class[] METHOD_PARAMS = new Class[]{ URL.class };
    protected final URLClassLoader loader;
    protected final Method methodAdd;
    
    protected int intCountClassLoaded = 0;
    
	protected CExtendedLogger Logger = null;
    protected CLanguage Lang = null;
    
    public CClassPathLoader( CExtendedLogger Logger, CLanguage Lang ) throws Exception {
		
		this.Logger = Logger;
		this.Lang = Lang;
    	
    	loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		methodAdd = URLClassLoader.class.getDeclaredMethod( METHOD_ADD_URL, METHOD_PARAMS );
		methodAdd.setAccessible( true );

	}
    
    public void setLogger( CExtendedLogger Logger ) {
    	
    	this.Logger = Logger;
    	
    }
    
    public CExtendedLogger getLogger() {
    	
    	return this.Logger;
    	
    }

    public void setLang( CLanguage Lang ) {
    	
    	this.Lang = Lang;
    	
    }
    
    public CLanguage getLang() {
    	
    	return this.Lang;
    	
    }
    
    public int getCountClassLoaded() {
    	
    	return intCountClassLoaded;
    	
    }
    
    public URL[] getURLs() {
        
    	return loader.getURLs();
    
    }

    public boolean addURL(URL url) {
    
    	boolean bResult = false;
    	
    	intCountClassLoaded = 0;
    	
    	if ( url != null ) {
           
    	   try {
             
    		   methodAdd.invoke( loader, new Object[] { url } );
           
    		   intCountClassLoaded += 1;
    		   
    		   bResult = true;
    		   
    	   } 
    	   catch ( Exception Ex ) {
    		    
    		   CExtendedLogger GlobalLogger = CExtendedLogger.getLogger( DefaultConstantsServicesDaemon.strDefaultLoggerName );

    		   GlobalLogger.LogException( "-1010", Ex.getMessage(), Ex ); 
           
    	   }
        
    	}
    	
    	return bResult;
    	
    }

    public void addURLs( URL[] urls ) {

    	if ( urls != null ) {
        
    	   for ( URL url : urls ) {
           
    		   addURL( url );
           
    	   }
        
    	}
    	
    }

    public void addFile( File FileToAdd ) throws MalformedURLException {
    
    	if ( FileToAdd != null ) {
        
    	   addURL( FileToAdd.toURI().toURL() );
        
    	}
    	
    }

    public void addFile(String strFileName ) throws MalformedURLException {
    
    	System.out.println( strFileName );
    	addFile( new File( strFileName ) );
    
    }
    
    public static File[] FindFilesToLoad( String strPath, String strFileExtenstion ) {
       
    	final String strFileExt = strFileExtenstion;
    	
        Vector<File> vUrls = new Vector<File>();

        File jarsDirectory = new File( strPath );
 
        if ( jarsDirectory.exists() && jarsDirectory.isDirectory() ) {
            
            File[] jars = jarsDirectory.listFiles( new FilenameFilter() {

                @Override
                public boolean accept( File dir, String name ) {
                
                	return name.endsWith( strFileExt );
                
                }
            
            } );

            for ( File jar : jars ) {
                
            	vUrls.add(jar);
            
            }
        
        }

        return vUrls.toArray( new File[0] );

    }
    
    public static File[] RecursiveFindFilesToLoad( String strPath, String strFileExtenstion, int intMaxDepth, int intActuakDepth ) {
    	
        Vector<File> vUrls = new Vector<File>();

        File Directory = new File( strPath );
 
        if ( Directory.exists() && Directory.isDirectory() ) {
            
            File[] ListFoundFile = Directory.listFiles(); 
            
            for ( File FileFound : ListFoundFile ) {
                
            	if ( FileFound.isDirectory() == true && FileFound.canRead() == true ) { 
            	
            		if ( intActuakDepth + 1 <= intMaxDepth ) {  
            			
            			File[] DeepListFoundFile = RecursiveFindFilesToLoad( FileFound.getAbsolutePath(), strFileExtenstion, intMaxDepth, intActuakDepth + 1 );
            		
                        for ( File DeepFileFound : DeepListFoundFile ) {
  
                    		vUrls.add(  DeepFileFound );
                        }
            		
            		}	
            	
            	}	
            	else if ( FileFound.isFile() == true && FileFound.getAbsolutePath().endsWith( strFileExtenstion ) == true ) {
            	
            		vUrls.add(  FileFound );

            	}	
            
            }
        
        }

        return vUrls.toArray( new File[0] );
        
    }
    
    public boolean LoadClassFiles( String strPath, String strFileExtenstion, int intMaxDepth ) {

    	boolean bResult = false;
        
        try {

			if ( Logger != null && Lang != null )   
        	    Logger.LogMessage( "1", Lang.Translate( "Loading classes from path: [%s]", strPath ) ); 

			File[] jars = RecursiveFindFilesToLoad( strPath, strFileExtenstion, intMaxDepth,  1 );
			
			if ( Logger != null && Lang != null )
				Logger.LogMessage( "1", Lang.Translate( "Jar count files found: [%s]", Integer.toString( jars.length ) ) ); 

            if ( jars.length > 0 ) {
                
            	for ( File jar : jars ) {

                	try {
                        
                		this.addFile( jar );

                		if ( Logger != null && Lang != null )
            				Logger.LogMessage( "1", Lang.Translate( "Added jar file from the path: [%s]", jar.getAbsolutePath() ) ); 
                    
                	} 
                	catch ( Exception Ex ) {
                       
                		if ( Logger != null && Lang != null ) 
                		    Logger.LogException( "-1010", Ex.getMessage(), Ex ); 

                	}
            
                }

                bResult = true;          
            
            };
            

    	} 
    	catch ( Exception Ex ) {

    		if ( Logger != null && Lang != null ) 
    		    Logger.LogException( "-1011", Ex.getMessage(), Ex ); 

        }
        
    	return bResult;
    	
    }
    

}
