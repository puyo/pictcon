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
import javax.swing.border.*;


/** A top-level window with a title and a border that is typically used to take some form of input from the user.
 */
public abstract class GameDialog extends JDialog {

	/** The size of the entire screen. */
	protected static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	/** The window that created this dialog. */
	protected Frame parent = null;

	/** The contents of this dialog. There is padding between this and the actual dialog window. */
	protected JPanel contents = new JPanel();

	public static final int DEFAULT_PADDING = 10;

	/** Creates a dialog box with a parent and title. */
	public GameDialog(Frame parent, String title, int padding) {
		super(parent, title, true);
		this.parent = parent;
		addWindowListener(new MyDialogAdapter());
		setResizable(true);
		contents.setLayout(new BorderLayout());
		contents.setBorder(new EmptyBorder(padding, padding, padding, padding));
		getContentPane().add(contents, BorderLayout.CENTER);
	}

	/** Centres this dialog on the screen. */
	public void centre() {
		pack();
		int width = getSize().width;
		int height = getSize().height;
		setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
	}

	/** Handles window events for this dialog. */
	protected static class MyDialogAdapter extends WindowAdapter {
		/** Handles window manager close event. */
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
}
