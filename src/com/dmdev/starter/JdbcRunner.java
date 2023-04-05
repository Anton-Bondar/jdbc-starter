package com.dmdev.starter;

import com.dmdev.starter.util.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        var flightId = 2L;
        List<Long> result = getTicketsByFlightId(flightId);
        System.out.format("Tickets %s for flight id %s", result, flightId);

        LocalDateTime startDate = LocalDate.of(2020, 1, 1).atStartOfDay();
        LocalDateTime endDate = LocalDateTime.now();
        List<Long> flights = getFlightsBetween(startDate, endDate);
        String dbName = "flight_repository";
        List<String> allTables = getAllTables(dbName);
        System.out.format("\nFlights between %s and %s are: %s", startDate, endDate, flights);
        System.out.format("\nDB %s has following tables are: %s", dbName, allTables);
        System.out.format("\nNew info created with id %d ", createInfo("My info"));
    }

    public static List<String> getAllTables(String dbName) throws SQLException {
        List<String> result = new ArrayList<>();
        try (var connection = ConnectionManager.open()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet catalogs = metaData.getCatalogs();
            while (catalogs.next()) {
                String catalog = catalogs.getString("TABLE_CAT");
                ResultSet schemas = metaData.getSchemas();
                while (schemas.next()) {
                    String schema = schemas.getString("TABLE_SCHEM");
                    if (schema.equals("public") && catalog.equals(dbName)) {
                        ResultSet tables = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"});
                        while (tables.next()) {
                            result.add(tables.getString("TABLE_NAME"));
                        }
                    }
                }
            }
        }
        return result;
    }

    private static List<Long> getFlightsBetween(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Long> result = new ArrayList<>();
        String sql = """
                SELECT id 
                FROM flight
                WHERE departure_date BETWEEN ? AND ?
                """;
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setFetchSize(50);
            preparedStatement.setQueryTimeout(10);
            preparedStatement.setMaxRows(100);

            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getLong("id"));
            }
            return result;
        }
    }

    private static List<Long> getTicketsByFlightId(Long flightId) throws SQLException {
        String sql = """
                SELECT id 
                FROM ticket
                WHERE flight_id = ?
                """;
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, flightId);
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                result.add(resultSet.getObject("id", Long.class)); // Null safe
            }
        }
        return result;
    }

    public static int createInfo(String text) throws SQLException {
        Integer result = null;
        String sql = """
                INSERT INTO info (data)
                VALUES
                ('%s')
                """.formatted(text);
        try (var connection = ConnectionManager.open();
             var statment = connection.createStatement()) {

            statment.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statment.getGeneratedKeys();

            if (generatedKeys.next()) {
                result = generatedKeys.getObject(1, Integer.class);
            }
        }
        return result;
    }
}
