package com.rey.template.entity.userresp.rel;

import com.rey.template.entity.userresp.master.MstMenu;
import com.rey.template.entity.userresp.master.MstRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rel_role_menu")
@Getter
@Setter
public class RelRoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rel_role_menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private MstRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private MstMenu menu;
}
