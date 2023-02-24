package com.renan.web.sofifa.players;

public record Defending(double defensiveAwareness, double standingTackle, double slidingTackle) {
	public String toString() {
		double defending = (defensiveAwareness() + standingTackle() + slidingTackle()) / 3;
		return String.valueOf(defending);

	}
}
