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
import java.util.StringTokenizer;

public class FICSSeekRemoveEvent extends ICSSeekRemoveEvent {
   public static final int SEEK_REMOVE_EVENT = ICSEvent.SEEK_REMOVE_EVENT;
   public static final Pattern pattern;

   static {
      pattern = Pattern.compile("^("                //begin full match
                              + "<sr>"           //seek 'n' is not in formula
			      + "([ \\d]+)"            //ad number
			      + ")"
		  , Pattern.MULTILINE);  //actually only one line
   }

   //instance/////////////////////////////////////////////////////////////
   public FICSSeekRemoveEvent (ICSProtocolHandler server) {
      super(server);
   }

   protected FICSSeekRemoveEvent (ICSProtocolHandler server, Matcher m) {
      this(server);
      assignMatches(m);
   }

   /* assignMatches **********************************************************/
   /** assigns the regex matches to their appropriate fields
    */
   protected void assignMatches (Matcher m) {
      StringTokenizer st = new StringTokenizer(m.group(2), " ", false );
      ads = new int[st.countTokens()];
      int i = 0;
      //numbers
      try {
         while (st.hasMoreTokens()) {

	    ads[i++] = Integer.parseInt(st.nextToken());
	    
	 }
      }
      catch (NumberFormatException e) {
         ICSErrorLog.report("FICSSeekRemoveEvent threw NumberFormatException"
	   + "for the ad(" + i + "): " + m.group(0));
	 setEventType(ICSEvent.UNKNOWN_EVENT);
	 setMessage(m.group(0));
      }
   }

   //ICSEvent/////////////////////////////////////////////////////////////
   public Pattern getPattern () {
      return pattern;
   }

   public ICSEvent newICSEventInstance (CharSequence s) {
      Matcher m = null;

      if ((m = matches(s)) != null)
         return new FICSSeekRemoveEvent(server, m);
      else 
         return null;
   }

   ////////////////////////////////////////////////////////////////////////
   public String getReadable () {
      StringBuffer sb = new StringBuffer(80);
      sb.append("SeekRemoved:");
      for (int i = 0; i < ads.length; i++) {
         sb.append(" ").append(ads[i]);
      }
         
      return sb.toString();
   }

   public String toString () {
      return getReadable();
   }
   public String getNative () { return null; }
}
