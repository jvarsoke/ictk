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

/**
 * This cooresponds to Kibitz, Whisper, and Board Say messages. The   
 * EventType tells which one it is. this is a really long description 
 * and I'm hoping to see the text wrap so I can test the new function 
 * I got from the book with all the new features this should look     
 * really good.                                                       
 */
public class ICSKibitzEvent extends ICSMessageEvent
                            implements ICSBoardEvent {

   //static initializer/////////////////////////////////////////////////////
   protected static final int KIBITZ_EVENT =  ICSEvent.KIBITZ_EVENT;

   //instance vars//////////////////////////////////////////////////////////
   protected String player;
   protected ICSAccountType acctType;
   protected ICSRating rating;
   protected int boardNumber;


   //constructors///////////////////////////////////////////////////////////
   public ICSKibitzEvent () {
      super(KIBITZ_EVENT);
   }

   //assessors/////////////////////////////////////////////////////////////
   public String getPlayer () {
      return player;
   }

   public ICSAccountType getAccountType () {
      return acctType;
   }

   public ICSRating getRating () {
      return rating;
   }

   //mutators//////////////////////////////////////////////////////////////
   public void setPlayer (String player) {
      this.player = player;
   }

   public void setAccountType (ICSAccountType acctType) {
      this.acctType = acctType;
   }

   public void setRating (ICSRating rating) {
      this.rating = rating;
   }

   //ICSBoardEvent////////////////////////////////////////////////////
   public int getBoardNumber () { 
      return boardNumber; 
   }
	 
   public void setBoardNumber (int board) { 
      this.boardNumber = board; 
   }
	 
   //readable//////////////////////////////////////////////////////////////
   public String getReadable () {
      return FICSKibitzEventParser.toNative(this);
   }
}
