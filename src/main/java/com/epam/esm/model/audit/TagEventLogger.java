package com.epam.esm.model.audit;

import com.epam.esm.model.Tag;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import java.time.LocalDateTime;

import static com.epam.esm.model.audit.Action.*;

public class TagEventLogger {


    @PostPersist
    public void postPersist(Tag tag) {
        logEvent(tag, CREATED);
    }

    @PostUpdate
    public void postUpdate(Tag tag) {
        logEvent(tag, UPDATED);
    }

    @PostRemove
    public void postRemove(Tag tag) {
        logEvent(tag, DELETED);
    }

    private static void logEvent(Tag tag, Action action) {
        LocalDateTime timestamp = LocalDateTime.now();
        System.out.printf("-------------------------------%n"
                        + "Time: %s%n"
                        + "Tag: {id:%d, name:%s}%n"
                        + "Action: %s%n"
                        + "-------------------------------%n",
                timestamp, tag.getId(), tag.getName(), action);
    }

}
