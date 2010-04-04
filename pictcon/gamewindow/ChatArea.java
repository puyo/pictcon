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
import javax.swing.*;

/** The GUI for chatting.
  */
public class ChatArea extends Container {

	private MyTextField editText;
	private TextArea displayText;

	private static final int DEFAULT_EDIT_TEXT_WIDTH = 300;
	private static final int DISPLAY_TEXT_ROWS = 4;
	private static final int DISPLAY_TEXT_COLUMNS = 60;
	
	/**
	  *
	  */
	public ChatArea() {
		buildGUI();
	}

	private void buildGUI() {
		// Make the Chat Area.
		setLayout(new BorderLayout());

		// Make a Text Area to display the chat.
		displayText = new TextArea("", DISPLAY_TEXT_ROWS, DISPLAY_TEXT_COLUMNS, TextArea.SCROLLBARS_VERTICAL_ONLY);
		displayText.setEditable(false);
		displayText.setBackground(Color.white);
		displayText.setForeground(Color.black);
		add(displayText, BorderLayout.NORTH);

		// Make a Text Field for the user input to the chat.
		editText = new MyTextField(DEFAULT_EDIT_TEXT_WIDTH);
		add(editText, BorderLayout.SOUTH);
	}
	
	public void addActionListener(ActionListener actionListener) {
		editText.addActionListener(actionListener);
	}
	
	public void enterMessage(String who) {
		displayText.append("\n*** " + who + " has entered ***");
	}
	
	public void leaveMessage(String who) {
		displayText.append("\n*** " + who + " has left ***");
	}

	public void chatMessage(String who, String message) {
		if (message.length() > 0) {
			if (message.substring(0,1).equals("/")){
				if (message.substring(1,3).equals("me")){
					displayText.append("\n* " + who + message.substring(3));
				}else if (message.substring(1,5).equals("nick")){
					displayText.append("\nNickname changing not yet implemented.");
				}
			} else {
				displayText.append("\n<" + who + "> " + message);
			}
		}
	}
	
	public String getMessage() {
		return editText.getText();
	}
	
	public void clearEditField() {
		editText.setText("");
	}
}
