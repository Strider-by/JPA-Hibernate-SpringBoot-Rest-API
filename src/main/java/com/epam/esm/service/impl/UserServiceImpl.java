package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.UserNotFoundException;
import com.epam.esm.model.User;
import com.epam.esm.model.dto.UserCreateDto;
import com.epam.esm.model.util.DtoConverter;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repository;

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    // todo: create custom user repo and throw exception there?
    public User getUser(long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User createUser(UserCreateDto dto) {
        User user = DtoConverter.toUser(dto);
        return repository.save(user);
    }

}
