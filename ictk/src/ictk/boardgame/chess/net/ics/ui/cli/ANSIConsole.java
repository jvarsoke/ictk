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

package ictk.boardgame.chess.net.ics.ui.cli;

import ictk.boardgame.chess.net.ics.*;
import ictk.boardgame.chess.net.ics.event.*;

import java.util.*;

public class ANSIConsole implements ICSEventListener {

   public boolean showTimestamp = true;
   public static Calendar cal = new GregorianCalendar();

   //colors
   public final static char ESC = '\u001B';
   public final static String BLACK   = "[0;30",
                              RED     = "[0;31m",
                              GREEN   = "[0;32m",
                              YELLOW  = "[0;33m",
                              BLUE    = "[0;34m",
                              MAGENTA = "[0;35m",
                              CYAN    = "[0;36m",
                              WHITE   = "[0;37m",
                              BOLD_BLACK   = "[1;30m",
                              BOLD_RED     = "[1;31m",
                              BOLD_GREEN   = "[1;32m",
                              BOLD_YELLOW  = "[1;33m",
                              BOLD_BLUE    = "[1;34m",
                              BOLD_MAGENTA = "[1;35m",
                              BOLD_CYAN    = "[1;36m",
                              BOLD_WHITE   = "[1;37m",
                              PLAIN = "[0;m";



   public void icsEventDispatched (ICSEvent evt) {
//      System.out.println("DISPATCHER(" + evt.getEventType() + "): ");
      String prefix = null;

       switch (evt.getEventType()) {

          case ICSEvent.CHANNEL_EVENT:
	     switch (((ICSChannelEvent) evt).getChannel()) {
	        case 1:  prefix = ESC + CYAN; break;
		case 85:
		case 88: prefix = ESC + YELLOW; break;
		default: prefix = ESC + BOLD_CYAN;
	     }
	     break;

	  case ICSEvent.SHOUT_EVENT:
	     switch (((ICSChannelEvent) evt).getChannel()) {
	        case ICSChannelEvent.EMOTE_CHANNEL: 
	        case ICSChannelEvent.SHOUT_CHANNEL: prefix = ESC + GREEN; 
		   break;
	        case ICSChannelEvent.SSHOUT_CHANNEL: 
	        case ICSChannelEvent.CSHOUT_CHANNEL: 
	        case ICSChannelEvent.TSHOUT_CHANNEL: 
		   prefix = ESC + BOLD_GREEN; 
	     }
	     break;

	  case ICSEvent.TELL_EVENT:
	  case ICSEvent.SAY_EVENT:
	  case ICSEvent.BOARD_SAY_EVENT:
	     prefix = ESC + BOLD_YELLOW;
	     break;

	  case ICSEvent.KIBITZ_EVENT:
	     prefix = ESC + BOLD_MAGENTA;
	     break;

	  case ICSEvent.WHISPER_EVENT:
	     prefix = ESC + MAGENTA;
	     break;

/*
	  case ICSEvent.SEEK_AD_EVENT:
	  case ICSEvent.SEEK_REMOVE_EVENT:
	  case ICSEvent.SEEK_CLEAR_EVENT:
	  case ICSEvent.PLAYER_CONNECTION_EVENT:
	  case ICSEvent.PLAYER_NOTIFICATION_EVENT:
	  */
	  case ICSEvent.GAME_RESULT_EVENT:
	  case ICSEvent.GAME_CREATED_EVENT:
	     prefix = ESC + BOLD_BLACK;
	     break;

/*
	  case ICSEvent.SEEK_AD_READABLE_EVENT:
	  case ICSEvent.GAME_NOTIFICATION_EVENT:
	     prefix = ESC + BLUE;
	     break;

	  case ICSEvent.QTELL_EVENT:
	     prefix = ESC + BOLD_RED;
	     break;

	  case ICSEvent.BOARD_UPDATE_EVENT:
	     prefix = ESC + YELLOW;
	     break;
	     */

	  default:
       }

       if (showTimestamp) {
          System.out.print(ESC + BOLD_BLACK 
	     + getTimestampAsString(evt.getTimestamp())
	     + ESC + PLAIN);
       }

       System.out.print("<" + evt.getEventType() + ">");

       if (prefix != null)
          System.out.println(prefix + evt + ESC + PLAIN);
       else
          System.out.println(evt);
   }

   public String getTimestampAsString (Date date) {
      StringBuffer sb = new StringBuffer(5);
      int tmp = 0;
         cal.setTime(date);

         tmp = cal.get(Calendar.HOUR_OF_DAY);
	 if (tmp < 10)
	    sb.append("0");
	 sb.append(tmp).append(":");

	 tmp = cal.get(Calendar.MINUTE);
	 if (tmp < 10)
	    sb.append("0");
	 sb.append(tmp);

	 return sb.toString();
   }
}
