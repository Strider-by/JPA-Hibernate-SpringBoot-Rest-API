package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.CertificatesSearchController;
import com.epam.esm.controller.api.exception.BadRequestParametersException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasViewV2;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class CertificatesSearchControllerImpl implements CertificatesSearchController {

    @Autowired
    private final CertificateService certificateService;

    public CertificatesSearchControllerImpl(CertificateService certificateService) {
        this.certificateService = certificateService;
    }


    @Override
    public HateoasViewV2<Certificate> searchCertificatesByPartOfNameOrDescription(Map<String, String> params) {
        SearchParametersVerifier.verify(params);
        int pageSize = getLimitParameter(params);
        int pageNumber = getPageParameter(params);
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Certificate> page = certificateService.searchCertificates(params, pageable);
        BiFunction<Integer, Integer, String> hrefGenerator = createSearchCertificatesHrefGenerator(params);
        HateoasViewV2 view = new HateoasViewV2(page, hrefGenerator);
        return view;
    }

    private BiFunction<Integer, Integer, String> createSearchCertificatesHrefGenerator(Map<String, String> params) {
        return (pageNumber, pageSize) -> {
            params.put("page", Integer.toString(pageNumber));
            params.put("limit", Integer.toString(pageSize));
            return linkTo(methodOn(CertificatesSearchController.class)
                    .searchCertificatesByPartOfNameOrDescription(params)).toString();
        };
    }

    @ExceptionHandler(BadRequestParametersException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Message badRequestFound(BadRequestParametersException ex) {
        return new Message(HttpStatus.BAD_REQUEST, ex.getMessage());
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
