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
package ictk.boardgame.ui;

import ictk.boardgame.Board;
import ictk.boardgame.BoardListener;


/* BoardDisplay **************************************************************/
/** This is a visual representation of a board.  The interface can be 
 *  extended by both GUI (Graphic) and CLI (command line) classes.
 */
public interface BoardDisplay extends BoardListener {

   /* setBoard **************************************************************/
   /** set the Board that this Display will use as a model.
    */
   public void setBoard (Board board);

   /* getBoard **************************************************************/
   /** returns the Board that this Display is using as a model.
    */
   public Board getBoard ();

   /* update ****************************************************************/
   /** this refreshes the board display.  Depending on what kind of display
    *  the implementing class is, this might do any number of different things
    *  from printing a board to System.out in the case of a CLI or refreshing
    *  the screen in the case of a GUI.
    */
   public void update ();
}
