package com.rey.template.repository;

import com.rey.template.entity.userresp.master.MstUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MstUserRepository extends JpaRepository<MstUser, Long> {
    Optional<MstUser> findByUsername(String username);
}
