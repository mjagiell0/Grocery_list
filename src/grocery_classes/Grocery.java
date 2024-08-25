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

    public String[] getCategories() {
        if (products.isEmpty())
            throw new NoSuchElementException("Grocery is empty");

        String[] categories = new String[products.size() + 1];
        categories[0] = "-";
        for (int i = 0; i < categories.length; i++)
            categories[i + 1] = products.get(i).getCategory();

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
