package com.epam.esm.controller.api;

import com.epam.esm.controller.api.exception.BadRequestParametersException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ControllerHelperTest {

    @ParameterizedTest
    @MethodSource("getTestDataFor_pageNumber")
    void pageNumber(Page input, int expected) {
        assertEquals(expected, ControllerHelper.pageNumber(input));
    }

    @Test
    void firstPageNumber() {
        assertEquals(ControllerHelper.FIRST_PAGE_NUMBER, ControllerHelper.firstPageNumber());
    }

    @ParameterizedTest
    @MethodSource("getTestDataFor_nextPageNumber")
    void nextPageNumber(Page input, int expected) {
        assertEquals(expected, ControllerHelper.nextPageNumber(input));
    }

    @ParameterizedTest
    @MethodSource("getTestDataFor_previousPageNumber")
    void previousPageNumber(Page input, int expected) {
        assertEquals(expected, ControllerHelper.previousPageNumber(input));
    }

    @Test
    void getPageNumberParameter_success_regularNumber() {
        int expected = 32;
        Map<String, String> paramMap = createPageNumberParamMap(expected);
        assertEquals(ControllerHelper.getPageNumberParameter(paramMap), expected);
    }

    @Test
    void getPageNumberParameter_success_noPageParamSet_defaultFirstPageNumberReturned() {
        int expected = ControllerHelper.FIRST_PAGE_NUMBER;
        Map<String, String> paramMap = new HashMap<>();
        assertEquals(ControllerHelper.getPageNumberParameter(paramMap), expected);
    }

    @Test
    void getPageNumberParameter_fail_nonNumberPageNumberParameter_toBeThrown_BadRequestParametersException() {
        String invalidPageNumberParameter = "page 24";
        Map<String, String> paramMap = createPageNumberParamMap(invalidPageNumberParameter);
        assertThrows(BadRequestParametersException.class, () -> ControllerHelper.getPageNumberParameter(paramMap));
    }

    @Test
    void getPageSizeParameter_success_regularNumber() {
        int expected = 32;
        Map<String, String> paramMap = createPageSizeParamMap(expected);
        assertEquals(ControllerHelper.getPageSizeParameter(paramMap), expected);
    }

    @Test
    void getPageSizeParameter_success_noPageSizeParamSet_defaultPageSizeReturned() {
        int expected = ControllerHelper.DEFAULT_PAGE_SIZE;
        Map<String, String> paramMap = new HashMap<>();
        assertEquals(ControllerHelper.getPageSizeParameter(paramMap), expected);
    }

    @Test
    void getPageNumberParameter_fail_nonNumberPageSizeParameter_toBeThrown_BadRequestParametersException() {
        String invalidPageSizeParameter = "31 item";
        Map<String, String> paramMap = createPageSizeParamMap(invalidPageSizeParameter);
        assertThrows(BadRequestParametersException.class, () -> ControllerHelper.getPageSizeParameter(paramMap));
    }

    @ParameterizedTest
    @MethodSource("getTestDataFor_toPageable")
    void toPageable(int pageNumber, int pageSize, Pageable verificationPageable) {
        Pageable actual = ControllerHelper.toPageable(pageNumber, pageSize);
        assertEquals(actual.getPageNumber(), verificationPageable.getPageNumber());
        assertEquals(actual.getPageSize(), verificationPageable.getPageSize());
    }

    private Map<String, String> createPageNumberParamMap(Object value) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ControllerHelper.DEFAULT_PAGE_NUMBER_PARAM_NAME, value.toString());
        return map;
    }

    private Map<String, String> createPageSizeParamMap(Object value) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ControllerHelper.DEFAULT_PAGE_SIZE_PARAM_NAME, value.toString());
        return map;
    }


    static Stream<Arguments> getTestDataFor_pageNumber() {
        int size = 100;
        long total = 800;
        int[] data = {0, 1, 20, 21, 44, 56, 100, 123, 456};
        int pageNumberShift = 0;

        return Arrays.stream(data)
                .mapToObj(pageNumber -> Arguments.of(
                        new PageImpl(Collections.emptyList(), PageRequest.of(pageNumber, size), total),
                        ControllerHelper.FIRST_PAGE_NUMBER + pageNumber + pageNumberShift
                    )
                );
    }

    static Stream<Arguments> getTestDataFor_nextPageNumber() {
        int size = 100;
        long total = 800;
        int[] data = {0, 1, 20, 21, 44, 56, 54, 96, 322, 11};
        int pageNumberShift = 1;

        return Arrays.stream(data)
                .mapToObj(pageNumber -> Arguments.of(
                        new PageImpl(Collections.emptyList(), PageRequest.of(pageNumber, size), total),
                        ControllerHelper.FIRST_PAGE_NUMBER + pageNumber + pageNumberShift
                    )
                );
    }

    static Stream<Arguments> getTestDataFor_previousPageNumber() {
        int size = 100;
        long total = 800;
        int[] data = {0, 1, 20, 21, 44, 56};
        int pageNumberShift = -1;

        return Arrays.stream(data)
                .mapToObj(pageNumber -> Arguments.of(
                        new PageImpl(Collections.emptyList(), PageRequest.of(pageNumber, size), total),
                        ControllerHelper.FIRST_PAGE_NUMBER + pageNumber + pageNumberShift
                    )
                );
    }

    static Stream<Arguments> getTestDataFor_toPageable() {
        int[][] data = {
                {1, 5},
                {7, 6},
                {18, 441},
                {2000_000, 1},
                {10, 10},
                {31, 31}
        };

        return Arrays.stream(data)
                .map(arr -> Arguments.of(
                        arr[0],
                        arr[1],
                        PageRequest.of(arr[0] - ControllerHelper.FIRST_PAGE_NUMBER, arr[1])
                )
        );
    }

}