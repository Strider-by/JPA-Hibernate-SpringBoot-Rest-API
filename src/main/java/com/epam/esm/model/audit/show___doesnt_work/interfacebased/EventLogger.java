package com.epam.esm.model.audit.show___doesnt_work.interfacebased;

import com.epam.esm.model.audit.Action;

import java.util.List;
import java.util.function.BiConsumer;

import static com.epam.esm.model.audit.Action.*;

public interface EventLogger<T> {

//    @PostPersist
    default void postPersist(T object) {
        consumeEvent(object, CREATED);
    }

//    @PostUpdate
    default void postUpdate(T object) {
        consumeEvent(object, UPDATED);
    }

//    @PostRemove
    default void postRemove(T object) {
        consumeEvent(object, DELETED);
    }

    List<BiConsumer<T, Action>> auditEventConsumers();


    default void consumeEvent(T object, Action action) {
        for (BiConsumer<T, Action> auditEventConsumer : auditEventConsumers()) {
            auditEventConsumer.accept(object, action);
        }
    }

}
