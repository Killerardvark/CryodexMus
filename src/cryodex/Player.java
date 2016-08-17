package cryodex;

import java.util.ArrayList;
import java.util.List;

import cryodex.CryodexController.Modules;
import cryodex.modules.Module;
import cryodex.modules.ModulePlayer;
import cryodex.xml.XMLObject;
import cryodex.xml.XMLUtils;
import cryodex.xml.XMLUtils.Element;

public class Player implements Comparable<Player>, XMLObject {

	private String name;
	private String groupName;
	private String saveId;
	private String email;
	private List<ModulePlayer> moduleInfo;

	public Player() {
		this("");
	}

	public Player(String name) {
		this.name = name;

		moduleInfo = new ArrayList<ModulePlayer>();
	}

	public Player(Element e) {
		this.name = e.getStringFromChild("NAME");
		this.groupName = e.getStringFromChild("GROUPNAME");
		this.saveId = e.getStringFromChild("SAVEID");
		this.email = e.getStringFromChild("EMAIL");

		Element moduleInfoElement = e.getChild("MODULE-INFO");

		moduleInfo = new ArrayList<ModulePlayer>();

		if (moduleInfoElement != null) {
			for (Element mp : moduleInfoElement.getChildren()) {
				String moduleName = mp.getStringFromChild("MODULE");
				Module m = Modules.getModuleByName(moduleName);
				moduleInfo.add(m.loadPlayer(this, mp));
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupName() {
		return groupName == null ? "" : groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSaveId() {
		return saveId;
	}

	public void setSaveId(String saveId) {
		this.saveId = saveId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<ModulePlayer> getModuleInfo() {
		return moduleInfo;
	}

	public void setModuleInfo(List<ModulePlayer> moduleInfo) {
		this.moduleInfo = moduleInfo;
	}

	public ModulePlayer getModuleInfoByModule(Module m) {
		String moduleName = Modules.getNameByModule(m);
		for (ModulePlayer mp : getModuleInfo()) {
			if (moduleName.equals(mp.getModuleName())) {
				return mp;
			}
		}

		ModulePlayer player = m.getNewModulePlayer(this);
		getModuleInfo().add(player);

		return player;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String toXML() {
		StringBuilder sb = new StringBuilder();

		appendXML(sb);

		return sb.toString();
	}

	@Override
	public StringBuilder appendXML(StringBuilder sb) {

		XMLUtils.appendObject(sb, "NAME", getName());
		XMLUtils.appendObject(sb, "ID", getSaveId());
		XMLUtils.appendObject(sb, "GROUPNAME", getGroupName());
		XMLUtils.appendObject(sb, "SAVEID", getSaveId());
		XMLUtils.appendObject(sb, "EMAIL", getEmail());
		XMLUtils.appendList(sb, "MODULE-INFO", "MODULE-PLAYER", getModuleInfo());

		return sb;
	}

	@Override
	public int compareTo(Player arg0) {
		return this.getName().toUpperCase()
				.compareTo(arg0.getName().toUpperCase());
	}
}
