package com.rey.template.entity.userresp.master;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mst_menu")
@Getter
@Setter
public class MstMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "menu_code")
    private String menuCode;

    @Column(name = "menu_name")
    private String menuName;

    @Column(name = "zul_path")
    private String zulPath;

    @Column(name = "menu_order")
    private Integer menuOrder;

    @Column(name = "active")
    private Boolean active;
}
