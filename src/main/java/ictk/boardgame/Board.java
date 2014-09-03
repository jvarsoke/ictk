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

package ictk.boardgame;

import java.util.List;

/* Board *******************************************************************/
/** Defines the board the game is played on.  Some games have more than 
 *  one board.  When this is the case the getLegalMoves() functions will
 *  report the legal moves on that board, not legal moves in the game.
 */
public interface Board {

   //setting game position/////////////////////////////////////////////////
   /*have we started the game from the typical inital position*/
//   public void setPosition ();

   /* setPositionDefault *************************************************/
   /** sets the board to the default position for the game.
    */
   public void setPositionDefault();

   /* isInitialPositionDefault *******************************************/
   /** Returns true if the very first position on this board is the 
    *  traditional default position for this type of game; instead of
    *  the first position being a position found in the middle of a game.
    */
   public boolean isInitialPositionDefault();

   /* setPositionClear ***************************************************/
   /** clears the board of all pieces.  Depending on the type of game
    *  this may be also the default board position.
    */
   public void setPositionClear ();


   //move legality/////////////////////////////////////////////////////////
   /* getLegalMoves  *****************************************************/
   /** returns a List of all legal moves for the current board state.
    */
   public List<Move> getLegalMoves();

   /* getLegalMoveCount **************************************************/
   /** returns the number of legal moves possible on the current board.
    */
   public int    getLegalMoveCount ();

   /**
     * @deprecated use {@link #verifyIsLegalMove(Move)} instead.  
     */
   @Deprecated
   public boolean isLegalMove (Move m);

   public void verifyIsLegalMove (Move m) throws OutOfTurnException, IllegalMoveException;

   //game state////////////////////////////////////////////////////////////
   public int    getPlayerToMove();

   /* getResult **********************************************************/
   /** gets the result of the game 
    */
   //public Result getResult ();

   /* playMove ***********************************************************/
   /** this is a rather short-sighted function.  It plays the move on the
    *  board, but does not add it to the recorded History for the game.
    *  this means that using the two in combination (History.add() and
    *  playMove()) will seriously FUBAR the state of the board.
    */
   public void playMove (Move m) throws IllegalMoveException,
                    OutOfTurnException;

   /* addBoardListener ***************************************************/
   /** add a listener for board updates
    */
   public void addBoardListener (BoardListener bl);

   /* removeBoardListener ************************************************/
   public void removeBoardListener (BoardListener bl);

   /* getBoardListener ***************************************************/
   public BoardListener[] getBoardListeners ();

   /* fireBoardEvent *****************************************************/
   /** this is used by controllers (such as History) to fire board events 
    *  to BoardListeners.  It also good to use this function to update
    *  all BoardListeners on things like a position change, instead of 
    *  updating the displays individually.
    *
    *  @param event the BoardEvent.<EVENT> you wish to send to listeners
    */
   public void fireBoardEvent (int event);
}
