package com.renan.web.sofifa.scraping;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class EnglandTeamsFetcher {

	public static void main(String[] args) {
		try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.getOptions().setJavaScriptEnabled(false);

			// Replace this URL with the actual URL of the Sofifa page you want to scrape
			String url = "https://sofifa.com/teams?type=all&na%5B0%5D=14&na%5B1%5D=25&na%5B2%5D=35&na%5B3%5D=42&r=240014&set=true&showCol%5B0%5D=ti&showCol%5B1%5D=oa&showCol%5B2%5D=at&showCol%5B3%5D=md&showCol%5B4%5D=df&showCol%5B5%5D=tb&showCol%5B6%5D=cw&showCol%5B7%5D=bs&showCol%5B8%5D=bd&showCol%5B9%5D=bp&showCol%5B10%5D=bps&showCol%5B11%5D=cc&showCol%5B12%5D=cs&showCol%5B13%5D=da&showCol%5B14%5D=dm&showCol%5B15%5D=dw&showCol%5B16%5D=dd&showCol%5B17%5D=dp&showCol%5B18%5D=ip&showCol%5B19%5D=ps&showCol%5B20%5D=sa&showCol%5B21%5D=ta";

			HtmlPage page = webClient.getPage(url);

			// Store the teams in a CSV file
			FileWriter csvWriter = new FileWriter("FC 24-teams.csv");
			writeHeaders(csvWriter);

			while (true) {
				// Extract teams from the current page
				extractTeams(page, csvWriter);

				// Check if there is a "Next" button
				HtmlAnchor nextButton = page.getFirstByXPath("//span[text()='Next']/ancestor::a[@href]");
				if (nextButton == null) {
					break; // No more pages
				}

				// Click the "Next" button
				page = nextButton.click();
			}

			csvWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeHeaders(FileWriter csvWriter) throws IOException {
		// Add headers to CSV file
		csvWriter.append("Game Version");
		csvWriter.append(",");
		csvWriter.append("Update Date");
		csvWriter.append(",");
		csvWriter.append("Team Avatar");
		csvWriter.append(",");
		csvWriter.append("Team Name");
		csvWriter.append(",");
		csvWriter.append("ID");
		csvWriter.append(",");
		csvWriter.append("Overall");
		csvWriter.append(",");
		csvWriter.append("Attack");
		csvWriter.append(",");
		csvWriter.append("Midfield");
		csvWriter.append(",");
		csvWriter.append("Defence");
		csvWriter.append(",");
		csvWriter.append("Transfer budget");
		csvWriter.append(",");
		csvWriter.append("Club worth");
		csvWriter.append(",");
		csvWriter.append("Speed");
		csvWriter.append(",");
		csvWriter.append("Dribbling");
		csvWriter.append(",");
		csvWriter.append("Passing");
		csvWriter.append(",");
		csvWriter.append("Positioning");
		csvWriter.append(",");
		csvWriter.append("Crossing");
		csvWriter.append(",");
		csvWriter.append("Shooting");
		csvWriter.append(",");
		csvWriter.append("Aggression");
		csvWriter.append(",");
		csvWriter.append("Pressure");
		csvWriter.append(",");
		csvWriter.append("Team width");
		csvWriter.append(",");
		csvWriter.append("Defender line");
		csvWriter.append(",");
		csvWriter.append("Domestic prestige");
		csvWriter.append(",");
		csvWriter.append("International prestige");
		csvWriter.append(",");
		csvWriter.append("Players");
		csvWriter.append(",");
		csvWriter.append("Starting XI average age");
		csvWriter.append(",");
		csvWriter.append("Whole team average age");
		csvWriter.append("\n");
	}

	private static void extractTeams(HtmlPage page, FileWriter csvWriter) throws IOException {
		// Extract teams from the current page
		List<HtmlTableRow> rows = page.getByXPath("//table[@class='table table-hover persist-area']/tbody/tr");
		List<HtmlSpan> gameVersion = page.getByXPath("//*[@id=\"body\"]/header/div[2]/div/h2/div[1]/a/span[1]");
		List<HtmlSpan> updateDate = page.getByXPath("//*[@id=\"body\"]/header/div[2]/div/h2/div[2]/a/span[1]");

		for (HtmlTableRow row : rows) {
			// Extract data for each team
			HtmlImage avatar = row.getFirstByXPath(".//td[1]//img[@data-src]");
			HtmlDivision teamNameDiv = row.getFirstByXPath(".//td[@class='col-name-wide']/div[1]");
			String teamName = teamNameDiv.asNormalizedText();

			HtmlTableDataCell idHtml = row.getFirstByXPath(".//td[@class='col col-ti']");
			String id = idHtml.asNormalizedText();

			HtmlSpan overallSpan = row.getFirstByXPath(".//td[@class='col col-oa']/span[1]");
			String overall = overallSpan.asNormalizedText();

			HtmlSpan attackSpan = row.getFirstByXPath(".//td[@class='col col-at']/span[1]");
			String attack = attackSpan.asNormalizedText();

			HtmlSpan midfieldSpan = row.getFirstByXPath(".//td[@class='col col-md']/span[1]");
			String midfield = midfieldSpan.asNormalizedText();

			HtmlSpan defenceSpan = row.getFirstByXPath(".//td[@class='col col-df']/span[1]");
			String defence = defenceSpan.asNormalizedText();

			HtmlTableDataCell transferBudgetSpan = row.getFirstByXPath(".//td[@class='col col-tb']");
			String transferBudget = transferBudgetSpan.asNormalizedText();

			HtmlTableDataCell clubWorthSpan = row.getFirstByXPath(".//td[@class='col col-cw']");
			String clubWorth = clubWorthSpan.asNormalizedText();

			HtmlTableDataCell speedSpan = row.getFirstByXPath(".//td[@class='col col-bs']");
			String speed = speedSpan.asNormalizedText();

			HtmlTableDataCell dribblingSpan = row.getFirstByXPath(".//td[@class='col col-bd']");
			String dribbling = dribblingSpan.asNormalizedText();

			HtmlTableDataCell passingSpan = row.getFirstByXPath(".//td[@class='col col-bp']");
			String passing = passingSpan.asNormalizedText();

			HtmlTableDataCell positioningSpan = row.getFirstByXPath(".//td[@class='col col-bps']");
			String positioning = positioningSpan.asNormalizedText();

			HtmlTableDataCell crossingSpan = row.getFirstByXPath(".//td[@class='col col-cc']");
			String crossing = crossingSpan.asNormalizedText();

			HtmlTableDataCell shootingSpan = row.getFirstByXPath(".//td[@class='col col-cs']");
			String shooting = shootingSpan.asNormalizedText();

			HtmlTableDataCell aggressionSpan = row.getFirstByXPath(".//td[@class='col col-da']");
			String aggression = aggressionSpan.asNormalizedText();

			HtmlTableDataCell pressureSpan = row.getFirstByXPath(".//td[@class='col col-dm']");
			String pressure = pressureSpan.asNormalizedText();

			HtmlTableDataCell teamWidthSpan = row.getFirstByXPath(".//td[@class='col col-dw']");
			String teamWidth = teamWidthSpan.asNormalizedText();

			HtmlTableDataCell defenderLineSpan = row.getFirstByXPath(".//td[@class='col col-dd']");
			String defenderLine = defenderLineSpan.asNormalizedText();

			HtmlSpan domesticPrestigeSpan = row.getFirstByXPath(".//td[@class='col col-dp']/span[1]");
			String domesticPrestige = domesticPrestigeSpan.asNormalizedText();

			HtmlSpan internationalPrestigeSpan = row.getFirstByXPath(".//td[@class='col col-ip']/span[1]");
			String internationalPrestige = internationalPrestigeSpan.asNormalizedText();

			HtmlTableDataCell playersSpan = row.getFirstByXPath(".//td[@class='col col-ps']");
			String players = playersSpan.asNormalizedText();

			HtmlSpan startingXiAverageAgeSpan = row.getFirstByXPath(".//td[@class='col col-sa']/span[1]");
			String startingXiAverageAge = startingXiAverageAgeSpan.asNormalizedText();

			HtmlSpan wholeTeamAverageAgeSpan = row.getFirstByXPath(".//td[@class='col col-ta']/span[1]");
			String wholeTeamAverageAge = wholeTeamAverageAgeSpan.asNormalizedText();

			// Write data to CSV
			csvWriter.append(gameVersion.get(0).asNormalizedText());
			csvWriter.append(",");
			csvWriter.append(updateDate.get(0).asNormalizedText().replace(",", ""));
			csvWriter.append(",");
			csvWriter.append(avatar.getAttribute("data-src"));
			csvWriter.append(",");
			csvWriter.append(teamName);
			csvWriter.append(",");
			csvWriter.append(id);
			csvWriter.append(",");
			csvWriter.append(overall);
			csvWriter.append(",");
			csvWriter.append(attack);
			csvWriter.append(",");
			csvWriter.append(midfield);
			csvWriter.append(",");
			csvWriter.append(defence);
			csvWriter.append(",");
			csvWriter.append(transferBudget);
			csvWriter.append(",");
			csvWriter.append(clubWorth);
			csvWriter.append(",");
			csvWriter.append(speed);
			csvWriter.append(",");
			csvWriter.append(dribbling);
			csvWriter.append(",");
			csvWriter.append(passing);
			csvWriter.append(",");
			csvWriter.append(positioning);
			csvWriter.append(",");
			csvWriter.append(crossing);
			csvWriter.append(",");
			csvWriter.append(shooting);
			csvWriter.append(",");
			csvWriter.append(aggression);
			csvWriter.append(",");
			csvWriter.append(pressure);
			csvWriter.append(",");
			csvWriter.append(teamWidth);
			csvWriter.append(",");
			csvWriter.append(defenderLine);
			csvWriter.append(",");
			csvWriter.append(domesticPrestige);
			csvWriter.append(",");
			csvWriter.append(internationalPrestige);
			csvWriter.append(",");
			csvWriter.append(players);
			csvWriter.append(",");
			csvWriter.append(startingXiAverageAge);
			csvWriter.append(",");
			csvWriter.append(wholeTeamAverageAge);
			csvWriter.append("\n");

			System.out.println(teamName);
		}
	}

}
