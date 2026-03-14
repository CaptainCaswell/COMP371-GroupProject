package com.airline;

public class EconomyTicket extends Ticket {

    // New tickets
    public EconomyTicket( Flight flight, Passenger passenger ) {
        super( flight, passenger, TicketStatus.BOOKED, TicketType.ECONOMY );
    }

    // Tickets from DB
    public EconomyTicket( int ticketID, Flight flight, Passenger passenger, TicketStatus status ) {
        super( ticketID, flight, passenger, status, TicketType.ECONOMY );
    }

    public float getRefund() {
        // 14 days min 70%

        if ( daysTillFlight() < 14 ) return 0;

        float refund = getPrice() * 0.70f;

        return refund;
    }
}
