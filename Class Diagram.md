# Class Diagram

``` mermaid
classDiagram
    App --> FlightUI
    FlightUI -- Flight

    App --> PassengerUI
    PassengerUI -- Passenger

    App --> Database

    Route -- Flight
    Plane -- Flight

    Ticket <-- Passenger
    Ticket --> Flight

    Ticket <|-- FirstClass
    Ticket <|-- Coach
    Ticket <|-- Economy

    class App {

    }

    class FlightUI {
        +addPlane()
        +addRoute()
        +addFlight()
        +removePlane()
        +removeRoute()
        +remoteFlight()
    }

    class PassengerUI {
        +searchFlight()
        +bookTicket()
        +payTicket()
        +cancelTicket()
    }

    class Passenger {
        -int passengerID
        -String Name
        -String Address
        -double money
    }

    class Plane {
        -int planeID
        -int firstClassSeats
        -int coachSeats
        -int economySeats
        +getSeats()
    }

    class Ticket {
        <<abstract>>
        -int ticketID
        -double price
        -String status
        +getTicketID()
        +getClass()
        +book()
        +pay()
        +cancel()
        +getStatus()
    }

    class Flight {
        -int flightID
        -int deptTime
        -int arriveTime
        +getDeparture()
        +seatsFree()
    }

    class Route {
        -int RouteID
        +String fromAirport
        +String toAirport
    }

    class FirstClass {
        cancel()
    }

    class Coach {
        cancel()
    }

    class Economy {
        cancel()
    }

```
