package com.dmdev.starter.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BasicConnectionPool {

    private static final String URL = PropertiesUtil.get("db.url");
    private static final String USER = PropertiesUtil.get("db.username");
    private static final String PASSWORD = PropertiesUtil.get("db.password");
    private static final int MAX_TIMEOUT = 10;

    private List<Connection> connectionPool;
    private List<Connection> usedConnections;
    private static int INITIAL_POOL_SIZE = 10;
    private static int MAX_POOL_SIZE = 10;

    public static BasicConnectionPool create() throws SQLException {

        List<Connection> pool = new CopyOnWriteArrayList<>();
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(URL, USER, PASSWORD));
        }
        return new BasicConnectionPool(pool);
    }

    public BasicConnectionPool(List<Connection> connectionPool) {
        this.connectionPool = connectionPool;
        this.usedConnections = new CopyOnWriteArrayList<>();
    }

    public Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                connectionPool.add(createConnection(URL, USER, PASSWORD));
            } else {
                throw new RuntimeException(
                        "Maximum pool size reached, no available connections!");
            }
        }

        Connection connection = connectionPool
                .remove(connectionPool.size() - 1);

        if (!connection.isValid(MAX_TIMEOUT)) {
            connection = createConnection(URL, USER, PASSWORD);
        }

        usedConnections.add(connection);
        return connection;
    }

    public boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    private static Connection createConnection(
            String url, String user, String password)
            throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }

    public void shutdown() {
        usedConnections.forEach(this::releaseConnection);
        for (Connection c : connectionPool) {
            try {
                c.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        connectionPool.clear();
    }
}
