package CommonClasses;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import ExtendedLogger.CExtendedLogger;

public class CSimplePlainTextLog {

	protected String strPathToStoreFiles;
	protected String strPrefixName; 
	protected String strFileExt; 
	protected String strHeader; 
	protected String strTail; 
	protected long lngMaxFileSize;
	
	protected Semaphore LockWriter;
	
	protected File FilePath;
	protected PrintWriter FileWriter;
	
	protected String strDateTimeFormatPrefix;
	
	protected SimpleDateFormat DTF;
	
	public CSimplePlainTextLog( String strPathToStoreFiles, String strPrefixName, String strDateTimeFormatPrefix, String strFileExt, String strHeader, String strTail, long lngMaxFileSize, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
		
			this.strPathToStoreFiles = strPathToStoreFiles;

			new File( strPathToStoreFiles ).mkdirs();
			
			if ( strPrefixName != null )
				this.strPrefixName = strPrefixName;
			else
				this.strPrefixName = "";

			if ( strDateTimeFormatPrefix != null )
				this.strDateTimeFormatPrefix = strDateTimeFormatPrefix;
			else
				this.strDateTimeFormatPrefix = "";
				
			if ( strDateTimeFormatPrefix.isEmpty() == false ) {
				
				DTF = new SimpleDateFormat( strDateTimeFormatPrefix );
				
			}
			
			if ( strFileExt != null && strFileExt.isEmpty() == false )
				this.strFileExt = strFileExt;
			else
				this.strFileExt = "log";

			if ( strHeader != null ) 
				this.strHeader = strHeader;
			else
				this.strHeader = "";

			if ( strTail != null )
				this.strTail = strTail;
			else
				this.strTail = "";

			if ( lngMaxFileSize > strHeader.length() + strTail.length() + 51200 ) //50KB
				this.lngMaxFileSize = lngMaxFileSize;
			else
				this.lngMaxFileSize = strHeader.length() + strTail.length() + 524288; //512KB

			LockWriter = new Semaphore( 1 );
		
			createPlainTextLogFile( true, true, 0, Logger, Lang );
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1020", Err.getMessage(), Err );

		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1021", Ex.getMessage(), Ex );

		}
	    
	}
	
	public void createPlainTextLogFile( boolean bForceCreation, boolean bLockWriter, long lngLengthDataToAdd, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
			
			if ( lngMaxFileSize > 0 ) {
			
				if ( bLockWriter ) {

					LockWriter.acquire();

				}	

				if ( bForceCreation || FilePath == null || FilePath.length() + lngLengthDataToAdd + strTail.length() >= lngMaxFileSize ) {

					if ( FileWriter != null ) {

						if ( strTail.isEmpty() == false ) {
						
							if ( strTail.endsWith( "\n" ) )
								FileWriter.print( strTail );
							else
								FileWriter.println( strTail );
							
						}	

						FileWriter.flush();
						FileWriter.close();

					}

					String strUUID = UUID.randomUUID().toString();
					
					String strNewFilePath = strPathToStoreFiles;

					if ( strPrefixName.isEmpty() == false ) {
					
						strNewFilePath += strPrefixName + "-";
					
					}	

					if ( strDateTimeFormatPrefix.isEmpty() == false ) {
					
						strNewFilePath += DTF.format( new Date() ) + "-";

					}
					
					strNewFilePath += strUUID + "." + strFileExt;
					
					FilePath = new File( strNewFilePath  );

					FileWriter = new PrintWriter( FilePath );

					if ( strHeader.isEmpty() == false ) {

						if ( strHeader.endsWith( "\n" ) )
							FileWriter.print( strHeader );
						else
							FileWriter.println( strHeader );
							
						FileWriter.flush();

					}

				}

			}
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1020", Err.getMessage(), Err );

		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1021", Ex.getMessage(), Ex );

		}
		finally {
			
			if ( bLockWriter ) {
				
				LockWriter.release();
				
			}	
			
		}
		
	}
	
	public void appendData( String strData, boolean bLockWriter, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
			
			if ( bLockWriter ) {
				
				LockWriter.acquire();
			
			}	

			createPlainTextLogFile( false, false, strData.length(), Logger, Lang );
			
			if ( strData.endsWith( "\n" ) )
				FileWriter.print( strData );
			else
				FileWriter.println( strData );
				
			createPlainTextLogFile( false, false, 0, Logger, Lang );
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1020", Err.getMessage(), Err );

		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1021", Ex.getMessage(), Ex );

		}
		finally {
			
			if ( bLockWriter ) {
				
				LockWriter.release();
				
			}	
			
		}
		
	}
	
}
