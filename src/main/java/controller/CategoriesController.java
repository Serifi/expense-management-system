package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.*;

import java.time.LocalDate;

/**
 * Controller class for managing category-related functionalities in a JavaFX application.
 * This class handles the UI interactions related to categories, including displaying categories,
 * adding, editing, and deleting categories, and navigating through different time periods to view
 * category-specific expenses. It utilizes {@link CategoryController} for handling the dialogs and
 * forms related to category modifications.
 */
public class CategoriesController {

    /** ScrollPane component that contains the visual representation of categories. */
    @FXML private ScrollPane categoriesSP;

    /** Label that displays the currently selected period in the UI. */
    @FXML private Label currPeriodL;

    /** ComboBox for selecting predefined time periods for filtering categories based on expenses. */
    @FXML private ComboBox<TimePeriod> periodCB;

    /** DatePicker for selecting specific dates, aiding in filtering category-specific expenses. */
    @FXML private DatePicker periodDP;

    /** Manages period-related functionalities, linking period selection with expense filtering. */
    private PeriodManager periodManager;

    /** Controller for handling category creation, modification, and deletion dialogs in the application. */
    private CategoryController categoryController;

    /**
     * Constructs an instance of CategoriesController.
     */
    public CategoriesController() {
        // Standard constructor, intentionally left empty
    }

    /**
     * Initializes the controller. This method sets up the initial state of the UI components
     * and loads the categories for the current period.
     */
    @FXML
    private void initialize() {
        setupScrollPane();
        categoryController = new CategoryController(this::loadCategories);
        periodManager = new PeriodManager(periodCB, periodDP, currPeriodL, this::loadCategories);
        periodManager.initCurrentDate();
        periodCB.setValue(TimePeriod.MONTH);
        loadCategories();
    }

    /**
     * Configures the scroll pane used for displaying category cards.
     */
    private void setupScrollPane() {
        FlowPane categoriesFP = (FlowPane) categoriesSP.getContent();
        if (categoriesFP == null) {
            categoriesFP = new FlowPane();
            categoriesFP.setVgap(16);
            categoriesFP.setHgap(16);
            categoriesFP.setAlignment(Pos.TOP_LEFT);

            categoriesSP.setContent(categoriesFP);
            categoriesSP.setFitToWidth(true);
            categoriesSP.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            categoriesSP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
    }

    /**
     * Loads categories into the flow pane within the scroll pane. Clears the existing categories
     * and fetches sorted categories from the CategoryManager, adding each to the UI using a stream pipeline.
     */
    private void loadCategories() {
        FlowPane categoriesFP = (FlowPane) categoriesSP.getContent();
        categoriesFP.getChildren().clear();
        CategoryManager.getInstance().getCategories()
                .stream()
                .sorted((Category c1, Category c2) -> {
                    if (c1.getName().equals(CategoryManager.DEFAULT_CATEGORY)) {
                        return -1;
                    }
                    if (c2.getName().equals(CategoryManager.DEFAULT_CATEGORY)) {
                        return 1;
                    }
                    return c1.getName().compareToIgnoreCase(c2.getName());
                })
                .map(this::createCategoryBox)
                .forEach(categoriesFP.getChildren()::add);
    }

    /**
     * Calculates the total expenses for a given category within the currently selected time period.
     *
     * @param category The category for which expenses are calculated.
     * @return The total expenses of the category within the selected time period.
     */
    public double currentExpenses(Category category) {
        LocalDate[] periodRange = periodManager.getSelectedPeriodRange();
        LocalDate start = periodRange[0];
        LocalDate end = periodRange[1];

        return ExpenseManager.getInstance().getExpenses().stream()
                .filter(expense -> expense.getCategory() != null && expense.getCategory().equals(category))
                .filter(expense -> !expense.getDate().isBefore(start) && !expense.getDate().isAfter(end))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Creates a visual representation of a category in the form of a VBox containing category details.
     *
     * @param category The category to represent.
     * @return VBox containing the category's visual representation.
     */
    private VBox createCategoryBox(Category category) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: #" + category.getColor().toString().substring(2, 8) + ";" +
                     "-fx-background-radius: 6;" + "-fx-padding: 20;");
        StackPane container = new StackPane();
        container.getChildren().add(box);
        addCategoryDetailsToBox(box, category);

        if (!category.getName().equals(CategoryManager.DEFAULT_CATEGORY)) {
            box.setOnMouseClicked(e -> goToCategoryModification(category));
        }

        return new VBox(container);
    }

    /**
     * Adds category name, limit, and current expenses details to a given VBox.
     *
     * @param box The VBox to which details are added.
     * @param category The category whose details are being displayed.
     */
    private void addCategoryDetailsToBox(VBox box, Category category) {
        Label nameL = new Label(category.getName());
        nameL.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 20));
        nameL.setTextFill(category.getFontColor());

        double limit = category.getLimit();
        Label limitL = new Label(Math.abs(limit) < 0.001 ? "kein Limit" : "Monatslimit: " + String.format(Pattern.AMOUNT_PATTERN, limit));
        limitL.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));

        Label expensesL = new Label("Ausgaben: " + String.format(Pattern.AMOUNT_PATTERN, currentExpenses(category)));
        expensesL.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));

        box.setSpacing(0);
        VBox.setMargin(limitL, new Insets(10, 0, 0, 0));
        box.getChildren().addAll(nameL, limitL, expensesL);
    }

    /**
     * Handles the navigation to the next period for viewing categories.
     */
    @FXML
    void goToNextPeriod() {
        periodManager.changePeriod(1);
        loadCategories();
    }

    /**
     * Handles the navigation to the previous period for viewing categories.
     */
    @FXML
    void goToPrevPeriod() {
        periodManager.changePeriod(-1);
        loadCategories();
    }

    /**
     * Opens the dialog for creating a new category.
     */
    @FXML
    void goToCategoryCreation() {
        categoryController.goToCategory(null);
    }

    /**
     * Opens the dialog for editing an existing category.
     *
     * @param category The category to edit.
     */
    private void goToCategoryModification(Category category) {
        categoryController.goToCategory(category);
    }
}