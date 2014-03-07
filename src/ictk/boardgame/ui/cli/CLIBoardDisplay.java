/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: CLIBoardDisplay.java,v 1.3 2003/08/14 06:40:50 jvarsoke Exp $
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
