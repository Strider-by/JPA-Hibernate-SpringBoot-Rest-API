package com.epam.esm.controller.api.impl;//package com.epam.esm.controller.api.impl;
//
//import com.epam.esm.controller.api.CertificatesSearchController;
//import com.epam.esm.controller.api.exception.BadRequestParametersException;
//import com.epam.esm.controller.util.Message;
//import com.epam.esm.entity.Certificate;
//import com.epam.esm.entity.representation.HateoasView;
//import com.epam.esm.service.CertificatesService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//public class CertificatesSearchControllerImpl implements CertificatesSearchController {
//
//    @Autowired
//    private final CertificatesService certificatesService;
//
//    public CertificatesSearchControllerImpl(CertificatesService certificatesService) {
//        this.certificatesService = certificatesService;
//    }
//
//
//    @Override
//    public HttpEntity<HateoasView<List<Certificate>>> searchCertificatesByPartOfNameOrDescription(
//            long limit,
//            int page,
//            HttpServletRequest request)
//            throws UnsupportedEncodingException {
//
//
//        String[] uriParts = request.getRequestURI().split("/");
//        int partsToSkip = 2;
//
//        String[] actualParametersPart = new String[uriParts.length - partsToSkip];
//        System.arraycopy(uriParts, partsToSkip, actualParametersPart, 0, actualParametersPart.length);
//
//        if (actualParametersPart.length % 2 != 0) {
//            throw new BadRequestParametersException("Quantity of parameters names and values must be equal");
//        }
//
//        Map<String, String> parameters = new HashMap<>();
//        for (int i = 0; i < actualParametersPart.length - 1; i += 2) {
//            String paramName = URLDecoder.decode(actualParametersPart[i].toLowerCase(), "utf-8");
//            String paramValue = URLDecoder.decode(actualParametersPart[i + 1].toLowerCase(), "utf-8");
//
//            boolean isValidParameter = SearchParameterVerifier.isAllowedNameValueParameter(paramName, paramValue);
//            if (!isValidParameter) {
//                throw new BadRequestParametersException(
//                        String.format("Invalid name - value parameter pair: [%s] = [%s]", paramName, paramValue));
//            }
//
//            parameters.put(paramName, paramValue);
//
//        }
//
//        List<Certificate> certificates = certificatesService.searchCertificates(parameters);
//        HateoasView<List<Certificate>> view = createHateoasView(limit, page, certificates);
//        return new HttpEntity<>(view);
//    }
//
//    private HateoasView<List<Certificate>> createHateoasView(long limit, int page, List<Certificate> certificates) {
//        return null;
//    }
//
//    @ExceptionHandler(BadRequestParametersException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    private Message badRequestFound(BadRequestParametersException ex) {
//        return new Message(HttpStatus.BAD_REQUEST, ex.getMessage());
//    }
//
//    private enum SearchParameterVerifier {
//
//        TAG("tag") {
//            @Override
//            public boolean isAllowedParameterValue(String value) {
//                return true;
//            }
//        },
//
//        CONTAINS("contains") {
//            @Override
//            public boolean isAllowedParameterValue(String value) {
//                return true;
//            }
//        },
//
//        SORT_BY("sort_by", Arrays.asList("date", "name")) {
//            @Override
//            public boolean isAllowedParameterValue(String value) {
//                return this.allowedParamValues.contains(value.toLowerCase());
//            }
//        },
//
//        ORDER("order", Arrays.asList("asc", "desc")) {
//            @Override
//            public boolean isAllowedParameterValue(String value) {
//                return this.allowedParamValues.contains(value.toLowerCase());
//            }
//        },
//
//        DEFAULT("") {
//            @Override
//            public boolean isAllowedParameterValue(String value) {
//                return false;
//            }
//        };
//
//        String paramName;
//        List<String> allowedParamValues;
//
//        SearchParameterVerifier(String paramName) {
//            this.paramName = paramName;
//        }
//
//        SearchParameterVerifier(String paramName, List<String> allowedParamValues) {
//            this.paramName = paramName;
//            this.allowedParamValues = allowedParamValues;
//        }
//
//        public static boolean isAllowedNameValueParameter(String paramName, String paramValue) {
//            return getParameterVerifier(paramName).isAllowedParameterValue(paramValue);
//        }
//
//        private static SearchParameterVerifier getParameterVerifier(String paramName) {
//            return Arrays.stream(values())
//                    .filter(verifier -> verifier.paramName.equalsIgnoreCase(paramName))
//                    .findAny()
//                    .orElse(DEFAULT);
//        }
//
//        public abstract boolean isAllowedParameterValue(String value);
//
//    }
//
//}
