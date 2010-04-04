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
import java.net.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.*;

/** Coordinates activities of servers.
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
public class ServerCoordinator extends Thread implements Runnable {

	/** A table that maps a server's client's user name to a Server object. */
	private Vector servers;
	
	/** Allows for players to be accepted. */
	private boolean acceptMorePlayers = true;	
	
	/** A java ServerSocket to allow client to connect. */
	private ServerSocket serverSocket;

	/** Place to display information about players joining. */
	private JTextArea message;

	/** So we know when the host wants to begin. */	
	private boolean readyToBegin = false;

	/** Set when the host joins. 
	  * If a game has begun and a new server joins then we should send them a 'begin game message'.
	  */	
	private boolean gameHasBegun = false;
	
	/** Used to start the server threads. */
	private boolean serverAdded = false;
	
	/** The user name of the host client. */	
	private String hostUserName;	

	/** Used to know how many 'pieces of paper' to create at the beginning of the game.*/
	private int numberOfStartingPlayers;

	/** Used to know how many rounds to allow space for on each 'piece of paper'.*/
	private int numberOfRounds;
	
	/** Used for storing round items.*/
	private RoundItem[][] roundItems;
	
	/** Creates a Server Coordinator at the given port. */
	public ServerCoordinator(String hostUserName, int port) {
		this.hostUserName = hostUserName;
		servers = new Vector();
		try {
			serverSocket = new ServerSocket(port);
		} catch(IOException e){
			Debug.error("Error connecting: " + e);
		}
	}

	/** The current number of server/client pairs. */
	public int getNumberOfUsers() {
		return servers.size();
	}

	/** Listens for clients and makes servers for them. */
	public void run() {
		Server newServer = null;
		while (acceptMorePlayers) {
			try {
				// Reset variable for this iteration
				serverAdded = false;
				
				// Accept a new connection.
				Socket newConn = serverSocket.accept();

				// Create a server to handle the connection.
				newServer = new Server(this, newConn);
				
				// Add the new server to the list.
				addServer(newServer.getUserName(), newServer);


				// Run the server.
				newServer.start();

				// If the game has begun and you are not the host, start the game late.
				if (gameHasBegun && !(newServer.getUserName().equalsIgnoreCase(hostUserName))) {
					newServer.receiveLateStart(numberOfStartingPlayers, numberOfRounds);
				}
				
				String m = "\n" + newServer.getUserName() + " joined. " + getNumberOfUsers() + " player(s) now in the game.";
				message.append(m);
				Debug.print(m);
			}
			catch (Exception e) { 
				Debug.error("Error connecting new user: " + e);
				e.printStackTrace();
				newServer.disconnect(e.toString());
			}
		}
	}

	/** Adds a new server to the system and tells the other servers about it.
	  */
	public synchronized void addServer(String userName, Server server) throws Exception {

		Enumeration serversEnum = servers.elements();
		boolean nameInUse = false;

		// Enumerate through all the already present servers to check if the new server has picked a name already in use.
		while (serversEnum.hasMoreElements() && !nameInUse) {
			Server tempServer = (Server)serversEnum.nextElement();
			if (userName.equalsIgnoreCase(tempServer.getUserName())){
				nameInUse = true;
			}
		}	

		// If the server picks a name that is already here, disconnect the new server, let the old one keep its name.
		if (nameInUse) {
			throw new Exception("Name '" + userName + "' already in use.");
		}

		// Give the server a list of all the other users.
		server.sendUserList(getUserList());
		
		// Add the new server to the table.
		servers.add(server);
		
		// Tell the other servers about this new server.
		broadcastEnterMessage(userName);
		
		if ((userName.equalsIgnoreCase(hostUserName)) && (readyToBegin) && (!gameHasBegun)) {
			System.out.println("Host is joined. Beginning game...");
			beginGame();
		}
	}

	/** Removes a server (by name) from the system and tells the other servers about it. */
	public synchronized void removeServer(String name) {
		int i = 0;
		while (i < servers.size()) {
			Server server = (Server)servers.get(i);
			if (name.equals(server.getUserName())){
				
				// Reassign workload.
				server.passWorkloadToNextUser();
				
				// Remove the server.
				servers.remove(i);
				broadcastLeaveMessage(name);
				break;
			}
			i++;	
		}			
	}

	/** Removes a server from the system and tells the other servers about it. */
	public synchronized void removeServer(Server server) {

		// We remove by Server, not by name. We have to enumerate through
		// all the servers to find out the name of this server.
		int i = 0;
		while (i < servers.size()) {
			if (server.equals((Server)servers.get(i))){
				String name = server.getUserName();

				// Reassign workload.
				server.passWorkloadToNextUser();

				servers.remove(i);
				broadcastLeaveMessage(name);
				break;
			}
			i++;
		}
	}

	/** Called by a Server object to broadcast a chat message to the other Server objects. */
	public synchronized void broadcastChatMessage(String name, String message) {
		Enumeration serversEnum = servers.elements();

		// Enumerate through all the servers and send them the chat message
		// Note that this will send a message back to the original sender, too.
		Server server = null;
		while (serversEnum.hasMoreElements()) {
			try {
				server = (Server)serversEnum.nextElement();
				server.sendChatMessage(name, message);
			}
			catch (IOException e) {
				Debug.error("Could not send chat message to server [" + server.getUserName() + "]: " + e);
				removeServer(server);
			}
		}
	}

	/** Tells all the servers when a new server has arrived. */
	public synchronized void broadcastEnterMessage(String name) {
		Enumeration e = servers.elements();

		// Enumerate through all the servers and tell them about the new server.
		while (e.hasMoreElements()) {
			Server server = (Server)e.nextElement();
			server.receiveEnterMessage(name);
		}
	}

	/** Tells all the servers that a server has left. */
	public synchronized void broadcastLeaveMessage(String name) {
		Enumeration e = servers.elements();

		// Enumerate through all the servers and tell them who left.
		while (e.hasMoreElements()) {
			Server server = (Server)e.nextElement();
			server.receiveLeaveMessage(name);
		}
	}


	public synchronized Server getServer(String userName) {
		Server server;
		for (int i = 0; i != servers.size(); i++) {
			server = (Server)servers.get(i);
			if (server.getUserName() == userName) {
				return server;
			}
		}
		return null;
	}

	public synchronized Server getServer(int i) {
		return (Server)servers.get(i);
	}

	public synchronized int getIndexOfServer(Server s) {
		return servers.indexOf(s);
	}

	public synchronized void queueRoundItemForNextUser(Server user, int paperID, int roundID) {
		user.nextServer().queueRoundItem(paperID, roundID);
	}
		
	public synchronized void sendRoundItemToOtherUsers(String sender, int paperID, int roundID, byte[] data) {
		Debug.print("sendRoundItemToOtherUsers();");
		Server server = null;
		for (int i = 0; i != servers.size(); i++) {
			server = (Server)servers.get(i);
			if (server.getUserName() != sender){
				try {
					// Send the round item to the next server in the list.
					server = (Server)servers.get(i);
					server.sendRoundItem(paperID, roundID, data);
				}
				catch (IOException e) {
					Debug.error("Could not send round item to server [" + server.getUserName() + "]: " + e);
					removeServer(server);
				}
			}
		}
	}

	public synchronized RoundItem getRoundItem(int paperID, int roundID) {
		return roundItems[paperID][roundID];
	}

	public synchronized void setRoundItem(int paperID, int roundID, RoundItem newRoundItem) {
		roundItems[paperID][roundID] = newRoundItem;
	}
	
	public synchronized void sendRoundItem(String who, int paperID, int roundID) {
		RoundItem item = roundItems[paperID][roundID];
		Server server = null;
		for (int i = 0; i != servers.size(); i++) {
			server = (Server)servers.get(i);
			if (server.getUserName() == who){
				try {
					// Send the round item to the desired user.
					server = (Server)servers.get(i);
					server.sendRoundItem(paperID, roundID, item.getData());
				}
				catch (IOException e) {
					Debug.error("Could not send round item to server [" + server.getUserName() + "]: " + e);
					removeServer(server);
				}
			}
		}
	}

	/** A list of all the users on the system. */
	public synchronized Vector getUserList() {
		Vector result = new Vector();
		for (int i = 0; i != servers.size(); i++) {
			result.add(((Server)servers.get(i)).getUserName());
		}
		return result;
	}
	
	public void setOutput(JTextArea message) {
		this.message = message;
	}

	public void setNumberOfRounds(int numberOfRounds) {
		this.numberOfRounds = numberOfRounds;
	}
	
	/** Used by the client to signal that they wish to begin the game. */
	public void setReadyToBegin(boolean readyToBegin){
		this.readyToBegin = readyToBegin;
	}
	
	/** Called when the host client is added to the vector of servers and the host client has signalled it is ready to begin.*/
	public synchronized void beginGame() {

		// Get the number of starting players.
		numberOfStartingPlayers = servers.size();
		
		// Make an array for round items (images and descriptions).
		roundItems = new RoundItem[numberOfStartingPlayers][numberOfRounds];
		
		// Initialise the elements of roundItems[][].
		for(int i = 0; i != numberOfStartingPlayers; i++){
			for(int j = 0; j != numberOfRounds; j++) {
				roundItems[i][j] = null;
			}
		}

		// Enumerate through all the servers and tell them to begin.
		Enumeration e = servers.elements();
		while (e.hasMoreElements()) {
			Server server = (Server)e.nextElement();
			server.receiveBeginGame(numberOfStartingPlayers, numberOfRounds);
			Debug.print("Server " + server.getUserName() + " receives 'begin game' message.");
		}	
		gameHasBegun = true;
	}
}
