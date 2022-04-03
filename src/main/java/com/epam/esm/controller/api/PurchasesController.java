package com.epam.esm.controller.api;

import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING;

@RequestMapping("/purchases")
public interface PurchasesController {

    String DEFAULT_LIMIT = "500";

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    Purchase getPurchaseById(@PathVariable long id);

    @GetMapping(value="/all", produces = "application/json")
    @ResponseBody
    HttpEntity<HateoasView<List<Purchase>>> getAllPurchases(
            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
            @RequestParam(defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int page);

    @GetMapping(value = "/user/{userId}", produces = "application/json")
    @ResponseBody
    HttpEntity<HateoasView<List<Purchase>>> getUserPurchases(
            @PathVariable long userId,
            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
            @RequestParam(defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int page);

    @GetMapping(value = "/primary-tags/{userId}", produces = "application/json")
    @ResponseBody
    HttpEntity<HateoasView<List<String>>> getUserPrimaryTags(
            @PathVariable long userId,
            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
            @RequestParam(defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int page);

    @GetMapping(value = "/primary-tags/", produces = "application/json")
    @ResponseBody
    HttpEntity<HateoasView<List<String>>> getPrimaryTags(
            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
            @RequestParam(defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int page
    );

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    Purchase purchaseCertificate(long userId, long certificateId);

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    Message deletePurchase(
            @PathVariable long id,
            HttpServletResponse response) throws IOException;

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = "application/json")
    @ResponseBody
    default void updatePurchase(
            @PathVariable("id") long id,
            HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.I_AM_A_TEAPOT.value());
    }

}
