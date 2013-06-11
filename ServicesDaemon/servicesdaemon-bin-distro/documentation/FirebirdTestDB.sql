SET SQL DIALECT 3;

SET NAMES UTF8;

CREATE DATABASE 'localhost:FirebirdTestDB' USER 'SYSDBA' PASSWORD 'masterkey' PAGE_SIZE 16384 DEFAULT CHARACTER SET UTF8;

CREATE GENERATOR GEN_TBLUSERS_ID;

CREATE TABLE tblUsers( id INT NOT NULL, username VARCHAR(50) NOT NULL, password VARCHAR(50) NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50), access_date DATE, access_time TIME, PRIMARY KEY (id) );

set term ^ ;

CREATE TRIGGER TBLUSERS_BI FOR tblUsers ACTIVE BEFORE INSERT
as
begin

  if ( NEW.id is null ) then
     NEW.id = gen_id( GEN_TBLUSERS_ID, 1 );

end ^

set term ; ^

insert into tblUsers( id, username, password, firstname, lastname, access_date, access_time ) values( null, 'test1', '123englishchick', 'System user firstname', 'System user lastname', null, null );
insert into tblUsers( id, username, password, firstname, lastname, access_date, access_time ) values( null, 'test2', '12345678', null, null, null, null );

CREATE TABLE tblGenericData( id INT, data1 VARCHAR(100), data2 VARCHAR(100) );

insert into tblGenericData( id, data1, data2 ) Values( 1, 'DataA1', 'DataA2' );
insert into tblGenericData( id, data1, data2 ) Values( 2, 'DataB1', 'DataB2' );
insert into tblGenericData( id, data1, data2 ) Values( 3, 'DataC1', 'DataC2' );
