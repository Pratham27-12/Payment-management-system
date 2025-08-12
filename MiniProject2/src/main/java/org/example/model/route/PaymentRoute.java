package org.example.model.route;

public class PaymentRoute {
    public static final String V1 = "/v1";
    public static final String API = "/api";
    public static final String PAYMENTS = "/payments";
    public static final String USERS = "/users";
    public static final String AUDIT_TRAIL_BASE_URL = API + V1 +  "/audit-trail";
    public static final String ID = "/{id}";
    public static final String USER_NAME = "/{userName}";
    public static final String CREATE = "/create";
    public static final String UPDATE = "/update";
    public static final String GET_ALL = "/get-all";
    public static final String START_DATE = "/start-date/{startDate}";
    public static final String END_DATE = "/end-date/{endDate}";
    public static final String UPDATE_USER_ROLE = "{userName}/role";
    public static final String UPDATE_USER_PASSWORD = USER_NAME + "/password";
    public static final String YEAR = "/year/{year}";
    public static final String REPORTS = "/reports";
    public static final String MONTHLY = "/monthly/{month}" + YEAR;
    public static final String QUARTERLY = "/quarterly/{quarter}" + YEAR;
}
