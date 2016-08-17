package cryodex.widget;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/**
 * This widget is a tabbed pane that has an extra tab at the end that can be
 * used for creating a new tab. The function triggerEvent must be implemented
 * and is called when that tab is clicked.
 * 
 * @author cbrown
 * 
 */
public abstract class AddTabTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1L;

	private JLabel placeHolder;

	public AddTabTabbedPane(String addTabText) {
		super.addTab(addTabText, getPlaceHolder());
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (getSelectedComponent() == getPlaceHolder()) {
					triggerEvent();
				}
			}
		});
	}

	private JLabel getPlaceHolder() {
		if (placeHolder == null) {
			placeHolder = new JLabel();
		}
		return placeHolder;
	}

	@Override
	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);
	}

	@Override
	public void addTab(String title, Component component) {

		int count = getTabCount();
		if (count == 0) {
			count = 1;
		}
		add(component, count - 1);
		setTitleAt(count - 1, title);
		setSelectedComponent(component);
	}

	public abstract void triggerEvent();
}