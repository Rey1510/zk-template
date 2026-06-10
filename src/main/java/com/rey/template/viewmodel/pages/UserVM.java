package com.rey.template.viewmodel.pages;

import com.rey.template.dto.UserDTO;
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
public class UserVM extends AuthorizedVM {

    // ---- Service ----
    @WireVariable
    private com.rey.template.service.UserService userService;

    // ---- Data ----
    private List<UserDTO> users = new ArrayList<>();
    private List<UserDTO> filteredUsers = new ArrayList<>();
    private String searchKeyword = "";

    // ---- Form State ----
    private boolean showForm = false;
    private UserDTO formUser = new UserDTO();
    private List<String> availableRoles = new ArrayList<>();

    @Init
    public void init() {
        validatePage("/pages/user.zul");
        availableRoles = userService.findAllRoleCodes();
        loadUsers();
    }

    private void loadUsers() {
        users = userService.findAll();
        applyFilter();
    }

    @Command
    @NotifyChange({"filteredUsers", "pagedUsers", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void search() {
        applyFilter();
    }

    private void applyFilter() {
        activePage = 0;
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            filteredUsers = new ArrayList<>(users);
        } else {
            String kw = searchKeyword.toLowerCase().trim();
            filteredUsers = users.stream()
                    .filter(u -> (u.getUsername() != null && u.getUsername().toLowerCase().contains(kw)) || 
                                 (u.getFullName() != null && u.getFullName().toLowerCase().contains(kw)) || 
                                 (u.getEmail() != null && u.getEmail().toLowerCase().contains(kw)))
                    .toList();
        }
    }

    // ---- CRUD Actions ----

    @Command
    @NotifyChange({"showForm", "formUser"})
    public void openAdd() {
        formUser = new UserDTO();
        showForm = true;
    }

    @Command
    @NotifyChange({"showForm", "formUser"})
    public void openEdit(@BindingParam("dto") UserDTO dto) {
        // Copy data into a separate DTO for form binding
        formUser = new UserDTO(
                dto.getId(),
                dto.getUsername(),
                dto.getFullName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getActive(),
                new ArrayList<>(dto.getRoleCodes())
        );
        showForm = true;
    }

    @Command
    @NotifyChange({"showForm"})
    public void cancelForm() {
        showForm = false;
    }

    @Command
    @NotifyChange({
        "users", "filteredUsers", "pagedUsers", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay", "showForm",
        "donutSvg", "allStyle", "successStyle", "pendingStyle", "failedStyle",
        "totalLabel", "successLabel", "pendingLabel", "failedLabel"
    })
    public void saveUser() {
        userService.save(formUser);
        loadUsers();
        showForm = false;
    }

    @Command
    @NotifyChange({"users", "filteredUsers", "pagedUsers", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void deleteUser(@BindingParam("id") Long id) {
        userService.delete(id);
        loadUsers();
    }

    @Command
    @NotifyChange("formUser")
    public void toggleRole(@BindingParam("role") String roleCode) {
        List<String> currentRoles = formUser.getRoleCodes();
        if (currentRoles.contains(roleCode)) {
            currentRoles.remove(roleCode);
        } else {
            currentRoles.add(roleCode);
        }
    }

    public boolean hasRole(String roleCode) {
        return formUser != null && formUser.getRoleCodes().contains(roleCode);
    }

    // ---- Getters & Setters ----

    public List<UserDTO> getFilteredUsers() { return filteredUsers; }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }

    public boolean isShowForm() { return showForm; }

    public UserDTO getFormUser() { return formUser; }
    public void setFormUser(UserDTO formUser) { this.formUser = formUser; }

    public List<String> getAvailableRoles() { return availableRoles; }

    // =========================================================
    // Custom Pagination
    // =========================================================
    private int activePage = 0;
    private final int pageSize = 5;

    public List<UserDTO> getPagedUsers() {
        int start = activePage * pageSize;
        int end = Math.min(start + pageSize, filteredUsers.size());
        if (start > filteredUsers.size() || start < 0) {
            return new ArrayList<>();
        }
        return filteredUsers.subList(start, end);
    }

    public boolean isHasPrev() {
        return activePage > 0;
    }

    public boolean isHasNext() {
        return (activePage + 1) * pageSize < filteredUsers.size();
    }

    public String getPagingInfo() {
        if (filteredUsers.isEmpty()) {
            return "No items to display";
        }
        int start = activePage * pageSize + 1;
        int end = Math.min(start + pageSize - 1, filteredUsers.size());
        return "Showing " + start + " - " + end + " of " + filteredUsers.size();
    }

    public String getCurrentPageDisplay() {
        int totalPages = (int) Math.ceil((double) filteredUsers.size() / pageSize);
        if (totalPages == 0) totalPages = 1;
        return (activePage + 1) + " / " + totalPages;
    }

    @Command
    @NotifyChange({"pagedUsers", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void nextPage() {
        if (isHasNext()) {
            activePage++;
        }
    }

    @Command
    @NotifyChange({"pagedUsers", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void prevPage() {
        if (isHasPrev()) {
            activePage--;
        }
    }
}
