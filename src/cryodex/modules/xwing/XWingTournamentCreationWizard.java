package cryodex.modules.xwing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import cryodex.CryodexController;
import cryodex.CryodexController.Modules;
import cryodex.Language;
import cryodex.Main;
import cryodex.Player;
import cryodex.modules.Tournament;
import cryodex.modules.xwing.XWingTournament.InitialSeedingEnum;
import cryodex.widget.ComponentUtils;
import cryodex.widget.DoubleList;
import cryodex.widget.SpringUtilities;

public class XWingTournamentCreationWizard extends JDialog {

	private static final long serialVersionUID = 1L;

	private final WizardOptions wizardOptions = new WizardOptions();

	private JPanel mainPanel = null;
	private JPanel contentPanel = null;
	private JPanel buttonPanel = null;

	private JButton previousButton = null;
	private JButton nextButton = null;
	private JButton finishButton = null;
	private JButton cancelButton = null;

	private final List<Page> pages = new ArrayList<Page>();

	public XWingTournamentCreationWizard() {
		super(Main.getInstance(), Language.tournament_wizard, true);

		this.add(getMainPanel());
		setCurrentPage(new MainPage());
		XWingTournamentCreationWizard.this.pack();
		this.setMinimumSize(new Dimension(450, 300));
	}

	private void setCurrentPage(Page page) {
		pages.add(page);

		getContentPanel().removeAll();
		getContentPanel().add(page.getPanel(), BorderLayout.CENTER);
		getContentPanel().validate();
		getContentPanel().repaint();
	}

	private Page getCurrentPage() {
		return pages.isEmpty() ? null : pages.get(pages.size() - 1);
	}

	private Page getPreviousPage() {
		return pages.size() > 1 ? pages.get(pages.size() - 2) : null;
	}

	private void goToPrevious() {

		if (getPreviousPage() != null) {
			pages.remove(pages.get(pages.size() - 1));

			getContentPanel().removeAll();
			getContentPanel().add(pages.get(pages.size() - 1).getPanel(),
					BorderLayout.CENTER);
			getContentPanel().validate();
			getContentPanel().repaint();
		} else {
			XWingTournamentCreationWizard.this.setVisible(false);
		}
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(getContentPanel(), BorderLayout.CENTER);
			mainPanel.add(getButtonPanel(), BorderLayout.SOUTH);
		}

		return mainPanel;
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel(new BorderLayout());
		}

		return contentPanel;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			buttonPanel.add(getPreviousButton());
			buttonPanel.add(getNextButton());
			buttonPanel.add(getFinishButton());
			buttonPanel.add(getCancelButton());
		}

		return buttonPanel;
	}

	private void setButtonVisibility(Boolean previous, Boolean next,
			Boolean finish) {
		getPreviousButton().setVisible(previous == null ? false : previous);
		getPreviousButton().setEnabled(previous != null);

		getNextButton().setVisible(next == null ? false : next);
		getNextButton().setEnabled(next != null);

		getFinishButton().setVisible(finish == null ? false : finish);
		getFinishButton().setEnabled(finish != null);
	}

	private JButton getPreviousButton() {
		if (previousButton == null) {
			previousButton = new JButton(Language.previous);
			previousButton.setEnabled(false);
			previousButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getCurrentPage().onPrevious();
				}
			});
		}

		return previousButton;
	}

	private JButton getNextButton() {
		if (nextButton == null) {
			nextButton = new JButton(Language.next);
			nextButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getCurrentPage().onNext();
				}
			});
		}

		return nextButton;
	}

	private JButton getFinishButton() {
		if (finishButton == null) {
			finishButton = new JButton(Language.finish);
			finishButton.setVisible(false);
			finishButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getCurrentPage().onFinish();
				}
			});
		}

		return finishButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(Language.cancel);
			cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					XWingTournamentCreationWizard.this.setVisible(false);
				}
			});
		}

		return cancelButton;
	}

	private interface Page {
		public JPanel getPanel();

		public void onNext();

		public void onPrevious();

		public void onFinish();
	}

	private class MainPage implements Page {

		JTextField nameTextField;

		JCheckBox mergeCB;

		JCheckBox splitCB;
		JTextField numSubs;

		JPanel pagePanel;

		@Override
		public JPanel getPanel() {

			setButtonVisibility(null, true, null);
			
			XWingTournamentCreationWizard.this.setMinimumSize(new Dimension(450, 300));

			if (pagePanel == null) {
				JPanel namePanel = new JPanel(new BorderLayout());

				JLabel nameHeader = new JLabel(
						"<HTML><H1>" + Language.name_event + "</H1></HTML>");

				nameTextField = new JTextField(10);

				namePanel.add(ComponentUtils.addToFlowLayout(nameHeader,
						FlowLayout.LEFT), BorderLayout.NORTH);
				namePanel.add(ComponentUtils.addToFlowLayout(nameTextField,
						FlowLayout.LEFT));

				JPanel creationOptionsPanel = new JPanel(new BorderLayout());

				JLabel additionalOptionsHeader = new JLabel(
						"<HTML><H1>" + Language.additional_options + "</H1></HTML>");

				creationOptionsPanel.add(ComponentUtils.addToFlowLayout(
						additionalOptionsHeader, FlowLayout.LEFT),
						BorderLayout.NORTH);

				JPanel splitEntryPanel = new JPanel(new BorderLayout());
				ComponentUtils.forceSize(splitEntryPanel, 210, 60);

				splitCB = new JCheckBox(Language.split_into_subtournaments);
				final JLabel splitLabel = new JLabel(
						Language.number_of_sub_tournaments + ":");
				numSubs = new JTextField(3);

				splitLabel.setVisible(false);
				numSubs.setVisible(false);

				splitCB.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						splitLabel.setVisible(splitCB.isSelected());
						numSubs.setVisible(splitCB.isSelected());
					}
				});

				splitEntryPanel.add(splitCB, BorderLayout.NORTH);
				splitEntryPanel.add(ComponentUtils.addToFlowLayout(
						ComponentUtils.addToHorizontalBorderLayout(splitLabel,
								numSubs, null), FlowLayout.LEFT),
						BorderLayout.CENTER);

				creationOptionsPanel.add(ComponentUtils.addToFlowLayout(
						splitEntryPanel, FlowLayout.LEFT), BorderLayout.SOUTH);

				mergeCB = new JCheckBox(Language.merge_multiple_tournaments_into_one);

				creationOptionsPanel.add(ComponentUtils.addToFlowLayout(
						mergeCB, FlowLayout.LEFT), BorderLayout.CENTER);

				pagePanel = ComponentUtils.addToFlowLayout(ComponentUtils
						.addToVerticalBorderLayout(namePanel,
								creationOptionsPanel, null), FlowLayout.CENTER);
			}
			return pagePanel;
		}

		@Override
		public void onNext() {

			wizardOptions.setName(nameTextField.getText());

			if (splitCB.isSelected()) {
				int subs = Integer.parseInt(numSubs.getText());
				wizardOptions.setSplit(subs);
			}

			if (mergeCB.isSelected()) {
				setCurrentPage(new MergeTournamentSelectionPage());
			} else {
				setCurrentPage(new PlayerSelectionPage());
			}
		}

		@Override
		public void onPrevious() {
			// Do nothing
		}

		@Override
		public void onFinish() {
			// Do nothing
		}
	}

	private class PlayerSelectionPage implements Page {

		private DoubleList<Player> playerList;
		private JCheckBox removeCurrentlyPlaying;

		private JPanel pagePanel;

		@Override
		public JPanel getPanel() {

			setButtonVisibility(true, true, null);
			
			XWingTournamentCreationWizard.this.setMinimumSize(new Dimension(450, 500));

			if (pagePanel == null) {

				pagePanel = new JPanel(new BorderLayout());

				JLabel header = new JLabel(
						"<HTML><H1>" + Language.select_players + "</H1></HTML>");

				pagePanel.add(ComponentUtils.addToFlowLayout(header,
						FlowLayout.CENTER), BorderLayout.NORTH);

				playerList = new DoubleList<Player>(
						CryodexController.getPlayers(), null,
						Language.available_players, Language.event_players);

				pagePanel.add(playerList, BorderLayout.CENTER);

				removeCurrentlyPlaying = new JCheckBox(
						Language.remove_players_currently_in_an_event);
				removeCurrentlyPlaying.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (removeCurrentlyPlaying.isSelected()) {
							List<Tournament> tournaments = CryodexController
									.getAllTournaments();
							List<Player> players1 = playerList.getList1Values();
							List<Player> players2 = playerList.getList2Values();
							for (Tournament t : tournaments) {
								players1.removeAll(t.getPlayers());
								players2.removeAll(t.getPlayers());
							}
							playerList.setValues(players1, players2);
						} else {
							List<Tournament> tournaments = CryodexController
									.getAllTournaments();
							List<Player> players = new ArrayList<Player>();

							for (Tournament t : tournaments) {
								for (Player p : t.getPlayers()) {
									if (players.contains(p) == false) {
										players.add(p);
									}
								}
							}

							List<Player> players1 = playerList.getList1Values();
							players1.addAll(players);
							List<Player> players2 = playerList.getList2Values();
							playerList.setValues(players1, players2);
						}
					}
				});

				pagePanel.add(ComponentUtils.addToFlowLayout(
						removeCurrentlyPlaying, FlowLayout.CENTER),
						BorderLayout.SOUTH);

			}

			return pagePanel;
		}

		@Override
		public void onNext() {
			List<XWingPlayer> xwingPlayerList = new ArrayList<>();
			for (Player p : playerList.getList2Values()) {
				xwingPlayerList.add((XWingPlayer) p
						.getModuleInfoByModule(Modules.XWING.getModule()));
			}
			wizardOptions.setPlayerList(xwingPlayerList);
			setCurrentPage(new AdditionalOptionsPage());
		}

		@Override
		public void onPrevious() {
			goToPrevious();
		}

		@Override
		public void onFinish() {
			// Do Nothing
		}

	}

	private class AdditionalOptionsPage implements Page {

		private JRadioButton randomRB;
		private JRadioButton byGroupRB;
		private JRadioButton byRankingRB;
		private JCheckBox singleElimination;

		private JTextField customPointsTF;
		private JRadioButton standardRB;
		private JRadioButton escalationRB;
		private JRadioButton epicRB;
		private JRadioButton customRB;

		private JRadioButton splitRandomRB;
		private JRadioButton splitByGroupRB;
		private JRadioButton splitByRanking;

		private JPanel pagePanel;

		@Override
		public JPanel getPanel() {

			setButtonVisibility(true, null, true);
			
			XWingTournamentCreationWizard.this.setMinimumSize(new Dimension(450, 500));

			if (pagePanel == null) {

				JPanel initialPairingPanel = new JPanel(new BorderLayout());

				JLabel header = new JLabel(
						"<HTML><H3>" + Language.first_round_pairing + "</H3></HTML>");

				initialPairingPanel
						.add(ComponentUtils.addToFlowLayout(header,
								FlowLayout.LEFT), BorderLayout.NORTH);

				JPanel tournamentTypesPanel = new JPanel(new SpringLayout());

				ButtonGroup bg = new ButtonGroup();

				randomRB = new JRadioButton(Language.random);
				byGroupRB = new JRadioButton(Language.seperate_by_group_name);
				byRankingRB = new JRadioButton(Language.by_ranking);
				singleElimination = new JCheckBox(
						"<HTML>" + Language.start_as_single_elimination + "</HTML>");

				bg.add(randomRB);
				bg.add(byGroupRB);
				bg.add(byRankingRB);

				randomRB.setSelected(true);

				tournamentTypesPanel.add(randomRB);
				tournamentTypesPanel.add(byGroupRB);
				if (wizardOptions.getSelectedTournaments() != null
						&& wizardOptions.getSelectedTournaments().isEmpty() == false) {
					tournamentTypesPanel.add(byRankingRB);
				}
				tournamentTypesPanel.add(singleElimination);

				SpringUtilities
						.makeCompactGrid(tournamentTypesPanel,
								tournamentTypesPanel.getComponentCount(), 1, 0,
								0, 0, 0);

				initialPairingPanel.add(ComponentUtils.addToFlowLayout(
						tournamentTypesPanel, FlowLayout.LEFT),
						BorderLayout.CENTER);

				JPanel centerPanel = new JPanel(new BorderLayout());

				JLabel pointHeader = new JLabel(
						"<HTML><H3>" + Language.choose_point_type + "</H3></HTML>");

				centerPanel.add(ComponentUtils.addToFlowLayout(pointHeader,
						FlowLayout.LEFT), BorderLayout.NORTH);

				JPanel pointsPanel = new JPanel(new SpringLayout());

				ButtonGroup pointsBG = new ButtonGroup();

				standardRB = new JRadioButton(Language.standard_points);
				escalationRB = new JRadioButton(
						Language.escalation_points);
				epicRB = new JRadioButton(Language.epic_points);
				customRB = new JRadioButton(
						Language.custom_points);

				final JLabel customPointsInfo = new JLabel(
						Language.comma_separated);

				ActionListener customListener = new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (customRB.isSelected()) {
							customPointsTF.setEnabled(true);
						} else {
							customPointsTF.setEnabled(false);
						}
					}
				};

				standardRB.addActionListener(customListener);
				escalationRB.addActionListener(customListener);
				epicRB.addActionListener(customListener);
				customRB.addActionListener(customListener);

				customPointsTF = new JTextField();
				customPointsTF.setColumns(12);
				customPointsTF.setEnabled(false);

				pointsBG.add(standardRB);
				pointsBG.add(escalationRB);
				pointsBG.add(epicRB);
				pointsBG.add(customRB);

				standardRB.setSelected(true);

				pointsPanel.add(standardRB);
				pointsPanel.add(escalationRB);
				pointsPanel.add(epicRB);
				pointsPanel.add(customRB);
				pointsPanel.add(customPointsInfo);

				pointsPanel.add(ComponentUtils.addToHorizontalBorderLayout(
						null, ComponentUtils.addToFlowLayout(customPointsTF,
								FlowLayout.LEFT), new JPanel()));

				SpringUtilities.makeCompactGrid(pointsPanel, 6, 1, 0, 0, 0, 0);

				centerPanel.add(ComponentUtils.addToFlowLayout(pointsPanel,
						FlowLayout.LEFT), BorderLayout.CENTER);

				JPanel splitOptionsPanel = new JPanel(new BorderLayout());

				JLabel splitOptionsHeader = new JLabel(
						"<HTML><H3>" + Language.how_to_split_tournament + "</H3></HTML>");

				splitOptionsPanel.add(ComponentUtils.addToFlowLayout(
						splitOptionsHeader, FlowLayout.LEFT),
						BorderLayout.NORTH);

				JPanel splitOptionsSubPanel = new JPanel(new SpringLayout());

				ButtonGroup splitOptionsBG = new ButtonGroup();

				splitRandomRB = new JRadioButton(Language.random);
				splitByGroupRB = new JRadioButton(Language.separate_by_group_name);
				splitByRanking = new JRadioButton(Language.split_by_ranking);

				splitOptionsBG.add(splitRandomRB);
				splitOptionsBG.add(splitByGroupRB);
				splitOptionsBG.add(splitByRanking);

				splitOptionsSubPanel.add(splitRandomRB);
				splitOptionsSubPanel.add(splitByGroupRB);
				if (wizardOptions.getSelectedTournaments() != null
						&& wizardOptions.getSelectedTournaments().isEmpty() == false) {
					splitOptionsSubPanel.add(splitByRanking);
					XWingTournamentCreationWizard.this.setMinimumSize(new Dimension(450, 550));
				}

				SpringUtilities
						.makeCompactGrid(splitOptionsSubPanel,
								splitOptionsSubPanel.getComponentCount(), 1, 0,
								0, 0, 0);

				splitOptionsPanel.add(ComponentUtils.addToFlowLayout(
						splitOptionsSubPanel, FlowLayout.LEFT),
						BorderLayout.CENTER);

				pagePanel = new JPanel(new FlowLayout());

				pagePanel
						.add(ComponentUtils.addToVerticalBorderLayout(
								initialPairingPanel, centerPanel, wizardOptions
										.getSplit() > 1 ? splitOptionsPanel
										: null));
			}

			return pagePanel;
		}

		@Override
		public void onNext() {
			// Do nothing
		}

		@Override
		public void onPrevious() {
			goToPrevious();
		}

		@Override
		public void onFinish() {

			if (standardRB.isSelected()) {
				wizardOptions.setPoints(100);
			} else if (escalationRB.isSelected()) {
				List<Integer> points = new ArrayList<Integer>();
				points.add(60);
				points.add(90);
				points.add(120);
				points.add(150);
				wizardOptions.setEscalationPoints(points);
			} else if (epicRB.isSelected()) {
				wizardOptions.setPoints(300);
			} else if (customRB.isSelected()) {
				try {
					Integer points = Integer.parseInt(customPointsTF.getText());
					wizardOptions.setPoints(points);
				} catch (Exception e) {
					String[] rounds = customPointsTF.getText().split(",");
					List<Integer> points = new ArrayList<Integer>();
					for (String s : rounds) {
						points.add(Integer.parseInt(s.trim()));
					}
					wizardOptions.setEscalationPoints(points);
				}

			}

			if (randomRB.isSelected()) {
				wizardOptions.setInitialSeedingEnum(InitialSeedingEnum.RANDOM);
			} else if (byGroupRB.isSelected()) {
				wizardOptions
						.setInitialSeedingEnum(InitialSeedingEnum.BY_GROUP);
			} else if (byRankingRB.isSelected()) {
				wizardOptions
						.setInitialSeedingEnum(InitialSeedingEnum.IN_ORDER);
			}

			if (singleElimination.isSelected()) {
				wizardOptions.setSingleElimination(true);
			}

			boolean fixByes = true;

			if (wizardOptions.getSplit() > 1) {
				Integer splitNum = wizardOptions.getSplit();

				List<WizardOptions> wizardOptionsList = new ArrayList<WizardOptions>();

				for (int i = 1; i <= splitNum; i++) {
					WizardOptions newWizardOption = new WizardOptions(
							wizardOptions);

					wizardOptionsList.add(newWizardOption);

					newWizardOption.setName(wizardOptions.getName() + " " + i);
					newWizardOption.setPlayerList(new ArrayList<XWingPlayer>());
				}

				if (splitByGroupRB.isSelected()) {
					Map<String, List<XWingPlayer>> playerMap = new HashMap<String, List<XWingPlayer>>();

					// Add players to map
					for (XWingPlayer p : wizardOptions.getPlayerList()) {
						List<XWingPlayer> playerList = playerMap.get(p
								.getPlayer().getGroupName());

						if (playerList == null) {
							playerList = new ArrayList<>();
							String groupName = p.getPlayer().getGroupName() == null ? ""
									: p.getPlayer().getGroupName();
							playerMap.put(groupName, playerList);
						}

						playerList.add(p);
					}

					int j = 0;
					for (String groupValue : playerMap.keySet()) {

						List<XWingPlayer> playerList = playerMap
								.get(groupValue);

						while (playerList.isEmpty() == false) {

							wizardOptionsList.get(j).getPlayerList()
									.add(playerList.get(0));
							j = j == splitNum - 1 ? 0 : j + 1;
							playerList.remove(0);
						}
					}

					//
					int first = 0;
					int last = wizardOptionsList.size() - 1;
					if (fixByes) {
						while (first < last) {

							if (wizardOptionsList.get(last).getPlayerList()
									.size() % 2 == 0) {
								last--;
							} else {
								if (wizardOptionsList.get(first)
										.getPlayerList().size() % 2 == 1
										&& wizardOptionsList.get(last)
												.getPlayerList().size() % 2 == 1) {
									XWingPlayer p = wizardOptionsList
											.get(first)
											.getPlayerList()
											.get(wizardOptionsList.get(first)
													.getPlayerList().size() - 1);

									wizardOptionsList.get(first)
											.getPlayerList().remove(p);

									wizardOptionsList.get(last).getPlayerList()
											.add(p);
								}
								first++;
							}
						}
					}
				} else if (splitByRanking.isSelected()) {
					List<XWingPlayer> tempPlayers = rankMergedPlayers(wizardOptions
							.getPlayerList());

					int ppt = tempPlayers.size() / splitNum;
					int overage = tempPlayers.size() % splitNum;
					for (int j = 0; j < splitNum; j++) {
						int count = j >= splitNum - overage ? ppt + 1 : ppt;
						wizardOptionsList.get(j).getPlayerList()
								.addAll(tempPlayers.subList(0, count));
						tempPlayers = tempPlayers.subList(count,
								tempPlayers.size());
					}

					for (int i = 0; i < wizardOptionsList.size(); i++) {
						// if (wizardOptionsList.get(i).getPlayerList().size() %
						// 2 == 0) {
						// continue;
						// }

						while (i + 1 < wizardOptionsList.size()
								&& (wizardOptionsList.get(i).getPlayerList()
										.size() % 2 == 1 || wizardOptionsList
										.get(i).getPlayerList().size() > wizardOptionsList
										.get(i + 1).getPlayerList().size())) {
							XWingPlayer t1 = wizardOptionsList
									.get(i)
									.getPlayerList()
									.get(wizardOptionsList.get(i)
											.getPlayerList().size() - 1);
							wizardOptionsList.get(i).getPlayerList().remove(t1);
							List<XWingPlayer> temp = new ArrayList<XWingPlayer>();
							temp.addAll(wizardOptionsList.get(i + 1)
									.getPlayerList());
							wizardOptionsList.get(i + 1).getPlayerList()
									.clear();
							wizardOptionsList.get(i + 1).getPlayerList()
									.add(t1);
							wizardOptionsList.get(i + 1).getPlayerList()
									.addAll(temp);
						}

					}
				} else {
					List<XWingPlayer> playerList = wizardOptions
							.getPlayerList();
					Collections.shuffle(playerList);
					int j = 0;
					while (playerList.isEmpty() == false) {

						wizardOptionsList.get(j).getPlayerList()
								.add(playerList.get(0));
						j = j == splitNum - 1 ? 0 : j + 1;
						playerList.remove(0);
					}

					//
					int first = 0;
					int last = wizardOptionsList.size() - 1;
					if (fixByes) {
						while (first < last) {

							if (wizardOptionsList.get(last).getPlayerList()
									.size() % 2 == 0) {
								last--;
							} else {
								if (wizardOptionsList.get(first)
										.getPlayerList().size() % 2 == 1
										&& wizardOptionsList.get(last)
												.getPlayerList().size() % 2 == 1) {
									XWingPlayer p = wizardOptionsList
											.get(first)
											.getPlayerList()
											.get(wizardOptionsList.get(first)
													.getPlayerList().size() - 1);

									wizardOptionsList.get(first)
											.getPlayerList().remove(p);

									wizardOptionsList.get(last).getPlayerList()
											.add(p);
								}
								first++;
							}
						}
					}
				}

				XWingTournamentCreationWizard.this.setVisible(false);

				for (WizardOptions wo : wizardOptionsList) {
					if (wo.getInitialSeedingEnum() == InitialSeedingEnum.IN_ORDER) {
						List<XWingPlayer> tempList = rankMergedPlayers(wo
								.getPlayerList());
						wo.setPlayerList(tempList);
					}
					XWingModule.makeTournament(wo);
				}
			} else {
				XWingTournamentCreationWizard.this.setVisible(false);
				XWingModule.makeTournament(wizardOptions);
			}
		}
	}

	private class MergeTournamentSelectionPage implements Page {

		private JPanel pagePanel = null;
		private final Map<XWingTournament, JCheckBox> checkBoxMap = new HashMap<XWingTournament, JCheckBox>();
		private JRadioButton all;
		private JRadioButton manual;
		private JTextField manualInput;

		@Override
		public JPanel getPanel() {
			
			setButtonVisibility(true, true, false);

			XWingTournamentCreationWizard.this.setMinimumSize(new Dimension(450, 500));
			
			if (pagePanel == null) {
				pagePanel = new JPanel(new BorderLayout());

				JLabel header = new JLabel(
						"<HTML><H3>" + Language.select_tournaments + "</H3></HTML>");

				JPanel listPanel = new JPanel(new SpringLayout());

				for (Tournament t : CryodexController.getAllTournaments()) {
					JCheckBox cb = new JCheckBox(t.getName());
					if (t instanceof XWingTournament) {
						checkBoxMap.put((XWingTournament) t, cb);
					}

					listPanel.add(cb);
				}

				SpringUtilities.makeCompactGrid(listPanel,
						listPanel.getComponentCount(), 1, 0, 0, 0, 0);

				JLabel playersFromLabel = new JLabel(
						"<HTML><H3>" + Language.how_many_players_from_each_event + "</H3></HTML>");
				ButtonGroup pf = new ButtonGroup();

				all = new JRadioButton(Language.all_players);
				manual = new JRadioButton(Language.let_me_pick + ":");
				manualInput = new JTextField(3);
				manualInput.setEnabled(false);

				ActionListener playersFromListener = new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						manualInput.setEnabled(manual.isSelected());
					}
				};

				all.addActionListener(playersFromListener);
				manual.addActionListener(playersFromListener);

				pf.add(all);
				pf.add(manual);
				all.setSelected(true);

				JPanel manualPanel = ComponentUtils
						.addToHorizontalBorderLayout(manual, ComponentUtils
								.addToFlowLayout(manualInput, FlowLayout.LEFT),
								null);

				JPanel howManyPlayersPanel = ComponentUtils
						.addToVerticalBorderLayout(playersFromLabel, all,
								manualPanel);

				pagePanel
						.add(ComponentUtils.addToFlowLayout(header,
								FlowLayout.LEFT), BorderLayout.NORTH);
				pagePanel.add(ComponentUtils.addToFlowLayout(listPanel,
						FlowLayout.LEFT), BorderLayout.CENTER);
				pagePanel.add(ComponentUtils.addToFlowLayout(
						howManyPlayersPanel, FlowLayout.CENTER),
						BorderLayout.SOUTH);
			}
			return ComponentUtils.addToFlowLayout(pagePanel, FlowLayout.CENTER);
		}

		@Override
		public void onNext() {
			wizardOptions.setMerge(true);
			List<XWingTournament> tournamentList = new ArrayList<XWingTournament>();
			Set<XWingPlayer> playerList = new TreeSet<XWingPlayer>();
			Integer playerCount = null;

			if (manual.isSelected()) {
				playerCount = Integer.parseInt(manualInput.getText());
			}

			for (XWingTournament t : checkBoxMap.keySet()) {
				if (checkBoxMap.get(t).isSelected()) {

					tournamentList.add(t);
					List<XWingPlayer> thisTournamentPlayers = new ArrayList<XWingPlayer>();
					thisTournamentPlayers.addAll(t.getXWingPlayers());

					if (playerCount == null
							|| thisTournamentPlayers.size() <= playerCount) {
						playerList.addAll(thisTournamentPlayers);
					} else {
						Collections.sort(thisTournamentPlayers,
								new XWingComparator(t,
										XWingComparator.rankingCompare));
						playerList.addAll(thisTournamentPlayers.subList(0,
								playerCount));
					}
				}
			}

			Integer points = null;
			for (XWingTournament t : tournamentList) {
				if (points == null) {
					points = t.getPoints();
				} else if (points.equals(t.getPoints()) == false) {
					tournamentList = null;
					break;
				}
			}

			List<XWingPlayer> addingList = new ArrayList<XWingPlayer>();
			addingList.addAll(playerList);
			wizardOptions.setPlayerList(addingList);
			wizardOptions.setSelectedTournaments(tournamentList);

			setCurrentPage(new AdditionalOptionsPage());

		}

		@Override
		public void onPrevious() {
			goToPrevious();
		}

		@Override
		public void onFinish() {
			// Do nothing
		}
	}

	public static class WizardOptions {

		private String name;
		private InitialSeedingEnum initialSeedingEnum;
		private List<XWingPlayer> playerList;
		private Integer points;
		private List<Integer> escalationPoints;
		private int split = 1;
		private boolean isMerge = false;
		private List<XWingTournament> selectedTournaments;
		private boolean isSingleElimination = false;

		public WizardOptions() {

		}

		public WizardOptions(WizardOptions wizardOptions) {
			this.name = wizardOptions.getName();
			this.initialSeedingEnum = wizardOptions.getInitialSeedingEnum();
			this.points = wizardOptions.getPoints();
			this.escalationPoints = wizardOptions.getEscalationPoints();
			this.isSingleElimination = wizardOptions.isSingleElimination();
		}

		public InitialSeedingEnum getInitialSeedingEnum() {
			return initialSeedingEnum;
		}

		public void setInitialSeedingEnum(InitialSeedingEnum initialSeedingEnum) {
			this.initialSeedingEnum = initialSeedingEnum;
		}

		public List<XWingPlayer> getPlayerList() {
			return playerList;
		}

		public void setPlayerList(List<XWingPlayer> playerList) {
			this.playerList = playerList;
		}

		public Integer getPoints() {
			return points;
		}

		public void setPoints(Integer points) {
			escalationPoints = null;
			this.points = points;
		}

		public List<Integer> getEscalationPoints() {
			return escalationPoints;
		}

		public void setEscalationPoints(List<Integer> escalationPoints) {
			this.points = null;
			this.escalationPoints = escalationPoints;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getSplit() {
			return split;
		}

		public void setSplit(int split) {
			this.split = split;
		}

		public boolean isMerge() {
			return isMerge;
		}

		public void setMerge(boolean isMerge) {
			this.isMerge = isMerge;
		}

		public List<XWingTournament> getSelectedTournaments() {
			return selectedTournaments;
		}

		public void setSelectedTournaments(
				List<XWingTournament> selectedTournaments) {
			this.selectedTournaments = selectedTournaments;
		}

		public boolean isSingleElimination() {
			return isSingleElimination;
		}

		public void setSingleElimination(boolean isSingleElimination) {
			this.isSingleElimination = isSingleElimination;
		}
	}

	public List<XWingPlayer> rankMergedPlayers(List<XWingPlayer> playerList) {
		XWingTournament mergeTournament = new XWingTournament("",
				wizardOptions.getPlayerList(), null, wizardOptions
						.getSelectedTournaments().get(0).getPoints(),
				wizardOptions.getSelectedTournaments().get(0)
						.getEscalationPoints(), false);
		for (XWingTournament t : wizardOptions.getSelectedTournaments()) {
			mergeTournament.getAllRounds().addAll(t.getAllRounds());
		}

		List<XWingPlayer> tempPlayers = new ArrayList<XWingPlayer>();
		tempPlayers.addAll(playerList);
		Collections.sort(tempPlayers, new XWingComparator(mergeTournament,
				XWingComparator.rankingCompare));
		return tempPlayers;
	}
}
