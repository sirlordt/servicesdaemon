package CommonClasses;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
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
import java.util.Map;
import java.util.Map.Entry;

/**
 * UNDOCUMENTED
 * Select * From tblPersons Where IdPerson=[paramvalue]ParamValue1[/paramvalue]
 *
 * @author Tomás Moreno based in work of Kevin Krumwiede
 */
public class CNamedCallableStatement implements CallableStatement {

	protected CallableStatement cs;
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

	public CNamedCallableStatement( Connection db, String strQuery, HashMap<String,String> strDelimiters ) throws SQLException {

		Iterator<Entry<String, String>> Delimiters = strDelimiters.entrySet().iterator();

		final String strDelimiterStart = "[##$$$##]"; 
		final String strDelimiterEnd   = "[/##$$$##]"; 

		while ( Delimiters.hasNext() ) {
			
			Entry<String,String> DelimiterPairs = Delimiters.next();
			
			strQuery = strQuery.replace( DelimiterPairs.getKey(), strDelimiterStart );
			strQuery = strQuery.replace( DelimiterPairs.getValue(), strDelimiterEnd );
			
		}

		// map params
		params = new LinkedHashMap<String, Integer>();

		int intParamCount = 0;

		boolean bInParam = false;

		int intQueryLength = strQuery.length();

		StringBuffer strParsedQuery = new StringBuffer();
		
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

				params.put( strParamName.toString(), new Integer( intParamCount ) );

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

		this.strParsedStatement = strParsedQuery.toString();

		// compile prepare Statement
		cs = db.prepareCall( strParsedQuery.toString() );
		
	}

	public CNamedCallableStatement( Connection db, LinkedHashMap<String, Integer> strParsedParams, String strParsedQuery ) throws SQLException {

		params = new LinkedHashMap<String, Integer>( strParsedParams );
		
		cs = db.prepareCall( strParsedQuery.toString() );

	}

	/* 
     Usage Input: ParseStatement( "Select * From tblPersonas Where IdPersona1=<param>ParamValue1</param> And IdPersona2=<param>ParamValue2</param>", "<param>", "</param>" );
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
		cs.addBatch();
	}



	/**
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	@Override
	public void clearParameters() throws SQLException {
		cs.clearParameters();
	}



	/**
	 * @see java.sql.PreparedStatement#execute()
	 */
	@Override
	public boolean execute() throws SQLException {
		return cs.execute();
	}



	/**
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	@Override
	public ResultSet executeQuery() throws SQLException {
		return cs.executeQuery();
	}



	/**
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	@Override
	public int executeUpdate() throws SQLException {
		return cs.executeUpdate();
	}



	/**
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return cs.getMetaData();
	}



	/**
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return cs.getParameterMetaData();
	}



	/**
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		cs.setArray(parameterIndex, x);
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
		cs.setArray(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		cs.setAsciiStream(parameterIndex, x);
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
		cs.setAsciiStream(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		cs.setAsciiStream(parameterIndex, x, length);
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
		cs.setAsciiStream(getIndex(parameterName), x, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, long)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		cs.setAsciiStream(parameterIndex, x, length);

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
		cs.setAsciiStream(getIndex(parameterName), x, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		cs.setBigDecimal(parameterIndex, x);

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
		cs.setBigDecimal(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		cs.setBinaryStream(parameterIndex, x);

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
		cs.setBinaryStream(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		cs.setBinaryStream(parameterIndex, x, length);
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
		cs.setBinaryStream(getIndex(parameterName), x, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, long)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		cs.setBinaryStream(parameterIndex, x, length);
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
		cs.setBinaryStream(getIndex(parameterName), x, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		cs.setBlob(parameterIndex, x);
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
		cs.setBlob(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream)
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		cs.setBlob(parameterIndex, inputStream);
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
		cs.setBlob(getIndex(parameterName), inputStream);
	}



	/**
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream, long)
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		cs.setBlob(parameterIndex, inputStream, length);
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
		cs.setBlob(getIndex(parameterName), inputStream, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		cs.setBoolean(parameterIndex, x);
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
		cs.setBoolean(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		cs.setByte(parameterIndex, x);
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
		cs.setByte(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		cs.setBytes(parameterIndex, x);
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
		cs.setBytes(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		cs.setCharacterStream(parameterIndex, reader);
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
		cs.setCharacterStream(getIndex(parameterName), reader);
	}



	/**
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		cs.setCharacterStream(parameterIndex, reader, length);
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
		cs.setCharacterStream(getIndex(parameterName), reader, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		cs.setCharacterStream(parameterIndex, reader, length);
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
		cs.setCharacterStream(getIndex(parameterName), reader, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		cs.setClob(parameterIndex, x);
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
		cs.setClob(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader)
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		cs.setClob(parameterIndex, reader);
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
		cs.setClob(getIndex(parameterName), reader);    
	}



	/**
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader, long)
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		cs.setClob(parameterIndex, reader, length);
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
		cs.setClob(getIndex(parameterName), reader, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		cs.setDate(parameterIndex, x);
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
		cs.setDate(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		cs.setDate(parameterIndex, x, cal);
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
		cs.setDate(getIndex(parameterName), x, cal);
	}



	/**
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		cs.setDouble(parameterIndex, x);
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
		cs.setDouble(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		cs.setFloat(parameterIndex, x);
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
		cs.setFloat(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		cs.setInt(parameterIndex, x);
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
		cs.setInt(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		cs.setLong(parameterIndex, x);
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
		cs.setLong(getIndex(parameterName), x);    
	}



	/**
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		cs.setNCharacterStream(parameterIndex, value);
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
		cs.setNCharacterStream(getIndex(parameterName), value);    
	}



	/**
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		cs.setNCharacterStream(parameterIndex, value, length);
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
		cs.setNCharacterStream(getIndex(parameterName), value, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setNClob(int, java.sql.NClob)
	 */
	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		cs.setNClob(parameterIndex, value);
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
		cs.setNClob(getIndex(parameterName), value);
	}



	/**
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader)
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		cs.setNClob(parameterIndex, reader);
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
		cs.setNClob(getIndex(parameterName), reader);
	}



	/**
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader, long)
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		cs.setNClob(parameterIndex, reader, length);
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
		cs.setNClob(getIndex(parameterName), reader, length);
	}



	/**
	 * @see java.sql.PreparedStatement#setNString(int, java.lang.String)
	 */
	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		cs.setNString(parameterIndex, value);
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
		cs.setNString(getIndex(parameterName), value);
	}  



	/**
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		cs.setNull(parameterIndex, sqlType);
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
		cs.setNull(getIndex(parameterName), sqlType);
	}



	/**
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName)
			throws SQLException {
		cs.setNull(parameterIndex, sqlType, typeName);
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
		cs.setNull(getIndex(parameterName), sqlType, typeName);
	}



	/**
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		cs.setObject(parameterIndex, x);
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
		cs.setObject(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		cs.setObject(parameterIndex, x, targetSqlType);
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
		cs.setObject(getIndex(parameterName), x, targetSqlType);
	}



	/**
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
	 */
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		cs.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
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
		cs.setObject(getIndex(parameterName), x, targetSqlType, scaleOrLength);
	}



	/**
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		cs.setRef(parameterIndex, x);
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
		cs.setRef(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setRowId(int, java.sql.RowId)
	 */
	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		cs.setRowId(parameterIndex, x);
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
		cs.setRowId(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setSQLXML(int, java.sql.SQLXML)
	 */
	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		cs.setSQLXML(parameterIndex, xmlObject);
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
		cs.setSQLXML(getIndex(parameterName), xmlObject);
	}



	/**
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		cs.setShort(parameterIndex, x);
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
		cs.setShort(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		cs.setString(parameterIndex, x);
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
		cs.setString(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		cs.setTime(parameterIndex, x);
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
		cs.setTime(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		cs.setTime(parameterIndex, x, cal);
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
		cs.setTime(getIndex(parameterName), x, cal);
	}



	/**
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	@Override
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		cs.setTimestamp(parameterIndex, x);
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
		cs.setTimestamp(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		cs.setTimestamp(parameterIndex, x, cal);
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
		cs.setTimestamp(getIndex(parameterName), x, cal);
	}



	/**
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		cs.setURL(parameterIndex, x);
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
		cs.setURL(getIndex(parameterName), x);
	}



	/**
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)	throws SQLException {
	
		cs.setUnicodeStream(parameterIndex, x, length);
		
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
	@SuppressWarnings("deprecation")
	public void setUnicodeStream(String parameterName, InputStream x, int length) throws SQLException {
		
		cs.setUnicodeStream(getIndex(parameterName), x, length);
		
	}



	/**
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	@Override
	public void addBatch(String sql) throws SQLException {
		
		cs.addBatch(sql);
		
	}



	/**
	 * @see java.sql.Statement#cancel()
	 */
	@Override
	public void cancel() throws SQLException {
		
		cs.cancel();
		
	}



	/**
	 * @see java.sql.Statement#clearBatch()
	 */
	@Override
	public void clearBatch() throws SQLException {
		
		cs.clearBatch();
		
	}



	/**
	 * @see java.sql.Statement#clearWarnings()
	 */
	@Override
	public void clearWarnings() throws SQLException {
		
		cs.clearWarnings();
		
	}



	/**
	 * @see java.sql.Statement#close()
	 */
	@Override
	public void close() throws SQLException {
		
		cs.close();
		
	}



	/**
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	@Override
	public boolean execute( String sql ) throws SQLException {
		
		return cs.execute(sql);
		
	}



	/**
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	@Override
	public boolean execute( String sql, int autoGeneratedKeys ) throws SQLException {
		
		return cs.execute(sql, autoGeneratedKeys);
		
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
		
		return cs.execute(sql, columnNames);
		
	}



	/**
	 * @see java.sql.Statement#executeBatch()
	 */
	@Override
	public int[] executeBatch() throws SQLException {
	
		return cs.executeBatch();
		
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
	
		return cs.executeUpdate( sql );
		
	}



	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
	
		return cs.executeUpdate(sql, autoGeneratedKeys);
	
	}



	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
	
		return cs.executeUpdate(sql, columnIndexes);
	
	}



	/**
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		
		return cs.executeUpdate(sql, columnNames);
		
	}



	/**
	 * @see java.sql.Statement#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		
		return cs.getConnection();
		
	}



	/**
	 * @see java.sql.Statement#getFetchDirection()
	 */
	@Override
	public int getFetchDirection() throws SQLException {
		
		return cs.getFetchDirection();
		
	}



	/**
	 * @see java.sql.Statement#getFetchSize()
	 */
	@Override
	public int getFetchSize() throws SQLException {
		
		return cs.getFetchSize();
		
	}



	/**
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		
		return cs.getGeneratedKeys();
		
	}



	/**
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	@Override
	public int getMaxFieldSize() throws SQLException {
		
		return cs.getMaxFieldSize();
		
	}



	/**
	 * @see java.sql.Statement#getMaxRows()
	 */
	@Override
	public int getMaxRows() throws SQLException {
		
		return cs.getMaxRows();
		
	}



	/**
	 * @see java.sql.Statement#getMoreResults()
	 */
	@Override
	public boolean getMoreResults() throws SQLException {
		
		return cs.getMoreResults();
		
	}



	/**
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	@Override
	public boolean getMoreResults(int current) throws SQLException {
		
		return cs.getMoreResults(current);
		
	}



	/**
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	@Override
	public int getQueryTimeout() throws SQLException {
		
		return cs.getQueryTimeout();
		
	}



	/**
	 * @see java.sql.Statement#getResultSet()
	 */
	@Override
	public ResultSet getResultSet() throws SQLException {
		
		return cs.getResultSet();
		
	}



	/**
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	@Override
	public int getResultSetConcurrency() throws SQLException {
		
		return cs.getResultSetConcurrency();
		
	}



	/**
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	@Override
	public int getResultSetHoldability() throws SQLException {
		
		return cs.getResultSetHoldability();
		
	}



	/**
	 * @see java.sql.Statement#getResultSetType()
	 */
	@Override
	public int getResultSetType() throws SQLException {
		
		return cs.getResultSetType();
		
	}



	/**
	 * @see java.sql.Statement#getUpdateCount()
	 */
	@Override
	public int getUpdateCount() throws SQLException {
		
		return cs.getUpdateCount();
		
	}



	/**
	 * @see java.sql.Statement#getWarnings()
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		
		return cs.getWarnings();
		
	}



	/**
	 * @see java.sql.Statement#isClosed()
	 */
	@Override
	public boolean isClosed() throws SQLException {
		
		return cs.isClosed();
		
	}



	/**
	 * @see java.sql.Statement#isPoolable()
	 */
	@Override
	public boolean isPoolable() throws SQLException {
		
		return cs.isPoolable();
		
	}

	/**
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	@Override
	public void setCursorName( String name ) throws SQLException {

		cs.setCursorName(name);
		
	}



	/**
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	@Override
	public void setEscapeProcessing( boolean enable ) throws SQLException {
		
		cs.setEscapeProcessing( enable );
		
	}



	/**
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	@Override
	public void setFetchDirection( int direction ) throws SQLException {
		
		cs.setFetchDirection( direction );
		
	}



	/**
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	@Override
	public void setFetchSize( int rows ) throws SQLException {
		
		cs.setFetchSize( rows );
		
	}



	/**
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	@Override
	public void setMaxFieldSize( int max ) throws SQLException {
		
		cs.setMaxFieldSize(max);
		
	}



	/**
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	@Override
	public void setMaxRows( int max ) throws SQLException {
		
		cs.setMaxRows(max);
		
	}



	/**
	 * @see java.sql.Statement#setPoolable(boolean)
	 */
	@Override
	public void setPoolable( boolean poolable ) throws SQLException {
		
		cs.setPoolable(poolable);
		
	}



	/**
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	@Override
	public void setQueryTimeout( int seconds ) throws SQLException {
		
		cs.setQueryTimeout(seconds);
		
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
		
		return (T) cs;
		
	}


	@Override
	public void closeOnCompletion() throws SQLException {

		cs.closeOnCompletion();
	}


	@Override
	public boolean isCloseOnCompletion() throws SQLException {

		return cs.isCloseOnCompletion();
		
	}


	@Override
	public Array getArray(int parameterIndex) throws SQLException {
		
		return cs.getArray( parameterIndex );
		
	}


	@Override
	public Array getArray(String parameterName) throws SQLException {
		
		return cs.getArray( parameterName );
		
	}


	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		
		return cs.getBigDecimal( parameterIndex );
		
	}


	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		
		return cs.getBigDecimal( parameterName );
		
	}


	@Override
	public BigDecimal getBigDecimal(int parameterIndex, int scale)
			throws SQLException {
		
		return cs.getBigDecimal( parameterIndex );
		
	}


	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {

		return cs.getBlob( parameterIndex );
		
	}


	@Override
	public Blob getBlob(String parameterName) throws SQLException {
	
		return cs.getBlob( parameterName );
	
	}


	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {

		return cs.getBoolean( parameterIndex );
	
	}


	@Override
	public boolean getBoolean(String parameterName) throws SQLException {

		return cs.getBoolean( parameterName );
		
	}


	@Override
	public byte getByte(int parameterIndex) throws SQLException {

		return cs.getByte( parameterIndex );
		
	}


	@Override
	public byte getByte(String parameterName) throws SQLException {

		return cs.getByte( parameterName );
		
	}


	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {

		return cs.getBytes( parameterIndex );
		
	}


	@Override
	public byte[] getBytes(String parameterName) throws SQLException {

		return cs.getBytes( parameterName );
		
	}


	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {

		return cs.getCharacterStream( parameterIndex );
		
	}


	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {

		return cs.getCharacterStream( parameterName );
		
	}


	@Override
	public Clob getClob(int parameterIndex) throws SQLException {

		return cs.getClob( parameterIndex );
		
	}


	@Override
	public Clob getClob(String parameterName) throws SQLException {

		return cs.getClob( parameterName );
		
	}


	@Override
	public Date getDate(int parameterIndex) throws SQLException {

		return cs.getDate( parameterIndex );
		
	}


	@Override
	public Date getDate(String parameterName) throws SQLException {

		return cs.getDate( parameterName );
		
	}


	@Override
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {

		return cs.getDate( parameterIndex, cal );
		
	}


	@Override
	public Date getDate(String parameterName, Calendar cal) throws SQLException {

		return cs.getDate( parameterName, cal );
		
	}


	@Override
	public double getDouble(int parameterIndex) throws SQLException {

		return cs.getDouble( parameterIndex );
		
	}


	@Override
	public double getDouble(String parameterName) throws SQLException {

		return cs.getDouble( parameterName );
		
	}


	@Override
	public float getFloat(int parameterIndex) throws SQLException {

		return cs.getFloat( parameterIndex );
		
	}


	@Override
	public float getFloat(String parameterName) throws SQLException {

		return cs.getFloat( parameterName );
		
	}


	@Override
	public int getInt(int parameterIndex) throws SQLException {

		return cs.getInt( parameterIndex );
		
	}


	@Override
	public int getInt(String parameterName) throws SQLException {

		return cs.getInt( parameterName );
		
	}


	@Override
	public long getLong(int parameterIndex) throws SQLException {

		return cs.getLong( parameterIndex );
		
	}


	@Override
	public long getLong(String parameterName) throws SQLException {

		return cs.getLong( parameterName );
		
	}


	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {

		return cs.getNCharacterStream( parameterIndex );
		
	}


	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {

		return cs.getNCharacterStream( parameterName );
		
	}


	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {

		return cs.getNClob( parameterIndex );
		
	}


	@Override
	public NClob getNClob(String parameterName) throws SQLException {

		return cs.getNClob( parameterName );
		
	}


	@Override
	public String getNString(int parameterIndex) throws SQLException {

		return cs.getNString( parameterIndex );
		
	}


	@Override
	public String getNString(String parameterName) throws SQLException {

		return cs.getNString( parameterName );
		
	}


	@Override
	public Object getObject(int parameterIndex) throws SQLException {

		return cs.getObject( parameterIndex );
		
	}


	@Override
	public Object getObject(String parameterName) throws SQLException {

		return cs.getObject( parameterName );
		
	}


	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map)
			throws SQLException {

		return cs.getObject( parameterIndex, map );
		
	}


	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {

		return cs.getObject( parameterName, map );
		
	}


	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {

		return cs.getObject( parameterIndex, type );
		
	}


	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {

		return cs.getObject( parameterName, type );
		
	}


	@Override
	public Ref getRef(int parameterIndex) throws SQLException {

		return cs.getRef( parameterIndex );
		
	}


	@Override
	public Ref getRef(String parameterName) throws SQLException {

		return cs.getRef( parameterName );
		
	}


	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {

		return cs.getRowId( parameterIndex );
		
	}


	@Override
	public RowId getRowId(String parameterName) throws SQLException {

		return cs.getRowId( parameterName );
		
	}


	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {

		return cs.getSQLXML( parameterIndex );
		
	}


	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {

		return cs.getSQLXML( parameterName );
		
	}


	@Override
	public short getShort(int parameterIndex) throws SQLException {

		return cs.getShort( parameterIndex );
		
	}


	@Override
	public short getShort(String parameterName) throws SQLException {
		
		return cs.getShort( parameterName );
		
	}


	@Override
	public String getString(int parameterIndex) throws SQLException {

		return cs.getString( parameterIndex );
		
	}


	@Override
	public String getString(String parameterName) throws SQLException {

		return cs.getString( parameterName );
		
	}


	@Override
	public Time getTime(int parameterIndex) throws SQLException {

		return cs.getTime( parameterIndex );
		
	}


	@Override
	public Time getTime(String parameterName) throws SQLException {

		return cs.getTime( parameterName );
		
	}


	@Override
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {

		return cs.getTime( parameterIndex, cal );
		
	}


	@Override
	public Time getTime(String parameterName, Calendar cal) throws SQLException {

		return cs.getTime( parameterName, cal );
		
	}


	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {

		return cs.getTimestamp( parameterIndex );
		
	}


	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {

		return cs.getTimestamp( parameterName );
		
	}


	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		
		return cs.getTimestamp( parameterIndex, cal );
		
	}


	@Override
	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {

		return cs.getTimestamp( parameterName, cal );
		
	}


	@Override
	public URL getURL(int parameterIndex) throws SQLException {

		return cs.getURL( parameterIndex );
		
	}


	@Override
	public URL getURL(String parameterName) throws SQLException {

		return cs.getURL( parameterName );
		
	}


	@Override
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {

       cs.registerOutParameter( parameterIndex, sqlType );
		
	}


	@Override
	public void registerOutParameter(String parameterName, int sqlType)	throws SQLException {

		cs.registerOutParameter( getIndex( parameterName ), sqlType );
		
	}


	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {

		cs.registerOutParameter( parameterIndex, sqlType, scale );
		
	}


	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, String typeName ) throws SQLException {

		cs.registerOutParameter( parameterIndex, sqlType, typeName );
		
	}


	@Override
	public void registerOutParameter(String parameterName, int sqlType, int scale ) throws SQLException {

		cs.registerOutParameter( getIndex( parameterName) , sqlType, scale );
		
	}


	@Override
	public void registerOutParameter(String parameterName, int sqlType, String typeName ) throws SQLException {
	
		cs.registerOutParameter( getIndex( parameterName ), sqlType, typeName );
		
	}


	@Override
	public boolean wasNull() throws SQLException {

		return cs.wasNull();
		
	}

}