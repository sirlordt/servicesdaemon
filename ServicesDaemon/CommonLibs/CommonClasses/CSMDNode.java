package CommonClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//SMD = Simple Mark Data

public class CSMDNode {

	public static final String _StartOpenTag = "[";
	public static final String _StartCloseTag = "]";
	public static final String _EndOpenTag = "[/";
	public static final String _EndCloseTag = "]";
	
	public String strName;
	public String strValue;
	
	HashMap<String,String> Attributes; 
	
	CSMDNode ParentNode;
	ArrayList<CSMDNode> ChildNodes;
	
	public CSMDNode() {
		
		this.strName = "";
		this.strValue = "";
		
	    this.Attributes = null; 
		
	    this.ParentNode = null;
	    this.ChildNodes = new ArrayList<CSMDNode>();
	    
	}
	
	public CSMDNode( String strSMCData ) {
		
		ArrayList<CSMDNode> Root = CSMDNode.PaseSMCData( strSMCData, CSMDNode._StartOpenTag, CSMDNode._StartCloseTag, CSMDNode._EndOpenTag, CSMDNode._EndCloseTag );
		
        if ( Root.size() > 0 ) { 
		
        	CSMDNode FirstRoot = Root.get( 0 );
        	
        	this.strName = FirstRoot.strName;
        	this.strValue = FirstRoot.strValue;
        	
        	this.Attributes = FirstRoot.Attributes; 

        	this.ChildNodes = FirstRoot.ChildNodes;
        	
        }
        else {
        	
        	this.strName = "";
        	this.strValue = "";

        	this.Attributes = new HashMap<String,String>(); 

        	this.ChildNodes = new ArrayList<CSMDNode>();

        }
	    
    	this.ParentNode = null;
    	
	}

	public String getAttributeByName( String strName ) {
		
		String strResult = null;
		
		strName = strName.toLowerCase();

		if ( this.Attributes != null ) {
		
			for ( Entry<String,String> Attribute : this.Attributes.entrySet() ) {

				if ( Attribute.getKey().toLowerCase().equals( strName ) ) {

					strResult = Attribute.getValue();

				}

			}

		}
		
		return strResult;
		
	}
	
	public CSMDNode getNodeByName( String strName ) {
		
		CSMDNode Result = null;
	
		strName = strName.toLowerCase();

		if ( strName.equals( this.strName.toLowerCase() ) ) {
			
			Result = this;
			
		}
		else if ( this.ChildNodes.size() > 0 ) {
		
			Result = CSMDNode.getNodeByName( this.ChildNodes, strName );
			
		}
		
		return Result;
		
	} 
	
	public static CSMDNode getNodeByName( ArrayList<CSMDNode> ChildNodes, String strName ) {

		CSMDNode Result = null;
		
		for ( CSMDNode Node: ChildNodes ) {

			if ( Node.strName.toLowerCase().equals( strName ) ) {
				
				Result = Node;

				break;
				
			}
			else if ( Node.ChildNodes.size() > 0 ) {
				
				Result = getNodeByName( Node.ChildNodes, strName );
				
				if ( Result != null )
					break;
				
			}
			
		}
		
		return Result;
		
	}

	public static String ParseName( String strDataToParse ) {
		
		String strResult = strDataToParse;
		
		int intIndex = strResult.indexOf( " " );
		
		if ( intIndex > 0 ) {
			
			strResult = strResult.substring( 0, intIndex );
			
		}
		
		return strResult;
		
	}
	
	public static HashMap<String,String> PaseAttributes( String strDataToParse ) {
		
		HashMap<String,String> Result = new HashMap<String,String>();
		
        Pattern pattern = Pattern.compile("(\\w+)=\"*((?<=\")[^\"]+(?=\")|([^\\s]+))\"*");

        Matcher matcher = pattern.matcher( strDataToParse );

        while( matcher.find() ){

        	Result.put( matcher.group(1), matcher.group(2) );
        	
        }        

		return Result;
		
	}
	
	public static ArrayList<CSMDNode> PaseSMCData( String strDataToParse, String strStartOpenTag, String strStartCloseTag, String strEndOpenTag, String strEndCloseTag ) {
	
		CSMDNode GrandRoot = new CSMDNode();

		if ( strDataToParse.isEmpty() == false && strStartOpenTag.equals( strEndOpenTag ) == false ) {

			CSMDNode ActualRoot = GrandRoot;

			int intIndexOffset = 0;

			int intIndexOffsetValue = 0;

			boolean bMoveIndex;

			while ( intIndexOffset < strDataToParse.length() ) {

				bMoveIndex = true;

				int intIndexTagOpen = strDataToParse.indexOf( strStartOpenTag, intIndexOffset );
				boolean bTagReallyOpen = true;

				if ( intIndexOffset + strEndOpenTag.length() < strDataToParse.length() )
					bTagReallyOpen = strDataToParse.indexOf( strEndOpenTag, intIndexOffset ) > intIndexTagOpen; //strDataToParse.substring( intIndexOffset, intIndexOffset + strEndOpenTag.length() ).equals( strEndOpenTag ) == false;

			    if ( intIndexTagOpen >= 0 && bTagReallyOpen == true ) {

			    	intIndexOffset = intIndexTagOpen;

			    	if ( intIndexOffset - intIndexOffsetValue > 0 ) {

			    		String strTagValue = strDataToParse.substring( intIndexOffsetValue, intIndexOffset );

			    		ActualRoot.strValue = strTagValue.trim();

			    		intIndexOffsetValue = intIndexOffset;

			    	}

			    	int intIndexStartCloseTag = strDataToParse.indexOf( strStartCloseTag, intIndexOffset ); 

			    	if ( intIndexStartCloseTag >= intIndexOffset ) {

			    		String strTag = strDataToParse.substring( intIndexOffset + strStartOpenTag.length(), intIndexStartCloseTag + strStartCloseTag.length() - 1 );

			    		CSMDNode NewRoot = new CSMDNode();
			    		NewRoot.strName = ParseName( strTag.trim() ).toLowerCase();
			    		NewRoot.Attributes = PaseAttributes( strTag.trim() );
			    		NewRoot.ParentNode = ActualRoot; 

			    		ActualRoot.ChildNodes.add( NewRoot );

			    		ActualRoot = NewRoot; //push down

			    		intIndexOffset = intIndexStartCloseTag + strStartCloseTag.length();

			    		intIndexOffsetValue = intIndexOffset;

			    		bMoveIndex = false;

			    	}
			    	else {

			    		break;

			    	}

			    }

			    boolean bParseCloseTag = false;

			    if ( bMoveIndex == false ) {

			    	String strTagClose = strDataToParse.substring( intIndexOffset, intIndexOffset + strEndOpenTag.length() );

			    	bParseCloseTag = strTagClose.equals( strEndOpenTag );

			    }
			    else {

			    	int intIndexTagClose = strDataToParse.indexOf( strEndOpenTag, intIndexOffset );				

			    	bParseCloseTag = intIndexTagClose >= intIndexOffset;

			    	if ( bParseCloseTag ) intIndexOffset = intIndexTagClose;

			    }

			    if ( bParseCloseTag == true ) {

			    	if ( intIndexOffset - intIndexOffsetValue > 0 ) {

			    		String strTagValue = strDataToParse.substring( intIndexOffsetValue, intIndexOffset );

			    		ActualRoot.strValue = strTagValue.trim();

			    		intIndexOffsetValue = intIndexOffset;

			    	}

			    	int intIndexEndCloseTag = strDataToParse.indexOf( strEndCloseTag, intIndexOffset ); 

			    	if ( intIndexEndCloseTag >= intIndexOffset ) {

			    		String strTag = strDataToParse.substring( intIndexOffset + strEndOpenTag.length(), intIndexEndCloseTag + strEndCloseTag.length() - 1 );

			    		if ( ParseName( strTag.trim() ).toLowerCase().equals( ActualRoot.strName ) == false ) {

			    			//unbalanced close tag the name don't match
			    			//remove from child list
			    			if ( ActualRoot.ParentNode != null )  
			    				ActualRoot.ParentNode.ChildNodes.remove( ActualRoot );

			    		}

			    		if ( ActualRoot.ParentNode != null )   
			    			ActualRoot = ActualRoot.ParentNode; //pop up
			    		else
			    			break; //unbalanced close tag

			    		intIndexOffset = intIndexEndCloseTag + strEndCloseTag.length();

			    		intIndexOffsetValue = intIndexOffset;

			    		bMoveIndex = false;

			    	}
			    	else {

			    		break;

			    	}

				}

				if ( bMoveIndex == true ) {

					intIndexOffset += 1;

				}

			}

		}
		
		return GrandRoot.ChildNodes;
		
	}

}
