package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.CertificatesController;
import com.epam.esm.controller.api.CertificatesSearchByTagsController;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasViewV2;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class CertificatesSearchByTagsControllerImpl implements CertificatesSearchByTagsController {

    @Autowired
    private final CertificateService certificateService;

    public CertificatesSearchByTagsControllerImpl(CertificateService certificateService) {
        this.certificateService = certificateService;
    }


    @Override
    public HateoasViewV2<Certificate> search(List<String> tags, int pageNumber, int pageSize) {
        tags = tags != null ? tags : Collections.emptyList();
        Page<Certificate> page = certificateService.searchCertificatesByTagNames(tags, pageNumber, pageSize);
        HateoasViewV2<Certificate> view = new HateoasViewV2<>(page);
        BiFunction<Integer, Integer, String> hrefGenerator = createSearchCertificateByTagNamesHrefGenerator(tags);
        view.generateStandardLinks(hrefGenerator);
        return view;
    }

    private BiFunction<Integer, Integer, String> createSearchCertificateByTagNamesHrefGenerator(List<String> tags) {
        return (pageNumber, pageSize) -> linkTo(methodOn(CertificatesSearchByTagsController.class)
                .search(tags, pageNumber, pageSize)).toString();
    }

//    private HateoasView<List<Certificate>> createHateoasView(
//            List<String> tags, long limit, int page, List<Certificate> certificates) {
//
//        HateoasView<List<Certificate>> view = new HateoasView<>(certificates);
//
//        if (page != FIRST_PAGE_NUMBER) {
//            view.add(linkTo(methodOn(CertificatesSearchByTagsControllerImpl.class)
//                        .search(tags, limit, FIRST_PAGE_NUMBER)).withRel(FIRST))
//                .add(linkTo(methodOn(CertificatesSearchByTagsControllerImpl.class)
//                        .search(tags, limit, page - 1)).withRel(PREVIOUS)
//            );
//        }
//
//        view.add(linkTo(methodOn(CertificatesSearchByTagsControllerImpl.class)
//                    .search(tags, limit, page)).withSelfRel())
//            .add(linkTo(methodOn(CertificatesSearchByTagsControllerImpl.class)
//                    .search(tags, limit, page + 1)).withRel(NEXT));
//
//        return view;
//    }

//    private HateoasViewV2<Certificate> createHateoasView(Page<Certificate> page, List<String> tags) {
//
//        HateoasViewV2<Certificate> view = new HateoasViewV2<>(page);
//
//        if (!isFirstPage(page)) {
//            view.add(linkTo(methodOn(CertificatesSearchByTagsController.class)
//                    .search(tags, FIRST_PAGE_NUMBER, page.getSize())).withRel(FIRST))
//                    .add(linkTo(methodOn(CertificatesController.class)
//                            .getAllCertificates(previousPageNumber(page), page.getSize())).withRel(PREVIOUS));
//        }
//
//        view.add(linkTo(methodOn(CertificatesSearchByTagsController.class)
//                .search(tags, pageNumber(page), page.getSize())).withSelfRel());
//
//        if (page.hasNext()) {
//            view.add(linkTo(methodOn(CertificatesSearchByTagsController.class)
//                    .search(tags, nextPageNumber(page), page.getSize())).withRel(NEXT));
//        }
//
//        return view;
//    }


}
