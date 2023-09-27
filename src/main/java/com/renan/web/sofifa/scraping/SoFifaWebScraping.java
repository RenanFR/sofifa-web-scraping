package com.renan.web.sofifa.scraping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class SoFifaWebScraping {

	public static final String PLAYERS_URL_LIST_CSV_PATH = "./src/main/resources/players_url_list.csv";
	public static final String SOFIFA_URL = "https://sofifa.com/players?r=240003&set=true";

	static List<String> playersURLList = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		AtomicBoolean hasNextPage = new AtomicBoolean(true);

		WebClient webClient = getWebClient();

		try {
			HtmlPage playersPage = webClient.getPage(SOFIFA_URL);
			while (hasNextPage.get()) {

				fetchTablePageForPlayersLinks(webClient, playersPage, hasNextPage);
			}
			writePlayersToReadCsv(PLAYERS_URL_LIST_CSV_PATH, playersURLList);

		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		} finally {
			webClient.close();
			System.out.println(playersURLList.size());
			System.out.println(playersURLList);
		}
	}

	public static void writePlayersToReadCsv(String path, List<String> playersURLList) throws IOException {
		CSVFormat csvFormat = CSVFormat.DEFAULT.builder().build();
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
		try (final CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {

			for (String playerURL : playersURLList) {
				csvPrinter.printRecord(playerURL);
			}
		}
	}

	public static WebClient getWebClient() {
		WebClient webClient = new WebClient(BrowserVersion.EDGE);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		return webClient;
	}

	private static void fetchTablePageForPlayersLinks(WebClient webClient, HtmlPage playersPage,
			AtomicBoolean hasNextPage) throws Exception {
		System.out.println(playersPage.getBaseURL());
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
		List<Object> spanNextPath = playersPage.getByXPath("//span[text()='Next']");
		if (spanNextPath == null || spanNextPath.isEmpty()) {
			System.out.println("\nLAST PAGE FOUND\n");
			hasNextPage.set(false);
			return;

		} else {
			HtmlSpan spanNext = (HtmlSpan) spanNextPath.get(0);
			String nextOffsetURL = spanNext.getEnclosingElement("a").getAttribute("href");
			String nextPageURL = SOFIFA_URL.replace("/" + SOFIFA_URL.split("/")[3], nextOffsetURL);
			hasNextPage.set(true);
			fetchTablePageForPlayersLinks(webClient, webClient.getPage(nextPageURL), hasNextPage);

		}

	}
}
