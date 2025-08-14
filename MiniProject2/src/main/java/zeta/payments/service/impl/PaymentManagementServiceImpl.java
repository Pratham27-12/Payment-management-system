package zeta.payments.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import zeta.payments.exception.PaymentManagementException;
import zeta.payments.dto.response.ReportResponse;
import zeta.payments.dto.response.PaymentLifeCycleManagementResponse;
import zeta.payments.commons.enums.PaymentType;
import zeta.payments.repository.PaymentRepository;
import zeta.payments.repository.UserRepository;
import zeta.payments.entity.Payment;
import zeta.payments.service.PaymentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

import static zeta.payments.util.DateUtil.convertEpochToDateAndReturnMonth;

@Service
public class PaymentManagementServiceImpl implements PaymentManagementService {

    Logger logger = Logger.getLogger(PaymentManagementServiceImpl.class.getName());

    @Autowired
    private PaymentRepository paymentRepository;

    private final Map<String, Double> exchangeRates;

    @Autowired
    private UserRepository userRepository;

    public PaymentManagementServiceImpl(@Value("#{${currency.to.inr.map:{T(java.util.Collections).emptyMap()}}}") Map<String, Double> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    @Override
    public PaymentLifeCycleManagementResponse createPaymentRecord(Payment payment) {
        try {
            paymentRepository.saveAndFlush(payment);
            logger.info("Payment created successfully: " + payment);
            return createPaymentSuccesResponse(List.of(payment), "Payment Created Successfully", "SUCCESS");
        } catch (Exception e) {
            logger.severe("Error creating payment: " + e.getMessage());
            throw new PaymentManagementException(500, "Internal Server Error" , "FAILURE");
        }
    }

    @Override
    public PaymentLifeCycleManagementResponse updatePayment(String id, Payment payment) {
        try {
            Optional<Payment> existingPayment = paymentRepository.findById(Long.parseLong(id));
            if(existingPayment.isPresent()) {
                String updatedBy = SecurityContextHolder.getContext().getAuthentication().getName();
                payment.setCreatedBy(updatedBy);
                paymentRepository.save(payment);
                logger.info("Payment updated successfully: " + payment);
                return createPaymentSuccesResponse(List.of(), "Payment Status Updated Successfully", "SUCCESS");
            }
            logger.warning("Payment not found for update, id: " + id);
            throw new PaymentManagementException(404, "Payment not found", "FAILURE");
        } catch(PaymentManagementException ex){
            throw new PaymentManagementException(ex.getHttpStatus(), ex.getMessage(), ex.getStatus());
        } catch (Exception e) {
            logger.severe("Error updating payment: " + e.getMessage());
            throw new PaymentManagementException(500, "Internal Server Error", "FAILURE");
        }
    }

    @Override
    public ReportResponse generateMonthlyReport(Long month, Long year) {
        logger.info("Generating monthly report for month: " + month + ", year: " + year);
        if(month < 1 || month > 12 || year < 1970 || year > LocalDate.now().getYear()) {
            logger.warning("Invalid month or year specified: month=" + month + ", year=" + year);
            throw new PaymentManagementException(404, "Invalid month or year specified", "FAILURE");
        }

        LocalDate startDate = LocalDate.of(year.intValue(), month.intValue(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        long startEpoch = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endEpoch = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<Payment> payments = paymentRepository.findPaymentsBetween(startEpoch, endEpoch);
        logger.info("Found " + payments.size() + " payments for the specified month and year.");
        return buildReport(payments, "MONTHLY");
    }

    @Override
    public ReportResponse generateQuarterlyReport(Long quarter, Long year) {
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
            default -> throw new PaymentManagementException(404, "Invalid quarter specified", "FAILURE");
        };

        long startEpoch = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endEpoch = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<Payment> payments = paymentRepository.findPaymentsBetween(startEpoch, endEpoch);
        return buildReport(payments, "QUARTERLY");
    }

    @Override
    public PaymentLifeCycleManagementResponse getAllPayment() {
        try {
            List<Payment> payments = paymentRepository.findAll();
            String message = "Payments Fetched Successfully";
            logger.info("Fetched " + payments.size() + " payments successfully.");
            return createPaymentSuccesResponse(payments, message, "SUCCESS");
        } catch (Exception e) {
            logger.severe("Error fetching all payments: " + e.getMessage());
            throw new PaymentManagementException(500, "Internal Server Error", "FAILURE");
        }
    }

    @Override
    public PaymentLifeCycleManagementResponse getPaymentById(Long id) {
        try {
            Optional<Payment> payment = Optional.ofNullable(paymentRepository.findById(id).orElseThrow(() -> new PaymentManagementException(404, "Payment not found", "FAILURE")));
            logger.info("Fetched payment by id: " + id + ", result: " + (payment.isPresent() ? "FOUND" : "NOT FOUND"));
            return createPaymentSuccesResponse(payment.map(List::of).orElse(null), "Payment Fetch Successfully", "SUCCESS");
        } catch (PaymentManagementException ex) {
            logger.severe("PaymentManagementException while fetching payment by id: " + id + ", error: " + ex.getMessage());
            throw new PaymentManagementException(ex.getHttpStatus(), ex.getMessage(), ex.getStatus());
        } catch (Exception e) {
            logger.severe("Error fetching payment by id: " + id + ", error: " + e.getMessage());
            throw new PaymentManagementException(500, "Internal Server Error", "FAILURE");
        }
    }

    @Override
    public PaymentLifeCycleManagementResponse deletePaymentById(Long id) {
        try {
            Optional<Payment> payment = paymentRepository.findById(id);
            if (payment.isPresent()) {
                paymentRepository.delete(payment.get());
                logger.info("Payment deleted successfully: " + payment.get());
                return createPaymentSuccesResponse(List.of(payment.get()), "Payment Deleted Successfully", "SUCCESS");
            }
            logger.warning("Payment not found for deletion, id: " + id);
            throw new PaymentManagementException(404, "Payment not found", "FAILURE");
        } catch (PaymentManagementException ex) {
            throw new PaymentManagementException(ex.getHttpStatus(), ex.getMessage(), ex.getStatus());
        } catch (Exception e) {
            logger.severe("Error deleting payment by id: " + id + ", error: " + e.getMessage());
            throw new PaymentManagementException(500, "Internal Server Error", "FAILURE");
        }
    }

    private ReportResponse buildReport(List<Payment> payments, String reportType) {
            if (payments.isEmpty()) {
                return getEmptyReportResponse(reportType, "No Payments Found for the specified period");
            }

            Map<String, ReportResponse.Data> reportData = payments.stream()
                    .collect(Collectors.groupingBy(
                            this::getPaymentMonth,
                            LinkedHashMap::new,
                            Collectors.collectingAndThen(Collectors.toList(), this::createReportData)
                    ));

            double totalIncoming = reportData.values().stream().mapToDouble(data -> data.inComingPayments).sum();
            double totalOutgoing = reportData.values().stream().mapToDouble(data -> data.outGoingPayments).sum();

            return ReportResponse.builder()
                    .reportType(reportType)
                    .date(LocalDate.now())
                    .balanceType(totalIncoming > totalOutgoing ? "CREDIT" : "DEBIT")
                    .reportData(reportData)
                    .totalNetBalance(Math.abs(totalIncoming - totalOutgoing))
                    .status("SUCCESS")
                    .build();
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

    private PaymentLifeCycleManagementResponse createPaymentSuccesResponse(List<Payment> payments, String message, String status) {
        return PaymentLifeCycleManagementResponse.builder()
                .payments(payments)
                .message(payments == null ? "No Payment Exist for given Payment ID" : message)
                .status(status)
                .build();
    }
}
