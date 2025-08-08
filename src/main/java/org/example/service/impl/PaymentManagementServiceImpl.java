package org.example.service.impl;

import org.example.model.response.ReportResponse;
import org.example.model.response.PaymentLifeCycleManagementResponse;
import org.example.model.enums.PaymentType;
import org.example.repository.PaymentRepository;
import org.example.repository.jdbc.dao.Payment;
import org.example.repository.jdbc.impl.PaymentRepositoryImpl;
import org.example.service.PaymentManagementService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

import static org.example.util.DateUtil.convertEpochToDateAndReturnMonth;

@Service
public class PaymentManagementServiceImpl implements PaymentManagementService {
    private final PaymentRepository paymentRepository;

    public PaymentManagementServiceImpl(PaymentRepositoryImpl paymentRepositoryImpl) {
        this.paymentRepository = paymentRepositoryImpl;
    }

    @Override
    public CompletableFuture<PaymentLifeCycleManagementResponse> createPaymentRecord(Payment payment) {
        return paymentRepository.createPayment(payment)
                .thenCompose(aVoid -> createPaymentSuccesResponse(List.of(), "Payment Created Successfully", "SUCCESS"));
    }

    @Override
    public CompletableFuture<PaymentLifeCycleManagementResponse> updatePayment(String id, String userName, Payment payment) {
        return  paymentRepository.updatePayment(id, payment, userName)
                    .thenCompose(aVoid -> createPaymentSuccesResponse(List.of(), "Payment Status Updated Successfully", "SUCCESS"));
    }

    @Override
    public CompletableFuture<ReportResponse> generateMonthlyReport(Long month, Long year) {
        LocalDate startDate = LocalDate.of(year.intValue(), month.intValue(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        long startEpoch = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endEpoch = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        CompletableFuture<List<Payment>> payments = paymentRepository.findPaymentsBetween(startEpoch, endEpoch);
        return buildReport(payments, "MONTHLY");
    }

    @Override
    public CompletableFuture<ReportResponse> generateQuarterlyReport(Long quarter, Long year) {
        LocalDate startDate;
        LocalDate endDate = switch (quarter.intValue()) {
            case 1 -> {
                startDate = LocalDate.of(year.intValue(), Month.JANUARY, 1);
                yield LocalDate.of(year.intValue(), Month.MARCH, 31);
            }
            case 2 -> {
                startDate = LocalDate.of(year.intValue(), Month.APRIL, 1);
                yield LocalDate.of(year.intValue(), Month.JUNE, 30);
            }
            case 3 -> {
                startDate = LocalDate.of(year.intValue(), Month.JULY, 1);
                yield LocalDate.of(year.intValue(), Month.SEPTEMBER, 30);
            }
            case 4 -> {
                startDate = LocalDate.of(year.intValue(), Month.OCTOBER, 1);
                yield LocalDate.of(year.intValue(), Month.DECEMBER, 31);
            }
            default -> throw new IllegalArgumentException("Invalid quarter: " + quarter);
        };

        long startEpoch = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endEpoch = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        CompletableFuture<List<Payment>> payments = paymentRepository.findPaymentsBetween(startEpoch, endEpoch);
        return buildReport(payments, "QUARTERLY");
    }

    @Override
    public CompletableFuture<PaymentLifeCycleManagementResponse> getAllPayment() {
        return paymentRepository.getAllPayments()
                .thenCompose(payments -> {
                    if (payments.isEmpty()) {
                        throw new RuntimeException("No Payments Found");
                    }
                    return createPaymentSuccesResponse(payments, "", "SUCCESS");
                });
    }

    @Override
    public CompletableFuture<PaymentLifeCycleManagementResponse> getPaymentById(String id) {
        return paymentRepository.getPaymentById(id)
                .thenCompose(payment -> {
                    if (payment == null) {
                        throw new RuntimeException("Payment Not Found");
                    }
                    return createPaymentSuccesResponse(List.of(payment), "", "SUCCESS");
                });
    }

    private CompletableFuture<ReportResponse> buildReport(CompletableFuture<List<Payment>> paymentsFuture, String reportType) {
        return paymentsFuture.thenApply(payments -> {
            if (payments.isEmpty()) {
                return getEmptyReportResponse(reportType, "No Payments Found for the specified period");
            }

            Map<String, ReportResponse.Data> reportData = payments.stream()
                    .collect(Collectors.groupingBy(
                            this::getPaymentMonth,
                            LinkedHashMap::new,
                            Collectors.collectingAndThen(Collectors.toList(), this::createReportData)
                    ));

            double totalIncoming = reportData.values().stream().mapToDouble(d -> d.inComingPayments).sum();
            double totalOutgoing = reportData.values().stream().mapToDouble(d -> d.outGoingPayments).sum();

            return ReportResponse.builder()
                    .reportType(reportType)
                    .date(LocalDate.now())
                    .balanceType(totalIncoming > totalOutgoing ? "CREDIT" : "DEBIT")
                    .reportData(reportData)
                    .totalNetBalance(Math.abs(totalIncoming - totalOutgoing))
                    .status("SUCCESS")
                    .build();
        });
    }

    private String getPaymentMonth(Payment payment) {
        return convertEpochToDateAndReturnMonth(payment.getCreatedAt());
    }

    private ReportResponse.Data createReportData(List<Payment> monthPayments) {
        ReportResponse.Data data = new ReportResponse.Data();
        data.inComingPayments = calculateTotalAmount(monthPayments, PaymentType.INCOMING);
        data.outGoingPayments = calculateTotalAmount(monthPayments, PaymentType.OUTGOING);
        data.netBalance = data.inComingPayments - data.outGoingPayments;
        return data;
    }

    private double calculateTotalAmount(List<Payment> payments, PaymentType type) {
        return payments.stream()
                .filter(payment -> payment.getType() == type)
                .mapToDouble(payment -> {
                    double amount = Double.parseDouble(payment.getAmount());
                    String currency = payment.getCurrency(); // Assuming Payment has getCurrency() method
                    return convertToINR(amount, currency);
                })
                .sum();
    }

    private double convertToINR(double amount, String currency) {
        if (currency == null || "INR".equalsIgnoreCase(currency)) {
            return amount;
        }

        Map<String, Double> exchangeRates = Map.of(
                "USD", 83.0,
                "EUR", 90.0,
                "GBP", 105.0,
                "JPY", 0.56,
                "CAD", 61.0
        );

        Double rate = exchangeRates.get(currency.toUpperCase());
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }

        return amount * rate;
    }

    private static ReportResponse getEmptyReportResponse(String reportType, String message) {
        return ReportResponse.builder()
                .reportType(reportType)
                .date(LocalDate.now())
                .status("SUCCESS")
                .message(message)
                .build();
    }

    private CompletableFuture<PaymentLifeCycleManagementResponse> createPaymentSuccesResponse(List<Payment> payments, String message, String status) {
        PaymentLifeCycleManagementResponse response = PaymentLifeCycleManagementResponse.builder()
                .payments(payments)
                .message(message)
                .status(status)
                .build();
        return CompletableFuture.completedFuture(response);
    }
}
