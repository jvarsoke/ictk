/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: BoardListener.java,v 1.3 2003/08/20 14:29:06 jvarsoke Exp $
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

import java.util.EventListener;

public interface BoardListener extends EventListener {

   /* boardUpdate ************************************************************/
   /** 
    *  @param b     the board the event occured on.
    *  @param event indicates what type of update has occured. (MOVE_EVENT, 
    *               POSITION_EVENT etc).
    */
    /* considering these additional params
    *  @param location the coordinate(s) of the update (may be null).
    *  @param aspect optional parameter providing more info on the update.
    */
   public void boardUpdate (Board b, int event);
}
