package com.epam.esm.controller.api.impl;

import com.epam.esm.model.User;
import com.epam.esm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static com.epam.esm.controller.api.impl.UserControllerImplTest.Data.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UserControllerImplTest {

    UserService service;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(UserService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserControllerImpl(service))
                .build();
    }


    @Test
    void createUser() throws Exception {
        when(service.createUser(any())).thenReturn(singleUser);
        mockMvc.perform(post("/users"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    void getUsers() throws Exception {
        when(service.getUsers(any())).thenReturn(userPage);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(pageSize));
    }

    static class Data {
        static final long userId = 1L;
        static final User singleUser = new User();
        static final int pageNumber = 0;
        static final int pageSize = 10;
        static final Pageable pageable = PageRequest.of(pageNumber, pageSize);
        static final long elementsTotal = 25;
        static final List<User> users = Collections.unmodifiableList(
                IntStream.range(0, pageSize).mapToObj(i -> new User()).collect(Collectors.toList()));
        static final Page<User> userPage = new PageImpl(users, pageable, elementsTotal);

        static {
            singleUser.setId(userId);
        }

    }

}