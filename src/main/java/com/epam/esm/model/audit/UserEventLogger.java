package com.epam.esm.model.audit;

import com.epam.esm.model.User;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import java.time.LocalDateTime;

import static com.epam.esm.model.audit.Action.*;

public class UserEventLogger {


    @PostPersist
    public void postPersist(User user) {
        logEvent(user, CREATED);
    }

    @PostUpdate
    public void postUpdate(User user) {
        logEvent(user, UPDATED);
    }

    @PostRemove
    public void postRemove(User user) {
        logEvent(user, DELETED);
    }

    private static void logEvent(User user, Action action) {
        LocalDateTime timestamp = LocalDateTime.now();
        System.out.printf("-------------------------------%n"
                        + "Time: %s%n"
                        + "User: %s%n"
                        + "Action: %s%n"
                        + "-------------------------------%n",
                timestamp, user, action);
    }

}
