package com.rey.template.viewmodel.pages;

import com.rey.template.dto.ReportDTO;
import com.rey.template.dto.Status;
import com.rey.template.util.UrlConstant;
import com.rey.template.viewmodel.common.AuthorizedVM;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.util.List;

@VariableResolver(DelegatingVariableResolver.class)
public class ReportVM extends AuthorizedVM {

    private List<ReportDTO> reports;

    private List<ReportDTO> filteredReports;

    private Status selectedStatus;

    @Init
    public void init() {

        validatePage(
                UrlConstant.URL_REPORT_ZUL);

        reports = List.of(
                new ReportDTO(1L, "Report A", Status.SUCCESS),
                new ReportDTO(2L, "Report B", Status.SUCCESS),
                new ReportDTO(3L, "Report C", Status.PENDING),
                new ReportDTO(4L, "Report D", Status.PENDING),
                new ReportDTO(5L, "Report E", Status.FAILED));

        filteredReports = reports;
    }

    @Command
    @NotifyChange({
            "filteredReports",
            "selectedStatus",
            "donutSvg",
            "allStyle",
            "successStyle",
            "pendingStyle",
            "failedStyle",
            "allStyle"
    })
    public void filter(
            @BindingParam("status") String status) {

        Status clicked = Status.valueOf(status);

        if (clicked == selectedStatus) {

            selectedStatus = null;
            filteredReports = reports;

            return;
        }

        selectedStatus = clicked;

        filteredReports = reports.stream()
                .filter(r -> r.getStatus() == selectedStatus)
                .toList();
    }

    @Command
    @NotifyChange({
            "filteredReports",
            "selectedStatus",
            "donutSvg",
            "allStyle",
            "successStyle",
            "pendingStyle",
            "failedStyle"
    })
    public void showAll() {

        selectedStatus = null;

        filteredReports = reports;
    }

    public List<ReportDTO> getFilteredReports() {
        return filteredReports;
    }

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

    public long getSuccessCount() {
        return reports.stream()
                .filter(r -> r.getStatus() == Status.SUCCESS)
                .count();
    }

    public long getPendingCount() {
        return reports.stream()
                .filter(r -> r.getStatus() == Status.PENDING)
                .count();
    }

    public long getFailedCount() {
        return reports.stream()
                .filter(r -> r.getStatus() == Status.FAILED)
                .count();
    }

    public int getTotalCount() {
        return reports.size();
    }

    public String getDonutSvg() {

        double success = getSuccessPercentage();
        double pending = getPendingPercentage();
        double failed = getFailedPercentage();

        double s1 = success;
        double s2 = success + pending;

        String successClass = segmentClass(Status.SUCCESS);
        String pendingClass = segmentClass(Status.PENDING);
        String failedClass  = segmentClass(Status.FAILED);

        return """
    <svg class='report-donut'
         width='260'
         height='260'
         viewBox='0 0 42 42'>

        <defs>

            <filter id='shadow'>
                <feDropShadow
                        dx='0'
                        dy='1'
                        stdDeviation='1'
                        flood-opacity='0.12'/>
            </filter>

        </defs>

        <!-- Background Ring -->

        <circle
            cx='21'
            cy='21'
            r='15.915'
            fill='transparent'
            stroke='#EDF2F7'
            stroke-width='4'/>

        <!-- Success -->

        <circle
            class='donut-segment %s'
            cx='21'
            cy='21'
            r='15.915'
            fill='transparent'
            stroke='#7FC8A9'
            stroke-width='4'
            stroke-linecap='round'
            filter='url(#shadow)'
            stroke-dasharray='%s %s'
            stroke-dashoffset='25'/>

        <!-- Pending -->

        <circle
            class='donut-segment %s'
            cx='21'
            cy='21'
            r='15.915'
            fill='transparent'
            stroke='#F2CC8F'
            stroke-width='4'
            stroke-linecap='round'
            filter='url(#shadow)'
            stroke-dasharray='%s %s'
            stroke-dashoffset='%s'/>

        <!-- Failed -->

        <circle
            class='donut-segment %s'
            cx='21'
            cy='21'
            r='15.915'
            fill='transparent'
            stroke='#E5989B'
            stroke-width='4'
            stroke-linecap='round'
            filter='url(#shadow)'
            stroke-dasharray='%s %s'
            stroke-dashoffset='%s'/>

        <!-- Center Value -->

        <text
            x='21'
            y='20'
            text-anchor='middle'
            font-size='5'
            font-weight='700'
            fill='#1F2937'>

            %s

        </text>

        <!-- Center Label -->

        <text
            x='21'
            y='25'
            text-anchor='middle'
            font-size='2'
            fill='#94A3B8'
            letter-spacing='.5'>

            %s

        </text>

    </svg>
    """.formatted(

                successClass,
                success,
                100 - success,

                pendingClass,
                pending,
                100 - pending,
                25 - s1,

                failedClass,
                failed,
                100 - failed,
                25 - s2,

                getCenterValue(),
                getCenterLabel()
        );
    }

    /**
     * Returns the extra CSS class for a donut segment based on the current filter selection.
     * <ul>
     *   <li>No filter active  → no extra class (normal appearance)</li>
     *   <li>This segment is selected → {@code donut-active} (thicker, full opacity)</li>
     *   <li>Another segment is selected → {@code donut-inactive} (thinner, dimmed)</li>
     * </ul>
     */
    private String segmentClass(Status segment) {

        if (selectedStatus == null) {
            return "";
        }

        return selectedStatus == segment
                ? "donut-active"
                : "donut-inactive";
    }

    private double percentage(long count) {

        if (reports.isEmpty()) {
            return 0;
        }

        return (count * 100.0) / reports.size();
    }

    public double getSuccessPercentage() {
        return percentage(getSuccessCount());
    }

    public double getPendingPercentage() {
        return percentage(getPendingCount());
    }

    public double getFailedPercentage() {
        return percentage(getFailedCount());
    }

    public boolean isSelected(Status status) {
        return selectedStatus == status;
    }

    public String getSelectedStatus() {

        return selectedStatus == null
                ? ""
                : selectedStatus.name();
    }

    public String getSuccessStyle() {

        if (selectedStatus == null) {
            return "legend-card";
        }

        return selectedStatus == Status.SUCCESS
                ? "legend-card legend-selected"
                : "legend-card legend-dimmed";
    }

    public String getPendingStyle() {

        if (selectedStatus == null) {
            return "legend-card";
        }

        return selectedStatus == Status.PENDING
                ? "legend-card legend-selected"
                : "legend-card legend-dimmed";
    }

    public String getFailedStyle() {

        if (selectedStatus == null) {
            return "legend-card";
        }

        return selectedStatus == Status.FAILED
                ? "legend-card legend-selected"
                : "legend-card legend-dimmed";
    }

    public String getCenterLabel() {

        if (selectedStatus == null) {
            return "TOTAL";
        }

        return selectedStatus.name();
    }

    public long getCenterValue() {

        if (selectedStatus == null) {
            return getTotalCount();
        }

        return filteredReports.size();
    }

    public String getAllStyle() {

        if (selectedStatus == null) {
            return "legend-card legend-selected";
        }

        return "legend-card legend-dimmed";
    }
}
