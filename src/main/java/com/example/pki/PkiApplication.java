package com.example.pki;

import com.example.pki.controller.CertificateController;
import com.example.pki.model.dto.CertificateDto;
import com.example.pki.model.enums.CA;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hibernate.id.GUIDGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.security.Security;
import java.util.Calendar;
import java.util.Date;

@SpringBootApplication
public class PkiApplication {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        // SpringApplication.run(PkiApplication.class, args);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 2);

        CertificateDto cd = new CertificateDto(CA.Root, Calendar.getInstance().getTime(),
                cal.getTime(), "123", "321");

        CertificateDto intermediate = new CertificateDto(CA.Intermediate, Calendar.getInstance().getTime(),
                cal.getTime(), "321", "456");

        CertificateDto endEntity = new CertificateDto(CA.EndEntity, Calendar.getInstance().getTime(),
                cal.getTime(), "456", "789");

        ApplicationContext ctx = SpringApplication.run(PkiApplication.class, args);

        CertificateController certificateController = (CertificateController) ctx.getBean("certificateController");

        certificateController.generateCertificate(cd);
        certificateController.generateCertificate(intermediate);
        certificateController.generateCertificate(endEntity);
    }

}
