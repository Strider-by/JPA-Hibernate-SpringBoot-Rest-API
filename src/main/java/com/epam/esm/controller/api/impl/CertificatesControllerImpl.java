package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.CertificatesController;
import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.representation.HateoasViewV2;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CertificatesControllerImpl implements CertificatesController {

    @Autowired
    private CertificateService certificateService;

    private static final BiFunction<Integer, Integer, String> GET_CERTIFICATES_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(CertificatesController.class)
                    .getAllCertificates(pageNumber, pageSize)).toString();


//    @Override
//    public HttpEntity<HateoasView<List<Certificate>>> getAllCertificates(int limit, int page) {
//        long offset = calcOffset(limit, page);
//        List<Certificate> certificates = certificatesService.getAllCertificates(limit, offset);
//        HateoasView<List<Certificate>> view = createGetAllCertificatesHateoasView(limit, page, certificates);
//        return new HttpEntity<>(view);
//    }

    @Override
    public HateoasViewV2<Certificate> getAllCertificates(int pageNumber, int pageSize) {
//        System.err.printf("params got page: %d  size: %d", pageNumber, pageSize);
        Page<Certificate> page = certificateService.getAllCertificates(
                calcPageNumberForPageRequest(pageNumber), pageSize);
//        System.err.println(certificatePage.getContent().size());

//        HateoasViewV2<Certificate> view = createGetAllCertificatesHateoasView(certificatePage);
        HateoasViewV2 view = new HateoasViewV2(page);
        view.generateStandardLinks(GET_CERTIFICATES_HREF_GENERATOR);
//        return new HttpEntity<>(view);
        return view;
    }

//    private HateoasViewV2<Certificate> createGetAllCertificatesHateoasView(Page<Certificate> page) {
//
//        HateoasViewV2<Certificate> view = new HateoasViewV2<>(page);
//
//        if (!isFirstPage(page)) {
//            view.add(linkTo(methodOn(CertificatesController.class)
//                    .getAllCertificates(FIRST_PAGE_NUMBER, page.getSize())).withRel(FIRST))
//                .add(linkTo(methodOn(CertificatesController.class)
//                    .getAllCertificates(previousPageNumber(page), page.getSize())).withRel(PREVIOUS));
//        }
//
//        view.add(linkTo(methodOn(CertificatesController.class)
//                    .getAllCertificates(pageNumber(page), page.getSize())).withSelfRel());
//
//        if (page.hasNext()) {
//            view.add(linkTo(methodOn(CertificatesController.class)
//                    .getAllCertificates(nextPageNumber(page), page.getSize())).withRel(NEXT));
//        }
//
//        return view;
//    }

    @Override
    public Certificate getCertificate(long id) {
        Certificate certificate = certificateService.getCertificate(id);
        if (certificate == null) {
            throw new CertificateNotFoundException(id);
        }
        return certificate;
    }

    @Override
    public Certificate createCertificate(CertificateCreateDto dto) {
        return certificateService.createCertificate(dto);
    }

    @Override
    public Message deleteCertificate(long id) {
        boolean deleted = certificateService.deleteCertificate(id);
        if (!deleted) {
            throw new CertificateNotFoundException(id);
        }

        return new Message(HttpStatus.OK, String.format("Certificate %d has been deleted", id));
    }

    @Override
    public Certificate updateCertificate(long id, MultiValueMap<String, String> params) {

        Certificate certificate = certificateService.updateCertificate(id, params);
        if (certificate == null) {
            throw new CertificateNotFoundException(id);
        }

        return certificate;
    }

    @ExceptionHandler({CertificateNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(produces = "application/json")
    private Message certificateNotFound(CertificateNotFoundException ex) {
        long id = ex.getCertificateId();
        return new Message(HttpStatus.NOT_FOUND, String.format("Certificate %d can not be found", id));
    }

//    @ExceptionHandler({EntityNotFoundException.class})
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @RequestMapping(produces = "application/json")
//    private Message certificateNotFoundV2(EntityNotFoundException ex) {
//        return new Message(HttpStatus.NOT_FOUND, ex.getMessage());
//    }

//    private boolean isFirstPage(Page page) {
//        return page.getNumber() == 0;
//    }
//
//    private int pageNumber(Page page) {
//        return page.getNumber() + FIRST_PAGE_NUMBER;
//    }
//
//    private int nextPageNumber(Page page) {
//        return page.getNumber() + 1 + FIRST_PAGE_NUMBER;
//    }
//
//    private int previousPageNumber(Page page) {
//        return pageNumber(page) - 1;
//    }
//
//    private int calcPageNumberForPageRequest(int pageNumberParam) {
//        return pageNumberParam - FIRST_PAGE_NUMBER;
//    }

}
