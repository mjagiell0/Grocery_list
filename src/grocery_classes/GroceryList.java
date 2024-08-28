package grocery_classes;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class GroceryList implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private HashMap<Product, Double> productList;
    private String name;
    private final int id;

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

    public ArrayList<String> getCategories() {
        if (productList.isEmpty())
            throw new NoSuchElementException("Grocery list is empty");

        ArrayList<String> categories = new ArrayList<>();

        for (Product product : productList.keySet())
            if (!categories.contains(product.getCategory()))
                categories.add(product.getCategory());

        return categories;
    }

    public void setName(String name) {
        if (name.isBlank())
            throw new IllegalArgumentException("Name cannot be blank.");
        this.name = name;
    }

    public void addProduct(Product product, double quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");
        productList.put(product, quantity);
    }

    public void removeProduct(int id) {
        for (Product product : productList.keySet())
            if (product.getId() == id) {
                productList.remove(product);
                break;
            }
    }

    public void setCustomQuantity(int productId, double quantity) {
        for (Product product : productList.keySet()){
            if (product.getId() == productId) {
                productList.put(product, quantity);
                break;
            }
        }
    }
}
