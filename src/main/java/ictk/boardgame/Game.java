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
