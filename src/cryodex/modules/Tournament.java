package cryodex.modules;

import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import cryodex.Player;
import cryodex.xml.XMLObject;

public interface Tournament extends XMLObject {

	public int getRoundCount();

	public void setPlayers(List<Player> players);

	public List<Player> getPlayers();

	public Set<Player> getAllPlayers();

	public String getName();

	public void setName(String name);

	public void updateVisualOptions();

	public void cancelRound(int roundNumber);

	public void generateRound(int roundNumber);

	public void generateSingleEliminationMatches(int cutSize);

	public void startTournament();

	public void addPlayer(Player p);

	public void dropPlayer(Player p);

	public void resetRankingTable();

	public boolean generateNextRound();

	public Icon getIcon();

	String getModuleName();

	TournamentGUI getTournamentGUI();
}
