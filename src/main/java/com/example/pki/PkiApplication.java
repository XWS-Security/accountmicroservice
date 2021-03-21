package com.example.pki;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class PkiApplication {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(PkiApplication.class, args);
//
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MONTH, 2);
//
//        CertificateDto cd = new CertificateDto(CA.Root, Calendar.getInstance().getTime(),
//                cal.getTime(), "root", null);

//        CertificateDto intermediate = new CertificateDto(CA.Intermediate, Calendar.getInstance().getTime(),
//                cal.getTime(), "321", "456", "intermediate", "root");
//
//        CertificateDto endEntity = new CertificateDto(CA.EndEntity, Calendar.getInstance().getTime(),
//                cal.getTime(), "456", "789", "endEntity", "intermediate");
//
//        ApplicationContext ctx = SpringApplication.run(PkiApplication.class, args);
//
//        CertificateController certificateController = (CertificateController) ctx.getBean("certificateController");
//
//        certificateController.generateCertificate(cd);
//        certificateController.generateCertificate(intermediate);
//        certificateController.generateCertificate(endEntity);
    }

}
