package model;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main JavaFX application class that manages the lifecycle and scenes of the MoneyApp.
 */
public class MoneyApp extends Application {
    /** Logger for this class to log important events, warnings, and errors. */
    private static final Logger logger = Logger.getLogger(MoneyApp.class.getName());

    /**
     * Constructs an instance of MoneyApp.
     */
    public MoneyApp() {
        // Standard constructor, intentionally left empty
    }

    /**
     * Starts the primary stage of the application, setting up the initial scene and loading resources.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set.
     * @throws IOException if there is an issue loading the necessary resources.
     */
    @Override
    public void start(Stage stage) throws IOException {
        loadData();
        setupScene(stage);
        stage.show();
    }

    /**
     * Loads application resources including expenses and categories from storage.
     *
     * @throws IOException if resources cannot be loaded.
     */
    private void loadData() throws IOException {
        CategoryManager.getInstance().setCategories(DataManager.loadCategories());
        ExpenseManager.getInstance().setExpenses(DataManager.loadExpenses(CategoryManager.getInstance()));

        if (CategoryManager.getInstance().getCategories().isEmpty() || ExpenseManager.getInstance().getExpenses().isEmpty()) {
            System.out.println("Failed to load resources.");
            Platform.exit();
        }
    }

    /**
     * Sets up the initial scene of the application.
     *
     * @param stage the primary stage to set the scene on.
     * @throws IOException if the FXML file cannot be loaded.
     */
    private void setupScene(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        stage.setScene(new Scene(loadFXML("../view/navigation"), 1280, 720));
    }

    /**
     * Loads an FXML file and returns the root node.
     *
     * @param fxml the path to the FXML file.
     * @return the loaded root node of the FXML file.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MoneyApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * The main entry point for all JavaFX applications.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called when the application should stop, and provides a convenient place to prepare for application exit and
     * save state.
     */
    @Override
    public void stop() {
        try {
            DataManager.saveExpenses(ExpenseManager.getInstance().getExpenses());
            DataManager.saveCategories(CategoryManager.getInstance().getCategories());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save expenses and category lists on application stop.", e);
        }
    }
}