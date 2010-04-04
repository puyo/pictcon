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
import java.util.zip.Deflater;
import java.util.zip.Inflater;

// ---------------------------------------------------------------------------
// Send Button
// ---------------------------------------------------------------------------

/** A button that when pressed sends either an image or a description of an image. */
public class SendImageButton extends SendButton implements ActionListener {

	private ImagePanel imagePanel;
	private InstructionArea instructionArea;
	private final int sizeOfInt = 4;
	
	/** Constructs the send button. 
	  * @param imagePanel = The imagePanel which contains the image to be sent or the image that the description to be sent describes.
	  * @param outputToServer = The output stream to send the information.
	  */
	public SendImageButton(ImagePanel imagePanel, InstructionArea instructionArea) {
		super();
		this.imagePanel = imagePanel;
		this.instructionArea = instructionArea;
	}
	
	public String getType() {
		return "SendImageButton";
	}

	/** Sends the image or description. */
	public void actionPerformed(ActionEvent e) {
		Vector deflatedImage = new Vector();
		int paperID = 0;
		int roundID = 0;

		Debug.print("SEND!");
		
		// Send the width and height of the image coded as 4 bytes each.
		try {
			outputToServer.writeInt(MessageTypes.ROUND_ITEM);
			
			// Deflate the image.
			deflatedImage = imagePanel.getDeflatedImage();
	
			// Calculate the number of bytes to be sent.
			int byteNum = sizeOfInt + sizeOfInt + deflatedImage.size();
			// 1 byte to indicate how long the player ID is.
			// The actual paperID.
			// 1 byte for the roundID.
			// Lastly the actual deflated image.
	
			// Send the 'number of bytes to be sent' coded as 4 bytes.
			outputToServer.writeInt(byteNum);
			
			// Send the paperID
			paperID = client.getCurrentItemPaperID();
			outputToServer.writeInt(paperID);	

			// Send the roundID
			roundID = client.getCurrentItemRoundID() + 1;			
			outputToServer.writeInt(roundID);

			// Disable, so images cannot be sent twice.
			setEnabled(false);

			// Disable images so that they cannot be edited once sent.
			imagePanel.setEditable(false);
			instructionArea.setWaitInstructions();

		} catch(IOException x) {
			Debug.error("Failed to send round item(paperID = " + paperID + ", roundID = " + roundID + ").");
		}

		// Send the actual deflated image bytes.
		sendVector(deflatedImage, outputToServer);
		
		// Send deflatedImage to own client (every client keeps a copy
		// of every round item).
		client.receiveRoundItem(paperID, roundID, vectorToByteArray(deflatedImage));
		client.nextQueueItem();
	}
}
