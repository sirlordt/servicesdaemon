package SystemObjectExists;

public interface ConstantsService {

	public static final String _Request_ObjectType        = "ObjectType";
	public final static String _Request_ObjectType_Type   = "Smallint";
	public final static String _Request_ObjectType_Length = "0";
	
	public static final String _Request_ObjectName        = "ObjectName";
	public final static String _Request_ObjectName_Type   = "Varchar";
	public final static String _Request_ObjectName_Length = "128";
	
	public final static String _Table = "1";
	public final static String _View = "2";
	public final static String _Function = "3";
	public final static String _Procedure = "4";
	
	public static final String _Main_File = "SystemObjectExists";
	public static final String _Conf_File = _Main_File + ".conf";
	public static final String _Main_File_Log = _Main_File + ".log";
	
}
