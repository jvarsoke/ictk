/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: Board.java,v 1.3 2003/08/18 01:56:47 jvarsoke Exp $
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
   public List   getLegalMoves();

   /* getLegalMoveCount **************************************************/
   /** returns the number of legal moves possible on the current board.
    */
   public int    getLegalMoveCount ();
   public boolean isLegalMove (Move m);

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
