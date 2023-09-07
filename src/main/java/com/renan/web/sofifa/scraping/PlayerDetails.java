package com.renan.web.sofifa.scraping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.renan.web.sofifa.players.Attacking;
import com.renan.web.sofifa.players.Defending;
import com.renan.web.sofifa.players.Goalkeeping;
import com.renan.web.sofifa.players.Mentality;
import com.renan.web.sofifa.players.Movement;
import com.renan.web.sofifa.players.Player;
import com.renan.web.sofifa.players.Power;
import com.renan.web.sofifa.players.Skill;

public class PlayerDetails {

	public static final String PLAYERS_DATA_CSV_PATH = "./src/main/resources/players_data.csv";
	public static final String REMAINDER_PLAYERS_TO_READ_CSV_PATH = "./src/main/resources/remainder_players_to_read";

	static List<Player> players = new ArrayList<>();
	static List<String> playersURLList = new CopyOnWriteArrayList<>();

	public static void main(String[] args) throws Exception {

		LocalDateTime start = LocalDateTime.now();

		WebClient webClient = SoFifaWebScraping.getWebClient();

		String playersRemainderDumpFile = REMAINDER_PLAYERS_TO_READ_CSV_PATH + "-"
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss")) + ".csv";

		loadPlayersPagesFromCsv();

		Builder csvFormatBuilder = CSVFormat.DEFAULT.builder();
		if (!Files.exists(Paths.get(PLAYERS_DATA_CSV_PATH))) {
			csvFormatBuilder.setHeader("NAME", "BIRTH_DATE", "COUNTRY", "TEAM", "ID", "OVERALL_RATING", "POTENTIAL",
					"VALUE", "WAGE", "HEIGHT", "KIT_NUMBER", "PREFERRED_FOOT", "WEAK_FOOT", "SKILL_MOVES",
					"INTERNATIONAL_REPUTATION", "REAL_FACE", "RELEASE_CLAUSE", "CROSSING", "FINISHING",
					"HEADING_ACCURACY", "SHORT_PASSING", "VOLLEYS", "DRIBBLING", "CURVE", "FK_ACCURACY", "LONG_PASSING",
					"BALL_CONTROL", "ACCELERATION", "SPRINT_SPEED", "AGILITY", "REACTIONS", "BALANCE", "SHOT_POWER",
					"JUMPING", "STAMINA", "STRENGTH", "LONG_SHOTS", "AGGRESSION", "INTERCEPTIONS", "POSITIONING",
					"VISION", "PENALTIES", "COMPOSURE", "DEFENSIVE_AWARENESS", "STANDING_TACKLE", "SLIDING_TACKLE",
					"GK_DIVING", "GK_HANDLING", "GK_KICKING", "GK_POSITIONING", "GK_REFLEXES", "POSITION");
		}

		BufferedWriter writer = Files.newBufferedWriter(Paths.get(PLAYERS_DATA_CSV_PATH), StandardOpenOption.APPEND,
				StandardOpenOption.CREATE);

		try (final CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormatBuilder.build())) {

			for (String playerURL : playersURLList) {

				Thread.sleep(1000);

				System.out.println(playerURL);

				String playerURLComplete = SoFifaWebScraping.SOFIFA_URL.replace("/players", playerURL);
				HtmlPage playerPage = webClient.getPage(playerURLComplete);

				List<Object> fifaLatestVersionForPlayerPath = playerPage.getByXPath("//span[contains(.,'FIFA 23')]");

				if (!fifaLatestVersionForPlayerPath.isEmpty()) {
					System.out.println("FIFA 23 player");
					HtmlDivision infoDiv = (HtmlDivision) playerPage.getByXPath("//div[contains(@class, 'info')]")
							.get(0);

					String name = infoDiv.getElementsByTagName("h1").get(0).getTextContent();

					HtmlDivision infoHeaderDiv = (HtmlDivision) infoDiv.getElementsByTagName("div").get(0);

					String country = infoHeaderDiv.getElementsByTagName("a").get(0).getAttribute("title");

					String team = "Unknown";
					List<Object> teamAnchorPath = playerPage
							.getByXPath("//*[@id=\"body\"]/div[2]/div/div[2]/div[4]/div/h5/a");
					if (teamAnchorPath != null && !teamAnchorPath.isEmpty()) {

						HtmlAnchor teamAnchor = (HtmlAnchor) teamAnchorPath.get(0);
						team = teamAnchor.getTextContent().trim();
					}

					String[] generalInformationDivParts = infoHeaderDiv.asNormalizedText().split("/");
					String[] positionBirthAndHeightInformation = generalInformationDivParts[0].split(" ");
					String mainPosition = positionBirthAndHeightInformation[0];
					String height = positionBirthAndHeightInformation[positionBirthAndHeightInformation.length - 1]
							.substring(0, 3);

					String day = positionBirthAndHeightInformation[positionBirthAndHeightInformation.length - 3];
					String dayPattern = day.replace(",", "").length() == 1 ? "d" : "dd";
					String monthPattern = "MMM";
					String yearPattern = "yyyy";
					String pattern = monthPattern + "-" + dayPattern + "-" + yearPattern;

					DateTimeFormatter birthDateFormatter = new DateTimeFormatterBuilder().appendPattern(pattern)
							.parseCaseInsensitive().toFormatter(Locale.US);
					String birthDateText = (positionBirthAndHeightInformation[positionBirthAndHeightInformation.length
							- 4] + "-" + day + "-"
							+ positionBirthAndHeightInformation[positionBirthAndHeightInformation.length - 2])
							.replace("(", "").replace(")", "").replace(",", "").replace(" ", "");
					LocalDate birthDate = LocalDate.parse(birthDateText, birthDateFormatter);
					String birthDateStr = birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

					String id = playerURL.split("/")[2];

					String preferredFoot = "Unknown";
					List<Object> preferredFootPath = playerPage.getByXPath("//label[contains(.,'Preferred foot')]");
					if (preferredFootPath != null && !preferredFootPath.isEmpty()) {
						HtmlLabel preferredFootLabel = (HtmlLabel) preferredFootPath.get(0);
						preferredFoot = preferredFootLabel.getEnclosingElement("li").getTextContent()
								.replace("Preferred foot", "");
					}

					Integer weakFoot = 0;
					List<Object> weakFootPath = playerPage.getByXPath("//label[contains(.,'Weak foot')]");
					if (weakFootPath != null && !weakFootPath.isEmpty()) {
						HtmlLabel weakFootLabel = (HtmlLabel) weakFootPath.get(0);
						weakFoot = Integer.parseInt(weakFootLabel.getEnclosingElement("li").getTextContent()
								.replace("Weak Foot", "").replaceAll("[^0-9]", ""));
					}

					Integer overallRating = 0;
					List<Object> overallRatingPath = playerPage
							.getByXPath("//div[contains(@class, 'sub') and contains(.,'Overall rating')]");
					if (overallRatingPath != null && !overallRatingPath.isEmpty()) {
						HtmlDivision overallRatingDiv = (HtmlDivision) overallRatingPath.get(0);
						overallRating = Integer.parseInt(overallRatingDiv.getEnclosingElement("div")
								.getElementsByTagName("span").get(0).getTextContent());
					}

					Integer potential = 0;
					List<Object> potentialPath = playerPage
							.getByXPath("//div[contains(@class, 'sub') and contains(.,'Potential')]");
					if (potentialPath != null && !potentialPath.isEmpty()) {
						HtmlDivision potentialDiv = (HtmlDivision) potentialPath.get(0);
						potential = Integer.parseInt(potentialDiv.getEnclosingElement("div")
								.getElementsByTagName("span").get(0).getTextContent());
					}

					String value = "Unknown";
					List<Object> valuePath = playerPage
							.getByXPath("//div[contains(@class, 'sub') and contains(.,'Value')]");
					if (valuePath != null && !valuePath.isEmpty()) {
						HtmlDivision valueDiv = (HtmlDivision) valuePath.get(0);
						value = valueDiv.getEnclosingElement("div").getTextContent().replace("Value", "");
					}

					String wage = "Unknown";
					List<Object> wagePath = playerPage
							.getByXPath("//div[contains(@class, 'sub') and contains(.,'Wage')]");
					if (wagePath != null && !wagePath.isEmpty()) {
						HtmlDivision wageDiv = (HtmlDivision) wagePath.get(0);
						wage = wageDiv.getEnclosingElement("div").getTextContent().replace("Wage", "");
					}

					Integer skillMoves = 0;
					List<Object> skillMovesPath = playerPage.getByXPath("//label[contains(.,'Skill moves')]");
					if (skillMovesPath != null && !skillMovesPath.isEmpty()) {
						HtmlLabel skillMovesLabel = (HtmlLabel) skillMovesPath.get(0);
						skillMoves = Integer.parseInt(skillMovesLabel.getEnclosingElement("li").getTextContent()
								.replace("Skill Moves", "").replaceAll("[^0-9]", ""));
					}

					Integer internationalReputation = 0;
					List<Object> internationalReputationPath = playerPage
							.getByXPath("//label[contains(.,'International reputation')]");
					if (internationalReputationPath != null && !internationalReputationPath.isEmpty()) {
						HtmlLabel internationalReputationLabel = (HtmlLabel) internationalReputationPath.get(0);
						internationalReputation = Integer
								.parseInt(internationalReputationLabel.getEnclosingElement("li").getTextContent()
										.replace("International reputation", "").replaceAll("[^0-9]", ""));
					}

					Boolean realFace = Boolean.FALSE;
					List<Object> realFacePath = playerPage.getByXPath("//label[contains(.,'Real face')]");
					if (realFacePath != null && !realFacePath.isEmpty()) {
						HtmlLabel realFaceLabel = (HtmlLabel) realFacePath.get(0);
						realFace = realFaceLabel.getEnclosingElement("li").getElementsByTagName("span").get(0)
								.getTextContent().equalsIgnoreCase("Yes");
					}

					String releaseClause = "0";
					List<Object> releaseClausePath = playerPage.getByXPath("//label[contains(.,'Release clause')]");
					if (releaseClausePath != null && !releaseClausePath.isEmpty()) {
						HtmlLabel releaseClauseLabel = (HtmlLabel) releaseClausePath.get(0);
						releaseClause = releaseClauseLabel.getEnclosingElement("li").getElementsByTagName("span").get(0)
								.getTextContent();
					}

					List<Object> kitNumberLabelPath = playerPage.getByXPath("//label[contains(.,'Kit number')]");

					String kitNumber = "Unknown";
					if (kitNumberLabelPath != null && !kitNumberLabelPath.isEmpty()) {

						HtmlLabel kitNumberLabel = (HtmlLabel) kitNumberLabelPath.get(0);
						kitNumber = kitNumberLabel.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");
					}

					HtmlSpan crossingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Crossing')]").get(0);
					String crossingValue = crossingSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan finishingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Finishing')]").get(0);
					String finishingValue = finishingSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan headingAccuracySpan = (HtmlSpan) playerPage
							.getByXPath("//span[contains(.,'Heading accuracy')]").get(0);
					String headingAccuracyValue = headingAccuracySpan.getEnclosingElement("li")
							.getElementsByTagName("span").get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan shortPassingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Short passing')]")
							.get(0);
					String shortPassingValue = shortPassingSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan volleysSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Volleys')]").get(0);
					String volleysValue = volleysSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan dribblingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Dribbling')]").get(0);
					String dribblingValue = dribblingSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan curveSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Curve')]").get(0);
					String curveValue = curveSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan fkAccuracySpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'FK Accuracy')]")
							.get(0);
					String fkAccuracyValue = fkAccuracySpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan longPassingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Long passing')]")
							.get(0);
					String longPassingValue = longPassingSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan ballControlSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Ball control')]")
							.get(0);
					String ballControlValue = ballControlSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan accelerationSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Acceleration')]")
							.get(0);
					String accelerationValue = accelerationSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan sprintSpeedSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Sprint speed')]")
							.get(0);
					String sprintSpeedValue = sprintSpeedSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan agilitySpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Agility')]").get(0);
					String agilityValue = agilitySpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan reactionsSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Reactions')]").get(0);
					String reactionsValue = reactionsSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan balanceSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Balance')]").get(0);
					String balanceValue = balanceSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan shotPowerSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Shot power')]")
							.get(0);
					String shotPowerValue = shotPowerSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan jumpingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Jumping')]").get(0);
					String jumpingValue = jumpingSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan staminaSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Stamina')]").get(0);
					String staminaValue = staminaSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan strengthSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Strength')]").get(0);
					String strengthValue = strengthSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan longShotsSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Long shots')]")
							.get(0);
					String longShotsValue = longShotsSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan aggressionSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Aggression')]")
							.get(0);
					String aggressionValue = aggressionSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan interceptionsSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Interceptions')]")
							.get(0);
					String interceptionsValue = interceptionsSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan positioningSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Positioning')]")
							.get(0);
					String positioningValue = positioningSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan visionSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Vision')]").get(0);
					String visionValue = visionSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan penaltiesSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Penalties')]").get(0);
					String penaltiesValue = penaltiesSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					String composureValue = "0";
					List<Object> composureSpanPath = playerPage.getByXPath("//span[contains(.,'Composure')]");
					if (composureSpanPath != null && !composureSpanPath.isEmpty()) {

						HtmlSpan composureSpan = (HtmlSpan) composureSpanPath.get(0);
						composureValue = composureSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
								.getTextContent().replaceAll("[^0-9]", "");
					}

					String defensiveAwarenessValue;
					List<Object> defensiveAwarenessPath = playerPage
							.getByXPath("//span[contains(.,'Defensive awareness')]");
					if (defensiveAwarenessPath != null && !defensiveAwarenessPath.isEmpty()) {

						HtmlSpan defensiveAwarenessSpan = (HtmlSpan) defensiveAwarenessPath.get(0);
						defensiveAwarenessValue = defensiveAwarenessSpan.getEnclosingElement("li")
								.getElementsByTagName("span").get(0).getTextContent().replaceAll("[^0-9]", "");
					} else {
						HtmlSpan markingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Marking')]").get(0);
						defensiveAwarenessValue = markingSpan.getEnclosingElement("li").getElementsByTagName("span")
								.get(0).getTextContent().replaceAll("[^0-9]", "");
					}

					HtmlSpan standingTackleSpan = (HtmlSpan) playerPage
							.getByXPath("//span[contains(.,'Standing tackle')]").get(0);
					String standingTackleValue = standingTackleSpan.getEnclosingElement("li")
							.getElementsByTagName("span").get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan slidingTackleSpan = (HtmlSpan) playerPage
							.getByXPath("//span[contains(.,'Sliding tackle')]").get(0);
					String slidingTackleValue = slidingTackleSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan gkDivingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'GK Diving')]").get(0);
					String gkDivingValue = gkDivingSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan gkHandlingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'GK Handling')]")
							.get(0);
					String gkHandlingValue = gkHandlingSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan gkKickingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'GK Kicking')]")
							.get(0);
					String gkKickingValue = gkKickingSpan.getEnclosingElement("li").getElementsByTagName("span").get(0)
							.getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan gkPositioningSpan = (HtmlSpan) playerPage
							.getByXPath("//span[contains(.,'GK Positioning')]").get(0);
					String gkPositioningValue = gkPositioningSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					HtmlSpan gkReflexesSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'GK Reflexes')]")
							.get(0);
					String gkReflexesValue = gkReflexesSpan.getEnclosingElement("li").getElementsByTagName("span")
							.get(0).getTextContent().replaceAll("[^0-9]", "");

					Player player = new Player(name, birthDate, country, team, id, overallRating, potential, value,
							wage, Integer.parseInt(height), kitNumber, preferredFoot, weakFoot, skillMoves,
							internationalReputation, realFace, releaseClause,
							new Attacking(Integer.parseInt(crossingValue), Integer.parseInt(finishingValue),
									Integer.parseInt(headingAccuracyValue), Integer.parseInt(shortPassingValue),
									Integer.parseInt(volleysValue)),
							new Skill(Integer.parseInt(dribblingValue), Integer.parseInt(curveValue),
									Integer.parseInt(fkAccuracyValue), Integer.parseInt(longPassingValue),
									Integer.parseInt(ballControlValue)),
							new Movement(Integer.parseInt(accelerationValue), Integer.parseInt(sprintSpeedValue),
									Integer.parseInt(agilityValue), Integer.parseInt(reactionsValue),
									Integer.parseInt(balanceValue)),
							new Power(Integer.parseInt(shotPowerValue), Integer.parseInt(jumpingValue),
									Integer.parseInt(staminaValue), Integer.parseInt(strengthValue),
									Integer.parseInt(longShotsValue)),
							new Mentality(Integer.parseInt(aggressionValue), Integer.parseInt(interceptionsValue),
									Integer.parseInt(positioningValue), Integer.parseInt(visionValue),
									Integer.parseInt(penaltiesValue), Integer.parseInt(composureValue)),
							new Defending(Integer.parseInt(defensiveAwarenessValue),
									Integer.parseInt(standingTackleValue), Integer.parseInt(slidingTackleValue)),
							new Goalkeeping(Integer.parseInt(gkDivingValue), Integer.parseInt(gkHandlingValue),
									Integer.parseInt(gkKickingValue), Integer.parseInt(gkPositioningValue),
									Integer.parseInt(gkReflexesValue)),
							mainPosition);
					players.add(player);

					csvPrinter.printRecord(player.name(), birthDateStr, player.country(), player.team(), player.id(),
							player.overallRating(), player.potential(), player.value(), player.wage(), player.height(),
							player.kitNumber(), player.preferredFoot(), player.weakFoot(), player.skillMoves(),
							player.internationalReputation(), player.realFace(), player.releaseClause(),
							player.attacking().crossing(), player.attacking().finishing(),
							player.attacking().headingAccuracy(), player.attacking().shortPassing(),
							player.attacking().volleys(), player.skill().dribbling(), player.skill().curve(),
							player.skill().fkAccuracy(), player.skill().longPassing(), player.skill().ballControl(),
							player.movement().acceleration(), player.movement().sprintSpeed(),
							player.movement().agility(), player.movement().reactions(), player.movement().balance(),
							player.power().shotPower(), player.power().jumping(), player.power().stamina(),
							player.power().strength(), player.power().longShots(), player.mentality().aggression(),
							player.mentality().interceptions(), player.mentality().positioning(),
							player.mentality().vision(), player.mentality().penalties(), player.mentality().composure(),
							player.defending().defensiveAwareness(), player.defending().standingTackle(),
							player.defending().slidingTackle(), player.goalkeeping().gkDiving(),
							player.goalkeeping().gkHandling(), player.goalkeeping().gkKicking(),
							player.goalkeeping().gkPositioning(), player.goalkeeping().gkReflexes(), player.position());
					csvPrinter.flush();
				} else {

					System.out.println("FIFA older version, skipping");
				}
				playersURLList.remove(playerURL);

				SoFifaWebScraping.writePlayersToReadCsv(playersRemainderDumpFile, playersURLList);
			}
		}

		webClient.close();
		LocalDateTime end = LocalDateTime.now();
		System.out.printf("PLAYERS SYNCHRONIZATION TOOK: %d MINUTES.%n", ChronoUnit.MINUTES.between(start, end));
	}

	private static void loadPlayersPagesFromCsv() throws IOException {

		Optional<Path> latestRemainderDump = Files.list(Paths.get("./src/main/resources/"))
				.filter(file -> file.getFileName().toString().startsWith("remainder_players_to_read")
						&& file.getFileName().toString().matches(".*\\d+.*"))
				.max((oneFile, otherFile) -> {
					return LocalDateTime
							.parse(oneFile.getFileName().toString().split("-")[1].split("\\.")[0],
									DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"))
							.compareTo(LocalDateTime.parse(
									otherFile.getFileName().toString().split("-")[1].split("\\.")[0],
									DateTimeFormatter.ofPattern("ddMMyyyyHHmmss")));
				});
		if (latestRemainderDump.isPresent()) {
			System.out.println("USING DUMP FILE: " + latestRemainderDump.get().getFileName());
		}
		try (Reader reader = Files
				.newBufferedReader(latestRemainderDump.orElse(Paths.get(SoFifaWebScraping.PLAYERS_URL_LIST_CSV_PATH)));
				CSVParser csvParser = new CSVParser(reader,
						CSVFormat.Builder.create().setSkipHeaderRecord(false).build());) {
			for (CSVRecord csvRecord : csvParser) {
				playersURLList.add(csvRecord.get(0));
			}
		}
		System.out.println("PLAYER REMAINING TO FETCH: " + playersURLList.size());
	}

}
