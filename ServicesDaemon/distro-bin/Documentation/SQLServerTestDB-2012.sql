USE [master]GO/****** Object:  Database [SQLServerTestDB]    Script Date: 02/08/2013 09:43:00 a.m. ******/CREATE DATABASE [SQLServerTestDB] CONTAINMENT = NONE ON  PRIMARY ( NAME = N'SQLServerTestDB', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL11.SQLEXPRESS\MSSQL\DATA\SQLServerTestDB.mdf' , SIZE = 5120KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB ) LOG ON ( NAME = N'SQLServerTestDB_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL11.SQLEXPRESS\MSSQL\DATA\SQLServerTestDB_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)GOALTER DATABASE [SQLServerTestDB] SET COMPATIBILITY_LEVEL = 110GOIF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))beginEXEC [SQLServerTestDB].[dbo].[sp_fulltext_database] @action = 'enable'endGOALTER DATABASE [SQLServerTestDB] SET ANSI_NULL_DEFAULT OFF GOALTER DATABASE [SQLServerTestDB] SET ANSI_NULLS OFF GOALTER DATABASE [SQLServerTestDB] SET ANSI_PADDING OFF GOALTER DATABASE [SQLServerTestDB] SET ANSI_WARNINGS OFF GOALTER DATABASE [SQLServerTestDB] SET ARITHABORT OFF GOALTER DATABASE [SQLServerTestDB] SET AUTO_CLOSE OFF GOALTER DATABASE [SQLServerTestDB] SET AUTO_CREATE_STATISTICS ON GOALTER DATABASE [SQLServerTestDB] SET AUTO_SHRINK OFF GOALTER DATABASE [SQLServerTestDB] SET AUTO_UPDATE_STATISTICS ON GOALTER DATABASE [SQLServerTestDB] SET CURSOR_CLOSE_ON_COMMIT OFF GOALTER DATABASE [SQLServerTestDB] SET CURSOR_DEFAULT  GLOBAL GOALTER DATABASE [SQLServerTestDB] SET CONCAT_NULL_YIELDS_NULL OFF GOALTER DATABASE [SQLServerTestDB] SET NUMERIC_ROUNDABORT OFF GOALTER DATABASE [SQLServerTestDB] SET QUOTED_IDENTIFIER OFF GOALTER DATABASE [SQLServerTestDB] SET RECURSIVE_TRIGGERS OFF GOALTER DATABASE [SQLServerTestDB] SET  DISABLE_BROKER GOALTER DATABASE [SQLServerTestDB] SET AUTO_UPDATE_STATISTICS_ASYNC OFF GOALTER DATABASE [SQLServerTestDB] SET DATE_CORRELATION_OPTIMIZATION OFF GOALTER DATABASE [SQLServerTestDB] SET TRUSTWORTHY OFF GOALTER DATABASE [SQLServerTestDB] SET ALLOW_SNAPSHOT_ISOLATION OFF GOALTER DATABASE [SQLServerTestDB] SET PARAMETERIZATION SIMPLE GOALTER DATABASE [SQLServerTestDB] SET READ_COMMITTED_SNAPSHOT OFF GOALTER DATABASE [SQLServerTestDB] SET HONOR_BROKER_PRIORITY OFF GOALTER DATABASE [SQLServerTestDB] SET RECOVERY SIMPLE GOALTER DATABASE [SQLServerTestDB] SET  MULTI_USER GOALTER DATABASE [SQLServerTestDB] SET PAGE_VERIFY CHECKSUM  GOALTER DATABASE [SQLServerTestDB] SET DB_CHAINING OFF GOALTER DATABASE [SQLServerTestDB] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) GOALTER DATABASE [SQLServerTestDB] SET TARGET_RECOVERY_TIME = 0 SECONDS GOUSE [SQLServerTestDB]GOUSE [SQLServerTestDB]GO/****** Object:  Sequence [dbo].[GEN_TBLBLOBDATA_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/CREATE SEQUENCE [dbo].[GEN_TBLBLOBDATA_ID]  AS [bigint] START WITH 1 INCREMENT BY 1 MINVALUE -9223372036854775808 MAXVALUE 9223372036854775807 CACHE GOUSE [SQLServerTestDB]GO/****** Object:  Sequence [dbo].[GEN_TBLGENERICDATA_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/CREATE SEQUENCE [dbo].[GEN_TBLGENERICDATA_ID]  AS [bigint] START WITH 3 INCREMENT BY 1 MINVALUE -9223372036854775808 MAXVALUE 9223372036854775807 CACHE GOUSE [SQLServerTestDB]GO/****** Object:  Sequence [dbo].[GEN_TBLGROUPS_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/CREATE SEQUENCE [dbo].[GEN_TBLGROUPS_ID]  AS [bigint] START WITH 1 INCREMENT BY 1 MINVALUE -9223372036854775808 MAXVALUE 9223372036854775807 CACHE GOUSE [SQLServerTestDB]GO/****** Object:  Sequence [dbo].[GEN_TBLUSERSDB_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/CREATE SEQUENCE [dbo].[GEN_TBLUSERSDB_ID]  AS [bigint] START WITH 1 INCREMENT BY 1 MINVALUE -9223372036854775808 MAXVALUE 9223372036854775807 CACHE GOUSE [SQLServerTestDB]GO/****** Object:  Sequence [dbo].[GEN_TBLUSERSENG_ID]    Script Date: 02/08/2013 09:43:00 a.m. ******/CREATE SEQUENCE [dbo].[GEN_TBLUSERSENG_ID]  AS [bigint] START WITH 1 INCREMENT BY 1 MINVALUE -9223372036854775808 MAXVALUE 9223372036854775807 CACHE GO/****** Object:  StoredProcedure [dbo].[SPCHECKDBUSER]    Script Date: 02/08/2013 09:43:00 a.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE PROCEDURE [dbo].[SPCHECKDBUSER]     @USERNAME varchar(75),     @PASSWD varchar(150),    @IDVALID integer out,    @IDUSER integer out,    @IDGROUP integer out,    @LASTACCESSDATE date out,    @LASTACCESSTIME time outASBEGIN    declare @DISABLED smallint    declare @CURRENT_PASSWD varchar(50)    SET NOCOUNT ON;    Select @IdUser = A.IdUser, @IdGroup = A.IdGroup, @Current_Passwd = A.Passwd, @Disabled = A.Disabled, @LastAccessDate = A.access_date, @LastAccessTime = A.access_time From tblUsersDB As A Where A.Username = @Username    if ( @IDUser Is Not null )    begin        if ( @Current_Passwd = @Passwd )       begin          if ( @Disabled = 0 Or @Disabled Is Null )          begin             Set @IdValid = 1;   /*Valid*/             Update tblUsersDB Set access_date = CAST(GETDATE() AS date), Access_time = CAST(GETDATE() AS time) Where IdUser = @IdUser --Update the last access date and time          end          else          begin             Set @IdUser = -1;             Set @IdGroup = -1;             Set @IdValid = -2;  /*Disabled*/          end       end       else       begin          Set @IdUser = -1;          Set @IdGroup = -1;          Set @IdValid = -1; /*Invalid password*/       end    end    else    begin       Set @IdUser = -1;       Set @IdGroup = -1;       Set @IdValid = -3; /*Not found*/    endENDGO/****** Object:  StoredProcedure [dbo].[SPGETGROUPS]    Script Date: 02/08/2013 09:43:00 a.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE PROCEDURE [dbo].[SPGETGROUPS]     @FORIDGROUP integerASBEGIN    SET NOCOUNT ON;    -- Insert statements for procedure here    Select A.IdGroup, A.Description from tblGroups A Where @ForIdGroup Is Null Or A.IdGroup = @ForIdGroupENDGO/****** Object:  StoredProcedure [dbo].[SPSETGROUPS]    Script Date: 02/08/2013 09:43:00 a.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE PROCEDURE [dbo].[SPSETGROUPS]     @IDGROUP integer,     @DESCRIPTION varchar(50)ASBEGIN    SET NOCOUNT ON;    Insert Into tblGroups(IdGroup,Description) Values(@IdGroup,@Description);ENDGO/****** Object:  Table [dbo].[tblAuto]    Script Date: 10/08/2013 03:35:37 p.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOSET ANSI_PADDING ONGOCREATE TABLE [dbo].[tblAuto](    [id] [int] IDENTITY(1,1) NOT NULL,    [value] [varchar](50) NOT NULL, CONSTRAINT [PK_tblAuto] PRIMARY KEY CLUSTERED (    [id] ASC)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]) ON [PRIMARY]GOSET ANSI_PADDING OFFGO/****** Object:  Table [dbo].[tblBlobData]    Script Date: 02/08/2013 09:43:00 a.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE TABLE [dbo].[tblBlobData](    [id] [int] NOT NULL,    [blobdata] [image] NOT NULL, CONSTRAINT [PK_tblBlobData] PRIMARY KEY CLUSTERED (    [id] ASC)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]GO/****** Object:  Table [dbo].[tblGenericData]    Script Date: 02/08/2013 09:43:00 a.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOSET ANSI_PADDING ONGOCREATE TABLE [dbo].[tblGenericData](    [id] [int] NOT NULL,    [data1] [varchar](100) NULL,    [data2] [varchar](100) NULL) ON [PRIMARY]GOSET ANSI_PADDING OFFGO/****** Object:  Table [dbo].[tblGroups]    Script Date: 02/08/2013 09:43:00 a.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOSET ANSI_PADDING ONGOCREATE TABLE [dbo].[tblGroups](    [idgroup] [int] NOT NULL,    [description] [varchar](50) NOT NULL, CONSTRAINT [PK_tblGroups] PRIMARY KEY CLUSTERED (    [idgroup] ASC)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]) ON [PRIMARY]GOSET ANSI_PADDING OFFGO/****** Object:  Table [dbo].[tblUsersDB]    Script Date: 02/08/2013 09:43:00 a.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOSET ANSI_PADDING ONGOCREATE TABLE [dbo].[tblUsersDB](    [iduser] [int] NULL,    [idgroup] [int] NOT NULL,    [disabled] [smallint] NOT NULL,    [username] [varchar](50) NOT NULL,    [passwd] [varchar](50) NOT NULL,    [firstname] [varchar](50) NULL,    [lastname] [varchar](50) NULL,    [access_date] [datetime] NULL,    [access_time] [datetime] NULL) ON [PRIMARY]GOSET ANSI_PADDING OFFGO/****** Object:  Table [dbo].[tblUsersEng]    Script Date: 02/08/2013 09:43:00 a.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOSET ANSI_PADDING ONGOCREATE TABLE [dbo].[tblUsersEng](    [iduser] [int] NOT NULL,    [idgroup] [int] NOT NULL,    [disabled] [smallint] NOT NULL,    [username] [varchar](50) NOT NULL,    [firstname] [varchar](50) NULL,    [lastname] [varchar](50) NULL,    [access_date] [datetime] NULL,    [access_time] [datetime] NULL, CONSTRAINT [PK_tblUsersEng] PRIMARY KEY CLUSTERED (    [iduser] ASC)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]) ON [PRIMARY]GOSET ANSI_PADDING OFFGO/****** Object:  Trigger [dbo].[TBLUSERSDB_BI] ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE TRIGGER [dbo].[TBLUSERSDB_BI]   ON [TBLUSERSDB]    INSTEAD OF INSERTAS   declare @IdUser int;BEGIN      SET NOCOUNT ON;   SET @IdUser = (SELECT IdUser FROM INSERTED)       If ( @IdUser is Null )   Begin      SET @IdUser = NEXT VALUE FOR GEN_TBLUSERSDB_ID      SELECT * INTO #Inserted FROM Inserted      UPDATE #Inserted SET IdUser = @IdUser      INSERT INTO tblUsersDB SELECT * FROM #Inserted   End   Else   Begin      SET @IdUser = NEXT VALUE FOR GEN_TBLUSERSDB_ID      INSERT INTO tblUsersDB SELECT * FROM Inserted   End    ENDGO/****** Object:  Trigger [dbo].[TBLUSERSENG_BI] ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE TRIGGER [dbo].[TBLUSERSENG_BI]   ON [TBLUSERSENG]    INSTEAD OF INSERTAS   declare @IdUser int;BEGIN      SET NOCOUNT ON;   SET @IdUser = (SELECT IdUser FROM INSERTED)       If ( @IdUser is Null )   Begin      SET @IdUser = NEXT VALUE FOR GEN_TBLUSERSENG_ID      SELECT * INTO #Inserted FROM Inserted      UPDATE #Inserted SET IdUser = @IdUser      INSERT INTO tblUsersEng SELECT * FROM #Inserted   End   Else   Begin      SET @IdUser = NEXT VALUE FOR GEN_TBLUSERSENG_ID      INSERT INTO tblUsersEng SELECT * FROM Inserted   End    ENDGO/****** Object:  Trigger [dbo].[TBLGROUPS_BI] ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE TRIGGER [dbo].[TBLGROUPS_BI]   ON [TBLGROUPS]    INSTEAD OF INSERTAS   declare @IdGroup int;BEGIN      SET NOCOUNT ON;   SET @IdGroup = (SELECT IdGroup FROM INSERTED)       If ( @IdGroup is Null )   Begin      SET @IdGroup = NEXT VALUE FOR GEN_TBLGROUPS_ID      SELECT * INTO #Inserted FROM Inserted      UPDATE #Inserted SET IdGroup = @IdGroup      INSERT INTO tblGroups SELECT * FROM #Inserted   End   Else   Begin      INSERT INTO tblGroups SELECT * FROM Inserted   End    ENDGO/****** Object:  Trigger [dbo].[TBLGENERICDATA_BI] ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE TRIGGER [dbo].[TBLGENERICDATA_BI]   ON [TBLGENERICDATA]    INSTEAD OF INSERTAS   declare @Id int;BEGIN      SET NOCOUNT ON;   SET @Id = (SELECT Id FROM INSERTED)       If ( @Id is Null )   Begin      SET @Id = NEXT VALUE FOR GEN_TBLGENERICDATA_ID      SELECT * INTO #Inserted FROM Inserted      UPDATE #Inserted SET Id = @Id      INSERT INTO tblGenericData SELECT * FROM #Inserted   End   Else   Begin      SET @Id = NEXT VALUE FOR GEN_TBLGENERICDATA_ID      INSERT INTO tblGenericData SELECT * FROM Inserted   End    ENDGO/****** Object:  Trigger [dbo].[TBLBLOBDATA_BI] ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE TRIGGER [dbo].[TBLBLOBDATA_BI]   ON [TBLBLOBDATA]    INSTEAD OF INSERTAS   declare @Id int;BEGIN      SET NOCOUNT ON;   SET @Id = (SELECT Id FROM INSERTED)       If ( @Id is Null )   Begin      SET @Id = NEXT VALUE FOR GEN_TBLBLOBDATA_ID      SELECT * INTO #Inserted FROM Inserted      UPDATE #Inserted SET Id = @Id      INSERT INTO tblBlobData SELECT * FROM #Inserted   End   Else   Begin      SET @Id = NEXT VALUE FOR GEN_TBLBLOBDATA_ID      INSERT INTO tblBlobData SELECT * FROM Inserted   End    ENDGO/****** Object:  UserDefinedFunction [dbo].[GETVALUE]    Script Date: 03/08/2013 01:44:30 p.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE FUNCTION [dbo].[GETVALUE]( @ID int )RETURNS intASBEGIN   RETURN 33 * @IDENDGO/****** Object:  UserDefinedFunction [dbo].[GETVALUES]    Script Date: 03/08/2013 01:44:44 p.m. ******/SET ANSI_NULLS ONGOSET QUOTED_IDENTIFIER ONGOCREATE FUNCTION [dbo].[GETVALUES]( @ID int )RETURNS @Results TABLE (   value int)ASBEGIN   INSERT @Results Select 33 * @ID   INSERT @Results Select 66 * @ID           RETURN ENDGO/** Insert basic data **/BEGIN TRAN T1;insert into tblGroups( idgroup, description ) values( null, 'Regular user group' );insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test1', '123qwerty', 'System user firstname', 'System user lastname', null, null );insert into tblUsersDB( iduser, idgroup, disabled, username, passwd, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'test2', '12345678', null, null, null, null );insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'sysdba', 'Default system user for Firebird', 'System user lastname', null, null );insert into tblUsersEng( iduser, idgroup, disabled, username, firstname, lastname, access_date, access_time ) values( null, 1, 0, 'root', 'Default system user for MySQL', null, null, null );insert into tblGenericData( id, data1, data2 ) Values( 1, 'DataA1', 'DataA2' );insert into tblGenericData( id, data1, data2 ) Values( 2, 'DataB1', 'DataB2' );insert into tblGenericData( id, data1, data2 ) Values( 3, 'DataC1', 'DataC2' );COMMIT TRAN T1;USE [master]GOALTER DATABASE [SQLServerTestDB] SET  READ_WRITE GO S E   [ m a s t e r ]  
 G O  
 / * * * * * *   O b j e c t :     D a t a b a s e   [ S Q L S e r v e r T e s t D B ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 C R E A T E   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]  
   C O N T A I N M E N T   =   N O N E  
   O N     P R I M A R Y    
 (   N A M E   =   N ' S Q L S e r v e r T e s t D B ' ,   F I L E N A M E   =   N ' C : \ P r o g r a m   F i l e s \ M i c r o s o f t   S Q L   S e r v e r \ M S S Q L 1 1 . S Q L E X P R E S S \ M S S Q L \ D A T A \ S Q L S e r v e r T e s t D B . m d f '   ,   S I Z E   =   5 1 2 0 K B   ,   M A X S I Z E   =   U N L I M I T E D ,   F I L E G R O W T H   =   1 0 2 4 K B   )  
   L O G   O N    
 (   N A M E   =   N ' S Q L S e r v e r T e s t D B _ l o g ' ,   F I L E N A M E   =   N ' C : \ P r o g r a m   F i l e s \ M i c r o s o f t   S Q L   S e r v e r \ M S S Q L 1 1 . S Q L E X P R E S S \ M S S Q L \ D A T A \ S Q L S e r v e r T e s t D B _ l o g . l d f '   ,   S I Z E   =   1 0 2 4 K B   ,   M A X S I Z E   =   2 0 4 8 G B   ,   F I L E G R O W T H   =   1 0 % )  
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   C O M P A T I B I L I T Y _ L E V E L   =   1 1 0  
 G O  
 I F   ( 1   =   F U L L T E X T S E R V I C E P R O P E R T Y ( ' I s F u l l T e x t I n s t a l l e d ' ) )  
 b e g i n  
 E X E C   [ S Q L S e r v e r T e s t D B ] . [ d b o ] . [ s p _ f u l l t e x t _ d a t a b a s e ]   @ a c t i o n   =   ' e n a b l e '  
 e n d  
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A N S I _ N U L L _ D E F A U L T   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A N S I _ N U L L S   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A N S I _ P A D D I N G   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A N S I _ W A R N I N G S   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A R I T H A B O R T   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A U T O _ C L O S E   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A U T O _ C R E A T E _ S T A T I S T I C S   O N    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A U T O _ S H R I N K   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A U T O _ U P D A T E _ S T A T I S T I C S   O N    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   C U R S O R _ C L O S E _ O N _ C O M M I T   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   C U R S O R _ D E F A U L T     G L O B A L    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   C O N C A T _ N U L L _ Y I E L D S _ N U L L   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   N U M E R I C _ R O U N D A B O R T   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   Q U O T E D _ I D E N T I F I E R   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   R E C U R S I V E _ T R I G G E R S   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T     D I S A B L E _ B R O K E R    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A U T O _ U P D A T E _ S T A T I S T I C S _ A S Y N C   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   D A T E _ C O R R E L A T I O N _ O P T I M I Z A T I O N   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   T R U S T W O R T H Y   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   A L L O W _ S N A P S H O T _ I S O L A T I O N   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   P A R A M E T E R I Z A T I O N   S I M P L E    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   R E A D _ C O M M I T T E D _ S N A P S H O T   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   H O N O R _ B R O K E R _ P R I O R I T Y   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   R E C O V E R Y   S I M P L E    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T     M U L T I _ U S E R    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   P A G E _ V E R I F Y   C H E C K S U M      
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   D B _ C H A I N I N G   O F F    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   F I L E S T R E A M (   N O N _ T R A N S A C T E D _ A C C E S S   =   O F F   )    
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T   T A R G E T _ R E C O V E R Y _ T I M E   =   0   S E C O N D S    
 G O  
 U S E   [ S Q L S e r v e r T e s t D B ]  
 G O  
 U S E   [ S Q L S e r v e r T e s t D B ]  
 G O  
 / * * * * * *   O b j e c t :     S e q u e n c e   [ d b o ] . [ G E N _ T B L B L O B D A T A _ I D ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 C R E A T E   S E Q U E N C E   [ d b o ] . [ G E N _ T B L B L O B D A T A _ I D ]    
   A S   [ b i g i n t ]  
   S T A R T   W I T H   1  
   I N C R E M E N T   B Y   1  
   M I N V A L U E   - 9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 8  
   M A X V A L U E   9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 7  
   C A C H E    
 G O  
 U S E   [ S Q L S e r v e r T e s t D B ]  
 G O  
 / * * * * * *   O b j e c t :     S e q u e n c e   [ d b o ] . [ G E N _ T B L G E N E R I C D A T A _ I D ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 C R E A T E   S E Q U E N C E   [ d b o ] . [ G E N _ T B L G E N E R I C D A T A _ I D ]    
   A S   [ b i g i n t ]  
   S T A R T   W I T H   3  
   I N C R E M E N T   B Y   1  
   M I N V A L U E   - 9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 8  
   M A X V A L U E   9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 7  
   C A C H E    
 G O  
 U S E   [ S Q L S e r v e r T e s t D B ]  
 G O  
 / * * * * * *   O b j e c t :     S e q u e n c e   [ d b o ] . [ G E N _ T B L G R O U P S _ I D ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 C R E A T E   S E Q U E N C E   [ d b o ] . [ G E N _ T B L G R O U P S _ I D ]    
   A S   [ b i g i n t ]  
   S T A R T   W I T H   1  
   I N C R E M E N T   B Y   1  
   M I N V A L U E   - 9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 8  
   M A X V A L U E   9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 7  
   C A C H E    
 G O  
 U S E   [ S Q L S e r v e r T e s t D B ]  
 G O  
 / * * * * * *   O b j e c t :     S e q u e n c e   [ d b o ] . [ G E N _ T B L U S E R S D B _ I D ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 C R E A T E   S E Q U E N C E   [ d b o ] . [ G E N _ T B L U S E R S D B _ I D ]    
   A S   [ b i g i n t ]  
   S T A R T   W I T H   1  
   I N C R E M E N T   B Y   1  
   M I N V A L U E   - 9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 8  
   M A X V A L U E   9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 7  
   C A C H E    
 G O  
 U S E   [ S Q L S e r v e r T e s t D B ]  
 G O  
 / * * * * * *   O b j e c t :     S e q u e n c e   [ d b o ] . [ G E N _ T B L U S E R S E N G _ I D ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 C R E A T E   S E Q U E N C E   [ d b o ] . [ G E N _ T B L U S E R S E N G _ I D ]    
   A S   [ b i g i n t ]  
   S T A R T   W I T H   1  
   I N C R E M E N T   B Y   1  
   M I N V A L U E   - 9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 8  
   M A X V A L U E   9 2 2 3 3 7 2 0 3 6 8 5 4 7 7 5 8 0 7  
   C A C H E    
 G O  
 / * * * * * *   O b j e c t :     S t o r e d P r o c e d u r e   [ d b o ] . [ S P C H E C K D B U S E R ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   P R O C E D U R E   [ d b o ] . [ S P C H E C K D B U S E R ]    
 	 @ U S E R N A M E   v a r c h a r ( 7 5 ) ,    
 	 @ P A S S W D   v a r c h a r ( 1 5 0 ) ,  
 	 @ I D V A L I D   i n t e g e r   o u t ,  
         @ I D U S E R   i n t e g e r   o u t ,  
         @ I D G R O U P   i n t e g e r   o u t ,  
         @ L A S T A C C E S S D A T E   d a t e   o u t ,  
         @ L A S T A C C E S S T I M E   t i m e   o u t  
 A S  
 B E G I N  
         d e c l a r e   @ D I S A B L E D   s m a l l i n t  
         d e c l a r e   @ C U R R E N T _ P A S S W D   v a r c h a r ( 5 0 )  
  
 	 S E T   N O C O U N T   O N ;  
  
         S e l e c t   @ I d U s e r   =   A . I d U s e r ,   @ I d G r o u p   =   A . I d G r o u p ,   @ C u r r e n t _ P a s s w d   =   A . P a s s w d ,   @ D i s a b l e d   =   A . D i s a b l e d ,   @ L a s t A c c e s s D a t e   =   A . a c c e s s _ d a t e ,   @ L a s t A c c e s s T i m e   =   A . a c c e s s _ t i m e   F r o m   t b l U s e r s D B   A s   A   W h e r e   A . U s e r n a m e   =   @ U s e r n a m e  
  
         i f   (   @ I D U s e r   I s   N o t   n u l l   )  
         b e g i n    
  
               i f   (   @ C u r r e n t _ P a s s w d   =   @ P a s s w d   )  
               b e g i n  
  
                     i f   (   @ D i s a b l e d   =   0   O r   @ D i s a b l e d   I s   N u l l   )  
                     b e g i n  
  
                           S e t   @ I d V a l i d   =   1 ;       / * V a l i d * /  
  
                           U p d a t e   t b l U s e r s D B   S e t   a c c e s s _ d a t e   =   C A S T ( G E T D A T E ( )   A S   d a t e ) ,   A c c e s s _ t i m e   =   C A S T ( G E T D A T E ( )   A S   t i m e )   W h e r e   I d U s e r   =   @ I d U s e r   - - U p d a t e   t h e   l a s t   a c c e s s   d a t e   a n d   t i m e  
  
                     e n d  
                     e l s e  
                     b e g i n  
  
                           S e t   @ I d U s e r   =   - 1 ;  
                           S e t   @ I d G r o u p   =   - 1 ;  
  
 	                   S e t   @ I d V a l i d   =   - 2 ;     / * D i s a b l e d * /  
  
                     e n d  
  
               e n d  
               e l s e  
               b e g i n  
  
                     S e t   @ I d U s e r   =   - 1 ;  
                     S e t   @ I d G r o u p   =   - 1 ;  
  
                     S e t   @ I d V a l i d   =   - 1 ;   / * I n v a l i d   p a s s w o r d * /  
  
               e n d  
  
         e n d  
         e l s e  
         b e g i n  
  
               S e t   @ I d U s e r   =   - 1 ;  
               S e t   @ I d G r o u p   =   - 1 ;  
  
               S e t   @ I d V a l i d   =   - 3 ;   / * N o t   f o u n d * /  
  
         e n d  
  
 E N D  
  
 G O  
 / * * * * * *   O b j e c t :     S t o r e d P r o c e d u r e   [ d b o ] . [ S P G E T G R O U P S ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   P R O C E D U R E   [ d b o ] . [ S P G E T G R O U P S ]    
 	 @ F O R I D G R O U P   i n t e g e r  
 A S  
 B E G I N  
 	 S E T   N O C O U N T   O N ;  
  
         - -   I n s e r t   s t a t e m e n t s   f o r   p r o c e d u r e   h e r e  
 	 S e l e c t   A . I d G r o u p ,   A . D e s c r i p t i o n   f r o m   t b l G r o u p s   A   W h e r e   @ F o r I d G r o u p   I s   N u l l   O r   A . I d G r o u p   =   @ F o r I d G r o u p  
  
 E N D  
  
 G O  
 / * * * * * *   O b j e c t :     S t o r e d P r o c e d u r e   [ d b o ] . [ S P S E T G R O U P S ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   P R O C E D U R E   [ d b o ] . [ S P S E T G R O U P S ]    
 	 @ I D G R O U P   i n t e g e r ,    
 	 @ D E S C R I P T I O N   v a r c h a r ( 5 0 )  
 A S  
 B E G I N  
 	 S E T   N O C O U N T   O N ;  
  
         I n s e r t   I n t o   t b l G r o u p s ( I d G r o u p , D e s c r i p t i o n )   V a l u e s ( @ I d G r o u p , @ D e s c r i p t i o n ) ;  
  
 E N D  
  
 G O  
  
 / * * * * * *   O b j e c t :     T a b l e   [ d b o ] . [ t b l A u t o ]         S c r i p t   D a t e :   1 0 / 0 8 / 2 0 1 3   0 3 : 3 5 : 3 7   p . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 S E T   A N S I _ P A D D I N G   O N  
 G O  
 C R E A T E   T A B L E   [ d b o ] . [ t b l A u t o ] (  
 	 [ i d ]   [ i n t ]   I D E N T I T Y ( 1 , 1 )   N O T   N U L L ,  
 	 [ v a l u e ]   [ v a r c h a r ] ( 5 0 )   N O T   N U L L ,  
   C O N S T R A I N T   [ P K _ t b l A u t o ]   P R I M A R Y   K E Y   C L U S T E R E D    
 (  
 	 [ i d ]   A S C  
 ) W I T H   ( P A D _ I N D E X   =   O F F ,   S T A T I S T I C S _ N O R E C O M P U T E   =   O F F ,   I G N O R E _ D U P _ K E Y   =   O F F ,   A L L O W _ R O W _ L O C K S   =   O N ,   A L L O W _ P A G E _ L O C K S   =   O N )   O N   [ P R I M A R Y ]  
 )   O N   [ P R I M A R Y ]  
  
 G O  
 S E T   A N S I _ P A D D I N G   O F F  
 G O  
 / * * * * * *   O b j e c t :     T a b l e   [ d b o ] . [ t b l B l o b D a t a ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   T A B L E   [ d b o ] . [ t b l B l o b D a t a ] (  
 	 [ i d ]   [ i n t ]   N O T   N U L L ,  
 	 [ b l o b d a t a ]   [ i m a g e ]   N O T   N U L L ,  
   C O N S T R A I N T   [ P K _ t b l B l o b D a t a ]   P R I M A R Y   K E Y   C L U S T E R E D    
 (  
 	 [ i d ]   A S C  
 ) W I T H   ( P A D _ I N D E X   =   O F F ,   S T A T I S T I C S _ N O R E C O M P U T E   =   O F F ,   I G N O R E _ D U P _ K E Y   =   O F F ,   A L L O W _ R O W _ L O C K S   =   O N ,   A L L O W _ P A G E _ L O C K S   =   O N )   O N   [ P R I M A R Y ]  
 )   O N   [ P R I M A R Y ]   T E X T I M A G E _ O N   [ P R I M A R Y ]  
  
 G O  
 / * * * * * *   O b j e c t :     T a b l e   [ d b o ] . [ t b l G e n e r i c D a t a ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 S E T   A N S I _ P A D D I N G   O N  
 G O  
 C R E A T E   T A B L E   [ d b o ] . [ t b l G e n e r i c D a t a ] (  
 	 [ i d ]   [ i n t ]   N O T   N U L L ,  
 	 [ d a t a 1 ]   [ v a r c h a r ] ( 1 0 0 )   N U L L ,  
 	 [ d a t a 2 ]   [ v a r c h a r ] ( 1 0 0 )   N U L L  
 )   O N   [ P R I M A R Y ]  
  
 G O  
 S E T   A N S I _ P A D D I N G   O F F  
 G O  
 / * * * * * *   O b j e c t :     T a b l e   [ d b o ] . [ t b l G r o u p s ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 S E T   A N S I _ P A D D I N G   O N  
 G O  
 C R E A T E   T A B L E   [ d b o ] . [ t b l G r o u p s ] (  
 	 [ i d g r o u p ]   [ i n t ]   N O T   N U L L ,  
 	 [ d e s c r i p t i o n ]   [ v a r c h a r ] ( 5 0 )   N O T   N U L L ,  
   C O N S T R A I N T   [ P K _ t b l G r o u p s ]   P R I M A R Y   K E Y   C L U S T E R E D    
 (  
 	 [ i d g r o u p ]   A S C  
 ) W I T H   ( P A D _ I N D E X   =   O F F ,   S T A T I S T I C S _ N O R E C O M P U T E   =   O F F ,   I G N O R E _ D U P _ K E Y   =   O F F ,   A L L O W _ R O W _ L O C K S   =   O N ,   A L L O W _ P A G E _ L O C K S   =   O N )   O N   [ P R I M A R Y ]  
 )   O N   [ P R I M A R Y ]  
  
 G O  
 S E T   A N S I _ P A D D I N G   O F F  
 G O  
 / * * * * * *   O b j e c t :     T a b l e   [ d b o ] . [ t b l U s e r s D B ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 S E T   A N S I _ P A D D I N G   O N  
 G O  
 C R E A T E   T A B L E   [ d b o ] . [ t b l U s e r s D B ] (  
 	 [ i d u s e r ]   [ i n t ]   N U L L ,  
 	 [ i d g r o u p ]   [ i n t ]   N O T   N U L L ,  
 	 [ d i s a b l e d ]   [ s m a l l i n t ]   N O T   N U L L ,  
 	 [ u s e r n a m e ]   [ v a r c h a r ] ( 5 0 )   N O T   N U L L ,  
 	 [ p a s s w d ]   [ v a r c h a r ] ( 5 0 )   N O T   N U L L ,  
 	 [ f i r s t n a m e ]   [ v a r c h a r ] ( 5 0 )   N U L L ,  
 	 [ l a s t n a m e ]   [ v a r c h a r ] ( 5 0 )   N U L L ,  
 	 [ a c c e s s _ d a t e ]   [ d a t e t i m e ]   N U L L ,  
 	 [ a c c e s s _ t i m e ]   [ d a t e t i m e ]   N U L L  
 )   O N   [ P R I M A R Y ]  
  
 G O  
 S E T   A N S I _ P A D D I N G   O F F  
 G O  
 / * * * * * *   O b j e c t :     T a b l e   [ d b o ] . [ t b l U s e r s E n g ]         S c r i p t   D a t e :   0 2 / 0 8 / 2 0 1 3   0 9 : 4 3 : 0 0   a . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 S E T   A N S I _ P A D D I N G   O N  
 G O  
 C R E A T E   T A B L E   [ d b o ] . [ t b l U s e r s E n g ] (  
 	 [ i d u s e r ]   [ i n t ]   N O T   N U L L ,  
 	 [ i d g r o u p ]   [ i n t ]   N O T   N U L L ,  
 	 [ d i s a b l e d ]   [ s m a l l i n t ]   N O T   N U L L ,  
 	 [ u s e r n a m e ]   [ v a r c h a r ] ( 5 0 )   N O T   N U L L ,  
 	 [ f i r s t n a m e ]   [ v a r c h a r ] ( 5 0 )   N U L L ,  
 	 [ l a s t n a m e ]   [ v a r c h a r ] ( 5 0 )   N U L L ,  
 	 [ a c c e s s _ d a t e ]   [ d a t e t i m e ]   N U L L ,  
 	 [ a c c e s s _ t i m e ]   [ d a t e t i m e ]   N U L L ,  
   C O N S T R A I N T   [ P K _ t b l U s e r s E n g ]   P R I M A R Y   K E Y   C L U S T E R E D    
 (  
 	 [ i d u s e r ]   A S C  
 ) W I T H   ( P A D _ I N D E X   =   O F F ,   S T A T I S T I C S _ N O R E C O M P U T E   =   O F F ,   I G N O R E _ D U P _ K E Y   =   O F F ,   A L L O W _ R O W _ L O C K S   =   O N ,   A L L O W _ P A G E _ L O C K S   =   O N )   O N   [ P R I M A R Y ]  
 )   O N   [ P R I M A R Y ]  
  
 G O  
 S E T   A N S I _ P A D D I N G   O F F  
 G O  
 / * * * * * *   O b j e c t :     T r i g g e r   [ d b o ] . [ T B L U S E R S D B _ B I ]   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   T R I G G E R   [ d b o ] . [ T B L U S E R S D B _ B I ]  
       O N   [ T B L U S E R S D B ]    
       I N S T E A D   O F   I N S E R T  
 A S    
     d e c l a r e   @ I d U s e r   i n t ;  
 B E G I N  
        
       S E T   N O C O U N T   O N ;  
  
       S E T   @ I d U s e r   =   ( S E L E C T   I d U s e r   F R O M   I N S E R T E D )  
 	  
       I f   (   @ I d U s e r   i s   N u l l   )  
       B e g i n  
  
             S E T   @ I d U s e r   =   N E X T   V A L U E   F O R   G E N _ T B L U S E R S D B _ I D  
  
             S E L E C T   *   I N T O   # I n s e r t e d   F R O M   I n s e r t e d  
  
             U P D A T E   # I n s e r t e d   S E T   I d U s e r   =   @ I d U s e r  
  
             I N S E R T   I N T O   t b l U s e r s D B   S E L E C T   *   F R O M   # I n s e r t e d  
  
       E n d  
       E l s e  
       B e g i n  
  
             S E T   @ I d U s e r   =   N E X T   V A L U E   F O R   G E N _ T B L U S E R S D B _ I D  
  
             I N S E R T   I N T O   t b l U s e r s D B   S E L E C T   *   F R O M   I n s e r t e d  
  
       E n d  
          
 E N D  
  
 G O  
  
 / * * * * * *   O b j e c t :     T r i g g e r   [ d b o ] . [ T B L U S E R S E N G _ B I ]   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   T R I G G E R   [ d b o ] . [ T B L U S E R S E N G _ B I ]  
       O N   [ T B L U S E R S E N G ]    
       I N S T E A D   O F   I N S E R T  
 A S    
     d e c l a r e   @ I d U s e r   i n t ;  
 B E G I N  
        
       S E T   N O C O U N T   O N ;  
  
       S E T   @ I d U s e r   =   ( S E L E C T   I d U s e r   F R O M   I N S E R T E D )  
 	  
       I f   (   @ I d U s e r   i s   N u l l   )  
       B e g i n  
  
             S E T   @ I d U s e r   =   N E X T   V A L U E   F O R   G E N _ T B L U S E R S E N G _ I D  
  
             S E L E C T   *   I N T O   # I n s e r t e d   F R O M   I n s e r t e d  
  
             U P D A T E   # I n s e r t e d   S E T   I d U s e r   =   @ I d U s e r  
  
             I N S E R T   I N T O   t b l U s e r s E n g   S E L E C T   *   F R O M   # I n s e r t e d  
  
       E n d  
       E l s e  
       B e g i n  
  
             S E T   @ I d U s e r   =   N E X T   V A L U E   F O R   G E N _ T B L U S E R S E N G _ I D  
  
             I N S E R T   I N T O   t b l U s e r s E n g   S E L E C T   *   F R O M   I n s e r t e d  
  
       E n d  
          
 E N D  
  
 G O  
  
 / * * * * * *   O b j e c t :     T r i g g e r   [ d b o ] . [ T B L G R O U P S _ B I ]   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   T R I G G E R   [ d b o ] . [ T B L G R O U P S _ B I ]  
       O N   [ T B L G R O U P S ]    
       I N S T E A D   O F   I N S E R T  
 A S    
     d e c l a r e   @ I d G r o u p   i n t ;  
 B E G I N  
        
       S E T   N O C O U N T   O N ;  
  
       S E T   @ I d G r o u p   =   ( S E L E C T   I d G r o u p   F R O M   I N S E R T E D )  
 	  
       I f   (   @ I d G r o u p   i s   N u l l   )  
       B e g i n  
  
             S E T   @ I d G r o u p   =   N E X T   V A L U E   F O R   G E N _ T B L G R O U P S _ I D  
  
             S E L E C T   *   I N T O   # I n s e r t e d   F R O M   I n s e r t e d  
  
             U P D A T E   # I n s e r t e d   S E T   I d G r o u p   =   @ I d G r o u p  
  
             I N S E R T   I N T O   t b l G r o u p s   S E L E C T   *   F R O M   # I n s e r t e d  
  
       E n d  
       E l s e  
       B e g i n  
  
             I N S E R T   I N T O   t b l G r o u p s   S E L E C T   *   F R O M   I n s e r t e d  
  
       E n d  
          
 E N D  
  
 G O  
  
 / * * * * * *   O b j e c t :     T r i g g e r   [ d b o ] . [ T B L G E N E R I C D A T A _ B I ]   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   T R I G G E R   [ d b o ] . [ T B L G E N E R I C D A T A _ B I ]  
       O N   [ T B L G E N E R I C D A T A ]    
       I N S T E A D   O F   I N S E R T  
 A S    
     d e c l a r e   @ I d   i n t ;  
 B E G I N  
        
       S E T   N O C O U N T   O N ;  
  
       S E T   @ I d   =   ( S E L E C T   I d   F R O M   I N S E R T E D )  
 	  
       I f   (   @ I d   i s   N u l l   )  
       B e g i n  
  
             S E T   @ I d   =   N E X T   V A L U E   F O R   G E N _ T B L G E N E R I C D A T A _ I D  
  
             S E L E C T   *   I N T O   # I n s e r t e d   F R O M   I n s e r t e d  
  
             U P D A T E   # I n s e r t e d   S E T   I d   =   @ I d  
  
             I N S E R T   I N T O   t b l G e n e r i c D a t a   S E L E C T   *   F R O M   # I n s e r t e d  
  
       E n d  
       E l s e  
       B e g i n  
  
             S E T   @ I d   =   N E X T   V A L U E   F O R   G E N _ T B L G E N E R I C D A T A _ I D  
  
             I N S E R T   I N T O   t b l G e n e r i c D a t a   S E L E C T   *   F R O M   I n s e r t e d  
  
       E n d  
          
 E N D  
  
 G O  
  
 / * * * * * *   O b j e c t :     T r i g g e r   [ d b o ] . [ T B L B L O B D A T A _ B I ]   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   T R I G G E R   [ d b o ] . [ T B L B L O B D A T A _ B I ]  
       O N   [ T B L B L O B D A T A ]    
       I N S T E A D   O F   I N S E R T  
 A S    
     d e c l a r e   @ I d   i n t ;  
 B E G I N  
        
       S E T   N O C O U N T   O N ;  
  
       S E T   @ I d   =   ( S E L E C T   I d   F R O M   I N S E R T E D )  
 	  
       I f   (   @ I d   i s   N u l l   )  
       B e g i n  
  
             S E T   @ I d   =   N E X T   V A L U E   F O R   G E N _ T B L B L O B D A T A _ I D  
  
             S E L E C T   *   I N T O   # I n s e r t e d   F R O M   I n s e r t e d  
  
             U P D A T E   # I n s e r t e d   S E T   I d   =   @ I d  
  
             I N S E R T   I N T O   t b l B l o b D a t a   S E L E C T   *   F R O M   # I n s e r t e d  
  
       E n d  
       E l s e  
       B e g i n  
  
             S E T   @ I d   =   N E X T   V A L U E   F O R   G E N _ T B L B L O B D A T A _ I D  
  
             I N S E R T   I N T O   t b l B l o b D a t a   S E L E C T   *   F R O M   I n s e r t e d  
  
       E n d  
          
 E N D  
  
 G O  
  
 / * * * * * *   O b j e c t :     U s e r D e f i n e d F u n c t i o n   [ d b o ] . [ G E T V A L U E ]         S c r i p t   D a t e :   0 3 / 0 8 / 2 0 1 3   0 1 : 4 4 : 3 0   p . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   F U N C T I O N   [ d b o ] . [ G E T V A L U E ] (   @ I D   i n t   )  
 R E T U R N S   i n t  
 A S  
 B E G I N  
  
       R E T U R N   3 3   *   @ I D  
  
 E N D  
  
 G O  
  
 / * * * * * *   O b j e c t :     U s e r D e f i n e d F u n c t i o n   [ d b o ] . [ G E T V A L U E S ]         S c r i p t   D a t e :   0 3 / 0 8 / 2 0 1 3   0 1 : 4 4 : 4 4   p . m .   * * * * * * /  
 S E T   A N S I _ N U L L S   O N  
 G O  
 S E T   Q U O T E D _ I D E N T I F I E R   O N  
 G O  
 C R E A T E   F U N C T I O N   [ d b o ] . [ G E T V A L U E S ] (   @ I D   i n t   )  
 R E T U R N S    
 @ R e s u l t s   T A B L E    
 (  
       v a l u e   i n t  
 )  
 A S  
 B E G I N  
  
       I N S E R T   @ R e s u l t s   S e l e c t   3 3   *   @ I D  
       I N S E R T   @ R e s u l t s   S e l e c t   6 6   *   @ I D  
 	   	  
       R E T U R N    
  
 E N D  
  
 G O  
  
 / * *   I n s e r t   b a s i c   d a t a   * * /  
 B E G I N   T R A N   T 1 ;  
  
 i n s e r t   i n t o   t b l G r o u p s (   i d g r o u p ,   d e s c r i p t i o n   )   v a l u e s (   n u l l ,   ' R e g u l a r   u s e r   g r o u p '   ) ;  
  
 i n s e r t   i n t o   t b l U s e r s D B (   i d u s e r ,   i d g r o u p ,   d i s a b l e d ,   u s e r n a m e ,   p a s s w d ,   f i r s t n a m e ,   l a s t n a m e ,   a c c e s s _ d a t e ,   a c c e s s _ t i m e   )   v a l u e s (   n u l l ,   1 ,   0 ,   ' t e s t 1 ' ,   ' 1 2 3 q w e r t y ' ,   ' S y s t e m   u s e r   f i r s t n a m e ' ,   ' S y s t e m   u s e r   l a s t n a m e ' ,   n u l l ,   n u l l   ) ;  
 i n s e r t   i n t o   t b l U s e r s D B (   i d u s e r ,   i d g r o u p ,   d i s a b l e d ,   u s e r n a m e ,   p a s s w d ,   f i r s t n a m e ,   l a s t n a m e ,   a c c e s s _ d a t e ,   a c c e s s _ t i m e   )   v a l u e s (   n u l l ,   1 ,   0 ,   ' t e s t 2 ' ,   ' 1 2 3 4 5 6 7 8 ' ,   n u l l ,   n u l l ,   n u l l ,   n u l l   ) ;  
  
 i n s e r t   i n t o   t b l U s e r s E n g (   i d u s e r ,   i d g r o u p ,   d i s a b l e d ,   u s e r n a m e ,   f i r s t n a m e ,   l a s t n a m e ,   a c c e s s _ d a t e ,   a c c e s s _ t i m e   )   v a l u e s (   n u l l ,   1 ,   0 ,   ' s y s d b a ' ,   ' D e f a u l t   s y s t e m   u s e r   f o r   F i r e b i r d ' ,   ' S y s t e m   u s e r   l a s t n a m e ' ,   n u l l ,   n u l l   ) ;  
 i n s e r t   i n t o   t b l U s e r s E n g (   i d u s e r ,   i d g r o u p ,   d i s a b l e d ,   u s e r n a m e ,   f i r s t n a m e ,   l a s t n a m e ,   a c c e s s _ d a t e ,   a c c e s s _ t i m e   )   v a l u e s (   n u l l ,   1 ,   0 ,   ' r o o t ' ,   ' D e f a u l t   s y s t e m   u s e r   f o r   M y S Q L ' ,   n u l l ,   n u l l ,   n u l l   ) ;  
  
 i n s e r t   i n t o   t b l G e n e r i c D a t a (   i d ,   d a t a 1 ,   d a t a 2   )   V a l u e s (   1 ,   ' D a t a A 1 ' ,   ' D a t a A 2 '   ) ;  
 i n s e r t   i n t o   t b l G e n e r i c D a t a (   i d ,   d a t a 1 ,   d a t a 2   )   V a l u e s (   2 ,   ' D a t a B 1 ' ,   ' D a t a B 2 '   ) ;  
 i n s e r t   i n t o   t b l G e n e r i c D a t a (   i d ,   d a t a 1 ,   d a t a 2   )   V a l u e s (   3 ,   ' D a t a C 1 ' ,   ' D a t a C 2 '   ) ;  
  
 C O M M I T   T R A N   T 1 ;  
  
 U S E   [ m a s t e r ]  
 G O  
 A L T E R   D A T A B A S E   [ S Q L S e r v e r T e s t D B ]   S E T     R E A D _ W R I T E    
 G O  
  
 