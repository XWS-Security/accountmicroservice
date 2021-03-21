package Exceptions;

public class CertificateAlreadyExists extends RuntimeException {

    public CertificateAlreadyExists() {
        super("Certificate already exists!");
    }
}
