package grocery_classes;

import java.util.ArrayList;

public class GroceryClient {
    private ArrayList<GroceryList> groceryLists;
    private String userName;
    private int id;

    public GroceryClient() {}

    public ArrayList<GroceryList> getGroceryLists() {
        return groceryLists;
    }

    public String getUserName() {
        return userName;
    }

    public int getId() {
        return id;
    }

    public void setGroceryLists(ArrayList<GroceryList> groceryLists) {
        this.groceryLists = groceryLists;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void removeGroceryList(int id) {
        groceryLists.removeIf(groceryList -> groceryList.getId() == id);
    }

    public void removeAll() {
        groceryLists.clear();
    }

    public void addGroceryList(GroceryList groceryList) {
        groceryLists.add(groceryList);
    }
}
