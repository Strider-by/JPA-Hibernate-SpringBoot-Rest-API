package com.epam.esm.model.representation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

public class HateoasView<T> extends RepresentationModel<HateoasView<T>> {

    private T content;

    @JsonCreator
    public HateoasView(@JsonProperty("content") T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

}
