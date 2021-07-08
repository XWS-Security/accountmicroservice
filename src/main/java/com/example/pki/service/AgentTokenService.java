package com.example.pki.service;

import javax.net.ssl.SSLException;

public interface AgentTokenService {
    String get() throws SSLException;
}
