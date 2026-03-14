package com.airline;

public class FirstClassTicket extends Ticket {

    // New tickets
    public FirstClassTicket( Flight flight, Passenger passenger ) {
        super( flight, passenger, TicketStatus.BOOKED, TicketType.FIRST );
    }

    // Tickets from DB
    public FirstClassTicket( int ticketID, Flight flight, Passenger passenger, TicketStatus status ) {
        super( ticketID, flight, passenger, status, TicketType.FIRST );
    }

    public float getRefund() {
        // Always 100%
        float refund = getPrice();

        return refund;
    }
}
