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
import javax.swing.*;
import java.io.*;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

// ---------------------------------------------------------------------------
// Send Button
// ---------------------------------------------------------------------------

/** A button that when pressed sends either an image or a description of an image. */
public abstract class SendButton extends JButton implements ActionListener {

	protected DataOutputStream outputToServer;
	protected final int sizeOfInt = 4;
	protected Client client;
	
	/** Constructs the send button. 
	  * @param imagePanel = The imagePanel which contains the image to be sent or the image that the description to be sent describes.
	  * @param outputToServer = The output stream to send the information.
	  */
	public SendButton() {
		super("Send");
		addActionListener(this);
	}

	public void setOutputStream(DataOutputStream outputToServer) {
		this.outputToServer = outputToServer;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	/** Sends the image or description. */
	public abstract void actionPerformed(ActionEvent e);

	// Return the type of send button the object is. E.g. SendImageButton or SendDescriptionButton.
	public abstract String getType();

	protected void sendVector(Vector vector, DataOutputStream outputToServer) {
		try {
			outputToServer.write(vectorToByteArray(vector));
		} catch (IOException e) {
			Debug.error("Error talking to server: " + e.getMessage());
		}
	}
	protected byte[] vectorToByteArray(Vector vector) {
		int size = vector.size();
		byte[] byteArray = new byte[size];
		for (int i = 0; i != size; i++) {
			byteArray[i] = ((Byte)vector.elementAt(i)).byteValue();
		}
		return byteArray;
	}
}
