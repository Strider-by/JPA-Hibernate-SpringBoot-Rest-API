package com.epam.esm.controller.api;

import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.model.representation.HateoasViewV2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.epam.esm.controller.api.ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING;

@RequestMapping("/tags")
public interface TagController {

    String DEFAULT_PAGE_SIZE = "10";

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    HateoasViewV2<Tag> getAllTags(
            @RequestParam(name = "page", defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = "limit", defaultValue = DEFAULT_PAGE_SIZE) int pageSize);

    @GetMapping(value = "/{name}", produces = "application/json")
    @ResponseBody
    Tag getTag(@PathVariable String name);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    Tag createTag(TagCreateDto tag);

    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    Message deleteTag(@PathVariable String name);

}
