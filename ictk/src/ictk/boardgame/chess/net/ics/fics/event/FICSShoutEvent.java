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

public class FICSShoutEvent extends ICSShoutEvent {
   //static///////////////////////////////////////////////////////////////
   public static final Pattern pattern;
   public static final int SHOUT_EVENT =  ICSEvent.SHOUT_EVENT,
                           SSHOUT_EVENT = ICSEvent.SSHOUT_EVENT,
                           CSHOUT_EVENT = ICSEvent.CSHOUT_EVENT,
                           TSHOUT_EVENT = ICSEvent.TSHOUT_EVENT,
                           SHOUT_EMOTE_EVENT = ICSEvent.SHOUT_EMOTE_EVENT;

   static {

   pattern = Pattern.compile("^:?("
                           + "("
                           + "(-->\\s)"                //emotes
                           + ICSEvent.REGEX_handle
                           + ICSEvent.REGEX_acct_type
                           + ICSEvent.REGEX_mesg
                           + ")"
                           + "|"
                           + "("
                           + ICSEvent.REGEX_handle
                           + ICSEvent.REGEX_acct_type
                           + "\\s+"
                           + "([sct]-)?"              //shout types
                           + "shouts:\\s"
                           + ")"  //shout
                           + ICSEvent.REGEX_mesg
                           + ")"  //whole thing
          , Pattern.MULTILINE);
			   
   }

   //instance/////////////////////////////////////////////////////////////
   
   //constructors/////////////////////////////////////////////////////////
   public FICSShoutEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSShoutEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   //ICSEvent/////////////////////////////////////////////////////////////
   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;

      if ((m = matches(s)) != null)
         return new FICSShoutEvent(server, m);
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

      if (m.group(3) != null) {   //emote
         setEventType(SHOUT_EMOTE_EVENT);
         setPlayer(m.group(4));

	 try {
	    accountType = new ICSAccountType(m.group(5));
	 }
	 catch (IOException e) {
	    System.err.println("FICSShoutEvent can't parse account type: "
	       + m.group(5) + " of " + m.group(0));
	    accountType = new ICSAccountType();
	 }

	 setMessage(m.group(6));
      }

      else {                     //shout
         setPlayer(m.group(9));
	 setMessage(m.group(12));

	 try {
	    accountType = new ICSAccountType(m.group(10));
	 }
	 catch (IOException e) {
	    System.err.println("FICSShoutEvent can't parse account type: "
	       + m.group(10) + " of " + m.group(0));
	    accountType = new ICSAccountType();
	 }

         if (m.group(11) != null) {
	    switch (m.group(11).charAt(0)) {
	       case 's': setEventType(SSHOUT_EVENT); break;
	       case 'c': setEventType(CSHOUT_EVENT); break;
	       case 't': setEventType(TSHOUT_EVENT); break;
	       default:
	          ICSErrorLog.report(
		     "ICSShoutEvent received unknown shout type: '"
		     + m.group(11).charAt(0) + "' from " + m.group(0));
	          setEventType(ICSEvent.UNKNOWN_EVENT);
		  setMessage(m.group(1)); //the whole message
	    }
	 }
	 else 
	    setEventType(SHOUT_EVENT);

      }
   }

   public String toString () {
      if (SHOUT_EMOTE_EVENT == eventType)
         return "--> " + getPlayer() + getAccountType() + getMessage();
      else if (ICSEvent.UNKNOWN_EVENT == eventType) {
         return getMessage();
      }
      else {
         StringBuffer sb = new StringBuffer();
	 sb.append(getPlayer())
	   .append(getAccountType())
	   .append(" ");

         switch (eventType) {
	    case CSHOUT_EVENT: sb.append("c-"); break;
	    case SSHOUT_EVENT: sb.append("s-"); break;
	    case TSHOUT_EVENT: sb.append("t-"); break;
	    default:
	 }
	 sb.append("shouts: ")
	   .append(getMessage());
	 return sb.toString();
      }
   }
   public String getNative () { return null; }
}
