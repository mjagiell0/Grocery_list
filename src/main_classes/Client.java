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
import java.util.List;
import java.util.Objects;


public class Client {
    public static void main(String[] args) throws IOException, InterruptedException, RuntimeException, ClassNotFoundException {
        try (Socket socket = new Socket("localhost", 2222)) {
            FormsHandler formsHandler = new FormsHandler();
            GroceryClient groceryClient = null;

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
                            break;
                        }

                        if (formsHandler.getListsForm().isAdd()) {
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
                        }

                        if (formsHandler.getListsForm().isDelete()) {
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
                        }

                        if (formsHandler.getListsForm().isChangeName()) {
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
                        }

                        if (formsHandler.getListsForm().isShare()) {
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
                        }

                        if (formsHandler.getListsForm().isGrocery()) {
                            int listId = formsHandler.getListsForm().getTempId();
                            GroceryList groceryList = groceryClient.getGroceryList(listId);

                            formsHandler.getGroceryListForm().setGroceryList(groceryList);
                            formsHandler.getListsForm().setVisible(false);
                            formsHandler.getListsForm().setGrocery(false);
                            statusCode = 3;
                            break;
                        }
                    }
                } else if (statusCode == 3) {
                    formsHandler.getGroceryListForm().setVisible(true);

                    while (statusCode == 3) {
                        Thread.sleep(100);

                        if (formsHandler.getGroceryListForm().isBack()) {
                            formsHandler.getGroceryListForm().setVisible(false);
                            formsHandler.getGroceryListForm().setBack(false);
                            statusCode = 2;
                            break;
                        }
                    }
                }
            }
        }
    }
}
