package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.UserNotFoundException;
import com.epam.esm.model.User;
import com.epam.esm.model.dto.UserCreateDto;
import com.epam.esm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.epam.esm.service.impl.UserServiceImplTest.Data.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    UserRepository repository;
    @InjectMocks
    UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers() {
        when(repository.findAll(pageable)).thenReturn(userPage);
        Page<User> actual = service.getUsers(pageable);
        assertTrue(userPage == actual);
    }

    @Test
    void getUser_success() {
        when(repository.getById(userId)).thenReturn(user);
        User actual = service.getUser(userId);
        assertTrue(user == actual);
    }

    @Test
    void getUser_fail_userNotFound() {
        when(repository.getById(userId)).thenThrow(new UserNotFoundException(userId));
        assertThrows(UserNotFoundException.class, () -> service.getUser(userId));
    }

    @Test
    void createUser() {
        when(repository.save(any())).thenReturn(createdUser);
        User actual = service.createUser(createDto);
        assertTrue(createdUser == actual);
    }

    static class Data {

        static final long userId = 1L;
        static final User user = new User();
        static final UserCreateDto createDto = new UserCreateDto();
        static final User createdUser = new User();

        static final int pageNumber = 0;
        static final int pageSize = 10;
        static final Pageable pageable = PageRequest.of(pageNumber, pageSize);
        static final long elementsTotal = 25;

        static final List<User> users = Collections.unmodifiableList(
                IntStream.range(0, pageSize).mapToObj(i -> new User()).collect(Collectors.toList()));
        static final Page<User> userPage = new PageImpl<>(users, pageable, elementsTotal);

    }

}