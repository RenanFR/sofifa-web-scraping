package com.renan.web.sofifa.players;

public record Mentality(double aggression, double interceptions, double positioning, double vision, double penalties,
		double composure) {
	public String toString() {
		double mentality = (aggression() + interceptions() + positioning() + vision() + penalties() + composure()) / 6;
		return String.valueOf(mentality);

	}
}
