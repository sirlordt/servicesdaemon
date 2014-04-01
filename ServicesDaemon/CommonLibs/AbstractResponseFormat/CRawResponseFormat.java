package AbstractResponseFormat;

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractService.CAbstractService;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultDataSet;
import CommonClasses.ConstantsCommonConfigXMLTags;
import CommonClasses.ConstantsMessagesCodes;
import ExtendedLogger.CExtendedLogger;

public class CRawResponseFormat extends CAbstractResponseFormat {

	CAbstractResponseFormat DefaultResponseFormat;
	
	public CRawResponseFormat() {
	
		this.strName = "RAW";
		strMinVersion = "1.0";
		strMaxVersion = "1.0";

		DefaultResponseFormat = null;

	}
	
	@Override
    public boolean initResponseFormat( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) {

		if ( super.initResponseFormat( ServicesDaemonConfig, OwnerConfig ) ) {
		
			String strDefaultResponseFormat = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Default_Response_Format, null );
			String strDefaultResponseFormatVersion = (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Default_Response_Format_Version, null );

			DefaultResponseFormat = getResponseFomat(strDefaultResponseFormat, strDefaultResponseFormatVersion );
 
			if ( DefaultResponseFormat == null ) {
				
				if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

					if ( OwnerConfig.Lang != null )
						OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Default response format not found. Check the [%s] and [%s] values in config file", strDefaultResponseFormat, strDefaultResponseFormatVersion ) );
					else
						OwnerConfig.Logger.logWarning( "-1", String.format( "Default response format not found. Check the [%s] and [%s] values in config file", strDefaultResponseFormat, strDefaultResponseFormatVersion ) );

				}    			
				
			}
			
			return DefaultResponseFormat != null;
		
		}
		else {
			
			return false;
			
		}
		
    }	
	
	@Override
	public String getContentType() {
	
		if ( DefaultResponseFormat != null ) {
		
			return DefaultResponseFormat.getContentType();
		
		}	
		else {
		
			if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

				if ( OwnerConfig.Lang != null ) {
				
					OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
				
					return OwnerConfig.Lang.translate( "Default response format not found. Check the log file for more details" );
					
				}
				else {
	
					OwnerConfig.Logger.logWarning( "-1", String.format( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
  
				}
				
			}    			

			return "Default response format not found. Check the log file for more details";
			
		}
		
	}

	@Override
	public String getCharacterEncoding() {
		
		if ( DefaultResponseFormat != null ) {
		
			return DefaultResponseFormat.getCharacterEncoding();
		
		}	
		else {
			
			if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

				if ( OwnerConfig.Lang != null ) {
				
					OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
				
					return OwnerConfig.Lang.translate( "Default response format not found. Check the log file for more details" );
					
				}
				else {
	
					OwnerConfig.Logger.logWarning( "-1", String.format( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
  
				}
				
			}    			

			return "Default response format not found. Check the log file for more details";
			
		}
		
	}

	@Override
	public String enumerateServices( HashMap<String, CAbstractService> RegisteredServices, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {
		
		if ( DefaultResponseFormat != null ) {
		
			return DefaultResponseFormat.enumerateServices( RegisteredServices, strVersion, strDateTimeFormat, strDateFormat, strTimeFormat, Logger, Lang );
		
		}
		else {
			
			if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

				if ( OwnerConfig.Lang != null ) {
				
					OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
				
					return OwnerConfig.Lang.translate( "Default response format not found. Check the log file for more details" );
					
				}
				else {
	
					OwnerConfig.Logger.logWarning( "-1", String.format( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
  
				}
				
			}    			

			return "Default response format not found. Check the log file for more details";
			
		}
		
	}

	@Override
	public boolean formatResultSet( HttpServletResponse Response, CResultDataSet ResultDataSet, CAbstractDBEngine DBEngine, int intInternalFetchSize, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, boolean bDeleteTempReponseFile, CExtendedLogger Logger, CLanguage Lang ) {
		
		if ( ResultDataSet.Result != null && ResultDataSet.Result instanceof ResultSet == false ) {
		
			if ( ResultDataSet.Result instanceof File || ResultDataSet.Result instanceof InputStream ) {
				
				return true;
				
			}
			else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

				if ( OwnerConfig.Lang != null )
					OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Result object type [%s] not supported", ResultDataSet.Result.getClass().getName() ) );
				else
					OwnerConfig.Logger.logWarning( "-1", String.format( "Result object type [%s] not supported", ResultDataSet.Result.getClass().getName() ) );

			}    
			
		}	
	    else if ( DefaultResponseFormat != null ) {
		
	    	return DefaultResponseFormat.formatResultSet(Response, ResultDataSet, DBEngine, intInternalFetchSize, strVersion, strDateTimeFormat, strDateFormat, strTimeFormat, bDeleteTempReponseFile, Logger, Lang );
	    
	    }
		
		return false;
			
	}

	@Override
	public boolean formatResultsSets( HttpServletResponse Response, ArrayList<CResultDataSet> ResultDataSetList, CAbstractDBEngine DBEngine, int intInternalFetchSize, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, boolean bDeleteTempReponseFile, CExtendedLogger Logger, CLanguage Lang, int intDummyParam ) {
		
		Object ResultObject = (ResultSet) CResultDataSet.getFirstResultSetNotNull( ResultDataSetList );
		
		if ( ResultObject != null && ResultObject instanceof ResultSet == false ) {
			
			if ( ResultObject instanceof File || ResultObject instanceof InputStream ) {
				
				return true;
				
			}
			else if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

				if ( OwnerConfig.Lang != null )
					OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Result object type [%s] not supported", ResultObject.getClass().getName() ) );
				else
					OwnerConfig.Logger.logWarning( "-1", String.format( "Result object type [%s] not supported", ResultObject.getClass().getName() ) );

			}    
			
		}	
	    else if ( DefaultResponseFormat != null ) {
		
	    	return DefaultResponseFormat.formatResultsSets(Response, ResultDataSetList, DBEngine, intInternalFetchSize, strVersion, strDateTimeFormat, strDateFormat, strTimeFormat, bDeleteTempReponseFile, Logger, Lang, intDummyParam );
	    
	    }

		return false;
		
	}

	@Override
	public String formatMemoryRowSet( CMemoryRowSet MemoryRowSet, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang) {
		
		if ( DefaultResponseFormat != null ) {
		
			return DefaultResponseFormat.formatMemoryRowSet( MemoryRowSet, strVersion, strDateTimeFormat, strDateFormat, strTimeFormat, Logger, Lang );
		
		}	
		else {
			
			if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

				if ( OwnerConfig.Lang != null ) {
				
					OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
				
					return OwnerConfig.Lang.translate( "Default response format not found. Check the log file for more details" );
					
				}
				else {
	
					OwnerConfig.Logger.logWarning( "-1", String.format( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
  
				}
				
			}    			

			return "Default response format not found. Check the log file for more details";
			
		}

		
	}

	@Override
	public String formatSimpleMessage( String strSecurityTokenID, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang ) {

		if ( DefaultResponseFormat != null ) {
		
			return DefaultResponseFormat.formatSimpleMessage( strSecurityTokenID, strTransactionID, intCode, strDescription, bAttachToError, strVersion, strDateTimeFormat, strDateFormat, strTimeFormat, Logger, Lang );
		
		}
		else {
		
			if ( OwnerConfig != null && OwnerConfig.Logger != null ) {

				if ( OwnerConfig.Lang != null ) {
				
					OwnerConfig.Logger.logWarning( "-1", OwnerConfig.Lang.translate( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
				
					return OwnerConfig.Lang.translate( "Default response format not found. Check the log file for more details" );
					
				}
				else {
	
					OwnerConfig.Logger.logWarning( "-1", String.format( "Default response format not found. Check the [%s] and [%s] values in config file", ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) );
  
				}
				
			}    			

			return "Default response format not found. Check the log file for more details";
		
		}
		
	}

}
