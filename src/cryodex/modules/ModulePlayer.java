package cryodex.modules;

import cryodex.Player;
import cryodex.xml.XMLObject;

public interface ModulePlayer extends Comparable<ModulePlayer>, XMLObject {

	public Player getPlayer();

	public void setPlayer(Player p);

	String getModuleName();
}
