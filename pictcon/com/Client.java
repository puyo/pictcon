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

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

import pictcon.*;
import pictcon.gamewindow.*;
import pictcon.queue.*;
import pictcon.paperwindow.*;

/** Handles sending and receiving information to and from the server.
  * <code>
  *                                                            |         
  *                                                            |         
  *                               /~~~~~~~~\                   |   /~~~~~~~~\
  *                               | Server |-----------------------| Client |
  *                              /\________/                   |   \________/
  *                             /                              |                  
  *                            /                               |                     
  *    /~~~~~~~~~~~~~~~~~~~~~\/      /~~~~~~~~\                |   /~~~~~~~~\
  *    |  Server Coordinator |-------| Server |--------------------| Client |
  *    \_____________________/\      \________/                |   \________/
  *                            \                               |               
  *                             \                              |               
  *                              \/~~~~~~~~\      /~~~~~~~~\   |
  *                               | Server |------| Client |   |
  *                               \________/      \________/   |
  *                                                            | 
  *                                                            |        
  *                                                  Local     |    Remote
  *                                                             
  * </code>
  */
public class Client extends Communicator implements ActionListener {

	private String serverIPAddress;
	private String userName;
	private Vector nameList;
	private GameWindow window;
	private ImagePanel imagePanel;
	private ChatArea chatArea;
	private InstructionArea instructionArea;
	private UserList userList;
	private SendDescriptionButton sendDescriptionButton;
	private Queue queue = new Queue();
	private QueueItem currentQueueItem;
	private boolean normalStart = false;
	private boolean waitingForRoundItem = false;
	private boolean waitingForQueueItem = true;
	private boolean readyToEndGame = false;
	private int playerPosition;
	private int papers;	
	private int rounds;
	private RoundItem[][] roundItems;
	
	public SendImageButton sendImageButton;
	
	/** Make a new client which will connect to server. */
	public Client(String serverIPAddress, String userName, GameWindow window) {
		this.serverIPAddress = serverIPAddress;
		this.userName = userName;
		this.window = window;
		this.instructionArea = window.getInstructionArea();
		this.chatArea = window.getChatArea();
		this.imagePanel = window.getImagePanel();
		this.userList = window.getUserList();
		this.sendImageButton = window.getSendImageButton();
		this.sendDescriptionButton = window.getSendDescriptionButton();		
		chatArea.addActionListener(this);
		nameList = new Vector();
		queue.setUserName(userName);

		try {
			// Connect to the server by creating a client socket.
			socket = new Socket(serverIPAddress, Constants.DEFAULT_PORT);
	
			// Create IO streams to/from the server.
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());
	
			sendImageButton.setOutputStream(output);
			sendImageButton.setClient(this);

			sendDescriptionButton.setOutputStream(output);
			sendDescriptionButton.setClient(this);

			// Send the user name to the server.
			output.writeUTF(userName);
		}
		catch (UnknownHostException e) {
			Debug.fatalError("Unknown host '" + serverIPAddress + "'.");
		}
		catch (SocketException e) {
			Debug.fatalError("Unable to connect to IP address '" + serverIPAddress + "'.");
		}
		catch (Exception e) {
			Debug.error("Error connecting to server, disconnecting: " + e);
			disconnect(e.toString());
			//Should handle this better.
			System.exit(1);
		}
	}

	//////////////////////////////////////////////////////////////////////
	// From user

	/** Handle action events. These will be when the user presses Enter after typing a chat message. */
	public void actionPerformed(ActionEvent e) {
		try {
			String chatMessage = chatArea.getMessage();
			// If the user has typed in a message - i.e. it is not a blank message.
			if (chatMessage.length() > 0) {
				// Send the chat message to the server (to send to the other users).
				sendChatMessage(userName, chatMessage);
				// Send the chat message to this client.
				chatArea.chatMessage(userName, chatMessage);
				chatArea.clearEditField();
			}
		} catch (SocketException x) {
			disconnect("Error sending chat message: " + x);			
		} catch (Exception x) {
			Debug.error("Error sending chat message: " + x);
		}
	}

	/** "Client" */
	public String name() {
		return "Client";
	}

	/** "Server" */
	public String partnerName() {
		return "Server";
	}

	//////////////////////////////////////////////////////////////////////
	// From server

	/** Set up an array to store all the roundItems (images and descriptions) received. */
	public void receiveBeginGame(int papers, int rounds){
		Debug.print("Client receiveBeginGame()");
		this.normalStart = true;
		this.papers = papers;
		this.rounds = rounds;

		// Allocate roundItems.
		roundItems = new RoundItem[papers][rounds];
		for (int i = 0; i != papers; i++) {
			roundItems[i] = new RoundItem[rounds];
		}

		// Print roundItems.
		Debug.print("Client roundItems: ");
		for (int j = 0; j != rounds; j++) {
			Debug.print("Round " + j + ": ");
			for (int i = 0; i != papers; i++){
				Debug.print(roundItems[i][j] + " ");
			}
			Debug.print("\n");
		}

		// Artificially 'receive' the first description (on local client).
		String initialString = new String("Whatever you like!");
		receiveRoundItem(playerPosition, 0, initialString.getBytes());

		// Tell the client to respond to 'Whatever you like!'.
		addQueueItem(playerPosition, 0);
	}

	/** Set up an array to store all the roundItems (images and descriptions) received. */
	public void receiveLateStart(int papers, int rounds){
		Debug.print("Client receiveLateStart()");
		if (!normalStart) {
			this.papers = papers;
			this.rounds = rounds;

			// Allocate roundItems.
			roundItems = new RoundItem[papers][rounds];
			for (int i = 0; i != papers; i++) {
				roundItems[i] = new RoundItem[rounds];
			}
	
			// Print roundItems.
			Debug.print("Client roundItems: ");
			for (int j = 0; j != rounds; j++) {
				Debug.print("Round " + j + ": ");
				for (int i = 0; i != papers; i++){
					Debug.print(roundItems[i][j] + " ");
				}
				Debug.print("\n");
			}
			waitingForRoundItem = true;
		}
	}
	
	/** When there's a new user, adds them to the list of players. */
	public void receiveEnterMessage(String who) {
		// Add the new player to the user list.
		nameList.add(who);
		userList.update(nameList);
		updatePlayerPosition();
		// Notify players via the chat area.
		chatArea.enterMessage(who);
	}

	/** When someone exits, removes them from the list of players. */
	public void receiveLeaveMessage(String who) {
		// Remove the player from the user list.
		nameList.remove(who);
		userList.update(nameList);
		updatePlayerPosition();

		// Notify players via the chat area.
		chatArea.leaveMessage(who);
	}

	/** Chat messages. */
	public void receiveChatMessage(String who, String message) {
		// Don't print the message if it comes from the local user.
		if (!who.equals(userName)) {
			chatArea.chatMessage(who, message);
		}
	}

	/** Receives an incoming round item from the Server and stores it. 
	  */
	public void receiveRoundItem(int paperID, int roundID, byte[] data) {
		Debug.print("received paperID = " + paperID + ", roundID = " + roundID + ".");
		RoundItem item = new RoundItem(paperID, roundID, data);
		roundItems[paperID][roundID] = item;
		checkIfLastItem(paperID);
		checkIfWaitingForRoundItem(paperID, roundID);			
	}



	/** Receives an incoming user list from the Server and updates the list of players. */
	public void receiveUserList(Vector nameList) {
		this.nameList = nameList;
		updatePlayerPosition();
	}
	
	/** Receives an incoming queue message from the Server and adds the queue item to the queue. */
	public void receiveQueueMessage(int paperID, int roundID){
		addQueueItem(paperID, roundID);
		/*
		if (waitingForQueueItem) {
			nextQueueItem();
		}
		*/
	}
	
	/** Check to see if the player is waiting to receive this round item.*/
	public void checkIfWaitingForRoundItem(int paperID, int roundID) {
		if (waitingForRoundItem) {
			if (currentQueueItem != null) {
				if ((currentQueueItem.getPaperID() == paperID) && (currentQueueItem.getRoundID() == roundID) && (waitingForQueueItem == false)) {	
					//Debug.print("roundItems[c.Paper()][c.Round()] " + roundItems[currentQueueItem.getPaperID()][currentQueueItem.getRoundID()]);
					waitingForRoundItem = false;
					displayCurrentQueueItem();
				}
			}
		}
	}

	/** Check if this is the last item for this list. */
	public void checkIfLastItem(int paperID) {
		boolean allItemsPresent = true;
		for (int i = 1; i < rounds; i++) {
			if (roundItems[paperID][i] == null) {
				allItemsPresent = false;
			}
		}	
		if (allItemsPresent == true) {
			PaperWindow paper = new PaperWindow(paperID, rounds, this);
			paper.show();
			checkIfLastPaper();
		}
	}

	/** Check if this is the last paper. */
	public void checkIfLastPaper() {
		boolean allItemsPresent = true;
		for (int i = 1; i < rounds; i++) {
			for (int j = 0; j < papers; j++) {
				if (roundItems[j][i] == null) {
					allItemsPresent = false;
				}
			}
		}	
		if (allItemsPresent == true) {
			readyToEndGame = true;
		}
	}

	/** Sets the game window to its describe phase. */
	public void setDescribePhase(RoundItem image) {
		instructionArea.setDescribeInstructions();
		imagePanel.setImage(image);
		imagePanel.setEditable(false);
		window.setDescribePhase();		
	}

	/** Sets the game window to its draw phase. */
	public void setDrawPhase(String description) {
		instructionArea.setDrawInstructions(description);
		imagePanel.clear();
		imagePanel.setEditable(true);
		window.setDrawPhase();
	}

	/** There is no more input for this user to complete. Wait for other players to finish their input. */
	public void waitForEndGame() {
		instructionArea.setWaitForEndGame();
		imagePanel.clear();
		imagePanel.setEditable(false);
//		window.setDrawPhase();
	}

	/** All papers have been received. The game is finished. */
	public void endGame() {
		instructionArea.endGame();
	}

	/* Make the next queue item the current queue item.
	 * If there are no items in the queue set the variable waitingForQueueItem to true.
	 * If there is an item in the queue, attempt to display it.
	 */
	public void nextQueueItem() {
		Debug.print("Client nextQueueItem()");
		currentQueueItem = queue.nextItem();
		if (currentQueueItem == null) {
			waitingForQueueItem = true;
		} else {
			waitingForQueueItem = false;
		}			
		if (!waitingForQueueItem) {
			displayCurrentQueueItem();
		}
	}
	
	public void addQueueItem(int paperID, int roundID) {
		//Debug.print("Client addQueueItem(" + paperID + ", " + roundID + ")");		
		queue.addItem(paperID, roundID);
		if (waitingForQueueItem) {
			waitingForQueueItem = false;
			nextQueueItem();
		}
	}

	/** Get the roundItem */
	public RoundItem getRoundItem(int paperID, int roundID) {
		Debug.print("Client getRoundItem(" + paperID + ", " + roundID + ")");
		return (RoundItem)roundItems[paperID][roundID];
	}

	/** Get the current round. */
	public int getCurrentItemRoundID() {
		return currentQueueItem.getRoundID();
	}

	/** Get the current paper. */
	public int getCurrentItemPaperID() {
		return currentQueueItem.getPaperID();
	}

	/** Get the list of users. */
	public UserList getUserList() {
		return userList;
	}

	/** Updates the player's current position in the game circle. */
	public void updatePlayerPosition() {
		for (int i = 0; i != nameList.size(); i++) {
			playerPosition = i;
			if(userName.equals(nameList.get(i))) {
				break;
			}
		}
		Debug.print("Client: Player position of " + userName + " is now " + playerPosition);
	}
	
	/** Displays the item next in the queue to be displyed. */
	private void displayCurrentQueueItem() {
		//Debug.print("displayCurrentQueueItem()");		
		if (currentQueueItem == null) {
			waitingForQueueItem = true;
		} else {
			waitingForQueueItem = false;
		}

		if (roundItems[currentQueueItem.getPaperID()][currentQueueItem.getRoundID()] == null) {
			waitingForRoundItem = true;
		} else {
			waitingForRoundItem = false;
		}

		if ((waitingForQueueItem == false) && (waitingForRoundItem == false)) {
			RoundItem item = roundItems[currentQueueItem.getPaperID()][currentQueueItem.getRoundID()];
			Debug.print("Client display: Round Item (paperID = " + item.getPaperID() + ", roundID = " + item.getRoundID() + ").");
			if ((item.getRoundID()) < (rounds - 1)){
				if (item.isImage()) {
					setDescribePhase(item);
				} else if (item.isDescription()) {
					byte[] descriptionBytes = item.getData();
					String description = new String(descriptionBytes);
					Debug.print("description = '" + description + "'.");
					setDrawPhase(description);
				}
			} else if (!readyToEndGame) {
				waitForEndGame();
			} else {
				endGame();
			}
		}
	}

	/** Writes a notification to standard output when being disconnected. */
	public void disconnect(String reason) {
		try {
			instructionArea.setInstructions("Disconnecting from server: " + reason + "\n Please restart this application.");
			System.out.println("Disconnecting from server: " + reason);
			socket.close();
		} catch (Exception e) {
			Debug.print(name() + " could not close connection to " + partnerName() + ": " + e);
		}
	}
}
