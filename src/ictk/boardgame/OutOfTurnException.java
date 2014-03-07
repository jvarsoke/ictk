/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: OutOfTurnException.java,v 1.1.1.1 2003/03/24 22:38:06 jvarsoke Exp $
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

/* OutOfTurnException - out of turn
 *
 * by jvarsoke (jvarsoke@bigfoot.com) CopyLeft 1998
 *
 * 19980901 jjv v0.01 - original
 *
 */

package ictk.boardgame;

/* OutOfTurnException *****************************************************/
/** tried to execute a move where the piece moved belonged to a player
 *  who's turn is wasn't.
 */
public class OutOfTurnException extends IllegalMoveException {

   public OutOfTurnException ()         { super(); }
   public OutOfTurnException (String s) { super(s); }
   public OutOfTurnException (String s, Move m) { super(s,m); }
}


