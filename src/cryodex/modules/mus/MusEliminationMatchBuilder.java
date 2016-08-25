package cryodex.modules.mus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MusEliminationMatchBuilder {

	private static int getNumberOfByes(int playerCount){
		
		int byes = 0;
		
		if(playerCount <= 2){
			byes = 2-playerCount;
		} else if(playerCount <= 4){
			byes = 4-playerCount;
		} else if(playerCount <= 8){
			byes = 8-playerCount;
		} else if(playerCount <= 16){
			byes = 16-playerCount;
		} else if(playerCount <= 32){
			byes = 32-playerCount;
		} else if(playerCount <= 64){
			byes = 64-playerCount;
		}
		
		return byes;
	}
	
	public static List<MusPlayer> chooseByePlayers(final MusTournament tournament, List<MusPlayer> players, int numberOfByes){
		
		Collections.sort(players, new Comparator<MusPlayer>() {
			@Override
			public int compare(MusPlayer o1, MusPlayer o2) {
				return Integer.compare(o1.getByes(tournament), o2.getByes(tournament));
			}
		});
		
		int minByes = players.get(0).getByes(tournament);
		
		List<MusPlayer> returnPlayers = new ArrayList<MusPlayer>();
		
		for(MusPlayer p : players){
			if(p.getByes(tournament) == minByes){
				returnPlayers.add(p);
			}
		}
		
		Random r = new Random();
		while(returnPlayers.size() > numberOfByes){
			returnPlayers.remove(r.nextInt(returnPlayers.size()));
		}
		
		return returnPlayers;
	}
	
	public static List<MusMatch> getBracketMatches(final MusTournament tournament, List<MusPlayer> allPlayers, int losses){
		
		List<MusPlayer> players = new ArrayList<MusPlayer>();
		
		for(MusPlayer p : allPlayers){
			if(p.getLosses(tournament) == losses){
				players.add(p);
			}
		}
		
		if(players.size() == 0){
			return new ArrayList<MusMatch>();
		}
		
		int numberOfByes = getNumberOfByes(players.size());
		
		List<MusPlayer> byePlayers = null;
		if(numberOfByes != 0){
			byePlayers = chooseByePlayers(tournament, players, numberOfByes);
			players.removeAll(byePlayers);
		}
		
		
		List<MusMatch> matches = generateNonDuplicatePairings(tournament, players);
		
		if(numberOfByes != 0){
			matches.addAll(generateByeMatches(byePlayers));
		}

		return matches;
	}

	private static List<MusMatch> generateNonDuplicatePairings(MusTournament tournament, List<MusPlayer> players) {
		
		boolean isDuplicates = true;
		int tryCountMax = 10;
		int tryCount = 0;
		
		List<MusMatch> returnMatches = new ArrayList<MusMatch>();
		
		while(isDuplicates && tryCount <= tryCountMax){
		
			tryCount++;
			isDuplicates = false;
			returnMatches.clear();
			
			Collections.shuffle(players);
			
			for(int index = 0 ; index < players.size() ; index = index + 2){
				MusMatch m = new MusMatch();
				m.setPlayer1(players.get(index));
				m.setPlayer2(players.get(index + 1));
				returnMatches.add(m);
				
				m.checkDuplicate(tournament.getAllRounds());
				if(m.isDuplicate()){
					isDuplicates = true;
				}
			}
		}
		
		return returnMatches;
	}
	
	private static List<MusMatch> generateByeMatches(List<MusPlayer> players){
		
		List<MusMatch> returnMatches = new ArrayList<MusMatch>();
		
		for(MusPlayer p : players){
			MusMatch m = new MusMatch();
			m.setPlayer1(p);
			m.setBye(true);
			returnMatches.add(m);
		}
		
		return returnMatches;
	}
}
