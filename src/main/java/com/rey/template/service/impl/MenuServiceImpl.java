package com.rey.template.service.impl;

import com.rey.template.dto.MenuDTO;
import com.rey.template.repository.RelRoleMenuRepository;
import com.rey.template.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private RelRoleMenuRepository roleMenuRepository;

    @Override
    public List<MenuDTO> getMenus(
            String username,
            String responsibility
    ) {
        return roleMenuRepository.findMenusByRoleCode(responsibility)
                .stream()
                .map(m -> new MenuDTO(m.getMenuCode(), m.getMenuName(), m.getZulPath()))
                .collect(Collectors.toList());
    }
}
