package com.app.domain.member.services;


import com.app.domain.member.entities.Role;
import com.app.domain.member.exceptions.RoleAlreadyExistsException;
import com.app.domain.member.exceptions.RoleNotFoundException;
import com.app.domain.member.repositories.RoleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setup() {
        role = roleService.create(DEFAULT_MEMBER_ROLE);
    }

    @AfterEach
    void clear() {
        roleRepository.deleteAll();
    }

    @Test
    void create_ok() {

        assertEquals(DEFAULT_MEMBER_ROLE, role.getTitle());
        assertNotNull(role.getId());
    }

    @Test
    void create_alreadyExists() {

        assertThrows(RoleAlreadyExistsException.class, () -> roleService.create(DEFAULT_MEMBER_ROLE));
    }

    @Test
    void findByTitle_ok() {

        Role role = roleService.findByTitle(DEFAULT_MEMBER_ROLE);

        assertEquals(DEFAULT_MEMBER_ROLE, role.getTitle());
    }

    @Test
    void deleteByTitle_ok() {
        assertDoesNotThrow(() -> roleService.findByTitle(DEFAULT_MEMBER_ROLE));

        roleService.deleteByTitle(DEFAULT_MEMBER_ROLE);

        assertThrows(RoleNotFoundException.class, () -> roleService.findByTitle(DEFAULT_MEMBER_ROLE));
    }

    @Test
    void deleteByTitle_roleNotFound() {
        roleRepository.deleteAll();

        assertThrows(RoleNotFoundException.class, () -> roleService.deleteByTitle(DEFAULT_MEMBER_ROLE));
    }

    @Test
    void findByTitle_notFound() {
        roleRepository.deleteAll();

        assertThrows(RoleNotFoundException.class, () -> roleService.findByTitle(DEFAULT_MEMBER_ROLE));
    }
}
