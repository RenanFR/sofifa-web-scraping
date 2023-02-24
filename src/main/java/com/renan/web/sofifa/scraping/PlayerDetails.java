package com.renan.web.sofifa.scraping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
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
		WebClient webClient = SoFifaWebScraping.getWebClient();
		
		String playersRemainderDumpFile = REMAINDER_PLAYERS_TO_READ_CSV_PATH + "-"
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss")) + ".csv";

		loadPlayersPagesFromCsv();

		BufferedWriter writer = Files.newBufferedWriter(Paths.get(PLAYERS_DATA_CSV_PATH), StandardOpenOption.APPEND,
				StandardOpenOption.CREATE);

		try (final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
				.setHeader("NAME", "HEIGHT", "KIT_NUMBER", "CROSSING", "FINISHING", "HEADING_ACCURACY", "SHORT_PASSING",
						"VOLLEYS", "DRIBBLING", "CURVE", "FK_ACCURACY", "LONG_PASSING", "BALL_CONTROL", "ACCELERATION",
						"SPRINT_SPEED", "AGILITY", "REACTIONS", "BALANCE", "AGGRESSION", "INTERCEPTIONS", "POSITIONING",
						"VISION", "PENALTIES", "COMPOSURE", "DEFENSIVE_AWARENESS", "STANDING_TACKLE", "SLIDING_TACKLE",
						"GK_DIVING", "GK_HANDLING", "GK_KICKING", "GK_POSITIONING", "GK_REFLEXES", "POSITION")
				.build())) {

			for (String playerURL : playersURLList) {

				Thread.sleep(10000);

				System.out.println(playerURL);

				String playerURLComplete = SoFifaWebScraping.SOFIFA_URL.replace("/players", playerURL);
				HtmlPage playerPage = webClient.getPage(playerURLComplete);
				HtmlHeading1 nameH1 = (HtmlHeading1) playerPage
						.getByXPath("//*[@id=\"body\"]/div[2]/div/div[2]/div[1]/div/h1").get(0);

				HtmlDivision generalInformationDiv = (HtmlDivision) playerPage
						.getByXPath("//*[@id=\"body\"]/div[2]/div/div[2]/div[1]/div/div").get(0);

				String[] generalInformationDivParts = generalInformationDiv.asNormalizedText().split("/");
				String[] positionBirthAndHeightInformation = generalInformationDivParts[0].split(" ");
				String mainPosition = positionBirthAndHeightInformation[0];
				String height = positionBirthAndHeightInformation[positionBirthAndHeightInformation.length - 1]
						.substring(0, 3);

				List<Object> kitNumberLabelPath = playerPage.getByXPath("//label[contains(.,'Kit Number')]");

				String kitNumber;
				if (kitNumberLabelPath != null && !kitNumberLabelPath.isEmpty()) {

					HtmlLabel kitNumberLabel = (HtmlLabel) kitNumberLabelPath.get(0);
					kitNumber = kitNumberLabel.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");
				} else {
					kitNumber = "0";
				}

				HtmlSpan crossingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Crossing')]").get(0);
				String crossingValue = crossingSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan finishingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Finishing')]").get(0);
				String finishingValue = finishingSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan headingAccuracySpan = (HtmlSpan) playerPage
						.getByXPath("//span[contains(.,'Heading Accuracy')]").get(0);
				String headingAccuracyValue = headingAccuracySpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan shortPassingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Short Passing')]")
						.get(0);
				String shortPassingValue = shortPassingSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan volleysSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Volleys')]").get(0);
				String volleysValue = volleysSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan dribblingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Dribbling')]").get(0);
				String dribblingValue = dribblingSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan curveSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Curve')]").get(0);
				String curveValue = curveSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan fkAccuracySpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'FK Accuracy')]").get(0);
				String fkAccuracyValue = fkAccuracySpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan longPassingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Long Passing')]")
						.get(0);
				String longPassingValue = longPassingSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan ballControlSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Ball Control')]")
						.get(0);
				String ballControlValue = ballControlSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan accelerationSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Acceleration')]")
						.get(0);
				String accelerationValue = accelerationSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan sprintSpeedSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Sprint Speed')]")
						.get(0);
				String sprintSpeedValue = sprintSpeedSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan agilitySpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Agility')]").get(0);
				String agilityValue = agilitySpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan reactionsSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Reactions')]").get(0);
				String reactionsValue = reactionsSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan balanceSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Balance')]").get(0);
				String balanceValue = balanceSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan shotPowerSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Shot Power')]").get(0);
				String shotPowerValue = shotPowerSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan jumpingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Jumping')]").get(0);
				String jumpingValue = jumpingSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan staminaSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Stamina')]").get(0);
				String staminaValue = staminaSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan strengthSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Strength')]").get(0);
				String strengthValue = strengthSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan longShotsSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Long Shots')]").get(0);
				String longShotsValue = longShotsSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan aggressionSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Aggression')]").get(0);
				String aggressionValue = aggressionSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan interceptionsSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Interceptions')]")
						.get(0);
				String interceptionsValue = interceptionsSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan positioningSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Positioning')]").get(0);
				String positioningValue = positioningSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan visionSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Vision')]").get(0);
				String visionValue = visionSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan penaltiesSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Penalties')]").get(0);
				String penaltiesValue = penaltiesSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan composureSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Composure')]").get(0);
				String composureValue = composureSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan defensiveAwarenessSpan = (HtmlSpan) playerPage
						.getByXPath("//span[contains(.,'Defensive Awareness')]").get(0);
				String defensiveAwarenessValue = defensiveAwarenessSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan standingTackleSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Standing Tackle')]")
						.get(0);
				String standingTackleValue = standingTackleSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan slidingTackleSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'Sliding Tackle')]")
						.get(0);
				String slidingTackleValue = slidingTackleSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan gkDivingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'GK Diving')]").get(0);
				String gkDivingValue = gkDivingSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]", "");

				HtmlSpan gkHandlingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'GK Handling')]").get(0);
				String gkHandlingValue = gkHandlingSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan gkKickingSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'GK Kicking')]").get(0);
				String gkKickingValue = gkKickingSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				HtmlSpan gkPositioningSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'GK Positioning')]")
						.get(0);
				String gkPositioningValue = gkPositioningSpan.getEnclosingElement("li").getTextContent()
						.replaceAll("[^0-9]", "");

				HtmlSpan gkReflexesSpan = (HtmlSpan) playerPage.getByXPath("//span[contains(.,'GK Reflexes')]").get(0);
				String gkReflexesValue = gkReflexesSpan.getEnclosingElement("li").getTextContent().replaceAll("[^0-9]",
						"");

				Player player = new Player(nameH1.getTextContent(), Integer.parseInt(height),
						Integer.parseInt(kitNumber),
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
						new Defending(Integer.parseInt(defensiveAwarenessValue), Integer.parseInt(standingTackleValue),
								Integer.parseInt(slidingTackleValue)),
						new Goalkeeping(Integer.parseInt(gkDivingValue), Integer.parseInt(gkHandlingValue),
								Integer.parseInt(gkKickingValue), Integer.parseInt(gkPositioningValue),
								Integer.parseInt(gkReflexesValue)),
						mainPosition);
				players.add(player);

				csvPrinter.printRecord(player.name(), player.height(), player.kitNumber(),
						player.attacking().crossing(), player.attacking().finishing(),
						player.attacking().headingAccuracy(), player.attacking().shortPassing(),
						player.attacking().volleys(), player.skill().dribbling(), player.skill().curve(),
						player.skill().fkAccuracy(), player.skill().longPassing(), player.skill().ballControl(),
						player.movement().acceleration(), player.movement().sprintSpeed(), player.movement().agility(),
						player.movement().reactions(), player.movement().balance(), player.power().shotPower(),
						player.power().jumping(), player.power().stamina(), player.power().strength(),
						player.power().longShots(), player.mentality().aggression(), player.mentality().interceptions(),
						player.mentality().positioning(), player.mentality().vision(), player.mentality().penalties(),
						player.mentality().composure(), player.defending().defensiveAwareness(),
						player.defending().standingTackle(), player.defending().slidingTackle(),
						player.goalkeeping().gkDiving(), player.goalkeeping().gkHandling(),
						player.goalkeeping().gkKicking(), player.goalkeeping().gkPositioning(),
						player.goalkeeping().gkReflexes(), player.position());
				csvPrinter.flush();

				playersURLList.remove(playerURL);

				SoFifaWebScraping.writePlayersToReadCsv(
						playersRemainderDumpFile,
						playersURLList);
			}
		}

		webClient.close();

	}

	private static void loadPlayersPagesFromCsv() throws IOException {

		Optional<Path> latestRemainderDump = Files.list(Paths.get("./src/main/resources/"))
				.filter(file -> file.getFileName().toString().startsWith("remainder_players_to_read")
						&& file.getFileName().toString().matches(".*\\d+.*"))
				.max((oneFile, otherFile) -> {
					return LocalDateTime
							.parse(oneFile.getFileName().toString().split("-")[1],
									DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"))
							.compareTo(LocalDateTime.parse(otherFile.getFileName().toString().split("-")[1],
									DateTimeFormatter.ofPattern("ddMMyyyyHHmmss")));
				});
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
