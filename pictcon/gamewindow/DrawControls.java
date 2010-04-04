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


package pictcon.gamewindow;
import pictcon.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import javax.swing.*;

// ---------------------------------------------------------------------------
// DrawControls
// ---------------------------------------------------------------------------

public class DrawControls extends SubArea implements ActionListener {

	private ImagePanel imagePanel;

	public DrawControls(ImagePanel imagePanel, SendButton sendButton) {
		this.imagePanel = imagePanel;
		setLayout(new FlowLayout());
		//setBackground(Color.lightGray);
		//setForeground(Color.black);

		JRadioButton draw = new JRadioButton("Draw");
		draw.addActionListener(this);
		draw.setSelected(true);

		JRadioButton erase = new JRadioButton("Erase");
		erase.addActionListener(this);

		ButtonGroup group = new ButtonGroup();
		group.add(draw);
		group.add(erase);

		add(draw);
		add(erase);
		add(sendButton);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Draw") {
			imagePanel.setDrawMode();
		} else if (e.getActionCommand() == "Erase") {
			imagePanel.setEraseMode();
		}
	}


	/*
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() instanceof JRadioButton) {
			if (((JRadioButton)e.getSource()).getLabel() == "Draw") {
				imagePanel.setDrawMode();
			} else {
				imagePanel.setEraseMode();
			}
		}
	}
	*/
}

