
CREATE PROCEDURE [dbo].[usp_Create_User]
	@id BIGINT NULL OUTPUT,
    @username VARCHAR(25),
    @firstName VARCHAR(25),
    @lastName VARCHAR(25),
    @dateOfBirth DATE,
    @gender VARCHAR(15),
    @email VARCHAR(30),
    @passwordHash NVARCHAR(128),
	@role VARCHAR(10),
    @dateCreated DATE
AS
    SET NOCOUNT ON
    BEGIN

    INSERT INTO [dbo].[tbl_user]
    (
        [Username],
        [FirstName],
        [LastName],
        [DateofBirth],
        [Gender],
        [Email],
        [PasswordHash],
        [DateCreated],
        [Role]
    )
    VALUES
    (
        @username,
        @firstName,
        @lastName,
        @dateofBirth,
        @gender,
        @email,
        @passwordHash,
        @dateCreated,
        @role
    )

    SELECT @id = SCOPE_IDENTITY();
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Retrieve_User_By_Id]
    @id BIGINT
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM dbo.tbl_user
    WHERE Id = @id AND Deleted = 0 

	RETURN @@Error

END
GO

CREATE PROCEDURE [dbo].[usp_Retrieve_User_By_Email]
	@key VARCHAR(30)

AS
BEGIN

	SET NOCOUNT ON;

	SELECT *
	FROM dbo.tbl_user
	WHERE Email = @key  AND Deleted = 0 

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Retrieve_User_By_Username]
	@key VARCHAR(30)
AS
BEGIN
	
	SET NOCOUNT ON;

    SELECT * 
	FROM dbo.tbl_user
	WHERE Username = @key  AND Deleted = 0 

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Retrieve_All_By_Nationality]
	@key VARCHAR(50),
	@pageNumber INT,
	@pageSize INT
AS
BEGIN
	
	SET NOCOUNT ON;

    SELECT * 
	FROM dbo.tbl_user
	WHERE Nationality = @key  AND Deleted = 0
	ORDER BY [id] ASC
	OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Retrieve_All_Users]
	@key VARCHAR(50),
	@pageNumber INT,
	@pageSize INT
AS
BEGIN
	
	SET NOCOUNT ON;

    SELECT * 
	FROM dbo.tbl_user
	ORDER BY [id] ASC
	OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Update_User]
	@username VARCHAR(25),
    @firstName VARCHAR(25),
    @lastName VARCHAR(25),
	@email VARCHAR(30),
	@passwordHash NVARCHAR(128),
	@gender VARCHAR(15),
    @dateOfBirth DATE,
    @nationality VARCHAR(40),
	@phoneNumber VARCHAR(20),
	@location VARCHAR(50),
	@genre VARCHAR(50),
	@biography VARCHAR(100),
	@website VARCHAR(50),
	@profileImagePath VARCHAR(50),
	@coverImagePath VARCHAR(50),
	@following BIGINT,
	@followers BIGINT,
	@role VARCHAR(10),
	@twitterProfileUrl VARCHAR(30),
	@verified BIT,
	@dateCreated DATE,
	@lockOutEnabled BIT,
	@lockOutEndDateUtc DATE,
	@accessFailedCount INT
AS
BEGIN

	SET NOCOUNT ON;

	UPDATE [dbo].[tbl_user]
	SET 
		Username = COALESCE(@username,Username),
		FirstName = COALESCE(@firstName,FirstName),
		LastName = COALESCE(@lastName,LastName),
		PasswordHash = COALESCE(@passwordHash,PasswordHash),
		Gender = COALESCE(@gender,Gender),
		DateofBirth = COALESCE(@dateOfBirth,DateOfBirth),
		Nationality = COALESCE(@nationality,Nationality),
		PhoneNumber = COALESCE(@phoneNumber,PhoneNumber),
		[Location] = COALESCE(@location,[location]),
		Genre = COALESCE(@genre,Genre),
		Biography = COALESCE(@biography,Biography),
		Website = COALESCE(@website,Website),
		ProfileImagePath = COALESCE(@profileImagePath,ProfileImagePath),
		CoverImagePath = COALESCE(@coverImagePath,CoverImagePath),
		[Following] = COALESCE(@following,[Following]),
		Followers = COALESCE(@followers,Followers),
		[Role] = COALESCE(@role,[Role]),
		TwitterProfileUrl = COALESCE(@twitterProfileUrl,TwitterProfileUrl),
		Verified = COALESCE(@verified,Verified),
		DateCreated = COALESCE(@dateCreated,DateCreated),
		LockOutEnabled = COALESCE(@lockOutEnabled,LockOutEnabled),
		LockOutEndDateUtc = COALESCE(@lockOutEndDateUtc,lockOutEndDateUtc),
		AccessFailedCount = COALESCE(@accessFailedCount,AccessFailedCount)
	WHERE Email = @email AND Deleted = 0

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Delete_User_By_Unique_Key]
	@key VARCHAR
AS
BEGIN

	SET NOCOUNT ON;

	UPDATE [dbo].[tbl_user]
	SET Deleted = 1 
	WHERE Email = @key

	RETURN @@Error
END
