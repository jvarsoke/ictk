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

public class FICSGameNotificationEvent extends ICSGameNotificationEvent {
   public static final int GAME_NOTIFICATION_EVENT 
                = ICSEvent.GAME_NOTIFICATION_EVENT;
   public static final Pattern pattern;

   static {
      pattern = Pattern.compile("^:?("                //begin full match
                                  + "Game notification:\\s"
                                  + ICSEvent.REGEX_handle   //white player
                                  + "\\s"
                                  + ICSEvent.REGEX_rating   //white's rating
                                  + "\\svs\\.\\s"
                                  + ICSEvent.REGEX_handle   //black player
                                  + "\\s"
                                  + ICSEvent.REGEX_rating   //black's rating
                                  + "\\s"
                                  + "(\\w+)"            //rated?
                                  + "\\s"
                                  + "(\\S+)"            //variant
                                  + "\\s"
                                  + "(\\d+)"            //initial time
                                  + "\\s"
                                  + "(\\d+)"            //incr
                                  + ":\\sGame\\s"
                                  + "(\\d+)"            //game number
                                  + ")"                 //match end
		  , Pattern.MULTILINE);  //actually only one line
   }

   //instance/////////////////////////////////////////////////////////////
   public FICSGameNotificationEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSGameNotificationEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   /* assignMatches **********************************************************/
   /** assigns the regex matches to their appropriate fields
    */
   protected void assignMatches (Matcher m) {
      //ICSEvent
      detectFake(m.group(0));

      white = m.group(2);
      whiteRating = new ICSRating(m.group(3));
      black = m.group(4);
      blackRating = new ICSRating(m.group(5));

      isRated = ("rated".equals(m.group(6)));
      variant = new ICSVariant(m.group(7));

      //numbers
      try {
         time = Integer.parseInt(m.group(8));
	 incr = Integer.parseInt(m.group(9));
	 boardNumber = Integer.parseInt(m.group(10));
      }
      catch (NumberFormatException e) {
         ICSErrorLog.report("FICSGameNotificationEvent"
	   + " threw NumberFormatException"
	   + "for: " + m.group(0));
	 setEventType(ICSEvent.UNKNOWN_EVENT);
	 setMessage(m.group(0));
      }
   }

   //ICSEvent/////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;

      if ((m = matches(s)) != null)
         return new FICSGameNotificationEvent(server, m);
      else 
         return null;
   }

   ////////////////////////////////////////////////////////////////////////
   public String getReadable () {
      StringBuffer sb = new StringBuffer(80);
      sb.append("Game Notification: ")
        .append(getWhitePlayer())
	.append(" vs. ")
	.append(getBlackPlayer());
      return sb.toString();
   }

   public String toString () {
      return getReadable();
   }
   public String getNative () { return null; }
}
