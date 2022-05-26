package com.epam.esm.model.audit;

import com.epam.esm.model.Purchase;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import java.time.LocalDateTime;

import static com.epam.esm.model.audit.Action.*;

public class PurchaseEventLogger {


    @PostPersist
    public void postPersist(Purchase purchase) {
        logEvent(purchase, CREATED);
    }

    @PostUpdate
    public void postUpdate(Purchase purchase) {
        logEvent(purchase, UPDATED);
    }

    @PostRemove
    public void postRemove(Purchase purchase) {
        logEvent(purchase, DELETED);
    }

    private static void logEvent(Purchase purchase, Action action) {
        LocalDateTime timestamp = LocalDateTime.now();
        System.out.printf("-------------------------------%n"
                        + "Time: %s%n"
                        + "Purchase: %s%n"
                        + "Action: %s%n"
                        + "-------------------------------%n",
                timestamp, purchase, action);
    }

}
