package GUI_forms;

import grocery_classes.GroceryList;
import grocery_classes.Product;
import measure_enums.Measure;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.*;

public class GroceryListForm extends JFrame {
    private JPanel contentPane;
    private JList list;
    private DefaultListModel listModel;
    private JButton backButton;
    private JButton addButton;
    private JButton deleteButton;
    private JComboBox categoryBox;
    private DefaultComboBoxModel categoryModel;

    private boolean back = false;
    private boolean add = false;
    private boolean delete = false;
    private boolean quantity = false;
    private boolean category = false;

    private GroceryList groceryList;

    private List<String> tempList; // TODO: zmienić na niestandardowy rekord
    private double tempValue;
    private String tempProduct; // TODO: zmienić na niestandardowy rekord
    private String categoryFilter = "-";

    public GroceryListForm() {
        setTitle("Panel logowania");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        tempList = new ArrayList<>();
        listModel = new DefaultListModel();
        categoryModel = new DefaultComboBoxModel();

        list.setModel(listModel);
        categoryBox.setModel(categoryModel);

        // TODO: niestandardowy wygląd rekordów

        setVisible(true);
        backButton.addActionListener(_ -> setBack(true));
        addButton.addActionListener(_ -> setAdd(true));
        deleteButton.addActionListener(_ -> onDelete());

        list.addMouseListener(new MouseAdapter() {public void mouseClicked(MouseEvent e) {onMouse(e);}});
    }

    private void onDelete() {
        int option = JOptionPane.showConfirmDialog(this, "Na pewno usunąć " + tempList.size() + " list?", "Usuwanie list", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION)
            setDelete(true);
    }

    private void onMouse(MouseEvent e) {
        if (e.getClickCount() == 2) {
            tempProduct = (String) list.getSelectedValuesList().getLast();
            JTextField quantityField = new JTextField();

            int option = JOptionPane.showConfirmDialog(null, quantityField, "Nowa ilość produktu", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String quantity = quantityField.getText();
                tempValue = Double.parseDouble(quantity);

                if (tempValue <= 0)
                    throw new IllegalArgumentException("Quantity must be greater than 0");
                setQuantity(true);
            }
        } else if (e.getClickCount() == 1) {
            tempProduct = (String) list.getSelectedValuesList().getLast();
            if (tempList.contains(tempProduct))
                tempList.remove(tempProduct);
            else
                tempList.add(tempProduct); // TODO: Zaznaczenie na widoku, że produkt jest wybrany
        }
    }

    public void setBack(boolean back) {
        this.back = back;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public void setQuantity(boolean quantity) {
        this.quantity = quantity;
    }

    public void setCategory(boolean category) {
        this.category = category;
    }

    public void setGroceryList(GroceryList groceryList) {
        this.groceryList = groceryList;
    }

    public void setCategoryBox() {
        ArrayList<String> categories = groceryList.getCategories();

        categoryModel.addElement("-");

        for (String category : categories)
            categoryModel.addElement(category);

        list.setModel(categoryModel);
    }

    public void setListModel() {
        HashMap<Product, Double> productMap = groceryList.getProductList();
        Set<Product> products = productMap.keySet();

        for (Product product : products) {
            double quantity = productMap.get(product);
            listModel.addElement(product.getName() + ", " + quantity + ", " + product.getMeasure() + ", " + new DecimalFormat("#.##").format(quantity * product.getPricePerMeasure()));
        }

        list.setModel(listModel);
        list.repaint();
    }

    public void updateListView() {

    }

    public boolean isBack() {
        return back;
    }

    public boolean isAdd() {
        return add;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isQuantity() {
        return quantity;
    }

    public boolean isCategory() {
        if (!categoryFilter.equals(categoryBox.getSelectedItem())) {
            categoryFilter = (String) categoryBox.getSelectedItem();
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        GroceryListForm frame = new GroceryListForm();
        HashMap<Product, Double> hashMap = new HashMap<>();
        Product[] products = {new Product("Jabłko", "Jedzenie", Measure.kg, 2.99, 1),
                new Product("Jajko", "Jedzenie", Measure.pcs, 6.99, 2),
                new Product("Telefon", "Elektronika", Measure.pcs, 699.99, 3),
                new Product("Olej napędowy", "Części samochodowe", Measure.l, 8.99, 4)};

        for (int i = 0; i < 4; i++)
            hashMap.put(products[i], (double) i + 2);


        GroceryList groceryList = new GroceryList("Lista zakupów", 1, hashMap);

        frame.setGroceryList(groceryList);
        frame.setCategoryBox();
        frame.setListModel();
        frame.setVisible(true);
    }
}
