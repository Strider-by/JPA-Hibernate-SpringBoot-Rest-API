package com.epam.esm.controller.api;

public abstract class ControllerHelper {
    public static final String NEXT = "next";
    public static final String PREVIOUS = "previous";
    public static final String FIRST = "first";
    public static final int FIRST_PAGE_NUMBER = 1;
    public static final String FIRST_PAGE_NUMBER_AS_STRING = "1";
    public static final String DEFAULT_PREVIOUS_ID = "0";

    public static long calcOffset(long limit, int page) {
        return (page - FIRST_PAGE_NUMBER) * limit;
    }
}
