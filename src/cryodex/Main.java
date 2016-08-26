package cryodex;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import cryodex.widget.RegisterPanel;
import cryodex.widget.SplashPanel;
import cryodex.widget.TournamentTabbedPane;

/**
 * Main class that creates a singleton of the GUI which everything else is built
 * on.
 * 
 * @author cbrown
 * 
 */
public class Main extends JFrame {

	public static final long delay = 3000;

	private static final long serialVersionUID = 1L;

	private static Main instance = null;

	public static Main getInstance() {
		if (instance == null) {

			instance = new Main();
			instance.setSize(300, 700);

			CryodexController.loadData();
			instance.getRegisterPanel().addPlayers(
					CryodexController.getPlayers());
			CryodexController.isLoading = true;
			MenuBar.getInstance().resetMenuBar();
			CryodexController.isLoading = false;
		}

		return instance;
	}

	private JPanel contentPane;
	private JPanel registerPane;
	private RegisterPanel registerPanel;
	private TournamentTabbedPane multipleTournamentTabbedPane;
	private JPanel tournamentPane;
	private JPanel singleTournamentPane;
	private JPanel warningPane;
	private JLabel warningLabel;

	private Main() {

		super("Cryodex - Mus - 1.2");
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
			Thread.sleep(delay - 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		getContentFlowPane().add(getWarningPane(), BorderLayout.SOUTH);
		getContentFlowPane().add(getRegisterPane(), BorderLayout.WEST);
		getContentFlowPane().add(getTournamentPane(), BorderLayout.CENTER);

		this.add(getContentFlowPane());
		registerPanel.registerButton();

		this.setJMenuBar(MenuBar.getInstance());
	}

	public JPanel getContentFlowPane() {
		if (contentPane == null) {
			contentPane = new JPanel(new BorderLayout());
		}
		return contentPane;
	}

	public RegisterPanel getRegisterPanel() {
		if (registerPanel == null) {
			registerPanel = new RegisterPanel();
		}
		return registerPanel;
	}

	public JPanel getRegisterPane() {
		if (registerPane == null) {
			registerPane = new JPanel(new BorderLayout());
			registerPane.add(getRegisterPanel(), BorderLayout.CENTER);
		}
		return registerPane;
	}

	public JPanel getTournamentPane() {
		if (tournamentPane == null) {
			tournamentPane = new JPanel(new BorderLayout());
		}

		return tournamentPane;
	}

	public JPanel getSingleTournamentPane() {
		if (singleTournamentPane == null) {
			singleTournamentPane = new JPanel(new BorderLayout());
		}

		return singleTournamentPane;
	}

	public JTabbedPane getMultipleTournamentTabbedPane() {
		if (multipleTournamentTabbedPane == null) {
			multipleTournamentTabbedPane = new TournamentTabbedPane();
		}
		return multipleTournamentTabbedPane;
	}

	public void setMultiple(boolean isMultiple) {

		getTournamentPane().removeAll();

		if (isMultiple) {
			getTournamentPane().add(getMultipleTournamentTabbedPane(),
					BorderLayout.CENTER);
		} else {
			getTournamentPane().add(getSingleTournamentPane(),
					BorderLayout.CENTER);
		}

		getTournamentPane().validate();
		getTournamentPane().repaint();
	}
	
	public JPanel getWarningPane() {
		if(warningPane == null){
			warningPane = new JPanel();
			warningPane.setVisible(false);
			warningLabel = new JLabel();
			warningPane.add(warningLabel);
			
			warningPane.setBackground(Color.orange);
			warningLabel.setBackground(Color.orange);
			Font font = new Font("Courier", Font.BOLD,15);
			warningLabel.setFont(font);
		}
		
		return warningPane;
	}
	
	public void setError(String error){
		if(error == null || error.isEmpty()){
			warningLabel.setText("");
			getWarningPane().setVisible(false);
		} else {
			warningLabel.setText(error);
			getWarningPane().setVisible(true);
		}
	}

	public static void main(String[] args) {
		new SplashPanel();

		Main.getInstance().setVisible(true);
		Main.getInstance().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
