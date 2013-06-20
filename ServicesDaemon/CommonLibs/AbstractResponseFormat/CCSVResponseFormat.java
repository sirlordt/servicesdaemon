package AbstractResponseFormat;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.rowset.CachedRowSet;

import AbstractService.CAbstractService;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultSetResult;

public class CCSVResponseFormat extends CAbstractResponseFormat {

	public CCSVResponseFormat() {
		
		this.strName = CSVTags._ResponseFormat_CSV;
		strMinVersion = "1.0";
		strMaxVersion = "1.0";
		
	}
	
	@Override
	public CAbstractResponseFormat getNewInstance() {

		CJSONResponseFormat NewInstance = new CJSONResponseFormat(); 
		
    	NewInstance.InitResponseFormat( this.ServicesDaemonConfig, this.OwnerConfig );
    	
    	return NewInstance;
	
	}

	@Override
	public String getContentType() {

    	return OwnerConfig.getConfigValue( "strCSV_ContentType" );
	
	}

	@Override
	public String getCharacterEncoding() {

    	return OwnerConfig.getConfigValue( "strCSV_CharSet" );
	
	}

    public void DescribeService( CAbstractService Service, CachedRowSet CachedRowset ) {
    	
    	if ( Service.getHiddenService() == false ) {
    		
    	}
    	
    }

	@Override
	public String EnumerateServices( HashMap<String, CAbstractService> RegisteredServices, String strVersion ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultSet(ResultSet ResultSet, String strVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultsSets(ArrayList<ResultSet> ResultsSest,
			String strVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultsSets(
			ArrayList<CResultSetResult> ResultsSetsList, String strVersion,
			int intDummyParam) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatMemoryRowSet(CMemoryRowSet MemoryRowSet,
			String strVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatMemoryRowSets(ArrayList<CMemoryRowSet> MemoryRowSets,
			String strVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatSimpleMessage(String strSecurityToken,
			String strTransactionID, int intCode, String strDescription,
			boolean bAttachToError, String strVersion) {
		// TODO Auto-generated method stub
		return null;
	}

}
