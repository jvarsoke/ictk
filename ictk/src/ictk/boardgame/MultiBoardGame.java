/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id$
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

/* MultiBoardGame ************************************************************/
/** this is for games that might use more than one board like BugHouse (a 
 *  chess variant for 4 players).
 */
public interface MultiBoardGame extends Game {

   /* getBoards **************************************************************/
   /**
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
