package GUI_forms;

import grocery_classes.GroceryList;
import grocery_classes.Product;
import measure_enums.Measure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class GroceryListForm extends JFrame {
    private JPanel contentPane;
    private JList<ProductForm> list;
    private DefaultListModel<ProductForm> listModel;
    private JButton backButton;
    private JButton addButton;
    private JButton deleteButton;
    private JComboBox<String> categoryBox;
    private JButton selectButton;
    private DefaultComboBoxModel<String> categoryModel;

    private boolean back = false;
    private boolean add = false;
    private boolean delete = false;
    private boolean quantity = false;

    private GroceryList groceryList;

    private List<Product> tempList;
    private double tempValue;
    private Product tempProduct;
    private String categoryFilter = "-";

    public GroceryListForm() {
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        tempList = new ArrayList<>();
        listModel = new DefaultListModel<>();
        categoryModel = new DefaultComboBoxModel<>();

        list.setModel(listModel);
        list.setCellRenderer(new ProductFormRenderer());
        categoryBox.setModel(categoryModel);

        backButton.addActionListener(_ -> onBack());
        addButton.addActionListener(_ -> setAdd(true));
        deleteButton.addActionListener(_ -> onDelete());
        selectButton.addActionListener(_ -> onSelect());

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                onMouse(e);
            }
        });
    }

    private void onBack() {
        setBack(true);
        categoryModel.removeAllElements();
        listModel.removeAllElements();
    }

    private void onSelect() {
        for (int i = 0; i < listModel.getSize(); i++) {
            ProductForm productForm = listModel.getElementAt(i);
            if (categoryFilter.equals("-") || productForm.getProduct().getCategory().equals(categoryFilter)) {
                productForm.setCheckbox(!productForm.isChecked());
                if (productForm.isChecked())
                    tempList.add(productForm.getProduct());
                else
                    tempList.remove(productForm.getProduct());
            }
        }
        list.repaint();
    }


    private void onDelete() {
        int option = JOptionPane.showConfirmDialog(this, "Na pewno usunąć " + tempList.size() + " produkt/y?", "Usuwanie list", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION)
            setDelete(true);
    }

    private void onMouse(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int index = list.getSelectedIndex();
            if (index >= 0 && listModel.get(index).isChecked()) {
                ProductForm selectedProductForm = listModel.get(index);
                tempProduct = selectedProductForm.getProduct();
                JTextField quantityField = new JTextField();

                int option = JOptionPane.showConfirmDialog(null, quantityField, "Nowa ilość produktu", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        tempValue = Double.parseDouble(quantityField.getText());
                        if (tempValue <= 0 || (tempValue % 1 != 0 && tempProduct.getMeasure().equals(Measure.pcs)))
                            JOptionPane.showMessageDialog(null, "Wprowadź poprawną liczbę", "Błąd", JOptionPane.ERROR_MESSAGE);
                        else {
                            selectedProductForm.setQuantity(tempValue);
                            setQuantity(true);
                            list.repaint();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Wprowadź poprawną liczbę", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else if (e.getClickCount() == 1) {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                ProductForm selectedProductForm = listModel.get(index);
                tempProduct = selectedProductForm.getProduct();
                if (tempList.contains(tempProduct)) {
                    tempList.remove(tempProduct);
                    selectedProductForm.setCheckbox(false);
                } else {
                    tempList.add(tempProduct);
                    selectedProductForm.setCheckbox(true);
                }
                list.repaint();
            }
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

    public void setRemoveEnable() {
        deleteButton.setEnabled(!list.getSelectedValuesList().isEmpty());
    }

    public int getGroceryListId() {
        return groceryList.getId();
    }

    public void setGroceryList(GroceryList groceryList) {
        this.groceryList = groceryList;
        setTitle(groceryList.getName());
        setListModel();
        setCategoryBox();
    }

    public void setCategoryBox() {
        try {
            ArrayList<String> categories = groceryList.getCategories();

            categoryModel.addElement("-");

            for (String category : categories)
                categoryModel.addElement(category);

            categoryBox.repaint();
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setListModel() {
        list.setEnabled(false);

        this.listModel.clear();
        DefaultListModel<ProductForm> listModel = new DefaultListModel<>();

        HashMap<Product, Double> productMap = groceryList.getProductList();
        Set<Product> products = productMap.keySet();

        for (Product product : products) {
            if (product.getCategory().equals(categoryFilter) || categoryFilter.equals("-")) {
                double quantity = productMap.get(product);
                listModel.addElement(new ProductForm(product, quantity));
            }
        }
        list.setModel(listModel);
        this.listModel = listModel;
        list.setEnabled(true);

        list.repaint();
    }

    public void updateListView(GroceryList groceryList) {
        setGroceryList(groceryList);
        setCategoryBox();
        setListModel();
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
            tempList.clear();
            return true;
        }
        return false;
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