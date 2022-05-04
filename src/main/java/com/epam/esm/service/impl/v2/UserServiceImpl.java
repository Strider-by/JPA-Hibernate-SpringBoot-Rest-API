package com.epam.esm.service.impl.v2;

import com.epam.esm.dao.UserDao;
import com.epam.esm.model.User;
import com.epam.esm.model.dto.UserCreateDto;
import com.epam.esm.model.util.DtoConverter;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

//    @Autowired
//    UserDao dao;

    @Autowired
    UserRepository repository;

    @Override
    public Page<User> getUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return repository.findAll(pageable);
    }

    @Override
    public User getUser(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public User createUser(UserCreateDto dto) {
        User user = DtoConverter.toUser(dto);
        return repository.save(user);
    }


//    @Override
//    public List<User> getAllUsers(long limit, long offset) {
//        return dao.getAllUsers(limit, offset);
//    }

//    @Override
//    public List<User> getAllUsersV2(long limit, long previousId) {
//        return dao.getAllUsersV2(limit, previousId);
//    }

}
