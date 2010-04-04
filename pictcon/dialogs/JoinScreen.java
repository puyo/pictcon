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

/** A dialog to enter the IP address of the host game the user wishes to  join. */
public class JoinScreen extends GameDialog implements ActionListener {

	private MyTextField address;
	private String defaultAddress;

	public JoinScreen(Frame parent, String defaultAddress) {
		super(parent, "Join Setup", GameDialog.DEFAULT_PADDING);
		this.defaultAddress = defaultAddress;
		buildGUI();
	}

	private void buildGUI() {
		JLabel message = new JLabel("Please enter the IP address of the host.");
		contents.add(message, BorderLayout.NORTH);
		address = new MyTextField(100);
		address.setText(defaultAddress);
		address.addActionListener(this);
		contents.add(address, BorderLayout.CENTER);
		centre();
	}

	/** Called when the user presses Enter. */
	public void actionPerformed(ActionEvent e) {
		String a = address.getText();
		if (a.length() > 0) {
			hide();
		}
	}

	public String getAddress() {
		return address.getText();
	}
}
