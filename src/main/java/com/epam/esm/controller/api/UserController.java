package com.epam.esm.controller.api;

import com.epam.esm.model.User;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.epam.esm.controller.api.ControllerHelper.*;

@RequestMapping("/users")
public interface UserController {

    @PostMapping(produces = "application/json")
    // todo: ask how to workaround empty post body case
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(/*@RequestBody UserCreateDto dto*/);

    @GetMapping(produces = "application/json")
    HateoasView<User> getUsers(
            @RequestParam(name = DEFAULT_PAGE_NUMBER_PARAM_NAME, defaultValue = FIRST_PAGE_NUMBER_AS_STRING)
            int pageNumber,
            @RequestParam(name = DEFAULT_PAGE_SIZE_PARAM_NAME, defaultValue = DEFAULT_PAGE_SIZE_AS_STRING)
            int pageSize);

}
