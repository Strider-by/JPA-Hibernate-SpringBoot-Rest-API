package com.epam.esm.controller.api;

import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import static com.epam.esm.controller.api.ControllerHelper.*;

@RequestMapping("/certificates")
public interface CertificateController {

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    HateoasView<Certificate> getAllCertificates(
            @RequestParam(name = DEFAULT_PAGE_NUMBER_PARAM_NAME, defaultValue = FIRST_PAGE_NUMBER_AS_STRING)
                    int pageNumber,
            @RequestParam(name = DEFAULT_PAGE_SIZE_PARAM_NAME, defaultValue = DEFAULT_PAGE_SIZE_AS_STRING)
                    int pageSize);

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    Certificate getCertificate(@PathVariable long id);

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    Certificate createCertificate(CertificateCreateDto dto);

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    Message deleteCertificate(@PathVariable long id);

    @PatchMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    Certificate updateCertificate(
            @PathVariable/*("id")*/ long id,
            @RequestBody MultiValueMap<String, String> params);

}
