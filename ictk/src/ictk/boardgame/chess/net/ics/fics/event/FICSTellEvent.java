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


public class FICSTellEvent extends ICSTellEvent {

   //static initializer/////////////////////////////////////////////////////
   public static final int TELL_EVENT =  ICSEvent.TELL_EVENT,
                           SAY_EVENT  =  ICSEvent.SAY_EVENT;

   public static final Pattern pattern;

   static {
      pattern  = Pattern.compile("^:?("                  //begin full match
                                  + ICSEvent.REGEX_handle
				  + ICSEvent.REGEX_acct_type
				  + "\\s(tells you|says):\\s"
				  + ICSEvent.REGEX_mesg
				  + ")"                    //end
          , Pattern.MULTILINE);
   }

   //instance vars//////////////////////////////////////////////////////////
   //constructors///////////////////////////////////////////////////////////
   public FICSTellEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSTellEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   //ICSEvent/////////////////////////////////////////////////////////////////
   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;
      if ((m = matches(s)) != null) 
         return new FICSTellEvent(server, m);
      else
         return null;
   }

   //ICSMessage///////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   //ICSTell//////////////////////////////////////////////////////////////////
   protected void assignMatches (Matcher m) {
      detectFake(m.group(0));

      setPlayer(m.group(2));

      try {
         accountType = new ICSAccountType(m.group(3));
      }
      catch (IOException e) {
         System.err.println("ICSAccountType couldn't parse " + m.group(3) 
	    + " of " + m.group(0));
	 accountType = new ICSAccountType();
      }

      if ("says".equals(m.group(4))) {
         setEventType(SAY_EVENT);
         isSay = true;
      }

      setMessage(m.group(5));
   }

   public String toString () {
      StringBuffer sb = new StringBuffer();

      sb.append(getPlayer());

      sb.append(getAccountType());

      if (isSay) 
         sb.append(" says: ");
      else
         sb.append(" tells you: ");

      sb.append(getMessage());

      return sb.toString();
   }
   public String getNative () { return null; }
}


