package com.epam.esm.controller.api;

import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.CertificateUpstreamDto;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/certificates")
public interface CertificatesController {

    String DEFAULT_LIMIT = "500";

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    HttpEntity<HateoasView<List<Certificate>>> getAllCertificates(
            @RequestParam(defaultValue = DEFAULT_LIMIT)
                    long limit,
            @RequestParam(defaultValue = ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING)
                    int page);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    Certificate getCertificate(@PathVariable long id);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    CertificateUpstreamDto createCertificate(CertificateCreateDto dto);

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    Message deleteCertificate(@PathVariable long id);

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = "application/json")
    @ResponseBody
    Certificate updateCertificate(
            @PathVariable("id") long id,
            @RequestBody MultiValueMap<String, String> params);

}
