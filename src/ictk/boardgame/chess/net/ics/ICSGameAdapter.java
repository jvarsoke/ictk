/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSAccountType.java,v 1.4 2003/08/19 21:32:07 jvarsoke Exp $
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

package ictk.boardgame.chess.net.ics;

import ictk.boardgame.*;
import ictk.boardgame.chess.net.ics.event.*;

/* ICSGameAdapter ************************************************************/
/** Used to link a Game model, such as ChessGame, to input events coming
 *  from the ICS server.
 */

/* this object needs to associate the History with the Server History. 
 * But the history might change (because the user is scrolling through it)
 * when we don't want to change the server.  And sometimes the server
 * will change when we don't want our board to change on the screen 
 * (such as when the user pauses the board).
 */
public class ICSGameAdapter implements ICSEventListener, BoardListener {
   protected ICSProtocolHandler ics;
   protected Game game;

   public ICSGameAdapter (ICSProtocolHandler ics, Game game) {
      this.ics = ics;
      this.game = game;
   }


   /* boardUpdate ************************************************************/
   /** Relay the board update to the server
    */
   public void boardUpdate (Board b, int event) {
      ics.sendCommand("");
   }

   /* icsEventDispatched *****************************************************/
   /** Relay the server's update to the board
    */
   public void icsEventDispatched (ICSEvent event) {
   }
}
