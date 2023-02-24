package com.renan.web.sofifa.players;

public record Power(double shotPower, double jumping, double stamina, double strength, double longShots) {
	public String toString() {
		double power = (shotPower() + jumping() + stamina() + strength() + longShots()) / 5;
		return String.valueOf(power);

	}
}
