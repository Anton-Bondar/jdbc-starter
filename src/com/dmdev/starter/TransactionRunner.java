package com.dmdev.starter;

import com.dmdev.starter.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionRunner {

    public static void main(String[] args) throws SQLException {
        long flightId = 7;
        var deleteFlightSql = "DELETE FROM flight WHERE id =?";
        var deleteTicketSql = "DELETE FROM ticket WHERE flight_id=?";
        Connection connection = null;
        PreparedStatement deleteFlightStatement = null;
        PreparedStatement deleteTicketStatement = null;
        try {
            connection = ConnectionManager.open();
            connection.setAutoCommit(false);
            deleteTicketStatement = connection.prepareStatement(deleteTicketSql);
            deleteTicketStatement.setLong(1, flightId);

            deleteFlightStatement = connection.prepareStatement(deleteFlightSql);
            deleteFlightStatement.setLong(1, flightId);

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


