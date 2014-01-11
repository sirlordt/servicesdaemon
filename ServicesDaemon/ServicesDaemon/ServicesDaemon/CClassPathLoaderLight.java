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
package ServicesDaemon;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CClassPathLoaderLight {

    protected static final String METHOD_ADD_URL = "addURL";
    protected static final Class<?>[] METHOD_PARAMS = new Class<?>[]{ URL.class };
    protected final URLClassLoader loader;
    protected final Method methodAdd;
    
    protected final String _BackupExtension = ".backup";
    
    protected int intCountClassLoaded = 0;
    
    public CClassPathLoaderLight() throws Exception {
		
    	loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		methodAdd = URLClassLoader.class.getDeclaredMethod( METHOD_ADD_URL, METHOD_PARAMS );
		methodAdd.setAccessible( true );

	}
    
    public int getCountClassLoaded() {
    	
    	return intCountClassLoaded;
    	
    }
    
    protected URL[] getURLs() {
        
    	return loader.getURLs();
    
    }

    protected boolean addURL( URL url, Logger logger ) {
    
    	boolean bResult = false;
    	
    	intCountClassLoaded = 0;
    	
    	if ( url != null ) {
           
    	   try {
             
    		   methodAdd.invoke( loader, new Object[] { url } );
           
    		   intCountClassLoaded += 1;
    		   
    		   bResult = true;
    		   
    	   } 
    	   catch ( Exception Ex ) {
    		    
    		   if ( logger != null )
    			   logger.log( Level.SEVERE, Ex.getMessage(), Ex ); 
           
    	   }
        
    	}
    	
    	return bResult;
    	
    }

    protected void addURLs( URL[] urls, Logger logger ) {

    	if ( urls != null ) {
        
    	   for ( URL url : urls ) {
           
    		   addURL( url, logger );
           
    	   }
        
    	}
    	
    }

    protected void addFile( File FileToAdd, Logger logger ) throws MalformedURLException {
    
    	if ( FileToAdd != null ) {
        
    	   addURL( FileToAdd.toURI().toURL(), logger );
        
    	}
    	
    }

    protected void addFile( String strFileName, Logger logger ) throws MalformedURLException {
    
    	System.out.println( strFileName );
    	addFile( new File( strFileName ), logger );
    
    }
    
    protected File[] FindFilesToLoad( String strPath, String strFileExtension ) {
       
    	final String strFileExt = strFileExtension;
    	
        Vector<File> vUrls = new Vector<File>();

        File jarsDirectory = new File( strPath );
 
        if ( jarsDirectory.exists() && jarsDirectory.isDirectory() ) {
            
            File[] jars = jarsDirectory.listFiles( new FilenameFilter() {

                @Override
                public boolean accept( File dir, String name ) {
                
                	return name.endsWith( strFileExt );
                
                }
            
            } );

			Arrays.sort( jars );
            
            for ( File jar : jars ) {
                
            	if ( jar.getAbsolutePath().endsWith( _BackupExtension ) == false ) {
            	
            		vUrls.add(jar);
            
            	}
            	
            }
        
        }

        return vUrls.toArray( new File[0] );

    }
    
    protected File[] RecursiveFindFilesToLoad( String strPath, String strFileExtension, int intMaxDepth, int intActuakDepth ) {
    	
        Vector<File> vUrls = new Vector<File>();

        File Directory = new File( strPath );
 
        if ( Directory.exists() && Directory.isDirectory() ) {
            
            File[] ListFoundFile = Directory.listFiles(); 
         
			Arrays.sort( ListFoundFile );
            
            for ( File FileFound : ListFoundFile ) {
                
            	if ( FileFound.isDirectory() == true && FileFound.canRead() == true ) { 
            	
            		if ( intActuakDepth + 1 <= intMaxDepth && FileFound.getAbsolutePath().endsWith( _BackupExtension ) == false ) {  
            			
            			File[] DeepListFoundFile = RecursiveFindFilesToLoad( FileFound.getAbsolutePath(), strFileExtension, intMaxDepth, intActuakDepth + 1 );
            		
                        for ( File DeepFileFound : DeepListFoundFile ) {
  
                    		vUrls.add(  DeepFileFound );
                        }
            		
            		}	
            	
            	}	
            	else if ( FileFound.isFile() == true && FileFound.getAbsolutePath().endsWith( strFileExtension ) == true ) {
            	
            		vUrls.add(  FileFound );

            	}	
            
            }
        
        }

        return vUrls.toArray( new File[0] );
        
    }
    
    public boolean LoadClassFiles( String strPath, String strFileExtension, int intMaxDepth, Logger logger  ) {

    	boolean bResult = false;
        
        try {

			if ( logger != null )   
        	    logger.log( Level.INFO, String.format( "Loading classes from path: [%s]", strPath ) ); 

			File[] jars = RecursiveFindFilesToLoad( strPath, strFileExtension, intMaxDepth,  1 );
			
			Arrays.sort( jars );
			
			if ( logger != null )
				logger.log( Level.INFO, String.format( "Jar count files found: [%s]", Integer.toString( jars.length ) ) ); 

            if ( jars.length > 0 ) {
                
            	for ( File jar : jars ) {

                	try {
                        
                		this.addFile( jar, logger );

                		if ( logger != null )
                			logger.log( Level.INFO, String.format( "Added jar file from the path: [%s]", jar.getAbsolutePath() ) ); 
                    
                	} 
                	catch ( Exception Ex ) {
                       
                		if ( logger != null ) 
                			logger.log( Level.SEVERE, Ex.getMessage(), Ex ); 

                	}
            
                }

                bResult = true;          
            
            };
            

    	} 
    	catch ( Exception Ex ) {

    		if ( logger != null ) 
    			logger.log( Level.SEVERE, Ex.getMessage(), Ex ); 

        }
        
    	return bResult;
    	
    }

}
