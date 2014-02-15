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
import CommonClasses.ConstantsCommonClasses;
import ExtendedLogger.CExtendedLogger;

public class CPlainTextDBReplicatorStore extends Thread implements IDBReplicator {  //extends Thread

	protected static final String _Go_Sleep = "go_sleep";

	protected volatile boolean bStopNow = false;
	
	protected boolean bTaskRunningLock = false;
	
	protected static final String _Start_Command_Block = "<CommandBlock><![CDATA[";
	protected static final String _Transaction_ID = "TransactionID=";
	protected static final String _Command_ID = "CommandBlockID=";
	protected static final String _Init_Command = "[CommandInit]";
	protected static final String _End_Command = "[/CommandEnd]";
	protected static final String _Param = "Param=\"";
	protected static final String _Command_Block_Length = "CommandBlockLength=";
	protected static final String _End_Command_Block = "]]></CommandBlock>";
	protected static final String _XML_Header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
	protected static final String _Header = "<Header ";

	public static final String _ReplicatorStorageFilesIndex = "ReplicatorStorageFilesIndex.txt";
	public static final String _ReplicatorCurrentStorageCommandID = "ReplicatorCurrentStorageCommandID.txt";
	
	protected String strRunningPath;
	
	protected String strName;
	
	protected String strSourceDBConnectionName; 
	
	protected long lngMaxStoreFileSize;
	
	protected long lngOnFailGoSleepFor;
	
	protected int intCurrentDBReplicatorChannelIndex;
	
	protected LinkedList<IDBChannelReplicator> DBChannelsReplicatorRegistered; 
	
	protected CExpresionsFilters ExpFilters;
	
	protected String strReplicatorStorePath;

	protected LinkedList<String> ReplicatorStoreFilesIndex; 
	
	protected String strCurrentReplicatorStoreFile;
	
	protected PrintWriter CurrentReplicatorStoreFileWriter;
	
	protected String strCurrentCommandID;
	
	protected Semaphore LockWriter;
	
	protected CExtendedLogger Logger;
	
	protected CLanguage Lang;
	
	File ReplicatorStoreFilePath;
	
	SimpleDateFormat DF; 
	SimpleDateFormat TF; 
	
	String strHeaderData;
	int intHeaderLength;
	
	String strDataBlockID;
	
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
			
			strReplicatorStorePath = strRunningPath + "Replicator" + File.separatorChar + strName + File.separatorChar;

			new File( strReplicatorStorePath ).mkdirs();
		
			ReplicatorStoreFilesIndex = this.loadReplicationStorageFilesIndex( strReplicatorStorePath + _ReplicatorStorageFilesIndex, Logger, Lang );

			DF = new SimpleDateFormat( "dd/MM/yyy" ); 
			TF = new SimpleDateFormat( "HH:mm:ss:SSS" ); 
			
			strDataBlockID = "";
			
			if ( ReplicatorStoreFilesIndex.size() > 0 ) {

				strCurrentReplicatorStoreFile = ReplicatorStoreFilesIndex.get( ReplicatorStoreFilesIndex.size() - 1 );

				this.rotateReplicationStoreFile( false, true, Logger, Lang );

				if ( ReplicatorStoreFilePath == null )
					ReplicatorStoreFilePath = new File( strReplicatorStorePath + strCurrentReplicatorStoreFile );
				
			}
			else {

				strDataBlockID = UUID.randomUUID().toString();
				
				strCurrentReplicatorStoreFile = strName + "." + strDataBlockID;

				ReplicatorStoreFilesIndex.add( strCurrentReplicatorStoreFile ); 

				this.saveReplicationStorageFilesIndex( ReplicatorStoreFilesIndex, strReplicatorStorePath + _ReplicatorStorageFilesIndex, Logger, Lang );

				ReplicatorStoreFilePath = new File( strReplicatorStorePath + strCurrentReplicatorStoreFile );
				
			}
			
			if ( CurrentReplicatorStoreFileWriter == null ) {
				
				if ( ReplicatorStoreFilePath.exists() )
					CurrentReplicatorStoreFileWriter = new PrintWriter( new FileOutputStream( ReplicatorStoreFilePath, true ) );
				else
					CurrentReplicatorStoreFileWriter = new PrintWriter( new FileOutputStream( ReplicatorStoreFilePath ) );
				
				strHeaderData = _XML_Header;
				strHeaderData += _Header + "CreatedDate=\"" + DF.format( new Date() ) + "\" CreatedTime=\"" + TF.format( new Date() )  + "\" DataBlockID=\"" + strDataBlockID + "\" />\n"; 
						
				intHeaderLength = strHeaderData.length();	
				
				CurrentReplicatorStoreFileWriter.println( strHeaderData );
				
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
    	
    	return strReplicatorStorePath;
    	
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
    
	protected LinkedList<String> loadReplicationStorageFilesIndex( String strFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
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
	
	protected synchronized void saveReplicationStorageFilesIndex( LinkedList<String> ReplicationStoreFilesIndex, String strFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
			
			File ReplicationStorageFilesIndex = new File( strFilePath );

			ReplicationStorageFilesIndex.delete();
			
			if ( ReplicationStorageFilesIndex.exists() == false ) {

				BufferedWriter writer = new BufferedWriter( new FileWriter( strFilePath ) );

				for ( String strLine: ReplicationStoreFilesIndex ) {

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

		try {
			
			if ( bLockWriter ) {
			
				LockWriter.acquire();
			
			}	
			
			if ( ReplicatorStoreFilePath.length() >= lngMaxStoreFileSize || bForceRotation ) {

				strDataBlockID = UUID.randomUUID().toString();
				
				strCurrentReplicatorStoreFile = strName + "." + strDataBlockID;

				ReplicatorStoreFilesIndex.add( strCurrentReplicatorStoreFile ); 

				this.saveReplicationStorageFilesIndex( ReplicatorStoreFilesIndex, strReplicatorStorePath + "ReplicationStorageFiles.txt", Logger, Lang );

				if ( CurrentReplicatorStoreFileWriter != null )
					CurrentReplicatorStoreFileWriter.close();
				
				ReplicatorStoreFilePath = new File( strReplicatorStorePath + strCurrentReplicatorStoreFile );

				CurrentReplicatorStoreFileWriter = new PrintWriter( ReplicatorStoreFilePath );

				strHeaderData = _XML_Header;
				strHeaderData += _Header + "CreatedDate=\"" + DF.format( new Date() ) + "\" CreatedTime=\"" + TF.format( new Date() )  + "\" DataBlockID=\"" + strDataBlockID + "\" />\n";
				
				intHeaderLength = strHeaderData.length();	
				
				CurrentReplicatorStoreFileWriter.println( strHeaderData );
				
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
	
	protected boolean checkExpressionsFilters( String strCommand, CExtendedLogger Logger ) {
		
		boolean bResult = false;
		
		if ( ExpFilters == null ) {
			
			bResult = true;
			
		}
		else if ( ExpFilters.checkExpressionInFilters( strCommand, Logger ) ) {
			
			if ( ExpFilters.strType.equalsIgnoreCase( ConstantsCommonClasses._Type_Allow ) )
				bResult = true;
			
		}
		else {
			
			if ( ExpFilters.strType.equalsIgnoreCase( ConstantsCommonClasses._Type_Block ) )
				bResult = true;
			
		}
		
		return bResult;
		
	} 
	
	public boolean addComplexQueryCommandToQueue( String strTransactionID, String strCommand, String strSourceDBConnectionName, LinkedHashMap<String,String> Params, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		if ( this.strSourceDBConnectionName.equalsIgnoreCase( strSourceDBConnectionName ) ) { 
		
			try {

				if ( checkExpressionsFilters( strCommand, Logger ) ) {	

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

					int intOrigNumberLength = Long.toString( CommandBlock.length() ).length();
					
					String strCommandBlockLength = _Command_Block_Length + Integer.toString( CommandBlock.length() ) + "\n";
					
					long lngTotal = CommandBlock.length() + strCommandBlockLength.length();
					
					int intLastNumberLength = Long.toString( lngTotal ).length();
					
					if ( intLastNumberLength > intOrigNumberLength ) {
					
						lngTotal += intLastNumberLength - intOrigNumberLength;
						
					}
					
					CommandBlock.insert( CommandBlock.length(), _Command_Block_Length + Long.toString( lngTotal ) + "\n" );
					
					LockWriter.acquire();

					if ( ReplicatorStoreFilePath.length() >= intHeaderLength && ReplicatorStoreFilePath.length() + CommandBlock.length() > lngMaxStoreFileSize ) {
						
						this.rotateReplicationStoreFile( true, false, Logger, Lang );  //Force rotate store file for don't exceed the max size of replication store file
						
					}
					
					CurrentReplicatorStoreFileWriter.print( CommandBlock.toString() );
					CurrentReplicatorStoreFileWriter.flush();

					bResult = true;

					this.rotateReplicationStoreFile( false, false, Logger, Lang ); //Rotate only if need

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

				LockWriter.release();

			}

		}
		
		return bResult;
		
	}

	public boolean addPlainQueryCommandToQueue( String strTransactionID, String strCommand, String strSourceDBConnectionName, CExtendedLogger Logger, CLanguage Lang  ) {

		boolean bResult = false;
		
		if ( this.strSourceDBConnectionName.equalsIgnoreCase( strSourceDBConnectionName ) ) { 
		
			try {

				if ( checkExpressionsFilters( strCommand, Logger ) ) {	

					StringBuilder CommandBlock = new StringBuilder();
					
					CommandBlock.append( _Start_Command_Block + "\n" );
					CommandBlock.append( _Transaction_ID + strTransactionID + "\n" );
					CommandBlock.append( _Command_ID + UUID.randomUUID().toString() + "\n" );
					CommandBlock.append( _Init_Command + "\n" );
					CommandBlock.append( strCommand + "\n" );
					CommandBlock.append( _End_Command + "\n" );
					//CommandBlock.append( _Command_Block_Length + Integer.toString( CommandBlock.length() ) + "\n" );
					CommandBlock.append( _End_Command_Block + "\n" );
					/*CurrentReplicationStoreFileWriter.println( _Start_Command_Block );
					CurrentReplicationStoreFileWriter.println( _Transaction_ID + strTransactionID );
					CurrentReplicationStoreFileWriter.println( _Command_ID + UUID.randomUUID().toString() );
					CurrentReplicationStoreFileWriter.println( _Init_Command );
					CurrentReplicationStoreFileWriter.println( strCommand );
					CurrentReplicationStoreFileWriter.println( _End_Command );
					CurrentReplicationStoreFileWriter.println( _End_Command_Block );*/

					int intOrigNumberLength = Long.toString( CommandBlock.length() ).length();
					
					String strCommandBlockLength = _Command_Block_Length + Integer.toString( CommandBlock.length() ) + "\n";
					
					long lngTotal = CommandBlock.length() + strCommandBlockLength.length();
					
					int intLastNumberLength = Long.toString( lngTotal ).length();
					
					if ( intLastNumberLength > intOrigNumberLength ) {
					
						lngTotal += intLastNumberLength - intOrigNumberLength;
						
					}
					
					CommandBlock.insert( CommandBlock.length(), _Command_Block_Length + Long.toString( lngTotal ) + "\n" );
					
					LockWriter.acquire();
					
					if ( ReplicatorStoreFilePath.length() >= intHeaderLength && ReplicatorStoreFilePath.length() + CommandBlock.length() > lngMaxStoreFileSize ) {
						
						this.rotateReplicationStoreFile( true, false, Logger, Lang ); //Force rotate store file for don't exceed the max size of replication store file
						
					}
					
					CurrentReplicatorStoreFileWriter.print( CommandBlock.toString() );
					CurrentReplicatorStoreFileWriter.flush();

					bResult = true;

					this.rotateReplicationStoreFile( false, false, Logger, Lang ); //Rotate only if need

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
				
				LockWriter.release();
				
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

	public boolean sendData( String strDataBlockID, String strTransactionID, String strCommandID, String strCommand, LinkedHashMap<String,String> Params, CExtendedLogger Logger, CLanguage Lang ) {
		
		boolean bResult = DBChannelsReplicatorRegistered.size() > 0;

		try {

			for ( int intChannelIndex = intCurrentDBReplicatorChannelIndex; intChannelIndex < DBChannelsReplicatorRegistered.size(); intChannelIndex++ ) {
				
				IDBChannelReplicator DBChannelReplicator = DBChannelsReplicatorRegistered.get( intChannelIndex );

				if ( DBChannelReplicator.sendData( strDataBlockID, strTransactionID, strCommandID, strCommand, Params, Logger, Lang ) == false ) {

					Logger.logError( "-1001", Lang.translate( "Failed to send data to channel [%s] stoped the replication", DBChannelReplicator.getName() ) );        
					bResult = false;
					break;

				}

				intCurrentDBReplicatorChannelIndex = intChannelIndex;
				
			}
			
			if ( bResult || DBChannelsReplicatorRegistered.size() == 0 )
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
	
	@Override
	public void run() {
		
		try {
	
			strCurrentCommandID = this.loadCurrentCommandID( strReplicatorStorePath + _ReplicatorCurrentStorageCommandID, Logger, Lang );

			LinkedHashMap<String,String> Params = new LinkedHashMap<String,String>(); 

			while ( bStopNow == false ) {
				
				RandomAccessFile reader = this.getCursorLastPosition( strCurrentCommandID, new File( strReplicatorStorePath + ReplicatorStoreFilesIndex.get( 0 ) ), Logger, Lang );
				
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

							strLine = strLine.substring( _Param.length() );
							
							int intIndexToken = strLine.indexOf( "\"=" );
							
							String strKey = strLine.substring( 0, intIndexToken + 2 );
							
							String strValue = strLine.substring( intIndexToken + 2 );
							
							Params.put( strKey, strValue );
							
						}
						else if ( strLine.startsWith( _End_Command_Block ) ) {

							if ( this.sendData( strDataBlockID, strTransactionID, strCommandID, strCommand, Params, Logger, Lang ) ) {
							
								//Save the new RecordID position
								strCurrentCommandID = strCommandID;

								this.saveCurrentCommandID( strCurrentCommandID, strReplicatorStorePath + _ReplicatorCurrentStorageCommandID, Logger, Lang ); 							
							
							}
							else {
								
								strLine = _Go_Sleep; //Fail to send the data go sleep
								break;
								
							}
							
						}

					}
					
					if ( strLine == null ) { //The end of file reached

						if ( ReplicatorStoreFilesIndex.size() > 1 ) { //Not remove the store file maybe the file is not full yet. Wait at another file on queue before do remove 
						
							new File( strReplicatorStorePath + ReplicatorStoreFilesIndex.get( 0 ) ).delete(); //Delete from disk the file

							synchronized ( ReplicatorStoreFilesIndex ) {

								ReplicatorStoreFilesIndex.remove( 0 ); //Remove from the index 

							}

							this.saveReplicationStorageFilesIndex( ReplicatorStoreFilesIndex, strReplicatorStorePath + _ReplicatorStorageFilesIndex, Logger, Lang ); //Save the disk the new index

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
