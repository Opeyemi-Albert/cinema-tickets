package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.constants.Message;
import uk.gov.dwp.uc.pairtest.constants.TicketConstant;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService,
                             SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validateAccountId(accountId);
        validateTicketRequests(ticketTypeRequests);

        int infantCount = 0;
        int childCount = 0;
        int adultCount = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            switch (request.getTicketType()) {
                case INFANT:
                    infantCount += request.getNoOfTickets();
                    break;
                case CHILD:
                    childCount += request.getNoOfTickets();
                    break;
                case ADULT:
                    adultCount += request.getNoOfTickets();
                    break;
            }
        }

        validateTicketCounts(infantCount, childCount, adultCount);

        int totalAmountToPay = calculateTotalAmount(childCount, adultCount);

        int totalSeatsToAllocate = adultCount + childCount;

        ticketPaymentService.makePayment(accountId, totalAmountToPay);
        seatReservationService.reserveSeat(accountId, totalSeatsToAllocate);
    }

    private void validateAccountId(Long accountId) {
        if (accountId <= 0) {
            throw new InvalidPurchaseException(Message.SIGN_IN_TO_YOUR_ACCOUNT_TO_PURCHASE_A_TICKET);
        }
    }

    private void validateTicketRequests(TicketTypeRequest... ticketTypeRequests) {
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException(Message.N0_TICKET_REQUESTED);
        }

        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request == null) {
                throw new InvalidPurchaseException(Message.CHOOSE_A_TICKET);
            }

            if (request.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException(Message.INVALID_TICKET_REQUEST);
            }
        }
    }

    private void validateTicketCounts(int infantCount, int childCount, int adultCount) {
        int totalTickets = infantCount + childCount + adultCount;

        if (totalTickets > TicketConstant.MAX_NO_TICKET) {
            throw new InvalidPurchaseException(Message.TICKET_ALLOTMENT_EXCEEDED);
        }

        if (totalTickets < 1) {
            throw new InvalidPurchaseException(Message.N0_TICKET_REQUESTED);
        }

        if (adultCount == 0 && ( childCount > 0 || infantCount > 0 )) {
            throw new InvalidPurchaseException(Message.TICKET_PURCHASE_WITHOUT_ADULT_NOT_ALLOWED);
        }
    }

    private int calculateTotalAmount( int childCount, int adultCount) {
        return (childCount * TicketConstant.CHILD_PRICE) + (adultCount * TicketConstant.ADULT_PRICE);
    }
}
