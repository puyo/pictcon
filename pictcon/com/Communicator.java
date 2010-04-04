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


package pictcon.com;
import pictcon.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.Vector;


/** Communicator communicates with another Communicator called 'partner' (via sockets).
  * (Supports Server/Client interface if each inherits from Communicator).
  * Implements algorithms for sending/receiving all the types of message desired in PictorialConsequences.
  */
public abstract class Communicator extends Thread implements Runnable {

	/** The name of this communicator. e.g. "Server" or "Client". */
	public abstract String name();

	/** The name of this communicator's partner. e.g. "Client" or "Server". */
	public abstract String partnerName();

	/** The socket used to communicate with the partner. */
	protected Socket socket;
	
	/** The input stream from the partner. */
	protected DataInputStream input;

	/** The output stream to the partner. */
	protected DataOutputStream output;

	/** Waits for messages and calls handlers when they arrive. */
	public void run() {

		// These methods implement a simple messaging protocol:
		// 4 byte integer message type
		// 4 byte message length
		// <message length> bytes of data

		try {
			while (true) {
				// Read the type of the next message.
				int messageType = input.readInt();
				int length = input.readInt();
				
				switch (messageType) {

					case MessageTypes.BEGIN:
						beginGame(length);
						break;

					case MessageTypes.ENTER:
						enterMessage(length);
						break;

					case MessageTypes.LEAVE:
						leaveMessage(length);
						break;

					case MessageTypes.CHAT_MESSAGE:
						chatMessage(length);
						break;

					case MessageTypes.ROUND_ITEM:
						roundItem(length);
						break;

					case MessageTypes.LATE_START:
						lateStart(length);
						break;

					case MessageTypes.USER_LIST:
						userList(length);
						break;

					case MessageTypes.QUEUE:
						queueMessage(length);
						break;

					// Skip any messages whose type we don't understand.
					default:
						throw new Exception("Message type not recognised.");
				}
			}
		}
		catch (EOFException e) {
			// Partner is no longer there! Call handler.
			disconnect(partnerName() + " cannot be found.");
		} 
		catch (SocketException e) {
			disconnect(partnerName() + " cannot be found.");
		}		
		catch (Exception e) {
			Debug.error(name() + " encountered an error communicating with its " + partnerName() + ": " + e);
			e.printStackTrace();
		}
	}
	
	private void prepareForMessage(int type, int length) throws IOException {
 		// Write message type.
		output.writeInt(type);

		// Write length (who + message).
		output.writeInt(length);
	}

	//////////////////////////////////////////////////////////////////////
	// Begin
	// (int) number of papers
	// (int) number of rounds

	/** Send a message to the partner telling it to begin the game with the current players.*/
	public void sendBeginGame(int numberOfPapers, int numberOfRounds) throws IOException {
 		prepareForMessage(MessageTypes.BEGIN, (2 * sizeOfInt));
		output.writeInt(numberOfPapers);
		output.writeInt(numberOfRounds);
	}

	private void beginGame(int length) throws IOException {
		int numberOfPapers = input.readInt();
		int numberOfRounds = input.readInt();

		Debug.print(name() + " received BEGIN(numberOfPapers = " + numberOfPapers + ", numberOfRounds = " + numberOfRounds + ")");

		receiveBeginGame(numberOfPapers, numberOfRounds);
	}

	/** Called when this communicator is told to begin the game. */
	public abstract void receiveBeginGame(int numberOfPapers, int numberOfRounds);
	
	//////////////////////////////////////////////////////////////////////
	// LateStart
	// (int) number of papers
	// (int) number of rounds

	/** Send a message to the partner telling it that it has started late and to begin the game.*/
	public void sendLateStart(int numberOfPapers, int numberOfRounds) throws IOException {
		Debug.print(name() + " sending LATE_START(numberOfPapers = " + numberOfPapers + ", numberOfRounds = " + numberOfRounds + ")");
 		prepareForMessage(MessageTypes.LATE_START, (2 * sizeOfInt));
		output.writeInt(numberOfPapers);
		output.writeInt(numberOfRounds);
	}

	private void lateStart(int length) throws IOException {
		int numberOfPapers = input.readInt();
		int numberOfRounds = input.readInt();

		Debug.print(name() + " received LATE_START(numberOfPapers = " + numberOfPapers + ", numberOfRounds = " + numberOfRounds + ")");

		receiveLateStart(numberOfPapers, numberOfRounds);
	}

	/** Called when this communicator is told to begin the game knowing that the other players have already begun. */
	public abstract void receiveLateStart(int numberOfPapers, int numberOfRounds);	

	//////////////////////////////////////////////////////////////////////
	// Enter
	// (int) length
	// (String) who

	/** Send a message to the partner telling it a user has entered. */
	public void sendEnterMessage(String who) throws IOException {
		output.writeInt(MessageTypes.ENTER);
		output.writeInt(who.length());
		output.writeBytes(who);
	}

	private void enterMessage(int length) throws IOException {
		byte[] data = new byte[length];
		input.readFully(data);
		String who = new String(data, 0);
		Debug.print(name() + " received ENTER(who = " + who + ")");
		receiveEnterMessage(who);
	}

	/** Called when this communicator receives an enter message. */
	public abstract void receiveEnterMessage(String who);

	//////////////////////////////////////////////////////////////////////
	// Leave
	// (int) length
	// (String) who

	/** Send a message to the partner telling it a user has left. */
	public void sendLeaveMessage(String who) throws IOException {
		output.writeInt(MessageTypes.LEAVE);
		output.writeInt(who.length());
		output.writeBytes(who);
	}

	private void leaveMessage(int length) throws IOException {
		byte[] data = new byte[length];
		input.readFully(data);
		String who = new String(data, 0);
		Debug.print(name() + " received LEAVE(who = " + who + ")");
		receiveLeaveMessage(who);
	}

	/** Called when this communicator receives a leave message. */
	public abstract void receiveLeaveMessage(String who);

	//////////////////////////////////////////////////////////////////////
	// Chat messages
	// (int) length of sender
	// (String) sender
	// (String) message

	private final int sizeOfInt = 4;
 		
	/** Send a chat message to the partner. */
 	public void sendChatMessage(String sender, String message) throws IOException {
 		
 		// (+ 4 bytes for length of sender)
 		prepareForMessage(MessageTypes.CHAT_MESSAGE, sizeOfInt + sender.length() + message.length());
 		
		// Write length of sender.
		output.writeInt(sender.length());

		// Write who and message.
		output.writeBytes(sender);
		output.writeBytes(message);
 	}

 	private void chatMessage(int length) throws IOException {

		int senderLength = input.readInt();
		int messageLength = length - senderLength - sizeOfInt;
			// (4 bytes for 4 byte length of sender)

		byte[] senderData = new byte[senderLength];
		byte[] messageData = new byte[messageLength];

		// Read sender.
		input.readFully(senderData);
		String sender  = new String(senderData, 0);

		// Read message.
		input.readFully(messageData);
		String message = new String(messageData, 0);

		Debug.print(name() + " received CHAT_MESSAGE(sender = " + sender + ", message = " + message + ")");
		
		// Receive chat message.
		receiveChatMessage(sender, message);
 	}
 	
	/** Called when this communicator receives a chat message. */
 	public abstract void receiveChatMessage(String who, String message);

	//////////////////////////////////////////////////////////////////////
	// Round Items
	// (int) paperID
	// (int) round ID
	// (bytes) data
	
	/** Send a round item to the partner. */
	public void sendRoundItem(int paperID, int roundID, byte[] data) throws IOException {
		Debug.print(name() + " sending ROUND_ITEM(paperID = " + paperID + ", roundID = " + roundID + ")");
 		prepareForMessage(MessageTypes.ROUND_ITEM, (2 * sizeOfInt) + data.length);
		output.writeInt(paperID);
		output.writeInt(roundID);
		output.write(data);
	}

	private void roundItem(int length) throws IOException {
		int paperID;
		int roundID;
		byte[] data = new byte[(length - (2 * sizeOfInt))];
		paperID = input.readInt();
		roundID = input.readInt();
		input.readFully(data);
		Debug.print(name() + " received ROUND_ITEM(paperID = " + paperID + ", roundID = " + roundID + ", data = " + data + ")");
		receiveRoundItem(paperID, roundID, data);
	}
	
	/** Called when this communicator receives an roundItem. */
	public abstract void receiveRoundItem(int paperID, int roundID, byte[] data);

	//////////////////////////////////////////////////////////////////////
	// User list

	/** Send a user list to the partner. */
	public void sendUserList(Vector nameList) throws IOException {

		String userListMessage = new String();

		for (int i = 0; i != nameList.size(); i++) {
			String name = (String)nameList.get(i);
			String nameLength = new String(Conversion.intToBytes(name.length()));
			userListMessage = userListMessage.concat(nameLength);
			userListMessage = userListMessage.concat(name);
		}

		output.writeInt(MessageTypes.USER_LIST);
		output.writeInt(userListMessage.length());
		output.writeBytes(userListMessage);
	}

	private void userList(int length) throws IOException {

		// Construct a name list from the input stream.
		Vector nameList = new Vector();

		int whoLength = 0;
		byte[] whoLengthBytes = new byte[sizeOfInt];
		byte[] whoBytes;
		String who;
		
		for (int bytesRemaining = length; bytesRemaining > 0; bytesRemaining -= sizeOfInt + whoLength)
		{
			// Get the length of the name.
			input.readFully(whoLengthBytes);
			whoLength = Conversion.bytesToInt(whoLengthBytes);
	
			// Read the name.
			whoBytes = new byte[whoLength];
			input.readFully(whoBytes);
			who = new String(whoBytes, 0);

			nameList.add(who);
		}

		Debug.print(name() + " received USER_LIST(nameList = " + nameList + ")");
		
		receiveUserList(nameList);
	}

	/** Called when this communicator receives a user list. */
	public abstract void receiveUserList(Vector userList);


	//////////////////////////////////////////////////////////////////////
	// Queue
	// (int) paperID
	// (int) roundID

	/** Send a message to the partner telling it there is a queue message for this particular round item.*/
	public void sendQueueMessage(int paperID, int roundID) throws IOException {
 		prepareForMessage(MessageTypes.QUEUE, (2 * sizeOfInt));
		output.writeInt(paperID);
		output.writeInt(roundID);
	}

	private void queueMessage(int length) throws IOException {
		int paperID = input.readInt();
		int roundID = input.readInt();
		Debug.print(name() + " received QUEUE(paperID = " + paperID + ", roundID = " + roundID + ")");
		receiveQueueMessage(paperID, roundID);
	}

	/** Called when this communicator is told to begin the game. */
	public abstract void receiveQueueMessage(int paperID, int roundID);
	
	//////////////////////////////////////////////////////////////////////
	// Disconnecting

	public abstract void disconnect(String reason);
}
