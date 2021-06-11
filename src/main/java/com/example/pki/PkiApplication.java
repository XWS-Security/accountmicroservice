package com.example.pki;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.JavaVersion;
import org.springframework.boot.system.SystemProperties;
import org.springframework.core.SpringVersion;

import java.security.Security;

@SpringBootApplication
public class PkiApplication {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(PkiApplication.class, args);


        System.out.println(SpringVersion.getVersion());
        System.out.println(SystemProperties.get("java.version"));
        System.out.println(JavaVersion.getJavaVersion().toString());
    }

}
