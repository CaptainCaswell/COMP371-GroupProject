package com.airline;

public class FirstClassTicket extends Ticket {

    public FirstClassTicket( Flight flight, Passenger passenger ) {
        super( flight, passenger, TicketStatus.BOOKED, TicketType.FIRST );
    }

    public float getRefund() {
        // Always full refund
        float refund = getPrice();

        return refund;
    }
}
