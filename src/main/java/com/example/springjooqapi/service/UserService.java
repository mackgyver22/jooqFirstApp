package com.example.springjooqapi.service;

import com.example.springjooqapi.model.UserPrincipal;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.springjooqapi.jooq.Tables.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userRecord = dsl.selectFrom(USERS)
                .where(USERS.USERNAME.eq(username))
                .fetchOne();

        if (userRecord == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Fetch user roles
        List<String> roles = dsl.select(ROLES.NAME)
                .from(ROLES)
                .join(USER_ROLES).on(ROLES.ID.eq(USER_ROLES.ROLE_ID))
                .where(USER_ROLES.USER_ID.eq(userRecord.getId()))
                .fetch(ROLES.NAME);

        // If no roles assigned, give default ROLE_USER
        if (roles.isEmpty()) {
            roles = List.of("ROLE_USER");
        }

        return new UserPrincipal(
                userRecord.getId(),
                userRecord.getUsername(),
                userRecord.getEmail(),
                userRecord.getPassword(),
                userRecord.getEnabled(),
                roles
        );
    }

    public UserPrincipal createUser(String username, String email, String password, 
                                   String firstName, String lastName) {
        // Check if username exists
        if (dsl.fetchExists(USERS, USERS.USERNAME.eq(username))) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email exists
        if (dsl.fetchExists(USERS, USERS.EMAIL.eq(email))) {
            throw new RuntimeException("Email already exists");
        }

        // Create user
        var userRecord = dsl.insertInto(USERS)
                .set(USERS.USERNAME, username)
                .set(USERS.EMAIL, email)
                .set(USERS.PASSWORD, passwordEncoder.encode(password))
                .set(USERS.FIRST_NAME, firstName)
                .set(USERS.LAST_NAME, lastName)
                .set(USERS.ENABLED, true)
                .returning()
                .fetchOne();

        // Assign default ROLE_USER
        var roleRecord = dsl.selectFrom(ROLES)
                .where(ROLES.NAME.eq("ROLE_USER"))
                .fetchOne();

        if (roleRecord != null) {
            dsl.insertInto(USER_ROLES)
                    .set(USER_ROLES.USER_ID, userRecord.getId())
                    .set(USER_ROLES.ROLE_ID, roleRecord.getId())
                    .execute();
        }

        return new UserPrincipal(
                userRecord.getId(),
                userRecord.getUsername(),
                userRecord.getEmail(),
                userRecord.getPassword(),
                userRecord.getEnabled(),
                List.of("ROLE_USER")
        );
    }

    public boolean existsByUsername(String username) {
        return dsl.fetchExists(USERS, USERS.USERNAME.eq(username));
    }

    public boolean existsByEmail(String email) {
        return dsl.fetchExists(USERS, USERS.EMAIL.eq(email));
    }
}
