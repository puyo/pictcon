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
import java.util.Vector;
import javax.swing.*;

public class UserList extends JPanel {

	private JPanel contents;
	private Vector nameList;
	private Vector textAreas;
		
	public UserList() {
		buildGUI();
	}
	
	private void buildGUI() {
		setLayout(new BorderLayout());
		//setBackground(Color.lightGray);
		//setForeground(Color.black);

		contents = new JPanel();
		//contents.setBackground(Color.lightGray);
		//contents.setForeground(Color.black);
		
		add(contents, BorderLayout.CENTER);
		
		contents.setLayout(new GridLayout(20, 1));
		add(new JPanel(), BorderLayout.NORTH);
		add(new JPanel(), BorderLayout.SOUTH);
		add(new JPanel(), BorderLayout.EAST);
		add(new JPanel(), BorderLayout.WEST);

		textAreas = new Vector();

		for (int i = 0; i != 10; i++) {
			JTextField textArea = new JTextField("", 8);
			textArea.setEditable(false);
			textAreas.add(textArea);
		}
		
		for (int i = 0; i != textAreas.size(); i++) {
			contents.add((JTextField)textAreas.get(i));
		}
	}

	public void update(Vector names) {
		nameList = names;
		for (int i = 0; i != textAreas.size(); i++) {
			((JTextField)textAreas.get(i)).setText("");
		}
		for (int i = 0; i != nameList.size(); i++) {
			((JTextField)textAreas.get(i)).setText((String)nameList.get(i));
		}
	}
	
	/** A comma-delimited list of names. */
	public String toString() {
		String result = "";
		int i;
		for (i = 0; i < (nameList.size() - 2); i++) {
			result += (String)nameList.get(i) + ", ";
		}
		if (nameList.size() > 1) {
			result += (String)nameList.get(i) + " and ";
			i++;
		}
		result += (String)nameList.get(i);
		return result;
	}
}
