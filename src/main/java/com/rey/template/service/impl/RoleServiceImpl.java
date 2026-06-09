package com.rey.template.service.impl;

import com.rey.template.dto.RoleDTO;
import com.rey.template.entity.userresp.master.MstMenu;
import com.rey.template.entity.userresp.master.MstRole;
import com.rey.template.entity.userresp.rel.RelRoleMenu;
import com.rey.template.repository.MstMenuRepository;
import com.rey.template.repository.MstRoleRepository;
import com.rey.template.repository.RelRoleMenuRepository;
import com.rey.template.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("roleService")
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private MstRoleRepository roleRepository;

    @Autowired
    private MstMenuRepository menuRepository;

    @Autowired
    private RelRoleMenuRepository roleMenuRepository;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    private void syncSequences() {
        try {
            entityManager.createNativeQuery("SELECT setval(pg_get_serial_sequence('zktmp.rel_role_menu', 'rel_role_menu_id'), COALESCE(MAX(rel_role_menu_id), 0) + 1, false) FROM zktmp.rel_role_menu").getSingleResult();
            entityManager.createNativeQuery("SELECT setval(pg_get_serial_sequence('zktmp.mst_role', 'role_id'), COALESCE(MAX(role_id), 0) + 1, false) FROM zktmp.mst_role").getSingleResult();
        } catch (Exception e) {
            // Safe fallback
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> findAll() {
        List<MstRole> roles = roleRepository.findAll();
        List<RoleDTO> dtoList = new ArrayList<>();
        for (MstRole r : roles) {
            List<RelRoleMenu> rels = roleMenuRepository.findByRoleRoleId(r.getRoleId());
            List<String> menuCodes = rels.stream()
                    .map(rm -> rm.getMenu().getMenuCode())
                    .collect(Collectors.toList());
            dtoList.add(new RoleDTO(
                    r.getRoleId(),
                    r.getRoleCode(),
                    r.getRoleName(),
                    menuCodes
            ));
        }
        return dtoList;
    }

    @Override
    public void save(RoleDTO dto) {
        syncSequences();
        MstRole role;
        if (dto.getRoleId() != null) {
            role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            role = new MstRole();
        }

        role.setRoleCode(dto.getRoleCode());
        role.setRoleName(dto.getRoleName());

        MstRole savedRole = roleRepository.saveAndFlush(role);

        if (savedRole.getRoleId() != null) {
            roleMenuRepository.deleteByRoleRoleId(savedRole.getRoleId());
            roleMenuRepository.flush();
        }

        if (dto.getMenuCodes() != null) {
            for (String menuCode : dto.getMenuCodes()) {
                Optional<MstMenu> menuOpt = menuRepository.findByMenuCode(menuCode);
                if (menuOpt.isPresent()) {
                    RelRoleMenu rel = new RelRoleMenu();
                    rel.setRole(savedRole);
                    rel.setMenu(menuOpt.get());
                    roleMenuRepository.save(rel);
                }
            }
            roleMenuRepository.flush();
        }
    }

    @Override
    public void delete(Long roleId) {
        roleMenuRepository.deleteByRoleRoleId(roleId);
        roleMenuRepository.flush();
        roleRepository.deleteById(roleId);
    }
}
