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
package CommonClasses;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * UNDOCUMENTED
 * Select * From tblPersons Where IdPerson=[paramvalue]ParamValue1[/paramvalue]
 *
 * @author Tomás Moreno based in work of Kevin Krumwiede
 */
public class CNamedPreparedStatement implements PreparedStatement {

	protected PreparedStatement ps;
	protected LinkedHashMap<String, Integer> params;
    protected String strParsedStatement = ""; 

	/* Original code constructor class from Kevin Krumwiede

    public NamedPreparedStatement(Connection db, String query, char delim) throws SQLException {
    // map params
    params = new HashMap<String, Integer>();
    int len = query.length();
    StringBuffer parsed_query = new StringBuffer();
    int param_count = 0;
    boolean in_param = false;
    StringBuffer param_name = null;
    for (int i = 0; i < len; ++i) {
      char c = query.charAt(i);
      if(c == delim) {
        if(!in_param) { // beginning of a parameter name
          in_param = true;
          param_name = new StringBuffer();
          ++param_count;
        }
        else { // end of a parameter name
          in_param = false;
          params.put(param_name.toString(), new Integer(param_count));
          parsed_query.append('?');
        }
      }
      else if(in_param) {
        param_name.append(c);
      }
      else {
        parsed_query.append(c);
      }

    }
    if(in_param) {
      throw new IllegalArgumentException("Parameter name not closed: " + param_name.toString());
    }
    // compile ps
    ps = db.prepareStatement(parsed_query.toString());
  }*/

    public static LinkedHashMap<String, Integer> parseQueryAndGetParams( String strQuery, HashMap<String,String> strDelimiters, StringBuffer strParsedQuery ) {
    	
    	LinkedHashMap<String, Integer> Result = new LinkedHashMap<String, Integer>();
    	
		Iterator<Entry<String, String>> Delimiters = strDelimiters.entrySet().iterator();

		final String strDelimiterStart = "[##$$$##]"; 
		final String strDelimiterEnd   = "[/##$$$##]"; 

		while ( Delimiters.hasNext() ) {
			
			Entry<String,String> DelimiterPairs = Delimiters.next();
			
			strQuery = strQuery.replace( DelimiterPairs.getKey(), strDelimiterStart );
			strQuery = strQuery.replace( DelimiterPairs.getValue(), strDelimiterEnd );
			
		}

		// map params
		Result = new LinkedHashMap<String, Integer>();

		int intParamCount = 0;

		boolean bInParam = false;

		int intQueryLength = strQuery.length();

		//StringBuffer strParsedQuery = new StringBuffer();
		
		StringBuffer strParamName = null;
		
        for ( int intIndex = 0; intIndex < intQueryLength; ++intIndex ) {

			int intMaxLengthStart = intIndex + strDelimiterStart.length();

			if ( intMaxLengthStart >= intQueryLength )
				intMaxLengthStart = intQueryLength; 

			String strDelimiterS = strQuery.substring( intIndex, intMaxLengthStart );

			int intMaxLengthEnd = intIndex + strDelimiterEnd.length();

			if ( intMaxLengthEnd >= intQueryLength )
				intMaxLengthEnd = intQueryLength; 

			String strDelimiterE = strQuery.substring( intIndex, intMaxLengthEnd );

			char c = strQuery.charAt( intIndex );

			if ( strDelimiterS.equals( strDelimiterStart ) == true && bInParam == false ) {

				bInParam = true;

				intIndex = intIndex + strDelimiterStart.length() - 1;

				strParamName = new StringBuffer();

				++intParamCount;

			}  
			else if ( strDelimiterE.equals( strDelimiterEnd ) == true && bInParam == true ) {

				bInParam = false;

				intIndex = intIndex + strDelimiterEnd.length() - 1;

				Result.put( strParamName.toString(), new Integer( intParamCount ) );

				strParsedQuery.append( '?' );

			} 
			else if ( bInParam == true ) {

				strParamName.append( c );

			}
			else {

				strParsedQuery.append( c );

			}

		}

		if ( bInParam == true ) {

			throw new IllegalArgumentException( "Parameter name not closed: " + strParamName.toString() );

		}
    	
		return Result;
		
    } 
    
	public CNamedPreparedStatement( Connection db, String strQuery, HashMap<String,String> strDelimiters ) throws SQLException {

		StringBuffer strParsedQuery = new StringBuffer();
		
		params = parseQueryAndGetParams( strQuery, strDelimiters, strParsedQuery );

		this.strParsedStatement = strParsedQuery.toString();
		
		// compile prepare Statement
		ps = db.prepareStatement( strParsedQuery.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );

	}

	public CNamedPreparedStatement( Connection db, LinkedHashMap<String, Integer> strParsedParams, String strParsedQuery ) throws SQLException {

		params = new LinkedHashMap<String, Integer>( strParsedParams );
		
		ps = db.prepareStatement( strParsedQuery.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );

	}

	public CNamedPreparedStatement( Connection db, LinkedHashMap<String, Integer> strParsedParams, String strParsedQuery, int intAutoGeneratedKeys ) throws SQLException {

		params = new LinkedHashMap<String, Integer>( strParsedParams );
		
		ps = db.prepareStatement( strParsedQuery.toString(), intAutoGeneratedKeys );

		//ps.set
		
	}

	public CNamedPreparedStatement( Connection db, LinkedHashMap<String, Integer> strParsedParams, String strParsedQuery, int[] intColumnIndexes ) throws SQLException {

		params = new LinkedHashMap<String, Integer>( strParsedParams );
		
		ps = db.prepareStatement( strParsedQuery.toString(), intColumnIndexes );

	}

	public CNamedPreparedStatement( Connection db, LinkedHashMap<String, Integer> strParsedParams, String strParsedQuery, String[] strColumnNames ) throws SQLException {

		params = new LinkedHashMap<String, Integer>( strParsedParams );
		
		ps = db.prepareStatement( strParsedQuery.toString(), strColumnNames );

	}
	
	public CNamedPreparedStatement( Connection db, LinkedHashMap<String, Integer> strParsedParams, String strParsedQuery, int intResultType, int intResultSetConcurrency ) throws SQLException {

		params = new LinkedHashMap<String, Integer>( strParsedParams );
		
		ps = db.prepareStatement( strParsedQuery.toString(), intResultType, intResultSetConcurrency );

	}
	
	public CNamedPreparedStatement( Connection db, LinkedHashMap<String, Integer> strParsedParams, String strParsedQuery, int intResultType, int intResultSetConcurrency, int intResultSetHoldability ) throws SQLException {

		params = new LinkedHashMap<String, Integer>( strParsedParams );
		
		ps = db.prepareStatement( strParsedQuery.toString(), intResultType, intResultSetConcurrency, intResultSetHoldability );

	}
	
	/* 

     Usage Input: ParseStatement( "Select * From tblPersonas Where IdPersona1=<param>ParamValue1</param> And IdPersona2=<param>P
     aramValue2</param>", "<param>", "</param>" );
     Output:  
      Select * From tblPersonas Where IdPersona1=? And IdPersona2=?
      ParamValue2: 2
      ParamValue1: 1	
	 */

	/*	
  public static void ParseStatement( String strQuery, String strDelimiterStart, String strDelimiterEnd ) throws Exception {

	// map params
    HashMap<String, Integer> params = new HashMap<String, Integer>();

	int intQueryLength = strQuery.length();

	StringBuffer strParsedQuery = new StringBuffer();

	int intParamCount = 0;

	boolean bInParam = false;

	StringBuffer strParamName = null;

    //System.out.println( strDelimiterStart );  
    //System.out.println( strDelimiterEnd );  

    for ( int intIndex = 0; intIndex < intQueryLength; ++intIndex ) {

       int intMaxLengthStart = intIndex + strDelimiterStart.length();

       if ( intMaxLengthStart >= intQueryLength )
          intMaxLengthStart = intQueryLength; 

       String strDelimiterS = strQuery.substring( intIndex, intMaxLengthStart );

       int intMaxLengthEnd = intIndex + strDelimiterEnd.length();

       if ( intMaxLengthEnd >= intQueryLength )
          intMaxLengthEnd = intQueryLength; 

       String strDelimiterE = strQuery.substring( intIndex, intMaxLengthEnd );

	   char c = strQuery.charAt( intIndex );

       //System.out.println( strDelimiterS );  
       //System.out.println( strDelimiterE );  

       if ( strDelimiterS.equals( strDelimiterStart ) == true && bInParam == false ) {

		  bInParam = true;

		  intIndex = intIndex + strDelimiterStart.length() - 1;

		  strParamName = new StringBuffer();

		  ++intParamCount;

          //System.out.println( "Param inicio" );  

       } 
	   else if ( strDelimiterE.equals( strDelimiterEnd ) == true && bInParam == true ) {

		  bInParam = false;

		  intIndex = intIndex + strDelimiterEnd.length() - 1;

		  params.put( strParamName.toString(), new Integer( intParamCount ) );

		  strParsedQuery.append( '?' );

          //System.out.println( "Param final" ); 

	   }
       else if ( bInParam == true ) {

		  strParamName.append( c );

	   }
       else {

	      strParsedQuery.append( c );

	   }

    }

	if ( bInParam == true ) {

	   throw new IllegalArgumentException( "Parameter name not closed: " + strParamName.toString() );

	}


    System.out.println( strParsedQuery.toString() );  

	Set set = params.entrySet();
    // Get an iterator
    Iterator i = set.iterator();

    // Display elements
    while ( i.hasNext() ) {

       Map.Entry me = (Map.Entry)i.next();
       System.out.print( me.getKey() + ": " );
       System.out.println( me.getValue() );

    } 

  }
	 */

	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @return
	 */
	protected int getIndex(String parameterName) {
		Integer idx = params.get(parameterName);
		if(idx == null)
			throw new IllegalArgumentException("Unknown parameter name: " + parameterName);
		return idx.intValue();
	}

	public LinkedHashMap<String,Integer> getNamedParams() {
		
		return params;
		
	}

    public String getParsedStatement() {
    	
    	return strParsedStatement;
    	
    }
	
	/**
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	@Override
	public void addBatch() throws SQLException {
		ps.addBatch();
	}



	/**
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	@Override
	public void clearParameters() throws SQLException {
		ps.clearParameters();
	}



	/**
	 * @see java.sql.PreparedStatement#execute()
	 */
	@Override
	public boolean execute() throws SQLException {
		return ps.execute();
	}



	/**
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	@Override
	public ResultSet executeQuery() throws SQLException {
		return ps.executeQuery();
	}



	/**
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	@Override
	public int executeUpdate() throws SQLException {
		return ps.executeUpdate();
	}



	/**
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return ps.getMetaData();
	}



	/**
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return ps.getParameterMetaData();
	}



	/**
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		ps.setArray(parameterIndex, x);
	}


	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setArray(String parameterName, Array x) throws SQLException {
		ps.setArray(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		ps.setAsciiStream(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setAsciiStream(String parameterName, InputStream x)
			throws SQLException {
		ps.setAsciiStream(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		ps.setAsciiStream(parameterIndex, x, length);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param length
	 * @throws SQLException
	 */
	public void setAsciiStream(String parameterName, InputStream x, int length)
			throws SQLException {
		ps.setAsciiStream(getIndex(parameterName), x, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, long)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		ps.setAsciiStream(parameterIndex, x, length);

	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param length
	 * @throws SQLException
	 */
	public void setAsciiStream(String parameterName, InputStream x, long length)
			throws SQLException {
		Integer idx = params.get(parameterName);
		if(idx == null)
			throw new IllegalArgumentException("Unknown parameter name: " + parameterName);
		ps.setAsciiStream(getIndex(parameterName), x, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		ps.setBigDecimal(parameterIndex, x);

	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setBigDecimal(String parameterName, BigDecimal x)
			throws SQLException {
		ps.setBigDecimal(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		ps.setBinaryStream(parameterIndex, x);

	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setBinaryStream(String parameterName, InputStream x)
			throws SQLException {
		ps.setBinaryStream(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		ps.setBinaryStream(parameterIndex, x, length);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param length
	 * @throws SQLException
	 */
	public void setBinaryStream(String parameterName, InputStream x, int length)
			throws SQLException {
		ps.setBinaryStream(getIndex(parameterName), x, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, long)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		ps.setBinaryStream(parameterIndex, x, length);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param length
	 * @throws SQLException
	 */
	public void setBinaryStream(String parameterName, InputStream x, long length)
			throws SQLException {
		ps.setBinaryStream(getIndex(parameterName), x, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		ps.setBlob(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setBlob(String parameterName, Blob x) throws SQLException {
		ps.setBlob(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream)
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		ps.setBlob(parameterIndex, inputStream);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param inputStream
	 * @throws SQLException
	 */
	public void setBlob(String parameterName, InputStream inputStream)
			throws SQLException {
		ps.setBlob(getIndex(parameterName), inputStream);
	}



	/**
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream, long)
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		ps.setBlob(parameterIndex, inputStream, length);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param inputStream
	 * @param length
	 * @throws SQLException
	 */
	public void setBlob(String parameterName, InputStream inputStream, long length)
			throws SQLException {
		ps.setBlob(getIndex(parameterName), inputStream, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		ps.setBoolean(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setBoolean(String parameterName, boolean x) throws SQLException {
		ps.setBoolean(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		ps.setByte(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setByte(String parameterName, byte x) throws SQLException {
		ps.setByte(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		ps.setBytes(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setBytes(String parameterName, byte[] x) throws SQLException {
		ps.setBytes(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		ps.setCharacterStream(parameterIndex, reader);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param reader
	 * @throws SQLException
	 */
	public void setCharacterStream(String parameterName, Reader reader)
			throws SQLException {
		ps.setCharacterStream(getIndex(parameterName), reader);
	}



	/**
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		ps.setCharacterStream(parameterIndex, reader, length);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param reader
	 * @param length
	 * @throws SQLException
	 */
	public void setCharacterStream(String parameterName, Reader reader, int length)
			throws SQLException {
		ps.setCharacterStream(getIndex(parameterName), reader, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		ps.setCharacterStream(parameterIndex, reader, length);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param reader
	 * @param length
	 * @throws SQLException
	 */
	public void setCharacterStream(String parameterName, Reader reader,
			long length) throws SQLException {
		ps.setCharacterStream(getIndex(parameterName), reader, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		ps.setClob(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setClob(String parameterName, Clob x) throws SQLException {
		ps.setClob(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader)
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		ps.setClob(parameterIndex, reader);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param reader
	 * @throws SQLException
	 */
	public void setClob(String parameterName, Reader reader) throws SQLException {
		ps.setClob(getIndex(parameterName), reader);    
	}



	/**
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader, long)
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		ps.setClob(parameterIndex, reader, length);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param reader
	 * @param length
	 * @throws SQLException
	 */
	public void setClob(String parameterName, Reader reader, long length)
			throws SQLException {
		ps.setClob(getIndex(parameterName), reader, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		ps.setDate(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setDate(String parameterName, Date x) throws SQLException {
		ps.setDate(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		ps.setDate(parameterIndex, x, cal);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param cal
	 * @throws SQLException
	 */
	public void setDate(String parameterName, Date x, Calendar cal)
			throws SQLException {
		ps.setDate(getIndex(parameterName), x, cal);
	}



	/**
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		ps.setDouble(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setDouble(String parameterName, double x) throws SQLException {
		ps.setDouble(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		ps.setFloat(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setFloat(String parameterName, float x) throws SQLException {
		ps.setFloat(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		ps.setInt(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setInt(String parameterName, int x) throws SQLException {
		ps.setInt(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		ps.setLong(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setLong(String parameterName, long x) throws SQLException {
		ps.setLong(getIndex(parameterName), x);    
	}



	/**
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		ps.setNCharacterStream(parameterIndex, value);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param value
	 * @throws SQLException
	 */
	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		ps.setNCharacterStream(getIndex(parameterName), value);    
	}



	/**
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		ps.setNCharacterStream(parameterIndex, value, length);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param value
	 * @param length
	 * @throws SQLException
	 */
	public void setNCharacterStream(String parameterName, Reader value,
			long length) throws SQLException {
		ps.setNCharacterStream(getIndex(parameterName), value, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setNClob(int, java.sql.NClob)
	 */
	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		ps.setNClob(parameterIndex, value);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param value
	 * @throws SQLException
	 */
	public void setNClob(String parameterName, NClob value) throws SQLException {
		ps.setNClob(getIndex(parameterName), value);
	}



	/**
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader)
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		ps.setNClob(parameterIndex, reader);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterIndex
	 * @param reader
	 * @throws SQLException
	 */
	public void setNClob(String parameterName, Reader reader) throws SQLException {
		ps.setNClob(getIndex(parameterName), reader);
	}



	/**
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader, long)
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		ps.setNClob(parameterIndex, reader, length);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param reader
	 * @param length
	 * @throws SQLException
	 */
	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		ps.setNClob(getIndex(parameterName), reader, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setNString(int, java.lang.String)
	 */
	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		ps.setNString(parameterIndex, value);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param value
	 * @throws SQLException
	 */
	public void setNString(String parameterName, String value)
			throws SQLException {
		ps.setNString(getIndex(parameterName), value);
	}  



	/**
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		ps.setNull(parameterIndex, sqlType);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param sqlType
	 * @throws SQLException
	 */
	public void setNull(String parameterName, int sqlType) throws SQLException {
		ps.setNull(getIndex(parameterName), sqlType);
	}



	/**
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName)
			throws SQLException {
		ps.setNull(parameterIndex, sqlType, typeName);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param sqlType
	 * @param typeName
	 * @throws SQLException
	 */
	public void setNull(String parameterName, int sqlType, String typeName)
			throws SQLException {
		ps.setNull(getIndex(parameterName), sqlType, typeName);
	}



	/**
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		ps.setObject(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setObject(String parameterName, Object x) throws SQLException {
		ps.setObject(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		ps.setObject(parameterIndex, x, targetSqlType);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param targetSqlType
	 * @throws SQLException
	 */
	public void setObject(String parameterName, Object x, int targetSqlType)
			throws SQLException {
		ps.setObject(getIndex(parameterName), x, targetSqlType);
	}



	/**
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
	 */
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		ps.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param targetSqlType
	 * @param scaleOrLength
	 * @throws SQLException
	 */
	public void setObject(String parameterName, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		ps.setObject(getIndex(parameterName), x, targetSqlType, scaleOrLength);
	}



	/**
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		ps.setRef(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setRef(String parameterName, Ref x) throws SQLException {
		ps.setRef(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setRowId(int, java.sql.RowId)
	 */
	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		ps.setRowId(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setRowId(String parameterName, RowId x) throws SQLException {
		ps.setRowId(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setSQLXML(int, java.sql.SQLXML)
	 */
	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		ps.setSQLXML(parameterIndex, xmlObject);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param xmlObject
	 * @throws SQLException
	 */
	public void setSQLXML(String parameterName, SQLXML xmlObject)
			throws SQLException {
		ps.setSQLXML(getIndex(parameterName), xmlObject);
	}



	/**
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		ps.setShort(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setShort(String parameterName, short x) throws SQLException {
		ps.setShort(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		ps.setString(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setString(String parameterName, String x) throws SQLException {
		ps.setString(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		ps.setTime(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setTime(String parameterName, Time x) throws SQLException {
		ps.setTime(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		ps.setTime(parameterIndex, x, cal);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param cal
	 * @throws SQLException
	 */
	public void setTime(String parameterName, Time x, Calendar cal)
			throws SQLException {
		ps.setTime(getIndex(parameterName), x, cal);
	}



	/**
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	@Override
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		ps.setTimestamp(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setTimestamp(String parameterName, Timestamp x)
			throws SQLException {
		ps.setTimestamp(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		ps.setTimestamp(parameterIndex, x, cal);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param cal
	 * @throws SQLException
	 */
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
			throws SQLException {
		ps.setTimestamp(getIndex(parameterName), x, cal);
	}



	/**
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		ps.setURL(parameterIndex, x);
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @throws SQLException
	 */
	public void setURL(String parameterName, URL x) throws SQLException {
		ps.setURL(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 */
	@Override
	@Deprecated
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)	throws SQLException {
	
		ps.setUnicodeStream(parameterIndex, x, length);
		
	}



	/**
	 * 
	 * UNDOCUMENTED
	 *
	 * @param parameterName
	 * @param x
	 * @param length
	 * @throws SQLException
	 */
	@Deprecated
	public void setUnicodeStream(String parameterName, InputStream x, int length) throws SQLException {
		
		ps.setUnicodeStream(getIndex(parameterName), x, length);
		
	}



	/**
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	@Override
	public void addBatch(String sql) throws SQLException {
		
		ps.addBatch(sql);
		
	}



	/**
	 * @see java.sql.Statement#cancel()
	 */
	@Override
	public void cancel() throws SQLException {
		
		ps.cancel();
		
	}



	/**
	 * @see java.sql.Statement#clearBatch()
	 */
	@Override
	public void clearBatch() throws SQLException {
		
		ps.clearBatch();
		
	}



	/**
	 * @see java.sql.Statement#clearWarnings()
	 */
	@Override
	public void clearWarnings() throws SQLException {
		
		ps.clearWarnings();
		
	}



	/**
	 * @see java.sql.Statement#close()
	 */
	@Override
	public void close() throws SQLException {
		
		ps.close();
		
	}



	/**
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	@Override
	public boolean execute( String sql ) throws SQLException {
		
		return ps.execute(sql);
		
	}



	/**
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	@Override
	public boolean execute( String sql, int autoGeneratedKeys ) throws SQLException {
		
		return ps.execute(sql, autoGeneratedKeys);
		
	}



	/**
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	@Override
	public boolean execute( String sql, int[] columnIndexes ) throws SQLException {
	
		return execute( sql, columnIndexes );
		
	}



	/**
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		
		return ps.execute(sql, columnNames);
		
	}



	/**
	 * @see java.sql.Statement#executeBatch()
	 */
	@Override
	public int[] executeBatch() throws SQLException {
	
		return ps.executeBatch();
		
	}



	/**
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	@Override
	public ResultSet executeQuery( String sql ) throws SQLException {

		return executeQuery( sql );
	
	}



	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	@Override
	public int executeUpdate( String sql ) throws SQLException {
	
		return ps.executeUpdate( sql );
		
	}



	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
	
		return ps.executeUpdate(sql, autoGeneratedKeys);
	
	}



	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
	
		return ps.executeUpdate(sql, columnIndexes);
	
	}



	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		
		return ps.executeUpdate(sql, columnNames);
		
	}



	/**
	 * @see java.sql.Statement#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		
		return ps.getConnection();
		
	}



	/**
	 * @see java.sql.Statement#getFetchDirection()
	 */
	@Override
	public int getFetchDirection() throws SQLException {
		
		return ps.getFetchDirection();
		
	}



	/**
	 * @see java.sql.Statement#getFetchSize()
	 */
	@Override
	public int getFetchSize() throws SQLException {
		
		return ps.getFetchSize();
		
	}



	/**
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		
		return ps.getGeneratedKeys();
		
	}



	/**
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	@Override
	public int getMaxFieldSize() throws SQLException {
		
		return ps.getMaxFieldSize();
		
	}



	/**
	 * @see java.sql.Statement#getMaxRows()
	 */
	@Override
	public int getMaxRows() throws SQLException {
		
		return ps.getMaxRows();
		
	}



	/**
	 * @see java.sql.Statement#getMoreResults()
	 */
	@Override
	public boolean getMoreResults() throws SQLException {
		
		return ps.getMoreResults();
		
	}



	/**
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	@Override
	public boolean getMoreResults(int current) throws SQLException {
		
		return ps.getMoreResults(current);
		
	}



	/**
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	@Override
	public int getQueryTimeout() throws SQLException {
		
		return ps.getQueryTimeout();
		
	}



	/**
	 * @see java.sql.Statement#getResultSet()
	 */
	@Override
	public ResultSet getResultSet() throws SQLException {
		
		return ps.getResultSet();
		
	}



	/**
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	@Override
	public int getResultSetConcurrency() throws SQLException {
		
		return ps.getResultSetConcurrency();
		
	}



	/**
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	@Override
	public int getResultSetHoldability() throws SQLException {
		
		return ps.getResultSetHoldability();
		
	}



	/**
	 * @see java.sql.Statement#getResultSetType()
	 */
	@Override
	public int getResultSetType() throws SQLException {
		
		return ps.getResultSetType();
		
	}



	/**
	 * @see java.sql.Statement#getUpdateCount()
	 */
	@Override
	public int getUpdateCount() throws SQLException {
		
		return ps.getUpdateCount();
		
	}



	/**
	 * @see java.sql.Statement#getWarnings()
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		
		return ps.getWarnings();
		
	}



	/**
	 * @see java.sql.Statement#isClosed()
	 */
	@Override
	public boolean isClosed() throws SQLException {
		
		return ps.isClosed();
		
	}



	/**
	 * @see java.sql.Statement#isPoolable()
	 */
	@Override
	public boolean isPoolable() throws SQLException {
		
		return ps.isPoolable();
		
	}

	/**
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	@Override
	public void setCursorName( String name ) throws SQLException {

		ps.setCursorName(name);
		
	}



	/**
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	@Override
	public void setEscapeProcessing( boolean enable ) throws SQLException {
		
		ps.setEscapeProcessing( enable );
		
	}



	/**
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	@Override
	public void setFetchDirection( int direction ) throws SQLException {
		
		ps.setFetchDirection( direction );
		
	}



	/**
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	@Override
	public void setFetchSize( int rows ) throws SQLException {
		
		ps.setFetchSize( rows );
		
	}



	/**
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	@Override
	public void setMaxFieldSize( int max ) throws SQLException {
		
		ps.setMaxFieldSize(max);
		
	}



	/**
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	@Override
	public void setMaxRows( int max ) throws SQLException {
		
		ps.setMaxRows(max);
		
	}



	/**
	 * @see java.sql.Statement#setPoolable(boolean)
	 */
	@Override
	public void setPoolable( boolean poolable ) throws SQLException {
		
		ps.setPoolable(poolable);
		
	}



	/**
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	@Override
	public void setQueryTimeout( int seconds ) throws SQLException {
		
		ps.setQueryTimeout(seconds);
		
	}



	/**
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor( Class<?> iface ) throws SQLException {
		
		return ( iface == PreparedStatement.class );
		
	}



	/**
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap( Class<T> iface ) throws SQLException {
		
		return (T) ps;
		
	}


	@Override
	public void closeOnCompletion() throws SQLException {

		ps.closeOnCompletion();
	}


	@Override
	public boolean isCloseOnCompletion() throws SQLException {

		return ps.isCloseOnCompletion();
		
	}

}
