CREATE DATABASE PGSQLTestDB WITH OWNER postgres TEMPLATE template0 ENCODING 'UTF8' TABLESPACE  pg_default LC_COLLATE 'C' LC_CTYPE 'C' CONNECTION LIMIT  -1;
   
\c pgsqltestdb

CREATE SEQUENCE GEN_TBLUSERSDB_ID;

CREATE TABLE tblUsersDB( iduser INT NOT NULL, idgroup INT NOT NULL, disabled SMALLINT NOT NULL, username VARCHAR(50) NOT NULL, passwd VARCHAR(50) NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50), access_date DATE, access_time TIME, PRIMARY KEY (iduser) );

CREATE SEQUENCE GEN_TBLUSERSENG_ID;

CREATE TABLE tblUsersEng( iduser INT NOT NULL, idgroup INT NOT NULL, disabled SMALLINT NOT NULL, username VARCHAR(50) NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50), access_date DATE, access_time TIME, PRIMARY KEY (iduser) );

CREATE SEQUENCE GEN_TBLGROUPS_ID;

CREATE TABLE tblGroups( idgroup INT NOT NULL, description VARCHAR(50) NOT NULL, PRIMARY KEY (idgroup) );

ALTER TABLE TBLUsersDB ADD CONSTRAINT FK_TBLUSERSDB_1 FOREIGN KEY (IDGROUP) REFERENCES TBLGROUPS (IDGROUP) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE TBLUsersENG ADD CONSTRAINT FK_TBLUSERSENG_1 FOREIGN KEY (IDGROUP) REFERENCES TBLGROUPS (IDGROUP) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE SEQUENCE GEN_TBLGENERICDATA_ID;

ALTER SEQUENCE GEN_TBLGENERICDATA_ID RESTART WITH 3;

CREATE TABLE tblGenericData( id INT, data1 VARCHAR(100), data2 VARCHAR(100), PRIMARY KEY (id) );

CREATE SEQUENCE GEN_TBLBLOBDATA_ID;

CREATE TABLE tblBlobData( id INT, blobdata BYTEA, PRIMARY KEY (id) );

CREATE FUNCTION F_TBLUSERSDB_BI() RETURNS TRIGGER AS $F_TBLUSERSDB_BI$ 
DECLARE
BEGIN
   
   if ( NEW.iduser is null ) then
      NEW.iduser := nextval( 'GEN_TBLUSERSDB_ID' );
   else
      Perform nextval( 'GEN_TBLUSERSDB_ID' );
   end if;
  
   return NEW;
   
END;
$F_TBLUSERSDB_BI$ LANGUAGE plpgsql;

CREATE TRIGGER TBLUSERSDB_BI BEFORE INSERT ON tblUsersDB FOR EACH ROW EXECUTE PROCEDURE F_TBLUSERSDB_BI();

CREATE FUNCTION F_TBLUSERSENG_BI() RETURNS TRIGGER AS $F_TBLUSERSENG_BI$ 
DECLARE
BEGIN
   
   if ( NEW.iduser is null ) then
      NEW.iduser := nextval( 'GEN_TBLUSERSENG_ID' );
   else
      Perform nextval( 'GEN_TBLUSERSENG_ID' );
   end if;
  
   return NEW;
   
END;
$F_TBLUSERSENG_BI$ LANGUAGE plpgsql;

CREATE TRIGGER TBLUSERSENG_BI BEFORE INSERT ON tblUsersEng FOR EACH ROW EXECUTE PROCEDURE F_TBLUSERSENG_BI(); 

CREATE FUNCTION F_TBLGROUPS_BI() RETURNS TRIGGER AS $F_TBLGROUPS_BI$ 
DECLARE
BEGIN
   
   if ( NEW.idgroup is null ) then
      NEW.idgroup := nextval( 'GEN_TBLGROUPS_ID' );
   end if;
  
   return NEW;
   
END;
$F_TBLGROUPS_BI$ LANGUAGE plpgsql;

CREATE TRIGGER TBLGROUPS_BI BEFORE INSERT ON tblGroups FOR EACH ROW EXECUTE PROCEDURE F_TBLGROUPS_BI(); 

CREATE FUNCTION F_TBLGENERICDATA_BI() RETURNS TRIGGER AS $F_TBLGENERICDATA_BI$ 
DECLARE
BEGIN
   
   if ( NEW.id is null ) then
      NEW.id := nextval( 'GEN_TBLGENERICDATA_ID' );
   else
      Perform nextval( 'GEN_TBLGENERICDATA_ID' );
   end if;
  
   return NEW;
   
END;
$F_TBLGENERICDATA_BI$ LANGUAGE plpgsql;

CREATE TRIGGER TBLGENERICDATA_BI BEFORE INSERT ON tblGenericData FOR EACH ROW EXECUTE PROCEDURE F_TBLGENERICDATA_BI();

CREATE FUNCTION F_TBLBLOBDATA_BI() RETURNS TRIGGER AS $F_TBLBLOBDATA_BI$ 
DECLARE
BEGIN
   
   if ( NEW.id is null ) then
      NEW.id := nextval( 'GEN_TBLBLOBDATA_ID' );
   else
      Perform nextval( 'GEN_TBLBLOBDATA_ID' );
   end if;
  
   return NEW;
   
END;
$F_TBLBLOBDATA_BI$ LANGUAGE plpgsql;

CREATE TRIGGER TBLBLOBDATA_BI BEFORE INSERT ON tblBlobData FOR EACH ROW EXECUTE PROCEDURE F_TBLBLOBDATA_BI(); 

CREATE TYPE SPCHECKDBUSER_RECORD AS (
    IDVALID int,
    IDUSER int,
    IDGROUP int,
    LASTACCESSDATE date,
    LASTACCESSTIME time
);

create Function SPCHECKDBUSER( Param_USERNAME varchar(75), Param_PASSWD varchar(150) ) RETURNS SPCHECKDBUSER_RECORD AS $SPCHECKDBUSER$
DECLARE
    result_record SPCHECKDBUSER_RECORD;
    DISABLED smallint;
    CURRENT_PASSWD varchar(50);
BEGIN

  Select A.IdUser, A.IdGroup, A.Passwd, A.Disabled, A.access_date, A.access_time Into result_record.IdUser, result_record.IdGroup, Current_Passwd, Disabled, result_record.LastAccessDate, result_record.LastAccessTime From tblUsersDB A Where A.Username = Param_Username; 
	
  if ( result_record.IDUser Is Not null ) then

     if ( Current_Passwd = Param_Passwd ) then

        if ( Disabled = 0 Or Disabled Is Null ) then

           result_record.IdValid := 1;   /*Valid*/

           Update tblUsersDB Set Access_date = now()::date, Access_time = now()::time Where IdUser = result_record.IdUser; /*Update the last access date and time*/

        else

            result_record.IdUser := -1;
            result_record.IdGroup := -1;
            
            result_record.IdValid := -2;  /*Disabled*/

        end if;

     else

         result_record.IdUser = -1;
         result_record.IdGroup = -1;

         result_record.IdValid = -1; /*Invalid password*/

     end if;

  else

      result_record.IdUser = -1;
      result_record.IdGroup = -1;

      result_record.IdValid = -3; /*Not found*/

  end if;
  
  return result_record;
  
END;
$SPCHECKDBUSER$ LANGUAGE plpgsql;

create Function SPGETGROUPS ( FORIDGROUP int ) RETURNS SETOF tblGroups AS $$
DECLARE
BEGIN
	
	RETURN QUERY Select A.IdGroup, A.Description from tblGroups A Where ForIdGroup Is Null Or A.IdGroup = ForIdGroup;
	
END; 
$$ LANGUAGE plpgsql;

create Function SPSETGROUPS ( IDGROUP integer, DESCRIPTION varchar(50) ) RETURNS VOID AS $$
DECLARE
BEGIN

	Insert Into tblGroups(IdGroup,Description) Values(IdGroup,Description);
	
END;
$$ LANGUAGE plpgsql;
 
/** Insert basic data **/

insert into tblGroups( idgroup, description ) values( null, 'Regular user group' );

insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test1', '123qwerty', 'System user firstname', 'System user lastname', null, null );
insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test2', '12345678', null, null, null, null );

insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'sysdba', 'Default system user for Firebird', 'System user lastname', null, null );
insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'root', 'Default system user for MySQL', null, null, null );

insert into tblGenericData( id, data1, data2 ) Values( 1, 'DataA1', 'DataA2' );
insert into tblGenericData( id, data1, data2 ) Values( 2, 'DataB1', 'DataB2' );
insert into tblGenericData( id, data1, data2 ) Values( 3, 'DataC1', 'DataC2' );

 