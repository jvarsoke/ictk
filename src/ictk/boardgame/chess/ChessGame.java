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
