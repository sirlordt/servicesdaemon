package DBReplicator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import CommonClasses.CExpresionsFilters;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CPlainTextDBReplicator extends Thread implements IDBReplicator {  //extends Thread

	protected static final String _Go_Sleep = "go_sleep";

	protected volatile boolean bStopNow = false;
	
	protected boolean bTaskRunningLock = false;
	
	protected static final String _Start_Command_Block = "<Command><![CDATA[";
	protected static final String _Transaction_ID = "TransactionID=";
	protected static final String _Command_ID = "CommandID=";
	protected static final String _Init_Command = "[CommandInit]";
	protected static final String _End_Command = "[/CommandEnd]";
	protected static final String _Param = "Param=\"";
	protected static final String _End_Command_Block = "]]></Command>";
	protected static final String _Header = "<Header ";

	public static final String _ReplicationStorageName = "ReplicationStorageFiles.txt";
	public static final String _ReplicationStorageCommandID = "ReplicationStorageCommandID.txt";
	
	protected String strRunningPath;
	
	protected String strName;
	
	protected String strSourceDBConnectionName; 
	
	protected long lngMaxStoreFileSize;
	
	protected long lngOnFailGoSleepFor;
	
	protected int intCurrentDBReplicatorChannelIndex;
	
	protected LinkedList<IDBChannelReplicator> DBChannelsReplicatorRegistered; 
	
	protected CExpresionsFilters ExpFilters;
	
	protected String strReplicationStorePath;

	protected LinkedList<String> ReplicationStoreFiles; 
	
	protected String strCurrentReplicationStoreFile;
	
	protected PrintWriter CurrentReplicationStoreFileWriter;
	
	protected String strCurrentCommandID;
	
	protected Semaphore LockWriter;
	
	protected CExtendedLogger Logger;
	
	protected CLanguage Lang;
	
	File ReplicationStoreFilePath;
	
	SimpleDateFormat DF; 
	SimpleDateFormat TF; 
	
	String strHeaderData;
	int intHeaderLength;
	
    public boolean initialize( String strRunningPath, String strName, String strSourceDBConnectionName, long lngMaxStoreFileSize, long lngOnFailGoSleepFor, CExtendedLogger Logger, CLanguage Lang ) {

    	boolean bResult = false;
    	
		this.setName( strName + "-DBReplicator" );
		
		try {

			bStopNow = false;
			
			intHeaderLength = 0;
			
			this.strRunningPath = strRunningPath;

			this.strName = strName;

			this.strSourceDBConnectionName = strSourceDBConnectionName;
			
			this.lngMaxStoreFileSize = lngMaxStoreFileSize;
			
			this.lngOnFailGoSleepFor = lngOnFailGoSleepFor;
			
			intCurrentDBReplicatorChannelIndex = 0;
			
			DBChannelsReplicatorRegistered = new LinkedList<IDBChannelReplicator>();
			
			strReplicationStorePath = strRunningPath + "Replication" + File.separatorChar + strName + File.separatorChar;

			new File( strReplicationStorePath ).mkdirs();
		
			ReplicationStoreFiles = this.loadReplicationStorageFiles( strReplicationStorePath + _ReplicationStorageName, Logger, Lang );

			DF = new SimpleDateFormat( "dd/MM/yyy" ); 
			TF = new SimpleDateFormat( "HH/mm/ss" ); 
			
			String strStoreID = "";
			
			if ( ReplicationStoreFiles.size() > 0 ) {

				strCurrentReplicationStoreFile = ReplicationStoreFiles.get( ReplicationStoreFiles.size() - 1 );

				this.rotateReplicationStoreFile( false, true, Logger, Lang );

				if ( ReplicationStoreFilePath == null )
					ReplicationStoreFilePath = new File( strReplicationStorePath + strCurrentReplicationStoreFile );
				
			}
			else {

				strStoreID = UUID.randomUUID().toString();
				
				strCurrentReplicationStoreFile = strName + "." + strStoreID;

				ReplicationStoreFiles.add( strCurrentReplicationStoreFile ); 

				this.saveReplicationStorageFiles( ReplicationStoreFiles, strReplicationStorePath + _ReplicationStorageName, Logger, Lang );

				ReplicationStoreFilePath = new File( strReplicationStorePath + strCurrentReplicationStoreFile );
				
			}
			

			if ( CurrentReplicationStoreFileWriter == null ) {
				
				if ( ReplicationStoreFilePath.exists() )
					CurrentReplicationStoreFileWriter = new PrintWriter( new FileOutputStream( ReplicationStoreFilePath, true ) );
				else
					CurrentReplicationStoreFileWriter = new PrintWriter( new FileOutputStream( ReplicationStoreFilePath ) );
				
				strHeaderData = _Header + "CreatedDate=\"" + DF.format( new Date() ) + "\" CreatedTime=\"" + TF.format( new Date() )  + "\" StoreID=\"" + strStoreID + "\" />"; 
						
				intHeaderLength = strHeaderData.length();	
				
				CurrentReplicationStoreFileWriter.println( strHeaderData );
				
			}
			
			LockWriter = new Semaphore( 1 );
			
			this.Logger = Logger;
			this.Lang = Lang;
			
			bResult = true;
			
    	}
    	catch ( Error Err ) {

    		if ( Logger != null )
    			Logger.logError( "-1020", Err.getMessage(), Err );

    	}
    	catch ( Exception Ex ) {

    		if ( Logger != null )
    			Logger.logException( "-1021", Ex.getMessage(), Ex );

    	}
		
		return bResult;
		
	}	
    
    public void setFilters( CExpresionsFilters ExpFilters ) {
    	
    	this.ExpFilters = ExpFilters;
    	
    }
    
    public CExpresionsFilters getFilters() {
    	
    	return ExpFilters;
    	
    }

    public String getReplicationStorePath() {
    	
    	return strReplicationStorePath;
    	
    }
    
    public void addChannelReplicator( IDBChannelReplicator DBChannelReplicator ) {
    	
    	DBChannelsReplicatorRegistered.add( DBChannelReplicator );
    	
    }
    
    public LinkedList<IDBChannelReplicator> getChannelsReplicator() {
    	
    	return DBChannelsReplicatorRegistered;
    	
    }
 
    public int getChannelsReplicatorCount() {
    	
    	return DBChannelsReplicatorRegistered.size();
    	
    }
    
	protected LinkedList<String> loadReplicationStorageFiles( String strFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
		LinkedList<String> Result = new LinkedList<String>();
		
		try {
		
			File ReplicationStorageFiles = new File( strFilePath );

			if ( ReplicationStorageFiles.exists() && ReplicationStorageFiles.canRead() ) {

				BufferedReader reader = new BufferedReader(new FileReader( strFilePath ) );
				String strLine;

				while ( ( strLine = reader.readLine() ) != null ) {

					Result.add( strLine );

				}

				reader.close();			

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
		
		return Result;
		
	}
	
	protected synchronized void saveReplicationStorageFiles( LinkedList<String> ReplicationStoreFiles, String strFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
			
			File ReplicationStorageFiles = new File( strFilePath );

			ReplicationStorageFiles.delete();
			
			if ( ReplicationStorageFiles.exists() == false ) {

				BufferedWriter writer = new BufferedWriter( new FileWriter( strFilePath ) );

				for ( String strLine: ReplicationStoreFiles ) {

					writer.write( strLine + "\n" );

				}

				writer.flush();
				writer.close();			

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
		
	} 
	
	protected String loadCurrentCommandID( String strFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
		String strResult = "";
		
		try {
			
			File ReplicationStorageRecordID = new File( strFilePath );

			if ( ReplicationStorageRecordID.exists() && ReplicationStorageRecordID.canRead() ) {

				BufferedReader reader = new BufferedReader(new FileReader( strFilePath ) );
				String strLine;

				if ( ( strLine = reader.readLine() ) != null ) {

					strResult = strLine;//net.maindataservices.Utilities.strToInteger( strLine , Logger );
					
				}

				reader.close();			

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
		
		return strResult;
		
	} 
	
	
	protected void saveCurrentCommandID( String strCurrentRecordID, String strFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
			
			BufferedWriter writer = new BufferedWriter( new FileWriter( strFilePath ) );

			writer.write( strCurrentRecordID + "\n" );

			writer.flush();
			writer.close();			
		
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
	
	public void rotateReplicationStoreFile( boolean bForceRotation, boolean bLockWriter, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bForceReleaseWriter = false;
		
		try {
			
			if ( bLockWriter ) {
			
				LockWriter.acquire();
			
				bForceReleaseWriter = true;
			
			}	
			
			if ( ReplicationStoreFilePath.length() >= lngMaxStoreFileSize || bForceRotation ) {

				String strStoreID = UUID.randomUUID().toString();
				
				strCurrentReplicationStoreFile = strName + "." + strStoreID;

				ReplicationStoreFiles.add( strCurrentReplicationStoreFile ); 

				this.saveReplicationStorageFiles( ReplicationStoreFiles, strReplicationStorePath + "ReplicationStorageFiles.txt", Logger, Lang );

				if ( CurrentReplicationStoreFileWriter != null )
					CurrentReplicationStoreFileWriter.close();
				
				ReplicationStoreFilePath = new File( strReplicationStorePath + strCurrentReplicationStoreFile );

				CurrentReplicationStoreFileWriter = new PrintWriter( ReplicationStoreFilePath );

				strHeaderData = _Header + "CreatedDate=\"" + DF.format( new Date() ) + "\" CreatedTime=\"" + TF.format( new Date() )  + "\" StoreID=\"" + strStoreID + "\" />";
				
				intHeaderLength = strHeaderData.length();	
				
				CurrentReplicationStoreFileWriter.println( strHeaderData );
				
			}
			
			if ( bLockWriter ) {
			
				LockWriter.release();
				
				bForceReleaseWriter = false;
				
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
		
		if ( bForceReleaseWriter ) {
			
			LockWriter.release();
			
		}
		
	}
	
	public boolean addComplexQueryCommandToQueue( String strTransactionID, String strCommand, String strSourceDBConnectionName, LinkedHashMap<String,String> Params, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		if ( this.strSourceDBConnectionName.equalsIgnoreCase( strSourceDBConnectionName ) ) { 
		
			try {

				if ( ExpFilters == null || ExpFilters.checkExpressionInFilters( strCommand , Logger ) ) {	

					StringBuilder CommandBlock = new StringBuilder();
					
					CommandBlock.append( _Start_Command_Block + "\n" );
					CommandBlock.append( _Transaction_ID + strTransactionID + "\n" );
					CommandBlock.append( _Command_ID + UUID.randomUUID().toString() + "\n" );
					CommandBlock.append( _Init_Command + "\n" );
					CommandBlock.append( strCommand + "\n" );
					CommandBlock.append( _End_Command + "\n" );
					
					/*CurrentReplicationStoreFileWriter.println( _Start_Command_Block );
					CurrentReplicationStoreFileWriter.println( _Transaction_ID + strTransactionID );
					CurrentReplicationStoreFileWriter.println( _Command_ID + UUID.randomUUID().toString() );
					CurrentReplicationStoreFileWriter.println( _Init_Command );
					CurrentReplicationStoreFileWriter.println( strCommand );
					CurrentReplicationStoreFileWriter.println( _End_Command );*/

					for ( Entry<String,String> Param : Params.entrySet() ) {

						//CurrentReplicationStoreFileWriter.println( _Param + Param.getKey() + "\"=" + Param.getValue() );
						CommandBlock.append( _Param + Param.getKey() + "\"=" + Param.getValue() + "\n" );

					}

					//CurrentReplicationStoreFileWriter.println( _End_Command_Block );
					CommandBlock.append( _End_Command_Block + "\n" );

					LockWriter.acquire();

					if ( ReplicationStoreFilePath.length() >= intHeaderLength && ReplicationStoreFilePath.length() + CommandBlock.length() > lngMaxStoreFileSize ) {
						
						this.rotateReplicationStoreFile( true, false, Logger, Lang );  //Force rotate store file for don't exceed the max size of replication store file
						
					}
					
					CurrentReplicationStoreFileWriter.print( CommandBlock.toString() );
					CurrentReplicationStoreFileWriter.flush();

					bResult = true;

					this.rotateReplicationStoreFile( false, false, Logger, Lang ); //Rotate only if need

					LockWriter.release();


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
		
		}
		
		return bResult;
		
	}

	public boolean addPlainQueryCommandToQueue( String strTransactionID, String strCommand, String strSourceDBConnectionName, CExtendedLogger Logger, CLanguage Lang  ) {

		boolean bResult = false;
		
		if ( this.strSourceDBConnectionName.equalsIgnoreCase( strSourceDBConnectionName ) ) { 
		
			try {

				if ( ExpFilters == null || ExpFilters.checkExpressionInFilters( strCommand , Logger ) ) {	

					StringBuilder CommandBlock = new StringBuilder();
					
					CommandBlock.append( _Start_Command_Block + "\n" );
					CommandBlock.append( _Transaction_ID + strTransactionID + "\n" );
					CommandBlock.append( _Command_ID + UUID.randomUUID().toString() + "\n" );
					CommandBlock.append( _Init_Command + "\n" );
					CommandBlock.append( strCommand + "\n" );
					CommandBlock.append( _End_Command + "\n" );
					CommandBlock.append( _End_Command_Block + "\n" );
					/*CurrentReplicationStoreFileWriter.println( _Start_Command_Block );
					CurrentReplicationStoreFileWriter.println( _Transaction_ID + strTransactionID );
					CurrentReplicationStoreFileWriter.println( _Command_ID + UUID.randomUUID().toString() );
					CurrentReplicationStoreFileWriter.println( _Init_Command );
					CurrentReplicationStoreFileWriter.println( strCommand );
					CurrentReplicationStoreFileWriter.println( _End_Command );
					CurrentReplicationStoreFileWriter.println( _End_Command_Block );*/

					LockWriter.acquire();
					
					if ( ReplicationStoreFilePath.length() >= intHeaderLength && ReplicationStoreFilePath.length() + CommandBlock.length() > lngMaxStoreFileSize ) {
						
						this.rotateReplicationStoreFile( true, false, Logger, Lang ); //Force rotate store file for don't exceed the max size of replication store file
						
					}
					
					CurrentReplicationStoreFileWriter.print( CommandBlock.toString() );
					CurrentReplicationStoreFileWriter.flush();

					bResult = true;

					this.rotateReplicationStoreFile( false, false, Logger, Lang ); //Rotate only if need

					LockWriter.release();

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

		}
		
		return bResult;
		
	}
	
	protected RandomAccessFile getCursorLastPosition( String strCurrentCommandID, File ReplicationStoreFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
		RandomAccessFile Result = null; 
		
		try {

			if ( ReplicationStoreFilePath != null && ReplicationStoreFilePath.exists() && ReplicationStoreFilePath.length() > 0 ) {

				String strLine = null;
				
				/*FileInputStream fstream = new FileInputStream( strReplicationStoreFilePath );
				
				DataInputStream in = new DataInputStream( fstream );

				Result = new BufferedReader( new InputStreamReader( in ) );*/
				
				//File ReplicationStoreFilePath = new File( strReplicationStoreFilePath );
				
				//if (  ) {
				
				Result = new RandomAccessFile( ReplicationStoreFilePath, "r" ); 

				long lngCursor = 0;

				while ( ( strLine = Result.readLine() ) != null ) {

					if ( strLine.startsWith( _Start_Command_Block ) ) {

						lngCursor = Result.getChannel().position();

					}
					else if ( strLine.startsWith( _Command_ID ) ) {

						String strCommandId = strLine.substring( _Command_ID.length() );

						if ( strCommandId.equalsIgnoreCase( strCurrentCommandID ) || strCurrentCommandID.isEmpty() ) {

							Result.seek( lngCursor );

							//in = new DataInputStream( fstream );

							//Result = new BufferedReader( new InputStreamReader( in ) );

							break;

						}

					}

				}

				if ( strLine == null && Result != null ) {

					Result.close();

					Result = new RandomAccessFile( ReplicationStoreFilePath, "r" ); //BufferedReader( new FileReader( strReplicationStoreFilePath ) );

				}

				//}
				
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
		
		return Result;
		
	} 
	
	public synchronized boolean getStopNow() {
		
		return bStopNow;
		
	}
	
	public synchronized void setStopNow() {
		
		this.bStopNow = true;
		
	}
	
	public boolean getTaskRunningLock() {
		
		return bTaskRunningLock;
		
	} 
	

	public boolean sendData( String strTransactionID, String strCommandID, String strCommand, LinkedHashMap<String,String> Params, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = true;

		try {

			for ( int intChannelIndex = intCurrentDBReplicatorChannelIndex; intChannelIndex < DBChannelsReplicatorRegistered.size(); intChannelIndex++ ) {
				
				IDBChannelReplicator DBChannelReplicator = DBChannelsReplicatorRegistered.get( intChannelIndex );

				if ( DBChannelReplicator.sendData( strTransactionID, strCommandID, strCommand, Params, Logger, Lang ) == false ) {

					Logger.logError( "-1001", Lang.translate( "Failed to send data to channel [%s] stoped the replication", DBChannelReplicator.getName() ) );        
					bResult = false;
					break;

				}

				intCurrentDBReplicatorChannelIndex = intChannelIndex;
				
			}
			
			if ( bResult )
				intCurrentDBReplicatorChannelIndex = 0;
			
		} 
		catch ( Error Err ) {

    		if ( Logger != null )
    			Logger.logError( "-1025", Err.getMessage(), Err );

    	}
    	catch ( Exception Ex ) {

    		if ( Logger != null )
    			Logger.logException( "-1026", Ex.getMessage(), Ex );

    	}
		
		return bResult;
		
	}
	
	//Override
	public void run() {
		
		try {
	
			strCurrentCommandID = this.loadCurrentCommandID( strReplicationStorePath + _ReplicationStorageCommandID, Logger, Lang );

			LinkedHashMap<String,String> Params = new LinkedHashMap<String,String>(); 

			while ( bStopNow == false ) {
				
				RandomAccessFile reader = this.getCursorLastPosition( strCurrentCommandID, new File( strReplicationStorePath + ReplicationStoreFiles.get( 0 ) ), Logger, Lang );
				
				if ( reader != null ) {
					
					String strLine = null;
					String strCommand = "";
					String strTransactionID = "";
					String strCommandID = "";
					
					Params.clear();
					
					while ( ( strLine = reader.readLine() ) != null ) {

						if ( strLine.startsWith( _Transaction_ID ) ) {

							strTransactionID = strLine.substring( _Transaction_ID.length() );
							
						}
						else if ( strLine.startsWith( _Command_ID ) ) {
							
							strCommandID = strLine.substring( _Command_ID.length() );							
							
						}
						else if ( strLine.startsWith( _Init_Command ) ) {

							while ( ( strLine = reader.readLine() ) != null ) {

								if ( strLine.startsWith( _End_Command ) ) {
								
									break;
									
								}
								else {
									
									if ( strCommand.isEmpty() )
										strCommand = strLine;
									else
										strCommand = strCommand + "\n" + strLine;
									
								}
									
							}	
							
						}
						else if ( strLine.startsWith( _Param ) ) {

							strLine = strLine.substring( 6 );
							
							int intIndexToken = strLine.indexOf( "\"=" );
							
							String strKey = strLine.substring( 0, intIndexToken + 2 );
							
							String strValue = strLine.substring( intIndexToken + 2 );
							
							Params.put( strKey, strValue );
							
						}
						else if ( strLine.startsWith( _End_Command_Block ) ) {

							if ( this.sendData( strTransactionID, strCommandID, strCommand, Params, Logger, Lang ) ) {
							
								//Save the new RecordID position
								strCurrentCommandID = strCommandID;

								this.saveCurrentCommandID( strCurrentCommandID, strReplicationStorePath + _ReplicationStorageCommandID, Logger, Lang ); 							
							
							}
							else {
								
								strLine = _Go_Sleep; //Fail to send the data go sleep
								break;
								
							}
							
						}

					}
					
					if ( strLine == null ) { //The end of file reached

						if ( ReplicationStoreFiles.size() > 1 ) { //Not remove the store file maybe the file is not full yet. Wait at another file on queue before do remove 
						
							new File( strReplicationStorePath + ReplicationStoreFiles.get( 0 ) ).delete(); //Delete from disk the file

							synchronized ( ReplicationStoreFiles ) {

								ReplicationStoreFiles.remove( 0 ); //Remove from the index 

							}

							this.saveReplicationStorageFiles( ReplicationStoreFiles, strReplicationStorePath + _ReplicationStorageName, Logger, Lang ); //Save the disk the new index

						}
						
					}
					else if ( strLine.equals( _Go_Sleep ) ) { //Fail go sleep before retry send the data again
						
						reader.close();
						
						Thread.sleep( lngOnFailGoSleepFor );
						
					}
					
				}
				else {
					
					Thread.sleep( 5000 ); //Nothing to send go sleep
					
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
		
	}

	
}
