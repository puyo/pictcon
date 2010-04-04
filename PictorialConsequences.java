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

import pictcon.*;
import pictcon.dialogs.*;
import pictcon.com.*;
import pictcon.gamewindow.*;
import pictcon.queue.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;
import java.net.*;
import java.util.*;

// ---------------------------------------------------------------------------
// PictorialConsequences Client
// ---------------------------------------------------------------------------

public class PictorialConsequences {

	/** Server IP address or host name. */
	private static String serverAddress;

	private static String userName;

	private static Socket socket = null;
	private static InputStream in = null;
	private static OutputStream out = null;

	private static ServerCoordinator serverCoordinator = null;
	private static Client client = null;

	private static GameWindow window;

	private static boolean runHost = false;
	
	/** Pictorial Consequences execution starts here. */
	public static void main(String args[]) {
		
		String confFileName = "PictorialConsequences.cfg";
		
		// Load configuration from file.
		try {
			Properties conf = new Properties();
			FileInputStream confFile = new FileInputStream(confFileName);
			conf.load(confFile);
			serverAddress = conf.getProperty("serverIPAddress", "localhost");
		} catch (Exception e) {
			Debug.print("Could not load configuration file '" + confFileName + "': " + e);
		}
		
		// Display a setup screen with options such as "Host" and "Join".
		// Store the result in the boolean runHost.
		setupScreen();
 		nameScreen();
		// Display a host screen or a join screen depending on what was chosen.
		if (runHost) {
			// Run the serverCoordinator from the local machine.
			serverAddress = "localhost";
			// Create the server coordinator.
			serverCoordinator = new ServerCoordinator(userName, Constants.DEFAULT_PORT);
			serverCoordinator.start();
			// Hosting.
			hostScreen();
		} else {
			// Joining, connect to the server.
			joinScreen();
		}
		
		window = new GameWindow();

		client = new Client(serverAddress, userName, window);
		client.start();
		
		if (runHost) {
			serverCoordinator.setReadyToBegin(true);
		}
	}

	// DIALOG BOXES //////////////////////////////////////////////////////

	/** Display a setup screen, so the user can choose 'host' or 'join'. */
	private static void setupScreen() {
		TitleScreen s = new TitleScreen(window);
		s.show();
		if (s.getChoice() == TitleScreen.HOST) {
			runHost = true;
		}
	}

	/** Display a screen which prompts the user for the name they wish to be known by. */
	private static void nameScreen() {
		NameScreen s = new NameScreen(window);
		s.show();
		userName = s.getName();
	}
	
	/** Display the host setup screen. */
	private static void hostScreen() {
		HostScreen s = new HostScreen(window, serverCoordinator);
		s.show();
	}

	/** Display the client join screen. */
	private static void joinScreen() {
		JoinScreen s = new JoinScreen(window, serverAddress);
		s.show();
		serverAddress = s.getAddress();
	}
}


