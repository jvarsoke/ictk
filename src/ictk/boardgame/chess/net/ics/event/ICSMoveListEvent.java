/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSMoveListEvent.java,v 1.6 2003/10/01 06:37:18 jvarsoke Exp $
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
import ictk.boardgame.chess.net.ics.fics.event.FICSMoveListParser;

import java.io.IOException;
import java.util.Calendar;

/* ICSMoveListEvent *********************************************************/
/** A list of moves from a game.  This is the result of the commands:
 *  "moves" or "smoves".
 */
public class ICSMoveListEvent extends ICSEvent 
                              implements ICSBoardEvent{
   //static///////////////////////////////////////////////////////////////
   protected static final int MOVE_LIST_EVENT = ICSEvent.MOVE_LIST_EVENT;

   //instance/////////////////////////////////////////////////////////////
   protected int boardNumber, initTime, incrTime;
   protected String white, black, status;
   protected ICSRating whiteRating, blackRating;
   protected ICSDate date;
   protected boolean isRated;
   protected ICSVariant variant;
   protected ICSResult result;
   protected ICSMove[] moves;

   //constructors/////////////////////////////////////////////////////////
   public ICSMoveListEvent () {
      super(MOVE_LIST_EVENT);
   }

   //mutators and accessors///////////////////////////////////////////////
   public ICSMove[] getMoves () { return moves; }
   public void setMoves (ICSMove[] list) { moves = list; }

   public String getWhitePlayer () { return white; }
   public void setWhitePlayer (String player) { white = player; }

   public String getBlackPlayer () { return black; }
   public void setBlackPlayer (String player) { black = player; }

   public ICSVariant getVariant () { return variant; }
   public void setVariant (ICSVariant gameType) { variant = gameType; }

   public ICSRating getWhiteRating () { return whiteRating; }
   public void setWhiteRating (ICSRating rating) { whiteRating = rating; }

   public ICSRating getBlackRating () { return blackRating; }
   public void setBlackRating (ICSRating rating) { blackRating = rating; }

   public ICSResult getResult () { return result; }
   public void setResult (ICSResult res) { result = res; }

   public int getInitialTime () { return initTime; }
   public void setInitialTime (int minutes) { initTime = minutes; }

   public int getIncrement () { return incrTime; }
   public void setIncrement (int seconds) { incrTime = seconds; }

   public boolean isRated () { return isRated; }
   public void setRated (boolean rated) { isRated = rated; }

   public void setBoardNumber (int board) { boardNumber = board; }

   public int getBoardNumber () { return boardNumber; }

   public void setStatus (String status) {
      this.status = status;
   }

   public String getStatus () { return status; }


   public void setDate (ICSDate date) { this.date = date; }
   public ICSDate getDate () { return date; }

   //readable//////////////////////////////////////////////////////////////
   public String getReadable () {
      return FICSMoveListParser.getInstance().toNative(this);
   }
}
