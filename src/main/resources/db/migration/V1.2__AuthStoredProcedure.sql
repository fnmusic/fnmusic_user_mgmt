
CREATE PROCEDURE [dbo].[usp_Increase_Login_Attempt]
    @status BIT NULL OUTPUT,
	@email VARCHAR(30)
AS
BEGIN
	DECLARE @accessFailedCount int
	DECLARE @lockOutEnabled bit
	
	SET NOCOUNT ON;

	UPDATE dbo.tbl_user
	SET AccessFailedCount += 1
	WHERE Email = @email

	SELECT 
		@accessFailedCount = AccessFailedCount,
		@lockOutEnabled = LockOutEnabled
	FROM dbo.tbl_user
	WHERE Email = @email

	IF @accessFailedCount >= 5 AND @lockOutEnabled = 0
	BEGIN 	
		UPDATE dbo.tbl_user
		SET LockOutEnabled = 1
		WHERE Email = @email
	END

    SET @status = 1
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Submit_Activation_Token]
    @status BIT NULL OUTPUT,	
	@id BIGINT NULL OUTPUT,
	@email VARCHAR(30),
	@token VARCHAR(25)
AS
BEGIN
	
	SET NOCOUNT ON;

	INSERT INTO [dbo].[tbl_account_activation]
	(
		Email,
		Token
	)
	VALUES (
		@email,
		@token
	)

    SET @status = 1;
	SET @id = SCOPE_IDENTITY();
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Retrieve_Activation_Token]
    @status BIT NULL OUTPUT,
	@email VARCHAR(30)
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT * 
	FROM [dbo].[tbl_account_activation]
	WHERE Email = @email

    SET @status = 1
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Activate_Account]
    @status BIT NULL OUTPUT,
	@email VARCHAR(30),
	@token VARCHAR(25)
AS
BEGIN
	
	SET NOCOUNT ON;

	UPDATE [dbo].[tbl_user]
	SET [Verified] = 1
	WHERE Email = @email

	DELETE FROM [dbo].[tbl_account_activation]
	WHERE Email = @email

    SET @status = 1
	RETURN @@Error
END 
GO

CREATE PROCEDURE [dbo].[usp_Submit_Password_Reset_Token]
    @status BIT NULL OUTPUT,
	@id BIGINT NULL OUTPUT,
	@email VARCHAR(30),
	@token VARCHAR(25)
AS
BEGIN
	
	SET NOCOUNT ON;

	INSERT INTO [dbo].[tbl_password_reset]
	(
		Email,
		Token
	)
	VALUES (
		@email,
		@token
	)

    SET @status = 1;
	SET @id = SCOPE_IDENTITY();
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Retrieve_Password_Reset_Token]
    @status BIT NULL OUTPUT,
	@email VARCHAR(30)
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT * 
	FROM [dbo].[tbl_password_reset]
	WHERE Email = @email

    SET @status = 1
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_Reset_Password]
    @status BIT NULL OUTPUT,
	@email VARCHAR(30),
	@passwordHash VARCHAR(25)
AS
BEGIN
	
	SET NOCOUNT ON;

	UPDATE [dbo].[tbl_user]
	SET [PasswordHash] = @passwordHash
	WHERE Email = @email

	DELETE FROM [dbo].[tbl_password_reset]
	WHERE Email = @email

    SET @status = 1
	RETURN @@Error
END 
GO

