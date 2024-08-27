package main_classes;

import grocery_classes.GroceryClient;
import grocery_classes.GroceryList;
import grocery_classes.Product;
import measure_enums.Measure;
import notification_classes.Notification;
import notification_classes.NotificationCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import static notification_classes.NotificationCode.*;
import static measure_enums.Measure.*;

public class Server {
    private final static DatabaseHandler databaseHandler;

    static {
        try {
            databaseHandler = new DatabaseHandler();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(2222)) {
            System.out.println("Server listening on port 2222...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection with: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();


            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e);
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

                while (true) {
                    Thread.sleep(100);
                    Notification notification = (Notification) inputStream.readObject();
                    NotificationCode code = notification.getCode();

                    if (code == SIGN_IN) {
                        signIn(notification, outputStream);
                    }

                }
            } catch (IOException | ClassNotFoundException | InterruptedException | SQLException e) {
                System.out.println("Client exception: " + e.getMessage());
            }
        }

        private static void signIn(Notification notification, ObjectOutputStream outputStream) throws SQLException, IOException {
            notification.setCode(SUCCESS);

            String login = (String) notification.getData()[0];
            String password = (String) notification.getData()[1];
            String sql = "{CALL CheckUserCredentials(?,?,?,?)}";

            int resultCode, userId;

            try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                statement.setString(1, login);
                statement.setString(2, password);
                statement.registerOutParameter(3, Types.INTEGER);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.execute();

                userId = statement.getInt(3);
                resultCode = statement.getInt(4);
            }

            if (resultCode == 1) {
                notification.setCode(ERROR);
                notification.setData(new String[]{"User does not exist"});
            } else if (resultCode == 2) {
                notification.setCode(ERROR);
                notification.setData(new String[]{"Incorrect login data"});
            } else {
                sql = "SELECT\n" +
                        "    l.ListName, l.ListID, p.ProductName, p.ProductID, p.CategoryName, p.Measure, p.Price, li.Amount\n" +
                        "FROM Lists l\n" +
                        "    JOIN UsersLists ul ON l.ListID = ul.ListID\n" +
                        "    JOIN ListItems li on l.ListID = li.ListID\n" +
                        "    JOIN Products p on li.ProductID = p.ProductID\n" +
                        "WHERE\n" +
                        "    ul.UserID = ?;";

                HashMap<Integer, GroceryList> groceryListHashMap = new HashMap<>();

                try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                    statement.setInt(1, userId);

                    statement.execute();

                    ResultSet resultSet = statement.getResultSet();

                    while (resultSet.next()) {
                        String listName = resultSet.getString("ListName");
                        int listID = resultSet.getInt("ListID");
                        String productName = resultSet.getString("ProductName");
                        int productID = resultSet.getInt("ProductID");
                        String categoryName = resultSet.getString("CategoryName");
                        String measureType = resultSet.getString("Measure");
                        double price = resultSet.getDouble("Price");
                        double quantity = resultSet.getDouble("Amount");

                        Measure measure = null;

                        switch (measureType) {
                            case "pcs" -> measure = pcs;
                            case "kg" -> measure = kg;
                            case "l" -> measure = l;
                        }

                        Product product = new Product(productName, categoryName, measure, price, productID);

                        GroceryList groceryList;

                        if (!groceryListHashMap.containsKey(listID)) {
                            groceryList = new GroceryList(listName, listID);
                            groceryListHashMap.put(listID, groceryList);
                        } else
                            groceryList = groceryListHashMap.get(listID);

                        groceryList.addProduct(product, quantity);
                    }
                }

                ArrayList<GroceryList> groceryLists = new ArrayList<>(groceryListHashMap.values());
                GroceryClient groceryClient = new GroceryClient(userId, login, groceryLists);
                notification.setData(new Object[]{groceryClient});
            }

            outputStream.writeObject(notification);
        }
    }
}
