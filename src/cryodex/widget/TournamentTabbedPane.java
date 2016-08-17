package cryodex.widget;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import cryodex.MenuBar;

@SuppressWarnings("serial")
public class TournamentTabbedPane extends JTabbedPane {

	@Override
	public void addTab(String arg0, Icon arg1, Component arg2) {
		super.addTab(arg0, arg1, arg2);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MenuBar.getInstance().resetMenuBar();
			}
		});

		JLabel lbl = new JLabel(arg0);
		lbl.setIcon(arg1);

		lbl.setIconTextGap(5);
		lbl.setHorizontalTextPosition(SwingConstants.RIGHT);

		setTabComponentAt(getTabCount() - 1, lbl);
	}
}
