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

package ictk.boardgame.chess.net.ics.event;
import ictk.boardgame.chess.net.ics.*;

import java.util.regex.*;
import java.io.IOException;

public abstract class ICSShoutEvent extends ICSMessageEvent {
   //static///////////////////////////////////////////////////////////////
   public static final int SHOUT_EVENT =  ICSEvent.SHOUT_EVENT,
                           SSHOUT_EVENT = ICSEvent.SSHOUT_EVENT,
                           CSHOUT_EVENT = ICSEvent.CSHOUT_EVENT,
                           TSHOUT_EVENT = ICSEvent.TSHOUT_EVENT,
                           SHOUT_EMOTE_EVENT = ICSEvent.SHOUT_EMOTE_EVENT;


   //instance/////////////////////////////////////////////////////////////
   protected int shoutType;
   protected ICSAccountType accountType;
   
   //constructors/////////////////////////////////////////////////////////
   public ICSShoutEvent (ICSProtocolHandler server) {
      super(server, SHOUT_EVENT);
   }

   public ICSAccountType getAccountType () {
      return accountType;
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
}
