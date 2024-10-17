package model;

import javafx.scene.paint.Color;

import java.util.UUID;

/**
 * Represents a category with a unique ID, name, color, spending limit, and associated logo.
 */
public class Category {
    /** Unique identifier for the category. It is immutable once the category is created. */
    private final UUID id;
    /** he name of the category which describes its purpose or usage. */
    private String name;
    /** The primary color associated with the category, used for UI display purposes. */
    private Color color;
    /** he color used for text displayed against the 'color' background, automatically calculated to ensure readability. */
    private Color fontColor;
    /** The spending limit for the category, which can act as a budget cap for expenses categorized under this. */
    private double limit;
    /** Default constructor for creating a category. */
    public Category() {
        this.id = UUID.randomUUID();
    }

    /**
     * Constructs a category with specified name, color, and spending limit.
     *
     * @param name  the name of the category
     * @param color the color associated with the category
     */
    public Category(String name, Color color) {
        this();
        this.name = name;
        this.color = color;
        this.fontColor = darkenColor(color);
        this.limit = 0.0;
    }

    /**
     * Darkens the given color by a specified factor.
     *
     * @param color the color to darken
     * @return the darkened color
     */
    private Color darkenColor(Color color) {
        double factor = 0.333;
        return new Color(
                color.getRed() * factor,
                color.getGreen() * factor,
                color.getBlue() * factor,
                color.getOpacity()
        );
    }

    /**
     * Gets the unique ID of the category.
     *
     * @return the unique ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the name of the category.
     *
     * @return the name of the category
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the category.
     *
     * @param name the new name for the category
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the color associated with the category.
     *
     * @return the color of the category
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the category.
     *
     * @param color the new color for the category
     */
    public void setColor(Color color) {
        this.color = color;
        this.fontColor = darkenColor(color);
    }

    /**
     * Gets the font color of the category.
     *
     * @return the font color of the category
     */
    public Color getFontColor() {
        return fontColor;
    }

    /**
     * Gets the spending limit for the category.
     *
     * @return the spending limit
     */
    public double getLimit() {
        return limit;
    }

    /**
     * Sets the spending limit for the category.
     *
     * @param limit the spending limit to set
     */
    public void setLimit(double limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", fontColor=" + fontColor +
                ", limit=" + limit +
                '}';
    }
}