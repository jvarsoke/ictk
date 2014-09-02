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
