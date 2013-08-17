package AbstractResponseFormat;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.rowset.CachedRowSet;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractService.CAbstractService;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultSetResult;
import ExtendedLogger.CExtendedLogger;

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

		if ( OwnerConfig != null )
			return OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_ContentType );
		else
			return "";
	
	}

	@Override
	public String getCharacterEncoding() {

		if ( OwnerConfig != null )
			return OwnerConfig.getConfigValue( ConstantsResponseFormat._CSV_CharSet );
		else
			return "";
	
	}

    public void DescribeService( CAbstractService Service, CachedRowSet CachedRowset ) {
    	
    	if ( Service.getHiddenService() == false ) {
    		
    	}
    	
    }

	@Override
	public String EnumerateServices( HashMap<String, CAbstractService> RegisteredServices, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultSet( ResultSet SQLDataSet, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultsSets( ArrayList<ResultSet> SQLDataSetList, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultSet( CResultSetResult SQLDataSetResult, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) { 
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultsSets( ArrayList<CResultSetResult> SQLDataSetResultList, CAbstractDBEngine DBEngine, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang, int intDummyParam ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatMemoryRowSet( CMemoryRowSet MemoryRowSet, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatMemoryRowSets( ArrayList<CMemoryRowSet> MemoryRowSetList, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatSimpleMessage( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}


}
