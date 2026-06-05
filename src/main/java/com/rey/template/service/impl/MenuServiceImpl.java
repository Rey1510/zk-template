package com.rey.template.service.impl;

import com.rey.template.dto.MenuDTO;
import com.rey.template.service.MenuService;
import com.rey.template.util.UrlConstant;
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
                            UrlConstant.URL_HOME_ZUL
                    ),
                    new MenuDTO(
                            "REPORT",
                            "Report",
                            UrlConstant.URL_REPORT_ZUL
                    )
            );
        }

        return List.of(
                new MenuDTO(
                        "HOME",
                        "Home",
                        UrlConstant.URL_HOME_ZUL
                )
        );
    }
}
