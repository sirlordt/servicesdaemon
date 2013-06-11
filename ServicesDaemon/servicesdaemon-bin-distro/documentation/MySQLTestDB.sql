create database MySQLTestDB;

use MySQLTestDB;

CREATE TABLE tblUsers( id INT NOT NULL AUTO_INCREMENT, username VARCHAR(50) NOT NULL, password VARCHAR(50) NOT NULL, firstname VARCHAR(50), lastname VARCHAR(50), access_date DATE, access_time TIME, PRIMARY KEY (id) );

insert into tblUsers( username, password, firstname, lastname, access_date, access_time ) values( 'test1', '123englishchick', 'System user firstname', 'System user lastname', null, null );
insert into tblUsers( username, password, firstname, lastname, access_date, access_time ) values( 'test2', '12345678', null, null, null, null );

CREATE TABLE tblGenericData( id INT, data1 VARCHAR(100), data2 VARCHAR(100) );

insert into tblGenericData( id, data1, data2 ) Values( 1, 'DataA1', 'DataA2' );
insert into tblGenericData( id, data1, data2 ) Values( 2, 'DataB1', 'DataB2' );
insert into tblGenericData( id, data1, data2 ) Values( 3, 'DataC1', 'DataC2' );
