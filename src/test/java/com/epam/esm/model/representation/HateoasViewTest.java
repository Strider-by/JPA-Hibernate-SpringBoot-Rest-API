package com.epam.esm.model.representation;

import com.epam.esm.controller.api.ControllerHelper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HateoasViewTest {

    @ParameterizedTest
    @MethodSource("getTestDataFor_hasLinkTo_self")
    void hasLinkTo_self(HateoasView view, boolean linkIsPresent) {
        assertEquals(view.hasLink(ControllerHelper.SELF), linkIsPresent);
    }

    @ParameterizedTest
    @MethodSource("getTestDataFor_hasLinkTo_firstPage")
    void hasLinkTo_firstPage(HateoasView view, boolean linkIsPresent) {
        assertEquals(view.hasLink(ControllerHelper.FIRST), linkIsPresent);
    }

    @ParameterizedTest
    @MethodSource("getTestDataFor_hasLinkTo_nextPage")
    void hasLinkTo_nextPage(HateoasView view, boolean linkIsPresent) {
        assertEquals(view.hasLink(ControllerHelper.NEXT), linkIsPresent);
    }

    @ParameterizedTest
    @MethodSource("getTestDataFor_hasLinkTo_previousPage")
    void hasLinkTo_previousPage(HateoasView view, boolean linkIsPresent) {
        assertEquals(view.hasLink(ControllerHelper.PREVIOUS), linkIsPresent);
    }

    static Stream<Arguments> getTestDataFor_hasLinkTo_self() {
        return Stream.of(
                    Data.onePageTotal_firstPage_emptyPage,
                    Data.onePageTotal_firstPage_fullPage,
                    Data.onePageTotal_firstPage_oneEntityOnPage,
                    Data.twoPagesTotal_firstPage_fullSecondPage,
                    Data.twoPagesTotal_firstPage_oneEntityOnSecondPage,
                    Data.twoPagesTotal_secondPage_fullSecondPage,
                    Data.twoPagesTotal_secondPage_oneEntityOnSecondPage,
                    Data.threePagesTotal_secondPage_fullThirdPage,
                    Data.threePagesTotal_secondPage_oneEntityOnThirdPage,
                    Data.threePagesTotal_thirdPage_fullThirdPage,
                    Data.threePagesTotal_thirdPage_oneEntityOnThirdPage)
                .map(HateoasViewTest::createHateoasView)
                .map(view -> Arguments.of(view, true));
    }

    static Stream<Arguments> getTestDataFor_hasLinkTo_firstPage() {
        Stream<Arguments> hasLink =
                Stream.of(
                        Data.twoPagesTotal_secondPage_fullSecondPage,
                        Data.twoPagesTotal_secondPage_oneEntityOnSecondPage,
                        Data.threePagesTotal_secondPage_fullThirdPage,
                        Data.threePagesTotal_secondPage_oneEntityOnThirdPage,
                        Data.threePagesTotal_thirdPage_fullThirdPage,
                        Data.threePagesTotal_thirdPage_oneEntityOnThirdPage)
                    .map(HateoasViewTest::createHateoasView)
                    .map(view -> Arguments.of(view, true));

        Stream<Arguments> hasNotLink =
                Stream.of(
                        Data.onePageTotal_firstPage_emptyPage,
                        Data.onePageTotal_firstPage_fullPage,
                        Data.onePageTotal_firstPage_oneEntityOnPage,
                        Data.twoPagesTotal_firstPage_fullSecondPage,
                        Data.twoPagesTotal_firstPage_oneEntityOnSecondPage)
                    .map(HateoasViewTest::createHateoasView)
                    .map(view -> Arguments.of(view, false));

        return Stream.concat(hasLink, hasNotLink);
    }

    static Stream<Arguments> getTestDataFor_hasLinkTo_nextPage() {
        Stream<Arguments> hasLink =
                Stream.of(
                        Data.twoPagesTotal_firstPage_fullSecondPage,
                        Data.twoPagesTotal_firstPage_oneEntityOnSecondPage,
                        Data.threePagesTotal_secondPage_fullThirdPage,
                        Data.threePagesTotal_secondPage_oneEntityOnThirdPage)
                    .map(HateoasViewTest::createHateoasView)
                    .map(view -> Arguments.of(view, true));

        Stream<Arguments> hasNotLink =
                Stream.of(
                        Data.onePageTotal_firstPage_emptyPage,
                        Data.onePageTotal_firstPage_fullPage,
                        Data.onePageTotal_firstPage_oneEntityOnPage,
                        Data.twoPagesTotal_secondPage_fullSecondPage,
                        Data.twoPagesTotal_secondPage_oneEntityOnSecondPage,
                        Data.threePagesTotal_thirdPage_fullThirdPage,
                        Data.threePagesTotal_thirdPage_oneEntityOnThirdPage)
                    .map(HateoasViewTest::createHateoasView)
                    .map(view -> Arguments.of(view, false));

        return Stream.concat(hasLink, hasNotLink);
    }

    static Stream<Arguments> getTestDataFor_hasLinkTo_previousPage() {
        Stream<Arguments> hasLink =
                Stream.of(
                        Data.twoPagesTotal_secondPage_fullSecondPage,
                        Data.twoPagesTotal_secondPage_oneEntityOnSecondPage,
                        Data.threePagesTotal_secondPage_fullThirdPage,
                        Data.threePagesTotal_secondPage_oneEntityOnThirdPage,
                        Data.threePagesTotal_thirdPage_fullThirdPage,
                        Data.threePagesTotal_thirdPage_oneEntityOnThirdPage)
                    .map(HateoasViewTest::createHateoasView)
                    .map(view -> Arguments.of(view, true));

        Stream<Arguments> hasNotLink =
                Stream.of(
                        Data.onePageTotal_firstPage_emptyPage,
                        Data.onePageTotal_firstPage_fullPage,
                        Data.onePageTotal_firstPage_oneEntityOnPage,
                        Data.twoPagesTotal_firstPage_fullSecondPage,
                        Data.twoPagesTotal_firstPage_oneEntityOnSecondPage)
                    .map(HateoasViewTest::createHateoasView)
                    .map(view -> Arguments.of(view, false));

        return Stream.concat(hasLink, hasNotLink);
    }

    static class Data {

        static final Page onePageTotal_firstPage_emptyPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(0, 10), 10);

        static final Page onePageTotal_firstPage_fullPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(0, 10), 10);

        static final Page onePageTotal_firstPage_oneEntityOnPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(0, 10), 1);

        static final Page twoPagesTotal_firstPage_fullSecondPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(0, 10), 20);

        static final Page twoPagesTotal_firstPage_oneEntityOnSecondPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(0, 10), 11);

        static final Page twoPagesTotal_secondPage_fullSecondPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(1, 10), 20);

        static final Page twoPagesTotal_secondPage_oneEntityOnSecondPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(1, 10), 11);

        static final Page threePagesTotal_secondPage_fullThirdPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(1, 10), 30);

        static final Page threePagesTotal_secondPage_oneEntityOnThirdPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(1, 10), 21);

        static final Page threePagesTotal_thirdPage_fullThirdPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(2, 10), 30);

        static final Page threePagesTotal_thirdPage_oneEntityOnThirdPage =
                new PageImpl(Collections.emptyList(), PageRequest.of(2, 10), 21);

        static final BiFunction<Integer, Integer, String> hrefGeneratorStub = (i, j) -> "localhost:8080";
    }

    static HateoasView createHateoasView(Page page) {
        return new HateoasView(page, Data.hrefGeneratorStub);
    }

}