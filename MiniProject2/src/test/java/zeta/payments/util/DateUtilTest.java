package zeta.payments.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // convertDdMmYyyyToEpochMilli Tests
    @Test
    void convertDdMmYyyyToEpochMilli_ValidDate_Success() {
        String dateString = "15/03/2024";
        long expectedEpoch = LocalDate.of(2024, 3, 15)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(expectedEpoch, result);
    }

    @Test
    void convertDdMmYyyyToEpochMilli_FirstDayOfYear_Success() {
        String dateString = "01/01/2024";
        long expectedEpoch = LocalDate.of(2024, 1, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(expectedEpoch, result);
    }

    @Test
    void convertDdMmYyyyToEpochMilli_LastDayOfYear_Success() {
        String dateString = "31/12/2023";
        long expectedEpoch = LocalDate.of(2023, 12, 31)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(expectedEpoch, result);
    }

    @Test
    void convertDdMmYyyyToEpochMilli_LeapYearDate_Success() {
        String dateString = "29/02/2024";
        long expectedEpoch = LocalDate.of(2024, 2, 29)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(expectedEpoch, result);
    }

    @Test
    void convertDdMmYyyyToEpochMilli_InvalidFormat_ReturnsMinusOne() {
        String dateString = "2024-03-15";

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(-1, result);
        assertTrue(errContent.toString().contains("Error parsing date:"));
    }

    @Test
    void convertDdMmYyyyToEpochMilli_InvalidDate_ReturnsMinusOne() {
        String dateString = "32/13/2024";

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(-1, result);
        assertTrue(errContent.toString().contains("Error parsing date:"));
    }

    @Test
    void convertDdMmYyyyToEpochMilli_EmptyString_ReturnsMinusOne() {
        String dateString = "";

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(-1, result);
        assertTrue(errContent.toString().contains("Error parsing date:"));
    }

    @Test
    void convertDdMmYyyyToEpochMilli_IncompleteDate_ReturnsMinusOne() {
        String dateString = "15/03";

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(-1, result);
        assertTrue(errContent.toString().contains("Error parsing date:"));
    }

    @Test
    void convertDdMmYyyyToEpochMilli_WrongSeparator_ReturnsMinusOne() {
        String dateString = "15-03-2024";

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(-1, result);
        assertTrue(errContent.toString().contains("Error parsing date:"));
    }

    @Test
    void convertDdMmYyyyToEpochMilli_ExtraCharacters_ReturnsMinusOne() {
        String dateString = "15/03/2024 extra";

        long result = DateUtil.convertDdMmYyyyToEpochMilli(dateString);

        assertEquals(-1, result);
        assertTrue(errContent.toString().contains("Error parsing date:"));
    }

    // convertEpochToDateAndReturnMonth Tests
    @Test
    void convertEpochToDateAndReturnMonth_January_ReturnsJanuary() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("January", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_February_ReturnsFebruary() {
        LocalDate date = LocalDate.of(2024, 2, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("February", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_March_ReturnsMarch() {
        LocalDate date = LocalDate.of(2024, 3, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("March", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_April_ReturnsApril() {
        LocalDate date = LocalDate.of(2024, 4, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("April", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_May_ReturnsMay() {
        LocalDate date = LocalDate.of(2024, 5, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("May", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_June_ReturnsJune() {
        LocalDate date = LocalDate.of(2024, 6, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("June", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_July_ReturnsJuly() {
        LocalDate date = LocalDate.of(2024, 7, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("July", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_August_ReturnsAugust() {
        LocalDate date = LocalDate.of(2024, 8, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("August", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_September_ReturnsSeptember() {
        LocalDate date = LocalDate.of(2024, 9, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("September", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_October_ReturnsOctober() {
        LocalDate date = LocalDate.of(2024, 10, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("October", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_November_ReturnsNovember() {
        LocalDate date = LocalDate.of(2024, 11, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("November", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_December_ReturnsDecember() {
        LocalDate date = LocalDate.of(2024, 12, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("December", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_EpochZero_ReturnsJanuary() {
        long epochMillis = 0L; // January 1, 1970

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("January", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_NegativeEpoch_ValidResult() {
        long epochMillis = -86400000L; // December 31, 1969

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("December", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_FutureDate_ValidResult() {
        LocalDate futureDate = LocalDate.of(2030, 6, 15);
        long epochMillis = futureDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("June", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_LeapYearDate_ValidResult() {
        LocalDate leapYearDate = LocalDate.of(2024, 2, 29);
        long epochMillis = leapYearDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        String result = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        assertEquals("February", result);
        assertTrue(outContent.toString().contains("Formatted Date:"));
    }

    @Test
    void convertEpochToDateAndReturnMonth_VerifyConsoleOutput() {
        LocalDate date = LocalDate.of(2024, 5, 15);
        long epochMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        DateUtil.convertEpochToDateAndReturnMonth(epochMillis);

        String output = outContent.toString();
        assertTrue(output.contains("Formatted Date:"));
        assertTrue(output.contains("15/05/24"));
    }

    // Integration tests combining both methods
    @Test
    void integrationTest_ConvertDateStringToEpochAndBack() {
        String originalDate = "15/03/2024";

        // Convert to epoch
        long epochMillis = DateUtil.convertDdMmYyyyToEpochMilli(originalDate);
        assertNotEquals(-1, epochMillis);

        // Convert back to month
        String monthName = DateUtil.convertEpochToDateAndReturnMonth(epochMillis);
        assertEquals("March", monthName);
    }

    @Test
    void integrationTest_InvalidDateDoesNotAffectSecondMethod() {
        // First call with invalid date
        long invalidEpoch = DateUtil.convertDdMmYyyyToEpochMilli("invalid");
        assertEquals(-1, invalidEpoch);

        // Second call with valid epoch should still work
        LocalDate date = LocalDate.of(2024, 7, 15);
        long validEpoch = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        String result = DateUtil.convertEpochToDateAndReturnMonth(validEpoch);
        assertEquals("July", result);
    }
}
