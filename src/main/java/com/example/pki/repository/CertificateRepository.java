package com.example.pki.repository;

import com.example.pki.model.OCSPCertificate;
import org.springframework.data.repository.CrudRepository;

public interface CertificateRepository extends CrudRepository<OCSPCertificate, Long> {

    OCSPCertificate findByFileName(String fileName);
}
