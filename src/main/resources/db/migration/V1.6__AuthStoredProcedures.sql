
CREATE PROCEDURE [dbo].[usp_increase_login_attempt]
    @email VARCHAR(30),
	@lockOutEndDate DATETIME
AS
BEGIN
	DECLARE @accessFailedCount int
	DECLARE @lockOutEnabled bit
	
	SET NOCOUNT ON;

	UPDATE dbo.tbl_user
	SET AccessFailedCount += 1
	WHERE Email = @email

	SELECT @accessFailedCount = AccessFailedCount, @lockOutEnabled = LockOutEnabled
	FROM dbo.tbl_user
	WHERE Email = @email

	IF @accessFailedCount >= 5 AND @lockOutEnabled = 0
	BEGIN 	
		UPDATE dbo.tbl_user
		SET LockOutEnabled = 1, LockOutEndDateUtc = @lockOutEndDate
		WHERE Email = @email
	END

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_submit_login_verification_token]
    @id BIGINT NULL OUTPUT,
    @email VARCHAR(30),
    @token VARCHAR(30),
    @expiryDate DATE
AS
BEGIN

    IF NOT EXISTS (SELECT *
        FROM dbo.tbl_login_verification
        WHERE Email = @email)
        BEGIN
            INSERT INTO dbo.tbl_login_verification
            (
                Email,
                Token,
                ExpiryDate
            )
            VALUES
            (
                @email,
                @token,
                @expiryDate
            )
        END
    ELSE
        BEGIN
            UPDATE dbo.tbl_login_verification
            SET Token = @token, ExpiryDate = @expiryDate
            WHERE Email = @email
        END

    SET @id = SCOPE_IDENTITY();
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_login_verification_token]
    @email VARCHAR(30)
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM dbo.tbl_login_verification
    WHERE Email = @email

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_login_verification_tokens]
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM dbo.tbl_login_verification
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[tbl_login_verification])

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_delete_login_verification_token]
    @email VARCHAR(30)
AS
BEGIN

    SET NOCOUNT ON;

    DELETE
    FROM dbo.tbl_login_verification
    WHERE Email = @email

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_submit_activation_token]
    @id BIGINT NULL OUTPUT,
	@email VARCHAR(30),
	@token VARCHAR(25),
	@expiryDate DATE
AS
BEGIN

	SET NOCOUNT ON;

	IF NOT EXISTS (SELECT *
        FROM [dbo].[tbl_account_activation]
        WHERE Email = @email)
        BEGIN
            INSERT INTO [dbo].[tbl_account_activation]
            (
                Email,
                Token,
                ExpiryDate
            )
            VALUES (
                @email,
                @token,
                @expiryDate
            )
        END
	ELSE
        BEGIN
            UPDATE [dbo].[tbl_account_activation]
            SET Token = @token
            WHERE Email = @email
        END

	SET @id = SCOPE_IDENTITY();
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_activation_token]
    @email VARCHAR(30)
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT * 
	FROM [dbo].[tbl_account_activation]
	WHERE Email = @email

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_activation_tokens]
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
    FROM [dbo].[tbl_account_activation]
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[tbl_account_activation])

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_delete_activation_token]
    @email VARCHAR(30)
AS
BEGIN

    SET NOCOUNT ON;

    DELETE
    FROM [dbo].[tbl_account_activation]
    WHERE Email = @email

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_activate_account]
	@email VARCHAR(30),
	@token VARCHAR(25)
AS
BEGIN
	
	SET NOCOUNT ON;

	UPDATE [dbo].[tbl_user]
	SET [EmailConfirmed] = 1, Activated = 1
	WHERE Email = @email

	DELETE FROM [dbo].[tbl_account_activation]
	WHERE Email = @email

	RETURN @@Error
END 
GO

CREATE PROCEDURE [dbo].[usp_submit_forgot_password_verification_token]
    @email VARCHAR(30),
    @token VARCHAR(25),
    @expiryDate VARCHAR(25),
    @id BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT *
        FROM [dbo].[tbl_forgot_password_verification]
        WHERE Email = @email)
        BEGIN
            INSERT INTO [dbo].[tbl_forgot_password_verification]
            (
                Email,
                Token,
                ExpiryDate
            )
            VALUES (
                @email,
                @token,
                @expiryDate
            )
        END
    ELSE
        BEGIN
            UPDATE [dbo].[tbl_forgot_password_verification]
            SET Token = @token, ExpiryDate = @expiryDate
            WHERE Email = @email
        END

    SET @id = SCOPE_IDENTITY();
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_forgot_password_verification_token]
    @email VARCHAR(30)
AS
BEGIN

	SET NOCOUNT ON;

	SELECT *
	FROM [dbo].[tbl_forgot_password_verification]
	WHERE Email = @email

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_forgot_password_verification_tokens]
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
    FROM [dbo].[tbl_forgot_password_verification]
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[tbl_forgot_password_verification])

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_delete_forgot_password_verification_token]
    @email VARCHAR(30)
AS
BEGIN

    SET NOCOUNT ON;

    DELETE
    FROM [dbo].[tbl_forgot_password_verification]
    WHERE Email = @email

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_submit_password_reset_token]
	@email VARCHAR(30),
	@token VARCHAR(25),
	@expiryDate DATE,
	@id BIGINT NULL OUTPUT
AS
BEGIN

	SET NOCOUNT ON;

	IF NOT EXISTS (SELECT *
        FROM [dbo].[tbl_password_reset]
        WHERE Email = @email)
        BEGIN
            INSERT INTO [dbo].[tbl_password_reset]
            (
                Email,
                Token,
                ExpiryDate
            )
            VALUES (
                @email,
                @token,
                @expiryDate
            )
        END
    ELSE
        BEGIN
            UPDATE [dbo].[tbl_password_reset]
            SET Token = @token, ExpiryDate = @expiryDate
            WHERE Email = @email
	    END

	SET @id = SCOPE_IDENTITY();
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_password_reset_token]
    @email VARCHAR(30)
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT * 
	FROM [dbo].[tbl_password_reset]
	WHERE Email = @email

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_password_reset_tokens]
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT
AS
BEGIN

    SET NOCOUNT ON

    SELECT *
    FROM [dbo].[tbl_password_reset]
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[tbl_password_reset])

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_delete_password_reset_token]
    @email VARCHAR(30)
AS
BEGIN

    SET NOCOUNT ON;

    DELETE
    FROM [dbo].[tbl_password_reset]
    WHERE Email = @email

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_reset_password]
	@email VARCHAR(30),
	@passwordHash VARCHAR(MAX)
AS
BEGIN
    DECLARE @userId BIGINT
	DECLARE @oldPasswordHash VARCHAR(MAX)

	SET NOCOUNT ON;

	SELECT @userId = Id, @oldPasswordHash = PasswordHash
	FROM [dbo].[tbl_user]
	WHERE Email = @email

	UPDATE [dbo].[tbl_user]
	SET [PasswordHash] = @passwordHash
	WHERE Email = @email

	DELETE FROM [dbo].[tbl_password_reset]
	WHERE Email = @email

	INSERT INTO [dbo].[tbl_old_passwords]
	(
	    UserId,
	    PasswordHash
	)
	VALUES
	(
	    @userId,
        @oldPasswordHash
	)

	RETURN @@Error
END 
GO

CREATE PROCEDURE [dbo].[usp_submit_phone_verification_token]
	@phone VARCHAR(20),
	@token VARCHAR(25),
	@expiryDate DATE,
	@id BIGINT NULL OUTPUT
AS
BEGIN

	SET NOCOUNT ON;

	IF NOT EXISTS (SELECT *
		FROM [dbo].[tbl_phone_verification]
		WHERE Phone = @phone)
		BEGIN
			INSERT INTO [dbo].[tbl_phone_verification]
			(
				Phone,
				Token,
				ExpiryDate
			)
			VALUES 
			(
				@phone,
				@token,
				@expiryDate
			)
		END
	ELSE
		BEGIN
			UPDATE [dbo].[tbl_phone_verification]
			SET Token = @token, ExpiryDate = @expiryDate
			WHERE Phone = @phone
		END
	
	SET @id = SCOPE_IDENTITY();
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_phone_verification_token] 
	@phone VARCHAR(20)
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT * 
	FROM [dbo].[tbl_phone_verification]
	WHERE Phone = @phone

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_phone_verification_tokens]
	@pageNumber INT,
	@pageSize INT,
	@no_of_records BIGINT
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT *
	FROM [dbo].[tbl_phone_verification]
	ORDER BY [Id] ASC
	OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[tbl_phone_verification])

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_delete_phone_verification_token]
    @phone VARCHAR(20)
AS
BEGIN

    SET NOCOUNT ON;

    DELETE
    FROM [dbo].[tbl_phone_verification]
    WHERE Phone = @phone

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_submit_email_verification_token]
	@email VARCHAR(30),
	@token VARCHAR(25),
	@expiryDate DATE,
	@id BIGINT NULL OUTPUT
AS
BEGIN

	SET NOCOUNT ON;

	IF NOT EXISTS (SELECT *
		FROM [dbo].[tbl_email_verification]
		WHERE Email = @email)
		BEGIN
			INSERT INTO [dbo].[tbl_email_verification]
			(
				Email,
				Token,
				ExpiryDate
			)
			VALUES 
			(
				@email,
				@token,
				@expiryDate
			)
		END
	ELSE
		BEGIN
			UPDATE [dbo].[tbl_email_verification]
			SET Token = @token, ExpiryDate = @expiryDate
			WHERE Email = @email
		END
	
	SET @id = SCOPE_IDENTITY();
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_email_verification_token] 
	@email VARCHAR(30)
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT * 
	FROM [dbo].[tbl_email_verification]
	WHERE Email = @email

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_email_verification_tokens]
	@pageNumber INT,
	@pageSize INT,
	@no_of_records BIGINT
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT *
	FROM [dbo].[tbl_email_verification]
	ORDER BY [Id] ASC
	OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[tbl_email_verification])

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_delete_email_verification_token]
    @email VARCHAR(30)
AS
BEGIN

    SET NOCOUNT ON;

    DELETE
    FROM [dbo].[tbl_email_verification]
    WHERE Email = @email

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_submit_two_factor_verification_token]
	@phone VARCHAR(20),
	@token VARCHAR(25),
	@expiryDate DATE,
	@id BIGINT NULL OUTPUT
AS
BEGIN

	SET NOCOUNT ON;

	IF NOT EXISTS (SELECT *
		FROM [dbo].[tbl_two_factor_verification]
		WHERE Phone = @phone)
		BEGIN
			INSERT INTO [dbo].[tbl_two_factor_verification]
			(
				Phone,
				Token,
				ExpiryDate
			)
			VALUES 
			(
				@phone,
				@token,
				@expiryDate
			)
		END
	ELSE
		BEGIN
			UPDATE [dbo].[tbl_two_factor_verification]
			SET Token = @token, ExpiryDate = @expiryDate
			WHERE Phone = @phone
		END
	
	SET @id = SCOPE_IDENTITY();
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_two_factor_verification_token] 
	@phone VARCHAR(20)
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT * 
	FROM [dbo].[tbl_two_factor_verification]
	WHERE Phone = @phone

	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_retrieve_all_two_factor_verification_tokens]
	@pageNumber INT,
	@pageSize INT,
	@no_of_records BIGINT
AS
BEGIN
	
	SET NOCOUNT ON;

	SELECT *
	FROM [dbo].[tbl_two_factor_verification]
	ORDER BY [Id] ASC
	OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY

    SET @no_of_records = (SELECT COUNT(*)
    FROM [dbo].[tbl_two_factor_verification])

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[usp_delete_two_factor_verification_token]
    @phone VARCHAR(20)
AS
BEGIN

    SET NOCOUNT ON;

    DELETE
    FROM [dbo].[tbl_two_factor_verification]
    WHERE Phone = @phone

    RETURN @@Error
END
GO

