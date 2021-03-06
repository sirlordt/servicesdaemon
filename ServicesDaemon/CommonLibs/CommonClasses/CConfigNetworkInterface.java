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
package CommonClasses;

import java.util.ArrayList;

public class CConfigNetworkInterface {

	public String strIP;
	public String strAddressType;
	public int intPort;
	public boolean bUseSSL;
	
	public CConfigNetworkInterface() {

		strIP = "127.0.0.1";
		strAddressType = "ipv4";
		intPort = 8080;
		bUseSSL = false;

	}

	public CConfigNetworkInterface( CConfigNetworkInterface ConfigNetworkInterfaceToClone ) {

		strIP = ConfigNetworkInterfaceToClone.strIP;
		strAddressType = ConfigNetworkInterfaceToClone.strAddressType;
		intPort = ConfigNetworkInterfaceToClone.intPort;
		bUseSSL = ConfigNetworkInterfaceToClone.bUseSSL;

	}
	
	public static boolean checkNetAddressExists( ArrayList<CConfigNetworkInterface> NetworkInterfaceList, String strNetAddress ) {
		
		for ( CConfigNetworkInterface ConfigNetworkInterface: NetworkInterfaceList ) {
			
			if ( ConfigNetworkInterface.strIP.toLowerCase().equals( strNetAddress.toLowerCase() ) ) {
				
				return true;
				
			}
			
		}
		
		return false;
	}  
	
}
