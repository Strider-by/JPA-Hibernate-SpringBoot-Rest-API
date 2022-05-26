package com.epam.esm.controller.api;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequestMapping("/search")
public interface CertificateSearchController {

    @GetMapping(produces = "application/json")
    HateoasView<Certificate> searchCertificatesByPartOfNameOrDescription(@RequestParam Map<String, String> params);
}
