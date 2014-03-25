/*
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke <ictk.jvarsoke [at] neverbox.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ictk.boardgame.chess.net.ics.ui.cli;

import ictk.boardgame.chess.net.ics.*;
import ictk.boardgame.chess.net.ics.event.*;

import java.util.*;

/** This class is used for XBoard like display of events from the server.
 *  Events are color coded for easy discrimination.
 */
public class ANSIConsole implements ICSEventListener {

   public boolean debug            = false;
   protected boolean showTimestamp = true;
   protected static Calendar cal   = new GregorianCalendar();

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

   /* icsEventDispatched ****************************************************/
   public void icsEventDispatched (ICSEvent evt) {
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

	  case ICSEvent.SEEK_REMOVE_EVENT:
	  case ICSEvent.SEEK_CLEAR_EVENT:
	  case ICSEvent.SEEK_AD_EVENT:

	  case ICSEvent.PLAYER_CONNECTION_EVENT:
	  case ICSEvent.PLAYER_NOTIFICATION_EVENT:
	  case ICSEvent.GAME_RESULT_EVENT:
	  case ICSEvent.GAME_CREATED_EVENT:
	     prefix = ESC + BOLD_BLACK;
	     break;

	  case ICSEvent.SEEK_AD_READABLE_EVENT:
	  case ICSEvent.GAME_NOTIFICATION_EVENT:
	     prefix = ESC + BLUE;
	     break;

	  case ICSEvent.BOARD_UPDATE_EVENT:
	  case ICSEvent.MOVE_LIST_EVENT:
	     prefix = ESC + YELLOW;
	     break;

	  default:

	  case ICSEvent.CHALLENGE_EVENT:
	     //TODO:should probably split line like Xboard
	     prefix = ESC + BOLD_RED;  
	     break;
      }

      if (showTimestamp) {
         System.out.print(ESC + BOLD_BLACK 
	    + getTimestampAsString(evt.getTimestamp())
	    + ESC + PLAIN);
      }

      if (debug)
         System.out.print("<" + evt.getEventType() + ">");

      if (prefix != null)
         System.out.println(prefix + evt + ESC + PLAIN);
      else
         System.out.println(evt);

      System.out.flush();
   }

   /* getTimestampAsString **************************************************/
   protected String getTimestampAsString (Date date) {
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

   /* setTimestampVisible ***************************************************/
   public void setTimestampVisible (boolean t) {
      showTimestamp = t;
   }

   /* isTimestampVisible ****************************************************/
   /** shows time stamped messages. <i>default: true</i>
    */
   public boolean isTimestampVisible () {
      return showTimestamp;
   }
}
