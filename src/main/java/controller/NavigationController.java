package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import model.ExpenseSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static model.MoneyApp.loadFXML;

/**
 * The {@code NavigationController} class is responsible for managing navigation between different views in the application.
 * It initializes and handles the interaction between UI components like buttons and the main content pane where different
 * FXML views are loaded.
 *
 * @author The only one who maintains and knows this bit of the code
 */
public class NavigationController implements Initializable {
    /** Button for navigating to the categories view. */
    @FXML private Button categoriesB;

    /** Button for navigating to the expenses view. */
    @FXML private Button expensesB;

    /** Button for navigating to the visualization view. */
    @FXML private Button visualizationB;

    /** Main container used for displaying different views as the user navigates through the application. */
    @FXML private BorderPane mainBP;

    /** Singleton instance of NavigationController to ensure consistent navigation state across the application. */
    private static NavigationController navigation;

    /** Base path for loading the view files. Used as a prefix when changing scenes. */
    private static final String BASE_PATH = "../view/";

    /** Style to apply to a navigation button when it is active. */
    private static final String ACTIVE_BUTTON_STYLE = "-fx-text-fill: #1769AA; -fx-background-color: #EFF6FF";

    /** Default background color style for navigation buttons when they are not active. */
    private static final String DEFAULT_BACKGROUND_COLOR = "-fx-background-color: #F8F8F8";

    /** Logger for this class to log navigation events and potential errors. */
    private static final Logger logger = Logger.getLogger(NavigationController.class.getName());

    /**
     * Constructs a new {@code NavigationController} and assigns it to the static navigation reference for later retrieval.
     */
    public NavigationController() {
        navigation = this;
    }

    /**
     * Retrieves the single instance of {@code NavigationController}.
     *
     * @return the current instance of {@code NavigationController}
     */
    public static NavigationController getNavigation() {
        return navigation;
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML fields are populated.
     * On initialization, it navigates to the Expenses view by default.
     *
     * @param url the location used to resolve relative paths for the root object, or null if unknown
     * @param resourceBundle the resources used to localize the root object, or null if not provided
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        goToExpenses();
    }

    /**
     * Handles navigation to a specified view and updates the UI to reflect the current active view.
     *
     * @param fxmlPath the relative path to the FXML file that should be loaded into the center of the main border pane
     * @param activeButton the button corresponding to the current view, which will be styled to indicate it is active
     */
    private void navigateTo(String fxmlPath, Button activeButton) {
        try {
            mainBP.setCenter(loadFXML(BASE_PATH + fxmlPath));
            resetButtonStyles();
            if (activeButton != null) {
                activeButton.setStyle(ACTIVE_BUTTON_STYLE);
            }
            mainBP.setStyle(DEFAULT_BACKGROUND_COLOR);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load the view: " + BASE_PATH + fxmlPath, e);
        }
    }

    /**
     * Resets the styles of all navigation buttons to their default state.
     */
    private void resetButtonStyles() {
        categoriesB.setStyle("");
        expensesB.setStyle("");
        visualizationB.setStyle("");
    }

    /**
     * Navigation method to go to the Expenses view.
     */
    @FXML
    public void goToExpenses() {
        navigateTo("expenses", expensesB);
        ExpenseSession.resetCurrentExpense();
    }

    /**
     * Navigates to the Expense Creation view.
     */
    public void goToExpenseCreation() {
        navigateTo("expense-creation", expensesB);
    }

    /**
     * Navigates to the Expense Modification view.
     */
    public void goToExpenseModification() {
        navigateTo("expense-modification", expensesB);
    }

    /**
     * Navigates to the Categories view.
     */
    @FXML
    public void goToCategories() {
        navigateTo("categories", categoriesB);
    }

    /**
     * Navigates to the Visualization view.
     */
    @FXML
    public void goToVisualization() {
        navigateTo("visualization", visualizationB);
    }
}