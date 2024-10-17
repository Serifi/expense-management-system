package model;

/**
 * Functional interface for filtering expenses. This interface is intended to be implemented
 * by any class or lambda expression that defines a specific filtering logic for expenses.
 */
@FunctionalInterface
public interface ExpenseFilter {
    /**
     * The abstract method to be implemented for filtering expenses.
     * When implemented, this method should contain the logic to filter expenses based on predefined criteria.
     */
    void filter();
}