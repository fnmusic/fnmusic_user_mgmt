USE [fnmusic_user_mgmt]
GO
/****** Object:  StoredProcedure [dbo].[uspRetrieveUserByUsername]    Script Date: 4/25/2019 11:02:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[uspRetrieveUserByUsername]
	@username VARCHAR(50)
AS
BEGIN
	
	SET NOCOUNT ON;

    SELECT * 
	FROM dbo.tbl_user
	WHERE Username = @username

	RETURN @@Error
END
GO

/****** Object:  StoredProcedure [dbo].[uspRetrieveUserByEmail]    Script Date: 4/25/2019 11:02:01 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[uspRetrieveUserByEmail]
	@email varchar(50)
AS
BEGIN

	SET NOCOUNT ON;

	SELECT *
	FROM dbo.tbl_user
	WHERE Email = @email

	RETURN @@Error
END
GO
