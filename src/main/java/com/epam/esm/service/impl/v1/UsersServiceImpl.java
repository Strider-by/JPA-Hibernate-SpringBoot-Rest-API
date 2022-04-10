package com.epam.esm.service.impl.v1;

import com.epam.esm.dao.UserDao;
import com.epam.esm.model.User;
import com.epam.esm.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersServiceImpl implements UsersService {

    @Autowired
    UserDao dao;


    @Override
    public List<User> getAllUsers(long limit, long offset) {
        return dao.getAllUsers(limit, offset);
    }

    @Override
    public List<User> getAllUsersV2(long limit, long previousId) {
        return dao.getAllUsersV2(limit, previousId);
    }

}
