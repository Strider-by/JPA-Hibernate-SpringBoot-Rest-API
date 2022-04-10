package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao {

    private static final String GET_ALL_USERS =
            "SELECT * FROM user LIMIT ? OFFSET ?";

    private static final String GET_ALL_USERS_V2 =
            "SELECT * FROM user WHERE id > ? ORDER BY id ASC LIMIT ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers(long limit, long offset) {
        return jdbcTemplate.query(
                GET_ALL_USERS,
                new UserRowMapper(),
                limit,
                offset
        );
    }

    @Override
    public List<User> getAllUsersV2(long limit, long previousId) {
        return jdbcTemplate.query(
                GET_ALL_USERS_V2,
                new UserRowMapper(),
                previousId,
                limit
        );
    }

    private class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            return user;
        }

    }

}
