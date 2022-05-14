package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.CertificateSearchController;
import com.epam.esm.controller.api.exception.BadRequestParametersException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class CertificateSearchControllerImpl extends BaseExceptionHandlingController implements CertificateSearchController {

    @Autowired
    private final CertificateService certificateService;


    public CertificateSearchControllerImpl(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Override
    public HateoasView<Certificate> searchCertificatesByPartOfNameOrDescription(Map<String, String> params) {
        SearchParametersVerifier.verify(params);
        int pageSize = getPageSizeParameter(params);
        int pageNumber = getPageNumberParameter(params);
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Certificate> page = certificateService.searchCertificates(params, pageable);
        BiFunction<Integer, Integer, String> hrefGenerator = createSearchCertificatesHrefGenerator(params);
        HateoasView view = new HateoasView(page, hrefGenerator);
        return view;
    }

    private BiFunction<Integer, Integer, String> createSearchCertificatesHrefGenerator(Map<String, String> params) {
        return (pageNumber, pageSize) -> {
            params.put("page", Integer.toString(pageNumber));
            params.put("limit", Integer.toString(pageSize));
            return linkTo(methodOn(CertificateSearchController.class)
                    .searchCertificatesByPartOfNameOrDescription(params)).toString();
        };
    }


    private enum SearchParametersVerifier {

        TAG("tag") {
            @Override
            public boolean isAllowedParameterValue(String value) {
                return true;
            }
        },

        CONTAINS("contains") {
            @Override
            public boolean isAllowedParameterValue(String value) {
                return true;
            }
        },

        SORT_BY("sort_by", Arrays.asList("date", "name", "updated")) {
            @Override
            public boolean isAllowedParameterValue(String value) {
                return this.allowedParamValues.contains(value.toLowerCase());
            }
        },

        ORDER("order", Arrays.asList("asc", "desc")) {
            @Override
            public boolean isAllowedParameterValue(String value) {
                return this.allowedParamValues.contains(value.toLowerCase());
            }
        },

        LIMIT("limit") {
            @Override
            public boolean isAllowedParameterValue(String value) {
                return true;
            }
        },

        PAGE("page") {
            @Override
            public boolean isAllowedParameterValue(String value) {
                return true;
            }
        },

        DEFAULT("") {
            @Override
            public boolean isAllowedParameterValue(String value) {
                return false;
            }
        };

        String paramName;
        List<String> allowedParamValues;

        SearchParametersVerifier(String paramName) {
            this.paramName = paramName;
        }

        SearchParametersVerifier(String paramName, List<String> allowedParamValues) {
            this.paramName = paramName;
            this.allowedParamValues = allowedParamValues;
        }

        public static boolean isAllowedNameValueParameter(String paramName, String paramValue) {
            return getParameterVerifier(paramName).isAllowedParameterValue(paramValue);
        }

        private static SearchParametersVerifier getParameterVerifier(String paramName) {
            return Arrays.stream(values())
                    .filter(verifier -> verifier.paramName.equalsIgnoreCase(paramName))
                    .findAny()
                    .orElse(DEFAULT);
        }

        public abstract boolean isAllowedParameterValue(String value);

        public static void verify(Map<String, String> params) throws BadRequestParametersException {
            for(Map.Entry<String, String> parameter : params.entrySet()) {
                if (!isAllowedNameValueParameter(parameter.getKey(), parameter.getValue())) {
                    throw new BadRequestParametersException("Sent parameters does not meet expected values");
                }
            }
        }

    }

}
