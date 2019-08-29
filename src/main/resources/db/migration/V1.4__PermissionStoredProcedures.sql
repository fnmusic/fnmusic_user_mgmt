
CREATE PROCEDURE [dbo].[psp_create_permission]
    @id INT NULL OUTPUT,
	@name VARCHAR(30),
	@description VARCHAR(MAX)
AS
BEGIN

	IF NOT EXISTS (SELECT * FROM [dbo].[tbl_permissions] WHERE [Name] = @name)
	BEGIN
		INSERT INTO [dbo].[tbl_permissions]
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

    SELECT @id = SCOPE_IDENTITY()
	RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_permission_by_id]
    @id INT
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM dbo.tbl_permissions
    WHERE [Id] = @id

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_permission_by_name]
    @key VARCHAR(30)
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM dbo.tbl_permissions
    WHERE [Name] = @key

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_permission_by_feature]
    @key VARCHAR(30),
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM dbo.uv_features_permissions
    WHERE Feature = @key
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

    SET @no_of_records = (SELECT COUNT(*)
    FROM dbo.uv_features_permissions
    WHERE Feature = @key)
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_retrieve_all_permissions]
    @pageNumber INT,
    @pageSize INT,
    @no_of_records BIGINT NULL OUTPUT
AS
BEGIN

    SET NOCOUNT ON;

    SELECT *
    FROM dbo.tbl_permissions
    ORDER BY [Id] ASC
    OFFSET @pageSize * (@pageNumber - 1) ROWS FETCH NEXT @pageSize ROWS ONLY;

    SET @no_of_records = (SELECT COUNT(*)
    FROM dbo.tbl_permissions)
    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_update_permission]
    @id INT,
    @name VARCHAR(30),
    @description VARCHAR(MAX)
AS
BEGIN

    UPDATE dbo.tbl_permissions
    SET [Name] = @name, Description = @description
    WHERE Id = @id

    RETURN @@Error
END
GO

CREATE PROCEDURE [dbo].[psp_delete_permission_by_id]
    @id INT
AS
BEGIN

    DELETE
    FROM dbo.tbl_permissions
    WHERE Id = @id

    RETURN @@Error
END
GO