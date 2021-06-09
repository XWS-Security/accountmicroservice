package com.example.pki.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerServiceImpl implements LoggerService {
    private final Logger logger;

    public LoggerServiceImpl(Class<?> parentClass) {
        this.logger = LoggerFactory.getLogger(parentClass);
    }

    @Override
    public void logException(String message) {
        logger.error("Unexpected exception: { message: {} }", message);
    }

    @Override
    public void logTokenException(String message) {
        logger.error("Token issue: {message: {} }", message);
    }

    @Override
    public void logCreateUser(String username) {
        logger.info("Creating user: { 'username': {} }", username);
    }

    @Override
    public void logCreateUserSuccess(String username) {
        logger.info("Created user successfully: { 'username': {} }", username);
    }

    @Override
    public void logCreateUserFail(String username, String reason) {
        logger.error("Created user failed: { 'username': {}, 'reason': {} }", username, reason);
    }

    @Override
    public void activationSuccess(String email) {
        logger.info("Activation successful: { 'email': {}, }", email);
    }

    @Override
    public void activationFailed(String email, String reason) {
        logger.error("Activation failed: { 'email': {}, 'reason': {} }", email, reason);
    }

    @Override
    public void triggerResetPasswordSuccess(String email) {
        logger.info("Trigger reset password success: { 'email': {}, }", email);
    }

    @Override
    public void triggerResetPasswordFailed(String email, String reason) {
        logger.error("Trigger reset password failed: { 'email': {}, 'reason': {} }", email, reason);
    }

    @Override
    public void passwordResetSuccess(String email) {
        logger.info("Password reset success: { 'email': {}, }", email);
    }

    @Override
    public void passwordResetFailed(String email, String reason) {
        logger.error("Password reset failed: { 'email': {}, 'reason': {} }", email, reason);
    }

    @Override
    public void passwordChangeSuccess(String email) {
        logger.info("Password change success: { 'email': {}, }", email);
    }

    @Override
    public void passwordChangeFailed(String email, String reason) {
        logger.error("Password change failed: { 'email': {}, }", email);
    }

    @Override
    public void sendTwoFactorAuthenticationSecretSuccess(String email) {
        logger.info("Two factor authentication secret send: { 'email': {}, }", email);
    }

    @Override
    public void sendTwoFactorAuthenticationSecretFailed(String email, String reason) {
        logger.error("Two factor authentication did not send: { 'email': {}, 'reason': {} }", email, reason);
    }

    @Override
    public void loginSuccess(String email) {
        logger.info("Login successful: { 'username': {}, }", email);
    }

    @Override
    public void loginFailed(String email, String reason) {
        logger.error("Login failed: { 'username': {}, 'reason': {} }", email, reason);
    }

    @Override
    public void createCertificateSuccess(String email) {
        logger.info("Certificate created: { 'email': {}, }", email);
    }

    @Override
    public void createCertificateFailed(String email, String reason) {
        logger.error("Certificate was not created: { 'email': {}, 'reason': {} }", email, reason);
    }

    @Override
    public void getAllCertificatesSuccess(String email) {
        logger.info("Get all certificates successfully: { 'email': {}, }", email);
    }

    @Override
    public void getAllCertificatesFailed(String email, String reason) {
        logger.error("Get all certificates failed: { 'email': {}, 'reason': {} }", email, reason);
    }

    @Override
    public void getCACertificatesSuccess(String email) {
        logger.info("Get all CA certificates successfully: { 'email': {}, }", email);
    }

    @Override
    public void getCACertificatesFailed(String email, String reason) {
        logger.error("Get all CA certificates failed: { 'email': {}, 'reason': {} }", email, reason);
    }

    @Override
    public void revokeCertificateSuccess(String email) {
        logger.info("Certificate revoke success: { 'email': {}, }", email);
    }

    @Override
    public void revokeCertificateFailed(String email, String reason) {
        logger.info("Certificate revoke failed: { 'email': {}, 'reason': {} }", email, reason);
    }

    @Override
    public void downloadCertificateSuccess(String username) {
        logger.info("Download certificate success: { 'username': {}, }", username);
    }

    @Override
    public void downloadCertificateFailed(String username, String reason) {
        logger.info("Download certificate failed: { 'username': {}, 'reason': {} }", username, reason);
    }

    @Override
    public void logUpdateUserSuccess(String username) {
        logger.info("Updated user successfully: { 'username': {}}", username);
    }

    @Override
    public void logUpdateUserFailed(String username, String reason) {
        logger.error("Updated user failed: { 'username': {}, 'reason': {} }", username, reason);
    }
}
