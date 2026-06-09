package com.rey.template.service;

import com.rey.template.dto.MenuDTO;
import com.rey.template.dto.MenuManagementDTO;

import java.util.List;

public interface MenuService {

    List<MenuDTO> getMenus(
            String username,
            String responsibility
    );

    List<MenuManagementDTO> findAll();

    List<MenuManagementDTO> findByRoleCode(String roleCode);

    void save(MenuManagementDTO dto);

    void delete(Long menuId);
}