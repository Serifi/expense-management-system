package controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Category;
import model.CategoryManager;

import java.util.logging.Logger;

/**
 * Manages the user interface interactions specific to creating, updating, and deleting categories.
 * This controller is responsible for handling the dialogs and forms associated with category management,
 * separating the UI logic from the main category display and navigation functionalities handled by
 * CategoriesController. This separation enhances modularity and makes the system easier to manage and scale.
 */
public class CategoryController {
    /** Logger used for logging information and errors related to category management operations. */
    private static final Logger logger = Logger.getLogger(CategoriesController.class.getName());

    /** Callback to refresh the category display in CategoriesController. */
    private final Runnable loadCategoriesCallback;

    /**
     * Constructs a CategoryController with a callback to update the category list.
     * @param loadCategoriesCallback a {@link Runnable} that invokes loadCategories from {@link CategoriesController}
     * to refresh the category display upon making any changes.
     */
    public CategoryController(Runnable loadCategoriesCallback) {
        this.loadCategoriesCallback = loadCategoriesCallback;
    }

    /**
     * Handles category creation or modification dialog functionalities.
     *
     * @param category The category to modify, or null if creating a new category.
     */
    protected void goToCategory(Category category) {
        final int INPUT_WIDTH = 850;
        final int GRID_GAP = 10;

        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle(category == null ? "Kategorie hinzufügen" : "Kategorie bearbeiten");

        ButtonType addButtonType = new ButtonType("Kategorie hinzufügen", ButtonBar.ButtonData.OK_DONE);
        ButtonType editButtonType = new ButtonType("Kategorie aktualisieren", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteButtonType = new ButtonType("Kategorie löschen", ButtonBar.ButtonData.OTHER);
        ButtonType cancelButtonType = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);

        if (category == null){
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, cancelButtonType);
            Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
            addButton.setStyle("-fx-text-fill: white; -fx-background-color: #2563EB;");
            addButton.setDisable(true);
        } else {
            dialog.getDialogPane().getButtonTypes().addAll(editButtonType, deleteButtonType, cancelButtonType);
            Node editButton = dialog.getDialogPane().lookupButton(editButtonType);
            editButton.setStyle("-fx-text-fill: white; -fx-background-color: #2563EB;");
            Node deleteButton = dialog.getDialogPane().lookupButton(deleteButtonType);
            deleteButton.setStyle("-fx-text-fill: white; -fx-background-color: #FF4747;");
        }

        GridPane grid = new GridPane();
        grid.setHgap(GRID_GAP);
        grid.setVgap(GRID_GAP);
        grid.setPadding(new Insets(GRID_GAP));

        TextField nameField = createTextField("Name", "Text einfügen...", grid, 0);
        nameField.setPrefWidth(INPUT_WIDTH);

        TextField limitField = createTextField("Limit", "kein Limit!", grid, 2);
        limitField.setPrefWidth(INPUT_WIDTH);

        ColorPicker colorPicker = createColorPicker(grid);
        colorPicker.setPrefWidth(INPUT_WIDTH);

        if (category != null) {
            nameField.setText(category.getName());
            limitField.setText(String.valueOf(category.getLimit()));
            colorPicker.setValue(category.getColor());
        }

        Label errorLabel = createErrorLabel(grid);

        nameField.textProperty().addListener((ObservableValue<? extends String> obs, String oldVal, String newVal) ->
                validateForm(addButtonType, editButtonType, nameField, limitField, errorLabel, dialog, category));
        limitField.textProperty().addListener((ObservableValue<? extends String> obs, String oldVal, String newVal) ->
                validateForm(addButtonType, editButtonType, nameField, limitField, errorLabel, dialog, category));

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(nameField::requestFocus);

        if (category == null) {
            dialog.setResultConverter((ButtonType dialogButton) -> {
                if (dialogButton == addButtonType){
                    return createCategoryFromFields(nameField, colorPicker, limitField);
                }
                return null;
            });

            dialog.showAndWait().ifPresent((Category cat) -> {
                CategoryManager.getInstance().addCategory(cat);
                loadCategoriesCallback.run();
            });
        } else {
            dialog.setResultConverter((ButtonType dialogButton) -> {
                if (dialogButton == editButtonType) {
                    return createCategoryFromFields(nameField, colorPicker, limitField);
                } else if (dialogButton == deleteButtonType) {
                    CategoryManager.getInstance().removeCategory(category);
                    loadCategoriesCallback.run();
                    return null;
                }
                return null;
            });

            dialog.showAndWait().ifPresent((Category result) -> {
                CategoryManager.getInstance().updateCategory(category.getName(), result);
                loadCategoriesCallback.run();
            });
        }
    }

    /**
     * Creates and adds a TextField to the specified row of the given GridPane with a label.
     *
     * @param label The text to display as the label for the TextField.
     * @param prompt The placeholder text to display when the TextField is empty.
     * @param grid The GridPane to which the TextField and its label will be added.
     * @param row The row index in the GridPane where the label and TextField will be placed.
     * @return The created TextField.
     */
    private TextField createTextField(String label, String prompt, GridPane grid, int row) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("System", FontWeight.NORMAL, 12));
        grid.add(labelNode, 0, row);
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        grid.add(textField, 0, row + 1);
        return textField;
    }

    /**
     * Creates and adds a ColorPicker to the GridPane at a predefined position.
     *
     * @param grid The GridPane where the ColorPicker will be added.
     * @return The created ColorPicker.
     */
    private ColorPicker createColorPicker(GridPane grid) {
        Label label = new Label("Farbe");
        label.setFont(Font.font("System", FontWeight.NORMAL, 12));
        grid.add(label, 0, 4);
        ColorPicker colorPicker = new ColorPicker();
        grid.add(colorPicker, 0, 5);
        return colorPicker;
    }

    /**
     * Creates a label for displaying error messages and adds it to a specific position in the GridPane.
     *
     * @param grid The GridPane where the error label will be added.
     * @return The created error Label configured for displaying error messages.
     */
    private Label createErrorLabel(GridPane grid) {
        Label label = new Label();
        label.setTextFill(Color.web("#FF4747"));
        grid.add(label, 0, 6, 2, 1);
        return label;
    }

    /**
     * Validates the form inputs from text fields and updates the UI accordingly.
     * It also sets the error message and disables the button if the validation fails.
     *
     * @param addButton  The ButtonType associated with the add button in the dialog.
     * @param editButton The ButtonType associated with the edit button in the dialog.
     * @param nameField  The TextField for the category name.
     * @param limitField The TextField for the category limit.
     * @param errorLabel The Label to display error messages.
     * @param dialog     The Dialog containing the form.
     * @param category   The category to modify, or null if creating a new category.
     */
    private void validateForm(ButtonType addButton, ButtonType editButton, TextField nameField, TextField limitField, Label errorLabel, Dialog<Category> dialog, Category category) {
        boolean nameIsNotEmpty = !nameField.getText().trim().isEmpty();
        Category existingCategory = CategoryManager.getInstance().getCategoryByName(nameField.getText());
        boolean nameIsUnique = existingCategory == null || existingCategory.equals(category);
        boolean limitIsValid = limitField.getText().isEmpty() || limitField.getText().matches("^\\d+(\\.\\d+)?$");

        errorLabel.setText(getErrorMessage(nameIsNotEmpty, nameIsUnique, limitIsValid, !limitField.getText().isEmpty()));
        updateButtonStates(dialog, addButton, editButton, nameIsNotEmpty, nameIsUnique, limitIsValid, !limitField.getText().isEmpty());
    }

    /**
     * Updates the enabled/disabled states of the buttons in the dialog based on the individual validation results of form fields.
     *
     * @param dialog The dialog containing the buttons.
     * @param addButton The button type used to add a new category.
     * @param editButton The button type used to edit an existing category.
     * @param nameIsNotEmpty Boolean indicating whether the name field is not empty.
     * @param nameIsUnique Boolean indicating whether the name is unique within the application's context.
     * @param limitIsValid Boolean indicating whether the limit is a valid number.
     * @param limitIsProvided Boolean indicating whether a limit has been provided in the limit field.
     */
    private void updateButtonStates(Dialog<Category> dialog, ButtonType addButton, ButtonType editButton,
                                    boolean nameIsNotEmpty, boolean nameIsUnique, boolean limitIsValid, boolean limitIsProvided) {
        boolean enableButtons = nameIsNotEmpty && nameIsUnique && (limitIsValid || !limitIsProvided);
        Node addNode = dialog.getDialogPane().lookupButton(addButton);
        if (addNode != null) {
            addNode.setDisable(!enableButtons);
        }
        Node editNode = dialog.getDialogPane().lookupButton(editButton);
        if (editNode != null) {
            editNode.setDisable(!enableButtons);
        }
    }

    /**
     * Creates a new Category object from the values entered in the form fields.
     *
     * @param nameField The TextField containing the category's name.
     * @param colorPicker The ColorPicker containing the category's color.
     * @param limitField The TextField containing the category's limit value.
     * @return A new Category object or null if an input is invalid.
     */
    private Category createCategoryFromFields(TextField nameField, ColorPicker colorPicker, TextField limitField) {
        try {
            String name = nameField.getText();
            Color color = colorPicker.getValue();
            double limit = Double.parseDouble(limitField.getText().isEmpty() ? "0" : limitField.getText());
            Category category = new Category(name, color);
            category.setLimit(limit);
            return category;
        } catch (NumberFormatException e) {
            logger.warning("Limit must be a valid number.");
            return null;
        }
    }

    /**
     * Constructs an error message based on form validation states.
     *
     * @param nameIsNotEmpty Indicates whether the name field is not empty.
     * @param nameIsUnique Indicates whether the name is unique among categories.
     * @param limitIsValid Indicates whether the limit value is valid (either empty or a valid number).
     * @param limitNonEmpty Indicates whether the limit field is not empty.
     * @return A string containing the error message.
     */
    private String getErrorMessage(boolean nameIsNotEmpty, boolean nameIsUnique, boolean limitIsValid, boolean limitNonEmpty) {
        StringBuilder errorMessage = new StringBuilder();
        if (nameIsNotEmpty && !nameIsUnique) {
            errorMessage.append("Name ist bereits vergeben. ");
        }
        if (!limitIsValid && limitNonEmpty) {
            errorMessage.append("Limit muss leer (kein Limit) oder eine positive Zahl sein. ");
        }
        return errorMessage.toString();
    }
}
