package com.epam.esm.service;

import com.epam.esm.model.User;
import com.epam.esm.model.dto.UserCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

//    List<User> getAllUsers(long limit, long offset);
//
//    List<User> getAllUsersV2(long limit, long previousId);

    Page<User> getUsers(Pageable pageable);

    User getUser(long id);

    User createUser(UserCreateDto dto);

}
