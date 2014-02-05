package DBReplicator;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import CommonClasses.CExpresionsFilters;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public interface IDBReplicator { //extend Thread

	/*Emulate thread class interface method*/
	public String getName();
	public void start();
	public void join() throws InterruptedException;
	/*Emulate thread class interface method*/
	
	public boolean getStopNow();
	public void setStopNow();
	public boolean getTaskRunningLock();
	
    public boolean initialize( String strRunningPath, String strName, String strSourceDBConnectionName, long lngMaxStoreFileSize, long lngOnFailGoSleepFor, CExtendedLogger Logger, CLanguage Lang );
	
    public void setFilters( CExpresionsFilters ExpFilters );
    public CExpresionsFilters getFilters();
	
    public String getReplicationStorePath();
    
    public void addChannelReplicator( IDBChannelReplicator DBChannelReplicator );
    public LinkedList<IDBChannelReplicator> getChannelsReplicator();
    public int getChannelsReplicatorCount();
    
	public void rotateReplicationStoreFile( CExtendedLogger Logger, CLanguage Lang );
    
	public boolean addComplexQueryCommandToQueue( String strTransactionID, String strCommand, String strSourceDBConnectionName, LinkedHashMap<String,String> Params, CExtendedLogger Logger, CLanguage Lang );
	public boolean addPlainQueryCommandToQueue( String strTransactionID, String strCommand, String strSourceDBConnectionName, CExtendedLogger Logger, CLanguage Lang  );
	
	public boolean sendData( String strTransactionID, String strCommandID, String strCommand, LinkedHashMap<String,String> Params, CExtendedLogger Logger, CLanguage Lang );
	
}
