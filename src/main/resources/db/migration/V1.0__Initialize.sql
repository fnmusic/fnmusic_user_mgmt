USE [fnmusic_user_mgmt]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[tbl_user](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Username] [varchar](50) NULL,
	[FirstName] [varchar](25) NOT NULL,
	[LastName] [varchar](25) NOT NULL,
	[Email] [varchar](30) NOT NULL,
	[EmailConfirmed] [varchar](6) NULL,
	[PasswordHash] [nvarchar](128) NOT NULL,
	[Gender] [varchar](12) NOT NULL,
	[DateofBirth] [date] NOT NULL,
	[Nationality] [varchar](40) NULL,
	[SecurityStamp] [varchar](128) NULL,
	[PhoneNumber] [varchar](20) NULL,
	[PhoneNumberConfirmed] [varchar](5) NULL,
	[Location] [varchar](50) NULL,
	[PrimaryGenre] [varchar](50) NULL,
	[Biography] [varchar](100) NULL,
	[Website] [varchar](50) NULL,
	[ProfileImagePath] [varchar](50) NULL,
	[CoverImagePath] [varchar](50) NULL,
	[Following] [int] NULL,
	[Followers] [int] NULL,
	[TwitterProfileUrl] [varchar](30) NULL,
	[facebookProfileUrl] [varchar](30) NULL,
	[Role] [varchar](10) NOT NULL,
	[AccountVerificationStatus] [bit] NULL,
	[DateCreated] [date] NOT NULL,
	[TwofactorEnabled] [varchar](5) NULL,
	[LockOutEnabled] [bit] NULL,
	[LockOutEndDateUtc] [date] NULL,
	[AccessFailedCount] [int] NULL,
	[LockAccountEnabled] [bit] NULL,
	[AccountStatus] [varchar](10) NULL,
	[Deleted] [bit] NULL,
	[DateDeleted] [date] NULL,
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

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_AccountVerificationStatus]  DEFAULT ((0)) FOR [AccountVerificationStatus]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_LockOutEnabled]  DEFAULT ((0)) FOR [LockOutEnabled]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_AccessFailedCount]  DEFAULT ((0)) FOR [AccessFailedCount]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_LockAccountEnabled]  DEFAULT ((0)) FOR [LockAccountEnabled]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Deleted]  DEFAULT ((0)) FOR [Deleted]
GO

--tbl password reset
CREATE TABLE [dbo].[tbl_password_reset](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Email] [varchar](30) NOT NULL,
	[PasswordResetToken] [varchar](50) NOT NULL,
 CONSTRAINT [PK_tbl_password_reset] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


