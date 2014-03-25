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
