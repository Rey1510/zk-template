package com.rey.template.entity;

import com.rey.template.dto.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "REPORT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(
            name = "REPORT_NAME",
            nullable = false,
            length = 100
    )
    private String reportName;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private Status status;

    @Column(
            name = "CREATED_BY",
            length = 50
    )
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(
            name = "UPDATED_BY",
            length = 50
    )
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;
}