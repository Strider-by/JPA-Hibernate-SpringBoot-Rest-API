package com.epam.esm.controller.api;

import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.epam.esm.controller.api.ControllerHelper.*;

@RequestMapping("/tags")
public interface TagController {

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    HateoasView<Tag> getAllTags(
            @RequestParam(name = DEFAULT_PAGE_NUMBER_PARAM_NAME, defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = DEFAULT_PAGE_SIZE_PARAM_NAME, defaultValue = DEFAULT_PAGE_SIZE_AS_STRING) int pageSize);

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
