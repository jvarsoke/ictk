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

//import java.util.regex.*;
import java.io.IOException;

/* ICSChannelEvent **********************************************************/
/** Channel Events double as Shout Events as well, since shouts are
 *  really nothing more than channels.
 */
public class ICSChannelEvent extends ICSMessageEvent {
   //static///////////////////////////////////////////////////////////////
   protected static final int CHANNEL_EVENT = ICSEvent.CHANNEL_EVENT;

   public static final int SHOUT_CHANNEL  = -1,
                           SSHOUT_CHANNEL = -2,
			   CSHOUT_CHANNEL = -3,
			   TSHOUT_CHANNEL = -4;


   //instance/////////////////////////////////////////////////////////////

      /** this is the channel number, or type of shout */
   protected int channel;

      /** is this an emote.  In shout this looks like "--> handle hugs"
       ** In channel tells this looks like "handle(50): <-- hugs"*/
   protected boolean isEmote; 

   protected ICSAccountType accountType;
   
   //constructors/////////////////////////////////////////////////////////
   public ICSChannelEvent () {
      super(CHANNEL_EVENT);
   }

   public void setAccountType (ICSAccountType t) {
      accountType = t;
   }

   public ICSAccountType getAccountType () {
      return accountType;
   }

   public int getChannel () {
      return channel;
   }

   public void setChannel (int channel) {
      this.channel = channel;
   }

   public boolean isEmote () {
      return isEmote;
   }

   public void setEmote (boolean t) {
      isEmote = t;
   }

   public String getReadable () {
      if (channel >= 0)
         return getPlayer() 
	        + getAccountType() 
		+ "(" + getChannel() + ")"
		+ ": "
		+ ((isEmote()) ? "<-- " : "")
                + getMessage();
      else {
	 if (channel == SHOUT_CHANNEL && isEmote())
	    return 
	        "--> " 
		+ getPlayer() 
		+ getAccountType() 
		+ getMessage();
	 else if (ICSEvent.UNKNOWN_EVENT == eventType) {
	    return getMessage();
	 }
	 else {
	    StringBuffer sb = new StringBuffer();
	    sb.append(getPlayer())
	      .append(getAccountType())
	      .append(" ");

	    switch (channel) {
	       case CSHOUT_CHANNEL: sb.append("c-"); break;
	       case SSHOUT_CHANNEL: sb.append("s-"); break;
	       case TSHOUT_CHANNEL: sb.append("t-"); break;
	       default:
	    }
	    sb.append("shouts: ");
	    if (channel == SHOUT_CHANNEL && isEmote())
	       sb.append("<-- ");
	    sb.append(getMessage());
	    return sb.toString();
	 }
      }
   }
}
