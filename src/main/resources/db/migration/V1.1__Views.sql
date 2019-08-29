
CREATE VIEW [dbo].[uv_followers]
AS
SELECT      dbo.tbl_follow.FanId AS Id, dbo.tbl_follow.UserId, dbo.tbl_user.FirstName, dbo.tbl_user.LastName, dbo.tbl_user.Email, dbo.tbl_user.Username
FROM        dbo.tbl_follow INNER JOIN
                dbo.tbl_user ON dbo.tbl_follow.FanId = dbo.tbl_user.Id
WHERE       (dbo.tbl_user.Deleted = 0)
GO

CREATE VIEW [dbo].[uv_following]
AS
SELECT      dbo.tbl_follow.UserId AS Id, dbo.tbl_follow.FanId, dbo.tbl_user.Username, dbo.tbl_user.FirstName, dbo.tbl_user.LastName, dbo.tbl_user.Email
FROM        dbo.tbl_follow INNER JOIN
                dbo.tbl_user ON dbo.tbl_follow.UserId = dbo.tbl_user.Id
WHERE       (dbo.tbl_user.Deleted = 0)
GO

CREATE VIEW [dbo].[uv_roles_features]
AS
SELECT      dbo.tbl_features.Id, dbo.tbl_features.Name, dbo.tbl_roles.Name AS Role, dbo.tbl_features.Description
FROM        dbo.tbl_features INNER JOIN
            dbo.tbl_roles ON dbo.tbl_features.RoleId = dbo.tbl_roles.Id
GO

CREATE VIEW [dbo].[uv_features_permissions]
AS
SELECT      dbo.tbl_features.Name AS Feature, dbo.tbl_permissions.Id, dbo.tbl_permissions.Name, dbo.tbl_permissions.Description
FROM        dbo.tbl_features INNER JOIN
            dbo.tbl_features_permissions ON dbo.tbl_features.Id = dbo.tbl_features_permissions.FeatureId INNER JOIN
                dbo.tbl_permissions ON dbo.tbl_features_permissions.PermissionId = dbo.tbl_permissions.Id
GO