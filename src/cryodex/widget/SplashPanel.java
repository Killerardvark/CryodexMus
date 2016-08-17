package cryodex.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cryodex.Main;

public class SplashPanel extends JWindow {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new SplashPanel();
	}

	public SplashPanel() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
					ex.printStackTrace();
				}

				showSplash();

			}
		});
	}

	public void showSplash() {

		JPanel content = (JPanel) getContentPane();
		content.setBackground(Color.black);

		// Set the window's bounds, centering the window
		int width = 500;
		int height = 400;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - width) / 2;
		int y = (screen.height - height) / 2;
		setBounds(x, y, width, height);

		// Build the splash screen
		JLabel ardvark = new JLabel(new ImageIcon(getClass().getResource(
				"logo2.jpg")));

		ImageIcon wait = new ImageIcon(getClass().getResource("wait.gif"));

		content.add(ardvark, BorderLayout.CENTER);
		content.add(new JLabel(wait), BorderLayout.SOUTH);

		// Display it
		setVisible(true);
		toFront();

		new ResourceLoader().execute();
	}

	public class ResourceLoader extends SwingWorker<Object, Object> {

		@Override
		protected Object doInBackground() throws Exception {

			// Wait a little while, maybe while loading resources
			try {
				Thread.sleep(Main.delay);
			} catch (Exception e) {
			}

			return null;

		}

		@Override
		protected void done() {
			setVisible(false);
		}

	}

}