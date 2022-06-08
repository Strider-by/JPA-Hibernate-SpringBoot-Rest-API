package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.UserNotFoundException;
import com.epam.esm.model.User;
import com.epam.esm.model.dto.UserCreateDto;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static com.epam.esm.service.impl.UserServiceImpl_UsingInnerDB_Test.Data.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceImpl_UsingInnerDB_Test {

    @Autowired
    UserService service;
    @Autowired
    UserRepository repository;

    @BeforeEach
    void setUp() {
        Data.init();
        setInitialUsers();
    }

    @AfterEach
    void cleanUp() {
        clearUserRepository();
    }

    @Test
    void createUser() {
        User user = service.createUser(userCreateDto);
        assertNotNull(user.getId());
        assertEquals(repository.count(), initialUsersQuantity + 1);
    }

    @Test
    void getUser_success() {
        long id = getUserIdThatIsPresentInDB();
        User user = service.getUser(id);
        assertEquals(user.getId(), id);
    }

    @Test
    void getUser_fail_userNotFound() {
        long id = getUserIdThatIsNotPresentInDB();
        assertThrows(UserNotFoundException.class, () -> service.getUser(id));
    }

    @Test
    void getUsers() {
        Page<User> userPage = service.getUsers(pageable);
        assertEquals(userPage.getContent().size(), initialUsersQuantity);
        assertEquals(userPage.getTotalElements(), initialUsersQuantity);
    }

    long getUserIdThatIsPresentInDB() {
        return repository.findAll().stream().map(User::getId).findAny().get();
    }

    long getUserIdThatIsNotPresentInDB() {
        return -1;
    }

    void setInitialUsers() {
        for (User user : initialUsers) {
            User savedUser = repository.save(user);
            assertNotNull(savedUser.getId());
        }

        assertEquals(initialUsersQuantity, repository.count());
    }

    void clearUserRepository() {
        repository.deleteAll();
    }

    static class Data {
        static Pageable pageable;
        static User[] initialUsers;
        static int initialUsersQuantity;
        static UserCreateDto userCreateDto;

        static void init() {
            pageable = PageRequest.of(0, 10);
            initialUsers = new User[] {new User(), new User()};
            initialUsersQuantity = initialUsers.length;
            userCreateDto = new UserCreateDto();
        }
    }

}