/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id$
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
import java.io.IOException;


/**This cooresponds to Kibits messages and Whisper messages.  A flag
 * descerns which one it is.
 */
public abstract class ICSKibitzEvent extends ICSMessageEvent 
                            implements ICSBoardEvent {

   //static initializer/////////////////////////////////////////////////////
   public static final int KIBITZ_EVENT =  ICSEvent.KIBITZ_EVENT,
                           WHISPER_EVENT = ICSEvent.WHISPER_EVENT;

   //instance vars//////////////////////////////////////////////////////////
   protected boolean isWhisper;
   protected int boardNumber;
   protected ICSAccountType accountType;
   protected ICSRating rating;

   //constructors///////////////////////////////////////////////////////////
   public ICSKibitzEvent (ICSProtocolHandler server) {
      super(server, KIBITZ_EVENT);
   }

   public ICSAccountType getAccountType () {
      return accountType;
   }

   public ICSRating getRating () {
      return rating;
   }

   //ICSBoardEvent Interface//////////////////////////////////////////////////
   public void setBoardNumber (int board) {
      boardNumber = board;
   }

   public int getBoardNumber () {
      return boardNumber;
   }

   public String toString () {
      StringBuffer sb = new StringBuffer();

      sb.append(getPlayer());

      sb.append(getAccountType());

      if (getRating() != null)
         sb.append("(").append(getRating()).append(")");

      sb.append("[").append(getBoardNumber()).append("]");

      if (isWhisper) 
         sb.append(" whispers: ");
      else
         sb.append(" kibitzes: ");

      sb.append(getMessage());

      return sb.toString();
   }
}
