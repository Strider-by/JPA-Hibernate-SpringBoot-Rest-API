package com.epam.esm.controller.api;

import com.epam.esm.model.User;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.esm.controller.api.ControllerHelper.DEFAULT_PREVIOUS_ID;
import static com.epam.esm.controller.api.ControllerHelper.FIRST_PAGE_NUMBER_AS_STRING;

@RequestMapping("/users")
public interface UsersController {

    String DEFAULT_LIMIT = "500";

    @RequestMapping(value = "/using_offset", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    HttpEntity<HateoasView<List<User>>> getAllUsers(
            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
            @RequestParam(defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int page
    );

    @RequestMapping(value = "/using_gte", method = RequestMethod.GET, produces = "application/json")
    HttpEntity<HateoasView<List<User>>> getAllUsersV2(
            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
            @RequestParam(defaultValue = DEFAULT_PREVIOUS_ID) long previousId
    );
//
//    @GetMapping(value = "/id", produces = "application/json")
//    @ResponseBody
//    HttpEntity<HateoasView<List<Purchase>>> getUserPurchases(
//            @PathVariable long id,
//            @RequestParam(defaultValue = DEFAULT_LIMIT) long limit,
//            @RequestParam(defaultValue = FIRST_PAGE_NUMBER_AS_STRING) int page);


}
