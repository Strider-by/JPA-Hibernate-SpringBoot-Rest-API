//package com.epam.esm.model.util;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.TimeZone;
//
// todo: try to make 'proper' date format for views without implicit upstream entity classes
//public class DateConverter {
//
//    public static String toISO8601DateString(Date date) {
//        TimeZone tz = TimeZone.getTimeZone("UTC");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS");
//        df.setTimeZone(tz);
//        String isoDate = df.format(date);
//
//        return isoDate;
//    }
//
//    public static Date milliSecondsToDate(String milliSecondsString) {
//        return milliSecondsString != null ? new Date(Long.parseLong(milliSecondsString)) : null;
//    }
//
//}
