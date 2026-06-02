package com.rey.template.service.impl;

import com.rey.template.dto.MenuDTO;
import com.rey.template.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Override
    public List<MenuDTO> getMenus(
            String username,
            String responsibility
    ) {

        if ("ADMIN".equals(responsibility)) {

            return List.of(
                    new MenuDTO(
                            "HOME",
                            "Home",
                            "/home.zul"
                    ),
                    new MenuDTO(
                            "REPORT",
                            "Report",
                            "/report.zul"
                    )
            );
        }

        return List.of(
                new MenuDTO(
                        "HOME",
                        "Home",
                        "/home.zul"
                )
        );
    }
}
