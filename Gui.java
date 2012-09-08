import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Gui extends Jplayer {
	JFrame frame;
	JScrollBar lrcScrollBar;
	JTextArea lrcTextArea;
	JTextField pathTextField;
	JButton chooseButton;
	JButton playButton;
	JProgressBar progressBar;
	JFrame chooserFileFrame;
	JFileChooser fileChooser;

	int flag = 0;

	@Override
	public void showLrc(long ms) {
		// TODO Auto-generated method stub
		if (lrc == null)
			return;

		String lrcString = lrc.getLrcElement(ms);

		if (lrcLine == lrc.currenLine)
			return;

		lrcLine = lrc.currenLine;
		if (lrcString == null) {
			lrcTextArea.setText(null);
		} else {
			lrcTextArea.setText(lrcString);
		}
	}

	@Override
	public void updateProgressBar(long current, long total) {
		int percent = (int) (current * 100 / total);

		progressBar.setValue(percent);
		progressBar.setString(percent + "%");
	}

	public Gui() {
		frame = new JFrame("JPlayer");
		pathTextField = new JTextField("/var/www/wind.mp3");
		playButton = new JButton("Play");
		progressBar = new JProgressBar(0, 0, 100);
		lrcTextArea = new JTextArea();
		lrcScrollBar = new JScrollBar();
		chooseButton = new JButton("\\/");

		progressBar.setStringPainted(true);

		playButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				flag = 1 - flag;

				try {
					if (flag == 0) {
						playButton.setText("Play");
						stop();
					} else {
						playButton.setText("Stop");
						open(pathTextField.getText());
						start();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		chooseButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				chooserFileFrame.setVisible(true);
			}
		});

		chooserFileFrame = new JFrame();
		chooserFileFrame.setBounds(200, 200, 640, 480);
		fileChooser = new JFileChooser("./");
		chooserFileFrame.add(fileChooser);
		// fileChooser.
		// chooserFileFrame.setVisible(true);
		fileChooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.println(event);
				if (event.getActionCommand() == fileChooser.APPROVE_SELECTION) {
					String pathString = fileChooser.getSelectedFile().getPath();
					pathTextField.setText(pathString);
				}
				chooserFileFrame.setVisible(false);
			}
		});

		frame.setBounds(100, 100, 400, 600);

		pathTextField.setBounds(15, 10, 270, 25);
		chooseButton.setBounds(290, 10, 20, 25);
		playButton.setBounds(315, 10, 80, 25);
		progressBar.setBounds(15, 45, 335, 15);
		lrcTextArea.setBounds(15, 75, 320, 490);
		lrcScrollBar.setBounds(335, 75, 13, 490);

		frame.add(pathTextField);
		frame.add(chooseButton);
		frame.add(playButton);
		frame.add(progressBar);
		frame.add(lrcTextArea);
		frame.add(lrcScrollBar);
		frame.setLayout(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		Gui guiPlayer = new Gui();
	}
}
