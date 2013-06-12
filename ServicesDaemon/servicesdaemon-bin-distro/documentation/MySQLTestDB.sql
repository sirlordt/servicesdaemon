create database MySQLTestDB;

use MySQLTestDB;

CREATE TABLE tblUsers( iduser INT NOT NULL AUTO_INCREMENT, idgroup INT NOT NULL, disabled SMALLINT NOT NULL, username VARCHAR(50) NOT NULL, passwd VARCHAR(50) NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50), access_date DATE, access_time TIME, PRIMARY KEY (iduser) );

CREATE TABLE tblGroups( idgroup INT NOT NULL AUTO_INCREMENT, description VARCHAR(50) NOT NULL, PRIMARY KEY (idgroup) );

CREATE TABLE tblGenericData( id INT, data1 VARCHAR(100), data2 VARCHAR(100), PRIMARY KEY (id) );

CREATE TABLE tblBlobData( id INT, blobdata blob, PRIMARY KEY (id) );

insert into tblGroups( idgroup, description ) values( null, 'Regular user group' );

insert into tblUsers( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test1', '123qwerty', 'System user firstname', 'System user lastname', null, null );
insert into tblUsers( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test2', '12345678', null, null, null, null );

insert into tblGenericData( id, data1, data2 ) Values( 1, 'DataA1', 'DataA2' );
insert into tblGenericData( id, data1, data2 ) Values( 2, 'DataB1', 'DataB2' );
insert into tblGenericData( id, data1, data2 ) Values( 3, 'DataC1', 'DataC2' );
