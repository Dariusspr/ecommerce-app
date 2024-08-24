package com.app.domain.member.services;

import com.app.domain.member.entities.Role;
import com.app.domain.member.exceptions.RoleAlreadyExistsException;
import com.app.domain.member.exceptions.RoleNotFoundException;
import com.app.domain.member.repositories.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class RoleService {

    private final RoleRepository roleRepository;

//    @PostConstruct
//    public void InitRoles_TEMPORARY() { // TODO
//        create(DEFAULT_MEMBER_ROLE);
//    }

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role create(Role.RoleTitle role) {
        try {
            return roleRepository.save(new Role(role));
        } catch (DataIntegrityViolationException e) {
            throw new RoleAlreadyExistsException();
        }
    }

    public Role findByTitle(Role.RoleTitle title) {
        return roleRepository.findByTitle(title).orElseThrow(RoleNotFoundException::new);
    }

    public void deleteByTitle(Role.RoleTitle title) {
        Role role = findByTitle(title);
        roleRepository.delete(role);
    }
}
