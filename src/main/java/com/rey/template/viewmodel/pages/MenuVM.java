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
    @NotifyChange({"filteredMenus", "pagedMenus", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void search() {
        applyFilter();
    }

    private void applyFilter() {
        activePage = 0;
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
    @NotifyChange({"menus", "filteredMenus", "pagedMenus", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay", "showForm"})
    public void saveMenu() {
        menuService.save(formMenu);
        loadMenus();
        showForm = false;
    }

    @Command
    @NotifyChange({"menus", "filteredMenus", "pagedMenus", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
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

    // =========================================================
    // Custom Pagination
    // =========================================================
    private int activePage = 0;
    private final int pageSize = 5;

    public List<MenuManagementDTO> getPagedMenus() {
        int start = activePage * pageSize;
        int end = Math.min(start + pageSize, filteredMenus.size());
        if (start > filteredMenus.size() || start < 0) {
            return new ArrayList<>();
        }
        return filteredMenus.subList(start, end);
    }

    public boolean isHasPrev() {
        return activePage > 0;
    }

    public boolean isHasNext() {
        return (activePage + 1) * pageSize < filteredMenus.size();
    }

    public String getPagingInfo() {
        if (filteredMenus.isEmpty()) {
            return "No items to display";
        }
        int start = activePage * pageSize + 1;
        int end = Math.min(start + pageSize - 1, filteredMenus.size());
        return "Showing " + start + " - " + end + " of " + filteredMenus.size();
    }

    public String getCurrentPageDisplay() {
        int totalPages = (int) Math.ceil((double) filteredMenus.size() / pageSize);
        if (totalPages == 0) totalPages = 1;
        return (activePage + 1) + " / " + totalPages;
    }

    @Command
    @NotifyChange({"pagedMenus", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void nextPage() {
        if (isHasNext()) {
            activePage++;
        }
    }

    @Command
    @NotifyChange({"pagedMenus", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void prevPage() {
        if (isHasPrev()) {
            activePage--;
        }
    }
}
