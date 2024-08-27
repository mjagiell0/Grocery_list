package GUI_forms;

import grocery_classes.Product;

import javax.swing.*;
import java.text.DecimalFormat;

public class ProductForm extends JFrame{
    private JPanel contentPane;
    private JCheckBox checkBox;
    private JLabel productLabel;
    private JLabel quantityLabel;
    private JLabel priceLabel;

    private final Product product;

    public ProductForm(Product product, double quantity) {
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        this.product = product;

        productLabel.setText(product.getName());
        quantityLabel.setText(new DecimalFormat("#.##").format(quantity) + " " + product.getMeasure());
        priceLabel.setText(new DecimalFormat("#.##").format(product.getPricePerMeasure() * quantity) + " zł");
    }

    public void setCheckbox(boolean state) {
        checkBox.setSelected(state);
    }

    public void setQuantity(double quantity) {
        quantityLabel.setText(new DecimalFormat("#.##").format(quantity) + " " + product.getMeasure());
        priceLabel.setText(new DecimalFormat("#.##").format(product.getPricePerMeasure() * quantity) + " zł");
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }

    public Product getProduct() {
        return product;
    }
}
