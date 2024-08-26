package GUI_forms;

import grocery_classes.Product;
import measure_enums.Measure;

import javax.swing.*;
import java.text.DecimalFormat;

public class ProductForm extends JFrame{
    private JPanel contentPane;
    private JCheckBox checkBox;
    private JLabel productLabel;
    private JLabel quantityLabel;
    private JLabel priceLabel;

    private final Measure measure;
    private final double pricePerMeasure;

    public ProductForm(Product product, double quantity) {
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);

        measure = product.getMeasure();
        pricePerMeasure = product.getPricePerMeasure();
        productLabel.setText(product.getName());
        quantityLabel.setText(new DecimalFormat("#.##").format(quantity) + measure);
        priceLabel.setText(new DecimalFormat("$.$$").format(pricePerMeasure * quantity) + " zł");
    }

    public void setCheckbox(boolean state) {
        checkBox.setSelected(state);
    }

    public void setQuantity(double quantity) {
        quantityLabel.setText(new DecimalFormat("#.##").format(quantity) + measure);
        priceLabel.setText(new DecimalFormat("$.$$").format(pricePerMeasure * quantity) + " zł");
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }
}
