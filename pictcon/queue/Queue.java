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

package pictcon.queue;
import pictcon.*;

import java.util.Vector;
import java.util.Enumeration;

public class Queue extends Vector {

	private String userName;
	private QueueItem currentItem;
		
	public Queue() {
		super();
	}
	
	/** Deletes current queue item, then makes the item in the queue
	with the earliest round number the new current queue item. */
	public QueueItem nextItem() {
		if (size() == 0) {
			return null;
		} else {
			currentItem = (QueueItem)get(0);
			Enumeration e = elements();
	
			// Enumerate through all the queue items and make the one
			// with the earliest round the currentItem.
			while (e.hasMoreElements()) {
				QueueItem item = (QueueItem)e.nextElement();
				//Debug.print("item: (" + item.getPaperID() + ", " + item.getRoundID() + ")");
				if (item.getRoundID() < currentItem.getRoundID()) {
					currentItem = item;
				}
			}
			//Debug.print("Queue CurrentItem: (" + currentItem.getPaperID() + ", " + currentItem.getRoundID() + ")");
			subtractCurrentItem();
			return currentItem;		
		}			
	}

	public void addItem(int paperID, int roundID) {
		add(new QueueItem(paperID, roundID));
//		Debug.print("Add queue item (paperID = " + paperID + ", roundID = " + roundID + ") to " + userName + "'s queue.");
	}

	public void add(Queue queue) {
		Enumeration e = queue.elements();
		// Enumerate through all the new queue items and add each to the current queue.
		while (e.hasMoreElements()) {
			QueueItem newItem = (QueueItem)e.nextElement();
			add(newItem);
		}
	}

	public void subtractItem(int paperID, int roundID) {
		Enumeration e = elements();
		// Enumerate through all the queue items and delete appropriate one.
		while (e.hasMoreElements()) {
			QueueItem item = (QueueItem)e.nextElement();
			if ((paperID == item.getPaperID()) &&  (roundID == item.getRoundID())) {
				remove(item);
			}
		}
//		Debug.print("Subtract queue item (paperID = " + paperID + ", roundID = " + roundID + ") from " + userName + "'s queue.");
	}

	/* Removes the current item from the queue. */
	private void subtractCurrentItem() {
//		Debug.print("Subtract queue item (paperID = " + currentItem.getPaperID() + ", roundID = " + currentItem.getRoundID() + ") from " + userName + "'s queue.");
		remove(currentItem);
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}

}
