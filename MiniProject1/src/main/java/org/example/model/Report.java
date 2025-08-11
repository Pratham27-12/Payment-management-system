package org.example.model;

import java.time.LocalDate;
import java.util.Map;

public class Report {
    private String reportType;
    private LocalDate date;
    private Map<String, Data> reportData;
    private String balanceType;
    private Long totalNetBalance;
    private String status;
    private String message;

    public static class Data {
        public Long inComingPayments;
        public Long outGoingPayments;
        public Long netBalance;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, Data> getReportData() {
        return reportData;
    }

    public void setReportData(Map<String, Data> reportData) {
        this.reportData = reportData;
    }

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    public Long getTotalNetBalance() {
        return totalNetBalance;
    }

    public void setTotalNetBalance(Long totalNetBalance) {
        this.totalNetBalance = totalNetBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
