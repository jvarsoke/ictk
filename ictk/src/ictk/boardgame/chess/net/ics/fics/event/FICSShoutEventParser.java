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

 */
public class FICSShoutEventParser extends ICSEventParser {

   //static/////////////////////////////////////////////////////////////////
   public static FICSShoutEventParser singleton;
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^:?(" //begin
         + "("
         + "(-->\\s)"
         + "([\\w]+)"
         + "((?:\\([A-Z*]+\\))*)"
         + "((?:.|\\s+\\\\|\\s+:)*)"
         + ")"
         + "|"
         + "("
         + "([\\w]+)"
         + "((?:\\([A-Z*]+\\))*)"
         + "\\s+"
         + "(?:([sct])-)?"
         + "shouts:\\s"
         + ")"
         + "((?:.|\\s+\\\\|\\s+:)*)"

         + ")"  //end
         , Pattern.MULTILINE);

      singleton = new FICSShoutEventParser();
   }

   //instance///////////////////////////////////////////////////////////////
   protected FICSShoutEventParser () {
      super(masterPattern);
   }

   /* getInstance ***********************************************************/
   public static ICSEventParser getInstance () {
       return singleton;
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSChannelEvent();
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSChannelEvent evt = (ICSChannelEvent) event;

      if (Log.debug && debug)
         Log.debug(DEBUG, "assigning matches", m);

      
      evt.setFake(detectFake(m.group(0)));
      
      evt.setEventType(ICSEvent.SHOUT_EVENT);
      //emote
      if (m.group(3) != null) {
            evt.setChannel(ICSChannelEvent.EMOTE_CHANNEL);
	    
      evt.setPlayer(m.group(4));
      
	    
      evt.setAccountType(parseICSAccountType(m, 5));
      
	    
      evt.setMessage(m.group(6));
      
      }
      else {
	    
      evt.setPlayer(m.group(8));
      
	    
      evt.setAccountType(parseICSAccountType(m, 9));
      
	    
      evt.setMessage(m.group(11));
      
         if (m.group(10) != null) {
           switch (m.group(10).charAt(0)) {
               case 's': 
	          evt.setChannel(ICSChannelEvent.SSHOUT_CHANNEL); 
		  break;
               case 'c': 
	          evt.setChannel(ICSChannelEvent.CSHOUT_CHANNEL); 
		  break;
               case 't': 
	          evt.setChannel(ICSChannelEvent.TSHOUT_CHANNEL); 
		  break;
               default:
                  Log.error(Log.PROG_WARNING,
                     "Received unknown shout type: '"
                     + m.group(10).charAt(0) + "' from " + m.group(0));
                  evt.setEventType(ICSEvent.UNKNOWN_EVENT);
                  evt.setMessage(m.group(0)); //the whole message
                  return;
            }
	 }
	 else {
	    evt.setChannel(ICSChannelEvent.SHOUT_CHANNEL);
	 }
      }
	 
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSChannelEvent evt = (ICSChannelEvent) event;
      StringBuffer sb = new StringBuffer(80);
      
      if (evt.isFake()) sb.append(":");
      
      if (evt.getChannel() == ICSChannelEvent.EMOTE_CHANNEL) {
         sb.append("--> ")
           .append(evt.getPlayer())
           .append(evt.getAccountType());
      }

      else {
         sb.append(evt.getPlayer())
           .append(evt.getAccountType())
           .append(" ");

         switch (evt.getChannel()) {
            case ICSChannelEvent.SHOUT_CHANNEL:
               break;

            case ICSChannelEvent.SSHOUT_CHANNEL:
               sb.append("s-");
               break;

            case ICSChannelEvent.CSHOUT_CHANNEL:
               sb.append("c-");
               break;

            case ICSChannelEvent.TSHOUT_CHANNEL:
               sb.append("t-");
               break;

            default:
               throw new IllegalStateException(
               "Tried to get a toNative() with the ShoutParser when the "
               + "channel is not a shout -- should use the Channel Parser for"
               + " channel(" + evt.getChannel() + ")");
         }
         sb.append("shouts: ");
      }

      sb.append(evt.getMessage());
	    

      return sb.toString();
   }
}
