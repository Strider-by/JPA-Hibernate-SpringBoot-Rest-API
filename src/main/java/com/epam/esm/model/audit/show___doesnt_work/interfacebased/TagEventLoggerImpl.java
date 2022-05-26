package com.epam.esm.model.audit.show___doesnt_work.interfacebased;

import com.epam.esm.model.Tag;
import com.epam.esm.model.audit.Action;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class TagEventLoggerImpl implements EventLogger<Tag> {

    private final List<BiConsumer<Tag, Action>> auditEventConsumers;

    public TagEventLoggerImpl() {
        auditEventConsumers = new ArrayList<>();
        auditEventConsumers.add(TagEventLoggerImpl::logEvent);
    }


//    @Override
//    @PostPersist
//    public void postPersist(Tag object) {
//        ITest.super.postPersist(object);
//    }
//
//    @Override
//    @PostUpdate
//    public void postUpdate(Tag object) {
//        ITest.super.postUpdate(object);
//    }
//
//    @Override
//    @PostRemove
//    public void postRemove(Tag object) {
//        ITest.super.postRemove(object);
//    }

    @Override
    public List<BiConsumer<Tag, Action>> auditEventConsumers() {
        return auditEventConsumers;
    }

    private static void logEvent(Tag tag, Action action) {
        LocalDateTime timestamp = LocalDateTime.now();
        System.err.printf("-------------------------------%n"
                        + "Time: %s%n"
                        + "Tag: {id:%d, name:%s} action: %s%n"
                        + "-------------------------------%n",
                timestamp, tag.getId(), tag.getName(), action);
    }

}
