package cryodex.modules.mus;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;

import cryodex.CryodexController;
import cryodex.CryodexController.Modules;
import cryodex.MenuBar;
import cryodex.Player;
import cryodex.modules.Menu;
import cryodex.modules.Module;
import cryodex.modules.ModulePlayer;
import cryodex.modules.RegistrationPanel;
import cryodex.modules.Tournament;
import cryodex.modules.mus.MusTournamentCreationWizard.WizardOptions;
import cryodex.xml.XMLUtils;
import cryodex.xml.XMLUtils.Element;

public class MusModule implements Module {

	private static MusModule module;

	public static MusModule getInstance() {
		if (module == null) {
			module = new MusModule();
		}

		return module;
	}

	private JCheckBoxMenuItem viewMenuItem;
	private MusMenu menu;
	private MusOptions options;

	private boolean isEnabled = true;

	private MusModule() {

	}

	@Override
	public Menu getMenu() {
		if (menu == null) {
			menu = new MusMenu();
		}
		return menu;
	}

	@Override
	public RegistrationPanel getRegistration() {
		return null;
	}

	@Override
	public void setModuleEnabled(Boolean enabled) {
		isEnabled = enabled;

//		getRegistration().getPanel().setVisible(enabled);
		getMenu().getMenu().setVisible(enabled);
	}

	@Override
	public boolean isModuleEnabled() {
		return isEnabled;
	}

	public static void createTournament() {
		JDialog wizard = new MusTournamentCreationWizard();
		wizard.setVisible(true);

	}

	public static void makeTournament(WizardOptions wizardOptions) {

		MusTournament tournament = new MusTournament(
				wizardOptions.getName(), wizardOptions.getPlayerList(),
				wizardOptions.getInitialSeedingEnum(),
				wizardOptions.getPoints(), wizardOptions.getEscalationPoints(),
				wizardOptions.getTournamentType());

		CryodexController.registerTournament(tournament);

		tournament.startTournament();

		MenuBar.getInstance().resetMenuBar();

		CryodexController.saveData();
	}

	public MusOptions getOptions() {
		if (options == null) {
			options = new MusOptions();
		}
		return options;
	}

	@Override
	public StringBuilder appendXML(StringBuilder sb) {
		XMLUtils.appendXMLObject(sb, "OPTIONS", getOptions());
		XMLUtils.appendObject(sb, "NAME", Modules.XWING.getName());
		return sb;
	}

	@Override
	public ModulePlayer loadPlayer(Player p, Element element) {
		return new MusPlayer(p, element);
	}

	@Override
	public Tournament loadTournament(Element element) {
		return new MusTournament(element);
	}

	@Override
	public void loadModuleData(Element element) {
		options = new MusOptions(element.getChild("OPTIONS"));
	}

	@Override
	public ModulePlayer getNewModulePlayer(Player player) {
		return new MusPlayer(player);
	}

	@Override
	public JCheckBoxMenuItem getViewMenuItem() {
		return viewMenuItem;
	}

	@Override
	public void setViewMenuItem(JCheckBoxMenuItem viewMenuItem) {
		this.viewMenuItem = viewMenuItem;
	}
}
