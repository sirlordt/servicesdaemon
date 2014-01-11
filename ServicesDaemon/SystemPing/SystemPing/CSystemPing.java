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
package SystemPing;

import java.sql.Date;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import AbstractService.CServicePreExecuteResult;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CClassPathLoader;
import CommonClasses.CMemoryRowSet;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsMessagesCodes;
import CommonClasses.NamesSQLTypes;
import DBCommonClasses.CDBAbstractService;

public class CSystemPing extends CDBAbstractService {

    public CSystemPing() {
    	
    	super();
    	
    }
    
	@Override
	public boolean initializeService( CConfigServicesDaemon ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = super.initializeService( ServicesDaemonConfig, OwnerConfig );
		
		try {
		
			this.bAuthRequired = false;
			this.strRunningPath = net.maindataservices.Utilities.getJarFolder( this.getClass() );
			this.strServiceName = "System.Ping";
			this.strServiceVersion = "0.0.0.1";

			this.setupService( ConstantsSystemPing._Main_File_Log, this.strRunningPath + ConstantsCommonClasses._Langs_Dir + ConstantsSystemPing._Main_File + "." + ConstantsCommonClasses._Lang_Ext ); //Init the Logger and Lang

			ServiceLogger.logMessage( "1", ServiceLang.translate( "Running dir: [%s]", this.strRunningPath ) );        
			ServiceLogger.logMessage( "1", ServiceLang.translate( "Version: [%s]", this.strServiceVersion ) );        

			CClassPathLoader ClassPathLoader = new CClassPathLoader();

			ClassPathLoader.LoadClassFiles( this.strRunningPath + ConstantsCommonClasses._Pre_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang );

			this.loadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strRunningPath + ConstantsCommonClasses._Post_Execute_Dir, ConstantsCommonClasses._Lib_Ext, 2, ServiceLogger, ServiceLang );

			this.loadAndRegisterServicePostExecute();

			this.strServiceDescription = ServiceLang.translate( "Lets see if the server responds correctly, for monitoring purposes" );

			ArrayList< CInputServiceParameter > ServiceInputParameters = new ArrayList< CInputServiceParameter >();

			CInputServiceParameter InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ResponseFormat, false, ConstantsCommonClasses._Request_ResponseFormat_Type, ConstantsCommonClasses._Request_ResponseFormat_Length, TParameterScope.IN, ServiceLang.translate( "Response format name, example: XML-DATAPACKET, CSV, JSON" ) );

			ServiceInputParameters.add( InputParameter ); 	

			InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ResponseFormatVersion, false, ConstantsCommonClasses._Request_ResponseFormatVersion_Type, ConstantsCommonClasses._Request_ResponseFormatVersion_Length, TParameterScope.IN, ServiceLang.translate( "Response format version, example: 1.1" ) );

			ServiceInputParameters.add( InputParameter ); 	

			InputParameter = new CInputServiceParameter( ConstantsCommonClasses._Request_ServiceName, true, ConstantsCommonClasses._Request_ServiceName_Type, ConstantsCommonClasses._Request_ServiceName_Length, TParameterScope.IN, ServiceLang.translate( "Service Name" ) );

			ServiceInputParameters.add( InputParameter );

			InputParameter = new CInputServiceParameter( ConstantsSystemPing._Request_Ping, true, ConstantsSystemPing._Request_Ping_Type, "0", TParameterScope.IN, ServiceLang.translate( "Whole number sent as a parameter and return increased by 1" ) );

			ServiceInputParameters.add( InputParameter );

			GroupsInputParametersService.put( ConstantsCommonClasses._Default, ServiceInputParameters );

		}
		catch ( Exception Ex ) {

			bResult = false;
			
			if ( OwnerLogger != null )
        		OwnerLogger.logException( "-1010", Ex.getMessage(), Ex );
			
		}

		return bResult;
		
	}
	
	@Override
	public int executeService( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String,CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

		int intResultCode = -1000;

		if ( this.checkServiceInputParameters( GroupsInputParametersService.get( ConstantsCommonClasses._Default ), Request, Response, ResponseFormat, strResponseFormatVersion, (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ) , (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang ) == true ) {

			CServicePreExecuteResult ServicePreExecuteResult = this.runServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

				Response.setContentType( ResponseFormat.getContentType() );
				Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

				try {

					/*RowSetMetaData RowsetMetaData = new RowSetMetaDataImpl();
					RowsetMetaData.setColumnCount( 3 );

					RowsetMetaData.setColumnName( 1, ConstantsSystemPing._ResponsePong );
					RowsetMetaData.setColumnType( 1, Types.BIGINT );
					RowsetMetaData.setColumnTypeName( 1, NamesSQLTypes._BIGINT );
					//RowsetMetaData.setColumnDisplaySize( 1, 50 );

					RowsetMetaData.setColumnName( 2, ConstantsSystemPing._ResponseDateRequest );
					RowsetMetaData.setColumnType( 2, Types.DATE );
					RowsetMetaData.setColumnTypeName( 2, NamesSQLTypes._DATE );

					RowsetMetaData.setColumnName( 3, ConstantsSystemPing._ResponseTimeRequest );
					RowsetMetaData.setColumnType( 3, Types.TIME );
					RowsetMetaData.setColumnTypeName( 3, NamesSQLTypes._TIME );

					RowsetMetaData.setTableName( 1, "data" );
					RowsetMetaData.setTableName( 2, "data" );
					RowsetMetaData.setTableName( 3, "data" );

					int intPing = Integer.parseInt( Request.getParameter( ConstantsSystemPing._RequestPing ) );

					CachedRowSet CachedRowset = new CachedRowSetImpl();
					CachedRowset.setMetaData( RowsetMetaData );				

					CachedRowset.moveToInsertRow();

					CachedRowset.updateInt( 1, intPing + 1 );

					CachedRowset.updateDate( 2, new Date( System.currentTimeMillis() ) );

					CachedRowset.updateTime( 3, new Time( System.currentTimeMillis() ) );

					CachedRowset.insertRow();

					CachedRowset.moveToCurrentRow();*/
					
					Long lngPing = Long.parseLong( Request.getParameter( ConstantsSystemPing._Request_Ping ) );

					CMemoryRowSet ResultMemoryRowSet = new CMemoryRowSet( false );
					
					ResultMemoryRowSet.addField( ConstantsSystemPing._Response_Pong, Types.BIGINT, NamesSQLTypes._BIGINT, 0, NamesSQLTypes._BIGINT );
					ResultMemoryRowSet.addField( ConstantsSystemPing._Response_Date_Request, Types.DATE, NamesSQLTypes._DATE, 0, NamesSQLTypes._DATE );
					ResultMemoryRowSet.addField( ConstantsSystemPing._Response_Time_Request, Types.TIME, NamesSQLTypes._TIME, 0, NamesSQLTypes._TIME );
					
					ResultMemoryRowSet.addData( ConstantsSystemPing._Response_Pong, lngPing + 1 );
					ResultMemoryRowSet.addData( ConstantsSystemPing._Response_Date_Request, new Date( System.currentTimeMillis() ) );
					ResultMemoryRowSet.addData( ConstantsSystemPing._Response_Time_Request, new Time( System.currentTimeMillis() ) );
					
					String strResponseBuffer = ResponseFormat.formatMemoryRowSet( ResultMemoryRowSet, strResponseFormatVersion, (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_DateTime_Format, null ) , (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Date_Format, null ), (String) OwnerConfig.sendMessage( ConstantsMessagesCodes._Global_Time_Format, null ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

					Response.getWriter().print( strResponseBuffer );

					intResultCode = 1;

				}
				catch ( Exception Ex ) {

					if ( ServiceLogger != null )
						ServiceLogger.logException( "-1010", Ex.getMessage(), Ex ); 
					else if ( OwnerLogger != null )
						OwnerLogger.logException( "-1010", Ex.getMessage(), Ex );

				}

			}
			else {

				intResultCode = ServicePreExecuteResult.intResultCode;

			}

			CServicePostExecuteResult ServicePostExecuteResult = this.runServicePostExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePostExecuteResult != null ) {

				intResultCode = ServicePostExecuteResult.intResultCode;

			}

		}

		return intResultCode;
	
	}

}
