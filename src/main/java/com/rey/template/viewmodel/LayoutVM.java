package com.rey.template.viewmodel;

import com.rey.template.dto.MenuDTO;
import com.rey.template.security.CurrentUserService;
import com.rey.template.service.MenuService;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.util.List;

@VariableResolver(DelegatingVariableResolver.class)
public class LayoutVM {

    @WireVariable
    private CurrentUserService currentUserService;

    @WireVariable("menuServiceImpl")
    private MenuService menuService;

    private List<MenuDTO> menus;

    private String fullName;

    private String responsibility;

    @Init
    public void init() {

        fullName = currentUserService
                .getUserSession()
                .getFullName();

        responsibility = currentUserService
                .getCurrentResponsibility()
                .getName();

        menus = menuService.getMenus(
                currentUserService.getCurrentUsername(),
                currentUserService
                        .getCurrentResponsibility()
                        .getCode()
        );
    }

    public List<MenuDTO> getMenus() {
        return menus;
    }

    public String getFullName() {
        return fullName;
    }

    public String getResponsibility() {
        return responsibility;
    }
}
