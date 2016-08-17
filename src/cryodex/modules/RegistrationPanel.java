package cryodex.modules;

import javax.swing.JPanel;

import cryodex.Player;

public interface RegistrationPanel {
	public JPanel getPanel();

	public void save(Player player);

	public void load(Player player);

	public void clearFields();
}
