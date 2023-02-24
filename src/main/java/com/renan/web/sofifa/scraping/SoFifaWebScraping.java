package com.renan.web.sofifa.scraping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.renan.web.sofifa.players.Player;

/**
 * Hello world!
 *
 */
public class SoFifaWebScraping {

	static List<Player> players = new ArrayList<>();
	static List<String> playersURLList = new ArrayList<>();

	public static void main(String[] args) {
		boolean hasNextPage = true;

		WebClient webClient = new WebClient(BrowserVersion.EDGE);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);

		try {
			String sofifaURL = "https://sofifa.com/players";
			HtmlPage playersPage = webClient.getPage(sofifaURL);
			while (hasNextPage) {
				fetchTablePageForPlayersLinks(playersPage);
			}
		} catch (FailingHttpStatusCodeException | IOException e) {

		} finally {
			webClient.close();
		}
	}

	private static void fetchTablePageForPlayersLinks(HtmlPage playersPage) {
		final HtmlTable playersTable = (HtmlTable) playersPage
				.getByXPath("//*[@id=\"body\"]/div[1]/div/div[2]/div/table").get(0);
		for (final HtmlTableBody body : playersTable.getBodies()) {
			final List<HtmlTableRow> rows = body.getRows();
			for (final HtmlTableRow row : rows) {
				String playerURL = row.getElementsByTagName("td").get(1).getElementsByTagName("a").get(0)
						.getAttribute("href");
				playersURLList.add(playerURL);
			}
		}
	}
}
