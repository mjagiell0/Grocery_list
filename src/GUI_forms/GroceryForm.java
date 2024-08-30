package GUI_forms;

import grocery_classes.Grocery;
import grocery_classes.Product;
import measure_enums.Measure;

import javax.swing.*;
import java.awt.*;
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
    private JButton customAddButton;
    private final DefaultComboBoxModel<String> categoryModel;

    private boolean add = false;
    private boolean cancel = false;
    private boolean customAdd = false;

    private Grocery grocery;
    private String categoryFilter = "-";
    private Product tempProduct;
    private double tempValue;
    private final HashMap<Product, Double> productsToAdd;

    public GroceryForm() {
        setTitle("Sklep");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        productsToAdd = new HashMap<>();
        listModel = new DefaultListModel<>();
        list.setModel(listModel);
        list.setCellRenderer(new ProductFormRenderer());
        categoryModel = new DefaultComboBoxModel<>();
        categoryBox.setModel(categoryModel);

        addButton.addActionListener(_ -> onAdd());
        cancelButton.addActionListener(_ -> onCancel());
        customAddButton.addActionListener(_ -> setCustomAdd(true));

        list.addMouseListener(new MouseAdapter() {
            @Override
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
                    productsToAdd.remove(tempProduct);
                    selectedProductForm.setCheckbox(false);
                    selectedProductForm.setQuantity(0);
                } else {
                    String quantityStr = JOptionPane.showInputDialog(this, "Podaj ilość produktu", "Ilość", JOptionPane.PLAIN_MESSAGE);
                    if (quantityStr != null) {
                        try {
                            tempValue = Double.parseDouble(quantityStr);
                            if (tempValue <= 0 || (tempValue % 1 != 0 && tempProduct.getMeasure().equals(Measure.pcs))) {
                                JOptionPane.showMessageDialog(this, "Wprowadź poprawną liczbę", "Błąd", JOptionPane.ERROR_MESSAGE);
                            } else {
                                productsToAdd.put(tempProduct, tempValue);
                                selectedProductForm.setQuantity(tempValue);
                                selectedProductForm.setCheckbox(true);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Wprowadź poprawną liczbę", "Błąd", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                list.repaint();
            }
        }
    }

    public void setList() {
        list.setEnabled(false);

        this.listModel.clear();
        DefaultListModel<ProductForm> listModel = new DefaultListModel<>();

        ArrayList<Product> products = grocery.getProducts(categoryFilter);

        for (Product product : products) {
            ProductForm productForm = new ProductForm(product, productsToAdd.getOrDefault(product, 0.0));
            if (productsToAdd.containsKey(product)) {
                productForm.setCheckbox(true);
            }
            listModel.addElement(productForm);
        }
        list.setModel(listModel);
        this.listModel = listModel;
        list.setEnabled(true);

        list.repaint();
    }


    public void setGrocery(Grocery grocery) {
        this.grocery = grocery;
        setCategoryBox();
        setList();
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void setCustomAdd(boolean customAdd) {
        this.customAdd = customAdd;
    }

    public void setCategoryBox() {
        categoryModel.removeAllElements();
        ArrayList<String> categories = grocery.getCategories();

        categoryModel.addElement("-");
        for (String category : categories) {
            categoryModel.addElement(category);
        }
    }

    public HashMap<Product, Double> getProductsToAdd() {
        return productsToAdd;
    }

    public Grocery getGrocery() {
        return grocery;
    }

    public boolean isCategoryChanged() {
        String selectedCategory = (String) categoryBox.getSelectedItem();
        if (!categoryFilter.equals(selectedCategory)) {
            categoryFilter = selectedCategory;

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

    public boolean isCustomAdd() {
        return customAdd;
    }

    public void clean() {
        productsToAdd.clear();
        listModel.clear();
        list.repaint();
    }

    private static class ProductFormRenderer implements ListCellRenderer<ProductForm> {
        @Override
        public Component getListCellRendererComponent(JList<? extends ProductForm> list,
                                                      ProductForm value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            JPanel panel = (JPanel) value.getContentPane();

            if (isSelected) {
                panel.setBackground(list.getSelectionBackground());
            } else {
                panel.setBackground(list.getBackground());
            }

            return panel;
        }
    }
}
