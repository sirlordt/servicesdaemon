package AbstractResponseFormat;

public interface CSVTags {

	public static final String _ResponseFormat_CSV                      = "CSV";

	public final static String _CSV_StructSecurityTokenID               = "SecurityTokenID";
	public final static String _CSV_StructTransactionID                 = "TransactionID";
	public final static String _CSV_StructAffectedRows                  = "AffectedRows";
	public final static String _CSV_StructCode                          = "Code";
	public final static String _CSV_StructDescription                   = "Description";
	public final static int    _CSV_StructDescriptionLength             = 255;
	public final static String _CSV_StructAuthor                        = "Author";
	public final static int    _CSV_StructAuthorLength                  = 255;
	public final static String _CSV_StructAuthorContact                 = "AuthorContact";
	public final static int    _CSV_StructAuthorContactLength           = 255;
	public final static String _CSV_StructAuthRequired                  = "AuthRequired";
	public final static int    _CSV_StructAuthRequiredLength            = 3;
	public final static String _CSV_StructServiceName                   = "ServiceName";
	public final static int    _CSV_StructServiceNameLength             = 75;
	public final static String _CSV_StructAccessType                    = "AcessType";
	public final static int    _CSV_StructAccessTypeLength              = 10;
	public final static String _CSV_StructType                          = "Type";
	public final static int    _CSV_StructTypeLength                    = 50;
	public final static String _CSV_StructTypeWidth                     = "Width";
	public final static String _CSV_StructSubType                       = "Subtype";
	public final static int    _CSV_StructSubTypeLength                 = 35;
	public final static String _CSV_StructRequired                      = "Required";
	public final static int    _CSV_StructRequiredLength                = 3;
	public final static String _CSV_StructParamSetName                  = "ParamSetName";
	public final static int    _CSV_StructParamSetNameLength            = 75;
	public final static String _CSV_StructParamName                     = "ParamName";
	public final static int    _CSV_StructParamNameLength               = 75;
	public final static String _CSV_StructParamDescription              = "ParamDescription";
	public final static int    _CSV_StructParamDescriptionLength        = 255;
	
	public final static String _FieldTypeInteger                        = "integer";
	public final static String _FieldTypeBigInt                         = "long";
	public final static String _FieldTypeString                         = "varchar";

}
