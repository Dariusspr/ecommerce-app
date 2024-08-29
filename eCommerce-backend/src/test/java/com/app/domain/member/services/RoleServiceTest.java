package com.app.domain.member.services;


import com.app.domain.member.entities.Role;
import com.app.domain.member.exceptions.RoleAlreadyExistsException;
import com.app.domain.member.exceptions.RoleNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    void create_ok() {
        Role role = roleService.create(DEFAULT_MEMBER_ROLE);

        assertEquals(DEFAULT_MEMBER_ROLE, role.getTitle());
        assertNotNull(role.getId());
    }

    @Test
    void create_alreadyExists() {
        roleService.create(DEFAULT_MEMBER_ROLE);

        assertThrows(RoleAlreadyExistsException.class, () -> roleService.create(DEFAULT_MEMBER_ROLE));
    }

    @Test
    void findByTitle_ok() {
        roleService.create(DEFAULT_MEMBER_ROLE);

        Role role = roleService.findByTitle(DEFAULT_MEMBER_ROLE);

        assertEquals(DEFAULT_MEMBER_ROLE, role.getTitle());
    }

    @Test
    void deleteByTitle_ok() {
        roleService.create(DEFAULT_MEMBER_ROLE);
        assertDoesNotThrow(() -> roleService.findByTitle(DEFAULT_MEMBER_ROLE));

        roleService.deleteByTitle(DEFAULT_MEMBER_ROLE);

        assertThrows(RoleNotFoundException.class, () -> roleService.findByTitle(DEFAULT_MEMBER_ROLE));
    }

    @Test
    void deleteByTitle_roleNotFound() {
        assertThrows(RoleNotFoundException.class, () -> roleService.deleteByTitle(DEFAULT_MEMBER_ROLE));
    }

    @Test
    void findByTitle_notFound() {
        assertThrows(RoleNotFoundException.class, () -> roleService.findByTitle(DEFAULT_MEMBER_ROLE));
    }
}
