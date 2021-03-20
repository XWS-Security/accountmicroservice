package Exceptions;

public class CertificateIsNotCA extends RuntimeException {

    public CertificateIsNotCA() {
        super("Certificate is not CA!");
    }
}
