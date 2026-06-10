package com.rey.template.viewmodel.pages;

import com.rey.template.dto.ReportDTO;
import com.rey.template.dto.Status;
import com.rey.template.entity.Report;
import com.rey.template.service.ReportService;
import com.rey.template.util.UrlConstant;
import com.rey.template.viewmodel.common.AuthorizedVM;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@VariableResolver(DelegatingVariableResolver.class)
public class ReportVM extends AuthorizedVM {

    // ---- Service ----
    @WireVariable
    private ReportService reportService;

    // ---- Data ----
    private List<ReportDTO> reports = new ArrayList<>();
    private List<ReportDTO> filteredReports = new ArrayList<>();
    private Status selectedStatus;

    // ---- CRUD form state ----
    /** Controls visibility of the add/edit modal */
    private boolean showForm = false;
    /** Holds the DTO being created or edited */
    private ReportDTO formReport = new ReportDTO(null, "", Status.PENDING);

    // =========================================================
    // Init
    // =========================================================

    @Init
    public void init() {
        validatePage(UrlConstant.URL_REPORT_ZUL);
        loadReports();
    }

    private void loadReports() {
        reports = reportService.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
        applyFilter();
    }

    // =========================================================
    // Filter / chart commands
    // =========================================================

    @Command
    @NotifyChange({
            "filteredReports", "pagedReports", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay",
            "selectedStatus", "donutSvg", "allStyle", "successStyle", "pendingStyle", "failedStyle"
    })
    public void filter(@BindingParam("status") String status) {
        Status clicked = Status.valueOf(status);
        if (clicked == selectedStatus) {
            selectedStatus = null;
        } else {
            selectedStatus = clicked;
        }
        applyFilter();
    }

    @Command
    @NotifyChange({
            "filteredReports", "pagedReports", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay",
            "selectedStatus", "donutSvg", "allStyle", "successStyle", "pendingStyle", "failedStyle"
    })
    public void showAll() {
        selectedStatus = null;
        applyFilter();
    }

    private void applyFilter() {
        activePage = 0;
        if (selectedStatus == null) {
            filteredReports = new ArrayList<>(reports);
        } else {
            filteredReports = reports.stream()
                    .filter(r -> r.getStatus() == selectedStatus)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    // =========================================================
    // CRUD Commands
    // =========================================================

    @Command
    @NotifyChange({ "showForm", "formReport" })
    public void openAdd() {
        formReport = new ReportDTO(null, "", Status.PENDING);
        showForm = true;
    }

    @Command
    @NotifyChange({ "showForm", "formReport" })
    public void openEdit(@BindingParam("dto") ReportDTO dto) {
        // Copy values into form so two-way binding doesn't mutate the list item
        // directly
        formReport = new ReportDTO(dto.getId(), dto.getName(), dto.getStatus());
        showForm = true;
    }

    @Command
    @NotifyChange({ "showForm" })
    public void cancelForm() {
        showForm = false;
    }

    @Command
    @NotifyChange({
            "reports", "filteredReports", "pagedReports", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay", "showForm",
            "donutSvg", "allStyle", "successStyle", "pendingStyle", "failedStyle",
            "totalLabel", "successLabel", "pendingLabel", "failedLabel"
    })
    public void saveReport() {
        Report entity = toEntity(formReport);
        reportService.save(entity);
        loadReports();
        showForm = false;
    }

    @Command
    @NotifyChange({
            "reports", "filteredReports", "pagedReports", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay",
            "donutSvg", "allStyle", "successStyle", "pendingStyle", "failedStyle",
            "totalLabel", "successLabel", "pendingLabel", "failedLabel"
    })
    public void deleteReport(@BindingParam("id") Long id) {
        reportService.delete(id);
        loadReports();
    }

    // =========================================================
    // Getters
    // =========================================================

    public List<ReportDTO> getFilteredReports() {
        return filteredReports;
    }

    public boolean isShowForm() {
        return showForm;
    }

    public ReportDTO getFormReport() {
        return formReport;
    }

    public Status[] getStatusValues() {
        return Status.values();
    }

    // ---- Labels ----

    public String getTotalLabel() {
        return "Total (" + getTotalCount() + ")";
    }

    public String getSuccessLabel() {
        return "SUCCESS (" + getSuccessCount() + ")";
    }

    public String getPendingLabel() {
        return "PENDING (" + getPendingCount() + ")";
    }

    public String getFailedLabel() {
        return "FAILED (" + getFailedCount() + ")";
    }

    // ---- Counts ----

    public long getSuccessCount() {
        return reports.stream().filter(r -> r.getStatus() == Status.SUCCESS).count();
    }

    public long getPendingCount() {
        return reports.stream().filter(r -> r.getStatus() == Status.PENDING).count();
    }

    public long getFailedCount() {
        return reports.stream().filter(r -> r.getStatus() == Status.FAILED).count();
    }

    public int getTotalCount() {
        return reports.size();
    }

    // ---- Percentages ----

    public double getSuccessPercentage() {
        return percentage(getSuccessCount());
    }

    public double getPendingPercentage() {
        return percentage(getPendingCount());
    }

    public double getFailedPercentage() {
        return percentage(getFailedCount());
    }

    private double percentage(long count) {
        if (reports.isEmpty())
            return 0;
        return (count * 100.0) / reports.size();
    }

    // ---- Status helpers ----

    public boolean isSelected(Status status) {
        return selectedStatus == status;
    }

    public String getSelectedStatus() {
        return selectedStatus == null ? "" : selectedStatus.name();
    }

    // ---- Card / legend styles ----

    public String getSuccessStyle() {
        return legendStyle(Status.SUCCESS);
    }

    public String getPendingStyle() {
        return legendStyle(Status.PENDING);
    }

    public String getFailedStyle() {
        return legendStyle(Status.FAILED);
    }

    public String getAllStyle() {
        return selectedStatus == null ? "legend-card legend-selected" : "legend-card legend-dimmed";
    }

    private String legendStyle(Status s) {
        if (selectedStatus == null)
            return "legend-card";
        return selectedStatus == s ? "legend-card legend-selected" : "legend-card legend-dimmed";
    }

    // ---- Donut center ----

    public String getCenterLabel() {
        return selectedStatus == null ? "TOTAL" : selectedStatus.name();
    }

    public long getCenterValue() {
        return selectedStatus == null ? getTotalCount() : filteredReports.size();
    }

    // =========================================================
    // Donut SVG
    // =========================================================

    public String getDonutSvg() {
        double success = getSuccessPercentage();
        double pending = getPendingPercentage();
        double failed = getFailedPercentage();
        double s1 = success;
        double s2 = success + pending;

        String sc = segmentClass(Status.SUCCESS);
        String pc = segmentClass(Status.PENDING);
        String fc = segmentClass(Status.FAILED);

        return """
                <svg class='report-donut' width='100' height='100' viewBox='0 0 42 42'>
                    <defs>
                        <filter id='shadow'>
                            <feDropShadow dx='0' dy='1' stdDeviation='1' flood-opacity='0.12'/>
                        </filter>
                    </defs>
                    <!-- Background Ring -->
                    <circle cx='21' cy='21' r='15.915' fill='transparent' stroke='#EDF2F7' stroke-width='4'/>
                    <!-- Success -->
                    <circle class='donut-segment %s' cx='21' cy='21' r='15.915'
                        fill='transparent' stroke='#7FC8A9' stroke-width='4' stroke-linecap='round'
                        filter='url(#shadow)' stroke-dasharray='%s %s' stroke-dashoffset='25'/>
                    <!-- Pending -->
                    <circle class='donut-segment %s' cx='21' cy='21' r='15.915'
                        fill='transparent' stroke='#F2CC8F' stroke-width='4' stroke-linecap='round'
                        filter='url(#shadow)' stroke-dasharray='%s %s' stroke-dashoffset='%s'/>
                    <!-- Failed -->
                    <circle class='donut-segment %s' cx='21' cy='21' r='15.915'
                        fill='transparent' stroke='#E5989B' stroke-width='4' stroke-linecap='round'
                        filter='url(#shadow)' stroke-dasharray='%s %s' stroke-dashoffset='%s'/>
                    <!-- Center Value -->
                    <text x='21' y='20' text-anchor='middle' font-size='5' font-weight='700' fill='#1F2937'>%s</text>
                    <!-- Center Label -->
                    <text x='21' y='25' text-anchor='middle' font-size='2' fill='#94A3B8' letter-spacing='.5'>%s</text>
                </svg>
                """.formatted(
                sc, success, 100 - success,
                pc, pending, 100 - pending, 25 - s1,
                fc, failed, 100 - failed, 25 - s2,
                getCenterValue(), getCenterLabel());
    }

    private String segmentClass(Status segment) {
        if (selectedStatus == null)
            return "";
        return selectedStatus == segment ? "donut-active" : "donut-inactive";
    }

    // =========================================================
    // Mapping helpers
    // =========================================================

    private ReportDTO toDto(Report entity) {
        return new ReportDTO(entity.getId(), entity.getReportName(), entity.getStatus());
    }

    private Report toEntity(ReportDTO dto) {
        Report r = new Report();
        r.setId(dto.getId());
        r.setReportName(dto.getName());
        r.setStatus(dto.getStatus());
        return r;
    }

    // =========================================================
    // Custom Pagination
    // =========================================================
    private int activePage = 0;
    private final int pageSize = 5;

    public List<ReportDTO> getPagedReports() {
        int start = activePage * pageSize;
        int end = Math.min(start + pageSize, filteredReports.size());
        if (start > filteredReports.size() || start < 0) {
            return new ArrayList<>();
        }
        return filteredReports.subList(start, end);
    }

    public boolean isHasPrev() {
        return activePage > 0;
    }

    public boolean isHasNext() {
        return (activePage + 1) * pageSize < filteredReports.size();
    }

    public String getPagingInfo() {
        if (filteredReports.isEmpty()) {
            return "No items to display";
        }
        int start = activePage * pageSize + 1;
        int end = Math.min(start + pageSize - 1, filteredReports.size());
        return "Showing " + start + " - " + end + " of " + filteredReports.size();
    }

    public String getCurrentPageDisplay() {
        int totalPages = (int) Math.ceil((double) filteredReports.size() / pageSize);
        if (totalPages == 0) totalPages = 1;
        return (activePage + 1) + " / " + totalPages;
    }

    @Command
    @NotifyChange({"pagedReports", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void nextPage() {
        if (isHasNext()) {
            activePage++;
        }
    }

    @Command
    @NotifyChange({"pagedReports", "hasPrev", "hasNext", "pagingInfo", "currentPageDisplay"})
    public void prevPage() {
        if (isHasPrev()) {
            activePage--;
        }
    }
}
