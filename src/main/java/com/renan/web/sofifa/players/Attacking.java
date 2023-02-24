package com.renan.web.sofifa.players;

public record Attacking(double crossing, double finishing, double headingAccuracy, double shortPassing,
		double volleys) {
	public String toString() {
		double attacking = (crossing() + finishing() + headingAccuracy() + shortPassing() + volleys()) / 5;
		return String.valueOf(attacking);
	}
}
