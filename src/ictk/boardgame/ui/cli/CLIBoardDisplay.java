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
package ictk.boardgame.ui.cli;

import ictk.boardgame.Board;
import ictk.boardgame.ui.BoardDisplay;

import java.io.PrintWriter;
import java.io.Writer;


/* CLIBoardDisplay ***********************************************************/
/** This is a command line visual representation of a board.
 */
public interface CLIBoardDisplay extends BoardDisplay {

   /* setInverse *************************************************************/
   /** sets the display to assume the foreground is darker than the background.
    *  This might be beneficial when using the display on a Black on White
    *  screen.  By default all CLI Displays assume White on Black.
    */
   public void setInverse (boolean t);

   /* isInverse *************************************************************/
   /** checks to see if the display is assuming the foreground is darker than
    *  the background.  For example, on a Black on White display.
    *
    *  @return true if the display assumes the foreground is darker.
    */
   public boolean isInverse ();

   /* setWriter *************************************************************/
   /** sets the stream where the display will be sent.  Currently a
    *  PrintWriter must be specified.
    *  <br>
    *  Example:
    *  <code>
    *     display.setWriter(new PrintWriter(System.out, true));
    *  </code>
    *  <br>
    *  Note: if you're expecting output and not seeing it, make sure you
    *  set auto-flush on for the PrintWriter.
    */
   public void setWriter (PrintWriter out);

   /* getWriter *************************************************************/
   /** returns the Writer currently being used.  This is always a PrintWriter
    *  with other Streams or Writers nestled inside.
    */
   public Writer getWriter ();

   /* print *****************************************************************/
   /** prints the current board onto the Writer.
    */
   public void print ();

   /* print *****************************************************************/
   /** prints the current specified board onto the Writer.
    */
   public void print (Board board);

}
