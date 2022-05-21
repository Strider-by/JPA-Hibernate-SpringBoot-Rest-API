package com.epam.esm.repository.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import javax.persistence.Query;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class QueryParametersSetterTest {

    @Mock
    private Query query;
    QueryParametersSetter setter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setter = QueryParametersSetter.of(query);
    }

    @ParameterizedTest
    @MethodSource("getTestDataFor_set")
    void set(String paramName, Object paramValue) {
        setter.set(paramName, paramValue);
        verify(query).setParameter(paramName, paramValue);
    }

    @ParameterizedTest
    @MethodSource("getTestDataFor_setIf")
    void setIf(String paramName, Object paramValue, boolean condition) {
        setter.setIf(condition, paramName, paramValue);

        if (condition) {
            verify(query).setParameter(paramName, paramValue);
        } else {
            verifyNoInteractions(query);
        }

    }

    static Stream<Arguments> getTestDataFor_set() {
        return Arrays.stream(
                new Object[][] {
                        {"param1", 114L},
                        {"param2", "Some random value"},
                        {"param3", new Object()},
                        {"param4", null}
                }
        ).map(arr -> Arguments.of(arr[0], arr[1]));
    }

    static Stream<Arguments> getTestDataFor_setIf() {
        return Arrays.stream(
                new Object[][] {
                        {"param1", 114L},
                        {"param2", "Some random value"},
                        {"param3", new Object()},
                        {"param4", null}
                }
        ).flatMap(arr -> Stream.of(
                Arguments.of(arr[0], arr[1], true),
                Arguments.of(arr[0], arr[1], false)));
    }

}