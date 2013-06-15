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

	public static final String _ResponseFormat_JAVA_XML_WEBROWSET = "JAVA-XML-WEBROWSET";

	public final static String _XMLStructSecurityToken         = "SecurityToken";
	public final static String _XMLStructTransactionID         = "TransactionID";
	public final static String _XMLStructCode                  = "Code";
	public final static String _XMLStructDescription           = "Description";
	public final static int _XMLStructDescriptionLength        = 255;
	public final static String _XMLStructAuthor                = "Author";
	public final static int _XMLStructAuthorLength             = 255;
	public final static String _XMLStructAuthorContact         = "AuthorContact";
	public final static int _XMLStructAuthorContactLength      = 255;
	public final static String _XMLStructAuthRequired          = "AuthRequired";
	public final static int _XMLStructAuthRequiredLength       = 3;
	public final static String _XMLStructServiceName           = "ServiceName";
	public final static int _XMLStructServiceNameLength        = 75;
	public final static String _XMLStructAccessType            = "AcessType";
	public final static int _XMLStructAccessTypeLength         = 10;
	public final static String _XMLStructType                  = "Type";
	public final static int _XMLStructTypeLength               = 50;
	public final static String _XMLStructTypeWidth             = "Width";
	public final static String _XMLStructSubType               = "Subtype";
	public final static int _XMLStructSubTypeLength            = 35;
	public final static String _XMLStructRequired              = "Required";
	public final static int _XMLStructRequiredLength           = 3;
	public final static String _XMLStructParamSetName          = "ParamSetName";
	public final static int _XMLStructParamSetNameLength       = 75;
	public final static String _XMLStructParamName             = "ParamName";
	public final static int _XMLStructParamNameLength          = 75;
	public final static String _XMLStructParamDescription      = "ParamDescription";
	public final static int _XMLStructParamDescriptionLength   = 255;
	
}
