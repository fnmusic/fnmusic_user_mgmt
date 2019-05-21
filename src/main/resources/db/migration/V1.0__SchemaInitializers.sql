
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[tbl_user](
	[Id] BIGINT IDENTITY(1,1) NOT NULL,
	[Username] VARCHAR(25) NULL,
	[FirstName] VARCHAR(25) NOT NULL,
	[LastName] VARCHAR(25) NOT NULL,
	[Email] VARCHAR(30) NOT NULL,
	[EmailConfirmed] VARCHAR(6) NULL,
	[PasswordHash] NVARCHAR(128) NOT NULL,
	[Gender] VARCHAR(15) NOT NULL,
	[DateofBirth] DATE NOT NULL,
	[Nationality] VARCHAR(40) NULL,
	[SecurityStamp] VARCHAR(128) NULL,
	[PhoneNumber] VARCHAR(20) NULL,
	[PhoneNumberConfirmed] VARCHAR(5) NULL,
	[Location] VARCHAR(50) NULL,
	[Genre] VARCHAR(50) NULL,
	[Biography] VARCHAR(100) NULL,
	[Website] VARCHAR(50) NULL,
	[ProfileImagePath] VARCHAR(50) NULL,
	[CoverImagePath] VARCHAR(50) NULL,
	[Following] BIGINT NULL,
	[Followers] BIGINT NULL,
	[TwitterProfileUrl] VARCHAR(30) NULL,
	[Role] VARCHAR(10) NOT NULL,
	[Verified] BIT NULL,
	[DateCreated] DATE NOT NULL,
	[TwofactorEnabled] VARCHAR(5) NULL,
	[LockOutEnabled] [bit] NULL,
	[LockOutEndDateUtc] DATE NULL,
	[AccessFailedCount] INT NULL,
	[Deleted] BIT NULL,
	[DateDeleted] DATE NULL,
 CONSTRAINT [PK_User_Tbl] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Following]  DEFAULT ((0)) FOR [Following]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Followers]  DEFAULT ((0)) FOR [Followers]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Verified]  DEFAULT ((0)) FOR [Verified]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_LockOutEnabled]  DEFAULT ((0)) FOR [LockOutEnabled]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_AccessFailedCount]  DEFAULT ((0)) FOR [AccessFailedCount]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Deleted]  DEFAULT ((0)) FOR [Deleted]
GO

CREATE TABLE [dbo].[tbl_account_activation]
(
	[Id] BIGINT IDENTITY(1,1) NOT NULL,
	[Email] VARCHAR(30) NOT NULL,
	[Token] VARCHAR(30) NOT NULL,
	CONSTRAINT [PK_Account_Activation_Tbl] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[tbl_password_reset](
	[Id] BIGINT IDENTITY(1,1) NOT NULL,
	[Email] VARCHAR(30) NOT NULL,
	[Token] VARCHAR(50) NOT NULL,
 CONSTRAINT [PK_tbl_password_reset] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO





