/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSResult.java,v 1.2 2003/09/29 08:19:05 jvarsoke Exp $
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

import ictk.util.Log;

public class ICSResult {
   public static final int UNDECIDED = 0,
   			   WHITE_WIN = 1,
			   DRAW      = 2,
			   BLACK_WIN = 3;

   protected int result;
   protected String desc;

   public ICSResult () {
      result = UNDECIDED;
   }

   public ICSResult (String s) {
      this();
      setResultCode(s);
   }

   public int getResultCode () { return result; }
   public void setResultCode (int res) { result = res; }

   public void setResultCode (String s) {
      if ("*".equals(s))
         result = UNDECIDED;
      else if ("1-0".equals(s))
         result = WHITE_WIN;
      else if ("1/2-1/2".equals(s))
         result = DRAW;
      else if ("0-1".equals(s))
         result = BLACK_WIN;
      else {
         Log.error(Log.PROG_WARNING, 
	    "ICSResult received '" + s + "' as a result.");
	 result = UNDECIDED;
      }
   }

   public String getReadableResult () {
      String s = null;

      switch (result) {
         case UNDECIDED: s = "*"; break;
	 case WHITE_WIN: s = "1-0"; break;
	 case DRAW:      s = "1/2-1/2"; break;
	 case BLACK_WIN: s = "0-1"; break;
	 default:
      }
      return s;
   }

   public String getDescription () { return desc; }
   public void setDescription (String s) { desc = s; }

   public String toString () {
      return getReadableResult();
   }


}
