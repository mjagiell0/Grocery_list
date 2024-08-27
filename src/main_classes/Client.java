package main_classes;

import GUI_forms.FormsHandler;
import grocery_classes.GroceryClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", 8080)) {
            FormsHandler formsHandler = new FormsHandler();
            GroceryClient groceryClient = new GroceryClient();

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            int statusCode = 1;

            if (statusCode == 1) {

            }
        }
    }
}
