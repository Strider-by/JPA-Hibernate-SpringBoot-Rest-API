//package com.epam.esm.model.audit;
//
//import com.epam.esm.model.Tag;
//
//import javax.persistence.PostPersist;
//import javax.persistence.PostRemove;
//import javax.persistence.PostUpdate;
//import java.time.LocalDateTime;
//
//public class TagEventLogger_old {
//
//    private EventsConsumersManager<Tag> eventsConsumersManager;
//
//    public TagEventLogger_old() {
//        eventsConsumersManager = new EventsConsumersManager<>(TagEventLogger_old::logEvent);
//    }
//
//    @PostPersist
//    public void postPersist(Tag tag) {
//        eventsConsumersManager.postPersist(tag);
//    }
//
//    @PostUpdate
//    public void postUpdate(Tag tag) {
//        eventsConsumersManager.postUpdate(tag);
//    }
//
//    @PostRemove
//    public void postRemove(Tag tag) {
//        eventsConsumersManager.postRemove(tag);
//    }
//
//    private static void logEvent(Tag tag, Action action) {
//        LocalDateTime timestamp = LocalDateTime.now();
//        System.out.printf("-------------------------------%n"
//                        + "Time: %s%n"
//                        + "Tag: {id:%d, name:%s}%n"
//                        + "Action: %s%n"
//                        + "-------------------------------%n",
//                timestamp, tag.getId(), tag.getName(), action);
//    }
//
//}
