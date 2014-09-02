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
