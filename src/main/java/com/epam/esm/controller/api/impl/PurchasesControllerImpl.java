package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.PurchasesController;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.PurchasesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class PurchasesControllerImpl implements PurchasesController {

    @Autowired
    private PurchasesService service;

    @Override
    public Purchase getPurchaseById(long id) {
        return service.getPurchaseById(id);
    }

    @Override
    public HttpEntity<HateoasView<List<Purchase>>> getAllPurchases(long limit, int page) {
        long offset = calcOffset(limit, page);
        List<Purchase> purchases = service.getAllPurchases(limit, offset);
        HateoasView<List<Purchase>> view = createGetAllPurchasesHateoasView(purchases, limit, page);
        return new HttpEntity<>(view);
    }

    private HateoasView<List<Purchase>> createGetAllPurchasesHateoasView(List<Purchase> purchases, long limit, int page) {
        HateoasView<List<Purchase>> view = new HateoasView<>(purchases);

        if (page != FIRST_PAGE_NUMBER) {
            view.add(linkTo(methodOn(PurchasesController.class)
                    .getAllPurchases(limit, FIRST_PAGE_NUMBER))
                    .withRel(FIRST));
            view.add(linkTo(methodOn(PurchasesController.class).getAllPurchases(limit, page - 1))
                    .withRel(PREVIOUS));
        }

        view.add(linkTo(methodOn(PurchasesController.class).getAllPurchases(limit, page))
                .withSelfRel());
        view.add(linkTo(methodOn(PurchasesController.class).getAllPurchases(limit, page + 1))
                .withRel(NEXT));

        return view;
    }

    @Override
    public HttpEntity<HateoasView<List<Purchase>>> getUserPurchases(long userId, long limit, int page) {
        long offset = calcOffset(limit, page);
        List<Purchase> userPurchases = service.getUserPurchases(userId, limit, offset);
        HateoasView<List<Purchase>> view = createGetUserPurchasesHateoasView(userPurchases, userId, limit, page);
        return new HttpEntity<>(view);
    }

    private HateoasView<List<Purchase>> createGetUserPurchasesHateoasView(List<Purchase> userPurchases, long userId, long limit, int page) {
        HateoasView<List<Purchase>> view = new HateoasView<>(userPurchases);

        if (page != FIRST_PAGE_NUMBER) {
            view.add(linkTo(methodOn(PurchasesController.class)
                    .getUserPurchases(userId, limit, FIRST_PAGE_NUMBER))
                    .withRel(FIRST));
            view.add(linkTo(methodOn(PurchasesController.class).getUserPurchases(userId, limit, page - 1))
                    .withRel(PREVIOUS));
        }

        view.add(linkTo(methodOn(PurchasesController.class).getUserPurchases(userId, limit, page))
                .withSelfRel());
        view.add(linkTo(methodOn(PurchasesController.class).getUserPurchases(userId, limit, page + 1))
                .withRel(NEXT));

        return view;
    }

    @Override
    public HttpEntity<HateoasView<List<String>>> getUserPrimaryTags(long userId, long limit, int page) {
        List<String> primaryTags = service.getUserPrimaryTags(userId);
        HateoasView<List<String>> view = createGetUserPrimaryTagsHateoasView(primaryTags, userId, limit, page);
        return new HttpEntity<>(view);
    }

    private HateoasView<List<String>> createGetUserPrimaryTagsHateoasView(List<String> primaryTags, long userId, long limit, int page) {
        HateoasView<List<String>> view = new HateoasView<>(primaryTags);

        if (page != FIRST_PAGE_NUMBER) {
            view.add(linkTo(methodOn(PurchasesController.class)
                    .getUserPrimaryTags(userId, limit, FIRST_PAGE_NUMBER))
                    .withRel(FIRST));
            view.add(linkTo(methodOn(PurchasesController.class).getUserPrimaryTags(userId, limit, page - 1))
                    .withRel(PREVIOUS));
        }

        view.add(linkTo(methodOn(PurchasesController.class).getUserPrimaryTags(userId, limit, page))
                .withSelfRel());
        view.add(linkTo(methodOn(PurchasesController.class).getUserPrimaryTags(userId, limit, page + 1))
                .withRel(NEXT));

        return view;
    }

    @Override
    public HttpEntity<HateoasView<List<String>>> getPrimaryTags(long limit, int page) {
        long offset = calcOffset(limit, page);
        List<String> primaryTags = service.getPrimaryTags(limit, offset);
        HateoasView<List<String>> view = createGetPrimaryTagsHateoasView(primaryTags, limit, page);
        return new HttpEntity<>(view);
    }

    private HateoasView<List<String>> createGetPrimaryTagsHateoasView(List<String> primaryTags, long limit, int page) {
        HateoasView<List<String>> view = new HateoasView<>(primaryTags);

        if (page != FIRST_PAGE_NUMBER) {
            view.add(linkTo(methodOn(PurchasesController.class)
                    .getPrimaryTags(limit, FIRST_PAGE_NUMBER))
                    .withRel(FIRST));
            view.add(linkTo(methodOn(PurchasesController.class).getPrimaryTags(limit, page - 1))
                    .withRel(PREVIOUS));
        }

        view.add(linkTo(methodOn(PurchasesController.class).getPrimaryTags(limit, page))
                .withSelfRel());
        view.add(linkTo(methodOn(PurchasesController.class).getPrimaryTags(limit, page + 1))
                .withRel(NEXT));

        return view;
    }

    @Override
    public Purchase purchaseCertificate(long userId, long certificateId) {
        return service.purchaseCertificate(userId, certificateId);
    }

    @Override
    public Message deletePurchase(long id, HttpServletResponse response) throws IOException {
        boolean success = service.deletePurchase(id);
        if (!success) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return new Message(HttpStatus.OK, String.format("Purchase %d has been deleted", id));
    }

}
