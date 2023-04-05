package com.dmdev.starter;

import com.dmdev.starter.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionRunner {

    private static final String DELETE_FLIGHT_SQL = "DELETE FROM flight WHERE id=%s";
    private static final String DELETE_TICKET_BY_FLIGHT_ID_SQL = "DELETE FROM ticket WHERE flight_id=%s";

    public static void main(String[] args) throws SQLException {
        long flightId = 7;
        //deleteWithException(flightId);
        bulkDelete(flightId);
    }

    public static void bulkDelete(long flightId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionManager.open();
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            statement.addBatch(DELETE_TICKET_BY_FLIGHT_ID_SQL.formatted(flightId));
            statement.addBatch(DELETE_FLIGHT_SQL.formatted(flightId));

            statement.executeBatch();

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    private static void deleteWithException(long flightId) throws SQLException {
        Connection connection = null;
        PreparedStatement deleteFlightStatement = null;
        PreparedStatement deleteTicketStatement = null;
        try {
            connection = ConnectionManager.open();
            connection.setAutoCommit(false);
            deleteTicketStatement = connection.prepareStatement(DELETE_TICKET_BY_FLIGHT_ID_SQL.formatted(flightId));

            deleteFlightStatement = connection.prepareStatement(DELETE_FLIGHT_SQL.formatted(flightId));

            deleteTicketStatement.executeUpdate();
            if (true) {
                throw new RuntimeException("Ooops");
            }
            deleteFlightStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (deleteTicketStatement != null) {
                deleteTicketStatement.close();
            }
            if (deleteFlightStatement != null) {
                deleteFlightStatement.close();
            }
        }
    }
}


