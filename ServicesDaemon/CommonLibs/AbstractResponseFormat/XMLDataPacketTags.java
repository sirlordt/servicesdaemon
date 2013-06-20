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
package AbstractResponseFormat;

public interface XMLDataPacketTags {

	//DATAPACKET body tags
	public static final String _ResponseFormat_XML_DATAPACKET = "XML-DATAPACKET";

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

	public final static String _XML_StructSecurityToken          = "SecurityToken";
	public final static String _XML_StructTransactionID          = "TransactionID";
	public final static String _XML_StructCode                   = "Code";
	public final static String _XML_StructDescription            = "Description";
	public final static String _XML_StructDescriptionLength      = "255";
	public final static String _XML_StructAuthor                 = "Author";
	public final static String _XML_StructAuthorLength           = "255";
	public final static String _XML_StructAuthorContact          = "AuthorContact";
	public final static String _XML_StructAuthorContactLength    = "255";
	public final static String _XML_StructAuthRequired           = "AuthRequired";
	public final static String _XML_StructAuthRequiredLength     = "3";
	public final static String _XML_StructServiceName            = "ServiceName";
	public final static String _XML_StructServiceNameLength      = "75";
	public final static String _XML_StructAccessType             = "AcessType";
	public final static String _XML_StructAccessTypeLength       = "10";
	public final static String _XML_StructType                   = "Type";
	public final static String _XML_StructTypeLength             = "50";
	public final static String _XML_StructTypeWidth              = "Width";
	public final static String _XML_StructSubType                = "Subtype";
	public final static String _XML_StructSubTypeLength          = "35";
	public final static String _XML_StructRequired               = "Required";
	public final static String _XML_StructRequiredLength         = "3";
	public final static String _XML_StructParamSetName           = "ParamSetName";
	public final static String _XML_StructParamsSetNameLength    = "75";
	public final static String _XML_StructParamName              = "ParamName";
	public final static String _XML_StructParamNameLength        = "75";
	public static final String _XML_StructParamDescription       = "ParamDescription";
	public final static String _XML_StructParamDescriptionLength = "255";

}
