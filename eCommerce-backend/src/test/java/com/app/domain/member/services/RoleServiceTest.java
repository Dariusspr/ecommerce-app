package com.app.domain.member.services;


import com.app.domain.member.entities.Role;
import com.app.domain.member.exceptions.RoleAlreadyExistsException;
import com.app.domain.member.exceptions.RoleNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    void create_ok() {
        Role.RoleTitle title = DEFAULT_MEMBER_ROLE;

        Role role = roleService.create(title);

        assertEquals(title, role.getTitle());
        assertNotNull(role.getId());
    }

    @Test
    void create_alreadyExists() {
        Role.RoleTitle title = DEFAULT_MEMBER_ROLE;

        roleService.create(title);

        assertThrows(RoleAlreadyExistsException.class, () -> roleService.create(title));
    }

    @Test
    void findByTitle_ok() {
        Role.RoleTitle title = DEFAULT_MEMBER_ROLE;
        roleService.create(title);

        Role role = roleService.findByTitle(title);

        assertEquals(title, role.getTitle());
    }

    @Test
    void findByTitle_roleNotFound() {
        Role.RoleTitle title = DEFAULT_MEMBER_ROLE;

        assertThrows(RoleNotFoundException.class, () -> roleService.findByTitle(title));
    }

    @Test
    void deleteByTitle_ok() {
        Role.RoleTitle title = DEFAULT_MEMBER_ROLE;
        roleService.create(title);
        assertDoesNotThrow(() -> roleService.findByTitle(title));

        roleService.deleteByTitle(title);

        assertThrows(RoleNotFoundException.class, () -> roleService.findByTitle(title));

    }

    @Test
    void deletedByTitle_roleNotFound() {
        assertThrows(RoleNotFoundException.class, () -> roleService.deleteByTitle(DEFAULT_MEMBER_ROLE));
    }
}
