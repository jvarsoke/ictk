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


public class FICSTellEventParser extends ICSEventParser {
   //static/////////////////////////////////////////////////////////////////
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
          "^:?("            //begin full match
//<template function=regex>
	  + REGEX_handle
	  + REGEX_acct_type
	  + "\\s(tells you|says):\\s"
	  + REGEX_mesg
//</template>
	  + ")"             //end
          , Pattern.MULTILINE);
   }

   //instance///////////////////////////////////////////////////////////////
   public FICSTellEventParser () {
      super(masterPattern);
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSTellEvent();
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSTellEvent evt = (ICSTellEvent) event;

      evt.setFake(detectFake(m.group(0)));

//<template function=assignMatches>
      evt.setPlayer(m.group(2));

      evt.setAccountType(parseAccountType(m, 3));

      if ("says".equals(m.group(4))) 
         evt.setEventType(ICSEvent.SAY_EVENT);
      else
         evt.setEventType(ICSEvent.TELL_EVENT);

      evt.setMessage(m.group(5));
//</template>
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {
      ICSTellEvent evt = (ICSTellEvent) event;
      StringBuffer sb = new StringBuffer(20);

      if (evt.isFake()) sb.append(":");
//<template function=toNative>
      sb.append(evt.getPlayer());

      sb.append(evt.getAccountType());

      if (evt.isSay()) 
         sb.append(" says: ");
      else
         sb.append(" tells you: ");

      sb.append(evt.getMessage());
//</template>

      return sb.toString();
   }
}
