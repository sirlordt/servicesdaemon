package AbstractResponseFormat;

public interface CSVTags {

	public static final String _ResponseFormat_CSV                      = "CSV";

	public final static String _CSV_StructSecurityToken                 = "SecurityToken";
	public final static String _CSV_StructTransactionID                 = "TransactionID";
	public final static String _CSV_StructCode                          = "Code";
	public final static String _CSV_StructDescription                   = "Description";
	public final static String _CSV_StructDescriptionLength             = "255";
	public final static String _CSV_StructAuthor                        = "Author";
	public final static String _CSV_StructAuthorLength                  = "255";
	public final static String _CSV_StructAuthorContact                 = "AuthorContact";
	public final static String _CSV_StructAuthorContactLength           = "255";
	public final static String _CSV_StructAuthRequired                  = "AuthRequired";
	public final static String _CSV_StructAuthRequiredLength            = "3";
	public final static String _CSV_StructServiceName                   = "ServiceName";
	public final static String _CSV_StructServiceNameLength             = "75";
	public final static String _CSV_StructAccessType                    = "AcessType";
	public final static String _CSV_StructAccessTypeLength              = "10";
	public final static String _CSV_StructType                          = "Type";
	public final static String _CSV_StructTypeLength                    = "50";
	public final static String _CSV_StructTypeWidth                     = "Width";
	public final static String _CSV_StructSubType                       = "Subtype";
	public final static String _CSV_StructSubTypeLength                 = "35";
	public final static String _CSV_StructRequired                      = "Required";
	public final static String _CSV_StructRequiredLength                = "3";
	public final static String _CSV_StructParamSetName                  = "ParamSetName";
	public final static String _CSV_StructParamSetNameLength            = "75";
	public final static String _CSV_StructParamName                     = "ParamName";
	public final static String _CSV_StructParamNameLength               = "75";
	public final static String _CSV_StructParamDescription              = "ParamDescription";
	public final static String _CSV_StructParamDescriptionLength        = "255";
	
	public final static String _CSV_StructSQLOperationCode              = "SQLOperationCode";
	public final static String _CSV_StructSQLOperationDescription       = "SQLOperationDescription";
	public final static String _CSV_StructSQLOperationDescriptionLength = "255";
}
