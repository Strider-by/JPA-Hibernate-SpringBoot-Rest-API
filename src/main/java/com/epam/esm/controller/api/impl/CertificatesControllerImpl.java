package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.CertificatesController;
import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.CertificateUpstreamDto;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.model.util.DtoConverter;
import com.epam.esm.service.CertificatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CertificatesControllerImpl implements CertificatesController {

    @Autowired
    private CertificatesService certificatesService;

    public CertificatesControllerImpl(CertificatesService certificatesService) {
        this.certificatesService = certificatesService;
    }

    public CertificatesControllerImpl() {
    }

    public void setCertificatesService(CertificatesService certificatesService) {
        this.certificatesService = certificatesService;
    }

    @Override
    public HttpEntity<HateoasView<List<Certificate>>> getAllCertificates(long limit, int page) {
        long offset = calcOffset(limit, page);
        List<Certificate> certificates = certificatesService.getAllCertificates(limit, offset);
        HateoasView<List<Certificate>> view = createGetAllCertificatesHateoasView(limit, page, certificates);
        return new HttpEntity<>(view);
    }

    private HateoasView<List<Certificate>> createGetAllCertificatesHateoasView(
            long limit, int page, List<Certificate> certificates) {

        HateoasView<List<Certificate>> view = new HateoasView<>(certificates);

        if (page != FIRST_PAGE_NUMBER) {
            view.add(linkTo(methodOn(CertificatesController.class)
                    .getAllCertificates(limit, FIRST_PAGE_NUMBER)).withRel(FIRST))
                .add(linkTo(methodOn(CertificatesController.class)
                    .getAllCertificates(limit, page - 1)).withRel(PREVIOUS));
        }

        view.add(linkTo(methodOn(CertificatesController.class)
                    .getAllCertificates(limit, page)).withSelfRel())
            .add(linkTo(methodOn(CertificatesController.class)
                    .getAllCertificates(limit, page + 1)).withRel(NEXT));

        return view;
    }

    @Override
    public Certificate getCertificate(long id) {
        Certificate certificate = certificatesService.getCertificate(id);
        if (certificate == null) {
            throw new CertificateNotFoundException(id);
        }
        return certificate;
    }

    @Override
    public CertificateUpstreamDto createCertificate(CertificateCreateDto dto) {
        return DtoConverter.toCertificateUpstreamDto(certificatesService.createCertificate(dto));
    }

    @Override
    public Message deleteCertificate(long id) {
        boolean deleted = certificatesService.deleteCertificate(id);
        if (!deleted) {
            throw new CertificateNotFoundException(id);
        }

        return new Message(HttpStatus.OK, String.format("Certificate %d has been deleted", id));
    }

    @Override
    public Certificate updateCertificate(long id, MultiValueMap<String, String> params) {

        Certificate certificate = certificatesService.updateCertificate(id, params);
        if (certificate == null) {
            throw new CertificateNotFoundException(id);
        }

        return certificate;
    }

    @ExceptionHandler(CertificateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(produces = "application/json")
    private Message certificateNotFound(CertificateNotFoundException ex) {
        long id = ex.getCertificateId();
        return new Message(HttpStatus.NOT_FOUND, String.format("Certificate %d can not be found", id));
    }


}
