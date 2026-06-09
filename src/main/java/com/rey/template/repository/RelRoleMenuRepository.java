package com.rey.template.repository;

import com.rey.template.entity.userresp.master.MstMenu;
import com.rey.template.entity.userresp.rel.RelRoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelRoleMenuRepository extends JpaRepository<RelRoleMenu, Long> {

    @Query("SELECT rm.menu FROM RelRoleMenu rm WHERE rm.role.roleCode = :roleCode AND rm.menu.active = true ORDER BY rm.menu.menuOrder ASC")
    List<MstMenu> findMenusByRoleCode(@Param("roleCode") String roleCode);

    List<RelRoleMenu> findByMenuMenuId(Long menuId);

    void deleteByMenuMenuId(Long menuId);

    List<RelRoleMenu> findByRoleRoleId(Long roleId);

    void deleteByRoleRoleId(Long roleId);
}
