package com.epam.esm.service;

import com.epam.esm.model.User;

import java.util.List;

public interface UsersService {

    List<User> getAllUsers(long limit, long offset);

    List<User> getAllUsersV2(long limit, long previousId);

}
