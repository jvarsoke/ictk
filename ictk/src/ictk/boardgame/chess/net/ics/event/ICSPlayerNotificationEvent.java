/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id$
 *
 *  This file is part of ICTK.
 *
 *  ICTK is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  ICTK is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ICTK; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ictk.boardgame.chess.net.ics.event;
import ictk.boardgame.chess.net.ics.*;

import java.util.regex.*;
import java.io.IOException;

public class ICSPlayerNotificationEvent extends ICSEvent {
   protected static final int PLAYER_NOTIFICATION_EVENT 
                = ICSEvent.PLAYER_NOTIFICATION_EVENT,
		              PLAYER_CONNECTION_EVENT
	        = ICSEvent.PLAYER_CONNECTION_EVENT;

   //instance/////////////////////////////////////////////////////////////
   protected boolean isConnected,
                     isNotification;
   protected String player;
   protected ICSAccountType accountType;

   public ICSPlayerNotificationEvent () {
      super(PLAYER_NOTIFICATION_EVENT);
   }

   //getters and setters//////////////////////////////////////////////////////
   public boolean isConnected () { return isConnected; }
   public void setConnected (boolean t) { isConnected = t; }

   public boolean isOnNotificationList () { return isNotification; }
   public void setOnNotificationList (boolean t) { isNotification = t; }

   public String getPlayer () { return player; }
   public void setPlayer (String handle) { player = handle; }

   public ICSAccountType getAccountType () { return accountType; }
   public void setAccountType (ICSAccountType account) {
       accountType = account;
   }

   public String getReadable () {
      StringBuffer sb = new StringBuffer(40);

      if (isNotification) {
	 sb.append("Notification: ")
	   .append(getPlayer())
	   .append(getAccountType())
	   .append(" has ")
	   .append(((isConnected) ? "arrived" : "departed"));
      }
      else {
	sb.append("[")
	   .append(getPlayer())
	   .append(" has ");
	 if (isConnected)
	    sb.append("connected");
	 else
	    sb.append("disconnected");
	 sb.append(".]");
      }

      return sb.toString();
   }
}
