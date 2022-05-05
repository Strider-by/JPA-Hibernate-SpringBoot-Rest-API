package com.epam.esm.model.representation;

import com.epam.esm.controller.api.ControllerHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;


public class HateoasView<T> extends RepresentationModel<HateoasView<T>> {

    @JsonProperty(value = "content")
    private List<T> content;
    @JsonProperty(value = "elements_total")
    private long elementsTotal;
    @JsonProperty(value = "pages_total")
    private long pagesTotal;
    @JsonProperty(value = "elements_on_current_page")
    private int elementsOnPage;
    @JsonProperty(value = "max_elements_on_page")
    private int pageSize;
    @JsonIgnore
    private Page<T> page;


    public HateoasView(Page<T> page) {
        this.page = page;
        this.content = page.getContent();
        this.elementsTotal = page.getTotalElements();
        this.pagesTotal = page.getTotalPages();
        this.elementsOnPage = page.getNumberOfElements();
        this.pageSize = page.getSize();
    }

    public HateoasView(Page page, BiFunction<Integer, Integer, String> hrefGenerator) {
        this(page);
        generateStandardLinks(hrefGenerator);
    }

    private void generateStandardLinks(BiFunction<Integer, Integer, String> hrefGenerator) {
        int pageSize = page.getSize();
        for (Relation relation : Relation.values()) {
            int pageNumber = relation.pageNumberGenerator.apply(page);
            if (relation.additionPredicate.test(page)) {
                add(Link.of(hrefGenerator.apply(pageNumber, pageSize), relation.relValue));
            }
        }
    }


    public enum Relation {

        SELF(
                ControllerHelper.SELF,
                AdditionPredicate.always,
                page -> ControllerHelper.pageNumber(page)),
        FIRST(
                ControllerHelper.FIRST,
                AdditionPredicate.hasPreviousPage,
                page -> ControllerHelper.firstPageNumber()),
        NEXT(
                ControllerHelper.NEXT,
                AdditionPredicate.hasNextPage,
                page -> ControllerHelper.nextPageNumber(page)),
        PREVIOUS(
                ControllerHelper.PREVIOUS,
                AdditionPredicate.hasPreviousPage,
                page -> ControllerHelper.previousPageNumber(page));

        private final String relValue;
        private final Predicate<Page> additionPredicate;
        private final Function<Page, Integer> pageNumberGenerator;

        Relation(String relValue, Predicate<Page> additionPredicate, Function<Page, Integer> pageNumberGenerator) {
            this.relValue = relValue;
            this.additionPredicate = additionPredicate;
            this.pageNumberGenerator = pageNumberGenerator;
        }

        private static class AdditionPredicate {
            private static final Predicate<Page> hasPreviousPage = Page::hasPrevious;
            private static final Predicate<Page> hasNextPage = Page::hasNext;
            private static final Predicate<Page> always = page -> true;
        }

    }

}
