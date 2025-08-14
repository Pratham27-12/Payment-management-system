package zeta.payments.commons.route;

public class PaymentRoute {
    public static final String V1 = "/v1";
    public static final String API = "/api";
    public static final String PAYMENTS = "/payments";
    public static final String USERS = "/users";
    public static final String AUDITS = "/audits";
    public static final String ID = "/{id}";
    public static final String USER_NAME = "/{userName}";
     public static final String UPDATE_USER_PASSWORD = USER_NAME + "/password";
    public static final String YEAR = "/year/{year}";
    public static final String REPORTS = "/reports";
    public static final String MONTHLY = "/month/{month}" + YEAR;
    public static final String QUARTERLY = "/quarter/{quarter}" + YEAR;
}
