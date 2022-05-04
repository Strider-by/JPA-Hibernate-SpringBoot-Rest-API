package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.UserController;
import com.epam.esm.model.User;
import com.epam.esm.model.dto.UserCreateDto;
import com.epam.esm.model.representation.HateoasViewV2;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService service;

    public static final BiFunction<Integer, Integer, String> GET_USERS_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(UserController.class).getUsers(pageNumber, pageSize)).toString();

//    @Override
//    public HttpEntity<HateoasView<List<User>>> getAllUsers(long limit, int page) {
//        long offset = calcOffset(limit, page);
//        List<User> users = service.getAllUsers(limit, offset);
//        HateoasView<List<User>> view = createGetAllUsersHateoasView(limit, page, users);
//        return new HttpEntity<>(view);
//    }
//
//    @Override
//    public HttpEntity<HateoasView<List<User>>> getAllUsersV2(long limit, long previousId) {
//        List<User> users = service.getAllUsersV2(limit, previousId);
//        HateoasView<List<User>> view = createGetAllUsersHateoasViewV2(limit, previousId, users);
//        return new HttpEntity<>(view);
//    }

    @Override
    public User createUser(/*UserCreateDto dto*/) {
        return service.createUser(new UserCreateDto());
    }

    @Override
    // todo: make PageRequest ot interface, create on fly?
    public HateoasViewV2<User> getUsers(int pageNumber, int pageSize) {
        Page<User> page = service.getUsers(calcPageNumberForPageRequest(pageNumber), pageSize);
//        HateoasViewV2<User> view = createGetUsersHateoasView(page);
        HateoasViewV2<User> view = new HateoasViewV2<>(page);
        view.generateStandardLinks(GET_USERS_HREF_GENERATOR);
        return view;
    }

//    private HateoasViewV2<User> createGetUsersHateoasView(Page<User> page) {
//        HateoasViewV2 view = new HateoasViewV2(page);
//        int pageSize = page.getSize();
//        view.addLink(createGetUsersHref(pageNumber(page), pageSize), Relation.SELF)
//            .addLink(createGetUsersHref(firstPageNumber(), pageSize), Relation.FIRST)
//            .addLink(createGetUsersHref(previousPageNumber(page), pageSize), Relation.PREVIOUS)
//            .addLink(createGetUsersHref(nextPageNumber(page), pageSize), Relation.NEXT);
//
//        return view;
//    }

//    private String createGetUsersHref(int pageNumber, int pageSize) {
//        return linkTo(methodOn(UserController.class).getUsers(pageNumber, pageSize)).toString();
//    }

//    private void doSmth() {
//        Function<WebMvcLinkBuilder, IntFunction<Integer>> function = x -> y -> y;
//        IntFunction<IntFunction<String>> f2 = x -> y -> linkTo(methodOn(UserController.class).getUsers(x, y)).toString();
//        BiFunction<Integer, Integer, String> hrefGenerator =
//                (pageNumber, pageSize) -> linkTo(methodOn(UserController.class).getUsers(pageNumber, pageSize)).toString();
//    }

//    private HateoasView<List<User>> createGetAllUsersHateoasView(long limit, int page, List<User> users) {
//        HateoasView<List<User>> view = new HateoasView<>(users);
//
//        if (page != FIRST_PAGE_NUMBER) {
//            view.add(linkTo(methodOn(UserController.class).getAllUsers(limit, FIRST_PAGE_NUMBER)).withRel(FIRST));
//            view.add(linkTo(methodOn(UserController.class).getAllUsers(limit, page - 1)).withRel(PREVIOUS));
//        }
//
//        view.add(linkTo(methodOn(UserController.class).getAllUsers(limit, page)).withSelfRel());
//        view.add(linkTo(methodOn(UserController.class).getAllUsers(limit, page + 1)).withRel(NEXT));
//        return view;
//    }
//
//    private HateoasView<List<User>> createGetAllUsersHateoasViewV2(long limit, long lastId, List<User> users) {
//        HateoasView<List<User>> view = new HateoasView<>(users);
//
//        if (lastId != 0) {
//            view.add(linkTo(methodOn(UserController.class).getAllUsersV2(limit, 0)).withRel(FIRST));
//        }
//
//        view.add(linkTo(methodOn(UserController.class).getAllUsersV2(limit, lastId)).withSelfRel());
//        view.add(linkTo(methodOn(UserController.class).getAllUsersV2(limit, lastId + limit)).withRel(NEXT));
//        return view;
//    }

}
