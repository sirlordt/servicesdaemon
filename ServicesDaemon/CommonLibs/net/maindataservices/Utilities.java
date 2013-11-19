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
package net.maindataservices;

import java.io.File;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class Utilities {

	public static Pattern VALID_IPV4_PATTERN = null;
	public static Pattern VALID_IPV6_PATTERN = null;
	public static String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	public static String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
	
	static {
	
		try {
		    
			VALID_IPV4_PATTERN = Pattern.compile( ipv4Pattern, Pattern.CASE_INSENSITIVE );
		    VALID_IPV6_PATTERN = Pattern.compile( ipv6Pattern, Pattern.CASE_INSENSITIVE );
		    
		} 
		catch ( Exception Ex ) {
		
		}
	
	}
	
	public static boolean isValidIPV4( String strIPAddress ) {
		
		Matcher m1 = Utilities.VALID_IPV4_PATTERN.matcher( strIPAddress );

		return m1.matches();
		
	}  

	public static boolean isValidIPV6( String strIPAddress ) {
		
		Matcher m1 = Utilities.VALID_IPV6_PATTERN.matcher( strIPAddress );

		return m1.matches();
		
	}  
	
	public static boolean isValidIP( String strIPAddress ) {
		    
		return isValidIPV4( strIPAddress ) || isValidIPV6( strIPAddress );
	  
	}
	
    public static boolean CheckDir( String strDirToCheck ) {
    	
       return CheckDir( strDirToCheck, null, null );
    
    }
    
    public static boolean CheckDir( String strDirToCheck, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;
    	
	    File DirToCheck = new File( strDirToCheck );

	    try {
	       
	    	if ( DirToCheck.exists() == true ) {
              
	    		if ( DirToCheck.canRead() == true ) {
            	  
	    			if ( DirToCheck.isDirectory() == true ) {
            		 
	    				bResult = true;
            	  
	    			}
	    			else if ( Logger != null ) {

	    				if ( Lang != null )  
	    					Logger.LogError( "-1003", Lang.Translate( "The dir path [%s] not is dir", DirToCheck.getAbsolutePath() ) );        
	    				else
	    					Logger.LogError( "-1003", String.format( "The dir path [%s] not is dir", DirToCheck.getAbsolutePath() ) );        
	    				
	    			}
              
	    		} 
	    		else if ( Logger != null ) {

	    			if ( Lang != null )  
	    				Logger.LogError( "-1002", Lang.Translate( "The dir in the path [%s] cannot read, please check the owner and permissions", DirToCheck.getAbsolutePath() ) );
	    			else
	    				Logger.LogError( "-1002", String.format( "The dir in the path [%s] cannot read, please check the owner and permissions", DirToCheck.getAbsolutePath() ) );
              
	    		}
	       
	    	}
	       
	    	else if ( Logger != null ) {

	    		if ( Lang != null )  
	    		    Logger.LogError( "-1001", Lang.Translate( "The dir in the path [%s] not exists", DirToCheck.getAbsolutePath() ) );        
	    		else
	    		    Logger.LogError( "-1001", String.format( "The dir in the path [%s] not exists", DirToCheck.getAbsolutePath() ) );        
					
	    	}
	   
	    }
	    catch ( Exception Ex ) {
		
			if ( Logger != null )   
	    	    Logger.LogException( "-1000", Ex.getMessage(), Ex );        
	  
	    }
	   
	    return bResult;
    	
    }
	
    public static boolean CheckFile( String strFileToCheck ) {

    	return CheckFile( strFileToCheck, null, null );
    
    }
    	
    public static boolean CheckFile( String strFileToCheck, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;
    	
	    File FileToCheck = new File( strFileToCheck );

	    try {
	       
	    	if ( FileToCheck.exists() == true ) {
              
	    		if ( FileToCheck.canRead() == true ) {
            	  
	    			if ( FileToCheck.isFile() == true ) {
            		 
	    				bResult = true;
            	  
	    			}
	    			else if ( Logger != null ) {
            		
	    				if ( Lang != null )   
	    				    Logger.LogError( "-1003", Lang.Translate( "The file path [%s] not is dir", FileToCheck.getAbsolutePath() ) );        
	    				else    
	    				    Logger.LogError( "-1003", String.format( "The file path [%s] not is dir", FileToCheck.getAbsolutePath() ) );        
	    				
	    			}
              
	    		} 
	    		else if ( Logger != null ) {
            	
    				if ( Lang != null )   
	    			    Logger.LogError( "-1002", Lang.Translate( "The file in the path [%s] cannot read, please check the owner and permissions", FileToCheck.getAbsolutePath() ) );
    				else
	    			    Logger.LogError( "-1002", String.format( "The file in the path [%s] cannot read, please check the owner and permissions", FileToCheck.getAbsolutePath() ) );
              
	    		}
	       
	    	}
	    	else if ( Logger != null ) {
	    	   
				if ( Lang != null )   
	    		   Logger.LogError( "-1001", Lang.Translate( "The file in the path [%s] not exists", FileToCheck.getAbsolutePath() ) );        
				else
		    	   Logger.LogError( "-1001", String.format( "The file in the path [%s] not exists", FileToCheck.getAbsolutePath() ) );        
				
	    	}
	   
	    }
	    catch ( Exception Ex ) {
		
			if ( Logger != null )   
	    	   Logger.LogException( "-1000", Ex.getMessage(), Ex );        
	  
	    }
	   
	    return bResult;
    	
    }

    public static int StrToInteger( String strStringToConvert ) {
    
    	return StrToInteger( strStringToConvert, null );
    
    }
    
    public static int StrToInteger( String strStringToConvert, CExtendedLogger Logger ) {
    	
    	int intResult = 0;
    	
    	try {
    		
           intResult = Integer.parseInt( strStringToConvert );
    	
    	}
    	catch ( Exception Ex ) {
    		
    		if ( Logger != null )   
    	       Logger.LogException( "-1015", Ex.getMessage(), Ex );        
    		
    	}
    	
    	return intResult;
    	
    }
    
    public static boolean CheckStringIsInteger( String strStringToTest, CExtendedLogger Logger ) {
    	
    	boolean bResult = false;
    	
    	try {
    		
           Integer.parseInt( strStringToTest );
           
           bResult = true;
    	
    	}
    	catch ( Exception Ex ) {
    		
    		if ( Logger != null )   
    	       Logger.LogException( "-1015", Ex.getMessage(), Ex );        
    		
    	}
    	
    	return bResult;
    	
    }
    
	public static int CompareVersions( String strVersion1, String strVersion2 ) {
		
		CVersionTokenizer Tokenizer1 = new CVersionTokenizer( strVersion1 );
		CVersionTokenizer Tokenizer2 = new CVersionTokenizer( strVersion2 );

		int intNumber1 = 0; 
		String strSuffix1 = "";

		int intNumber2 = 0;
		String strSuffix2 = "";

		while ( Tokenizer1.MoveNext() ) {
			
			if ( Tokenizer2.MoveNext() == false ) {
				
				do {
				
					intNumber1 = Tokenizer1.getNumber();
					strSuffix1 = Tokenizer1.getSuffix();
					
					if ( intNumber1 != 0 || strSuffix1.length() != 0 ) {
						
						// Version one is longer than number two, and non-zero
						return 1;
						
					}
					
				} while ( Tokenizer1.MoveNext() );

				// Version one is longer than version two, but zero
				return 0;
			
			}

			intNumber1 = Tokenizer1.getNumber();
			strSuffix1 = Tokenizer1.getSuffix();
			
			intNumber2 = Tokenizer2.getNumber();
			strSuffix2 = Tokenizer2.getSuffix();

			if ( intNumber1 < intNumber2 ) {

				// Number one is less than number two
				return -1;
				
			}
			
			if ( intNumber1 > intNumber2 ) {
				
				// Number one is greater than number two
				return 1;
				
			}

			boolean bEmpty1 = strSuffix1.length() == 0;
			boolean bEmpty2 = strSuffix2.length() == 0;

			if ( bEmpty1 && bEmpty2 )
				continue; // No suffixes
			
			if ( bEmpty1 )
				return 1; // First suffix is empty (1.2 > 1.2b)
			
			if ( bEmpty2 )
				return -1; // Second suffix is empty (1.2a < 1.2)

			// Lexical comparison of suffixes
			int intResult = strSuffix1.compareTo( strSuffix2 );
			
			if ( intResult != 0 )
				return intResult;

		}

		if ( Tokenizer2.MoveNext() ) {
			
			do {
			
				intNumber2 = Tokenizer2.getNumber();
				strSuffix2 = Tokenizer2.getSuffix();
				
				if ( intNumber2 != 0 || strSuffix2.length() != 0 ) {
				
					// Version one is longer than version two, and non-zero
					return -1;
					
				}
				
			} while ( Tokenizer2.MoveNext() );

			// Version two is longer than version one, but zero
			return 0;
			
		}
		
		return 0;
		
	}
	
	public static boolean VersionGreaterEquals( String strVersion1, String strVersion2 ) {
		
		int intResult = CompareVersions( strVersion1, strVersion2 );
		
		return intResult >= 0;
		
	}  

	public static boolean VersionLessEquals( String strVersion1, String strVersion2 ) {
		
		int intResult = CompareVersions( strVersion1, strVersion2 );
		
		return intResult <= 0;
		
	}  
	
	public static ArrayList<String> ParseTokensByTags( String strStartTag, String strEndTag, String strTagContained, boolean bIgnoreDuplicated, boolean bForceLowerCase ) {

		ArrayList<String> ResultListTokens = new ArrayList<String>();
		
		int intStartTagIndex = 0;
		
		while ( intStartTagIndex >= 0 && intStartTagIndex < strTagContained.length() ) {
			
			intStartTagIndex = strTagContained.indexOf( strStartTag, intStartTagIndex );
			
			if ( intStartTagIndex >= 0 ) {
				
				int intEndTagIndex = strTagContained.indexOf( strEndTag, intStartTagIndex );;
								
				if ( intEndTagIndex > intStartTagIndex ) {

					String strTagToAdd = strTagContained.substring( intStartTagIndex + strStartTag.length(), intEndTagIndex );
					
					if ( bForceLowerCase == true )
						strTagToAdd = strTagToAdd.toLowerCase();
					
					if ( bIgnoreDuplicated == false || ResultListTokens.contains( strTagToAdd ) == false )     
					    ResultListTokens.add( strTagToAdd );
					
					intStartTagIndex = intEndTagIndex + strEndTag.length();
					
				}
				else {
					
					break;
					
				}
				
			}
			
		}
		
		return ResultListTokens;
		
	} 
	
	public static String GenerateString( String strCharacters, int intLength ) {
	
		Random RandomGenerator = new Random();
		
		return GenerateString( RandomGenerator, strCharacters, intLength );
		
	}
	
    public static String HashCrypt( String StringToCryptHash, String strHashAlgorithm, CExtendedLogger Logger ) {
    	   
        MessageDigest MsgDigest;

        try {
            
            MsgDigest = MessageDigest.getInstance( strHashAlgorithm );  //"SHA-512");

            MsgDigest.update( StringToCryptHash.getBytes() );
            
            byte[] MsgBytes = MsgDigest.digest();
            
            String strOut = "";
            
            for ( int i = 0; i < MsgBytes.length; i++ ) {
                
               byte bytTemp = MsgBytes[ i ];

               String S = Integer.toHexString( new Byte( bytTemp ) );
                
               while ( S.length() < 2 ) {

                  S = "0" + S;

               }
                
               S = S.substring( S.length() - 2 );
                
               strOut += S;

            }

            return strOut;

        } 
        catch ( Exception Ex ) {
            
    		if ( Logger != null )   
     	       Logger.LogException( "-1015", Ex.getMessage(), Ex );        

        }

        return "";

    }

	public static boolean IsValidDateTimeFormat( String strDateTimeFormat, CExtendedLogger Logger ) {
		
		boolean bResult = false;
		
		try {
		
			SimpleDateFormat DTFormatter = new SimpleDateFormat( strDateTimeFormat );
			//SimpleDateFormat TFormatter = new SimpleDateFormat("HHmmss");
			//SimpleDateFormat DTFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");
			//DateTimeFormat x = new DateTim

			Date date = new Date();
			
			String strTmp = DTFormatter.format( date );
			
			return strTmp != null && strTmp.isEmpty() == false;
			
		}
		catch ( Exception Ex ) {
			
    		if ( Logger != null )   
      	       Logger.LogException( "-1015", Ex.getMessage(), Ex );        
			
		}
		
		return bResult;
		
	}
    
	public static String GenerateString( Random RandomGenerator, String strCharacters, int intLength ) {
	    
		char[] strText = new char[ intLength ];
	
	    for (int i = 0; i < intLength; i++) {

	    	strText[i] = strCharacters.charAt( RandomGenerator.nextInt( strCharacters.length() ) );
	    
	    }
	    
	    return new String(strText);
	    
	}	

    public static String CryptString( String strStringToCrypt, String strCryptAlgorithm, String strCryptKey, CExtendedLogger Logger ) {
    	
        String strResult = strStringToCrypt;
    	
    	try {
    		
    		DESKeySpec keySpec = new DESKeySpec( strCryptKey.getBytes( "UTF8" ) ); 
    		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( strCryptAlgorithm );
    		SecretKey key = keyFactory.generateSecret(keySpec);
    		
    		byte[] arrClearTextBytes = strStringToCrypt.getBytes( "UTF8" );      

    		Cipher cipher = Cipher.getInstance( strCryptAlgorithm ); // cipher is not thread safe
    		cipher.init( Cipher.ENCRYPT_MODE, key );
    		strResult = new String( Base64.encode( cipher.doFinal( arrClearTextBytes ) ) );
    		
    	}
        catch ( Exception Ex ) {
            
    		if ( Logger != null )   
     	       Logger.LogException( "-1015", Ex.getMessage(), Ex );        

        }
    	
    	return strResult;
    	
    }
    
    public static String UncryptString( String strStringToUncrypt, String strUncryptAlgorithm, String strUncryptKey, CExtendedLogger Logger ) {
    	
        String strResult = strStringToUncrypt;
    	
    	try {
    		
    		DESKeySpec keySpec = new DESKeySpec( strUncryptKey.getBytes( "UTF8" ) ); 
    		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( strUncryptAlgorithm );
    		SecretKey key = keyFactory.generateSecret( keySpec );
    		
    		byte[] arrCryptedBytes = strStringToUncrypt.getBytes( "UTF8" );      

    		Cipher cipher = Cipher.getInstance( strUncryptAlgorithm ); // cipher is not thread safe
    		cipher.init( Cipher.DECRYPT_MODE, key );
    		strResult = new String( cipher.doFinal( Base64.decode( arrCryptedBytes ) ) );
    		
    	}
        catch ( Exception Ex ) {
            
    		if ( Logger != null )   
     	       Logger.LogException( "-1015", Ex.getMessage(), Ex );        

        }
    	
    	return strResult;
    	
    }
    
    public static int getIndexByValue( String[] strStringArray, String strValue ) {
    	
    	strValue = strValue.toLowerCase();
    	
        for ( int intIndex = 0; intIndex < strStringArray.length; intIndex++ ) {
     	   
     	   if ( strStringArray[ intIndex ].toLowerCase().equals( strValue ) ) {
     		
     		   return intIndex;
     		   
     	   }
     	   
        }
        
        return -1;
     	
     	
     }
 
    public static int countSubString( String strToFind, String strSearch ) {

    	 try {

    		 int intCount = 0;

    		 for ( int intFromIndex = 0; intFromIndex > -1; intCount++ ) {
    		
    			 intFromIndex = strToFind.indexOf( strSearch, intFromIndex + ( ( intCount > 0 ) ? 1 : 0 ) );
    		 
    		 }	 

    		 return intCount - 1;
    		 
    	 }
    	 catch ( Exception Ex ) {

        	 return -1;
        	 
    	 }

     }
   
	public static String UncryptString( String strPasswordCrypted, String strPasswordCryptedSep, String strDefaultCryptAlgorithm, String strCryptedPassword, CExtendedLogger Logger, CLanguage Lang ) {
		
		String strResult = strCryptedPassword;
		
		int intPassCryptedLength = strPasswordCrypted.length(); //ConfigXMLTagsDBServicesManager._Password_Crypted.length();
		int intPassCryptedSepLength = strPasswordCryptedSep.length(); //ConfigXMLTagsDBServicesManager._Password_Crypted_Sep.length();
		
		if ( strCryptedPassword.length() > intPassCryptedLength && strResult.substring( 0, intPassCryptedLength ).equals( strPasswordCrypted) ) {

			strResult =  strResult.substring( intPassCryptedLength + intPassCryptedSepLength, strResult.length() );
			
			String strCryptKeys[] = { 
					                  "xfzm29dp",
					                  "6m3m7xa5",
			                          "e48c4xyi",		                  
			                          "6we7og02",		                  
			                          "4m7gypao",		                  
			                          "hy6z2m0x",		                  
			                          "2zx6kynd",		                  
			                          "1k9c0666",		                  
			                          "q3f5i11j",		                  
			                          "4y84x0j7"		                  
			                        };

			int intIndexPassSep = strResult.indexOf( strPasswordCryptedSep, 0 );
			
			String strCryptKeyIndex =  strResult.substring( 0, intIndexPassSep );
			
			int intCryptKeyIndex = Utilities.StrToInteger( strCryptKeyIndex );
			
			if ( intCryptKeyIndex > 0 && intCryptKeyIndex <= strCryptKeys.length ) {
				
				strResult =  strResult.substring( intIndexPassSep + intPassCryptedSepLength, strResult.length() );
				
				//DefaultConstantsSystemStartSession.strDefaultCryptAlgorithm
				strResult = Utilities.UncryptString( strResult, strDefaultCryptAlgorithm, strCryptKeys[ intCryptKeyIndex ], Logger );
				
				
			}
			else {
				
		    	Logger.LogError( "-1001", Lang.Translate( "The crypt key index [%s] is not valid", strCryptKeyIndex ) );        
				
			}
			
		}
		else {
			
	    	Logger.LogWarning( "-1", Lang.Translate( "Using clear text password" ) );        
			
		}
		
		return strResult;
		
	}
 
	public static String ReplaceToHTMLEntity( String strToFindAndReplace ) {
		
        strToFindAndReplace = strToFindAndReplace.replaceAll( "&", "&amp;" );
        strToFindAndReplace = strToFindAndReplace.replaceAll( "<", "&lt;" );
        strToFindAndReplace = strToFindAndReplace.replaceAll( ">", "&gt;" );
        strToFindAndReplace = strToFindAndReplace.replaceAll( "\"", "&quot;" );
        strToFindAndReplace = strToFindAndReplace.replaceAll( "\'", "&apos;" );
		
		return strToFindAndReplace;
		
	}
	
}
