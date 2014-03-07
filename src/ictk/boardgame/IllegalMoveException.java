/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: IllegalMoveException.java,v 1.1.1.1 2003/03/24 22:38:06 jvarsoke Exp $
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

/* IllegalMoveException ****************************************************/
/** this is thrown when you try to execute or add an illegal move to the
 *  game.
 */
public class IllegalMoveException extends MoveException {
   String moveString;
   public IllegalMoveException ()         { super();  }
   public IllegalMoveException (String s) { super(s); }
   public IllegalMoveException (String s, Move _m) { super(s, _m); }

   /* setMoveString *********************************************************/
   /** if the move wasn't processed, or doesn't yet have sensible data
    *  the moveString might have been set.  This is basically a way for
    *  bad input values to be returned by parsers.
    */
   public void setMoveString (String m) { moveString = m; }

   /* getMoveString ********************************************************/
   /** returns a value that failed to be parsed into a legal move.
    */
   public String getMoveString () { return moveString; }

   public String toString () {
      if (moveString == null)
         return getMessage();
      else
         return getMessage() + ": " + moveString;
   }
}
