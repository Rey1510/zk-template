package com.rey.template.service;

import com.rey.template.dto.MenuDTO;

import java.util.List;

public interface MenuService {

    List<MenuDTO> getMenus(
            String username,
            String responsibility
    );

}