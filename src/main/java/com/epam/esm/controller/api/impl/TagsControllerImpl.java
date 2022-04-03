package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.TagsController;
import com.epam.esm.controller.api.exception.TagAlreadyExistsException;
import com.epam.esm.controller.api.exception.TagNotFoundException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagDownstreamDto;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TagsControllerImpl implements TagsController {

    @Autowired
    private final TagsService tagsService;

    public TagsControllerImpl(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @Override
    public HttpEntity<HateoasView<List<Tag>>> getAllTags(long limit, int page) {
        long offset = calcOffset(limit, page);
        List<Tag> tags = tagsService.getAllTags(limit, offset);
        HateoasView<List<Tag>> view = createGetAllTagsHateoasView(tags, limit, page);
        return new HttpEntity<>(view);
    }

    private HateoasView<List<Tag>> createGetAllTagsHateoasView(List<Tag> tags, long limit, int page) {
        HateoasView<List<Tag>> view = new HateoasView<>(tags);

        if (page != FIRST_PAGE_NUMBER) {
            view.add(linkTo(methodOn(TagsController.class).getAllTags(limit, FIRST_PAGE_NUMBER)).withRel(FIRST));
            view.add(linkTo(methodOn(TagsController.class).getAllTags(limit, page - 1)).withRel(PREVIOUS));
        }

        view.add(linkTo(methodOn(TagsController.class).getAllTags(limit, page)).withSelfRel());
        view.add(linkTo(methodOn(TagsController.class).getAllTags(limit, page + 1)).withRel(NEXT));
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
    public Message createTag(TagDownstreamDto dto) {
        boolean created = tagsService.createTag(dto);
        String name = dto.getName();
        if (!created) {
            throw new TagAlreadyExistsException(name);
        }

        return new Message(HttpStatus.CREATED, String.format("Tag '%s' has been created", name));
    }

    @Override
    public Message deleteTag(String name) {
        boolean deleted = tagsService.deleteTag(name);
        if (!deleted) {
            throw new TagNotFoundException(name);
        }

        return new Message(HttpStatus.OK, String.format("Tag '%s' has been deleted", name));
    }


    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(produces = "application/json") // todo do i need this?
    private Message tagNotFound(TagNotFoundException ex) {
        String tagName = ex.getTagName();
        return new Message(HttpStatus.NOT_FOUND, String.format("Tag '%s' can not be found", tagName));
    }

    @ExceptionHandler(TagAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    private Message tagAlreadyExist(TagAlreadyExistsException ex) {
        String tagName = ex.getTagName();
        return new Message(HttpStatus.CONFLICT, String.format("Tag '%s' already exists", tagName));
    }

}
