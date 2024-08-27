package GUI_forms;

import grocery_classes.GroceryClient;
import grocery_classes.GroceryList;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ListsForm extends JFrame {
    private JPanel contentPane;
    private JList list;
    private DefaultListModel<String> model;
    private JButton logOutButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton changeNameButton;
    private JButton shareButton;

    private boolean loggedOut = false;
    private boolean add = false;
    private boolean delete = false;
    private boolean changeName = false;
    private boolean share = false;
    private boolean grocery = false;

    private String tempListName;
    private int tempId;

    private GroceryClient groceryClient;

    public ListsForm() {
        setTitle("Listy zakupów");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        model = new DefaultListModel<>();

        logOutButton.addActionListener(_ ->setLoggedOut(true));
        addButton.addActionListener(_ -> onAdd());
        deleteButton.addActionListener(_ -> onDelete());
        changeNameButton.addActionListener(_ -> onChangeName());
        shareButton.addActionListener(_ -> onShare());
        list.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) {onMouse(e);}});
    }

    private void onMouse(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int index = list.locationToIndex(e.getPoint());
            if (index >= 0) {
                tempListName = model.getElementAt(index);
                tempId = groceryClient.getGroceryList(tempListName).getId();
                setGrocery(true);
            }
        }
    }

    private void onShare() {
        int option = JOptionPane.showConfirmDialog(this,"Na pewno udostępnić " + model.size() +" list?","Udostępnianie list",JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION)
            setShare(true);
    }

    private void onChangeName() {
        JTextField listName = new JTextField();
        int option = JOptionPane.showConfirmDialog(this,listName,"Nowa nazwa listy",JOptionPane.OK_CANCEL_OPTION);

        if(option == JOptionPane.OK_OPTION){
            tempListName = listName.getText();
            setChangeName(true);
        }
    }

    private void onDelete() {
        int option = JOptionPane.showConfirmDialog(this,"Na pewno usunąć " + list.getSelectedValuesList().size() + " list?", "Usuwanie list", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION)
            setDelete(true);
    }

    private void onAdd() {
        JTextField listName = new JPasswordField(10);
        int option = JOptionPane.showConfirmDialog(this, listName,
                "Nazwa nowej listy", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            tempListName = listName.getText();
            setAdd(true);
        }
    }

    public void setLoggedOut(boolean loggedOut) {
        this.loggedOut = loggedOut;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public void setChangeName(boolean changeName) {
        this.changeName = changeName;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public void setGrocery(boolean grocery) {
        this.grocery = grocery;
    }

    public void setGroceryClient(GroceryClient groceryClient) {
        this.groceryClient = groceryClient;

        model.clear();
        model.addAll(groceryClient.getGroceryListNames());

        list.setModel(model);
        list.repaint();
    }

    public void setButtonsEnable() {
        deleteButton.setEnabled(isSomeGroceryListSelected());
        changeNameButton.setEnabled(isOneGroceryListSelected());
        shareButton.setEnabled(isSomeGroceryListSelected());
    }

    public List<String> getSelectedGroceryList() {
        if (model.isEmpty())
            throw new NoSuchElementException("No grocery list selected");

        List<String> list = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++)
            list.add(model.getElementAt(i));

        return list;
    }

    public List<Integer> getSelectedGroceryListIDs() {
        if (model.isEmpty())
            throw new NoSuchElementException("No grocery list selected");

        List<Integer> list = new ArrayList<>();
        for (GroceryList groceryList : groceryClient.getGroceryLists())
            if (model.contains(groceryList.getName()))
                list.add(groceryList.getId());

        return list;
    }

    public String getTempListName() {
        return tempListName;
    }

    public int getTempId() {
        return tempId;
    }

    public boolean isLoggedOut() {
        return loggedOut;
    }

    public boolean isAdd() {
        return add;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isChangeName() {
        return changeName;
    }

    public boolean isShare() {
        return share;
    }

    public boolean isGrocery() {
        return grocery;
    }

    public boolean isSomeGroceryListSelected() {
        return !list.getSelectedValuesList().isEmpty();
    }

    public boolean isOneGroceryListSelected() {
        return list.getSelectedValuesList().size() == 1;
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(ListsForm::new);
    }
}
