package com.gov.cinematickets;

import com.gov.cinematickets.dwppairtest.TicketServiceImpl;
import com.gov.cinematickets.dwppairtest.core.MessageConstants;
import com.gov.cinematickets.dwppairtest.domain.TicketTypeRequest;
import com.gov.cinematickets.dwppairtest.domain.Type;
import com.gov.cinematickets.dwppairtest.exception.InvalidPurchaseException;
import com.gov.cinematickets.thirdparty.paymentgateway.TicketPaymentService;
import com.gov.cinematickets.thirdparty.seatbooking.SeatReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.gov.cinematickets.dwppairtest.domain.Type.ADULT;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TicketingServiceImplTest {

	@Mock
	private TicketPaymentService paymentService;

	@Mock
	private SeatReservationService reservationService;

	private static final Long accountId = 11445L;

	@InjectMocks
	private TicketServiceImpl ticketService;

	@Test // Valid Ticket Purchase Request 1 - One Adult and A Child
	public void testPurchaseTicketMethodWithOneAdultAndOneChild() {

		TicketTypeRequest[] requests = { new TicketTypeRequest(ADULT, 1),
				new TicketTypeRequest(Type.CHILD, 1) };

		assertDoesNotThrow(() -> ticketService.purchaseTickets(accountId, requests));
	}
	//

	@Test // Valid Purchase ticket Request 2 - For less than 25 tickets
	public void testPurchaseTicketMethodForLessThan25Tickets() {
		TicketTypeRequest[] requests = new TicketTypeRequest[23];
		for (int i = 0; i < requests.length; i++) {
			requests[i] = new TicketTypeRequest(ADULT, 1);
		}
		assertDoesNotThrow(() -> ticketService.purchaseTickets(accountId, requests));
	}

	@Test // Invalid Purchase ticket Request 1 - For more than 25 tickets
	public void testPurchaseTicketMethodWhenMoreThan25TicketsAreRequested() {
		TicketTypeRequest[] requests = new TicketTypeRequest[26];
		for (int i = 0; i < requests.length; i++) {
			requests[i] = new TicketTypeRequest(ADULT, 1);
		}

		InvalidPurchaseException message = assertThrows(InvalidPurchaseException.class,
				() -> ticketService.purchaseTickets(accountId, requests));
		assertEquals(MessageConstants.ALLOTMENT_EXCEEDED, message.getMessage());
	}

	@Test // Invalid Purchase ticket Request 2 - No accountId provided
	public void testPurchaseTicketMethodForMoreThan25Tickets() {

		TicketTypeRequest[] requests = { new TicketTypeRequest(ADULT, 1) };
		InvalidPurchaseException message = assertThrows(InvalidPurchaseException.class,
				() -> ticketService.purchaseTickets(null, requests));
		assertEquals(MessageConstants.LOGIN_OR_SIGN_UP, message.getMessage());
	}

	@Test // Invalid Purchase ticket Request 3 - Purchasing a ticket without an Adult
	public void testPurchaseTicketMethodWhenAChildOrInfantPurchaseATicketWithoutAnAdult() {

		TicketTypeRequest[] requests = { new TicketTypeRequest(Type.CHILD, 1),
				new TicketTypeRequest(Type.INFANT, 1) };
		InvalidPurchaseException message = assertThrows(InvalidPurchaseException.class,
				() -> ticketService.purchaseTickets(accountId, requests));
		assertEquals(MessageConstants.NOT_ALLOWED, message.getMessage());
	}

	@Test // Invalid Purchase ticket Request 4 - NO Ticket Requested
	void testPurchaseTicketMethodWithoutTicketsRequested() {
		TicketTypeRequest[] requests = {};

		InvalidPurchaseException message = assertThrows(InvalidPurchaseException.class,
				() -> ticketService.purchaseTickets(accountId, requests));

		assertEquals(MessageConstants.CHOOSE_A_TICKET, message.getMessage());
	}

	@Test // Invalid Purchase ticket Request 5 - Using a negative integer
	public void testPurchaseTicketMethodWithANegativeValueAsNoOfTicket() {

		TicketTypeRequest[] requests = { new TicketTypeRequest(ADULT, 1),
				new TicketTypeRequest(Type.CHILD, -1) };
		assertThrows(InvalidPurchaseException.class,
				() -> ticketService.purchaseTickets(accountId, requests));
	}

}
