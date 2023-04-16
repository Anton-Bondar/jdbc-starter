package com.dmdev.starter;

import com.dmdev.starter.dao.TicketDao;
import com.dmdev.starter.dto.TicketFilter;
import com.dmdev.starter.entity.Ticket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DaoRunner {

    public static void main(String[] args) {
        //createTest();
        //deleteTest();
        //updateTest();
        //findAllTest();
        findAllWithPagination();
        var flight = TicketDao.getInstance().findById(5L);
        System.out.println("flight: "+flight);
    }

    private static void findAllWithPagination() {
        var ticketDao = TicketDao.getInstance();
        var ticketFilter = new TicketFilter(3, 0, null, "A1");
        List<Ticket> tickets = ticketDao.findAll(ticketFilter);
        System.out.println("Filtered tickets: "+tickets);
    }

    private static void findAllTest() {
        var ticketDao = TicketDao.getInstance();
        List<Ticket> tickets = ticketDao.findAll();
        System.out.println("All tickets: "+tickets);
    }

    private static void updateTest() {
        var ticketDao = TicketDao.getInstance();
        Optional<Ticket> maybeTicket = ticketDao.findById(2L);
        maybeTicket.ifPresent(
                (ticket -> {
                    System.out.println("Current ticket: " + ticket);
                    ticket.setCost(BigDecimal.valueOf(188.88));
                    ticketDao.update(ticket);
                })
        );
    }

    private static void deleteTest() {
        var ticketDao = TicketDao.getInstance();

        boolean deleted = ticketDao.delete(57L);
        System.out.println("Was ticket deleted: " + deleted);
    }

    private static void createTest() {
        var ticketDao = TicketDao.getInstance();
        var ticket = new Ticket();
        ticket.setPassengerNo("12345678");
        ticket.setPassengerName("Test");
        //ticket.setFlightId(3L);
        ticket.setSeatNo("B3");
        ticket.setCost(BigDecimal.TEN);

        Ticket savedTicket = ticketDao.save(ticket);
        System.out.println("Ticket created: " + savedTicket);
    }
}
