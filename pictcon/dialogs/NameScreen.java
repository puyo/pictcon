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

/** A dialog to enter the name the player wishes to be known by. */
public class NameScreen extends GameDialog implements ActionListener {

	private MyTextField name;

	/** Creates a dialog box to enter a name. */
	public NameScreen(Frame parent) {
		super(parent, "Name Setup", GameDialog.DEFAULT_PADDING);
		buildGUI();
	}

	/** Builds the dialog box from GUI components. */
	private void buildGUI() {
		JLabel message = new JLabel("Please enter your name.");
		contents.add(message, BorderLayout.NORTH);
		name = new MyTextField(100);
		name.addActionListener(this);
		contents.add(name, BorderLayout.CENTER);
		centre();
	}

	/** When the user presses Enter, stores the name chosen. */
	public void actionPerformed(ActionEvent e) {
		if (name.getText().length() > 0) {
			hide();
		}
	}

	/** The name entered. */
	public String getName() {
		return name.getText();
	}
}
