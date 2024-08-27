package GUI_forms;

import grocery_classes.Grocery;
import grocery_classes.Product;
import measure_enums.Measure;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class GroceryForm extends JFrame {
    private JPanel contentPane;
    private JList<ProductForm> list;
    private DefaultListModel<ProductForm> listModel;
    private JButton addButton;
    private JButton cancelButton;
    private JComboBox<String> categoryBox;
    private DefaultComboBoxModel<String> categoryModel;

    private boolean add = false;
    private boolean cancel = false;

    private Grocery grocery;
    private String categoryFilter = "-";
    private Product tempProduct;
    private double tempValue;
    private HashMap<Product, Double> productsToAdd;

    public GroceryForm() {
        setTitle("Sklep");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        productsToAdd = new HashMap<>();
        listModel = new DefaultListModel<>();
        list.setModel(listModel);
        categoryModel = new DefaultComboBoxModel<>();
        categoryBox.setModel(categoryModel);

        addButton.addActionListener(_ -> onAdd());
        cancelButton.addActionListener(_ -> onCancel());

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                onMouse(e);
            }
        });
    }

    private void onCancel() {
        setCancel(true);
        setList();
    }

    private void onAdd() {
        setAdd(true);
        setList();
    }

    private void onMouse(MouseEvent e) {
        if (e.getClickCount() == 1) {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                ProductForm selectedProductForm = listModel.get(index);
                tempProduct = selectedProductForm.getProduct();

                if (productsToAdd.containsKey(tempProduct)) {
                    selectedProductForm.setCheckbox(false);
                    productsToAdd.remove(tempProduct);
                } else {
                    JTextField quantityField = new JTextField();

                    int option = JOptionPane.showConfirmDialog(null, quantityField, "Podaj ilość produktu", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        try {
                            tempValue = Double.parseDouble(quantityField.getText());
                            if (tempValue <= 0 || (tempValue % 1 != 0 && tempProduct.getMeasure().equals(Measure.pcs)))
                                JOptionPane.showMessageDialog(null, "Wprowadź poprawną liczbę", "Błąd", JOptionPane.ERROR_MESSAGE);
                            else {
                                selectedProductForm.setQuantity(tempValue);
                                selectedProductForm.setCheckbox(true);
                                productsToAdd.put(tempProduct, tempValue);
                                list.repaint();
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Wprowadź poprawną liczbę", "Błąd", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    }

    public void setList() {
        listModel.clear();
        for (Product product : grocery.getProducts(categoryFilter))
            listModel.addElement(new ProductForm(product, 0.0));

        list.repaint();
    }

    public void setGrocery(Grocery grocery) {
        this.grocery = grocery;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void setCategoryBox() {
        ArrayList<String> categories = grocery.getCategories();

        categoryModel.addElement("-");

        for (String category : categories)
            categoryModel.addElement(category);

        categoryBox.repaint();
    }

    public HashMap<Product, Double> getProductsToAdd() {
        return productsToAdd;
    }


    public boolean isCategory() {
        if (!categoryFilter.equals(categoryBox.getSelectedItem())) {
            categoryFilter = (String) categoryBox.getSelectedItem();
            return true;
        }
        return false;
    }

    public boolean isAdd() {
        return add;
    }

    public boolean isCancel() {
        return cancel;
    }
}
