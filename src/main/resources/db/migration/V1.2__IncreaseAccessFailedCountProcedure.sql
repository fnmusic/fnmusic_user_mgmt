USE [fnmusic_user_mgmt]
GO
/****** Object:  StoredProcedure [dbo].[uspIncreaseAccessFailedCount]    Script Date: 4/25/2019 11:02:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[usp_increase_access_failed_count]
	@email VARCHAR 
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

	RETURN @@Error

END
GO
