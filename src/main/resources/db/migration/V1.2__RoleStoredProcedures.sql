
CREATE PROCEDURE [dbo].[psp_create_role]
    @id INT NULL OUTPUT,
    @name VARCHAR(30),
    @description VARCHAR(MAX)
AS
BEGIN

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_roles] WHERE [Name] = @name)
    BEGIN
        INSERT INTO dbo.tbl_roles
        (
            [Name],
            [Description]
        )
        VALUES
        (
            @name,
            @description
        )
    END

    SELECT id = SCOPE_IDENTITY()
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_role_by_id]
    @id INT
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM [dbo].[tbl_roles]
    WHERE Id = @id

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_role_by_name]
    @key VARCHAR(20)
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM [dbo].[tbl_roles]
    WHERE [Name] = @key

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_all_roles]
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM [dbo].[tbl_roles]
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

    SET @no_of_records = (SELECT COUNT(*)
    FROM dbo.tbl_roles)
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_update_role]
    @id INT,
    @name VARCHAR(30),
    @description VARCHAR(MAX)
AS
BEGIN

    UPDATE [tbl_roles]
    SET [Name] = @name, Description = @description
    WHERE [Id] = @id

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_delete_role_by_id]
    @id INT
AS
BEGIN

    DELETE
    FROM [dbo].[tbl_roles]
    WHERE [Id] = @id

    RETURN @@Error
END
GO