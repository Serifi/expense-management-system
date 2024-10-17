package controller;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import model.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller class for handling expense creation and modification.
 */
public class ExpenseController {
    /** Logger for logging errors and information related to expense operations. */
    private static final Logger logger = Logger.getLogger(ExpenseController.class.getName());

    /** Maximum width for displayed images to ensure UI consistency. */
    private static final double IMAGE_MAX_WIDTH = 975;

    /** Maximum height for displayed images to maintain layout proportions. */
    private static final double IMAGE_MAX_HEIGHT = 256;

    /** Directory path where images related to expenses are stored. */
    public static final String IMAGE_DIRECTORY = "data/images";

    /** List of text input controls for checking filled status to enable action buttons. */
    private ObservableList<TextInputControl> textFields;

    /** The currently managed expense, either being edited or newly created. */
    private Expense expense;

    /** File reference to the selected image for the expense. */
    private File imageFile;

    /** Button to initiate the creation of a new expense. */
    @FXML private Button createB;

    /** Button to initiate the editing of an existing expense. */
    @FXML private Button editB;

    /** Container for displaying the selected image of an expense. */
    @FXML private StackPane imageP;

    /** Spinner for inputting and adjusting the monetary amount of an expense. */
    @FXML private Spinner<Double> amountTF;

    /** DatePicker for selecting the date of an expense. */
    @FXML private DatePicker dateDP;

    /** Custom text field for inputting the time of an expense with formatting and validation. */
    @FXML private CustomTextField timeTF;

    /** TextField for inputting the location where the expense occurred. */
    @FXML private TextField locationTF;

    /** ComboBox for selecting the category of an expense from a predefined list. */
    @FXML private ComboBox<String> categoryCB;

    /** TextArea for entering a description of the expense. */
    @FXML private TextArea descriptionTF;

    /**
     * Constructs an instance of ExpenseController.
     */
    public ExpenseController() {
        // Standard constructor, intentionally left empty
    }

    /**
     * Initializes the controller.
     */
    public void initialize() {
        setupAmountSpinner();
        setupDatePicker();
        setupTimeTextField();
        setupCategoryComboBox();
        setupTextFields();

        checkFields();

        if (ExpenseSession.isCurrentExpense()) {
            expense = ExpenseSession.getCurrentExpense();
            loadExpenseData();
        }
    }

    /**
     * Sets up the amount spinner.
     */
    private void setupAmountSpinner() {
        amountTF.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1.0));
        amountTF.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        amountTF.valueProperty().addListener((ObservableValue<? extends Double> obs, Double oldVal, Double newVal) -> checkFields());
    }

    /**
     * Sets up the date picker.
     */
    private void setupDatePicker() {
        dateDP.setConverter(new StringConverter<>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return LocalDate.parse(string, dateFormatter);
                } catch (Exception e) {
                    return null;
                }
            }
        });
        dateDP.setValue(LocalDate.now());
        dateDP.valueProperty().addListener((ObservableValue<? extends LocalDate> obs, LocalDate oldVal, LocalDate newVal) -> checkFields());
    }

    /**
     * Sets up the time text field.
     */
    private void setupTimeTextField() {
        timeTF.setRight(new FontIcon(Material2OutlinedMZ.TIMER));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(Pattern.TIME_PATTERN);
        String currentTime = LocalTime.now(ZoneId.systemDefault()).format(timeFormatter);
        timeTF.setText(currentTime);
        updateTextFieldStyle(currentTime, timeFormatter);
        timeTF.textProperty().addListener((ObservableValue<? extends String> obs, String oldVal, String newVal) -> {
            updateTextFieldStyle(newVal, timeFormatter);
            checkFields();
        });
    }

    /**
     * Sets up the category combo box.
     */
    private void setupCategoryComboBox() {
        List<Category> categories = CategoryManager.getInstance().getCategories();
        if (categories.isEmpty()) {
            logger.log(Level.WARNING, "No categories found.");
        }
        ObservableList<String> categoryNames = FXCollections.observableArrayList(
                categories.stream().map(Category::getName).collect(Collectors.toList())
        );
        categoryNames.sort(String::compareTo);
        categoryCB.setItems(categoryNames);
        categoryCB.getSelectionModel().select("Default");
        categoryCB.valueProperty().addListener((ObservableValue<? extends String> obs, String oldVal, String newVal) -> checkFields());
    }

    /**
     * Sets up text fields.
     */
    private void setupTextFields() {
        textFields = FXCollections.observableArrayList(locationTF, descriptionTF);
        textFields.forEach(field -> field.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> checkFields()));
    }

    /**
     * Updates the style of the time text field based on its content.
     *
     * @param newVal         The new value of the text field.
     * @param timeFormatter  The formatter for parsing time.
     */
    private void updateTextFieldStyle(String newVal, DateTimeFormatter timeFormatter) {
        if (newVal != null && !newVal.isEmpty()) {
            try {
                LocalTime.parse(newVal, timeFormatter);
                setTextFieldStyle(true);
            } catch (DateTimeParseException e) {
                setTextFieldStyle(false);
            }
        } else {
            resetTextFieldStyle();
        }
    }

    /**
     * Sets the style of the time text field based on its validity.
     *
     * @param isValid True if the time is valid, false otherwise.
     */
    private void setTextFieldStyle(boolean isValid) {
        timeTF.pseudoClassStateChanged(Styles.STATE_INTERACTIVE, isValid);
        timeTF.pseudoClassStateChanged(Styles.STATE_DANGER, !isValid);
    }

    /**
     * Resets the style of the time text field.
     */
    private void resetTextFieldStyle() {
        timeTF.pseudoClassStateChanged(Styles.STATE_INTERACTIVE, false);
        timeTF.pseudoClassStateChanged(Styles.STATE_DANGER, false);
    }

    /**
     * Opens a file chooser to select an image.
     */
    @FXML
    void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Bildauswahl");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        imageFile = fileChooser.showOpenDialog(null);
        if (imageFile != null) {
            displayImage(imageFile);
        }
    }

    /**
     * Displays the selected image in the image pane.
     *
     * @param file The image file to display.
     */
    private void displayImage(File file) {
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        fitImage(imageView);
        imageP.getChildren().setAll(imageView);
        StackPane.setAlignment(imageView, Pos.CENTER);
    }

    /**
     * Fits the image view within the given pane.
     *
     * @param imageView The image view to fit.
     */
    private void fitImage(ImageView imageView) {
        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();

        if (imageWidth / imageHeight > IMAGE_MAX_WIDTH / IMAGE_MAX_HEIGHT) {
            imageView.setFitWidth(IMAGE_MAX_WIDTH);
            imageView.setFitHeight(-1);
        } else {
            imageView.setFitHeight(IMAGE_MAX_HEIGHT);
            imageView.setFitWidth(-1);
        }
    }

    /**
     * Saves the selected image to the data folder and returns the relative path.
     *
     * @return The relative path of the saved image.
     */
    private String saveImage() {
        if (imageFile == null) {
            return null;
        }
        try {
            File destDir = new File(IMAGE_DIRECTORY);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File destFile = new File(destDir, imageFile.getName());
            Files.copy(imageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return IMAGE_DIRECTORY + "/" + imageFile.getName();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save image.", e);
            return null;
        }
    }

    /**
     * Adds 1.0 to the current amount.
     */
    @FXML
    void addOne() { addAmount(1.0); }

    /**
     * Adds 2.0 to the current amount.
     */
    @FXML
    void addTwo() { addAmount(2.0); }

    /**
     * Adds 5.0 to the current amount.
     */
    @FXML
    void addFive() { addAmount(5.0); }

    /**
     * Adds 10.0 to the current amount.
     */
    @FXML
    void addTen() { addAmount(10.0); }

    /**
     * Adds 20.0 to the current amount.
     */
    @FXML
    void addTwenty() { addAmount(20.0); }

    /**
     * Adds 50.0 to the current amount.
     */
    @FXML
    void addFifty() { addAmount(50.0); }

    /**
     * Adds 100.0 to the current amount.
     */
    @FXML
    void addHundred() { addAmount(100.0); }

    /**
     * Adds the specified amount to the spinner.
     *
     * @param amount The amount to add.
     */
    private void addAmount(double amount) {
        try {
            double currentValue = amountTF.getValue();
            amountTF.getValueFactory().setValue(currentValue + amount);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid format for amount.", e);
        }
    }

    /**
     * Removes the displayed image.
     */
    @FXML
    void removeImage() {
        imageP.getChildren().clear();
        imageFile = null;
    }

    /**
     * Navigates to the expenses view.
     */
    @FXML
    void goToExpenses() {
        NavigationController.getNavigation().goToExpenses();
    }

    /**
     * Checks if all required fields are valid and enables/disables the create button.
     */
    private void checkFields() {
        boolean allFieldsFilled = textFields.stream().noneMatch(field -> field.getText().isEmpty());
        boolean amountValid = amountTF.getValue() != null && amountTF.getValue() > 0;
        boolean timestampValid = dateDP.getValue() != null && timeTF.getText().isEmpty() || timeTF.getPseudoClassStates().contains(Styles.STATE_INTERACTIVE);

        if (createB != null) {
            createB.setDisable(!(allFieldsFilled && amountValid && timestampValid));
        }
        if (editB != null) {
            editB.setDisable(!(allFieldsFilled && amountValid && timestampValid));
        }
    }

    /**
     * Loads the data of the current expense into the input fields.
     */
    private void loadExpenseData() {
        if (expense != null) {
            dateDP.setValue(expense.getDate());
            locationTF.setText(expense.getLocation());
            amountTF.getValueFactory().setValue(expense.getAmount());
            descriptionTF.setText(expense.getDescription());
            timeTF.setText(expense.getTime() != null ? expense.getTime().toString() : "");
            categoryCB.setValue(expense.getCategory().getName());
            if (expense.getImagePath() != null) {
                imageFile = new File(expense.getImagePath());
                displayImage(imageFile);
            }
            checkFields();
        }
    }

    /**
     * Shows a dialog indicating that the limit for the category has been exceeded and asks the user if they want to proceed.
     *
     * @param category the category whose limit has been exceeded
     * @param exceededAmount the amount by which the limit is exceeded
     * @param onSuccess the action to perform if the user chooses to proceed despite the exceeded limit
     */
    private void showLimitExceededDialog(Category category, double exceededAmount, Runnable onSuccess) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Limit überschritten");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Limit der Kategorie '%s' wurde in dem Monat um " + Pattern.AMOUNT_PATTERN + " überschritten. Möchten Sie fortfahren?", category.getName(), exceededAmount));

        ButtonType yesButtonType = new ButtonType("Ja");
        ButtonType noButtonType = new ButtonType("Nein", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButtonType, noButtonType);

        DialogPane dialogPane = alert.getDialogPane();
        Button yesButton = (Button) dialogPane.lookupButton(yesButtonType);
        yesButton.setStyle("-fx-background-color: #FF6961; -fx-text-fill: #FFFFFF;");

        alert.showAndWait().ifPresent((ButtonType response) -> {
            if (response == yesButtonType) {
                onSuccess.run();
            }
        });
    }

    /**
     * Checks category spending limit before committing an expense.
     *
     *  @param category the category of the expense being added or edited
     *  @param newAmount the amount of the new expense or the difference if an existing expense is being edited
     *  @param editingExpense the expense that is being edited, null if a new expense is being added
     *  @param onSuccess a runnable that is executed if the spending does not exceed the limit
     *  @return true if the total spending including the new expense does not exceed the category's limit, false otherwise
     */
    private boolean checkCategoryLimit(Category category, double newAmount, Expense editingExpense, Runnable onSuccess) {
        if (category.getLimit() > 0) {
            YearMonth currentMonth = YearMonth.now();
            double totalSpentInCategory = ExpenseManager.getInstance().getExpenses().stream()
                    .filter((Expense exp) -> exp.getCategory().equals(category))
                    .filter((Expense exp) -> YearMonth.from(exp.getDate()).equals(currentMonth))
                    .filter((Expense exp) -> !exp.equals(editingExpense))
                    .mapToDouble(Expense::getAmount)
                    .sum();

            double totalAmount = totalSpentInCategory + newAmount;
            if (totalAmount > category.getLimit()) {
                double exceededAmount = totalAmount - category.getLimit();
                showLimitExceededDialog(category, exceededAmount, onSuccess);
                return false;
            }
        }
        return true;
    }

    /**
     * Creates an expense with the entered data.
     */
    @FXML
    void createExpense() {
        Category category = CategoryManager.getInstance().getCategoryByName(categoryCB.getValue());
        double totalAmount = amountTF.getValue();

        Runnable creationAction = () -> {
            Expense newExpense = new Expense(dateDP.getValue(), locationTF.getText(), totalAmount, descriptionTF.getText());
            newExpense.setTime(timeTF.getText().isEmpty() ? null : LocalTime.parse(timeTF.getText()));
            newExpense.setCategory(category);
            newExpense.setImagePath(saveImage());
            ExpenseManager.getInstance().addExpense(newExpense);
            NavigationController.getNavigation().goToExpenses();
        };

        if (checkCategoryLimit(category, totalAmount, null, creationAction)) {
            creationAction.run();
        }
    }

    /**
     * Modifies the expense with the entered data.
     */
    @FXML
    void editExpense() {
        if (expense != null) {
            Category selectedCategory = CategoryManager.getInstance().getCategoryByName(categoryCB.getValue());
            double newAmount = amountTF.getValue();

            Runnable updateAction = () -> {
                expense.setDate(dateDP.getValue());
                expense.setLocation(locationTF.getText());
                expense.setAmount(newAmount);
                expense.setDescription(descriptionTF.getText());
                expense.setTime(timeTF.getText().isEmpty() ? null : LocalTime.parse(timeTF.getText()));
                expense.setCategory(selectedCategory);
                expense.setImagePath(saveImage());
                finalizeExpenseUpdate();
            };

            if (checkCategoryLimit(selectedCategory, newAmount, expense, updateAction)) {
                updateAction.run();
            }
        }
    }

    /**
     * Completes the expense updating process. This method updates the modified expense in the system,
     * clears the current session, sets the expense reference to null, and redirects to the expenses overview.
     * It ensures that the expense lifecycle is cleanly managed and navigates away from the current view.
     */
    private void finalizeExpenseUpdate() {
        ExpenseManager.getInstance().updateExpense(expense);
        ExpenseSession.resetCurrentExpense();
        expense = null;
        NavigationController.getNavigation().goToExpenses();
    }

    /**
     * Deletes the expense.
     */
    @FXML
    void deleteExpense() {
        if (expense != null) {
            ExpenseManager.getInstance().removeExpense(expense);
            ExpenseSession.resetCurrentExpense();
            expense = null;
            NavigationController.getNavigation().goToExpenses();
        }
    }
}