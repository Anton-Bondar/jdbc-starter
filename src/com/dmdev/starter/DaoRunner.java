package com.dmdev.starter;

import java.math.BigDecimal;

public class DaoRunner {

    public static void main(String[] args) {
        //createTest();
        deleteTest();
    }

    private static void deleteTest() {
        var ticketDao = TicketDao.getInstance();

        boolean deleted = ticketDao.delete(57L);
        System.out.println("Was ticket deleted: "+ deleted);
    }

    private static void createTest() {
        var ticketDao = TicketDao.getInstance();
        var ticket = new Ticket();
        ticket.setPassengerNo("12345678");
        ticket.setPassengerName("Test");
        ticket.setFlightId(3L);
        ticket.setSeatNo("B3");
        ticket.setCost(BigDecimal.TEN);

        Ticket savedTicket = ticketDao.save(ticket);
        System.out.println("Ticket created: " + savedTicket);
    }
}
