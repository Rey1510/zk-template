package com.rey.template.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportDTO {

    private Long id;

    private String name;

    private Status status;
}
