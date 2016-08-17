package cryodex.widget;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cryodex.CryodexController;
import cryodex.Language;
import cryodex.Player;
import cryodex.modules.Module;

/**
 * Panel for the creation of players. Also contains the donation link.
 * 
 * @author cbrown
 * 
 */
public class RegisterPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton saveButton;
	private JButton deleteButton;
	private JButton cancelButton;
	private JList<Player> playerList;
	private JTextField nameField;
	private DefaultListModel<Player> userModel;
	private JLabel counterLabel;
	private JLabel donationLabel;

	private JTextField groupField;
	private JTextField emailField;
	private JPanel playerInfoPanel;
	private JPanel playerPanel;

	public RegisterPanel() {
		super(new BorderLayout());

		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Panel set up to push everything to the top
		this.add(getPlayerPanel(), BorderLayout.NORTH);
		this.add(new JPanel(), BorderLayout.CENTER);
	}

	/**
	 * Main panel layout
	 * 
	 * @return
	 */
	private JPanel getPlayerPanel() {
		if (playerPanel == null) {
			playerPanel = new JPanel(new BorderLayout());

			JLabel playersTitle = new JLabel("<html><b>"+Language.player_info+"</b></html>");
			playersTitle.setFont(new Font(playersTitle.getFont().getName(),
					playersTitle.getFont().getStyle(), 20));

			JScrollPane listScroller = new JScrollPane(getPlayerList());
			listScroller.setPreferredSize(new Dimension(150, 250));

			JPanel labelPanel = ComponentUtils.addToVerticalBorderLayout(
					getCounterLabel(), getDonationLabel(), null);

			JPanel southPanel = ComponentUtils.addToVerticalBorderLayout(null,
					listScroller, labelPanel);

			playerPanel.add(playersTitle, BorderLayout.NORTH);
			playerPanel.add(getPlayerInfoPanel(), BorderLayout.CENTER);
			playerPanel.add(southPanel, BorderLayout.SOUTH);
		}

		return playerPanel;
	}

	/**
	 * Information input
	 * 
	 * @return
	 */
	private JPanel getPlayerInfoPanel() {
		if (playerInfoPanel == null) {
			playerInfoPanel = new JPanel(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			playerInfoPanel.add(new JLabel(Language.name), gbc);

			gbc.gridy++;
			gbc.anchor = GridBagConstraints.EAST;
			playerInfoPanel.add(getNameField(), gbc);

			gbc.gridy++;
			playerInfoPanel.add(new JLabel(Language.group_name), gbc);

			gbc.gridy++;
			playerInfoPanel.add(getGroupNameField(), gbc);

			gbc.gridy++;
			playerInfoPanel.add(new JLabel(Language.email_address), gbc);

			gbc.gridy++;
			playerInfoPanel.add(getEmailField(), gbc);

//			for (Module m : CryodexController.getModules()) {
//				gbc.gridy++;
//				playerInfoPanel.add(m.getRegistration().getPanel(), gbc);
//			}

			gbc.gridy++;
			playerInfoPanel
					.add(ComponentUtils.addToHorizontalBorderLayout(
							getSaveButton(), getCancelButton(),
							getDeleteButton()), gbc);
		}
		return playerInfoPanel;
	}

	private JTextField getNameField() {
		if (nameField == null) {
			nameField = new JTextField();
		}
		return nameField;
	}

	private JTextField getGroupNameField() {
		if (groupField == null) {
			groupField = new JTextField();
		}
		return groupField;
	}

	private JTextField getEmailField() {
		if (emailField == null) {
			emailField = new JTextField();
		}
		return emailField;
	}

	private JButton getSaveButton() {

		if (saveButton == null) {

			saveButton = new JButton(Language.save);

			saveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// /////////////////////////////
					// Update general information
					// /////////////////////////////
					String name = getNameField().getText();
					String groupName = getGroupNameField().getText();
					String email = getEmailField().getText();

					if (name == null || name.isEmpty()) {
						JOptionPane.showMessageDialog((Component) null,
								Language.name_required, Language.error,
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Create new player or get previous player instance
					Player player;
					if (getPlayerList().getSelectedIndex() == -1) {
						for (Player p : CryodexController.getPlayers()) {
							if (p.getName().equals(name)) {
								JOptionPane.showMessageDialog((Component) null,
										Language.name_already_exist,
										Language.error, JOptionPane.ERROR_MESSAGE);
								return;
							}
						}

						player = new Player();
						CryodexController.getPlayers().add(player);
						getUserModel().addElement(player);

					} else {
						player = getPlayerList().getSelectedValue();
					}

					player.setName(name);
					if (groupName != null) {
						player.setGroupName(groupName);
					}
					player.setEmail(email);

//					for (Module m : CryodexController.getModules()) {
//						m.getRegistration().save(player);
//					}

					clearFields();
					CryodexController.saveData();

					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {
							getNameField().grabFocus();
							getNameField().requestFocus();
						}
					});

					updateCounterLabel();
				}
			});
		}
		return saveButton;
	}

	private JButton getDeleteButton() {

		if (deleteButton == null) {
			deleteButton = new JButton(Language.delete);
			deleteButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					CryodexController.getPlayers().remove(
							getPlayerList().getSelectedValue());

					getUserModel().removeElement(
							getPlayerList().getSelectedValue());

					clearFields();
					CryodexController.saveData();

					updateCounterLabel();
				}

			});
			deleteButton.setEnabled(false);
		}

		return deleteButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(Language.cancel);
			cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					clearFields();

					updateCounterLabel();
				}
			});
		}
		return cancelButton;
	}

	private void clearFields() {
		getNameField().setText("");
		getPlayerList().clearSelection();
		getDeleteButton().setEnabled(false);
		getNameField().setRequestFocusEnabled(true);
		getGroupNameField().setText("");
		getEmailField().setText("");

//		for (Module m : CryodexController.getModules()) {
//			m.getRegistration().clearFields();
//		}
	}

	public void addPlayers(List<Player> players) {
		for (Player p : players) {
			getUserModel().addElement(p);
		}
	}

	private DefaultListModel<Player> getUserModel() {
		if (userModel == null) {
			userModel = new DefaultListModel<Player>();
			userModel.addListDataListener(new ListDataListener() {

				@Override
				public void intervalRemoved(ListDataEvent e) {
					updateCounterLabel();
				}

				@Override
				public void intervalAdded(ListDataEvent e) {
					updateCounterLabel();
				}

				@Override
				public void contentsChanged(ListDataEvent e) {
					updateCounterLabel();
				}
			});
		}

		return userModel;
	}

	private JList<Player> getPlayerList() {
		if (playerList == null) {
			playerList = new JList<Player>(getUserModel());
			playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			playerList.setLayoutOrientation(JList.VERTICAL);
			playerList.setVisibleRowCount(-1);
			playerList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent arg0) {
					Player player = playerList.getSelectedValue();

					if (player == null) {
						getDeleteButton().setEnabled(false);
					} else {
						getDeleteButton().setEnabled(true);
						getNameField().setText(player.getName());
						getGroupNameField().setText(player.getGroupName());
						getEmailField().setText(player.getEmail());

//						for (Module m : CryodexController.getModules()) {
//							m.getRegistration().load(player);
//						}
					}

					updateCounterLabel();
				}
			});
		}

		return playerList;
	}

	public void updateCounterLabel() {
		getCounterLabel().setText(Language.player_count + getUserModel().getSize());
	}

	public JLabel getCounterLabel() {
		if (counterLabel == null) {
			counterLabel = new JLabel();
		}

		return counterLabel;
	}

	public JLabel getDonationLabel() {
		if (donationLabel == null) {
			donationLabel = new JLabel(
					"<html><a href=\"\">"+Language.donate+"</a></html>");
			donationLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			donationLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					CryodexController.sendDonation();
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
				}

			});
		}
		return donationLabel;
	}

	public void registerButton() {
		this.getRootPane().setDefaultButton(getSaveButton());
	}
	
	public void importPlayers(List<Player> players){
		for(Player p : players){
			CryodexController.getPlayers().add(p);
			getUserModel().addElement(p);
		}
		CryodexController.saveData();
	}

}
