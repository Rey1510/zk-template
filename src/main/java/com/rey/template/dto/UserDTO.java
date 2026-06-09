package com.rey.template.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String password;
    private Boolean active = true;
    private List<String> roleCodes = new ArrayList<>();

    public String getRolesDisplay() {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return "";
        }
        return String.join(", ", roleCodes);
    }
}
