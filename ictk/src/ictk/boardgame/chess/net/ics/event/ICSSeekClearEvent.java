/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2003 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
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

/*--------------------------------------------------------------------------*
 * This file was auto-generated 
 * by $Id$
 * on Sun Aug 24 18:48:29 EST 2003
 *--------------------------------------------------------------------------*/

import ictk.boardgame.chess.net.ics.*;
import ictk.boardgame.chess.net.ics.fics.event.*;
import ictk.util.Log;

import java.util.regex.*;
import java.io.IOException;

/**
 * This message from the server indicates that all known seeks should 
 * be cleared.                                                        
 */
public class ICSSeekClearEvent extends ICSEvent {


   //static initializer/////////////////////////////////////////////////////
   protected static final int SEEK_CLEAR_EVENT =  ICSEvent.SEEK_CLEAR_EVENT;

   

   //instance vars//////////////////////////////////////////////////////////


   //constructors///////////////////////////////////////////////////////////
   public ICSSeekClearEvent () {
      super(SEEK_CLEAR_EVENT);
   }

   //assessors/////////////////////////////////////////////////////////////
   //mutators//////////////////////////////////////////////////////////////

   //readable//////////////////////////////////////////////////////////////
   public String getReadable () {
      return FICSSeekClearParser.getInstance().toNative(this);
   }
}
