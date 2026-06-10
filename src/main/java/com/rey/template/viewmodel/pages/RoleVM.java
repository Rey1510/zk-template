package com.rey.template.viewmodel.pages;

import com.rey.template.dto.RoleDTO;
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
public class RoleVM extends AuthorizedVM {

    @WireVariable("roleService")
    private com.rey.template.service.RoleService roleService;

    @WireVariable("menuServiceImpl")
    private com.rey.template.service.MenuService menuService;

    // ---- Data ----
    private List<RoleDTO> roles = new ArrayList<>();
    private List<RoleDTO> filteredRoles = new ArrayList<>();
    private String searchKeyword = "";

    // ---- Form State ----
    private boolean showForm = false;
    private RoleDTO formRole = new RoleDTO();
    private List<MenuManagementDTO> availableMenus = new ArrayList<>();

    @Init
    public void init() {
        validatePage("/pages/role.zul");
        availableMenus = menuService.findAll().stream()
                .filter(MenuManagementDTO::getActive)
                .toList();
        loadRoles();
    }

    private void loadRoles() {
        roles = roleService.findAll();
        applyFilter();
    }

    @Command
    @NotifyChange({"filteredRoles", "pagedRoles", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void search() {
        applyFilter();
    }

    private void applyFilter() {
        activePage = 0;
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            filteredRoles = new ArrayList<>(roles);
        } else {
            String kw = searchKeyword.toLowerCase().trim();
            filteredRoles = roles.stream()
                    .filter(r -> (r.getRoleCode() != null && r.getRoleCode().toLowerCase().contains(kw)) ||
                                 (r.getRoleName() != null && r.getRoleName().toLowerCase().contains(kw)))
                    .toList();
        }
    }

    // ---- CRUD Actions ----

    @Command
    @NotifyChange({"showForm", "formRole"})
    public void openAdd() {
        formRole = new RoleDTO();
        showForm = true;
    }

    @Command
    @NotifyChange({"showForm", "formRole"})
    public void openEdit(@BindingParam("dto") RoleDTO dto) {
        formRole = new RoleDTO(
                dto.getRoleId(),
                dto.getRoleCode(),
                dto.getRoleName(),
                new ArrayList<>(dto.getMenuCodes())
        );
        showForm = true;
    }

    @Command
    @NotifyChange("showForm")
    public void cancelForm() {
        showForm = false;
    }

    @Command
    @NotifyChange({"roles", "filteredRoles", "pagedRoles", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay", "showForm"})
    public void saveRole() {
        roleService.save(formRole);
        loadRoles();
        showForm = false;
    }

    @Command
    @NotifyChange({"roles", "filteredRoles", "pagedRoles", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void deleteRole(@BindingParam("id") Long id) {
        roleService.delete(id);
        loadRoles();
    }

    @Command
    @NotifyChange("formRole")
    public void toggleMenu(@BindingParam("menu") String menuCode) {
        List<String> currentMenus = formRole.getMenuCodes();
        if (currentMenus.contains(menuCode)) {
            currentMenus.remove(menuCode);
        } else {
            currentMenus.add(menuCode);
        }
    }

    public boolean hasMenu(String menuCode) {
        return formRole != null && formRole.getMenuCodes().contains(menuCode);
    }

    // ---- Getters & Setters ----

    public List<RoleDTO> getFilteredRoles() { return filteredRoles; }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }

    public boolean isShowForm() { return showForm; }

    public RoleDTO getFormRole() { return formRole; }
    public void setFormRole(RoleDTO formRole) { this.formRole = formRole; }

    public List<MenuManagementDTO> getAvailableMenus() { return availableMenus; }

    // =========================================================
    // Custom Pagination
    // =========================================================
    private int activePage = 0;
    private final int pageSize = 5;

    public List<RoleDTO> getPagedRoles() {
        int start = activePage * pageSize;
        int end = Math.min(start + pageSize, filteredRoles.size());
        if (start > filteredRoles.size() || start < 0) {
            return new ArrayList<>();
        }
        return filteredRoles.subList(start, end);
    }

    public boolean isHasPrev() {
        return activePage > 0;
    }

    public boolean isHasNext() {
        return (activePage + 1) * pageSize < filteredRoles.size();
    }

    public String getPagingInfo() {
        if (filteredRoles.isEmpty()) {
            return "No items to display";
        }
        int start = activePage * pageSize + 1;
        int end = Math.min(start + pageSize - 1, filteredRoles.size());
        return "Showing " + start + " - " + end + " of " + filteredRoles.size();
    }

    public String getCurrentPageDisplay() {
        int totalPages = (int) Math.ceil((double) filteredRoles.size() / pageSize);
        if (totalPages == 0) totalPages = 1;
        return (activePage + 1) + " / " + totalPages;
    }

    @Command
    @NotifyChange({"pagedRoles", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void nextPage() {
        if (isHasNext()) {
            activePage++;
        }
    }

    @Command
    @NotifyChange({"pagedRoles", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void prevPage() {
        if (isHasPrev()) {
            activePage--;
        }
    }
}
