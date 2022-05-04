package com.epam.esm.model.representation;

import com.epam.esm.controller.api.ControllerHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class PageRepresentation<T> extends RepresentationModel<PageRepresentation<T>> implements Page<T> {

    private final Page<T> page;

    public PageRepresentation(Page<T> page, String pageNumber, String pageSize) {
        this.page = page;
        addPreviousLink(page, pageNumber, pageSize);
        addNextLink(page, pageNumber, pageSize);
        addFirstLink(page, pageNumber, pageSize);
        addLastLink(page, pageNumber, pageSize);
        addSelfLink(page, pageNumber, pageSize);
    }


    private void addFirstLink(Page<T> page, String pageNumber, String pageSize) {
        Link link = buildPageLink(pageNumber, page.getNumber() - 1, pageSize, page.getSize(),
                    ControllerHelper.FIRST);
        add(link);
    }

    private void addLastLink(Page<T> page, String pageNumber, String pageSize) {
        Link link = buildPageLink(pageNumber, page.getNumber() - 1, pageSize, page.getSize(),
                ControllerHelper.LAST);
        add(link);
    }

    private void addPreviousLink(Page<T> page, String pageNumber, String pageSize) {
        if (page.hasPrevious()) {
            Link link = buildPageLink(pageNumber, page.getNumber() - 1, pageSize, page.getSize(),
                    ControllerHelper.PREVIOUS);
            add(link);
        }
    }

    private void addNextLink(Page<T> page, String pageNumber, String pageSize) {
        if (page.hasNext()) {
            Link link = buildPageLink(pageNumber, page.getNumber() + 1, pageSize, page.getSize(),
                    ControllerHelper.NEXT);
            add(link);
        }
    }

    private void addSelfLink(Page<T> page, String pageNumber, String pageSize) {
        Link link = buildPageLink(pageNumber, page.getNumber() + 1, pageSize, page.getSize(),
                ControllerHelper.SELF);
        add(link);
    }

    private ServletUriComponentsBuilder createBuilder() {
        return ServletUriComponentsBuilder.fromCurrentRequestUri();
    }

    private Link buildPageLink(String pageParam, int page, String sizeParam, int size, String relation) {
        String path = createBuilder()
                .queryParam(pageParam, page)
                .queryParam(sizeParam, size)
                .build()
                .toUriString();
        Link link = Link.of(path, relation);
        return link;
    }

    @Override
    public int getNumber() {
        return page.getNumber();
    }

    @Override
    public int getSize() {
        return page.getSize();
    }

    @Override
    public int getTotalPages() {
        return page.getTotalPages();
    }

    @Override
    public int getNumberOfElements() {
        return page.getNumberOfElements();
    }

    @Override
    public List<T> getContent() {
        return page.getContent();
    }

    @Override
    public boolean hasContent() {
        return page.hasContent();
    }

    @Override
    public Sort getSort() {
        return page.getSort();
    }

    @Override
    public boolean isFirst() {
        return page.isFirst();
    }

    @Override
    public boolean isLast() {
        return page.isLast();
    }

    @Override
    public boolean hasNext() {
        return page.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    @Override
    public Pageable nextPageable() {
        return page.nextPageable();
    }

    @Override
    public Pageable previousPageable() {
        return page.previousPageable();
    }

    @Override
    public long getTotalElements() {
        return page.getTotalElements();
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return page.map(converter);
    }

    @Override
    public Iterator<T> iterator() {
        return page.iterator();
    }

}
