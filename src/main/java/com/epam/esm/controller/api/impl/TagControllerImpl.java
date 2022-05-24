package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.TagController;
import com.epam.esm.controller.api.exception.TagNotFoundException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TagControllerImpl extends ControllerExceptionHandlingBase implements TagController {

    @Autowired
    private final TagService tagService;

    private static final BiFunction<Integer, Integer, String> GET_TAGS_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(TagController.class)
                    .getAllTags(pageNumber, pageSize)).toString();


    public TagControllerImpl(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public HateoasView<Tag> getAllTags(int pageNumber, int pageSize) {
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Tag> page = tagService.getAllTags(pageable);
        HateoasView<Tag> view = new HateoasView<>(page, GET_TAGS_HREF_GENERATOR);
        return view;
    }

    @Override
    public Tag getTag(String name) {
        Tag tag = tagService.getTag(name);
        if (tag == null) {
            throw new TagNotFoundException(name);
        }
        return tag;
    }

    @Override
    public Tag createTag(TagCreateDto dto) {
        Tag tag = tagService.createTag(dto);
        return tag;
    }

    @Override
    public Message deleteTag(String name) {
        tagService.deleteTag(name);
        return new Message(HttpStatus.OK, String.format("Tag '%s' has been deleted", name));
    }

}
