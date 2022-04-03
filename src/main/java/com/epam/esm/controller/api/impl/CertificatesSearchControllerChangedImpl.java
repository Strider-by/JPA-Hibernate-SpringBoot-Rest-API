package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.CertificatesSearchControllerChanged;
import com.epam.esm.controller.api.exception.BadRequestParametersException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.CertificatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CertificatesSearchControllerChangedImpl implements CertificatesSearchControllerChanged {

    @Autowired
    private final CertificatesService certificatesService;

    public CertificatesSearchControllerChangedImpl(CertificatesService certificatesService) {
        this.certificatesService = certificatesService;
    }


    @Override
    public HttpEntity<HateoasView<List<Certificate>>> searchCertificatesByPartOfNameOrDescription(
            Map<String, String> params) {

        if (!SearchParameterVerifier.verify(params)) {
            throw new BadRequestParametersException("Sent parameters does not meet expected values");
        }


        long limit = getLimitParameter(params);
        int page = getPageParameter(params);
        long offset = calcOffset(limit, page);

        List<Certificate> certificates = certificatesService.searchCertificates(limit, offset, params);

        HateoasView<List<Certificate>> view = createHateoasView(page, params, certificates);
        return new HttpEntity<>(view);
    }

    private int getPageParameter(Map<String, String> params) {
        String paramsSetPage = params.get("page");
        try {
            return paramsSetPage != null ? Integer.parseInt(paramsSetPage) : FIRST_PAGE_NUMBER;
        } catch (NumberFormatException ex) {
                throw new BadRequestParametersException("Failed to parse page parameter");
        }
    }

    private long getLimitParameter(Map<String, String> params) {
        String paramsSetLimit = params.get("limit");
        try {
            return paramsSetLimit != null ? Long.parseLong(paramsSetLimit) : DEFAULT_LIMIT;
        } catch (NumberFormatException ex) {
            throw new BadRequestParametersException("Failed to parse limit parameter");
        }
    }

    private HateoasView<List<Certificate>> createHateoasView(
            int page, Map<String, String> params, List<Certificate> certificates) {
        HateoasView<List<Certificate>> view = new HateoasView<>(certificates);

        if (page != FIRST_PAGE_NUMBER) {
            params.put("page", FIRST_PAGE_NUMBER_AS_STRING);

            view.add(linkTo(methodOn(CertificatesSearchControllerChanged.class)
                    .searchCertificatesByPartOfNameOrDescription(params)).withRel(FIRST));

            params.put("page", Integer.toString(page - 1));
            view.add(linkTo(methodOn(CertificatesSearchControllerChanged.class)
                    .searchCertificatesByPartOfNameOrDescription(params)).withRel(PREVIOUS));
        }

        params.put("page", Integer.toString(page));
        view.add(linkTo(methodOn(CertificatesSearchControllerChanged.class)
                .searchCertificatesByPartOfNameOrDescription(params)).withSelfRel());

        params.put("page", Integer.toString(page + 1));
        view.add(linkTo(methodOn(CertificatesSearchControllerChanged.class)
                .searchCertificatesByPartOfNameOrDescription(params)).withRel(NEXT));


//        String paramsAsUriString =
//                MvcUriComponentsBuilder.fromController(CertificatesSearchControllerChanged.class).queryParams(params)
//                        /*.build(params)*/.toUriString();
//
//        System.err.println(" >> " + paramsAsUriString);

//        view/*.add(linkTo(methodOn(CertificatesSearchControllerChanged.class)
//                .searchCertificatesByPartOfNameOrDescription(limit, page, params)).withSelfRel())*/
//            .add(linkTo(CertificatesSearchControllerChanged.class).slash("dodo")
//                    /*.slash(paramsAsUriString)*/.withRel(NEXT))
//        .add(
//                Link.of(
//                        linkTo(CertificatesSearchControllerChanged.class)
//                                .toUriComponentsBuilder().queryParams(params).toUriString()
//                , NEXT));

        return view;
    }

    @ExceptionHandler(BadRequestParametersException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Message badRequestFound(BadRequestParametersException ex) {
        return new Message(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private enum SearchParameterVerifier {

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

        SORT_BY("sort_by", Arrays.asList("date", "name")) {
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

        SearchParameterVerifier(String paramName) {
            this.paramName = paramName;
        }

        SearchParameterVerifier(String paramName, List<String> allowedParamValues) {
            this.paramName = paramName;
            this.allowedParamValues = allowedParamValues;
        }

        public static boolean isAllowedNameValueParameter(String paramName, String paramValue) {
            return getParameterVerifier(paramName).isAllowedParameterValue(paramValue);
        }

        private static SearchParameterVerifier getParameterVerifier(String paramName) {
            return Arrays.stream(values())
                    .filter(verifier -> verifier.paramName.equalsIgnoreCase(paramName))
                    .findAny()
                    .orElse(DEFAULT);
        }

        public abstract boolean isAllowedParameterValue(String value);

        public static boolean verify(Map<String, String> params) {
            for(Map.Entry<String, String> parameter : params.entrySet()) {
                if (!isAllowedNameValueParameter(parameter.getKey(), parameter.getValue())) {
                    return false;
                }
            }
            return true;
        }

    }

}
