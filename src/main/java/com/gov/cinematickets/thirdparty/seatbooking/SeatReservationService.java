package com.gov.cinematickets.thirdparty.seatbooking;

public interface SeatReservationService {

	void reserveSeat(long accountId, int totalSeatsToAllocate);

}