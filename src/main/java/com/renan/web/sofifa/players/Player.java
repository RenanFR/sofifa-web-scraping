package com.renan.web.sofifa.players;

public record Player(String name, String id, Integer overallRating, Integer potential, String value, String wage,
		Integer height, String kitNumber, String preferredFoot, Integer weakFoot, Integer skillMoves,
		Integer internationalReputation, Boolean realFace, String releaseClause, Attacking attacking, Skill skill,
		Movement movement, Power power, Mentality mentality, Defending defending, Goalkeeping goalkeeping,
		String position) {

}