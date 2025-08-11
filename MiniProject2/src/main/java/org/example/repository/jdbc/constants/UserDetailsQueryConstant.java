package org.example.repository.jdbc.constants;

public class UserDetailsQueryConstant {
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_ROLE = "user_role";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    public static final String TABLE = "payment_system.user_details";

    public static String getAllUserDetails() {
        return String.format("SELECT * FROM %s", TABLE);
    }

    public static String getUserDetailsByUserName() {
        return String.format("SELECT * FROM %s WHERE %s = ? ", TABLE, USER_NAME);
    }

    public static String createUserDetails() {
        return String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?::user_role, ?, ?)", TABLE,
                USER_ID, USER_NAME, USER_ROLE, PASSWORD, EMAIL);
    }

    public static String updateUserRoleByUserName() {
        return String.format("UPDATE %s SET %s = ?::user_role WHERE %s = ?", TABLE,
                 USER_ROLE, USER_NAME);
    }

    public static String updateUserPasswordByUserName() {
        return String.format("UPDATE %s SET %s = ? WHERE %s = ?", TABLE,
                PASSWORD, USER_NAME);
    }
}
