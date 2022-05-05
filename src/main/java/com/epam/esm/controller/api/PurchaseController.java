package com.epam.esm.controller.api;

import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.model.representation.HateoasViewV2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING;
import static com.epam.esm.controller.api.ControllerHelper.calcPageNumberForPageRequest;

@RequestMapping("/purchases")
public interface PurchaseController {

    String DEFAULT_PAGE_SIZE = "10";

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    Purchase getPurchaseById(@PathVariable long id);


    @GetMapping(value="/all", produces = "application/json")
    @ResponseBody
    HateoasViewV2<Purchase> getAllPurchases(
            @RequestParam(name = "page", defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = "limit", defaultValue = DEFAULT_PAGE_SIZE) int pageSize);


    @GetMapping(value = "/user/{userId}", produces = "application/json")
    @ResponseBody
    HateoasViewV2<Purchase> getUserPurchases(
            @PathVariable long userId,
            @RequestParam(name = "page", defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = "limit", defaultValue = DEFAULT_PAGE_SIZE) int pageSize);


    @GetMapping(value = "/primary-tags/user/{userId}", produces = "application/json")
    @ResponseBody
    HateoasViewV2<Tag> getUserPrimaryTags(
            @PathVariable long userId,
            @RequestParam(name = "page", defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = "limit", defaultValue = DEFAULT_PAGE_SIZE) int pageSize);


    @GetMapping(value = "/primary-tags/", produces = "application/json")
    @ResponseBody
    HateoasViewV2<Tag> getPrimaryTags(
            @RequestParam(name = "page", defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = "limit", defaultValue = DEFAULT_PAGE_SIZE) int pageSize);


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
