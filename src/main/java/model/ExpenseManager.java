package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages a collection of expenses, providing methods to add, remove, and filter expenses based on various criteria.
 */
public class ExpenseManager {
    /** A static list that holds all the expenses managed by this class. This list is shared across all instances. */
    private static final List<Expense> expenses = new ArrayList<>();
    /** The singleton instance of ExpenseManager to ensure it is accessed globally. */
    private static ExpenseManager instance;

    /**
     * Private constructor to prevent instantiation outside the singleton pattern.
     */
    private ExpenseManager() {}

    /**
     * Provides the single instance of ExpenseManager using the singleton design pattern.
     * If the instance does not exist, it creates a new one.
     *
     * @return the single instance of ExpenseManager
     */
    public static synchronized ExpenseManager getInstance() {
        if (instance == null) {
            instance = new ExpenseManager();
        }
        return instance;
    }

    /**
     * Returns a new list containing all the expenses.
     * This ensures the encapsulation and immutability of the internal expense list.
     *
     * @return a list of expenses
     */
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Sets the list of expenses, replacing any existing expenses.
     *
     * @param newExpenses the new list of expenses
     */
    public void setExpenses(List<Expense> newExpenses) {
        expenses.clear();
        if (newExpenses != null) {
            expenses.addAll(newExpenses);
        }
    }

    /**
     * Adds an expense to the internal list.
     *
     * @param expense the expense to be added, not null
     */
    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    /**
     * Removes a specified expense from the internal list.
     *
     * @param expense the expense to be removed, not null
     */
    public void removeExpense(Expense expense) {
        expenses.remove(expense);
    }

    /**
     * Updates an existing expense with new details.
     *
     * @param expense the expense to be updated
     */
    public void updateExpense(Expense expense) {
        int index = expenses.indexOf(expense);
        if (index != -1) {
            expenses.set(index, expense);
        }
    }

    /**
     * Filters the list of expenses based on the provided search text, selected categories, selected period, and current date.
     * Returns a list of expenses that match all the given filters.
     *
     * @param searchText          the search text used to filter expenses by description or location
     * @param selectedCategories  a list of categories to filter the expenses
     * @param selectedPeriod      the time period used to filter expenses
     * @param currentDate         the reference date used to calculate date ranges for the selected time period
     * @return a list of filtered expenses
     */
    public List<Expense> getExpenses(String searchText, List<Category> selectedCategories, TimePeriod selectedPeriod, LocalDate currentDate) {
        return expenses.stream()
                .filter(expense -> passesFilters(expense, searchText, selectedCategories, selectedPeriod, currentDate))
                .collect(Collectors.toList());
    }

    /**
     * Updates the category of all expenses that are associated with a given category.
     * This method is typically called when a category is updated or removed in {@link CategoryManager}.
     *
     * @param oldCategory the category to be replaced or updated
     * @param newCategory the new or updated category to assign to the expenses
     */
    public void updateCategoryInExpenses(Category oldCategory, Category newCategory) {
        expenses.forEach((Expense expense) -> {
            if (expense.getCategory() != null && expense.getCategory().equals(oldCategory)) {
                expense.setCategory(newCategory);
            }
        });
    }

    /**
     * Evaluates if an expense passes all set filters: search text, category selection, and time period.
     *
     * @param expense the expense to check against filters
     * @param searchText text to match against expense description or location
     * @param selectedCategories categories to match against expense category
     * @param selectedPeriod time period within which the expense must fall
     * @param currentDate the date used to determine the time period context
     * @return true if the expense matches all filters, false otherwise
     */
    private boolean passesFilters(Expense expense, String searchText, List<Category> selectedCategories, TimePeriod selectedPeriod, LocalDate currentDate) {
        return passesSearchFilter(expense, searchText) &&
                passesCategoryFilter(expense, selectedCategories) &&
                passesPeriodFilter(expense, selectedPeriod, currentDate);
    }

    /**
     * Checks if the expense matches the provided search text by location or description.
     *
     * @param expense the expense to check
     * @param searchText the search text to match against the expense's location or description
     * @return true if the search text is null, empty, or matches the expense's location or description
     */
    private boolean passesSearchFilter(Expense expense, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return true;
        }
        return expense.getLocation().toLowerCase().contains(searchText.toLowerCase()) ||
                expense.getDescription().toLowerCase().contains(searchText.toLowerCase());
    }

    /**
     * Checks if the expense's category matches any of the selected categories.
     *
     * @param expense the expense to check
     * @param selectedCategories the categories to match against the expense's category
     * @return true if no categories are selected or if the expense's category is in the selected categories
     */
    private boolean passesCategoryFilter(Expense expense, List<Category> selectedCategories) {
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            return true;
        }
        return expense.getCategory() != null && selectedCategories.contains(expense.getCategory());
    }

    /**
     * Determines whether an expense falls within the time range defined by the selected period and current date.
     *
     * @param expense the expense to check
     * @param selectedPeriod the period within which the expense date should fall
     * @param currentDate the reference date for determining the applicable period range
     * @return true if the expense date falls within the defined period range, false otherwise
     */
    private boolean passesPeriodFilter(Expense expense, TimePeriod selectedPeriod, LocalDate currentDate) {
        if (selectedPeriod == null) {
            return true;
        }
        LocalDate startDate = selectedPeriod.getStartDate(currentDate);
        LocalDate endDate = selectedPeriod.getEndDate(currentDate);
        return !expense.getDate().isBefore(startDate) && !expense.getDate().isAfter(endDate);
    }

    /**
     * Returns a string representation of the expense list, containing details of all expenses.
     *
     * @return a formatted string representing the entire list of expenses
     */
    @Override
    public String toString() {
        return "Expense List:\n" + expenses.stream()
                .map(Expense::toString)
                .collect(Collectors.joining("\n"));
    }
}