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

public abstract class ICSSeekAdEvent extends ICSEvent implements ICSSeekEvent {
   public static final int SEEK_AD_EVENT = ICSEvent.SEEK_AD_EVENT;

   public static final int COLOR_UNSPECIFIED = 0,
                           COLOR_WHITE       = 1,
			   COLOR_BLACK       = 2;

   //instance/////////////////////////////////////////////////////////////
   protected String player;
   protected ICSAccountType accountType;
   protected int adNumber,
		 time,
		 incr,
		 rangeLow,
		 rangeHigh;

   protected ICSRating rating;

   protected boolean isRated,
                     isManual,
		     /**seeking user insists that responses meet his formula*/
		     isRestrictedByFormula,
		     /**meets this user's forumla*/
		     meetsFormula;

   protected int     color;
   protected ICSVariant variant;

   public ICSSeekAdEvent (ICSProtocolHandler server, int eventType) {
      super(server, eventType);
   }

   public ICSSeekAdEvent (ICSProtocolHandler server) {
      this(server, SEEK_AD_EVENT);
   }

   //getters and setters//////////////////////////////////////////////////////
   public String getPlayer () { return player; }
   public void setPlayer (String player) { this.player = player; }

   public ICSAccountType getAccountType () { return accountType; }
   public void setAccountType (ICSAccountType acct) { accountType = acct; }

   public int getAdNumber () { return adNumber; }
   public void setAdNumber (int num) { adNumber = num; }

   public ICSRating getRating () { return rating;}
   public void setRating (ICSRating rating) { this.rating = rating; }

   public int getInitialTime () { return time; }
   public void setInitialTime (int itime) { time = itime; }

   public int getTimeIncrement () { return incr; }
   public void setTimeIncrement (int incr) { this.incr = incr; }

   public int getRatingRangeLow () { return rangeLow; }
   public void setRatingRangeLow (int rating) { rangeLow = rating; }
   
   public int getRatingRangeHigh () { return rangeHigh; }
   public void setRatingRangeHigh (int rating) { rangeHigh = rating; }

   public boolean isRated () { return isRated; }
   public void setRated (boolean t) { isRated = t; }

   public boolean isManual () { return isManual; }
   public void setManual (boolean t) { isManual = t; }

   public boolean isRestrictedByFormula () { return isRestrictedByFormula; }
   public void setRestrictedByFormula (boolean t) { isRestrictedByFormula = t; }

   public boolean meetsFormula () { return meetsFormula; }
   public void setMeetsFormula (boolean t) { meetsFormula = t; }

   public int getColor () { return color; }
   public void setColor (int color) { this.color = color; }

   ////////////////////////////////////////////////////////////////////////
   public String getReadable () {
   //Hindoo (1474) seeking 5 0 rated blitz m ("play 5" to respond)
      StringBuffer sb = new StringBuffer(80);
      sb.append(player)
        .append(accountType)
	.append(" (")
	.append(rating)
	.append(") seeking ")
	.append(time)
	.append(" ")
	.append(incr)
	.append(" ");

      if (!isRated)
        sb.append("un");
      sb.append("rated ")
        .append(variant)
	.append(" ");

      if (color == COLOR_WHITE)
         sb.append("[white] ");
      else if (color == COLOR_BLACK)
         sb.append("[black] ");

      if (isManual)
         sb.append("m ");

      if (isRestrictedByFormula)
         sb.append("f ");

      sb.append("(\"play ")
        .append(adNumber)
	.append("\" to respond)");
        

      return sb.toString();
   }

   public String toString () {
      return getReadable();
   }
}
