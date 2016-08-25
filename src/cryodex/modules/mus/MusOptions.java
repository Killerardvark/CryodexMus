package cryodex.modules.mus;

import cryodex.CryodexController;
import cryodex.modules.Tournament;
import cryodex.xml.XMLObject;
import cryodex.xml.XMLUtils;
import cryodex.xml.XMLUtils.Element;

public class MusOptions implements XMLObject {
	boolean showKillPoints = true;
	boolean enterOnlyPoints = true;

	public MusOptions() {

	}

	public MusOptions(Element e) {

		if (e != null) {
			showKillPoints = e.getBooleanFromChild("SHOWKILLPOINTS", true);
			enterOnlyPoints = e.getBooleanFromChild("ENTERONLYPOINTS", true);
		}
	}

	public boolean isShowKillPoints() {
		return showKillPoints;
	}

	public void setShowKillPoints(boolean showKillPoints) {
		this.showKillPoints = showKillPoints;
		updateTournamentVisuals();
	}

	public boolean isEnterOnlyPoints() {
		return enterOnlyPoints;
	}

	public void setEnterOnlyPoints(boolean enterOnlyPoints) {
		this.enterOnlyPoints = enterOnlyPoints;
		updateTournamentVisuals();
	}

	private void updateTournamentVisuals() {
		if (CryodexController.isLoading == false
				&& CryodexController.getAllTournaments() != null) {
			for (Tournament t : CryodexController.getAllTournaments()) {
				if (t instanceof MusTournament) {
					t.updateVisualOptions();
				}
			}
		}

		CryodexController.saveData();
	}

	@Override
	public StringBuilder appendXML(StringBuilder sb) {
		XMLUtils.appendObject(sb, "SHOWKILLPOINTS", showKillPoints);
		XMLUtils.appendObject(sb, "ENTERONLYPOINTS", enterOnlyPoints);

		return sb;
	}
}