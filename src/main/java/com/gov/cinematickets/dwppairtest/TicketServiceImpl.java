package com.gov.cinematickets.dwppairtest;

import com.gov.cinematickets.dwppairtest.core.MessageConstants;
import com.gov.cinematickets.dwppairtest.domain.TicketTypeRequest;
import com.gov.cinematickets.dwppairtest.exception.InvalidPurchaseException;
import com.gov.cinematickets.thirdparty.paymentgateway.TicketPaymentService;
import com.gov.cinematickets.thirdparty.seatbooking.SeatReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.gov.cinematickets.dwppairtest.domain.Type.ADULT;
import static com.gov.cinematickets.dwppairtest.domain.Type.CHILD;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketPaymentService ticketPaymentService;

	private final SeatReservationService seatReservationService;

	@Override
	public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
			throws InvalidPurchaseException {

		if (accountId == null) {
			log.error(MessageConstants.LOGIN_OR_SIGN_UP);
			throw new InvalidPurchaseException((MessageConstants.LOGIN_OR_SIGN_UP));
		}

		if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
			log.error(MessageConstants.CHOOSE_A_TICKET);
			throw new InvalidPurchaseException((MessageConstants.CHOOSE_A_TICKET));
		}

		int totalSeats = 0;
		int totalAmount = 0;
		int totalAdultTickets = 0;
		int totalChildTickets = 0;
		int totalInfantTickets = 0;

		for (TicketTypeRequest request : ticketTypeRequests) {
			int quantity = request.noOfTickets();

			if (quantity < 1) {
				log.error(MessageConstants.INVALID_TICKET);
				throw new InvalidPurchaseException((MessageConstants.INVALID_TICKET));
			}

			switch (request.getTicketType()) {
			case ADULT:
				totalAdultTickets += quantity;
				totalSeats += quantity;
				totalAmount += quantity * ADULT.getPrice();
				break;
			case CHILD:
				totalChildTickets += quantity;
				totalSeats += quantity;
				totalAmount += quantity * CHILD.getPrice();
				break;
			case INFANT:
				totalInfantTickets += quantity;
				break;
			}
		}
		int totalTickets = totalAdultTickets + totalChildTickets + totalInfantTickets;

		if (totalTickets > MessageConstants.MAX) {
			log.error(MessageConstants.ALLOTMENT_EXCEEDED);
			throw new InvalidPurchaseException(MessageConstants.ALLOTMENT_EXCEEDED);
		}

		if (totalAdultTickets == 0 && (totalChildTickets > 0 || totalInfantTickets > 0)) {
			log.error(MessageConstants.NOT_ALLOWED);
			throw new InvalidPurchaseException(MessageConstants.NOT_ALLOWED);
		}

		// make a payment request to the `TicketPaymentService`.
		ticketPaymentService.makePayment(accountId, totalAmount);

		// make a seat reservation request to the `SeatReservationService`.
		seatReservationService.reserveSeat(accountId, totalSeats);
	}

}
