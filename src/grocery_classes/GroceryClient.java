package grocery_classes;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class GroceryClient implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ArrayList<GroceryList> groceryLists;
    private String userName;
    private int id;

    public GroceryClient(int id, String userName, ArrayList<GroceryList> groceryLists) {
        this.id = id;
        this.userName = userName;
        this.groceryLists = groceryLists;
    }

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

    public GroceryList getGroceryList(int id) {
        for (GroceryList list : getGroceryLists())
            if (list.getId() == id)
                return list;

        throw new ArrayStoreException("Client has no grocery list with id: " + id);
    }

    public String getUserName() {
        return userName;
    }

    public int getId() {
        return id;
    }

    public void removeGroceryList(int id) {
        groceryLists.removeIf(groceryList -> groceryList.getId() == id);
    }

    public void addGroceryList(GroceryList groceryList) {
        groceryLists.add(groceryList);
    }
}
