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
package AbstractService;

import CommonClasses.NamesSQLTypes;

public class CInputServiceParameter {

       public enum TParameterScope { IN, OUT, INOUT };
	
	   protected String  strParameterName;
	   protected boolean bParameterRequired;
	   protected String  strParameterDataType; //VarChar, Int
	   protected int intParameterDataTypeID;
	   protected String  strParameterDataTypeWidth; //100
	   protected TParameterScope ParameterScope; //IN, OUT, INTOUT
	   protected String  strParameterDescription;

	   protected final void InitParameter() {
	       
	      this.strParameterName          = "BaseParameter";
	      this.bParameterRequired        = true;
	      this.strParameterDataType      = "";
	      this.intParameterDataTypeID    = 0;
	      this.strParameterDataTypeWidth = "0";
	      this.ParameterScope            = TParameterScope.IN;  ////IN, OUT, INTOUT
	      this.strParameterDescription   = "Parámetro";
	       
	   }

	   public CInputServiceParameter() {

		   this.InitParameter();

	   }
		
	   public CInputServiceParameter( String strParameterName, boolean bParameterRequired, String strParameterDataType, String strParameterDataTypeWidth, TParameterScope ParameterScope, String strParameterDescription ) {

	      this.InitParameter();
	      this.strParameterName           = strParameterName;
	      this.bParameterRequired         = bParameterRequired;
	      this.strParameterDataType       = strParameterDataType; 
	      this.strParameterDataTypeWidth  = strParameterDataTypeWidth;
          this.ParameterScope             = ParameterScope;  //IN, OUT, INOUT
	      this.strParameterDescription    = strParameterDescription;

	      this.intParameterDataTypeID =  NamesSQLTypes.ConvertToJavaSQLType( this.strParameterDataType );
	      
	      /*if ( this.strParameterType.equals( NamesSQLTypes.strSQL_VARCHAR ) ) {

	    	  this.intParameterTypeID = Types.VARCHAR;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_CHAR ) ) {

	    	  this.intParameterTypeID = Types.CHAR;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_INTEGER ) ) {

	    	  this.intParameterTypeID = Types.INTEGER;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_SMALLINT ) ) {

	    	  this.intParameterTypeID = Types.SMALLINT;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_BIGINT ) ) {

	    	  this.intParameterTypeID = Types.BIGINT;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_DATE ) ) {

	    	  this.intParameterTypeID = Types.DATE;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_TIME ) ) {

	    	  this.intParameterTypeID = Types.TIME;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_TIMESTAMP ) ) {

	    	  this.intParameterTypeID = Types.TIMESTAMP;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_BLOB ) ) {

	    	  this.intParameterTypeID = Types.BLOB;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_FLOAT ) || this.strParameterType.equals( NamesSQLTypes.strSQL_NUMERIC ) || this.strParameterType.equals( NamesSQLTypes.strSQL_CURRENCY ) || this.strParameterType.equals( NamesSQLTypes.strSQL_MONEY ) ) {

	    	  this.intParameterTypeID = Types.FLOAT;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_DECIMAL ) ) {

	    	  this.intParameterTypeID = Types.DECIMAL;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_DOUBLE ) ) {

	    	  this.intParameterTypeID = Types.DOUBLE;

	      }
	      else if ( this.strParameterType.equals( NamesSQLTypes.strSQL_BOOLEAN ) ) {

	    	  this.intParameterTypeID = Types.BOOLEAN;

	      }*/
	   
	   }

	   public String getParameterName() {

	      return this.strParameterName;

	   }

	   public boolean getParameterRequired() {

	      return this.bParameterRequired;

	   }

	   public String getParameterDataType() {

	      return this.strParameterDataType;

	   }

	   public int getParameterDataTypeID() {

		  return this.intParameterDataTypeID;

	   }
	   
	   public String getParameterDataTypeWidth() {

	      return this.strParameterDataTypeWidth;

	   }

	   public TParameterScope getParameterScope() {

	      return this.ParameterScope;

	   }
	   
	   public static TParameterScope parseParameterScope( String strName ) {
		   
		   strName = strName.toLowerCase();
		   
		   if ( strName.equals( "out" ) ) {
			   
			   return TParameterScope.OUT;
			   
		   }
		   else if ( strName.equals( "inout" ) ) {
			   
			   return TParameterScope.INOUT;
			   
		   }
		   
		   return TParameterScope.IN;
		   
	   }

	   public String getParameterDescription() {

	      return this.strParameterDescription;

	   }

}
