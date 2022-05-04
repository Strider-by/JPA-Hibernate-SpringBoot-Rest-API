package com.epam.esm.service;

import com.epam.esm.model.User;
import com.epam.esm.model.dto.UserCreateDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

//    List<User> getAllUsers(long limit, long offset);
//
//    List<User> getAllUsersV2(long limit, long previousId);

    Page<User> getUsers(int pageNumber, int pageSize);

    User getUser(long id);

    User createUser(UserCreateDto dto);

}
