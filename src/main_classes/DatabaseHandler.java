package main_classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
    private final Connection connection;

    public DatabaseHandler() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3333/Shop","root","root");
    }

    public Connection getConnection() {
        return connection;
    }
}
