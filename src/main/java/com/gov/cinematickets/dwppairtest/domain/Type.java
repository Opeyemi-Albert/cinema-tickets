package com.gov.cinematickets.dwppairtest.domain;

import lombok.Getter;

@Getter
public enum Type {

	INFANT(0), CHILD(15), ADULT(25);

	private final int price;

	Type(int price) {
		this.price = price;
	}

}
