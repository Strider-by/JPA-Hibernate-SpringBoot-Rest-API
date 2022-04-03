package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.UsersController;
import com.epam.esm.model.User;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UsersControllerImpl implements UsersController {

    @Autowired
    private UsersService service;

    @Override
    public HttpEntity<HateoasView<List<User>>> getAllUsers(long limit, int page) {
        long offset = calcOffset(limit, page);
        List<User> users = service.getAllUsers(limit, offset);
        HateoasView<List<User>> view = createGetAllUsersHateoasView(limit, page, users);
        return new HttpEntity<>(view);
    }

    @Override
    public HttpEntity<HateoasView<List<User>>> getAllUsersV2(long limit, long previousId) {
        List<User> users = service.getAllUsersV2(limit, previousId);
        HateoasView<List<User>> view = createGetAllUsersHateoasViewV2(limit, previousId, users);
        return new HttpEntity<>(view);
    }

    private HateoasView<List<User>> createGetAllUsersHateoasView(long limit, int page, List<User> users) {
        HateoasView<List<User>> view = new HateoasView<>(users);

        if (page != FIRST_PAGE_NUMBER) {
            view.add(linkTo(methodOn(UsersController.class).getAllUsers(limit, FIRST_PAGE_NUMBER)).withRel(FIRST));
            view.add(linkTo(methodOn(UsersController.class).getAllUsers(limit, page - 1)).withRel(PREVIOUS));
        }

        view.add(linkTo(methodOn(UsersController.class).getAllUsers(limit, page)).withSelfRel());
        view.add(linkTo(methodOn(UsersController.class).getAllUsers(limit, page + 1)).withRel(NEXT));
        return view;
    }

    private HateoasView<List<User>> createGetAllUsersHateoasViewV2(long limit, long lastId, List<User> users) {
        HateoasView<List<User>> view = new HateoasView<>(users);

        if (lastId != 0) {
            view.add(linkTo(methodOn(UsersController.class).getAllUsersV2(limit, 0)).withRel(FIRST));
        }

        view.add(linkTo(methodOn(UsersController.class).getAllUsersV2(limit, lastId)).withSelfRel());
        view.add(linkTo(methodOn(UsersController.class).getAllUsersV2(limit, lastId + limit)).withRel(NEXT));
        return view;
    }

}
