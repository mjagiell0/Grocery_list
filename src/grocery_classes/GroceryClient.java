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

    public ArrayList<String> getGroceryListNames() {
        if (groceryLists.isEmpty())
            throw new ArrayStoreException("Client has no grocery lists");

        ArrayList<String> groceryListNames = new ArrayList<>();

        for (GroceryList list : getGroceryLists())
            groceryListNames.add(list.getName());

        return groceryListNames;
    }

    public GroceryList getGroceryList(String listName) {
        for (GroceryList list : getGroceryLists())
            if (list.getName().equals(listName))
                return list;

        throw new ArrayStoreException("Client has no grocery list with name: " + listName);
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
