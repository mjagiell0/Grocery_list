package grocery_classes;

import measure_enums.Measure;

import java.io.Serial;
import java.io.Serializable;

public record Product(String name, String category, Measure measure, double pricePerMeasure,
                      int id) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

}
