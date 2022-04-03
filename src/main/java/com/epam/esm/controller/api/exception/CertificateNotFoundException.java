package com.epam.esm.controller.api.exception;

public class CertificateNotFoundException extends RuntimeException {

    private final long certificateId;

    public CertificateNotFoundException(long certificateId) {
        this.certificateId = certificateId;
    }

    public long getCertificateId() {
        return certificateId;
    }

}
