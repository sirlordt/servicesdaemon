create database MySQLTestDB;

use MySQLTestDB;

CREATE TABLE tblUsersDB( iduser INT NOT NULL AUTO_INCREMENT, idgroup INT NOT NULL, disabled SMALLINT NOT NULL, username VARCHAR(50) NOT NULL, passwd VARCHAR(50) NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50), access_date DATE, access_time TIME, PRIMARY KEY (iduser) );

CREATE TABLE tblUsersEng( iduser INT NOT NULL AUTO_INCREMENT, idgroup INT NOT NULL, disabled SMALLINT NOT NULL, username VARCHAR(50) NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50), access_date DATE, access_time TIME, PRIMARY KEY (iduser) );

CREATE TABLE tblGroups( idgroup INT NOT NULL AUTO_INCREMENT, description VARCHAR(50) NOT NULL, PRIMARY KEY (idgroup) );

CREATE TABLE tblGenericData( id INT, data1 VARCHAR(100), data2 VARCHAR(100), PRIMARY KEY (id) );

CREATE TABLE tblBlobData( id INT, blobdata blob, PRIMARY KEY (id) );

DELIMITER $$

CREATE PROCEDURE SPCHECKDBUSER( IN USERNAME VARCHAR(75), IN PASSWD VARCHAR(150), OUT IDVALID integer, OUT IDUSER integer, OUT IDGROUP integer, OUT LASTACCESSDATE date, OUT LASTACCESSTIME time )
BEGIN
	
  DECLARE DISABLED SMALLINT;
  DECLARE CURRENT_PASSWD VARCHAR(50);
  
  Set IdUser = -1;
  Set IdGroup = -1;
	
  Select A.IdUser, A.IdGroup, A.Passwd, A.Disabled, A.access_date, A.access_time From tblUsersDB A Where A.Username = Username Into IdUser, IdGroup, Current_Passwd, Disabled, LastAccessDate, LastAccessTime;
	
  if ( IDUser Is Not null ) then

     if ( Current_Passwd = Passwd ) then

        if ( Disabled = 0 Or Disabled Is Null ) then

           Set IdValid = 1;   /*Valid*/

           Update tblUsersDB A Set A.access_date = current_date, A.Access_time = current_time Where A.IdUser = IdUser; /*Update the last access date and time*/

        else

           Set IdUser = -1;
           Set IdGroup = -1;
           Set IdValid = -2;  /*Disabled*/

        end if;

     else

        Set IdUser = -1;
        Set IdGroup = -1;
        Set IdValid = -1; /*Invalid password*/

     end if;

  else

     Set IdUser = -1;
     Set IdGroup = -1;
     Set IdValid = -3; /*Not found*/

  end if;
	
END$$ 

CREATE PROCEDURE SPGETGROUPS ( IN FORIDGROUP integer )
BEGIN

  Select A.IdGroup, A.Description from tblGroups A Where ForIdGroup Is Null Or A.IdGroup = ForIdGroup;
	
END$$ 

CREATE procedure SPSETGROUPS ( IN IDGROUP integer, IN DESCRIPTION varchar(50) )
BEGIN

  Insert Into tblGroups(IdGroup,Description) Values(IdGroup,Description);
	
END$$

DELIMITER ;

insert into tblGroups( idgroup, description ) values( null, 'Regular user group' );

insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test1', '123qwerty', 'System user firstname', 'System user lastname', null, null );
insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test2', '12345678', null, null, null, null );

insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'sysdba', 'Default system user for Firebird', 'System user lastname', null, null );
insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'root', 'Default system user for MySQL', null, null, null );

insert into tblGenericData( id, data1, data2 ) Values( 1, 'DataA1', 'DataA2' );
insert into tblGenericData( id, data1, data2 ) Values( 2, 'DataB1', 'DataB2' );
insert into tblGenericData( id, data1, data2 ) Values( 3, 'DataC1', 'DataC2' );

ALTER TABLE tblGenericData AUTO_INCREMENT=4;

