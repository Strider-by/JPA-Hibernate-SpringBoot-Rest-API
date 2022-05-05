package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.TagController;
import com.epam.esm.controller.api.exception.TagNotFoundException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TagControllerImpl implements TagController {

    @Autowired
    private final TagsService tagsService;

    private static final BiFunction<Integer, Integer, String> GET_TAGS_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(TagController.class)
                    .getAllTags(pageNumber, pageSize)).toString();


    public TagControllerImpl(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @Override
    public HateoasView<Tag> getAllTags(int pageNumber, int pageSize) {
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Tag> page = tagsService.getAllTags(pageable);
        HateoasView<Tag> view = new HateoasView<>(page, GET_TAGS_HREF_GENERATOR);
        return view;
    }

    @Override
    public Tag getTag(String name) {
        Tag tag = tagsService.getTag(name);
        if (tag == null) {
            throw new TagNotFoundException(name);
        }
        return tag;
    }

    @Override
    public Tag createTag(TagCreateDto dto) {
        Tag tag = tagsService.createTag(dto);
        return tag;
    }

    @Override
    public Message deleteTag(String name) {
        tagsService.deleteTag(name);
        return new Message(HttpStatus.OK, String.format("Tag '%s' has been deleted", name));
    }

    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(produces = "application/json") // todo do i need this? // No!?
    private Message tagNotFound(TagNotFoundException ex) {
        String tagName = ex.getTagName();
        return new Message(HttpStatus.NOT_FOUND, String.format("Tag '%s' can not be found", tagName));
    }

}
