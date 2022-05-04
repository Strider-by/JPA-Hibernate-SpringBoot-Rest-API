package com.epam.esm.controller.api;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasViewV2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequestMapping("/new_search")
public interface CertificatesSearchController {

    String DEFAULT_LIMIT_AS_STRING = "500";
    int DEFAULT_LIMIT = 500;

    @GetMapping(produces = "application/json")
    HateoasViewV2<Certificate> searchCertificatesByPartOfNameOrDescription(
            @RequestParam
            Map<String, String> params);





}
