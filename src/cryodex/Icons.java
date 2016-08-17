package cryodex;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {

	private static Icons instance;

	public static Icons getInstance() {
		if (instance == null) {
			instance = new Icons();
		}
		return instance;
	}

	private ImageIcon redx;
	private ImageIcon greencheckmark;

	public Icon getRedX() {
		if (redx == null) {
			URL imgURL = Icons.class.getResource("RedX.png");
			if (imgURL == null) {
				System.out.println("fail!!!!!!!!!!");
			}
			redx = new ImageIcon(imgURL);
		}
		return redx;
	}

	public Icon getGreenCheckMark() {
		if (greencheckmark == null) {
			URL imgURL = Icons.class.getResource("GreenCheckMark.png");
			if (imgURL == null) {
				System.out.println("fail!!!!!!!!!!");
			}
			greencheckmark = new ImageIcon(imgURL);
		}
		return greencheckmark;
	}

}
