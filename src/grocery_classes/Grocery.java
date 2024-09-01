package grocery_classes;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Grocery implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final ArrayList<Product> products;

    public Grocery(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Product> getProducts(String category) {
        if (products.isEmpty())
            throw new NoSuchElementException("Grocery is empty");

        if (category.equals("-"))
            return products;
        else {
            ArrayList<Product> filteredProducts = new ArrayList<>();
            for (Product product : products)
                if (category.equals(product.category()))
                    filteredProducts.add(product);

            return filteredProducts;
        }
    }

    public ArrayList<String> getCategories() {
        if (products.isEmpty())
            throw new NoSuchElementException("Grocery is empty");

        ArrayList<String> categories = new ArrayList<>();
        for (Product product : products)
            if(!categories.contains(product.category()))
                categories.add(product.category());

        return categories;
    }

    public void addProduct(Product product) {
        products.add(product);
    }
}
