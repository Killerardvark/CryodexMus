package cryodex.modules.xwing;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import cryodex.CryodexController;
import cryodex.CryodexController.Modules;
import cryodex.Language;
import cryodex.Main;
import cryodex.Player;
import cryodex.modules.Module;
import cryodex.modules.Tournament;
import cryodex.xml.XMLObject;
import cryodex.xml.XMLUtils;
import cryodex.xml.XMLUtils.Element;

public class XWingTournament implements XMLObject, Tournament {

	public enum InitialSeedingEnum {
		RANDOM, BY_GROUP, IN_ORDER;
	}

	private final List<XWingRound> rounds;
	private List<XWingPlayer> players;
	private final InitialSeedingEnum seedingEnum;
	private final XWingTournamentGUI tournamentGUI;
	private String name;
	private final Integer points;
	private List<Integer> escalationPoints;
	private boolean startAsSingleElimination = false;

	public XWingTournament(Element tournamentElement) {

		this.players = new ArrayList<>();
		this.rounds = new ArrayList<>();
		seedingEnum = InitialSeedingEnum.RANDOM;

		tournamentGUI = new XWingTournamentGUI(this);

		String playerIDs = tournamentElement.getStringFromChild("PLAYERS");

		Module m = Modules.getModuleByName(getModuleName());

		for (String s : playerIDs.split(",")) {
			Player p = CryodexController.getPlayerByID(s);

			if (p != null) {
				XWingPlayer xp = (XWingPlayer) p.getModuleInfoByModule(m);
				if (xp != null) {
					players.add(xp);
				}
			}
		}

		Element roundElement = tournamentElement.getChild("ROUNDS");

		for (Element e : roundElement.getChildren()) {
			rounds.add(new XWingRound(e, this));
		}

		name = tournamentElement.getStringFromChild("NAME");
		points = tournamentElement.getIntegerFromChild("POINTS");

		String escalationPointsString = tournamentElement
				.getStringFromChild("ESCALATIONPOINTS");

		if (escalationPointsString != null
				&& escalationPointsString.isEmpty() == false) {
			escalationPoints = new ArrayList<Integer>();
			for (String s : escalationPointsString.split(",")) {
				escalationPoints.add(new Integer(s));
			}
		}

		int counter = 1;
		for (XWingRound r : rounds) {
			if (r.isSingleElimination()) {
				getTournamentGUI().getRoundTabbedPane()
						.addSingleEliminationTab(r.getMatches().size() * 2,
								r.getPanel());
			} else {
				getTournamentGUI().getRoundTabbedPane().addSwissTab(counter,
						r.getPanel());
				counter++;
			}

		}

		getTournamentGUI().getRankingTable().setPlayers(getAllXWingPlayers());
	}

	public XWingTournament(String name, List<XWingPlayer> players,
			InitialSeedingEnum seedingEnum, Integer points,
			List<Integer> escalationPoints, boolean isSingleElimination) {
		this.name = name;
		this.players = new ArrayList<>(players);
		this.rounds = new ArrayList<>();
		this.seedingEnum = seedingEnum;
		this.points = points;
		this.escalationPoints = escalationPoints;
		this.startAsSingleElimination = isSingleElimination;

		tournamentGUI = new XWingTournamentGUI(this);
	}

	public XWingRound getLatestRound() {
		if (rounds == null || rounds.isEmpty()) {
			return null;
		} else {
			return rounds.get(rounds.size() - 1);
		}
	}

	public int getRoundNumber(XWingRound round) {
		int count = 0;
		for (XWingRound r : rounds) {
			count++;
			if (r == round) {
				return count;
			}
		}

		return 0;
	}

	public XWingRound getRound(int i) {
		if (rounds == null) {
			return null;
		} else {
			return rounds.get(i);
		}
	}

	public XWingRound getSelectedRound() {
		if (rounds == null) {
			return null;
		} else {
			return getAllRounds().get(
					getTournamentGUI().getRoundTabbedPane().getSelectedIndex());
		}
	}

	public List<XWingRound> getAllRounds() {
		return rounds;
	}

	@Override
	public int getRoundCount() {
		if (rounds == null) {
			return 0;
		} else {
			return rounds.size();
		}
	}

	@Override
	public void setPlayers(List<Player> players) {
		List<XWingPlayer> xwPlayers = new ArrayList<>();

		for (Player p : players) {
			XWingPlayer xp = new XWingPlayer(p);
			xwPlayers.add(xp);
		}

		setXWingPlayer(xwPlayers);
	}

	@Override
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<Player>();

		for (XWingPlayer xp : getXWingPlayers()) {
			players.add(xp.getPlayer());
		}

		return players;
	}

	public List<XWingPlayer> getXWingPlayers() {
		return players;
	}

	public void setXWingPlayer(List<XWingPlayer> players) {
		this.players = players;
	}

	/**
	 * Returns any players have have played at least one match. This calls back
	 * dropped players into the list.
	 * 
	 * @return
	 */
	public Set<XWingPlayer> getAllXWingPlayers() {
		// TreeSets and Head To Head comparisons can have problems.
		// Do not use them together.
		Set<XWingPlayer> allPlayers = new TreeSet<XWingPlayer>(
				new XWingComparator(this,
						XWingComparator.rankingCompareNoHeadToHead));

		for (XWingRound r : getAllRounds()) {
			for (XWingMatch m : r.getMatches()) {
				if (m.isBye()) {
					allPlayers.add(m.getPlayer1());
				} else {
					allPlayers.add(m.getPlayer1());
					if (m.getPlayer2() != null) {
						allPlayers.add(m.getPlayer2());
					}
				}
			}
		}

		allPlayers.addAll(players);

		return allPlayers;
	}

	@Override
	public Set<Player> getAllPlayers() {
		Set<Player> players = new TreeSet<Player>();

		for (XWingPlayer xp : getAllXWingPlayers()) {
			players.add(xp.getPlayer());
		}

		return players;
	}

	@Override
	public XWingTournamentGUI getTournamentGUI() {
		return tournamentGUI;
	}

	@Override
	public String getName() {
		return name;
	}

	public Integer getPoints() {
		return points;
	}

	public List<Integer> getEscalationPoints() {
		return escalationPoints;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void updateVisualOptions() {
		if (CryodexController.isLoading == false) {
			for (XWingRound r : getAllRounds()) {
				r.getPanel().resetGamePanels(true);
			}
		}
	}

	@Override
	public boolean generateNextRound() {

		// All matches must have a result filled in
		if (getLatestRound().isComplete() == false) {
			JOptionPane
					.showMessageDialog(Main.getInstance(),
							Language.round_incomplete);
			return false;
		}

		// Single elimination checks
		if (getLatestRound().isSingleElimination()) {
			// If there was only one match then there is no reason to create a
			// new round.
			if (getLatestRound().getMatches().size() == 1) {
				JOptionPane
						.showMessageDialog(Main.getInstance(),
								Language.final_round);
				return false;
			}

			if (getLatestRound().isValid(true) == false) {
				JOptionPane
						.showMessageDialog(
								Main.getInstance(),
								Language.round_invalid);
				return false;
			}

			generateSingleEliminationMatches(getLatestRound().getMatches()
					.size());
		} else {
			// Regular swiss round checks
			if (getLatestRound().isValid(false) == false) {
				JOptionPane
						.showMessageDialog(
								Main.getInstance(),
								Language.round_invalid);
				return false;
			}

			generateRound(getAllRounds().size() + 1);
		}
		return true;
	}

	@Override
	public void cancelRound(int roundNumber) {
		if (rounds.size() >= roundNumber) {
			// If we are generating a past round. Clear all existing rounds that
			// will be erased.
			while (rounds.size() >= roundNumber) {
				int index = rounds.size() - 1;
				XWingRound roundToRemove = rounds.get(index);
				for (XWingMatch m : roundToRemove.getMatches()) {
					m.setWinner(null);
					m.setBye(false);
					m.setPlayer1(null);
					m.setPlayer2(null);
					m.setPlayer1PointsDestroyed(null);
					m.setPlayer2PointsDestroyed(null);
				}
				rounds.remove(roundToRemove);

				getTournamentGUI().getRoundTabbedPane().remove(index);
			}
		}
	}

	@Override
	public void generateRound(int roundNumber) {

		// if trying to skip a round...stop it
		if (roundNumber > rounds.size() + 1) {
			throw new IllegalArgumentException();
		}

		cancelRound(roundNumber);

		List<XWingMatch> matches;
		if (roundNumber == 1) {

			matches = new ArrayList<XWingMatch>();
			List<XWingPlayer> tempList = new ArrayList<>();
			tempList.addAll(getXWingPlayers());

			List<XWingPlayer> firstRoundByePlayers = new ArrayList<>();
			for (XWingPlayer p : tempList) {
				if (p.isFirstRoundBye()) {
					firstRoundByePlayers.add(p);
				}
			}
			tempList.removeAll(firstRoundByePlayers);

			if (seedingEnum == InitialSeedingEnum.IN_ORDER) {

				while (tempList.isEmpty() == false) {
					XWingPlayer player1 = tempList.get(0);
					XWingPlayer player2 = null;
					tempList.remove(0);
					if (tempList.isEmpty() == false) {
						player2 = tempList.get(0);
						tempList.remove(0);
					}

					XWingMatch match = new XWingMatch(player1, player2);
					matches.add(match);
				}

			} else if (seedingEnum == InitialSeedingEnum.RANDOM) {
				Collections.shuffle(tempList);

				while (tempList.isEmpty() == false) {
					XWingPlayer player1 = tempList.get(0);
					XWingPlayer player2 = tempList.get(tempList.size() - 1);
					tempList.remove(player1);
					if (player1 == player2) {
						player2 = null;
					} else {
						tempList.remove(player2);
					}

					XWingMatch match = new XWingMatch(player1, player2);
					matches.add(match);
				}
			} else if (seedingEnum == InitialSeedingEnum.BY_GROUP) {
				Map<String, List<XWingPlayer>> playerMap = new HashMap<String, List<XWingPlayer>>();

				// Add players to map
				for (XWingPlayer p : tempList) {
					List<XWingPlayer> playerList = playerMap.get(p.getPlayer()
							.getGroupName());

					if (playerList == null) {
						playerList = new ArrayList<>();
						String groupName = p.getPlayer().getGroupName() == null ? ""
								: p.getPlayer().getGroupName();
						playerMap.put(groupName, playerList);
					}

					playerList.add(p);
				}

				// Shuffle up the lists
				List<String> seedValues = new ArrayList<>(playerMap.keySet());
				Collections.shuffle(seedValues);

				// Shuffle each group list
				for (List<XWingPlayer> list : playerMap.values()) {
					Collections.shuffle(list);
				}

				XWingPlayer p1 = null;
				XWingPlayer p2 = null;
				while (seedValues.isEmpty() == false) {
					int i = 0;
					while (i < seedValues.size()) {
						if (p1 == null) {
							p1 = playerMap.get(seedValues.get(i)).get(0);
						} else {
							p2 = playerMap.get(seedValues.get(i)).get(0);
							matches.add(new XWingMatch(p1, p2));
							p1 = null;
							p2 = null;
						}

						playerMap.get(seedValues.get(i)).remove(0);

						if (playerMap.get(seedValues.get(i)).isEmpty()) {
							seedValues.remove(i);
						} else {
							i++;
						}
					}

					Collections.shuffle(seedValues);
				}
				if (p1 != null) {
					matches.add(new XWingMatch(p1, null));
				}
			}

			for (XWingPlayer p : firstRoundByePlayers) {
				matches.add(new XWingMatch(p, null));
			}

		} else {

			matches = getMatches(getXWingPlayers());
		}
		XWingRound r = new XWingRound(matches, this, roundNumber);
		rounds.add(r);
		if (roundNumber == 1
				&& startAsSingleElimination
				&& (matches.size() == 1 || matches.size() == 2
						|| matches.size() == 4 || matches.size() == 8
						|| matches.size() == 16 || matches.size() == 32)) {
			r.setSingleElimination(true);
			getTournamentGUI().getRoundTabbedPane().addSingleEliminationTab(
					r.getMatches().size() * 2, r.getPanel());
		} else {
			getTournamentGUI().getRoundTabbedPane().addSwissTab(roundNumber,
					r.getPanel());
		}

		getTournamentGUI().getRankingTable().setPlayers(getAllXWingPlayers());
	}

	private List<XWingMatch> getMatches(List<XWingPlayer> userList) {
		List<XWingMatch> matches = new ArrayList<XWingMatch>();

		List<XWingPlayer> tempList = new ArrayList<XWingPlayer>();
		tempList.addAll(userList);
		Collections.sort(tempList, new XWingComparator(this,
				XWingComparator.pairingCompare));

		XWingMatch byeMatch = null;
		// Setup the bye match if necessary
		// The player to get the bye is the lowest ranked player who has not had
		// a bye yet or who has the fewest byes
		if (tempList.size() % 2 == 1) {
			XWingPlayer byeUser = null;
			int byUserCounter = 1;
			int minByes = 0;
			try {
				while (byeUser == null
						|| byeUser.getByes(this) > minByes
						|| (byeUser.getMatches(this) != null && byeUser
								.getMatches(this)
								.get(byeUser.getMatches(this).size() - 1)
								.isBye())) {
					if (byUserCounter > tempList.size()) {
						minByes++;
						byUserCounter = 1;
					}
					byeUser = tempList.get(tempList.size() - byUserCounter);

					byUserCounter++;

				}
			} catch (ArrayIndexOutOfBoundsException e) {
				byeUser = tempList.get(tempList.size() - 1);
			}
			byeMatch = new XWingMatch(byeUser, null);
			tempList.remove(byeUser);
		}

		matches = new XWingRandomMatchGeneration(this, tempList)
				.generateMatches();

		if (XWingMatch.hasDuplicate(matches)) {
			JOptionPane
					.showMessageDialog(Main.getInstance(),
							Language.duplicate_resolution_failure);
		}

		// Add the bye match at the end
		if (byeMatch != null) {
			matches.add(byeMatch);
		}

		return matches;
	}

	@Override
	public void generateSingleEliminationMatches(int cutSize) {

		List<XWingMatch> matches = new ArrayList<>();

		List<XWingMatch> matchesCorrected = new ArrayList<XWingMatch>();

		if (getLatestRound().isSingleElimination()) {
			List<XWingMatch> lastRoundMatches = getLatestRound().getMatches();

			for (int index = 0; index < lastRoundMatches.size(); index = index + 2) {
				XWingMatch newMatch = new XWingMatch(lastRoundMatches
						.get(index).getWinner(), lastRoundMatches
						.get(index + 1).getWinner());
				matches.add(newMatch);
			}

			matchesCorrected = matches;
		} else {
			List<XWingPlayer> tempList = new ArrayList<>();
			tempList.addAll(getXWingPlayers());
			Collections.sort(tempList, new XWingComparator(this,
					XWingComparator.rankingCompare));
			tempList = tempList.subList(0, cutSize);

			while (tempList.isEmpty() == false) {
				XWingPlayer player1 = tempList.get(0);
				XWingPlayer player2 = tempList.get(tempList.size() - 1);
				tempList.remove(player1);
				if (player1 == player2) {
					player2 = null;
				} else {
					tempList.remove(player2);
				}

				XWingMatch match = new XWingMatch(player1, player2);
				matches.add(match);
			}

			switch (matches.size()) {
			case 4:
				matchesCorrected.add(matches.get(0));
				matchesCorrected.add(matches.get(3));
				matchesCorrected.add(matches.get(2));
				matchesCorrected.add(matches.get(1));
				break;
			case 8:
				matchesCorrected.add(matches.get(0));
				matchesCorrected.add(matches.get(7));
				matchesCorrected.add(matches.get(4));
				matchesCorrected.add(matches.get(3));
				matchesCorrected.add(matches.get(2));
				matchesCorrected.add(matches.get(5));
				matchesCorrected.add(matches.get(6));
				matchesCorrected.add(matches.get(1));
				break;
			default:
				matchesCorrected = matches;
			}
		}

		XWingRound r = new XWingRound(matchesCorrected, this, null);
		r.setSingleElimination(true);
		rounds.add(r);
		getTournamentGUI().getRoundTabbedPane().addSingleEliminationTab(
				cutSize, r.getPanel());

		CryodexController.saveData();
	}

	@Override
	public StringBuilder appendXML(StringBuilder sb) {

		String playerString = "";
		String seperator = "";
		for (XWingPlayer p : players) {
			playerString += seperator + p.getPlayer().getSaveId();
			seperator = ",";
		}

		XMLUtils.appendObject(sb, "PLAYERS", playerString);

		XMLUtils.appendList(sb, "ROUNDS", "ROUND", getAllRounds());

		String escalationString = "";
		seperator = "";
		if (escalationPoints != null) {
			for (Integer p : escalationPoints) {
				escalationString += seperator + p;
				seperator = ",";
			}
		}

		XMLUtils.appendObject(sb, "ESCALATIONPOINTS", escalationString);
		XMLUtils.appendObject(sb, "POINTS", points);
		XMLUtils.appendObject(sb, "NAME", name);
		XMLUtils.appendObject(sb, "MODULE", Modules.XWING.getName());

		return sb;
	}

	@Override
	public void startTournament() {
		generateRound(1);
	}

	@Override
	public void addPlayer(Player p) {
		
		for(XWingRound r : getAllRounds()){
			for(XWingMatch m : r.getMatches()){
				if(m.getPlayer1().getPlayer().equals(p)){
					getXWingPlayers().add(m.getPlayer1());
					return;
				} else if(m.getPlayer2() != null && m.getPlayer2().getPlayer().equals(p)) {
					getXWingPlayers().add(m.getPlayer2());
					return;
				}
			}
		}
		
		XWingPlayer xPlayer = new XWingPlayer(p);
		getXWingPlayers().add(xPlayer);
	}

	@Override
	public void dropPlayer(Player p) {

		XWingPlayer xPlayer = null;

		for (XWingPlayer xp : getXWingPlayers()) {
			if (xp.getPlayer() == p) {
				xPlayer = xp;
				break;
			}
		}

		if (xPlayer != null) {
			getXWingPlayers().remove(xPlayer);
		}

		resetRankingTable();
	}

	@Override
	public void resetRankingTable() {
		getTournamentGUI().getRankingTable().setPlayers(getAllXWingPlayers());
	}

	@Override
	public Icon getIcon() {
		URL imgURL = XWingTournament.class.getResource("x.png");
		if (imgURL == null) {
			System.out.println("fail!!!!!!!!!!");
		}
		ImageIcon icon = new ImageIcon(imgURL);
		return icon;
	}

	@Override
	public String getModuleName() {
		return Modules.XWING.getName();
	}
}
