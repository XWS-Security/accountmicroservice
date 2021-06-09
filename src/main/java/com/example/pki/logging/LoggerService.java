package com.example.pki.logging;

public interface LoggerService {
    void logException(String message);

    void logValidationFailed(String message);

    void logTokenException(String message);

    void logCreateUser(String username);

    void logCreateUserSuccess(String username);

    void logCreateUserFail(String username, String reason);

    void logUpdateUserSuccess(String username);

    void logUpdateUserFailed(String username, String reason);

    void activationSuccess(String email);

    void activationFailed(String email, String reason);

    void triggerResetPasswordSuccess(String email);

    void triggerResetPasswordFailed(String email, String reason);

    void passwordResetSuccess(String email);

    void passwordResetFailed(String email, String reason);

    void passwordChangeSuccess(String email);

    void passwordChangeFailed(String email, String reason);

    void sendTwoFactorAuthenticationSecretSuccess(String email);

    void sendTwoFactorAuthenticationSecretFailed(String email, String reason);

    void loginSuccess(String email);

    void loginFailed(String email, String reason);

    void createCertificateSuccess(String email);

    void createCertificateFailed(String email, String reason);

    void getAllCertificatesSuccess(String email);

    void getAllCertificatesFailed(String email, String reason);

    void getCACertificatesSuccess(String email);

    void getCACertificatesFailed(String email, String reason);

    void revokeCertificateSuccess(String email);

    void revokeCertificateFailed(String email, String reason);

    void downloadCertificateSuccess(String username);

    void downloadCertificateFailed(String username, String reason);
}
