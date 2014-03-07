/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ChessGame.java,v 1.4 2003/08/18 01:56:48 jvarsoke Exp $
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

package ictk.boardgame.chess;

import ictk.boardgame.*;

import java.io.IOException;

/* ChessGame *****************************************************************/
/** This is a container class to house the ChessGameInfo, ChessBoard and
 *  History for the Board.
 */
public class ChessGame implements SingleBoardGame {
   public static final boolean BLACK = true;
   public static final boolean WHITE = false;

     /** the chess board */
   protected ChessBoard board;
     /** the history of moves for the board */
   protected History history;              
     /** the game information for this game */
   protected ChessGameInfo gameInfo;

   //Constructors/////////////////////////////////////////////////////////////
   public ChessGame () {
      this(null);
   }

   public ChessGame (ChessGameInfo _gameInfo) {
      this(_gameInfo, null, null);
   }

   public ChessGame (ChessGameInfo _gameInfo, ChessBoard _board) {
      this(_gameInfo, _board, null);
   }

   public ChessGame (ChessGameInfo _gameInfo, ChessBoard _board, 
                     History _hist) {

      gameInfo = _gameInfo;
      history  = _hist;
      board    = _board;

      if (board == null) {
	 board = new ChessBoard();
      }

      if (history == null)
         history = new History(this);
   }

   //Game Methods/////////////////////////////////////////////////////////////

   public int getNumberOfPlayers () { return 2; }

   public GameInfo getGameInfo () { return gameInfo; }

   public void setGameInfo (GameInfo gi) { gameInfo = (ChessGameInfo) gi; }

   public History getHistory () { return history; }

   public Board getBoard () { return board; }

   public Board[] getBoards () { 
      Board[] b = new Board[1];
      b[0] = board;

      return b;
   }

   public void setResult (Result result) { 
      Move m = history.getFinalMove(true);
      m.setResult(result);
      if (gameInfo != null)
         gameInfo.setResult(result);
   }

   /* getPlayerToMove *******************************************************/
   /** returns 0 if it is White to move, 1 if Black to move.
    */
   public int getPlayerToMove () {
      return  (board.isBlackMove()) ? 1 : 0;
   }

   public int[] getPlayersToMove () {
      int[] i = new int[1];
      i[0] = getPlayerToMove();

      return i;
   }

   //Mutators////////////////////////////////////////////////////////////////
   public void setBoard (Board _board) { 
      board = (ChessBoard) _board; 
   }

   public void setHistory (History _hist) { 
      history = _hist; 
   }

   /* getCurrentResult ******************************************************/
   public Result getCurrentResult () {
      Result r = null;
      Move m = null;
      
      m = history.getCurrentMove();
      if (m != null)
         r = m.getResult();

      if (r == null)
         return new ChessResult(ChessResult.UNDECIDED);
      else
         return r;
   }

   /* getResult *************************************************************/
   public Result getResult () {
      Result r = null;
      Move m = null;

      m = history.getFinalMove(true);
      if (m != null)
         r = m.getResult();
      else
         return new ChessResult(ChessResult.UNDECIDED);

      return r;
   }

   //Display/////////////////////////////////////////////////////////////////
   /* toString **************************************************************/
   /** this is purely for diagnostic purposes.
    */
   public String toString () {
      String sboard = board.toString();
      return ((gameInfo == null) ? "No Game Info" : gameInfo.toString()) 
	     + "\n" + history + "\n" + sboard;
   }

   /* dump ******************************************************************/
   /** this is purely for diagnostic purposes.
    */
   public String dump() {
      String sboard = board.toString();
      return ((gameInfo == null) ? "No Game Info" : gameInfo.toString()) 
	     + "\n" + history + "\n" + sboard;
   }

   /* equals ****************************************************************/
   /** compares the history (shallow equals) and gameinfo.  The current
    *  state of the board is not evaluated.
    */
   public boolean equals (Object obj) {
      if (this == obj) return true;
      if ((obj == null) || (obj.getClass() != this.getClass()))
         return false;

      boolean t = true;
      ChessGame g = (ChessGame) obj;

      t = t && ((gameInfo == g.gameInfo)
               || (gameInfo != null && gameInfo.equals(g.gameInfo)));
      t = t && ((history == g.history)
               || (history != null && history.equals(g.history)));

      return t;
   }

   /* hashCode **************************************************************/
   public int hashCode () {
      int hash = 7;

      hash = 31 * hash + ((history == null) ? 0 : history.hashCode());
      hash = 31 * hash + ((gameInfo == null) ? 0 : gameInfo.hashCode());

      return hash;
   }
}
