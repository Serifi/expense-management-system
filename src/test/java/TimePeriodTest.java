import model.TimePeriod;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TimePeriod}.
 */
class TimePeriodTest {

    /**
     * Constructs a new instance of TimePeriodTest.
     */
    public TimePeriodTest() {
    }

    /**
     * Tests the {@link TimePeriod#getStartDate(LocalDate)} method.
     * Verifies that the start date is correctly calculated based on the specified time period.
     */
    @Test
    void testGetStartDate() {
        LocalDate currentDate = LocalDate.of(2023, 7, 10);

        assertEquals(currentDate, TimePeriod.DAY.getStartDate(currentDate), "Start date for DAY should be the current date");

        assertEquals(currentDate, TimePeriod.WEEK.getStartDate(currentDate), "Start date for WEEK should be the Monday of the current week");

        LocalDate expectedMonthStart = LocalDate.of(2023, 7, 1);
        assertEquals(expectedMonthStart, TimePeriod.MONTH.getStartDate(currentDate), "Start date for MONTH should be the first day of the month");

        LocalDate expectedYearStart = LocalDate.of(2023, 1, 1);
        assertEquals(expectedYearStart, TimePeriod.YEAR.getStartDate(currentDate), "Start date for YEAR should be the first day of the year");
    }

    /**
     * Tests the {@link TimePeriod#getEndDate(LocalDate)} method.
     * Verifies that the end date is correctly calculated based on the specified time period.
     */
    @Test
    void testGetEndDate() {
        LocalDate currentDate = LocalDate.of(2023, 7, 10);

        assertEquals(currentDate, TimePeriod.DAY.getEndDate(currentDate), "End date for DAY should be the current date");

        LocalDate expectedWeekEnd = LocalDate.of(2023, 7, 16);
        assertEquals(expectedWeekEnd, TimePeriod.WEEK.getEndDate(currentDate), "End date for WEEK should be the Sunday of the current week");

        LocalDate expectedMonthEnd = LocalDate.of(2023, 7, 31);
        assertEquals(expectedMonthEnd, TimePeriod.MONTH.getEndDate(currentDate), "End date for MONTH should be the last day of the month");

        LocalDate expectedYearEnd = LocalDate.of(2023, 12, 31);
        assertEquals(expectedYearEnd, TimePeriod.YEAR.getEndDate(currentDate), "End date for YEAR should be the last day of the year");
    }

    /**
     * Tests the {@link TimePeriod#getDisplayText(LocalDate)} method.
     * Verifies that the display text is correctly formatted based on the specified time period.
     */
    @Test
    void testGetDisplayText() {
        LocalDate currentDate = LocalDate.of(2023, 7, 10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        String expectedDayDisplay = currentDate.format(formatter);
        assertEquals(expectedDayDisplay, TimePeriod.DAY.getDisplayText(currentDate), "Display text for DAY should be the current date formatted");

        LocalDate startOfWeek = TimePeriod.WEEK.getStartDate(currentDate);
        LocalDate endOfWeek = TimePeriod.WEEK.getEndDate(currentDate);
        String expectedWeekDisplay = startOfWeek.format(formatter) + " - " + endOfWeek.format(formatter);
        assertEquals(expectedWeekDisplay, TimePeriod.WEEK.getDisplayText(currentDate), "Display text for WEEK should be the start and end dates of the week formatted");

        String expectedMonthDisplay = "JULY 2023";
        assertEquals(expectedMonthDisplay, TimePeriod.MONTH.getDisplayText(currentDate), "Display text for MONTH should be the month and year");

        String expectedYearDisplay = "2023";
        assertEquals(expectedYearDisplay, TimePeriod.YEAR.getDisplayText(currentDate), "Display text for YEAR should be the year");
    }
}