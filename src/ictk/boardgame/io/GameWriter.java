/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: GameWriter.java,v 1.2 2003/08/02 15:32:55 jvarsoke Exp $
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
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.Writer;

import ictk.boardgame.Game;
import ictk.boardgame.GameInfo;
import ictk.boardgame.Board;
import ictk.boardgame.History;

/* ChessWriter *************************************************************/
/** ChessWriter is an abstract class that writer game files
 */
public abstract class GameWriter extends PrintWriter {
   public GameWriter (OutputStream _out) {
      super(_out, true);
   }

   public GameWriter (Writer _out) {
      super(_out);
   }

   /** set the move notation style you wish the output to be in.
    */
   public abstract void setMoveNotation (MoveNotation mn);

   /** returns the currect move notation style.
    */
   public abstract MoveNotation getMoveNotation ();

   /** should variations be presented in the Writer's output or not.
    */
   public abstract void setExportVariations (boolean t);

   /** is variation data presented in the Writer's output or not.
    */
   public abstract boolean isExportVariations ();

   /** are comments presented in the Writer's output or not.
    */
   public abstract void setExportComments (boolean t);

   /** will comments be presented in the output.
    */
   public abstract boolean isExportComments ();

   /** write the full game (GameInfo, History & Board) to the 
    *  output buffer.
    */
   public abstract void writeGame (Game g)
          throws IOException;

   /** write the GameInfo to the output buffer
    */
   public abstract void writeGameInfo (GameInfo gi)
          throws IOException;

   /** write the game History to the output buffer
    */
   public abstract void writeHistory (History h)
          throws IOException;

   /** write the current Board to the output buffer
    */
   public abstract void writeBoard (Board b)
          throws IOException;

}
