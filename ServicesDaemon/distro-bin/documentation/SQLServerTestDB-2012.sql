USE [master]
GO
/****** Object:  Database [SQLServerTestDB]    Script Date: 02/08/2013 09:43:00 a.m. ******/
CREATE DATABASE [SQLServerTestDB]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'SQLServerTestDB', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL11.SQLEXPRESS\MSSQL\DATA\SQLServerTestDB.mdf' , SIZE = 5120KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'SQLServerTestDB_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL11.SQLEXPRESS\MSSQL\DATA\SQLServerTestDB_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [SQLServerTestDB] SET COMPATIBILITY_LEVEL = 110
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [SQLServerTestDB].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [SQLServerTestDB] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET ARITHABORT OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET AUTO_CREATE_STATISTICS ON 
GO
ALTER DATABASE [SQLServerTestDB] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [SQLServerTestDB] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [SQLServerTestDB] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET  DISABLE_BROKER 
GO
ALTER DATABASE [SQLServerTestDB] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [SQLServerTestDB] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [SQLServerTestDB] SET  MULTI_USER 
GO
ALTER DATABASE [SQLServerTestDB] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [SQLServerTestDB] SET DB_CHAINING OFF 
GO
ALTER DATABASE [SQLServerTestDB] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [SQLServerTestDB] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
USE [SQLServerTestDB]
GO
USE [SQLServerTestDB]
GO
/****** Object:  Sequence [dbo].[GEN_TBLBLOBDATA_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/
CREATE SEQUENCE [dbo].[GEN_TBLBLOBDATA_ID] 
 AS [bigint]
 START WITH 1
 INCREMENT BY 1
 MINVALUE -9223372036854775808
 MAXVALUE 9223372036854775807
 CACHE 
GO
USE [SQLServerTestDB]
GO
/****** Object:  Sequence [dbo].[GEN_TBLGENERICDATA_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/
CREATE SEQUENCE [dbo].[GEN_TBLGENERICDATA_ID] 
 AS [bigint]
 START WITH 3
 INCREMENT BY 1
 MINVALUE -9223372036854775808
 MAXVALUE 9223372036854775807
 CACHE 
GO
USE [SQLServerTestDB]
GO
/****** Object:  Sequence [dbo].[GEN_TBLGROUPS_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/
CREATE SEQUENCE [dbo].[GEN_TBLGROUPS_ID] 
 AS [bigint]
 START WITH 1
 INCREMENT BY 1
 MINVALUE -9223372036854775808
 MAXVALUE 9223372036854775807
 CACHE 
GO
USE [SQLServerTestDB]
GO
/****** Object:  Sequence [dbo].[GEN_TBLUSERSDB_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/
CREATE SEQUENCE [dbo].[GEN_TBLUSERSDB_ID] 
 AS [bigint]
 START WITH 1
 INCREMENT BY 1
 MINVALUE -9223372036854775808
 MAXVALUE 9223372036854775807
 CACHE 
GO
USE [SQLServerTestDB]
GO
/****** Object:  Sequence [dbo].[GEN_TBLUSERSENG_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/
CREATE SEQUENCE [dbo].[GEN_TBLUSERSENG_ID] 
 AS [bigint]
 START WITH 1
 INCREMENT BY 1
 MINVALUE -9223372036854775808
 MAXVALUE 9223372036854775807
 CACHE 
GO
/****** Object:  StoredProcedure [dbo].[SPCHECKDBUSER]    Script Date: 02/08/2013 09:43:00 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[SPCHECKDBUSER] 
	@USERNAME varchar(75), 
	@PASSWD varchar(150),
	@IDVALID integer out,
    @IDUSER integer out,
    @IDGROUP integer out,
    @LASTACCESSDATE date out,
    @LASTACCESSTIME time out
AS
BEGIN
    declare @DISABLED smallint
    declare @CURRENT_PASSWD varchar(50)

	SET NOCOUNT ON;

    Select @IdUser = A.IdUser, @IdGroup = A.IdGroup, @Current_Passwd = A.Passwd, @Disabled = A.Disabled, @LastAccessDate = A.access_date, @LastAccessTime = A.access_time From tblUsersDB As A Where A.Username = @Username

    if ( @IDUser Is Not null )
    begin 

       if ( @Current_Passwd = @Passwd )
       begin

          if ( @Disabled = 0 Or @Disabled Is Null )
          begin

             Set @IdValid = 1;   /*Valid*/

             Update tblUsersDB Set access_date = CAST(GETDATE() AS date), Access_time = CAST(GETDATE() AS time) Where IdUser = @IdUser --Update the last access date and time

          end
          else
          begin

             Set @IdUser = -1;
             Set @IdGroup = -1;

	         Set @IdValid = -2;  /*Disabled*/

          end

       end
       else
       begin

          Set @IdUser = -1;
          Set @IdGroup = -1;

          Set @IdValid = -1; /*Invalid password*/

       end

    end
    else
    begin

       Set @IdUser = -1;
       Set @IdGroup = -1;

       Set @IdValid = -3; /*Not found*/

    end

END

GO
/****** Object:  StoredProcedure [dbo].[SPGETGROUPS]    Script Date: 02/08/2013 09:43:00 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[SPGETGROUPS] 
	@FORIDGROUP integer
AS
BEGIN
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select A.IdGroup, A.Description from tblGroups A Where @ForIdGroup Is Null Or A.IdGroup = @ForIdGroup

END

GO
/****** Object:  StoredProcedure [dbo].[SPSETGROUPS]    Script Date: 02/08/2013 09:43:00 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[SPSETGROUPS] 
	@IDGROUP integer, 
	@DESCRIPTION varchar(50)
AS
BEGIN
	SET NOCOUNT ON;

    Insert Into tblGroups(IdGroup,Description) Values(@IdGroup,@Description);

END

GO

/****** Object:  Table [dbo].[tblAuto]    Script Date: 10/08/2013 03:35:37 p.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tblAuto](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[value] [varchar](50) NOT NULL,
 CONSTRAINT [PK_tblAuto] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tblBlobData]    Script Date: 02/08/2013 09:43:00 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tblBlobData](
	[id] [int] NOT NULL,
	[blobdata] [image] NOT NULL,
 CONSTRAINT [PK_tblBlobData] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[tblGenericData]    Script Date: 02/08/2013 09:43:00 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tblGenericData](
	[id] [int] NOT NULL,
	[data1] [varchar](100) NULL,
	[data2] [varchar](100) NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tblGroups]    Script Date: 02/08/2013 09:43:00 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tblGroups](
	[idgroup] [int] NOT NULL,
	[description] [varchar](50) NOT NULL,
 CONSTRAINT [PK_tblGroups] PRIMARY KEY CLUSTERED 
(
	[idgroup] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tblUsersDB]    Script Date: 02/08/2013 09:43:00 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tblUsersDB](
	[iduser] [int] NULL,
	[idgroup] [int] NOT NULL,
	[disabled] [smallint] NOT NULL,
	[username] [varchar](50) NOT NULL,
	[passwd] [varchar](50) NOT NULL,
	[firstname] [varchar](50) NULL,
	[lastname] [varchar](50) NULL,
	[access_date] [datetime] NULL,
	[access_time] [datetime] NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tblUsersEng]    Script Date: 02/08/2013 09:43:00 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tblUsersEng](
	[iduser] [int] NOT NULL,
	[idgroup] [int] NOT NULL,
	[disabled] [smallint] NOT NULL,
	[username] [varchar](50) NOT NULL,
	[firstname] [varchar](50) NULL,
	[lastname] [varchar](50) NULL,
	[access_date] [datetime] NULL,
	[access_time] [datetime] NULL,
 CONSTRAINT [PK_tblUsersEng] PRIMARY KEY CLUSTERED 
(
	[iduser] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Trigger [dbo].[TBLUSERSDB_BI] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TBLUSERSDB_BI]
   ON [TBLUSERSDB] 
   INSTEAD OF INSERT
AS 
  declare @IdUser int;
BEGIN
   
   SET NOCOUNT ON;

   SET @IdUser = (SELECT IdUser FROM INSERTED)
	
   If ( @IdUser is Null )
   Begin

      SET @IdUser = NEXT VALUE FOR GEN_TBLUSERSDB_ID

      SELECT * INTO #Inserted FROM Inserted

      UPDATE #Inserted SET IdUser = @IdUser

      INSERT INTO tblUsersDB SELECT * FROM #Inserted

   End
   Else
   Begin

      SET @IdUser = NEXT VALUE FOR GEN_TBLUSERSDB_ID

      INSERT INTO tblUsersDB SELECT * FROM Inserted

   End
    
END

GO

/****** Object:  Trigger [dbo].[TBLUSERSENG_BI] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TBLUSERSENG_BI]
   ON [TBLUSERSENG] 
   INSTEAD OF INSERT
AS 
  declare @IdUser int;
BEGIN
   
   SET NOCOUNT ON;

   SET @IdUser = (SELECT IdUser FROM INSERTED)
	
   If ( @IdUser is Null )
   Begin

      SET @IdUser = NEXT VALUE FOR GEN_TBLUSERSENG_ID

      SELECT * INTO #Inserted FROM Inserted

      UPDATE #Inserted SET IdUser = @IdUser

      INSERT INTO tblUsersEng SELECT * FROM #Inserted

   End
   Else
   Begin

      SET @IdUser = NEXT VALUE FOR GEN_TBLUSERSENG_ID

      INSERT INTO tblUsersEng SELECT * FROM Inserted

   End
    
END

GO

/****** Object:  Trigger [dbo].[TBLGROUPS_BI] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TBLGROUPS_BI]
   ON [TBLGROUPS] 
   INSTEAD OF INSERT
AS 
  declare @IdGroup int;
BEGIN
   
   SET NOCOUNT ON;

   SET @IdGroup = (SELECT IdGroup FROM INSERTED)
	
   If ( @IdGroup is Null )
   Begin

      SET @IdGroup = NEXT VALUE FOR GEN_TBLGROUPS_ID

      SELECT * INTO #Inserted FROM Inserted

      UPDATE #Inserted SET IdGroup = @IdGroup

      INSERT INTO tblGroups SELECT * FROM #Inserted

   End
   Else
   Begin

      INSERT INTO tblGroups SELECT * FROM Inserted

   End
    
END

GO

/****** Object:  Trigger [dbo].[TBLGENERICDATA_BI] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TBLGENERICDATA_BI]
   ON [TBLGENERICDATA] 
   INSTEAD OF INSERT
AS 
  declare @Id int;
BEGIN
   
   SET NOCOUNT ON;

   SET @Id = (SELECT Id FROM INSERTED)
	
   If ( @Id is Null )
   Begin

      SET @Id = NEXT VALUE FOR GEN_TBLGENERICDATA_ID

      SELECT * INTO #Inserted FROM Inserted

      UPDATE #Inserted SET Id = @Id

      INSERT INTO tblGenericData SELECT * FROM #Inserted

   End
   Else
   Begin

      SET @Id = NEXT VALUE FOR GEN_TBLGENERICDATA_ID

      INSERT INTO tblGenericData SELECT * FROM Inserted

   End
    
END

GO

/****** Object:  Trigger [dbo].[TBLBLOBDATA_BI] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[TBLBLOBDATA_BI]
   ON [TBLBLOBDATA] 
   INSTEAD OF INSERT
AS 
  declare @Id int;
BEGIN
   
   SET NOCOUNT ON;

   SET @Id = (SELECT Id FROM INSERTED)
	
   If ( @Id is Null )
   Begin

      SET @Id = NEXT VALUE FOR GEN_TBLBLOBDATA_ID

      SELECT * INTO #Inserted FROM Inserted

      UPDATE #Inserted SET Id = @Id

      INSERT INTO tblBlobData SELECT * FROM #Inserted

   End
   Else
   Begin

      SET @Id = NEXT VALUE FOR GEN_TBLBLOBDATA_ID

      INSERT INTO tblBlobData SELECT * FROM Inserted

   End
    
END

GO

/****** Object:  UserDefinedFunction [dbo].[GETVALUE]    Script Date: 03/08/2013 01:44:30 p.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GETVALUE]( @ID int )
RETURNS int
AS
BEGIN

   RETURN 33 * @ID

END

GO

/****** Object:  UserDefinedFunction [dbo].[GETVALUES]    Script Date: 03/08/2013 01:44:44 p.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[GETVALUES]( @ID int )
RETURNS 
@Results TABLE 
(
   value int
)
AS
BEGIN

   INSERT @Results Select 33 * @ID
   INSERT @Results Select 66 * @ID
	 	
   RETURN 

END

GO

/** Insert basic data **/
BEGIN TRAN T1;

insert into tblGroups( idgroup, description ) values( null, 'Regular user group' );

insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test1', '123qwerty', 'System user firstname', 'System user lastname', null, null );
insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test2', '12345678', null, null, null, null );

insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'sysdba', 'Default system user for Firebird', 'System user lastname', null, null );
insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'root', 'Default system user for MySQL', null, null, null );

insert into tblGenericData( id, data1, data2 ) Values( 1, 'DataA1', 'DataA2' );
insert into tblGenericData( id, data1, data2 ) Values( 2, 'DataB1', 'DataB2' );
insert into tblGenericData( id, data1, data2 ) Values( 3, 'DataC1', 'DataC2' );

COMMIT TRAN T1;

USE [master]
GO
ALTER DATABASE [SQLServerTestDB] SET  READ_WRITE 
GO

