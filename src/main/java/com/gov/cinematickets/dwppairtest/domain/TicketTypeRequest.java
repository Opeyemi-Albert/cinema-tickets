package com.gov.cinematickets.dwppairtest.domain;

import lombok.Getter;

public class TicketTypeRequest {

	private final int noOfTickets;

	private final Type type;

	public TicketTypeRequest(Type type, int noOfTickets) {
		this.type = type;
		this.noOfTickets = noOfTickets;
	}

	public Type getTicketType() {
		return type;
	}

	public int noOfTickets() {
		return noOfTickets;
	}

}
