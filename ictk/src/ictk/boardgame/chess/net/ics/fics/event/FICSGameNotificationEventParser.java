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


public class FICSGameNotificationEventParser extends ICSEventParser {
   //static/////////////////////////////////////////////////////////////////
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^:?("
//<template function=regex>
	 + "Game notification:\\s"
	 + REGEX_handle        //white player
	 + "\\s"
	 + REGEX_rating        //white's rating
	 + "\\svs\\.\\s"
	 + REGEX_handle        //black player
	 + "\\s"
	 + REGEX_rating        //black's rating
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
//</template>
	 + ")"                 //match end
         , Pattern.MULTILINE);
   }

   //instance///////////////////////////////////////////////////////////////
   public FICSGameNotificationEventParser () {
      super(masterPattern);
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSGameNotificationEvent();
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSGameNotificationEvent evt = (ICSGameNotificationEvent) event;

      evt.setFake(detectFake(m.group(0)));

//<template function=assignMatches>
      evt.setWhitePlayer(m.group(2));
      evt.setWhiteRating(parseRating(m, 3));
      evt.setBlackPlayer(m.group(4));
      evt.setBlackRating(parseRating(m, 5));

      evt.setRated("rated".equals(m.group(6)));
      evt.setVariant(new ICSVariant(m.group(7)));

      //numbers
      int index = 0;
      try {
         evt.setInitialTime(Integer.parseInt(m.group(index = 8)));
         evt.setIncrement(Integer.parseInt(m.group(index = 9)));
         evt.setBoardNumber(Integer.parseInt(m.group(index = 10)));
      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING,
           " threw NumberFormatException"
           + "for: " + m.group(index) + " of " + m.group(0));
         evt.setEventType(ICSEvent.UNKNOWN_EVENT);
         evt.setMessage(m.group(0));
      }
//</template>
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSGameNotificationEvent evt = (ICSGameNotificationEvent) event;
      StringBuffer sb = new StringBuffer(20);

      if (evt.isFake()) sb.append(":");

//<template function=toNative>
      sb.append("Game notification: ")
        .append(evt.getWhitePlayer())
	.append(" (")
	.append(evt.getWhiteRating())
	.append(") vs. ")
	.append(evt.getBlackPlayer())
	.append(" (")
	.append(evt.getBlackRating())
	.append(") ");

      if (!evt.isRated())
         sb.append("un");

      sb.append("rated ") 
        .append(evt.getVariant())
	.append(" ")
	.append(evt.getInitialTime())
	.append(" ")
	.append(evt.getIncrement())
	.append(": Game ")
	.append(evt.getBoardNumber());
//</template>

      return sb.toString();
   }
}


