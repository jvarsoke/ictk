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


public class ICSTellEvent extends ICSMessageEvent {

   //static initializer/////////////////////////////////////////////////////
   public static final int TELL_EVENT =  ICSEvent.TELL_EVENT,
                           SAY_EVENT  =  ICSEvent.SAY_EVENT;

   //instance vars//////////////////////////////////////////////////////////
   protected ICSAccountType accountType;

   //constructors///////////////////////////////////////////////////////////
   public ICSTellEvent () {
      super(TELL_EVENT);
   }

   public void setAccountType (ICSAccountType acct) {
      accountType = acct;
   }

   public ICSAccountType getAccountType () {
      return accountType;
   }

   public boolean isSay () {
      return eventType == ICSEvent.SAY_EVENT;
   }

   public String getReadable () {
      StringBuffer sb = new StringBuffer();

      sb.append(getPlayer());

      sb.append(getAccountType());

      if (eventType == ICSEvent.SAY_EVENT) 
         sb.append(" says: ");
      else
         sb.append(" tells you: ");

      sb.append(getMessage());

      return sb.toString();
   }
}


