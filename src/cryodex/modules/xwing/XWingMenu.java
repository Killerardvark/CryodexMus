package cryodex.modules.xwing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cryodex.CryodexController;
import cryodex.Language;
import cryodex.CryodexController.Modules;
import cryodex.Main;
import cryodex.Player;
import cryodex.modules.Menu;
import cryodex.modules.xwing.export.XWingExportController;
import cryodex.widget.ComponentUtils;

@SuppressWarnings("serial")
public class XWingMenu implements Menu {

	private JMenu mainMenu;

	private JMenu viewMenu;
	private JMenu tournamentMenu;
	private JMenu roundMenu;
	private JMenu exportMenu;

	private JMenuItem deleteTournament;

	private JCheckBoxMenuItem showKillPoints;
	private JCheckBoxMenuItem onlyEnterPoints;

	private JMenuItem cutPlayers;

	@Override
	public JMenu getMenu() {

		if (mainMenu == null) {

			mainMenu = new JMenu(Modules.XWING.getName());
			mainMenu.setMnemonic('X');

			JMenuItem createNewTournament = new JMenuItem(
					Language.create_new_tournament);
			createNewTournament.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Main.getInstance().setExtendedState(Frame.MAXIMIZED_BOTH);
					XWingModule.createTournament();
				}
			});

			deleteTournament = new JMenuItem(Language.delete_tournament);
			deleteTournament.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					CryodexController.deleteTournament(true);
				}
			});

			mainMenu.add(createNewTournament);
			mainMenu.add(deleteTournament);
			mainMenu.add(getViewMenu());
			mainMenu.add(getTournamentMenu());
			mainMenu.add(getRoundMenu());
			mainMenu.add(getExportMenu());
		}

		return mainMenu;
	}

	public JMenu getViewMenu() {
		if (viewMenu == null) {
			viewMenu = new JMenu(Language.view);

			showKillPoints = new JCheckBoxMenuItem(Language.show_points);
			showKillPoints.setSelected(XWingModule.getInstance().getOptions()
					.isShowKillPoints());
			showKillPoints.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					XWingModule.getInstance().getOptions()
							.setShowKillPoints(showKillPoints.isSelected());
				}
			});

			onlyEnterPoints = new JCheckBoxMenuItem(Language.only_enter_points);
			onlyEnterPoints.setSelected(XWingModule.getInstance().getOptions()
					.isEnterOnlyPoints());
			onlyEnterPoints.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					XWingModule.getInstance().getOptions()
							.setEnterOnlyPoints(onlyEnterPoints.isSelected());
				}
			});

			viewMenu.add(showKillPoints);
			viewMenu.add(onlyEnterPoints);
		}

		return viewMenu;
	}

	public JMenu getTournamentMenu() {
		if (tournamentMenu == null) {
			tournamentMenu = new JMenu(Language.tournament);

			JMenuItem addPlayer = new JMenuItem(Language.add_player);
			addPlayer.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					List<Player> players = new ArrayList<Player>();
					players.addAll(CryodexController.getPlayers());

					for (Player p : CryodexController.getActiveTournament()
							.getPlayers()) {
						players.remove(p);
					}

					if (players.isEmpty()) {
						JOptionPane.showMessageDialog(Main.getInstance(),
								Language.all_players_added);
					} else {

						PlayerSelectionDialog<Player> d = new PlayerSelectionDialog<Player>(
								players) {

							@Override
							public void playerSelected(Player p) {
								CryodexController.getActiveTournament()
										.addPlayer(p);

								CryodexController.getActiveTournament()
										.resetRankingTable();
							}
						};

						d.setVisible(true);
					}
				}
			});

			JMenuItem dropPlayer = new JMenuItem(Language.drop_player);
			dropPlayer.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					JDialog d = new PlayerSelectionDialog<Player>(
							CryodexController.getActiveTournament()
									.getPlayers()) {

						@Override
						public void playerSelected(Player p) {
							CryodexController.getActiveTournament().dropPlayer(
									p);
						}
					};
					d.setVisible(true);

				}
			});

			JMenuItem generateNextRound = new JMenuItem(Language.generate_next_round);
			generateNextRound.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					CryodexController.getActiveTournament().generateNextRound();
				}
			});

			tournamentMenu.add(generateNextRound);
			tournamentMenu.add(addPlayer);
			tournamentMenu.add(dropPlayer);
			tournamentMenu.add(getCutPlayers());
		}

		return tournamentMenu;
	}

	public JMenu getRoundMenu() {
		if (roundMenu == null) {
			roundMenu = new JMenu(Language.round);

			JMenuItem generateNextRound = new JMenuItem(Language.regenerate_round);
			generateNextRound.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					XWingTournament tournament = (XWingTournament) CryodexController
							.getActiveTournament();

					int index = tournament.getTournamentGUI()
							.getRoundTabbedPane().getSelectedIndex();

					int result = JOptionPane.showConfirmDialog(
							Main.getInstance(),
							Language.round_regen_warning);

					if (result == JOptionPane.OK_OPTION) {
						XWingRound r = tournament.getRound(index);
						if (r.isSingleElimination()) {
							int playerCount = r.getMatches().size() * 2;
							tournament.cancelRound(tournament.getRoundNumber(r));
							tournament
									.generateSingleEliminationMatches(playerCount);
						} else {
							tournament.generateRound(index + 1);
						}

						tournament.getTournamentGUI().getRoundTabbedPane()
								.validate();
						tournament.getTournamentGUI().getRoundTabbedPane()
								.repaint();
					}

				}
			});

			JMenuItem cancelRound = new JMenuItem(Language.cancel_round);
			cancelRound.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					XWingTournament tournament = (XWingTournament) CryodexController
							.getActiveTournament();

					int index = tournament.getTournamentGUI()
							.getRoundTabbedPane().getSelectedIndex();

					if (index == 0) {
						CryodexController.deleteTournament(true);
						return;
					}

					int result = JOptionPane.showConfirmDialog(
							Main.getInstance(),
							Language.round_cancel_warning);

					if (result == JOptionPane.OK_OPTION) {
						tournament.cancelRound(index + 1);

						tournament.getTournamentGUI().getRoundTabbedPane()
								.setSelectedIndex(index - 1);

						CryodexController.saveData();

						resetMenuBar();
					}
				}
			});

			JMenuItem swapPlayers = new JMenuItem(Language.swap_players);
			swapPlayers.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					XWingTournament tournament = (XWingTournament) CryodexController
							.getActiveTournament();

					if (tournament.getSelectedRound().isComplete()) {
						JOptionPane.showMessageDialog(Main.getInstance(),
								Language.swap_complete_warning);
						return;
					}

					XWingSwapPanel.showSwapPanel();
				}
			});

			roundMenu.add(generateNextRound);
			roundMenu.add(cancelRound);
			roundMenu.add(swapPlayers);
		}
		return roundMenu;
	}

	public JMenuItem getCutPlayers() {
		if (cutPlayers == null) {
			cutPlayers = new JMenuItem(Language.cut_to_top_players);
			cutPlayers.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					XWingTournament tournament = (XWingTournament) CryodexController
							.getActiveTournament();

					if (tournament.getLatestRound().isComplete() == false) {
						JOptionPane.showMessageDialog(Main.getInstance(),
								Language.round_incomplete);
						return;
					}

					JDialog d = new CutPlayersDialog();
					d.setVisible(true);
				}
			});
		}
		return cutPlayers;
	}

	public JMenu getExportMenu() {
		if (exportMenu == null) {
			exportMenu = new JMenu(Language.export);

			JMenuItem exportPlayerList = new JMenuItem(Language.player_list);
			exportPlayerList.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					XWingExportController.playerList(CryodexController
							.getActiveTournament().getPlayers());
				}
			});

			JMenuItem exportMatches = new JMenuItem(Language.export_matches);
			exportMatches.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					XWingExportController.exportMatches();
				}
			});

			JMenuItem exportMatchSlips = new JMenuItem(Language.export_match_slips);
			exportMatchSlips.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					XWingTournament tournament = (XWingTournament) CryodexController
							.getActiveTournament();
					XWingRound round = tournament.getLatestRound();

					int roundNumber = round.isSingleElimination() ? 0
							: tournament.getRoundNumber(round);

					XWingExportController.exportTournamentSlips(tournament,
							round.getMatches(), roundNumber);
				}
			});

			JMenuItem exportMatchSlipsWithStats = new JMenuItem(
					Language.export_match_slips_with_stats);
			exportMatchSlipsWithStats.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					XWingTournament tournament = (XWingTournament) CryodexController
							.getActiveTournament();
					XWingRound round = tournament.getLatestRound();

					int roundNumber = round.isSingleElimination() ? 0
							: tournament.getRoundNumber(round);

					XWingExportController.exportTournamentSlipsWithStats(
							tournament, round.getMatches(), roundNumber);
				}
			});

			JMenuItem exportRankings = new JMenuItem(Language.export_rankings);
			exportRankings.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					XWingExportController
							.exportRankings((XWingTournament) CryodexController
									.getActiveTournament());
				}
			});

			JMenuItem exportTournamentReport = new JMenuItem(
					Language.export_tournament_report);
			exportTournamentReport.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					XWingExportController
							.exportTournamentReport((XWingTournament) CryodexController
									.getActiveTournament());
				}
			});

			exportMenu.add(exportPlayerList);
			exportMenu.add(exportMatches);
			exportMenu.add(exportMatchSlips);
			exportMenu.add(exportMatchSlipsWithStats);
			exportMenu.add(exportRankings);
			exportMenu.add(exportTournamentReport);
		}
		return exportMenu;
	}

	@Override
	public void resetMenuBar() {

		boolean isXWingTournament = CryodexController.getActiveTournament() != null
				&& CryodexController.getActiveTournament() instanceof XWingTournament;

		showKillPoints.setSelected(XWingModule.getInstance().getOptions()
				.isShowKillPoints());
		onlyEnterPoints.setSelected(XWingModule.getInstance().getOptions()
				.isEnterOnlyPoints());

		deleteTournament.setEnabled(isXWingTournament);
		getTournamentMenu().setEnabled(isXWingTournament);
		getRoundMenu().setEnabled(isXWingTournament);
		getExportMenu().setEnabled(isXWingTournament);

		if (isXWingTournament) {
			boolean isSingleElimination = ((XWingTournament) CryodexController
					.getActiveTournament()).getLatestRound()
					.isSingleElimination();
			getCutPlayers().setEnabled(!isSingleElimination);
		}
	}

	private abstract class PlayerSelectionDialog<K extends Comparable<K>>
			extends JDialog {

		private static final long serialVersionUID = 1945413167979638452L;

		private final JComboBox<K> userCombo;

		@SuppressWarnings("unchecked")
		public PlayerSelectionDialog(List<K> players) {
			super(Main.getInstance(), Language.select_player, true);

			Collections.sort(players);

			JPanel mainPanel = new JPanel(new BorderLayout());

			userCombo = new JComboBox<K>();
			for (K k : players) {
				userCombo.addItem(k);
			}
			ComponentUtils.forceSize(userCombo, 20, 25);

			JButton ok = new JButton(Language.ok);
			ok.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					K p = (K) userCombo.getSelectedItem();

					playerSelected(p);

					PlayerSelectionDialog.this.setVisible(false);
				}
			});

			JButton cancel = new JButton(Language.cancel);
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PlayerSelectionDialog.this.setVisible(false);
				}
			});

			mainPanel.add(userCombo, BorderLayout.CENTER);
			mainPanel.add(ComponentUtils.addToHorizontalBorderLayout(ok, null,
					cancel), BorderLayout.SOUTH);

			this.add(mainPanel);

			PlayerSelectionDialog.this
					.setLocationRelativeTo(Main.getInstance());
			PlayerSelectionDialog.this.pack();
			this.setMinimumSize(new Dimension(200, 0));
		}

		public abstract void playerSelected(K p);
	}

	private class CutPlayersDialog extends JDialog {

		private static final long serialVersionUID = 1945413167979638452L;

		private final JComboBox<Integer> cutCombo;

		public CutPlayersDialog() {
			super(Main.getInstance(), Language.cut_players, true);

			JPanel mainPanel = new JPanel(new BorderLayout());

			int currentPlayers = CryodexController.getActiveTournament()
					.getPlayers().size();

			Integer[] options = { 2, 4, 8, 16, 32, 64 };

			while (options[options.length - 1] > currentPlayers) {
				Integer[] tempOptions = new Integer[options.length - 1];
				for (int i = 0; i < tempOptions.length; i++) {
					tempOptions[i] = options[i];
				}
				options = tempOptions;
			}

			cutCombo = new JComboBox<Integer>(options);
			ComponentUtils.forceSize(cutCombo, 10, 25);

			JButton add = new JButton(Language.make_cut);
			add.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					Integer p = (Integer) cutCombo.getSelectedItem();

					CryodexController.getActiveTournament()
							.generateSingleEliminationMatches(p);

					CutPlayersDialog.this.setVisible(false);

					getCutPlayers().setEnabled(false);
				}
			});

			JButton cancel = new JButton(Language.cancel);
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					CutPlayersDialog.this.setVisible(false);
				}
			});

			mainPanel.add(ComponentUtils.addToHorizontalBorderLayout(
					new JLabel(Language.cut_to_top), cutCombo, null),
					BorderLayout.CENTER);
			mainPanel.add(ComponentUtils.addToHorizontalBorderLayout(add, null,
					cancel), BorderLayout.SOUTH);

			this.add(mainPanel);

			CutPlayersDialog.this.setLocationRelativeTo(Main.getInstance());
			CutPlayersDialog.this.pack();
			this.setMinimumSize(new Dimension(200, 0));
		}
	}
}
