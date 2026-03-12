package com.airline;

public class CoachTicket extends Ticket {

    // New tickets
    public CoachTicket( Flight flight, Passenger passenger ) {
        super( flight, passenger, TicketStatus.BOOKED, TicketType.COACH );
    }

    // Tickets from DB
    public CoachTicket( int ticketID, Flight flight, Passenger passenger, TicketStatus status ) {
        super( ticketID, flight, passenger, status, TicketType.COACH );
    }

    public float getRefund() {
        // TODO Calculate
        float refund = getPrice();

        return refund;
    }
}
