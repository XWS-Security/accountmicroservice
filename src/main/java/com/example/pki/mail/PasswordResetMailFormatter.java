package com.example.pki.mail;

public class PasswordResetMailFormatter implements MailFormatter<String> {

    @Override
    public String getText(String code) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <title>SecurityApp</title>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>\n" +
                "            ®™SecurityApp\n" +
                "        </h1>\n" +
                "        <hr>\n" +
                "        <p>\n" +
                "            Your password reset code is is " + code + "\n" +
                "<br> Thank you for using our application!" +
                "        </p>\n" +
                "    </body>\n" +
                "</html>";
    }

    @Override
    public String getSubject() {
        return "Welcome to ®™SecurityApp!";
    }
}
