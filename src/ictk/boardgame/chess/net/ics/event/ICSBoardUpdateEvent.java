/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSBoardUpdateEvent.java,v 1.4 2003/10/01 06:37:18 jvarsoke Exp $
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

/* ICSBoardUpdateEvent ******************************************************/
/** A board representation that indicates the client should update the
 *  local representation of the board.  An example of this is Sytle12.
 */
public class ICSBoardUpdateEvent extends ICSEvent 
                                          implements ICSBoardEvent {

   public static final int EVENT_TYPE = ICSEvent.BOARD_UPDATE_EVENT;

      /** this is an isolated position the user is looking at.  This
       ** arrises as a result of "ref 3" or the "sposition" command */
   public static final int ISO_POSITION_RELATION = -3,
      /** this is a game being examined that the user is observing */
                           OBSERVING_EXAMINED_RELATION = -2,
      /** this is a game the user is observing */
			   EXAMINING_RELATION = 2,
      /** this is a game the user is playing but it is not his move */
			   PLAYING_NOT_MY_MOVE_RELATION = -1,
      /** this is a game the user is playing and it is his move */
			   PLAYING_MY_MOVE_RELATION = 1,
      /** this is a game being played that the user is observing */
			   OBSERVING_PLAY_RELATION = 0;
                           
   String white, black, verboseMove, sanMove;
   protected boolean isBlackMove,
                     canWhiteCastleOO,
		     canWhiteCastleOOO,
		     canBlackCastleOO,
		     canBlackCastleOOO,
		     flipBoard,
		     clockMoving;
   protected char[][] board;
   protected int enpassantFile = -1,
                    /** 50 move rule count */
                 irreversable,  
                 boardNumber,
		 moveNumber,

/** my relation to this game:
    -3 isolated position, such as for "ref 3" or the "sposition" command
    -2 I am observing game being examined
     2 I am the examiner of this game
    -1 I am playing, it is my opponent's move
     1 I am playing and it is my move
     0 I am observing a game being played
*/
		 relation,
		 /**initial time control in seconds*/
		 itime, 
		 /**increment in seconds*/
		 incr,
		 /**white's total material*/
		 whiteMaterial,
		 /**black's total material*/
		 blackMaterial,
		 whiteClock,
		 blackClock,
		 /**time spend on the last move in seconds**/
		 moveTime,
		 /**timeseal delta (lag)*/
		 timesealDelta;


   //getters setters//////////////////////////////////////////////////////////
   public ICSBoardUpdateEvent () {
      super(ICSEvent.BOARD_UPDATE_EVENT);
      board = new char[8][8];
   }

   //accessors////////////////////////////////////////////////////////////////
   public char[][] getBoardArray () { return board; }

   public boolean isBlackToMove () { return isBlackMove; }

   public String getWhitePlayer () { return white; }
   public String getBlackPlayer () { return black; }
   public int getMoveNumber () { return moveNumber; }
   public int getEnPassantFile () { return enpassantFile; }
   public int getPlySinceLastIrreversableMove () { return irreversable; }
   public int getBoardNumber () { return boardNumber; } 
   public int getRelation () { return relation; }
   public int getInitialTime () { return itime; }
   public int getIncrement () { return incr; }
   public int getWhiteMaterial () { return whiteMaterial; }
   public int getBlackMaterial () { return blackMaterial; }
   public int getWhiteClock () { return whiteClock; }
   public int getBlackClock () { return blackClock; }
   public int getLag () { return timesealDelta; }
   public String getSAN () { return sanMove; }
   public boolean isBlackMove () { return isBlackMove; }
   public boolean isWhiteCastleableKingside () { return canWhiteCastleOO; }
   public boolean isWhiteCastleableQueenside () { return canWhiteCastleOOO; }
   public boolean isBlackCastleableKingside () { return canBlackCastleOO; }
   public boolean isBlackCastleableQueenside () { return canBlackCastleOOO; }

   public int getMoveTime () { return moveTime; }
   public String getVerboseMove () { return verboseMove; }

   public boolean isFlipBoard () { return flipBoard; }
   public boolean isClockMoving () { return clockMoving; }

   public String getMoveTimeAsString () { 
      return getClockAsString(moveTime, true); 
   }

   public String getBlackClockAsString () { 
      return getClockAsString(blackClock, false);
   }

   public String getWhiteClockAsString () { 
      return getClockAsString(whiteClock, false);
   }

   //mutators/////////////////////////////////////////////////////////////////
   public void setBoardArray (char[][] b) { board = b; }
   public void setBoardNumber (int board) { boardNumber = board; }

   public void setBlackMove (boolean t) { isBlackMove = t; }

   public void setWhitePlayer (String player) { white = player; }
   public void setBlackPlayer (String player) { black = player; }
   public void setMoveNumber (int num) { moveNumber = num; }
   public void setEnPassantFile (int ep) { enpassantFile = ep; }
   public void setPlySinceLastIrreversableMove (int ply) { irreversable = ply; }
   public void setRelation (int pov) { relation = pov; }
   public void setInitialTime (int seconds) { itime = seconds; }
   public void setIncrement (int seconds) { incr = seconds; }
   public void setWhiteMaterial (int value) { whiteMaterial = value; }
   public void setBlackMaterial (int value) { blackMaterial = value; }
   public void setWhiteClock (int ms) { whiteClock = ms; }
   public void setBlackClock (int ms) { blackClock = ms; }
   public void setLag (int ms) { timesealDelta = ms; }
   public void setSAN (String san) { sanMove = san; }
   public void setWhiteCastleableKingside (boolean t) { canWhiteCastleOO = t; }
   public void setWhiteCastleableQueenside (boolean t) { 
      canWhiteCastleOOO = t; 
   }
   public void setBlackCastleableKingside (boolean t) { canBlackCastleOO = t; }
   public void setBlackCastleableQueenside (boolean t) { 
      canBlackCastleOOO = t; 
   }
   public void setVerboseMove (String move) { verboseMove = move; }
   public void setFlipBoard (boolean t) { flipBoard = t; }
   public void setClockMoving (boolean t) { clockMoving = t; }

   public void setMoveTime (int ms) { moveTime = ms; }

   ///////////////////////////////////////////////////////////////////////////

   protected String getClockAsString (int clock, boolean move) {
      StringBuffer sb = new StringBuffer(7);
      int h, m, s, ms;

      h = clock / 3600000;
      m = (clock % 3600000) / 60000;
      s = (clock % 60000) / 1000;
      ms = clock % 1000;

      if (move && h > 1) {
	 sb.append(h).append(":");
	 if (m < 10)
	    sb.append(0);
      }
      sb.append(m).append(":");
      if (s < 10)
         sb.append(0);
      sb.append(s).append(".");
      if (ms < 100)
         sb.append(0);
      if (ms < 10)
         sb.append(0);
      sb.append(ms);
      return sb.toString();
   }

   ////////////////////////////////////////////////////////////////////////
   /** this is completely temporary and only for diagnostic purposes.
    */
   public String getReadable () {
      StringBuffer sb = new StringBuffer(80);
      sb.append("Board Update(" + getBoardNumber() + "): ")
        .append(getWhitePlayer())
        .append(" vs. ")
        .append(getBlackPlayer())
        .append("\n\n");

         int r =board.length-1, f=0;
         for (r = board[0].length-1; r >= 0; r--) {
	    sb.append ("   ").append(r+1).append("  ");
            for (f=0; f < board.length; f++) {
               if (board[f][r] != ' ')
                  sb.append(board[f][r]);
               else
                  if ((f+r) % 2 == 1)
                     sb.append("#");
                  else
                     sb.append(" ");
               sb.append(" ");

            }
	    if (r == 5)
	       sb.append("  ")
	         .append(getBlackClockAsString());
	    if (r == 3)
	       sb.append("  ")
	         .append(getWhiteClockAsString());
            sb.append("\n");
        }
	sb.append("\n      A B C D E F G H\n\n");
	if (!isBlackMove())
	   sb.append("         ")
	     .append(getMoveNumber())
	     .append(".")
             .append(getSAN()).append("\n");
        else
	   sb.append("       ")
	     .append(getMoveNumber())
	     .append("..")
             .append(getSAN()).append("\n");

      return sb.toString();
   }

   public String toString () {
      return getReadable();
   }
}
