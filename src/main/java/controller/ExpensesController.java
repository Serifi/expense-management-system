package controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import model.*;
import org.controlsfx.control.CheckComboBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Controller for managing expenses in a JavaFX application.
 * This controller handles UI interactions, updates views based on model data, and reacts to user inputs.
 */
public class ExpensesController {

    // Header components used for filtering and managing views
    /** TextField for inputting search criteria. */
    @FXML private TextField searchTF;

    /** ComboBox with checkboxes for selecting multiple categories. */
    @FXML private CheckComboBox<Category> categoriesCB;

    /** ComboBox for selecting predefined time periods. */
    @FXML private ComboBox<TimePeriod> periodCB;

    /** DatePicker for selecting custom date ranges. */
    @FXML private DatePicker periodDP;

    /** Label displaying the currently selected period. */
    @FXML private Label currPeriodL;

    // Table components to display expense details
    /** TableView to display expenses. */
    @FXML private TableView<Expense> expenseTV;

    /** TableColumn for actions like edit/delete. */
    @FXML private TableColumn<Expense, Void> actionC;

    /** TableColumn showing the amount of each expense. */
    @FXML private TableColumn<Expense, Double> amountC;

    /** TableColumn showing the category of each expense. */
    @FXML private TableColumn<Expense, Category> categoryC;

    /** TableColumn showing the date of each expense. */
    @FXML private TableColumn<Expense, LocalDate> dateC;

    /** TableColumns for description, image, and location. */
    @FXML private TableColumn<Expense, String> descriptionC, imageC, locationC;

    // Footer components for displaying summary and control pagination
    /** Labels for displaying financial summaries. */
    @FXML private Label limitAmountL, limitL, overspentAmountL, overspentL, pageL, totalAmountL;

    /** Progress bar showing the spending relative to set limits. */
    @FXML private ProgressIndicator spentLimitBar;

    /** Manages the period selection logic. */
    private PeriodManager periodManager;

    /** Current page index in pagination. */
    private int currentPageIndex = 0;

    /** Number of items to display per page. */
    private final int itemsPerPage = 5;

    /**
     * Constructs an instance of ExpensesController.
     */
    public ExpensesController() {
        // Standard constructor, intentionally left empty
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up table columns, categories, current date, periods, listeners, and updates category spending and pagination.
     */
    @FXML
    void initialize() {
        initTableColumns();
        initCategories();
        setupListeners();
        updateCategorySpending(ExpenseManager.getInstance().getExpenses());
        updatePagination(ExpenseManager.getInstance().getExpenses());
        periodManager = new PeriodManager(periodCB, periodDP, currPeriodL, this::filterExpenses);
    }

    // Table -----------------------------------------------------------------------------------------------------------

    /**
     * Initializes the table columns by setting up their cell value factories and cell factories.
     */
    private void initTableColumns() {
        setupColumn(dateC, "date");
        setupColumn(locationC, "location");
        setupColumn(amountC, "amount");
        setupColumn(descriptionC, "description");
        setupImageColumn();
        setupCategoryColumn();
        setupActionColumn();

        expenseTV.getItems().addAll(ExpenseManager.getInstance().getExpenses());
        expenseTV.getSortOrder().add(dateC);
        updateTotalAmount(expenseTV.getItems());
    }

    /**
     * Sets up a table column with a specified property.
     *
     * @param column   the table column to set up
     * @param property the property to associate with the column
     * @param <T>      the type of the property
     */
    private <T> void setupColumn(TableColumn<Expense, T> column, String property) {
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(param -> createCenteredTextCell());
    }

    /**
     * Creates a centered text cell for a table column.
     *
     * @param <T> the type of the cell content
     * @return a centered text cell
     */
    private <T> TableCell<Expense, T> createCenteredTextCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        };
    }

    /**
     * Sets up the image column in the table.
     */
    private void setupImageColumn() {
        imageC.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageC.setCellFactory(param -> new ImageCell());
    }

    /**
     * Sets up the category column in the table.
     */
    private void setupCategoryColumn() {
        categoryC.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryC.setStyle("-fx-alignment: CENTER");
        categoryC.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : createCategoryLabel(item));
            }
        });
    }

    /**
     * Creates and returns a styled label for a given category.
     * The label is styled to show the category's name, background color, text color, and other visual properties.
     * It ensures that the category's visual representation is consistent and distinct in the table view.
     *
     * @param item The category for which the label is to be created. This category provides the text, background color, and text color for the label.
     * @return A fully styled {@link Label} object ready to be displayed in the table cell.
     */
    private Label createCategoryLabel(Category item) {
        Label label = new Label(item.getName());
        label.setAlignment(Pos.CENTER);
        label.setPrefWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(4));
        label.setBackground(new Background(new BackgroundFill(item.getColor(), new CornerRadii(4), Insets.EMPTY)));
        label.setTextFill(item.getFontColor());
        label.setStyle("-fx-font-weight: bold");
        return label;
    }

    /**
     * Sets up the action column in the table with an action icon for expense modification.
     */
    private void setupActionColumn() {
        actionC.setCellFactory(param -> new TableCell<>() {
            private final FontIcon actionIcon = new FontIcon("bi-box-arrow-up-right");
            { actionIcon.setOnMouseClicked(event -> handleActionIconClick(this, getTableView()));
              actionIcon.setCursor(Cursor.HAND); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionIcon);
            }
        });
    }

    /**
     * Handles click actions on the icon within a table cell, setting the current expense in the session based on the clicked cell's index,
     * and navigating to the expense modification view.
     *
     * @param cell the TableCell that was clicked, used to determine the position of the expense in the TableView.
     * @param tableView the TableView containing the expenses, used to retrieve the specific expense data.
     */
    private void handleActionIconClick(TableCell<?, ?> cell, TableView<Expense> tableView) {
        ExpenseSession.setCurrentExpense(tableView.getItems().get(cell.getIndex()));
        NavigationController.getNavigation().goToExpenseModification();
    }

    /**
     * Initializes the category combo box with available categories.
     */
    private void initCategories() {
        categoriesCB.getItems().addAll(CategoryManager.getInstance().getCategories());
        categoriesCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return CategoryManager.getInstance().getCategoryByName(string);
            }
        });
    }

    // Header ----------------------------------------------------------------------------------------------------------

    /**
     * Sets up listeners for the search text field, category combo box, and period combo box and date picker.
     */
    private void setupListeners() {
        searchTF.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> filterExpenses());
        categoriesCB.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends Category> change) -> filterExpenses());
    }

    /**
     * Advances to the next period based on the selected time period.
     */
    @FXML
    void goToNextPeriod() {
        periodManager.changePeriod(1);
    }

    /**
     * Reverts to the previous period based on the selected time period.
     */
    @FXML
    void goToPrevPeriod() {
        periodManager.changePeriod(-1);
    }

    /**
     * Navigates to the expense creation view.
     */
    @FXML
    void goToExpenseCreation() {
        NavigationController.getNavigation().goToExpenseCreation();
    }

    /**
     * Resets all filters, clearing the search text field, category combo box, period combo box, and date picker.
     */
    @FXML
    void resetFilter() {
        searchTF.clear();
        categoriesCB.getCheckModel().clearChecks();
        periodCB.getSelectionModel().clearSelection();
        periodCB.setPromptText("Zeitraum");
        currPeriodL.setText("Zeitraum");
        periodDP.setVisible(false);
        periodManager.initCurrentDate();
        filterExpenses();
    }

    // Footer ----------------------------------------------------------------------------------------------------------

    /**
     * Retrieves the filtered list of expenses based on the current search, category, and period filters.
     *
     * @return a list of filtered expenses
     */
    private List<Expense> filteredExpenses() {
        return ExpenseManager.getInstance().getExpenses(
                searchTF.getText(),
                categoriesCB.getCheckModel().getCheckedItems(),
                periodCB.getValue(),
                periodManager.getCurrentDate()
        );
    }

    /**
     * Filters the expenses based on the current search, category, and period filters, and updates the view accordingly.
     */
    private void filterExpenses() {
        updateTotalAmount(filteredExpenses());
        updatePagination(filteredExpenses());
        updateCategorySpending(filteredExpenses());
    }

    /**
     * Updates the total amount label with the total amount of the given expenses.
     *
     * @param expenses the list of expenses to calculate the total amount from
     */
    private void updateTotalAmount(List<Expense> expenses) {
        double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();
        totalAmountL.setText(String.format(Pattern.AMOUNT_PATTERN, totalAmount));
    }

    /**
     * Updates the category spending information based on the given expenses.
     *
     * @param expenses the list of expenses to calculate the category spending from
     */
    private void updateCategorySpending(List<Expense> expenses) {
        boolean hasVisibleElements = setInitialVisibility(categoriesCB.getCheckModel().getCheckedItems().isEmpty());

        if (!hasVisibleElements){
            return;
        }

        double totalLimit = 0.0;
        double totalSpent = 0.0;
        boolean noLimitPresent = false;

        YearMonth currentMonth = YearMonth.now();

        for (Category category : categoriesCB.getCheckModel().getCheckedItems()) {
            double limit = category.getLimit();
            if (limit < 0.001) {
                noLimitPresent = true;
                break;
            }
            totalLimit += limit;
            totalSpent += calculateTotalSpentByCategory(expenses, category, currentMonth);
        }

        if (noLimitPresent) {
            setVisibility(false);
            return;
        }

        updateSpentLimitBar(totalLimit, totalSpent);
    }

    /**
     * Calculates the total amount spent for a given category from the given expenses within the current month.
     *
     * @param expenses the list of expenses to calculate the total spent from
     * @param category the category to filter the expenses by
     * @param currentMonth the current month to filter the expenses
     * @return the total amount spent for the given category within the current month
     */
    private double calculateTotalSpentByCategory(List<Expense> expenses, Category category, YearMonth currentMonth) {
        return expenses.stream()
                .filter(expense -> expense.getCategory().equals(category))
                .filter(expense -> YearMonth.from(expense.getDate()).equals(currentMonth))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Updates the spent limit bar with the total limit and total spent amounts.
     *
     * @param totalLimit the total limit amount
     * @param totalSpent the total spent amount
     */
    private void updateSpentLimitBar(double totalLimit, double totalSpent) {
        setVisibility(true);

        limitAmountL.setText(String.format(Pattern.AMOUNT_PATTERN, totalLimit));
        if (totalSpent > totalLimit) {
            overspentAmountL.setVisible(true);
            overspentAmountL.setText(String.format(Pattern.AMOUNT_PATTERN, totalSpent - totalLimit));
        } else {
            overspentL.setVisible(false);
            overspentAmountL.setVisible(false);
        }

        if (totalLimit > 0) {
            double progress = totalSpent / totalLimit;
            spentLimitBar.setProgress(Math.min(progress, 1.0));
        }
    }

    /**
     * Sets the initial visibility of the footer components based on whether any categories are selected.
     *
     * @param isEmpty whether the category selection is empty
     * @return the visibility status
     */
    private boolean setInitialVisibility(boolean isEmpty) {
        setVisibility(!isEmpty);
        return !isEmpty;
    }

    /**
     * Sets the visibility of the footer components.
     *
     * @param isVisible whether the components should be visible
     */
    private void setVisibility(boolean isVisible) {
        spentLimitBar.setVisible(isVisible);
        overspentL.setVisible(isVisible);
        overspentAmountL.setVisible(isVisible);
        limitL.setVisible(isVisible);
        limitAmountL.setVisible(isVisible);
    }

    /**
     * Updates the pagination based on the given list of expenses.
     *
     * @param expenses the list of expenses to paginate
     */
    private void updatePagination(List<Expense> expenses) {
        int totalItems = expenses.size();
        int maxPageIndex = (int) Math.ceil((double) totalItems / itemsPerPage) - 1;
        currentPageIndex = Math.max(0, Math.min(currentPageIndex, maxPageIndex));

        int fromIndex = currentPageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);

        expenseTV.getItems().clear();
        expenseTV.getItems().addAll(expenses.subList(fromIndex, toIndex));
        updatePageLabel(fromIndex, toIndex, totalItems);
    }

    /**
     * Updates the page label with the current page information.
     *
     * @param fromIndex the start index of the current page
     * @param toIndex   the end index of the current page
     * @param totalItems the total number of items
     */
    private void updatePageLabel(int fromIndex, int toIndex, int totalItems) {
        int pageStart = fromIndex + 1;
        if (totalItems == 0) {
            pageStart = 0;
        }
        pageL.setText(pageStart + " to " + toIndex + " from " + totalItems);
    }

    /**
     * Navigates to the first page of the table.
     */
    @FXML
    void goToFirstPage() {
        currentPageIndex = 0;
        filterExpenses();
    }

    /**
     * Navigates to the previous page of the table.
     */
    @FXML
    void goToPrevPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            filterExpenses();
        }
    }

    /**
     * Navigates to the next page of the table.
     */
    @FXML
    void goToNextPage() {
        int maxPageIndex = (int) (Math.ceil((double) filteredExpenses().size() / itemsPerPage) - 1);
        if (currentPageIndex < maxPageIndex) {
            currentPageIndex++;
            updatePagination(filteredExpenses());
        }
    }

    /**
     * Navigates to the last page of the table.
     */
    @FXML
    void goToLastPage() {
        currentPageIndex = (int) (Math.ceil((double) filteredExpenses().size() / itemsPerPage) - 1);
        updatePagination(filteredExpenses());
    }

    /**
     * A custom {@link TableCell} for displaying images in a table view. This cell handles the display of
     * an image or a placeholder icon based on the existence of the image file at the specified path.
     * It ensures images are displayed with a fixed size and maintains the aspect ratio.
     */
    private static class ImageCell extends TableCell<Expense, String> {
        /** ImageView for displaying the image, configured to preserve aspect ratio and fit specific dimensions. */
        private final ImageView imageView = new ImageView();
        /** StackPane container for the ImageView or placeholder, ensuring centered and appropriate sizing. */
        private final StackPane stackPane = new StackPane();
        /** FontIcon used as a placeholder when no valid image is available, indicating absence of image. */
        private final FontIcon placeholder = new FontIcon("bi-image");
        /** Constant size for images and icons within the cell, ensuring uniformity. */
        private static final int IMAGE_SIZE = 72;

        /**
         * Constructs an ImageCell setting up the ImageView and placeholder with predefined dimensions
         * and styles. It prepares the StackPane to display the image or placeholder centered.
         */
        public ImageCell() {
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(IMAGE_SIZE);
            imageView.setFitWidth(IMAGE_SIZE);
            placeholder.setIconSize(IMAGE_SIZE);

            stackPane.setPrefSize(IMAGE_SIZE, IMAGE_SIZE);
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setStyle("-fx-background-color: #FFFFFF;");
            stackPane.getChildren().add(placeholder);
        }

        /**
         * Updates the item in the cell. This method is called automatically whenever the item in the cell changes.
         *
         * @param imagePath The path to the image file to display in the cell. If the path is empty or null, a placeholder is shown.
         * @param empty     A flag indicating whether the cell should be empty.
         */
        @Override
        protected void updateItem(String imagePath, boolean empty) {
            super.updateItem(imagePath, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                return;
            }

            setText(null);
            if (imagePath == null || imagePath.isEmpty()) {
                stackPane.getChildren().setAll(placeholder);
            } else {
                loadImage(imagePath);
            }

            setGraphic(stackPane);
        }

        /**
         * Loads an image from the specified file path and sets it in the ImageView. If the file does not exist,
         * sets the placeholder icon instead.
         *
         * @param imagePath The path of the image file to load.
         */
        private void loadImage(String imagePath) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString(), IMAGE_SIZE, IMAGE_SIZE, true, true, false);
                imageView.setImage(image);
                stackPane.getChildren().setAll(imageView);
            } else {
                stackPane.getChildren().setAll(placeholder);
            }
        }
    }
}