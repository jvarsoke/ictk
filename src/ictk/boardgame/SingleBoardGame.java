/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: SingleBoardGame.java,v 1.1.1.1 2003/03/24 22:38:06 jvarsoke Exp $
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

/* SingleBoardGame ***********************************************************/
/** this is for use with games that only use one board like scrabble,
 *  Go, Chess, & monopoly.
 */
public interface SingleBoardGame extends Game {

   /* getBoard ***************************************************************/
   /**returns the game board (if more than one it returns the first board
    * which might be arbitrary.
    */
   public Board getBoard ();

   /* setBoard ***************************************************************/
   /** set the board for this game.
    */
   public void setBoard (Board board);

   /* getPlayerToMove *******************************************************/
   /** returns the number of the player to move.  This can then be sent to 
    *  GameInfo.getPlayer(int) to receive the Player object.
    */
   public int getPlayerToMove ();
}
