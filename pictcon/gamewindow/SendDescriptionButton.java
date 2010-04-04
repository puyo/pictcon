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
import pictcon.com.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;

// ---------------------------------------------------------------------------
// Send Description Button
// ---------------------------------------------------------------------------

/** A button that when pressed sends a description of an image. */
public class SendDescriptionButton extends SendButton implements ActionListener {

	private DescriptionField descriptionField;
	private InstructionArea instructionArea;
	private String description;
	
	/** Constructs the send button. 
	  * @param descriptionField = The descriptionField which contains the description to be sent.
	  */
	SendDescriptionButton(DescriptionField descriptionField, InstructionArea instructionArea) {
		super();
		this.descriptionField = descriptionField;
		this.instructionArea = instructionArea;
	}
	
	public String getType() {
		return "SendDescriptionButton";
	}

	/** Sends the image or description. */
	public void actionPerformed(ActionEvent e) {	

		description = descriptionField.getText();
		int paperID = 0;
		int roundID = 0;
		
		Debug.print("SEND!");

		try {
			outputToServer.writeInt(MessageTypes.ROUND_ITEM);
			//Debug.print("SendDescriptionButton MessageTypes.DESCRIPTION: " + MessageTypes.DESCRIPTION);
			
			//Work out the number of bytes to be sent.
			int byteNum = sizeOfInt + sizeOfInt + description.length();
			
			// Send the 'number of bytes to be sent' coded as 4 bytes.
			outputToServer.writeInt(byteNum);
			//Debug.print("SendDescriptionButton byteNum: " + byteNum);
			
			// Send the paperID
			paperID = client.getCurrentItemPaperID();
			outputToServer.writeInt(paperID);	
			//Debug.print("SendDescriptionButton paperID: " + paperID);

			// Send the roundID
			roundID = client.getCurrentItemRoundID() + 1;
			outputToServer.writeInt(roundID);
			//Debug.print("SendDescriptionButton roundID: " + roundID);

			// Send the description			
			outputToServer.writeBytes(description);			

			// Disable, so descriptions cannot be sent twice.
			setEnabled(false);
			descriptionField.setEnabled(false);
			instructionArea.setWaitInstructions();
		}
		catch(IOException x) {
			Debug.error("Failed to send round item(paperID = " + paperID + ", roundID = " + roundID + ").");
		}

		// Send Round item to own client.
		byte[] data = description.getBytes();
		client.receiveRoundItem(paperID, roundID, data);
		client.nextQueueItem();
	}
}
