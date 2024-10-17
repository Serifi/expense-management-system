package model;

/**
 * Defines common format patterns used throughout the application for consistency in displaying amounts, dates, and times.
 */
public class Pattern {
    /** Pattern for formatting monetary amounts to two decimal places. */
    public static final String AMOUNT_PATTERN = "%.2f";
    /** Date format pattern used for displaying dates in day, month, and year format. */
    public static final String DATE_PATTERN = "dd.MM.yyyy";
    /** Time format pattern for displaying time in hours and minutes. */
    public static final String TIME_PATTERN = "HH:mm";

    /**
     * Constructs a new instance of Pattern.
     */
    public Pattern() {
        // Standard constructor, intentionally left empty
    }
}