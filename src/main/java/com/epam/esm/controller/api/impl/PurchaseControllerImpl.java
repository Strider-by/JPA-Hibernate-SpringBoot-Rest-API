package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.PurchaseController;
import com.epam.esm.controller.api.exception.BadRequestParametersException;
import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.controller.api.exception.UserNotFoundException;
import com.epam.esm.controller.util.Message;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.model.representation.HateoasViewV2;
import com.epam.esm.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static com.epam.esm.model.representation.HateoasViewV2.Relation;

@RestController
public class PurchaseControllerImpl implements PurchaseController {

    @Autowired
    private PurchaseService service;

    private static final BiFunction<Integer, Integer, String> GET_PURCHASES_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(PurchaseController.class)
                    .getAllPurchases(pageNumber, pageSize)).toString();

    private static final BiFunction<Integer, Integer, String> GET_PRIMARY_TAGS_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(PurchaseController.class)
                    ._getPrimaryTags(pageNumber, pageSize)).toString();

    @Override
    public Purchase getPurchaseById(long id) {
        return service.getPurchaseById(id);
    }

    @Override
    public HateoasViewV2<Purchase> getAllPurchases(int pageNumber, int pageSize) {
//        long offset = calcOffset(pageSize, pageNumber);
        Page<Purchase> page = service.getAllPurchases(calcPageNumberForPageRequest(pageNumber), pageSize);
//        HateoasView<List<Purchase>> view = createGetAllPurchasesHateoasView(purchases, pageSize, pageNumber);
//        HateoasViewV2<Purchase> view = createGetAllPurchasesHateoasView(page);
        HateoasViewV2 view = new HateoasViewV2(page, GET_PURCHASES_HREF_GENERATOR);
        return view;
    }

//    private HateoasViewV2<Purchase> createGetAllPurchasesHateoasView(Page<Purchase> page) {
//        HateoasViewV2 view = new HateoasViewV2(page);
//
//        if (!isFirstPage(page)) {
//            view.add(linkTo(methodOn(PurchaseController.class)
//                    .getAllPurchases(firstPageNumber(), page.getSize()))
//                    .withRel(FIRST));
//            view.add(linkTo(methodOn(PurchaseController.class).getAllPurchases(previousPageNumber(page), page.getSize()))
//                    .withRel(PREVIOUS));
//        }
//
//        view.add(linkTo(methodOn(PurchaseController.class).getAllPurchases(pageNumber(page), page.getSize()))
//                .withSelfRel());
//        view.add(linkTo(methodOn(PurchaseController.class).getAllPurchases(nextPageNumber(page), page.getSize()))
//                .withRel(NEXT));
//
//        return view;
//    }

//    private HateoasView<List<Purchase>> createGetAllPurchasesHateoasView(List<Purchase> purchases, int limit, int page) {
//        HateoasView<List<Purchase>> view = new HateoasView<>(purchases);
//
//        if (page != FIRST_PAGE_NUMBER) {
//            view.add(linkTo(methodOn(PurchaseController.class)
//                    .getAllPurchases(FIRST_PAGE_NUMBER, limit))
//                    .withRel(FIRST));
//            view.add(linkTo(methodOn(PurchaseController.class).getAllPurchases(page - 1, limit))
//                    .withRel(PREVIOUS));
//        }
//
//        view.add(linkTo(methodOn(PurchaseController.class).getAllPurchases(page, limit))
//                .withSelfRel());
//        view.add(linkTo(methodOn(PurchaseController.class).getAllPurchases(page + 1, limit))
//                .withRel(NEXT));
//
//        return view;
//    }

    @Override
    public HateoasViewV2<Purchase> getUserPurchases(long userId, int pageNumber, int pageSize) {
//        long offset = calcOffset(limit, page);
        Page<Purchase> page = service.getUserPurchases(userId, calcPageNumberForPageRequest(pageNumber), pageSize);
//        HateoasView<List<Purchase>> view = createGetUserPurchasesHateoasView(userPurchases, userId, limit, page);
        BiFunction<Integer, Integer, String> hrefGenerator = createGetUserPurchasesHrefGenerator(userId);
        HateoasViewV2<Purchase> view = new HateoasViewV2<>(page, hrefGenerator);
        return view;
    }

    private BiFunction<Integer, Integer, String> createGetUserPurchasesHrefGenerator(long userId) {
        return (pageNumber, pageSize) -> linkTo(methodOn(PurchaseController.class)
                    .getUserPurchases(userId, pageNumber, pageSize)).toString();
    }

//    private HateoasView<List<Purchase>> createGetUserPurchasesHateoasView(List<Purchase> userPurchases, long userId, long limit, int page) {
//        HateoasView<List<Purchase>> view = new HateoasView<>(userPurchases);
//
//        if (page != FIRST_PAGE_NUMBER) {
//            view.add(linkTo(methodOn(PurchasesController.class)
//                    .getUserPurchases(userId, limit, FIRST_PAGE_NUMBER))
//                    .withRel(FIRST));
//            view.add(linkTo(methodOn(PurchasesController.class).getUserPurchases(userId, limit, page - 1))
//                    .withRel(PREVIOUS));
//        }
//
//        view.add(linkTo(methodOn(PurchasesController.class).getUserPurchases(userId, limit, page))
//                .withSelfRel());
//        view.add(linkTo(methodOn(PurchasesController.class).getUserPurchases(userId, limit, page + 1))
//                .withRel(NEXT));
//
//        return view;
//    }

//    private HateoasViewV2<Purchase> createGetUserPurchasesHateoasView(Page<Purchase> page, long userId) {
//        HateoasViewV2<Purchase> view = new HateoasViewV2<>(page);
//
//        if (isFirstPage(page)) {
//            view.add(linkTo(methodOn(PurchaseController.class)
//                    .getUserPurchases(userId, page.getSize(), firstPageNumber()))
//                    .withRel(FIRST));
//            view.add(linkTo(methodOn(PurchaseController.class).getUserPurchases(userId, page.getSize(), nextPageNumber(page)))
//                    .withRel(PREVIOUS));
//        }
//
//        view.add(linkTo(methodOn(PurchaseController.class).getUserPurchases(userId, page.getSize(), pageNumber(page)))
//                .withSelfRel());
//
//        if (page.hasNext()) {
//            view.add(linkTo(methodOn(PurchaseController.class).getUserPurchases(userId, page.getSize(), nextPageNumber(page)))
//                    .withRel(NEXT));
//        }
//
//        return view;
//    }

    @Override
    public HateoasViewV2<Tag> getUserPrimaryTags(long userId, int pageNumber, int pageSize) {
        // todo: make sure I got rid out of using calculating page on this level
        Page<Tag> page = service.getUserPrimaryTags(userId, pageNumber, pageSize);
        BiFunction<Integer, Integer, String> hrefGenerator = createGetUserPrimaryTagsHrefGenerator(userId);
        HateoasViewV2<Tag> view = new HateoasViewV2<>(page, hrefGenerator);
        return view;
    }

    private BiFunction<Integer, Integer, String> createGetUserPrimaryTagsHrefGenerator(long userId) {
        return (pageNumber, pageSize) -> linkTo(methodOn(PurchaseController.class)
                .getUserPrimaryTags(userId, pageNumber, pageSize)).toString();
    }

//    private HateoasViewV2<Tag> createGetUserPrimaryTagsHateoasView(long userId, Page<Tag> page) {
//        HateoasViewV2<Tag> view = new HateoasViewV2<>(page);
//
//        view.addLink(linkTo(methodOn(
//                PurchaseController.class).getUserPrimaryTags(userId, page.getSize(), pageNumber(page))), Relation.SELF);
//        view.addLink(linkTo(methodOn(
//                PurchaseController.class).getUserPrimaryTags(userId, page.getSize(), firstPageNumber())), Relation.FIRST);
//        view.addLink(linkTo(methodOn(
//                PurchaseController.class).getUserPrimaryTags(userId, page.getSize(), previousPageNumber(page))), Relation.PREVIOUS);
//        view.addLink(linkTo(methodOn(
//                PurchaseController.class).getUserPrimaryTags(userId, page.getSize(), nextPageNumber(page))), Relation.NEXT);
//
//        return view;
//    }

//    private HateoasView<List<String>> createGetUserPrimaryTagsHateoasView(List<String> primaryTags, long userId, long limit, int page) {
//        HateoasView<List<String>> view = new HateoasView<>(primaryTags);
//
//        if (page != FIRST_PAGE_NUMBER) {
//            view.add(linkTo(methodOn(PurchasesController.class)
//                    .getUserPrimaryTags(userId, limit, FIRST_PAGE_NUMBER))
//                    .withRel(FIRST));
//            view.add(linkTo(methodOn(PurchasesController.class).getUserPrimaryTags(userId, limit, page - 1))
//                    .withRel(PREVIOUS));
//        }
//
//        view.add(linkTo(methodOn(PurchasesController.class).getUserPrimaryTags(userId, limit, page))
//                .withSelfRel());
//        view.add(linkTo(methodOn(PurchasesController.class).getUserPrimaryTags(userId, limit, page + 1))
//                .withRel(NEXT));
//
//        return view;
//    }

    @Override
    public HateoasViewV2<Tag> getPrimaryTags(int pageNumber, int pageSize) {
//        long offset = calcOffset(limit, page);
//        List<String> primaryTags = service.getPrimaryTags(limit, offset);
//        HateoasView<List<String>> view = createGetPrimaryTagsHateoasView(primaryTags, limit, page);
//        return new HttpEntity<>(view);
        Page<Tag> page = service.getPrimaryTags(pageNumber, pageSize);
        HateoasViewV2<Tag> view = new HateoasViewV2<>(page, GET_PRIMARY_TAGS_HREF_GENERATOR);
        return view;
    }

//    private HateoasView<List<String>> createGetPrimaryTagsHateoasView(List<String> primaryTags, long limit, int page) {
//        HateoasView<List<String>> view = new HateoasView<>(primaryTags);
//
//        if (page != FIRST_PAGE_NUMBER) {
//            view.add(linkTo(methodOn(PurchaseController.class)
//                    .getPrimaryTags(limit, FIRST_PAGE_NUMBER))
//                    .withRel(FIRST));
//            view.add(linkTo(methodOn(PurchaseController.class).getPrimaryTags(limit, page - 1))
//                    .withRel(PREVIOUS));
//        }
//
//        view.add(linkTo(methodOn(PurchaseController.class).getPrimaryTags(limit, page))
//                .withSelfRel());
//        view.add(linkTo(methodOn(PurchaseController.class).getPrimaryTags(limit, page + 1))
//                .withRel(NEXT));
//
//        return view;
//    }

    @Override
    public Purchase purchaseCertificate(long userId, long certificateId) {
        try {
            return service.purchaseCertificate(userId, certificateId);
        } catch (CertificateNotFoundException | UserNotFoundException ex) {
            throw new BadRequestParametersException(ex);
        }
    }

    @Override
    public Message deletePurchase(long id, HttpServletResponse response) throws IOException {
        boolean success = service.deletePurchase(id);
        if (!success) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return new Message(HttpStatus.OK, String.format("Purchase %d has been deleted", id));
    }

    // todo: add exception processor

}
