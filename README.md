# Ticket Booking Service

## Overview
This project implements a ticket booking service that allows users to purchase tickets for different types of passengers: Infant, Child, and Adult.
The system enforces specific constraints to ensure valid ticket purchases and integrates with existing external services for payment processing and seat reservations.


## Ticket Types & Pricing
| Ticket Type | Price | Notes |
|-------|-----|--------------------------------------------|
| INFANT | £0  | Must be accompanied by an Adult, no seat allocated |
| CHILD | £15 | Must be accompanied by an Adult |
| ADULT | £25 | At least one Adult ticket must be purchased |

## App Features
- Customers can purchase multiple tickets at a time (maximum of 25).
- Infants do not require a paid ticket and are not allocated a seat (they sit on an Adult's lap).
- A valid purchase must contain at least one Adult ticket.
- Integration with `TicketPaymentService` for handling payments.
- Integration with `SeatReservationService` for reserving seats for Child and Adult ticket holders.

## Assumptions
- Any account with an ID greater than zero is valid and has sufficient funds.
- `TicketPaymentService` is an external service that works without defects, and payments will always succeed.
- `SeatReservationService` is an external service that works without defects, and seats will always be reserved upon request.

## App Usage
1. The user specifies the number and type of tickets they want to purchase.
2. The system validates the request based on the defined constraints.
3. If valid, the `TicketPaymentService` processes the payment.
4. If applicable, the `SeatReservationService` reserves seats for Child and Adult tickets.
5. The transaction completes successfully if both services operate without errors.

## Installation & Setup
To run the project:
1. Clone the repository.
2. Ensure you have Java installed.
3. Build the project using Maven.
4. Run the application.

## Technologies Used
- Java 11
- Maven
- jUnit
- Mockito
