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


package pictcon.paperwindow;

import pictcon.*;
import pictcon.com.*;
import pictcon.gamewindow.*;
import pictcon.queue.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;


public class PaperWindow extends JFrame implements ActionListener {

	private Client client;

	private int paperID;
	private int rounds;

	private String title;
	
	// Must be worked out with calculations.
	private int width = 128;
	private int height = 640;

	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	private JButton quitButton;
	private JButton saveButton;

	private String quitButtonCaption = "Okay";
	private String saveButtonCaption = "Save";
	private PaperPanel paperPanel;

	public PaperWindow(int paperID, int rounds, Client client) {
		super("Paper " + (paperID + 1));
		this.title = (String)("Paper" + (paperID + 1));
		this.paperID = paperID;
		this.rounds = rounds;
		this.client = client;
		buildGUI();
	}

	
	/** Build and display the paper window. */
	private void buildGUI() {

		paperPanel = new PaperPanel(paperID, rounds, client, width);
		JScrollPane scrollPane = new JScrollPane(paperPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel options = new JPanel();
		options.setLayout(new GridLayout(1, 2, 10, 10));
		
		quitButton = new JButton(quitButtonCaption);
		quitButton.addActionListener(this);
		options.add(quitButton);

		saveButton = new JButton(saveButtonCaption);
		saveButton.addActionListener(this);
		options.add(saveButton);

		getContentPane().add(options, BorderLayout.SOUTH);

		pack();

		// Make sure height of paper is not too big for the screen.
		int height = paperPanel.getSize().height;
		if ((float)height >= 0.8*screenSize.height) {
			height = (int)(0.8*screenSize.height);
		}
		
		setSize(new Dimension(paperPanel.getSize().width + 26, height));
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == quitButtonCaption) {
			hide();
		} else if (command == saveButtonCaption) {
			// Save this paper!
			save();
		}
	}

	private void save() {
		//Create a file chooser
		String defaultDirectory = new String((String)("." + File.separatorChar + "." + File.separatorChar + "saved"));
		Debug.print("." + File.separatorChar);
		final JFileChooser fc = new JFileChooser(defaultDirectory);         
		
		PaperFileFilter filter = new PaperFileFilter("GIF images");
		filter.addExtension("gif");
		filter.addExtension("png");
		fc.setFileFilter(filter);
		fc.setSelectedFile(new File("" + title + ".gif"));
		int returnVal = fc.showSaveDialog(this);
		File file = fc.getSelectedFile();
		BufferedImage paper;
		paper = paperPanel.getPaper();
		try {
			FileOutputStream out = new FileOutputStream(file);
			GIFOutputStream.writeGIF((OutputStream)out, (Image)paper, GIFOutputStream.BLACK_AND_WHITE);
		} catch (Exception e) {
			Debug.print("Error saving image: " + e);
		}
	}
}


/** Panel contained in a PaperWindow. */
class PaperPanel extends JPanel {

	private BufferedImage paper;
	private Vector images = new Vector();
	private Vector roundItems = new Vector();
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private FontMetrics fontMetrics = getFontMetrics(font);

	private int paperWidth;

	private final int paddingVertical = 5;
	private final int paddingHorizontal = 5;

	int itemPadding = 1;
	
	public PaperPanel(int paperID, int rounds, Client client, int width) {
		this.paperWidth = width;

		for (int i = 1; i != rounds; ++i) {
			roundItems.add(client.getRoundItem(paperID, i));
		}
		
		makeImages();
		
		// Add the credits at the end.
		String credits = "A game by " + client.getUserList().toString() + ".";
		images.add(textToImage(credits, Color.gray));
		
		makePaper();
	}

	public Dimension getPreferredSize() {
		return new Dimension(paper.getWidth(), paper.getHeight() + 50);
	}

	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getSize().width, getSize().height);
		g.drawImage(paper, 0, 0, this);
	}

	public BufferedImage getPaper() {
		return paper;
	}

	/** Convert each RoundItem to an equivalent BufferedImage. */
	private void makeImages() {
		for (int i = 0; i != roundItems.size(); ++i) {
			BufferedImage b = makeImage((RoundItem)roundItems.get(i));
			images.add(b);
		}
	}

	private BufferedImage textToImage(String text, Color col) {
		int x = 0;
		int y = 0;
		String line = "";
		Vector lines = new Vector();
		StringTokenizer st = new StringTokenizer(text);
		int spaceWidth = fontMetrics.stringWidth(" ");
		while (st.hasMoreTokens()) {
			String word = st.nextToken();
			int wordWidth = fontMetrics.stringWidth(word);

			//This is one longish sentence, which I am typing out now because we are debugging this program. Yey!

			//Debug.print("'" + word + "' is " + wordWidth + " pixels wide (" + (x + wordWidth + ) + "/" + );

			if (x + wordWidth + paddingHorizontal*2 < paperWidth) {
				// Add this word to the current line.
				x += wordWidth + spaceWidth;
				line += word + " ";
			} else {
				// Store line.
				lines.add(line);

				// Next line.
				x = wordWidth + spaceWidth;
				y++;
				line = word + " ";
			}
		}

		// Store what's left!
		if (line != "") {
			lines.add(line);
		}

		// We now have a vector of strings which fit on the paper.

		int lineHeight = fontMetrics.getHeight();
		int height = lines.size() * lineHeight;
		Debug.print("lineHeight = " + lineHeight);
		Debug.print("height = " + height);
		BufferedImage image = new BufferedImage(paperWidth + paddingHorizontal*2, height + paddingVertical*2, BufferedImage.TYPE_BYTE_INDEXED);
		Graphics g = image.getGraphics();

		g.setFont(font);
		g.setColor(Color.white);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(col);
		for (int i = 0; i != lines.size(); ++i) {
			g.drawString((String)lines.get(i), paddingHorizontal, paddingVertical + fontMetrics.getAscent() + (i * lineHeight));
		}
		return image;
	}

	/** Make a BufferedImage from a single RoundItem. */
	private BufferedImage makeImage(RoundItem item) {
		if (item.isImage()) {
			// We're working on an image.
			byte[] imageBytes = item.getData();
			BufferedImage image = Conversion.bytesToImage(imageBytes, paperWidth);
			return image;
		} else {
			// We're working on a description.
			return textToImage(new String(item.getData()), Color.black);
		}
	}

	/** Make one big BufferedImage by vertically tiling images of round items. */
	private void makePaper() {

		paper = new BufferedImage(paperWidth + paddingHorizontal*2, getPaperHeight(), BufferedImage.TYPE_BYTE_INDEXED);
		Graphics g = paper.getGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, paper.getWidth(), paper.getHeight());

		int y = 0;
		g.setColor(Color.gray);
		// Add the round items (everything except the credits).
		for (int i = 0; i != (images.size() - 1); ++i) {
			BufferedImage item = (BufferedImage)images.get(i);
			int x = (paper.getWidth() - item.getWidth())/2;

			// Add the image to the paper.
			g.drawImage(item, x, y, this);

			// If it was an image, draw a rectangle.
			if (((RoundItem)roundItems.get(i)).isImage()) {
				g.drawRect(x - 1, y - 1, item.getWidth() + 1, item.getHeight() + 1);
			}

			y += item.getHeight() + itemPadding;
		}
		
		// Add the credits.
		BufferedImage item = (BufferedImage)images.get(images.size() - 1);
		int x = (paper.getWidth() - item.getWidth())/2;
		g.drawImage(item, x, y, this);
	}

	/** Calculate the height of this strip of paper. */
	private int getPaperHeight() {
		int height = 0;
		for (int i = 0; i != images.size(); ++i) {
			BufferedImage item = (BufferedImage)images.get(i);
			height += item.getHeight() + itemPadding;
		}
		return height;
	}
}


class PaperFileFilter extends javax.swing.filechooser.FileFilter {

	private String description;
	private Hashtable filters = null;

	public PaperFileFilter(String description) {
		this.filters = new Hashtable();
		this.description = description;
	}

	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.get(extension) != null) {
				return true;
			}
		}
		return false;
	}

	public void addExtension(String extension) {
		if (filters == null) {
			filters = new Hashtable();
		}
		filters.put(extension.toLowerCase(), this);
	}

	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			};
		}
		return null;
	}

	public String getDescription() {
		return description;
	}
}
