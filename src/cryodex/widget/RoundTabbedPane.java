package cryodex.widget;

import javax.swing.JPanel;

import cryodex.CryodexController;
import cryodex.Language;

public class RoundTabbedPane extends AddTabTabbedPane {

	private static final long serialVersionUID = 7883808610257649371L;

	public RoundTabbedPane() {
		super(Language.generate_next_round);
	}

	public void addSwissTab(int roundNumber, JPanel roundPanel) {
		setVisible(true);
		while (this.getTabCount() > roundNumber) {
			this.remove(roundNumber - 2);
		}

		this.addTab(Language.round + " " + roundNumber, roundPanel);

		this.validate();
		this.repaint();
	}

	public void addSingleEliminationTab(int numberOfPlayers, JPanel roundPanel) {
		setVisible(true);

		this.addTab(Language.top + numberOfPlayers, roundPanel);

		this.validate();
		this.repaint();
	}

	@Override
	public void triggerEvent() {
		boolean successful = CryodexController.getActiveTournament()
				.generateNextRound();

		if (successful == false) {
			setSelectedIndex(getTabCount() - 2);
		}
	}
}
