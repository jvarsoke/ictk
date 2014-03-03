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

import ictk.boardgame.chess.net.ics.*;
import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.ui.cli.ANSIConsole;

import java.util.*;

/** this class demonstrates how to create a listener for particular 
 *  channels.  Mostly it's used to demonstrate how a class subscribes
 *  to a channel via the ICSEventRouter.  This class will label the 
 *  messages sent to channel 1 as [help].
 */
public class ChannelListenerExample implements ICSEventListener {
   public boolean showTimestamp = true;
   public static Calendar cal = new GregorianCalendar();

   //colors
   public final static char ESC = '\u001B';
   public final static String BLACK   = ESC + "[0;30",
                              YELLOW  = ESC + "[0;33m",
                              CYAN    = ESC + "[0;36m",
                              BOLD_BLACK   = ESC + "[1;30m",
                              BOLD_RED     = ESC + "[1;31m",
                              BOLD_YELLOW  = ESC + "[1;33m",
                              BOLD_CYAN    = ESC + "[1;36m",
                              PLAIN = ESC + "[0;m";

   public void icsEventDispatched (ICSEvent evt) {
      ICSChannelEvent chEvt = (ICSChannelEvent) evt;
      String prefix = null;

      if (showTimestamp) {
         System.out.print(BOLD_BLACK 
            + getTimestampAsString(evt.getTimestamp())
	    + PLAIN);
      }

      System.out.print("<" + evt.getEventType() + ">");

      switch (chEvt.getChannel()) {
         case 1:
	    System.out.print(BOLD_BLACK);
	    System.out.print("[help]");

	    if (chEvt.getAccountType().is(ICSAccountType.ADMIN))
	       System.out.print(BOLD_RED);
	    else if (chEvt.getAccountType().is(ICSAccountType.SERVICE_REP))
	       System.out.print(BOLD_YELLOW);
	    else
	       System.out.print(PLAIN);

	    System.out.print(chEvt.getPlayer());
	    System.out.print(BOLD_BLACK);
	    System.out.print(": ");
	    System.out.print(CYAN);
	    System.out.print(chEvt.getMessage());
	    System.out.println(PLAIN);
	    break;
	 default:
	    System.out.println("subscribed to unhandled channel [" 
	       + chEvt.getChannel() + "]");
      }
      System.out.flush();
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
