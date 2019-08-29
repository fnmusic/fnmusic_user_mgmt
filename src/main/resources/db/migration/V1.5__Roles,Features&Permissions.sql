
CREATE PROCEDURE [dbo].[psp_add_permissions_to_features]
    @featureId INT,
    @permissionId INT
AS
BEGIN

    INSERT INTO dbo.tbl_features_permissions
    (
        FeatureId,
        PermissionId
    )
    VALUES
    (
        @featureId,
        @permissionId
    )

    RETURN @@Error
END
GO


--Add ROLES
EXEC dbo.psp_create_role
    @id = NULL,
    @name = 'USER',
    @description = 'Average User Privileges'
GO

EXEC dbo.psp_create_role
    @id = NULL,
    @name = 'ADMIN',
    @description = 'Admin User Privileges'
GO

EXEC dbo.psp_create_role
    @id = NULL,
    @name = 'SUPER_ADMIN',
    @description = 'Super_Admin User Privileges'
GO

--Add FEATURES
BEGIN
    DECLARE @userRoleId INT
    DECLARE @adminRoleId INT
    DECLARE @superAdminRoleId INT

    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_features] WHERE Name = 'USER_FEATURE')
    BEGIN
        SELECT  @userRoleId = Id FROM [dbo].[tbl_roles] WHERE Name = 'USER'
        EXEC    [dbo].[psp_create_feature]
                @id = NULL,
                @name = 'USER_FEATURE',
                @roleId = @userRoleId,
                @description = 'Default Feature for User Role'
    END

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_features] WHERE Name = 'ADMIN_FEATURE')
    BEGIN
        SELECT  @adminRoleId = Id FROM [dbo].[tbl_roles] WHERE Name = 'ADMIN'
        EXEC    [dbo].[psp_create_feature]
                @id = NULL,
                @name = 'ADMIN_FEATURE',
                @roleId = @adminRoleId,
                @description = 'Default Feature for Admin Role'
    END

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_features] WHERE Name = 'SUPER_ADMIN_FEATURE')
    BEGIN
        SELECT  @superAdminRoleId = Id FROM [dbo].[tbl_roles] WHERE Name = 'SUPER_ADMIN'
        EXEC    [dbo].[psp_create_feature]
                @id = NULL,
                @name = 'SUPER_ADMIN_FEATURE',
                @roleId = @superAdminRoleId,
                @description = 'Default Feature for Super Admin Role'
    END
END
GO

--Add PERMISSIONS & Add to FEATURES
BEGIN
    DECLARE @permission_Id INT
    DECLARE @feature_Id INT

    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_permissions] WHERE Name = 'READ_USER_DATA')
    BEGIN
        EXEC    [dbo].[psp_create_permission]
                @id = NULL,
                @name = 'READ_USER_DATA',
                @description = 'Retrieve User Data'
    END

    SELECT @permission_Id = Id FROM [dbo].[tbl_permissions] WHERE Name = 'READ_USER_DATA'
    SELECT @feature_Id = Id FROM [dbo].[tbl_features] WHERE Name = 'USER_FEATURE'

    IF NOT EXISTS(SELECT * FROM [dbo].[tbl_features_permissions] WHERE FeatureId = @feature_Id AND PermissionId = @permission_Id)
    BEGIN
        EXEC    [dbo].[psp_add_permissions_to_features]
                @featureId = @feature_Id,
                @permissionId = @permission_Id
    END

---------

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_permissions] WHERE Name = 'UPDATE_USER_DATA')
    BEGIN
        EXEC    [dbo].[psp_create_permission]
                @id = NULL,
                @name = 'UPDATE_USER_DATA',
                @description = 'Update User Data'
    END

    SELECT @permission_Id = Id FROM [dbo].[tbl_permissions] WHERE Name = 'UPDATE_USER_DATA'
    SELECT @feature_Id = Id FROM [dbo].[tbl_features] WHERE Name = 'USER_FEATURE'

    IF NOT EXISTS(SELECT * FROM [dbo].[tbl_features_permissions] WHERE FeatureId = @feature_Id AND PermissionId = @permission_Id)
    BEGIN
        EXEC    [dbo].[psp_add_permissions_to_features]
                @featureId = @feature_Id,
                @permissionId = @permission_Id
    END

---------

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_permissions] WHERE Name = 'FOLLOW_USER')
    BEGIN
        EXEC    [dbo].[psp_create_permission]
                @id = NULL,
                @name = 'FOLLOW_USER',
                @description = 'Follow & Unfollow Users'
    END

    SELECT @permission_Id = Id FROM [dbo].[tbl_permissions] WHERE Name = 'FOLLOW_USER'
    SELECT @feature_Id = Id FROM [dbo].[tbl_features] WHERE Name = 'USER_FEATURE'

    IF NOT EXISTS(SELECT * FROM [dbo].[tbl_features_permissions] WHERE FeatureId = @feature_Id AND PermissionId = @permission_Id)
    BEGIN
        EXEC    [dbo].[psp_add_permissions_to_features]
                @featureId = @feature_Id,
                @permissionId = @permission_Id
    END

---------

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_permissions] WHERE Name = 'READ_FAN_DATA')
    BEGIN
        EXEC    [dbo].[psp_create_permission]
                @id = NULL,
                @name = 'READ_FAN_DATA',
                @description = 'Retrieve Fan Data'
    END

    SELECT @permission_Id = Id FROM [dbo].[tbl_permissions] WHERE Name = 'READ_FAN_DATA'
    SELECT @feature_Id = Id FROM [dbo].[tbl_features] WHERE Name = 'USER_FEATURE'

    IF NOT EXISTS(SELECT * FROM [dbo].[tbl_features_permissions] WHERE FeatureId = @feature_Id AND PermissionId = @permission_Id)
    BEGIN
        EXEC    [dbo].[psp_add_permissions_to_features]
                @featureId = @feature_Id,
                @permissionId = @permission_Id
    END

---------

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_permissions] WHERE Name = 'SUSPEND_USER')
    BEGIN
        EXEC    [dbo].[psp_create_permission]
                @id = NULL,
                @name = 'SUSPEND_USER',
                @description = 'Suspend an existing user account'
    END

    SELECT @permission_Id = Id FROM [dbo].[tbl_permissions] WHERE Name = 'SUSPEND_USER'
    SELECT @feature_Id = Id FROM [dbo].[tbl_features] WHERE Name = 'ADMIN_FEATURE'

    IF NOT EXISTS(SELECT * FROM [dbo].[tbl_features_permissions] WHERE FeatureId = @feature_Id AND PermissionId = @permission_Id)
    BEGIN
        EXEC    [dbo].[psp_add_permissions_to_features]
                @featureId = @feature_Id,
                @permissionId = @permission_Id
    END

---------

    IF NOT EXISTS (SELECT * FROM [dbo].[tbl_permissions] WHERE Name = 'UNLOCK_USER')
    BEGIN
        EXEC    [dbo].[psp_create_permission]
                @id = NULL,
                @name = 'UNLOCK_USER',
                @description = 'Unlock an existing user account'
    END

    SELECT @permission_Id = Id FROM [dbo].[tbl_permissions] WHERE Name = 'UNLOCK_USER'
    SELECT @feature_Id = Id FROM [dbo].[tbl_features] WHERE Name = 'ADMIN_FEATURE'

    IF NOT EXISTS(SELECT * FROM [dbo].[tbl_features_permissions] WHERE FeatureId = @feature_Id AND PermissionId = @permission_Id)
    BEGIN
        EXEC    [dbo].[psp_add_permissions_to_features]
                @featureId = @feature_Id,
                @permissionId = @permission_Id
    END
END