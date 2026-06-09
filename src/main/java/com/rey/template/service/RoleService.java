package com.rey.template.service;

import com.rey.template.dto.RoleDTO;
import java.util.List;

public interface RoleService {
    List<RoleDTO> findAll();
    void save(RoleDTO dto);
    void delete(Long roleId);
}
