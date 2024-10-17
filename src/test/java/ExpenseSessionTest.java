import model.Expense;
import model.ExpenseSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ExpenseSession}.
 */
class ExpenseSessionTest {
    /** Holds details about a specific expense, including date, amount, category, and description. */
    private Expense expense;

    /**
     * Constructs a new instance of ExpenseSessionTest.
     */
    public ExpenseSessionTest() {
    }

    /**
     * Sets up the test environment.
     * Initializes an {@link Expense} instance and resets the current expense session.
     */
    @BeforeEach
    void setUp() {
        expense = new Expense(LocalDate.now(), "Supermarket", 50.0, "Groceries");
        ExpenseSession.resetCurrentExpense();
    }

    /**
     * Tests the {@link ExpenseSession#setCurrentExpense(Expense)} and {@link ExpenseSession#getCurrentExpense()} methods.
     * Verifies that the current expense can be set and retrieved correctly.
     */
    @Test
    void testSetGetCurrentExpense() {
        ExpenseSession.setCurrentExpense(expense);

        Expense retrievedExpense = ExpenseSession.getCurrentExpense();

        assertNotNull(retrievedExpense, "The current expense should not be null");
        assertEquals(expense, retrievedExpense, "The retrieved expense should match the set expense");
    }

    /**
     * Tests the {@link ExpenseSession#resetCurrentExpense()} method.
     * Verifies that the current expense can be reset to null.
     */
    @Test
    void testResetCurrentExpense() {
        ExpenseSession.setCurrentExpense(expense);

        ExpenseSession.resetCurrentExpense();

        assertNull(ExpenseSession.getCurrentExpense(), "The current expense should be null after reset");
    }

    /**
     * Tests the {@link ExpenseSession#isCurrentExpense()} method.
     * Verifies that the presence of a current expense can be correctly determined.
     */
    @Test
    void testIsCurrentExpense() {
        assertFalse(ExpenseSession.isCurrentExpense(), "There should be no current expense initially");
        ExpenseSession.setCurrentExpense(expense);

        assertTrue(ExpenseSession.isCurrentExpense(), "There should be a current expense after setting one");
        ExpenseSession.resetCurrentExpense();

        assertFalse(ExpenseSession.isCurrentExpense(), "There should be no current expense after reset");
    }
}
