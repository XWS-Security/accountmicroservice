package Exceptions;

public class BadPasswordResetCodeException extends Exception {
    public BadPasswordResetCodeException() {
        super("The entered activation code does not match the one we sent.");
    }
}
