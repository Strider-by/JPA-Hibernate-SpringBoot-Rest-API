package com.epam.esm.model.audit;

import com.epam.esm.model.Certificate;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import java.time.LocalDateTime;

import static com.epam.esm.model.audit.Action.*;

public class CertificateEventLogger {


    @PostPersist
    public void postPersist(Certificate certificate) {
        logEvent(certificate, CREATED);
    }

    @PostUpdate
    public void postUpdate(Certificate certificate) {
        logEvent(certificate, UPDATED);
    }

    @PostRemove
    public void postRemove(Certificate certificate) {
        logEvent(certificate, DELETED);
    }

    private static void logEvent(Certificate certificate, Action action) {
        LocalDateTime timestamp = LocalDateTime.now();
        System.out.printf("-------------------------------%n"
                        + "Time: %s%n"
                        + "Certificate: %s%n"
                        + "Action: %s%n"
                        + "-------------------------------%n",
                timestamp, certificate, action);
    }

}
