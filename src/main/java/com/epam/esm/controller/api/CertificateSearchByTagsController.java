package com.epam.esm.controller.api;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.*;

@RequestMapping("/searchByTags")
public interface CertificateSearchByTagsController {

    @GetMapping(produces = "application/json")
    HateoasView<Certificate> search(
            @RequestParam(value = "tag", required = false)
                    List<String> tags,
            @RequestParam(name = DEFAULT_PAGE_NUMBER_PARAM_NAME, defaultValue = FIRST_PAGE_NUMBER_AS_STRING)
                    int pageNumber,
            @RequestParam(name = DEFAULT_PAGE_SIZE_PARAM_NAME, defaultValue = DEFAULT_PAGE_SIZE_AS_STRING)
                    int pageSize);

}
