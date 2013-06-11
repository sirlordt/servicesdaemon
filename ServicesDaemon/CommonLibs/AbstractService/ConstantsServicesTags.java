package AbstractService;

import java.sql.Types;

import CommonClasses.NamesSQLTypes;

public interface ConstantsServicesTags {

	public final static byte _ReadWriteService                       = 0;
	public final static byte _ReadService                            = 1;
	public final static byte _WriteService                           = 2;

	public final static String _RequestSecurityTokenID               = "SecurityTokenID";
	public final static String _RequestSecurityTokenIDType           = "BigInt";
	public final static String _RequestTransactionID                 = "TransactionID";
	public final static String _RequestTransactionIDType             = "BigInt";
	public final static String _RequestServiceName                   = "ServiceName";
	public final static String _RequestServiceNameType               = "VarChar";
	public final static String _RequestServiceNameLength             = "255";
	public final static String _RequestResponseFormat                = "ResponseFormatSD";
	public final static String _RequestResponseFormatType            = "VarChar";
	public final static String _RequestResponseFormatLength          = "75";
	public final static String _RequestResponseFormatVersion         = "ResponseFormatVersionSD";
	public final static String _RequestResponseFormatVersionType     = "VarChar";
	public final static String _RequestResponseFormatVersionLength   = "15";

	public final static String _SessionSecurityTokens                = "Security.Tokens";
	//public final static String _SessionKey                         = "Session.Key";
	
    public final static String _Default                              = "default";
	
    public final static String _SecurityTokenID                      = "SecurityTokenID";
	public final static String _SecurityTokenIDType                  = NamesSQLTypes._BIGINT;
	public final static int _SecurityTokenIDTypeID                   = Types.BIGINT;
	public final static String _TransactionID                        = "TransactionID";
	public final static String _TransactionIDType                    = NamesSQLTypes._BIGINT;
	public final static int _TransactionIDTypeID                     = Types.BIGINT;
	public final static String _Code                                 = "Code";
	public final static String _CodeType                             = NamesSQLTypes._INTEGER;
	public final static int _CodeTypeID                              = Types.INTEGER;
	public final static String _Description                          = "Description";
	public final static String _DescriptionType                      = NamesSQLTypes._VARCHAR;
	public final static int _DescriptionTypeID                       = Types.VARCHAR;
	public final static int _DescriptionLength                       = 255;
	
}
