package grocery_classes;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class GroceryList {
    private HashMap<Product, Double> productList;
    private String name;
    private final int id;

    public GroceryList(String name, int id, HashMap<Product, Double> productList) {
        this.name = name;
        this.id = id;
        this.productList = productList;
    }

    public GroceryList(String name, int id) {
        this.name = name;
        this.id = id;
        this.productList = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public HashMap<Product, Double> getProductList() {
        return productList;
    }

    public String[] getCategories() {
        if (productList.isEmpty())
            throw new NoSuchElementException("Grocery list is empty");

        String[] categories = new String[productList.size()];
        int i = 0;

        for (Product product : productList.keySet()) {
            categories[i] = product.getCategory();
            i++;
        }

        return categories;
    }

    public void setName(String name) {
        if (name.isBlank())
            throw new IllegalArgumentException("Name cannot be blank.");
        this.name = name;
    }

    public void setProductList(HashMap<Product, Double> productList) {
        this.productList = productList;
    }

    public void addProduct(Product product, double quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");
        productList.put(product, quantity);
    }

    public void removeProduct(Product product) {
        if (!productList.containsKey(product))
            throw new IllegalArgumentException("Product does not exist.");
        productList.remove(product);
    }

    public void removeAllProducts() {
        productList.clear();
    }

    public void setCustomQuantity(Product product, double quantity) {
        productList.put(product, quantity);
    }
}
