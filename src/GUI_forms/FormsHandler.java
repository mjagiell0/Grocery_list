package GUI_forms;

public class FormsHandler {
    private final LoginForm loginForm;
    private final ListsForm listsForm;
    private final GroceryListForm groceryListForm;
    private final GroceryForm groceryForm;

    public FormsHandler() {
        loginForm = new LoginForm();
        listsForm = new ListsForm();
        groceryForm = new GroceryForm();
        groceryListForm = new GroceryListForm();
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public ListsForm getListsForm() {
        return listsForm;
    }

    public GroceryListForm getGroceryListForm() {
        return groceryListForm;
    }

    public GroceryForm getGroceryForm() {
        return groceryForm;
    }
}
