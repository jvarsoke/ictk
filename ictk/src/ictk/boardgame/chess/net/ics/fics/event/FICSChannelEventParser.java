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

public class FICSChannelEventParser extends ICSEventParser {
   //static///////////////////////////////////////////////////////////////
   public static final Pattern masterPattern;

   static {
      masterPattern = Pattern.compile(
          "^:?("            //begin full match
//<template function=regex>
	  + REGEX_handle
	  + REGEX_acct_type
	  + "(?:\\((\\d+)\\))"      //channel #
	  + ":\\s"
	  + REGEX_mesg
//</template>
	  + ")"             //end
          , Pattern.MULTILINE);
   }

   //instance/////////////////////////////////////////////////////////////
   
   //constructors/////////////////////////////////////////////////////////
   public FICSChannelEventParser () {
      super(masterPattern);
   }

   //ICSEventParser///////////////////////////////////////////////////////
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSChannelEvent();
      
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches **********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSChannelEvent evt = (ICSChannelEvent) event;
      evt.setFake(detectFake(m.group(0)));

      evt.setPlayer(m.group(2));
      
//<template function=assignMatches>
      evt.setAccountType(parseAccountType(m, 3));

      try {
         evt.setChannel(Integer.parseInt(m.group(4)));
      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING, 
	    "Can't parse channel number: "
	    + m.group(4) + " of " + m.group(0));
	 evt.setEventType(ICSEvent.UNKNOWN_EVENT);
	 evt.setMessage(m.group(0));
	 return;
      }

//FIXME: need to check for channel emote
      evt.setMessage(m.group(5));
//</template>
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) { 

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSChannelEvent evt = (ICSChannelEvent) event;
      StringBuffer sb = new StringBuffer();

      if (evt.isFake()) sb.append(":");

//<template function=toNative>
      sb.append(evt.getPlayer())
        .append(evt.getAccountType().toString())
	.append("(")
	.append(evt.getChannel())
	.append(")")
	.append(": ");
      if (evt.isEmote())
        sb.append("<-- ");
      sb.append(evt.getMessage());
//</template>

      return sb.toString();
   }
}
