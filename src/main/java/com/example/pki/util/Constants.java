package com.example.pki.util;

public class Constants {
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{2,12}";
    public static final String USERNAME_INVALID_MESSAGE = "Username must be 2 to 12 characters long and can contain only letters, numbers and an underscore.";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=]).{10,20}$";
    public static final String PASSWORD_INVALID_MESSAGE = "Password must be 10-20 characters long and contain at least one lower and upper case character, a number and a special character";
    public static final String PLAIN_TEXT_PATTERN = "^[^<>]*";
    public static final String INVALID_CHARACTER_MESSAGE = "^[^<>]*";
    public static final String WEBSITE_PATTERN = "^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$";
    public static final String WEBSITE_INVALID_MESSAGE = "Invalid website";
}
