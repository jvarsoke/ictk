/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: Result.java,v 1.2 2003/07/28 05:20:07 jvarsoke Exp $
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


/** this is used to mark the result of a game.  this interface has minimal
 *  methods in it because it still is not certain how to organize the results
 *  of all types of games elegantly into a few interfaces.  As games are 
 *  developed you might see more methods and interfaces that corrispond to
 *  Result.
 */
public interface Result {
      /** the came has not been decided, or is still in progress*/
   public static final int UNDECIDED = 0;

   /* isUndecided **********************************************************/
   /** returns true if the game is yet undecided.
    */
   public boolean isUndecided ();
}
