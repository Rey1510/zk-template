package com.rey.template.service;

import com.rey.template.dto.UserDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> findAll();
    void save(UserDTO dto);
    void delete(Long id);
    List<String> findAllRoleCodes();
}
