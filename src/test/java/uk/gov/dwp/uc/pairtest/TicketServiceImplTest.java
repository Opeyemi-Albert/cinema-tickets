package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.constants.Message;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class TicketServiceImplTest {
    @Mock
    private TicketPaymentService ticketPaymentService;
    @Mock
    private SeatReservationService seatReservationService;
    private TicketService ticketService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    @Test
    public void testTicketServiceWithValidTicketRequestTypeAndAccountId() {

        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));


        verify(ticketPaymentService).makePayment(1L, 65);
        verify(seatReservationService).reserveSeat(1L, 3);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testIfTicketServiceWillRejectInvalidAccountId() {

        ticketService.purchaseTickets(0L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1));

        fail(Message.SIGN_IN_TO_YOUR_ACCOUNT_TO_PURCHASE_A_TICKET);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testIfTicketServiceWillRejectNullTicketRequests() {

        ticketService.purchaseTickets(1L, (TicketTypeRequest[]) null);

        fail(Message.CHOOSE_A_TICKET);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testIfTicketServiceWillRejectEmptyTicketRequests() {

        ticketService.purchaseTickets(1L);

        fail(Message.N0_TICKET_REQUESTED);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testIfTicketServiceWillRejectNegativeTicketCount() {

        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1));

        fail(Message.INVALID_TICKET_REQUEST);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testIfTicketServiceWillRejectZeroTicketCount() {

        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0));

        fail(Message.N0_TICKET_REQUESTED);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testIfTicketServiceWillRejectMoreThanMaxTickets() {

        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26));

        fail(Message.TICKET_ALLOTMENT_EXCEEDED);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testIfTicketServiceWillRejectChildTicketsWithoutAdult() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1));

        fail(Message.TICKET_PURCHASE_WITHOUT_ADULT_NOT_ALLOWED);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testIfTicketServiceWillRejectInfantTicketsWithoutAdult() {

        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));

        fail(Message.TICKET_PURCHASE_WITHOUT_ADULT_NOT_ALLOWED);
    }

    @Test
    public void testTicketServiceWithTheMaximumAllowedNoOfTickets() {

        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5));

        verify(ticketPaymentService).makePayment(1L, 400);
        verify(seatReservationService).reserveSeat(1L, 20);
    }

    @Test
    public void testTicketServiceWithMultipleValidRequestsOfSameType() {

        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));

        verify(ticketPaymentService).makePayment(1L, 105);
        verify(seatReservationService).reserveSeat(1L, 5);
    }

    @Test
    public void ticketServiceShouldVerifyNoInteractionsForInvalidRequests() {
        try {
            ticketService.purchaseTickets(0L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1));
        } catch (InvalidPurchaseException e) {
            e.getCause();
        }

        verifyNoInteractions(ticketPaymentService);
        verifyNoInteractions(seatReservationService);
    }
}