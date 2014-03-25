/*
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke <ictk.jvarsoke [at] neverbox.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
