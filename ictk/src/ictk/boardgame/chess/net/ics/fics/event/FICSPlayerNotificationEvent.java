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

package ictk.boardgame.chess.net.ics.fics.event;
import ictk.boardgame.chess.net.ics.event.*;

import ictk.boardgame.chess.net.ics.*;

import java.util.regex.*;
import java.io.IOException;

public class FICSPlayerNotificationEvent extends ICSPlayerNotificationEvent {
   public static final int PLAYER_NOTIFICATION_EVENT 
                = ICSEvent.PLAYER_NOTIFICATION_EVENT;
   public static final Pattern pattern;

   static {
      pattern = Pattern.compile(
                                  "(^:?Notification:\\s"
                                  + REGEX_handle
                                  + REGEX_acct_type
                                  + "\\shas (arrived|departed)."
                                  + ")"                  //end
		  , Pattern.MULTILINE);  //actually only one line
   }

   //instance/////////////////////////////////////////////////////////////
   public FICSPlayerNotificationEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSPlayerNotificationEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   /* assignMatches **********************************************************/
   /** assigns the regex matches to their appropriate fields
    */
   protected void assignMatches (Matcher m) {
      detectFake(m.group(0));
      setPlayer(m.group(2));
      try {
         accountType = new ICSAccountType(m.group(3));
      }
      catch (IOException e) {
	 System.err.println("FICSPlayerNotificationEvent"
            + " can't parse account type: "
	    + m.group(3) + " of " + m.group(0));
	 accountType = new ICSAccountType();
      }
      if ("arrived".equals(m.group(4)))
         isConnection = true;
   }

   //ICSEvent/////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;

      if ((m = matches(s)) != null)
         return new FICSPlayerNotificationEvent(server, m);
      else 
         return null;
   }

   ////////////////////////////////////////////////////////////////////////
   public String toString () {
      return "Notification: " + getPlayer() + getAccountType()
          + " has " + ((isConnection) ? "arrived" : "departed");
   }
   public String getNative () { return null; }
}
