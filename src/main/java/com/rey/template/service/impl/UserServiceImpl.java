package com.rey.template.service.impl;

import com.rey.template.dto.UserDTO;
import com.rey.template.entity.userresp.master.MstRole;
import com.rey.template.entity.userresp.master.MstUser;
import com.rey.template.entity.userresp.rel.RelUserRole;
import com.rey.template.repository.MstRoleRepository;
import com.rey.template.repository.MstUserRepository;
import com.rey.template.repository.RelUserRoleRepository;
import com.rey.template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private MstUserRepository userRepository;

    @Autowired
    private MstRoleRepository roleRepository;

    @Autowired
    private RelUserRoleRepository userRoleRepository;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    private void syncSequences() {
        try {
            entityManager.createNativeQuery("SELECT setval(pg_get_serial_sequence('zktmp.rel_user_role', 'rel_user_role_id'), COALESCE(MAX(rel_user_role_id), 0) + 1, false) FROM zktmp.rel_user_role").getSingleResult();
            entityManager.createNativeQuery("SELECT setval(pg_get_serial_sequence('zktmp.mst_user', 'user_id'), COALESCE(MAX(user_id), 0) + 1, false) FROM zktmp.mst_user").getSingleResult();
        } catch (Exception e) {
            // Safe fallback
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        List<MstUser> entityList = userRepository.findAll();
        List<UserDTO> dtoList = new ArrayList<>();

        for (MstUser user : entityList) {
            List<RelUserRole> userRoles = userRoleRepository.findByUserUserId(user.getUserId());
            List<String> roleCodes = userRoles.stream()
                    .map(ur -> ur.getRole().getRoleCode())
                    .toList();

            dtoList.add(new UserDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getActive(),
                    new ArrayList<>(roleCodes)
            ));
        }
        return dtoList;
    }

    @Override
    public void save(UserDTO dto) {
        syncSequences();
        MstUser user;
        if (dto.getId() != null) {
            user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = new MstUser();
        }

        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setActive(dto.getActive());

        MstUser savedUser = userRepository.saveAndFlush(user);

        // Reset and re-add roles by user ID
        if (savedUser.getUserId() != null) {
            userRoleRepository.deleteByUserUserId(savedUser.getUserId());
            userRoleRepository.flush();
        }

        if (dto.getRoleCodes() != null) {
            for (String roleCode : dto.getRoleCodes()) {
                Optional<MstRole> roleOpt = roleRepository.findByRoleCode(roleCode);
                if (roleOpt.isPresent()) {
                    RelUserRole rel = new RelUserRole();
                    rel.setUser(savedUser);
                    rel.setRole(roleOpt.get());
                    userRoleRepository.save(rel);
                }
            }
            userRoleRepository.flush();
        }
    }

    @Override
    public void delete(Long id) {
        userRoleRepository.deleteByUserUserId(id);
        userRoleRepository.flush();
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllRoleCodes() {
        return roleRepository.findAll().stream()
                .map(MstRole::getRoleCode)
                .toList();
    }
}
