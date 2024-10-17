package model;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.time.LocalDate;

/**
 * Manages periods for filtering expenses in a user interface.
 * This class integrates with JavaFX components to allow dynamic period selection and displaying
 * for expense filtering based on selected dates or predefined periods (like day, week, month, year).
 */
public class PeriodManager {
    /** Holds the currently selected date, which can be adjusted through the UI. */
    private LocalDate currentDate;

    /** ComboBox for selecting predefined time periods such as day, week, month, or year. */
    private final ComboBox<TimePeriod> periodCB;

    /** DatePicker for selecting a specific date, providing a calendar interface for easy date navigation. */
    private final DatePicker periodDP;

    /** Label that displays the currently selected period or date in a user-friendly format. */
    private final Label currPeriodL;

    /** An instance of ExpenseFilter that is triggered to filter expenses based on the selected period and date. */
    private final ExpenseFilter expenseFilter;

    /**
     * Constructs a PeriodManager with specified UI components and an expense filter.
     * Initializes the date and period components and sets up the necessary listeners.
     *
     * @param periodCB the combo box for selecting a time period
     * @param periodDP the date picker for selecting a specific date
     * @param currPeriodL the label for displaying the current period
     * @param expenseFilter the expense filter logic to apply when period changes
     */
    public PeriodManager(ComboBox<TimePeriod> periodCB, DatePicker periodDP, Label currPeriodL, ExpenseFilter expenseFilter) {
        this.periodCB = periodCB;
        this.periodDP = periodDP;
        this.currPeriodL = currPeriodL;
        this.expenseFilter = expenseFilter;
        this.currentDate = LocalDate.now();
        initPeriods();
        setupListeners();
    }

    /**
     * Returns the current date.
     *
     * @return the current LocalDate
     */
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /**
     * Initializes the current date to today.
     */
    public void initCurrentDate() {
        this.currentDate = LocalDate.now();
    }

    /**
     * Initializes the period components, setting up their visual representation and data.
     */
    private void initPeriods() {
        periodDP.setShowWeekNumbers(false);
        periodCB.getItems().setAll(TimePeriod.values());
        periodCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(TimePeriod period) {
                return period.name();
            }

            @Override
            public TimePeriod fromString(String string) {
                return TimePeriod.valueOf(string);
            }
        });

        periodCB.setCellFactory(comboBox -> new ListCell<>() {
            @Override
            protected void updateItem(TimePeriod item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? null : item.name());
            }
        });

        periodCB.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TimePeriod item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? null : item.name());
            }
        });
    }

    /**
     * Sets up listeners for period and date selection changes to trigger appropriate actions.
     */
    private void setupListeners() {
        periodCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TimePeriod> observable, TimePeriod oldValue, TimePeriod newValue) -> {
            if (newValue != null) {
                periodDP.setVisible(true);
                updateCurrentPeriodLabel();
                expenseFilter.filter();
            }
        });

        periodDP.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            if (newValue != null) {
                currentDate = newValue;
                updateCurrentPeriodLabel();
                expenseFilter.filter();
            }
        });
    }

    /**
     * Changes the current period based on the specified amount to move forward or backward.
     *
     * @param amount the number of periods to move (positive for future, negative for past)
     */
    public void changePeriod(int amount) {
        TimePeriod selectedPeriod = periodCB.getValue();
        if (selectedPeriod != null) {
            currentDate = switch (selectedPeriod) {
                case DAY -> currentDate.plusDays(amount);
                case WEEK -> currentDate.plusWeeks(amount);
                case MONTH -> currentDate.plusMonths(amount);
                case YEAR -> currentDate.plusYears(amount);
            };
            expenseFilter.filter();
            updateCurrentPeriodLabel();
        }
    }

    /**
     * Updates the label to show the current period's display text.
     */
    private void updateCurrentPeriodLabel() {
        TimePeriod selectedPeriod = periodCB.getValue();
        if (selectedPeriod != null) {
            currPeriodL.setText(selectedPeriod.getDisplayText(currentDate));
        }
    }

    /**
     * Gets the range of dates for the selected period.
     *
     * @return an array of two LocalDate objects representing the start and end dates of the selected period
     */
    public LocalDate[] getSelectedPeriodRange() {
        TimePeriod selectedPeriod = periodCB.getValue();
        if (selectedPeriod == null) {
            selectedPeriod = TimePeriod.DAY;
        }
        LocalDate startDate = selectedPeriod.getStartDate(currentDate);
        LocalDate endDate = selectedPeriod.getEndDate(currentDate);
        return new LocalDate[]{startDate, endDate};
    }
}