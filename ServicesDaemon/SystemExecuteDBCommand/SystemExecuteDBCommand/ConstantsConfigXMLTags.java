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
package SystemExecuteDBCommand;

public interface ConstantsConfigXMLTags {

	public static final String _PreExecute_Dir = "PreExecute_Dir";
	public static final String _PostExecute_Dir = "PostExecute_Dir";

	public static final String _Logger = "Logger";
	public static final String _LogSQL_Statement = "LogSQL_Statement";
	
	public static final String _Replicators = "Replicators";
	public static final String _Replicator = "Replicator";
	public static final String _Source_DBConnection_Name = "Source_DBConnection_Name";
	public static final String _Max_Store_File_Size = "Max_Store_File_Size";
	public static final String _On_Fail_Go_Sleep_For = "On_Fail_Go_Sleep_For";

	public static final long _Min_Store_File_Size = 10240; //10 kb
	public static final long _Store_File_Size = 153600;  //150 kb

	public static final long _Min_Fail_Sleep_Millis = 10000; //10 Seconds
	public static final long _Fail_Sleep_Millis = 300000;  //5 Minutes on millis 60*5*1000
	
	public static final String _Channels = "Channels";
	public static final String _Channel = "Channel";
	
	
	
}
