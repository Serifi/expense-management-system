package model;

/**
 * Provides a session-level storage for an {@link Expense} object. This class uses a singleton pattern to manage the current
 * expense globally across the application lifecycle, allowing for easy access and modification of the expense in a controlled manner.
 */
public class ExpenseSession {
    /** Holds the current expense that is actively being edited or viewed in the session. */
    private static Expense currentExpense;

    /**
     * Constructs an instance of ExpenseSession.
     */
    public ExpenseSession() {
        // Standard constructor, intentionally left empty
    }

    /**
     * Retrieves the current expense stored in the session.
     *
     * @return the current {@link Expense} if it exists; otherwise, returns {@code null}.
     */
    public static Expense getCurrentExpense() {
        return currentExpense;
    }

    /**
     * Sets the current expense in the session to the provided expense.
     * This method is used to initialize the session with an expense to be edited or viewed.
     *
     * @param expense The {@link Expense} object to be set as the current expense in the session.
     */
    public static void setCurrentExpense(Expense expense) {
        currentExpense = expense;
    }

    /**
     * Clears the current expense from the session. This method should be called to ensure that
     * the session does not hold onto an expense object longer than necessary, to free up resources
     * and prevent outdated data from being used.
     */
    public static void resetCurrentExpense() {
        currentExpense = null;
    }

    /**
     * Checks if there is an active expense stored in the session.
     *
     * @return {@code true} if there is an expense present in the session; {@code false} otherwise.
     */
    public static boolean isCurrentExpense() {
        return currentExpense != null;
    }
}