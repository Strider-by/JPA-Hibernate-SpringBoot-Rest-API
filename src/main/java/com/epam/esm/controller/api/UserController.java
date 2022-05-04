package com.epam.esm.controller.api;

import com.epam.esm.model.User;
import com.epam.esm.model.dto.UserCreateDto;
import com.epam.esm.model.representation.HateoasViewV2;
import org.springframework.web.bind.annotation.*;

import static com.epam.esm.controller.api.ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING;

@RequestMapping("/users")
public interface UserController {

    String DEFAULT_PAGE_SIZE = "500";

//    @RequestMapping(value = "/using_offset", method = RequestMethod.GET, produces = "application/json")
//    @ResponseBody
//    HttpEntity<HateoasView<List<User>>> getAllUsers(
//            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
//            @RequestParam(defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int page
//    );
//
//    @RequestMapping(value = "/using_gte", method = RequestMethod.GET, produces = "application/json")
//    HttpEntity<HateoasView<List<User>>> getAllUsersV2(
//            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
//            @RequestParam(defaultValue = DEFAULT_PREVIOUS_ID) long previousId
//    );

    @PostMapping(produces = "application/json")
    // todo: ask how to workaround empty post body case
    User createUser(/*@RequestBody UserCreateDto dto*/);

    @GetMapping(produces = "application/json")
    HateoasViewV2<User> getUsers(
            @RequestParam(name = "page", defaultValue = FIRST_PAGE_NUMBER_AS_STRING)
            int pageNumber,
            @RequestParam(name = "limit", defaultValue = DEFAULT_PAGE_SIZE)
            int pageSize);
//
//    @GetMapping(value = "/id", produces = "application/json")
//    @ResponseBody
//    HttpEntity<HateoasView<List<Purchase>>> getUserPurchases(
//            @PathVariable long id,
//            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
//            @RequestParam(defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int page);


}
