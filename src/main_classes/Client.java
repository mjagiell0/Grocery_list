package main_classes;

import GUI_forms.FormsHandler;
import grocery_classes.Grocery;
import grocery_classes.GroceryClient;
import grocery_classes.GroceryList;
import grocery_classes.Product;
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

            Grocery grocery = (Grocery) inputStream.readObject();
            formsHandler.getGroceryForm().setGrocery(grocery);

            int statusCode = 1;

            while (true) {
                if (statusCode == 1) {
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
                            formsHandler.getLoginForm().setSignUp(false);
                        }
                    }
                } else if (statusCode == 2) {
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

                            outputStream.writeObject(notification);
                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                int newListId = (int) notification.getData()[0];

                                GroceryList groceryList = new GroceryList(newListName, newListId);
                                groceryClient.addGroceryList(groceryList);
                                formsHandler.getListsForm().setGroceryClient(groceryClient);

                                formsHandler.getListsForm().setMessage("Pomyślnie dodano listę " + newListName + ".");
                            } else if (notification.getCode() == ERROR)
                                System.out.println("Error: " + notification.getData()[0]);

                            formsHandler.getListsForm().setAdd(false);
                        } else if (formsHandler.getListsForm().isDelete()) {
                            List<Integer> groceryIDsList = formsHandler.getListsForm().getSelectedGroceryListIDs();

                            notification.setCode(DELETE_LIST);
                            notification.setData(new Object[]{groceryClient.getId(), groceryIDsList});

                            outputStream.writeObject(notification);
                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                for (Integer id : groceryIDsList)
                                    groceryClient.removeGroceryList(id);

                                formsHandler.getListsForm().setGroceryClient(groceryClient);

                                formsHandler.getListsForm().setMessage("Pomyślnie usunięto " + groceryIDsList.size() + " list.");
                            } else {
                                ArrayList<Integer> deletedLists = (ArrayList<Integer>) notification.getData()[0];

                                for (Integer id : deletedLists)
                                    groceryClient.removeGroceryList(id);

                                formsHandler.getListsForm().setGroceryClient(groceryClient);
                                formsHandler.getListsForm().setMessage("Nie wszystkie listy udało się usunąć.");
                            }

                            formsHandler.getListsForm().setDelete(false);
                        } else if (formsHandler.getListsForm().isChangeName()) {
                            String newListName = formsHandler.getListsForm().getTempListName();
                            int id = formsHandler.getListsForm().getTempId();

                            notification.setCode(CHANGE_LIST_NAME);
                            notification.setData(new Object[]{groceryClient.getId(), id, newListName});

                            outputStream.writeObject(notification);
                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                groceryClient.getGroceryList(id).setName(newListName);
                                formsHandler.getListsForm().setMessage("Pomyślnie zmieniono nazwę listy.");
                                formsHandler.getListsForm().setGroceryClient(groceryClient);
                            } else
                                formsHandler.getListsForm().setMessage("Nie udało się zmienić nazwy listy.");

                            formsHandler.getListsForm().setChangeName(false);
                        } else if (formsHandler.getListsForm().isShare()) {
                            List<Integer> groceryListsIDs = formsHandler.getListsForm().getSelectedGroceryListIDs();
                            String userName = formsHandler.getListsForm().getTempUserName();

                            notification.setCode(SHARE_LIST);
                            notification.setData(new Object[]{userName, groceryListsIDs});

                            outputStream.writeObject(notification);
                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS)
                                formsHandler.getListsForm().setMessage("Pomyślnie udostępniono " + groceryListsIDs.size() + " list.");
                            else {
                                formsHandler.getListsForm().setMessage("Nie udało się udostępnić niektórych list.");
                                System.out.println(notification.getData()[0]);
                            }
                            formsHandler.getListsForm().setShare(false);
                        } else if (formsHandler.getListsForm().isGrocery()) {
                            int listId = formsHandler.getListsForm().getTempId();
                            GroceryList groceryList = groceryClient.getGroceryList(listId);

                            formsHandler.getGroceryListForm().setGroceryList(groceryList);
                            formsHandler.getListsForm().setVisible(false);
                            formsHandler.getListsForm().setGrocery(false);
                            statusCode = 3;
                        }
                    }
                } else if (statusCode == 3) {
                    formsHandler.getGroceryListForm().setMessage("");
                    formsHandler.getGroceryListForm().setVisible(true);

                    while (statusCode == 3) {
                        Thread.sleep(100);

                        formsHandler.getGroceryListForm().setRemoveEnable();

                        if (formsHandler.getGroceryListForm().isBack()) {
                            formsHandler.getGroceryListForm().setVisible(false);
                            formsHandler.getGroceryListForm().setBack(false);
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

                            outputStream.writeObject(notification);
                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                GroceryList groceryList = groceryClient.getGroceryList(listId);

                                for (Integer id : productsIDs)
                                    groceryList.removeProduct(id);

                                formsHandler.getGroceryListForm().updateListView(groceryList);

                                formsHandler.getGroceryListForm().setMessage("Pomyślnie usunięto " + productsIDs.size() + " produktów.");
                            } else
                                System.out.println(notification.getData()[0]);

                            formsHandler.getGroceryListForm().setDelete(false);
                        } else if (formsHandler.getGroceryListForm().isQuantity()) {
                            int productId = formsHandler.getGroceryListForm().getTempProduct().getId();
                            int listId = formsHandler.getGroceryListForm().getGroceryListId();
                            double quantity = formsHandler.getGroceryListForm().getTempValue();

                            notification.setCode(CHANGE_QUANTITY);
                            notification.setData(new Object[]{listId, productId, quantity});

                            outputStream.writeObject(notification);
                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                GroceryList groceryList = groceryClient.getGroceryList(listId);

                                groceryList.setCustomQuantity(productId, quantity);

                                formsHandler.getGroceryListForm().setMessage("Pomyślnie zmieniono ilość produktu.");
                            } else {
                                System.out.println(notification.getData()[0]);
                                formsHandler.getGroceryListForm().setMessage("Coś poszło nie tak");
                            }

                            formsHandler.getGroceryListForm().setQuantity(false);
                        }
                    }
                } else {
                    formsHandler.getGroceryForm().setVisible(true);

                    while (statusCode == 4) {
                        Thread.sleep(100);

                        if (formsHandler.getGroceryForm().isCancel()) {
                            formsHandler.getGroceryForm().clean();
                            formsHandler.getGroceryForm().setVisible(false);
                            formsHandler.getGroceryForm().setCancel(false);
                            statusCode = 3;
                        } else if (formsHandler.getGroceryForm().isAdd()) {
                            HashMap<Product, Double> productsToAdd = formsHandler.getGroceryForm().getProductsToAdd();
                            int listId = formsHandler.getGroceryListForm().getGroceryListId();

                            notification.setCode(ADD_PRODUCT);
                            notification.setData(new Object[]{listId, productsToAdd});

                            outputStream.writeObject(notification);
                            notification = (Notification) inputStream.readObject();

                            if (notification.getCode() == SUCCESS) {
                                GroceryList groceryList = groceryClient.getGroceryList(listId);

                                for (Product product : productsToAdd.keySet()) {
                                    double quantity = productsToAdd.get(product);
                                    groceryList.addProduct(product, quantity);
                                }

                                formsHandler.getGroceryListForm().setMessage("Pomyślnie dodano " + productsToAdd.size() + " produktów.");
                            } else
                                System.out.println(notification.getData()[0]);

                            formsHandler.getGroceryForm().setAdd(false);
                            formsHandler.getGroceryForm().setCancel(true);
                        } else if (formsHandler.getGroceryForm().isCategoryChanged()) {
                            formsHandler.getGroceryForm().setList();
                        }
                    }
                }
            }
        }
    }
}
