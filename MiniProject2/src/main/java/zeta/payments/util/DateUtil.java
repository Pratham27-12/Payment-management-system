package zeta.payments.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static long convertDdMmYyyyToEpochMilli(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return -1;
        }
    }

    public static String convertEpochToDateAndReturnMonth(long epochMillis) {
        Date date = new Date(epochMillis);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String formattedDate = dateFormat.format(date);
        System.out.println("Formatted Date: " + formattedDate);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        return monthFormat.format(date);
    }
}
