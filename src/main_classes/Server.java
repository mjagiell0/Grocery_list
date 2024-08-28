package main_classes;

import grocery_classes.Grocery;
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
import java.util.List;

import static notification_classes.NotificationCode.*;
import static measure_enums.Measure.*;

public class Server {
    private final static DatabaseHandler databaseHandler;
    private static Grocery grocery;

    static {
        try {
            databaseHandler = new DatabaseHandler();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SQLException {
        groceryInit();

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

    private static void groceryInit() throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products";

        try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next()) {
                int productId = resultSet.getInt(1);
                String productName = resultSet.getString(2);
                String category = resultSet.getString(3);
                Measure measure = null;

                switch (resultSet.getString(4)) {
                    case "pcs" -> measure = pcs;
                    case "kg" -> measure = kg;
                    case "l" -> measure = l;
                }

                double price = resultSet.getDouble(5);

                Product product = new Product(productName, category, measure, price, productId);

                products.add(product);
            }
        }

        grocery = new Grocery(products);
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
                outputStream.writeObject(grocery);

                while (true) {
                    Thread.sleep(100);
                    Notification notification = (Notification) inputStream.readObject();
                    NotificationCode code = notification.getCode();

                    if (code == SIGN_IN)
                        signIn(notification, outputStream);
                    else if (code == ADD_LIST)
                        addList(notification, outputStream);
                    else if (code == DELETE_LIST)
                        deleteList(notification, outputStream);
                    else if (code == CHANGE_LIST_NAME)
                        changeListName(notification, outputStream);
                    else if (code == SHARE_LIST)
                        shareList(notification, outputStream);
                    else if (code == ADD_PRODUCT)
                        addProduct(notification, outputStream);
                    else if (code == DELETE_PRODUCT)
                        deleteProduct(notification, outputStream);
                    else if (code == CHANGE_QUANTITY)
                        changeQuantity(notification, outputStream);
                }
            } catch (IOException | ClassNotFoundException | InterruptedException | SQLException e) {
                System.out.println("Client exception: " + e.getMessage());
            }
        }

        private static void changeQuantity(Notification notification, ObjectOutputStream outputStream) throws SQLException, IOException {
            int listId = (int) notification.getData()[0];
            int productId = (int) notification.getData()[1];
            double quantity = (double) notification.getData()[2];
            String sql = "{CALL ChangeCount(?,?,?)}";

            try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                statement.setInt(1, listId);
                statement.setInt(2, productId);
                statement.setDouble(3, quantity);

                statement.execute();
            }

            notification.setCode(SUCCESS);

            outputStream.writeObject(notification);
        }

        private static void deleteProduct(Notification notification, ObjectOutputStream outputStream) throws SQLException, IOException {
            int listId = (int) notification.getData()[0];
            List<Integer> productsIDs = (List<Integer>) notification.getData()[1];
            String sql = "{CALL RemoveItem(?,?,?)}";

            int resultCode;
            boolean flag = false;

            for (Integer productID : productsIDs) {
                try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                    statement.setInt(1, listId);
                    statement.setInt(2, productID);
                    statement.registerOutParameter(3, Types.INTEGER);

                    statement.execute();

                    resultCode = statement.getInt(3);
                }

                if (resultCode != 0 && !flag) {
                    notification.setCode(ERROR);
                    notification.setData(new String[]{"Unsuccessful deleting some products in list " + listId});
                }
            }

            if (!flag)
                notification.setCode(SUCCESS);

            outputStream.writeObject(notification);
        }

        private static void addProduct(Notification notification, ObjectOutputStream outputStream) throws SQLException, IOException {
            int listId = (int) notification.getData()[0];
            HashMap<Product, Double> productsToAdd = (HashMap<Product, Double>) notification.getData()[1];
            String sql = "{CALL AddItem(?,?,?,?)}";

            int resultCode;
            boolean flag = false;

            for (Product product : productsToAdd.keySet()) {
                try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                    statement.setInt(1, listId);
                    statement.setInt(2, product.getId());
                    statement.setDouble(3, productsToAdd.get(product));
                    statement.registerOutParameter(4, Types.INTEGER);

                    statement.execute();

                    resultCode = statement.getInt(4);
                }

                if (resultCode == 0 && !flag) {
                    notification.setCode(ERROR);
                    notification.setData(new String[]{"Unsuccessful adding product to list " + listId});
                    flag = true;
                }
            }

            if (!flag)
                notification.setCode(SUCCESS);

            outputStream.writeObject(notification);
        }

        private static void shareList(Notification notification, ObjectOutputStream outputStream) throws SQLException, IOException {
            String userName = (String) notification.getData()[0];
            ArrayList<Integer> list = (ArrayList<Integer>) notification.getData()[1];
            String sql = "{CALL AccessToList(?,?,?)}";

            int resultCode;
            boolean flag = false;

            for (Integer listId : list) {
                try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                    statement.setInt(1, listId);
                    statement.setString(2, userName);
                    statement.registerOutParameter(3, Types.INTEGER);

                    statement.execute();

                    resultCode = statement.getInt(3);
                }

                if (resultCode == -1) {
                    notification.setCode(ERROR);
                    notification.setData(new String[]{"User not found"});
                    flag = true;
                } else if (resultCode == -2) {
                    notification.setCode(ERROR);
                    notification.setData(new String[]{"User already has access to list"});
                    flag = true;
                }
            }

            if (!flag)
                notification.setCode(SUCCESS);

            outputStream.writeObject(notification);
        }

        private static void changeListName(Notification notification, ObjectOutputStream outputStream) throws SQLException, IOException {
            int userId = (int) notification.getData()[0];
            int listId = (int) notification.getData()[1];
            String listName = (String) notification.getData()[2];
            String sql = "{CALL ChangeListName(?,?,?,?)}";

            int resultCode;

            try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                statement.setInt(1, userId);
                statement.setInt(2, listId);
                statement.setString(3, listName);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.execute();

                resultCode = statement.getInt(4);
            }
            if (resultCode == 0)
                notification.setCode(SUCCESS);
            else
                notification.setCode(ERROR);

            outputStream.writeObject(notification);
        }

        private static void deleteList(Notification notification, ObjectOutputStream outputStream) throws SQLException, IOException {
            int userId = (int) notification.getData()[0];
            ArrayList<Integer> listIDs = (ArrayList<Integer>) notification.getData()[1];
            String sql = "{CALL RemoveList(?,?,?)}";

            int resultCode;
            boolean flag = false;
            ArrayList<Integer> deletedLists = new ArrayList<>();

            for (Integer listId : listIDs) {
                try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                    statement.setInt(1, userId);
                    statement.setInt(2, listId);
                    statement.registerOutParameter(3, Types.INTEGER);

                    statement.execute();

                    resultCode = statement.getInt(3);

                    if (resultCode == 1)
                        flag = true;
                    else if (resultCode == 0)
                        deletedLists.add(listId);
                }
            }

            if (flag) {
                notification.setCode(ERROR);
                notification.setData(new Object[]{deletedLists});
            } else
                notification.setCode(SUCCESS);

            outputStream.writeObject(notification);
        }

        private static void addList(Notification notification, ObjectOutputStream outputStream) throws SQLException, IOException {
            String listName = (String) notification.getData()[0];
            int userId = (int) notification.getData()[1];
            String sql = "{CALL AddList(?,?,?,?)}";

            int resultCode, listId;

            try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                statement.setInt(1, userId);
                statement.setString(2, listName);
                statement.registerOutParameter(3, Types.INTEGER);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.execute();

                listId = statement.getInt(3);
                resultCode = statement.getInt(4);
            }

            if (resultCode == 0) {
                notification.setCode(ERROR);
                notification.setData(new String[]{"Unsuccessful adding list " + listName});
            } else if (resultCode == -1) {
                notification.setCode(ERROR);
                notification.setData(new String[]{"List " + listName + " already exists"});
            } else {
                notification.setCode(SUCCESS);
                notification.setData(new Integer[]{listId});
            }

            outputStream.writeObject(notification);
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
                sql = "SELECT ul.ListID, l.ListName FROM UsersLists ul JOIN Lists l ON ul.ListID = l.ListID WHERE ul.UserID = ?;";

                ArrayList<GroceryList> groceryLists = new ArrayList<>();

                try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                    statement.setInt(1, userId);

                    statement.execute();

                    ResultSet resultSet = statement.getResultSet();

                    while (resultSet.next()) {
                        String listName = resultSet.getString("ListName");
                        int listID = resultSet.getInt("ListID");

                        GroceryList groceryList = new GroceryList(listName, listID);
                        groceryLists.add(groceryList);
                    }
                }

                sql = "SELECT p.ProductName, p.CategoryName, li.Amount, p.Measure, p.Price, p.ProductID FROM ListItems li JOIN Products p ON li.ProductID = p.ProductID WHERE ListID = ?;";

                for (GroceryList groceryList : groceryLists) {
                    try (CallableStatement statement = databaseHandler.getConnection().prepareCall(sql)) {
                        statement.setInt(1, groceryList.getId());

                        statement.execute();

                        ResultSet resultSet = statement.getResultSet();

                        while (resultSet.next()) {
                            String productName = resultSet.getString("ProductName");
                            String categoryName = resultSet.getString("CategoryName");
                            double quantity = resultSet.getDouble("Amount");
                            double price = resultSet.getDouble("Price");
                            int productID = resultSet.getInt("ProductID");
                            Measure measure = null;

                            switch (resultSet.getString("Measure")) {
                                case "pcs" -> measure = pcs;
                                case "kg" -> measure = kg;
                                case "l" -> measure = l;
                            }

                            Product product = new Product(productName, categoryName, measure, price, productID);

                            groceryList.addProduct(product, quantity);
                        }
                    }
                }


                GroceryClient groceryClient = new GroceryClient(userId, login, groceryLists);
                notification.setData(new Object[]{groceryClient});
            }

            outputStream.writeObject(notification);
        }
    }
}
