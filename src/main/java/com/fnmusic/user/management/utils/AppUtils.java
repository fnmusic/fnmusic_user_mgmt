package com.fnmusic.user.management.utils;


import com.fnmusic.base.Utils.ConstantUtils;
import com.fnmusic.base.Utils.MailType;
import com.fnmusic.base.models.Mail;
import com.fnmusic.user.management.model.AuditLog;
import com.fnmusic.user.management.model.PasswordReset;
import com.fnmusic.user.management.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Stephen.Enunwah on 4/5/2019
 */
public class AppUtils {

    private static Logger logger = LoggerFactory.getLogger(AppUtils.class);

    public static boolean isNotNull(Object value) {

        if (value != null) {
            return true;
        }

        return false;
    }



    public static AuditLog getAuditLogInstance(AuditLogType auditLogType, Object object) {

        AuditLog auditLog = new AuditLog();
        auditLog.setAuditLogId(UUID.randomUUID().toString());
        auditLog.setCreatedDate(new Date());
        auditLog.setAuditObject(object);

        try {
            switch (auditLogType) {
                case register:
                case login:
                    User user = object.getClass().isAssignableFrom(User.class) ? (User) object : null;
                    if (user == null) {
                        throw new IllegalStateException("AuditLog Object 'User' cannot be null");
                    }
                    auditLog.setAction(ConstantUtils.ACTIONS[0]);
                    auditLog.setDetail(user.getEmail() + " created an account");
                    auditLog.setEntityId(UUID.randomUUID().toString());
                    auditLog.setEntityName(user.getEmail());
                    auditLog.setUsername(user.getUsername());
                    return auditLog;

                case forgotPassword:
                    String email = object.getClass().isAssignableFrom(String.class) ? (String) object : null;
                    if (email == null) {
                        throw new IllegalStateException("AuditLog Object 'ForgotPassword' cannot be null");
                    }
                    auditLog.setAction(ConstantUtils.ACTIONS[2]);
                    auditLog.setDetail("Password reset initiated on " + email);
                    auditLog.setEntityId(UUID.randomUUID().toString());
                    auditLog.setEntityName(email);
                    return auditLog;

                case passwordReset:
                    PasswordReset passwordReset = object.getClass().isAssignableFrom(PasswordReset.class) ? (PasswordReset) object : null;
                    if (passwordReset == null) {
                        throw new IllegalStateException("AuditLog Object 'PasswordReset cannot be null'");
                    }
                    auditLog.setAction(ConstantUtils.ACTIONS[3]);
                    auditLog.setDetail("Password reset performed completed on " + passwordReset.getEmail());
                    auditLog.setEntityId(UUID.randomUUID().toString());
                    auditLog.setEntityName(passwordReset.getEmail());
                    return auditLog;

                default:
                    return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static Mail getMailInstance(MailType mailType, Object object, String pathToken) {
        Mail mail = new Mail();

        switch (mailType) {
            case accountActivation:
                User user = object.getClass().isAssignableFrom(User.class) ? (User) object : null;
                if (user == null) {
                    throw new IllegalStateException("Mail Object 'User' cannot be null ");
                }

                mail.setMailFrom(ConstantUtils.MAILSENDER);
                mail.setMailTo(new String[]{user.getEmail()});
                mail.setMailCc(null);
                mail.setMailBcc(null);
                mail.setMailSubject("ACTIVATE YOUR ACCOUNT");
                mail.setTemplateName(ConstantUtils.MAIL_TEMPLATE_NAMES[0]);
                mail.setBody("kindly click on the link below to activate your account");
                mail.setActionLink(ConstantUtils.URL_PATHS[0] + pathToken);
                return mail;

            case passwordReset:
                String email = object.getClass().isAssignableFrom(String.class) ? (String) object : null;
                if (email == null) {
                    throw new IllegalStateException("String 'Email' cannot be null");
                }

                mail.setMailFrom(ConstantUtils.MAILSENDER);
                mail.setMailTo(new String[]{email});
                mail.setMailCc(null);
                mail.setMailBcc(null);
                mail.setMailSubject("PASSWORD RESET");
                mail.setTemplateName(ConstantUtils.MAIL_TEMPLATE_NAMES[0]);
                mail.setBody("kindly click on the link below to reset your password");
                mail.setActionLink(ConstantUtils.URL_PATHS[1] + email + "/" + pathToken);
                return mail;

            default:
                return null;

        }
    }


}
