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


public class FICSKibitzEventParser extends ICSEventParser {
   //static/////////////////////////////////////////////////////////////////
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^:?("
//<template function=regex>
	 + REGEX_handle
	 + REGEX_acct_type
	 + "(" 
	 + REGEX_rating
	 + ")?"
	 + "\\[(\\d+)\\]"        //game number
	 + "\\s(kibitzes|whispers|says):\\s"
	 + "((.|\\s+\\\\)*)"     //message
//</template>
	 + ")"
         , Pattern.MULTILINE);
   }

   //instance///////////////////////////////////////////////////////////////
   public FICSKibitzEventParser () {
      super(masterPattern);
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSKibitzEvent();
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSKibitzEvent evt = (ICSKibitzEvent) event;

      evt.setFake(detectFake(m.group(0)));

//<template function=assignMatches>
      evt.setPlayer(m.group(2));

      evt.setAccountType(parseAccountType(m, 3));

      if (m.group(4) != null)
         evt.setRating(parseRating(m, 5));

      try {
         evt.setBoardNumber(Integer.parseInt(m.group(6)));
      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING,
	    "Can't parse board number for: "
            + m.group(6) + " of " + m.group(0));
	 evt.setEventType(ICSEvent.UNKNOWN_EVENT);
	 evt.setMessage(m.group(0));
	 return;
      }

      if ("whispers".equals(m.group(7))) {
         evt.setEventType(ICSEvent.WHISPER_EVENT);
      }
      else if ("says".equals(m.group(7))) {
         evt.setEventType(ICSEvent.BOARD_SAY_EVENT);
      }

      evt.setMessage(m.group(8));
//</template>
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSKibitzEvent evt = (ICSKibitzEvent) event;
      StringBuffer sb = new StringBuffer(20);

      if (evt.isFake()) sb.append(":");
//<template function=toNative>
      sb.append(evt.getPlayer())
        .append(evt.getAccountType());

      if (evt.getEventType() != ICSEvent.BOARD_SAY_EVENT)
         sb.append("(")
	   .append(evt.getRating())
	   .append(")");

      sb.append("[")
        .append(evt.getBoardNumber())
	.append("]");

      switch (evt.getEventType()) {
         case ICSEvent.KIBITZ_EVENT:
	    sb.append(" kibitzes: ");
	    break;

         case ICSEvent.WHISPER_EVENT:
	    sb.append(" whispers: ");
	    break;

         case ICSEvent.BOARD_SAY_EVENT:
	    sb.append(" says: ");
	    break;
      }

      sb.append(evt.getMessage());

      return sb.toString();
//</template>
   }
}


