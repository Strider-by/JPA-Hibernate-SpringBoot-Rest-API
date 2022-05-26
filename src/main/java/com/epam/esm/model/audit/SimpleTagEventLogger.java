//package com.epam.esm.model.audit;
//
//import com.epam.esm.model.Tag;
//
//import javax.persistence.PostPersist;
//import javax.persistence.PostRemove;
//import javax.persistence.PostUpdate;
//import java.time.LocalDateTime;
//
//public class SimpleTagEventLogger extends EventsConsumersManager<Tag> {
//
//    public SimpleTagEventLogger() {
//        addAuditEventConsumer(SimpleTagEventLogger::logEvent);
//    }
//
//    @Override
//    @PostPersist
//    public void postPersist(Tag object) {
//        super.postPersist(object);
//    }
//
//    @Override
//    @PostUpdate
//    public void postUpdate(Tag object) {
//        super.postUpdate(object);
//    }
//
//    @Override
//    @PostRemove
//    public void postRemove(Tag object) {
//        super.postRemove(object);
//    }
//
//    private static void logEvent(Tag tag, Action action) {
//        LocalDateTime timestamp = LocalDateTime.now();
//        System.out.printf("-------------------------------%n"
//                + "Time: %s%n"
//                + "Tag: {id:%d, name:%s} action: %s%n"
//                + "-------------------------------%n",
//                timestamp, tag.getId(), tag.getName(), action);
//    }
//
//}
