
CREATE PROCEDURE [dbo].[usp_create_user]
	@id BIGINT NULL OUTPUT,
    @username VARCHAR(25),
    @phone VARCHAR(20),
    @name VARCHAR(25),
    @email VARCHAR(30),
    @passwordHash NVARCHAR(128),
	@role VARCHAR(20),
    @dateCreated DATE
AS
BEGIN

    SET NOCOUNT ON

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_user] WHERE Email = @email)
    BEGIN
        INSERT INTO [dbo].[tbl_user]
        (
            [Username],
            [Name],
            [Email],
            [Phone],
            [PasswordHash],
            [DateCreated],
            [Role]
        )
        VALUES
        (
            @username,
            @name,
            @email,
            @phone,
            @passwordHash,
            @dateCreated,
            @role
        )
    END

    SELECT @id = SCOPE_IDENTITY();
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_user_by_id]
    @userId BIGINT
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
    FROM dbo.tbl_user
    WHERE Id = @userId AND Deleted = 0

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_user_by_email]
	@key VARCHAR(30)
AS
BEGIN

	SET NOCOUNT ON

	SELECT *
	FROM dbo.tbl_user
	WHERE Email = @key  AND Deleted = 0

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_user_by_username]
	@key VARCHAR(30)
AS
BEGIN

	SET NOCOUNT ON

    SELECT *
	FROM dbo.tbl_user
	WHERE Username = @key AND Deleted = 0

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_user_by_phone]
    @key VARCHAR(20)
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
    FROM dbo.tbl_user
    WHERE Phone = @key AND Deleted = 0

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_by_nationality]
	@key VARCHAR(50),
	@pageNumber INT,
	@pageSize INT,
	@no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
	FROM dbo.tbl_user
	WHERE Nationality = @key  AND Deleted = 0
	ORDER BY [Id] ASC
	OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

	SET @no_of_records = (SELECT COUNT(*)
    FROM dbo.tbl_user
    WHERE Nationality = @key)

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_users]
	@pageNumber INT,
	@pageSize INT,
	@no_of_records BIGINT NULL OUTPUT
AS
BEGIN

	SET NOCOUNT ON

    SELECT *
	FROM dbo.tbl_user
	ORDER BY [Id] ASC
	OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

	SET @no_of_records = (SELECT COUNT(*)
    FROM dbo.tbl_user)

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_update_username]
    @userId BIGINT,
    @username VARCHAR(50)
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET Username = @username
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_update_email]
    @userId BIGINT,
    @email VARCHAR(30)
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET Email = @email
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE[dbo].[usp_update_email_confirmed]
    @userId BIGINT,
    @Status BIT
AS
BEGIN

    SET NOCOUNT ON;

    UPDATE [dbo].[tbl_user]
    SET EmailConfirmed = @status
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_update_phone]
    @userId BIGINT,
    @phone VARCHAR(20)
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET Phone = @phone
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE[dbo].[usp_update_phone_confirmed]
    @userId BIGINT,
    @Status BIT
AS
BEGIN

    SET NOCOUNT ON;

    UPDATE [dbo].[tbl_user]
    SET PhoneConfirmed = @status
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_update_nationality]
    @userId BIGINT,
    @country VARCHAR(40)
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET Nationality = @country
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_update_two_factor]
    @userId BIGINT,
    @enabled BIT
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET TwoFactorEnabled = @enabled
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_update_password_reset_protection]
    @userId BIGINT,
    @status BIT
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET PasswordResetProtection = @status
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_update_user]
    @id BIGINT,
    @name VARCHAR(25),
	@email VARCHAR(30),
    @dateOfBirth DATE,
    @monthAndDay VARCHAR(20),
    @year VARCHAR(20),
	@location VARCHAR(50),
	@genre VARCHAR(50),
	@biography VARCHAR(100),
	@website VARCHAR(50),
	@profileImagePath VARCHAR(50),
	@coverImagePath VARCHAR(50),
	@twitterProfile VARCHAR(50),
	@facebookProfile VARCHAR(50),
	@youtubePage VARCHAR(50)
AS
BEGIN

	SET NOCOUNT ON

	UPDATE [dbo].[tbl_user]
	SET
	    Name = COALESCE(@name,Name),
		DateOfBirth = COALESCE(@dateOfBirth,DateOfBirth),
		MonthAndDay = COALESCE(@monthAndDay,MonthAndDay),
		[Year] = COALESCE(@year,[Year]),
		[Location] = COALESCE(@location,[location]),
		Genre = COALESCE(@genre,Genre),
		Biography = COALESCE(@biography,Biography),
		Website = COALESCE(@website,Website),
		ProfileImagePath = COALESCE(@profileImagePath,ProfileImagePath),
		CoverImagePath = COALESCE(@coverImagePath,CoverImagePath),
		TwitterProfile = COALESCE(@twitterProfile,TwitterProfile),
		FacebookProfile = COALESCE(@facebookProfile,FacebookProfile),
		YoutubePage = COALESCE(@youtubePage,YoutubePage)
	WHERE Id = @id AND Deleted = 0

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_update_activation_status]
    @userId BIGINT,
    @status BIT,
    @dateDeleted DATE NULL
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET Activated = @status, DateDeleted = @dateDeleted
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_old_passwords]
    @userId BIGINT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON

    SELECT UserId AS Id,PasswordHash
    FROM [dbo].[tbl_old_passwords]
    WHERE UserId = @userId

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[tbl_old_passwords]
    WHERE UserId = @userId)

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_update_password]
    @userId BIGINT,
    @password VARCHAR(MAX)
AS
BEGIN

    DECLARE @old_password NVARCHAR(128)

    SET NOCOUNT ON

    SELECT @old_password = PasswordHash
    FROM [dbo].[tbl_user]
    WHERE Id = @userId

    UPDATE [dbo].[tbl_user]
    SET PasswordHash = @password
    WHERE Id = @userId

    INSERT INTO [dbo].[tbl_old_passwords]
    (
        UserId,
        PasswordHash
    )
    VALUES
    (
        @userId,
        @old_password
    )

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_delete_user_by_id]
	@userId BIGINT
AS
BEGIN

	SET NOCOUNT ON

	UPDATE [dbo].[tbl_user]
	SET Deleted = 1
	WHERE [Id] = @userId

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_follow_user]
    @userId BIGINT,
    @followerId BIGINT
AS
BEGIN

    DECLARE @followerCount BIGINT
    DECLARE @followingCount BIGINT

    SET NOCOUNT ON

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_follow] WHERE UserId = @userId AND FollowerId = @followerId)
    BEGIN
        INSERT INTO [dbo].[tbl_follow]
        (
            UserId,
            FollowerId
        )
        VALUES
        (
            @userId,
            @followerId
        )
    END

    SELECT @followerCount = COUNT(*)
    FROM [dbo].[tbl_follow]
    WHERE UserId = @userId

    UPDATE [dbo].[tbl_user]
    SET [Followers] = @followerCount
    WHERE Id = @userId

    SELECT @followingCount = COUNT(*)
    FROM [dbo].[tbl_follow]
    WHERE FollowerId = @followerId

    UPDATE [dbo].[tbl_user]
    SET [Following] = @followingCount
    WHERE Id = @followerId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_unfollow_user]
    @userId BIGINT,
    @followerId BIGINT
AS
BEGIN

    DECLARE @followerCount BIGINT
    DECLARE @followingCount BIGINT

    SET NOCOUNT ON

    DELETE FROM [dbo].[tbl_follow]
    WHERE UserId = @userId AND FollowerId = @followerId

    SELECT @followerCount = COUNT(*)
    FROM [dbo].[tbl_follow]
    WHERE UserId = @userId

    UPDATE [dbo].[tbl_user]
    SET [Followers] = @followerCount
    WHERE Id = @userId

    SELECT @followingCount = COUNT(*)
    FROM [dbo].[tbl_follow]
    WHERE FollowerId = @followerId

    UPDATE [dbo].[tbl_user]
    SET [Following] = @followingCount
    WHERE Id = @followerId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_get_followers]
    @userId BIGINT,
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
    FROM [dbo].[uv_followers]
    WHERE UserId = @userId
    ORDER BY [Id] DESC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[uv_followers]
    WHERE UserId = @userId)

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_get_following]
    @userId BIGINT,
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
    FROM [dbo].[uv_following]
    WHERE FollowerId = @userId
    ORDER BY [Id] DESC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[uv_following]
    WHERE FollowerId = @userId)

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_is_follower]
    @userId BIGINT,
    @followerId BIGINT
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
    FROM [dbo].[uv_followers]
    WHERE Id = @followerId AND UserId = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_is_following]
    @userId BIGINT,
    @followerId BIGINT
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
    FROM [dbo].[uv_following]
    WHERE Id = @userId AND FollowerId = @followerId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_suspend_user]
    @userId BIGINT,
    @suspensionEndDate DATETIME
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET Suspended = 1
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_suspended_users]
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    DECLARE @record_count BIGINT

    SET NOCOUNT ON

    SELECT *
    FROM dbo.tbl_user
    WHERE Suspended = 1
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

    SET @no_of_records = (SELECT COUNT(*)
    FROM dbo.tbl_user
    WHERE Suspended = 1)

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_unsuspend_user]
    @userId BIGINT
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET Suspended = 0, SuspensionEndDate = NULL
    WHERE Id = @userId

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_locked_users]
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    DECLARE @record_count BIGINT

    SET NOCOUNT ON

    SELECT *
    FROM dbo.tbl_user
    WHERE LockOutEnabled = 1
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

    SET @no_of_records = (SELECT COUNT(*)
    FROM dbo.tbl_user
    WHERE LockOutEnabled = 1)

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_unlock_user_by_id]
    @userId BIGINT
AS
BEGIN

    SET NOCOUNT ON

    UPDATE [dbo].[tbl_user]
    SET LockOutEnabled = 0, LockOutEndDateUtc = NULL, AccessFailedCount = 0
    WHERE Id = @userId

    RETURN @@Error
END
GO
