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

/**
 * This cooresponds to Kibitz, Whisper, and Board Say messages. The   
 * EventType tells which one it is. this is a really long description 
 * and I'm hoping to see the text wrap so I can test the new function 
 * I got from the book with all the new features this should look     
 * really good.                                                       
 */
public class FICSKibitzEventParser extends ICSEventParser {

   //static/////////////////////////////////////////////////////////////////
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^:?(" //begin
         + "([\\w]+)"
         + "((?:\\([A-Z*]+\\))*)"
         + "\\(\\s*([0-9+-]+[EP]?)\\)"
         + "\\[(\\d+)\\]"
         + "\\s(kibitzes|whispers|says):\\s"
         + "((.|\\s+\\\\|\\s+:)*)"

         + ")"  //end
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
      
      evt.setPlayer(m.group(2));
      
      evt.setAccountType(parseICSAccountType(m, 3));
      
      evt.setRating(parseICSRating(m, 5));
      
      try {
         evt.setBoardNumber(Integer.parseInt(m.group(6)));
      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING,
            "Can't parse boardNumber for: "
            + m.group(6) 
            + " of " + m.group(0));
         evt.setEventType(ICSEvent.UNKNOWN_EVENT);
         evt.setMessage(m.group(0));
         return;
      }
      
      evt.setMessage(m.group(8));
      
      if ("whispers".equals(m.group(7))) {
         evt.setEventType(ICSEvent.WHISPER_EVENT);
      }
      else if ("says".equals(m.group(7))) {
         evt.setEventType(ICSEvent.BOARD_SAY_EVENT);
      }
	    
   }

   /* toNative ***************************************************************/
   public static String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSKibitzEvent evt = (ICSKibitzEvent) event;
      StringBuffer sb = new StringBuffer(20);
      
      if (evt.isFake()) sb.append(":");
      
      sb.append(evt.getPlayer())
        .append(evt.getAccountType());

      if (evt.getEventType() != ICSEvent.BOARD_SAY_EVENT)
         sb.apped("(")
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
   }
}
