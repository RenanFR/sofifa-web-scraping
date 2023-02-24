package com.renan.web.sofifa.players;

public record Movement(double acceleration, double sprintSpeed, double agility, double reactions, double balance) {
	public String toString() {
		double movement = (acceleration() + sprintSpeed() + agility() + reactions() + balance()) / 5;
		return String.valueOf(movement);

	}
}
