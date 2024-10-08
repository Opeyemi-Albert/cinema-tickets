package com.gov.cinematickets.dwppairtest;

import com.gov.cinematickets.dwppairtest.domain.TicketTypeRequest;
import com.gov.cinematickets.dwppairtest.exception.InvalidPurchaseException;

public interface TicketService {

	void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
			throws InvalidPurchaseException;

}
