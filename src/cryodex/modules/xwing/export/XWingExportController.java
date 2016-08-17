package cryodex.modules.xwing.export;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cryodex.CryodexController;
import cryodex.Language;
import cryodex.Player;
import cryodex.modules.Tournament;
import cryodex.modules.xwing.XWingComparator;
import cryodex.modules.xwing.XWingMatch;
import cryodex.modules.xwing.XWingPlayer;
import cryodex.modules.xwing.XWingRound;
import cryodex.modules.xwing.XWingTournament;

public class XWingExportController {

	public static String appendRankings(XWingTournament tournament) {
		List<XWingPlayer> playerList = new ArrayList<XWingPlayer>();
		List<XWingPlayer> activePlayers = tournament.getXWingPlayers();

		playerList.addAll(tournament.getAllXWingPlayers());
		Collections.sort(playerList, new XWingComparator(tournament, XWingComparator.rankingCompare));

		String content = "<table border=\"1\"><tr><th>"+Language.rank+"</th><th>"+Language.name+"</th><th>"+Language.score+"</th><th>"+Language.mov+"</th><th>"+Language.sos+"</th></tr>";

		for (XWingPlayer p : playerList) {

			String name = p.getName();

			if (activePlayers.contains(p) == false) {
				name = "(D#" + p.getRoundDropped(tournament) + ")" + name;
			}

			content += "<tr><td>" + p.getRank(tournament) + "</td><td>" + name + "</td><td>" + p.getScore(tournament)
					+ "</td><td>" + p.getMarginOfVictory(tournament) + "</td><td>" + p.getAverageSoS(tournament)
					+ "</td></tr>";
		}

		content += "</table>";

		return content;
	}

	public static void exportRankings(XWingTournament tournament) {

		String content = appendRankings(tournament);

		displayHTML(content, Language.export_rankings);
	}

	public static String appendMatches(XWingTournament tournament, List<XWingMatch> matches) {
		String content = "";

		int counter = 1;
		for (XWingMatch m : matches) {
			String matchString = "";
			if (m.getPlayer2() == null) {
				matchString += m.getPlayer1().getName() + Language.has_a_bye_fragment;
			} else {
				matchString += m.getPlayer1().getName() + " "+Language.vs+" " + m.getPlayer2().getName();
				if (CryodexController.getOptions().isShowTableNumbers()) {
					matchString = counter + ": " + matchString;
					counter++;
				}

				if (m.isMatchComplete()) {
					matchString += " - "+Language.match_results+": ";
					if (m.getWinner() != null) {
						matchString += m.getWinner().getName() + " " + Language.is_the_winner;
					}

					if (m.getPlayer1PointsDestroyed() != null && m.getPlayer2PointsDestroyed() != null) {
						matchString += " " + m.getPlayer1PointsDestroyed() + " "+Language.to+" " + m.getPlayer2PointsDestroyed();
					}
				}
			}
			content += "<div>" + matchString + "</div>";
		}

		return content;
	}

	public static void exportMatches() {

		XWingTournament tournament = (XWingTournament) CryodexController.getActiveTournament();

		List<XWingTournament> xwingTournaments = new ArrayList<XWingTournament>();
		if (tournament.getName().endsWith(" 1")) {

			String name = tournament.getName().substring(0, tournament.getName().lastIndexOf(" "));

			List<Tournament> tournaments = CryodexController.getAllTournaments();

			for (Tournament t : tournaments) {
				if (t instanceof XWingTournament && t.getName().contains(name)) {
					xwingTournaments.add((XWingTournament) t);
				}
			}
		} else {
			xwingTournaments.add(tournament);
		}

		String content = "";

		for (XWingTournament xt : xwingTournaments) {

			XWingRound round = xt.getLatestRound();

			int roundNumber = round.isSingleElimination() ? 0 : xt.getRoundNumber(round);

			List<XWingMatch> matches = round.getMatches();

			content += "<h3>"+Language.event+": " + xt.getName() + "</h3>";

			if (roundNumber == 0) {
				content += "<h3>"+Language.top+" " + (matches.size() * 2) + "</h3>";
			} else {
				content += "<h3>"+Language.round+" " + roundNumber + "</h3>";
			}

			content += appendMatches(xt, matches);
		}
		displayHTML(content, Language.export_matches);
	}

	public static void exportTournamentReport(XWingTournament tournament) {
		String content = "";
		int roundNumber = 1;
		for (XWingRound r : tournament.getAllRounds()) {
			if (r.isSingleElimination()) {
				content += "<h3>"+Language.top+" " + (r.getMatches().size() * 2) + "</h3>";
			} else {
				content += "<h3>"+Language.round+" " + roundNumber + "</h3>";
			}
			content += appendMatches(tournament, r.getMatches());

			roundNumber++;
		}

		content += "<h3>"+Language.rankings+"</h3>";
		content += appendRankings(tournament);

		displayHTML(content, Language.tournament_report);
	}

	public static void exportTournamentSlipsWithStats(XWingTournament tournament, List<XWingMatch> matches,
			int roundNumber) {

		String content = "";

		int counter = 1;
		for (XWingMatch m : matches) {
			String matchString = "";
			if (m.getPlayer2() != null) {
				matchString += "<table width=100%><tr><th><h4>" + Language.event + " " + tournament.getName() + " - " +Language.round+" " + roundNumber + " - "+Language.table+" " + counter
						+ "</h4></th><th vAlign=bottom align=left><h4>" + m.getPlayer1().getName()
						+ "</h4></th><th vAlign=bottom align=left><h4>" + m.getPlayer2().getName()
						+ "</h4></th></tr><tr><td><table border=\"1\"><tr><th>"+Language.name+"</th><th>"+Language.rank+"</td><th>"+Language.score+"</th><th>"+Language.mov+"</th><th>"+Language.sos+"</th></tr><tr>"
						+ "<td class=\"smallFont\">" + m.getPlayer1().getName() + "</td><td class=\"smallFont\">"
						+ m.getPlayer1().getRank(tournament) + "</td><td class=\"smallFont\">"
						+ m.getPlayer1().getScore(tournament) + "</td><td class=\"smallFont\">"
						+ m.getPlayer1().getMarginOfVictory(tournament) + "</td><td class=\"smallFont\">"
						+ m.getPlayer1().getAverageSoS(tournament) + "</td></tr><tr><td class=\"smallFont\">"
						+ m.getPlayer2().getName() + "</td><td class=\"smallFont\">"
						+ m.getPlayer2().getRank(tournament) + "</td><td class=\"smallFont\">"
						+ m.getPlayer2().getScore(tournament) + "</td><td class=\"smallFont\">"
						+ m.getPlayer2().getMarginOfVictory(tournament) + "</td><td class=\"smallFont\">"
						+ m.getPlayer2().getAverageSoS(tournament) + "</td></tr></table>"
						+ "</td><td class=\"smallFont\">"
						+ "<div style=\"vertical-align: bottom; height: 100%;\">"+Language.points+" ____________</div>"
						+ "</br>"
						+ "<div style=\"vertical-align: top; height: 100%;\"><input type=\"text\" style=\"width: 25px\"/> <input type=\"text\" style=\"width: 25px\"/> <input type=\"text\" style=\"width: 25px\"/></div>"
						+ "</td><td class=\"smallFont\">"
						+ "<div style=\"vertical-align: bottom; height: 100%;\">"+Language.points+" ____________</div>"
						+ "</br>"
						+ "<div style=\"vertical-align: top; height: 100%;\"><input type=\"text\" style=\"width: 25px\"/> <input type=\"text\" style=\"width: 25px\"/> <input type=\"text\" style=\"width: 25px\"/></div>"
						+ "</td></tr></table>";

				if (counter % 6 == 0) {
					matchString += "<hr class=\"pagebreak\">";
				} else {
					matchString += "<hr>";
				}

				content += matchString;
				counter++;
			}
		}

		displayHTML(content, Language.export_match_slips);
	}

	public static void exportTournamentSlips(XWingTournament tournament, List<XWingMatch> matches, int roundNumber) {

		String content = "";

		int counter = 1;
		for (XWingMatch m : matches) {
			String matchString = "";
			if (m.getPlayer2() != null) {
				matchString += "<table width=100%><tr><td><h4>" + Language.event + " " + tournament.getName() + " - " +Language.round+" " + roundNumber + " - "+Language.table+" " + counter
						+ "</h4></td><td vAlign=bottom align=left><h4>" + m.getPlayer1().getName()
						+ "</h4></td><td vAlign=bottom align=left><h4>" + m.getPlayer2().getName()
						+ "</h4></td></tr><tr><td>" + "</td><td class=\"smallFont\">"
						+ "<div style=\"vertical-align: bottom; height: 100%;\">"+Language.points+" ____________</div>"
						+ "</br>"
						+ "<div style=\"vertical-align: top; height: 100%;\"><input type=\"text\" style=\"width: 25px\"/> <input type=\"text\" style=\"width: 25px\"/> <input type=\"text\" style=\"width: 25px\"/></div>"
						+ "</td><td class=\"smallFont\">"
						+ "<div style=\"vertical-align: bottom; height: 100%;\">"+Language.points+" ____________</div>"
						+ "</br>"
						+ "<div style=\"vertical-align: top; height: 100%;\"><input type=\"text\" style=\"width: 25px\"/> <input type=\"text\" style=\"width: 25px\"/> <input type=\"text\" style=\"width: 25px\"/></div>"
						+ "</td></tr></table>";

				if (counter % 6 == 0) {
					matchString += "<hr class=\"pagebreak\">";
				} else {
					matchString += "<hr>";
				}

				content += matchString;
				counter++;
			}
		}

		displayHTML(content, Language.export_match_slips);
	}

	public static void playerList(List<Player> players) {
		Set<Player> sortedPlayers = new TreeSet<Player>();
		sortedPlayers.addAll(players);

		StringBuilder sb = new StringBuilder();

		for (Player p : sortedPlayers) {
			sb.append(p.getName()).append("<br>");
		}

		displayHTML(sb.toString(), Language.player_list);
	}

	public static void displayHTML(String content, String filename) {
		String fancyCss = "table{border-collapse: collapse;margin: 20px;}th{color:white; background-color:DarkSlateGray; font-size:120%;} tr:nth-child(odd){	background-color:lightgray;}";
		String internationalCharacters = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />";
		String html = "<html><head><style type=\"text/css\">.pagebreak {page-break-after: always;}.smallFont{font-size:10px}"
				+ fancyCss + "</style>" + internationalCharacters + "</head><body>" + content + "</body></html>";

		try {
			File file = File.createTempFile(filename, ".html");

			FileOutputStream stream = new FileOutputStream(file);

			stream.write(html.getBytes("UTF-8"));
			stream.flush();
			stream.close();

			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
