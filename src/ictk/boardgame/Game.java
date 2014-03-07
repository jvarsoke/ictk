/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: Game.java,v 1.3 2003/08/13 15:13:59 jvarsoke Exp $
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

/* Game *********************************************************************/
/** the abstract Game class is the parent of all types of games that will
 *  be supported.  It needs to be generic enough to support all kinds of 
 *  turn based games from Chess, to Go, to Scrabble, etc.  Therefore,
 *  commonalities in these classes will increase the number of methods
 *  in this class.  Note: this is only an interface because the implementation
 *  of these functions is so simple there's no need to make this a class 
 *  . . .yet.
 */
public interface Game {

   /* getNumberOfPlayers *****************************************************/
   /** gets the number of players involved in this type of game, though
    *  there might not be two valid Player objects for this game.  For 
    *  example: Chess would always return 2, even if only one player is 
    *  known.  But Scrabble might return 2-6 depending on how many people
    *  are playing that particular game.
    */
   public int getNumberOfPlayers ();

   /* getGameInfo ************************************************************/
   /** this returns the GameInfo object for this game.  The GameInfo object 
    *  includes player information as well as where the game was played.
    */
   public GameInfo getGameInfo ();

   public void setGameInfo (GameInfo gi);

   /* getHistory *************************************************************/
   /** returns the move history for the entire game.  You need to use this 
    *  object to add moves to the game and navigate through the game's
    *  move list.
    */
   public History getHistory ();

   public void setHistory (History history);

   /* getCurrentResult *******************************************************/
   /** returns the current status of this Game.
    */
   public Result getCurrentResult ();

   /* getResult **************************************************************/
   /** returns file final result of the game on the main line.
    */
   public Result getResult ();

   public void setResult (Result result);

   /* getBoards **************************************************************/
   /** Board games with only one board return a single element Board[] array.
    * @return all the boards use in this game.
    */
   public Board[] getBoards();

   /* getPlayersToMove ******************************************************/
   /** returns an array containing the index number of the Players who have 
    *  the ability to make a move at this time.  The numbers can then be sent
    *  to GameInfo.getPlayer(int) to receive the Player object.
    */
   public int[] getPlayersToMove ();
}
