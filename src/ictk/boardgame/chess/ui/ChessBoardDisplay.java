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
