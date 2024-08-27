package main_classes;

import notification_classes.Notification;
import notification_classes.NotificationCode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

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



                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                System.out.println("Client exception: " + e);
            }
        }
    }
}
