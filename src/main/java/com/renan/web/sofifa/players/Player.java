package com.renan.web.sofifa.players;

import java.time.LocalDate;

public record Player(String name, LocalDate birthDate, String country, String team, String id, Integer overallRating,
		Integer potential, String value, String wage, Integer height, String kitNumber, String preferredFoot,
		Integer weakFoot, Integer skillMoves, Integer internationalReputation, Boolean realFace, String releaseClause,
		Attacking attacking, Skill skill, Movement movement, Power power, Mentality mentality, Defending defending,
		Goalkeeping goalkeeping, String position) {

}