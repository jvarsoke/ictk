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

public class FICSGameCreatedEvent extends ICSGameCreatedEvent {
   public static final int GAME_CREATED_EVENT 
                = ICSEvent.GAME_CREATED_EVENT;
   public static final Pattern pattern;

   static {
/*
{Game 73 (RRRR vs. leoj) Creating rated blitz match.}
{Game 116 (Arodin vs. Chernobog) Continuing rated blitz match.}
*/
      pattern = Pattern.compile("^:?("               //beginning
                                    + "\\{Game\\s"
                                    + "(\\d+)"               //game number
                                    + "\\s\\("
                                    + REGEX_handle           //white
                                    + "\\svs\\.\\s"
                                    + REGEX_handle           //black
                                    + "\\)\\s"
				    + "(Creating|Continuing)\\s"
				    + "(rated|unrated)\\s"   //rated
				    + "(\\S+)"               //variant
				    + "\\smatch\\.}"
                                    + ")"                    //end match
          , Pattern.MULTILINE);
   }

   //instance/////////////////////////////////////////////////////////////
   public FICSGameCreatedEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSGameCreatedEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   /* assignMatches **********************************************************/
   /** assigns the regex matches to their appropriate fields
    */
   protected void assignMatches (Matcher m) {
      //ICSEvent
      detectFake(m.group(0));

      white = m.group(3);
      black = m.group(4);

      isContinued = ("Continuing".equals(m.group(5)));

      isRated = ("rated".equals(m.group(6)));

      variant = new ICSVariant(m.group(7));

      //numbers
      try {
	 boardNumber = Integer.parseInt(m.group(2));
      }
      catch (NumberFormatException e) {
         ICSErrorLog.report("ICSGameCreatedEvent"
	   + " threw NumberFormatException"
	   + "for: " + m.group(0));
	 setEventType(ICSEvent.UNKNOWN_EVENT);
	 setMessage(m.group(0));
      }
   }

   //getters and setters//////////////////////////////////////////////////////

   //ICSEvent/////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;

      if ((m = matches(s)) != null)
         return new FICSGameCreatedEvent(server, m);
      else 
         return null;
   }

   ////////////////////////////////////////////////////////////////////////
   public String getReadable () {
      StringBuffer sb = new StringBuffer(80);
      sb.append("Game Created(" + getBoardNumber() + "): ")
        .append(getWhitePlayer())
	.append(" vs. ")
	.append(getBlackPlayer())
	.append(" ");

      if (!isRated) 
         sb.append("un");

      sb.append("rated ")
        .append(getVariant());

      if (isContinued)
        sb.append(" (continued)");

      return sb.toString();
   }

   public String toString () {
      return getReadable();
   }
   public String getNative () { return null; }
}
