package cryodex.modules.mus;

import java.util.ArrayList;
import java.util.List;

import cryodex.xml.XMLObject;
import cryodex.xml.XMLUtils;
import cryodex.xml.XMLUtils.Element;

public class MusRound implements XMLObject {
	private List<MusMatch> matches;
	private MusRoundPanel panel;
	private Boolean isSingleElimination = false;

	public MusRound(Element roundElement, MusTournament t) {
		this.isSingleElimination = roundElement
				.getBooleanFromChild("ISSINGLEELIMINATION");

		Element matchElement = roundElement.getChild("MATCHES");

		if (matchElement != null) {
			matches = new ArrayList<MusMatch>();
			for (Element e : matchElement.getChildren()) {
				matches.add(new MusMatch(e));
			}
		}

		this.panel = new MusRoundPanel(t, matches);
	}

	public MusRound(List<MusMatch> matches, MusTournament t,
			Integer roundNumber) {
		this.matches = matches;
		this.panel = new MusRoundPanel(t, matches);
	}

	public List<MusMatch> getMatches() {
		return matches;
	}

	public void setMatches(List<MusMatch> matches) {
		this.matches = matches;
	}

	public MusRoundPanel getPanel() {
		return panel;
	}

	public void setPanel(MusRoundPanel panel) {
		this.panel = panel;
	}

	public void setSingleElimination(boolean isSingleElimination) {
		this.isSingleElimination = isSingleElimination;
	}

	public boolean isSingleElimination() {
		return isSingleElimination == null ? false : isSingleElimination;
	}

	@Override
	public StringBuilder appendXML(StringBuilder sb) {

		XMLUtils.appendObject(sb, "ISSINGLEELIMINATION", isSingleElimination());
		XMLUtils.appendList(sb, "MATCHES", "MATCH", getMatches());

		return sb;
	}

	public boolean isComplete() {
		for (MusMatch m : getMatches()) {
			if (m.isMatchComplete() == false) {
				return false;
			}
		}
		return true;
	}

	public boolean isValid(boolean isSingleElimination) {
		boolean result = true;
		for (MusMatch m : getMatches()) {
			if (m.isValidResult(isSingleElimination) == false) {
				result = false;
				break;
			}
		}

		panel.markInvalid(isSingleElimination);

		return result;
	}
}
