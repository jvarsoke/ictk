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


public class FICSSeekAdEventParser extends ICSEventParser {
   //static/////////////////////////////////////////////////////////////////
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^:?(" //begin
//<template function=regex>
//<s> 56 w=Hasdrubal ti=00 rt=1805E t=3 i=0 r=r tp=wild/fr c=? rr=0-9999 a=t f=f
	 + "<s(n)?>"           //seek 'n' is not in formula
	 + "\\s"
	 + "(\\d+)"            //ad number
	 + "\\s"
	 + "w=" + REGEX_handle
	 + "\\s"
	 + "ti=(\\d+)"         //titles
	 + "\\s"
	 + "rt=(\\d+[\\sPE])"  //rating
	 + "\\s"
	 + "t=(\\d+)"          //initial time
	 + "\\s"
	 + "i=(\\d+)"          //incr
	 + "\\s"
	 + "r=([ur])"          //rated?
	 + "\\s"
	 + "tp=([\\S]+)"       //variant
	 + "\\s"
	 + "c=([BW\\?])"       //color
	 + "\\s"
	 + "rr=(\\d+)-(\\d+)"  //rating range
	 + "\\s"
	 + "a=([tf])"          //automatic / manual
	 + "\\s"
	 + "f=([tf])"          //formula
	 + ".*"                //what's left over
//</template>
         + ")"  //end
         , Pattern.MULTILINE);
   }

   //instance///////////////////////////////////////////////////////////////
   public FICSSeekAdEventParser () {
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
      //meets our formula?
      if (m.group(2) == null)
         evt.setMeetsFormula(true);
      else if (m.group(2).charAt(0) == 'n')
            evt.setMeetsFormula(false);
         else
            Log.error(Log.PROG_WARNING,
	       "Received unknown character in <s[n]?> area: "
	       + m.group(2) + " of " + m.group(0));

      //player
      evt.setPlayer(m.group(4));

      //rated?
      evt.setRated(m.group(9).charAt(0) == 'r');

      evt.setVariant(new ICSVariant(m.group(10)));

      //colors
      switch (m.group(11).charAt(0)) {
         case '?': 
	    evt.setColor(ICSSeekAdEvent.COLOR_UNSPECIFIED); break;
         case 'W':
	    evt.setColor(ICSSeekAdEvent.COLOR_WHITE); break;
         case 'B':
	    evt.setColor(ICSSeekAdEvent.COLOR_BLACK); break;
         default:
	    Log.error(Log.PROG_WARNING,
               "Received unknown character in c=[WB\\?] area: " 
	       + m.group(11) + " of " + m.group(0));
      }

      //automatic/manual
      evt.setManual(m.group(14).charAt(0) == 'f');

      //restricted by formula
      evt.setRestrictedByFormula(m.group(15).charAt(0) == 't');

      //numbers
      int i = 0;
      int acct = 0;
      try {
         evt.setAdNumber(Integer.parseInt(m.group(i=3)));
	 acct = Integer.parseInt(m.group(i=5),16);
         evt.setRating(new ICSRating(m.group(i=6)));
         evt.setInitialTime(Integer.parseInt(m.group(i=7)));
         evt.setIncrement(Integer.parseInt(m.group(i=8)));
         evt.setRatingRangeLow(Integer.parseInt(m.group(i=12)));
         evt.setRatingRangeHigh(Integer.parseInt(m.group(i=13)));
      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING,
	    "Can't parser number "
            + m.group(i) + " of " + m.group(0));
         evt.setEventType(ICSEvent.UNKNOWN_EVENT);
         evt.setMessage(m.group(0));
	 return;
      }

      ICSAccountType accttype = new ICSAccountType();
      //FIXME: needs to parse the mask in "acct"
      evt.setAccountType(accttype);
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
      sb.append("<s");
      if (!evt.meetsFormula())
         sb.append("n");
      sb.append("> ")
        .append(evt.getAdNumber())
	.append(" w=")
	.append(evt.getPlayer())
	.append(" ti=")
	//FIXME: account types need to be converted into title mask
	.append("00")
	.append(" rt=")
	.append(evt.getRating())
	.append(" t=")
	.append(evt.getInitialTime())
	.append(" i=")
	.append(evt.getIncrement())
	.append(" r=")
	.append(((evt.isRated()) ? 'r' : 'u'))
	.append(" tp=")
	.append(evt.getVariant())
	.append(" c=");
      switch(evt.getColor()) {
         case ICSSeekAdEvent.COLOR_UNSPECIFIED:
	    sb.append("?"); break;
         case ICSSeekAdEvent.COLOR_WHITE:
	    sb.append("W"); break;
         case ICSSeekAdEvent.COLOR_BLACK:
	    sb.append("B"); break;
      }
      sb.append(" rr=")
        .append(evt.getRatingRangeLow())
        .append("-")
	.append(evt.getRatingRangeHigh())
	.append(" a=")
	.append(((evt.isManual()) ? 'f' : 't'))
	.append(" f=")
	.append(((evt.isRestrictedByFormula()) ? 't' : 'f'));

//</template>

      return sb.toString();
   }
}


