package cryodex.modules.xwing;

import java.util.List;

import cryodex.CryodexController;
import cryodex.CryodexController.Modules;
import cryodex.Player;
import cryodex.modules.Module;
import cryodex.xml.XMLObject;
import cryodex.xml.XMLUtils;
import cryodex.xml.XMLUtils.Element;

public class XWingMatch implements XMLObject {

	public static final int WIN_POINTS = 1;
	public static final int BYE_POINTS = 1;
	public static final int LOSS_POINTS = 0;

	private XWingPlayer player1;
	private XWingPlayer player2;
	private XWingPlayer winner;
	private boolean isBye = false;
	private Integer player1PointsDestroyed;
	private Integer player2PointsDestroyed;
	private boolean isDuplicate;

	public XWingMatch() {

	}

	public XWingMatch(XWingPlayer player1, XWingPlayer player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public XWingMatch(Element matchElement) {

		Module m = Modules.getModuleByName(getModuleName());

		String player1String = matchElement.getStringFromChild("PLAYER1");
		Player p = CryodexController.getPlayerByID(player1String);
		if (p != null) {
			player1 = (XWingPlayer) p.getModuleInfoByModule(m);
		}

		String player2String = matchElement.getStringFromChild("PLAYER2");
		p = CryodexController.getPlayerByID(player2String);
		if (p != null) {
			player2 = (XWingPlayer) p.getModuleInfoByModule(m);
		}

		String winnerString = matchElement.getStringFromChild("WINNER");
		p = CryodexController.getPlayerByID(winnerString);
		if (p != null) {
			winner = (XWingPlayer) p.getModuleInfoByModule(m);
		}

		isBye = matchElement.getBooleanFromChild("ISBYE");
		isDuplicate = matchElement.getBooleanFromChild("ISDUPLICATE");

		player1PointsDestroyed = matchElement.getIntegerFromChild("PLAYER1POINTS");
		player2PointsDestroyed = matchElement.getIntegerFromChild("PLAYER2POINTS");

	}

	public XWingPlayer getPlayer1() {
		return player1;
	}

	public void setPlayer1(XWingPlayer player1) {
		this.player1 = player1;
	}

	public XWingPlayer getPlayer2() {
		return player2;
	}

	public void setPlayer2(XWingPlayer player2) {
		this.player2 = player2;
	}

	public XWingPlayer getWinner() {
		return winner;
	}

	public void setWinner(XWingPlayer winner) {
		this.winner = winner;
	}

	public boolean isBye() {
		return isBye;
	}

	public void setBye(boolean isBye) {
		this.isBye = isBye;
	}

	public Integer getPlayer1PointsDestroyed() {
		return player1PointsDestroyed;
	}

	public void setPlayer1PointsDestroyed(Integer player1PointsDestroyed) {
		this.player1PointsDestroyed = player1PointsDestroyed;
	}

	public Integer getPlayer2PointsDestroyed() {
		return player2PointsDestroyed;
	}

	public void setPlayer2PointsDestroyed(Integer player2PointsDestroyed) {
		this.player2PointsDestroyed = player2PointsDestroyed;
	}

	public boolean isDuplicate() {
		return isDuplicate;
	}

	public void setDuplicate(boolean isDuplicate) {
		this.isDuplicate = isDuplicate;
	}

	public boolean isMatchComplete() {
		return isBye || winner != null;
	}

	public boolean isValidResult(boolean isSingleElimination) {
		Integer player1Points = player1PointsDestroyed == null ? 0 : player1PointsDestroyed;
		Integer player2Points = player2PointsDestroyed == null ? 0 : player2PointsDestroyed;

		// If there is no second player, it must be a bye
		if (player2 == null && isBye) {
			return true;
		}

		// For single elimination we just look to make sure the correct
		// player is the winner according to points
		if ((winner == player1 && player1Points >= player2Points)
				|| (winner == player2 && player2Points >= player1Points)
				|| (player1Points == player2Points && winner != null)) {
			return true;
		}
		
		return false;
	}

	public void checkDuplicate(List<XWingRound> rounds) {

		if (this.getPlayer2() == null) {
			this.setDuplicate(false);
			return;
		}

		for (XWingRound r : rounds) {
			if (r.isSingleElimination()) {
				continue;
			}

			for (XWingMatch match : r.getMatches()) {
				if (match.getPlayer2() == null || match == this) {
					continue;
				}

				if ((match.getPlayer1() == this.getPlayer1() && match.getPlayer2() == this.getPlayer2())
						|| (match.getPlayer1() == this.getPlayer2() && match.getPlayer2() == this.getPlayer1())) {
					this.setDuplicate(true);
					return;
				}
			}
		}

		this.setDuplicate(false);
	}

	@Override
	public String toString() {
		return getPlayer1() + " vs " + getPlayer2() + " : isDuplicate=" + isDuplicate();
	}

	@Override
	public StringBuilder appendXML(StringBuilder sb) {

		XMLUtils.appendObject(sb, "PLAYER1", getPlayer1().getPlayer().getSaveId());
		XMLUtils.appendObject(sb, "PLAYER2", getPlayer2() == null ? "" : getPlayer2().getPlayer().getSaveId());
		XMLUtils.appendObject(sb, "WINNER", getWinner() == null ? "" : getWinner().getPlayer().getSaveId());
		XMLUtils.appendObject(sb, "ISBYE", isBye());
		XMLUtils.appendObject(sb, "PLAYER1POINTS", getPlayer1PointsDestroyed());
		XMLUtils.appendObject(sb, "PLAYER2POINTS", getPlayer2PointsDestroyed());
		XMLUtils.appendObject(sb, "ISDUPLICATE", isDuplicate());

		return sb;
	}

	public static boolean hasDuplicate(List<XWingMatch> matches) {
		boolean duplicateFound = false;
		for (XWingMatch dc : matches) {
			if (dc.isDuplicate()) {
				duplicateFound = true;
				break;
			}
		}

		return duplicateFound;
	}

	public String getModuleName() {
		return Modules.XWING.getName();
	}
}
