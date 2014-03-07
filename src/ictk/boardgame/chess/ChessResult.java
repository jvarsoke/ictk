/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id: ChessResult.java,v 1.2 2003/07/28 05:20:07 jvarsoke Exp $
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

package ictk.boardgame.chess;
import ictk.boardgame.Result;

/* ChessResult **************************************************************/
/** Setting this for a particular move indicates the termination of 
 *  the chess game.
 */
public class ChessResult implements Result {
      /** the game is unfinished */
   public static final int UNDECIDED                      = Result.UNDECIDED,
      /** the game ends with neither player having a decisive advantage */
                           DRAW                           = UNDECIDED + 1,
      /** white wins the game */
                           WHITE_WIN                      = DRAW + 1,
      /** black wins the game */
                           BLACK_WIN                      = WHITE_WIN + 1;

      /** what result has this Result been set to?*/
   protected int index = UNDECIDED;

      /* reason for the result ex: what the draw by repition, agreement, 
       * 50 move rule etc?*/
//             int reason = 0;

   public ChessResult (int i) {
      index = i;
   }

   /* set ********************************************************************/
   /** sets the result as UNDECIDED, DRAW, etc */
   public void set (int i) {
      index = i;
   }

   /* get ********************************************************************/
   /** gets the index UNDECIDED, DRAW etc */
   public int getIndex () {
      return index;
   }

   public boolean isUndecided () { return index == UNDECIDED; }
   public boolean isWhiteWin () { return index == WHITE_WIN; }
   public boolean isDraw () { return index == DRAW; }
   public boolean isBlackWin () { return index == BLACK_WIN; }

   /* setReason *************************************************************/
   /* sets the reason for the result (out of time, 3x rep, etc)
    */
    /*
   public void setReason (int r) {
      reason = r;
   }
   */

   /* getReason *************************************************************/
   /* gets the reason for the result (out of time, 3x rep, etc)
    */
    /*
   public int getReason () {  return reason; }
   */

   /* returns the String 
   public String getReasonString () {
      if (reason > 0 && reason < reasonDescriptions.length)
         return reasonDescriptions[reason];
      else 
         return null;
   }
   */

   /* toString **************************************************************/
   /** returns the basic PGN forms that represend UNDECIDED, etc
    *  Should only be used for diagnostics. 
    */
   public String toString () {
      String s = null;

      switch (index) {
         case UNDECIDED: s = "*";       break;
         case WHITE_WIN: s = "1-0";     break;
         case DRAW:      s = "1/2-1/2"; break;
         case BLACK_WIN: s = "0-1";     break;
	 default: 
	    s = "?";
      }
      return s;
   }

   /* equals *****************************************************************/
   /** test to make sure the indexes are equal
    */
   public boolean equals (Object obj) {
      if (obj == this) return true;
      if ((obj == null) || (obj.getClass() != this.getClass()))
         return false;

      ChessResult r = (ChessResult) obj;
      
      return index == r.index;
   }

   /* hashCode ***************************************************************/
   public int hashCode () {
      int hash = 7;
      
      hash = 31 * hash + index;

      return hash;
   }
}
