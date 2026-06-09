package com.rey.template.service;

import com.rey.template.dto.Status;
import com.rey.template.entity.Report;

import java.util.List;

public interface ReportService {
    List<Report> findAll();
    List<Report> findByStatus(Status status);
    Report save(Report report);
    void delete(Long id);
}
