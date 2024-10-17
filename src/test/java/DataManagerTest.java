import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.scene.paint.Color;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DataManager}.
 */
class DataManagerTest {
    /** Temporarily stores files or directories created during tests, automatically cleaned up after tests are completed. */
    @TempDir
    Path tempDir;

    /**
     * Constructs a new instance of DataManagerTest.
     */
    public DataManagerTest() {
    }

    /** Handles serialization and deserialization of objects to and from JSON format for data processing. */
    private ObjectMapper objectMapper;

    /**
     * Sets up the test environment.
     * Initializes the {@link ObjectMapper} with custom serializers/deserializers
     * and sets the base path for data storage to a temporary directory.
     */
    @BeforeEach
    void setUp() {
        DataManager.setBasePath(tempDir.toString() + "/data/");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        SimpleModule colorModule = new SimpleModule();
        colorModule.addSerializer(Color.class, new ColorSerializer());
        colorModule.addDeserializer(Color.class, new ColorDeserializer());
        objectMapper.registerModule(colorModule);
    }

    /**
     * Tests that the {@link DataManager} private constructor throws an
     * {@link UnsupportedOperationException} when accessed via reflection.
     */
    @Test
    void testPrivateConstructor() {
        Constructor<DataManager> constructor = null;
        try {
            constructor = DataManager.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Expected UnsupportedOperationException");
        } catch (NoSuchMethodException | IllegalAccessException e) {
            fail("Reflection failed: " + e.getMessage());
        } catch (InvocationTargetException e) {
            assertInstanceOf(UnsupportedOperationException.class, e.getCause(), "Expected UnsupportedOperationException");
        } catch (InstantiationException e) {
            fail("InstantiationException should not be thrown: " + e.getMessage());
        } finally {
            if (constructor != null) {
                constructor.setAccessible(false);
            }
        }
    }

    /**
     * Tests the {@link DataManager#saveCategories(List)} method with a null list.
     * Ensures that no exceptions are thrown.
     */
    @Test
    void testSaveCategoriesWithNullList() {
        assertDoesNotThrow(() -> DataManager.saveCategories(null));
    }

    /**
     * Tests the {@link DataManager#saveCategories(List)} method with an empty list.
     * Ensures that no exceptions are thrown.
     */
    @Test
    void testSaveCategoriesWithEmptyList() {
        assertDoesNotThrow(() -> DataManager.saveCategories(new ArrayList<>()));
    }

    /**
     * Tests the {@link DataManager#saveCategories(List)} method.
     * Verifies that categories can be saved to a file and loaded correctly.
     * @throws IOException if an I/O error occurs when writing to the file
     */
    @Test
    void testSaveCategories() throws IOException {
        List<Category> categories = List.of(new Category("Test Category", Color.RED));

        DataManager.saveCategories(categories);

        File file = new File(tempDir.toString() + "/data/categories.json");
        assertTrue(file.exists(), "The categories.json file should exist");

        List<Category> loadedCategories = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Category.class));
        assertEquals(1, loadedCategories.size(), "There should be one category in the file");
        Category loadedCategory = loadedCategories.get(0);
        assertEquals("Test Category", loadedCategory.getName(), "The name of the category should be 'Test Category'");
        assertEquals(Color.RED, loadedCategory.getColor(), "The color of the category should be RED");
    }

    /**
     * Tests the {@link DataManager#loadCategories()} method when no file exists.
     * Ensures that the returned list of categories is empty.
     * @throws IOException if an I/O error occurs during file access
     */
    @Test
    void testLoadCategoriesWithNoFile() throws IOException {
        List<Category> categories = DataManager.loadCategories();

        assertTrue(categories.isEmpty(), "The categories list should be empty if no file exists");
    }

    /**
     * Tests the {@link DataManager#loadCategories()} method when the file is empty.
     * Ensures that the returned list of categories is empty.
     * @throws IOException if an I/O error occurs when reading from the file
     */
    @Test
    void testLoadCategoriesWithEmptyFile() throws IOException {
        assertTrue(DataManager.loadCategories().isEmpty(), "The categories list should be empty if the file is empty");
    }

    /**
     * Tests the {@link DataManager#loadCategories()} method.
     * Verifies that categories can be loaded from a file correctly.
     * @throws IOException if an I/O error occurs during file read operations
     */
    @Test
    void testLoadCategories() throws IOException {
        List<Category> categories = List.of(new Category("Test Category", Color.RED));
        File file = new File(tempDir.toString() + "/data/categories.json");
        objectMapper.writeValue(file, categories);

        List<Category> loadedCategories = DataManager.loadCategories();

        assertEquals(1, loadedCategories.size(), "There should be one category in the loaded list");
        Category loadedCategory = loadedCategories.get(0);
        assertEquals("Test Category", loadedCategory.getName(), "The name of the loaded category should be 'Test Category'");
        assertEquals(Color.RED, loadedCategory.getColor(), "The color of the loaded category should be RED");
    }

    /**
     * Tests the {@link DataManager#saveExpenses(List)} method with a null list.
     * Ensures that only IOException is thrown.
     */
    @Test
    void testSaveExpensesWithNullList() {
        assertDoesNotThrow(() -> DataManager.saveExpenses(null));
    }

    /**
     * Tests the {@link DataManager#saveExpenses(List)} method with an empty list.
     * Ensures that only IOException is thrown.
     */
    @Test
    void testSaveExpensesWithEmptyList() {
        assertDoesNotThrow(() -> DataManager.saveExpenses(new ArrayList<>()));
    }

    /**
     * Tests the {@link DataManager#saveExpenses(List)} method.
     * Verifies that expenses can be saved to a file and loaded correctly.
     * @throws IOException if an I/O error occurs during data storage or retrieval
     */
    @Test
    void testSaveExpenses() throws IOException {
        Category testCategory = new Category("Test Category", Color.RED);
        Expense expense1 = new Expense(LocalDate.now(), "Test Location", 100.0, "Test Description");
        expense1.setCategory(testCategory);
        List<Expense> expenses = List.of(expense1);

        DataManager.saveExpenses(expenses);

        File file = new File(tempDir.toString() + "/data/expenses.json");
        assertTrue(file.exists(), "The expenses.json file should exist");

        List<Expense> loadedExpenses = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        assertEquals(1, loadedExpenses.size(), "There should be one expense in the file");
        Expense loadedExpense = loadedExpenses.get(0);
        assertEquals("Test Location", loadedExpense.getLocation(), "The location of the expense should be 'Test Location'");
        assertEquals(100.0, loadedExpense.getAmount(), "The amount of the expense should be 100.0");
        assertEquals("Test Description", loadedExpense.getDescription(), "The description of the expense should be 'Test Description'");
        assertNotNull(loadedExpense.getCategory(), "The category of the expense should not be null");
        assertEquals("Test Category", loadedExpense.getCategory().getName(), "The category name should be 'Test Category'");
    }

    /**
     * Tests the {@link DataManager#loadExpenses(CategoryManager)} method when no file exists.
     * Ensures that the returned list of expenses is empty.
     * @throws IOException if an I/O error occurs when accessing the storage medium
     */
    @Test
    void testLoadExpensesWithNoFile() throws IOException {
        assertTrue(DataManager.loadExpenses(CategoryManager.getInstance()).isEmpty(), "The expenses list should be empty if no file exists");
    }

    /**
     * Tests the {@link DataManager#loadExpenses(CategoryManager)} method when the file is empty.
     * Ensures that the returned list of expenses is empty.
     * @throws IOException if an I/O error occurs during file read operations
     */
    @Test
    void testLoadExpensesWithEmptyFile() throws IOException {
        assertTrue(DataManager.loadExpenses(CategoryManager.getInstance()).isEmpty(), "The expenses list should be empty if the file is empty");
    }

    /**
     * Tests the {@link DataManager#loadExpenses(CategoryManager)} method.
     * Verifies that expenses can be loaded from a file correctly.
     * @throws IOException if an I/O error occurs while reading the file
     */
    @Test
    void testLoadExpenses() throws IOException {
        Category testCategory = new Category("Test Category", Color.RED);
        CategoryManager categoryManager = CategoryManager.getInstance();
        categoryManager.addCategory(testCategory);

        Expense expense1 = new Expense(LocalDate.now(), "Test Location", 100.0, "Test Description");
        Expense expense2 = new Expense(LocalDate.now(), "Test Location 2", 50.0, "Test Description 2");
        expense1.setCategory(testCategory);
        List<Expense> expenses = List.of(expense1, expense2);

        File file = new File(tempDir.toString() + "/data/expenses.json");
        objectMapper.writeValue(file, expenses);

        List<Expense> loadedExpenses = DataManager.loadExpenses(categoryManager);

        assertEquals(2, loadedExpenses.size(), "There should be two expenses in the loaded list");
        Expense loadedExpense1 = loadedExpenses.get(0);
        assertEquals("Test Location", loadedExpense1.getLocation(), "The location of the first loaded expense should be 'Test Location'");
        assertEquals(100.0, loadedExpense1.getAmount(), "The amount of the first loaded expense should be 100.0");
        assertEquals("Test Description", loadedExpense1.getDescription(), "The description of the first loaded expense should be 'Test Description'");
        assertNotNull(loadedExpense1.getCategory(), "The category of the first loaded expense should not be null");
        assertEquals("Test Category", loadedExpense1.getCategory().getName(), "The category name of the first loaded expense should be 'Test Category'");

        Expense loadedExpense2 = loadedExpenses.get(1);
        assertEquals("Test Location 2", loadedExpense2.getLocation(), "The location of the second loaded expense should be 'Test Location 2'");
        assertEquals(50.0, loadedExpense2.getAmount(), "The amount of the second loaded expense should be 50.0");
        assertEquals("Test Description 2", loadedExpense2.getDescription(), "The description of the second loaded expense should be 'Test Description 2'");
        assertNull(loadedExpense2.getCategory(), "The category of the second loaded expense should be null");
    }

    /**
     * Tests the {@link DataManager#setBasePath(String)} method.
     * Verifies that the base path can be set correctly and the new directory exists.
     */
    @Test
    void testSetBasePath() {
        String newPath = tempDir.toString() + "/newData/";
        DataManager.setBasePath(newPath);

        File newDir = new File(newPath);
        assertTrue(newDir.exists() && newDir.isDirectory(), "New data directory should exist");
    }
}
