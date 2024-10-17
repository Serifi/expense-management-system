import model.Category;
import model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Expense}.
 */
class ExpenseTest {
    /** Holds details about a specific expense, including date, amount, category, and description. */
    private Expense expense;

    /**
     * Constructs a new instance of ExpenseTest.
     */
    public ExpenseTest() {
    }

    /**
     * Sets up the test environment.
     * Initializes an {@link Expense} instance before each test.
     */
    @BeforeEach
    void setUp() {
        expense = new Expense();
    }

    /**
     * Tests the default constructor of the {@link Expense} class.
     * Verifies that all fields are initialized to their default values.
     */
    @Test
    void testDefaultConstructor() {
        assertNotNull(expense.getId());
        assertNull(expense.getDate());
        assertNull(expense.getTime());
        assertNull(expense.getLocation());
        assertEquals(0.0, expense.getAmount());
        assertNull(expense.getDescription());
        assertNull(expense.getImagePath());
        assertNull(expense.getCategory());
    }

    /**
     * Tests the parameterized constructor of the {@link Expense} class.
     * Verifies that all fields are initialized with the provided values.
     */
    @Test
    void testParameterizedConstructor() {
        LocalDate date = LocalDate.now();
        String location = "Supermarket";
        double amount = 50.0;
        String description = "Groceries";

        Expense expense = new Expense(date, location, amount, description);

        assertNotNull(expense.getId());
        assertEquals(date, expense.getDate());
        assertEquals(location, expense.getLocation());
        assertEquals(amount, expense.getAmount());
        assertEquals(description, expense.getDescription());
        assertNull(expense.getTime());
        assertNull(expense.getImagePath());
        assertNull(expense.getCategory());
    }

    /**
     * Tests the {@link Expense#setDate(LocalDate)} and {@link Expense#getDate()} methods.
     * Verifies that the date can be set and retrieved correctly.
     */
    @Test
    void testSetGetDate() {
        LocalDate date = LocalDate.now();
        expense.setDate(date);
        assertEquals(date, expense.getDate());
    }

    /**
     * Tests the {@link Expense#setTime(LocalTime)} and {@link Expense#getTime()} methods.
     * Verifies that the time can be set and retrieved correctly.
     */
    @Test
    void testSetGetTime() {
        LocalTime time = LocalTime.now();
        expense.setTime(time);
        assertEquals(time, expense.getTime());
    }

    /**
     * Tests the {@link Expense#setLocation(String)} and {@link Expense#getLocation()} methods.
     * Verifies that the location can be set and retrieved correctly.
     */
    @Test
    void testSetGetLocation() {
        String location = "Airport";
        expense.setLocation(location);
        assertEquals(location, expense.getLocation());
    }

    /**
     * Tests the {@link Expense#setAmount(double)} and {@link Expense#getAmount()} methods.
     * Verifies that the amount can be set and retrieved correctly.
     */
    @Test
    void testSetGetAmount() {
        double amount = 200.0;
        expense.setAmount(amount);
        assertEquals(amount, expense.getAmount());
    }

    /**
     * Tests the {@link Expense#setDescription(String)} and {@link Expense#getDescription()} methods.
     * Verifies that the description can be set and retrieved correctly.
     */
    @Test
    void testSetGetDescription() {
        String description = "Flight Ticket";
        expense.setDescription(description);
        assertEquals(description, expense.getDescription());
    }

    /**
     * Tests the {@link Expense#setImagePath(String)} and {@link Expense#getImagePath()} methods.
     * Verifies that the image path can be set and retrieved correctly.
     */
    @Test
    void testSetGetImagePath() {
        String imagePath = "/path/to/image.jpg";
        expense.setImagePath(imagePath);
        assertEquals(imagePath, expense.getImagePath());
    }

    /**
     * Tests the {@link Expense#setCategory(Category)} and {@link Expense#getCategory()} methods.
     * Verifies that the category can be set and retrieved correctly.
     */
    @Test
    void testSetGetCategory() {
        Category category = new Category("Travel", javafx.scene.paint.Color.BLUE);
        expense.setCategory(category);
        assertEquals(category, expense.getCategory());
    }

    /**
     * Tests the {@link Expense#toString()} method.
     * Verifies that the string representation of the expense is correct.
     */
    @Test
    void testToString() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        String location = "Downtown";
        double amount = 75.0;
        String description = "Restaurant";
        String imagePath = "/path/to/image.jpg";
        Category category = new Category("Food", javafx.scene.paint.Color.RED);

        expense.setDate(date);
        expense.setTime(time);
        expense.setLocation(location);
        expense.setAmount(amount);
        expense.setDescription(description);
        expense.setImagePath(imagePath);
        expense.setCategory(category);

        String expected = "Expense{" +
                "id=" + expense.getId() +
                ", date=" + date +
                ", time=" + time +
                ", location='" + location + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", category=" + category +
                '}';

        assertEquals(expected, expense.toString());
    }
}