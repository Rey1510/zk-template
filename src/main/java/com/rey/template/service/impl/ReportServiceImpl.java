package com.rey.template.service.impl;

import com.rey.template.dto.Status;
import com.rey.template.entity.Report;
import com.rey.template.repository.ReportRepository;
import com.rey.template.service.ReportService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public List<Report> findAll() {
        return reportRepository.findAll(Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"));
    }

    @Override
    public List<Report> findByStatus(Status status) {
        return reportRepository.findByStatus(status);
    }

    @Override
    public Report save(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public void delete(Long id) {
        reportRepository.deleteById(id);
    }
}
