package cryodex.modules;

import javax.swing.JCheckBoxMenuItem;

import cryodex.Player;
import cryodex.xml.XMLObject;
import cryodex.xml.XMLUtils.Element;

public interface Module extends XMLObject {

	public Menu getMenu();

	public RegistrationPanel getRegistration();

	public void setModuleEnabled(Boolean enabled);

	public boolean isModuleEnabled();

	public Tournament loadTournament(Element element);

	public void loadModuleData(Element element);

	public ModulePlayer loadPlayer(Player p, Element element);

	public ModulePlayer getNewModulePlayer(Player player);
	
	public JCheckBoxMenuItem getViewMenuItem();
	
	public void setViewMenuItem(JCheckBoxMenuItem viewMenuItem);
}
