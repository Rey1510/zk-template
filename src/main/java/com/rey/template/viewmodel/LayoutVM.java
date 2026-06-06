package com.rey.template.viewmodel;

import com.rey.template.dto.MenuDTO;
import com.rey.template.dto.ResponsibilityDTO;
import com.rey.template.security.CurrentUserService;
import com.rey.template.service.MenuService;
import com.rey.template.util.UrlConstant;
import com.rey.template.viewmodel.common.BaseVM;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.util.List;

@Getter
@Setter
@VariableResolver(DelegatingVariableResolver.class)
public class LayoutVM extends BaseVM {

    @WireVariable
    private CurrentUserService currentUserService;

    @WireVariable("menuServiceImpl")
    private MenuService menuService;

    private List<MenuDTO> menus;

    private String fullName;

    private String responsibility;

    private List<ResponsibilityDTO> responsibilities;

    private ResponsibilityDTO selectedResponsibility;

    private String currentPage = UrlConstant.URL_WELCOME_ZUL;

    @Init
    public void init() {

        validateLogin();

        fullName = currentUserService
                .getUserSession()
                .getFullName();

        responsibilities = currentUserService.getResponsibilities();

        selectedResponsibility =
                currentUserService.getCurrentResponsibility();

        menus = menuService.getMenus(
                currentUserService.getCurrentUsername(),
                currentUserService
                        .getCurrentResponsibility()
                        .getCode()
        );

        loadMenu();
    }

    @Command
    @NotifyChange({
            "menus",
            "responsibility",
            "selectedResponsibility"
    })
    public void changeResponsibility() {

        currentUserService.changeResponsibility(
                selectedResponsibility.getCode()
        );

        loadMenu();
    }

    private void loadMenu() {
        responsibility = currentUserService.getCurrentResponsibility().getName();
        menus = menuService.getMenus(currentUserService.getCurrentUsername(),
                currentUserService.getCurrentResponsibility().getCode());
    }

    @Command
    @NotifyChange("currentPage")
    public void openMenu(
            @BindingParam("menu") MenuDTO menu
    ) {
        currentPage = menu.getUrl();
    }

    @Command
    public void logout() {

        currentUserService.logout();

        Executions.sendRedirect(
                UrlConstant.URL_LOGIN_ZUL
        );
    }
}
