package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents time periods that can be used to categorize or filter data by days, weeks, months, or years.
 * This enumeration facilitates operations that depend on the granularity of time intervals and includes methods to
 * calculate the start and end dates for each period, which centralizes the date calculation logic.
 */
public enum TimePeriod {
    /** Represents a single day. */
    DAY,
    /** Represents a week. */
    WEEK,
    /** Represents a month. */
    MONTH,
    /** Represents a year. */
    YEAR;

    /**
     * Constructs an instance of TimePeriod.
     */
    TimePeriod() {
    }

    /**
     * Returns the start date of the period based on the provided current date.
     *
     * @param currentDate the date from which the period's start date is calculated
     * @return the start date of the period
     */
    public LocalDate getStartDate(LocalDate currentDate) {
        return switch (this) {
            case DAY -> currentDate;
            case WEEK -> currentDate.with(DayOfWeek.MONDAY);
            case MONTH -> currentDate.withDayOfMonth(1);
            case YEAR -> currentDate.withDayOfYear(1);
        };
    }

    /**
     * Returns the end date of the period based on the provided current date.
     *
     * @param currentDate the date from which the period's end date is calculated
     * @return the end date of the period
     */
    public LocalDate getEndDate(LocalDate currentDate) {
        return switch (this) {
            case DAY -> currentDate;
            case WEEK -> currentDate.with(DayOfWeek.MONDAY).plusDays(6);
            case MONTH -> currentDate.withDayOfMonth(1).plusMonths(1).minusDays(1);
            case YEAR -> currentDate.withDayOfYear(1).plusYears(1).minusDays(1);
        };
    }

    /**
     * Returns a string representation of the current date adjusted to the selected time period.
     * This representation varies depending on the type of the period:
     * - DAY: formatted as "dd.MM.yyyy"
     * - WEEK: formatted as "start date - end date" of the week in "dd.MM.yyyy" format
     * - MONTH: formatted as "Month Year"
     * - YEAR: formatted as "Year"
     *
     * @param currentDate the date from which the period is calculated
     * @return a formatted string representing the time period
     */
    public String getDisplayText(LocalDate currentDate) {
        String displayText;
        switch (this) {
            case DAY:
                displayText = currentDate.format(DateTimeFormatter.ofPattern(Pattern.DATE_PATTERN));
                break;
            case WEEK:
                LocalDate startOfWeek = getStartDate(currentDate);
                LocalDate endOfWeek = getEndDate(currentDate);
                displayText = startOfWeek.format(DateTimeFormatter.ofPattern(Pattern.DATE_PATTERN)) +
                        " - " + endOfWeek.format(DateTimeFormatter.ofPattern(Pattern.DATE_PATTERN));
                break;
            case MONTH:
                displayText = currentDate.getMonth().toString() + " " + currentDate.getYear();
                break;
            case YEAR:
                displayText = String.valueOf(currentDate.getYear());
                break;
            default:
                displayText = "";
                break;
        }
        return displayText;
    }
}