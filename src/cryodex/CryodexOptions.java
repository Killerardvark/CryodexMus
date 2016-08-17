package cryodex;

import java.util.ArrayList;
import java.util.List;

import cryodex.CryodexController.Modules;
import cryodex.modules.Tournament;
import cryodex.xml.XMLObject;
import cryodex.xml.XMLUtils;
import cryodex.xml.XMLUtils.Element;

public class CryodexOptions implements XMLObject {
	private boolean showTableNumbers = true;
	private boolean showQuickFind = false;
	private final List<Modules> nonVisibleModules = new ArrayList<Modules>();

	public CryodexOptions() {

	}

	public CryodexOptions(Element e) {
		showTableNumbers = e.getBooleanFromChild("SHOWTABLENUMBERS", false);
		showQuickFind = e.getBooleanFromChild("SHOWQUICKFIND", false);

		String modulesToTurnOff = e.getStringFromChild("NONVISIBLEMODULES");

		if (modulesToTurnOff != null && modulesToTurnOff.isEmpty() == false) {
			for (String s : modulesToTurnOff.split(",")) {
				Modules m = Modules.getEnumByName(s);

				if (m != null) {
					nonVisibleModules.add(m);
					m.getModule().setModuleEnabled(false);
					m.getModule().getViewMenuItem().setSelected(false);
				}
			}
		}
	}

	public boolean isShowTableNumbers() {
		return showTableNumbers;
	}

	public void setShowTableNumbers(boolean showTableNumbers) {
		this.showTableNumbers = showTableNumbers;
		updateTournamentVisuals();
	}

	public boolean isShowQuickFind() {
		return showQuickFind;
	}

	public void setShowQuickFind(boolean showQuickFind) {
		this.showQuickFind = showQuickFind;
		updateTournamentVisuals();
	}

	public List<Modules> getNonVisibleModules() {
		return nonVisibleModules;
	}

	private void updateTournamentVisuals() {
		if (CryodexController.isLoading == false
				&& CryodexController.getAllTournaments() != null) {
			for (Tournament t : CryodexController.getAllTournaments()) {
				t.updateVisualOptions();
			}
			CryodexController.saveData();
		}
	}

	@Override
	public StringBuilder appendXML(StringBuilder sb) {

		String moduleString = "";
		String seperator = "";
		for (Modules m : getNonVisibleModules()) {
			moduleString += seperator + m.getName();
			seperator = ",";
		}

		XMLUtils.appendObject(sb, "SHOWQUICKFIND", showQuickFind);
		XMLUtils.appendObject(sb, "SHOWTABLENUMBERS", showTableNumbers);
		XMLUtils.appendObject(sb, "NONVISIBLEMODULES", moduleString);

		return sb;
	}
}