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


/**This cooresponds to Kibits messages and Whisper messages.  A flag
 * descerns which one it is.
 */
public class FICSKibitzEvent extends ICSKibitzEvent {

   //static initializer/////////////////////////////////////////////////////
   public static final int KIBITZ_EVENT =  ICSEvent.KIBITZ_EVENT,
                           WHISPER_EVENT = ICSEvent.WHISPER_EVENT;
   public static final Pattern pattern;
   static {
      pattern  = Pattern.compile("^("               //begin full match
                                  + ICSEvent.REGEX_handle
                                  + ICSEvent.REGEX_acct_type
                                  + ICSEvent.REGEX_rating
                                  + "\\[(\\d+)\\]"         //game number
                                  + "\\s(kibitzes|whispers):\\s"
                                  + "((.|\\s+\\\\)*)"     //message
                                  + ")"                    //end
          , Pattern.MULTILINE);
   }

   //instance vars//////////////////////////////////////////////////////////

   //constructors///////////////////////////////////////////////////////////
   public FICSKibitzEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSKibitzEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   //ICSEvent/////////////////////////////////////////////////////////////////
   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;
      if ((m = matches(s)) != null) 
         return new FICSKibitzEvent(server, m);
      else
         return null;
   }

   //ICSMessage///////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   //ICSKibitz////////////////////////////////////////////////////////////////
   protected void assignMatches (Matcher m) {
      detectFake(m.group(0));

      setPlayer(m.group(2));

      try {
         accountType = new ICSAccountType(m.group(3));
      }
      catch (IOException e) {
         System.err.println("ICSRating couldn't parse " + m.group(3) + " of "
	    + m.group(0));
	 accountType = new ICSAccountType();
      }

      try {
	 if (m.group(4) != null)
	    rating = new ICSRating(m.group(4));
      }
      catch (NumberFormatException e) {
         System.err.println("ICSRating couldn't parse " + m.group(4) + " of "
	    + m.group(0));
	 rating = null;
      }

      try {
         boardNumber = Integer.parseInt(m.group(5));
      }
      catch (NumberFormatException e) {
         System.err.println("ISCKibitzEvent couldn't parse board number for: "
	    + m.group(5) + " of " + m.group(0));
      }

      if ("whispers".equals(m.group(6))) {
         isWhisper = true;
	 setEventType(WHISPER_EVENT);
      }

      setMessage(m.group(7));
   }

   public String toString () {
      StringBuffer sb = new StringBuffer();

      sb.append(getPlayer());

      sb.append(getAccountType());

      if (getRating() != null)
         sb.append("(").append(getRating()).append(")");

      sb.append("[").append(getBoardNumber()).append("]");

      if (isWhisper) 
         sb.append(" whispers: ");
      else
         sb.append(" kibitzes: ");

      sb.append(getMessage());

      return sb.toString();
   }
   public String getNative () { return null; }
}
