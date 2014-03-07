/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSUnknownEvent.java,v 1.1 2003/05/13 16:01:39 jvarsoke Exp $
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

import java.util.regex.*;

/* ICSUnknownEvent ***********************************************************/
/** this is for unclassified events.
 */
public class ICSUnknownEvent extends ICSEvent {
   public static final int UNKNOWN_EVENT = ICSEvent.UNKNOWN_EVENT;

   //Contstructors/////////////////////////////////////////////////////////////
   public ICSUnknownEvent (ICSProtocolHandler server) {
      super(server, UNKNOWN_EVENT);
   }

   public ICSUnknownEvent (ICSProtocolHandler server, String mesg) {
      super(server, UNKNOWN_EVENT);
      message = mesg;
   }

   public String getReadable () {
      return message;
   }
}
