package com.rey.template.entity.userresp.rel;

import com.rey.template.entity.userresp.master.MstRole;
import com.rey.template.entity.userresp.master.MstUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rel_user_role")
@Getter
@Setter
public class RelUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rel_user_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MstUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private MstRole role;
}