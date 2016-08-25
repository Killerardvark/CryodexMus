package cryodex.modules.mus;

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

public class MusTournament implements XMLObject, Tournament {

	public enum InitialSeedingEnum {
		RANDOM, BY_GROUP, IN_ORDER;
	}
	
	public enum TournamentTypeEnum {
		SWISS, SINGLE, DOUBLE, TRIPLE;
	}

	private final List<MusRound> rounds;
	private List<MusPlayer> players;
	private final InitialSeedingEnum seedingEnum;
	private final MusTournamentGUI tournamentGUI;
	private String name;
	private final Integer points;
	private List<Integer> escalationPoints;
	private TournamentTypeEnum tournamentType = TournamentTypeEnum.SWISS;

	public MusTournament(Element tournamentElement) {

		this.players = new ArrayList<>();
		this.rounds = new ArrayList<>();
		seedingEnum = InitialSeedingEnum.RANDOM;

		tournamentGUI = new MusTournamentGUI(this);

		String playerIDs = tournamentElement.getStringFromChild("PLAYERS");

		Module m = Modules.getModuleByName(getModuleName());

		for (String s : playerIDs.split(",")) {
			Player p = CryodexController.getPlayerByID(s);

			if (p != null) {
				MusPlayer xp = (MusPlayer) p.getModuleInfoByModule(m);
				if (xp != null) {
					players.add(xp);
				}
			}
		}

		Element roundElement = tournamentElement.getChild("ROUNDS");

		for (Element e : roundElement.getChildren()) {
			rounds.add(new MusRound(e, this));
		}

		name = tournamentElement.getStringFromChild("NAME");
		points = tournamentElement.getIntegerFromChild("POINTS");
		
		String tournamentTypeString = tournamentElement.getStringFromChild("TOURNAMENTTYPE");
		
		if(tournamentTypeString != null && tournamentTypeString.isEmpty() == false){
			tournamentType = TournamentTypeEnum.valueOf(tournamentTypeString);
		}

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
		for (MusRound r : rounds) {
			if (r.isSingleElimination() && isElimination() == false) {
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

	public MusTournament(String name, List<MusPlayer> players,
			InitialSeedingEnum seedingEnum, Integer points,
			List<Integer> escalationPoints, TournamentTypeEnum tournamentType) {
		this.name = name;
		this.players = new ArrayList<>(players);
		this.rounds = new ArrayList<>();
		this.seedingEnum = seedingEnum;
		this.points = points;
		this.escalationPoints = escalationPoints;
		this.tournamentType = tournamentType;

		tournamentGUI = new MusTournamentGUI(this);
	}

	public MusRound getLatestRound() {
		if (rounds == null || rounds.isEmpty()) {
			return null;
		} else {
			return rounds.get(rounds.size() - 1);
		}
	}

	public int getRoundNumber(MusRound round) {
		int count = 0;
		for (MusRound r : rounds) {
			count++;
			if (r == round) {
				return count;
			}
		}

		return 0;
	}

	public MusRound getRound(int i) {
		if (rounds == null) {
			return null;
		} else {
			return rounds.get(i);
		}
	}

	public MusRound getSelectedRound() {
		if (rounds == null) {
			return null;
		} else {
			return getAllRounds().get(
					getTournamentGUI().getRoundTabbedPane().getSelectedIndex());
		}
	}

	public List<MusRound> getAllRounds() {
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
		List<MusPlayer> xwPlayers = new ArrayList<>();

		for (Player p : players) {
			MusPlayer xp = new MusPlayer(p);
			xwPlayers.add(xp);
		}

		setXWingPlayer(xwPlayers);
	}

	@Override
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<Player>();

		for (MusPlayer xp : getXWingPlayers()) {
			players.add(xp.getPlayer());
		}

		return players;
	}

	public List<MusPlayer> getXWingPlayers() {
		return players;
	}

	public void setXWingPlayer(List<MusPlayer> players) {
		this.players = players;
	}

	/**
	 * Returns any players have have played at least one match. This calls back
	 * dropped players into the list.
	 * 
	 * @return
	 */
	public Set<MusPlayer> getAllXWingPlayers() {
		// TreeSets and Head To Head comparisons can have problems.
		// Do not use them together.
		Set<MusPlayer> allPlayers = new TreeSet<MusPlayer>(
				new MusComparator(this,
						MusComparator.rankingCompareNoHeadToHead));

		for (MusRound r : getAllRounds()) {
			for (MusMatch m : r.getMatches()) {
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

		for (MusPlayer xp : getAllXWingPlayers()) {
			players.add(xp.getPlayer());
		}

		return players;
	}

	@Override
	public MusTournamentGUI getTournamentGUI() {
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
			for (MusRound r : getAllRounds()) {
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
			
			generateEliminationRound(getAllRounds().size() + 1);
			
			// If there was only one match then there is no reason to create a
			// new round.
//			if (getLatestRound().getMatches().size() == 1) {
//				JOptionPane
//						.showMessageDialog(Main.getInstance(),
//								Language.final_round);
//				return false;
//			}
//
//			if (getLatestRound().isValid(true) == false) {
//				JOptionPane
//						.showMessageDialog(
//								Main.getInstance(),
//								Language.round_invalid);
//				return false;
//			}
//
//			generateSingleEliminationMatches(getLatestRound().getMatches()
//					.size());
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
				MusRound roundToRemove = rounds.get(index);
				for (MusMatch m : roundToRemove.getMatches()) {
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
		
		if(tournamentType == TournamentTypeEnum.SINGLE || tournamentType == TournamentTypeEnum.DOUBLE || tournamentType == TournamentTypeEnum.TRIPLE){
			generateEliminationRound(roundNumber);
		} else {
			generateSwissRound(roundNumber);
		}
		
		getTournamentGUI().getRankingTable().setPlayers(getAllXWingPlayers());
	}
	
	private void generateEliminationRound(int roundNumber){
		
		List<MusMatch> matches = new ArrayList<MusMatch>();
		
		if(tournamentType == TournamentTypeEnum.SINGLE){
			matches = MusEliminationMatchBuilder.getBracketMatches(this, getXWingPlayers(), 0);
		} else if(tournamentType == TournamentTypeEnum.DOUBLE){
			matches = MusEliminationMatchBuilder.getBracketMatches(this, getXWingPlayers(), 0);

			List<MusMatch> matches2 = new ArrayList<MusMatch>();			
			matches2 = MusEliminationMatchBuilder.getBracketMatches(this, getXWingPlayers(), 1);
			
			if(matches2.size() == 1 && matches.size() == 1){
				if(matches2.get(0).isBye() && matches.get(0).isBye()){
					matches.get(0).setPlayer2(matches2.get(0).getPlayer1());
					matches.get(0).setBye(false);
					matches2.clear();
				}
			}

			matches.addAll(matches2);
			
		} else if(tournamentType == TournamentTypeEnum.TRIPLE){
			matches = MusEliminationMatchBuilder.getBracketMatches(this, getXWingPlayers(), 0);

			List<MusMatch> matches2 = new ArrayList<MusMatch>();
			matches2 = MusEliminationMatchBuilder.getBracketMatches(this, getXWingPlayers(), 1);

			List<MusMatch> matches3 = new ArrayList<MusMatch>();
			matches3 = MusEliminationMatchBuilder.getBracketMatches(this, getXWingPlayers(), 2);
			
			if(matches3.size() == 1 && matches2.size() == 1){
				if(matches3.get(0).isBye() && matches2.get(0).isBye()){
					matches2.get(0).setPlayer2(matches3.get(0).getPlayer1());
					matches2.get(0).setBye(false);
					matches3.clear();
				}
			}
			if(matches2.size() == 1 && matches.size() == 1 && matches3.size() == 0){
				if(matches2.get(0).isBye() && matches.get(0).isBye()){
					matches.get(0).setPlayer2(matches2.get(0).getPlayer1());
					matches.get(0).setBye(false);
					matches2.clear();
				}
			}
			if(matches3.size() == 1 && matches.size() == 1 && matches2.size() == 0){
				if(matches3.get(0).isBye() && matches.get(0).isBye()){
					matches.get(0).setPlayer2(matches3.get(0).getPlayer1());
					matches.get(0).setBye(false);
					matches3.clear();
				}
			}
			
			matches.addAll(matches2);
			matches.addAll(matches3);
		}
		
		MusRound r = new MusRound(matches, this, roundNumber);
		r.setSingleElimination(true);
		rounds.add(r);
		getTournamentGUI().getRoundTabbedPane().addSwissTab(roundNumber, r.getPanel());;
	}
	
	private void generateSwissRound(int roundNumber){
		List<MusMatch> matches;
		if (roundNumber == 1) {

			matches = new ArrayList<MusMatch>();
			List<MusPlayer> tempList = new ArrayList<>();
			tempList.addAll(getXWingPlayers());

			List<MusPlayer> firstRoundByePlayers = new ArrayList<>();
			for (MusPlayer p : tempList) {
				if (p.isFirstRoundBye()) {
					firstRoundByePlayers.add(p);
				}
			}
			tempList.removeAll(firstRoundByePlayers);

			if (seedingEnum == InitialSeedingEnum.IN_ORDER) {

				while (tempList.isEmpty() == false) {
					MusPlayer player1 = tempList.get(0);
					MusPlayer player2 = null;
					tempList.remove(0);
					if (tempList.isEmpty() == false) {
						player2 = tempList.get(0);
						tempList.remove(0);
					}

					MusMatch match = new MusMatch(player1, player2);
					matches.add(match);
				}

			} else if (seedingEnum == InitialSeedingEnum.RANDOM) {
				Collections.shuffle(tempList);

				while (tempList.isEmpty() == false) {
					MusPlayer player1 = tempList.get(0);
					MusPlayer player2 = tempList.get(tempList.size() - 1);
					tempList.remove(player1);
					if (player1 == player2) {
						player2 = null;
					} else {
						tempList.remove(player2);
					}

					MusMatch match = new MusMatch(player1, player2);
					matches.add(match);
				}
			} else if (seedingEnum == InitialSeedingEnum.BY_GROUP) {
				Map<String, List<MusPlayer>> playerMap = new HashMap<String, List<MusPlayer>>();

				// Add players to map
				for (MusPlayer p : tempList) {
					List<MusPlayer> playerList = playerMap.get(p.getPlayer()
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
				for (List<MusPlayer> list : playerMap.values()) {
					Collections.shuffle(list);
				}

				MusPlayer p1 = null;
				MusPlayer p2 = null;
				while (seedValues.isEmpty() == false) {
					int i = 0;
					while (i < seedValues.size()) {
						if (p1 == null) {
							p1 = playerMap.get(seedValues.get(i)).get(0);
						} else {
							p2 = playerMap.get(seedValues.get(i)).get(0);
							matches.add(new MusMatch(p1, p2));
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
					matches.add(new MusMatch(p1, null));
				}
			}

			for (MusPlayer p : firstRoundByePlayers) {
				matches.add(new MusMatch(p, null));
			}

		} else {

			matches = getMatches(getXWingPlayers());
		}
		MusRound r = new MusRound(matches, this, roundNumber);
		rounds.add(r);
		if (roundNumber == 1
				&& tournamentType == TournamentTypeEnum.SINGLE
				&& (matches.size() == 1 || matches.size() == 2
						|| matches.size() == 4 || matches.size() == 8
						|| matches.size() == 16 || matches.size() == 32)) {
			r.setSingleElimination(true);
			getTournamentGUI().getRoundTabbedPane().addSwissTab(
					roundNumber, r.getPanel());
		} else {
			getTournamentGUI().getRoundTabbedPane().addSwissTab(roundNumber,
					r.getPanel());
		}

	}

	private List<MusMatch> getMatches(List<MusPlayer> userList) {
		List<MusMatch> matches = new ArrayList<MusMatch>();

		List<MusPlayer> tempList = new ArrayList<MusPlayer>();
		tempList.addAll(userList);
		Collections.sort(tempList, new MusComparator(this,
				MusComparator.pairingCompare));

		MusMatch byeMatch = null;
		// Setup the bye match if necessary
		// The player to get the bye is the lowest ranked player who has not had
		// a bye yet or who has the fewest byes
		if (tempList.size() % 2 == 1) {
			MusPlayer byeUser = null;
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
			byeMatch = new MusMatch(byeUser, null);
			tempList.remove(byeUser);
		}

		matches = new MusRandomMatchGeneration(this, tempList)
				.generateMatches();

		if (MusMatch.hasDuplicate(matches)) {
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

		List<MusMatch> matches = new ArrayList<>();

		List<MusMatch> matchesCorrected = new ArrayList<MusMatch>();

		if (getLatestRound().isSingleElimination()) {
			List<MusMatch> lastRoundMatches = getLatestRound().getMatches();

			for (int index = 0; index < lastRoundMatches.size(); index = index + 2) {
				MusMatch newMatch = new MusMatch(lastRoundMatches
						.get(index).getWinner(), lastRoundMatches
						.get(index + 1).getWinner());
				matches.add(newMatch);
			}

			matchesCorrected = matches;
		} else {
			List<MusPlayer> tempList = new ArrayList<>();
			tempList.addAll(getXWingPlayers());
			Collections.sort(tempList, new MusComparator(this,
					MusComparator.rankingCompare));
			tempList = tempList.subList(0, cutSize);

			while (tempList.isEmpty() == false) {
				MusPlayer player1 = tempList.get(0);
				MusPlayer player2 = tempList.get(tempList.size() - 1);
				tempList.remove(player1);
				if (player1 == player2) {
					player2 = null;
				} else {
					tempList.remove(player2);
				}

				MusMatch match = new MusMatch(player1, player2);
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

		MusRound r = new MusRound(matchesCorrected, this, null);
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
		for (MusPlayer p : players) {
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
		XMLUtils.appendObject(sb,  "TOURNAMENTTYPE", tournamentType);
		
		return sb;
	}

	@Override
	public void startTournament() {
		generateRound(1);
	}

	@Override
	public void addPlayer(Player p) {
		
		for(MusRound r : getAllRounds()){
			for(MusMatch m : r.getMatches()){
				if(m.getPlayer1().getPlayer().equals(p)){
					getXWingPlayers().add(m.getPlayer1());
					return;
				} else if(m.getPlayer2() != null && m.getPlayer2().getPlayer().equals(p)) {
					getXWingPlayers().add(m.getPlayer2());
					return;
				}
			}
		}
		
		MusPlayer xPlayer = new MusPlayer(p);
		getXWingPlayers().add(xPlayer);
	}

	@Override
	public void dropPlayer(Player p) {

		MusPlayer xPlayer = null;

		for (MusPlayer xp : getXWingPlayers()) {
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
		URL imgURL = MusTournament.class.getResource("x.png");
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

	public boolean isElimination() {
		return tournamentType == TournamentTypeEnum.SINGLE || tournamentType == TournamentTypeEnum.DOUBLE || tournamentType == TournamentTypeEnum.TRIPLE;
	}
}
