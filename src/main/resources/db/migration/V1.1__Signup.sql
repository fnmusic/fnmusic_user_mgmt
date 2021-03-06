USE[fnmusic_user_mgmt]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_create_user] (
    @id BIGINT NULL OUTPUT,
    @username VARCHAR(15),
    @firstname VARCHAR(15),
    @lastname VARCHAR(15),
    @dateofBirth DATE,
    @gender VARCHAR(6),
    @email VARCHAR(30),
    @password VARCHAR(128),
    @dateCreated DATE
)
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
        @firstname,
        @lastname,
        @dateofBirth,
        @gender,
        @email,
        @password,
        @dateCreated,
        'user'
    )

    SELECT @id = SCOPE_IDENTITY();

    RETURN @@Error

END
GO


