/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: BoardEvent.java,v 1.1 2003/08/18 01:41:40 jvarsoke Exp $
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

public interface BoardEvent {
      /** a move has been executed on the board */
   public int MOVE            = 1,
      /** a move has been unexecuted on the board. This could be 
       ** because of a Takeback, or a call to prev() in the History */
              UNMOVE          = 2,
      /** the position has changed in some way and the whole board should
          be updated */
              POSITION        = 2,
      /** a series of moves is about to be executed and the Display should
       ** choose whether it wants to display each move, or whether it only
       ** should display the end position. This must always be followed by
       ** a TRAVERSAL_END_CODE. */
	      TRAVERSAL_BEGIN = 3,
      /** the traversal has ended and the Display should assume the next
       ** move event is not part of the traversal. */
	      TRAVERSAL_END   = 4;
}
