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
import pictcon.com.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.InetAddress;


public class HostScreen extends GameDialog implements ActionListener, SpinListener {

	private ServerCoordinator serverCoordinator;
	
	// Information about joining players.
	private JTextArea message;
	private JPanel information;
	private Spinner spinner;
	private JTextField roundsField;
	private int rounds = 10;
	private int maxRounds = 99;
	private int minRounds = 2;
	
	public HostScreen(Frame parent, ServerCoordinator serverCoordinator) {
		super(parent, "Host Setup", GameDialog.DEFAULT_PADDING);
		this.serverCoordinator = serverCoordinator;
		buildGUI();
		serverCoordinator.setOutput(message);
	}

	private void buildGUI() {
		information = new JPanel();
		information.setLayout(new BorderLayout());

		// Set up the message area. This must be set up first as it is used to display errors 
		// which occur when setting up the other parts of the GUI.
		message = new JTextArea("Waiting for players to join...", 20, 40);
		message.setEditable(false);
		//message.setEnabled(false);
		message.setBackground(Color.white);
		message.setForeground(Color.black);
		information.add(message, BorderLayout.SOUTH);
		
		// Add a small buffer between message display window and IP addy.
		JPanel buffer = new JPanel();
		information.add(buffer, BorderLayout.CENTER);
		
		// Set up the I.P. Address display. This will appear at the very top.
		try {
			JLabel address = new JLabel("I.P. Address = " + InetAddress.getLocalHost());		
			information.add(address, BorderLayout.NORTH);
			
		} catch (Exception e){
			message.append("Cannot find local I.P. Address.");
		}
	
		// Add information (IP Addy and message display window) to the host screen.
		contents.add(information, BorderLayout.NORTH);
		
		// Create a spinner to choose the number of rounds for the game.
		JPanel options = new JPanel();
		JLabel label = new JLabel("Number of rounds");
		options.add(label);
		roundsField = new JTextField(Integer.toString(rounds), 3);
		spinner = new Spinner(roundsField);
		spinner.addSpinListener(this);
		options.add(spinner);
		
		// Add the options to the host screen.
		contents.add(options, BorderLayout.CENTER);

		// Add the begin game button to the host screen.
		JButton hostButton = new JButton("Begin Game");
		hostButton.addActionListener(this);
		hostButton.requestFocus();
		hostButton.grabFocus();
		contents.add(hostButton, BorderLayout.SOUTH);
		centre();
	}


	// Called when the user presses 'Begin game'.
	public void actionPerformed(ActionEvent e) {
		try {
			int tempRounds = Integer.parseInt(roundsField.getText());
			if ((tempRounds >= minRounds) && (tempRounds <= maxRounds)) {
				rounds = tempRounds;
			} else {
				throw new Exception("Number chosen is out of bounds.");
			}
		serverCoordinator.setNumberOfRounds((rounds + 1));
		serverCoordinator.setReadyToBegin(true);
		hide();
		} catch (NumberFormatException x) {
			message.append("\nCannot begin game, number of rounds is not an integer.");
		} catch (Exception x) {
			message.append("\nCannot begin game, illegal number of rounds.");
		}
	}	
	
	public void spinnerSpunUp(SpinEvent e) {
		try {
			int tempRounds = Integer.parseInt(roundsField.getText());
			if ((tempRounds >= minRounds) && (tempRounds <= maxRounds)) {
				rounds = tempRounds;
			}
		} catch (NumberFormatException x) {
		}
		if (rounds < maxRounds) {
			rounds++;
		}
		roundsField.setText(Integer.toString(rounds));	
	}

	public void spinnerSpunDown(SpinEvent e) {
		try {
			int tempRounds = Integer.parseInt(roundsField.getText());
			if ((tempRounds >= minRounds) && (tempRounds <= maxRounds)) {
				rounds = tempRounds;
			}
		} catch (NumberFormatException x) {
		}
		if (rounds > minRounds) {
			rounds--;
		}
		roundsField.setText(Integer.toString(rounds));
	}

}


