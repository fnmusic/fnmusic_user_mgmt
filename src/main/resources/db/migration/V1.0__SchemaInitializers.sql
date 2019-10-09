
CREATE TABLE [dbo].[tbl_user](
	[Id] BIGINT IDENTITY(1,1) NOT NULL,
	[Username] VARCHAR(25) NULL,
	[FirstName] VARCHAR(25) NOT NULL,
	[LastName] VARCHAR(25) NOT NULL,
	[Email] VARCHAR(30) NOT NULL,
	[EmailConfirmed] BIT NOT NULL,
	[PasswordHash] VARCHAR(MAX) NOT NULL,
	[Gender] VARCHAR(15) NULL,
	[DateOfBirth] DATE NULL,
	[MonthAndDay] VARCHAR(20) NULL,
	[Year] VARCHAR(20) NULL,
	[Nationality] VARCHAR(40) NULL,
	[SecurityStamp] VARCHAR(128) NULL,
	[Phone] VARCHAR(20) NULL,
	[PhoneConfirmed] BIT NOT NULL,
	[Location] VARCHAR(50) NULL,
	[Genre] VARCHAR(50) NULL,
	[Biography] VARCHAR(100) NULL,
	[Website] VARCHAR(50) NULL,
	[ProfileImagePath] VARCHAR(50) NULL,
	[CoverImagePath] VARCHAR(50) NULL,
	[Following] BIGINT NOT NULL,
	[Followers] BIGINT NOT NULL,
	[TwitterProfile] VARCHAR(50) NULL,
	[FacebookProfile] VARCHAR(50) NULL,
	[YoutubePage] VARCHAR(50) NULL,
	[Role] VARCHAR(20) NOT NULL,
	[Verified] BIT NOT NULL,
	[DateCreated] DATE NOT NULL,
	[TwoFactorEnabled] BIT NOT NULL,
	[LockOutEnabled] BIT NOT NULL,
	[LockOutEndDateUtc] DATETIME NULL,
	[AccessFailedCount] INT NOT NULL,
	[Activated] BIT NOT NULL,
	[PasswordResetProtection] BIT NOT NULL,
	[Deleted] BIT NOT NULL,
	[DateDeleted] DATE NULL,
	[Suspended] BIT NOT NULL,
	[SuspensionEndDate] DATETIME NULL
 CONSTRAINT [PK_User_Tbl] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [Df_tbl_user_EmailConfirmed] DEFAULT ((0)) FOR [EmailConfirmed]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [Df_tbl_user_PhoneConfirmed] DEFAULT ((0)) FOR [PhoneConfirmed]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Following]  DEFAULT ((0)) FOR [Following]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Followers]  DEFAULT ((0)) FOR [Followers]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Verified]  DEFAULT ((0)) FOR [Verified]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_TwoFactorEnabled]  DEFAULT ((0)) FOR [TwoFactorEnabled]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_LockOutEnabled]  DEFAULT ((0)) FOR [LockOutEnabled]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_AccessFailedCount]  DEFAULT ((0)) FOR [AccessFailedCount]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Activated] DEFAULT ((0)) FOR [Activated]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_PasswordResetProtection] DEFAULT ((0)) FOR [PasswordResetProtection]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Deleted]  DEFAULT ((0)) FOR [Deleted]
GO

ALTER TABLE [dbo].[tbl_user] ADD  CONSTRAINT [DF_tbl_user_Suspended]  DEFAULT ((0)) FOR [Suspended]
GO

CREATE TABLE [dbo].[tbl_login_verification]
(
    [Id] BIGINT IDENTITY(1,1) NOT NULL,
    [Email] VARCHAR(30) NOT NULL,
    [Token] VARCHAR(30) NOT NULL,
    [ExpiryDate] DATE NOT NULL,
    CONSTRAINT [PK_Login_Verification_Tbl] PRIMARY KEY CLUSTERED
(
  [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[tbl_account_activation]
(
	[Id] BIGINT IDENTITY(1,1) NOT NULL,
	[Email] VARCHAR(30) NOT NULL,
	[Token] VARCHAR(30) NOT NULL,
	[ExpiryDate] DATE NOT NULL,
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
	[ExpiryDate] DATE NOT NULL,
    CONSTRAINT [PK_Password_Reset_Tbl] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[tbl_forgot_password_verification](
    [Id] BIGINT IDENTITY(1,1) NOT NULL,
    [Email] VARCHAR(30) NOT NULL,
    [Token] VARCHAR(15) NOT NULL,
    [ExpiryDate] DATE NOT NULL,
    CONSTRAINT [PK_Forgot_Password_Verification _Tbl] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


CREATE TABLE [dbo].[tbl_follow] (
    [Id] BIGINT IDENTITY(1,1) NOT NULL,
    [UserId] BIGINT NOT NULL,
    [FanId] BIGINT NOT NULL,
    CONSTRAINT [PK_Follower_Tbl] PRIMARY KEY CLUSTERED
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[tbl_follow] WITH CHECK ADD CONSTRAINT [FK_tbl_follow_tbl_user_UserId] FOREIGN KEY (UserId) REFERENCES [dbo].[tbl_user](Id)
GO

ALTER TABLE [dbo].[tbl_follow] WITH CHECK ADD CONSTRAINT [FK_tbl_follow_tbl_user_FanId] FOREIGN KEY (FanId) REFERENCES [dbo].[tbl_user](Id)
GO

CREATE TABLE [dbo].[tbl_old_passwords] (
    [Id] BIGINT IDENTITY(1,1) NOT NULL,
    [UserId] BIGINT NOT NULL,
    [PasswordHash] VARCHAR(MAX) NOT NULL
    CONSTRAINT [PK_Old_Passwords_Tbl] PRIMARY KEY CLUSTERED
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[tbl_old_passwords] WITH CHECK ADD CONSTRAINT [Fk_tbl_tbl_old_passwords_tbl_user_UserId] FOREIGN KEY (UserId) REFERENCES [dbo].[tbl_user](Id)
GO

CREATE TABLE [dbo].[tbl_roles] (
    [Id] INT IDENTITY(1,1) NOT NULL,
    [Name] VARCHAR(20) NOT NULL,
    [Description] VARCHAR(MAX) NULL,
    CONSTRAINT [PK_Roles_Tbl] PRIMARY KEY CLUSTERED
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[tbl_features] (
    [Id] INT IDENTITY(1,1) NOT NULL,
    [Name] VARCHAR(30) NOT NULL,
    [RoleId] INT NOT NULL,
    [Description] VARCHAR(MAX) NULL,
    CONSTRAINT [PK_Features_Tbl] PRIMARY KEY CLUSTERED
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[tbl_features] WITH CHECK ADD CONSTRAINT [FK_tbl_features_tbl_roles] FOREIGN KEY (RoleId) REFERENCES [dbo].[tbl_roles] (Id)
GO

CREATE TABLE [dbo].[tbl_permissions] (
    [Id] INT IDENTITY(1,1) NOT NULL,
    [Name] VARCHAR(30) NOT NULL,
    [Description] VARCHAR(MAX) NULL,
    CONSTRAINT [PK_Permissions_Tbl] PRIMARY KEY CLUSTERED
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[tbl_features_permissions] (
    [Id] INT IDENTITY(1,1) NOT NULL,
    [FeatureId] INT NOT NULL,
    [PermissionId] INT NOT NULL,
    CONSTRAINT [PK_Features_Permissions_Tbl] PRIMARY KEY CLUSTERED
(
    [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[tbl_phone_verification] (
    [Id] BIGINT IDENTITY(1,1) NOT NULL,
    [Phone] VARCHAR(30) NOT NULL,
    [Token] VARCHAR(30) NOT NULL,
    [ExpiryDate] DATE NOT NULL,
   CONSTRAINT [PK_Phone_Verification_Tbl] PRIMARY KEY CLUSTERED
(
     [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[tbl_email_verification] (
    [Id] BIGINT IDENTITY(1,1) NOT NULL,
    [Email] VARCHAR(30) NOT NULL,
    [Token] VARCHAR(30) NOT NULL,
    [ExpiryDate] DATE NOT NULL,
    CONSTRAINT [PK_Email_Verification_Tbl] PRIMARY KEY CLUSTERED
(
  [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[tbl_two_factor_verification] (
    [Id] BIGINT IDENTITY(1,1) NOT NULL,
    [Phone] VARCHAR(30) NOT NULL,
    [Token] VARCHAR(30) NOT NULL,
    [ExpiryDate] DATE NOT NULL,
    CONSTRAINT [PK_TwoFactor_Verification_Tbl] PRIMARY KEY CLUSTERED
(
  [Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
)
