package CryptPasswordSD;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import Utilities.Base64;

public class CMain {

	public CMain() {
		// TODO Auto-generated constructor stub
	}

    public static String CryptString( String strStringToCrypt, String strCryptAlgorithm, String strCryptKey ) {
    	
        String strResult = strStringToCrypt;
    	
    	try {
    		
    		DESKeySpec keySpec = new DESKeySpec( strCryptKey.getBytes( "UTF8" ) ); 
    		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( strCryptAlgorithm );
    		SecretKey key = keyFactory.generateSecret( keySpec );
    		
    		byte[] arrClearTextBytes = strStringToCrypt.getBytes( "UTF8" );      

    		Cipher cipher = Cipher.getInstance( strCryptAlgorithm ); // cipher is not thread safe
    		cipher.init( Cipher.ENCRYPT_MODE, key );
    		strResult = new String( Base64.encode( cipher.doFinal( arrClearTextBytes ) ) );
    		
    	}
        catch ( Exception Ex ) {
            

        }
    	
    	return strResult;
    	
    }
   

    public static int StrToInteger( String strStringToConvert ) {
    	
    	int intResult = 0;
    	
    	try {
    		
           intResult = Integer.parseInt( strStringToConvert );
    	
    	}
    	catch ( Exception Ex ) {
    		
    	}
    	
    	return intResult;
    	
    }
    
    
    public static void main(String[] args) {

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
		
		if ( args.length == 2 ) {
			
			int intCryyptKeyIndex = StrToInteger( args[ 0 ] );
			
			if ( intCryyptKeyIndex > 0 && intCryyptKeyIndex < strCryptKeys.length ) {
				
				String strClearPassword = args[ 1 ];
				
				if ( strClearPassword.isEmpty() == false ) {
					
					String strCryptedPassword = CryptString( strClearPassword, "DES", strCryptKeys[ intCryyptKeyIndex ] );
					
					System.out.println( "crypted:" + args[ 0 ] + ":" + strCryptedPassword );
					
				}
				else {
					
					System.out.println( "Error: <ClearPassword> cannot empty string" );
					
				}
				
			}
			else {
				
				System.out.println( "Error: <KeyIndex> must be integer in the range from 1 to 10" );
				
			}
 			
		}
		else {
			
			System.out.println( "Usage: java -jar CryptPassword.jar <KeyIndex> <ClearPassword>" );
			System.out.println( "<KeyIndex> = integer in the range from 1 to 10" );
			System.out.println( "<ClearPassword> = string clear password" );
			System.out.println( "Output: crypted:<KeyIndex>:CryptedPassword" );
			
		}
		
	}

}
