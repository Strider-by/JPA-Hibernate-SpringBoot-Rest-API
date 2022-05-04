package com.epam.esm.controller.api;

import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.representation.HateoasViewV2;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/certificates")
public interface CertificatesController {

    String DEFAULT_PAGE_SIZE = "500";

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    HateoasViewV2<Certificate> getAllCertificates(
            @RequestParam(defaultValue = ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING, name = "page")
                    int pageNumber,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE, name = "limit")
                    int pageSize);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    Certificate getCertificate(@PathVariable long id);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    Certificate createCertificate(CertificateCreateDto dto);

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    Message deleteCertificate(@PathVariable long id);

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = "application/json")
    @ResponseBody
    Certificate updateCertificate(
            @PathVariable("id") long id,
            @RequestBody MultiValueMap<String, String> params);

}
