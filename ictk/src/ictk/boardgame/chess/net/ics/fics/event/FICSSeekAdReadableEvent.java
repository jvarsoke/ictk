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

public class FICSSeekAdReadableEvent extends ICSSeekAdReadableEvent {
   public static final int 
       SEEK_AD_READABLE_EVENT = ICSEvent.SEEK_AD_READABLE_EVENT;
   public static final Pattern pattern;

   static {
      pattern = Pattern.compile("^"
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
          , Pattern.MULTILINE);
   }

   //instance/////////////////////////////////////////////////////////////

   public FICSSeekAdReadableEvent (ICSProtocolHandler server) {
      super(server);
      meetsFormula = true;
   }

   protected FICSSeekAdReadableEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   /* assignMatches **********************************************************/
   /** assigns the regex matches to their appropriate fields
    */
   protected void assignMatches (Matcher m) {
      //player
      setPlayer(m.group(1));
      accountType = new ICSAccountType(2);

      //rated?
      if (m.group(6).charAt(0) == 'r')
         isRated = true;

      variant = new ICSVariant(m.group(7));

      //colors
      if (m.group(8) != null) 
         switch (m.group(8).charAt(0)) {
            case 'w': color = ICSSeekAdEvent.COLOR_WHITE; break;
            case 'b': color = ICSSeekAdEvent.COLOR_BLACK; break;
	    default:
	       ICSErrorLog.report("FICSSeekAdReadableEvent"
	         + " received unknown character"
	         + " in [color] area: " + m.group(0));
         }
      else 
         color = COLOR_UNSPECIFIED;

      //automatic/manual
      if (m.group(9) != null)
         isManual = true;

      //restricted by formula
      if (m.group(10) != null)
         isRestrictedByFormula = true;


      //numbers
      try {
         adNumber = Integer.parseInt(m.group(11));
	 rating = new ICSRating(m.group(3));
	 time = Integer.parseInt(m.group(4));
	 incr = Integer.parseInt(m.group(5));
	 /*
	 rangeLow = Integer.parseInt(m.group(12));
	 rangeHigh = Integer.parseInt(m.group(13));
	 */
      }
      catch (NumberFormatException e) {
         ICSErrorLog.report("FICSSeekAdReadableEvent threw"
	   + " NumberFormatException for"
	   + " the ad: " + m.group(0));
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
         return new FICSSeekAdReadableEvent(server, m);
      else 
         return null;
   }

   public String toString () {
      return getReadable();
   }
   public String getNative () { return null; }
}


