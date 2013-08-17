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

public interface JavaXMLWebRowSetTags {

	public static final String _ResponseFormat_JAVA_XML_WEBROWSET   = "JAVA-XML-WEBROWSET";

	public final static String _XML_StructSecurityTokenID            = "SecurityTokenID";
	public final static String _XML_StructTransactionID              = "TransactionID";
	public final static String _XML_StructCode                       = "Code";
	public final static String _XML_StructDescription                = "Description";
	public final static int _XML_StructDescriptionLength             = 255;
	public final static String _XML_StructAuthor                     = "Author";
	public final static int _XML_StructAuthorLength                  = 255;
	public final static String _XML_StructAuthorContact              = "AuthorContact";
	public final static int _XML_StructAuthorContactLength           = 255;
	public final static String _XML_StructAuthRequired               = "AuthRequired";
	public final static int _XML_StructAuthRequiredLength            = 3;
	public final static String _XML_StructServiceName                = "ServiceName";
	public final static int _XML_StructServiceNameLength             = 75;
	public final static String _XML_StructAccessType                 = "AcessType";
	public final static int _XML_StructAccessTypeLength              = 10;
	public final static String _XML_StructType                       = "Type";
	public final static int _XML_StructTypeLength                    = 50;
	public final static String _XML_StructTypeWidth                  = "Width";
	public final static String _XML_StructSubType                    = "Subtype";
	public final static int _XML_StructSubTypeLength                 = 35;
	public final static String _XML_StructRequired                   = "Required";
	public final static int _XML_StructRequiredLength                = 3;
	public final static String _XML_StructParamSetName               = "ParamSetName";
	public final static int _XML_StructParamSetNameLength            = 75;
	public final static String _XML_StructParamName                  = "ParamName";
	public final static int _XML_StructParamNameLength               = 75;
	public final static String _XML_StructParamDescription           = "ParamDescription";
	public final static int _XML_StructParamDescriptionLength        = 255;
	
	public final static String _XML_StructSQLOperationAffectedRows   = "SQLOperationAffectedRows";
	public final static String _XML_StructSQLOperationCode           = "SQLOperationCode";
	public final static String _XML_StructSQLOperationDescription    = "SQLOperationDescription";
	public final static int _XML_StructSQLOperationDescriptionLength = 255;
	
}
