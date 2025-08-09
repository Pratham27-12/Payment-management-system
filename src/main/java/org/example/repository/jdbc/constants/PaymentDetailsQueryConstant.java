package org.example.repository.jdbc.constants;

public class PaymentDetailsQueryConstant {
    public static final String PAYMENT_ID = "payment_id";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String TYPE = "payment_type";
    public static final String CATEGORY = "category";
    public static final String CREATED_BY = "created_by";
    public static final String ACCOUNT_NAME = "account_name";
    public static final String STATUS = "status";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    public static final String TABLE = "payment_system.payment_details";

    public static String createPaymentDetailsQuery() {
        return String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?::payment_type, ?::payment_category, ?::payment_status)",
                TABLE, PAYMENT_ID, CREATED_BY, ACCOUNT_NAME, AMOUNT, CURRENCY, TYPE, CATEGORY, STATUS);
    }

    public static String updatePaymentDetailsById() {
        return String.format("UPDATE %s SET %s = ?::payment_status, %s = ? WHERE %s = ?", TABLE,
                STATUS, CREATED_BY, PAYMENT_ID);
    }

    public static String getPaymentDetailsById() {
        return String.format("SELECT * FROM %s WHERE %s = ?", TABLE, PAYMENT_ID);
    }

    public static String getAllPaymentDetails() {
        return String.format("SELECT * FROM %s", TABLE);
    }

    public static String getPaymentDetailsByCreatedAtRange() {
        return String.format("SELECT * FROM %s WHERE %s between ? AND ?", TABLE, CREATED_AT);
    }
}
