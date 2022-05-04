package com.epam.esm.controller.api;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasViewV2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/searchByTags")
public interface CertificatesSearchByTagsController {

    String DEFAULT_PAGE_SIZE = "500";

//    @RequestMapping(/*value = "**", */method = RequestMethod.GET, produces = "application/json")
//    HttpEntity<HateoasView<List<Certificate>>> search(
//            @RequestParam(value = "tag", required = false)
//                    List<String> tags,
//            @RequestParam(required = false, defaultValue = DEFAULT_LIMIT)
//                    long limit,
//            @RequestParam(required = false, defaultValue = ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING)
//                    int page);

//    HttpEntity<HateoasViewV2<Certificate>> search(List<String> tags, int pageNumber, int pageSize);

    @RequestMapping(/*value = "**", */method = RequestMethod.GET, produces = "application/json")
    HateoasViewV2<Certificate> search(
            @RequestParam(value = "tag", required = false)
                    List<String> tags,
            @RequestParam(defaultValue = ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING, name = "page")
                    int pageNumber,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE, name = "limit")
                    int pageSize);

}
