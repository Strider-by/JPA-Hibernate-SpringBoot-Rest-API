//package com.epam.esm.model.audit;
//
//import com.epam.esm.model.Tag;
//
//import javax.persistence.PostPersist;
//import javax.persistence.PostRemove;
//import javax.persistence.PostUpdate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.BiConsumer;
//
//import static com.epam.esm.model.audit.Action.*;
//
//public class TagAuditListener {
//
//    private List<BiConsumer<Tag, Action>> auditEventConsumers = new ArrayList<>();
//
//    @PostPersist
//    public void postPersist(Tag object) {
//        System.err.println("post persist");
//        consumeEvent(object, CREATED);
//    }
//
//    @PostUpdate
//    public void beforeUpdate(Tag object) {
//        System.err.println("post update");
//        consumeEvent(object, UPDATED);
//    }
//
//    @PostRemove
//    public void beforeRemove(Tag object) {
//        System.err.println("post delete");
//        consumeEvent(object, DELETED);
//    }
//
//    public List<BiConsumer<Tag, Action>> getAuditEventConsumers() {
//        return auditEventConsumers;
//    }
//
//    public TagAuditListener addAuditEventConsumer(BiConsumer<Tag, Action> consumer) {
//        this.auditEventConsumers.add(consumer);
//        return this;
//    }
//
//    private void consumeEvent(Tag object, Action action) {
//        for (BiConsumer<Tag, Action> auditEventConsumer : auditEventConsumers) {
//            auditEventConsumer.accept(object, action);
//        }
//    }
//
//}
