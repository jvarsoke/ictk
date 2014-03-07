/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: MoveException.java,v 1.1.1.1 2003/03/24 22:38:06 jvarsoke Exp $
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

/* MoveException - something fishy about the move
 *
 * by jvarsoke (jvarsoke@bigfoot.com) CopyLeft 1998
 *
 * 19980902 jjv v0.01 - original
 *
 */

package ictk.boardgame;

/* MoveException ***********************************************************/
/** An Exception that occurs due to a move.
 */
public class MoveException extends BoardGameException {
   protected Move m;

   public MoveException ()         { super();  }
   public MoveException (String s) { super(s); }
   public MoveException (String s, Move _m) { 
      super(s);
      m = _m; 
   }

   /* getMove **************************************************************/
   /** returns the move that caused the problem.  Sometimes the move is
    *  in a poor state since it was illegal and probably not verified.
    */
   public Move getMove () {
      return m;
   }
}


