package Exceptions;

public class PasswordResetTriesExceededException extends Exception {
    public PasswordResetTriesExceededException() {
        super("You have exceeded the password reset try limit. The code is no longer valid. If you wish to reset the password you will need to try again.");
    }
}
