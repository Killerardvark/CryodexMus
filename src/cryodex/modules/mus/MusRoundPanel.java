package cryodex.modules.mus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cryodex.CryodexController;
import cryodex.Language;
import cryodex.Player;
import cryodex.widget.ComponentUtils;
import cryodex.widget.ConfirmationTextField;

public class MusRoundPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final List<MusMatch> matches;
	private final List<GamePanel> gamePanels = new ArrayList<GamePanel>();
	private JPanel quickEntryPanel;
	private JPanel quickEntrySubPanel;
	private JTextField roundNumber;
	private JComboBox<MusPlayer> playerCombo;
	private final JScrollPane scroll;

	private final MusTournament tournament;

	public MusRoundPanel(MusTournament t, List<MusMatch> matches) {

		super(new BorderLayout());

		this.tournament = t;
		this.matches = matches;
		this.setBorder(BorderFactory.createLineBorder(Color.black));

		int counter = 1;
		for (MusMatch match : matches) {
			GamePanel gpanel = new GamePanel(counter, match);
			gamePanels.add(gpanel);
			counter++;
		}

		scroll = new JScrollPane(ComponentUtils.addToFlowLayout(buildPanel(), FlowLayout.CENTER));
		scroll.setBorder(BorderFactory.createEmptyBorder());

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				scroll.getVerticalScrollBar().setValue(0);
				scroll.getVerticalScrollBar().setUnitIncrement(15);
			}
		});

		this.add(getQuickEntryPanel(), BorderLayout.NORTH);
		this.add(scroll, BorderLayout.CENTER);
	}

	public JPanel getQuickEntryPanel() {
		if (quickEntryPanel == null) {
			quickEntryPanel = new JPanel(new BorderLayout());
			quickEntryPanel.setVisible(CryodexController.getOptions().isShowQuickFind());
			ComponentUtils.forceSize(quickEntryPanel, 405, 135);

			quickEntrySubPanel = new JPanel(new BorderLayout());
			ComponentUtils.forceSize(quickEntrySubPanel, 400, 130);

			roundNumber = new JTextField(5);

			roundNumber.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					update();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					update();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					update();
				}
			});

			List<MusPlayer> playerList = new ArrayList<MusPlayer>();

			playerList.add(new MusPlayer(new Player()));
			playerList.addAll(tournament.getXWingPlayers());

			Collections.sort(playerList);

			playerCombo = new JComboBox<MusPlayer>(playerList.toArray(new MusPlayer[playerList.size()]));

			playerCombo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					update();
				}
			});

			quickEntryPanel
					.add(ComponentUtils.addToFlowLayout(
							ComponentUtils.addToHorizontalBorderLayout(new JLabel(Language.enter_table_number),
									roundNumber, ComponentUtils.addToHorizontalBorderLayout(
											new JLabel(Language.or_choose_a_player), playerCombo, null)),
							FlowLayout.CENTER), BorderLayout.NORTH);

			quickEntryPanel.add(quickEntrySubPanel);
		}

		return quickEntryPanel;
	}

	public void update() {

		scroll.getViewport().removeAll();
		scroll.getViewport().add(ComponentUtils.addToFlowLayout(buildPanel(), FlowLayout.CENTER));
		ComponentUtils.repaint(MusRoundPanel.this);

		Integer i = null;
		try {
			i = Integer.parseInt(roundNumber.getText());
		} catch (NumberFormatException e) {

		}

		MusPlayer player = playerCombo.getSelectedIndex() == 0 ? null : (MusPlayer) playerCombo.getSelectedItem();

		if (player != null) {
			roundNumber.setEnabled(false);
		} else if (i != null) {
			playerCombo.setEnabled(false);
		} else {
			roundNumber.setEnabled(true);
			playerCombo.setEnabled(true);
		}

		GamePanel gamePanel = null;
		if (i != null) {
			if (i > gamePanels.size()) {
				return;
			}

			gamePanel = gamePanels.get(i - 1);
		} else if (player != null) {
			for (GamePanel g : gamePanels) {
				if (g.getMatch().getPlayer1() == player) {
					gamePanel = g;
					break;
				} else if (g.getMatch().getPlayer2() != null && g.getMatch().getPlayer2() == player) {
					gamePanel = g;
					break;
				}
			}
		}

		if (gamePanel == null) {
			return;
		}

		quickEntrySubPanel.add(gamePanel.getPlayerTitle(), BorderLayout.CENTER);

		quickEntrySubPanel.removeAll();

		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(gamePanel.getPlayerTitle(), gbc);

		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(gamePanel.getResultCombo(), gbc);

		if (gamePanel.getMatch().getPlayer2() != null) {
			gbc.gridy++;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.NONE;

			gbc.gridx = 1;
			panel.add(gamePanel.getPlayer1KillLabel(), gbc);

			gbc.gridx = 2;
			panel.add(gamePanel.getPlayer1KillPointsField(), gbc);

			gbc.gridy++;
			gbc.gridwidth = 1;

			gbc.gridx = 1;
			panel.add(gamePanel.getPlayer2KillLabel(), gbc);

			gbc.gridx = 2;
			panel.add(gamePanel.getPlayer2KillPointsField(), gbc);
		}

		quickEntrySubPanel.add(panel, BorderLayout.CENTER);

		ComponentUtils.repaint(quickEntrySubPanel);
		ComponentUtils.repaint(quickEntryPanel);
	}

	public JPanel buildPanel() {

		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = -1;

		for (GamePanel gp : gamePanels) {
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.EAST;
			panel.add(gp.getPlayerTitle(), gbc);

			gbc.gridx = 2;
			gbc.fill = GridBagConstraints.BOTH;
			panel.add(gp.getResultCombo(), gbc);

			if (gp.getMatch().getPlayer2() != null) {
				gbc.gridy++;
				gbc.gridwidth = 1;
				gbc.fill = GridBagConstraints.NONE;

				gbc.gridx = 1;
				panel.add(gp.getPlayer1KillLabel(), gbc);

				gbc.gridx = 2;
				panel.add(gp.getPlayer1KillPointsField(), gbc);

				gbc.gridx = 3;
				gbc.anchor = GridBagConstraints.WEST;
				panel.add(gp.getPlayer1KillPointsField().getIndicator(), gbc);

				gbc.gridy++;
				gbc.gridwidth = 1;
				gbc.anchor = GridBagConstraints.EAST;

				gbc.gridx = 1;
				panel.add(gp.getPlayer2KillLabel(), gbc);

				gbc.gridx = 2;
				panel.add(gp.getPlayer2KillPointsField(), gbc);

				gbc.gridx = 3;
				gbc.anchor = GridBagConstraints.WEST;
				panel.add(gp.getPlayer2KillPointsField().getIndicator(), gbc);
			}
		}

		return panel;
	}

	public List<MusMatch> getMatches() {
		return matches;
	}

	public void resetGamePanels(boolean isTextOnly) {
		for (GamePanel gp : gamePanels) {
			gp.reset(isTextOnly);
		}
		getQuickEntryPanel().setVisible(CryodexController.getOptions().isShowQuickFind());
		ComponentUtils.repaint(this);
	}

	private class GamePanel {
		private final MusMatch match;
		private JLabel playersTitle;
		private JComboBox<String> resultsCombo;
		private ConfirmationTextField player1KillPoints;
		private ConfirmationTextField player2KillPoints;
		private JLabel player1KillLabel;
		private JLabel player2KillLabel;
		private boolean isLoading = false;
		private int tableNumber = -1;

		public GamePanel(int tableNumber, MusMatch match) {

			this.tableNumber = tableNumber;

			isLoading = true;

			this.match = match;

			reset(false);

			init();
		}

		private void init() {
			if (match.isMatchComplete()) {
				if (match.isBye()) {
					getResultCombo().setSelectedIndex(1);
				} else {
					if (match.getWinner() == match.getPlayer1()) {
						getResultCombo().setSelectedIndex(1);
					} else if (match.getWinner() == match.getPlayer2()) {
						getResultCombo().setSelectedIndex(2);
					}
				}
			}
			if (match.getPlayer2() != null) {
				if (match.getPlayer1PointsDestroyed() != null) {
					getPlayer1KillPointsField().setText(String.valueOf(match.getPlayer1PointsDestroyed()));
				}
				if (match.getPlayer2PointsDestroyed() != null) {
					getPlayer2KillPointsField().setText(String.valueOf(match.getPlayer2PointsDestroyed()));
				}
			}
			isLoading = false;
		}

		private MusMatch getMatch() {
			return match;
		}

		private JLabel getPlayerTitle() {
			if (playersTitle == null) {

				playersTitle = new JLabel("");
				playersTitle.setFont(new Font(playersTitle.getFont().getName(), playersTitle.getFont().getStyle(), 20));
				playersTitle.setHorizontalAlignment(SwingConstants.RIGHT);
			}
			return playersTitle;
		}

		private JLabel getPlayer1KillLabel() {
			if (player1KillLabel == null) {
				player1KillLabel = new JLabel();
			}
			return player1KillLabel;
		}

		private JLabel getPlayer2KillLabel() {
			if (player2KillLabel == null) {
				player2KillLabel = new JLabel();
			}
			return player2KillLabel;
		}

		private String[] getComboValues() {

			if (match.getPlayer2() == null) {
				String[] values = { Language.select_a_result, Language.bye };
				return values;
			} else {
				String generic = MusModule.getInstance().getOptions().isEnterOnlyPoints() ? Language.enter_results
						: Language.select_a_result;
				String[] values = { generic, Language.win + " - " + match.getPlayer1().getName(),
						Language.win + " - " + match.getPlayer2().getName() };
				return values;
			}
		}

		private JComboBox<String> getResultCombo() {
			if (resultsCombo == null) {

				resultsCombo = new JComboBox<String>(getComboValues());

				resultsCombo.setRenderer(new DefaultListCellRenderer() {
					private static final long serialVersionUID = 1L;

					@Override
					public void paint(Graphics g) {
						setForeground(Color.BLACK);
						super.paint(g);
					}
				});

				resultsCombo.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						comboChange();
					}
				});
			}
			return resultsCombo;
		}

		private void comboChange() {

			if (isLoading) {
				return;
			}

			switch (resultsCombo.getSelectedIndex()) {
			case 0:
				match.setWinner(null);
				match.setBye(false);
				break;
			case 1:
				if (match.getPlayer2() == null) {
					match.setBye(true);
				} else {
					match.setWinner(match.getPlayer1());
				}
				break;
			case 2:
				match.setWinner(match.getPlayer2());
				break;
			default:
				break;
			}
			tournament.getTournamentGUI().getRankingTable().resetPlayers();

		}

		public ConfirmationTextField getPlayer1KillPointsField() {
			if (player1KillPoints == null) {
				player1KillPoints = new ConfirmationTextField();
				player1KillPoints.addFocusListener(new FocusListener() {

					@Override
					public void focusLost(FocusEvent arg0) {
						Integer points = null;
						try {
							points = Integer.valueOf(player1KillPoints.getText());
						} catch (Exception e) {

						}

						match.setPlayer1PointsDestroyed(points);
						tournament.getTournamentGUI().getRankingTable().resetPlayers();

						if (MusModule.getInstance().getOptions().isEnterOnlyPoints()) {
							setResultsCombo();
						}
					}

					@Override
					public void focusGained(FocusEvent arg0) {
					}
				});
				ComponentUtils.forceSize(player1KillPoints, 50, 25);
			}

			return player1KillPoints;
		}

		public ConfirmationTextField getPlayer2KillPointsField() {
			if (player2KillPoints == null) {
				player2KillPoints = new ConfirmationTextField();
				player2KillPoints.addFocusListener(new FocusListener() {

					@Override
					public void focusLost(FocusEvent arg0) {
						Integer points = null;
						try {
							points = Integer.valueOf(player2KillPoints.getText());
						} catch (Exception e) {

						}

						match.setPlayer2PointsDestroyed(points);
						tournament.getTournamentGUI().getRankingTable().resetPlayers();

						if (MusModule.getInstance().getOptions().isEnterOnlyPoints()) {
							setResultsCombo();
						}
					}

					@Override
					public void focusGained(FocusEvent arg0) {
					}
				});
				ComponentUtils.forceSize(player2KillPoints, 50, 25);
			}

			return player2KillPoints;
		}

		public void reset(boolean isTextOnly) {

			String titleText = null;

			boolean showKillPoints = MusModule.getInstance().getOptions().isShowKillPoints();
			boolean enterOnlyPoints = MusModule.getInstance().getOptions().isEnterOnlyPoints();

			getPlayer1KillLabel().setVisible(showKillPoints);
			getPlayer1KillPointsField().setVisible(showKillPoints);
			getPlayer2KillLabel().setVisible(showKillPoints);
			getPlayer2KillPointsField().setVisible(showKillPoints);

			if (match.getPlayer2() == null) {
				titleText = match.getPlayer1().getName() + " " + Language.has_a_bye;
			} else {
				titleText = match.getPlayer1().getName() + " " + Language.vs + " " + match.getPlayer2().getName();
				if (match.isDuplicate()) {
					titleText = "(" + Language.duplicate + ")" + titleText;
				}

				if (CryodexController.getOptions().isShowTableNumbers()) {
					titleText = tableNumber + ": " + titleText;
				}

				getPlayer1KillLabel().setText(match.getPlayer1().getName() + " " + Language.points);
				getPlayer2KillLabel().setText(match.getPlayer2().getName() + " " + Language.points);
			}

			getPlayerTitle().setText(titleText);

			if (isTextOnly == false) {
				getResultCombo().removeAllItems();
				for (String s : getComboValues()) {
					getResultCombo().addItem(s);
				}
			}

			getResultCombo().setEnabled(!enterOnlyPoints);

			if (enterOnlyPoints) {
				if (getComboValues().length == 2) {
					getResultCombo().setSelectedIndex(1);
					match.setBye(true);
				} else {
					setResultsCombo();
				}
			}
		}

		/**
		 * This function sets the combo box value to the winner of the match
		 * based on points.
		 */
		public void setResultsCombo() {

			boolean enterOnlyPoints = MusModule.getInstance().getOptions().isEnterOnlyPoints();

			if (match.getPlayer1PointsDestroyed() != null || match.getPlayer2PointsDestroyed() != null) {

				Integer p1points = match.getPlayer1PointsDestroyed() == null ? 0 : match.getPlayer1PointsDestroyed();
				Integer p2points = match.getPlayer2PointsDestroyed() == null ? 0 : match.getPlayer2PointsDestroyed();

				if (p1points.equals(p2points)) {
					// Only reset the result if it was not enabled before. This
					// prevents the combo box from resetting if the result
					// didn't actually change.
					if (getResultCombo().isEnabled() == false) {
						getResultCombo().setSelectedIndex(0);
					}
					getResultCombo().setEnabled(true);
				}
				if (p1points > p2points) {
					getResultCombo().setSelectedIndex(1);
					getResultCombo().setEnabled(!enterOnlyPoints);
				}

				if (p2points > p1points) {
					getResultCombo().setSelectedIndex(2);
					getResultCombo().setEnabled(!enterOnlyPoints);
				}
			} else {
				getResultCombo().setSelectedIndex(0);
				getResultCombo().setEnabled(!enterOnlyPoints);
			}
			comboChange();
		}

		public void markInvalid(boolean isSingleElimination) {
			if (match.isValidResult(isSingleElimination) == false) {
				getPlayerTitle().setForeground(Color.red);
			} else {
				getPlayerTitle().setForeground(Color.black);
			}
		}
	}

	public void markInvalid(boolean isSingleElimination) {
		for (GamePanel gamePanel : gamePanels) {
			gamePanel.markInvalid(isSingleElimination);
		}
	}

}
