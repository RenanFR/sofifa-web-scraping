package com.renan.web.sofifa.players;

public record Player(String name, Integer height, Integer kitNumber, Attacking attacking, Skill skill, Movement movement, Power power,
		Mentality mentality, Defending defending, Goalkeeping goalkeeping, String position) {

}