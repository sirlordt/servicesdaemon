package ExtendedLogger;

//import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class CExtendedLogRecord extends LogRecord  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7645250759981736025L;
	protected String strCode;
	protected String strSourcePackageName;
    protected long intLineNumber;
	protected String strThreadName;
	protected String strLogType;
	
	public CExtendedLogRecord( Level level, String msg ) {
		
		super( level, msg );
		
		strCode = "";
		strSourcePackageName = "";
		intLineNumber = -1;
		strThreadName = "";
		strLogType = "";
		
	}

	public void setSourcePackageName( String strSourcePackageName ) {
		
		this.strSourcePackageName = strSourcePackageName;
		
	}

	public String getSourcePackageName() {
		
		return strSourcePackageName;
		
	}

	public void setCode( String strCode ) {
		
		this.strCode = strCode;
		
	}

	public String getCode() {
		
		return strCode;
		
	}

	public void setLineNumber( long intLineNumber ) {
		
		this.intLineNumber = intLineNumber;
		
	}

	public long getLineNunber() {
		
		return intLineNumber;
		
	}
	
	public void setThreadName( String strThreadName ) {
		
		this.strThreadName = strThreadName;
		
	}

	public String getThreadName() {
		
		return strThreadName;
		
	}
	
	public void setLogType( String strLogType ) {
		
		this.strLogType = strLogType;
		
	}

	public String getLogType() {
		
		return strLogType;
		
	}
	
}
