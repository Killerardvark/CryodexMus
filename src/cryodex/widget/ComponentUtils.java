package cryodex.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ComponentUtils {

	public static void removeBorder(JComponent component) {
		component.setBorder(BorderFactory.createEmptyBorder());
	}

	public static void forceSize(JComponent component, int width, int height) {
		Dimension d = new Dimension(width, height);
		component.setMinimumSize(d);
		component.setMaximumSize(d);
		component.setPreferredSize(d);
	}

	public static JPanel addToVerticalBorderLayout(JComponent north,
			JComponent center, JComponent south) {
		JPanel panel = new JPanel(new BorderLayout());

		if (north != null) {
			panel.add(north, BorderLayout.NORTH);
		}

		if (center != null) {
			panel.add(center, BorderLayout.CENTER);
		}

		if (south != null) {
			panel.add(south, BorderLayout.SOUTH);
		}

		return panel;
	}

	public static JPanel addToHorizontalBorderLayout(JComponent west,
			JComponent center, JComponent east) {
		JPanel panel = new JPanel(new BorderLayout());

		if (west != null) {
			panel.add(west, BorderLayout.WEST);
		}

		if (center != null) {
			panel.add(center, BorderLayout.CENTER);
		}

		if (east != null) {
			panel.add(east, BorderLayout.EAST);
		}

		return panel;
	}

	public static JPanel addToFlowLayout(JComponent comp,
			int flowLayoutAlignment) {
		JPanel panel = new JPanel(new FlowLayout(flowLayoutAlignment));
		panel.add(comp);
		return panel;
	}

	public static void repaint(JComponent comp) {
		comp.validate();
		comp.repaint();
	}
}
