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
package AbstractResponseFormat;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import net.maindataservices.Utilities;


import AbstractDBEngine.CAbstractDBEngine;
import AbstractService.CAbstractService;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultDataSet;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.ConstantsCommonClasses;
import ExtendedLogger.CExtendedLogger;

public abstract class CAbstractResponseFormat {

	protected static ArrayList<CAbstractResponseFormat> RegisteredResponsesFormats = null;
	protected static int intResponseFormatSearchCodeResult = 0;
	protected static String strResponseFormatVersionSearchResult = "";
	
    protected String strName;
    protected String strMinVersion;
    protected String strMaxVersion;

    protected CConfigServicesDaemon ServicesDaemonConfig; 
    protected CAbstractConfigLoader OwnerConfig;
    
	static {
    	
    	RegisteredResponsesFormats = new ArrayList<CAbstractResponseFormat>();
    
    }
    
	public static CAbstractResponseFormat getResponseFomat( String strName, String strVersion ) {
		
	    CAbstractResponseFormat Result = null;
	    
	    strName = strName.toLowerCase();
		 
	    intResponseFormatSearchCodeResult = -2; //No response format name found
	    strResponseFormatVersionSearchResult = ""; //No version response format found
	    
	    for ( CAbstractResponseFormat ResponseFormat : RegisteredResponsesFormats ) {
			
			if ( ResponseFormat.strName.toLowerCase().equals( strName ) == true ) {
			
				if ( strVersion.equals( ConstantsCommonClasses._Version_Any ) || ( Utilities.versionGreaterEquals( strVersion, ResponseFormat.getMinVersion() ) && Utilities.versionLessEquals( strVersion, ResponseFormat.getMaxVersion() ) ) ) {
				
				   intResponseFormatSearchCodeResult = 0;
				   Result = ResponseFormat;
				   break;
				   
				}
				else {
					
				   intResponseFormatSearchCodeResult = -1; //no version match for response format
					
				}
				
				strResponseFormatVersionSearchResult = ResponseFormat.getMinVersion(); //Save the last response format version available
				
			}
			
		}
		
		return Result;
		
	}
	
	public static int getReponseFormatSearchCodeResult() {
		
		return intResponseFormatSearchCodeResult;
		
	}
	
	public static String getReponseFormatVersionSearchResult() {
		
		return strResponseFormatVersionSearchResult;
		
	}

	public static boolean registerResponseFormat( CAbstractResponseFormat ResponseFormat ) { 
		
		boolean bResult = false;
				
		if ( RegisteredResponsesFormats.contains( ResponseFormat ) == false ) {
			
			RegisteredResponsesFormats.add( ResponseFormat );
		
			bResult = true;
			
		}
		
		return bResult;
		
	}
    
    public static void clearRegisteredResponseFormat() {
    	
    	RegisteredResponsesFormats.clear();
    	
    }
    
    public static int getCountRegisteredResponsesFormats() {
    	
    	return RegisteredResponsesFormats.size();
    	
    }
	
	public String getName() {
    	
    	return this.strName;
    	
    } 
    
    public String getMinVersion() {
    	
    	return this.strMinVersion;
    	
    }
	
    public String getMaxVersion() {
    	
    	return this.strMaxVersion;
    }
        
    public boolean initResponseFormat( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) {
   
    	boolean bResult = true;
    	
    	this.ServicesDaemonConfig = ServicesDaemonConfig;
    	this.OwnerConfig = OwnerConfig;
    	
    	return bResult;
    	
    }
	
    //public abstract CAbstractResponseFormat getNewInstance();
    public abstract String getContentType();
    public abstract String getCharacterEncoding();
    public abstract String enumerateServices( HashMap<String,CAbstractService> RegisteredServices, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang );
    public abstract boolean formatResultSet( HttpServletResponse Response, CResultDataSet ResultDataSet, CAbstractDBEngine DBEngine, int intInternalFetchSize, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, boolean bDeleteTempReponseFile, CExtendedLogger Logger, CLanguage Lang );
    public abstract boolean formatResultsSets( HttpServletResponse Response, ArrayList<CResultDataSet> ResultDataSetList, CAbstractDBEngine DBEngine, int intInternalFetchSize, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, boolean bDeleteTempReponseFile, CExtendedLogger Logger, CLanguage Lang, int intDummyParam );
    public abstract String formatMemoryRowSet( CMemoryRowSet MemoryRowSet, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang );
    public abstract String formatSimpleMessage( String strSecurityTokenID, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion, String strDateTimeFormat, String strDateFormat, String strTimeFormat, CExtendedLogger Logger, CLanguage Lang );

    public boolean copyToResponseStream( HttpServletResponse Response, File TempResponseFormatedFile, int intChunkSize, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;
    	
    	try {
    		
    		FileInputStream TempInputStream = new FileInputStream( TempResponseFormatedFile );

    		Response.setContentLength( (int) TempResponseFormatedFile.length() );
    		
    		if ( intChunkSize == 0)
    			intChunkSize = 10240; //10kb
    		
    		byte[] bytBuffer = new byte[ intChunkSize ];
    		
    		int intBytesReaded;
    		
    		while ( ( intBytesReaded = TempInputStream.read( bytBuffer ) ) != -1 ) {
    			
    			/*if ( lngBytesReaded < intChunkSize ) {
    				
    				bytBuffer = Arrays.copyOf( bytBuffer, (int) lngBytesReaded + 1 );
    				
    			}*/
    			
    			Response.getOutputStream().write( bytBuffer, 0, intBytesReaded );
    			
    		}
    		
    		TempInputStream.close();
    		
    	}
    	catch ( Exception Ex ) {
    		
			if ( Logger != null )
				Logger.logException( "-1010", Ex.getMessage(), Ex );
			else if ( OwnerConfig != null && OwnerConfig.Logger != null )
				OwnerConfig.Logger.logException( "-1010", Ex.getMessage(), Ex );
    		
    	}
    	
    	return bResult;
    	
    }
    
}
