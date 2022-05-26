package com.epam.esm.model.audit.show;

import com.epam.esm.model.audit.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.epam.esm.model.audit.Action.*;

public class EventsConsumersManager<T> {

    private final List<BiConsumer<T, Action>> auditEventConsumers;

    public EventsConsumersManager() {
        auditEventConsumers = new ArrayList<>();
    }

    public EventsConsumersManager(BiConsumer<T, Action>... auditEventConsumers) {
        this.auditEventConsumers = Arrays.stream(auditEventConsumers).collect(Collectors.toList());
    }

    public void postPersist(T object) {
        consumeEvent(object, CREATED);
    }

    public void postUpdate(T object) {
        consumeEvent(object, UPDATED);
    }

    public void postRemove(T object) {
        consumeEvent(object, DELETED);
    }

    public EventsConsumersManager<T> addAuditEventConsumer(BiConsumer<T, Action> consumer) {
        this.auditEventConsumers.add(consumer);
        return this;
    }

    private void consumeEvent(T object, Action action) {
        for (BiConsumer<T, Action> auditEventConsumer : auditEventConsumers) {
            auditEventConsumer.accept(object, action);
        }
    }

}
