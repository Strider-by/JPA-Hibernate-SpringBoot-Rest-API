package com.epam.esm.controller.api;

import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.epam.esm.controller.api.ControllerHelper.*;

@RequestMapping("/purchases")
public interface PurchaseController {

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    Purchase getPurchaseById(@PathVariable long id);

    @GetMapping(value = "/all", produces = "application/json")
    @ResponseBody
    HateoasView<Purchase> getAllPurchases(
            @RequestParam(name = DEFAULT_PAGE_NUMBER_PARAM_NAME, defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = DEFAULT_PAGE_SIZE_PARAM_NAME, defaultValue = DEFAULT_PAGE_SIZE_AS_STRING) int pageSize);

    @GetMapping(value = "/user/{userId}", produces = "application/json")
    @ResponseBody
    HateoasView<Purchase> getUserPurchases(
            @PathVariable long userId,
            @RequestParam(name = DEFAULT_PAGE_NUMBER_PARAM_NAME, defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = DEFAULT_PAGE_SIZE_PARAM_NAME, defaultValue = DEFAULT_PAGE_SIZE_AS_STRING) int pageSize);

    @GetMapping(value = "/primary-tags/user/{userId}", produces = "application/json")
    @ResponseBody
    HateoasView<Tag> getUserPrimaryTags(
            @PathVariable long userId,
            @RequestParam(name = DEFAULT_PAGE_NUMBER_PARAM_NAME, defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = DEFAULT_PAGE_SIZE_PARAM_NAME, defaultValue = DEFAULT_PAGE_SIZE_AS_STRING) int pageSize);

    @GetMapping(value = "/primary-tags/", produces = "application/json")
    @ResponseBody
    HateoasView<Tag> getPrimaryTags(
            @RequestParam(name = DEFAULT_PAGE_NUMBER_PARAM_NAME, defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int pageNumber,
            @RequestParam(name = DEFAULT_PAGE_SIZE_PARAM_NAME, defaultValue = DEFAULT_PAGE_SIZE_AS_STRING) int pageSize);

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    Purchase purchaseCertificate(long userId, long certificateId);

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    Message deletePurchase(@PathVariable long id);

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = "application/json")
    @ResponseBody
    default void updatePurchase(@PathVariable("id") long id, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.I_AM_A_TEAPOT.value());
    }

}
