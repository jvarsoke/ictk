/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSHistoryEvent.java,v 1.3 2003/10/01 06:37:18 jvarsoke Exp $
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

package ictk.boardgame.chess.net.ics.event;
import ictk.boardgame.chess.net.ics.*;

import java.io.IOException;

/* ICSHistoryEvent **********************************************************/
/** A history of games played by a particular user.  This is the result
 *  of the "history" command.
 */
public class ICSHistoryEvent extends ICSEvent {
   //static///////////////////////////////////////////////////////////////
   protected static final int HISTORY_EVENT = ICSEvent.HISTORY_EVENT;


   //instance/////////////////////////////////////////////////////////////
   ICSGameInfo[] list;
   String player;
   
   //constructors/////////////////////////////////////////////////////////
   public ICSHistoryEvent (ICSProtocolHandler server) {
      super(server, HISTORY_EVENT);
   }

   public String getPlayer () { return player; }
   public void setPlayer (String name) { player = name; }

   public ICSGameInfo[] getHistoryList () { return list; }
   public void setHistoryList (ICSGameInfo[] l) { list = l; }

   /** doesn't do much yet.
    */
   public String getReadable () {
      return "History List";
   }
}
