package BPServicesManager;

public abstract class DefaultConstantsBPServicesManager {

	public static String strDefaultRunningPath = "";

	public static final String strDefaultMainFile = "BPServicesManager";
	public static final String strDefaultConfFile = strDefaultMainFile + ".conf";
	public static final String strDefaultMainFileLog = strDefaultMainFile + ".log";
	public static final String strDefaultLogsSystemDir = "Logs/";
	
	public static final String strDefaultTempDir = "Temp/"; 
	public static final String strDefaultDBServicesDir = "BPServices/"; 
	public static final String strDefaultResponsesFormatsDir = "ResponsesFormats/";

	public static final String strDefaultLoggerName = "BPServicesManagerLogger"; 

	public static final String strDefaultResponseFormat = ConfigXMLTagsBPServicesManager._ResponseFormat_JAVA_XML_WEBROWSET;
	public static final String strDefaultResponseFormatVersion = "1.0";
	
	public static final String strDefaultChasetXML = "UTF-8";
	public static final String strDefaultContentTypeXML = "text/xml";

	public static final String strDefaultChasetJSON = "UTF-8";
	public static final String strDefaultContentTypeJSON = "text/json";
	
	public static final String strDefaultChasetCSV = "UTF-8";
	public static final String strDefaultContentTypeCSV = "text/plain";
	public static final boolean bDefaultFieldsQuoteCSV = true;
	public static final String strDefaultSeparatorSymbolCSV = ";";
	public static final boolean bDefaultShowHeadersCSV = true;
	
	public static final int intDefaultInternalFetchSize = 25000;
	
	public static final String strDefaultVersionAny = "any";
	
}
