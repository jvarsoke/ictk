/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ChessBoardDisplay.java,v 1.3 2003/08/14 01:17:03 jvarsoke Exp $
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
package ictk.boardgame.chess.ui;


/* ChessBoardDisplay *********************************************************/
/** This is a visual representation of a chess board.  The interface can be 
 *  extended by both GUI (Graphic) and CLI (command line) classes.
 */
public interface ChessBoardDisplay {

      /** no coordinates to appear around the boarders of the board */
   public static int NO_COORDINATES     = 0,
      /** only coordinates above the board to appear */
                     TOP_COORDINATES    = 1,
      /** only coordinates to the right of the board to appear */
		     RIGHT_COORDINATES  = 2,
      /** only coordinates under the board to appear */
		     BOTTOM_COORDINATES = 4,
      /** only coordinates left of the board to appear */
		     LEFT_COORDINATES   = 8;

   /* setWhiteOnBottom *******************************************************/
   /** orients the board so White is on the bottom.  This setting is 
    *  overriden by setPlayerToMoveOnBottom(true).
    */
   public void setWhiteOnBottom (boolean t);

   /* isWhiteOnBottom ********************************************************/
   /** returns true if the board is oriented so white is currently on the 
    *  bottom.  This value could change on the next move, depending on 
    *  the value of setPlayerToMoveOnBottom(true).
    */
   public boolean isWhiteOnBottom ();

   /* setSideToMoveOnBottom **************************************************/
   /** sets the board to re-orient itself so the side to move is at the
    *  bottom after every move on update.
    */
   public void setSideToMoveOnBottom (boolean t);

   /* getSideToMoveOnBottom **************************************************/
   /** returns true if the board is set to re-orient itself so the side
    *  to move is at the bottom after every move on update.  This does not
    *  indicate whether the board is currently oriented so that the current
    *  player to move is on the bottom.
    */
   public boolean getSideToMoveOnBottom ();

   /* setVisibleCoordinates **************************************************/
   /** sets which coordinates should appear when the board is displayed.
    *  Coordinates can be bitwise ORed together to get multiple
    *  coordinates to appear.
    *  <br>
    *  For example: TOP_COORDINATES | BOTTOM_COORDINATES
    */
   public void setVisibleCoordinates (int whichCoordinatesMask);

   /* getVisibleCoordinates **************************************************/
   /** returns the mask of which coordinates are set visible for this board.
    */
   public int getVisibleCoordinates ();

   /* setLowerCaseCoordinates ************************************************/
   /** sets the coordinates to appear in lowercase.
    */
   public void setLowerCaseCoordinates (boolean t);

   /* isLowerCaseCoordinates *************************************************/
   /** returns if the coordinates are currently set to appear in lowercase.
    */
   public boolean isLowerCaseCoordinates ();
}
