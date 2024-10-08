package GUI_forms;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginForm extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JButton signUpButton;
    private JLabel message;
    private JPanel contentPane;

    private boolean signIn = false;
    private boolean signUp = false;

    public LoginForm() {
        setTitle("Panel logowania");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        signInButton.addActionListener(_ -> onSignIn());
        signUpButton.addActionListener(_ -> onSignUp());

        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    signInButton.doClick();
            }
        };

        loginField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }

    private void onSignUp() {
        if (loginField.getText().isBlank() || !passwordField.echoCharIsSet())
            setMessage("Pola nie mogą być puste");
        else {
            JPasswordField confirmPasswordField = new JPasswordField();
            int option = JOptionPane.showConfirmDialog(this, confirmPasswordField,
                    "Potwierdź hasło", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (password.equals(confirmPassword)) {
                    setSignUp(true);
                    setMessage("Pomyślnie zarejestrowano");
                } else
                    setMessage("Hasła się nie zgadzają");

            }
        }
    }

    private void onSignIn() {
        if (loginField.getText().isBlank() || !passwordField.echoCharIsSet())
            setMessage("Pola nie mogą być puste");
        else {
            setSignIn(true);
            setMessage("Pomyślnie zalogowano");
        }
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public String getLogin() {
        return loginField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setSignIn(boolean isSignIn) {
        this.signIn = isSignIn;
    }

    public void setSignUp(boolean isSignUp) {
        this.signUp = isSignUp;
    }

    public boolean isSignIn() {
        return signIn;
    }

    public boolean isSignUp() {
        return signUp;
    }

    public void clearInputFields(){
        loginField.setText("");
        passwordField.setText("");
    }
}
