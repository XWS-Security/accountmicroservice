package com.example.pki.mail;

public interface MailFormatter<T> {
    String getText(T params);
    String getSubject();
}
