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
import pictcon.queue.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.Vector;

/** Handles sending and receiving information to and from the client and also communicates via the Server Coordinator to other Servers.
  *
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
  */
public class Server extends Communicator {

	/** The server coordinator. */
	private ServerCoordinator serverCoordinator;

	/** The name the user wants to be known by. */
	private String userName;

	private boolean[][] roundItems;

	private Queue workload;

	/** Creates a Server. */
	public Server(ServerCoordinator serverCoordinator, Socket clientSocket) throws IOException {

		this.serverCoordinator = serverCoordinator;
		this.socket = clientSocket;

		// Get data streams to the socket.
		input = new DataInputStream(clientSocket.getInputStream());
		output = new DataOutputStream(clientSocket.getOutputStream());

		// Read the first thing that the user sends as the name they want to use.
		userName = input.readUTF();
		
		workload = new Queue();
		workload.setUserName(userName);
		Debug.print("Creating a workload queue for " + userName + ".");
	}
    
	/** "Server" */
	public String name() {
		return "Server";
	}

	/** "Client" */
	public String partnerName() {
		return "Client";
	}
	/** The user name of the player. */
	public String getUserName() {
		return userName;
	}
	
	/** Set up an array to keep track of which roundItems the client has and which ones it still needs. */
	public void receiveBeginGame(int papers, int rounds){
		roundItems = new boolean[papers][rounds];
		// Initialise the array to set all values false.
		for (int i = 0; i != papers; i++){
			for (int j = 0; j != rounds; j++) {
				roundItems[i][j] = false;
			}
		}
		try {
			sendBeginGame(papers, rounds);
		} catch (Exception e) {
			serverCoordinator.removeServer(this);
		}
	}

	/** Set up an array to keep track of which roundItems the client has and which ones it still needs. 
	  * Send the client all the round items currently available.
	  */
	public void receiveLateStart(int papers, int rounds){
		Debug.print(userName + ": 'late start' message.");		
		roundItems = new boolean[papers][rounds];
		try {
			sendLateStart(papers, rounds);
			RoundItem item;
			for (int i = 0; i != papers; i++){
				for (int j = 0; j != rounds; j++) {
					item = serverCoordinator.getRoundItem(i,j);
					if (item == null) {						
						roundItems[i][j] = false;
					} else {
						serverCoordinator.sendRoundItem(userName, item.getPaperID(), item.getRoundID());
						Debug.print(userName + ": sending(paper = " + item.getPaperID() + ", round = " +item.getRoundID() + ").");		
						roundItems[i][j] = true;
					}	
				}
			}
		} catch (Exception e) {
			Debug.print(userName + ": exception while starting late. " + e);					
			serverCoordinator.removeServer(this);
		}
	}

	/** Called by the server coordinator whenever a user enters.
	  * The data part of the message is just the name of the user who has
	  * entered.
	  * @see MessageTypes
	  */
	public void receiveEnterMessage(String who) {
		// Pass on message to the client.
		try {
			sendEnterMessage(who);
		} catch (Exception e) {
			serverCoordinator.removeServer(this);
		}
	}

	/** Called by the server coordinator whenever a user leaves.
	  * The data part of the message is just the name of the user who has
	  * left.
	  * @see MessageTypes
	  */
	public void receiveLeaveMessage(String who) {
		// Pass on message to the client.
		try {
			sendLeaveMessage(who);
		} catch (Exception e) {
			serverCoordinator.removeServer(this);
		}
	}

	/** Reads an incoming chat message from the user and sends it to the server coordinator.
	  */
 	public void receiveChatMessage(String who, String message) {
		serverCoordinator.broadcastChatMessage(who, message);
	}

	/** Receives an incoming roundItem from the client and passes it on to the Server Coordinator.
	 */
	public void receiveRoundItem(int paperID, int roundID, byte[] data) {
		// Store a copy with the server coordinator.
		serverCoordinator.setRoundItem(paperID, roundID, new RoundItem(paperID, roundID, data));

		// Debugging Info.
		if ((roundID % 2) == 0) {
			String description = new String(data);
			Debug.print("Server: description = '" + description + "'.");
		}

		// Tell the server coordinator to send this roundItem to everyone else.
		serverCoordinator.sendRoundItemToOtherUsers(userName, paperID, roundID, data);

		// Tell the next server to add this roundItem to its queue.
		nextServer().queueRoundItem(paperID, roundID);

		// Remove this item from our queue because we did it!
		receiveQueueMessage(paperID, roundID);
	}
	
	/** N/A */
	public void receiveUserList(Vector userList) {
	}

	/** Remove the preceeding item to this one from our queue because we did it!
	  */
	public void receiveQueueMessage(int paperID, int roundID){
		removeQueueItem(paperID, roundID - 1);
	}

	public void disconnect(String reason) {
		try {
			socket.close();
			serverCoordinator.removeServer(this);
		} catch (Exception e) {
			Debug.error(name() + " could not close connection to " + partnerName() + ": " + e);
		}
	}

	public void queueRoundItem(int paperID, int roundID) {
		//Debug.print("Server queueRoundItem(paperID = " + paperID + ", roundID = " + roundID + ")");
		try {
			// Tell the server coordinator.
			addQueueItem(paperID, roundID);

			// Tell the client.
			sendQueueMessage(paperID, roundID);
		} catch (Exception e) {
			Debug.error("Server exception while attempting to queue round item " + paperID + ", roundID = " + roundID + "): " + e.toString());
			e.printStackTrace();
			System.exit(1);
			nextServer().queueRoundItem(paperID, roundID);
			disconnect("Could not find client while queueing round item.");
		}
	}

	public Server nextServer() {
		int serverIndex = serverCoordinator.getIndexOfServer(this);
		int nextServerIndex = (serverIndex + 1) % serverCoordinator.getNumberOfUsers();
		return serverCoordinator.getServer(nextServerIndex);
	}

	public void passWorkloadToNextUser() {
		// Add each item in our workload to the next server.
		Server nextUser = nextServer();
		for (int i = 0; i != workload.size(); ++i) {
			QueueItem item = (QueueItem)workload.get(i);
			nextUser.queueRoundItem(item.getPaperID(), item.getRoundID());
		}
	}		

	public void addQueueItem(int paperID, int roundID) {
		workload.addItem(paperID, roundID);
	}

	public void removeQueueItem(int paperID, int roundID) {
		workload.subtractItem(paperID, roundID);
	}
}
