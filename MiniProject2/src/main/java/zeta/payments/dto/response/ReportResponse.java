package zeta.payments.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Builder
public class ReportResponse {
    private String reportType;
    private LocalDate date;
    private Map<String, Data> reportData;
    private String balanceType;
    private Double totalNetBalance;
    private String status;
    private String message;

    public static class Data {
        public Double inComingPayments;
        public Double outGoingPayments;
        public Double netBalance;
    }
}
