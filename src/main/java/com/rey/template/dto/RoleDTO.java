package com.rey.template.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private List<String> menuCodes = new ArrayList<>();

    public String getMenusDisplay() {
        if (menuCodes == null || menuCodes.isEmpty()) {
            return "";
        }
        return String.join(", ", menuCodes);
    }
}
