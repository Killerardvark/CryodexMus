package cryodex.modules.mus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cryodex.CryodexController;
import cryodex.CryodexController.Modules;
import cryodex.Player;
import cryodex.modules.ModulePlayer;
import cryodex.modules.Tournament;
import cryodex.xml.XMLObject;
import cryodex.xml.XMLUtils;
import cryodex.xml.XMLUtils.Element;

public class MusPlayer implements Comparable<ModulePlayer>, XMLObject,
		ModulePlayer {

	public static enum Faction{
		IMPERIAL,
		REBEL,
		SCUM;	
	}
	
	private Player player;
	private String seedValue;
	private boolean firstRoundBye = false;
	private String squadId;
	private Faction faction;
	private HashMap<Faction, Integer> killMap;

	public MusPlayer(Player p) {
		player = p;
		seedValue = String.valueOf(Math.random());
	}

	public MusPlayer(Player p, Element e) {
		this.player = p;
		this.seedValue = e.getStringFromChild("SEEDVALUE");
		this.firstRoundBye = e.getBooleanFromChild("FIRSTROUNDBYE");
		this.squadId = e.getStringFromChild("SQUADID");
		String factionString = e.getStringFromChild("FACTION");
		
		if(factionString != null && factionString.isEmpty() == false){
			faction = Faction.valueOf(factionString);	
		} else {
			faction = Faction.IMPERIAL;
		}
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getSeedValue() {
		return seedValue;
	}

	public void setSeedValue(String seedValue) {
		this.seedValue = seedValue;
	}

	public boolean isFirstRoundBye() {
		return firstRoundBye;
	}

	public void setFirstRoundBye(boolean firstRoundBye) {
		this.firstRoundBye = firstRoundBye;
	}

	public String getSquadId() {
		return squadId;
	}

	public void setSquadId(String squadId) {
		this.squadId = squadId;
	}

	public Faction getFaction() {
		return faction;
	}

	public void setFaction(Faction faction) {
		this.faction = faction;
	}
	
	public void clearKillMap() {
		killMap = null;
	}

	public HashMap<Faction, Integer> getKillMap() {

		if (killMap == null) {

			killMap = new HashMap<Faction, Integer>();
			killMap.put(Faction.REBEL, 0);
			killMap.put(Faction.IMPERIAL, 0);
			killMap.put(Faction.SCUM, 0);

			for (Tournament t : CryodexController.getAllTournaments()) {
			if(t instanceof MusTournament){
				MusTournament tournament = (MusTournament)t;
				for (MusRound r : tournament.getAllRounds()) {
					for (MusMatch m : r.getMatches()) {
						if (m.getPlayer2() != null) {
							if (m.getPlayer1() == this) {
								Integer points = killMap.get(m.getPlayer2()
										.getFaction());
								points = m.getPlayer1PointsDestroyed() == null ? points
										: points
												+ m.getPlayer1PointsDestroyed();
								killMap.put(m.getPlayer2().getFaction(), points);
							} else if (m.getPlayer2() == this) {
								Integer points = killMap.get(m.getPlayer1()
										.getFaction());
								points = m.getPlayer2PointsDestroyed() == null ? points
										: points
												+ m.getPlayer2PointsDestroyed();
								killMap.put(m.getPlayer1().getFaction(), points);
							}
						}
					}
				}
			}
			}
		}
		return killMap;
	}


	public List<MusMatch> getMatches(MusTournament t) {

		List<MusMatch> matches = new ArrayList<MusMatch>();

		if (t != null) {

			rounds: for (MusRound r : t.getAllRounds()) {
				if (r.isSingleElimination() && t.isElimination() == false) {
					continue;
				}
				for (MusMatch m : r.getMatches()) {
					if (m.getPlayer1() == this
							|| (m.getPlayer2() != null && m.getPlayer2() == this)) {
						matches.add(m);
						continue rounds;
					}
				}
			}
		}
		return matches;
	}

	@Override
	public String toString() {
		return getPlayer().getName();
	}

	public int getScore(MusTournament t) {
		int score = 0;
		for (MusMatch match : getMatches(t)) {
			if (match.getWinner() == this) {
				score += MusMatch.WIN_POINTS;
			} else if (match.isBye()) {
				score += MusMatch.BYE_POINTS;
			} else {
				score += MusMatch.LOSS_POINTS;
			}
		}

		return score;
	}

	public double getAverageScore(MusTournament t) {
		return getScore(t) * 1.0 / getMatches(t).size();
	}

	public double getAverageSoS(MusTournament t) {
		double sos = 0.0;
		List<MusMatch> matches = getMatches(t);

		for (MusMatch m : matches) {
			if (m.isBye() == false && m.getWinner() != null) {
				if (m.getPlayer1() == this) {
					sos += m.getPlayer2().getAverageScore(t);
				} else {
					sos += m.getPlayer1().getAverageScore(t);
				}
			}

			if (isFirstRoundBye() && m.isBye() && m == matches.get(0)) {
				sos += (getMatches(t).size() - 1) * 5;
			}
		}

		double averageSos = sos / matches.size();
		if (Double.isNaN(averageSos) != true) {
			BigDecimal bd = new BigDecimal(averageSos);
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
		return averageSos;
	}

	public int getWins(MusTournament t) {
		int score = 0;
		for (MusMatch match : getMatches(t)) {
			if (match.getWinner() == this || match.isBye()) {
				score++;
			}
		}
		return score;
	}

	public int getLosses(MusTournament t) {
		int score = 0;
		for (MusMatch match : getMatches(t)) {
			if (match.getWinner() != null && match.getWinner() != this) {
				score++;
			}
		}
		return score;
	}

	public int getByes(MusTournament t) {
		int byes = 0;
		for (MusMatch match : getMatches(t)) {
			if (match.isBye()) {
				byes++;
			}
		}
		return byes;
	}

	// public int getStrengthOfSchedule(XWingTournament t) {
	// Integer sos = 0;
	// List<XWingMatch> matches = getMatches(t);
	//
	// for (XWingMatch m : matches) {
	// if (m.isBye() == false & (m.getWinner() != null || m.isDraw())) {
	// if (m.getPlayer1() == this) {
	// sos += m.getPlayer2().getScore(t);
	// } else {
	// sos += m.getPlayer1().getScore(t);
	// }
	// }
	//
	// if (isFirstRoundBye() && m.isBye() && m == matches.get(0)) {
	// sos += (getMatches(t).size() - 1) * 5;
	// }
	// }
	//
	// return sos;
	// }

	public int getRank(MusTournament t) {
		List<MusPlayer> players = new ArrayList<MusPlayer>();
		players.addAll(t.getXWingPlayers());
		Collections.sort(players, new MusComparator(t,
				MusComparator.rankingCompare));

		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) == this) {
				return i + 1;
			}
		}

		return 0;
	}

	public int getEliminationRank(MusTournament t) {

		int rank = 0;

		for (MusRound r : t.getAllRounds()) {
			if (r.isSingleElimination()) {
				for (MusMatch m : r.getMatches()) {
					if ((m.getPlayer1() == this || m.getPlayer2() == this)
							&& (m.getWinner() != null && m.getWinner() != this)) {
						return r.getMatches().size() * 2;
					}

					if (r.getMatches().size() == 1 && m.getWinner() != null
							&& m.getWinner() == this) {
						return 1;
					}
				}
			}
		}

		return rank;
	}

	public int getMarginOfVictory(MusTournament t) {

		int roundNumber = 0;

		Integer movPoints = 0;

		for (MusMatch match : getMatches(t)) {

			roundNumber++;

			Integer tournamentPoints = t.getPoints();
			if (tournamentPoints == null && t.getEscalationPoints() != null
					&& t.getEscalationPoints().isEmpty() == false) {

				tournamentPoints = t.getEscalationPoints().size() >= roundNumber ? t
						.getEscalationPoints().get(roundNumber - 1) : t
						.getEscalationPoints().get(
								t.getEscalationPoints().size() - 1);
			}

			if (match.isBye()) {
				movPoints += tournamentPoints + (tournamentPoints / 2);
				continue;
			} else if (match.getWinner() == null) {
				continue;
			}

			boolean isPlayer1 = match.getPlayer1() == this;

			int player1Points = match.getPlayer1PointsDestroyed() == null ? 0
					: match.getPlayer1PointsDestroyed();
			int player2Points = match.getPlayer2PointsDestroyed() == null ? 0
					: match.getPlayer2PointsDestroyed();

			int diff = player1Points - player2Points;

			movPoints += isPlayer1 ? tournamentPoints + diff : tournamentPoints
					- diff;
		}
		return movPoints;
	}

	/**
	 * Returns true if the player has defeated every other person in their score
	 * group.
	 * 
	 * @param t
	 * @return
	 */
	public boolean isHeadToHeadWinner(MusTournament t) {

		if (t != null) {
			int score = getScore(t);
			List<MusPlayer> players = new ArrayList<MusPlayer>();
			for (MusPlayer p : t.getXWingPlayers()) {
				if (p != this && p.getScore(t) == score) {
					players.add(p);
				}
			}

			if (players.isEmpty()) {
				return false;
			}

			playerLoop: for (MusPlayer p : players) {
				for (MusMatch m : p.getMatches(t)) {
					if (m.getWinner() != null && m.getWinner() == this) {
						continue playerLoop;
					}
				}
				return false;
			}
		}

		return true;
	}

	public int getRoundDropped(MusTournament t) {
		for (int i = t.getAllRounds().size(); i > 0; i--) {

			boolean found = false;
			MusRound r = t.getAllRounds().get(i - 1);
			for (MusMatch m : r.getMatches()) {
				if (m.getPlayer1() == this) {
					found = true;
					break;
				} else if (m.isBye() == false && m.getPlayer2() == this) {
					found = true;
					break;
				}
			}

			if (found) {
				return i + 1;
			}
		}

		return 0;
	}

	public String getName() {
		return getPlayer().getName();
	}

	@Override
	public String getModuleName() {
		return Modules.XWING.getName();
	}

	public String toXML() {
		StringBuilder sb = new StringBuilder();

		appendXML(sb);

		return sb.toString();
	}

	@Override
	public StringBuilder appendXML(StringBuilder sb) {

		XMLUtils.appendObject(sb, "MODULE", Modules.XWING.getName());
		XMLUtils.appendObject(sb, "SEEDVALUE", getSeedValue());
		XMLUtils.appendObject(sb, "FIRSTROUNDBYE", isFirstRoundBye());
		XMLUtils.appendObject(sb, "SQUADID", getSquadId());
		XMLUtils.appendObject(sb, "FACTION", getFaction());

		return sb;
	}

	@Override
	public int compareTo(ModulePlayer arg0) {
		return this.getPlayer().getName().toUpperCase()
				.compareTo(arg0.getPlayer().getName().toUpperCase());
	}
}
