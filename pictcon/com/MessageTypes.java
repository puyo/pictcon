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

/** Contains constants to identify each type of message sent between the server and client.
  */
public abstract class MessageTypes
{
	/** Begin game.
	  * An begin game message has the format: length, number of papers, number of rounds.
	  */
	public static final int BEGIN = 1;

	/** Enter game.
	  * An enter message has the format: length, user name.
	  */
	public static final int ENTER = 2;

	/** Leave game.
	  * A leave message has the format: length, user name.
	  */
	public static final int LEAVE = 3;

	/** A chat message.
	  * A chat message has the format: length of entire message, length of user name, user name.
	  */
	public static final int CHAT_MESSAGE = 4;

	/** Either an image or a description.
	  * A round item message has the format: length of message, an int for the playerID,
	  * an int for the roundID, the actual description or deflated image.
	  */
	public static final int ROUND_ITEM = 5;

	/** A player has started later than the others.
	  * Let them join in the middle of the game.
	  */

	public static final int LATE_START = 6;

	/** A list containing all the current players in order.
	  * A UserList message has the format: length of entire message, number of users, length of user 
	  * name, user name, length of user name, user name, etc.
	  */

	public static final int USER_LIST = 7;

	/** Queue Message.
	  * A queue message has the format: length, paperID, roundID.
	  */
	public static final int QUEUE = 8;

//	TODO some other day.
//	public static final int CHANGE_NAME = 6;
//	public static final int ACTION = 7;
}
