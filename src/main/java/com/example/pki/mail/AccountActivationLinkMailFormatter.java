package com.example.pki.mail;

public class AccountActivationLinkMailFormatter implements MailFormatter<String> {
    @Override
    public String getText(String link) {
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
                "            Your activation link is <a href=\"" +
                link +
                "\">here</a>.\n" +
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
