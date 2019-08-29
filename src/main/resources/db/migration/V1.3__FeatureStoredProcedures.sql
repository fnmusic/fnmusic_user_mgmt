
CREATE PROCEDURE [dbo].[psp_create_feature]
    @id INT NULL OUTPUT,
    @name VARCHAR(30),
    @roleId INT,
    @description VARCHAR(MAX)
AS
BEGIN

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_features] WHERE [Name] = @name)
    BEGIN
        INSERT INTO dbo.tbl_features
        (
            [Name],
            [RoleId],
            [Description]
        )
        VALUES
        (
            @name,
            @roleId,
            @description
        )
    END

    SELECT id = SCOPE_IDENTITY();
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_feature_by_id]
    @id INT
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM [dbo].[tbl_features]
    WHERE Id = @id

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_feature_by_role]
    @key VARCHAR(20)
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM [dbo].[uv_roles_features]
    WHERE Role = @key

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_feature_by_name]
    @key VARCHAR(20)
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM [dbo].[tbl_features]
    WHERE [Name] = @key

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_all_features]
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM [dbo].[tbl_features]
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

    SET @no_of_records = (SELECT COUNT(*)
    FROM dbo.tbl_features)
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_update_feature]
    @id INT,
    @name VARCHAR(30),
    @roleId INT,
    @description VARCHAR(MAX)
AS
BEGIN

    UPDATE [tbl_features]
    SET [Name] = @name, RoleId = @roleId, Description = @description
    WHERE [Id] = @id

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_delete_feature_by_id]
    @id INT
AS
BEGIN

    DELETE
    FROM [dbo].[tbl_features]
    WHERE [Id] = @id

    RETURN @@Error
END
GO