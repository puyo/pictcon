// Pictorial Consequences
// Lucy Ding and Gregory McIntyre
// 2001

/*
This file is part of Pictorial Consequences.     

Pictorial Consequences is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.     

Pictorial Consequences is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.     

You should have received a copy of the GNU General Public License
along with Pictorial Consequences; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package pictcon.dialogs;
import pictcon.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/** Displays a dialog so the user can choose 'host' or 'join'.*/
public class TitleScreen extends GameDialog implements ActionListener {

	/** Value of getChoice() when choice has not yet been made. */
	public static int NONE = 0;

	/** Value of getChoice() when 'host' was chosen. */
	public static int HOST = 1;

	/** Value of getChoice() when 'join' was chosen. */
	public static int JOIN = 2;

	private int choice = NONE;

	/** Constructs the setup screen. */
	public TitleScreen(Frame parent) {
		super(parent, "Pictorial Consequences", 0);
		buildGUI();
	}

	/** Gets the user's choice. */
	public int getChoice() {
		return choice;
	}

	/** Make a GUI with three buttons, 'host', 'join' and 'quit'. */
	private void buildGUI() {

		Title title = new Title();
		contents.add(title, BorderLayout.NORTH);

		JPanel options = new JPanel();
		options.setLayout(new GridLayout(1, 3, 10, 10));
		options.setBorder(new EmptyBorder(5, 5, 5, 5));
		contents.add(options, BorderLayout.CENTER);

		JPanel credits = new JPanel();
		credits.setAlignmentX(RIGHT_ALIGNMENT);
		credits.add(new JLabel("A game by Gregory McIntyre and Lucy Ding."));
		contents.add(credits, BorderLayout.SOUTH);

		JButton hostButton = new JButton("Host Game");
		JButton joinButton = new JButton("Join Game");
		JButton quitButton = new JButton("Quit");
		hostButton.addActionListener(this);
		joinButton.addActionListener(this);
		quitButton.addActionListener(this);
		options.add(hostButton, BorderLayout.WEST);
		options.add(joinButton, BorderLayout.EAST);
		options.add(quitButton, BorderLayout.SOUTH);

		centre();
	}

	/** When a button is pressed, records the user's choice and hides the dialog. */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "Host Game") {
			choice = HOST;
		} else if (command == "Join Game") {
			choice = JOIN;
		} else {
			System.exit(0);
		}
		hide();
	}
}


class Title extends JPanel {

	private Image title = null;

	public Title() {
	}
	
	public void paint(Graphics g) {
		if (title == null) {
			readTitle();
		}
		g.drawImage(title, 0, 0, this);
	}

	public Dimension getPreferredSize() {
		return getSize();
	}

	private void readTitle() {

		title = getToolkit().createImage("images/title.gif");

		// Wait for image to load.
		MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(title, 0);
		try {
			tracker.waitForID(0);
			if (tracker.isErrorAny()) {
				System.out.println("Error loading image '" + "images/title.gif" + "'.");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Dimension getSize() {
		if (title == null) {
			readTitle();
		}
		return new Dimension(title.getWidth(null), title.getHeight(null));
	}
}
