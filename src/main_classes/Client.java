package main_classes;

import GUI_forms.FormsHandler;
import grocery_classes.Grocery;
import grocery_classes.GroceryClient;
import grocery_classes.GroceryList;
import grocery_classes.Product;
import measure_enums.Measure;
import notification_classes.Notification;

import static notification_classes.NotificationCode.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Client {
    public static void main(String[] args) throws IOException, InterruptedException, RuntimeException, ClassNotFoundException {
        try (Socket socket = new Socket("localhost", 2222)) {
            FormsHandler formsHandler = new FormsHandler();
            GroceryClient groceryClient = null;

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Notification notification = new Notification();
            Grocery grocery = null;

            int statusCode = 1;

            while (true) {
                if (statusCode == 1) {
                    formsHandler.getLoginForm().setMessage("");
                    formsHandler.getLoginForm().clearInputFields();
                    formsHandler.getLoginForm().setVisible(true);

                    while (statusCode == 1) {
                        Thread.sleep(100);

                        if (formsHandler.getLoginForm().isSignIn() || formsHandler.getLoginForm().isSignUp()) {
                            String login = formsHandler.getLoginForm().getLogin();
                            String password = formsHandler.getLoginForm().getPassword();

                            notification.setData(new String[]{login, password});
                            if (formsHandler.getLoginForm().isSignIn())
                                notification.setCode(SIGN_IN);
                            else
                                notification.setCode(SIGN_UP);

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                groceryClient = (GroceryClient) notification.getData()[0];

                                formsHandler.getListsForm().setGroceryClient(groceryClient);
                                formsHandler.getLoginForm().setVisible(false);
                                statusCode = 2;
                            } else
                                System.out.println("Error: " + notification.getData()[0]);

                            formsHandler.getLoginForm().setSignIn(false);
                            formsHandler.getLoginForm().setSignUp(false);
                        }
                    }
                } else if (statusCode == 2) {
                    formsHandler.getListsForm().setTitle("Listy zakupów - " + groceryClient.getUserName());
                    formsHandler.getListsForm().setMessage("");
                    formsHandler.getListsForm().setVisible(true);

                    while (statusCode == 2) {
                        Thread.sleep(100);

                        formsHandler.getListsForm().setButtonsEnable();

                        if (formsHandler.getListsForm().isLoggedOut()) {
                            statusCode = 1;
                            formsHandler.getListsForm().setVisible(false);
                            formsHandler.getListsForm().setLoggedOut(false);
                        } else if (formsHandler.getListsForm().isAdd()) {
                            String newListName = formsHandler.getListsForm().getTempListName();

                            notification.setCode(ADD_LIST);
                            notification.setData(new Object[]{newListName, groceryClient.getId()});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                int newListId = (int) notification.getData()[0];

                                GroceryList groceryList = new GroceryList(newListName, newListId);
                                groceryClient.addGroceryList(groceryList);
                                formsHandler.getListsForm().setGroceryClient(groceryClient);

                                formsHandler.getListsForm().setMessage("Dodano listę " + newListName + ".");
                            } else if (notification.getCode() == ERROR) {
                                System.out.println("Error: " + notification.getData()[0]);
                                formsHandler.getListsForm().setMessage("Błąd (dodawanie)");
                            }

                            formsHandler.getListsForm().setAdd(false);
                        } else if (formsHandler.getListsForm().isDelete()) {
                            List<Integer> groceryIDsList = formsHandler.getListsForm().getSelectedGroceryListIDs();

                            notification.setCode(DELETE_LIST);
                            notification.setData(new Object[]{groceryClient.getId(), groceryIDsList});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                for (Integer id : groceryIDsList)
                                    groceryClient.removeGroceryList(id);

                                formsHandler.getListsForm().setGroceryClient(groceryClient);

                                formsHandler.getListsForm().setMessage("Usunięto " + groceryIDsList.size() + " list.");
                            } else {
                                ArrayList<Integer> deletedLists = (ArrayList<Integer>) notification.getData()[0];

                                for (Integer id : deletedLists)
                                    groceryClient.removeGroceryList(id);

                                formsHandler.getListsForm().setGroceryClient(groceryClient);
                                formsHandler.getListsForm().setMessage("Błąd (usuwanie)");
                            }

                            formsHandler.getListsForm().setDelete(false);
                        } else if (formsHandler.getListsForm().isChangeName()) {
                            String newListName = formsHandler.getListsForm().getTempListName();
                            int listId = formsHandler.getListsForm().getTempId();

                            notification.setCode(CHANGE_LIST_NAME);
                            notification.setData(new Object[]{groceryClient.getId(), listId, newListName});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                groceryClient.getGroceryList(listId).setName(newListName);
                                formsHandler.getListsForm().setMessage("Pomyślnie zmieniono nazwę.");
                                formsHandler.getListsForm().setGroceryClient(groceryClient);
                            } else
                                formsHandler.getListsForm().setMessage("Błąd (zmiana nazwy).");

                            formsHandler.getListsForm().setChangeName(false);
                        } else if (formsHandler.getListsForm().isShare()) {
                            List<Integer> groceryListsIDs = formsHandler.getListsForm().getSelectedGroceryListIDs();
                            String userName = formsHandler.getListsForm().getTempUserName();

                            notification.setCode(SHARE_LIST);
                            notification.setData(new Object[]{userName, groceryListsIDs});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS)
                                formsHandler.getListsForm().setMessage("Udostępniono " + groceryListsIDs.size() + " list.");
                            else {
                                formsHandler.getListsForm().setMessage("Błąd (udostępnianie).");
                                System.out.println(notification.getData()[0]);
                            }
                            formsHandler.getListsForm().setShare(false);
                        } else if (formsHandler.getListsForm().isGrocery()) {
                            int listId = formsHandler.getListsForm().getTempId();

                            notification.setCode(GROCERY_LIST);
                            notification.setData(new Integer[]{listId});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                try {
                                    HashMap<Product, Double> products = (HashMap<Product, Double>) notification.getData()[0];

                                    GroceryList groceryList = groceryClient.getGroceryList(listId);
                                    groceryList.setProductList(products);

                                    formsHandler.getGroceryListForm().setGroceryList(groceryList);
                                    formsHandler.getListsForm().setVisible(false);

                                    statusCode = 3;
                                } catch (ClassCastException e) {
                                    System.out.println(e.getMessage());
                                }
                            } else {
                                formsHandler.getListsForm().setMessage("Błąd (pobieranie listy)");
                                System.out.println(notification.getData()[0]);
                            }

                            formsHandler.getListsForm().setGrocery(false);
                        } else if (formsHandler.getListsForm().isRefresh()) {
                            int userId = groceryClient.getId();
                            String login = groceryClient.getUserName();

                            notification.setCode(REFRESH_LISTS);
                            notification.setData(new Object[]{userId, login});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                groceryClient = (GroceryClient) notification.getData()[0];

                                formsHandler.getListsForm().setMessage("Odświeżono");
                                formsHandler.getListsForm().setGroceryClient(groceryClient);
                            } else if (notification.getCode() == ERROR) {
                                System.out.println("Error: " + notification.getData()[0]);
                                formsHandler.getListsForm().setMessage("Coś poszło nie tak");
                            }

                            formsHandler.getListsForm().setRefresh(false);
                        }
                    }
                } else if (statusCode == 3) {
                    formsHandler.getGroceryListForm().setMessage("");
                    formsHandler.getGroceryListForm().setTitle(formsHandler.getGroceryListForm().getGroceryListName() + " - " + groceryClient.getUserName());
                    formsHandler.getGroceryListForm().setVisible(true);

                    while (statusCode == 3) {
                        Thread.sleep(100);

                        formsHandler.getGroceryListForm().setDeleteEnable();

                        if (formsHandler.getGroceryListForm().isBack()) {
                            formsHandler.getGroceryListForm().setVisible(false);
                            formsHandler.getGroceryListForm().setBack(false);
                            formsHandler.getGroceryListForm().clearTempList();

                            statusCode = 2;
                        } else if (formsHandler.getGroceryListForm().isAdd()) {
                            formsHandler.getGroceryListForm().setVisible(false);
                            formsHandler.getGroceryListForm().setAdd(false);

                            statusCode = 4;
                        } else if (formsHandler.getGroceryListForm().isCategory()) {
                            formsHandler.getGroceryListForm().setListModel();
                        } else if (formsHandler.getGroceryListForm().isDelete()) {
                            List<Integer> productsIDs = formsHandler.getGroceryListForm().getSelectedProductsIDs();
                            int listId = formsHandler.getGroceryListForm().getGroceryListId();

                            notification.setCode(DELETE_PRODUCT);
                            notification.setData(new Object[]{listId, productsIDs});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                GroceryList groceryList = groceryClient.getGroceryList(listId);

                                for (Integer id : productsIDs)
                                    groceryList.removeProduct(id);

                                formsHandler.getGroceryListForm().setGroceryList(groceryList);

                                formsHandler.getGroceryListForm().setMessage("Usunięto " + productsIDs.size() + " produktów.");
                            } else {
                                System.out.println(notification.getData()[0]);
                                formsHandler.getGroceryListForm().setMessage("Produkt usunięty przez innego użytkownika.");
                            }

                            formsHandler.getGroceryListForm().setDelete(false);
                            formsHandler.getGroceryListForm().clearTempList();
                        } else if (formsHandler.getGroceryListForm().isQuantity()) {
                            int productId = formsHandler.getGroceryListForm().getTempProduct().getId();
                            int listId = formsHandler.getGroceryListForm().getGroceryListId();
                            double quantity = formsHandler.getGroceryListForm().getTempValue();

                            notification.setCode(CHANGE_QUANTITY);
                            notification.setData(new Object[]{listId, productId, quantity});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                GroceryList groceryList = groceryClient.getGroceryList(listId);

                                groceryList.setCustomQuantity(productId, quantity);

                                formsHandler.getGroceryListForm().setMessage("Zmieniono ilość produktu.");
                            } else {
                                System.out.println(notification.getData()[0]);
                                formsHandler.getGroceryListForm().setMessage("Produkt usunięty przez innego użytkownika.");
                            }

                            formsHandler.getGroceryListForm().setQuantity(false);
                            formsHandler.getGroceryListForm().clearTempList();
                        } else if (formsHandler.getGroceryListForm().isRefresh()) {
                            int listId = formsHandler.getListsForm().getTempId();

                            notification.setCode(REFRESH_PRODUCTS);
                            notification.setData(new Integer[]{listId});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                HashMap<Product, Double> products = (HashMap<Product, Double>) notification.getData()[0];

                                GroceryList groceryList = groceryClient.getGroceryList(listId);
                                groceryList.setProductList(products);

                                formsHandler.getGroceryListForm().setGroceryList(groceryList);
                                formsHandler.getGroceryListForm().setMessage("Odświeżono.");
                            } else {
                                formsHandler.getListsForm().setMessage("Coś poszło nie tak (odświeżanie produktów).");
                                System.out.println(notification.getData()[0]);
                            }

                            formsHandler.getGroceryListForm().setRefresh(false);
                            formsHandler.getGroceryListForm().clearTempList();
                        }
                    }
                } else if (statusCode == 4) {
                    notification.setCode(GROCERY_INIT);
                    outputStream.reset();
                    outputStream.writeObject(notification);
                    outputStream.flush();

                    notification = (Notification) inputStream.readObject();

                    if (notification.getCode() == SUCCESS) {
                        grocery = (Grocery) notification.getData()[0];
                        formsHandler.getGroceryForm().setGrocery(grocery);
                    } else {
                        formsHandler.getGroceryListForm().setMessage("Błąd danych sklepu");
                        statusCode = 3;
                    }

                    formsHandler.getGroceryForm().setVisible(true);

                    while (statusCode == 4) {
                        Thread.sleep(100);

                        if (formsHandler.getGroceryForm().isCancel()) {
                            formsHandler.getGroceryForm().clean();
                            formsHandler.getGroceryForm().setVisible(false);
                            formsHandler.getGroceryForm().setCancel(false);
                            statusCode = 3;
                        } else if (formsHandler.getGroceryForm().isAdd()) {
                            if (!formsHandler.getGroceryForm().getProductsToAdd().isEmpty()) {
                                HashMap<Product, Double> productsToAdd = formsHandler.getGroceryForm().getProductsToAdd();
                                int listId = formsHandler.getGroceryListForm().getGroceryListId();

                                notification.setCode(ADD_PRODUCT);
                                notification.setData(new Object[]{listId, productsToAdd});

                                outputStream.reset();
                                outputStream.writeObject(notification);
                                outputStream.flush();

                                notification = (Notification) inputStream.readObject();

                                if (notification.getCode() == SUCCESS) {
                                    GroceryList groceryList = groceryClient.getGroceryList(listId);

                                    for (Product product : productsToAdd.keySet()) {
                                        double quantity = productsToAdd.get(product);
                                        groceryList.addProduct(product, quantity);
                                    }

                                    formsHandler.getGroceryListForm().setGroceryList(groceryList);

                                    formsHandler.getGroceryListForm().setMessage("Pomyślnie dodano " + productsToAdd.size() + " produktów.");
                                } else
                                    System.out.println(notification.getData()[0]);
                            }

                            formsHandler.getGroceryForm().setAdd(false);
                            formsHandler.getGroceryForm().setCancel(true);
                        } else if (formsHandler.getGroceryForm().isCategoryChanged()) {
                            formsHandler.getGroceryForm().setList();
                        } else if (formsHandler.getGroceryForm().isCustomAdd()) {
                            formsHandler.getGroceryForm().clean();
                            formsHandler.getGroceryForm().setVisible(false);
                            formsHandler.getGroceryForm().setCustomAdd(false);
                            statusCode = 5;
                        }
                    }
                } else {
                    formsHandler.getCustomProductForm().setVisible(true);

                    while (statusCode == 5) {
                        Thread.sleep(100);

                        if (formsHandler.getCustomProductForm().isCancel()) {
                            formsHandler.getCustomProductForm().setVisible(false);
                            formsHandler.getCustomProductForm().setCancel(false);
                            statusCode = 4;
                        } else if (formsHandler.getCustomProductForm().isAdd()) {
                            String productName = formsHandler.getCustomProductForm().getTempProductName();
                            String categoryName = formsHandler.getCustomProductForm().getTempCategoryName();
                            Measure measure = formsHandler.getCustomProductForm().getTempMeasure();
                            double price = formsHandler.getCustomProductForm().getTempPrice();

                            notification.setCode(CUSTOM_PRODUCT);
                            notification.setData(new Object[]{productName, categoryName, measure, price});

                            outputStream.reset();
                            outputStream.writeObject(notification);
                            outputStream.flush();

                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                int productId = (int) notification.getData()[0];

                                Product product = new Product(productName, categoryName, measure, price, productId);
                                grocery.addProduct(product);
                                formsHandler.getGroceryForm().setGrocery(grocery);
                            } else if (notification.getCode() == ERROR)
                                System.out.println(notification.getData()[0]);

                            formsHandler.getCustomProductForm().setAdd(false);
                            formsHandler.getCustomProductForm().setCancel(true);
                        }
                    }
                }
            }
        }
    }
}
