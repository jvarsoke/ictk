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

/**
 * Direct tells to the user through "tell" or "say".                  
 */
public class FICSTellEventParser extends ICSEventParser {

   //static/////////////////////////////////////////////////////////////////
   public static FICSTellEventParser singleton;
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^:?(" //begin
         + "([\\w]+)"
         + "((?:\\([A-Z*]+\\))*)"
         + "\\s(tells\\syou|says):\\s"
         + "((?:.|\\s+\\\\|\\s+:)*)"

         + ")"  //end
         , Pattern.MULTILINE);

      singleton = new FICSTellEventParser();
   }

   //instance///////////////////////////////////////////////////////////////
   protected FICSTellEventParser () {
      super(masterPattern);
   }

   /* getInstance ***********************************************************/
   public static ICSEventParser getInstance () {
       return singleton;
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

      if (Log.debug && debug)
         Log.debug(DEBUG, "assigning matches", m);

      
      evt.setFake(detectFake(m.group(0)));
      
      evt.setPlayer(m.group(2));
      
      evt.setAccountType(parseICSAccountType(m, 3));
      
      evt.setMessage(m.group(5));
      
      if ("tells you".equals(m.group(4))) {
         evt.setEventType(ICSEvent.TELL_EVENT);
      }
      else if ("says".equals(m.group(4))) {
         evt.setEventType(ICSEvent.SAY_EVENT);
      }
	    
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSTellEvent evt = (ICSTellEvent) event;
      StringBuffer sb = new StringBuffer(20);
      
      if (evt.isFake()) sb.append(":");
      
      sb.append(evt.getPlayer())
        .append(evt.getAccountType());

      switch (evt.getEventType()) {
         case ICSEvent.TELL_EVENT:
            sb.append(" tells you: ");
            break;

         case ICSEvent.SAY_EVENT:
            sb.append(" says: ");
            break;
      }

      sb.append(evt.getMessage());
	    

      return sb.toString();
   }
}
