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

public class DescriptionArea extends SubArea {
	
	private ImagePanel imagePanel;
	private SendButton sendButton;
	private DescriptionField descriptionField;
		
	public DescriptionArea(ImagePanel imagePanel, SendButton sendButton, DescriptionField descriptionField) {
		this.imagePanel = imagePanel;
		this.sendButton = sendButton;
		this.descriptionField = descriptionField;		
		buildGUI();
	}

	private void buildGUI() {
		//setBackground(Color.lightGray);
		//setForeground(Color.black);		
		add(descriptionField);		
		add(sendButton);
	}

	public String getText() {
		return descriptionField.getText();
	}
}
