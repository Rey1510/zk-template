package com.rey.template.repository;

import com.rey.template.dto.Status;
import com.rey.template.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository
        extends JpaRepository<Report, Long> {

    List<Report> findByStatus(Status status);

}
