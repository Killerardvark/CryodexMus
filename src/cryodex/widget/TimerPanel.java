package cryodex.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cryodex.Language;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class TimerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel timeLabel;
	private JButton startTimeButton;
	private JButton stopTimeButton;
	private JButton resetTimeButton;
	private long timeStart = 0;
	private JSpinner spinner;
	private Timer timer;
	private long timeRemaining = 0;
	private long millisInRound = 0;

	private final static java.text.SimpleDateFormat timerFormat = new java.text.SimpleDateFormat(
			"ss");

	public TimerPanel() {

		super(new FlowLayout());
		JPanel panel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel spinnerPanel = new JPanel(new FlowLayout());

		spinnerPanel.add(new JLabel(Language.minutes));
		spinnerPanel.add(getSpinner());

		buttonPanel.add(getStartTimeButton(), BorderLayout.NORTH);
		buttonPanel.add(getStopTimeButton(), BorderLayout.CENTER);
		buttonPanel.add(getResetTimeButton(), BorderLayout.SOUTH);

		panel.add(getTimeLabel(), BorderLayout.NORTH);
		panel.add(buttonPanel, BorderLayout.CENTER);
		panel.add(spinnerPanel, BorderLayout.SOUTH);

		this.add(panel);
	}

	public JLabel getTimeLabel() {
		if (timeLabel == null) {
			timeLabel = new JLabel(" ", JLabel.CENTER);
			resetTime();
		}
		return timeLabel;
	}

	public JSpinner getSpinner() {
		if (spinner == null) {
			spinner = new JSpinner(new SpinnerNumberModel(75, 1, 1440, 1));
			JComponent field = spinner.getEditor();
			Dimension prefSize = field.getPreferredSize();
			prefSize = new Dimension(30, prefSize.height);
			field.setPreferredSize(prefSize);
			spinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					resetTime();
				}
			});
		}
		return spinner;
	}

	public JButton getStartTimeButton() {
		if (startTimeButton == null) {
			startTimeButton = new JButton(Language.start);
			startTimeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					startTime();
				}
			});
		}

		return startTimeButton;
	}

	public Timer getTimer() {
		if (timer == null) {
			timer = new Timer(1, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					checkTime();
				}
			});
		}
		return timer;
	}

	public JButton getStopTimeButton() {
		if (stopTimeButton == null) {
			stopTimeButton = new JButton(Language.stop);
			stopTimeButton.setEnabled(false);
			stopTimeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					stopTime();
				}
			});
		}

		return stopTimeButton;
	}

	public JButton getResetTimeButton() {
		if (resetTimeButton == null) {
			resetTimeButton = new JButton(Language.reset);
			resetTimeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					resetTime();
				}
			});
		}

		return resetTimeButton;
	}

	private void startTime() {
		getSpinner().setEnabled(false);
		getStartTimeButton().setEnabled(false);
		getResetTimeButton().setEnabled(false);
		getStopTimeButton().setEnabled(true);

		if (millisInRound == 0) {

			Number d = (Number) getSpinner().getValue();

			millisInRound = d.longValue() * 60 * 1000;
		}

		timeStart = System.currentTimeMillis();
		getTimer().start();
	}

	private void stopTime() {
		getSpinner().setEnabled(true);
		getStartTimeButton().setEnabled(true);
		getResetTimeButton().setEnabled(true);
		getStopTimeButton().setEnabled(false);

		getTimer().stop();
		millisInRound = timeRemaining;
	}

	private void resetTime() {
		Number d = (Number) getSpinner().getValue();
		millisInRound = d.longValue() * 60 * 1000;
		getTimeLabel()
				.setText(
						d.longValue() + ":"
								+ timerFormat.format(new java.util.Date(0)));
	}

	private void checkTime() {

		long currentTime = System.currentTimeMillis();

		long timeElapsed = (currentTime - timeStart);

		long minutesElapsed = timeElapsed / 1000 / 60;

		long minutesForRound = (millisInRound - 1) / 1000 / 60;

		long minutesRemaining = minutesForRound - minutesElapsed;

		timeRemaining = millisInRound - timeElapsed;

		if (timeRemaining < 0) {
			getTimeLabel().setText(Language.round_over);
			stopTime();

			startAudio();
		} else {
			getTimeLabel().setText(
					minutesRemaining
							+ ":"
							+ timerFormat.format(new java.util.Date(
									timeRemaining)));
		}

	}

	private void startAudio() {
		try {
			// ** add this into your application code as appropriate
			// Open an input stream to the audio file.
			InputStream in = new FileInputStream("Cryodex.wav");
			// Create an AudioStream object from the input stream.
			AudioStream as = new AudioStream(in);
			// Use the static class member "player" from class AudioPlayer to
			// play
			// clip.
			AudioPlayer.player.start(as);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}