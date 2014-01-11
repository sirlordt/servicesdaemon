package ServicesDaemon;

import java.io.File;
import java.net.URLDecoder;

/*
 * Very clean boot not external dependencies all class must be on this .jar 
 * 
 * Absolute and minimal required class for boot the program
 * 
 * CBootstrap.class, CClassPathLoaderLight.class, CProgramRunner.class 
 * 
 * */

public class CBootstrap {

	public static final String _LibsDir = "Libs" + File.separatorChar;
    public static final String _LibrariesExt = ".jar";

    public final static String getJarFolder( Class<?> ClassDef ) {

    	String s = "";

    	try {

    		String name = ClassDef.getCanonicalName().replace( '.', '/' );

    		s = ClassDef.getClass().getResource( "/" + name + ".class" ).toString();

    		s = URLDecoder.decode( s, "UTF-8" );
    		
    		s = s.replace( '/', File.separatorChar );

    		if ( s.indexOf(".jar") >= 0 )
    			s = s.substring( 0, s.indexOf(".jar") + 4 );
    		else
    			s = s.substring( 0, s.indexOf(".class") );

    		if ( s.indexOf( "jar:file:\\" )  == 0 ) { //Windows style path SO inside jar file 

    			s = s.substring( 10 );

    		}
    		else if ( s.indexOf( "file:\\" )  == 0 ) { //Windows style path SO .class file

    			s = s.substring( 6 );

    		}
    		else { //Unix family ( Linux/BSD/Mac/Solaris ) style path SO

    			s = s.substring( s.lastIndexOf(':') + 1 );

    		}

    		s = s.substring( 0, s.lastIndexOf( File.separatorChar ) + 1 );

    	}
    	catch ( Exception Ex ) { 

    		Ex.printStackTrace();

    	}

    	return s;
    	
    }
	
    public static void main( String[] args ) throws Exception {

		String strDefaultRunningPath = getJarFolder( CBootstrap.class );
		
		System.out.println( "Running path: \"" + strDefaultRunningPath + "\"" );  

		/*
		 * 
		 *  VERY important LOAD *.jar file on Libs directory and add to classpath from java automagical
		 * 
		 * */
		System.out.println( "Loading bootstrap libraries from: \"" + strDefaultRunningPath + _LibsDir + "\"" );  

		CClassPathLoaderLight ClassPathLoaderLight = new CClassPathLoaderLight();
		
		ClassPathLoaderLight.LoadClassFiles( strDefaultRunningPath + _LibsDir, _LibrariesExt, 2, null );
    	
		try {
			
			int intRunCode = 0;
			
			do {
			
				CProgramRunner ProgramRunner = new CProgramRunner();

				intRunCode = ProgramRunner.runProgram( strDefaultRunningPath, args );
				
				ProgramRunner = null;
			
			} while ( intRunCode == 1000 ); //Restart the program code
			
		}
		catch ( Exception Ex ) {
			
			//System.out.println( "Exception: " + Ex.getMessage() );
			Ex.printStackTrace();
			
		}
		catch ( Error Err ) {
			
			Err.printStackTrace();
			//System.out.println( "Error: " + Err.getMessage() );  
			
		}
		
    }
    
}
