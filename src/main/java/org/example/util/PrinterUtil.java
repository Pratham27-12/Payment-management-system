package org.example.util;

import org.example.model.response.ReportResponse;
import org.example.repository.jdbc.dao.AuditTrail;
import org.example.repository.jdbc.dao.Payment;
import org.example.repository.jdbc.dao.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PrinterUtil {

    public static void printPaymentsTable(List<Payment> payments) {
        String format = "| %-10s | %-10s | %-8s | %-18s | %-10s | %-10s | %-10s | %-20s | %-20s |%n";
        System.out.format("+------------+------------+----------+--------------------+------------+------------+------------+----------------------+----------------------+%n");
        System.out.format("| ID         | Amount     | Currency | Category           | Type       | Status     | User       | Created At           | Updated At           |%n");
        System.out.format("+------------+------------+----------+--------------------+------------+------------+------------+----------------------+----------------------+%n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        for (Payment p : payments) {
            System.out.format(format,
                    trim(p.getId(), 10),
                    trim(p.getAmount(), 10),
                    trim(p.getCurrency(), 8),
                    trim(p.getCategory().name(), 18),
                    trim(p.getType().name(), 10),
                    trim(p.getStatus().name(), 10),
                    trim(p.getUserName(), 10),
                    formatDate(p.getCreatedAt(), formatter),
                    formatDate(p.getUpdatedAt(), formatter));
        }

        System.out.format("+------------+------------+----------+--------------------+------------+------------+------------+----------------------+----------------------+%n");
    }

    public static void printPaymentsAuditTable(List<AuditTrail> audits) {
        String format = "| %-10s | %-10s | %-8s | %-18s | %-10s | %-10s | %-10s | %-20s | %-20s |%n";
        System.out.format("+------------+------------+----------+--------------------+------------+------------+------------+----------------------+----------------------+%n");
        System.out.format("| ID         | Amount     | Currency | Category           | Type       | Status     | User       | Created At           | Updated At           |%n");
        System.out.format("+------------+------------+----------+--------------------+------------+------------+------------+----------------------+----------------------+%n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        for (AuditTrail p : audits) {
            System.out.format(format,
                    trim(p.getId(), 10),
                    trim(p.getAmount(), 10),
                    trim(p.getCurrency(), 8),
                    trim(p.getCategory().name(), 18),
                    trim(p.getType().name(), 10),
                    trim(p.getStatus().name(), 10),
                    trim(p.getUserName(), 10),
                    formatDate(p.getCreatedAt(), formatter),
                    formatDate(p.getUpdatedAt(), formatter));
        }

        System.out.format("+------------+------------+----------+--------------------+------------+------------+------------+----------------------+----------------------+%n");
    }

    public static void printUsersTable(List<User> users) {
        String format = "| %-10s | %-15s | %-20s | %-12s |%n";
        System.out.format("+------------+-----------------+----------------------+--------------+%n");
        System.out.format("| ID         | Username        | Password             | Role         |%n");
        System.out.format("+------------+-----------------+----------------------+--------------+%n");

        for (User user : users) {
            System.out.format(format,
                    trim(user.getId(), 10),
                    trim(user.getUsername(), 15),
                    maskPassword(user.getPassword()),
                    user.getRole() != null ? user.getRole().name() : "N/A");
        }

        System.out.format("+------------+-----------------+----------------------+--------------+%n");
    }

    public static void printReport(ReportResponse reportResponse) {
        System.out.println("Report: " + reportResponse.getReportType());

        LocalDate date = reportResponse.getDate();
        String formattedDate = date.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Generated At: " + formattedDate);
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.printf("| %-6s | %-15s | %-15s | %-12s |%n", "Month", "Total INCOMING", "Total OUTGOING", "Net Balance");
        System.out.println("------------------------------------------------------------");

        long totalIncoming = 0L;
        long totalOutgoing = 0L;
        long totalNet = 0L;

        for (Map.Entry<String, ReportResponse.Data> entry : reportResponse.getReportData().entrySet()) {
            String month = entry.getKey();
            ReportResponse.Data data = entry.getValue();

            long in = data.inComingPayments != null ? data.inComingPayments : 0L;
            long out = data.outGoingPayments != null ? data.outGoingPayments : 0L;
            long net = data.netBalance != null ? data.netBalance : 0L;

            totalIncoming += in;
            totalOutgoing += out;
            totalNet += net;

            System.out.printf("| %-6s | %-15s | %-15s | %-12s |%n",
                    month,
                    formatCurrency(in),
                    formatCurrency(out),
                    formatCurrency(net));
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("Cumulative INCOMING: ₹" + formatCurrency(totalIncoming));
        System.out.println("Cumulative OUTGOING: ₹" + formatCurrency(totalOutgoing));
        System.out.println("Cumulative Net Balance: ₹" + formatCurrency(totalNet));
    }

    private static String formatCurrency(long value) {
        return String.format("%,d", value);
    }

    private static String maskPassword(String password) {
        if (password == null) return "N/A";
        return "*".repeat(Math.min(password.length(), 20));
    }

    private static String formatDate(Long epochMilli, DateTimeFormatter formatter) {
        return epochMilli == null ? "N/A" : formatter.format(Instant.ofEpochMilli(epochMilli));
    }

    private static String trim(String value, int maxLength) {
        if (value == null) return "N/A";
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 3) + "...";
    }

}
