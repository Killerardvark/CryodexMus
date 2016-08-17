package cryodex.widget;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import cryodex.Icons;

public class ConfirmationTextField extends JTextField {

	private static final long serialVersionUID = 1L;

	private JLabel indicator;

	private Icon redx = Icons.getInstance().getRedX();
	private Icon greencheckmark = Icons.getInstance().getGreenCheckMark();

	public ConfirmationTextField() {
		super();

		addListener();
		setIcon();
	}

	private void addListener() {
		this.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				setIcon();
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
	}
	
	@Override
	public void setText(String t) {
		super.setText(t);
		setIcon();
	}

	private void setIcon(){
		Integer points = null;
		try {
			points = Integer.valueOf(ConfirmationTextField.this
					.getText());
		} catch (Exception e) {

		}
		
		JLabel label = new JLabel();
		label.setBorder(BorderFactory.createLineBorder(Color.red));
		
		
		if (points == null) {
			getIndicator().setIcon(redx);
		} else {
			getIndicator().setIcon(greencheckmark);
		}

	}
	
	public JLabel getIndicator() {
		if (indicator == null) {
			indicator = new JLabel();
		}

		return indicator;
	}

}
