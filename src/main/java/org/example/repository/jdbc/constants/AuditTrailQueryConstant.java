package org.example.repository.jdbc.constants;

public class AuditTrailQueryConstant {
    public static final String PAYMENT_ID = "payment_id";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String TYPE = "payment_type";
    public static final String CATEGORY = "category";
    public static final String STATUS = "status";
    public static final String USER_NAME = "user_name";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String TABLE = "payment_system.audit_trail";

    public static String getAuditTrailById(){
        return String.format("SELECT * FROM %s WHERE %s = ?", TABLE, PAYMENT_ID);
    }

    public static String getAuditTrailByCreatedAtRange() {
        return String.format("SELECT * FROM %s WHERE %s BETWEEN ? AND ?", TABLE, CREATED_AT);
    }
}
