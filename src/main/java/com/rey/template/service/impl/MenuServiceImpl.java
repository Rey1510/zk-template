package com.rey.template.service.impl;

import com.rey.template.dto.MenuDTO;
import com.rey.template.dto.MenuManagementDTO;
import com.rey.template.entity.userresp.master.MstMenu;
import com.rey.template.repository.MstMenuRepository;
import com.rey.template.repository.RelRoleMenuRepository;
import com.rey.template.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("menuServiceImpl")
@Transactional
public class MenuServiceImpl implements MenuService {

    @Autowired
    private RelRoleMenuRepository roleMenuRepository;

    @Autowired
    private MstMenuRepository menuRepository;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    private void syncSequences() {
        try {
            entityManager.createNativeQuery("SELECT setval(pg_get_serial_sequence('zktmp.mst_menu', 'menu_id'), COALESCE(MAX(menu_id), 0) + 1, false) FROM zktmp.mst_menu").getSingleResult();
        } catch (Exception e) {
            // Safe fallback
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDTO> getMenus(
            String username,
            String responsibility
    ) {
        return roleMenuRepository.findMenusByRoleCode(responsibility)
                .stream()
                .map(m -> new MenuDTO(m.getMenuCode(), m.getMenuName(), m.getZulPath()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuManagementDTO> findAll() {
        List<MstMenu> menus = menuRepository.findAll();
        List<MenuManagementDTO> dtoList = new ArrayList<>();
        for (MstMenu m : menus) {
            dtoList.add(new MenuManagementDTO(
                    m.getMenuId(),
                    m.getMenuCode(),
                    m.getMenuName(),
                    m.getZulPath(),
                    m.getMenuOrder(),
                    m.getActive()
            ));
        }
        return dtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuManagementDTO> findByRoleCode(String roleCode) {
        List<MstMenu> menus = roleMenuRepository.findMenusByRoleCode(roleCode);
        List<MenuManagementDTO> dtoList = new ArrayList<>();
        for (MstMenu m : menus) {
            dtoList.add(new MenuManagementDTO(
                    m.getMenuId(),
                    m.getMenuCode(),
                    m.getMenuName(),
                    m.getZulPath(),
                    m.getMenuOrder(),
                    m.getActive()
            ));
        }
        return dtoList;
    }

    @Override
    public void save(MenuManagementDTO dto) {
        syncSequences();
        MstMenu menu;
        if (dto.getMenuId() != null) {
            menu = menuRepository.findById(dto.getMenuId())
                    .orElseThrow(() -> new RuntimeException("Menu not found"));
        } else {
            menu = new MstMenu();
        }

        menu.setMenuCode(dto.getMenuCode());
        menu.setMenuName(dto.getMenuName());
        menu.setZulPath(dto.getZulPath());
        menu.setMenuOrder(dto.getMenuOrder());
        menu.setActive(dto.getActive());

        menuRepository.saveAndFlush(menu);
    }

    @Override
    public void delete(Long menuId) {
        roleMenuRepository.deleteByMenuMenuId(menuId);
        roleMenuRepository.flush();
        menuRepository.deleteById(menuId);
    }
}
