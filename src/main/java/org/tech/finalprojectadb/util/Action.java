package org.tech.finalprojectadb.util;

public enum Action {

	VIEW(1d),
	LIKE(2.5d),
	PURCHASE(4d);

	private final double weight;

	Action(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}
}
