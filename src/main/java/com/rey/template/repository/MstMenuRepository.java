package com.rey.template.repository;

import com.rey.template.entity.userresp.master.MstMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MstMenuRepository extends JpaRepository<MstMenu, Long> {
    Optional<MstMenu> findByMenuCode(String menuCode);
}
