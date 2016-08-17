package cryodex;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import cryodex.CryodexController.Modules;
import cryodex.modules.Module;
import cryodex.widget.AboutPanel;

public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	private JMenu fileMenu;
	private JMenu viewMenu;
	private JMenu helpMenu;

	private JCheckBoxMenuItem showTableNumbers;
	private JCheckBoxMenuItem showQuickFind;

	private static MenuBar instance;

	public static MenuBar getInstance() {
		if (instance == null) {
			instance = new MenuBar();
			instance.resetMenuBar();
		}
		return instance;
	}

	private MenuBar() {

		this.add(getFileMenu());
		this.add(getViewMenu());

		for (final Module m : CryodexController.getModules()) {
			this.add(m.getMenu().getMenu());
		}

		this.add(getHelpMenu());
	}

	public JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu(Language.file);
			fileMenu.setMnemonic('F');

			JMenuItem saveAs = new JMenuItem(Language.save_as);
			saveAs.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					CryodexController.saveAs();
				}
			});
			
			JMenuItem load = new JMenuItem(Language.load);
			load.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					CryodexController.load();
				}
			});
			
			JMenuItem importPlayers = new JMenuItem(Language.import_players);
			importPlayers.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PlayerImport.importPlayers();
				}
			});

			JMenuItem exit = new JMenuItem(Language.exit);
			exit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Main.getInstance().dispose();
				}
			});

			fileMenu.add(saveAs);
			fileMenu.add(load);
			fileMenu.add(importPlayers);
			fileMenu.add(exit);
		}

		return fileMenu;
	}

	public JMenu getViewMenu() {
		if (viewMenu == null) {
			viewMenu = new JMenu(Language.view);
			viewMenu.setMnemonic('V');

			showTableNumbers = new JCheckBoxMenuItem(Language.show_table_numbers);
			showTableNumbers.setSelected(true);
			showTableNumbers.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					CryodexController.getOptions().setShowTableNumbers(showTableNumbers.isSelected());
				}
			});

			showQuickFind = new JCheckBoxMenuItem(Language.show_quick_table_search);
			showQuickFind.setSelected(false);
			showQuickFind.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					CryodexController.getOptions().setShowQuickFind(showQuickFind.isSelected());
				}
			});

			final JCheckBoxMenuItem showRegistrationPanel = new JCheckBoxMenuItem(Language.show_registration_panel);
			showRegistrationPanel.setSelected(true);
			showRegistrationPanel.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					Main.getInstance().getRegisterPane().remove(Main.getInstance().getRegisterPanel());
					if (showRegistrationPanel.isSelected()) {
						Main.getInstance().getRegisterPane().add(Main.getInstance().getRegisterPanel());
					}

					Main.getInstance().validate();
					Main.getInstance().repaint();
				}
			});

			viewMenu.add(showQuickFind);
			viewMenu.add(showTableNumbers);
			viewMenu.add(showRegistrationPanel);

			for (final Module m : CryodexController.getModules()) {
				final JCheckBoxMenuItem moduleItem = new JCheckBoxMenuItem(Modules.getNameByModule(m));
				moduleItem.setSelected(m.isModuleEnabled());
				moduleItem.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent arg0) {
						m.setModuleEnabled(moduleItem.isSelected());
						Modules moduleEnum = Modules.getEnumByName(Modules.getNameByModule(m));
						if (moduleItem.isSelected()) {
							CryodexController.getOptions().getNonVisibleModules().remove(moduleEnum);
						} else {
							CryodexController.getOptions().getNonVisibleModules().add(moduleEnum);
						}
						CryodexController.saveData();
					}
				});

				m.setViewMenuItem(moduleItem);

				viewMenu.add(moduleItem);
			}
		}

		return viewMenu;
	}

	@Override
	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu(Language.help);
			helpMenu.setMnemonic('H');

			JMenuItem about = new JMenuItem(Language.about);
			about.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					AboutPanel.showAboutPanel();
				}
			});
			JMenuItem whereIsSave = new JMenuItem(Language.where_is_save_file);
			whereIsSave.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					File path = new File(System.getProperty("user.dir"));
					if (path.exists() == false) {
						System.out.println(Language.error_user_directory);
					}
					File file = new File(path, CryodexController.CRYODEX_SAVE);

					if (file.exists()) {
						JOptionPane.showMessageDialog(Main.getInstance(),
								"<html>" + Language.save_file_found + "<b>" + file.getAbsolutePath() + "</b></html>");
					} else if (path.exists() == false) {
						JOptionPane.showMessageDialog(Main.getInstance(), Language.no_save_location);
					} else if (file.exists() == false) {
						JOptionPane.showMessageDialog(Main.getInstance(),
								"<html>" + Language.no_save_file_1 + "<b>" + CryodexController.CRYODEX_SAVE + "</b>"
										+ Language.no_save_file_2 + "<b>" + path.getAbsolutePath() + "</b></html>");
					}
				}
			});

			helpMenu.add(about);
			helpMenu.add(whereIsSave);
		}
		return helpMenu;
	}

	public void resetMenuBar() {

		showTableNumbers.setSelected(CryodexController.getOptions().isShowTableNumbers());
		showQuickFind.setSelected(CryodexController.getOptions().isShowQuickFind());

		for (Module m : CryodexController.getModules()) {
			m.getMenu().resetMenuBar();
		}
	}

	public void updateTournamentOptions(CryodexOptions options) {
		options.setShowTableNumbers(showTableNumbers.isSelected());
		options.setShowQuickFind(showQuickFind.isSelected());
	}

}
