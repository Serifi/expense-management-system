package controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import model.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class responsible for managing and updating visualizations in the application,
 * including pie charts and bar charts that display expense data for selected time periods.
 * This class supports changing visualization types and navigating through different periods
 * to reflect the appropriate data in the charts.
 */
public class VisualizationController {
    /** Represents pie chart option */
    private static final String PIE_CHART = "Kreisdiagramm";

    /** Represents bar chart option */
    private static final String BAR_CHART = "Balkendiagramm"; //

    /** The pie chart component for displaying category-based expense distribution. */
    @FXML private PieChart pieChart;

    /** The bar chart component for displaying expenses by category over time. */
    @FXML private BarChart<String, Number> barChart;

    /** DatePicker component for selecting the specific period for data visualization. */
    @FXML private DatePicker periodDP;

    /** ComboBox for selecting predefined time periods for the charts. */
    @FXML private ComboBox<TimePeriod> periodCB;

    /** ComboBox for selecting between different types of charts. */
    @FXML private ComboBox<String> chartCB;

    /** Label to display the currently selected period. */
    @FXML private Label currPeriodL;

    /** Label to display the total expenses for the selected period. */
    @FXML private Label totalExpensesLabel;

    /** Manages the period data for the charts. */
    private PeriodManager periodManager;

    /**
     * Constructs an instance of VisualizationController.
     */
    public VisualizationController() {
        // Standard constructor, intentionally left empty
    }

    /**
     * Initializes the controller. This method sets up initial state and behaviors for the combo boxes,
     * date picker, and charts. It also binds the period manager to UI components and registers
     * necessary event listeners.
     */
    @FXML
    public void initialize() {
        initPeriodCB();
        initChartCB();
        updateChartsForSelectedPeriod();
    }

    /**
     * Initializes the period combo box and sets the initial period selection to YEAR.
     */
    private void initPeriodCB() {
        periodManager = new PeriodManager(periodCB, periodDP, currPeriodL, this::updateChartsForSelectedPeriod);
        periodCB.getSelectionModel().select(TimePeriod.YEAR);
    }

    /**
     * Initializes the chart type combo box with options for pie and bar charts.
     * It sets up listeners to handle changes in chart selection.
     */
    private void initChartCB() {
        chartCB.getItems().addAll(PIE_CHART, BAR_CHART);
        chartCB.getSelectionModel().select(PIE_CHART);
        chartCB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            onChartTypeChanged();
            updateChartsForSelectedPeriod();
        });
    }

    /**
     * Handles the change of chart type by updating UI components visibility
     * based on the selected chart type.
     */
    @FXML
    private void onChartTypeChanged() {
        if (PIE_CHART.equals(chartCB.getValue())) {
            pieChart.setVisible(true);
            barChart.setVisible(false);
        } else if (BAR_CHART.equals(chartCB.getValue())) {
            pieChart.setVisible(false);
            barChart.setVisible(true);
        }
    }

    /**
     * Advances the current viewing period to the next period.
     */
    @FXML
    private void goToNextPeriod() {
        periodManager.changePeriod(1);
        updateChartsForSelectedPeriod();
    }

    /**
     * Moves the current viewing period to the previous period.
     */
    @FXML
    private void goToPrevPeriod() {
        periodManager.changePeriod(-1);
        updateChartsForSelectedPeriod();
    }

    /**
     * Updates the data displayed in the charts based on the selected period and chart type.
     */
    private void updateChartsForSelectedPeriod() {
        if (PIE_CHART.equals(chartCB.getValue())) {
            updatePieChartData();
        } else if (BAR_CHART.equals(chartCB.getValue())) {
            updateBarChartData();
        }
    }

    /**
     * Refreshes the pie chart display with updated data. This method is executed on the JavaFX
     * application thread and handles all steps from data retrieval to UI updates, including
     * clearing old data, fetching new totals, and updating the chart and total expenses label.
     */
    private void updatePieChartData() {
        Platform.runLater(() -> {
            pieChart.getData().clear();
            LocalDate[] period = periodManager.getSelectedPeriodRange();
            Map<String, Double> categoryTotals = calculateCategoryTotalsForPeriod(ExpenseManager.getInstance().getExpenses(), period[0], period[1]);
            ObservableList<PieChart.Data> pieChartData = createPieChart(categoryTotals);
            pieChart.setData(pieChartData);
            pieChart.setLabelsVisible(true);
            totalExpensesLabel.setText(String.format(Pattern.AMOUNT_PATTERN, calculateTotalExpenses(categoryTotals)));
        });
    }

    /**
     * Generates observable list of PieChart.Data from category expense totals.
     *
     * @param categoryTotals Map with category names as keys and their corresponding expense totals as values.
     * @return ObservableList of PieChart.Data, ready for display.
     */
    private ObservableList<PieChart.Data> createPieChart(Map<String, Double> categoryTotals) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryTotals.forEach((key, value) ->
                pieChartData.add(new PieChart.Data(key + ": " + String.format(Pattern.AMOUNT_PATTERN, value), value))
        );
        return pieChartData;
    }

    /**
     * Calculates the total expenses from a map of category totals.
     *
     * @param categoryTotals Map where each key is a category and each value is the total expense for that category.
     * @return Total expenses as a double.
     */
    private double calculateTotalExpenses(Map<String, Double> categoryTotals) {
        return categoryTotals.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Updates and displays bar chart data for the currently selected period.
     */
    private void updateBarChartData() {
        Platform.runLater(() -> {
            barChart.getData().clear();
            LocalDate[] period = periodManager.getSelectedPeriodRange();
            Map<String, Double> categoryTotals = calculateCategoryTotalsForPeriod(ExpenseManager.getInstance().getExpenses(), period[0], period[1]);
            createBarChart(categoryTotals);
            totalExpensesLabel.setText(String.format(Pattern.AMOUNT_PATTERN, calculateTotalExpenses(categoryTotals)));
        });
    }

    /**
     * Populates the bar chart with data from category totals.
     *
     * @param categoryTotals Map with category names and their corresponding expense totals.
     */
    private void createBarChart(Map<String, Double> categoryTotals) {
        categoryTotals.forEach((String category, Double total) -> {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(category, total));
            series.setName(category);
            barChart.getData().add(series);
        });
    }

    /**
     * Calculates the total expenses per category for a given time period.
     *
     * @param expenses List of expenses to calculate totals from.
     * @param start Start date of the period.
     * @param end End date of the period.
     * @return A map of category names to their total expenses.
     */
    private Map<String, Double> calculateCategoryTotalsForPeriod(List<Expense> expenses, LocalDate start, LocalDate end) {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            LocalDate expenseDate = expense.getDate();
            if ((expenseDate.isEqual(start) || expenseDate.isAfter(start)) && (expenseDate.isEqual(end) || expenseDate.isBefore(end))) {
                String categoryName = expense.getCategory().getName();
                categoryTotals.merge(categoryName, expense.getAmount(), Double::sum);
            }
        }
        return categoryTotals;
    }
}