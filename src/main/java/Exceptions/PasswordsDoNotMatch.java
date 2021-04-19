package Exceptions;

public class PasswordsDoNotMatch extends RuntimeException {

    public PasswordsDoNotMatch() {
        super("Passwords do not match!");
    }
}
