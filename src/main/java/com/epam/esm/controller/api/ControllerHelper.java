package com.epam.esm.controller.api;

import com.epam.esm.controller.api.exception.BadRequestParametersException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public abstract class ControllerHelper {

    public static final String SELF = "self";
    public static final String NEXT = "next";
    public static final String PREVIOUS = "previous";
    public static final String FIRST = "first";
    public static final String LAST = "last";
    public static final int FIRST_PAGE_NUMBER = 1;
    public static final String FIRST_PAGE_NUMBER_AS_STRING = "1";
    public static final int DEFAULT_LIMIT = 10;


    public static int pageNumber(Page page) {
        return page.getNumber() + FIRST_PAGE_NUMBER;
    }

    public static int firstPageNumber() {
        return FIRST_PAGE_NUMBER;
    }

    public static int nextPageNumber(Page page) {
        return page.getNumber() + 1 + FIRST_PAGE_NUMBER;
    }

    public static int previousPageNumber(Page page) {
        return pageNumber(page) - 1;
    }

    public static int calcPageNumberForPageRequest(int pageNumberParam) {
        return pageNumberParam - FIRST_PAGE_NUMBER;
    }

    public static int getPageParameter(Map<String, String> params) {
        String paramsSetPage = params.get("page");
        try {
            return paramsSetPage != null ? Integer.parseInt(paramsSetPage) : FIRST_PAGE_NUMBER;
        } catch (NumberFormatException ex) {
            throw new BadRequestParametersException("Failed to parse page parameter");
        }
    }

    public static int getLimitParameter(Map<String, String> params) {
        String paramsSetLimit = params.get("limit");
        try {
            return paramsSetLimit != null ? Integer.parseInt(paramsSetLimit) : DEFAULT_LIMIT;
        } catch (NumberFormatException ex) {
            throw new BadRequestParametersException("Failed to parse limit parameter");
        }
    }

    public static Pageable toPageable(int pageNumber, int pageSize) {
        return PageRequest.of(calcPageNumberForPageRequest(pageNumber), pageSize);
    }

//    public static int getLimitParameter(Map<String, String> params, int defaultLimit) {
//        String paramsSetLimit = params.get("limit");
//        try {
//            return paramsSetLimit != null ? Integer.parseInt(paramsSetLimit) : defaultLimit;
//        } catch (NumberFormatException ex) {
//            throw new BadRequestParametersException("Failed to parse limit parameter");
//        }
//    }

}
