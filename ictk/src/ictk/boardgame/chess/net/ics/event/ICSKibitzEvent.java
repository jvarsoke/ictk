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
import ictk.util.Log;

import java.util.regex.*;
import java.io.IOException;


/**This cooresponds to Kibits messages and Whisper messages.  A flag
 * descerns which one it is.
 */
public class ICSKibitzEvent extends ICSMessageEvent 
                            implements ICSBoardEvent {

   //static initializer/////////////////////////////////////////////////////
   protected static final int KIBITZ_EVENT =  ICSEvent.KIBITZ_EVENT,
                              WHISPER_EVENT = ICSEvent.WHISPER_EVENT,
			      BOARD_SAY_EVENT = ICSEvent.BOARD_SAY_EVENT;

   //instance vars//////////////////////////////////////////////////////////
   protected int boardNumber;
   protected ICSAccountType accountType;
   protected ICSRating rating;

   //constructors///////////////////////////////////////////////////////////
   public ICSKibitzEvent () {
      super(KIBITZ_EVENT);
   }

   public ICSAccountType getAccountType () {
      return accountType;
   }

   public void setAccountType (ICSAccountType acct) {
      accountType = acct;
   }

   /** BOARD_SAY_EVENTs have no rating associated with them
    */
   public ICSRating getRating () {
      return rating;
   }

   /** BOARD_SAY_EVENTs have no rating associated with them
    */
   public void setRating (ICSRating rating) {
      this.rating = rating;
   }

   //ICSBoardEvent Interface//////////////////////////////////////////////////
   public void setBoardNumber (int board) {
      boardNumber = board;
   }

   public int getBoardNumber () {
      return boardNumber;
   }

   public String getReadable () {
      StringBuffer sb = new StringBuffer(20);

      sb.append(getPlayer());

      sb.append(getAccountType());

      if (getRating() != null)
         sb.append("(").append(getRating()).append(")");

      sb.append("[").append(getBoardNumber()).append("]");

      switch (eventType) {
         case ICSEvent.WHISPER_EVENT:
            sb.append(" whispers: "); break;
	 case ICSEvent.KIBITZ_EVENT:
            sb.append(" kibitzes: "); break;
	 case ICSEvent.BOARD_SAY_EVENT:
            sb.append(" says: "); break;
	 default:
	    Log.error(Log.PROG_ERROR, 
	       "Received bad eventType: " + eventType);
      }

      sb.append(getMessage());

      return sb.toString();
   }
}
