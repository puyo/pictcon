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
import javax.swing.*;

public class InstructionArea extends JTextArea {

	private static final int ROWS = 4;

	private Color background;

	InstructionArea(Color background) {
		super(ROWS, 1);
		this.background = background;
		buildGUI();
	}

	private void buildGUI() {
		setEditable(false);
		setWrapStyleWord(true);
		setLineWrap(true);
		setForeground(Color.black);
		setBackground(background);
	}

	public void setInstructions(String instructions) {
		setText(instructions);
	}

	public void setDrawInstructions(String description) {
		setText("Please draw a picture of '" + description + "'.");
	}

	public void setDescribeInstructions() {
		setText("Please describe the picture shown below.");
	}

	public void setWaitInstructions() {
		setText("Please wait...");
	}

	public void setWaitForEndGame() {
		setText("Please wait for the other players to complete their pictures and descriptions.");
	}
	
	public void endGame() {
		setText("All papers are complete. To play again, please quit and restart this program.");
	}	
}