package grocery_classes;

import measure_enums.Measure;

public class Product {
    private final String name;
    private final String category;
    private final Measure measure;
    private final double pricePerMeasure;
    private final int id;

    public Product(String name, String category, Measure measure, double pricePerMeasure, int id) {
        this.name = name;
        this.category = category;
        this.measure = measure;
        this.pricePerMeasure = pricePerMeasure;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Measure getMeasure() {
        return measure;
    }

    public double getPricePerMeasure() {
        return pricePerMeasure;
    }

    public int getId() {
        return id;
    }
}
