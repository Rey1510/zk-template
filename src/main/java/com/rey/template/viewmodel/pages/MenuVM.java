package com.rey.template.viewmodel.pages;

import com.rey.template.dto.MenuManagementDTO;
import com.rey.template.viewmodel.common.AuthorizedVM;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.util.ArrayList;
import java.util.List;

@VariableResolver(DelegatingVariableResolver.class)
public class MenuVM extends AuthorizedVM {

    @WireVariable("menuServiceImpl")
    private com.rey.template.service.MenuService menuService;

    // ---- Data ----
    private List<MenuManagementDTO> menus = new ArrayList<>();
    private List<MenuManagementDTO> filteredMenus = new ArrayList<>();
    private String searchKeyword = "";

    // ---- Form State ----
    private boolean showForm = false;
    private MenuManagementDTO formMenu = new MenuManagementDTO();

    @Init
    public void init() {
        validatePage("/pages/menu.zul");
        loadMenus();
    }

    private void loadMenus() {
        menus = menuService.findAll();
        applyFilter();
    }

    @Command
    @NotifyChange("filteredMenus")
    public void search() {
        applyFilter();
    }

    private void applyFilter() {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            filteredMenus = new ArrayList<>(menus);
        } else {
            String kw = searchKeyword.toLowerCase().trim();
            filteredMenus = menus.stream()
                    .filter(m -> (m.getMenuCode() != null && m.getMenuCode().toLowerCase().contains(kw)) ||
                                 (m.getMenuName() != null && m.getMenuName().toLowerCase().contains(kw)) ||
                                 (m.getZulPath() != null && m.getZulPath().toLowerCase().contains(kw)))
                    .toList();
        }
    }

    // ---- CRUD Actions ----

    @Command
    @NotifyChange({"showForm", "formMenu"})
    public void openAdd() {
        formMenu = new MenuManagementDTO();
        formMenu.setMenuOrder(1);
        formMenu.setActive(true);
        showForm = true;
    }

    @Command
    @NotifyChange({"showForm", "formMenu"})
    public void openEdit(@BindingParam("dto") MenuManagementDTO dto) {
        formMenu = new MenuManagementDTO(
                dto.getMenuId(),
                dto.getMenuCode(),
                dto.getMenuName(),
                dto.getZulPath(),
                dto.getMenuOrder(),
                dto.getActive()
        );
        showForm = true;
    }

    @Command
    @NotifyChange("showForm")
    public void cancelForm() {
        showForm = false;
    }

    @Command
    @NotifyChange({"menus", "filteredMenus", "showForm"})
    public void saveMenu() {
        menuService.save(formMenu);
        loadMenus();
        showForm = false;
    }

    @Command
    @NotifyChange({"menus", "filteredMenus"})
    public void deleteMenu(@BindingParam("id") Long id) {
        menuService.delete(id);
        loadMenus();
    }

    // ---- Getters & Setters ----

    public List<MenuManagementDTO> getFilteredMenus() { return filteredMenus; }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }

    public boolean isShowForm() { return showForm; }

    public MenuManagementDTO getFormMenu() { return formMenu; }
    public void setFormMenu(MenuManagementDTO formMenu) { this.formMenu = formMenu; }
}
