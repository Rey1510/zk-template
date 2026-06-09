package com.rey.template.repository;

import com.rey.template.entity.userresp.master.MstRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MstRoleRepository extends JpaRepository<MstRole, Long> {
    Optional<MstRole> findByRoleCode(String roleCode);
}
