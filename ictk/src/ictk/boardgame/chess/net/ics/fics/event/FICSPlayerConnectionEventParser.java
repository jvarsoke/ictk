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
import ictk.util.Log;

import java.util.regex.*;
import java.io.IOException;


public class FICSPlayerConnectionEventParser extends ICSEventParser {
   //static/////////////////////////////////////////////////////////////////
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^:?(" //begin
//<template function=regex>
         + "\\["
	 + REGEX_handle               //handle
	 + "\\shas\\s"
	 + "(connected|disconnected)" //come/go
	 + "\\.\\]"
//</template>
         + ")"  //end
         , Pattern.MULTILINE);
   }

   //instance///////////////////////////////////////////////////////////////
   public FICSPlayerConnectionEventParser () {
      super(masterPattern);
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSPlayerNotificationEvent();
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSPlayerNotificationEvent evt = (ICSPlayerNotificationEvent) event;

      evt.setFake(detectFake(m.group(0)));
//<template function=assignMatches>
      evt.setEventType(ICSEvent.PLAYER_CONNECTION_EVENT);
      evt.setPlayer(m.group(2));
      if ("connected".equals(m.group(3)))
         evt.setConnected(true);
//</template>
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSPlayerNotificationEvent evt = (ICSPlayerNotificationEvent) event;
      StringBuffer sb = new StringBuffer(20);

      if (evt.isFake()) sb.append(":");
//<template function=toNative>
      sb.append("[")
        .append(evt.getPlayer())
        .append(" has ");
      if (evt.isConnected())
         sb.append("connected");
      else
         sb.append("disconnected");
      sb.append(".]");
//</template>

      return sb.toString();
   }
}


