package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Singleton class to manage a list of categories.
 */
public class CategoryManager {
    /** Default category name to be used when no specific category is assigned. */
    public static final String DEFAULT_CATEGORY = "Default";
    /** Singleton instance of the CategoryManager. */
    private static CategoryManager instance;
    /** List of categories managed by this CategoryManager. */
    private final ObservableList<Category> categories;

    /**
     * Private constructor to initialize the category list and add the default category.
     */
    private CategoryManager() {
        categories = FXCollections.observableArrayList();
        categories.add(new Category(DEFAULT_CATEGORY, Color.GRAY));
    }

    /**
     * Returns the single instance of CategoryList.
     *
     * @return the instance of CategoryList
     */
    public static synchronized CategoryManager getInstance() {
        if (instance == null) {
            instance = new CategoryManager();
        }
        return instance;
    }

    /**
     * Adds a new category to the list.
     *
     * @param category the category to add
     */
    public void addCategory(Category category) {
        if (categories.stream().noneMatch(c -> c.getName().equalsIgnoreCase(category.getName()))) {
            categories.add(category);
        }
    }

    /**
     * Removes a specified category from the list.
     * If the category is associated with any expenses, those expenses are updated to use the default category.
     *
     * @param category the category to remove
     */
    public void removeCategory(Category category) {
        if (!category.getName().equals(DEFAULT_CATEGORY)) {
            categories.remove(category);
            ExpenseManager.getInstance().updateCategoryInExpenses(category, getCategoryByName(DEFAULT_CATEGORY));
        }
    }

    /**
     * Updates an existing category. All associated expenses are updated to reflect the changes.
     *
     * @param oldName the current name of the category to be updated
     * @param newCategory the new category details to update
     */
    public void updateCategory(String oldName, Category newCategory) {
        if (!oldName.equals(DEFAULT_CATEGORY)) {
            Category oldCategory = getCategoryByName(oldName);
            if (oldCategory != null && (newCategory.getName().equalsIgnoreCase(oldName) || getCategoryByName(newCategory.getName()) == null)) {
                oldCategory.setName(newCategory.getName());
                oldCategory.setColor(newCategory.getColor());
                oldCategory.setLimit(newCategory.getLimit());

                categories.set(categories.indexOf(oldCategory), oldCategory);
                ExpenseManager.getInstance().updateCategoryInExpenses(oldCategory, oldCategory);
            }
        }
    }

    /**
     * Gets the list of all categories.
     *
     * @return a list of categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * Sets the list of categories, replacing any existing categories.
     * The default category is ensured to be present in the list.
     *
     * @param newCategories the new list of categories
     */
    public void setCategories(List<Category> newCategories) {
        categories.clear();
        if (newCategories != null && !newCategories.isEmpty()) {
            categories.addAll(newCategories);
        }

        if (categories.stream().noneMatch(c -> c.getName().equals(DEFAULT_CATEGORY))) {
            categories.add(new Category(DEFAULT_CATEGORY, Color.GRAY));
        }
    }

    /**
     * Gets a category by its name.
     *
     * @param name the name of the category
     * @return the category with the specified name, or null if not found
     */
    public Category getCategoryByName(String name) {
        Optional<Category> category = categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
        return category.orElse(null);
    }

    /**
     * Returns a string representation of the category list, containing details of all categories.
     *
     * @return a formatted string representing the entire list of categories
     */
    @Override
    public String toString() {
        return "Category List:\n" + categories.stream()
                .map(Category::toString)
                .collect(Collectors.joining("\n"));
    }
}