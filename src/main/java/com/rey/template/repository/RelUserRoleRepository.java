package com.rey.template.repository;

import com.rey.template.entity.userresp.rel.RelUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelUserRoleRepository extends JpaRepository<RelUserRole, Long> {

    @Query("""
        select ur
        from RelUserRole ur
        join fetch ur.role r
        join fetch ur.user u
        where u.username = :username
    """)
    List<RelUserRole> findByUserUsername(String username);

    @Query("""
        select ur
        from RelUserRole ur
        join fetch ur.role r
        join fetch ur.user u
        where u.userId = :userId
    """)
    List<RelUserRole> findByUserUserId(Long userId);

    void deleteByUserUserId(Long userId);
}
