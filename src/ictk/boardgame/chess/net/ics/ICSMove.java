/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ICSMove.java,v 1.2 2003/08/24 05:44:49 jvarsoke Exp $
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

public class ICSMove {

   /** is this move black's move */
   protected boolean isBlack;
   /** the Standard Algebraic Notation version of the move**/
   protected String  san;
   /** time it took for the last move in milliseconds*/
   protected int moveTime,
                 moveNumber;

   public ICSMove () {
   }

   public String getSAN () { return san; }
   public void setSAN (String move) { san = move; }

   public int getMoveTime () { return moveTime; }
   public void setMoveTime (int milliseconds) { moveTime = milliseconds; }

   public boolean isBlack () { return isBlack; }
   public void setBlack (boolean t) { isBlack = t; }

   public int getMoveNumber () { return moveNumber; }
   public void setMoveNumber (int number) { moveNumber = number; } 

   /** The format will be HH:MM:SS.mmm or MM:SS.mmm or M:SS.mmm depending
    *  on if H > 0 and if M > 9.
    */
   public String getMoveTimeAsString () {
      StringBuffer sb = new StringBuffer(7);
      int h, m, s, ms;

      h = moveTime / 3600000;
      m = (moveTime % 3600000) / 60000;
      s = (moveTime % 60000) / 1000;
      ms = moveTime % 1000;

      if (h > 1) {
         sb.append(h).append(":");
	 if (m < 10)
	    sb.append(0);
      }

      sb.append(m).append(":");
      if (s < 10)
         sb.append(0);

      sb.append(s).append(".");
      if (ms < 100)
         sb.append(0);
      if (ms < 10)
         sb.append(0);
      sb.append(ms);
      return sb.toString();
   }

   public String toString () {
      return moveNumber + ((isBlack) ? "b" : "w") + ". " + san 
         + "(" + getMoveTimeAsString() + ")";
   }
}
