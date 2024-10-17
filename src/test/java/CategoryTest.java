import javafx.scene.paint.Color;
import model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the {@link Category} class.
 */
public class CategoryTest {
    /** Holds category details for operations like display, update, or deletion within the application. */
    private Category category;

    /**
     * Constructs a new instance of CategoryTest.
     */
    public CategoryTest() {
    }

    /**
     * Sets up the test environment.
     * Initializes the {@link Category} instance before each test.
     */
    @BeforeEach
    public void setUp() {
        category = new Category("Test Category", Color.BLUE);
    }

    /**
     * Tests the {@link Category} constructor.
     * Verifies that the category is initialized with the correct name, color, id, font color, and limit.
     */
    @Test
    public void testConstructor() {
        assertEquals("Test Category", category.getName());
        assertEquals(Color.BLUE, category.getColor());
        assertNotNull(category.getId());
        assertNotNull(category.getFontColor());
        assertEquals(0.0, category.getLimit());
    }

    /**
     * Tests the {@link Category#setName(String)} method.
     * Verifies that the category name can be set correctly.
     */
    @Test
    public void testSetName() {
        category.setName("New Category");
        assertEquals("New Category", category.getName());
    }

    /**
     * Tests the {@link Category#setColor(Color)} method.
     * Verifies that the category color can be set correctly and the font color is not null.
     */
    @Test
    public void testSetColor() {
        category.setColor(Color.RED);
        assertEquals(Color.RED, category.getColor());
        assertNotNull(category.getFontColor());
    }

    /**
     * Tests the {@link Category#setLimit(double)} method.
     * Verifies that the category limit can be set correctly.
     */
    @Test
    public void testSetLimit() {
        category.setLimit(200.0);
        assertEquals(200.0, category.getLimit());
    }

    /**
     * Tests the {@link Category#getId()} method.
     * Verifies that the category id is not null.
     */
    @Test
    public void testGetId() {
        UUID id = category.getId();
        assertNotNull(id);
    }

    /**
     * Tests the {@link Category#toString()} method.
     * Verifies that the string representation of the category is correct.
     */
    @Test
    public void testToString() {
        String expected = "Category{id=" + category.getId() +
                ", name='Test Category'" +
                ", color=" + Color.BLUE +
                ", fontColor=" + category.getFontColor() +
                ", limit=" + 0.0 +
                '}';
        assertEquals(expected, category.toString());
    }
}
