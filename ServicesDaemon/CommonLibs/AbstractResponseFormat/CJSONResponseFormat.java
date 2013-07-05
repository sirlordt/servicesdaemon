package AbstractResponseFormat;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.rowset.CachedRowSet;

import AbstractService.CAbstractService;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultSetResult;
import ExtendedLogger.CExtendedLogger;

public class CJSONResponseFormat extends CAbstractResponseFormat {

	public CJSONResponseFormat() {
		
		this.strName = JSONTags._ResponseFormat_JSON;
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
			return OwnerConfig.getConfigValue( ConstantsResponseFormat._JSON_ContentType );
		else
			return "";
	
	}

	@Override
	public String getCharacterEncoding() {

		if ( OwnerConfig != null )
			return OwnerConfig.getConfigValue( ConstantsResponseFormat._JSON_CharSet );
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
	public String FormatResultSet( ResultSet ResultSet, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultsSets( ArrayList<ResultSet> ResultsSest, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultSet( CResultSetResult ResultSetResult, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatResultsSets( ArrayList<CResultSetResult> ResultsSetsList, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang, int intDummyParam ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatMemoryRowSet( CMemoryRowSet MemoryRowSet, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatMemoryRowSets( ArrayList<CMemoryRowSet> MemoryRowSets, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String FormatSimpleMessage( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		// TODO Auto-generated method stub
		return null;
	}


}
