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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import Utilities.Utilities;

import AbstractService.CAbstractService;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CResultSetResult;
import CommonClasses.CServicesDaemonConfig;
import DBServicesManager.DefaultConstantsDBServicesManager;

public abstract class CAbstractResponseFormat {

	protected static ArrayList<CAbstractResponseFormat> RegisteredResponsesFormats = null;
	protected static int intResponseFormatSearchCodeResult = 0;
	protected static String strResponseFormatVersionSearchResult = "";
	
    protected String strName;
    protected String strMinVersion;
    protected String strMaxVersion;

    protected CServicesDaemonConfig ServicesDaemonConfig; 
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
			
				if ( strVersion.equals( DefaultConstantsDBServicesManager.strDefaultVersionAny ) || ( Utilities.VersionGreaterEquals( strVersion, ResponseFormat.getMinVersion() ) && Utilities.VersionLessEquals( strVersion, ResponseFormat.getMaxVersion() ) ) ) {
				
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

	public static boolean ResigterResponseFormat( CAbstractResponseFormat ResponseFormat ) { 
		
		boolean bResult = false;
				
		if ( RegisteredResponsesFormats.contains( ResponseFormat ) == false ) {
			
			RegisteredResponsesFormats.add( ResponseFormat );
		
			bResult = true;
			
		}
		
		return bResult;
		
	}
    
    public static void ClearRegisteredResponseFormat() {
    	
    	RegisteredResponsesFormats.clear();
    	
    }
    
    public static int GetCountRegisteredResponsesFormats() {
    	
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
        
    public boolean InitResponseFormat( CServicesDaemonConfig ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) {
   
    	boolean bResult = true;
    	
    	this.ServicesDaemonConfig = ServicesDaemonConfig;
    	this.OwnerConfig = OwnerConfig;
    	
    	return bResult;
    	
    }
	
    public abstract CAbstractResponseFormat getNewInstance();
    public abstract String getContentType();
    public abstract String getCharacterEncoding();
    public abstract String EnumerateServices( HashMap<String,CAbstractService> RegisteredServices, String strVersion );
    public abstract String FormatResultSet( ResultSet ResultSet, String strVersion );
    public abstract String FormatResultsSets( ArrayList<ResultSet> ResultsSest, String strVersion );
    public abstract String FormatResultsSets( ArrayList<CResultSetResult> ResultsSest, String strVersion, int intDummyParam );
    public abstract String FormatMemoryRowSet( CMemoryRowSet MemoryRowtSet, String strVersion );
    public abstract String FormatMemoryRowSets( ArrayList<CMemoryRowSet> MemoryRowtSets, String strVersion );
    public abstract String FormatSimpleMessage( String strSecurityToken, String strTransactionID, int intCode, String strDescription, boolean bAttachToError, String strVersion );

}
