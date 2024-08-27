package grocery_classes;

import measure_enums.Measure;

import java.io.Serial;
import java.io.Serializable;

public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String category;
    private final Measure measure;
    private final double minimumQuantity;
    private final double pricePerMeasure;
    private final int id;

    public Product(String name, String category, Measure measure, double pricePerMeasure, int id) {
        this.name = name;
        this.category = category;
        this.measure = measure;
        this.pricePerMeasure = pricePerMeasure;
        this.id = id;

        if (measure == Measure.pcs)
            minimumQuantity = 1.0;
        else
            minimumQuantity = 0.25;
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

    public double getMinimumQuantity() {
        return minimumQuantity;
    }

    public int getId() {
        return id;
    }
}
