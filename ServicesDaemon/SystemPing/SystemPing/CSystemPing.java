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

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;

import com.sun.rowset.CachedRowSetImpl;

import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.CInputServiceParameter;
import AbstractService.CServicePreExecuteResult;
import AbstractService.ConstantsServicesTags;
import AbstractService.DefaultConstantsServices;
import AbstractService.CInputServiceParameter.TParameterScope;
import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CClassPathLoader;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.DefaultConstantsServicesDaemon;
import CommonClasses.NamesSQLTypes;

public class CSystemPing extends CAbstractService {

    public final static String getJarFolder() {

        String name =  CSystemPing.class.getCanonicalName().replace( '.', '/' );

        String s = CSystemPing.class.getClass().getResource( "/" + name + ".class" ).toString();

        s = s.replace( '/', File.separatorChar );

        if ( s.indexOf(".jar") >= 0 )
           s = s.substring( 0, s.indexOf(".jar") + 4 );
        else
           s = s.substring( 0, s.indexOf(".class") );

        if ( s.indexOf( "jar:file:\\" )  == 0 ) { //Windows style path SO inside jar file 

        	s = s.substring( 10 );

        }
        else if ( s.indexOf( "file:\\" )  == 0 ) { //Windows style path SO .class file

        	s = s.substring( 6 );

        }
        else { //Unix family ( Linux/BSD/Mac/Solaris ) style path SO

            s = s.substring( s.lastIndexOf(':') + 1 );

        }

        return s.substring( 0, s.lastIndexOf( File.separatorChar ) + 1 );

    }

	@Override
	public boolean InitializeService( CServicesDaemonConfig ServicesDaemonConfig, CAbstractConfigLoader OwnerConfig ) { // Alternate manual contructor

		boolean bResult = super.InitializeService( ServicesDaemonConfig, OwnerConfig );
		
		try {
		
			this.bAuthRequired = false;
			this.strJarRunningPath = getJarFolder();
			DefaultConstantsSystemPing.strDefaultRunningPath = this.strJarRunningPath;
			this.strServiceName = "System.Ping";
			this.strServiceVersion = "0.0.0.1";

			this.SetupService( DefaultConstantsSystemPing.strDefaultMainFileLog, DefaultConstantsSystemPing.strDefaultRunningPath + DefaultConstantsServices.strDefaultLangsDir + DefaultConstantsSystemPing.strDefaultMainFile + "." + ServicesDaemonConfig.strDefaultLang ); //Init the Logger and Lang

			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Running dir: [%s]", this.strJarRunningPath ) );        
			ServiceLogger.LogMessage( "1", ServiceLang.Translate( "Version: [%s]", this.strServiceVersion ) );        

			CClassPathLoader ClassPathLoader = new CClassPathLoader( ServiceLogger, ServiceLang );

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPreExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePreExecute();

			ClassPathLoader.LoadClassFiles( this.strJarRunningPath + DefaultConstantsServices.strDefaultPostExecuteDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

			this.LoadAndRegisterServicePostExecute();

			this.strServiceDescription = ServiceLang.Translate( "Lets see if the server responds correctly, for monitoring purposes" );

			ArrayList< CInputServiceParameter > ServiceInputParameters = new ArrayList< CInputServiceParameter >();

			CInputServiceParameter InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormat, false, ConstantsServicesTags._RequestResponseFormatType, ConstantsServicesTags._RequestResponseFormatLength, TParameterScope.IN, ServiceLang.Translate( "Response format name, example: XML-DATAPACKET, CSV, JSON" ) );

			ServiceInputParameters.add( InputParameter ); 	

			InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestResponseFormatVersion, false, ConstantsServicesTags._RequestResponseFormatVersionType, ConstantsServicesTags._RequestResponseFormatVersionLength, TParameterScope.IN, ServiceLang.Translate( "Response format version, example: 1.1" ) );

			ServiceInputParameters.add( InputParameter ); 	

			InputParameter = new CInputServiceParameter( ConstantsServicesTags._RequestServiceName, true, ConstantsServicesTags._RequestServiceNameType, ConstantsServicesTags._RequestServiceNameLength, TParameterScope.IN, ServiceLang.Translate( "Service Name" ) );

			ServiceInputParameters.add( InputParameter );

			InputParameter = new CInputServiceParameter( ConstantsSystemPing._RequestPing, true, ConstantsSystemPing._RequestPingType, "0", TParameterScope.IN, ServiceLang.Translate( "Whole number sent as a parameter and return increased by 1" ) );

			ServiceInputParameters.add( InputParameter );

			GroupsInputParametersService.put( ConstantsServicesTags._Default, ServiceInputParameters );

		}
		catch ( Exception Ex ) {

			bResult = false;
			
			if ( OwnerLogger != null )
        		OwnerLogger.LogException( "-1010", Ex.getMessage(), Ex );
			
		}

		return bResult;
		
	}
	
	@Override
	public int ExecuteService( int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String,CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {

		int intResultCode = -1000;

		if ( this.CheckServiceInputParameters( GroupsInputParametersService.get( ConstantsServicesTags._Default ), Request, Response, ResponseFormat, strResponseFormatVersion, OwnerConfig.getConfigValue( ConstantsSystemPing._Global_DateTime_Format ) , OwnerConfig.getConfigValue( ConstantsSystemPing._Global_Date_Format ), OwnerConfig.getConfigValue( ConstantsSystemPing._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang ) == true ) {

			CServicePreExecuteResult ServicePreExecuteResult = this.RunServicePreExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePreExecuteResult == null || ServicePreExecuteResult.bStopExecuteService == false ) {

				Response.setContentType( ResponseFormat.getContentType() );
				Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

				try {

					RowSetMetaData RowsetMetaData = new RowSetMetaDataImpl();
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

					CachedRowset.moveToCurrentRow();

					String strResponseBuffer = ResponseFormat.FormatResultSet( CachedRowset, strResponseFormatVersion, OwnerConfig.getConfigValue( ConstantsSystemPing._Global_DateTime_Format ) , OwnerConfig.getConfigValue( ConstantsSystemPing._Global_Date_Format ), OwnerConfig.getConfigValue( ConstantsSystemPing._Global_Time_Format ), this.ServiceLogger!=null?this.ServiceLogger:this.OwnerLogger, this.ServiceLang!=null?this.ServiceLang:this.OwnerLang );

					Response.getWriter().print( strResponseBuffer );

					intResultCode = 1;

				}
				catch ( Exception Ex ) {

					if ( ServiceLogger != null )
						ServiceLogger.LogException( "-1010", Ex.getMessage(), Ex ); 
					else if ( OwnerLogger != null )
						OwnerLogger.LogException( "-1010", Ex.getMessage(), Ex );

				}

			}
			else {

				intResultCode = ServicePreExecuteResult.intResultCode;

			}

			CServicePostExecuteResult ServicePostExecuteResult = this.RunServicePostExecute( intEntryCode, Request, Response, strSecurityTokenID, RegisteredServices, ResponseFormat, strResponseFormatVersion );

			if ( ServicePostExecuteResult != null ) {

				intResultCode = ServicePostExecuteResult.intResultCode;

			}

		}

		return intResultCode;
	
	}

}
