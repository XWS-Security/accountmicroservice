package Exceptions;

public class CertificateIsNotValid extends RuntimeException {

    public CertificateIsNotValid() {
        super("Certificate is not valid!");
    }
}
