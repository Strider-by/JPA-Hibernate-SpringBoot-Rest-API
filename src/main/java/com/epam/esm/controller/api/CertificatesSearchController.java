package com.epam.esm.controller.api;//package com.epam.esm.controller.api;
//
//import com.epam.esm.entity.Certificate;
//import com.epam.esm.entity.representation.HateoasView;
//import org.springframework.http.HttpEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.UnsupportedEncodingException;
//import java.util.List;
//
//@RequestMapping("/searchOld")
//public interface CertificatesSearchController {
//
//    String DEFAULT_LIMIT = "500";
//
//    @RequestMapping(value = "**", method = RequestMethod.GET, produces = "application/json")
//    HttpEntity<HateoasView<List<Certificate>>> searchCertificatesByPartOfNameOrDescription(
//            @RequestParam(defaultValue = DEFAULT_LIMIT)
//            long limit,
//            @RequestParam(defaultValue = ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING)
//            int page,
//            HttpServletRequest request)
//            throws UnsupportedEncodingException;
//}
