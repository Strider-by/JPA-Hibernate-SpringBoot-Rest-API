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
    public static final int DEFAULT_PAGE_SIZE = 10;
    // can't use Integer.toString(...) here
    public static final String DEFAULT_PAGE_SIZE_AS_STRING = "10";
    public static final String DEFAULT_PAGE_NUMBER_PARAM_NAME = "page";
    public static final String DEFAULT_PAGE_SIZE_PARAM_NAME = "limit";


    public static int pageNumber(Page<?> page) {
        return page.getNumber() + FIRST_PAGE_NUMBER;
    }

    public static int firstPageNumber() {
        return FIRST_PAGE_NUMBER;
    }

    public static int nextPageNumber(Page<?> page) {
        return page.getNumber() + 1 + FIRST_PAGE_NUMBER;
    }

    public static int previousPageNumber(Page<?> page) {
        return pageNumber(page) - 1;
    }

    public static int getPageNumberParameter(Map<String, String> params) {
        String paramsSetPage = params.get(DEFAULT_PAGE_NUMBER_PARAM_NAME);
        try {
            return paramsSetPage != null ? Integer.parseInt(paramsSetPage) : FIRST_PAGE_NUMBER;
        } catch (NumberFormatException ex) {
            throw new BadRequestParametersException("Failed to parse page parameter");
        }
    }

    public static int getPageSizeParameter(Map<String, String> params) {
        String paramsSetLimit = params.get(DEFAULT_PAGE_SIZE_PARAM_NAME);
        try {
            return paramsSetLimit != null ? Integer.parseInt(paramsSetLimit) : DEFAULT_PAGE_SIZE;
        } catch (NumberFormatException ex) {
            throw new BadRequestParametersException("Failed to parse limit parameter");
        }
    }

    public static Pageable toPageable(int pageNumber, int pageSize) {
        return PageRequest.of(calcPageNumberForPageRequest(pageNumber), pageSize);
    }

    private static int calcPageNumberForPageRequest(int pageNumberParam) {
        return pageNumberParam - FIRST_PAGE_NUMBER;
    }

}
