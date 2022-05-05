package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.CertificateSearchByTagsController;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class CertificateSearchByTagsControllerImpl implements CertificateSearchByTagsController {

    @Autowired
    private final CertificateService certificateService;


    public CertificateSearchByTagsControllerImpl(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Override
    public HateoasView<Certificate> search(List<String> tags, int pageNumber, int pageSize) {
        tags = tags != null ? tags : Collections.emptyList();
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Certificate> page = certificateService.searchCertificatesByTagNames(tags, pageable);
        BiFunction<Integer, Integer, String> hrefGenerator = createSearchCertificateByTagNamesHrefGenerator(tags);
        HateoasView<Certificate> view = new HateoasView<>(page, hrefGenerator);
        return view;
    }

    private BiFunction<Integer, Integer, String> createSearchCertificateByTagNamesHrefGenerator(List<String> tags) {
        return (pageNumber, pageSize) -> linkTo(methodOn(CertificateSearchByTagsController.class)
                .search(tags, pageNumber, pageSize)).toString();
    }

}
