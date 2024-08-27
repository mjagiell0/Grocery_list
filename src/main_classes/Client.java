package main_classes;

import GUI_forms.FormsHandler;
import GUI_forms.ProductForm;
import grocery_classes.GroceryClient;
import grocery_classes.GroceryList;
import notification_classes.Notification;
import static notification_classes.NotificationCode.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;


public class Client {
    public static void main(String[] args) throws IOException, InterruptedException, RuntimeException, ClassNotFoundException {
        try (Socket socket = new Socket("localhost", 2222)) {
            FormsHandler formsHandler = new FormsHandler();
            GroceryClient groceryClient;

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Notification notification = new Notification();

            int statusCode = 1;

            while (true) {
                if (statusCode == 1) {
                    formsHandler.getLoginForm().clearInputFields();
                    formsHandler.getLoginForm().setVisible(true);

                    while (statusCode == 1) {
                        Thread.sleep(100);

                        if (formsHandler.getLoginForm().isSignIn()) {
                            String login = formsHandler.getLoginForm().getLogin();
                            String password = formsHandler.getLoginForm().getPassword();

                            notification.setData(new String[]{login, password});
                            notification.setCode(SIGN_IN);

                            outputStream.writeObject(notification);
                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                groceryClient = (GroceryClient) notification.getData()[0];

                                formsHandler.getListsForm().setGroceryClient(groceryClient);
                                formsHandler.getLoginForm().setVisible(false);
                                statusCode = 2;
                            } else
                                System.out.println("Error: " + notification.getData()[0]);

                            formsHandler.getLoginForm().setSignIn(false);
                        }
                    }
                } else if (statusCode == 2) {
                    formsHandler.getListsForm().setVisible(true);
                    while (statusCode == 2) {

                    }
                }
            }
        }
    }
}
