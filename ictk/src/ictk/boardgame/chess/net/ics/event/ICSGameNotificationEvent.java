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

public abstract class ICSGameNotificationEvent extends ICSEvent 
                                      implements ICSBoardEvent {
   public static final int GAME_NOTIFICATION_EVENT 
                = ICSEvent.GAME_NOTIFICATION_EVENT;

   //instance/////////////////////////////////////////////////////////////
   protected String white, black;
   protected ICSRating whiteRating, blackRating;
   protected boolean isRated;
   protected ICSVariant variant;
   protected int time, incr, boardNumber;

   public ICSGameNotificationEvent (ICSProtocolHandler server) {
      super(server, GAME_NOTIFICATION_EVENT);
   }

   //getters and setters//////////////////////////////////////////////////////
   public String getWhitePlayer () { return white; }
   public void setWhitePlayer (String player) { white = player; }

   public String getBlackPlayer () { return black; }
   public void setBlackPlayer (String player) { black = player; }

   public ICSRating getWhiteRating () { return whiteRating; }
   public void setWhiteRating (ICSRating rating) { whiteRating = rating; }

   public ICSRating getBlackRating () { return blackRating; }
   public void setBlackRating (ICSRating rating) { blackRating = rating; }

   public boolean isRated () { return isRated; }
   public void setRated (boolean t) { isRated = t; }

   public ICSVariant getVariant () { return variant; }
   public void setVariant (ICSVariant variant) { this.variant = variant; }

   public int getInitialTime () { return time; }
   public void setInitialTime (int itime) { time = itime; }

   public int getIncrement () { return incr; }
   public void setIncrement (int increment) { incr = increment; }

   public int getBoardNumber () { return boardNumber; }
   public void setBoardNumber (int board) { boardNumber = board; }


   ////////////////////////////////////////////////////////////////////////
   public String getReadable () {
      StringBuffer sb = new StringBuffer(80);
      sb.append("Game Notification: ")
        .append(getWhitePlayer())
	.append(" vs. ")
	.append(getBlackPlayer());
      return sb.toString();
   }

   public String toString () {
      return getReadable();
   }
}
