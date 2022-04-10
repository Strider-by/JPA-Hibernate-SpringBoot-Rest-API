package com.epam.esm.dao;

import com.epam.esm.model.User;

import java.util.List;

public interface UserDao {

    List<User> getAllUsers(long limit, long offset);

    List<User> getAllUsersV2(long limit, long previousId);

}
