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

package ictk.boardgame.chess.net;

import ictk.boardgame.chess.net.ics.*;
import ictk.boardgame.chess.net.ics.event.*;


public class ANSIConsole implements ICSEventListener {

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
          case ICSChannelEvent.CHANNEL_EVENT:
	     switch (((ICSChannelEvent) evt).getChannel()) {
	        case 1:  prefix = ESC + CYAN; break;
		case 85:
		case 88: prefix = ESC + YELLOW; break;
		default: prefix = ESC + BOLD_CYAN;
	     }
	     break;

	  case ICSTellEvent.TELL_EVENT:
	  case ICSTellEvent.SAY_EVENT:
	  case ICSBoardSayEvent.BOARD_SAY_EVENT:
	     prefix = ESC + BOLD_YELLOW;
	     break;

	  case ICSKibitzEvent.KIBITZ_EVENT:
	     prefix = ESC + BOLD_MAGENTA;
	     break;

	  case ICSKibitzEvent.WHISPER_EVENT:
	     prefix = ESC + MAGENTA;
	     break;

	  case ICSShoutEvent.SHOUT_EVENT:
	  case ICSShoutEvent.SHOUT_EMOTE_EVENT:
	     prefix = ESC + GREEN;
	     break;

	  case ICSShoutEvent.CSHOUT_EVENT:
	  case ICSShoutEvent.SSHOUT_EVENT:
	  case ICSShoutEvent.TSHOUT_EVENT:
	     prefix = ESC + BOLD_GREEN;
	     break;

	  case ICSSeekAdEvent.SEEK_AD_EVENT:
	  case ICSSeekRemoveEvent.SEEK_REMOVE_EVENT:
	  case ICSSeekClearEvent.SEEK_CLEAR_EVENT:
	  case ICSPlayerConnectionEvent.PLAYER_CONNECTION_EVENT:
	  case ICSPlayerNotificationEvent.PLAYER_NOTIFICATION_EVENT:
	  case ICSGameResultEvent.GAME_RESULT_EVENT:
	  case ICSGameCreatedEvent.GAME_CREATED_EVENT:
	     prefix = ESC + BOLD_BLACK;
	     break;

	  case ICSSeekAdReadableEvent.SEEK_AD_READABLE_EVENT:
	  case ICSGameNotificationEvent.GAME_NOTIFICATION_EVENT:
	     prefix = ESC + BLUE;
	     break;

	  case ICSQTellEvent.QTELL_EVENT:
	     prefix = ESC + BOLD_RED;
	     break;

	  case ICSBoardUpdateEvent.BOARD_UPDATE_EVENT:
	     prefix = ESC + YELLOW;
	     break;

	  default:
       }
       System.out.print("<" + evt.getEventType() + ">");

       if (prefix != null)
          System.out.println(prefix + evt + ESC + PLAIN);
       else
          System.out.println(evt);
   }
}
