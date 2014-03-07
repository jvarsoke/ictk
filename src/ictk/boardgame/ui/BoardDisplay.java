/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: BoardDisplay.java,v 1.2 2003/08/14 01:17:03 jvarsoke Exp $
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
