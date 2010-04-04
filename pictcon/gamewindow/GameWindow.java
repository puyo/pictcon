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
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;

public class GameWindow extends JFrame {

	private InstructionArea instructionArea;
	private ImagePanel imagePanel;
	private UserList userList;
	private ChatArea chatArea;
	private Graphics g;

 	private SendImageButton sendImageButton;
	private SendDescriptionButton sendDescriptionButton;
	
	private DrawControls drawControls;
	private DescriptionField descriptionField;
	private DescriptionArea descriptionArea;

	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	private static final int DRAW_PHASE = 1;
	private static final int DESCRIBE_PHASE = 2;
	
	private int phase = DRAW_PHASE;
	
	public GameWindow() {
		super("Pictorial Consequences");
		
		instructionArea = new InstructionArea(getBackground());
		imagePanel = new ImagePanel(384, 384, Constants.resolutionUnit);

		descriptionField = new DescriptionField();

		sendImageButton = new SendImageButton(imagePanel, instructionArea);
		sendDescriptionButton = new SendDescriptionButton(descriptionField, instructionArea);

		drawControls = new DrawControls(imagePanel, sendImageButton);
		descriptionArea = new DescriptionArea(imagePanel, sendDescriptionButton, descriptionField);

		userList = new UserList();
		chatArea = new ChatArea();

		buildGUI();
	}
	
	Container middle;
	
	/** Build and display the game window. */
	private void buildGUI() {

		Border margin = new EmptyBorder(5, 5, 5, 5);
		Border border = LineBorder.createGrayLineBorder();

		JPanel gamePanel = new JPanel();

		gamePanel.setLayout(new BorderLayout());

		middle = new Container();
		middle.setLayout(new BorderLayout());

		JScrollPane instructionAreaScrollPane = new JScrollPane(instructionArea);
		Border gap = new EmptyBorder(0, 0, 5, 0);
		instructionAreaScrollPane.setBorder(new CompoundBorder(gap, border));
		instructionAreaScrollPane.setViewportBorder(margin);
		middle.add(instructionAreaScrollPane, BorderLayout.NORTH);

		JPanel imagePanelPanel = new JPanel();
		imagePanelPanel.add(imagePanel);
		imagePanelPanel.setBorder(border);
		middle.add(imagePanelPanel, BorderLayout.CENTER);

		middle.add(drawControls, BorderLayout.SOUTH);

		gamePanel.add(middle, BorderLayout.CENTER);
		gamePanel.add(userList, BorderLayout.EAST);

		gamePanel.add(chatArea, BorderLayout.SOUTH);

		gamePanel.setBorder(margin);
		
		getContentPane().add(gamePanel);
		setResizable(false);

		pack();
		int width = getSize().width;
		int height = getSize().height;
		show();
		
		g = getGraphics(); // Get graphics to use so that drawcontrols/description area refreshes properly.
		
		setSize(width, height);
		setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		
		addWindowListener(new MyAdapter());
		
		// Begin with the window disabled.
		imagePanel.setEditable(false);
		sendImageButton.setEnabled(false);
		instructionArea.setWaitInstructions();
	}

	public ChatArea getChatArea() {
		return chatArea;
	}

	public InstructionArea getInstructionArea() {
		return instructionArea;
	}

	public ImagePanel getImagePanel() {
		return imagePanel;
	}

	public UserList getUserList() {
		return userList;
	}
	
	public SendImageButton getSendImageButton() {
		return sendImageButton;
	}

	public SendDescriptionButton getSendDescriptionButton() {
		return sendDescriptionButton;
	}

	// Used to allow window manager to close application.
	static class MyAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	public void setDrawPhase() {
		descriptionField.setText("");
		descriptionField.setEnabled(true);
		sendImageButton.setEnabled(true);
		middle.remove(descriptionArea);
		middle.add(drawControls, BorderLayout.SOUTH);
		pack();
		update(g);
		show();
	}

	public void setDescribePhase() {
		descriptionField.setText("");
		sendDescriptionButton.setEnabled(true);
		middle.remove(drawControls);
		descriptionField.setEnabled(true);
		middle.add(descriptionArea, BorderLayout.SOUTH);
		pack();
		update(g);
		show();
	}
}
