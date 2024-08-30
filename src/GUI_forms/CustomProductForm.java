package GUI_forms;

import measure_enums.Measure;

import static measure_enums.Measure.*;

import javax.swing.*;
import java.util.Objects;

public class CustomProductForm extends JFrame {
    private JPanel contentPane;
    private JTextField productNameField;
    private JTextField categoryNameField;
    private JComboBox<String> measureBox;
    private JTextField priceField;
    private JButton cancelButton;
    private JButton addButton;
    private JLabel message;

    private boolean add = false;
    private boolean cancel = false;

    private String tempProductName;
    private String tempCategoryName;
    private Measure tempMeasure;
    private double tempPrice;

    public CustomProductForm() {
        setTitle("Niestandardowy produkt");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("-");
        model.addElement("pcs");
        model.addElement("kg");
        model.addElement("l");
        model.addElement("m");
        measureBox.setModel(model);

        addButton.addActionListener(_ -> onAdd());
        cancelButton.addActionListener(_ -> onCancel());
    }

    private void onCancel() {
        setCancel(true);
        setMessage("");
    }

    private void onAdd() {
        tempProductName = productNameField.getText();
        tempCategoryName = categoryNameField.getText();
        String measureString = Objects.requireNonNull(measureBox.getSelectedItem()).toString();
        String priceString = priceField.getText();

        switch (measureString) {
            case "pcs" -> tempMeasure = pcs;
            case "l" -> tempMeasure = l;
            case "kg" -> tempMeasure = kg;
            case "m" -> tempMeasure = m;
        }

        if (tempProductName.isEmpty() || tempCategoryName.isEmpty() || measureString.equals("-") || priceString.isEmpty() )
            setMessage("Wypełnij wszystkie pola");
        else {
            tempPrice = Double.parseDouble(priceField.getText());
            if (tempPrice <= 0)
                setMessage("Nieprawidłowa wartość ceny");
            else {
                setAdd(true);
                setMessage("");
            }
        }
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isAdd() {
        return add;
    }

    public boolean isCancel() {
        return cancel;
    }

    public String getTempProductName() {
        return tempProductName;
    }

    public String getTempCategoryName() {
        return tempCategoryName;
    }

    public Measure getTempMeasure() {
        return tempMeasure;
    }

    public double getTempPrice() {
        return tempPrice;
    }
}
