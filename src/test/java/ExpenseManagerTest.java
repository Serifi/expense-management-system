import javafx.scene.paint.Color;
import model.Category;
import model.Expense;
import model.ExpenseManager;
import model.TimePeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ExpenseManager}.
 */
class ExpenseManagerTest {
    /** Manages operations related to expenses, including addition, modification, and deletion. */
    private ExpenseManager expenseManager;
    /** Represents the category associated with food-related expenses in the application. */
    private Category foodCategory;
    /** Represents the category associated with travel-related expenses in the application. */
    private Category travelCategory;

    /**
     * Constructs a new instance of ExpenseManagerTest.
     */
    public ExpenseManagerTest() {
    }

    /**
     * Sets up the test environment.
     * Initializes the {@link ExpenseManager} instance and adds some test expenses.
     */
    @BeforeEach
    void setUp() {
        expenseManager = ExpenseManager.getInstance();
        expenseManager.setExpenses(new ArrayList<>());

        foodCategory = new Category("Food", Color.CORAL);
        travelCategory = new Category("Travel", Color.ORCHID);

        Expense expense1 = new Expense(LocalDate.of(2024, 7, 25), "Supermarket", 50.0, "Groceries");
        expense1.setCategory(foodCategory);

        Expense expense2 = new Expense(LocalDate.of(2024, 7, 16), "Airport", 200.0, "Flight Ticket");
        expense2.setCategory(travelCategory);

        Expense expense3 = new Expense(LocalDate.of(2024, 8, 11), "Downtown", 75.0, "Restaurant");
        expense3.setCategory(foodCategory);

        expenseManager.addExpense(expense1);
        expenseManager.addExpense(expense2);
        expenseManager.addExpense(expense3);
    }

    /**
     * Tests the {@link ExpenseManager#getInstance()} method.
     * Ensures that the same instance of {@link ExpenseManager} is returned every time.
     */
    @Test
    void testGetInstance() {
        ExpenseManager instance1 = ExpenseManager.getInstance();
        ExpenseManager instance2 = ExpenseManager.getInstance();
        assertSame(instance1, instance2, "ExpenseManager should return the same instance");
    }

    /**
     * Tests the {@link ExpenseManager#addExpense(Expense)} method.
     * Verifies that an expense can be added successfully.
     */
    @Test
    void testAddExpense() {
        Expense newExpense = new Expense(LocalDate.now(), "Cafe", 20.0, "Coffee");
        expenseManager.addExpense(newExpense);
        assertTrue(expenseManager.getExpenses().contains(newExpense), "Expense should be added");
    }

    /**
     * Tests the {@link ExpenseManager#removeExpense(Expense)} method.
     * Verifies that an expense can be removed successfully.
     */
    @Test
    void testRemoveExpense() {
        Expense expenseToRemove = expenseManager.getExpenses().get(0);
        expenseManager.removeExpense(expenseToRemove);
        assertFalse(expenseManager.getExpenses().contains(expenseToRemove), "Expense should be removed");
    }

    /**
     * Tests the {@link ExpenseManager#updateExpense(Expense)} method.
     * Verifies that an expense can be updated successfully.
     */
    @Test
    void testUpdateExpense() {
        Expense expenseToUpdate = expenseManager.getExpenses().get(0);
        expenseToUpdate.setDescription("Updated Description");
        expenseManager.updateExpense(expenseToUpdate);
        assertEquals("Updated Description", expenseManager.getExpenses().get(0).getDescription(), "Expense should be updated");
    }

    /**
     * Tests the {@link ExpenseManager#updateExpense(Expense)} method to verify that attempting to update a non-existing expense
     * does not add it to the internal expense list.
     */
    @Test
    void testUpdateNonExistingExpense() {
        Expense newExpense = new Expense();
        newExpense.setDescription("non-existent");
        expenseManager.updateExpense(newExpense);
        assertFalse(expenseManager.getExpenses().contains(newExpense), "Non-existing expense should not be added to the list");
    }

    /**
     * Tests filtering expenses by search text using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesBySearchText() {
        List<Expense> filteredExpenses = expenseManager.getExpenses("groceries", null, null, LocalDate.now());
        assertEquals(1, filteredExpenses.size(), "There should be 1 expense matching 'groceries'");
        assertEquals("Groceries", filteredExpenses.get(0).getDescription(), "The description should match 'Groceries'");
    }

    /**
     * Tests filtering expenses by category using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesByCategory() {
        List<Category> selectedCategories = new ArrayList<>();
        selectedCategories.add(foodCategory);

        List<Expense> filteredExpenses = expenseManager.getExpenses("", selectedCategories, null, LocalDate.now());
        assertEquals(2, filteredExpenses.size(), "There should be 2 expenses in 'Food' category");
    }

    /**
     * Tests filtering expenses by day period using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesByDayPeriod() {
        List<Expense> filteredExpenses = expenseManager.getExpenses("", null, TimePeriod.DAY, LocalDate.of(2024, 7, 25));
        assertEquals(1, filteredExpenses.size(), "There should be 1 expense in the 'DAY' period");
        assertEquals("Groceries", filteredExpenses.get(0).getDescription(), "The description should match 'Groceries'");
    }

    /**
     * Tests filtering expenses by week period using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesByWeekPeriod() {
        List<Expense> filteredExpenses = expenseManager.getExpenses("", null, TimePeriod.WEEK, LocalDate.of(2024, 7, 25));
        assertEquals(1, filteredExpenses.size(), "There should be 1 expense in the 'WEEK' period");
        assertEquals("Groceries", filteredExpenses.get(0).getDescription(), "The description should match 'Groceries'");
    }

    /**
     * Tests filtering expenses by month period using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesByMonthPeriod() {
        List<Expense> filteredExpenses = expenseManager.getExpenses("", null, TimePeriod.MONTH, LocalDate.of(2024, 7, 25));
        assertEquals(2, filteredExpenses.size(), "There should be 2 expenses in the 'MONTH' period");
    }

    /**
     * Tests filtering expenses by year period using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesByYearPeriod() {
        List<Expense> filteredExpenses = expenseManager.getExpenses("", null, TimePeriod.YEAR, LocalDate.of(2024, 7, 25));
        assertEquals(3, filteredExpenses.size(), "There should be 3 expenses in the 'YEAR' period");
    }

    /**
     * Tests filtering expenses by search text and category using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesBySearchTextAndCategory() {
        List<Category> selectedCategories = new ArrayList<>();
        selectedCategories.add(foodCategory);

        List<Expense> filteredExpenses = expenseManager.getExpenses("restaurant", selectedCategories, null, LocalDate.now());
        assertEquals(1, filteredExpenses.size(), "There should be 1 expense matching 'restaurant' in 'Food' category");
        assertEquals("Restaurant", filteredExpenses.get(0).getDescription(), "The description should match 'Restaurant'");
    }

    /**
     * Tests filtering expenses by search text and period using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesBySearchTextAndPeriod() {
        List<Expense> filteredExpenses = expenseManager.getExpenses("flight", null, TimePeriod.MONTH, LocalDate.now());
        assertEquals(1, filteredExpenses.size(), "There should be 1 expense matching 'flight' in the 'MONTH' period");
        assertEquals("Flight Ticket", filteredExpenses.get(0).getDescription(), "The description should match 'Flight Ticket'");
    }

    /**
     * Tests filtering expenses by category and period using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesByCategoryAndPeriod() {
        List<Category> selectedCategories = new ArrayList<>();
        selectedCategories.add(travelCategory);

        List<Expense> filteredExpenses = expenseManager.getExpenses("", selectedCategories, TimePeriod.MONTH, LocalDate.now());
        assertEquals(1, filteredExpenses.size(), "There should be 1 expense in 'Travel' category in the 'MONTH' period");
        assertEquals("Flight Ticket", filteredExpenses.get(0).getDescription(), "The description should match 'Flight Ticket'");
    }

    /**
     * Tests filtering expenses by search text, category, and period using {@link ExpenseManager#getExpenses(String, List, TimePeriod, LocalDate)}.
     */
    @Test
    void testFilterExpensesByAllFilters() {
        List<Category> selectedCategories = new ArrayList<>();
        selectedCategories.add(travelCategory);

        List<Expense> filteredExpenses = expenseManager.getExpenses("flight", selectedCategories, TimePeriod.YEAR, LocalDate.now());
        assertEquals(1, filteredExpenses.size(), "There should be 1 expense matching 'flight' in 'Travel' category in the 'YEAR' period");
        assertEquals("Flight Ticket", filteredExpenses.get(0).getDescription(), "The description should match 'Flight Ticket'");
    }

    /**
     * Tests the {@link ExpenseManager#toString()} method.
     * Verifies that the string representation of the expense list is correct.
     */
    @Test
    void testToString() {
        List<Expense> expenses = expenseManager.getExpenses();
        StringBuilder expected = new StringBuilder("Expense List:\n");
        for (Expense expense : expenses) {
            expected.append(expense.toString()).append("\n");
        }
        assertEquals(expected.toString().trim(), expenseManager.toString().trim(), "toString should return the correct format and content");
    }
}