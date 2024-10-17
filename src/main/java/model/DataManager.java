package model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the storage and retrieval of application data such as expenses and categories from JSON files.
 * Utilizes Jackson for JSON serialization and deserialization.
 */
public class DataManager {
    /** Base path for storing application data. This path is determined dynamically at runtime based on the user's directory. */
    private static String basePath = System.getProperty("user.dir") + "/data/";
    /** Logger for this class to log important system events, potential errors, and other significant actions performed during data operations. */
    private static final Logger logger = Logger.getLogger(DataManager.class.getName());
    /** ObjectMapper instance for handling JSON serialization and deserialization. */
    private static final ObjectMapper mapper = createObjectMapper();

    static {
        ensureDirectoryExists();
    }

    /**
     * Private constructor to prevent instantiation.
     * Throws an UnsupportedOperationException if instantiation is attempted.
     */
    private DataManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Ensures the base directory exists for data storage.
     */
    private static void ensureDirectoryExists() {
        new File(basePath).mkdirs();
    }

    /**
     * Sets the base path for saving and loading data files, ensuring the directory exists.
     *
     * @param path the base path to set
     */
    public static void setBasePath(String path) {
        basePath = path;
        ensureDirectoryExists(); // Make sure the new directory exists
    }

    /**
     * Creates and configures an ObjectMapper for JSON serialization and deserialization.
     * Includes support for Java 8 Date and Time API and custom serializers for JavaFX Color.
     *
     * @return the configured ObjectMapper
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());

        SimpleModule colorModule = new SimpleModule();
        colorModule.addSerializer(Color.class, new ColorSerializer());
        colorModule.addDeserializer(Color.class, new ColorDeserializer());
        mapper.registerModule(colorModule);

        return mapper;
    }

    /**
     * Saves a list of expenses to a JSON file.
     *
     * @param expenses the list of expenses to save
     * @throws IOException if an I/O error occurs
     */
    public static void saveExpenses(List<Expense> expenses) throws IOException {
        File file = new File(basePath + "expenses.json");
        if (expenses == null || expenses.isEmpty()) {
            logger.log(Level.WARNING, "No expenses to save.");
        } else {
            mapper.writeValue(file, expenses);
        }
    }

    /**
     * Loads a list of expenses from a JSON file.
     *
     * @param categories the list of categories to associate with the expenses
     * @return the loaded list of expenses, managed by an ExpenseManager
     * @throws IOException if an I/O error occurs
     */
    public static List<Expense> loadExpenses(CategoryManager categories) throws IOException {
        File file = new File(basePath + "expenses.json");
        if (!file.exists() || file.length() == 0) {
            logger.log(Level.WARNING, "No expense file found or file is empty.");
            return List.of();
        }

        List<Expense> loadedExpenses = mapper.readValue(file, TypeFactory.defaultInstance().constructCollectionType(List.class, Expense.class));
        for (Expense expense : loadedExpenses) {
            if (expense.getCategory() != null) {
                expense.setCategory(categories.getCategoryByName(expense.getCategory().getName()));
            }
        }

        return loadedExpenses;
    }

    /**
     * Saves a list of categories to a JSON file.
     *
     * @param categories the list of categories to save
     * @throws IOException if an I/O error occurs
     */
    public static void saveCategories(List<Category> categories) throws IOException {
        File file = new File(basePath + "categories.json");
        if (categories == null || categories.isEmpty()) {
            logger.log(Level.WARNING, "No categories to save.");
        } else {
            mapper.writeValue(file, categories);
        }
    }

    /**
     * Loads a list of categories from a JSON file.
     *
     * @return the loaded list of categories
     * @throws IOException if the file does not exist, is empty, or an error occurs during read
     */
    public static List<Category> loadCategories() throws IOException {
        File file = new File(basePath + "categories.json");
        if (!file.exists() || file.length() == 0) {
            logger.log(Level.WARNING, "No category file found or file is empty.");
            return List.of();
        }

        return mapper.readValue(file, TypeFactory.defaultInstance().constructCollectionType(List.class, Category.class));
    }
}