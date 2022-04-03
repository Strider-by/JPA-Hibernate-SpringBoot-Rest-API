package com.epam.esm.controller.api;

import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagDownstreamDto;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING;

@RequestMapping("/tags")
public interface TagsController {

    String DEFAULT_LIMIT = "500";

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    HttpEntity<HateoasView<List<Tag>>> getAllTags(
            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
            @RequestParam(defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int page
    );

    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    Tag getTag(@PathVariable String name);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    Message createTag(TagDownstreamDto tag);

    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    Message deleteTag(@PathVariable String name);

}
