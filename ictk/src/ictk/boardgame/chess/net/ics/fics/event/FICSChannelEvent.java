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

public class FICSChannelEvent extends ICSChannelEvent {
   //static///////////////////////////////////////////////////////////////
   public static final Pattern pattern;
   public static final int CHANNEL_EVENT = ICSEvent.CHANNEL_EVENT;

   static {
      pattern  = Pattern.compile("^:?("            //begin full match
                                  + ICSEvent.REGEX_handle
                                  + ICSEvent.REGEX_acct_type
                                  + "(?:\\((\\d+)\\))"      //channel #
                                  + ":\\s"
				  + ICSEvent.REGEX_mesg
                                  + ")"                    //end
          , Pattern.MULTILINE);
   }

   //instance/////////////////////////////////////////////////////////////
   
   //constructors/////////////////////////////////////////////////////////
   public FICSChannelEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSChannelEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
      setEventType(CHANNEL_EVENT);
   }

   //ICSEvent/////////////////////////////////////////////////////////////
   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;

      if ((m = matches(s)) != null)
         return new FICSChannelEvent(server, m);
      else 
         return null;
   }

   //ICSMessageEvent//////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   //ICSChannelEvent/////////////////////////////////////////////////////
   protected void assignMatches (Matcher m) {
      detectFake(m.group(0));

      setPlayer(m.group(2));
      
      try {
         accountType = new ICSAccountType(m.group(3));
      }
      catch (IOException e) {
         System.err.println("FICSChannelEvent can't parse channel number: "
	    + m.group(3) + " of " + m.group(0));
	 accountType = new ICSAccountType();
      }

      try {
         setChannel(Integer.parseInt(m.group(4)));
      }
      catch (NumberFormatException e) {
         System.err.println("FICSChannelEvent can't parse channel number: "
	    + m.group(4) + " of " + m.group(0));
      }

      setMessage(m.group(5));
   }

   public String toString () {
      return getPlayer() + getAccountType() + "(" + getChannel() + ")"
         + ": " + getMessage();
   }
   public String getNative () { return null; }
}
