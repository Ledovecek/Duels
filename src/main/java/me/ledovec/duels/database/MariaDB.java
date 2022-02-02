package me.ledovec.duels.database;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.mariadb.jdbc.MariaDbStatement;

import java.sql.*;

public class MariaDB {

    @Getter
    private Connection connection;
    private String username, password, url, driver;

    public void connect(String username, String password, String url, String driver) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.driver = driver;

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url + "?user=" + username + "&password=" + password);
            System.out.println("[DUELS] --> [MariaDB Driver] Connection created - successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[DUELS] --> [MariaDB Driver] Connection failed - shutdown.");
            Bukkit.shutdown();
        }
    }

    public boolean exec(String query) {
        try {
            Statement st = connection.createStatement();
            return st.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet execQuery(String query) {
        try {
            Statement st = connection.createStatement();
            return st.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
