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

public class FICSSeekAdEvent extends ICSSeekAdEvent {
   public static final int SEEK_AD_EVENT = ICSEvent.SEEK_AD_EVENT;
   public static final Pattern pattern;

   static {
//<s> 56 w=Hasdrubal ti=00 rt=1805E t=3 i=0 r=r tp=wild/fr c=? rr=0-9999 a=t f=f 
      pattern = Pattern.compile("^("                //begin full match
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
			      + ")"
		  , Pattern.MULTILINE);  //actually only one line
   }

   //instance/////////////////////////////////////////////////////////////
   public FICSSeekAdEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSSeekAdEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   /* assignMatches **********************************************************/
   /** assigns the regex matches to their appropriate fields
    */
   protected void assignMatches (Matcher m) {
      //meets our formula?
      if (m.group(2) == null)
         meetsFormula = true;
      else if (m.group(2).charAt(0) == 'n')
	    meetsFormula = false;
	 else
	    ICSErrorLog.report("FICSSeekAdEvent received unknown character"
	      + " in <s[n]?> area: " + m.group(0));

      //player
      setPlayer(m.group(4));

      //rated?
      if (m.group(9).charAt(0) == 'r')
         isRated = true;

      variant = new ICSVariant(m.group(10));

      //colors
      switch (m.group(11).charAt(0)) {
         case '?': color = ICSSeekAdEvent.COLOR_UNSPECIFIED; break;
         case 'W': color = ICSSeekAdEvent.COLOR_WHITE; break;
         case 'B': color = ICSSeekAdEvent.COLOR_BLACK; break;
	 default:
	    ICSErrorLog.report("FICSSeekAdEvent received unknown character"
	      + " in c=[WB\\?] area: " + m.group(0));
      }

      //automatic/manual
      if (m.group(14).charAt(0) == 'f')
         isManual = true;

      //restricted by formula
      if (m.group(15).charAt(0) == 't')
         isRestrictedByFormula = true;


      //numbers
      int i = 0;
      try {
	 i = 3;
         adNumber = Integer.parseInt(m.group(3));
	 i = 5;
         accountType = new ICSAccountType(Integer.parseInt(m.group(5),16));
	 i = 6;
	 rating = new ICSRating(m.group(6));
	 i = 7;
	 time = Integer.parseInt(m.group(7));
	 i = 8;
	 incr = Integer.parseInt(m.group(8));
	 i = 12;
	 rangeLow = Integer.parseInt(m.group(12));
	 i = 13;
	 rangeHigh = Integer.parseInt(m.group(13));
      }
      catch (NumberFormatException e) {
         ICSErrorLog.report("FICSSeekAdEvent threw NumberFormatException"
	   + " (" + m.group(i) + ")"  
	   + " for the ad: " + m.group(0));
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
         return new FICSSeekAdEvent(server, m);
      else 
         return null;
   }

   ////////////////////////////////////////////////////////////////////////
   public String getReadable () {
   //Hindoo (1474) seeking 5 0 rated blitz m ("play 5" to respond)
      StringBuffer sb = new StringBuffer(80);
      sb.append(player)
        .append(accountType)
	.append(" (")
	.append(rating)
	.append(") seeking ")
	.append(time)
	.append(" ")
	.append(incr)
	.append(" ");

      if (!isRated)
        sb.append("un");
      sb.append("rated ")
        .append(variant)
	.append(" ");

      if (color == ICSSeekAdEvent.COLOR_WHITE)
         sb.append("[white] ");
      else if (color == ICSSeekAdEvent.COLOR_BLACK)
         sb.append("[black] ");

      if (isManual)
         sb.append("m ");

      if (isRestrictedByFormula)
         sb.append("f ");

      sb.append("(\"play ")
        .append(adNumber)
	.append("\" to respond)");
        

      return sb.toString();
   }

   public String toString () {
      return getReadable();
   }
   public String getNative () { return null; }
}
