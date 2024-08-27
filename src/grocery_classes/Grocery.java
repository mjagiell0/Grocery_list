package grocery_classes;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Grocery {
    private ArrayList<Product> products;

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
                if (category.equals(product.getCategory()))
                    filteredProducts.add(product);

            return filteredProducts;
        }
    }

    public ArrayList<String> getCategories() {
        if (products.isEmpty())
            throw new NoSuchElementException("Grocery is empty");

        ArrayList<String> categories = new ArrayList<>();
        categories.add("-");
        for (Product product : products)
            if(!categories.contains(product.getCategory()))
                categories.add(product.getCategory());

        return categories;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public void removeAll() {
        products.clear();
    }
}
