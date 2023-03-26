package com.dmdev.starter;

import com.dmdev.starter.util.ConnectionManager;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        Class<Driver> driverClass = Driver.class;
        try (Connection connection = ConnectionManager.open()) {
            System.out.println(connection.getTransactionIsolation());
        }
    }
}
