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


public class FICSSeekAdReadableEventParser extends ICSEventParser {
   //static/////////////////////////////////////////////////////////////////
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^:?(" //begin
//<template function=regex>
	 + REGEX_handle
	 + REGEX_acct_type
	 + "\\s+"
	 + REGEX_rating
	 + "\\sseeking\\s"
	 + "(\\d+)"               //start time
	 + "\\s"
	 + "(\\d+)"               //inc
	 + "\\s"
	 + "(\\w+)"               //(un)rated
	 + "\\s"
	 + "(\\S+)"               //variant wild/8
	 + "(?:\\s\\[(\\w+)\\])?" //[color]
	 + "(?:\\s(m))?"          //m --manual
	 + "(?:\\s(f))?"          //f --formula
	 + "\\s\\(\"play\\s"
	 + "(\\d+)"               //game number
	 + ".*"                  //rest
//</template>
         + ")"  //end
         , Pattern.MULTILINE);
   }

   //instance///////////////////////////////////////////////////////////////
   public FICSSeekAdReadableEventParser () {
      super(masterPattern);
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSSeekAdEvent();
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSSeekAdEvent evt = (ICSSeekAdEvent) event;

      evt.setFake(detectFake(m.group(0)));
//<template function=assignMatches>

      //player
      evt.setPlayer(m.group(2));
      evt.setAccountType(parseAccountType(m, 3));

      //rated?
      evt.setRated(m.group(7).charAt(0) == 'r');

      evt.setVariant(new ICSVariant(m.group(8)));

      //colors
      if (m.group(9) == null)
         evt.setColor(ICSSeekAdEvent.COLOR_UNSPECIFIED);
      else {
	 switch (m.group(9).charAt(0)) {
	    case 'w':
	       evt.setColor(ICSSeekAdEvent.COLOR_WHITE); break;
	    case 'b':
	       evt.setColor(ICSSeekAdEvent.COLOR_BLACK); break;
	    default:
	       Log.error(Log.PROG_WARNING,
		  "Received color specification area: "
		  + "[" + m.group(9) + "]" + " of " + m.group(0));
	 }
      }

      //automatic/manual
      evt.setManual(m.group(10) != null);

      //restricted by formula
      evt.setRestrictedByFormula(m.group(10) != null);

      //numbers
      int i = 0;
      try {
         evt.setAdNumber(Integer.parseInt(m.group(i=12)));
         evt.setRating(new ICSRating(m.group(i=4)));
         evt.setInitialTime(Integer.parseInt(m.group(i=5)));
         evt.setIncrement(Integer.parseInt(m.group(i=6)));

//FIXME: is it even possible to see rating ranges here?
/*
         evt.setRatingRangeLow(Integer.parseInt(m.group(i=12)));
         evt.setRatingRangeHigh(Integer.parseInt(m.group(i=13)));
*/
      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING,
            "Can't parser number "
            + m.group(i) + " of " + m.group(0));
         evt.setEventType(ICSEvent.UNKNOWN_EVENT);
         evt.setMessage(m.group(0));
      }
//</template>
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSSeekAdEvent evt = (ICSSeekAdEvent) event;
      StringBuffer sb = new StringBuffer(20);

      if (evt.isFake()) sb.append(":");

//<template function=toNative>
      sb.append(evt.getPlayer())
        .append(evt.getAccountType())
	.append(" [")
	.append(evt.getRating())
	.append("] seeking ")
	.append(evt.getInitialTime())
	.append(" ")
	.append(evt.getIncrement());
	
      if (evt.isRated())
         sb.append(" rated ");
      else
         sb.append(" unrated ");

      sb.append(evt.getVariant());

      if (evt.getColor() != ICSSeekAdEvent.COLOR_UNSPECIFIED)
         if (evt.getColor() == ICSSeekAdEvent.COLOR_BLACK)
	    sb.append(" [black]");
	 else
	    sb.append(" [white]");

      if (evt.isManual())
         sb.append(" m");

      if (evt.isRestrictedByFormula())
         sb.append(" f");

      sb.append(" play ")
        .append(evt.getAdNumber());

//</template>

      return sb.toString();
   }
}


