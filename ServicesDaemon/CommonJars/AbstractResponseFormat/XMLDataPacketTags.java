package AbstractResponseFormat;

public interface XMLDataPacketTags {

	   //DATAPACKET body tags
	   public final static String _DataPacket                     = "DATAPACKET";
	   public final static String _DPVersion                       = "dpversion";
	   public final static String _MetaData                       = "METADATA";
	   public final static String _Fields                         = "FIELDS";
	   public final static String _Field                          = "FIELD";
	   public final static String _Params                         = "PARAMS";
	   public final static String _RowCount                       = "rowcount";
	   public final static String _RowData                        = "ROWDATA";
	   public final static String _Row                            = "ROW";
	   public final static String _Errors                         = "ERRORS";
	   public final static String _ErrorCount                    =  "errorcount";
	   public final static String _Error                          = "ERROR";
	   //DATAPACKET body tags

	   //DATAPACKET body attrib tags
	   public final static String _AttrName                       = "attrname";
	   public final static String _FieldType                      = "fieldtype";
	   public final static String _FieldTypeSubType               = "SUBTYPE";
	   public final static String _FieldTypeShortInteger          = "i2";
	   public final static String _FieldTypeInteger               = "i4";
	   public final static String _FieldTypeBigInt                = "i8";
	   public final static String _FieldTypeString                = "string";
	   public final static String _FieldTypeStringWidth           = "width";
	   public final static String _FieldTypeDate                  = "date";
	   public final static String _FieldTypeTime                  = "time";
	   public final static String _FieldTypeDateTime              = "datetime";
	   public final static String _FieldTypeBlob                  = "bin.hex";
	   public final static String _FieldTypeBlobSubTypeBinary     = "Binary";
	   public final static String _FieldTypeNested                = "nested";
	   public final static String _FieldTypeFloat                 = "r8";
	   public final static String _FieldTypeCurrency              = "r8";
	   public final static String _FieldTypeCurrencySubTypeMoney  = "Money";

	   public final static String _InputParameters                = "InputParameters";

	   public final static String _XMLStructSecurityToken         = "SecurityToken";
	   public final static String _XMLStructTransactionID         = "TransactionID";
	   public final static String _XMLStructCode                  = "Code";
	   public final static String _XMLStructDescription           = "Description";
	   public final static String _XMLStructDescriptionLength     = "255";
	   public final static String _XMLStructAuthor                = "Author";
	   public final static String _XMLStructAuthorLength          = "255";
	   public final static String _XMLStructAuthorContact         = "AuthorContact";
	   public final static String _XMLStructAuthorContactLength   = "255";
	   public final static String _XMLStructAuthRequired          = "AuthRequired";
	   public final static String _XMLStructAuthRequiredLength    = "3";
	   public final static String _XMLStructName                  = "Name";
	   public final static String _XMLStructNameLength            = "75";
	   public final static String _XMLStructAccessType            = "AcessType";
	   public final static String _XMLStructAccessTypeLength      = "10";
	   public final static String _XMLStructType                  = "Type";
	   public final static String _XMLStructTypeLength            = "50";
	   public final static String _XMLStructTypeWidth             = "Width";
	   public final static String _XMLStructSubType               = "Subtype";
	   public final static String _XMLStructSubTypeLength         = "35";
	   public final static String _XMLStructRequired              = "Required";
	   public final static String _XMLStructRequiredLength        = "3";
	   
}
