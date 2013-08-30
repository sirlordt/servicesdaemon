package AbstractResponseFormat;

public interface JSONTags {

	public static final String _ResponseFormat_JSON                   = "JSON";

	public final static String _JSON_StructSecurityTokenID            = "SecurityTokenID";
	public final static String _JSON_StructTransactionID              = "TransactionID";
	public final static String _JSON_StructAffectedRows               = "AffectedRows";
	public final static String _JSON_StructCode                       = "Code";
	public final static String _JSON_StructDescription                = "Description";
	public final static int    _JSON_StructDescriptionLength          = 255;
	public final static String _JSON_StructAuthor                     = "Author";
	public final static int    _JSON_StructAuthorLength               = 255;
	public final static String _JSON_StructAuthorContact              = "AuthorContact";
	public final static int    _JSON_StructAuthorContactLength        = 255;
	public final static String _JSON_StructAuthRequired               = "AuthRequired";
	public final static int    _JSON_StructAuthRequiredLength         = 3;
	public final static String _JSON_StructServiceName                = "ServiceName";
	public final static int    _JSON_StructServiceNameLength          = 75;
	public final static String _JSON_StructAccessType                 = "AcessType";
	public final static int    _JSON_StructAccessTypeLength           = 10;
	public final static String _JSON_StructType                       = "Type";
	public final static int    _JSON_StructTypeLength                 = 50;
	public final static String _JSON_StructTypeWidth                  = "Width";
	public final static String _JSON_StructSubType                    = "Subtype";
	public final static int    _JSON_StructSubTypeLength              = 35;
	public final static String _JSON_StructRequired                   = "Required";
	public final static int    _JSON_StructRequiredLength             = 3;
	public final static String _JSON_StructParamSetName               = "ParamSetName";
	public final static int    _JSON_StructParamSetNameLength         = 75;
	public final static String _JSON_StructParamName                  = "ParamName";
	public final static int    _JSON_StructParamNameLength            = 75;
	public final static String _JSON_StructParamDescription           = "ParamDescription";
	public final static int    _JSON_StructParamDescriptionLength     = 255;
	
	/*public final static String _JSON_StructSQLOperationCode           = "SQLOperationCode";
	public final static String _JSON_StructSQLOperationDescription    = "SQLOperationDescription";
	public final static int _JSON_StructSQLOperationDescriptionLength = 255;*/
	
}
