import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import model.Category;
import model.CategoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the {@link CategoryManager} class.
 */
public class CategoryManagerTest {
    /** Manages category operations including creation, updating, deletion, and retrieval. */
    private CategoryManager categoryManager;

    /**
     * Constructs a new instance of CategoryManagerTest.
     */
    public CategoryManagerTest() {
    }

    /**
     * Sets up the test environment.
     * Initializes the {@link CategoryManager} instance before each test.
     */
    @BeforeEach
    public void setUp() {
        categoryManager = CategoryManager.getInstance();
    }

    /**
     * Tests the {@link CategoryManager#getInstance()} method.
     * Ensures that the same instance of {@link CategoryManager} is returned every time.
     */
    @Test
    public void testGetInstance() {
        CategoryManager instance1 = CategoryManager.getInstance();
        CategoryManager instance2 = CategoryManager.getInstance();
        assertSame(instance1, instance2, "CategoryManager should return the same instance");
    }

    /**
     * Tests the {@link CategoryManager#addCategory(Category)} method.
     * Verifies that a category can be added and retrieved by its name.
     */
    @Test
    public void testAddCategory() {
        Category newCategory = new Category("TestCategory", Color.BLUE);

        categoryManager.addCategory(newCategory);

        Category retrievedCategory = categoryManager.getCategoryByName("TestCategory");
        assertNotNull(retrievedCategory, "Category should be added");
        assertEquals("TestCategory", retrievedCategory.getName());
        assertEquals(Color.BLUE, retrievedCategory.getColor());
    }

    /**
     * Verifies that the CategoryManager does not allow adding a new category with the same name as an existing one.
     * The test ensures that a duplicate category name entry with a different attribute (color) does not replace or
     * get added alongside the original category. It checks the integrity of category name uniqueness within the manager.
     */
    @Test
    public void testAddDuplicateCategory() {
        CategoryManager categoryManager = CategoryManager.getInstance();

        Category firstCategory = new Category("TestCategory", Color.BLUE);
        categoryManager.addCategory(firstCategory);

        Category duplicateCategory = new Category("TestCategory", Color.RED);
        categoryManager.addCategory(duplicateCategory);

        Category retrievedCategory = categoryManager.getCategoryByName("TestCategory");
        assertNotNull(retrievedCategory, "Category should exist");
        assertEquals("TestCategory", retrievedCategory.getName());
        assertEquals(Color.BLUE, retrievedCategory.getColor(), "Original category color should remain unchanged");

        long count = categoryManager.getCategories().stream()
                .filter(c -> c.getName().equalsIgnoreCase("TestCategory"))
                .count();
        assertEquals(1, count, "There should be only one category named 'TestCategory'");
    }

    /**
     * Tests the {@link CategoryManager#removeCategory(Category)} method.
     * Verifies that a category can be removed successfully.
     */
    @Test
    public void testRemoveCategory() {
        Category newCategory = new Category("TestCategory2", Color.BLUE);
        categoryManager.addCategory(newCategory);

        categoryManager.removeCategory(newCategory);

        Category retrievedCategory = categoryManager.getCategoryByName("TestCategory2");
        assertNull(retrievedCategory, "Category should be removed");
    }

    /**
     * Tests that the default category cannot be removed.
     */
    @Test
    public void testRemoveDefaultCategory() {
        Category defaultCategory = categoryManager.getCategoryByName(CategoryManager.DEFAULT_CATEGORY);
        categoryManager.removeCategory(defaultCategory);

        Category retrievedDefaultCategory = categoryManager.getCategoryByName(CategoryManager.DEFAULT_CATEGORY);
        assertNotNull(retrievedDefaultCategory, "Default category should not be removed");
    }

    /**
     * Tests the {@link CategoryManager#updateCategory(String, Category)} method.
     * Verifies that a category can be updated with new details.
     */
    @Test
    public void testUpdateCategory() {
        // Setup initial category
        Category oldCategory = new Category("OldCategory", Color.RED);
        categoryManager.addCategory(oldCategory);

        Category newCategory = new Category("UpdatedCategory", Color.GREEN);
        categoryManager.updateCategory(oldCategory.getName(), newCategory);


        Category retrievedCategory = categoryManager.getCategoryByName("UpdatedCategory");
        assertNotNull(retrievedCategory, "Updated category should be present");
        assertEquals("UpdatedCategory", retrievedCategory.getName());
        assertEquals(Color.GREEN, retrievedCategory.getColor());
    }

    /**
     * Tests that the default category cannot be renamed or changed in color.
     * This test verifies that any attempt to update the default category is ignored, maintaining its integrity.
     */
    @Test
    public void testUpdateDefaultCategory() {
        String defaultName = CategoryManager.DEFAULT_CATEGORY;
        Category newDefaultCategory = new Category("NewDefaultName", Color.BLUE);
        categoryManager.updateCategory(defaultName, newDefaultCategory);

        Category defaultCategory = categoryManager.getCategoryByName(defaultName);
        assertNotNull(defaultCategory, "Default category should still exist");
        assertEquals(defaultName, defaultCategory.getName(), "Default category name should not change");
        assertEquals(Color.GRAY, defaultCategory.getColor(), "Default category color should not change");
    }

    /**
     * Tests the {@link CategoryManager#getCategories()} method.
     * Verifies that the categories list is not null and contains the added category.
     */
    @Test
    public void testGetCategories() {
        Category newCategory = new Category("TestCategory", Color.BLUE);
        categoryManager.addCategory(newCategory);

        List<Category> categories = categoryManager.getCategories();

        assertNotNull(categories, "Categories list should not be null");
        assertFalse(categories.isEmpty(), "Categories list should not be empty");
    }

    /**
     * Tests the {@link CategoryManager#setCategories(List)} method.
     * Verifies that the categories list can be set and retrieved correctly.
     */
    @Test
    public void testSetCategories() {
        List<Category> newCategories = List.of(new Category("NewCategory1", Color.BLUE), new Category("NewCategory2", Color.RED));

        categoryManager.setCategories(newCategories);

        List<Category> categories = categoryManager.getCategories();
        assertEquals(3, categories.size(), "Categories list should contain the default category and the new categories");
    }

    /**
     * Tests the {@link CategoryManager#getCategoryByName(String)} method.
     * Verifies that a category can be found by its name.
     */
    @Test
    public void testGetCategoryByName() {
        Category newCategory = new Category("TestCategory", Color.BLUE);
        categoryManager.addCategory(newCategory);

        Category retrievedCategory = categoryManager.getCategoryByName("TestCategory");

        assertNotNull(retrievedCategory, "Category should be found by name");
        assertEquals("TestCategory", retrievedCategory.getName());
    }

    /**
     * Tests the {@link CategoryManager#toString()} method.
     * Verifies that the string representation of the category list is correct.
     */
    @Test
    void testToString() {
        Category category1 = new Category("Food", Color.RED);
        Category category2 = new Category("Travel", Color.BLUE);
        ObservableList<Category> categories = FXCollections.observableArrayList(category1, category2);
        categoryManager.setCategories(categories);

        String result = categoryManager.toString();

        String expected = "Category List:\n" + categoryManager.getCategories().stream()
                .map(Category::toString)
                .collect(Collectors.joining("\n"));
        assertEquals(expected, result, "The toString method should return the correct string representation of the category list including the default category");
    }
}