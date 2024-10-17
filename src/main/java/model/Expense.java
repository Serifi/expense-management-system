package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Represents an expense with details such as date, time, location, amount, description, image path, and category.
 */
public class Expense {
    /** Unique identifier for the expense, immutable once set. */
    private final UUID id;
    /** The date on which the expense occurred. */
    private LocalDate date;
    /** The time at which the expense occurred. */
    private LocalTime time;
    /** The location where the expense occurred. */
    private String location;
    /** The monetary amount of the expense. */
    private double amount;
    /** A brief description of the expense. */
    private String description;
    /** Path to an image file related to the expense, can be null if no image is associated. */
    private String imagePath;
    /** Category of the expense, helping in classification and analysis. */
    private Category category;

    /**
     * Default constructor.
     */
    public Expense() {
        this.id = UUID.randomUUID();
    }

    /**
     * Constructs an Expense with specified date, location, amount, and description.
     *
     * @param date        the date of the expense
     * @param location    the location where the expense occurred
     * @param amount      the amount of the expense
     * @param description a brief description of the expense
     */
    public Expense(LocalDate date, String location, double amount, String description) {
        this();
        this.date = date;
        this.location = location;
        this.amount = amount;
        this.description = description;
    }

    /**
     * Gets the unique ID of the expense.
     *
     * @return the unique ID of the expense
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the date of the expense.
     *
     * @return the date of the expense
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date of the expense.
     *
     * @param date the new date of the expense
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the location where the expense occurred.
     *
     * @return the location of the expense
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the expense.
     *
     * @param location the new location of the expense
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the amount of the expense.
     *
     * @return the amount of the expense
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the expense.
     *
     * @param amount the new amount of the expense
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the description of the expense.
     *
     * @return the description of the expense
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the expense.
     *
     * @param description the new description of the expense
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the image path associated with the expense.
     *
     * @return the image path of the expense
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the image path associated with the expense.
     *
     * @param imagePath the new image path of the expense
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Gets the category of the expense.
     *
     * @return the category of the expense
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the category of the expense.
     *
     * @param category the new category of the expense
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Gets the time of the expense.
     *
     * @return the time of the expense
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Sets the time of the expense.
     *
     * @param time the new time of the expense
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Returns a string representation of the Expense object.
     *
     * @return a string representation of the Expense object
     */
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", date=" + date +
                ", time=" + time +
                ", location='" + location + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", category=" + category +
                '}';
    }
}