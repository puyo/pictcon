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
import pictcon.queue.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Vector;
import java.util.zip.Deflater;
import java.lang.Math;
import javax.swing.*;

/** The Image area for display in the describe phase and editing in the draw phase. 
  * In the Draw phase, the variable editable is set true. In the describe phase
  * editable is set false.
  */
public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener {
	private int width;
	private int height;
	private int resolutionUnit;

	private BufferedImage imageBuffer;	
	private Graphics2D imageBufferGraphics;

	private static final int ERASE = 0;
	private static final int DRAW = 1;

	// Low level mode, actually used (draw/erase).
	private int mode = DRAW;

	// High level mode, specified externally (draw/erase).
	private int controlsMode = DRAW;

	private int currentX, currentY;
	private int lastX, lastY;

	private boolean started = false;
	private boolean drawFloats = false;
	private boolean editable = true;

	/** Constructs an image of width by height pixels.*/
	public ImagePanel(int width, int height, int resolutionUnit) {

		this.width = width;
		this.height = height;
		this.resolutionUnit = resolutionUnit;
		
		// Create the back buffer and Graphics.
		imageBuffer = new BufferedImage((width/resolutionUnit), (height/resolutionUnit), BufferedImage.TYPE_BYTE_INDEXED);
		imageBufferGraphics = imageBuffer.createGraphics();
		imageBufferGraphics.setBackground(Color.white);

		// Clear the image.
		imageBufferGraphics.setColor(getBackground());
		imageBufferGraphics.fillRect(0, 0, width/resolutionUnit, height/resolutionUnit);

		setBackground(Color.white);
		setForeground(Color.black);
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public Dimension getSize() {
		return new Dimension(width, height);
	}

	public Dimension getPreferredSize() {
		return getSize();
	}

	/** Set whether or not the image is editable. */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/** Width and height in pixels of one 'point' in this image. */
	public int getResolutionUnit() {
		return resolutionUnit;
	}

	/** Width in pixels of this image. */
	public int getWidth() {
		return width;
	}

	/** Height in pixels of this image. */
	public int getHeight() {
		return height;
	}

	/** Change to drawing mode. */
	public void setDrawMode() {
		mode = controlsMode = DRAW;
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	/** Change to erasing mode. */
	public void setEraseMode() {
		mode = controlsMode = ERASE;
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}

	/** Clears the image to the background colour. */
	public void clear() {
		boolean editableStatus = editable;
		editable = true;

		// Clear a rectangle the size of the image.
		imageBufferGraphics.clearRect(0, 0, width/resolutionUnit, height/resolutionUnit);
		repaint();
		editable = editableStatus;
	}

	/** Sets the image currently being displayed. */
	public void setImage(RoundItem newImage) {
		boolean editableStatus = editable;
		editable = true;
		Vector imageVector = new Vector();
		imageVector = Conversion.inflate(newImage.getData());
		Raster imageRaster = Conversion.vectorToRaster(imageBuffer, imageVector);
//		Raster imageRaster = vectorToRaster(getDummyImage());

		// Set the buffered image to the raster.
		imageBuffer.setData(imageRaster);
		repaint();
		editable = editableStatus;
	}
	
	/** Paint the image buffer to the screen.*/
	public void paint(Graphics g) {
		g.drawImage(imageBuffer, 0, 0, width, height, this);

		// Draw any floating graphics.
		if (drawFloats) {
			// When in erase mode, draw a floating rectangle over the area selected.
			if (mode == ERASE) {
				imageBufferGraphics.setColor(getBackground());

				int x1 = Math.min(currentX, lastX);
				int x2 = Math.max(currentX, lastX);
				int y1 = Math.min(currentY, lastY);
				int y2 = Math.max(currentY, lastY);
				g.drawRect(x1, y1, x2 - x1, y2 - y1);
			}
		}
	}

	/** Updates the buffered image. Only used if the image is editable. */
	private void updateBuffer() {
		if (editable) {
			switch (mode) {
				case ERASE:
					int x1 = Math.min(currentX, lastX);
					int x2 = Math.max(currentX, lastX);
					int y1 = Math.min(currentY, lastY);
					int y2 = Math.max(currentY, lastY);
					int x1Scaled = x1/resolutionUnit;
					int y1Scaled = y1/resolutionUnit;
					int x2Scaled = (int)Math.ceil((double)x2/(double)resolutionUnit);
					int y2Scaled = (int)Math.ceil((double)y2/(double)resolutionUnit);
					imageBufferGraphics.clearRect(x1Scaled, y1Scaled, x2Scaled - x1Scaled, y2Scaled - y1Scaled);
					break;
	
				case DRAW:
				default:
					// Draw the current line.
					imageBufferGraphics.setColor(Color.black);
					if (started) {
						imageBufferGraphics.drawLine((lastX/resolutionUnit), (lastY/resolutionUnit), (currentX/resolutionUnit), (currentY/resolutionUnit));
//						imageBufferGraphics.drawString("String is the best substance in the whole wide world", (currentX/resolutionUnit), (currentY/resolutionUnit));
					}
					break;
			}
		}
	}

	/** Overrides the standard update method to prevent flickering. */
	public void update(Graphics g) {
		paint(g);
	}

	/** Saves the current image to a text file */
	private void save(String filename) {
		try {
			FileOutputStream out = new FileOutputStream(filename);
			Raster r = imageBuffer.getData();

			int[] pixel = new int[1];

			int i, j;
			for (j = 0; j < (getHeight()/resolutionUnit); ++j) {
				for (i = 0; i < (getWidth()/resolutionUnit); ++i) {
					r.getPixel(i, j, pixel);
					if (pixel[0] != 0) {
						out.write('#');
					} else {
						out.write('.');
					}
				}
				out.write('\n');
			}
			out.close();

		} catch (IOException e) {
			Debug.error("Error saving image: " + e);
		}
	}

	/** Returns a Vector of bytes representing the image. 
	  * The image has been converted into vector of bytes in which each byte represents 8 pixels in the image. 
	  * E.g. in binary the byte 10100011 would represent:
	  * white pixel, black pixel, white pixel, black pixel, black pixel, black pixel, white pixel, white pixel. 
	  * This vector of bytes is then deflated using the java implementation of the ZLIB compression algorithm 
	  * (i.e. the java Deflater class).
	  */
	public Vector getDeflatedImage() {
		// A byte represents 8 pixels. 
		Vector imageBytes = new Vector();
		// Make a raster of the current image.
		final Raster raster = imageBuffer.getData();
		// Used to store the current byte being 'filled' with information.
		int currentByte = 0;
		// Used to keep track of which bit in the current byte we are writing data to.
		int currentBit = 0;
		// RGB bands in image.
		final int bands = 1;
		// Storage for getPixel().
		int[] pixel = new int[bands];

		// Copy the image pixels to bytes (8 pixels per byte).
		/* Warning: With this method of of pixel storage, there may be an additional 0-7 extra black pixels on the end of the Image. These should be trimmed in the image display method.*/ 
		for (int j = 0; j != (height/resolutionUnit); j++) {
			for (int i = 0; i != (width/resolutionUnit); i++) {
				raster.getPixel(i, j, pixel);
				if (pixel[0] == 215){
					// If the current pixel is white, set the bit in the current byte.
					// (Fill up the beginning of the byte first, hence 7 - currentBit.)					
					currentByte |= (0x1 << (7 - currentBit)); // OR a single bit in the correct position with the current byte.
				}
				currentBit += 1;
				if (currentBit == 8){
					imageBytes.add(new Byte((byte)currentByte));
					currentByte= 0;
					currentBit = 0;
				}
			}
		}
//		Debug.print("Image raster stored as bytes: " + imageBytes.size() + " bytes.");
		byte[] inflatedImage = new byte[imageBytes.size()];
		for (int i = 0; i != imageBytes.size(); i++) {
			inflatedImage[i] = ((Byte)imageBytes.get(i)).byteValue();
		}
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		Vector deflatedImage = new Vector();
		int deflatedByteCount;
		byte[] buffer = new byte[10];
		deflater.setInput(inflatedImage);
		deflater.finish();
		while ((deflatedByteCount = deflater.deflate(buffer)) > 0) {
			for (int i = 0; i != deflatedByteCount; i++)
				deflatedImage.add(new Byte(buffer[i]));
		}
//		Debug.print("deflatedImage.size() = " + deflatedImage.size());
		return deflatedImage;
	}

	public Vector getDummyImage() {
		// A byte represents 8 pixels. 
		Vector imageBytes = new Vector();

		byte blackByte = (byte)85;
		byte whiteByte = (byte)255;

		// RGB bands in image.
		final int bands = 1;
		// Storage for getPixel().
		int[] pixel = new int[bands];

		// Top half black.
		for (int j = 0; j != ((height/16)/resolutionUnit); j++) {
			for (int i = 0; i != (width/resolutionUnit); i++) {
				imageBytes.add(new Byte(blackByte));
			}
		}
		// Bottom half white.
		for (int j = 0; j != ((height/16)/resolutionUnit); j++) {
			for (int i = 0; i != (width/resolutionUnit); i++) {
				imageBytes.add(new Byte(whiteByte));
			}
		}
//		Debug.print("Image raster stored as bytes: " + imageBytes.size() + " bytes.");
		return imageBytes;
	}
	
	/////////////////////////////////////////////////////////////////
	// Mouse event handling

	public void mouseDragged(MouseEvent e) {
		e.consume();
		switch (mode) {
			case ERASE:
				currentX = e.getX();
				currentY = e.getY();
				drawFloats = true;
				repaint();
				break;
			case DRAW:
			default:
				lastX = currentX;
				lastY = currentY;
				currentX = e.getX();
				currentY = e.getY();
				updateBuffer();
				repaint();
				break;
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		e.consume();

		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			mode = ERASE;
		}

		switch (mode) {
			case ERASE:
				lastX = currentX = e.getX();
				lastY = currentY = e.getY();
				break;
			case DRAW:
			default:
				lastX = currentX = e.getX();
				lastY = currentY = e.getY();
				started = true;
				break;
		}
	}

	public void mouseReleased(MouseEvent e) {
		e.consume();
		switch (mode) {
			case ERASE:
				currentX = e.getX();
				currentY = e.getY();
				drawFloats = false;
				updateBuffer();
				repaint();
				break;
			case DRAW:
			default:
				break;
		}
		mode = controlsMode;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		e.consume();
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			mode = ERASE;
		}
		switch (mode) {
			case ERASE:
				break;
			case DRAW:
				lastX = currentX;
				lastY = currentY;
				currentX = e.getX();
				currentY = e.getY();
				updateBuffer();
				repaint();
				break;
			default:
				break;
		}
	}

}
