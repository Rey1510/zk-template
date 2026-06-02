package com.rey.template.security;

import com.rey.template.dto.ResponsibilityDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.List;

@Component
@SessionScope
@Getter
@Setter
public class UserSession implements Serializable {

    private String username;
    private String fullName;

    private List<ResponsibilityDTO> responsibilities;

    private ResponsibilityDTO currentResponsibility;

    public boolean isLoggedIn() {
        return username != null;
    }
}
