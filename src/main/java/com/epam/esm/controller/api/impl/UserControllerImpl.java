package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.UserController;
import com.epam.esm.model.User;
import com.epam.esm.model.dto.UserCreateDto;
import com.epam.esm.model.representation.HateoasView;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.BiFunction;

import static com.epam.esm.controller.api.ControllerHelper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserControllerImpl extends ControllerExceptionHandlingBase implements UserController {

    @Autowired
    private UserService service;

    public UserControllerImpl(UserService service) {
        this.service = service;
    }

    public static final BiFunction<Integer, Integer, String> GET_USERS_HREF_GENERATOR =
            (pageNumber, pageSize) -> linkTo(methodOn(UserController.class).getUsers(pageNumber, pageSize)).toString();


    @Override
    public User createUser(/*UserCreateDto dto*/) {
        return service.createUser(new UserCreateDto());
    }

    @Override
    public HateoasView<User> getUsers(int pageNumber, int pageSize) {
        Pageable pageable = toPageable(pageNumber, pageSize);
        Page<User> page = service.getUsers(pageable);
        HateoasView<User> view = new HateoasView<>(page, GET_USERS_HREF_GENERATOR);
        return view;
    }

}
