package com.rey.template.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuManagementDTO {
    private Long menuId;
    private String menuCode;
    private String menuName;
    private String zulPath;
    private Integer menuOrder;
    private Boolean active = true;
}
