SET SQL DIALECT 3;

SET NAMES UTF8;

CREATE DATABASE 'localhost:FirebirdTestDB' USER 'SYSDBA' PASSWORD 'masterkey' PAGE_SIZE 16384 DEFAULT CHARACTER SET UTF8;

CREATE GENERATOR GEN_TBLUSERSDB_ID;

CREATE TABLE tblUsersDB( iduser INT NOT NULL, idgroup INT NOT NULL, disabled SMALLINT NOT NULL, username VARCHAR(50) NOT NULL, passwd VARCHAR(50) NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50), access_date DATE, access_time TIME, PRIMARY KEY (iduser) );

CREATE GENERATOR GEN_TBLUSERSENG_ID;

CREATE TABLE tblUsersEng( iduser INT NOT NULL, idgroup INT NOT NULL, disabled SMALLINT NOT NULL, username VARCHAR(50) NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50), access_date DATE, access_time TIME, PRIMARY KEY (iduser) );

CREATE GENERATOR GEN_TBLGROUPS_ID;

CREATE TABLE tblGroups( idgroup INT NOT NULL, description VARCHAR(50) NOT NULL, PRIMARY KEY (idgroup) );

ALTER TABLE TBLUsersDB ADD CONSTRAINT FK_TBLUSERSDB_1 FOREIGN KEY (IDGROUP) REFERENCES TBLGROUPS (IDGROUP) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE TBLUsersENG ADD CONSTRAINT FK_TBLUSERSENG_1 FOREIGN KEY (IDGROUP) REFERENCES TBLGROUPS (IDGROUP) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE GENERATOR GEN_TBLGENERICDATA_ID;

CREATE TABLE tblGenericData( id INT, data1 VARCHAR(100), data2 VARCHAR(100), PRIMARY KEY (id) );

CREATE GENERATOR GEN_TBLBLOBDATA_ID;

CREATE TABLE tblBlobData( id INT, blobdata blob, PRIMARY KEY (id) );

set term ^ ;

CREATE TRIGGER TBLUSERSDB_BI FOR tblUsersDB ACTIVE BEFORE INSERT
as
begin

  if ( NEW.iduser is null ) then
     NEW.iduser = gen_id( GEN_TBLUSERSDB_ID, 1 );

end ^

set term ; ^

set term ^ ;

CREATE TRIGGER TBLUSERSENG_BI FOR tblUsersEng ACTIVE BEFORE INSERT
as
begin

  if ( NEW.iduser is null ) then
     NEW.iduser = gen_id( GEN_TBLUSERSENG_ID, 1 );

end ^

set term ; ^

set term ^ ;

CREATE TRIGGER TBLGROUPS_BI FOR tblGroups ACTIVE BEFORE INSERT
as
begin

  if ( NEW.idgroup is null ) then
     NEW.idgroup = gen_id( GEN_TBLGROUPS_ID, 1 );

end ^

set term ; ^

set term ^ ;

CREATE TRIGGER TBLGENERICDATA_BI FOR tblGenericData ACTIVE BEFORE INSERT
as
begin

  if ( NEW.id is null ) then
     NEW.id = gen_id( GEN_TBLGENERICDATA_ID, 1 );

end ^

set term ; ^

set term ^ ;

CREATE TRIGGER TBLBLOBDATA_BI FOR tblBlobData ACTIVE BEFORE INSERT
as
begin

  if ( NEW.id is null ) then
     NEW.id = gen_id( GEN_TBLBLOBDATA_ID, 1 );

end ^ 

set term ; ^

set term ^ ;

create or alter procedure SPCHECKDBUSER (
    USERNAME varchar(75),
    PASSWD varchar(150))
returns (
    IDVALID integer,
    IDUSER integer,
    IDGROUP integer,
    LASTACCESSDATE date,
    LASTACCESSTIME time)
as
declare variable DISABLED smallint;
declare variable CURRENT_PASSWD varchar(50);
begin

  Select A.IdUser, A.IdGroup, A.Passwd, A.Disabled, A.access_date, A.access_time From tblUsersDB A Where A.Username = :Username Into :IdUser, :IdGroup, :Current_Passwd, :Disabled, :LastAccessDate, :LastAccessTime;

  if ( IDUser Is Not null ) then
  begin 

     if ( Current_Passwd = Passwd ) then
     begin

        if ( Disabled = 0 Or Disabled Is Null ) then
        begin

           IdValid = 1;   /*Valid*/

           Update tblUsersDB A Set A.access_date = current_date, A.Access_time = current_time Where A.IdUser = :IdUser; /*Update the last access date and time*/

        end
        else
        begin

           IdValid = -2;  /*Disabled*/

        end

     end
     else
     begin

        IdValid = -1; /*Invalid password*/

     end

  end
  else
  begin

     IdValid = -3; /*Not found*/

  end

  suspend;

end ^

set term ; ^


/** Insert basic data **/

insert into tblGroups( idgroup, description ) values( null, 'Regular user group' );

insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test1', '123qwerty', 'System user firstname', 'System user lastname', null, null );
insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test2', '12345678', null, null, null, null );

insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'sysdba', 'Default system user for Firebird', 'System user lastname', null, null );
insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'root', 'Default system user for MySQL', null, null, null );

insert into tblGenericData( id, data1, data2 ) Values( 1, 'DataA1', 'DataA2' );
insert into tblGenericData( id, data1, data2 ) Values( 2, 'DataB1', 'DataB2' );
insert into tblGenericData( id, data1, data2 ) Values( 3, 'DataC1', 'DataC2' );

