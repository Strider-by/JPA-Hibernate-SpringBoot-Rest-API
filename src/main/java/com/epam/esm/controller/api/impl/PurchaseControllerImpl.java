package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.PurchaseController;
import com.epam.esm.controller.api.exception.BadRequestParametersException;
import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.controller.api.exception.PurchaseNotFoundException;
import com.epam.esm.controller.api.exception.UserNotFoundException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class PurchaseControllerImpl implements PurchaseController {

    @Autowired
    private PurchaseService service;

    private static final BiFunction<Integer, Integer, String> GET_PURCHASES_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(PurchaseController.class)
                    .getAllPurchases(pageNumber, pageSize)).toString();

    private static final BiFunction<Integer, Integer, String> GET_PRIMARY_TAGS_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(PurchaseController.class)
                    .getPrimaryTags(pageNumber, pageSize)).toString();


    public PurchaseControllerImpl(PurchaseService service) {
        this.service = service;
    }

    @Override
    public Purchase getPurchaseById(long id) {
        return service.getPurchaseById(id);
    }

    @Override
    public HateoasView<Purchase> getAllPurchases(int pageNumber, int pageSize) {
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Purchase> page = service.getAllPurchases(pageable);
        HateoasView view = new HateoasView(page, GET_PURCHASES_HREF_GENERATOR);
        return view;
    }

    @Override
    public HateoasView<Purchase> getUserPurchases(long userId, int pageNumber, int pageSize) {
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Purchase> page = service.getUserPurchases(userId, pageable);
        BiFunction<Integer, Integer, String> hrefGenerator = createGetUserPurchasesHrefGenerator(userId);
        HateoasView<Purchase> view = new HateoasView<>(page, hrefGenerator);
        return view;
    }

    private BiFunction<Integer, Integer, String> createGetUserPurchasesHrefGenerator(long userId) {
        return (pageNumber, pageSize) -> linkTo(methodOn(PurchaseController.class)
                    .getUserPurchases(userId, pageNumber, pageSize)).toString();
    }

    @Override
    public HateoasView<Tag> getUserPrimaryTags(long userId, int pageNumber, int pageSize) {
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Tag> page = service.getUserPrimaryTags(userId, pageable);
        BiFunction<Integer, Integer, String> hrefGenerator = createGetUserPrimaryTagsHrefGenerator(userId);
        HateoasView<Tag> view = new HateoasView<>(page, hrefGenerator);
        return view;
    }

    private BiFunction<Integer, Integer, String> createGetUserPrimaryTagsHrefGenerator(long userId) {
        return (pageNumber, pageSize) -> linkTo(methodOn(PurchaseController.class)
                .getUserPrimaryTags(userId, pageNumber, pageSize)).toString();
    }

    @Override
    public HateoasView<Tag> getPrimaryTags(int pageNumber, int pageSize) {
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<Tag> page = service.getPrimaryTags(pageable);
        HateoasView<Tag> view = new HateoasView<>(page, GET_PRIMARY_TAGS_HREF_GENERATOR);
        return view;
    }

    @Override
    public Purchase purchaseCertificate(long userId, long certificateId) {
        try {
            return service.purchaseCertificate(userId, certificateId);
        } catch (CertificateNotFoundException | UserNotFoundException ex) {
            throw new BadRequestParametersException(ex);
        }
    }

    @Override
    public Message deletePurchase(long id) {
        service.deletePurchase(id);
        return new Message(HttpStatus.OK, String.format("Purchase %d has been deleted", id));
    }

    @ExceptionHandler({PurchaseNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(produces = "application/json")
    private Message purchaseNotFound(PurchaseNotFoundException ex) {
        long id = ex.getPurchaseId();
        return new Message(HttpStatus.NOT_FOUND, String.format("Purchase %d can not be found", id));
    }

}
