package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.CertificatesSearchByTagsController;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.CertificatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class CertificatesSearchByTagsControllerImpl implements CertificatesSearchByTagsController {

    @Autowired
    private final CertificatesService certificatesService;

    public CertificatesSearchByTagsControllerImpl(CertificatesService certificatesService) {
        this.certificatesService = certificatesService;
    }


    @Override
    public HttpEntity<HateoasView<List<Certificate>>> search(List<String> tags, long limit, int page) {

        long offset = calcOffset(limit, page);
        tags = tags != null ? tags : Collections.emptyList();

        List<Certificate> certificates = certificatesService.searchCertificatesByTagNames(tags, limit, offset);
        HateoasView<List<Certificate>> view = createHateoasView(tags, limit, page, certificates);
        return new HttpEntity<>(view);
    }

    private HateoasView<List<Certificate>> createHateoasView(
            List<String> tags, long limit, int page, List<Certificate> certificates) {

        HateoasView<List<Certificate>> view = new HateoasView<>(certificates);

        if (page != FIRST_PAGE_NUMBER) {
            view.add(linkTo(methodOn(CertificatesSearchByTagsControllerImpl.class)
                        .search(tags, limit, FIRST_PAGE_NUMBER)).withRel(FIRST))
                .add(linkTo(methodOn(CertificatesSearchByTagsControllerImpl.class)
                        .search(tags, limit, page - 1)).withRel(PREVIOUS)
            );
        }

        view.add(linkTo(methodOn(CertificatesSearchByTagsControllerImpl.class)
                    .search(tags, limit, page)).withSelfRel())
            .add(linkTo(methodOn(CertificatesSearchByTagsControllerImpl.class)
                    .search(tags, limit, page + 1)).withRel(NEXT));

        return view;
    }


}
