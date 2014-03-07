/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: GameReader.java,v 1.1.1.1 2003/03/24 22:38:06 jvarsoke Exp $
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

package ictk.boardgame.io;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;

import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.AmbiguousMoveException;
import ictk.boardgame.Game;
import ictk.boardgame.GameInfo;
import ictk.boardgame.History;
import ictk.boardgame.Board;

/* GameReader ***************************************************************/
/** reads games in various notations.
 */
public abstract class GameReader extends BufferedReader {

   public GameReader (Reader _ir) {
      super(_ir);
   }

   /** read the Game (History, Board, and GameInfo) from the input Buffer
    */
   public abstract Game readGame ()
          throws InvalidGameFormatException,
                 IllegalMoveException,
                 AmbiguousMoveException,
                 IOException;

   /** read the GameInfo from the input buffer
    */
   public abstract GameInfo readGameInfo ()
          throws IOException;

   /** read the game History from the input buffer
    */
   public abstract History readHistory ()
          throws InvalidGameFormatException,
                 IllegalMoveException,
                 AmbiguousMoveException,
                 IOException;

   /** read the game Board from the input buffer 
    */
   public abstract Board readBoard ()
          throws IOException;

}
