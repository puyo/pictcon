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


package pictcon;

import java.awt.*;
import java.util.zip.Inflater;
import java.util.Vector;
import java.awt.image.*;

public abstract class Conversion {

	public static byte[] intToBytes(int number) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)((number >> 24) & 0x00000FF);;
		bytes[1] = (byte)((number >> 16) & 0x00000FF);
		bytes[2] = (byte)((number >> 8) & 0x00000FF);
		bytes[3] = (byte)(number & 0x00000FF);
		return bytes;
	}

	public static int bytesToInt(byte[] bytes) {
		int result = (int)bytes[3];
		result |= (int)(bytes[2] << 8);
		result |= (int)(bytes[1] << 16);
		result |= (int)(bytes[0] << 24);
		return result;
	}

	public static boolean[] byteToBinaryBooleanForm(byte numberByte) {
		boolean[] binaryForm = new boolean[8];
		int number = numberByte;
		if (number < 0){
			number += 256;
		}
		if (number >= 128){
			binaryForm[0] = true;
			number -= 128;
		} else 	binaryForm[0] = false;
		if (number >= 64){
			binaryForm[1] = true;
			number -= 64;
		} else 	binaryForm[1] = false;
		if (number >= 32){
			binaryForm[2] = true;
			number -= 32;
		} else 	binaryForm[2] = false;
		if (number >= 16){
			binaryForm[3] = true;
			number -= 16;
		} else 	binaryForm[3] = false;
		if (number >= 8){
			binaryForm[4] = true;
			number -= 8;
		} else 	binaryForm[4] = false;
		if (number >= 4){
			binaryForm[5] = true;
			number -= 4;
		} else 	binaryForm[5] = false;
		if (number >= 2){
			binaryForm[6] = true;
			number -= 2;
		} else 	binaryForm[6] = false;
		if (number >= 1){
			binaryForm[7] = true;
			number -= 1;
		} else 	binaryForm[7] = false;
		
		if (number != 0){
			Debug.print("Error in conversion! Number = " + number + ".");
		}
		return binaryForm;
	}

	public static BufferedImage bytesToImage(byte[] imageData, int width) {
		Vector imageVector = new Vector();
		imageVector = inflate(imageData);
		int height = 8*imageVector.size()/width;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
		Raster imageRaster = vectorToRaster(image, imageVector);
		image.setData(imageRaster);
		return image;
	}

	private static final int BUFFER_SIZE = 10;

	/** Inflates an array of bytes to produces an uncompressed Vector of bytes. */
	public static Vector inflate(byte[] deflatedImage) {

		Inflater inflater = new Inflater();
		Vector inflatedImage = new Vector();
		int deflatedByteCount;  // Number of compressed bytes stored in the buffer.
		byte[] buffer = new byte[BUFFER_SIZE];
	
		// INFLATE

		inflater.setInput(deflatedImage);
		try {
			// While there are more inflated bytes,
			while ((deflatedByteCount = inflater.inflate(buffer)) > 0) {
				// Add the buffer to the inflatedImage vector.
				for (int i = 0; i != deflatedByteCount; i++)
					inflatedImage.add(new Byte(buffer[i]));
			}
//		Debug.print("Inflated Image stored as bytes: " + inflatedImage.size() + " bytes.");
		}
		catch (Exception e) {
		}
		return inflatedImage;
	}
	
	/** Take a vector of bytes and create a Raster. image is needed to create the raster. */
	public static Raster vectorToRaster(BufferedImage image, Vector vector) {
		Point origin = new Point(0, 0);
		Raster unwritable = image.getData();
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = unwritable.createCompatibleWritableRaster();
		// RGB bands in image.
		final int bands = 1;
		// Used to set pixels in the raster to white.
		int[] white = new int[bands];
		// Used to set pixels in the raster to black.
		int[] black = new int[bands];

		int i = 0;
		int j = 0;

		// Initilatise to white. 
		for (int a = 0; a != bands; a++) {
			white[a] = 215;
		}
		// Initilatise to black. 
		for (int b = 0; b != bands; b++) {
			black[b] = 0;
		}

		int blackPixels = 0;
		int whitePixels = 0;
		int bytes = 0;

		//		Debug.print(width + "x" + height);

		for (int x = 0; x != vector.size(); x++) {
			bytes++;
			byte currentByte = ((Byte)(vector.get(x))).byteValue();
			boolean[] binaryForm = Conversion.byteToBinaryBooleanForm(currentByte);
			for (int y = 0; (y != binaryForm.length) && (j < height); y++) {
				if (binaryForm[y] == true) {
					//					Debug.print(i + "," + j);
					raster.setPixel(i, j, white);
					whitePixels++;
				} else if (binaryForm[y] == false) {
					raster.setPixel(i, j, black);
					//Debug.print("Set pixel black");
					blackPixels++;
				}
				i++;
				if (i >= width) {
					i = 0;
					j++;
				}
			}
		}

//		Debug.print("Number of pixels set to black: " + blackPixels);
//		Debug.print("Number of pixels set to white: " + whitePixels);
//		Debug.print("Number of bytes: " + bytes);
		
		return raster;
	}
}
