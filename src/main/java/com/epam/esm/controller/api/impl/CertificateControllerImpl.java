package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.CertificateController;
import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CertificateControllerImpl extends ControllerExceptionHandlingBase implements CertificateController {

    @Autowired
    private CertificateService certificateService;

    private static final BiFunction<Integer, Integer, String> GET_CERTIFICATES_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(CertificateController.class)
                    .getAllCertificates(pageNumber, pageSize)).toString();


    public CertificateControllerImpl(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Override
    public HateoasView<Certificate> getAllCertificates(int pageNumber, int pageSize) {
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Certificate> page = certificateService.getAllCertificates(pageable);
        HateoasView view = new HateoasView(page, GET_CERTIFICATES_HREF_GENERATOR);
        return view;
    }

    @Override
    public Certificate getCertificate(long id) {
        return certificateService.getCertificate(id);
    }

    @Override
    public Certificate createCertificate(CertificateCreateDto dto) {
        return certificateService.createCertificate(dto);
    }

    @Override
    public Message deleteCertificate(long id) {
        certificateService.deleteCertificate(id);
        return new Message(HttpStatus.OK, String.format("Certificate %d has been deleted", id));
    }

    @Override
    public Certificate updateCertificate(long id, MultiValueMap<String, String> params) {
        return certificateService.updateCertificate(id, params);
    }

}
