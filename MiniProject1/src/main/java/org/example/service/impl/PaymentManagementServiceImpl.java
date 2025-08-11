package org.example.service.impl;

import org.example.model.Report;
import org.example.model.PaymentLifeCycleManagementResponse;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;
import org.example.repository.PaymentRepository;
import org.example.repository.jdbc.dao.Payment;
import org.example.repository.jdbc.impl.PaymentRepositoryImpl;
import org.example.service.PaymentManagementService;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

import static org.example.util.DateUtil.convertEpochToDateAndReturnMonth;
import static org.example.util.ValidatorUtil.validatePayment;
import static org.example.util.ValidatorUtil.validateUserAdmin;
import static org.example.util.ValidatorUtil.validateUserManager;

public class PaymentManagementServiceImpl implements PaymentManagementService {
    @Override
    public CompletableFuture<PaymentLifeCycleManagementResponse> createPaymentRecord(Payment payment, String userName, String password) {
        PaymentRepository paymentRepository = new PaymentRepositoryImpl();
        return validateUserAdmin(userName, password).thenCompose(validateUserResponse -> {
            if (!validateUserResponse.isValid()) {
                throw new RuntimeException(validateUserResponse.getErrorMessage());
            }
            return validatePayment(payment).thenCompose(validationResponse -> {
                if (!validationResponse.isValid()) {
                    throw new RuntimeException(validationResponse.getErrorMessage());
                }
                return paymentRepository.createPayment(payment)
                        .thenCompose(aVoid -> createPaymentSuccesResponse(List.of(), "Payment Created Successfully", "SUCCESS"));
            });
        });
    }

    @Override
    public CompletableFuture<PaymentLifeCycleManagementResponse> updatePayment(String id, String userName, String password, String status) {
        PaymentRepository paymentRepository = new PaymentRepositoryImpl();
        return validateUserManager(userName, password).thenCompose(validateUserResponse -> {
            if (!validateUserResponse.isValid()) {
                throw new RuntimeException(validateUserResponse.getErrorMessage());
            }
            return paymentRepository.updatePaymentStatus(id, PaymentStatus.valueOf(status), userName)
                    .thenCompose(aVoid -> createPaymentSuccesResponse(List.of(), "Payment Status Updated Successfully", "SUCCESS"));
        });
    }

    @Override
    public CompletableFuture<Report> generateMonthlyReport(int month, int year) {
        PaymentRepository paymentRepository = new PaymentRepositoryImpl();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        long startEpoch = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endEpoch = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        CompletableFuture<List<Payment>> payments = paymentRepository.findPaymentsBetween(startEpoch, endEpoch);
        return buildReport(payments, "MONTHLY");
    }

    @Override
    public CompletableFuture<Report> generateQuarterlyReport(int quarter, int year) {
        PaymentRepository paymentRepository = new PaymentRepositoryImpl();

        LocalDate startDate;
        LocalDate endDate = switch (quarter) {
            case 1 -> {
                startDate = LocalDate.of(year, Month.JANUARY, 1);
                yield LocalDate.of(year, Month.MARCH, 31);
            }
            case 2 -> {
                startDate = LocalDate.of(year, Month.APRIL, 1);
                yield LocalDate.of(year, Month.JUNE, 30);
            }
            case 3 -> {
                startDate = LocalDate.of(year, Month.JULY, 1);
                yield LocalDate.of(year, Month.SEPTEMBER, 30);
            }
            case 4 -> {
                startDate = LocalDate.of(year, Month.OCTOBER, 1);
                yield LocalDate.of(year, Month.DECEMBER, 31);
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
        PaymentRepository paymentRepository = new PaymentRepositoryImpl();

        return paymentRepository.getAllPayments()
                .thenCompose(payments -> {
                    if (payments.isEmpty()) {
                        throw new RuntimeException("No Payments Found");
                    }
                    return createPaymentSuccesResponse(payments, "","SUCCESS");
                });
    }

    @Override
    public CompletableFuture<PaymentLifeCycleManagementResponse> getPaymentById(String id) {
        PaymentRepository paymentRepository = new PaymentRepositoryImpl();

        return paymentRepository.getPaymentById(id)
                .thenCompose(payment -> {
                    if (payment == null) {
                        throw new RuntimeException("Payment Not Found");
                    }
                    return createPaymentSuccesResponse(List.of(payment), "","SUCCESS");
                });
    }

    private CompletableFuture<Report> buildReport(CompletableFuture<List<Payment>> paymentsFuture, String reportType) {
        Report report = new Report();
        return paymentsFuture.thenApply(payments -> {
            if (payments.isEmpty()) {
                report.setStatus("FAILURE");
                report.setMessage("No Payments Found for the specified period");
                return report;
            }

            Map<String, Report.Data> reportData = payments.stream().collect(Collectors.groupingBy(
                    payment -> {
                        return convertEpochToDateAndReturnMonth(payment.getCreatedAt());
                    },
                    LinkedHashMap::new,
                    Collectors.collectingAndThen(Collectors.toList(), monthPayments -> {
                        Report.Data data = new Report.Data();
                        data.inComingPayments = monthPayments.stream()
                                .filter(p -> p.getType() == PaymentType.INCOMING)
                                .mapToLong(p -> Long.parseLong(p.getAmount()))
                                .sum();
                        data.outGoingPayments = monthPayments.stream()
                                .filter(p -> p.getType() == PaymentType.OUTGOING)
                                .mapToLong(p -> Long.parseLong(p.getAmount()))
                                .sum();
                        data.netBalance = data.inComingPayments - data.outGoingPayments;
                        return data;
                    })
            ));

            long totalIncoming = reportData.values().stream().mapToLong(d -> d.inComingPayments).sum();
            long totalOutgoing = reportData.values().stream().mapToLong(d -> d.outGoingPayments).sum();


            report.setReportType(reportType);
            report.setDate(LocalDate.now());
            report.setReportData(reportData);
            report.setBalanceType(totalIncoming > totalOutgoing ? "CREDIT" : "DEBIT");
            report.setTotalNetBalance(Math.abs(totalIncoming - totalOutgoing));
            return report;
        });
    }

    private CompletableFuture<PaymentLifeCycleManagementResponse> createPaymentSuccesResponse(List<Payment> payments, String message, String status) {
        PaymentLifeCycleManagementResponse response = new PaymentLifeCycleManagementResponse();
        response.setPayments(payments);
        response.setMessage(message);
        response.setStatus(status);
        return CompletableFuture.completedFuture(response);
    }
}
